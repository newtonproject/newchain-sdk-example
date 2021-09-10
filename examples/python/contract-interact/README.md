# Contract Interact Python

与 newchain 合约交互 的 Python程序

## 环境要求 

- python3

- virtualenv

## 安装环境
```
virtualenv venv --python=python3.8
pip3 install -r requirements
```

## 使用

- 将合约 `abi` 信息放入 `contracts` 文件夹
- 在 `contracts/config` 配置合约地址
- 在 `contracts/utils` 中获取合约信息
- 参考 `zombie.py` 编写合约交互内容
- 在 `main.py` 中调用相关合约信息
