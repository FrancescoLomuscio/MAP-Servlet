package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import database.TableSchema.Column;

/**
 * La classe TableData modella l’insieme di transazioni collezionate in una
 * tabella.
 *
 */
public class TableData {

	/**
	 * Il gestore della connessione al database.
	 */
	private DbAccess db;

	/**
	 * Il costruttore della classe.
	 * 
	 * @param db
	 *            Il gestore della connessione al database.
	 */
	public TableData(DbAccess db) {
		this.db = db;
	}

	/**
	 * Restituisce una lista di transazioni, di una tabella specificata, priva di
	 * duplicati.
	 * 
	 * @param table
	 *            Il nome della tabella.
	 * @return lista di transazioni, della tabella table, priva di duplicati.
	 * @throws SQLException
	 *             Se si presenta un errore nella comunicazione con il database.
	 * @throws EmptySetException
	 *             Se il risultato della query è un insieme vuoto.
	 */
	public List<Example> getDistinctTransazioni(String table) throws SQLException, EmptySetException {
		List<Example> transactions = new LinkedList<Example>();
		TableSchema schema = new TableSchema(db, table);
		Statement s = db.getConnection().createStatement();
		ResultSet r = s.executeQuery("SELECT DISTINCT * FROM MapDB." + table);
		if (!r.next())
			throw new EmptySetException();
		do {
			Example row = new Example();
			for (int i = 1; i <= schema.getNumberOfAttributes(); i++) {
				if (schema.getColumn(i - 1).isNumber()) {
					row.add(r.getDouble(i));
				} else {
					row.add(r.getString(i));
				}
			}
			transactions.add(row);
		} while (r.next());
		s.close();
		return transactions;
	}

	/**
	 * Restituisce l'insieme dei valori presenti nella colonna di una tabella
	 * specificata.
	 * 
	 * @param table
	 *            Il nome della tabella.
	 * @param column
	 *            La colonna da cui estrarre i valori.
	 * @return L'insieme dei valori presenti nella colonna di nome column di table.
	 * @throws SQLException
	 *             Se si presenta un errore nella comunicazione con il database.
	 */
	public Set<Object> getDistinctColumnValues(String table, Column column) throws SQLException {
		TreeSet<Object> sortedSet = new TreeSet<Object>();
		Statement s = db.getConnection().createStatement();
		ResultSet r = s.executeQuery("SELECT " + column.getColumnName() + " from MapDB." + table);
		if (column.isNumber()) {
			while (r.next()) {
				sortedSet.add(r.getDouble(1));
			}
		} else {
			while (r.next()) {
				sortedSet.add(r.getString(1));
			}
		}
		s.close();
		return sortedSet;
	}

	/**
	 * Restituisce il risultato di una query di aggregazione sulla colonna della
	 * tabella specificata.
	 * 
	 * @param table
	 *            Il nome della tabella.
	 * @param column
	 *            La colonna da cui estrarre il valore.
	 * @param aggregate
	 *            La tipologia di aggregazione.
	 * @return risultato della query di aggregazione sulla colonna della tabella
	 *         specificata.
	 * @throws SQLException
	 *             Se si presenta un errore nella comunicazione con il database.
	 * @throws NoValueException
	 *             Se vi è l'assenza di un valore all’interno di un resultset.
	 */
	public Object getAggregateColumnValue(String table, Column column, QUERY_TYPE aggregate)
			throws SQLException, NoValueException {
		Statement s = db.getConnection().createStatement();
		ResultSet r = s.executeQuery("SELECT " + aggregate + "(" + column.getColumnName() + ") from MapDB." + table);
		if (!r.next())
			throw new NoValueException();
		Object result = r.getObject(1);
		s.close();
		return result;
	}

}
