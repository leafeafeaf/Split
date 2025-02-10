import torch
import gc
import numpy as np
from torch.cuda import memory_allocated, empty_cache
import torch.optim as optim
from config import device
import config

def clear_memory():
    if device != 'cpu':
        empty_cache()
    gc.collect()

# 학습 알고리즘


def epoch(data_loader, mode = 'train'):
    from training.visualization import net, loss_fn
    
    # 사용되는 변수 초기화
    iter_loss, iter_acc, last_grad_performed = [], [], False
    
    # 1 iteration 학습 알고리즘(for문을 나오면 1 epoch 완료)
    for _data, _label in data_loader:
        _data, _label = _data.to(device), _label.type(torch.LongTensor).to(device)
        
        # 1. Feed-forward
        if mode == 'train':
            net.train()
        else:
            # 학습때만 쓰이는 Dropout, Batch Normalization을 미사용
            net.eval()
            
        result = net(_data) # 1 Batch에 대한 결과값 도출 Class에 대한 확률값으로
        _, out = torch.max(result, 1) # result에서 최대 확률값을 기준으로 예측 class 도출
        
        # 2. Loss 계산
        loss = loss_fn(result, _label) # GT 와 Label 비교하여 Loss 산정
        iter_loss.append(loss.item()) # 학습 추이를 위하여 Loss를 기록
        
        # 3. 역전파 작업 후 Gradient Descent
        if mode == 'train':
            optimizer = optim.Adam(net.parameters(), lr=0.0005)
            # scheduler = optim.lr_scheduler.ReduceLROnPlateau(optimizer, mode='min', factor=0.5, patience=3, verbose=True)
            optimizer.zero_grad() # 미분을 통해 얻은 기울기를 초기화 for 다음 epoch
            loss.backward() # 역전파 작업
            optimizer.step() # Gradient Descent 수행
            last_grad_performed = True # for문 나가면 epoch 카운터 += 1
            
        # 4. 정확도 계산
        acc_partial = (out == _label).float().sum() # GT == Label 인 개수
        acc_partial = acc_partial / len(_label) # i TP / (TP + TN)) 에서 정확도 산출
        iter_acc.append(acc_partial.item()) # 학습 추이를 위하여 Acc. 기록
        
    # 역전파 작업 후 Epoch 카운터 += 1
    if last_grad_performed:
        config.epoch_cnt += 1
        
    clear_memory()
        
    # loss와 acc의 평균값 for 학습추이 그래프, 모든 GT와 Label값 for 컨퓨전 매트릭스
    return np.average(iter_loss), np.average(iter_acc)

def epoch_not_finished():
    # 에폭이 끝남을을 판단
    maximum_epoch = 50
    return config.epoch_cnt < maximum_epoch


def save_model():
    from training.visualization import net
    torch.save(net.state_dict(), 'model.pth')

