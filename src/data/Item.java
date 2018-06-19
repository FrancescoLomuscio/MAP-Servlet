package data;

import java.io.Serializable;
import java.util.*;

/**
 * La classe astratta Item modella una coppia attributo-valore.
 *
 */
public abstract class Item implements Serializable {
	/**
	 * L'attributo da rappresentare.
	 */
	private Attribute attribute;
	/**
	 * Il valore da rappresentare.
	 */
	private Object value;

	/**
	 * Il costruttore della classe.
	 * 
	 * @param attribute
	 *            L'attributo da rappresentare.
	 * @param value
	 *            Il valore da rappresentare.
	 */
	Item(Attribute attribute, Object value) {
		this.attribute = attribute;
		this.value = value;
	}

	/**
	 * Restituisce l'attributo.
	 * 
	 * @return L'attributo.
	 */
	Attribute getAttribute() {
		return attribute;
	}

	/**
	 * Restituisce il valore.
	 * 
	 * @return Il valore.
	 */
	Object getValue() {
		return value;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return value.toString();
	}

	/**
	 * Calcola la distanza tra l'oggetto in input e il valore dell'Item.
	 * 
	 * @param a
	 *            L'oggetto col quale calcolare la distanza.
	 * @return La distanza.
	 */
	abstract double distance(Object a);

	/**
	 * Aggiorna il valore dell'item con il risultato di data.computePrototype.
	 * 
	 * @param data
	 *            La tabella in cui calcolare il valore aggiornato.
	 * @param clusteredData
	 *            L'insieme degli indici di riga del cluster di cui calcolare il
	 *            centroide.
	 */
	public void update(Data data, Set<Integer> clusteredData) {
		value = data.computePrototype(clusteredData, attribute);
	}
}
