### Общая информация
Типовое приложение представляет трёхзвенную архитектуру:
* controller
* service
* domain

Первый слой controller мы уже реализовали в одном из предыдущих заданий. Также мы создали миграцию и научились применять её к БД.

Теперь нужно научиться загружать данные из БД (domain) и применять к ним некоторую логику обработки (service).


### Задача 1
Реализуйте слой domain для работы с БД при помощи JDBC API.

Нужно реализовать 3 операции для каждой из таблиц с сущностями: add, remove, findAll.

Требования:
* dao/repository, если нужно, должны возвращать специфичный DTO, а не Row/RowSet
* нужно использовать JdbcTemplate или JdbcClient (да, голый JDBC, Hibernate будет дальше)
* для транзакций нужно использовать TransactionTemplate или @Transactional

На получившиеся классы следует написать тесты на каждый метод при помощи IntegrationEnvironment из прошлого задания, например:

    @SpringBootTest
    public class JdbcLinkTest extends IntegrationEnvironment {
        @Autowired
        private JdbcLinkDao linkRepository;
        @Autowired
        private JdbcTgChatRepository chatRepository;
        @Autowired
        private JdbcTemplate jdbcTemplate;
    
        @Test
        @Transactional
        @Rollback
        void addTest() {
        }
    
        @Test
        @Transactional
        @Rollback
        void removeTest() {
        }
    }


### Задача 2
Реализуйте сервисы для добавления, удаления и получения данных из таблиц и добавьте соответствующие вызовы в controller'ы.

Убедитесь, что всё работает (добавление, удаление, получение) при помощи вызовов из Swagger UI.

Если возникнет сложность с дизайном интерфейсов, можете взять следующий код за основу:

    public interface LinkService {
        Link add(long tgChatId, URI url);
        Link remove(long tgChatId, URI url);
        Collection<Link> listAll(long tgChatId);
    }
    
    public interface TgChatService {
        void register(long tgChatId);
        void unregister(long tgChatId);
    }
    
    public interface LinkUpdater {
        int update();
    }

Требования:
* Для сервисов, связанных с БД, используйте интерфейсы, а конкретную имплементацию именуемую префиксом Jdbc*
  * Например, вы создали интерфейс interface LinkService { ... }, тогда класс должен быть class JdbcLinkService implements LinkService { ... }
* В интерфейсах не должно быть JDBC-специфичных типов, например, RowSet
* Jdbc*-имплементации классов положите в отдельный подпакет jdbc


### Задача 3
Текущее приложение умеет добавлять, удалять и показывать список ссылок, но ничего не делает для поиска и оповещения.

В одном из предыдущих заданий мы сделали простой планировщик, который раз в N секунд выводит запись в консоль.

Расширьте функционал планировщика:
* в БД ищется список ссылок, которые давно не проверялись
* при помощи GithubClient/StackOverFlowClient проверялись обновления устаревших ссылок
* если обновления есть, то вызывается BotClient и уведомление об обновлении уходит в приложение bot
* нас интересует только факт обновления, а не их характер, т.е. достаточно сказать "есть обновление"

Важно: планировщик должен использовать для работы интерфейсы, т.е. сущности без префикса Jdbc*.


### Задача 4
Метод findAll позволяет загрузить все ссылки из БД, но на самом деле мы могли бы сделать фильтрацию (поиск ссылок, которые давно не проверялись) на стороне БД.

Добавьте метод для поиска ссылок по критерию.

В планировщике измените код таким образом, чтобы использовался новый метод.
