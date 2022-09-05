# -*- coding: utf-8 -*-
# Copyright (c) Facebook, Inc. and its affiliates. All Rights Reserved
# File: transformer.py

import inspect
import pprint
from abc import ABCMeta, abstractmethod

import numpy as np

from typing import Optional, Tuple

from .transform import CropTransform, ScaleTransform, ToTensor


def extract_img_size(img):
    if len(img.shape) == 4:
        t, h, w = img.shape[:3]
    elif len(img.shape) in (2, 3):
        h, w = img.shape[:2]
        t = None
    else:
        raise ("Unsupported input with shape of {}".format(img.shape))
    return t, h, w


class TransformGen(metaclass=ABCMeta):
    """
    TransformGen takes an image of type uint8 in range [0, 255], or
    floating point in range [0, 1] or [0, 255] as input.

    It creates a :class:`Transform` based on the given image, sometimes with randomness.
    The transform can then be used to transform images
    or other data (boxes, points, annotations, etc.) associated with it.

    The assumption made in this class
    is that the image itself is sufficient to instantiate a transform.
    When this assumption is not true, you need to create the transforms by your own.

    A list of `TransformGen` can be applied with :func:`apply_transform_gens`.
    """

    def _init(self, params=None):
        if params:
            for k, v in params.items():
                if k != "self" and not k.startswith("_"):
                    setattr(self, k, v)

    @abstractmethod
    def get_transform(self, img):
        pass

    def _rand_range(self, low=1.0, high=None, size=None):
        """
        Uniform float random number between low and high.
        """
        if high is None:
            low, high = 0, low
        if size is None:
            size = []
        return np.random.uniform(low, high, size)

    def __repr__(self):
        """
        Produce something like:
        "MyTransformGen(field1={self.field1}, field2={self.field2})"
        """
        try:
            sig = inspect.signature(self.__init__)
            classname = type(self).__name__
            argstr = []
            for name, param in sig.parameters.items():
                assert (
                    param.kind != param.VAR_POSITIONAL and param.kind != param.VAR_KEYWORD
                ), "The default __repr__ doesn't support *args or **kwargs"
                assert hasattr(self, name), (
                    "Attribute {} not found! "
                    "Default __repr__ only works if attributes match the constructor.".format(name)
                )
                attr = getattr(self, name)
                default = param.default
                if default is attr:
                    continue
                argstr.append("{}={}".format(name, pprint.pformat(attr)))
            return "{}({})".format(classname, ", ".join(argstr))
        except AssertionError:
            return super().__repr__()

    __str__ = __repr__


class Resize(TransformGen):
    """ Resize image to a target size"""

    def __init__(self, shape, *, temporal_scaling: Optional[float] = None, interp="bicubic"):
        """
        Args:
            shape: (h, w) tuple or a int
            interp: PIL interpolation method
        """
        if isinstance(shape, int):
            shape = (shape, shape)
        shape = tuple(shape)
        self._init(locals())

    def get_transform(self, img):
        t, h, w = extract_img_size(img)
        new_t = None
        if self.temporal_scaling is not None:
            assert t is not None
            new_t = int(t * self.temporal_scaling)
        return ScaleTransform(
            h, w, self.shape[0], self.shape[1], t=t, new_t=new_t, interp=self.interp
        )


class RandomCrop(TransformGen):
    """
    Randomly crop a subimage out of an image.
    """

    def __init__(self, crop_type: str, crop_size, *, temporal_crop_ratio: Optional[float] = None):
        """
        Args:
            crop_type (str): one of "relative_range", "relative", "absolute".
                See `config/defaults.py` for explanation.
            crop_size (tuple[float]): the relative ratio or absolute pixels of
                height and width
        """
        super().__init__()
        assert crop_type in ["relative_range", "relative", "absolute"]
        self._init(locals())

    def get_transform(self, img):
        t, h, w = extract_img_size(img)
        croph, cropw = self.get_crop_size((h, w))
        if self.temporal_crop_ratio is not None:
            assert t is not None
            cropt = int(t * (1. - self.temporal_crop_ratio) + 0.5)
            assert t >= cropt
        else:
            cropt = None
        assert h >= croph and w >= cropw, "Shape computation in {} has bugs.".format(self)
        h0 = np.random.randint(h - croph + 1)
        w0 = np.random.randint(w - cropw + 1)
        t0 = np.random.randint(t - cropt + 1) if cropt is not None else None
        return CropTransform(w0, h0, cropw, croph, t0=t0, z=cropt)

    def get_crop_size(self, image_size):
        """
        Args:
            image_size (tuple): height, width

        Returns:
            crop_size (tuple): height, width in absolute pixels
        """
        h, w = image_size
        if self.crop_type == "relative":
            ch, cw = self.crop_size
            return int(h * ch + 0.5), int(w * cw + 0.5)
        elif self.crop_type == "relative_range":
            crop_size = np.asarray(self.crop_size, dtype=np.float32)
            ch, cw = crop_size + np.random.rand(2) * (1 - crop_size)
            return int(h * ch + 0.5), int(w * cw + 0.5)
        elif self.crop_type == "absolute":
            return self.crop_size
        else:
            NotImplementedError("Unknown crop type {}".format(self.crop_type))


class CenterCrop(TransformGen):
    """
    Crop the center of the frames with given crop size.
    """

    def __init__(self, crop_size: Tuple[int, int], *, temporal_crop_ratio: Optional[float] = None):
        super().__init__()
        self._init(locals())

    def get_transform(self, img):
        t, h, w = extract_img_size(img)
        croph, cropw = self.crop_size
        if self.temporal_crop_ratio is not None:
            assert t is not None
            cropt = int(t * (1. - self.temporal_crop_ratio) + 0.5)
            assert t >= cropt
        else:
            cropt = None
        h0 = int(0.5 * (h - croph))
        w0 = int(0.5 * (w - cropw))
        t0 = int(0.5 * (t - cropt)) if cropt is not None else None
        return CropTransform(w0, h0, cropw, croph, t0=t0, z=cropt)


class ToTensorGen(TransformGen):

    def __init__(self, normalizer=None):
        super().__init__()
        self._init(locals())

    def get_transform(self, img):
        target_dtype = img.dtype
        return ToTensor(normalizer=self.normalizer, target_dtype=target_dtype)
