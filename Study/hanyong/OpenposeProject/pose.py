import cv2


BODY_PARTS_BODY_25 = {0: "Nose", 1: "Neck", 2: "RShoulder", 3: "RElbow", 4: "RWrist",
                      5: "LShoulder", 6: "LElbow", 7: "LWrist", 8: "MidHip", 9: "RHip",
                      10: "RKnee", 11: "RAnkle", 12: "LHip", 13: "LKnee", 14: "LAnkle",
                      15: "REye", 16: "LEye", 17: "REar", 18: "LEar", 19: "LBigToe",
                      20: "LSmallToe", 21: "LHeel", 22: "RBigToe", 23: "RSmallToe", 24: "RHeel", 25: "Background"}

POSE_PAIRS_BODY_25 = [[0, 1], [0, 15], [0, 16], [1, 2], [1, 5], [1, 8], [8, 9], [8, 12], [9, 10], [12, 13], [2, 3],
                      [3, 4], [5, 6], [6, 7], [10, 11], [13, 14], [15, 17], [16, 18], [14, 21], [19, 21], [20, 21],
                      [11, 24], [22, 24], [23, 24]]

# 각 파일 path
protoFile = "pose_deploy.prototxt"
weightsFile = "pose_iter_584000.caffemodel"

# 위의 path에 있는 network 불러오기
net = cv2.dnn.readNetFromCaffe(protoFile, weightsFile)


# 키포인트를 저장할 빈 리스트
points = []

cam = cv2.VideoCapture('test.mp4')
#cam = cv2.VideoCapture(0)

inputWidth = 320;
inputHeight = 240;
inputScale = 1.0 / 255;

# 반복문을 통해 카메라에서 프레임을 지속적으로 받아옴
while cv2.waitKey(1) < 0:  # 아무 키나 누르면 끝난다.
    # 웹캠으로부터 영상 가져옴
    hasFrame, frame = cam.read()

    # 영상이 커서 느리면 사이즈를 줄이자
    # frame=cv2.resize(frame,dsize=(320,240),interpolation=cv2.INTER_AREA)

    # 웹캠으로부터 영상을 가져올 수 없으면 웹캠 중지
    if not hasFrame:
        cv2.waitKey()
        break

    #
    frameWidth = frame.shape[1]
    frameHeight = frame.shape[0]

    inpBlob = cv2.dnn.blobFromImage(frame, inputScale, (inputWidth, inputHeight), (0, 0, 0), swapRB=False, crop=False)

    imgb = cv2.dnn.imagesFromBlob(inpBlob)
    # cv2.imshow("motion",(imgb[0]*255.0).astype(np.uint8))

    # network에 넣어주기
    net.setInput(inpBlob)

    # 결과 받아오기
    output = net.forward()

    # 키포인트 검출시 이미지에 그려줌
    points = []
    for i in range(0, 25):
        # 해당 신체부위 신뢰도 얻음.
        probMap = output[0, i, :, :]

        # global 최대값 찾기
        minVal, prob, minLoc, point = cv2.minMaxLoc(probMap)

        # 원래 이미지에 맞게 점 위치 변경
        x = (frameWidth * point[0]) / output.shape[3]
        y = (frameHeight * point[1]) / output.shape[2]

        # 키포인트 검출한 결과가 0.1보다 크면(검출한곳이 위 BODY_PARTS랑 맞는 부위면) points에 추가, 검출했는데 부위가 없으면 None으로
        if prob > 0.1:
            #cv2.circle(frame, (int(x), int(y)), 3, (0, 255, 255), thickness=-1,
                       #lineType=cv2.FILLED)  # circle(그릴곳, 원의 중심, 반지름, 색)
            #cv2.putText(frame, "{}".format(i), (int(x), int(y)), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 0, 255), 1,
                        #lineType=cv2.LINE_AA)
            points.append((int(x), int(y)))
        else:
            points.append(None)

    # 각 POSE_PAIRS별로 선 그어줌 (머리 - 목, 목 - 왼쪽어깨, ...)
    print(points)
    for pair in POSE_PAIRS_BODY_25:
        partA = pair[0]  # 0
        partB = pair[1]  # 1

        # partA와 partB 사이에 선을 그어줌 (cv2.line)
        if points[partA] and points[partB]:
            cv2.line(frame, points[partA], points[partB], (26,160,239), 1)

    cv2.imshow("Output-Keypoints", frame)

cam.release()
cv2.destroyAllWindows()