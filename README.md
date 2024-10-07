# Advertisement System

## How to run locally
1. Run docker-compose files from the 'docker' directory
2. Build jar for all services under 'services' directory
3. Run docker-compose.yml in root directory

## Requirements

- User can publish advertisements
- User can see his own advertisements
- User can edit his advertisement
- User can manage his advertisement
- User can leave a request for moderator to publish his advertisement
- User can deactivate his published advertisement
- User can leave a request for moderator to activate his advertisement
- Unauthorized user can see other users' advertisements (if they are not deactivated) but without contact info
- Authorized user can see other users' advertisements (if they are not deactivated) with contact info
- User can search for advertisements

- Moderator can approve or reject advertisement
- Administrator can assign roles to users


```plantuml UseCases
@startuml
actor User
actor Moderator
actor Administrator

rectangle "UseCases" #C0C0C0 {
left to right direction
User --> (Publish Advertisements)
User --> (View Own Advertisements)
User --> (Edit Advertisement)
User --> (Manage Advertisement)
User --> (Request Moderator to Publish Advertisement)
User --> (Deactivate Published Advertisement)
User --> (Request Moderator to Activate Advertisement)
User --> (Search for Advertisements)
User --> (View Other Users' Advertisements) : <<Unauthorized User>>

User --> (View Other Users' Advertisements) : <<Authorized User>>

Moderator --> (Approve Advertisement)
Moderator --> (Reject Advertisement)

Administrator --> (Assign Roles to Users)
}
@enduml
```

```plantuml С4 Container Diagram (without Observability stack)
@startuml
!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Container.puml

skinparam wrapWidth 300
LAYOUT_WITH_LEGEND()
LAYOUT_LANDSCAPE()
title
<b>AdvertisementSystem v2024.10.06</b>
<i>Система объявлений</i>
end title

System_Boundary(sms_service, "SMS service") {
System_Ext(sms_service_api, "SMS Service API")
}

'System_Boundary(observability, "Observability") {
'System_Ext(loki, "Grafana Loki")
'System_Ext(prometheus, "Grafana Prometheus")
'System_Ext(tempo, "Grafana Tempo")
'}

System_Boundary(pay_service, "Pay service") {
System_Ext(pay_service_api, "Pay Service API")
}
System_Boundary(temporal_stack, "Temporal Workflow Engine") {
SystemQueue_Ext(temporal_api, "Temporal API")
}

SystemQueue_Ext(kafka, "Kafka")
SystemDb_Ext(redis, "Redis")

Person(user, "Пользователь")

System_Boundary(advertisement_system, "Система объявлений") {
Container(ads_service, "Сервис объявлений", "Kotlin, SpringBoot", "Просмотр и управление объявлениями")
Container(auth_service, "Сервис авторизации", "Kotlin, SpringBoot", "Авторизация пользователей, валидация токенов")
Container(payment_service, "Сервис платежей", "Kotlin, SpringBoot", "Оплата объявлений")
Container(saga_orchestrator, "Сервис-оркестратор", "Kotlin, SpringBoot", "Управление процессом повышения приоритета объявления")
}

Rel_R(user, ads_service, "Просмотр, управление объявлениями", "HTTP")
Rel_R(user, auth_service, "Авторизация", "HTTP")
Rel_L(auth_service, user, "Токен", "HTTP")

Rel_L(ads_service, kafka, "Сохраняет", "kafka tcp")
Rel_R(kafka, ads_service, "Потребляет", "kafka tcp")

Rel_D(ads_service, saga_orchestrator, "Инициализация процесса", "HTTP")
Rel_L(ads_service, redis, "Кеширование результатов запроса", "TCP")
Rel_R(saga_orchestrator, temporal_api, "Сохраняет", "grpc")
Rel(temporal_api, payment_service, "Потребляет", "grpc")
Rel(payment_service, temporal_api, "Сохраняет", "grpc")
Rel(temporal_api, ads_service, "Потребляет", "grpc")

Lay_D(kafka, pay_service_api)

Rel(ads_service, auth_service, "Валидация токена", "HTTP")

'Rel(ads_service, loki, "Логгирование", "HTTP")
'Rel(ads_service, prometheus, "Метрики", "HTTP")
'Rel(ads_service, tempo, "Трейсинг", "HTTP")
'Rel(auth_service, loki, "Логгирование", "HTTP")
'Rel(auth_service, prometheus, "Метрики", "HTTP")
'Rel(auth_service, tempo, "Трейсинг", "HTTP")
'Rel(payment_service, loki, "Логгирование", "HTTP")
'Rel(payment_service, prometheus, "Метрики", "HTTP")
'Rel(payment_service, tempo, "Трейсинг", "HTTP")

Rel(auth_service, sms_service_api, "Отправка SMS", "HTTP")
Rel_D(payment_service, pay_service_api, "Транзакция", "HTTP")
@enduml
```

```plantuml User Authorization Flow
@startuml
autonumber
Client -> Auth_Service : Request OTP POST /api/v1/otp
Auth_Service --> Client : 'Otp sent' response
Client -> Auth_Service : Verify OTP POST /api/v1/auth/login
Auth_Service --> Client : Response with 'access' and 'refresh' tokens
Client -> Auth_Service : Renew access token POST /api/v1/auth/access-token
Auth_Service --> Client : Updated access token
Client -> Auth_Service : Logout access POST /api/v1/auth/logout
Auth_Service --> Client : Successful logout
@enduml
```

```plantuml Advertisement Lifecycle
@startuml
[*] --> CREATED
CREATED -> CREATED : Edited
CREATED -> ON_MODERATION : Publish
ON_MODERATION --> [*] : Reject
ON_MODERATION -> PUBLISHED : Approve
PUBLISHED -> DEACTIVATED: Deactivate
PUBLISHED -> ON_MODERATION: Edited
DEACTIVATED -> ON_MODERATION: Activate
DEACTIVATED -> CREATED: Edited
@enduml
```

## About
The purpose of this project is to learn different technologies and system design approaches\
Technologies used:
- Kotlin
- Spring Boot 3.x, WebFlux
- Spring Data R2DBC, JOOQ
- MongoDB, PostgreSQL
- Redis for cache
- Kafka for metric's counter
- SAGA-orchestrator with temporal.io to manage distributed transaction for the case: 
'increase advertisement priority by paying for it' 
- WireMock as mock-server for 'faking' external systems like 'pay-service' or 'sms-service'
- JWT-based authentication

## TODOs
- Add more tests
- Add more validation
- add ABAC for advertisement management?
- image's file type and size validation
- update preview image if original photo was deleted by user
- add Postman collection
- should take into account priority when sorting advertisements
- dependencies versions as variables
- GRPC instead of REST between microservices 
- use message broker instead of direct call of saga-orchestrator
- add scheduler to reset ads' priority when it's expired


## API Usage
1. Register internal client in auth-service (so ads-service could make requests to auth-service to validate token):
```
curl --location 'http://localhost:8081/api/v1/internal/auth/registration' \
--header 'X-Internal-Auth: 22]Rb8L7z9yityA_nH6-t-pxVd8#q%' \
--header 'Content-Type: application/json' \
--data '{
    "appName": "ads-client"
}'
```
In response you will get credentials for newly created client:
```
{
    "username": "test-client",
    "password": "9b,W038Ml*"
}
```
Then you will need to specify these credentials as ads-service's env variables in docker-compose.yml:
```
      AUTH_SERVICE_USERNAME: test-client
      AUTH_SERVICE_PASSWORD: 9b,W038Ml*
```
2. Register new user\
2.1 Request OTP (SMS mock server is used)
```
curl --location 'http://localhost:8081/api/v1/otp' \
--header 'Accept-Language: EN' \
--header 'Content-Type: application/json' \
--data '{
    "phone": "55555",
    "otpType": "AUTH"
}'
```
2.2 Verify OTP
```
curl --location 'http://localhost:8081/api/v1/auth/login' \
--header 'Content-Type: application/json' \
--data '{
    "phone": "55555",
    "code": "3959"
}'
```

In response you will get access and refresh tokens. Access token will be needed for subsequent requests
```
{
    "accessToken": "eyJhbGciOiJSUzUxMiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJjMzE1OTRjOS03Y2MyLTQ4MzUtOWUyZC1mZTc2MDdlYTQ4N2MiLCJzdWIiOiJ1c2VyIiwiZXhwIjoxNzI3OTU0MDU4LCJpYXQiOjE3Mjc5NTA0NTgsIm5iZiI6MTcyNzk1MDQ1OH0.CGv5tsdYMhg_drpVb64p2i2NO3ItwhgvKhLuUL2GaYLqideWCRTQzPAr5C9k5StdJCCYWmkYxIeBoZrtacWfvh2uUNdeeuUhsKIHs7hvPFfLHeLtORwdz9SiRoEL0eqtrSDnwFedtCJqlGKMS8q8Ftor-FCXDmG7DtSvX57Harm_kLTydLOPyQYToHZb9bH8d5cjEtoZNJlthAL7qX1yIooU6y4KhBRB2ixs9dLJJxT5vIuTyLSuC65bGSUvHJRP5mW_F5AI_TFgfLItNpG-DRZlI-Fv3SnnuZqbYwcWbR7X2g1MsGiSS11SxHtp46p0NwlcnoBRWzhJICtUmU4lDg",
    "refreshToken": "eyJhbGciOiJSUzUxMiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJjMzE1OTRjOS03Y2MyLTQ4MzUtOWUyZC1mZTc2MDdlYTQ4N2MiLCJzdWIiOiJ1c2VyIiwiZXhwIjoxNzMwNTQyNDU4LCJpYXQiOjE3Mjc5NTA0NTgsIm5iZiI6MTcyNzk1MDQ1OH0.OvA799846nHgBkwCRJIoqL48xPUiML8GErQGH-0U5YAd8E-YjXPHLODMy5jy6TYdjTt1VdwF3atzNQWoydcDftlXXm3fF2tVas-mTuuqoSjbB9raqUSdaz_WoDzRF3nM7k46c67PNS07VVymeS_zljTttiBd03WX8KpSdAfLZA2dexqE03zwtzXz56T_n6kZg5YGNHt5eafaE9VV-BZ03ew_bvJY9pV8Hu4tv96zo6W2bopEcXoH8qkK7nhZxKcL4jUsbuy6w2KSn9M9tBlbMLVX_15HwWFmhFBX5yw1t2_7nE-uZUutqe4QNPrPzqxn2w0O6Zz6atqkCVefvfgOlw"
}
```

3. To be able to manage advertisements (approve/reject) you will need a user with 'MODERATOR' role.\
At this moment 'user management' functionality is not yet implemented so you will need to manually assign your user a role:
```
INSERT INTO advertisement.users_roles (id, user_id, role_id, created_at) 
VALUES('4ce88f48-2817-4815-9397-6e6d697d9496'::uuid, 'c31594c9-7cc2-4835-9e2d-fe7607ea487c'::uuid, 2, '2024-10-03 13:14:18.498');
```

4. To be able to make payments using 'PAY service' mock you will need a user account.\
Right now the only way to assign user an account is to manually execute query in DB:
```
INSERT INTO payment.user_accounts (id, user_id, account_id) 
VALUES('c31594c9-7cc2-4835-9e2d-fe7607ea487c'::uuid, 'c31594c9-7cc2-4835-9e2d-fe7607ea487c', 'c31594c9-7cc2-4835-9e2d-fe7607ea487c');
```