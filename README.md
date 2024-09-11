PowerForecast Backend
=============
### 날씨 빅데이터 콘테스트: 기상에 따른 공동주택 전력수요 예측 개선
> 기상에 따른 공동주택 전력수요 예측을 개선하고 예측 결과 시각화하여 서비스로 제공해주는 웹사이트입니다.

## 개발환경
### 개발기간
2024.05.28 ~ 2024.06.27

### 프로젝트 팀 구성 및 역할
|백엔드|프런트엔드|데이터분석|
|:---:|:---:|:---:|
|이영인|김우정|이세련|
|<a href="https://github.com/Dr-Energy/Backend_PowerForecast" target="_blank"><img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white"></a>|<a href="https://github.com/Dr-Energy/FrontEnd_PowerForecast" target="_blank"><img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white"></a>|<a href="https://github.com/Dr-Energy/DA_PowerForecast" target="_blank"><img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white"></a>|

### 프레임워크
<img width="1280" alt="dr_e_framework" src="https://github.com/user-attachments/assets/84aacabb-345f-4ea9-9bbb-1a6d5e13b5dd">

### 시스템 구조
<img width="1280" alt="dr_e_Architecture" src="https://github.com/user-attachments/assets/1a0aad68-b3e0-4369-b51e-71cc86ae4dad">

## 주요기능
1. JWT 토큰 방식을 사용한 사용자 회원가입, 로그인
2. 기상청 API를 통한 지역에 따른 실시간 날씨 정보 조회
3. 지역에 따른 전력 예측 정보 조회
4. 이상 전력 예측에 따른 알람 전송
    - 웹소켓으로 구현
5. 전력 예측에 따른 과거 알람 목록 조회
    - 상승, 하락, 이상 알람

## 구현 결과
#### [ 클릭해여 재생해주세요 ]
[![Video Label](https://github.com/user-attachments/assets/ec913ef7-e281-4db6-8765-eef6ebbb1a9d)](https://www.youtube.com/watch?v=izu8rs2vhy0)

## EER-Diagram
<img width="1277" alt="dr_e_EER" src="https://github.com/user-attachments/assets/05ed830b-17c8-4c7b-a5f4-22fa208507ef">

## REST API
