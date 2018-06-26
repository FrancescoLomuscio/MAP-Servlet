package mining;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import data.Data;
import data.OutOfRangeSampleSize;
import database.DatabaseConnectionException;
import database.EmptySetException;
import database.EmptyTypeException;
import database.NoValueException;

/**
 * Classe di test per la classe KMeansMiner.
 *
 */
public class KMeansMinerTest {
	/**
	 * KMeansMiner di test.
	 */
	private static KMeansMiner kmeans;

	@BeforeAll
	static void setUpAll() {
		try {
			kmeans = new KMeansMiner(5, "playtennis");
			kmeans.salva("KMeansMinerTestData", true);
		} catch (DatabaseConnectionException | SQLException | IOException | OutOfRangeSampleSize e) {
			fail();
		}
	}

	/**
	 * Inizializza prima di ogni test kmeans con un numero di cluster pari a 5.
	 * Questa configurazione verrà usata per i test come caso generico.
	 * 
	 */
	@BeforeEach
	void setUpEach() {
		try {
			kmeans = new KMeansMiner(5, "playtennis");
		} catch (OutOfRangeSampleSize e) {
			fail();
		}
	}

	/**
	 * Test su funzione carica.
	 */
	@Test
	void caricaTest() {
		try {
			kmeans.kmeans(new Data("playtennis", "map.ct3bmfk5atya.us-east-2.rds.amazonaws.com"));
			kmeans.salva("KMeansMinerTestData", true);
			assertAll("Test caricamento", () -> {
				assertEquals(kmeans.getC().toString(), new KMeansMiner("KMeansMinerTestData").getC().toString(),
						"Test caricamento fallito");
			});
		} catch (IOException | OutOfRangeSampleSize | NoValueException | DatabaseConnectionException | SQLException
				| EmptySetException | EmptyTypeException e) {
			fail();
		}
	}

	/**
	 * Test su funzione kmeans corretto.
	 */
	@Test
	void kmeansTest() {
		try {
			assertTrue(kmeans.kmeans(new Data("playtennis", "map.ct3bmfk5atya.us-east-2.rds.amazonaws.com")) > 0,
					"Test kmeans fallito");
		} catch (OutOfRangeSampleSize | NoValueException | DatabaseConnectionException | SQLException
				| EmptySetException | EmptyTypeException e) {
			fail();
		}
	}

	/**
	 * Test su funzione kmeans con input errato.
	 */
	@Test
	void kmeansTestWrong() {
		assertThrows(OutOfRangeSampleSize.class,
				() -> new KMeansMiner(16, "playtennis")
						.kmeans(new Data("playtennis", "map.ct3bmfk5atya.us-east-2.rds.amazonaws.com")),
				"Test kmeans fallito");
	}
}
