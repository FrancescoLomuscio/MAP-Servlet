package database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * La classe TableSchema modella lo schema di una tabella nel database
 * relazionale.
 *
 */
public class TableSchema {
	/**
	 * Il gestore della connessione al database.
	 */
	private DbAccess db;

	/**
	 * La classe Column modella una colonna del database.
	 *
	 */
	public class Column {
		/**
		 * Il nome della colonna.
		 */
		private String name;
		/**
		 * Il tipo della colonna.
		 */
		private String type;

		/**
		 * Il costruttore della classe.
		 * 
		 * @param name
		 *            Il nome della colonna.
		 * @param type
		 *            Il tipo della colonna.
		 */
		private Column(String name, String type) {
			this.name = name;
			this.type = type;
		}

		/**
		 * Restituisce il nome della colonna.
		 * 
		 * @return Il nome della colonna.
		 */
		public String getColumnName() {
			return name;
		}

		/**
		 * Stabilisce se il tipo della colonna è un numero.
		 * 
		 * @return True se il tipo della colonna è un numero, false altrimenti.
		 */
		public boolean isNumber() {
			return type.equals("number");
		}

		/**
		 * Stabilisce se il tipo della colonna è una stringa.
		 * 
		 * @return True se il tipo della colonna è una stringa, false altrimenti.
		 */
		public boolean isString() {
			return type.equals("string");
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return name + ":" + type;
		}
	}

	/**
	 * Lo schema della tabella.
	 */
	private List<Column> tableSchema = new ArrayList<Column>();

	/**
	 * Il costruttore della classe.
	 * 
	 * @param db
	 *            Il gestore della connessione al database.
	 * @param tableName
	 *            Il nome della tabella.
	 * @throws SQLException
	 *             Se si presenta un errore nella comunicazione con il database.
	 */
	public TableSchema(DbAccess db, String tableName) throws SQLException {
		this.db = db;
		HashMap<String, String> mapSQL_JAVATypes = new HashMap<String, String>();
		mapSQL_JAVATypes.put("CHAR", "string");
		mapSQL_JAVATypes.put("VARCHAR", "string");
		mapSQL_JAVATypes.put("LONGVARCHAR", "string");
		mapSQL_JAVATypes.put("BIT", "string");
		mapSQL_JAVATypes.put("SHORT", "number");
		mapSQL_JAVATypes.put("INT", "number");
		mapSQL_JAVATypes.put("LONG", "number");
		mapSQL_JAVATypes.put("FLOAT", "number");
		mapSQL_JAVATypes.put("DOUBLE", "number");

		Connection con = db.getConnection();
		DatabaseMetaData meta = con.getMetaData();
		ResultSet res = meta.getColumns(null, null, tableName, null);

		while (res.next()) {
			if (mapSQL_JAVATypes.containsKey(res.getString("TYPE_NAME"))) {
				tableSchema.add(
						new Column(res.getString("COLUMN_NAME"), mapSQL_JAVATypes.get(res.getString("TYPE_NAME"))));
			} else {
				tableSchema.add(new Column("undefined", "undefined"));
			}
		}
		res.close();

	}

	/**
	 * Restituisce il numero di attributi dello schema.
	 * 
	 * @return Il numero di attributi dello schema.
	 */
	public int getNumberOfAttributes() {
		return tableSchema.size();
	}

	/**
	 * Restituisce la colonna con indice specificato.
	 * 
	 * @param index
	 *            L'indice della tabella.
	 * @return La colonna con indice index.
	 */
	public Column getColumn(int index) {
		return tableSchema.get(index);
	}

}
