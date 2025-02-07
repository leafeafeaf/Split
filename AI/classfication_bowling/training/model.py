# LSTM 모델 구조 설계

import torch.nn as nn
import torch.optim as optim
import torch.nn.functional as F
class skel_LSTM(nn.Module):
    def __init__(self):
        super(skel_LSTM, self).__init__()
        self.lstm1 = nn.LSTM(input_size=34, hidden_size=128, num_layers=1, batch_first=True)
        self.lstm2 = nn.LSTM(input_size=128, hidden_size=256, num_layers=1, batch_first=True)
        self.lstm3 = nn.LSTM(input_size=256, hidden_size=64, num_layers=1, batch_first=True)
        self.dropout = nn.Dropout(0.2)
        self.batch_norm = nn.BatchNorm1d(64)
        self.fc = nn.Linear(64, 2)
    
    def forward(self, x):
        x, _ = self.lstm1(x)
        x, _ = self.lstm2(x)
        x, _ = self.lstm3(x)
        x = self.dropout(x)
        x = self.batch_norm(x[:, -1, :])
        x = self.fc(x)
        return x