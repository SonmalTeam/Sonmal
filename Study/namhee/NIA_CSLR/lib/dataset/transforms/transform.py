import numpy as np
import torch
from fvcore.transforms.transform import Transform
from torch import Tensor

from typing import Dict, Optional, Union


def normalize(tensor, mean, std):
    """
    Normalize a tensor image with mean and standard deviation.
    This is a modification of the one from torchvision to support the image size of (N, C, H, W).

    Args:
        tensor (Tensor): Tensor image of size (C, H, W) or (N, C, H, W) to be normalized.
        mean (sequence): Sequence of means for each channel.
        std (sequence): Sequence of standard deviations for each channel.

    Returns:
        Tensor: Normalized Tensor image.
    """
    if not torch.is_tensor(tensor):
        raise TypeError('tensor should be a torch tensor. Got {}.'.format(type(tensor)))

    assert tensor.ndim in (3, 4), (
        "Expected tensor to be a tensor image of size (C, H, W) or (N, C, H, W)."
        "Got tensor.size() ={}.".format(tensor.size())
    )
    dtype = tensor.dtype
    device = tensor.device
    mean = torch.as_tensor(mean, dtype=dtype, device=device)
    std = torch.as_tensor(std, dtype=dtype, device=device)
    if (std == 0).any():
        raise ValueError(
            'std evaluated to zero after conversion to {}, leading to division by zero.'.
            format(dtype)
        )

    if mean.ndim == 1:
        mean = mean[:, None, None]
    if std.ndim == 1:
        std = std[:, None, None]
    if tensor.ndim == 4:
        mean = mean[None, :, ...]
        std = std[None, :, ...]

    tensor.sub_(mean).div_(std)
    return tensor


def to_tensor(
    numpy_array: np.ndarray,
    *,
    normalizer: Optional[Dict] = None,
    divider: Union[int, float] = 255.0,
    is_5d_tensor: bool = False
) -> Tensor:
    """
    Convert the numpy array to torch float tensor with the same dimension.
    Args:
        numpy_array (ndarray): of shape NxHxWxC, or HxWxC
            represent an image. The array can be of type uint8 in range
            [0, 255], or floating point in range [0, 1] or [0, 255].
        normalizer (Tuple or List): optionally normalize the tensor with given mean and std vector.
    Returns:
        float_tensor (tensor): converted float tensor.
    """
    assert isinstance(numpy_array, np.ndarray)
    ndim = len(numpy_array.shape)
    assert ndim in (3, 4)

    numpy_array = numpy_array / (divider if numpy_array.dtype == np.uint8 else 1)
    if is_5d_tensor:
        assert ndim == 4
        shift_factor = 4
    else:
        shift_factor = 3
    numpy_array = np.moveaxis(numpy_array, -1, ndim - shift_factor).astype(np.float32)
    if is_5d_tensor:
        numpy_array = numpy_array[None, ...]
    float_tensor = torch.from_numpy(np.ascontiguousarray(numpy_array))
    if normalizer is not None:
        return normalize(float_tensor, **normalizer)

    return float_tensor


def to_numpy(float_tensor: torch.Tensor, target_dtype: np.dtype) -> np.ndarray:
    # to numpy array for a 5d tensor. (B=1, C, T, H, W) -> (T, H, W, C)

    assert float_tensor.ndim == 5
    assert float_tensor.size(0) == 1
    float_tensor = float_tensor.squeeze(0).permute(1, 2, 3, 0)

    if target_dtype == np.uint8:
        # Need to specifically call round here, notice in pytroch the round
        # is half to even.
        # https://github.com/pytorch/pytorch/issues/16498
        float_tensor = float_tensor.round().byte()
    return float_tensor.numpy()


class ToTensor(Transform):
    """
    Convert the numpy image to `torch.Tensor` and divide by 255 if image is `uint8` type.
    """

    def __init__(self, normalizer=None, target_dtype=None):
        """
        Args:
            normalizer (dict, optional): dict with keys [mean, std]
            target_dtype (dtype, optional): the target dtype of the numpy array to represent
            the image as output. The array can be of type uint8 in range
            [0, 255], or floating point in range [0, 1] or [0, 255].
        """
        super().__init__()
        self._set_attributes(locals())

    def apply_image(self, img):
        return to_tensor(img, normalizer=self.normalizer)

    def apply_coords(self, coords: np.ndarray) -> np.ndarray:
        return coords

    def inverse(self) -> Transform:
        pass


class ScaleTransform(Transform):
    """
    Resize the image to a target size.
    """

    def __init__(
        self,
        h: int,
        w: int,
        new_h: int,
        new_w: int,
        *,
        t: Optional[int] = None,
        new_t: Optional[int] = None,
        interp: str = None
    ):
        """
        Args:
            h, w (int): original image size.
            new_h, new_w (int): new image size.
            interp (str): interpolation methods. Options includes `nearest`, `linear`
                (3D-only), `bilinear`, `bicubic` (4D-only), and `area`.
                Details can be found in:
                https://pytorch.org/docs/stable/nn.functional.html
        """
        super().__init__()
        self._set_attributes(locals())

    def apply_image(self, img: np.ndarray, interp: Optional[str] = None) -> np.ndarray:
        """
        Resize the image(s).
        Args:
            img (ndarray): of shape NxHxWxC, or HxWxC or HxW. The array can be
                of type uint8 in range [0, 255], or floating point in range
                [0, 1] or [0, 255].
            interp (str): interpolation methods. Options includes `nearest`, `linear`
                (3D-only), `bilinear`, `bicubic` (4D-only), and `area`.
                Details can be found in:
                https://pytorch.org/docs/stable/nn.functional.html
        Returns:
            ndarray: resized image(s).
        """
        if len(img.shape) == 4:
            t, h, w = img.shape[:3]
        elif len(img.shape) in (2, 3):
            h, w = img.shape[:2]
        else:
            raise ("Unsupported input with shape of {}".format(img.shape))
        assert (self.h == h and  # noqa
                self.w == w), "Input size mismatch h w {}:{} -> {}:{}".format(self.h, self.w, h, w)
        interp_method = interp if interp is not None else self.interp
        # Option of align_corners is only supported for linear, bilinear,
        # and bicubic.
        if interp_method in ["linear", "bilinear", "trilinear", "bicubic"]:
            align_corners = False
        else:
            align_corners = None

        # note: this is quite slow for int8 images because torch does not
        # support it https://github.com/pytorch/pytorch/issues/5580
        if self.t is not None and self.new_t is not None:
            new_size = (self.new_t, self.new_h, self.new_w)
        else:
            new_size = (self.new_h, self.new_w)

        float_tensor = torch.nn.functional.interpolate(
            to_tensor(img, divider=1, is_5d_tensor=True),
            size=new_size,
            mode=interp_method,
            align_corners=align_corners,
        )
        return to_numpy(float_tensor, img.dtype)

    def apply_coords(self, coords: np.ndarray) -> np.ndarray:
        pass
        return coords

    def apply_segmentation(self, segmentation: np.ndarray) -> np.ndarray:
        pass

    def inverse(self) -> Transform:
        pass


class CropTransform(Transform):

    def __init__(
        self,
        x0: int,
        y0: int,
        w: int,
        h: int,
        *,
        t0: Optional[int] = None,
        z: Optional[int] = None
    ):
        # TODO: flip the order of w and h.
        """
        Args:
            x0, y0, w, h (int): crop the image(s) by img[y0:y0+h, x0:x0+w].
        """
        super().__init__()
        self._set_attributes(locals())

    def apply_image(self, img: np.ndarray) -> np.ndarray:
        """
        Crop the image(s).
        Args:
            img (ndarray): of shape NxHxWxC, or HxWxC or HxW. The array can be
                of type uint8 in range [0, 255], or floating point in range
                [0, 1] or [0, 255].
        Returns:
            ndarray: cropped image(s).
        """
        if len(img.shape) <= 3:
            return img[self.y0:self.y0 + self.h, self.x0:self.x0 + self.w]
        else:
            if self.t0 is not None and self.z is not None:
                return img[self.t0:self.t0 + self.z, self.y0:self.y0 +  # noqa
                           self.h, self.x0:self.x0 +  # noqa
                           self.w, :]
            return img[..., self.y0:self.y0 + self.h, self.x0:self.x0 + self.w, :]

    def apply_coords(self, coords: np.ndarray) -> np.ndarray:
        pass

    def apply_polygons(self, polygons: list) -> list:
        pass
