import string
import cv2
from pytube import YouTube
import mediapipe as mp
import numpy as np
import pandas as pd
import time, os
import shutil
from tqdm import tqdm
import re

#actions = ['Oui', 'Bonjour', 'Non','Cava','Silvous']
seq_length = 10
secs_for_action = 2

# MediaPipe hands model
mp_hands = mp.solutions.hands
mp_drawing = mp.solutions.drawing_utils
hands = mp_hands.Hands(
    max_num_hands=1,
    min_detection_confidence=0.5,
    min_tracking_confidence=0.5)

#caplist = np.zeros((5,5)).astype(np.str)


caplist = [[0] * 2 for _ in range(105)]
# for i in range(5):
#   print(caplist[i])

# def collect_video(name, video_id, start_time, duration_time):
#     for idx, action in enumerate(actions):
#         #print(caplist)
#         if(name == action):
#             caplist.append(action)
#             print('data/videos/{}/{}.mp4'.format(name,name + "-" + video_id))
#             cv = cv2.VideoCapture('data/videos/{}/{}.mp4'.format(name,name + "-" + video_id))
#             #np.append(caplist[idx],cv)
#             caplist.append(cv)

words = []
def collect_words(num, provider, year, direc, type, file, word, index):
    #print(word)
    if(num <= 105):
        if(word not in words):
                #print(word)
                words.append(word)

def change_extension(num, provider, year, direc, type, file, word, index):
    #print(file)
    #print(type(file))
    print(str(file).replace("MOV","avi"))
    file = str(file).replace("MOV","avi")
        

def collect_video(num, provider, year, direc, type, file, word, index):
    for idx, w in enumerate(words):
        #print(caplist)
        if(word == w and num <= 524):
            #print(word)
            caplist.append(w)
            #print('{}: {}'.format(word,file))
            cv = cv2.VideoCapture('D:\\koreaSignLanguage\\ksl\\videos\\{}'.format(file))
            #np.append(caplist[idx],cv)
            caplist.append(cv)

# currentPath = os.getcwd()
# os.chdir("D:\koreaSignLanguage\ksl")

df_links = pd.read_excel("D:\koreaSignLanguage\ksl\Mdata.xlsx", engine='openpyxl')
#print(df_links.head(3))
for idx, row in tqdm(df_links.iterrows(), total=df_links.shape[0]):
    collect_words(*row)
    #change_extension(*row)

####################change extension####################
# listy = df_links["파일명"].str.contains('.MOV')
# listi = df_links["파일명"].str.contains('.MTS')
# for i in listy:
#     if i:
#         last = df_links['파일명'].str.replace('MOV','avi')

# for i in listi:
#     if i:
#         last = df_links['파일명'].str.replace('MTS','avi')

# print(last)
# df_links['파일명'] = last
# df_links.to_excel('D:\koreaSignLanguage\ksl\Mdata.xlsx', index=False)
########################################################

#print(df_links.head(3))
#print(len(words)) #105
#print(words)

# for idx, row in tqdm(df_links.iterrows(), total=df_links.shape[0]):
#     collect_video(*row)
# #print(len(caplist))
# print(caplist)

######################################################################################
for idx, w in enumerate(words):
    #print(action)
    listy = df_links.loc[(df_links["한국어"]==w),:]
    #print(listy["id"].values)
    index = 0
    for i in listy["파일명"].values:
        #print("{},{}".format(idx,index))
        cv = cv2.VideoCapture('D:\\koreaSignLanguage\\ksl\\videos\\{}'.format(i))
        caplist[idx][index] = (cv)
        index+=1
########################################################################################

#print(caplist)
# for i in range(106):
#   print(caplist[i])


# for idx, row in tqdm(df_links.iterrows(), total=df_links.shape[0]):
#     collect_video(*row)


#print(len(caplist)) #16

# for ix, c in enumerate(caplist):
#     while True:
#       #print('enter')
#       ret, img = c.read()
#       if not ret:
#         break
#       cv2.imshow('video', img)
#       if cv2.waitKey(1) == ord('q'):
#         break
#     c.release()
#     cv2.destroyAllWindows()
# # #
created_time = int(time.time())
shutil.rmtree(r'dataset')
os.makedirs('dataset', exist_ok=True)

#while True:
    #print('enter')

# frame_width = capture.get(cv2.CAP_PROP_FRAME_WIDTH)
# frame_height = capture.get(cv2.CAP_PROP_FRAME_HEIGHT)

for idx, action in enumerate(words):
    # ret, img = cap.read()
    # img = cv2.flip(img, 1)

    # if not ret:
    #    continue

    # print(idx)
    # print(action)
    # cv2.putText(img, f'Waiting for collecting {action.upper()} action...', org=(10, 30), fontFace=cv2.FONT_HERSHEY_SIMPLEX, fontScale=1, color=(255, 255, 255), thickness=2)
    # cv2.imshow('img', img)
    # cv2.waitKey(3000)   #3초 동안 준비할 시간
    for i in range(len(words)):
        if i==idx:
            data = []
            for ix, cap in enumerate(caplist[i]):
                if cap!=0:  #cap이 있음  cap = caplist[0][0] ~caplist[0][1] ....caplist[104][0] ~caplist[104][1]
                    #print(f'"{i}":"{action}"')  #0: 0,
                    start_time = time.time()
                   
                    while time.time() - start_time < secs_for_action:  #0.8초동안
                        #print(cap)
                        ret, img = cap.read()
                        #img = cv2.flip(img, 1)
                        if not ret:
                            break
                        img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)
                        result = hands.process(img)
                        img = cv2.cvtColor(img, cv2.COLOR_RGB2BGR)

                        if result.multi_hand_landmarks is not None:
                            for res in result.multi_hand_landmarks:
                                joint = np.zeros((21, 4))
                                for j, lm in enumerate(res.landmark):
                                    joint[j] = [lm.x, lm.y, lm.z, lm.visibility]   #visibility는 손가락 노드가 이미지 상에서 보이는지 안보이는지 확률로 나타낸 것.

                                # Compute angles between joints
                                v1 = joint[[0,1,2,3,0,5,6,7,0,9,10,11,0,13,14,15,0,17,18,19], :3] # Parent joint
                                v2 = joint[[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20], :3] # Child joint
                                v = v2 - v1 # [20, 4] vector가 나옴, ex) 0과 1를 잇는 vector등 / 각 관절에 대한 벡터들
                                # Normalize v
                                v = v / np.linalg.norm(v, axis=1)[:, np.newaxis]  #벡터의 길이를 구하는 공식을 사용해 길이로 나눠줌 / 크기 1짜리 벡터

                                # Get angle using arcos of dot product
                                #각 벡터 간의 각도를 구함
                                angle = np.arccos(np.einsum('nt,nt->n',
                                    v[[0,1,2,4,5,6,8,9,10,12,13,14,16,17,18],:],
                                    v[[1,2,3,5,6,7,9,10,11,13,14,15,17,18,19],:])) # [15,]

                                angle = np.degrees(angle) # Convert radian to degree

                                angle_label = np.array([angle], dtype=np.float32)
                                angle_label = np.append(angle_label, idx) #idx: come=0, away=1, spin=2

                                d = np.concatenate([joint.flatten(), angle_label])  #angle 정보

                                data.append(d)

                                mp_drawing.draw_landmarks(img, res, mp_hands.HAND_CONNECTIONS)

                        #cv2.imshow('img', img)
#                         # if cv2.waitKey(1) == ord('q'):
#                         #     break
            #secs_for_action초 후에 / caplist[i]의 요소들 data 다 수집한 뒤
            data = np.array(data)
            print(action, data.shape)  #action마다 저장된 data의 0 (25, 100): 25/100
            np.save(os.path.join('dataset', f'raw_{action}_{created_time}'), data)   #저장

            # Create sequence data
            full_seq_data = []
            print("len of data: {}".format(len(data)))
            for seq in range(len(data) - seq_length):
                full_seq_data.append(data[seq:seq + seq_length])

            full_seq_data = np.array(full_seq_data)
            print(action, full_seq_data.shape)
            np.save(os.path.join('dataset', f'seq_{action}_{created_time}'), full_seq_data)
