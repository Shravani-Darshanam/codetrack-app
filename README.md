# CodeTrack: full web app
Track problems you solve on any platform, hit a daily goal of 5, and revise with an auto-generated weekly test.

Stack: Spring Boot 3 (JWT auth, JPA, H2) + React (Vite). The built React app is already in
`src/main/resources/static`, so one command runs everything.

## Run (Java 17 + Maven)
    mvn spring-boot:run
    open http://localhost:8080

## Develop the frontend
    cd frontend && npm install && npm run dev      # http://localhost:5173 (proxies /api to :8080)
    npm run build && cp -r dist/* ../src/main/resources/static/

## Config (src/main/resources/application.properties)
- jwt.secret: change before deploying (32+ chars)
- Database: H2 file at ./data/codetrack. For MySQL add mysql-connector-j and change spring.datasource.*

## API
POST /api/auth/register|login · GET/POST /api/problems · DELETE /api/problems/{id}
GET /api/stats · GET /api/tests/current · POST /api/tests/{id}/submit
Weekly tests are generated every Sunday 8 AM and on demand.
