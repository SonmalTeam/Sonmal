from tensorflow import keras




# Press the green button in the gutter to run the script.
if __name__ == '__main__':
    model = keras.models.load_model('/home/jupyter-j7d202/test/AITest/sentence_model_0509_248.h5', compile=False)
    export_path = './pb'
    model.save()
    model.save(export_path, save_format="tf")
