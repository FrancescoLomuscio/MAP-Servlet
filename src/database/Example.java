package database;

import java.util.ArrayList;
import java.util.List;

/**
 * La classe Example modella una transazione letta dalla base di dati.
 *
 */
public class Example implements Comparable<Example> {
	/**
	 * Il contenuto della transazione.
	 */
	private List<Object> example = new ArrayList<Object>();

	/**
	 * Aggiunge un elemento alla transazione.
	 * 
	 * @param o
	 *            L'elemento da aggiungere.
	 */
	void add(Object o) {
		example.add(o);
	}

	/**
	 * Restituisce l'elemento della transazione nella posizione specifcata.
	 * 
	 * @param i
	 *            La posizione dell'elemento da leggere.
	 * @return L'elemento della transazione in posizione i.
	 */
	public Object get(int i) {
		return example.get(i);
	}

	/**
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Example ex) {

		int i = 0;
		for (Object o : ex.example) {
			if (!o.equals(this.example.get(i)))
				return ((Comparable) o).compareTo(example.get(i));
			i++;
		}
		return 0;
	}

	/**
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String str = "";
		for (Object o : example)
			str += o.toString() + " ";
		return str;
	}

}