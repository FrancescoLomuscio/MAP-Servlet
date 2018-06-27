package mining;

import java.io.Serializable;
import data.*;

/**
 * La classe ClusterSet modella un insieme di cluster.
 *
 */
public class ClusterSet implements Serializable {
	/**
	 * L'insieme dei cluster.
	 */
	private Cluster C[];
	/**
	 * Il numero degli elementi dell'insieme di cluster.
	 */
	private int i = 0;

	/**
	 * Il costruttore della classe.
	 * 
	 * @param k
	 *            Il numero di cluster da generare.
	 * @throws OutOfRangeSampleSize
	 *             Se il numero di cluster da generare è troppo grande o troppo
	 *             piccolo.
	 */
	ClusterSet(int k) throws OutOfRangeSampleSize {
		try {
			this.C = new Cluster[k];
		} catch (NegativeArraySizeException e) {
			throw new OutOfRangeSampleSize();
		}
	}

	/**
	 * Aggiunge un cluster all'insieme.
	 * 
	 * @param c
	 *            Il cluster da aggiungere.
	 */
	private void Add(Cluster c) {
		C[i] = c;
		i++;
	}

	/**
	 * Restituisce il cluster nella posizione specificata.
	 * 
	 * @param i
	 *            L'indice in cui si trova il cluster.
	 * @return Il cluster in posizione i.
	 */
	public Cluster get(int i) {
		return C[i];
	}

	/**
	 * Genera i cluster e i relativi centroidi e li inserisce nell'insieme.
	 * 
	 * @param data
	 *            La tabella da cui generare i cluster .
	 * @throws OutOfRangeSampleSize
	 *             Se il numero di cluster da generare è troppo grande o troppo
	 *             piccolo.
	 */
	void initializeCentroids(Data data) throws OutOfRangeSampleSize {
		int centroidIndexes[] = data.sampling(C.length);
		for (int i = 0; i < centroidIndexes.length; i++) {
			Tuple centroidI = data.getItemSet(centroidIndexes[i]);
			Add(new Cluster(centroidI));
		}
	}

	/**
	 * Restituisce il cluster avente centroide con distanza minore rispetto alla
	 * tupla specificata.
	 * 
	 * @param tuple
	 *            La tupla di cui calcolare la distanza dai centroidi.
	 * @return Il cluster avente centroide con distanza minore rispetto alla tupla
	 *         specificata.
	 */
	Cluster nearestCluster(Tuple tuple) {
		Tuple min = C[0].getCentroid();
		Cluster near = C[0];
		double d_min = min.getDistance(tuple);
		double temp = tuple.getDistance(C[0].getCentroid());
		for (int j = 1; j < C.length; j++) {
			temp = tuple.getDistance(C[j].getCentroid());
			if (temp < d_min) {
				min = C[j].getCentroid();
				d_min = temp;
				near = C[j];
			}
		}
		return near;
	}

	/**
	 * Restituisce il cluster contenente la tupla con indice specificato.
	 * 
	 * @param id
	 *            L'indice della riga da cercare.
	 * @return Il cluster contenente la riga specificata, null se la riga non è
	 *         contenuta in alcun cluster.
	 */
	Cluster currentCluster(int id) {
		for (int j = 0; j < C.length; j++) {
			if (C[j].contain(id)) {
				return C[j];
			}
		}
		return null;
	}

	/**
	 * Aggiorna i centroidi.
	 * 
	 * @param data
	 *            La tabella con cui aggiornare i centroidi.
	 */
	void updateCentroids(Data data) {
		for (int j = 0; j < C.length; j++) {
			C[j].computeCentroid(data);
		}
	}

	/**
	 * Restituisce i centroidi dei cluster sotto forma di stringa.
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String str = new String();
		for (int j = 0; j < C.length; j++) {
			str += j + " " + C[j].toString() + "\n";
		}
		return str;
	}

	/**
	 * Restituisce i centroidi dei cluster e tutte le tuple ad essi appartenenti.
	 * sotto forma di stringa.
	 * 
	 * @param data
	 *            La tabella da cui ricavare le tuple.
	 * @return I centroidi dei cluster e tutte le tuple ad essi appartenenti.
	 */
	public String toString(Data data) {
		String str = new String();
		for (int i = 0; i < C.length; i++) {
			if (C[i] != null) {
				str += i + ":" + C[i].toString(data) + "\n";
			}
		}
		return str;
	}
}
