# Mini Trello

## Цель фронтенда

[Макет и критерии готовности](frontend/design/target-ui.md) · [изображение макета](frontend/design/target-ui.svg)

[Минимальный план фронтенда](frontend/design/frontend-plan.md)

Учебный REST API для управления задачами: доски, колонки и карточки.
Можно создавать задачи, менять приоритет и дедлайн, перемещать карточки между колонками.
Эндпоинт `/api/boards/{id}/focus` возвращает до трёх задач с учётом приоритета и дедлайна.

**Стек:** Java 17, Spring Boot, Spring Data JPA, PostgreSQL, Maven.

## Запуск

1. Установить JDK 17+ и Maven, запустить PostgreSQL и создать базу `Trello`.
2. Скопировать `src/main/resources/application.example.properties` в `src/main/resources/application.properties` и указать свои данные подключения к базе.
3. В корне проекта выполнить:

```bash
mvn spring-boot:run
```

Swagger UI: http://localhost:8080/swagger-ui/index.html
