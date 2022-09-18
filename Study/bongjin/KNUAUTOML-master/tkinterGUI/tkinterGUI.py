import cv2
import tkinter
from PIL import Image
from PIL import ImageTk

automllabel = "hello" #automl에서 뽑아낸 레이블
def convert_to_tkimage():
    global src
    nimg = cv2.cvtColor(src, cv2.COLOR_BGR2RGB)
    font = cv2.FONT_HERSHEY_DUPLEX  # 텍스트의 폰트를 지정.
    cv2.putText(nimg, automllabel, (240, 80), font, 2, (0, 255, 0), 2,cv2.LINE_AA) #파라미터 : img,레이블 입력, 레이블시작좌표, 폰트, 크기확대비율, 색깔 ,굵기,선유형
    img = Image.fromarray(nimg)
    imgtk = ImageTk.PhotoImage(image=img)
    label.config(image=imgtk)
    label.image = imgtk
window=tkinter.Tk()
window.title("Sign Translate") #Sign Translate(수화)
window.geometry("640x480+100+100")#너비x높이+x좌표+y좌표

src = cv2.imread("hellojaewan.png") # 레이블인식할 사진
src = cv2.resize(src, (640, 400))

img = cv2.cvtColor(src, cv2.COLOR_BGR2RGB)

img = Image.fromarray(img)
imgtk = ImageTk.PhotoImage(image=img)

label = tkinter.Label(window, image=imgtk)
label.pack(side="top")

button = tkinter.Button(window, text="Translate", command=convert_to_tkimage)
button.pack(side="bottom", expand=True, fill='both')

window.mainloop()