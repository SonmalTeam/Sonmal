import torch.optim as optim
from torch.nn import Module
from torch.optim import Optimizer
from torch.optim.lr_scheduler import _LRScheduler
from yacs.config import CfgNode

from .scheduler import WarmupCosineLR, WarmupCosineLRFixMatch, WarmupMultiStepLR


def build_optimizer(cfg: CfgNode, model: Module) -> Optimizer:
    """
    Build an optimizer from config.
    """
    if cfg.SOLVER.OPTIM_NAME == "SGD":
        optimizer = optim.SGD(
            model.parameters(),
            lr=cfg.SOLVER.BASE_LR,
            momentum=cfg.SOLVER.SGD.MOMENTUM,
            weight_decay=cfg.SOLVER.SGD.WEIGHT_DECAY,
            nesterov=cfg.SOLVER.SGD.NESTEROV
        )
    elif cfg.SOLVER.OPTIM_NAME == "Adam":
        optimizer = optim.Adam(
            model.parameters(),
            lr=cfg.SOLVER.BASE_LR,
            betas=(cfg.SOLVER.ADAM.BETA1, cfg.SOLVER.ADAM.BETA2),
            eps=cfg.SOLVER.ADAM.EPS,
            weight_decay=cfg.SOLVER.ADAM.WEIGHT_DECAY
        )
    else:
        raise ValueError("Unknown Optimizer: {}".format(cfg.SOLVER.OPTIM_NAME))
    return optimizer


def build_lr_scheduler(cfg: CfgNode, optimizer: Optimizer) -> _LRScheduler:
    """
    Build a LR scheduler from config.
    """
    name = cfg.SOLVER.LR_SCHEDULER_NAME
    if name == "WarmupMultiStepLR":
        return WarmupMultiStepLR(
            optimizer,
            cfg.SOLVER.STEPS,
            gamma=cfg.SOLVER.GAMMA,
            warmup_factor=cfg.SOLVER.WARMUP_FACTOR,
            warmup_iters=cfg.SOLVER.WARMUP_ITERS,
            warmup_method=cfg.SOLVER.WARMUP_METHOD,
        )
    elif name == "WarmupCosineLR":
        max_iter = cfg.SOLVER.MAX_ITER
        if cfg.SOLVER.RAMPDOWN_ITERS > 0:
            max_iter = max(max_iter, cfg.SOLVER.RAMPDOWN_ITERS)
        return WarmupCosineLR(
            optimizer,
            max_iter,
            warmup_factor=cfg.SOLVER.WARMUP_FACTOR,
            warmup_iters=cfg.SOLVER.WARMUP_ITERS,
            warmup_method=cfg.SOLVER.WARMUP_METHOD,
        )
    elif name == "WarmupCosineLRFixMatch":
        return WarmupCosineLRFixMatch(
            optimizer,
            cfg.SOLVER.MAX_ITER,
            warmup_factor=cfg.SOLVER.WARMUP_FACTOR,
            warmup_iters=cfg.SOLVER.WARMUP_ITERS,
            warmup_method=cfg.SOLVER.WARMUP_METHOD,
        )
    else:
        raise ValueError("Unknown LR scheduler: {}".format(name))
