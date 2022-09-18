import pickle

from torch.autograd import Variable
import torch.onnx
import torchvision
import torch

from model.seq2seq_gru_attention import GRU_AT_Encoder, GRU_AT_Decoder, Attention, GRU_AT_Seq2Seq
from utils.seq2seq_preprocessing import target_preprocessing

if __name__ == '__main__':
    shape = [7129, 376, 246]

    word_to_index, max_len, vocab, decoder_input = target_preprocessing('keti_ann/train.csv', mode='ksl')

    input_size = 246
    HID_DIM = 512
    N_LAYERS = 2
    OUTPUT_DIM = len(vocab)
    emb_dim = 128
    DEC_DROPOUT = 0.5
    device = torch.device("cuda:0" if torch.cuda.is_available() else "cpu")


    enc = GRU_AT_Encoder(input_size, HID_DIM, N_LAYERS)
    att = Attention(HID_DIM)
    dec = GRU_AT_Decoder(OUTPUT_DIM, emb_dim, HID_DIM, N_LAYERS, att, DEC_DROPOUT)
    model = GRU_AT_Seq2Seq(enc, dec, device).to(device)

    print(model)
    model.eval()
    x = torch.randn(16, 12,3 ,246, len(vocab))
    torch.onnx.export(model, x, 'test.onnx', export_params=True, input_names=['input'], output_names=['output'])
    # model = torch.load('download_sh/best_model.pt')
    # print(model)