from collections import OrderedDict

import dlib, cv2
import mediapipe as mp
import os

POSE_PAIRS_FACE_68 = [[0, 1], [1, 2], [2, 3], [3, 4], [4, 5], [5, 6], [6, 7], [7, 8], [8, 9], [9, 10], [10, 11],
                      [11, 12], [12, 13], [13, 14], [14, 15], [15, 16], [17, 18], [18, 19], [19, 20], [20, 21], [22, 23],
                      [23, 24], [24, 25], [25, 26], [27, 28], [28, 29], [29, 30], [31, 32], [32, 33], [33, 34], [34, 35],
                      [36, 37], [37, 38], [38, 39], [39, 40], [40, 41], [41, 36], [42, 43], [43, 44], [44, 45], [45, 46],
                      [46, 47], [47, 42], [48, 49], [49, 50], [50, 51], [51, 52], [52, 53], [53, 54], [54, 55], [55, 56],
                      [56, 57], [57, 58], [58, 59], [59, 48], [60, 61], [61, 62], [62, 63], [63, 64], [64, 65], [65, 66],
                      [66, 67], [67, 60]]



detector = dlib.get_frontal_face_detector()
sp = dlib.shape_predictor('shape_predictor_68_face_landmarks.dat')

mpHands = mp.solutions.hands
hands = mpHands.Hands()
mpDraw = mp.solutions.drawing_utils
mp_holistic = mp.solutions.holistic
holistic_model = mp_holistic.Holistic(
    static_image_mode=False,
    min_detection_confidence=0.5,
    min_tracking_confidence=0.5
)

#cam = cv2.VideoCapture('test.mp4')
cam = cv2.VideoCapture(0)



while True:
    now_frame_boy = cam.get(cv2.CAP_PROP_POS_FRAMES)
    total_frame_boy = cam.get(cv2.CAP_PROP_FRAME_COUNT)

    if now_frame_boy == total_frame_boy:
        break

    img, frame = cam.read()
    face = detector(frame)
    #print("face")
    #print(face)
    for f in face:
        #dlib으로 얼굴 검출
        #cv2.rectangle(frame, (f.left(), f.top()), (f.right(), f.bottom()), (0,0,255), 2)
        #print("f")
        #print(f)
        land = sp(frame, f)
        land_list = []
        #print("land")
        #print(land)

        points = []
        for l in land.parts():
            land_list.append([l.x, l.y])
            #cv2.circle(frame, (l.x, l.y), 3, (255,0,0), -1)
            points.append((l.x, l.y))

        for pair in POSE_PAIRS_FACE_68:
            partA = pair[0]  # 0
            partB = pair[1]  # 1

            # partA와 partB 사이에 선을 그어줌 (cv2.line)
            if points[partA] and points[partB]:
                cv2.line(frame, points[partA], points[partB], (0, 255, 0), 1)

    imgRGB = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
    results = holistic_model.process(imgRGB)
    # print(results.multi_hand_landmarks)

    if results.right_hand_landmarks:
        # print(results.right_hand_landmarks)
        mpDraw.draw_landmarks(frame, results.right_hand_landmarks, mp_holistic.HAND_CONNECTIONS)

        right_keypoint_pos = []
        for rightHandLms in results.right_hand_landmarks.landmark:
            print(rightHandLms)
            h, w, c = frame.shape
            cx, cy = int(rightHandLms.x * w), int(rightHandLms.y * h)
            # print(cx,cy)
            right_keypoint_pos.append((cx, cy))
        print("right hand")
        print(right_keypoint_pos)

    if results.left_hand_landmarks:
        mpDraw.draw_landmarks(frame, results.left_hand_landmarks, mp_holistic.HAND_CONNECTIONS)

        left_keypoint_pos = []
        for leftHandLms in results.left_hand_landmarks.landmark:
            print(leftHandLms)
            h, w, c = frame.shape
            cx, cy = int(leftHandLms.x * w), int(leftHandLms.y * h)
            # print(cx,cy)
            left_keypoint_pos.append((cx, cy))
        print("left hand")
        print(left_keypoint_pos)

    cv2.imshow('A',frame)

    if cv2.waitKey(1)=='q':
        break

cam.release()
cv2.destroyAllWindows()