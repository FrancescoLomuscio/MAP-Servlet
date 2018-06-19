package data;

/**
 * La classe DiscreteItem modella il concetto di Item (coppia attributo-valore)
 * con valori discreti rappresentati come Stringhe.
 */
class DiscreteItem extends Item {
	/**
	 * Il costruttore della classe.
	 * 
	 * @param attribute
	 *            L'attributo da rappresentare.
	 * @param value
	 *            Il valore da rappresentare.
	 */
	DiscreteItem(DiscreteAttribute attribute, String value) {
		super(attribute, value);
	}

	/**
	 * @see data.Item#distance(java.lang.Object)
	 */
	double distance(Object a) {
		Item b = (Item) a;
		if (getValue().equals(b.getValue()))
			return 0;
		else
			return 1;
	}
}
