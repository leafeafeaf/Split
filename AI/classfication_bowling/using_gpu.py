import tensorflow as tf

def using_gpu():
    from tensorflow.python.client import device_lib
    print(device_lib.list_local_devices())

    tf.config.list_physical_devices('GPU')
