from __future__ import print_function

import time
import sys

import numpy as np
import os

## from Sign Language code

import json
import glob
import matplotlib.pyplot as plt

from PIL import Image, ImageDraw, ImageFont, ImageTk
import PIL.Image, PIL.ImageTk

from random import randrange

import io
import pandas as pd
import h5py

import tkinter

from tkinter import Canvas, LabelFrame, Button, Scale, HORIZONTAL, Checkbutton, Label, StringVar, BooleanVar, DoubleVar, \
    filedialog
from tkinter.ttk import *

from keras.models import model_from_json

import random

import grpc

import openpose_pb2
import openpose_pb2_grpc

import cv2
import pickle

import threading

from collections import deque

import copy

import math

font_size = 30
font_color = (255, 255, 255)

height = 960
width = 1280


def keypoints(filename, part='pose'):
    data_file = open(filename)
    data = json.load(data_file)
    keypoints = data["people"][0][part + '_keypoints']
    num_keypoints = len(keypoints)

    li = range(num_keypoints)
    sub = li[0::3]

    x = []
    y = []
    confidence = []

    for index in sub:
        x_coordinate = keypoints[index]
        y_coordinate = keypoints[index + 1]
        conf = keypoints[index + 2]

        x.append(x_coordinate)
        y.append(y_coordinate)
        confidence.append(conf)

    x = np.array(x)
    y = np.array(y)
    confidence = np.array(confidence)

    return x, y, confidence


def normalize_array(input_array):
    normalized_array = (input_array - input_array.mean() - 0.0000001) / (input_array.std() - 0.0000001)
    return normalized_array


def extract_keyframe_information(label_num, person_id, direction):
    frame_keypoint_file_path = []

    for t in range(1000):
        frame_keypoint_file_path += glob.glob(
            '../../../test_data/output/' + direction + '/' + str(label_num) + '_' + str(person_id) + '/KETI_SL_' + str(
                label_num) + '_' + str(person_id) + '_00000000' + str(t).zfill(4) + '_keypoints.json')

        fnum = len(frame_keypoint_file_path)

    frames_pose_keypoints = []
    frames_hand_keypoints = []
    frames_face_keypoints = []

    for frame in range(fnum):
        frame_pose_keypoints_x, frame_pose_keypoints_y, frame_pose_confidence = keypoints(
            frame_keypoint_file_path[frame], 'pose')
        frames_pose_keypoints.append([frame_pose_keypoints_x, frame_pose_keypoints_y, frame_pose_confidence])

        frame_right_hand_keypoints_x, frame_right_hand_keypoints_y, frame_right_hand_confidence = keypoints(
            frame_keypoint_file_path[frame], 'hand_right')
        frame_left_hand_keypoints_x, frame_left_hand_keypoints_y, frame_left_hand_confidence = keypoints(
            frame_keypoint_file_path[frame], 'hand_left')
        frames_hand_keypoints.append(
            [frame_right_hand_keypoints_x, frame_right_hand_keypoints_y, frame_right_hand_confidence,
             frame_left_hand_keypoints_x, frame_left_hand_keypoints_y, frame_left_hand_confidence])

        frame_face_keypoints_x, frame_face_keypoints_y, frame_face_confidence = keypoints(
            frame_keypoint_file_path[frame], 'face')
        frames_face_keypoints.append([frame_face_keypoints_x, frame_face_keypoints_y, frame_face_confidence])

        # start, final = finding_start_and_final_by_hand_position(frames_pose_keypoints)

    start, final = finding_start_and_final_by_arm_angle2(frames_pose_keypoints)

    return frames_pose_keypoints, frames_hand_keypoints, start, final


def finding_start_and_final_by_arm_angle2(frames_pose_keypoints):
    fnum = len(frames_pose_keypoints)

    frame_difference = []
    frame_indices = []
    frame_armangles = []

    for frame in range(fnum - 1):

        while np.mean(frames_pose_keypoints[frame][2]) < 0.4:
            frame = frame + 1
            if frame == fnum:
                break

        if frame == fnum:
            break

        current_frame_x = frames_pose_keypoints[frame][0]
        current_frame_y = frames_pose_keypoints[frame][1]

        current_x_y_ratio = (current_frame_y[3] - current_frame_y[4]) / np.abs(current_frame_x[3] - current_frame_x[4])

        current_arm_angle = np.arctan(current_x_y_ratio)
        frame_armangles.append(current_arm_angle)

    frame_armangles = np.maximum(np.array(frame_armangles) + 1, 0)

    threshold = 0.5

    start = -1
    last = 0

    while np.sum(frame_armangles[start:last]) / np.sum(frame_armangles) < 0.9:

        threshold = threshold - 0.01

        start = -1
        last = 0

        for index in range(0, len(frame_armangles)):
            if frame_armangles[index] > threshold and start == -1:
                start = index
            elif frame_armangles[index] > threshold:
                last = index

    # plt.plot(frame_armangles)
    # plt.plot([start, start], [0, np.max(frame_armangles)], 'k-')
    # plt.plot([last, last], [0, np.max(frame_armangles)], 'k-')
    # plt.plot([0, fnum], [threshold, threshold], 'k-')
    # plt.show()

    return start, last


# pose_keypoints, hand_keypoints, start_frame, last_frame = extract_keyframe_information(0, 1, 'front')


def sampling_normalized_keypoints(keypoint_frames, num_samples, start, last):
    margin = 0

    num_frames = len(keypoint_frames[1])

    start = np.maximum(start - margin, 0)
    last = np.minimum(last + margin, num_frames - 1)

    num_keyframes = last - start + 1

    interval_size = int(num_keyframes / (num_samples - 1))

    sampled_frames = []

    starting_gap = int((last - (start + (num_samples - 1) * interval_size)) / 2)

    print("Starting index: ", start + starting_gap)
    for index in range(num_samples):
        current_frame = keypoint_frames[1][start + starting_gap + (interval_size * index)]

        sampled_frame = []
        sampled_frame.append(normalize_array(current_frame[0]))
        sampled_frame.append(normalize_array(current_frame[1]))
        sampled_frame.append(normalize_array(current_frame[2]))

        sampled_frame_hand = np.concatenate([sampled_frame[0], sampled_frame[1], sampled_frame[2]])

        current_frame = keypoint_frames[0][start + starting_gap + (interval_size * index)]

        sampled_frame = []
        sampled_frame.append(normalize_array(current_frame[0]))
        sampled_frame.append(normalize_array(current_frame[1]))
        sampled_frame.append(normalize_array(current_frame[2]))

        sampled_frame_pose = np.concatenate([sampled_frame[0], sampled_frame[1], sampled_frame[2]])

        sampled_frames.append(np.concatenate([sampled_frame_hand, sampled_frame_pose]))

    print(sampled_frames[0].shape)

    return np.array(sampled_frames)


os.environ["CUDA_VISIBLE_DEVICES"] = ""

json_file = open('sentence_model_0509_248.json', 'r')
loaded_model_json = json_file.read()
json_file.close()

loaded_model = model_from_json(loaded_model_json)
loaded_model.load_weights("sentence_model_0509_248.h5")
print("Loaded model from disk")

os.environ["CUDA_VISIBLE_DEVICES"] = "0"

download_heatmaps = False


def showHeatmaps(hm):
    for idx, h in enumerate(hm):
        cv2.imshow("HeatMap " + str(idx), h)


def showPAFs(PAFs, startIdx=0, endIdx=16):
    allpafs = []
    for idx in range(startIdx, endIdx):
        X = PAFs[idx * 2]
        Y = PAFs[idx * 2 + 1]
        tmp = np.dstack((X, Y, np.zeros_like(X)))
        allpafs.append(tmp)

    pafs = np.mean(allpafs, axis=0)
    cv2.imshow("PAF", pafs)


# Sang-Ki Ko's customized codes

def choosePeopleToShow(faces):
    # print("how many: " + str(len(faces)))
    if len(faces) == 0:
        return -1, 0

    confidence = []

    for idx in range(len(faces)):
        confidence.append(averageConfidence(faces[idx]))

    return np.argmax(confidence), confidence[np.argmax(confidence)]


def averageY(keypoints):
    accum = 0
    for idx in range(len(keypoints)):
        accum = accum + keypoints[idx][1]

    return accum / len(keypoints)


def averageConfidence(keypoints):
    accum = 0
    for idx in range(len(keypoints)):
        accum = accum + keypoints[idx][2]

    return accum / len(keypoints)


def rightHandUp(pose):
    if (pose[3][0] - pose[4][0] != 0):
        xy_ratio = (pose[3][1] - pose[4][1]) / np.abs(pose[3][0] - pose[4][0])
    else:
        xy_ratio = (pose[3][1] - pose[4][1]) / 0.000001

    arm_angle = np.arctan(xy_ratio)

    # print(pose[3][1], pose[4][1], pose[3][0], pose[4][0])

    # print(xy_ratio)
    # print(arm_angle)

    if (pose[4][0] == 0 or pose[4][1] == 0):  # if hand is not shown in the frame
        return False

    return arm_angle > -0.8


def rightArmAngle(pose):
    result = np.arctan2(pose[4][1] - pose[3][1], pose[4][0] - pose[3][0]) - np.arctan2(pose[2][1] - pose[3][1],
                                                                                       pose[2][0] - pose[3][0])
    if result > math.pi:
        result = 2 * math.pi - result

    return np.abs(result)


def leftArmAngle(pose):
    result = np.arctan2(pose[7][1] - pose[6][1], pose[7][0] - pose[6][0]) - np.arctan2(pose[5][1] - pose[6][1],
                                                                                       pose[5][0] - pose[6][0])
    if result > math.pi:
        result = 2 * math.pi - result

    return np.abs(result)


def leftHandUp(pose):
    if (pose[6][0] - pose[7][0] != 0):
        xy_ratio = (pose[6][1] - pose[7][1]) / np.abs(pose[6][0] - pose[7][0])
    else:
        xy_ratio = (pose[6][1] - pose[7][1]) / 0.000001

    arm_angle = np.arctan(xy_ratio)

    # print(pose[6][1], pose[7][1], pose[6][0], pose[7][0])

    # print(xy_ratio)
    # print(arm_angle)

    if (pose[7][0] == 0 or pose[7][1] == 0):  # if hand is not shown in the frame
        return False

    return arm_angle > -0.3


def handUp(pose, rHand, lHand):
    # print("rHand Confidence: ", averageY(rHand))
    # print("lHand Confidence: ", averageY(lHand))

    if (averageY(rHand) > height or averageY(rHand) == 0) or (averageY(lHand) > height or averageY(lHand) == 0):
        return False

    if (rightArmAngle(pose) < 1.0 or leftArmAngle(pose) < 1.0):
        print(leftArmAngle(pose), " | ", rightArmAngle(pose))
        return True
    else:
        return False

    if leftHandUp(pose) or rightHandUp(pose):
        return True
    else:
        return False


def normalizeFrameInfo(pose, face, rHand, lHand):
    # print(rHand)

    # print(rHand.mean(axis=0))
    # print(rHand - rHand.mean(axis=0))
    rHandX = []
    rHandY = []
    rHandConf = []

    lHandX = []
    lHandY = []
    lHandConf = []

    poseX = []
    poseY = []
    poseConf = []

    faceX = []
    faceY = []
    faceConf = []

    for idx in [0, 1, 2, 3, 4, 5, 6, 7, 14, 15, 16, 17]:
        poseX.append(pose[idx][0])
        poseY.append(pose[idx][1])
        poseConf.append(pose[idx][2])

    for idx in range(len(rHand)):
        rHandX.append(rHand[idx][0])
        rHandY.append(rHand[idx][1])
        rHandConf.append(rHand[idx][2])

        lHandX.append(lHand[idx][0])
        lHandY.append(lHand[idx][1])
        lHandConf.append(lHand[idx][2])

    for idx in range(len(face)):
        faceX.append(face[idx][0])
        faceY.append(face[idx][1])
        faceConf.append(face[idx][2])

    '''
    rHandX_mean = np.mean(np.asarray(rHandX))
    lHandX_mean = np.mean(np.asarray(lHandX))
    rHandY_mean = np.mean(np.asarray(rHandY))
    lHandY_mean = np.mean(np.asarray(lHandY))
    rHandConf_mean = np.mean(np.asarray(rHandConf))
    lHandConf_mean = np.mean(np.asarray(lHandConf))

    faceX_mean = np.mean(np.asarray(faceX))
    faceY_mean = np.mean(np.asarray(faceY))
    faceConf_mean = np.mean(np.asarray(faceConf))

    total_mean = []
    total_mean.append(normalize_array(np.array([faceX_mean, rHandX_mean, lHandX_mean])))
    total_mean.append(normalize_array(np.array([faceY_mean, rHandY_mean, lHandY_mean])))
    total_mean.append(normalize_array(np.array([faceConf_mean, rHandConf_mean, lHandConf_mean])))
    '''

    rHandX = normalize_array(np.asarray(rHandX))
    rHandY = normalize_array(np.asarray(rHandY))
    rHandConf = normalize_array(np.asarray(rHandConf))
    lHandX = normalize_array(np.asarray(lHandX))
    lHandY = normalize_array(np.asarray(lHandY))
    lHandConf = normalize_array(np.asarray(lHandConf))

    poseX = normalize_array(np.asarray(poseX))
    poseY = normalize_array(np.asarray(poseY))
    poseConf = normalize_array(np.asarray(poseConf))

    faceX = normalize_array(np.asarray(faceX))
    faceY = normalize_array(np.asarray(faceY))
    faceConf = normalize_array(np.asarray(faceConf))

    # return np.concatenate([rHandX, rHandY, rHandConf, poseX, poseY, poseConf])
    return np.concatenate([lHandX, lHandY, rHandX, rHandY, faceX, faceY, poseX, poseY])


class App:
    show_keypoint = False
    df_sentence = None
    threshold = 0

    def __init__(self, window, window_title, video_source=0):
        self.window = window

        self.window.grid_rowconfigure(0, weight=1)
        self.window.grid_columnconfigure(0, weight=1)
        self.window.title(window_title)

        self.window.geometry(str(width + 340) + "x" + str(height + 20))
        self.video_source = video_source

        if len(sys.argv) > 1:
            self.filename = filedialog.askopenfilename(initialdir="/home/aircketi/demo_video/", title="Select file",
                                                       filetypes=(("video files", "*.MOV"), ("video files", "*.MTS"),
                                                                  ("all files", "*.*")))
            print(self.filename)
            self.video_source = self.filename

        # print(getAnnotationFromLabel(0))

        # open video source (by default this will try to open the computer webcam)
        self.vid = MyVideoCapture(self.video_source)

        # Create a canvas that can fit the above video source size
        self.canvas = tkinter.Canvas(window, width=width, height=height)
        # self.canvas.pack()
        self.canvas.place(x=10, y=10)

        # Button that lets the user take a snapshot
        self.btn_snapshot = tkinter.Button(window, text="Snapshot", command=self.snapshot)
        # self.btn_snapshot.pack(anchor=tkinter.CENTER, expand=True)
        # sign_frame = tkinter.LabelFrame(window, text="Recognized Sign", width=600)
        # sign_frame.pack(padx=10, pady=10)
        # sign_frame.place(x=10, y=500)

        keti_ci = Image.open("KETI_CI.png")
        keti_ci = keti_ci.resize((280, 70), Image.ANTIALIAS)
        photo = ImageTk.PhotoImage(keti_ci)

        self.ci_label = Label(window, image=photo)
        self.ci_label.place(x=width + 30, y=10)

        self.signs = []
        self.confs = []

        for idx in range(5):
            self.signs.append(StringVar())
            self.confs.append(DoubleVar())

        # sign_label = Label(window, textvariable=self.signs[0])
        # sign_label.config(width=30)
        # sign_label.config(font=("Courier", 15))
        # sign_label.place(x=10, y = 500)

        self.actual_fps = StringVar()
        self.frame_num = StringVar()
        self.hand_status = StringVar()

        fps_label = Label(window, textvariable=self.actual_fps)
        fps_label.place(x=width + 20, y=110)

        frame_label = Label(window, textvariable=self.frame_num)
        frame_label.place(x=width + 100, y=110)

        hand_label = Label(window, textvariable=self.hand_status)
        hand_label.place(x=width + 200, y=110)

        self.var = BooleanVar()
        cb = Checkbutton(window, text="Show Feature Points", variable=self.var, command=self.onClick)
        # cb.select()
        cb.place(x=width + 40, y=150)

        self.thres = tkinter.Scale(window, from_=0, to=100, orient=HORIZONTAL, label="Recognition Threshold: ",
                                   length=200)
        self.thres.place(x=width + 70, y=190)

        # listbox = tkinter.Listbox(window)
        # listbox.place(x=660, y=90)

        # for idx in range(5):
        #    listbox.insert(tkinter.END, self.signs[idx])

        self.tv = Treeview(window)
        self.tv['columns'] = ('conf')
        self.tv.place(x=width + 20, y=270)
        self.tv.heading('#0', text='Recognized Sign')
        self.tv.column('#0', anchor='w')
        self.tv.heading('conf', text='Confidence')
        self.tv.column('conf', anchor='center', width=100)

        # After it is called once, the update method will be automatically called every delay milliseconds
        self.delay = 1
        self.update()

        self.window.bind("<Key>", self.key)

        self.window.mainloop()

    def key(self, event):
        print("pressed", repr(event.char))
        if event.char == 'q':
            self.vid.__del__()
            self.window.destroy()

    def onClick(self):

        if self.var.get() == True:
            self.show_keypoint = True
        else:
            self.show_keypoint = False

    def snapshot(self):
        # Get a frame from the video source
        frame, sign = self.vid.get_frame(self.show_keypoint, self.threshold)

        self.filename = filedialog.askopenfilename(initialdir="/home/aircketi/demo_video/", title="Select file",
                                                   filetypes=(("video files", "*.MOV"), ("video files", "*.MTS"),
                                                              ("all files", "*.*")))

        self.video_source = self.filename
        # self.window.mainloop()

        # cv2.imwrite("frame-" + time.strftime("%d-%m-%Y-%H-%M-%S") + ".jpg", cv2.cvtColor(frame, cv2.COLOR_RGB2BGR))

    def update(self):
        # Get a frame from the video source
        # print("pre-update!!!")
        frame, sign = self.vid.get_frame(self.show_keypoint, self.threshold)

        # print(sign)
        # print(frame)

        if (sign is None):
            # self.photo = PIL.ImageTk.PhotoImage(image = PIL.Image.fromarray(frame))
            # self.canvas.create_image(0, 0, image = self.photo, anchor = tkinter.NW)
            # self.window.after(self.delay, self.update)
            return

        self.threshold = self.thres.get()

        # self.op_fps.set(round(sign[3],2))
        self.actual_fps.set("FPS: " + str(round(sign[2], 2)))
        self.frame_num.set("Frame #: " + str(sign[4]))
        self.hand_status.set("Hand Status: " + sign[5])

        if (len(sign) != 0):
            if (len(sign[0]) != 0):
                self.tv.delete(*self.tv.get_children())
                for idx in range(5):
                    # self.signs[idx].set(sign[0][idx] + " (" +str(round(sign[1][idx],4))+")" )
                    if sign[1][idx] * 100 >= self.threshold:
                        self.confs[idx].set(sign[1][idx])
                        self.tv.insert('', 'end', text=sign[0][idx], values=('{0:1.4f}'.format(sign[1][idx])))

        frame = cv2.resize(frame, (width, height))
        self.photo = PIL.ImageTk.PhotoImage(image=PIL.Image.fromarray(frame))
        self.canvas.create_image(0, 0, image=self.photo, anchor=tkinter.NW)

        self.window.after(self.delay, self.update)


class MyVideoCapture:
    download_heatmaps = False

    actual_fps = 0
    paused = False
    delay = {True: 0, False: 1}

    keyframes = []
    selectedKeyframes = []

    handStatusBuffer = []
    previousHandStatus = False  # Starts with 'hand down pose'
    handStatus = False  # Starts with 'hand down pose'
    handStatusStr = "DOWN"

    frame_num = -1

    text = ''
    text_size = [0, 0]

    recognized_time = 0

    df_sentence = None

    stub = None
    responses = None

    frame_list = deque([])

    frame = None

    start_time = 0

    def generate_frames(self):

        frame_num = 0

        while True:
            ret, rec_frame = self.vid.read()
            if ret:
                self.frame = rec_frame
                frame_to_send = cv2.resize(rec_frame, (656, 368))
                # frame = cv2.bilateralFilter(frame, 9, 75, 75)

                frame_num = frame_num + 1

                frame_as_bytes = pickle.dumps(frame_to_send)

                message = self.make_frame(frame_as_bytes, frame_num, frame_num)
                print("Sending %s at %s" % (message.num_frame, message.num_frame))

                # cv2.imshow('Video', frame)

                # time.sleep(0.0002)
                # if cv2.waitKey(1) & 0xFF == ord('q'):
                #    break

                yield message
            else:
                break

        self.vid.release()

    def make_frame(self, message, num_frame, time):
        return openpose_pb2.Image(image=message, num_frame=num_frame, time=time)

    def __init__(self, video_source=0):
        # Open the video source
        self.vid = cv2.VideoCapture(video_source)

        self.start_time = time.time()

        if not self.vid.isOpened():
            raise ValueError("Unable to open video source", video_source)

        self.loadSentenceDB()

        # Get video source width and height
        self.width = self.vid.get(cv2.CAP_PROP_FRAME_WIDTH)
        self.height = self.vid.get(cv2.CAP_PROP_FRAME_HEIGHT)

        self.channel = grpc.insecure_channel('10.0.0.20:50051')
        # with grpc.insecure_channel('10.0.0.20:50051') as channel:
        self.stub = openpose_pb2_grpc.OpenPoseRPCStub(self.channel)

        self.responses = self.stub.VideoStream(self.generate_frames())

    def loadSentenceDB(self):
        print("Read Annotations...")

        df = pd.read_csv('KETI-2017-SL-Annotation-v2-0117.csv', encoding='euc-kr')

        df.drop(columns=['Unnamed: 7', 'Unnamed: 8'], inplace=True)

        self.df_sentence = df.loc[df['타입(단어/문장)'] == '문장']

        sentence_category = self.df_sentence['한국어'].astype('category')
        sentence_category.cat.codes
        self.df_sentence['한국어 ID'] = sentence_category.cat.codes

    def getSentenceFileName(self, label, person, direction):
        fileId = self.df_sentence.ix[
            (self.df_sentence['언어 제공자 ID'] == person) & (self.df_sentence['한국어 ID'] == label) & (
                        self.df_sentence['방향'] == direction)]['번호'].values[0]
        fileName = glob.glob('/home/aircketi/KETI-SL-2017/' + 'KETI_SL_' + str(fileId).zfill(10) + '.*')
        return fileName

    def getAnnotationFromLabel(self, label):
        return self.df_sentence.loc[self.df_sentence['한국어 ID'] == label]['한국어'].iloc[0]

    def get_frame(self, show_keypoint, threshold):

        t = time.time()
        response = next(self.responses)
        op_fps = 1.0 / (time.time() - t)

        print("Received frame %s at %s" % (response.num_frame, response.time))

        frame_info = pickle.loads(response.image)
        self.frame_num = self.frame_num + 1
        # local_frame = cv2.resize(frame_info[0], (width,height))
        local_frame = cv2.resize(self.frame, (width, height))

        # local_frame = np.copy(self.frame)

        # frame = rgb

        # t = time.time()

        # t = time.time() - t
        # op_fps = 1.0 / t

        # local_frame = self.frame

        persons = frame_info[1]
        lHands = frame_info[2][0]
        rHands = frame_info[2][1]
        faces = frame_info[3]
        # print(persons.shape)
        # print(persons)

        predicted_signs = []
        predicted_conf = []

        personIdx, conf = choosePeopleToShow(faces)

        if conf != 0:
            # print(persons[personIdx])
            # print(lHands[personIdx])
            # print(rHands[personIdx])

            pose = persons[personIdx]
            lHand = lHands[personIdx]
            rHand = rHands[personIdx]
            face = faces[personIdx]

            if show_keypoint:
                local_frame = draw_keypoints(local_frame, pose, face, rHand, lHand)

            # if handUp(persons[personIdx]):
            #    isHandUp = "DOWN"
            # else:
            #    isHandUp = "UP"

            # cv2.putText(res, 'HAND STATUS = %s' % isHandUp, (20, 40), 0, 0.5, (0, 0, 255))

            frameFeature = normalizeFrameInfo(pose, face, lHand, rHand)
            # print(frameFeature.shape)

            self.previousHandStatus = self.handStatus

            if handUp(pose, rHand, lHand):
                self.handStatus = True
            else:
                self.handStatus = False

            # print(handStatus)

            self.handStatusBuffer.append(self.handStatus)

            if len(self.handStatusBuffer) > 5:
                self.handStatusBuffer.pop(0)

            count = 0
            for idx in range(len(self.handStatusBuffer)):
                if self.handStatusBuffer[idx] == True:
                    count = count + 1

            if count > 2:
                self.handStatusStr = "UP"
                self.handStatus = True
            else:
                self.handStatusStr = "DOWN"
                self.handStatus = False

            if self.handStatus == True:
                self.keyframes.append([frameFeature, self.frame_num])

            # print(previousHandStatus, handStatus)

            # cv2.putText(res, 'HAND STATUS = %s' % handStatusStr, (20, 40), 0, 0.5, (255, 255, 255))

            # predicted_signs = []
            # predicted_conf = []

            numKeyframes = len(self.keyframes)

            if self.previousHandStatus == True and self.handStatus == False:

                if numKeyframes < 20 and numKeyframes > 13:
                    print("Pad frames...")
                    for idx in range(20 - numKeyframes):
                        self.keyframes.append(self.keyframes[numKeyframes - 1])
                elif numKeyframes <= 13:
                    self.keyframes = []
                    local_frame = draw_sign_on_frame(local_frame, self.text, font_size)
                    return (cv2.cvtColor(local_frame, cv2.COLOR_BGR2RGB),
                            [predicted_signs, predicted_conf, self.actual_fps, op_fps, self.frame_num,
                             self.handStatusStr])

                predicted_signs = []
                predicted_conf = []

                gap = np.floor((numKeyframes - 1) / (20 - 1))

                for idx in range(20):
                    self.selectedKeyframes.append(self.keyframes[idx * int(gap)][0])
                    # print("Selected frame number: ", keyframes[idx*int(gap)][1])
                    # print("Gap: ", gap, "numKeyframes: ", numKeyframes)

                prediction_result = loaded_model.predict(np.expand_dims(np.asarray(self.selectedKeyframes), axis=0))

                top_5_indices = prediction_result[0].argsort()[-5:][::-1]

                # print("Prediction result is ", prediction_result, "Label is", top_5_indices[0])
                self.recognized_time = time.time()
                # print(self.recognized_time)

                predicted_signs = [self.getAnnotationFromLabel(top_5_indices[idx]) for idx in range(5)]
                predicted_conf = [prediction_result[0][top_5_indices[idx]] for idx in range(5)]

                # print(predicted_sign)

                # for idx in range(5):
                # print("Sign: ",getAnnotationFromLabel(top_5_indices[idx]), "Conf: ", prediction_result[0][top_5_indices[idx]])
                # print(predicted_signs[idx])

                self.selectedKeyframes = []
                self.keyframes = []

        # unicode_font = ImageFont.truetype("NanumGothic-Bold.ttf", font_size)
        # pil_res = Image.fromarray(frame)
        # draw = ImageDraw.Draw(pil_res)

        # small_font = ImageFont.truetype("NanumGothic.ttf", 15)

        # draw.text((10,10), 'UI FPS = %f, OP FPS = %f, FRAME NUM = %d' % (self.actual_fps, op_fps, self.frame_num), font=small_font, fill=font_color)
        # draw.text((10,40), 'HAND STATUS = %s' % self.handStatusStr, font=small_font, fill=font_color)

        # unicode_font = ImageFont.truetype("NanumGothic-Bold.ttf", font_size)

        if len(predicted_signs) != 0 and predicted_conf[0] * 100 >= threshold:
            self.text = predicted_signs[0] + " (" + str(round(predicted_conf[0] * 100, 2)) + "%)"

            # self.text_size = draw.textsize(self.text, font=unicode_font)

            # for idx in range(4):
            # print(predicted_signs[idx+1], predicted_conf[idx+1])

        if (time.time() - self.recognized_time > 2.5):
            self.text = ''

        # draw.text((width/2 - (self.text_size[0]/2),height-100), self.text, font=unicode_font, fill=font_color)

        result_frame = draw_sign_on_frame(local_frame, self.text, font_size)

        self.actual_fps = self.frame_num / (time.time() - self.start_time)

        # Return a boolean success flag and the current frame converted to BGR
        return (cv2.cvtColor(result_frame, cv2.COLOR_BGR2RGB),
                [predicted_signs, predicted_conf, self.actual_fps, op_fps, self.frame_num, self.handStatusStr])

        # Release the video source when the object is destroyed

    def __del__(self):
        if self.vid.isOpened():
            self.vid.release()


def draw_keypoints(frame, pose, face, rHand, lHand):
    for idx in range(pose.shape[0]):
        cv2.circle(frame, (int(pose[idx][0] / 656 * width), int(pose[idx][1] / 368 * height)), 5, (0, 0, 255), -1)

    for idx in range(face.shape[0]):
        cv2.circle(frame, (int(face[idx][0] / 656 * width), int(face[idx][1] / 368 * height)), 3, (255, 0, 0), -1)

    for idx in range(rHand.shape[0]):
        cv2.circle(frame, (int(rHand[idx][0] / 656 * width), int(rHand[idx][1] / 368 * height)), 3, (0, 255, 0), -1)

    for idx in range(lHand.shape[0]):
        cv2.circle(frame, (int(lHand[idx][0] / 656 * width), int(lHand[idx][1] / 368 * height)), 3, (0, 255, 0), -1)

    return frame


def draw_sign_on_frame(frame, text, fontsize):
    unicode_font = ImageFont.truetype("NanumGothic-Bold.ttf", fontsize)
    pil_res = Image.fromarray(frame)
    draw = ImageDraw.Draw(pil_res)
    text_size = draw.textsize(text, font=unicode_font)

    while (text_size[0] >= 620):
        fontsize = fontsize - 2
        unicode_font = ImageFont.truetype("NanumGothic-Bold.ttf", fontsize)
        text_size = draw.textsize(text, font=unicode_font)

    draw.text((width / 2 - (text_size[0] / 2), height - 100), text, font=unicode_font, fill=font_color)

    return np.array(pil_res)


if __name__ == '__main__':
    App(tkinter.Tk(), "KETI Sign Language Recognizer v2.0")

    # del op
    # del loaded_model
    # del loaded_model_json

