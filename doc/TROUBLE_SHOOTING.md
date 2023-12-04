# Trouble Shooting
프로젝트 중 발생한 문제들에 대해서 정리합니다.

## 필터 내 예외처리
토큰을 검증하는 과정에서 TokenProvider의 validateToken 메소드에서 던진 예외가 null로 뜨면서 에러처리가 안 되는 문제가 있었습니다.
- 원인 : 
토큰의 유효성 검증은 필터단에서 일어나는 로직이었고 필터는 servlet이전에 동작하기 때문에 @ControllerAdvice와 @ExceptionHandler로 처리할 수 없었습니다.

- 해결 : 
처음엔 JwtAuthenticationFilter의 예외를 처리할 수 있는 jwtExceptionFilter를 만들어서 HttpServletResponse를 세팅해서 내려주는 방식으로 해결하였습니다.
그러다가 인증과정에서 발생하는 예외는 AuthenticaionEntryPoint에서 잡아줄 수 있다는 것을 알게되어 AuthenticaionEntryPoint을 구현한 CustomAuthenticaionEntryPoint를 만들어 넘어오는 에러 타입에 따라 응답 형식을 에러타입, 에러 메세지, 에러코드 형식으로 세팅하여 클라이언트쪽으로 보내는 방식으로 해결했습니다.

## Dirty Checking
서비스 단 update 메소드에 @Transactional 사용하여 Dirty Checking을 통한 회원 정보 수정 작업 중 입력 한 정보 외에 모든 필드의 update 쿼리가 실행 되는 일이 있었습니다.

- 원인 : 
Dirty Checking은 모든 필드의 업데이트를 기본으로 하고 있어 발생한 문제였습니다.

- 해결 : 
관련 엔티티에 @DynamicUpdate을 추가해서 변경한 필드만 업데이트 되도록 하여 해결하였습니다.

## 영속성 관리 문제
UserDetailsService의 loadByUsername() 메소드를 통해 DB에서 조회되는 사용자 정보를 반환하기 위해 UserDetails를 엔티티에서 구현하였는데 이 경우 필터에서부터 entity를 들고다니게 되어 영속성 유지에서 문제 발생 가능성이 있었습니다.

이를 해결하고자 UserDetails를 엔티티에서 구현하는 것이 아닌 UserAdapter객체를 생성하여 구현하도록 하고 필드로는 인증완료된 Member 객체와 id, password를 담을 수 있도록 하였습니다. 

이를 통해 엔티티를 직접 필터에서부터 끌고다니는 문제를 없애고, 컨트롤러 단에서는 인증이 완료된 Authentication 객체를 UserAdapter를 통해 받아올 수 있게 됩니다.

