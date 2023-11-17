# 🎁선물하기 펀딩 서비스 (11.13 ~ 12.15)
사용자가 등록한 상품에 대해 다른 사용자들이 펀딩개념으로 자금을 모을 수 있는 서비스 입니다.

## 프로젝트 기능 및 설계
### [회원가입 & 로그인]

⭐회원가입
  - 자체 회원가입
  - 소셜로그인 (OAuth) : 카카오, 네이버

⭐로그인 (JWT + Redis)
  - JWT의 보안강화를 위해 Refresh Token을 Redis에 저장하는 방식으로 구현.

### [회원 관련 기능]
⭐회원 정보 조회
  - 본인의 회원 정보와 등록한 상품 & 상품의 펀딩 정보 확인 가능.

⭐회원 정보 수정
  - 회원정보 수정 가능.

⭐사용자 검색
  - 이메일을 통해 사용자 검색
  -> ES 이용한 구현
    
⭐친구 추가 요청
  - 기존에 친구가 아닌 사용자에 대해 친구 추가 요청을 보낼 수 있음.
  - 현재 자신이 친구 추가 요청을 보내지 않은 사용자에게만 요청을 보낼 수 있음.
    ex) A(id 1) -> B(id 2)에게 친구 요청 시
    - 친구 목록 테이블에 1 2 등록일시 WAIT 1 레코드 생성.
    
⭐친구요청 목록 조회
  - 현재 자신에게 들어온 친구 추가 요청 목록을 확인.
    - 회원 아이디가 자신이고 WAIT 상태의 요청들을 확인.

⭐친구요청 수락/거절
  - 친구 요청을 수락하면 요청자, 대상자  모두 업데이트.
    ex)  1 2 등록일시 WAIT 1 레코드에서 B가 요청 수락시 WAIT -> ACCEPT로 바뀜.
         B의 친구목록을 조회할 때 A가 떠야하므로 2 1 등록일시 ACCEPT null 이런 형태의 레코드 함께 삽입.
  
  - 거절하면(DENIED) 하루 뒤 테이블에서 삭제.
    
⭐친구목록 조회
  - 친구인 사용자들(상태가 ACCEPT인)의 목록 조회
  - 친구로 등록된 사용자 삭제 가능.
    
### [상품]
⭐전체 상품 목록 조회
  - 상품 테이블에 저장된 상품 정보를 랭킹 순으로 조회
    
⭐ 상품 검색
  - ES를 이용해 상품 조회

### [펀딩]
⭐ 펀딩 상품 등록
  - 펀딩을 진행할 자신의 상품으로 등록.
  - 상품 등록 시 펀딩 진행 기간은 1년으로 자동 지정.
      
⭐ 펀딩 상품 취소
  - 마감일 이전에 펀딩자가 진행 중이던 펀딩을 취소한 경우
  - 등록한 상품 중에서 취소 가능
  - 펀딩이 진행되고 있던 상황이라면 자기 지갑으로 펀딩 금액만큼 옮겨짐.
  - 더 이상 해당 펀딩자의 펀딩 진행 상품으로 보여지지 않음.

⭐ 펀딩 금액 달성
  - 마감일 이전에 펀딩 금액 달성 시 더 이상 다른 사용자의 펀딩 불가.
  - 펀딩이 완료된 상품에 대해서 펀딩자의 지갑으로 금액 이전 불가.
  - 달성 및 주소 확인 메일 전송 후 1일 뒤 지정한 주소로 상품 배송. (이때까진 배송상태 delivery_wait)
  - 배송 후 펀딩자의 펀딩 상품 목록에서 숨겨짐.(delivery_success)

⭐ 펀딩 금액 미달성
  - 마감일 임박 3일 전부터 안내 메일 발송.
  - 마감일 도래 시 펀딩 상품 목록에서 숨겨짐.
  - 마감일까지 펀딩이 완료되지 않았다면 펀딩자의 지갑으로 펀딩 금액만큼 들어옴.

### [후원]
⭐ 사용자가 등록한 펀딩 상품 조회
  - 사용자가 등록해놓은 상품과 펀딩 현황 확인
    - 펀딩 진행률이 높은 현황으로 정렬.
  - 친구로 등록된 사용자의 상품만 확인 가능.
    
⭐ 후원하기
  - 상대방이 등록해놓은 펀딩 상품에 대해 후원할 금액을 입력해서 지갑에서 금액 감소 후 후원완료.
  - 지갑의 돈은 펀딩 금액보다 커야함. (작을 시 미리 충전해서 사용)
    -> 동시성 이슈 해결문제
    
⭐ 후원취소
  - 후원 진행 후 1시간 이내의 건에 대해서만 취소 가능.
  - 취소 후 후원자의 지갑으로 금액 반환.
  - 후원한 상품에도 취소 금액만큼 펀딩 총액 감소.


### [결제]
⭐ 내 지갑으로 금액 충전
  - 결제 금액만큼 금액 충천. 

## ERD


## Trouble Shooting
![Uploading 선물하기 펀딩 프로젝트.png…]()


## Tech Stack
- Programming <img src="https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=Java&logoColor=white"/>
- Framework  <img src="https://img.shields.io/badge/Springboot-6DB33F?style=for-the-badge&logo=Springboot&logoColor=white"/>
- Database <img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white"/>
- security <img src="https://img.shields.io/badge/springsecurity-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white"/>
- search engine <img src="https://img.shields.io/badge/elasticsearch-005571?style=for-the-badge&logo=elasticsearch&logoColor=white"/>
- Cache & Concurrency <img src="https://img.shields.io/badge/redis-DC382D?style=for-the-badge&logo=redis&logoColor=white"/>
- release <img src="https://img.shields.io/badge/docker-2496ED?style=for-the-badge&logo=docker&logoColor=white"/> <img src="https://img.shields.io/badge/amazonaws-232F3E?style=for-the-badge&logo=amazonaws&logoColor=white"/> 
- Configuration Management <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white"/> <img src="https://img.shields.io/badge/sourcetree-0052CC?style=for-the-badge&logo=sourcetree&logoColor=white"/>
