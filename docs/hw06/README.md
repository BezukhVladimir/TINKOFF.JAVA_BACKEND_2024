### Задача 1
Сделайте интеграцию клиентов, которые были написаны в прошлом задании, и получите готовое рабочее приложение.



### Задача 2
Требуется реализовать функционал, аналогичный JDBC -- напишите Jpa*-имплементации для интерфейсов, например, JpaLinkService.

Не забудьте про интеграционные тесты при помощи IntegrationEnvironment.



### Задача 3
У нас появилось 2 (с бонусным -- 3) имплементации интерфейсов.

Сделайте чтобы выбор бэкенда для работы с БД был конфигурируемым.

Добавьте в AplicationConfig новое поле AccessType databaseAccessType, где AccessType:

    public enum AccessType {
        JDBC, JPA,
        JOOQ // если делали
    }

Или другими словами: если app.database-access-type=jdbc, то используется JDBC-имплементация, если app.database-access-type=jpa, то используется JPA-имплементация.



Для этого создайте conditional-конфигурации для каждого типа взаимодействия при помощи @ConditionalOnProperty:

    @Configuration
    @ConditionalOnProperty(prefix = "app", name = "database-access-type", havingValue = "jdbc")
    public class JdbcAccessConfiguration {
        // ... @Bean-методы
    }


Не забудьте удалить любые аннотации связанные с жизненным циклом (@Service/@Component) с базовых интерфейсов и имплементаций, создание имплементаций должно происходить в конфигурационном файле при помощи @Bean:

    @Bean
    public LinkService linkService(
        JpaLinkRepository linkRepository,
        JpaTgChatRepository tgChatRepository,
        JpaSubscriptionRepository subscriptionRepository
    ) {
        return new JpaLinkService(linkRepository, tgChatRepository, subscriptionRepository);
    }
