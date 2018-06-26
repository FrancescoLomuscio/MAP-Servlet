package servlet;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import data.Data;
import data.OutOfRangeSampleSize;
import database.DatabaseConnectionException;
import database.DbAccess;
import database.EmptySetException;
import database.EmptyTypeException;
import database.NoValueException;
import database.TableData;
import database.TableSchema;
import mining.KMeansMiner;

/**
 * La classe Servlet implementa i metodi di HttpServlet per la comunicazione con
 * i client.
 */
@WebServlet("/Servlet")
class Servlet extends HttpServlet {
	/**
	 * La stringa che rappresenta l'indirizzo del database.
	 */
	final private String databaseUrl = "map.ct3bmfk5atya.us-east-2.rds.amazonaws.com";

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		KMeansMiner kmeans;
		Data data;
		ObjectOutputStream out = new ObjectOutputStream(response.getOutputStream());
		try {
			if ("DB".equals(request.getParameter("command"))) {
				String tabName = request.getParameter("tabName");
				String nCluster = request.getParameter("nCluster");
				String saveName = request.getParameter("saveName");
				String[] result = new String[2];
				boolean saveExist;
				try {
					synchronized (this) {
						saveExist = isSaved(saveName);
						data = new Data(tabName, databaseUrl);
					}
					try {
						kmeans = new KMeansMiner(new Integer(nCluster), tabName);
						int iterations = kmeans.kmeans(data);
						try {
							synchronized (this) {
								kmeans.salva(saveName, saveExist);
							}
							StringBuffer buf = new StringBuffer("Numero iterazioni: ");
							buf.append(iterations).append("\n");
							buf.append(kmeans.getC().toString(data));
							if (saveExist)
								result[0] = "Attenzione, il salvataggio verrà sovrascritto!";
							else
								result[0] = "OK";
							result[1] = buf.toString();
							out.writeObject(result);
						} catch (IOException e) {
							result[0] = "Errore nell'esecuzione ";
							out.writeObject(result);
						}
					} catch (NumberFormatException | OutOfRangeSampleSize e) {
						result[0] = "Errore nel numero di cluster";
						out.writeObject(result);
					}
				} catch (NoValueException | DatabaseConnectionException | SQLException | EmptySetException
						| EmptyTypeException e) {
					result[0] = "Errore nella connessione al database";
					out.writeObject(result);
				}
			} else if ("LOAD".equals(request.getParameter("command"))) {
				try {
					synchronized (this) {
						kmeans = new KMeansMiner(request.getParameter("loadName"));
						data = new Data(kmeans.getTabName(), databaseUrl);
					}
					out.writeObject(kmeans.getC().toString(data));
				} catch (NoValueException | DatabaseConnectionException | SQLException | EmptySetException
						| EmptyTypeException e) {
					out.writeObject("Errore nell'acquisizione della tabella");
				} catch (ClassNotFoundException e) {
					out.writeObject("Errore nell'esecuzione");
				}
			} else if ("SAVED".equals(request.getParameter("command"))) {
				synchronized (this) {
					List<String> saves;
					try {
						saves = saves();
						out.writeObject(saves);
					} catch (DatabaseConnectionException | SQLException e) {
						e.printStackTrace();
					}
				}
			} else {
				out.writeObject("Errore: impossibile eseguire la richiesta.");
			}
		} finally {
			out.close();
		}
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
		TreeSet<Object> savingNames ;
		try {
		db.initConnection(databaseUrl);
		TableData tableData = new TableData(db);
		TableSchema tableSchema = new TableSchema(db, tableName);
		savingNames = (TreeSet<Object>) tableData.getDistinctColumnValues(tableName,
				tableSchema.getColumn(0));
		}finally {
			db.closeConnection();
		}
		return savingNames.contains(saveName);
	}

	/**
	 * Restituisce la lista dei nomi dei salvataggi presenti sul database.
	 * 
	 * @return La lista dei nomi dei salvataggi presenti sul database.
	 * @throws DatabaseConnectionException
	 * @throws SQLException
	 */
	private List<String> saves() throws DatabaseConnectionException, SQLException {
		List<String> saves;
		DbAccess db = new DbAccess();
		db.initConnection(databaseUrl);
		Connection conn = db.getConnection();
		try {
			Statement s = conn.createStatement();
			ResultSet r = s.executeQuery("select name from MapDB.savings;");
			saves = new ArrayList<String>();
			while (r.next()) {
				saves.add(r.getString(1));
			}
		} finally {
			conn.close();
			db.closeConnection();
		}
		return saves;
	}
}
