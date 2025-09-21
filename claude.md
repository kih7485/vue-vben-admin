# Vue Vben Admin - 프로젝트 개요

## 📋 프로젝트 정보

**Vue Vben Admin**은 Vue 3, Vite, TypeScript를 기반으로 구축된 현대적인 중후반대 관리 시스템 템플릿입니다.

- **버전**: 5.5.9
- **홈페이지**: https://vben.pro
- **저장소**: https://github.com/vbenjs/vue-vben-admin
- **라이선스**: MIT

## 🏗️ 프로젝트 구조

### 모노레포 아키텍처
pnpm workspace와 Turborepo를 사용한 모노레포 구조로 구성되어 있습니다.

```
vue-vben-admin/
├── apps/                    # 애플리케이션
│   ├── backend-mock/       # 백엔드 Mock 서버
│   ├── web-antd/          # Ant Design Vue 버전
│   ├── web-ele/           # Element Plus 버전
│   └── web-naive/         # Naive UI 버전
├── packages/               # 공유 패키지
│   ├── @core/             # 핵심 기능
│   │   ├── base/          # 기본 유틸리티
│   │   ├── composables/   # Vue Composables
│   │   ├── preferences/   # 사용자 설정 관리
│   │   └── ui-kit/        # UI 컴포넌트 킷
│   ├── constants/         # 상수 정의
│   ├── effects/           # 이펙트 관련 기능
│   ├── icons/             # 아이콘 관리
│   ├── locales/           # 다국어 지원
│   ├── preferences/       # 환경설정
│   ├── stores/            # Pinia 스토어
│   ├── styles/            # 스타일시트
│   ├── types/             # TypeScript 타입 정의
│   └── utils/             # 유틸리티 함수
├── internal/               # 내부 도구
│   ├── lint-configs/      # Lint 설정
│   ├── node-utils/        # Node.js 유틸리티
│   ├── tailwind-config/   # Tailwind CSS 설정
│   ├── tsconfig/          # TypeScript 설정
│   └── vite-config/       # Vite 설정
├── playground/            # 개발 플레이그라운드
├── docs/                  # 문서
└── scripts/               # 빌드 및 배포 스크립트
```

## 🚀 주요 기능

### 기술 스택
- **프레임워크**: Vue 3.5.17
- **빌드 도구**: Vite 7.1.2
- **언어**: TypeScript 5.8.3
- **CSS 프레임워크**: Tailwind CSS 3.4.17
- **상태 관리**: Pinia 3.0.3
- **라우팅**: Vue Router 4.5.1
- **국제화**: Vue I18n 11.1.7

### UI 프레임워크 지원
3가지 UI 프레임워크 버전을 제공합니다:
- **Ant Design Vue** (4.2.6)
- **Element Plus** (2.10.2)
- **Naive UI** (2.42.0)

### 핵심 기능
1. **인증 및 권한 관리**: 동적 라우트 기반 권한 시스템
2. **다국어 지원**: 내장된 국제화 기능
3. **테마 시스템**: 다양한 테마 색상 및 사용자 정의 옵션
4. **레이아웃 시스템**: 유연한 레이아웃 구성
5. **Mock 서버**: 개발용 백엔드 Mock 서버 내장
6. **PWA 지원**: Progressive Web App 기능
7. **코드 품질**: ESLint, Prettier, Stylelint 통합

## 📦 개발 환경 요구사항

- **Node.js**: >= 20.10.0
- **pnpm**: >= 9.12.0
- **패키지 매니저**: pnpm 10.14.0

## 🛠️ 주요 스크립트

### 개발
```bash
pnpm install          # 의존성 설치
pnpm dev             # 모든 앱 개발 서버 실행
pnpm dev:antd        # Ant Design 버전 실행
pnpm dev:ele         # Element Plus 버전 실행
pnpm dev:naive       # Naive UI 버전 실행
pnpm dev:play        # 플레이그라운드 실행
```

### 빌드
```bash
pnpm build           # 전체 빌드
pnpm build:antd      # Ant Design 버전 빌드
pnpm build:ele       # Element Plus 버전 빌드
pnpm build:naive     # Naive UI 버전 빌드
pnpm build:analyze   # 번들 분석과 함께 빌드
```

### 코드 품질
```bash
pnpm lint            # 코드 린팅
pnpm format          # 코드 포맷팅
pnpm typecheck       # TypeScript 타입 체크
pnpm check           # 전체 코드 검사 (순환 의존성, 타입, 철자 등)
pnpm test:unit       # 단위 테스트 실행
pnpm test:e2e        # E2E 테스트 실행
```

### 기타
```bash
pnpm preview         # 빌드 결과 미리보기
pnpm clean           # 빌드 결과물 및 캐시 정리
pnpm reinstall       # 클린 재설치
pnpm update:deps     # 의존성 업데이트
```

## 🔧 개발 도구

### Turbo 작업
프로젝트는 Turborepo를 사용하여 빌드 최적화와 캐싱을 관리합니다:
- `build`: 의존성 순서에 따른 빌드
- `dev`: 개발 서버 (캐시 없음, persistent)
- `preview`: 빌드 후 미리보기
- `typecheck`: 타입 체크

### Git 커밋 규칙
Angular 커밋 컨벤션을 따릅니다:
- `feat`: 새로운 기능
- `fix`: 버그 수정
- `docs`: 문서 수정
- `style`: 코드 스타일 변경
- `refactor`: 리팩토링
- `perf`: 성능 개선
- `test`: 테스트 코드
- `chore`: 빌드 과정 또는 보조 도구 변경
- `ci`: CI 관련 변경
- `types`: 타입 정의 변경

### 개발 환경 설정
- **VS Code Workspace**: `vben-admin.code-workspace` 파일 제공
- **Git Hooks**: Lefthook을 통한 pre-commit, commit-msg 검증
- **Docker**: 도커 빌드 지원 (`build:docker`)

## 📝 추가 정보

### 브라우저 지원
- Chrome 80+ (권장)
- Edge (최신 2개 버전)
- Firefox (최신 2개 버전)
- Safari (최신 2개 버전)
- IE는 지원하지 않음

### 테스트 계정
- 사용자명: vben
- 비밀번호: 123456

### 문서
- 공식 문서: https://doc.vben.pro/
- GitHub Discussions: https://github.com/anncwb/vue-vben-admin/discussions

## 🎯 개발 팁

1. **모노레포 작업**: 특정 패키지에서만 명령 실행하려면 `pnpm -F <package-name> <command>` 사용
2. **의존성 관리**: catalog를 통해 버전 관리 (`pnpm-workspace.yaml` 참조)
3. **타입 안정성**: 모든 패키지에서 TypeScript 사용 권장
4. **코드 품질**: 커밋 전 항상 `pnpm check` 실행

## 🚀 빠른 시작

```bash
# 1. 저장소 클론
git clone https://github.com/vbenjs/vue-vben-admin.git

# 2. 디렉토리 이동
cd vue-vben-admin

# 3. pnpm 활성화 (필요시)
npm i -g corepack
corepack enable

# 4. 의존성 설치
pnpm install

# 5. 개발 서버 실행 (Ant Design 버전)
pnpm dev:antd
```

개발 서버는 기본적으로 http://localhost:5173 에서 실행됩니다.