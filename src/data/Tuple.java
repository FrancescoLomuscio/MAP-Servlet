package data;

import java.io.Serializable;
import java.util.*;

/**
 * La classe Tuple modella il concetto di tupla come sequenza di coppie
 * attributo-valore.
 */
public class Tuple implements Serializable {
	/**
	 * L'array di Item che rappresentano la tupla.
	 */
	private Item[] tuple;

	/**
	 * Il costruttore della classe. Costruisce una tupla vuota della dimensione
	 * specificata.
	 * 
	 * @param size
	 *            La dimensione della tupla da costruire.
	 */
	Tuple(int size) {
		tuple = new Item[size];
	}

	/**
	 * Restituisce la lunghezza della tupla.
	 * 
	 * @return la lunghezza della tupla.
	 */
	public int getLength() {
		return tuple.length;
	}

	/**
	 * Restituisce l'Item della tupla nella posizione specificata.
	 * 
	 * @param i
	 *            La posizione dell'Item da restituire.
	 * @return L'Item della tupla nella posizione specificata.
	 */
	public Item get(int i) {
		return tuple[i];
	}

	/**
	 * Inserisce l'Item specificato nella posizione specificata della tupla.
	 * 
	 * @param c
	 *            L'Item da inserire.
	 * @param i
	 *            La posizione della tupla in cui inserire l'Item,
	 */
	void add(Item c, int i) {
		tuple[i] = c;
	}

	/**
	 * Restituisce la distanza della tupla corrente dalla tupla in input. La
	 * distanza è ottenuta come la somma delle distanze tra gli Item in posizioni
	 * eguali nelle due tuple.
	 * 
	 * @param obj
	 *            La tupla dalla quale calcolare la distanza.
	 * @return La distanza calcolata.
	 */
	public double getDistance(Tuple obj) {
		double dist = 0.0;
		for (int i = 0; i < tuple.length; i++)
			dist += tuple[i].distance(obj.get(i));
		return dist;
	}

	/**
	 * Restituisce la media delle distanze tra la tupla corrente e quelle ottenibili
	 * dalle righe della matrice in data aventi indice in clusteredData.
	 * 
	 * @param data
	 *            La tabella con le tuple dalle quali calcolare la distanza media.
	 * @param clusteredData
	 *            Il set contenente gli indici delle tuple di data da considerare.
	 * @return La media calcolata.
	 */
	public double avgDistance(Data data, Set<Integer> clusteredData) {
		double p = 0.0, sumDist = 0.0;
		for (Integer i : clusteredData) {
			double d = getDistance(data.getItemSet(i));
			sumDist += d;
		}
		p = sumDist / clusteredData.size();
		return p;
	}
}
