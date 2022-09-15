# -*- coding: utf-8 -*-

################################################################################
## Form generated from reading UI file 'first.ui'
##
## Created by: Qt User Interface Compiler version 5.15.2
##
## WARNING! All changes made in this file will be lost when recompiling UI file!
################################################################################
from PySide2 import QtCore, QtGui, QtWidgets
from PySide2.QtCore import *
from PySide2.QtGui import *
from PySide2.QtWidgets import *
import numpy.core.multiarray

import cv2
import sys
import qimage2ndarray

import tensorflow as tf
import numpy as np
import threading
from time import sleep


class Ui_MainWindow(object):
    def setupUi(self, MainWindow):
        if not MainWindow.objectName():
            MainWindow.setObjectName(u"MainWindow")
        MainWindow.resize(510, 540)
        self.centralwidget = QWidget(MainWindow)
        self.centralwidget.setObjectName(u"centralwidget")
        self.pushButton = QPushButton(self.centralwidget)
        self.pushButton.setObjectName(u"pushButton")
        self.pushButton.setGeometry(QRect(310, 90, 111, 28))
        self.pushButton.clicked.connect(self.bFunc)

        self.pushButton2 = QPushButton(self.centralwidget)
        self.pushButton2.setText('Clear')
        self.pushButton2.setGeometry(QRect(430, 90, 50, 28))
        self.pushButton2.clicked.connect(self.clearF)

        self.video_size = QSize(270,480)
        self.image_label = QLabel('Video Panel',self.centralwidget)
        self.image_label.setFixedSize(self.video_size)
        self.image_label.setGeometry(QRect(10,20,270,480))
        self.image_label.setAlignment(Qt.AlignCenter)
        self.image_label.setFrameStyle(QFrame.Panel)
        font = QFont()
        font.setFamily(u"D2Coding")
        font.setPointSize(24)
        font.setBold(True)
        self.image_label.setFont(font)


        #self.setup_camera()
        # self.graphicsView = QGraphicsView(self.centralwidget)
        # self.graphicsView.setObjectName(u"graphicsView")
        # self.graphicsView.setGeometry(QRect(10, 20, 270, 480))

        self.checkBox = QCheckBox(self.centralwidget)
        self.checkBox.setObjectName(u"checkBox")
        self.checkBox.setGeometry(QRect(310, 60, 110, 20))
        self.checkBox.stateChanged.connect(self.checkBoxChange)
        #self.checkBox.toggle()
        self.label = QLabel(self.centralwidget)
        #self.label.setObjectName(u"label")
        self.label.setText("-Label-")
        self.label.setGeometry(QRect(300, 170, 190, 50))
        font = QFont()
        font.setFamily(u"D2Coding")
        font.setPointSize(22)
        font.setBold(True)
        font.setWeight(150)
        self.label.setFont(font)

        self.label2 = QLabel(self.centralwidget)
        self.label2.setGeometry(QRect(310,280,160,220))
        font = QFont()
        font.setFamily(u"D2Coding")
        font.setPointSize(12)
        self.label2.setFont(font)
        self.label2.setText("hello:\nhot:\nthanks\ncoffee:\n포장:\nnone:")


        # self.tableWidget = QTableWidget(self.centralwidget)
        # self.tableWidget.setObjectName(u"tableWidget")
        # self.tableWidget.setGeometry(QRect(300, 270, 190, 230))

        MainWindow.setCentralWidget(self.centralwidget)
        self.statusbar = QStatusBar(MainWindow)
        self.statusbar.setObjectName(u"statusbar")
        MainWindow.setStatusBar(self.statusbar)

        self.retranslateUi(MainWindow)

        QMetaObject.connectSlotsByName(MainWindow)
    # setupUi

    def retranslateUi(self, MainWindow):
        MainWindow.setWindowTitle(QCoreApplication.translate("MainWindow", u"SIgn Language", None))
#if QT_CONFIG(tooltip)
        self.pushButton.setToolTip(QCoreApplication.translate("MainWindow", u"\uc601\uc0c1 \ud30c\uc77c\uc744 \ubd88\ub7ec\uc635\ub2c8\ub2e4.", None))
#endif // QT_CONFIG(tooltip)
        self.pushButton.setText(QCoreApplication.translate("MainWindow", u"\uc601\uc0c1 \ubd88\ub7ec\uc624\uae30", None))
        self.checkBox.setText(QCoreApplication.translate("MainWindow", u"\uce74\uba54\ub77c \uc774\uc6a9", None))
        #self.label.setText(QCoreApplication.translate("MainWindow", u"TextLabel", None))
    # retranslateUi

    # def sum_of_label(self, temp):
    #     dic = {}
    #     dic['hello'] = (temp[0] + temp[6] + temp[7] + temp[14])/4
    #     dic['hot'] = (temp[3] + temp[11])/2
    #     dic['thanks'] = (temp[4] + temp[8])/2
    #     dic['americano'] = (temp[2] + temp[10])/2
    #     dic['packing'] = (temp[5] + temp[9] + temp[12] + temp[13])/4
    #     dic['none'] = temp[1]
    #     max = 0
    #     for label, value in dic.items():
    #         if value > max:
    #             max_label = label
    #             max = value
    #     self.label.setText('%s:%f'%(max_label,max)) 

    def clearF(self):

        self.capture.release()
        self.image_label.setText('Video Panel')
        self.checkBox.setChecked(False)
        self.label2.setText("hello:\nhot:\nthanks\ncoffee:\n포장:\nnone:")
        self.label.setText("-Label-")

    def checkBoxChange(self,state):

        if state == Qt.Checked:
            #self.capture.release()
            tc = threading.Thread(target=self.setup_camera1)
            tc.daemon = True
            tc.start()
            #self.setup_camera()
        elif state != Qt.Checked:
            #self.timer.stop()
            self.capture.release()
            self.image_label.setText('Video Panel')
            self.label2.setText("hello:\nhot:\nthanks\ncoffee:\n포장:\nnone:")
            self.label.setText("-Label-")
        
    def setup_camera1(self):
        """Initialize camera.
        """
       
        #self.capture = cv2.VideoCapture('C:/Users/degdo/Desktop/practice/KNU_AutoML/KNUAUTOML/python_program/test.mov')
        self.capture = cv2.VideoCapture(0)
        #self.capture.set(cv2.CAP_PROP_FRAME_WIDTH, self.video_size.width())
        #self.capture.set(cv2.CAP_PROP_FRAME_HEIGHT, self.video_size.height())
        
        while True:
            a, frame = self.capture.read()
            if a:
                frame = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
                frame = cv2.flip(frame, 1)
                copy_frame = frame.copy()
                frame = copy_frame[:,320-135:320+135] 

                t2 = threading.Thread(target=self.labelf,args=(frame,))
                t2.daemon = True
                t2.start()

                image = qimage2ndarray.array2qimage(frame)  #SOLUTION FOR MEMORY LEAK
                self.image_label.setPixmap(QPixmap.fromImage(image))
                self.image_label.update()
                
                #sleep(0.001)

            else:
                break
        self.capture.release()
        self.image_label.setText('Video Panel')



    # def setup_camera(self):
    #     """Initialize camera.
    #     """

    #     self.capture = cv2.VideoCapture(0)
    #     #self.capture.set(cv2.CAP_PROP_FRAME_WIDTH, self.video_size.width())
    #     #self.capture.set(cv2.CAP_PROP_FRAME_HEIGHT, self.video_size.height())

    #     self.timer = QTimer()
    #     self.timer.timeout.connect(self.display_video_stream)
    #     self.timer.start(30)


    # def display_video_stream(self):
    #     """Read frame from camera and repaint QLabel widget.
    #     """
    #     _, frame = self.capture.read()
    #     frame = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
    #     frame = cv2.flip(frame, 1)
    #     copy_frame = frame.copy()
    #     frame = copy_frame[:,320-135:320+135] 
    #     # #
    #     # img = cv2.resize(frame,dsize=(224,224))
    #     # img = np.uint8(img)
    #     # img = img[np.newaxis]
    #     # interpreter.set_tensor(input_details[0]['index'], img)
    #     # interpreter.invoke()
    #     # output_data = interpreter.get_tensor(output_details[0]['index'])
    #     # temp = output_data[0].tolist()
    #     # #print(tolist1[9])
    #     # #self.label.setText('%d'%tolist1[9])
    #     # #sum_of_label(self, tolist1)
    #     # # 레이블 출력
    #     # dic = {}
    #     # dic['hello'] = (temp[0] + temp[6] + temp[7] + temp[14])/4
    #     # dic['hot'] = (temp[3] + temp[11])/2
    #     # dic['thanks'] = (temp[4] + temp[8])/2
    #     # dic['americano'] = (temp[2] + temp[10])/2
    #     # dic['packing'] = (temp[5] + temp[9] + temp[12] + temp[13])/4
    #     # dic['none'] = temp[1]
    #     # max = 0
    #     # for label, value in dic.items():
    #     #     if value > max:
    #     #         max_label = label
    #     #         max = value
    #     # self.label.setText('%s'%max_label) #:\n%f  ,max)
    #     # # di_key = dic.keys()
    #     # # di_val = dic.values()
    #     # # self.tableWidget.setItem(0,0,QTableWidgetItem('%s'%di_key[0]))
    #     # # self.tableWidget.setItem(0,1,QTableWidgetItem("%s"%di_val[0]))
    #     # ##
    #     # #
        
    
    #     image = qimage2ndarray.array2qimage(frame)  #SOLUTION FOR MEMORY LEAK
    #     self.image_label.setPixmap(QPixmap.fromImage(image))
    

    def bFunc(self):
        #self.capture.release()
        filename = QFileDialog.getOpenFileName(MainWindow,"open",'./')
        t1 = threading.Thread(target=self.setup_camera2,args=(filename[0],))
        t1.daemon = True
        t1.start()

    def setup_camera2(self, fname):
        """Initialize camera.
        """
       
        #self.capture = cv2.VideoCapture('C:/Users/degdo/Desktop/practice/KNU_AutoML/KNUAUTOML/python_program/test.mov')
        self.capture = cv2.VideoCapture(fname)
        #self.capture.set(cv2.CAP_PROP_FRAME_WIDTH, self.video_size.width())
        #self.capture.set(cv2.CAP_PROP_FRAME_HEIGHT, self.video_size.height())
        
        while True:
            a, frame = self.capture.read()
            if a:
                frame = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
                #frame = cv2.flip(frame, 1)
                frame = cv2.rotate(frame,cv2.ROTATE_90_CLOCKWISE)
                frame = cv2.resize(frame, dsize=(270,480))    

                t2 = threading.Thread(target=self.labelf,args=(frame,))
                t2.daemon = True
                t2.start()

                image = qimage2ndarray.array2qimage(frame)  #SOLUTION FOR MEMORY LEAK
                self.image_label.setPixmap(QPixmap.fromImage(image))
                self.image_label.update()
                
                #sleep(0.001)

            else:
                break
        self.capture.release()
        self.image_label.setText('Video Panel')

    def labelf(self, frame):
        interpreter = tf.lite.Interpreter(model_path="C:/SSAFY/SignLanguage/Study/bongjin/KNUAUTOML-master/python_program/1204_model-export_icn_tflite-vision_model_edge2_high-2020-12-04T03_01_01.444480Z_model.tflite")
        interpreter.allocate_tensors()
        img = cv2.resize(frame,dsize=(224,224))
        img = np.uint8(img)
        img = img[np.newaxis]
        interpreter.set_tensor(input_details[0]['index'], img)
        interpreter.invoke()
        output_data = interpreter.get_tensor(output_details[0]['index'])
        temp = output_data[0].tolist()
        #print(tolist1[9])
        #self.label.setText('%d'%tolist1[9])
        #sum_of_label(self, tolist1)
        # 레이블 출력
        dic = {}
        dic['hello'] = max(temp[0] , temp[6] , temp[7] , temp[14])
        dic['hot'] = max(temp[3] , temp[11])
        dic['thanks'] = max(temp[4] , temp[8])
        dic['coffee'] = max(temp[2] , temp[10])
        dic['포장'] = max(temp[5] , temp[9] , temp[12] , temp[13])
        dic['none'] = temp[1]
        maxv = 0
        for label, value in dic.items():
            if value > maxv:
                max_label = label
                maxv = value
        self.label.setText('%s'%max_label) #:\n%f  ,max)
        self.label2.setText("hello:\t%d\nhot:\t%d\nthanks:\t%d\ncoffee:\t%d\n포장:\t%d\nnone:\t%d"%(dic['hello'],dic['hot'],dic['thanks'],dic['coffee'],dic['포장'],dic['none']))
    
    

if __name__ == "__main__":

    interpreter = tf.lite.Interpreter(model_path="C:/SSAFY/SignLanguage/Study/bongjin/KNUAUTOML-master/python_program/1204_model-export_icn_tflite-vision_model_edge2_high-2020-12-04T03_01_01.444480Z_model.tflite")
    interpreter.allocate_tensors()
    input_details = interpreter.get_input_details()
    output_details = interpreter.get_output_details()

    app = QtWidgets.QApplication(sys.argv)
    app.setStyle(QStyleFactory.create('oxygen'))
    MainWindow = QtWidgets.QMainWindow()
    ui = Ui_MainWindow()
    ui.setupUi(MainWindow)
    MainWindow.show()
    sys.exit(app.exec_())