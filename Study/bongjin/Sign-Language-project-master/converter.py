from torch.autograd import Variable
import torch.onnx
import torchvision
import torch

if __name__ == '__main__':
    model = torch.load('download_sh/best_model.pt')
    print(model)