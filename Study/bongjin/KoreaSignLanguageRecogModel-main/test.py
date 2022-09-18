import cv2
import mediapipe as mp
import numpy as np
from tensorflow.keras.models import load_model
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import LSTM, Dense
from tensorflow.keras.callbacks import TensorBoard
import tensorflow as tf
import pandas as pd
from tqdm import tqdm
import io
import matplotlib.pyplot as plt

#actions = ['Oui', 'Bonjour', 'Non','Cava','Silvous']
seq_length = 10

actions = []
def collect_words(num, provider, year, direc, type, file, word, index):
    #print(word)
    if(num <= 11):
        if(word not in actions):
                #print(word)
                actions.append(word)

df_links = pd.read_excel("D:\koreaSignLanguage\ksl\Mdata.xlsx", engine='openpyxl')
#print(df_links.head(3))
for idx, row in tqdm(df_links.iterrows(), total=df_links.shape[0]):
    collect_words(*row)

#model = load_model('models/myModel.h5')
model = load_model('models/videoInput.h5')

# MediaPipe hands model
mp_hands = mp.solutions.hands
mp_drawing = mp.solutions.drawing_utils
hands = mp_hands.Hands(
    max_num_hands=1,
    min_detection_confidence=0.5,
    min_tracking_confidence=0.5)


# w = int(cap.get(cv2.CAP_PROP_FRAME_WIDTH))
# h = int(cap.get(cv2.CAP_PROP_FRAME_HEIGHT))
# fourcc = cv2.VideoWriter_fourcc('m', 'p', '4', 'v')
# out = cv2.VideoWriter('input.mp4', fourcc, cap.get(cv2.CAP_PROP_FPS), (w, h))
# out2 = cv2.VideoWriter('output.mp4', fourcc, cap.get(cv2.CAP_PROP_FPS), (w, h))
def image(img,label):
    seq = []
    action_seq = []
    img0 = img.copy()

    #img = cv2.flip(img, 1)
    img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
    result = hands.process(img)
    img = cv2.cvtColor(img, cv2.COLOR_RGB2BGR)

    if result.multi_hand_landmarks is not None:
        for res in result.multi_hand_landmarks:
            joint = np.zeros((21, 4))  #x,y,z,visibility
            for j, lm in enumerate(res.landmark):
                joint[j] = [lm.x, lm.y, lm.z, lm.visibility]

            # Compute angles between joints
            v1 = joint[[0,1,2,3,0,5,6,7,0,9,10,11,0,13,14,15,0,17,18,19], :3] # Parent joint
            v2 = joint[[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20], :3] # Child joint
            v = v2 - v1 # [20, 3]
            # Normalize v
            v = v / np.linalg.norm(v, axis=1)[:, np.newaxis]

            # Get angle using arcos of dot product
            angle = np.arccos(np.einsum('nt,nt->n',
                v[[0,1,2,4,5,6,8,9,10,12,13,14,16,17,18],:], 
                v[[1,2,3,5,6,7,9,10,11,13,14,15,17,18,19],:])) # [15,]

            angle = np.degrees(angle) # Convert radian to degree

            d = np.concatenate([joint.flatten(), angle])  #joint와 angle을 만들어줌

            seq.append(d)

            mp_drawing.draw_landmarks(img, res, mp_hands.HAND_CONNECTIONS)

            # if len(seq) < seq_length:
            #     continue

            input_data = np.expand_dims(np.array(seq[-seq_length:], dtype=np.float32), axis=0)

            y_pred = model.predict(input_data).squeeze()

            i_pred = int(np.argmax(y_pred))
            conf = y_pred[i_pred]

            # if conf < 0.9:   #이 action은 확실하지 않음
            #     continue

            action = actions[i_pred]
            print("predicted: {}".format(action))
            action_seq.append(action)
            #print(action_seq)
            # if len(action_seq) < 3:
            #     continue

            # this_action = '?'
            # if action_seq[-1] == action_seq[-2] == action_seq[-3]:   #마지막 3개의 action이 모두 같은 액션일때 -> 유효한 gesture
            #     this_action = action

            if label == action:
              cv2.putText(img, 'Good', org= (int(img.shape[1]),int(img.shape[0]+30)),fontFace=cv2.FONT_HERSHEY_SIMPLEX, fontScale=1, color=(255,255,255), thickness=2)
              cv2.putText(img, f'{action_seq[-1]}', org=(int(res.landmark[0].x * img.shape[1]), int(res.landmark[0].y * img.shape[0] + 20)), fontFace=cv2.FONT_HERSHEY_SIMPLEX, fontScale=1, color=(255, 255, 0), thickness=2)
            else:
              cv2.putText(img, 'False', org= (int(img.shape[1]),int(img.shape[0]+30)),fontFace=cv2.FONT_HERSHEY_SIMPLEX, fontScale=1, color=(255,0,0), thickness=2)
              cv2.putText(img, f'{action_seq[-1]}', org=(int(res.landmark[0].x * img.shape[1]), int(res.landmark[0].y * img.shape[0] + 20)), fontFace=cv2.FONT_HERSHEY_SIMPLEX, fontScale=1, color=(255, 0, 0), thickness=2)

    # out.write(img0)
    # out2.write(img)
    cv2.imshow('img', img)
    cv2.waitKey()
    return img

def video(cap,label):
    seq = []
    action_seq = []

    while cap.isOpened():
        ret, img = cap.read()

        if not ret:
             print('--------------------------------------------------------------------')
             break

        img0 = img.copy()
        
        #img = cv2.flip(img, 1)   #좌우반전 / train set과의 차이를 만들기 위함
        img = cv2.resize(img,(800,800))
        img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
        result = hands.process(img)
        img = cv2.cvtColor(img, cv2.COLOR_RGB2BGR)

        if result.multi_hand_landmarks is not None:
            for res in result.multi_hand_landmarks:
                joint = np.zeros((21, 4))
                for j, lm in enumerate(res.landmark):
                    joint[j] = [lm.x, lm.y, lm.z, lm.visibility]  #joint에 landmark 값들 저장

                # Compute angles between joints
                v1 = joint[[0,1,2,3,0,5,6,7,0,9,10,11,0,13,14,15,0,17,18,19], :3] # Parent joint
                v2 = joint[[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20], :3] # Child joint
                v = v2 - v1 # [20, 3]
                # Normalize v
                v = v / np.linalg.norm(v, axis=1)[:, np.newaxis]

                # Get angle using arcos of dot product
                angle = np.arccos(np.einsum('nt,nt->n',
                    v[[0,1,2,4,5,6,8,9,10,12,13,14,16,17,18],:], 
                    v[[1,2,3,5,6,7,9,10,11,13,14,15,17,18,19],:])) # [15,]

                angle = np.degrees(angle) # Convert radian to degree

                d = np.concatenate([joint.flatten(), angle])  #joint와 angle을 만들어줌

                seq.append(d)

                mp_drawing.draw_landmarks(img, res, mp_hands.HAND_CONNECTIONS)

                if len(seq) < seq_length:
                    continue

                input_data = np.expand_dims(np.array(seq[-seq_length:], dtype=np.float32), axis=0)

                y_pred = model.predict(input_data).squeeze()

                i_pred = int(np.argmax(y_pred))
                conf = y_pred[i_pred]

                if conf < 0.9:   #이 action은 확실하지 않음
                    continue

                action = actions[i_pred]
                action_seq.append(action)

                if len(action_seq) < 3:
                    continue

                this_action = '?'
                if action_seq[-1] == action_seq[-2] == action_seq[-3]:   #마지막 3개의 action이 모두 같은 액션일때 -> 유효한 gesture
                    this_action = action
                
                print("predicted: {} label: {}".format(this_action,label))
                if label == this_action:  #int(img.shape[1]),int(img.shape[0]+20)
                  #cv2.putText(img, 'True', org= (0,0),fontFace=cv2.FONT_HERSHEY_SIMPLEX, fontScale=1, color=(255,255,0), thickness=2)
                  cv2.putText(img, f'{this_action}', org=(int(res.landmark[0].x * img.shape[1]), int(res.landmark[0].y * img.shape[0] + 20)), fontFace=cv2.FONT_HERSHEY_SIMPLEX, fontScale=1, color=(0,0,0), thickness=2)
                else:
                  #cv2.putText(img, 'False', org= (0,0),fontFace=cv2.FONT_HERSHEY_SIMPLEX, fontScale=1, color=(255,0,0), thickness=2)
                  cv2.putText(img, f'{this_action}', org=(int(res.landmark[0].x * img.shape[1]), int(res.landmark[0].y * img.shape[0] + 20)), fontFace=cv2.FONT_HERSHEY_SIMPLEX, fontScale=1, color=(0, 0, 0), thickness=2)  

        # out.write(img0)s
        # out2.write(img)
        cv2.imshow('img', img)
        if cv2.waitKey(1) == ord('q'):
            return img
            # break

if __name__ == "__main__":
    imagelist = []
    #labellist = ['1','2','3','4','5','7','8','9','10']
    labellist = ['0','1','2','3','4','5','7','8','9','10']
    i = 0
    while i != 11:
         cv = cv2.VideoCapture('test/min/{}.avi'.format(i))
         video(cv,i)
         i+=1

    # cv = cv2.VideoCapture('test/KETI_SL_0000000839.avi') #0
    # # img = video(cv,labellist[0])
    # # imagelist.append(img)
    # cv = cv2.VideoCapture('test/side/KETI_SL_0000001258.avi')  #1
    # # img = video(cv,labellist[1])
    # # imagelist.append(img)
    # cv = cv2.VideoCapture('test/side/KETI_SL_0000001259.avi')  #2
    # # img = video(cv,labellist[2]) 
    # # imagelist.append(img)
    # cv = cv2.VideoCapture('test/side/KETI_SL_0000001260.avi')  #3
    # # img = video(cv,labellist[3])
    # # imagelist.append(img)
    # cv = cv2.VideoCapture('test/side/KETI_SL_0000000843.avi')  #4
    # # img = video(cv,labellist[4])
    # # imagelist.append(img)
    # cv = cv2.VideoCapture('test/side/KETI_SL_0000000844.avi')  #5
    # # img = video(cv,labellist[5])
    # # imagelist.append(img)
    # cv = cv2.VideoCapture('test/side/KETI_SL_0000000845.avi')  #6
    # # img = video(cv,labellist[6])
    # # imagelist.append(img)
    # cv = cv2.VideoCapture('test/side/KETI_SL_0000000846.avi')  #7
    # # img = video(cv,labellist[7])
    # # imagelist.append(img)
    # cv = cv2.VideoCapture('test/side/KETI_SL_0000000847.avi')  #8
    # # img = video(cv,labellist[8])
    # # imagelist.append(img)
    # cv = cv2.VideoCapture('test/side/KETI_SL_0000000848.avi')  #9
    # # img = video(cv,labellist[9])
    # # imagelist.append(img)
    # cv = cv2.VideoCapture('test/side/KETI_SL_0000000849.avi')  #10
    #video(cv,labellist[10])
    #img = video(cv,labellist[10])
    #imagelist.append(img)
#     for i in labellist:
#         #print(i)
#         img = cv2.imread('test/{}.PNG'.format(i))
#         print("{}".format(img.shape))
#         img = cv2.resize(img,(500,500))
#         img = image(img,i)
#         imagelist.append(img)

#     log_dir = 'logs/ksl/'  # 'logs/gradient_tape/' 'logs/after/'  
#     test_summary_writer = tf.summary.create_file_writer(log_dir)
#     with test_summary_writer.as_default(): # Don't forget to reshape. 
#         #images = np.reshape(imagelist[0:9], (9, 500, 500, 3)) 
#         #tf.summary.image("9 test data examples", [images[0]], step=0)
#         image1 = tf.random.uniform(shape=[8, 8, 3])
#         image2 = tf.random.uniform(shape=[8, 8, 3])
#         tf.summary.image("grayscale_noise", [image1, image2], step=0)
#         rgb_image_uint8 = tf.constant([
#             [[1, 1, 0], [0, 0, 1]],
#             ], dtype=tf.uint8) * 255
#         tf.summary.image("picture", [rgb_image_uint8], step=1)        

# logdir = "logs/ksl"
# file_writer = tf.summary.create_file_writer(logdir)

# def plot_to_image(figure):
#   """Converts the matplotlib plot specified by 'figure' to a PNG image and
#   returns it. The supplied figure is closed and inaccessible after this call."""
#   # Save the plot to a PNG in memory.
#   buf = io.BytesIO()
# #   plt.savefig(buf, format='png')
# #   # Closing the figure prevents it from being displayed directly inside
# #   # the notebook.
# #   plt.close(figure)
# #   buf.seek(0)
#   # Convert PNG buffer to TF image
#   image = tf.image.decode_png(buf.getvalue(), channels=4)
#   # Add the batch dimension
#   image = tf.expand_dims(image, 0)
#   return image

# def image_grid():
#   """Return a 5x5 grid of the MNIST images as a matplotlib figure."""
#   # Create a figure to contain the plot.
#   figure = plt.figure(figsize=(10,10))
    
#   for j in range(9):
#     plt.subplot(5, 5, j, title=labellist[j])
#     plt.xticks([])
#     plt.yticks([])
#     plt.grid(False)
#     plt.imshow(imagelist[j], cmap=plt.cm.binary)
#   plt.show()

#   return figure

# Prepare the plot
# figure = image_grid()
# Convert to image and log
#with file_writer.as_default():
  #tf.summary.image("Test Data", plot_to_image(figure), step=0)

    # cap = cv2.VideoCapture('test/KETI_SL_0000000839.avi')
    # label = '0'
    # video(cap,label)