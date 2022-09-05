import logging

import numpy as np
from fvcore.transforms.transform import Transform, TransformList
from torch import Tensor

from typing import List, Tuple

from .transform_gen import CenterCrop, RandomCrop, Resize, ToTensorGen, TransformGen


def build_transform_gen(cfg, is_train: bool = True) -> List[TransformGen]:
    """
    Create a list of :class:`TransformGen` from config.
    Now it includes resizing and flipping.

    Returns:
        list[TransformGen]
    """
    logger = logging.getLogger(__name__)

    resize = cfg.DATASET.TRANSFORM.RESIZE_IMG
    ts = cfg.DATASET.TRANSFORM.TEMPORAL_SCALING
    crop = cfg.DATASET.TRANSFORM.CROP_SIZE
    tc = cfg.DATASET.TRANSFORM.TEMPORAL_CROP_RATIO
    norm_params = dict(mean=cfg.DATASET.TRANSFORM.MEAN, std=cfg.DATASET.TRANSFORM.STD)
    tfm_gens = []

    # resize
    # tfm_gens.append(Resize(resize, temporal_scaling=ts if is_train else 1.0, interp="trilinear"))
    tfm_gens.append(Resize(resize, temporal_scaling=ts, interp="trilinear"))
    if is_train:
        tfm_gens.append(RandomCrop("absolute", crop, temporal_crop_ratio=tc))
    else:
        tfm_gens.append(CenterCrop(crop, temporal_crop_ratio=tc))
        # tfm_gens.append(CenterCrop(crop, temporal_crop_ratio=0.0))
    tfm_gens.append(ToTensorGen(normalizer=norm_params))

    if is_train:
        logger.info("TransformGens used in training: " + str(tfm_gens))
    else:
        logger.info("TransformGens used in validation: " + str(tfm_gens))
    return tfm_gens


def apply_transform_gens(transform_gens: List[TransformGen],
                         img: np.ndarray) -> Tuple[Tensor, TransformList]:
    """
    Apply a list of :class:`TransformGen` on the input image, and
    returns the transformed image and a list of transforms.

    We cannot simply create and return all transforms without
    applying it to the image, because a subsequent transform may
    need the output of the previous one.

    Args:
        transform_gens (list): list of :class:`TransformGen` instance to
            be applied.
        img (ndarray): uint8 or floating point images with 1 or 3 channels.

    Returns:
        ndarray: the transformed image
        TransformList: contain the transforms that's used.
    """
    for g in transform_gens:
        assert isinstance(g, TransformGen), g

    check_dtype(img)

    tfms = []
    for g in transform_gens:
        tfm = g.get_transform(img)
        assert isinstance(
            tfm, Transform
        ), "TransformGen {} must return an instance of Transform! Got {} instead".format(g, tfm)
        img = tfm.apply_image(img)
        tfms.append(tfm)
    return img, TransformList(tfms)


def check_dtype(img):
    assert isinstance(img, np.ndarray), "[TransformGen] Needs an numpy array, but got a {}!".format(
        type(img)
    )
    assert not isinstance(img.dtype, np.integer) or (
        img.dtype == np.uint8
    ), "[TransformGen] Got image of type {}, use uint8 or floating points instead!".format(
        img.dtype
    )
    assert img.ndim in [3, 4], img.ndim
