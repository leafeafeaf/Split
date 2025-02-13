import pandas as pd
import matplotlib.pyplot as plt
from sklearn.linear_model import LinearRegression
import numpy as np
import joblib
from sklearn.metrics import mean_squared_error

# 샘플 데이터 (CSV 파일을 대신해서 직접 정의)
file_path = 'C:\\Users\\SSAFY\\code\\PJT\\S12P11B202\\AI\\posture_correct\\angles_data.csv'
with open(file_path, "r") as file:
    data = pd.read_csv(file)

df = pd.DataFrame(data)

# 선형 회귀 및 그래프 함수
def plot_linear_regression(x, y, xlabel, ylabel):
    x = np.array(x).reshape(-1, 1)
    y = np.array(y)

    model = LinearRegression()
    model.fit(x, y)
    y_pred = model.predict(x)

    mse = mean_squared_error(y, y_pred)
    print(f"Training {ylabel} MSE: {mse:.4f}")

    if (ylabel == 'Arm Angle'): 
        joblib.dump((model, mse), "./model/arm_model.pkl")
        print("Model saved as 팔 model")

    elif (ylabel == 'Foot Angle'):
        joblib.dump((model, mse), "./model/foot_model.pkl")
        print("Model saved as 발 model")

    elif (ylabel == 'Pelvis Angle'):
        joblib.dump((model, mse), "./model/pelvis_model.pkl")
        print("Model saved as 골반 model")

    plt.scatter(x, y, color='blue', label='Data points')
    plt.plot(x, y_pred, color='red', linewidth=2, label='Linear regression')
    plt.xlabel(xlabel)
    plt.ylabel(ylabel)
    plt.legend()
    plt.show()

# 1. 독립변수: foot_angle, 종속변수: arm_angle
plot_linear_regression(df['foot_angle'], df['arm_angle'], 'Foot Angle', 'Arm Angle')

# 2. 독립변수: pelvis_angle, 종속변수: foot_angle
plot_linear_regression(df['pelvis_angle'], df['foot_angle'], 'Pelvis Angle', 'Foot Angle')

# 3. 독립변수: foot_angle, 종속변수: pelvis_angle
plot_linear_regression(df['foot_angle'], df['pelvis_angle'], 'Foot Angle', 'Pelvis Angle')

