package data;

/**
 * La classe ContinuousAttribute modella il concetto di attributo con valore
 * appartenente a un intervallo continuo.
 *
 */
class ContinuousAttribute extends Attribute {
	/**
	 * Il massimo valore ammissibile nell'intervallo.
	 */
	private double max;
	/**
	 * Il minimo valore ammissibile nell'intervallo.
	 */
	private double min;

	/**
	 * Il costruttore della classe.
	 * 
	 * @param name
	 *            Il nome dell'attributo.
	 * @param index
	 *            L'identificativo numerico dell'attributo.
	 * @param max
	 *            Il massimo valore ammissibile nell'intervallo.
	 * @param min
	 *            Il minimo valore ammissibile nell'intervallo.
	 */
	ContinuousAttribute(String name, int index, double max, double min) {
		super(name, index);
		this.max = max;
		this.min = min;
	}

	/**
	 * Normalizza il valore del parametro in input nell'intervallo [0,1].
	 * 
	 * @param v
	 *            Il valore da normalizzare.
	 * @return Il valore normalizzato.
	 */
	double getScaledValue(double v) {
		return (v - min) / (max - min);
	}
}