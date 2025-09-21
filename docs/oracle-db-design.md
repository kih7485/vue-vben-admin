# Oracle Database 설계서 - 시스템 관리 및 인증

## 📋 개요
Vue Vben Admin 시스템의 사용자 인증 및 권한 관리를 위한 Oracle Database 스키마 설계

## 🗂️ 테이블 구조

### 1. 사용자 관리

#### TB_USER (사용자 테이블)
```sql
CREATE TABLE TB_USER (
    USER_ID         VARCHAR2(50)    NOT NULL,   -- 사용자 ID (PK)
    USER_NAME       VARCHAR2(100)   NOT NULL,   -- 사용자명
    PASSWORD        VARCHAR2(255)   NOT NULL,   -- 비밀번호 (암호화)
    REAL_NAME       NVARCHAR2(50),              -- 실명
    EMAIL           VARCHAR2(100),              -- 이메일
    PHONE           VARCHAR2(20),               -- 전화번호
    DEPT_CODE       VARCHAR2(20),               -- 부서 코드 (FK)
    STATUS          CHAR(1)         DEFAULT '1', -- 상태 (1: 활성, 0: 비활성)
    AVATAR          VARCHAR2(500),              -- 프로필 이미지 URL
    LAST_LOGIN_TIME DATE,                       -- 마지막 로그인 시간
    LAST_LOGIN_IP   VARCHAR2(50),               -- 마지막 로그인 IP
    PWD_RESET_TIME  DATE,                       -- 비밀번호 재설정 시간
    LOCK_FLAG       CHAR(1)         DEFAULT '0', -- 잠금 여부 (1: 잠금, 0: 정상)
    DEL_FLAG        CHAR(1)         DEFAULT '0', -- 삭제 여부 (1: 삭제, 0: 정상)
    CREATE_BY       VARCHAR2(50),               -- 생성자
    CREATE_TIME     DATE            DEFAULT SYSDATE, -- 생성 시간
    UPDATE_BY       VARCHAR2(50),               -- 수정자
    UPDATE_TIME     DATE,                       -- 수정 시간
    REMARK          VARCHAR2(500),              -- 비고
    CONSTRAINT PK_USER PRIMARY KEY (USER_ID)
);

-- 인덱스
CREATE UNIQUE INDEX IDX_USER_EMAIL ON TB_USER(EMAIL);
CREATE INDEX IDX_USER_DEPT ON TB_USER(DEPT_CODE);
CREATE INDEX IDX_USER_STATUS ON TB_USER(STATUS, DEL_FLAG);
```

#### TB_USER_ROLE (사용자-역할 매핑 테이블)
```sql
CREATE TABLE TB_USER_ROLE (
    USER_ID     VARCHAR2(50)    NOT NULL,   -- 사용자 ID (FK)
    ROLE_ID     VARCHAR2(20)    NOT NULL,   -- 역할 ID (FK)
    CREATE_TIME DATE            DEFAULT SYSDATE, -- 생성 시간
    CONSTRAINT PK_USER_ROLE PRIMARY KEY (USER_ID, ROLE_ID),
    CONSTRAINT FK_UR_USER FOREIGN KEY (USER_ID) REFERENCES TB_USER(USER_ID),
    CONSTRAINT FK_UR_ROLE FOREIGN KEY (ROLE_ID) REFERENCES TB_ROLE(ROLE_ID)
);
```

### 2. 역할 및 권한 관리

#### TB_ROLE (역할 테이블)
```sql
CREATE TABLE TB_ROLE (
    ROLE_ID     VARCHAR2(20)    NOT NULL,   -- 역할 ID (PK)
    ROLE_NAME   NVARCHAR2(50)   NOT NULL,   -- 역할명
    ROLE_CODE   VARCHAR2(50)    NOT NULL,   -- 역할 코드
    DESCRIPTION NVARCHAR2(200),             -- 역할 설명
    SORT_NO     NUMBER(4)       DEFAULT 0,  -- 정렬 순서
    STATUS      CHAR(1)         DEFAULT '1', -- 상태 (1: 활성, 0: 비활성)
    DATA_SCOPE  CHAR(1)         DEFAULT '1', -- 데이터 범위 (1: 전체, 2: 부서, 3: 개인)
    DEL_FLAG    CHAR(1)         DEFAULT '0', -- 삭제 여부
    CREATE_BY   VARCHAR2(50),               -- 생성자
    CREATE_TIME DATE            DEFAULT SYSDATE, -- 생성 시간
    UPDATE_BY   VARCHAR2(50),               -- 수정자
    UPDATE_TIME DATE,                       -- 수정 시간
    REMARK      VARCHAR2(500),              -- 비고
    CONSTRAINT PK_ROLE PRIMARY KEY (ROLE_ID)
);

-- 인덱스
CREATE UNIQUE INDEX IDX_ROLE_CODE ON TB_ROLE(ROLE_CODE);
```

#### TB_ROLE_MENU (역할-메뉴 매핑 테이블)
```sql
CREATE TABLE TB_ROLE_MENU (
    ROLE_ID     VARCHAR2(20)    NOT NULL,   -- 역할 ID (FK)
    MENU_ID     VARCHAR2(20)    NOT NULL,   -- 메뉴 ID (FK)
    CREATE_TIME DATE            DEFAULT SYSDATE, -- 생성 시간
    CONSTRAINT PK_ROLE_MENU PRIMARY KEY (ROLE_ID, MENU_ID),
    CONSTRAINT FK_RM_ROLE FOREIGN KEY (ROLE_ID) REFERENCES TB_ROLE(ROLE_ID),
    CONSTRAINT FK_RM_MENU FOREIGN KEY (MENU_ID) REFERENCES TB_MENU(MENU_ID)
);
```

### 3. 메뉴 관리

#### TB_MENU (메뉴 테이블)
```sql
CREATE TABLE TB_MENU (
    MENU_ID     VARCHAR2(20)    NOT NULL,   -- 메뉴 ID (PK)
    PARENT_ID   VARCHAR2(20)    DEFAULT '0', -- 상위 메뉴 ID
    MENU_NAME   NVARCHAR2(50)   NOT NULL,   -- 메뉴명
    MENU_PATH   VARCHAR2(200),              -- 라우터 경로
    COMPONENT   VARCHAR2(255),              -- 컴포넌트 경로
    MENU_TYPE   CHAR(1)         NOT NULL,   -- 메뉴 타입 (M: 메뉴, C: 목록, F: 버튼)
    VISIBLE     CHAR(1)         DEFAULT '1', -- 표시 여부 (1: 표시, 0: 숨김)
    STATUS      CHAR(1)         DEFAULT '1', -- 상태 (1: 활성, 0: 비활성)
    PERMS       VARCHAR2(100),              -- 권한 표시
    ICON        VARCHAR2(100),              -- 아이콘
    SORT_NO     NUMBER(4)       DEFAULT 0,  -- 정렬 순서
    IS_FRAME    CHAR(1)         DEFAULT '0', -- 외부 링크 여부 (1: 예, 0: 아니오)
    IS_CACHE    CHAR(1)         DEFAULT '1', -- 캐시 여부 (1: 캐시, 0: 비캐시)
    CREATE_BY   VARCHAR2(50),               -- 생성자
    CREATE_TIME DATE            DEFAULT SYSDATE, -- 생성 시간
    UPDATE_BY   VARCHAR2(50),               -- 수정자
    UPDATE_TIME DATE,                       -- 수정 시간
    REMARK      VARCHAR2(500),              -- 비고
    CONSTRAINT PK_MENU PRIMARY KEY (MENU_ID)
);

-- 인덱스
CREATE INDEX IDX_MENU_PARENT ON TB_MENU(PARENT_ID);
CREATE INDEX IDX_MENU_STATUS ON TB_MENU(STATUS, VISIBLE);
```

### 4. 부서 관리

#### TB_DEPT (부서 테이블)
```sql
CREATE TABLE TB_DEPT (
    DEPT_CODE   VARCHAR2(20)    NOT NULL,   -- 부서 코드 (PK)
    PARENT_CODE VARCHAR2(20)    DEFAULT '0', -- 상위 부서 코드
    ANCESTORS   VARCHAR2(500),              -- 조상 목록 (계층 구조)
    DEPT_NAME   NVARCHAR2(50)   NOT NULL,   -- 부서명
    LEADER      VARCHAR2(50),               -- 부서장 사용자 ID
    PHONE       VARCHAR2(20),               -- 전화번호
    EMAIL       VARCHAR2(100),              -- 이메일
    SORT_NO     NUMBER(4)       DEFAULT 0,  -- 정렬 순서
    STATUS      CHAR(1)         DEFAULT '1', -- 상태 (1: 활성, 0: 비활성)
    DEL_FLAG    CHAR(1)         DEFAULT '0', -- 삭제 여부
    CREATE_BY   VARCHAR2(50),               -- 생성자
    CREATE_TIME DATE            DEFAULT SYSDATE, -- 생성 시간
    UPDATE_BY   VARCHAR2(50),               -- 수정자
    UPDATE_TIME DATE,                       -- 수정 시간
    CONSTRAINT PK_DEPT PRIMARY KEY (DEPT_CODE)
);

-- 인덱스
CREATE INDEX IDX_DEPT_PARENT ON TB_DEPT(PARENT_CODE);
CREATE INDEX IDX_DEPT_STATUS ON TB_DEPT(STATUS, DEL_FLAG);
```

### 5. 인증 및 세션 관리

#### TB_USER_TOKEN (사용자 토큰 테이블)
```sql
CREATE TABLE TB_USER_TOKEN (
    TOKEN_ID        VARCHAR2(50)    NOT NULL,   -- 토큰 ID (PK)
    USER_ID         VARCHAR2(50)    NOT NULL,   -- 사용자 ID (FK)
    ACCESS_TOKEN    VARCHAR2(500)   NOT NULL,   -- 액세스 토큰
    REFRESH_TOKEN   VARCHAR2(500),              -- 리프레시 토큰
    TOKEN_TYPE      VARCHAR2(20)    DEFAULT 'Bearer', -- 토큰 타입
    EXPIRES_IN      NUMBER(10),                 -- 만료 시간(초)
    USER_AGENT      VARCHAR2(500),              -- User Agent
    CLIENT_IP       VARCHAR2(50),               -- 클라이언트 IP
    CREATE_TIME     DATE            DEFAULT SYSDATE, -- 생성 시간
    EXPIRE_TIME     DATE,                       -- 만료 시간
    LAST_ACCESS     DATE,                       -- 마지막 접근 시간
    CONSTRAINT PK_USER_TOKEN PRIMARY KEY (TOKEN_ID),
    CONSTRAINT FK_UT_USER FOREIGN KEY (USER_ID) REFERENCES TB_USER(USER_ID)
);

-- 인덱스
CREATE INDEX IDX_TOKEN_USER ON TB_USER_TOKEN(USER_ID);
CREATE INDEX IDX_TOKEN_ACCESS ON TB_USER_TOKEN(ACCESS_TOKEN);
CREATE INDEX IDX_TOKEN_EXPIRE ON TB_USER_TOKEN(EXPIRE_TIME);
```

#### TB_LOGIN_LOG (로그인 로그 테이블)
```sql
CREATE TABLE TB_LOGIN_LOG (
    LOG_ID          VARCHAR2(32)    NOT NULL,   -- 로그 ID (PK)
    USER_ID         VARCHAR2(50),               -- 사용자 ID
    LOGIN_TYPE      CHAR(1)         DEFAULT '1', -- 로그인 타입 (1: 로그인, 2: 로그아웃)
    LOGIN_IP        VARCHAR2(50),               -- 로그인 IP
    LOGIN_LOCATION  NVARCHAR2(255),             -- 로그인 위치
    BROWSER         VARCHAR2(100),              -- 브라우저
    OS              VARCHAR2(100),              -- 운영체제
    STATUS          CHAR(1)         DEFAULT '1', -- 상태 (1: 성공, 0: 실패)
    MSG             NVARCHAR2(255),             -- 메시지
    LOGIN_TIME      DATE            DEFAULT SYSDATE, -- 로그인 시간
    CONSTRAINT PK_LOGIN_LOG PRIMARY KEY (LOG_ID)
);

-- 인덱스
CREATE INDEX IDX_LOG_USER ON TB_LOGIN_LOG(USER_ID);
CREATE INDEX IDX_LOG_TIME ON TB_LOGIN_LOG(LOGIN_TIME);
```

### 6. 시스템 설정

#### TB_CONFIG (시스템 설정 테이블)
```sql
CREATE TABLE TB_CONFIG (
    CONFIG_ID       NUMBER(10)      NOT NULL,   -- 설정 ID (PK)
    CONFIG_NAME     NVARCHAR2(100)  NOT NULL,   -- 설정명
    CONFIG_KEY      VARCHAR2(100)   NOT NULL,   -- 설정 키
    CONFIG_VALUE    VARCHAR2(500),              -- 설정 값
    CONFIG_TYPE     CHAR(1)         DEFAULT 'N', -- 설정 타입 (Y: 시스템, N: 일반)
    CREATE_BY       VARCHAR2(50),               -- 생성자
    CREATE_TIME     DATE            DEFAULT SYSDATE, -- 생성 시간
    UPDATE_BY       VARCHAR2(50),               -- 수정자
    UPDATE_TIME     DATE,                       -- 수정 시간
    REMARK          VARCHAR2(500),              -- 비고
    CONSTRAINT PK_CONFIG PRIMARY KEY (CONFIG_ID)
);

-- 인덱스
CREATE UNIQUE INDEX IDX_CONFIG_KEY ON TB_CONFIG(CONFIG_KEY);
```

#### TB_CODE_TYPE (코드 타입 테이블)
```sql
CREATE TABLE TB_CODE_TYPE (
    CODE_TYPE_ID    NUMBER(10)      NOT NULL,   -- 코드 타입 ID (PK)
    CODE_TYPE_NAME  NVARCHAR2(100)  NOT NULL,   -- 코드 타입명
    CODE_TYPE       VARCHAR2(100)   NOT NULL,   -- 코드 타입 코드
    STATUS          CHAR(1)         DEFAULT '1', -- 상태
    CREATE_BY       VARCHAR2(50),               -- 생성자
    CREATE_TIME     DATE            DEFAULT SYSDATE, -- 생성 시간
    UPDATE_BY       VARCHAR2(50),               -- 수정자
    UPDATE_TIME     DATE,                       -- 수정 시간
    REMARK          VARCHAR2(500),              -- 비고
    CONSTRAINT PK_CODE_TYPE PRIMARY KEY (CODE_TYPE_ID)
);

-- 인덱스
CREATE UNIQUE INDEX IDX_CODE_TYPE ON TB_CODE_TYPE(CODE_TYPE);
```

#### TB_CODE_DATA (코드 데이터 테이블)
```sql
CREATE TABLE TB_CODE_DATA (
    CODE_DATA_ID    NUMBER(10)      NOT NULL,   -- 코드 데이터 ID (PK)
    CODE_TYPE       VARCHAR2(100)   NOT NULL,   -- 코드 타입 (FK)
    CODE_LABEL      NVARCHAR2(100)  NOT NULL,   -- 코드 라벨
    CODE_VALUE      VARCHAR2(100)   NOT NULL,   -- 코드 값
    CODE_SORT       NUMBER(4)       DEFAULT 0,  -- 정렬 순서
    STATUS          CHAR(1)         DEFAULT '1', -- 상태
    IS_DEFAULT      CHAR(1)         DEFAULT 'N', -- 기본값 여부
    CREATE_BY       VARCHAR2(50),               -- 생성자
    CREATE_TIME     DATE            DEFAULT SYSDATE, -- 생성 시간
    UPDATE_BY       VARCHAR2(50),               -- 수정자
    UPDATE_TIME     DATE,                       -- 수정 시간
    REMARK          VARCHAR2(500),              -- 비고
    CONSTRAINT PK_CODE_DATA PRIMARY KEY (CODE_DATA_ID),
    CONSTRAINT FK_CODE_DATA_TYPE FOREIGN KEY (CODE_TYPE) REFERENCES TB_CODE_TYPE(CODE_TYPE)
);

-- 인덱스
CREATE INDEX IDX_CODE_DATA_TYPE ON TB_CODE_DATA(CODE_TYPE);
```

## 🔄 시퀀스

```sql
-- 사용자 시퀀스
CREATE SEQUENCE SEQ_USER_ID START WITH 1 INCREMENT BY 1;

-- 로그 시퀀스
CREATE SEQUENCE SEQ_LOG_ID START WITH 1 INCREMENT BY 1;

-- 설정 시퀀스
CREATE SEQUENCE SEQ_CONFIG_ID START WITH 1 INCREMENT BY 1;

-- 코드 시퀀스
CREATE SEQUENCE SEQ_CODE_TYPE_ID START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE SEQ_CODE_DATA_ID START WITH 1 INCREMENT BY 1;
```

## 📊 초기 데이터

### 기본 역할
```sql
-- 관리자 역할
INSERT INTO TB_ROLE (ROLE_ID, ROLE_NAME, ROLE_CODE, DESCRIPTION, SORT_NO, STATUS)
VALUES ('ROLE001', '시스템 관리자', 'admin', '시스템 전체를 관리하는 최고 권한 역할', 1, '1');

-- 일반 사용자 역할
INSERT INTO TB_ROLE (ROLE_ID, ROLE_NAME, ROLE_CODE, DESCRIPTION, SORT_NO, STATUS)
VALUES ('ROLE002', '일반 사용자', 'user', '기본 사용자 권한을 가진 일반 역할', 2, '1');
```

### 기본 부서
```sql
INSERT INTO TB_DEPT (DEPT_CODE, PARENT_CODE, DEPT_NAME, SORT_NO, STATUS)
VALUES ('DEPT001', '0', '본사', 1, '1');

INSERT INTO TB_DEPT (DEPT_CODE, PARENT_CODE, DEPT_NAME, SORT_NO, STATUS)
VALUES ('DEPT002', 'DEPT001', '개발팀', 1, '1');

INSERT INTO TB_DEPT (DEPT_CODE, PARENT_CODE, DEPT_NAME, SORT_NO, STATUS)
VALUES ('DEPT003', 'DEPT001', '운영팀', 2, '1');
```

### 기본 관리자 계정
```sql
-- 비밀번호는 암호화 필요 (예: 123456)
INSERT INTO TB_USER (USER_ID, USER_NAME, PASSWORD, REAL_NAME, EMAIL, DEPT_CODE, STATUS)
VALUES ('admin', 'admin', '$2a$10$...', '시스템 관리자', 'admin@example.com', 'DEPT002', '1');

-- 관리자 역할 부여
INSERT INTO TB_USER_ROLE (USER_ID, ROLE_ID)
VALUES ('admin', 'ROLE001');
```

### 기본 코드 데이터
```sql
-- 사용자 상태 코드
INSERT INTO TB_CODE_TYPE (CODE_TYPE_ID, CODE_TYPE_NAME, CODE_TYPE, STATUS)
VALUES (SEQ_CODE_TYPE_ID.NEXTVAL, '사용자 상태', 'user_status', '1');

INSERT INTO TB_CODE_DATA (CODE_DATA_ID, CODE_TYPE, CODE_LABEL, CODE_VALUE, CODE_SORT, IS_DEFAULT)
VALUES (SEQ_CODE_DATA_ID.NEXTVAL, 'user_status', '활성', '1', 1, 'Y');
INSERT INTO TB_CODE_DATA (CODE_DATA_ID, CODE_TYPE, CODE_LABEL, CODE_VALUE, CODE_SORT)
VALUES (SEQ_CODE_DATA_ID.NEXTVAL, 'user_status', '비활성', '0', 2);

-- 메뉴 타입 코드
INSERT INTO TB_CODE_TYPE (CODE_TYPE_ID, CODE_TYPE_NAME, CODE_TYPE, STATUS)
VALUES (SEQ_CODE_TYPE_ID.NEXTVAL, '메뉴 타입', 'menu_type', '1');

INSERT INTO TB_CODE_DATA (CODE_DATA_ID, CODE_TYPE, CODE_LABEL, CODE_VALUE, CODE_SORT)
VALUES (SEQ_CODE_DATA_ID.NEXTVAL, 'menu_type', '디렉토리', 'M', 1);
INSERT INTO TB_CODE_DATA (CODE_DATA_ID, CODE_TYPE, CODE_LABEL, CODE_VALUE, CODE_SORT)
VALUES (SEQ_CODE_DATA_ID.NEXTVAL, 'menu_type', '메뉴', 'C', 2);
INSERT INTO TB_CODE_DATA (CODE_DATA_ID, CODE_TYPE, CODE_LABEL, CODE_VALUE, CODE_SORT)
VALUES (SEQ_CODE_DATA_ID.NEXTVAL, 'menu_type', '버튼', 'F', 3);

-- 데이터 범위 코드
INSERT INTO TB_CODE_TYPE (CODE_TYPE_ID, CODE_TYPE_NAME, CODE_TYPE, STATUS)
VALUES (SEQ_CODE_TYPE_ID.NEXTVAL, '데이터 범위', 'data_scope', '1');

INSERT INTO TB_CODE_DATA (CODE_DATA_ID, CODE_TYPE, CODE_LABEL, CODE_VALUE, CODE_SORT)
VALUES (SEQ_CODE_DATA_ID.NEXTVAL, 'data_scope', '전체', '1', 1);
INSERT INTO TB_CODE_DATA (CODE_DATA_ID, CODE_TYPE, CODE_LABEL, CODE_VALUE, CODE_SORT)
VALUES (SEQ_CODE_DATA_ID.NEXTVAL, 'data_scope', '부서', '2', 2);
INSERT INTO TB_CODE_DATA (CODE_DATA_ID, CODE_TYPE, CODE_LABEL, CODE_VALUE, CODE_SORT)
VALUES (SEQ_CODE_DATA_ID.NEXTVAL, 'data_scope', '개인', '3', 3);

-- 로그인 타입 코드
INSERT INTO TB_CODE_TYPE (CODE_TYPE_ID, CODE_TYPE_NAME, CODE_TYPE, STATUS)
VALUES (SEQ_CODE_TYPE_ID.NEXTVAL, '로그인 타입', 'login_type', '1');

INSERT INTO TB_CODE_DATA (CODE_DATA_ID, CODE_TYPE, CODE_LABEL, CODE_VALUE, CODE_SORT)
VALUES (SEQ_CODE_DATA_ID.NEXTVAL, 'login_type', '로그인', '1', 1);
INSERT INTO TB_CODE_DATA (CODE_DATA_ID, CODE_TYPE, CODE_LABEL, CODE_VALUE, CODE_SORT)
VALUES (SEQ_CODE_DATA_ID.NEXTVAL, 'login_type', '로그아웃', '2', 2);
```

### 기본 메뉴
```sql
-- 시스템 관리 메뉴
INSERT INTO TB_MENU (MENU_ID, PARENT_ID, MENU_NAME, MENU_PATH, MENU_TYPE, ICON, SORT_NO)
VALUES ('M001', '0', '시스템 관리', '/system', 'M', 'ion:settings-outline', 9997);

-- 사용자 관리
INSERT INTO TB_MENU (MENU_ID, PARENT_ID, MENU_NAME, MENU_PATH, COMPONENT, MENU_TYPE, ICON, SORT_NO)
VALUES ('M002', 'M001', '사용자 관리', '/system/user', '/views/system/user/list', 'C', 'mdi:account', 1);

-- 역할 관리
INSERT INTO TB_MENU (MENU_ID, PARENT_ID, MENU_NAME, MENU_PATH, COMPONENT, MENU_TYPE, ICON, SORT_NO)
VALUES ('M003', 'M001', '역할 관리', '/system/role', '/views/system/role/list', 'C', 'mdi:account-group', 2);

-- 메뉴 관리
INSERT INTO TB_MENU (MENU_ID, PARENT_ID, MENU_NAME, MENU_PATH, COMPONENT, MENU_TYPE, ICON, SORT_NO)
VALUES ('M004', 'M001', '메뉴 관리', '/system/menu', '/views/system/menu/list', 'C', 'mdi:menu', 3);

-- 부서 관리
INSERT INTO TB_MENU (MENU_ID, PARENT_ID, MENU_NAME, MENU_PATH, COMPONENT, MENU_TYPE, ICON, SORT_NO)
VALUES ('M005', 'M001', '부서 관리', '/system/dept', '/views/system/dept/list', 'C', 'charm:organisation', 4);

-- 코드 관리
INSERT INTO TB_MENU (MENU_ID, PARENT_ID, MENU_NAME, MENU_PATH, COMPONENT, MENU_TYPE, ICON, SORT_NO)
VALUES ('M006', 'M001', '코드 관리', '/system/code', '/views/system/code/list', 'C', 'mdi:code-tags', 5);
```

## 🔐 보안 고려사항

### 1. 비밀번호 암호화
- BCrypt 또는 PBKDF2 알고리즘 사용
- Salt 값 포함하여 저장

### 2. 토큰 관리
- JWT 토큰 사용
- 액세스 토큰: 30분
- 리프레시 토큰: 7일
- 토큰 만료 시 자동 갱신

### 3. 로그인 보안
- 5회 실패 시 계정 잠금
- 캡차 기능 적용
- IP 기반 접근 제어

### 4. 데이터 암호화
- 민감 정보 컬럼 암호화 (TDE 사용)
- 통신 구간 SSL/TLS 적용

## 📈 성능 최적화

### 1. 인덱스 전략
- 자주 조회되는 컬럼에 인덱스 생성
- 복합 인덱스 활용
- 정기적인 인덱스 리빌드

### 2. 파티셔닝
```sql
-- 로그 테이블 월별 파티셔닝
CREATE TABLE TB_LOGIN_LOG (
    -- 컬럼 정의...
)
PARTITION BY RANGE (LOGIN_TIME) (
    PARTITION P202501 VALUES LESS THAN (TO_DATE('2025-02-01', 'YYYY-MM-DD')),
    PARTITION P202502 VALUES LESS THAN (TO_DATE('2025-03-01', 'YYYY-MM-DD')),
    -- ...
);
```

### 3. 데이터 아카이빙
- 6개월 이상 지난 로그 데이터 아카이빙
- 삭제된 데이터 별도 테이블 보관

## 🔄 백업 및 복구

### 1. 백업 정책
- 일일 풀 백업
- 시간별 증분 백업
- RMAN 사용

### 2. 복구 시나리오
- Point-in-time 복구
- 테이블스페이스 복구
- 데이터파일 복구

## 📝 명명 규칙

### 1. 테이블
- TB_ 접두사 사용
- 대문자, 언더스코어 구분

### 2. 컬럼
- 대문자, 언더스코어 구분
- 의미 있는 약어 사용

### 3. 인덱스
- IDX_ 접두사 사용
- IDX_테이블명_컬럼명

### 4. 제약조건
- PK_테이블명: 기본키
- FK_테이블약어_참조테이블약어: 외래키
- UK_테이블명_컬럼명: 유니크

## 🚀 향후 확장 계획

### 1. 감사 로그
- 모든 CRUD 작업 로깅
- 변경 이력 추적

### 2. 알림 시스템
- 시스템 알림 테이블
- 사용자별 알림 설정

### 3. 워크플로우
- 결재 프로세스 테이블
- 위임 관리

### 4. 파일 관리
- 첨부파일 메타데이터
- 파일 버전 관리