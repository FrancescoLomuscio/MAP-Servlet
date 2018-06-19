package data;

import java.io.Serializable;

/**
 * La classe astratta Attribute modella il concetto di attributo di una tabella
 * di un database relazionale.
 *
 */
abstract class Attribute implements Serializable {
	/**
	 * Il nome dell'attributo.
	 */
	private String name;
	/**
	 * L'identificativo numerico dell'attributo.
	 */
	private int index;

	/**
	 * Il costruttore della classe.
	 * 
	 * @param name
	 *            Il nome dell'attributo.
	 * @param index
	 *            Identificativo numerico dell'attributo.
	 */
	 Attribute(String name, int index) {
		this.name = name;
		this.index = index;
	}

	/**
	 * Restituisce il nome dell'attributo.
	 * 
	 * @return il nome dell'attributo.
	 */
	String getName() {
		return this.name;
	}

	/**
	 * Restituisce l'identificativo numerico dell'attributo.
	 * 
	 * @return l'identificativo numerico dell'attributo.
	 */
	int getIndex() {
		return this.index;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return this.name;
	}
}