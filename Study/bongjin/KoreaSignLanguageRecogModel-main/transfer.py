from tensorflow import keras
model = keras.models.load_model('models/dnetccnl_doc3d_final.pkl', compile=False)

export_path = 'transfer/'
model.save(export_path, save_format="tf")
#tf.keras.models.save_model(export_path, save_format="tf")


import tensorflow as tf

saved_model_dir = 'transfer/'
converter = tf.lite.TFLiteConverter.from_saved_model(saved_model_dir)
converter.target_spec.supported_ops = [tf.lite.OpsSet.TFLITE_BUILTINS,
                                       tf.lite.OpsSet.SELECT_TF_OPS]
tflite_model = converter.convert()
open('transfer/dnetccnl.tflite', 'wb').write(tflite_model)
