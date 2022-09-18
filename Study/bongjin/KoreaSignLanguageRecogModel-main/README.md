## Hand Gesture Recognition

Deep learning based hand gesture recognition using LSTM and MediaPipie.

## Data set
![image](https://user-images.githubusercontent.com/73246476/166186708-7036d512-6395-4859-8864-2c2c91b31604.png)
https://aihub.or.kr/opendata/keti-data/recognition-laguage/KETI-02-003

## Process
![image](https://user-images.githubusercontent.com/73246476/166186774-368a9b28-49db-4007-b4f1-c67419beb5c2.png)

## Result

![image](https://user-images.githubusercontent.com/73246476/166186644-67b1eb12-d03f-4e8a-b19c-635a82da13d4.png)
![image](https://user-images.githubusercontent.com/73246476/166186654-dcc7c5fa-6341-4d95-95ea-2ef68d4f646d.png)


## Files

Pretrained model in *models* directory.

**create_dataset.py**

Collect dataset from korean hand language data set from AI Hub

**train.py**

Create and train the model using collected dataset.

**test.py**

Test the model using webcam or video.

## Dependency

- Python 3
- TensorFlow 2.4
- numpy
- OpenCV
- MediaPipe
"# KoreaSignLanguageRecogModel" 
