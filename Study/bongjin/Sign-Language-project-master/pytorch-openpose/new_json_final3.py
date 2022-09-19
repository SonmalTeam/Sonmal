import cv2
import os
import copy
import numpy as np
import re
import argparse
import json
from collections import OrderedDict
from src import model
from src import util
from src.body import Body
from src.hand import Hand
from pathlib import Path
parser = argparse.ArgumentParser(
        description="Process a video annotating poses detected.")
parser.add_argument('--source', type=str, default='/content/drive/MyDrive/수화/code/LDI/10481_12656_frame', help='input_folder')
args = parser.parse_args()
path = args.source

file_data = OrderedDict()

body_estimation = Body('model/body_pose_model.pth')
hand_estimation = Hand('model/hand_pose_model.pth')

pathlist = Path('/content/drive/MyDrive/수화/code/LDI/10481_12656_frame').glob('**/*.jpg')

for test_image in pathlist:
    # run time 중지 방지
    if os.path.exists('/content/drive/MyDrive/수화/10481_12656_frame_openpose/' + test_image.name.rstrip('.jpg')
                      + '_keypoint.json'):
        print('file exists')
        continue
    else:
        print(test_image.name)
    # frame number 설정
    num = test_image.name.split('_')[3]
    num = re.sub(".jpg", "", num)
    if 30 <= int(num) <= 110:
        os.chdir(path)
        oriImg = cv2.imread(test_image.name)  # B,G,R order
        candidate, subset = body_estimation(oriImg)
        canvas = copy.deepcopy(oriImg)
        canvas = util.draw_bodypose(canvas, candidate, subset)
        # detect hand
        hands_list = util.handDetect(candidate, subset, oriImg)

        all_hand_peaks = []
        for x, y, w, is_left in hands_list:
            # cv2.rectangle(canvas, (x, y), (x+w, y+w), (0, 255, 0), 2, lineType=cv2.LINE_AA)
            # cv2.putText(canvas, 'left' if is_left else 'right', (x, y), cv2.FONT_HERSHEY_SIMPLEX, 1, (0, 0, 255), 2)

            # if is_left:
            # plt.imshow(oriImg[y:y+w, x:x+w, :][:, :, [2, 1, 0]])
            # plt.show()
            peaks = hand_estimation(oriImg[y:y + w, x:x + w, :])
            peaks[:, 0] = np.where(peaks[:, 0] == 0, peaks[:, 0], peaks[:, 0] + x)
            peaks[:, 1] = np.where(peaks[:, 1] == 0, peaks[:, 1], peaks[:, 1] + y)
            # else:
            #     peaks = hand_estimation(cv2.flip(oriImg[y:y+w, x:x+w, :], 1))
            #     peaks[:, 0] = np.where(peaks[:, 0]==0, peaks[:, 0], w-peaks[:, 0]-1+x)
            #     peaks[:, 1] = np.where(peaks
            #
            #   [:, 1]==0, peaks[:, 1], peaks[:, 1]+y)
            #     print(peaks)
            all_hand_peaks.append(peaks)

        file_data["original_frame"] = test_image.name

        if len(candidate) == 0:
            continue
        else:
            file_data["candidate"] = candidate.tolist()
            file_data["subset"] = subset.tolist()

        if len(all_hand_peaks) == 0:
            file_data["left_hand_key_point"] = [[0, 0], [0, 0], [0, 0], [0, 0], [0, 0], [0, 0], [0, 0], [0, 0], [0, 0],
                                                [0, 0],
                                                [0, 0], [0, 0], [0, 0], [0, 0], [0, 0], [0, 0], [0, 0], [0, 0], [0, 0],
                                                [0, 0], [0, 0]]
            file_data["right_hand_key_point"] = [[0, 0], [0, 0], [0, 0], [0, 0], [0, 0], [0, 0], [0, 0], [0, 0], [0, 0],
                                                 [0, 0],
                                                 [0, 0], [0, 0], [0, 0], [0, 0], [0, 0], [0, 0], [0, 0], [0, 0], [0, 0],
                                                 [0, 0], [0, 0]]
        elif len(all_hand_peaks) == 1:
            file_data["left_hand_key_point"] = all_hand_peaks[0].tolist()
            file_data["right_hand_key_point"] = [[0, 0], [0, 0], [0, 0], [0, 0], [0, 0], [0, 0], [0, 0], [0, 0], [0, 0],
                                                 [0, 0],
                                                 [0, 0], [0, 0], [0, 0], [0, 0], [0, 0], [0, 0], [0, 0], [0, 0], [0, 0],
                                                 [0, 0], [0, 0]]
        else:
            file_data["left_hand_key_point"] = all_hand_peaks[0].tolist()
            file_data["right_hand_key_point"] = all_hand_peaks[1].tolist()

        with open('/content/drive/MyDrive/수화/code/LDI/10481_12656_frame_openpose/' + test_image.name.rstrip(
                '.jpg') + '_keypoint.json', 'w', encoding="utf-8") as make_file:
            json.dump(file_data, make_file, ensure_ascii=False, indent="\t")
    else:
        continue