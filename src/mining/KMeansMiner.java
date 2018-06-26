package mining;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.TreeSet;

import data.*;
import database.DatabaseConnectionException;
import database.DbAccess;
import database.TableData;
import database.TableSchema;

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
	 * Il nome della tabella che si vuole clusterizzare.
	 */
	private String tabName;

	/**
	 * Salva lo stato dell'oggetto su una tabella predefinita del database.
	 * 
	 * @param saveName
	 *            Il nome del salvataggio.
	 * @return true se il salvataggio è stato sovrascritto, false se è stato creato
	 *         un nuovo salvataggio.
	 * @throws DatabaseConnectionException
	 * @throws SQLException
	 * @throws IOException
	 */
	public boolean salva(String saveName) throws DatabaseConnectionException, SQLException, IOException {
		boolean isSaved = isSaved(saveName);
		if (isSaved) {
			DbAccess db = new DbAccess();
			db.initConnection("map.ct3bmfk5atya.us-east-2.rds.amazonaws.com");
			Connection conn = db.getConnection();
			try {
				String statement = "update MapDB.savings set data = ? where name = '" + saveName + "';";
				PreparedStatement ps = conn.prepareStatement(statement);
				ByteArrayOutputStream byteOS = new ByteArrayOutputStream();
				ObjectOutputStream objectOS = new ObjectOutputStream(byteOS);
				try {
					objectOS.writeObject(C);
					objectOS.writeObject(tabName);
					byte[] bytes = byteOS.toByteArray();
					ByteArrayInputStream byteIS = new ByteArrayInputStream(bytes);
					try {
						ps.setBinaryStream(1, byteIS, bytes.length);
						ps.executeUpdate();
					} finally {
						byteIS.close();
					}
				} finally {
					objectOS.close();
					byteOS.close();
				}
			} finally {
				conn.close();
				db.closeConnection();
			}
		} else {
			DbAccess db = new DbAccess();
			db.initConnection("map.ct3bmfk5atya.us-east-2.rds.amazonaws.com");
			Connection conn = db.getConnection();
			try {
				String statement = "insert into savings values (?, ?);";
				PreparedStatement ps = conn.prepareStatement(statement);
				ByteArrayOutputStream byteOS = new ByteArrayOutputStream();
				ObjectOutputStream objectOS = new ObjectOutputStream(byteOS);
				try {
					objectOS.writeObject(C);
					objectOS.writeObject(tabName);
					byte[] bytes = byteOS.toByteArray();
					ByteArrayInputStream byteIS = new ByteArrayInputStream(bytes);
					try {
						ps.setBinaryStream(2, byteIS, bytes.length);
						ps.setString(1, saveName);
						ps.executeUpdate();
					} finally {
						byteIS.close();
					}
				} finally {
					objectOS.close();
					byteOS.close();
				}
			} finally {
				conn.close();
				db.closeConnection();
			}
		}
		return isSaved;
	}

	/**
	 * Carica da database lo stato di un oggetto della classe KMeansMiner.
	 * 
	 * @param saveName
	 *            Il nome del salvataggio da cui caricare l'oggetto.
	 * @throws DatabaseConnectionException
	 * @throws SQLException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public KMeansMiner(String saveName)
			throws DatabaseConnectionException, SQLException, IOException, ClassNotFoundException {
		DbAccess db = new DbAccess();
		db.initConnection("map.ct3bmfk5atya.us-east-2.rds.amazonaws.com");
		Connection conn = db.getConnection();
		try {
			Statement s = conn.createStatement();
			ResultSet r = s.executeQuery("select data from MapDB.savings where name = '" + saveName + "';");
			if (r.next()) {
				byte[] bytes = (byte[]) r.getObject(1);
				ByteArrayInputStream byteIS = new ByteArrayInputStream(bytes);
				ObjectInputStream objectIS = new ObjectInputStream(byteIS);
				try {
					C = (ClusterSet) objectIS.readObject();
					tabName = (String) objectIS.readObject();
				} finally {
					byteIS.close();
					objectIS.close();
				}
			}
		} finally {
			conn.close();
			db.closeConnection();
		}
	}

	/**
	 * Il costruttore della classe.
	 * 
	 * @param k
	 *            Il numero di cluster da generare.
	 * @param tabName
	 *            Il nome della tabella da clusterizzare.
	 * @throws OutOfRangeSampleSize
	 *             Se il numero di cluster è troppo grande o troppo piccolo.
	 */
	public KMeansMiner(int k, String tabName) throws OutOfRangeSampleSize {
		C = new ClusterSet(k);
		this.tabName = tabName;
	}

	/**
	 * @return Il nome della tabella clusterizzata.
	 */
	public String getTabName() {
		return tabName;
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

	/**
	 * Verifica che il nome del salvataggio specificato sia esistente.
	 * 
	 * @param saveName
	 *            Il nome del salvataggio.
	 * @return true se è gia presente un salvataggio con tale nome, false
	 *         altrimenti.
	 * @throws DatabaseConnectionException
	 * @throws SQLException
	 */
	private boolean isSaved(String saveName) throws DatabaseConnectionException, SQLException {
		final String tableName = "savings";
		DbAccess db = new DbAccess();
		TreeSet<Object> savingNames;
		try {
			db.initConnection("map.ct3bmfk5atya.us-east-2.rds.amazonaws.com");
			TableData tableData = new TableData(db);
			TableSchema tableSchema = new TableSchema(db, tableName);
			savingNames = (TreeSet<Object>) tableData.getDistinctColumnValues(tableName, tableSchema.getColumn(0));
		} finally {
			db.closeConnection();
		}
		return savingNames.contains(saveName);
	}
}
