import logging
import sys
from collections import OrderedDict
from random import shuffle


from torch.utils.data import DataLoader, Dataset, Sampler
from yacs.config import CfgNode

from typing import List

from .sign_dataset import SignDataset
from .transforms import build_transform_gen
from .vocabulary import build_vocab


def tokenize_text(text: str) -> List[str]:
    return text.split()


def build_dataset(cfg: CfgNode) -> Dataset:
    logger = logging.getLogger()

    data_root = cfg.DATASET.DATA_ROOT
    img_prefix_train = cfg.DATASET.TRAIN.IMG_PREFIX
    ann_file_train = cfg.DATASET.TRAIN.ANN_FILE
    img_prefix_val = cfg.DATASET.VAL.IMG_PREFIX
    ann_file_val = cfg.DATASET.VAL.ANN_FILE
    exclude_token = cfg.DATASET.VOCABULARY.EXCLUDE_TOKENS

    tfm_gens = build_transform_gen(cfg, is_train=False)
    train_dataset = SignDataset(
        data_root,
        ann_file_train,
        img_prefix=img_prefix_train,
        tfm_gens=tfm_gens,
        tokenize=tokenize_text,
        is_train=True,
        exclude_token=exclude_token
    )
    vocab = build_vocab(cfg, train_dataset, sys.maxsize, min_freq=1)



    tfm_gens = build_transform_gen(cfg, is_train=False)
    val_dataset = SignDataset(
        data_root,
        ann_file_val,
        img_prefix=img_prefix_val,
        tfm_gens=tfm_gens,
        tokenize=tokenize_text,
        is_train=False,
        exclude_token=exclude_token
    )
 
    # load vocabulary to dataset
    train_dataset.load_vocab(vocab)
    val_dataset.load_vocab(vocab)
    print()
    logger.info(
        "{} examples for Train, {} examples for Valid. Number of Vocabulary: {}".format(
            len(train_dataset), len(val_dataset), len(vocab.stoi)
        )
    )
    print()

    return train_dataset, val_dataset


class BucketBatchSampler(Sampler):
    # want inputs to be an array
    def __init__(self, frames_path, batch_size, drop_last=False):
        self.batch_size = batch_size
        self.drop_last = drop_last
        ind_n_len = []
        for i, p in enumerate(frames_path):
            ind_n_len.append((i, len(p)))
        self.ind_n_len = ind_n_len
        self.batch_list = self._generate_batch_map()
        self.num_batches = len(self.batch_list) if not self.drop_last else len(self.batch_list) - 1

    def _generate_batch_map(self):
        # shuffle all of the indices first so they are put into buckets differently
        shuffle(self.ind_n_len)
        # Organize lengths, e.g., batch_map[10] = [30, 124, 203, ...]
        # <= indices of sequences of length 10
        batch_map = OrderedDict()
        for idx, length in self.ind_n_len:
            if length not in batch_map:
                batch_map[length] = [idx]
            else:
                batch_map[length].append(idx)
        flattened_map = []
        for key in sorted(batch_map.keys()):
            # print("debug: batch_map_key_val: {}, {}".format(key, batch_map[key]))
            flattened_map.extend(batch_map[key])

        # group by batch size
        batch_list = []
        for i in range(0, len(flattened_map), self.batch_size):
            batch_list.append(flattened_map[i:i + self.batch_size])
        return batch_list

    def batch_count(self):
        return self.num_batches

    def __len__(self):
        return self.num_batches

    def __iter__(self):
        batch_list = self._generate_batch_map()
        shuffle(batch_list)  # shuffle batch groups
        for batch_inds in batch_list:
            if len(batch_inds) < self.batch_size and self.drop_last:
                continue
            yield batch_inds


def build_data_loader(cfg) -> DataLoader:
    batch_per_gpu = cfg.SOLVER.BATCH_PER_GPU
    worker_per_gpu = cfg.DATASET.WORKER_PER_GPU
    GPU_ID = cfg.GPU_ID
    if not isinstance(GPU_ID, list):
        GPU_ID = [GPU_ID]

    train_dataset, val_dataset = build_dataset(cfg)
    # multiple data loaders
    train_loader = DataLoader(
        dataset=train_dataset,
        collate_fn=train_dataset.collate,
        batch_sampler=BucketBatchSampler(
            [example["frames"] for example in train_dataset.examples], batch_per_gpu * len(GPU_ID)
        ),
        shuffle=False,
        drop_last=False,
        num_workers=worker_per_gpu * len(GPU_ID)
    )
    val_loader = DataLoader(
        dataset=val_dataset,
        collate_fn=val_dataset.collate,
        batch_size=batch_per_gpu * len(GPU_ID),
        shuffle=False,
        drop_last=False,
        num_workers=worker_per_gpu * len(GPU_ID)
    )

    return train_loader, val_loader
