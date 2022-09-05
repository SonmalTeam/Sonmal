import argparse
import logging
import os
import random
from argparse import Namespace
from datetime import datetime
from pathlib import Path

import numpy as np
import torch
from yacs.config import CfgNode

from typing import Optional


def default_argument_parser():
    """
    Create a parser with some common arguments

    Returns:
        argparse.ArgumentParser:
    """
    parser = argparse.ArgumentParser(description="")
    parser.add_argument("--config-file", default="", metavar="FILE", help="path to config file")
    parser.add_argument("--eval-only", action="store_true", help="perform evaluation only")
    parser.add_argument(
        "opts",
        help="Modify config options using the command-line",
        default=None,
        nargs=argparse.REMAINDER,
    )
    return parser


def default_setup(cfg: CfgNode, args: Namespace) -> CfgNode:
    """
    Perform some basic common setups at the beginning of a job

    Args:
        cfg (CfgNode): the full config to be used
        args (argparse.Namespace): the command line arguments to be logged

    Returns:
        cfg (CfgNode)
    """
    seed = _set_seed(None if cfg.SEED < 0 else cfg.SEED)
    experiment_name = _get_experiment_name(cfg, seed)

    output_dir = os.path.join(cfg.OUTPUT_DIR, experiment_name)
    Path(output_dir).mkdir(parents=True, exist_ok=True)

    logger = _setup_logger()

    logger.info("Command line arguments: " + str(args))
    if hasattr(args, "config_file") and args.config_file != "":
        logger.info(
            "Contents of args.config_file={}:\n{}".format(
                args.config_file,
                Path(args.config_file).open().read()
            )
        )

    # reproducability
    torch.backends.cudnn.benchmark = cfg.CUDNN_BENCHMARK
    torch.backends.cudnn.deterministic = cfg.CUDNN_DETERMINISTIC

    cfg.OUTPUT_DIR = output_dir
    cfg.SEED = seed

    logger.info("Running with full config:\n{}".format(cfg))
    if output_dir:
        path = os.path.join(output_dir, "config.yaml")
        with Path(path).open("w") as f:
            f.write(cfg.dump())
        logger.info("Full config saved to {}".format(path))

    return cfg


def _get_experiment_name(cfg: CfgNode, seed: int) -> str:
    """
    Set experiment name based on config

    Args:
        cfg (CfgNode): the full config to be used
        seed (int): random seed to be used
    Returns:
        exp_name (str)
    """
    exp_name = ""
    if cfg.NOTE:
        exp_name += "note_{}_".format(cfg.NOTE)
    exp_name += "seed_{}".format(seed)
    return exp_name


def _setup_logger(name: str = "cslr"):
    logger = logging.getLogger(name)
    logging.basicConfig(
        format="%(asctime)s - %(levelname)s - %(name)s -  %(message)s",
        datefmt="%m/%d/%Y %H:%M:%S",
        level=logging.INFO
    )
    return logger


def _set_seed(seed: Optional[int] = None) -> int:
    """
    Set the random seed for the RNG in torch, numpy and python.
    Args:
        seed (int): if None, will use a strong random seed.

    Returns:
        seed (int): proper random seed
    """
    if seed is None:
        seed = (
            os.getpid() + int(datetime.now().strftime("%S%f")) +  # noqa
            int.from_bytes(os.urandom(2), "big")
        )
    torch.manual_seed(seed)
    torch.cuda.manual_seed(seed)
    np.random.seed(seed)
    random.seed(seed)

    logger = logging.getLogger(__name__)
    logger.info("Using a random seed {}".format(seed))

    return seed
