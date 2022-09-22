# 🤝 Sonmal (손말: 수어 통역 서비스)

[로고이미지]

---

베프는 당신의 친구가 되어주고, 당신의 장애물을 허물겠습니다.

**I'm Your Best Friend, I'm Your Barrier Free**

(위 로고는 '친구'를 의미하는 수어입니다.)

---



:black_large_square: 사이트 주소

https://iamyourbf.site/



:black_large_square: UCC 영상

[![](BF_Barrier_Free_Project.assets/unknown-16451225244061.png)](https://www.youtube.com/watch?v=foHTciLIa9U)



---



## 1️⃣ 문제 제기 및 프로젝트 개발 필요성



최근 몇 년 간 코로나 19 유행으로 인해 비대면 화상 수업 플랫폼을 활용하는 빈도가 늘어났습니다.

시간과 공간에 구애받지 않고 수업을 듣는다는 점이 원격 수업의 주요 장점으로 언급됩니다.

그러나 여전히 다른 학생들과 동등한 수업권을 받지 못하는 학생들이 많습니다.



> 2017년 국립 국어원에서 발표한 한국수어 사용 실태조사에 따르면 농인이 주로 사용하는 의사소통 방법은 '수어'이다. **일상적인 의사소통에서 가장 많이 사용하는 언어가 '수어'라고 응답한 농인이 69.3%로 조사되었다.** 이는 농인의 제1언어가 '수어'임을 말해주는 결과이다. 하지만 가족과의 의사소통에서는 수어 사용 비율이 42.7%로 다소 낮게 나타났는데, 이는 가족 구성원 모두가 수어에 능숙한 것은 아니기 때문인 것으로 보인다. 더욱이 코로나19 비대면 원격수업 속 청각장애인들은 온라인 수업 수강 시 자막서비스가 제공되지 않아 어려움을 겪고 있다는 지적이 제기됐다.
​																																			이대섭, '2017 한국 수어 실태 조사 연구', 국립 국어원



`베프`는 그 중에서 청각 장애가 있는 학생들이 비대면 화상 수업 시스템에서 소외받고 있으며, 자막 기능을 제공하는 플랫폼이라도 수어를 주로 쓰는 학생들에게는 큰 도움이 되지 않는다는 사실에 주목하였습니다.



이러한 배경에서 `베프`는 **학생들의 진정한 온라인 수업권을 보장하는 온라인 화상 수업 플랫폼**을 기획하였습니다.







## 2️⃣ 프로젝트 소개

>  비대면 온라인 교육 수요가 증가함에 따라 **학생들의 온라인 수업권을 보장하기 위한** 온라인 화상 교육 플랫폼을 개발한다. 본 플랫폼의 주요 기능은 크게 4가지로 나뉜다.


### :star2: Web RTC 기능(화상 수업)



![webrtc](BF_Barrier_Free_Project.assets/webrtc-16451129097734.gif)



👩  WebRTC 기능을 통한 원격 화상 수업 서비스를 제공합니다.

메인 페이지에서 **`수업 참여`** 버튼을 통해 강의실 리스트로 들어갈 수 있습니다.

강의실 리스트에서 원하는 수업을 선택하여 입장하면 곧바로 비대면 화상 수업이 가능합니다.

강의실 생성, 강의실 나가기, 채팅, 마이크 on/off, 음소거 on/off 등 화상 수업에 필요한 기본적인 기능 역시 제공됩니다.





### :star2: 자동 자막 생성

![자막](BF_Barrier_Free_Project.assets/자막-16451136166946.gif)



👩 Web Speech API를 통한 실시간으로 자막을 생성 기능을 제공합니다.

수업 참여자는 상단에 있는 **`CC`** 버튼을 클릭하여 자막 생성을 on/off 할 수 있습니다.





### :star2: 수어 자동 번역



![수어](BF_Barrier_Free_Project.assets/수어-16451136108145.gif)



:woman: 호스트의 말을 수어로 실시간 번역하는 기능을 제공합니다.

수업 참여자는 자막 기능과 마찬가지로 상단에 있는 **`수어를 의미하는 손 모양`** 버튼을 클릭하여 수어 번역을 on/off 할 수 있습니다. 

현재는 한정된 개발 시간에 의해 수어 모델링 데이터가 부족하여 아래 예시로만 이 기능이 지원됩니다. 데이터는 추후 계속해서 추가될 예정입니다. 



**예시)**

안녕하세요.

우리는 당신의 친구 베프입니다.

 저희 시스템은 수어를 통역합니다.

또 자막을 실시간 처리합니다.

당신의 권리를 지키겠습니다.





### :star2: 자동 수업 기록



![수업기록](BF_Barrier_Free_Project.assets/수업기록-16451139269627.gif)



:woman: 수업 기록을 자동으로 기록하고, 이메일로 보내주는 서비스를 제공합니다.

사용자가 자리를 비우거나 제대로 못들은 수업내용을 자동으로 기록합니다.
사용자가 수업실을 나갔을 때, 사용자의 이메일로 수업 기록이 전달됩니다.



## :three: Install and Usage

### 시스템 환경

I'm Your BF는 아래와 같은 환경에서 실행 중입니다.

- Cloud : AWS EC2
- OS : Ubuntu 20.04.3 LTS
- CPU 모델 : Intel(R) Xeon(R) CPU E5-2686 v4 @ 2.30GHz
- Total Memory : 16396056 kB
- 물리 cpu 개수 : 1 | cpu당 물리 코어 : 4 

### 시스템 구성

- docker : 20.10.12
- docker-compose : 1.29.2
- DB : mysql  8.0.28
- WebRTC : openvidu 2.20.0
- FrontEnd : Vue build 파일 
- BackEnd : Springboot 
- SSLIS : uwsgi  2.0.20 | django
- Modeling : blender  3.0

### Server Port

| 이름                | 포트 번호 |
| ------------------- | --------- |
| web server(nginx)   | 80        |
| springboot (tomcat) | 8080      |
| django(uwsgi)       | 8000      |
| openvidu(http)      | 4442      |
| openvidu(https)     | 4443      |
| https               | 443       |
| mysql               | 3306      |


#### Ubuntu 버전 업 및 기본 설치

```
sudo apt-get update
sudo apt-get install nodejs
sudo apt-get install npm
```

#### frontend 빌드 및 backend 무중단 배포

```
#frontend
npm run build
#backend
nohup java -jar bf.jar & 
```

#### HTTPS 키 발급

```
sudo apt-get install letsencrypt
# 만약 nginx를 사용중이면 중지
sudo systemctl stop nginx
# 인증서 발급
sudo letsencrypt certonly --standalone -d www제외한 도메인 이름
# 아래 키가 발급되는 경로를 /etc/nginx/sites-availabe/default 설정파일에 넣어줘야 함.
 ssl_certificate /etc/letsencrypt/live/도메인이름/fullchain.pem; 
 ssl_certificate_key /etc/letsencrypt/live/도메인이름/privkey.pem;
```



## :four: 개발 플로우

### :star: 아키텍처

![img](BF_Barrier_Free_Project.assets/unknown-16451189575012.png)



### :star: WebRTC

#### :scroll: OpenVidu

> WebRTC 기반의 화상 서비스 커스텀 플랫폼
>
> Kurento Media Sever 와 OpenVidu Server 간 Web Socket 통신
>
> 많은 프레임워크와 높은 호환성을 자랑
>
> https://docs.openvidu.io/en/stable/


![image-20220218102231419](BF_Barrier_Free_Project.assets/image-20220218102231419.png)



Web RTC 기술 기반으로 쉽고 간편하게 화상 회의 서비스를 커스텀할 수 있는 OpenVidu 플랫폼을 활용하여 서비스를 개발하였습니다.

- Session

- Publisher
- Subscriber



- **주의점**
  - HTTPS SSL 필수
  - Backend에서 얻어온 토큰 값을 frontend에서 사용하여 세션 연결(공식문서에서 권장하고 있음)







### :star: Speech to Sign Language Interpret System(SSLIS)



![image-20220218033225555](BF_Barrier_Free_Project.assets/image-20220218033225555-16451227495363.png)

- Web Speech API (STT)

  - 음성 데이터를 텍스트 변환해주는 OPEN API입니다.
    - 사용 목적 : 발언자의 음성을 텍스트로 변환한 후 자막 영상을 제공하기 위해 사용하였습니다.

- Pykomoran

  - 형태소 분석 라이브러리

    - 사용 목적 : 문장을 수어 문법에 맞춰 나누기 위해 사용하였습니다.

  - 예시

    ```python
    komoran = Komoran(DEFAULT_MODEL['FULL'])
    print(komoran.get_list('당신의 권리를 지키겠습니다'))
    -----------------------------------------
    # result : (['당신', '권리', '지키다'], ['명사', '명사', '용언'])
    ```

- AI API · DATA

  - 단어간 유사도 분석 API
    - 사용 목적 : 이름은 같지만 뜻이 다른 단어를 구분하기 위해 사용하였습니다. 
  - 예시

  ![image-20220218033300143](BF_Barrier_Free_Project.assets/image-20220218033300143-16451227818144.png)

![image-20220218033312752](BF_Barrier_Free_Project.assets/image-20220218033312752.png)

- beautifulsoup4
  - 웹 크롤링 라이브러리
    - 사용 목적 : 국립수어원에서 영상 크롤링을 목적으로 사용하였습니다.
- opencv-python
  -  수백 개의 컴퓨터 비전 알고리즘을 포함하는 오픈 소스 라이브러리
     - 사용 목적 : 영상 재생 및 자르기







### :star: Blender

![블렌더 모델링 작업 이미지](https://cdn.discordapp.com/attachments/927839574657482769/943908037364678696/blender.PNG)



- **blender**는 제품 디자인, 게임모델링, 애니메이션, 피규어 아트, 건축 등 다양한 분야에서 사용 가능한 3D컴퓨터 그래픽 제작 소프트웨어입니다.  
- **사용이유**
  -  수어 영상 데이터를 크롤링해서 가져와보니 영상마다 구도나, 사람의 위치, 자막의 유무 등이 너무 달라서, 가져온 영상데이터로 번역을 진행하게 되면 수어의 이해도가 떨어질 것 같아 시인성을 높히기 위해 도입했습니다.
- **Ready Player me**에서 **아바타를 생성**하고 blender에 적용하여 animation을 만든 뒤, 영상으로 저장하여 이를 수어 영상으로 활용하였습니다.



## :five: 추진 계획

> 빠르게 웹사이트 기본 기능을 구현하고, 수어 번역 및 화상 수업 메인 기능에 집중하는 전략


### 1주차(1월 5일 ~ 1월 14일)

- 팀 빌딩

- 개발환경 설정
- Spring boot, Vue3 학습

- 아이디어 기획

- 그라운드 롤 회의
  - 팀 규칙, 팀원 목표 공유
  - Git commit 규칙
  - Jira 규칙
  - 코딩 컨벤션
  - Notion 페이지 개설
- Git 스터디



### 2주차(1월 17일 ~ 1월 21일)

- 주제 선정

  수어 번역 플랫폼 

  이름 : 베프

  - Best Friend

  - Barrier Free

    :arrow_right: **BF**

- 팀원 역할 분배

- 협업 채널 논의

  - Jira
  - MM
  - Discord
  - KakaoTalk

- 개발 기획

  - 기능 명세서

  - 상세 기능 일정 논의 

  - 웹사이트 디자인 컨셉
    - 로고 초안 제작
    
      ![image-20220218175150450](README.assets/image-20220218175150450.png)
    
  - User Flow 

  - ERD 

  - 와이어 프레임

- 프로젝트 활용 기술 학습 공유
  - Web RTC
  - 자연어 처리
  - 음성 인식 STT API
  - 3D 아바타 모델링
  - REST API

- 서비스 배포 환경 구축

- Openvidu tutorial 진행



### 3주차(1월 24일 ~ 1월 28일)

- 웹사이트 기본 기능 개발 시작
  - 메인 페이지

  - 회원관리
    - 로그인
    - 회원가입
    - 아이디 찾기
    - 비밀번호 찾기
    - 프로필 조회
      - 이름 수정
      - 이메일 수정
      - 비밀번호 변경

  - 강의실 관리
    - 강의실 리스트 조회
    - 강의실 생성
    - 강의실 상세 조회
    - 강의실 삭제

- django 프로젝트 생성
- 수어 번역 관련 기술 학습 공유
- 기본 프로젝트 서버 배포 및 빌드 테스트



### **4주차(1월 31일 ~ 2월 4일)**

- 설날 명절

- Openvidu 세션 생성

- 웹사이트 기본 기능 마저 구현

- 수어 데이터 크롤링

  

### 5주차(2월 7일 ~ 2월 11일)

- STT 기능 구현(음성 - 텍스트)
- 문장 형태소 분석, 품사 분류, 형태소 재배치
- 메인 페이지 디자인 개선
- 웹사이트 전체 디자인 개선

- 강의실 기본 기능 구현

  - 채팅
  - 마이크,  음소거, 자막 on/off
  - 강의실 생성, 강의실 목록 필터링
  - 강의실 목록, 강의실 CSS 디자인

- 강의실 메인 기능 구현

  - 자막 자동 생성
  - 수어 비디오 영상 제공

- 강의실 추가 기능 구현

  - 화면 공유
  - 수업 기록 자동 생성 후 이메일 발송
  - 아바타 모델링(블렌더)

- Django media url 설정

- OpenVidu 배포

- Vue - Java client - OpenVidu 연동

- Nginx - springboot proxy 

- Springboot 

  

### 6주차(2월 14일 ~ 2월 18일)

- 테스트 케이스 작성
- 기능 테스트 및 디버깅
- 서버 최종 배포
- 산출물 정리
- UCC 촬영 및 편집
- 최종 발표





## :six: 기능 명세 요구 사항 정의서

https://ibb.co/C04Jwqd

(이미지 크기가 커서 링크로 대체합니다.)



## :seven: REST API



![img](BF_Barrier_Free_Project.assets/unknown-16451149236688.png)





## :eight: ERD

![img](BF_Barrier_Free_Project.assets/Im_Your_BF_DB_ERD.png)





## :nine: 와이어 프레임



![img](BF_Barrier_Free_Project.assets/unknown.png)







## :keycap_ten:  Coding Convention



### :triangular_ruler: Language Convention

- JAVA : 네이버 - [JAVA 코딩 컨벤션](https://naver.github.io/hackday-conventions-java/)
- JAVASCRIPT : AIRBNB - [JAVASCRIPT 컨벤션](https://github.com/airbnb/javascript)
- 주석 : /**

### :triangular_ruler: Commit Message Convention



```
feat : 새로운 기능에 대한 커밋
fix : 버그 수정에 대한 커밋
build : 빌드 관련 파일 수정에 대한 커밋
chore : 그 외 자잘한 수정에 대한 커밋
ci : CI관련 설정 수정에 대한 커밋
docs : 문서 수정에 대한 커밋
style : 코드 스타일 혹은 포맷 등에 관한 커밋
refactor :  코드 리팩토링에 대한 커밋
test : 테스트 코드 수정에 대한 커밋
```





### :triangular_ruler: 우선순위 규칙

| Highest(1) | 이걸 끝내지 않고서는 다음 단계로 갈 수 없음                  |
| ---------- | ------------------------------------------------------------ |
| High(2)    | Highest를 끝내고 해야하는 것                                 |
| Mid(3)     | 항상 있는 것 or 중요하지만 시간이 많이 남음(우선 순위를 미룰 수 있음) |
| Low(4)     | 해도 되고 안 해도 됨                                         |
| Lowest(5)  | 진짜 필요할 때만                                             |



### :triangular_ruler: Jira Epic
| 이름 			| 내용			|
|----------------|------------------------|
| 기획          | 기획서, 명세서, 요구사항 정리, 문서 작성             |
| Sign Language | 수어 번역 관련 모든 처리                             |
| WebRTC        | WebRTC 관련 모든 처리(openvidu)                      |
| Project       | 백엔드, 프론트엔드 REQ&RES, DB스키마, 데이터 처리 등 |
| Design        | 와이어 프레임, 프로토타입, 화면 구성, 레이아웃 배치  |
| 배포 및 CI/CD | 배포, action, jenkins, CI/CD 등                      |
| 발표          | PPT 제작, 발표 연습, 시연 영상 찍기 등의 발표 준비   |
| UCC           | UCC 스토리보드  제작, UCC 제작 회의 및 촬영          |
| Docs          | Readme, 기타 제출해야 할 문서 작성                   |









## :family: 팀 소개



### :angel: 팀원 소개

> | 소개  | 이름   | 역할                            | Github		 |
> | ----- | ------ | ------------------------------- | ----------|
> | 🐻팀장 | 남궁휘 | 프로젝트 총괄                   | https://github.com/whiterabbit7 |
> | 🐨팀원 | 김순요 | 모델 연동 및 웹 페이지 구축     | https://github.com/KSoonYo |
> | 🐯팀원 | 손모은 | 알고리즘 개발 및 세부 기능 설계 | https://github.com/moeun2 |
> | 🐱팀원 | 한승훈 | 자연어 전처리 및 데이터 전처리  | https://github.com/gkstmdgns422 |
> | 🐰팀원 | 황보라 | 데이터 수집 및 세부 기능 설계   | https://github.com/yellow-purple |


### :palm_tree: Front end

- **남궁휘(팀장, frontend)**
  - 발표 총괄
  - BF 네비게이션 바 구현 및 스타일링
  - BF 메인 페이지 구현 및 CSS 스타일링

- **김순요(팀원, frontend)**

  - 회의 기록 담당
  - Web Speech API를 활용한 STT 구현
    - 음성에 매핑한 수어 영상 출력
    - 음성을 자막 텍스트로 출력
  - 강의실 CSS 디자인
  - 강의실 기본 기능 구현
    - 화면 공유
    - 채팅
    - 마이크 on/off
    - 음소거 on/off
    - 자막 on/off
    - 수어 on/off
    - 호스트, 참여자 화상 소통
    - 수업 기록
    - 강의 종료

- **한승훈(팀원, frontend)**

  - PyKomoran, django로 수어 형태소 분석 구현
    
    - PyKomoran : STT로 들어오는 단어의 형태소 분석
    - django : DB에 영상, 수어 단어 모델링
    
    - AI API·DATA로 단어간 유사도 분석
      - AI API·DATA : DB안에 이름이 중복된 단어가 존재할시 문장에서 단어간의 유사도 분석을 통해 올바른 단어 파싱

  - 강의실 리스트, 프로필 페이지, 로그인, 회원가입 기능 구현 및 CSS 스타일링

  - 아이디 찾기, 비밀번호 찾기 CSS 스타일링

  - BF 메인 페이지 구현 및 CSS 스타일링



### :railway_track: Back end

- **손모은(팀원, backend)**

  - 서버 총괄

  - BF 로그인, 회원가입, 프로필 CRUD 백엔드 API 구축(회원관리)

  - 배포 환경 구축

    - ubuntu
    - nginx 
    - vue
    - spring boot
    - django
    - openVidu 
    - HTTPS SSL 

  - 수어 데이터 크롤링

  - MySQL 데이터베이스 구축 및 연동

    

- **황보라(팀원 backend)**

  - UCC 영상 촬영, 편집 총괄
  - BF 강의실 CRUD 백엔드 API 구축(강의실 관리)
  - 이미지 관리 API 구축
  - OpenVidu Java 백엔드 API 구축
  - 수어 애니메이션 아바타 모델링
  - 수어 데이터 크롤링
  - MySQL 데이터베이스 구축 및 연동
  



## :heart: 소감



**남궁휘**: 갑작스러운 팀장님의 이탈로 팀장 역할을 이어 받게 됐는데 좋은 팀원분들과 함께 프로젝트 진행을 할 수 있어서 너무 즐거웠습니다. 다들 너무 잘해주셔서 저는 크게 프로젝트에 기여한게 없는것 같아 너무 죄송하네요. 다음 특화 프로젝트도 함께 진행하니까 더 열심히 공부해서 프로젝트에 좀 기여를 더 하고 싶어요! 모두 수고 많으셨습니다!



**한승훈**: 이제 끝이났네요.. 생전 처음 체계적으로 프로젝트를 진행해보며 부족한점이 많았지만 팀원들 덕분에 잘 버티고 이겨냈다고 생각합니다.. 또 너무 열심히하고 싶은 마음에 팀원분들에게 폐를 끼쳤을까봐 죄송한 마음도 있습니다.  프로젝트를 진행에 도움과 디자인 논의를 했던 우리 팀장 휘님, 강의실 관련 문제로 항상 얘기를 나눴던 보라님, 회원관리와 서버에러잡기로 저를 불러내주시던 모은님, 프론트 기능 논의로 매일 저와 씨름했던 순요님 모두 감사합니다!!! 특화, 자율 프로젝트, 더 나아가 취업에서도 모두 만났으면 좋겠습니다!
