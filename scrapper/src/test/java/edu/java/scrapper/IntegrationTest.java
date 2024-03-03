package edu.java.scrapper;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.DirectoryResourceAccessor;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.io.File;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;

@Testcontainers
public abstract class IntegrationTest {
    public static PostgreSQLContainer<?> POSTGRES;

    static {
        POSTGRES = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("scrapper")
            .withUsername("postgres")
            .withPassword("postgres");
        POSTGRES.start();


        runMigrations(POSTGRES);
    }

    private static void runMigrations(JdbcDatabaseContainer<?> c) {
        try (Connection connection = DriverManager.getConnection(
            c.getJdbcUrl(),
            c.getUsername(),
            c.getPassword())
        ) {
            var database = DatabaseFactory.getInstance()
                .findCorrectDatabaseImplementation(new JdbcConnection(connection));

            Path changelogPath = new File("").toPath()
                .toAbsolutePath()
                .getParent()
                .resolve("migrations");

            var liquibase =
                new Liquibase(
                    "master.xml",
                    new DirectoryResourceAccessor(changelogPath),
                    database
                );

            liquibase.update(new Contexts(), new LabelExpression());

            // TODO
            // Метод liquibase.update указан как Deprecated
            // Информации по актуальному способу запуска нашёл мало
            // https://forum.liquibase.org/t/4-21-1-is-out-deprecates-liquibase-update/8087
            // Код ниже, к сожалению, не работает
            /*
            try {
                new CommandScope("update")
                    .addArgumentValue("changeLogFile", "master.xml")
                    .addArgumentValue("resourceAccessor", new DirectoryResourceAccessor(changelogPath))
                    .addArgumentValue("database", database)
                    .execute();
            } catch (CommandExecutionException e) {
                System.out.println("Error running update: " + e.getMessage());
            }
             */
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @DynamicPropertySource
    static void jdbcProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }
}
