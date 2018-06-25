package data;

import java.util.*;

/**
 * La classe DiscreteAttribute modella il concetto di attributo con valore
 * appartenente a un intervallo discreto.
 */
class DiscreteAttribute extends Attribute implements Iterable<String> {
	/**
	 * Insieme dei valori ammissibili.
	 */
	private TreeSet<String> values;

	/**
	 * Il costruttore della classe.
	 * 
	 * @param name
	 *            Il nome dell'attributo.
	 * @param index
	 *            L'identificativo numerico dell'attributo.
	 * @param values
	 *            L'insieme dei valori ammissibili.
	 */
	DiscreteAttribute(String name, int index, TreeSet<String> values) {
		super(name, index);
		this.values = values;
	}

	/**
	 * Calcola la frequenza del valore v nella tabella data secondo le righe
	 * indicizzate da idList.
	 * 
	 * @param data
	 *            La tabella in cui calcolare la frequenza.
	 * @param idList
	 *            L'insieme delle righe da considerare.
	 * @param v
	 *            Il valore di cui calcolare la frequenza.
	 * @return La frequenza del valore v.
	 */
	int frequency(Data data, Set<Integer> idList, String v) {
		int num = 0;
		for (Integer i : idList) {
			if (data.getAttributeValue(i, getIndex()).equals(v))
				num++;
		}
		return num;
	}

	/**
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<String> iterator() {
		return values.iterator();
	}
}
