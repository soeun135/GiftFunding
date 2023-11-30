## Trouble Shooting

### 필터 내 예외처리

### Dirty Checking
서비스 단 update 메소드에 @Transactional 사용하여 Dirty Checking을 통한 회원 정보 수정 작업 중 입력 한 정보 외에 모든 필드의 update 쿼리가 실행 됨.
Dirty Checking은 모든 필드의 업데이트를 기본으로 하고 있어 발생한 문제로 엔티티에 @DynamicUpdate 추가해서 해결.
