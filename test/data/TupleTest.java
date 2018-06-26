package data;

import data.Tuple;
import database.DatabaseConnectionException;
import database.EmptySetException;
import database.EmptyTypeException;
import database.NoValueException;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.TreeSet;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Classe di test per la classe Tuple.
 *
 */
public class TupleTest {
	/**
	 * Tuple di test.
	 */
	private static Tuple tuple;
	/**
	 * Data di test.
	 */
	private static Data data;

	/**
	 * Inizializza data con la tabella playtennis. Inizializza tuple con la prima
	 * riga di data. Questa configurazione verrà usata per i test come caso
	 * generico.
	 */
	@BeforeAll
	static void setUpAll() {
		try {
			data = new Data("playtennis","map.ct3bmfk5atya.us-east-2.rds.amazonaws.com");
			tuple = data.getItemSet(0);
		} catch (NoValueException | DatabaseConnectionException | SQLException | EmptySetException
				| EmptyTypeException e) {
			fail();
		}
	}

	/**
	 * Test su funzione getLenght.
	 */
	@Test
	void getLengthTest() {
		assertEquals(tuple.getLength(), 5, "Test lunghezza tupla fallito");
	}

	/**
	 * Test su funzione get.
	 */
	@Test
	void getTest() {
		assertEquals(tuple.get(0).toString(), "sunny", "Test lettura elemento tupla fallito");
	}

	/**
	 * Test su funzione getDistance.
	 */
	@Test
	void getDistanceTest() {
		assertAll("Controllo calcolo della distanza fra tuple", () -> {
			assertEquals(tuple.getDistance(tuple), 0, "Test calcolo distanza fallito");
			assertTrue(tuple.getDistance(data.getItemSet(2)) >= 0, "Test calcolo lunghezza fallito");
		});
	}

	/**
	 * Test su funzione avgDistance.
	 */
	@Test
	void avgDistanceTest() {
		TreeSet<Integer> tempSet = new TreeSet<Integer>();
		tempSet.add(1);
		tempSet.add(2);
		tempSet.add(3);
		assertTrue(tuple.avgDistance(data, tempSet) >= 0, "Test calcolo distanza media fallito");
	}
}
