package org.zapodot.junit.db;

import com.github.davidmoten.rx.jdbc.Database;
import org.junit.Rule;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

import static org.junit.Assert.*;

public class EmbeddedDatabaseRuleWithBuilderTest {

    @Rule
    public final EmbeddedDatabaseRule embeddedDatabaseRule = EmbeddedDatabaseRule.builder().withMode("ORACLE").withInitialSql(
            "CREATE TABLE Customer(id INTEGER PRIMARY KEY, name VARCHAR(512)); "
            + "INSERT INTO CUSTOMER(id, name) VALUES (1, 'John Doe')").build();

    @Test
    public void testUsingRxJava() throws Exception {
        assertNotNull(embeddedDatabaseRule.getConnection());
        final Database database = Database.from(embeddedDatabaseRule.getConnection());
        assertNotNull(database.select("SELECT sysdate from DUAL").getAs(Date.class).toBlocking().single());

        assertEquals("John Doe", database.select("select name from customer where id=1").getAs(String.class).toBlocking().single());
    }

    @Test
    public void testUsingConnectionUrl() throws Exception {

        try(final Connection connection = DriverManager.getConnection(embeddedDatabaseRule.getConnectionJdbcUrl())) {
            try(final Statement statement = connection.createStatement()) {
                try (final ResultSet resultSet = statement.executeQuery("SELECT * from CUSTOMER")) {
                    assertTrue(resultSet.next());
                }
            }
        }

    }
}