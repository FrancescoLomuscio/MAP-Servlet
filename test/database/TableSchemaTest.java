package database;

import database.TableSchema;
import database.TableSchema.Column;
import database.DatabaseConnectionException;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Classe di test per la classe TableSchema.
 *
 */
public class TableSchemaTest {
	/**
	 * TableSchema di test.
	 */
	private static TableSchema tableSchema;
	/**
	 * DBAccess di test.
	 */
	private static DbAccess db;

	/**
	 * Inizializza la connessione al database e assegna a tableSchema lo schema
	 * della tabella playtennis. Questa configurazione verrà usata per i test come
	 * caso generico.
	 */
	@BeforeAll
	static void setUpAll() {
		db = new DbAccess();
		try {
			db.initConnection("map.ct3bmfk5atya.us-east-2.rds.amazonaws.com");
			tableSchema = new TableSchema(db, "playtennis");
		} catch (DatabaseConnectionException | SQLException e) {
			fail();
		}
	}

	/**
	 * Test su funzione getNumberOfAttributes.
	 */
	@Test
	void getNumberOfAttributesTest() {
		assertEquals(tableSchema.getNumberOfAttributes(), 5, "Test numero colonne fallito");
	}

	/**
	 * Test su funzione getColumn.
	 */
	@Test
	void getColumnTest() {
		Column temp = tableSchema.getColumn(0);
		Column temp2 = tableSchema.getColumn(1);
		assertAll("Test lettura colonna", () -> {
			assertTrue(temp.isString(), "Test lettura colonna fallito");
			assertTrue(temp2.isNumber(), "Test lettura colonna fallito");
			assertEquals(temp.getColumnName(), "outlook", "Test lettura colonna fallito");
			assertThrows(IndexOutOfBoundsException.class, () -> tableSchema.getColumn(-1),
					"Test lettura colonna fallito");
			assertThrows(IndexOutOfBoundsException.class, () -> tableSchema.getColumn(20),
					"Test lettura colonna fallito");
		});
	}

	/**
	 * Chiude la connessione al database alla fine dei test.
	 */
	@AfterAll
	static void tearDownAll() {
		db.closeConnection();
	}
}
