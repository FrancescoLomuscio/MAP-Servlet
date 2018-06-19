package mining;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import data.*;

/**
 * La classe KMeansMiner gestisce l'implementazione dell’algoritmo kmeans.
 *
 */
public class KMeansMiner implements Serializable {
	/**
	 * L'insieme dei cluster.
	 */
	private ClusterSet C;

	/**
	 * Salva su file lo stato dell'oggetto.
	 * 
	 * @param fileName
	 *            Il nome del file in cui salvare l'oggetto.
	 * @throws FileNotFoundException
	 *             Se vi è stato un errore nell'accesso al file.
	 * @throws IOException
	 *             Se vi è stato un errore nella scrittura su file.
	 */
	public void salva(String fileName) throws FileNotFoundException, IOException {
		ObjectOutputStream outStream = new ObjectOutputStream(new FileOutputStream(fileName));
		outStream.writeObject(C);
		outStream.close();
	}

	/**
	 * Carica da file lo stato di un oggetto della classe KMeansMiner.
	 * 
	 * @param fileName
	 *            Il nome del file da cui caricare l'oggetto.
	 * @throws FileNotFoundException
	 *             Se vi è stato un errore nell'accesso al file.
	 * @throws IOException
	 *             Se vi è stato un errore nella lettura da file.
	 * @throws ClassNotFoundException
	 */
	public KMeansMiner(String fileName) throws FileNotFoundException, IOException, ClassNotFoundException {
		ObjectInputStream inStream = new ObjectInputStream(new FileInputStream(fileName));
		C = (ClusterSet) inStream.readObject();
		inStream.close();
	}

	/**
	 * Il costruttore della classe.
	 * 
	 * @param k
	 *            Il numero di cluster da generare.
	 * @throws OutOfRangeSampleSize
	 *             Se il numero di cluster è troppo grande o troppo piccolo.
	 */
	public KMeansMiner(int k) throws OutOfRangeSampleSize {
		C = new ClusterSet(k);
	}

	/**
	 * Restituisce l'insieme dei cluster.
	 * 
	 * @return L'insieme dei cluster.
	 */
	public ClusterSet getC() {
		return C;
	}

	/**
	 * Clusterizza la tabella specificata secondo l'algoritmo di data mining kmeans.
	 * 
	 * @param data
	 *            La tabella da clusterizzare.
	 * @return Il numero di iterazioni.
	 * @throws OutOfRangeSampleSize
	 *             Se il numero di cluster è troppo grande o troppo piccolo.
	 */
	public int kmeans(Data data) throws OutOfRangeSampleSize {
		int numberOfIterations = 0;
		// STEP 1
		C.initializeCentroids(data);
		boolean changedCluster = false;
		do {
			numberOfIterations++;
			// STEP 2
			changedCluster = false;
			for (int i = 0; i < data.getNumberOfExamples(); i++) {
				Cluster nearestCluster = C.nearestCluster(data.getItemSet(i));
				Cluster oldCluster = C.currentCluster(i);
				boolean currentChange = nearestCluster.addData(i);
				if (currentChange)
					changedCluster = true;
				// rimuovo la tupla dal vecchio cluster
				if (currentChange && oldCluster != null)
					// il nodo va rimosso dal suo vecchio cluster
					oldCluster.removeTuple(i);
			}
			// STEP 3
			C.updateCentroids(data);
		} while (changedCluster);
		return numberOfIterations;
	}
}
