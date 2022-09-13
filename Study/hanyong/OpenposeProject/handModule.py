import cv2
import mediapipe as mp
import time

cam = cv2.VideoCapture(0)

mpHands = mp.solutions.hands
hands = mpHands.Hands()
mpDraw = mp.solutions.drawing_utils
mp_drawing_styles = mp.solutions.drawing_styles

mp_holistic = mp.solutions.holistic
holistic_model = mp_holistic.Holistic(
    static_image_mode=False,
    min_detection_confidence=0.5,
    min_tracking_confidence=0.5
)

while True:
    img, frame = cam.read()

    imgRGB = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
    results = holistic_model.process(imgRGB)
    # print(results.multi_hand_landmarks)

    if results.right_hand_landmarks:
        print("right hand")
        #print(results.right_hand_landmarks)
        mpDraw.draw_landmarks(frame, results.right_hand_landmarks, mp_holistic.HAND_CONNECTIONS)

        right_keypoint_pos = []
        for rightHandLms in results.right_hand_landmarks.landmark:
            print(rightHandLms)
            h,w,c = frame.shape
            cx, cy = int(rightHandLms.x*w), int(rightHandLms.y*h)
            #print(cx,cy)
            right_keypoint_pos.append((cx,cy))
        print(right_keypoint_pos)

    if results.left_hand_landmarks:
        print("left hand")
        mpDraw.draw_landmarks(frame, results.left_hand_landmarks, mp_holistic.HAND_CONNECTIONS)

        left_keypoint_pos = []
        for leftHandLms in results.left_hand_landmarks.landmark:
            print(leftHandLms)
            h, w, c = frame.shape
            cx, cy = int(leftHandLms.x * w), int(leftHandLms.y * h)
            # print(cx,cy)
            left_keypoint_pos.append((cx, cy))
        print(left_keypoint_pos)

    cv2.imshow("Image", frame)
    cv2.waitKey(1)