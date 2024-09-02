# T1_recording_Portal

# Запуск сервиса:
Работайте имено на моей ветке develop_backend.
```
git clone https://github.com/az3l1t/T1_recording_Portal.git
cd T1_recording_Portal
git checkout develop_backend
```

```
docker-compose down
docker-compose up --build
```

Swagger - сервис аутентификации
```
http://localhost:8000/swagger-ui/index.html#/
```

PostgreSQL
```
Используйте либо psql, либо pgADMIN, либо DBEAVER,
чтобы посмотреть на записанные данные.

url: ${DB_URL:jdbc:postgresql://localhost:5432/users}
```

# Сервис слотов (graphql)
Swagger - это спецификация для REST API, поэтому здесь её не будет. Запускайте мой сервис. Переходите на страницу http://localhost:8010/graphiql и играйтесь, господа! Пишу небольшую инструкцию, если вы до этого не сталкивались с graphQL.
### Запросы делятся на два вида: query / mutation.
QUERY запросы - это замена get-запросам. После инициализации функции и входных данных - вы перечисляете данные, которые хотите получить. Вы можете получать столько данных, сколько захотите. Для этого всего существует лишь один 'endpoint'
```
query {
  getAvailableSlotsForDate(date: "2024-08-23") {
    id
    startTime
    endTime
    bookedBy
    employeeName
  }
}
```

MUTATION запросы - альтернатива запросов post/delete/put в REST. Тут вы в функции передаете дату, время и человека, которого хотите записать на этот слот.
#### P.S в localStorage я просил хранить токен и ник. Сюда будем записывать ник пока что.
После того, как я доделаю сервис создания файлов и отправки на mail - добавим в слоты так же имя и фамилию, как и сказано в тз.
```
mutation {
  handleSlotBooking(date: "2025-08-23", time: "14:00", bookedBy: "John Doe")
}
```

### Есть один endpoint REST в этом сервисе - это заполнение данных о работниках посредством отправки csv файла формата:
Для отправки данных нужен авторизационный токен админа.
Аккаунт админа пока что один. Вот его данные:
Логин - admin_l9dkcd
Пароль - admin_ls45jd2
```
John Doe,2024-08-25T12:00
Jane Smith,2024-08-25T13:00
Alice Johnson,2024-08-25T15:00
Bob Brown,2024-08-25T16:00
Guy Man,2024-08-27T12:00
```
http://localhost:8010/api/v1/employees/uploadSchedule
Нужно в body в виде form-data отправить файл формата csv.

# Для фронта:
- При тестировании желательно фронт поднимать 
на nginx на порту 3000, чтобы не было проблем с cors.
- JWT токен и имя пользователя хранить в localStorage первое время. 
- Приходит все в ответе.
- Прилагаю небольшую инструкцию, как я в своем проекте отправлял graphql запросы.
```
                // Запрос на получение букв
                $.ajax({
                    url: 'http://localhost:8020/graphql',
                    method: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify({
                        query: `
                            query {
                                getAllLetters {
                                    character
                                }
                            }
                        `
                    }),
                    success: function(response) {
                        console.log('GraphQL response:', response);

                        if (response.data && response.data.getAllLetters) {
                            const letters = response.data.getAllLetters;
                            displayLetters(letters);
                        } else {
                            console.error('Unexpected response structure:', response);
                            alert('Ошибка получения букв.');
                        }
                    },
                    error: function(error) {
                        console.error('Error fetching letters:', error);
                        alert('Ошибка получения букв.');
                    }
                });

```

# Для тестера:
- Все параметры для запросов написаны ниже в сущностях.
- Все тесты проводить по localhost:8000
- JWT-токен не нужен для доступа к этим эндпоинтам.
- База данных каждый раз сносится, пока мы не на облаке) (P.S если перезапустишь сервис, то данные удалятся). В проде будет именно обновление.

- Лучше тестировать запросы в postman, потому что в graphiql нельзя добавлять токен аунтентификации.