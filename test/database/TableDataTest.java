package database;

import database.TableData;
import database.DatabaseConnectionException;
import database.EmptySetException;

import database.NoValueException;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Classe di test per la classe TableData.
 *
 */
public class TableDataTest {
	/**
	 * TableData di test.
	 */
	private static TableData tableData;
	/**
	 * DBAccess di test.
	 */
	private static DbAccess db;

	/**
	 * Inizializza la connessione al database. Inizializza tableData. Questa
	 * configurazione verrà usata per i test come caso generico.
	 */
	@BeforeAll
	static void setUpAll() {
		db = new DbAccess();
		try {
			db.initConnection("map.ct3bmfk5atya.us-east-2.rds.amazonaws.com");
			tableData = new TableData(db);
		} catch (DatabaseConnectionException e) {
			fail();
		}
	}

	/**
	 * Test su funzione getDistinctTransazioni.
	 */
	@Test
	void getDistinctTransazioniTest() {
		try {
			List<Example> ls = tableData.getDistinctTransazioni("playtennis");
			assertAll("Test transazioni distinte", () -> {
				assertThrows(SQLException.class, () -> tableData.getDistinctTransazioni("false"));
				assertEquals(ls.size(), new TreeSet<Example>(ls).size(), "Test transazioni distinte fallito");
			});
		} catch (SQLException | EmptySetException e) {
			fail();
		}

	}

	/**
	 * Test su funzione getDistinctColumnValues.
	 */
	@Test
	void getDistinctColumnValuesTest() {
		try {
			TableSchema tableSchema = new TableSchema(db, "playtennis");
			Set<Object> set = tableData.getDistinctColumnValues("playtennis", tableSchema.getColumn(0));
			Set<Object> controlSet = new TreeSet<Object>();
			controlSet.add("sunny");
			controlSet.add("overcast");
			controlSet.add("rain");
			assertAll("Test controllo dominio dei valori", () -> {
				assertThrows(SQLException.class,
						() -> tableData.getDistinctColumnValues("false", tableSchema.getColumn(0)),
						"Test dominio dei valori di un attributo con tabella inesistente fallito");
				assertThrows(IndexOutOfBoundsException.class,
						() -> tableData.getDistinctColumnValues("playtennis", tableSchema.getColumn(-1)),
						"Test dominio dei valori di un attributo con indice colonna errato fallito");
				assertThrows(IndexOutOfBoundsException.class,
						() -> tableData.getDistinctColumnValues("playtennis", tableSchema.getColumn(30)),
						"Test dominio dei valori di un attributo con indice colonna errato fallito");
				assertEquals(set, controlSet, "Test dominio dei valori di un attributo fallito");
			});
		} catch (SQLException e) {
			fail();
		}
	}

	/**
	 * Test su funzione getAggregateColumnValues.
	 */
	@Test
	void getAggregateColumnValueTest() {
		try {
			TableSchema tableSchema = new TableSchema(db, "playtennis");
			Object obj = tableData.getAggregateColumnValue("playtennis", tableSchema.getColumn(0),
					database.QUERY_TYPE.MAX);
			Object controlObj = new String("sunny");
			assertAll("Test controllo dominio dei valori", () -> {
				assertThrows(SQLException.class, () -> tableData.getAggregateColumnValue("false",
						tableSchema.getColumn(0), database.QUERY_TYPE.MAX),
						"Test query di aggregazione con tabella inesistente fallito");
				assertThrows(IndexOutOfBoundsException.class,
						() -> tableData.getAggregateColumnValue("playtennis", tableSchema.getColumn(-1),
								database.QUERY_TYPE.MAX),
						"Test query di aggregazione con indice colonna errato fallito");
				assertThrows(IndexOutOfBoundsException.class,
						() -> tableData.getAggregateColumnValue("playtennis", tableSchema.getColumn(30),
								database.QUERY_TYPE.MAX),
						"Test query di aggregazione con indice colonna errato fallito");
				assertEquals(obj, controlObj, "Test query di aggregazione fallito");
			});
		} catch (SQLException | NoValueException e) {
			fail();
		}
	}

	/**
	 * Chiude la connessione al database alla fine dei test.
	 */
	@AfterAll
	static void tearDownAll() {
		db.closeConnection();
	}

}
