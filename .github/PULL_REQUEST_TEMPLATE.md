## 📌 PR 제목

[도메인] 작업 내용 요약 (ex: [Auth] 로그인 API 리팩토링)

---

## 🛠️ 작업 개요

- 어떤 목적의 작업인지 한두 문장으로 요약
- 기능 추가 / 버그 수정 / 리팩토링 구분

예:

- 기존 로그인 API의 예외 처리를 개선하고, 토큰 만료 로직을 수정했습니다.

---

## ✅ 변경 사항

- 핵심 변경 사항을 항목으로 정리해 주세요

예:

- `AuthService` 내 토큰 발급 로직 수정
- `JwtTokenProvider` 내 `validateToken` 로직 리팩토링
- 로그인 성공 시 사용자 정보를 포함한 응답 반환

---

## 🧪 테스트

- 직접 수행한 테스트나 작성한 테스트 코드에 대해 설명해 주세요

예:

- Postman으로 로그인 API 직접 호출하여 정상 동작 확인
- `AuthServiceTest` 단위 테스트 추가
- 토큰 만료 시 401 반환 확인

---

## ⚠️ API 변경 여부

- [ ] YES
- [ ] NO

> (YES인 경우 변경된 API 명세를 아래에 작성)

```http
POST /api/v1/auth/login
Request: { email, password }
Response: { accessToken, userInfo }