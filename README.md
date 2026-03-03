# prography-11th-backend
prography 11th backend assignment


## 외래키 제약 방식의 사용이유
- 본 프로젝트는 MSA 환경도 아니고 JPA를 사용해서 진행할 것이라 개발 편의성과 데이터의 일관성을 이유로 외래키 제약 방식을 사용했습니다.

## index 설정 이유
- UIX_MEMBER_LOGIN_ID : 로그인은 시스템에서 빈번하게 발생하는 조회임과 동시에 중복 방지 차원에서 유니크 인덱스로 선정
- UIX_QR_HASH_VALUE : QR코드는 매 기수 출석 시마다 쌓일 경우 데이터가 방대해 질 경우 대비와 중복 방지 차원에서 유니크 인덱스로 선정
- UIX_MEMBER_COHORT : 한 기수의 동일 회원이 두번 등록 됨을 방지하기 위함과 조회 성능 최적화를 위해 선정
- UIX_SESSION_MEMBER_ATTENDANCE : 세션내의 출결 목록 조회 성능 향상과 중복 출석 방지를 db 차원에서 방지 하기 위해 선정

- 추후 추가적인 인덱스로 성능향상을 노릴 수도 있겠지만, 테이블 성격 및 프로젝트 규모 상 비효율적이라 생각됩니다.