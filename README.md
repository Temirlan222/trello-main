# Trello Mini

Учебный REST API на Java 17, Spring Boot, Spring Data JPA и PostgreSQL. Есть доски, колонки и карточки. Smart Focus выбирает до трёх карточек по приоритету и дедлайну.

## Запуск

1. Установите JDK 17 или новее и PostgreSQL.
2. Создайте в PostgreSQL базу данных `Trello`.
3. Скопируйте `src/main/resources/application.example.properties` в `application.properties` в той же папке и укажите свои логин и пароль PostgreSQL.
4. В корне проекта выполните `mvnw.cmd spring-boot:run` или запустите `TrelloMainApplication` в IntelliJ IDEA.

После запуска API доступен на `http://localhost:8080`, Swagger UI — на `http://localhost:8080/swagger-ui.html`.

## Тесты

Запуск на Windows: `mvnw.cmd test`. Тесты `BoardServiceTest` проверяют выбор карточек для Smart Focus и используют Mockito вместо подключения к базе данных.
