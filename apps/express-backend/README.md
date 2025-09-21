# Express Backend

Vue Vben Admin의 Spring Boot 기반 백엔드 API 서버입니다.

## 📋 프로젝트 정보

- **Framework**: Spring Boot 3.5.6
- **Java Version**: JDK 25
- **Database**: Oracle Database
- **Security**: Spring Security + JWT
- **Build Tool**: Gradle 8.10.2

## 🏗️ 아키텍처

### 패키지 구조
```
com.winus.express/
├── common/                    # 공통 기능
│   ├── config/               # 설정 클래스 (Security, CORS)
│   ├── dto/                  # 공통 DTO (ApiResponse, PageResponse, BaseEntity)
│   └── util/                 # 유틸리티 (JwtUtil)
├── modules/                  # 비즈니스 모듈
│   ├── auth/                 # 인증/인가 모듈
│   │   ├── controller/       # 인증 컨트롤러
│   │   └── dto/              # 인증 관련 DTO
│   └── system/               # 시스템 관리 모듈
│       ├── user/             # 사용자 관리
│       ├── role/             # 역할 관리
│       ├── menu/             # 메뉴 관리
│       └── department/       # 부서 관리
└── security/                 # 보안 관련
    ├── filter/               # JWT 인증 필터
    ├── provider/             # 인증 제공자
    └── principal/            # 사용자 주체
```

## 🚀 기술 스택

### Core Dependencies
- **Spring Boot Starter Web** - REST API 개발
- **Spring Boot Starter Data JPA** - 데이터 액세스
- **Spring Boot Starter Security** - 보안 및 인증
- **Spring Boot Starter Validation** - 데이터 검증

### Database
- **Oracle JDBC Driver** (ojdbc11:23.5.0.24.07) - Oracle 데이터베이스 연결

### Security & JWT
- **JWT API** (jjwt-api:0.12.6) - JWT 토큰 처리
- **JWT Implementation** (jjwt-impl:0.12.6)
- **JWT Jackson** (jjwt-jackson:0.12.6)

### Development Tools
- **Lombok** - 코드 간소화
- **Spring Boot DevTools** - 개발 편의성
- **Spring Boot Configuration Processor** - 설정 자동완성

### Testing
- **Spring Boot Starter Test** - 테스트 프레임워크
- **Spring Security Test** - 보안 테스트

## 📦 주요 기능

### 1. 인증 및 권한 관리
- JWT 기반 토큰 인증
- Role 기반 접근 제어 (RBAC)
- 비밀번호 암호화 (BCrypt)
- 토큰 갱신 (Refresh Token)

### 2. 사용자 관리
- 사용자 CRUD 작업
- 비밀번호 변경/초기화
- 계정 잠금/해제
- 사용자별 권한 관리

### 3. 시스템 관리
- **역할 관리**: 시스템 역할 및 권한 설정
- **메뉴 관리**: 동적 메뉴 구성 및 권한 연결
- **부서 관리**: 조직 구조 관리

## 🛠️ 개발 환경 설정

### 필요 조건
- JDK 25
- Oracle Database
- Gradle 8.10.2+

### 환경 설정
`src/main/resources/application.yml` 파일에서 다음 설정을 구성하세요:

```yaml
spring:
  datasource:
    url: jdbc:oracle:thin:@localhost:1521:XE
    username: your_username
    password: your_password

app:
  jwt:
    secret: your-jwt-secret-key
    access-token-expiration: 86400000  # 24시간
    refresh-token-expiration: 604800000 # 7일
  cors:
    allowed-origins:
      - http://localhost:5173
      - http://localhost:3000
```

## 🚀 실행 방법

### 1. 빌드 및 실행
```bash
# 프로젝트 빌드
./gradlew build

# 애플리케이션 실행
./gradlew bootRun

# 또는 JAR 파일 실행
java -jar build/libs/express-backend-0.0.1-SNAPSHOT.jar
```

### 2. 개발 모드 실행
```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

## 📋 API 문서

### 인증 API (`/api/auth`)
- `POST /api/auth/login` - 로그인
- `POST /api/auth/refresh` - 토큰 갱신
- `POST /api/auth/logout` - 로그아웃
- `GET /api/auth/me` - 현재 사용자 정보

### 사용자 관리 API (`/api/users`)
- `GET /api/users` - 사용자 목록 조회 (페이징)
- `GET /api/users/{userId}` - 사용자 상세 조회
- `POST /api/users` - 사용자 생성
- `PUT /api/users/{userId}` - 사용자 수정
- `DELETE /api/users/{userId}` - 사용자 삭제
- `PUT /api/users/{userId}/password` - 비밀번호 변경
- `PUT /api/users/{userId}/roles` - 사용자 역할 수정

### 역할 관리 API (`/api/roles`)
- `GET /api/roles` - 역할 목록 조회
- `POST /api/roles` - 역할 생성
- `PUT /api/roles/{roleId}` - 역할 수정
- `DELETE /api/roles/{roleId}` - 역할 삭제
- `PUT /api/roles/{roleId}/menus` - 역할 메뉴 권한 설정

### 메뉴 관리 API (`/api/menus`)
- `GET /api/menus` - 메뉴 목록 조회
- `GET /api/menus/tree` - 메뉴 트리 구조 조회
- `GET /api/menus/user/{userId}` - 사용자별 메뉴 조회
- `POST /api/menus` - 메뉴 생성
- `PUT /api/menus/{menuId}` - 메뉴 수정
- `DELETE /api/menus/{menuId}` - 메뉴 삭제

### 부서 관리 API (`/api/departments`)
- `GET /api/departments` - 부서 목록 조회
- `GET /api/departments/tree` - 부서 트리 구조 조회
- `POST /api/departments` - 부서 생성
- `PUT /api/departments/{deptId}` - 부서 수정
- `DELETE /api/departments/{deptId}` - 부서 삭제

## 🔐 보안 설정

### JWT 토큰
- **Access Token**: 24시간 유효
- **Refresh Token**: 7일 유효
- **알고리즘**: HMAC SHA-256

### 권한 설정
- `@PreAuthorize` 어노테이션을 통한 메서드 레벨 보안
- Role 기반 접근 제어 (ROLE_ADMIN, ROLE_USER 등)

### CORS 설정
- 프론트엔드 개발 서버 허용 (localhost:5173, localhost:3000)
- 인증 정보 포함 요청 허용

## 🗄️ 데이터베이스 설계

주요 테이블:
- `TB_USER` - 사용자 정보
- `TB_ROLE` - 역할 정보
- `TB_MENU` - 메뉴 정보
- `TB_DEPARTMENT` - 부서 정보
- `TB_USER_ROLE` - 사용자-역할 매핑
- `TB_ROLE_MENU` - 역할-메뉴 권한 매핑

자세한 데이터베이스 설계는 `docs/oracle-db-design.md` 파일을 참조하세요.

## 🧪 테스트

```bash
# 단위 테스트 실행
./gradlew test

# 테스트 커버리지 리포트 생성
./gradlew jacocoTestReport
```

## 📝 로깅

- **로그 레벨**: INFO (운영), DEBUG (개발)
- **로그 파일**: `logs/spring.log`
- **로그 패턴**: 날짜, 시간, 레벨, 스레드, 클래스명, 메시지

## 🔧 트러블슈팅

### 자주 발생하는 문제

1. **Oracle 연결 오류**
   - JDBC URL, 사용자명, 비밀번호 확인
   - Oracle 서버 실행 상태 확인

2. **JWT 토큰 오류**
   - JWT secret key 설정 확인
   - 토큰 만료 시간 확인

3. **CORS 오류**
   - allowed-origins 설정 확인
   - 프론트엔드 포트 번호 확인

## 📄 라이선스

이 프로젝트는 MIT 라이선스를 따릅니다.

## 👥 기여자

Vue Vben Admin 팀

---

더 자세한 정보는 [Vue Vben Admin 공식 문서](https://doc.vben.pro/)를 참조하세요.