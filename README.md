# 🐶 CATDOGPIA
반려동물 패션 커뮤니티 커머스  
#### - 💻 [배포링크](http://ec2-3-35-49-238.ap-northeast-2.compute.amazonaws.com/)   
<br>

## 👨‍👩‍👧‍👦 어금지
|팀장|팀원|팀원|팀원|
|:---:|:---:|:---:|:---:|
|![](https://github.com/likelion-backend-5th/Final_Project_7Team/assets/115778770/e7cce174-4edb-48eb-9652-70af99e43c23)|![](https://github.com/likelion-backend-5th/Final_Project_7Team/assets/115778770/e4a98776-a12d-4238-b6dd-75128684f0db)|![](https://github.com/likelion-backend-5th/Final_Project_7Team/assets/115778770/575675bc-ddc5-463b-a265-2961aadf3805)|![](https://github.com/likelion-backend-5th/Final_Project_7Team/assets/115778770/090e4f02-b000-4250-9f06-5632a4ee423b)|
|[김규리](https://github.com/kkyuwoo)|[김주환](https://github.com/juhwan-Ki)|[이용준](https://github.com/2hapu)|[최선영](https://github.com/choiseonyoung)|
|회원가입, 로그인, 커뮤니티|관리자, 고객센터, 신고|상품, 결제|장바구니, 마이페이지, 메인화면|
<br>

## 💡 프로젝트 소개
다양한 스타일과 사이즈의 반려동물 의류, 잡화를 판매하는 커머스와 반려인들이 주로 정보를 얻는 커뮤니티를 결합하여 반려동물과 함께하는 삶을 더욱 특별하고 스타일리시하게 만들어주는 커뮤니티 커머스 플랫폼입니다.
<br>
<br>

## ✨ 대표 페이지
### - 메인 페이지
![메인](https://github.com/likelion-backend-5th/Final_Project_7Team/assets/115778770/1d4d7c8b-ae89-44b3-a9e1-6180c48dfdeb)

### - 커뮤니티 페이지
![커뮤니티](https://github.com/likelion-backend-5th/Final_Project_7Team/assets/115778770/d263664e-0e48-4e6f-ba93-6b71098cbe29)

### - 관리자 페이지
![관리자](https://github.com/likelion-backend-5th/Final_Project_7Team/assets/115778770/430440df-8e8c-4b03-bad7-80035a1bf332)


## 🗓 프로젝트 기간
2023.08.03 ~ 2023.09.15
<br>
<br>

## ⚙ 아키텍처, ERD
### - 아키텍처
![2c8ca1c54b8db9f6](https://github.com/likelion-backend-5th/Final_Project_7Team/assets/87765888/d249204b-59ab-44c2-9f78-75e3baf9174e)


### - ERD
![](https://github.com/likelion-backend-5th/Final_Project_7Team/assets/115778770/67e22a02-737d-4d68-88e3-de101c170c19)
https://www.erdcloud.com/d/nL2CXshZxY9vNDQSx
<br>
<br>

## 🛠 개발 환경
#### - 통합 개발 환경 : IntelliJ IDEA
#### - 프로그래밍 언어 : Java, JavaScript
#### - 버전 관리 시스템 : GitHub
#### - DBMS : MySQL
#### - 프레임워크 : Spring Boot
#### - 클라우드 플랫폼 : RDS, S3
#### - 배포 : EC2, Docker
<br>

## 📝 주요 기능
### 1️⃣ 회원가입
- 아이디, 비밀번호, 이름, 휴대폰 번호, 이메일, 닉네임을 입력하고 이메일 인증 후 가입하기 버튼을 누르면 회원가입
- 네이버 간편 회원가입
- 아이디, 닉네임 중복 검사 / 비밀번호, 휴대폰 번호, 이메일 유효성 검사

### 2️⃣ 로그인
- 인증 - JWT 토큰 : Access Token / Refresh Token
- 아이디와 비밀번호가 일치하는지 확인 후 AccessToken과 RefreshToken 생성 및 발급
- 토큰 검증 후 유효시간 지났을 경우 RefreshToken으로 AccessToken 재발급 + RefreshToken 재발급 (RTR)
- 아이디 찾기
- 비밀번호 찾기 - 임시 비밀번호 이메일로 발급
- 로그아웃 - DB와 쿠키의 RefreshToken 삭제
- Oauth2 네이버 소셜 로그인 & 로그아웃
- 인가 - 토큰 검증 후 권한 부여
- 블랙리스트 회원 로그인 불가 처리

### 3️⃣ 커뮤니티
- 게시글 등록 - 로그인한 사용자에 한해 카테고리 선택, 제목, 이미지(0~5개까지 등록 가능), 내용 작성
- 게시글 수정 - 해당 게시글 작성자에 한해 카테고리 변경, 제목 변경, 이미지 변경(추가, 삭제 가능), 내용 변경
- 게시글 삭제 - 해당 게시글 작성자에 한해 Soft Delete
- 댓글 등록 - 로그인한 사용자에 한해 내용 작성
- 댓글 수정 - 해당 댓글 작성자에 한해 내용 변경
- 댓글 삭제 - 해당 댓글 작성자에 한해 Soft Delete
- 조회수
- 좋아요 - 로그인한 사용자에 한해 게시글 좋아요 가능, 버튼 다시 누르면 좋아요 취소
- 신고 - 로그인한 사용자에 한해 유해한 게시글, 댓글을 신고사유와 함께 관리자에게 신고
- 전체 게시글 목록 조회
- 카테고리 별 게시글 목록 조회
- 인기 게시글 목록 조회 - 일주일 이내 작성된 게시글 중 좋아요가 많은 순서로 3개. 좋아요 수가 같을 시 조회수 많은 순서
- 검색 필터[제목+내용, 제목, 내용, 작성자], 키워드로 게시글 검색

### 4️⃣ 장바구니
- 장바구니에 본인이 추가한 상풍 목록 조회
- 상품의 수량을 변경하거나 선택한 상품 일부 또는 전체 삭제
- 각 상품별 총 금액과 전체 상품 금액 표시

### 5️⃣ 마이페이지
- 프로필
   <br> 아이디, 이름, 닉네임, 이메일, 휴대폰 번호 등 본인이 회원가입시 등록했던 정보 조회
   <br> 비밀번호를 비롯한 개인 정보 변경
- 주문내역
   <br> 현재까지 상품을 주문한 내역을 주문 상태별로 필터링하여 확인
   <br> 주문번호를 클릭하여 주문한 상품 정보, 배송지 정보, 결제 정보 등의 주문 상세 확인
   <br> 주문 상태에 따라 구매 확정, 리뷰 작성, 교환 및 환불 요청 등 가능
- 적립금
   <br> 보유 적립금 총합 표시
   <br> 적립 및 사용 내역 확인 가능
- 배송지 관리
   <br> 카카오 우편번호 API를 통한 주소 입력
   <br> 기본 배송지 설정
   <br> 등록된 배송지 정보들 조회 및 수정, 삭제
- 게시글 관리
   <br> 현재까지 커뮤니티에 작성한 게시글 내역 조회
   <br> 선택한 글 일부 또는 전체 삭제

### 6️⃣ 메인 화면
- header, footer 표시
- 최근 일주일 간 주문량이 많았던 상품 목록 표시
- 최신 등록순 상품 표시
- 커뮤니티 인기글 표시

### 7️⃣ 관리자 기능

**회원관리**
- 캣독피아에 가입한 회원 조회 및 정보 수정
- 블랙리스트 관리 및 회원 삭제

**상품관리**
- 등록된 상품 조회, 등록 및 수정, 삭제 기능 구현 (Soft Delete)
- 상품 등록 시 상품명, 가격, 대표이미지, 상세이미지, 상품 상태를 설정하고 여러 상품 옵션을 가질 수 있도록 구현
- 상품 등록 및 수정 시 이미지는 반드시 들어가도록 구현하였고 이미지는 AWS S3에 저장하도록 구현

**주문관리**
- 발생한 주문에 대한 상세 내역 조회 및 주문 상태 변경 기능 구현
- 발생한 주문 처리에 따라 상태를 변경하도록 하고 변경된 상태에 따른 시간을 저장도록 구현

 **커뮤니티관리**
 - 커뮤니티 게시글 조회 및 게시글 단일, 다중 삭제 구현
 - 커뮤니티 게시글 상세 조회 시 댓글 목록 조회 
 - 댓글 목록 조회 시 댓글 등록 및 부적합한 댓글 삭제 기능 구현
 
**상품QnA관리**
- 사용자가 등록한 상품 QnA 조회 및 단일, 다중 삭제 구현
- 상세 조회 및 QnA에 대한 답변 기능 구현

**1:1문의관리**
- 사용자가 등록한 1:1문의 조회 및 단일, 다중 삭제 구현
- 상세 조회 및 1:1문의에 대한 답변 기능 구현

**신고관리**
- 신고된 신고 조회 및 신고 단일, 다중 삭제 구현
- 상세조회를 통해 신고된 게시글 또는 댓글에 대한 정보확인
- 신고처리를 통해 해당 신고된 게시글 또는 댓글을 삭제
- 신고를 3번 당하게되면 블랙리스트 회원으로 처리하여 로그인이 되지 않도록 처리 

**공지사항관리**
- 관리자가 등록한 공지사항 조회 및 단일, 다중 삭제 구현
- 공지사항 등록 및 수정 기능 구현

### 8️⃣ 고객센터
- 고객센터에서 관리자가 등록한 공지사항과 자주묻는 질문 조회
- 사용자가 마이페이지에서 자신이 등록한 1:1 문의 목록 조회
- 상세조회를 통해 관리자가 자신이 등록한 문의에 대한 답변 조회
- 사용자가 문의할 분류를 선택 후 문의 등록 가능
- 문의글은 관리자가 확인하고 있는 도중에서 수정이 될 수 있기 때문에 수정 기능은 구현하지 않음