package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * La classe DbAccess gestisce la connessione a un database specificato.
 *
 */
public class DbAccess {
	/**
	 * Il driver per la connessione al database.
	 */
	private String DRIVER_CLASS_NAME = "org.gjt.mm.mysql.Driver";
	/**
	 * La tipologia di DBMS.
	 */
	private final String DBMS = "jdbc:mysql";
	/**
	 * L'indirizzo del server.
	 */
	private String SERVER = "localhost";
	/**
	 * Il nome del database.
	 */
	private final String DATABASE = "MapDB";
	/**
	 * La porta di connessione.
	 */
	private final int PORT = 3306;
	/**
	 * L'identificativo dell'utente.
	 */
	private final String USER_ID = "MapUserRemote";
	/**
	 * La password dell'utente per accedere alle tabelle del database.
	 */
	private final String PASSWORD = "map";
	/**
	 * La connessione al database.
	 */
	private Connection conn;

	/**
	 * Inizializza la connessione al database con indirizzo specificato, localhost
	 * se il parametro è nullo.
	 * 
	 * @param server
	 *            l'indirizzo del database.
	 * @throws DatabaseConnectionException
	 *             Se vi è un errore nella connessione al database.
	 */
	public void initConnection(String server) throws DatabaseConnectionException {
		try {
			Class.forName(DRIVER_CLASS_NAME).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (!"".equals(server) && server != null)
				SERVER = server;
			conn = DriverManager.getConnection(DBMS + "://" + SERVER + ":" + PORT + "/" + DATABASE, USER_ID, PASSWORD);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseConnectionException();
		}
	}

	/**
	 * Restituisce la connessione al database.
	 * 
	 * @return La connessione al database.
	 */
	public Connection getConnection() {
		return conn;
	}

	/**
	 * Chiude la connessione al database.
	 */
	public void closeConnection() {
		try {
			conn.close();
		} catch (RuntimeException | SQLException e) {
			e.printStackTrace();
		}
	}
}
