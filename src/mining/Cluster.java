package mining;

import data.*;

import java.io.Serializable;
import java.util.*;

/**
 * La classe Cluster modella un cluster.
 *
 */
//$
class Cluster implements Serializable {
	/**
	 * Il centroide del cluster.
	 */
	private Tuple centroid;
	/**
	 * L'insieme degli indici delle righe del cluster.
	 */
	private Set<Integer> clusteredData = new HashSet<Integer>();

	/**
	 * Il costruttore della classe.
	 * 
	 * @param centroid
	 *            Il centroide del cluster.
	 */
	Cluster(Tuple centroid) {
		this.centroid = centroid;
	}

	/**
	 * Restituisce la tupla rappresentante il centroide del cluster.
	 * 
	 * @return La tupla rappresentante il centroide del cluster.
	 */
	Tuple getCentroid() {
		return centroid;
	}

	/**
	 * Calcola il centroide del cluster secondo la tabella specificata.
	 * 
	 * @param data
	 *            La tabella con cui calcolare il centroide.
	 */
	void computeCentroid(Data data) {
		for (int i = 0; i < centroid.getLength(); i++) {
			centroid.get(i).update(data, clusteredData);
		}
	}

	/**
	 * Inserisce la riga specificata nell'insieme di indici.
	 * 
	 * @param id
	 *            La riga da inserire.
	 * @return true se la riga è stato inserito, false altrimenti.
	 */
	boolean addData(int id) {
		return clusteredData.add(id);
	}

	/**
	 * Stabilisce se la riga specificata è contenuta nel cluster.
	 * 
	 * @param id
	 *            La riga da cercare.
	 * @return true se la riga è presente, false altrimenti.
	 */
	boolean contain(int id) {
		return clusteredData.contains(id);
	}

	/**
	 * Rimuove dal cluster la tupla specificata.
	 * 
	 * @param id
	 *            La riga da rimuovere.
	 */
	void removeTuple(int id) {
		clusteredData.remove(id);
	}

	/**
	 * Restituisce il centroide del cluster sotto forma di stringa.
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String str = "Centroid=( ";
		for (int i = 0; i < centroid.getLength(); i++)
			str += centroid.get(i) + " ";
		str += ")";
		return str;
	}

	/**
	 * Restituisce il centroide del cluster e tutte le tuple ad esso appartenenti
	 * sotto forma di stringa.
	 * 
	 * @param data
	 *            La tabella da cui ricavare le tuple.
	 * @return Il centroide del cluster e tutte le tuple ad esso appartenenti sotto
	 *         forma di stringa.
	 */
	public String toString(Data data) {
		StringBuffer buf = new StringBuffer("Centroid=(");
		for (int i = 0; i < centroid.getLength(); i++) {
			buf.append(centroid.get(i));
			buf.append(" ");
		}
		buf.append(")\nExamples:\n");
		for (Integer i : clusteredData) {
			buf.append("[");
			for (int j = 0; j < data.getNumberOfAttributes(); j++) {
				buf.append(data.getAttributeValue(i, j));
				buf.append(", ");
			}
			buf.append("] dist=");
			buf.append(getCentroid().getDistance(data.getItemSet(i)));
			buf.append("\n");
		}
		buf.append("\nAvgDistance=");
		buf.append(getCentroid().avgDistance(data, clusteredData));
		return buf.toString();
	}
}
