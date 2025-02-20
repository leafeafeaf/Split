import tensorflow as tf
import torch

def using_gpu_tf():
    from tensorflow.python.client import device_lib
    print(device_lib.list_local_devices())

    tf.config.list_physical_devices('GPU')

def using_gpu_torch():
    if torch.cuda.is_available() == True:
        device = 'cuda:0'
        print("GPU 사용 가능")
    else:
        device = 'cpu'
