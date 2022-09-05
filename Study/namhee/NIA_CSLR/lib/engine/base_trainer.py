from torch.nn import Module
from torch.optim import Optimizer
from torch.optim.lr_scheduler import _LRScheduler
from yacs.config import CfgNode


class BaseTrainer:
    """class for BaseTrainer"""

    def __init__(
        self,
        cfg: CfgNode,
    ) -> None:
        pass

    def load_checkpoint(self) -> None:
        pass

    def save_checkpoint(self) -> None:
        pass

    def _build_writers(self) -> list:
        pass

    def build_model(self) -> Module:
        pass

    def build_optimizer(self) -> Optimizer:
        pass

    def build_lr_scheduler(self, cfg: CfgNode, optimizer: Optimizer) -> _LRScheduler:
        pass
