# 🎁 Sonmal (손말: 수어 통역 서비스)


![img_sonmal_logo_512px](https://user-images.githubusercontent.com/49026286/192984903-ed46455c-5a70-47ef-b97b-9f7f95829dc4.png)

---


간단한 의사소통은 통역사 없이 **손말**로 할 수 있습니다.

수어 통역권 확장을 목표로 프로젝트를 진행하였습니다.


(Simple communication can be done with hands without an interpreter.
The project was carried out with the aim of expanding the right to interpret sign language.)



<br/>

---

# 🧷 링크

**앱스토어**

https://play.google.com/store/apps/details?id=com.d202.sonmal



**UCC 영상**

[UCC 유튜브 링크]



---



## 1️⃣ 기획 배경

**Q1. 한국수어와 한국어 문법이 같다?**


아뇨, 같지 않습니다! 한국 수어와 한국어는 단어의 단위는 물론 문장 순서까지 다릅니다. 한국 수어가 제1언어인 사람들은 한국어가 제2외국어인 셈이죠


**Q2. 통역 센터 지원은 농인 50명 중 1명 꼴로 제공된다?**


농인 200명 중 통역사 1명 꼴로 제공되는 수치입니다. 그래서 충분히 서비스가 제공된다고 보기는 어려운 수치입니다.


이렇듯 많은 분들이 수어에 대한 이해가 부족합니다.


나라에서도 급하게 이들을 위한 법 제정을 했지만
아직까지도 그들의 장벽을 허물 수 있는 제도와 환경은 부족합니다.


이러한 내용을 바탕으로 농인들을 위한 수어 번역 기능의 필요성을 절실히 깨닫게 되었고, 저희는 수어 번역 기능을 제공하는 모바일 어플리케이션 `손말`을 기획하게 되었습니다.

**타겟 설정**
1. 수어를 제1국어로 사용하며 한국어에 서툰 사람
2. 수어를 제1국어로 사용하지만 한국어를 어느 정도 아는 사람
3. 수어를 모르고 한국어 텍스트의 음성 번역을 필요로 하는 사람

<br/>

## 2️⃣ 프로젝트 소개

>  실시간 수어 통역과 매크로를 이용한 수어통역권 확장


### 👍 실시간 통역


![image](https://user-images.githubusercontent.com/49026286/195801301-88057f53-8387-48a6-a52d-a590bf53e38a.png)
![image](https://user-images.githubusercontent.com/49026286/195801423-36f7f1e5-397a-488c-8c71-ae6300274af5.png)




🔈  실시간으로 텍스트 혹은 지수어를 입력하면 번역하여 음성으로 출력해줍니다.





### 👍 매크로


![image](https://user-images.githubusercontent.com/49026286/194220677-9f1ef74c-fd9f-4e69-ba30-ca3a7449b3bc.png)


🔈 자주 사용하는 문장을 수어 영상&텍스트 혹은 텍스트로 저장해놓고, 필요할 때 누르면 음성으로 출력해줘요.






### 👍 통화


![image](https://user-images.githubusercontent.com/49026286/195800558-f05ff4f0-4234-4a00-8e56-066fc0040193.png)


🔈 통화를 할 때, 텍스트를 입력하면 음성으로 전달해줘요. 영상통화를 하면, 지수어도 번역해서 전달할 수 있어요.


<br/>

## :three: 개발 환경

### 시스템 환경

- Server : AWS EC2
- OS : Ubuntu 20.04 LTS 

### 시스템 구성

**Frontend(Android)**
- Kotlin 1.7.10
- JDK 11.0.11
- Gradle 7.3.3
- SDK - MIN 21/Target 32/Compile 32
- WebRTC
- TensorflowLite

**Backend**
- Docker
- Jenkins
- MariaDB  8.0.28
- SpringBoot 2.7.3
- Java 11
- WebRTC : openvide
- RTSP

**GPU Server**
- Jupyterhub

**프로젝트 관리**
- 형상 관리 : Gitlab
- 이슈 관리 : Jira
- 프로젝트 관리 : Notion
- 커뮤니케이션 : Mattermost
- 디자인 : Figma

### Server Port

| 이름                | 포트 번호 |
| ------------------- | --------- |
| web server(nginx)   | 80        |
| springboot (tomcat) | 8090      |
| openvidu(http)      | 4442      |
| openvidu(https)     | 4443      |
| https               | 443       |
| mariadb             | 3306      |


<br/>

## :four: 시스템 구조도


![시스템 구조도](https://user-images.githubusercontent.com/49026286/199017240-4da672b2-7141-4ccf-bb4c-e80bad827435.png)

<br/>

## :five: ERD

![image](https://user-images.githubusercontent.com/49026286/194246933-db499a84-e2be-406b-8ff1-a77c23cb6cf0.png)


<br/>

## :six: 와이어프레임

[와이어프레임 이미지와 설명]

<br/>

---


🌐 Git Flow
```
master : 제품으로 출시될 수 있는 브랜치
release : 이번 출시 버전을 준비하는 브랜치
develop : 다음 출시 버전을 개발하는 브랜치
feature : 기능을 개발하는 브랜치
hotfix : 출시 버전에서 발생한 버그를 수정 하는 브랜치
```


🌐 Git branch 생성 규칙

master ← develop ←  (release) ← be/fe ← feat  **순서대로 머지** 한다.


개발 시 **feat-기능 이름** 으로 브랜치를 만들어 상위에 머지한다.

- BE/**feat/naver-login**
- FE/**feat/naver-login**

UI만 작업을 할때는 가장 뒤에 UI를 작성한다.

- FE/**feat/naver-login-UI**


🌐 Git 커밋 컨벤션 생성 규칙
```
feat : 기능 추가
fix : 버그 수정
docs : 문서 수정
style : 단순 수정 (세미콜론, 코드 이동, 띄어 쓰기, 이름 변경)
rename: 추가된 기능, 별 내용 없이 폴더 및 파일만 추가, 폴더 및 파일 이름 수정, 옮기기
refactor : 코드 리팩토링
test : 테스트 추가
study : 학습 내용
```
**ver01(간략) : commit type: Epic/대분류 | 작업 단위 [Jira 이슈 번호]**


feat: 회원관리 | 네이버 로그인 기능 추가  [Jira 이슈번호]

**ver02(설명) : ver01양식(Jira 번호 빼고) - 본문 - [Jira 이슈 번호]**

feat: 회원관리 | 네이버 로그인 기능 추가

본문은 위, 아래 한 줄 띄우고 원하는 대로 작성한다.

 => 이런 식으로 특수기호를 쓰거나 이모티콘을 쓰는 것도 가능하다. 😄

단, 한 줄에 너무 길지 않도록 작성하자.

[Jira 이슈 번호]

<br/>

---


## :angel: 팀원 소개

<table>
    <tr>
        <td height="140px" align="center"> <a href="https://github.com/nhee-dev">
            <img src="https://avatars.githubusercontent.com/u/49919262?v=4" width="140px" /> <br><br> 김남희 <br></a> <br></td>
        <td height="140px" align="center"> <a href="https://github.com/Sicoree">
            <img src="https://avatars.githubusercontent.com/u/97591071?v=4" width="140px" /> <br><br> 배시현 <br></a> <br></td>
        <td height="140px" align="center"> <a href="https://github.com/toy9910">
            <img src="https://avatars.githubusercontent.com/u/50603217?v=4" width="140px" /> <br><br> 배한용 <br></a> <br></td>
        <td height="140px" align="center"> <a href="https://github.com/forlivd">
            <img src="https://avatars.githubusercontent.com/u/84622281?v=4" width="140px" /> <br><br> 서재형 <br></a> <br></td>
        <td height="140px" align="center"> <a href="https://github.com/JeongBJ">
            <img src="https://avatars.githubusercontent.com/u/85900947?v=4" width="140px" /> <br><br> 정봉진 <br></a> <br></td>
            <td height="140px" align="center"> <a href="https://github.com/pmi4202">
            <img src="https://avatars.githubusercontent.com/u/49026286?v=4" width="140px" /> <br><br> 편예린 <br></a> <br></td>
    </tr>
    <tr>
        <td align="center"> 팀장 <br> Android <br> UI/UX <br> 프로젝트 관리
        <td align="center"> Backend <br> DB, API 설계 <br> 서버 배포 <br> 매크로 API<br> OpenVidu
        <td align="center"> Android <br> 자료 수집 <br> point 처리 <br> TensorflowLite 변환 <br> 실시간 통역
        <td align="center"> Android <br> 회원 관리 <br> 매크로 기능
        <td align="center"> Android <br> 자료 수집 <br> 모델 분석 <br> TensorflowLite 변환 <br> 통화 기능
        <td align="center"> Backend <br> DB, API 설계 <br> 서버 배포 <br> 회원관리 API <br> JWT
    </tr>
</table>

