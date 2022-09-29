# 🤙 Sonmal (손말: 수어 통역 서비스)

[로고이미지]

---


간단한 의사소통은 통역사 없이 **손말**로 할 수 있습니다.

수어 통역권 확장을 목표로 프로젝트를 진행하였습니다.


(Simple communication can be done with hands without an interpreter.
The project was carried out with the aim of expanding the right to interpret sign language.)



<br/>

---

# 🧷 링크

**앱스토어**

[링크]



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


### :star2: 실시간 통역



[완성이미지]



🔈  실시간으로 텍스트 혹은 지수어를 입력하면 번역하여 음성으로 출력해줍니다.





### :star2: 매크로


[완성이미지]


🔈 자주 사용하는 문장을 수어 영상&텍스트 혹은 텍스트로 저장해놓고, 필요할 때 누르면 음성으로 출력해줘요.





### :star2: 통화


[이미지]


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

## :four: 설계

### :star: 아키텍처

[시스템 아키텍처]

<br/>

## :five: ERD

[ERD 이미지와 설명]


<br/>

## :six: 와이어프레임

[와이어프레임 이미지와 설명]

<br/>

---


✅ Git Flow
```
master : 제품으로 출시될 수 있는 브랜치
release : 이번 출시 버전을 준비하는 브랜치
develop : 다음 출시 버전을 개발하는 브랜치
feature : 기능을 개발하는 브랜치
hotfix : 출시 버전에서 발생한 버그를 수정 하는 브랜치
```


✅ Git branch 생성 규칙

master ← develop ←  (release) ← be/fe ← feat  **순서대로 머지** 한다.


개발 시 **feat-기능 이름** 으로 브랜치를 만들어 상위에 머지한다.

- BE/**feat/naver-login**
- FE/**feat/naver-login**

UI만 작업을 할때는 가장 뒤에 UI를 작성한다.

- FE/**feat/naver-login-UI**


✅ Git 커밋 컨벤션 생성 규칙
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

> | 소개  | 이름   | 역할                            |
> | ----- | ------ | ------------------------------- |
> | 🐻팀장 | 김남희 | UI/UX, Android                 |
> | 🐨팀원 | 배시현 | DB, API 설계 및 서버 배포, 매크로 API, OpenVidu   |
> | 🐯팀원 | 배한용 | 자료 수집 및 point처리, TensorflowLite변환, 실시간 통역 |
> | 🐱팀원 | 서재형 | Android 회원 관리 및 매크로 기능 |
> | 🐼팀원 | 정봉진 | 자료 수집 및 모델 분석, TensorflowLite변환, 통화   |
> | 🐰팀원 | 편예린 | DB, API 설계 및 서버 배포, 회원관리 API, JWT   |

