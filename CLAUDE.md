# mestemplate

재사용 가능한 MES 백엔드 플랫폼 템플릿. 고객별 소스 포크 없이 설정·어댑터로 다고객 지원이 목표.

## Stack

* **Runtime**: Java 17 / Spring Boot 3.x / Gradle
* **DB**: PostgreSQL / Spring Data JPA / QueryDSL (복잡 조회) / Flyway (미도입 — 프로덕션 전 필수)
* **Auth**: Spring Security (Stateless) / JWT (access token 구현, refresh token 미구현)
* **기타**: Lombok / Manual Mapper (MapStruct 제거) / springdoc-openapi / Testcontainers

## Modules

* `global`: ApiResponse, ErrorCode, BusinessException, BaseEntity, Security (JWT, Filter, Principal)
* `auth`: 로그인 API, JWT 발급
* `item`: 품목 마스터 CRUD + QueryDSL 검색
* `user`: 사용자 마스터 CRUD + QueryDSL 검색 + 비밀번호 해시

## Docs

* `docs/ARCHITECTURE.md`: 패키지 구조, 요청·응답 흐름, 멀티테넌트, 인증, 영속성 규칙, Decision Log
* `docs/PRD.md`: 제품 목표, 구현 현황, 로드맵
* `docs/ADR.md`: 기술 결정 기록 (선택 / 이유 / 트레이드오프)

## Principles

* 결정이 필요하면 `docs/ARCHITECTURE.md` 확인 → Decision Log에 없으면 `docs/ADR.md`에 추가
* 구현은 minimal but complete. 불필요한 추상화 금지
* 한 수직 슬라이스를 끝까지 완성한 후 다음 모듈로 이동
* 새 모듈·API 변경·규칙 변경 시 관련 docs도 함께 업데이트
* 고객별 소스 포크 금지 — 설정·전략 인터페이스·어댑터로 확장

## Architecture Rules

**요청 흐름**
```
Controller → Request/Search → Command/Query → UseCase → Service
→ Domain → RepositoryPort → RepositoryAdapter → Mapper → Entity/JPA/QueryDSL → DB
```

**레이어 경계**
* Service는 RepositoryPort만 의존 (JPA Repository 직접 참조 금지)
* Controller는 JPA Entity 노출 금지
* 매퍼는 도메인 ↔ 엔티티 변환 전담 (도메인이 엔티티를 import하지 않음)

**멀티테넌트**
* 모든 업무 테이블에 `tenant_id` 필수
* 모든 조회·쓰기에 tenant 범위 포함 — Service 레이어뿐 아니라 **Adapter 레이어도** tenant-scoped 조회 사용
* `softDelete()` 포트 시그니처는 반드시 `tenantId` 포함

**영속성**
* `save()` update path: `findByTenantIdAndId()`로 managed entity 로드 후 필드 갱신 → `saveAndFlush()`. 새 엔티티 객체로 merge 금지 (응답에 `createdAt: null` 발생)
* Soft delete unique 제약: `@UniqueConstraint(tenant_id, code, deleted)` — Flyway 도입 시 `WHERE deleted = false` partial index로 교체
* 단순 저장·존재 확인·ID 조회 → Spring Data JPA / 목록·검색·동적 필터·조인 → QueryDSL

**인증**
* `parseToken()`은 `sub`·`tenantId`·`role` 누락 시 `IllegalArgumentException` throw
* Filter는 `JwtException | IllegalArgumentException` catch
* 이후 요청은 `@AuthenticationPrincipal MesPrincipal`로 `userId`·`tenantId` 사용

## Comment Style

* 한 줄 주석만 사용, 한국어로 작성
* 코드만으로 의도가 불명확하거나 숨겨진 제약·워크어라운드가 있을 때만 작성

## Security Rules

* 시크릿·DB 비밀번호·JWT 시크릿 커밋 금지 — 환경변수 사용
* Request DTO에 Bean Validation 필수
* ID로 리소스 접근하는 API는 tenant 범위 쿼리로 격리 (객체 수준 인가는 Phase 3)
* OWASP API Security Top 10 기준 준수

## Testing

* 테스트 우선순위: 도메인 → 서비스 → 레포지토리 통합 → API 통합 → 인가 → 테넌트 격리
* Repository 통합 테스트는 Testcontainers + 실 PostgreSQL 사용
* 핵심 시나리오(성공/실패)만 테스트, 과도한 mock 지양

## Roadmap

| Phase | 내용 | 상태 |
|---|---|---|
| 0 | 플랫폼 기반 (global, item, user, JWT 인증) | ✅ |
| 1 | 마스터 데이터 (warehouse, location) | ⬜ |
| 2 | 재고 흐름 (receiving, issue, balance, 이동 이력) | ⬜ |
| 3 | 인가 (role 기반 접근 제한, refresh token, 테넌트 격리 테스트) | ⚠️ 일부 |
| 4 | 생산 기초 (work order, 실적, lot) | ⬜ |
| 5 | 품질·설비 | ⬜ |
| 6 | 프로덕션 준비 (Flyway, Docker, CI/CD, Actuator, 로그) | ⬜ |

## Common Commands

```powershell
.\gradlew.bat clean build
.\gradlew.bat test
.\gradlew.bat bootRun
```
