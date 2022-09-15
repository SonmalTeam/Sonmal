import torch.onnx
import torch
from torch.autograd import Variable


def main():
    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    model = pytorch_model()
    dummy_input = Variable(torch.randn(1, 3, 256, 256))
    state_dict = torch.load('/home/jupyter-j7d202/test/AITest/model_best.pth')
    model.load_state_dict(state_dict)
    torch.onnx.export(model, dummy_input, "model.onnx")

if __name__ == '__main__':
    main()