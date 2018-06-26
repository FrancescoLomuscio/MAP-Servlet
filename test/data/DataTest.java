package data;

import data.Data;
import database.DatabaseConnectionException;
import database.EmptySetException;
import database.EmptyTypeException;
import database.NoValueException;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.SQLException;
import java.util.Random;
import java.util.TreeSet;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Classe di test per la classe Data.
 *
 */
@Tag("Data")
public class DataTest {
	/**
	 * Data di test.
	 */
	private static Data data;

	/**
	 * Inizializza data con la tabella playtennis. Questa configurazione verrà usata
	 * per i test come caso generico.
	 */
	@BeforeAll
	static void setUpAll() {
		try {
			data = new Data("playtennis","map.ct3bmfk5atya.us-east-2.rds.amazonaws.com");
		} catch (NoValueException | DatabaseConnectionException | SQLException | EmptySetException
				| EmptyTypeException e) {
			fail();
		}
	}

	/**
	 * Test con tabella inesistente.
	 */
	@Test
	void wrongTableTest() {
		assertThrows(Exception.class, () -> new Data("foo","map.ct3bmfk5atya.us-east-2.rds.amazonaws.com"));
	}

	/**
	 * Test su funzione getNumberOfExamples.
	 */
	@Test
	void getNumberOfExamplesTest() {
		assertEquals(data.getNumberOfExamples(), 14, "Test numero di righe fallito");
	}

	/**
	 * Test su funzione getNumberOfAttributes.
	 */
	@Test
	void getNumberOfAttributesTest() {
		assertEquals(data.getNumberOfAttributes(), 5, "Test numero di colonne fallito");
	}

	/**
	 * Test su funzione getAttributeValue.
	 */
	@Test
	void getAttributeValueTest() {
		assertAll("Controllo sulla lettura di un elemento dalla tabella.", () -> {
			assertEquals(data.getAttributeValue(0, 0), "sunny", "Test lettura elemento fallito");
			assertThrows(IndexOutOfBoundsException.class, () -> data.getAttributeValue(-4, 1000),
					"Test lettura elemento fallito");
		});
	}

	/**
	 * Test su funzione getItemSet.
	 */
	@Test
	void getItemSetTest() {
		assertAll("Controllo sulla lettura di una tupla della tabella.", () -> {
			assertTrue(data.getItemSet(0) instanceof data.Tuple, "Test lettura tupla fallito");
			assertThrows(IndexOutOfBoundsException.class, () -> data.getItemSet(-4), "Test lettura tupla fallito");
			assertThrows(IndexOutOfBoundsException.class, () -> data.getItemSet(40), "Test lettura tupla fallito");
		});
	}

	/**
	 * Test su funzione sampling con input errato.
	 */
	@Test
	void samplingTestWrong() {
		assertAll("Controllo generazione centroidi casuali", () -> {
			assertThrows(OutOfRangeSampleSize.class, () -> data.sampling(-2), "Test generazione centroidi fallito");
			assertThrows(OutOfRangeSampleSize.class, () -> data.sampling(22), "Test generazione centroidi fallito");
		});
	}

	/**
	 * Test su funzione sampling con input corretto.
	 */
	@Test
	void samplingTestRight() {
		boolean result = true;
		Random rand = new Random();
		int k = rand.nextInt(data.getNumberOfExamples());
		try {
			int[] temp = data.sampling(k);
			if (temp.length != k) {
				result = false;
			}
			TreeSet<Integer> tempSet = new TreeSet<Integer>();
			for (int i = 0; i < temp.length; i++) {
				tempSet.add(temp[i]);
			}
			if (tempSet.size() != temp.length)
				result = false;
		} catch (OutOfRangeSampleSize e) {
			fail();
		}
		assertTrue(result, "Test generazione centroidi fallito");
	}
}
