package org.zapodot.junit.db;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.zapodot.junit.db.annotations.DataSourceConfig;
import org.zapodot.junit.db.annotations.EmbeddedDatabase;
import org.zapodot.junit.db.common.Engine;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(EmbeddedDatabaseExtension.class)
@DataSourceConfig(
        engine = Engine.HSQLDB,
        initialSqlResources = "classpath:create-tables.sql"
)
class EmbeddedDatabaseExtensionHasSQLResourcesTest {

    @EmbeddedDatabase
    private Connection connection;

    @Test
    void connectionHasBeenInjected() {
        assertNotNull(connection);
    }

    @Test
    void sqlResourceExecuted() throws SQLException {
        try(final Statement statement = connection.createStatement();
            final ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM BRAND"))  {
                assertTrue(resultSet.next());
                assertEquals(0, resultSet.getInt(1));
        }
    }
}