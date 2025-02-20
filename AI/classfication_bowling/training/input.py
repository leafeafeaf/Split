# input tensor로 변환

import torch.nn as nn
import torch.optim as optim
import torch.nn.functional as F
from torch.utils.data import Dataset, DataLoader
import torch
import numpy as np

class InputData(Dataset):
    def __init__(self, seq_list):
        self.y = [] # 라벨
        self.x = [] # keypoints
        for dic in seq_list:
            self.y.append(dic['key'])
            self.x.append(dic['value'])

    def __getitem__(self, index):
        label = self.y[index]
        data = self.x[index]
        return torch.Tensor(np.array(data)), torch.tensor(np.array(int(label)))

    def __len__(self):
        return len(self.x)