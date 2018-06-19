package data;

/**
 * La classe ContinuousItem modella il concetto di Item (coppia
 * attributo-valore) con valori continui rappresentati come double.
 *
 */
class ContinuousItem extends Item {
	/**
	 * Il costruttore della classe.
	 * 
	 * @param attribute
	 *            L'attributo da rappresentare.
	 * @param value
	 *            Il valore da rappresentare.
	 */
	ContinuousItem(Attribute attribute, Double value) {
		super(attribute, value);
	}

	/**
	 * @see data.Item#distance(java.lang.Object)
	 */
	double distance(Object a) {
		Item b = (Item) a;
		double dist = ((ContinuousAttribute) getAttribute()).getScaledValue((double) this.getValue());
		dist -= ((ContinuousAttribute) b.getAttribute()).getScaledValue((double) b.getValue());
		return Math.abs(dist);
	}
}
