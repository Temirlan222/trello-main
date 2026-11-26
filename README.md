# Mini Trello

Канбан-доска для работы с задачами. В интерфейсе можно создавать доски и колонки, добавлять и редактировать карточки, переносить их между колонками и открывать подборку важных задач. Данные хранятся в PostgreSQL.

**Стек** — Angular, TypeScript, Java 17, Spring Boot, Spring Data JPA, PostgreSQL и Maven.

## Подготовка

1. Установите JDK 17+, Maven, Node.js 26 с npm и PostgreSQL. Запустите PostgreSQL и создайте базу `Trello`, как указано в шаблоне. В SQL имя с заглавной буквой пишется в кавычках: `CREATE DATABASE "Trello";`.
2. Скопируйте [`backend/main/resources/application.example.properties`](backend/main/resources/application.example.properties) в `backend/main/resources/application.properties`. Укажите адрес базы, имя пользователя и пароль. Если база называется иначе, измените `spring.datasource.url`.
3. Установите зависимости фронтенда из корня репозитория.

   ```powershell
   cd frontend
   npm.cmd ci
   ```

## Запуск фронтенда и бэкенда

### Через IntelliJ IDEA на Windows

Откройте корень репозитория с `pom.xml` как Maven-проект и выберите JDK. В списке конфигураций запуска выберите **Trello Full Stack** и нажмите **Run**. Эта конфигурация запускает Spring Boot и Angular вместе. PostgreSQL должен быть запущен отдельно.

### Через два терминала

В первом терминале из корня репозитория запустите Spring Boot.

```powershell
mvn spring-boot:run
```

Во втором терминале из корня репозитория запустите Angular. Первый терминал оставьте открытым.

```powershell
cd frontend
npm.cmd start
```

### Куда зайти после запуска

- [Mini Trello](http://localhost:4200/) — интерфейс для работы с досками.
- [Swagger UI](http://localhost:8080/swagger-ui/index.html) — документация и ручная проверка API.

Во время разработки Angular отправляет запросы `/api` на Spring Boot через [настроенный прокси](frontend/proxy.conf.json). Чтобы проверить весь путь, создайте доску и карточку в интерфейсе, затем обновите страницу и убедитесь, что данные сохранились.
