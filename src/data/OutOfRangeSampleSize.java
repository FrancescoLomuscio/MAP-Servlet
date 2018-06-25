package data;

/**
 * La classe OutOfRangeSampleSize modella l'eccezione: "Calcolo di un numero non
 * valido di cluster".
 *
 */
public class OutOfRangeSampleSize extends Exception {
	/**
	 * Stampa su standard output un messaggio rappresentativo dell'eccezione.
	 */
	public void print() {
		System.out.println("Exception: number of clusters out of range");
	}
}
