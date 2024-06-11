### Задача 1
За каждый сценарий можно получить 5 баллов, но не более 10 баллов в сумме

Расширьте функционал системы таким образом, чтобы помимо факта обновления присылалась информация о том, что изменилось.

Можете выбрать несколько случаев, например:
* появился новый тикет
* добавили новый коммит/ветку
* в вопросе появился новый ответ
* в вопросе появился новый комментарий

Реализуйте один или несколько сценариев, делать все необязательно, также можно придумать свои сценарии.

Не забудьте отразить изменения в чат-боте.

Изменения потребуют небольших правок в контракте, а так же схеме хранения данных.



### Задание 2 (5 баллов)
JDBC достаточно простой API, при помощи которого мы программируем "на строках", т.к. никакого взаимодействия между SQL-запросами (строки) и Java-кодом нет.

Альтернатива SQL существует -- это построители запросов, такие как Criteria API.

Они гибче чем голые строки, но у них тоже есть недостаток -- типобезопасность.

Например, если в таблице create table mytable(id int) мы изменим тип поля id на другой, например, text, то ошибки компиляции в запросе

    jdbcTemplate.query(sql, (rs, rn) -> rs.getInt("id"), ...)

мы не получим. Ошибка произойдёт во время выполнения и будет заключаться, что код ожидает int, когда в БД уже давно String.

Чем больше кодовая база, тем чаще возникают такие проблемы, и любой рефакторинг превращается в боль, а если у вас нет тестов -- в кошмар.

Для решения этой проблемы придумали библиотеку JOOQ.

По схеме БД создаётся Java-код, который создаёт обвязки для генерации SQL.

Возьмем простой пример:

    select id, url, last_check_time, created_by, created_at
    from link
        join subscription on link.id = link_id
    where tg_chat_id = ?

Тогда такой запрос в случае JOOQ будет выглядеть как:

    dslContext.select(LINK.fields())
        .from(LINK)
        .join(SUBSCRIPTION).on(LINK.ID.eq(SUBSCRIPTION.LINK_ID))
        .where(SUBSCRIPTION.TG_CHAT_ID.eq(tgChatId))
        .fetchInto(Link.class);

Получается так, что если какой-нибудь из типов в схеме БД изменится, то код просто не скомпилируется и мы узнаем о проблеме сразу.

Другой плюс библиотеки -- достаточно умные автоматические мапперы (fetchInto).

Минус у библиотеки один -- это кодогенерация, т.е. после правки схемы БД вам нужно перегенерировать вспомогательный код.

Ваша задача написать JOOQ-реализации интерфейсов из прошлого задания, например, JooqLinkService.

Но перед тем как писать сервисы нам нужно сгенерировать вспомогательный код.

Для генерации кода стоит завести отдельный модуль scrapper-jooq.

Вам потребуются зависимости:

    org.springframework.boot:spring-boot-starter-jooq
    org.jooq:jooq-codegen
    org.jooq:jooq-meta-extensions-liquibase
    org.liquibase:liquibase-core

В модуле напишите функцию-генератор при помощи программного API.

Новый код нужно генерировать в модуле scrapper в пакете edu.java.scrapper.domain.jooq.

Если не получится разобраться с документацией -- ниже пример генератора.

    import org.jooq.codegen.GenerationTool;
    import org.jooq.meta.jaxb.Configuration;
    import org.jooq.meta.jaxb.Database;
    import org.jooq.meta.jaxb.Generate;
    import org.jooq.meta.jaxb.Generator;
    import org.jooq.meta.jaxb.Property;
    import org.jooq.meta.jaxb.Target;
        
    public class JooqCodegen {
        public static void main(String[] args) throws Exception {
            Database database = new Database()
                .withName("org.jooq.meta.extensions.liquibase.LiquibaseDatabase")
                .withProperties(
                    new Property().withKey("rootPath").withValue("migrations"),
                    new Property().withKey("scripts").withValue("master.xml")
                );

            Generate options = new Generate()
                .withGeneratedAnnotation(true)
                .withGeneratedAnnotationDate(false)
                .withNullableAnnotation(true)
                .withNullableAnnotationType("org.jetbrains.annotations.Nullable")
                .withNonnullAnnotation(true)
                .withNonnullAnnotationType("org.jetbrains.annotations.NotNull")
                .withJpaAnnotations(false)
                .withValidationAnnotations(true)
                .withSpringAnnotations(true)
                .withConstructorPropertiesAnnotation(true)
                .withConstructorPropertiesAnnotationOnPojos(true)
                .withConstructorPropertiesAnnotationOnRecords(true)
                .withFluentSetters(false)
                .withDaos(false)
                .withPojos(true);
    
            Target target = new Target()
                .withPackageName("ru.tinkoff.edu.java.scrapper.domain.jooq")
                .withDirectory("scrapper/src/main/java");
    
            Configuration configuration = new Configuration()
                .withGenerator(
                    new Generator()
                        .withDatabase(database)
                        .withGenerate(options)
                        .withTarget(target)
                );
    
            GenerationTool.generate(configuration);
        }
    }
