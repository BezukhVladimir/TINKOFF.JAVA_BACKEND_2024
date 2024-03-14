package edu.java.scrapper;

import java.io.File;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import javax.sql.DataSource;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.DirectoryResourceAccessor;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class IntegrationTest {
    public static final PostgreSQLContainer<?> POSTGRES;
    public static final DataSource dataSource;
    public static final JdbcTemplate jdbcTemplate;

    static {
        POSTGRES = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("scrapper")
            .withUsername("postgres")
            .withPassword("postgres");
        POSTGRES.start();

        dataSource = DataSourceBuilder.create()
            .url(POSTGRES.getJdbcUrl())
            .username(POSTGRES.getUsername())
            .password(POSTGRES.getPassword())
            .build();

        jdbcTemplate = new JdbcTemplate(dataSource);

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
