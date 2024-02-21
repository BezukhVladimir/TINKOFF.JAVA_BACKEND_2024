### Задача 1
Для получения информации об обновлениях нам потребуются HTTP-клиенты для GitHub и StackOverflow в модуле scrapper.

Создание (современных) клиентов в Spring Boot возможно 2 способами: WebClient и HttpExchange.

После создания зарегистрируйте клиентов как бины в отдельном файле ClientConfiguration при помощи аннотации @Bean.

Ограничения:

1. Запрещается использовать RestTemplate или готовые SDK для доступа к API.
2. При создании клиента обязательно должна быть возможность указать базовый URL. При этом если он не указывается, то должен использоваться URL по умолчанию.
3. В *Response-классах для хранения времени должен использоваться класс OffsetDateTime.


Подсказка: часто API возвращает слишком много данных. Можно пропустить их объявление в DTO-классе, тогда лишние поля будут проигнорированы.

Например, если бы мне потребовалось создать клиента для сайта Gitlab, который возвращает информацию о пользователе, то его интерфейс мог бы выглядеть следующим образом:

    interface GitlabClient {
        UserResponse fetchUser(String user);
    }

#### Клиент GitHub

Создайте клиент GitHubClient любым удобным для вас способом.

Вам потребуется изучить API-документацию и найти нужный метод для получения информации из репозитория.

#### Клиент StackOverflow

Создайте клиент StackOverflowClient любым удобным для вас способом.

Вам потребуется изучить API-документацию и найти нужный метод для получения информации о вопросе (передаётся номер вопроса).


### Задача 2
Протестируйте клиентов при помощи библиотеки WireMock (зависимость уже добавлена).


### Задача 3
В работе сервисов иногда приходится прибегать к механизму фоновых задач. Почти всегда это какие-то простые сценарии, не требующие сложной логики, например, обновить данные в кэше или проверить состояние чего-либо.

В нашей задаче требуется периодически ходить по ссылкам из БД и проверять не появились ли обновления.

1. Создайте класс LinkUpdaterScheduler с единственным методом update и добавьте логирование-заглушку в тело метода.
2. Включите поддержку @Schedule*-аннотаций при помощи @EnableScheduling.
3. Сделайте метод update запланированным (@Scheduled).
4. Добавьте в ApplicationConfig поле scheduler типа record Scheduler(Duration interval).
5. В application.properties/yaml создайте ключ app.scheduler.interval и задайте ему какое-нибудь значение (5-15с).
6. Сделайте так, чтобы аннотация @Scheduled на методе update инициализировала значение delay из конфигурации.


Подсказка: @Scheduled-аннотация поддерживает SpEL -- возможность обращения к контексту Spring через специальный синтаксис, например:

    @Scheduled(fixedDelayString = "#{@beanName}")