package data;

/**
 * La classe OutOfRangeSampleSize modella l'eccezione: "calcolo di un numero non
 * valido di cluster".
 *
 */
public class OutOfRangeSampleSize extends Exception {
	/**
	 * Il costruttore della classe: ha implementazione vuota.
	 */
	public OutOfRangeSampleSize() {
	};

	/**
	 * Stampa su standard output un messaggio rappresentativo dell'eccezione.
	 */
	public void print() {
		System.out.println("Exception: number of clusters out of range");
	}
}
