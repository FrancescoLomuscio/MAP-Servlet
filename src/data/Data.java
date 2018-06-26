package data;

import java.sql.SQLException;
import java.util.*;
import database.DatabaseConnectionException;
import database.DbAccess;
import database.EmptySetException;
import database.EmptyTypeException;
import database.Example;
import database.NoValueException;
import database.QUERY_TYPE;
import database.TableData;
import database.TableSchema;

/**
 * La classe Data modella il concetto di insieme di tuple sulle quali calcolare
 * i cluster.
 */
public class Data {
	/**
	 * La lista delle tuple.
	 */
	private List<Example> data = new ArrayList<Example>();
	/**
	 * Il numero delle tuple.
	 */
	private int numberOfExamples;
	/**
	 * La lista degli attributi delle tuple.
	 */
	private List<Attribute> attributeSet = new LinkedList<Attribute>();

	/**
	 * Il costruttore della classe. Costruisce l'oggetto collegandosi al database
	 * relativamente alla tabella col nome specificato.
	 * 
	 * @param table
	 *            Il nome della tabella nel database.
	 * @param server
	 *            L'indirizzo del server.
	 * 
	 * @throws NoValueException
	 * @throws DatabaseConnectionException
	 * @throws SQLException
	 * @throws EmptySetException
	 * @throws EmptyTypeException
	 */
	public Data(String table, String server)
			throws NoValueException, DatabaseConnectionException, SQLException, EmptySetException, EmptyTypeException {
		DbAccess db = new DbAccess();
		try {
			db.initConnection(server);
			TableData td = new TableData(db);
			TableSchema ts = new TableSchema(db, table);
			for (int i = 0; i < ts.getNumberOfAttributes(); i++) {
				TableSchema.Column temp = ts.getColumn(i);
				if (temp.isNumber()) {
					Object tempMax = td.getAggregateColumnValue(table, temp, QUERY_TYPE.MAX);
					Object tempMin = td.getAggregateColumnValue(table, temp, QUERY_TYPE.MIN);
					if (tempMax instanceof Float) {
						double max = (float) tempMax;
						double min = (float) tempMin;
						attributeSet.add(new ContinuousAttribute(temp.getColumnName(), i, max, min));
					} else if (tempMax instanceof Integer) {
						double max = (int) tempMax;
						double min = (int) tempMin;
						attributeSet.add(new ContinuousAttribute(temp.getColumnName(), i, max, min));
					} else if (tempMax instanceof Short) {
						double max = (short) tempMax;
						double min = (short) tempMin;
						attributeSet.add(new ContinuousAttribute(temp.getColumnName(), i, max, min));
					} else if (tempMax instanceof Long) {
						double max = (long) tempMax;
						double min = (long) tempMin;
						attributeSet.add(new ContinuousAttribute(temp.getColumnName(), i, max, min));
					} else if (tempMax instanceof Double) {
						double max = (double) tempMax;
						double min = (double) tempMin;
						attributeSet.add(new ContinuousAttribute(temp.getColumnName(), i, max, min));
					}
				} else if (temp.isString()) {
					TreeSet<String> values = new TreeSet<String>();
					TreeSet<Object> distinctValues = (TreeSet<Object>) td.getDistinctColumnValues(table, temp);
					for (Object j : distinctValues) {
						values.add((String) j);
					}
					attributeSet.add(new DiscreteAttribute(temp.getColumnName(), i, values));
				} else {
					throw new EmptyTypeException();
				}
			}
			data = td.getDistinctTransazioni(table);
			numberOfExamples = data.size();
		} finally {
			db.closeConnection();
		}
	}

	/**
	 * Restituisce il numero delle tuple.
	 * 
	 * @return il numero delle tuple.
	 */
	public int getNumberOfExamples() {
		return numberOfExamples;
	}

	/**
	 * Restituisce il numero degli attributi delle tuple.
	 * 
	 * @return il numero degli attributi delle tuple.
	 */
	public int getNumberOfAttributes() {
		return attributeSet.size();
	}

	/**
	 * Restituisce il valore nella posizione indicizzata da attributeIndex nella
	 * tupla indicizzata da exampleIndex.
	 * 
	 * @param exampleIndex
	 *            L'indice della tupla dove cercare il valore.
	 * @param attributeIndex
	 *            L'indice dell'attributo nella tupla dove cercare il valore.
	 * @return Il valore cercato.
	 */
	public Object getAttributeValue(int exampleIndex, int attributeIndex) {
		return data.get(exampleIndex).get(attributeIndex);
	}

	//!!!
	//NON VIENE MAI UTILIZZATO!!!
	//!!!
	/**
	 * Restituisce L'attributo nell'indice specificato.
	 * 
	 * @param index
	 *            L'indice dell' attributo.
	 * @return L'attributo nell'indice specificato.
	 */
	private Attribute getAttribute(int index) {
		return attributeSet.get(index);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String s = "";
		for(Attribute attribute : attributeSet) {
			s += attribute.getName();
		}
		s += "\n";
		for (int i = 0; i < numberOfExamples; i++) {
			s += (1 + i) + ":";
			for (int j = 0; j < getNumberOfAttributes(); j++) {
				s += data.get(i).get(j);
				if (j != attributeSet.size() - 1)
					s += ",";
			}
			if (i != numberOfExamples - 1)
				s += "\n";
		}
		return s;
	}

	/**
	 * Restituisce la tupla nell'indice specificato.
	 * 
	 * @param index
	 *            L'indice della tupla da restituire.
	 * @return La tupla nell'indice specificato.
	 */
	public Tuple getItemSet(int index) {
		Tuple tuple = new Tuple(attributeSet.size());
		for (Attribute j : attributeSet) {
			Attribute temp = j;
			if (temp instanceof DiscreteAttribute) {
				tuple.add(new DiscreteItem((DiscreteAttribute) temp, (String) data.get(index).get(j.getIndex())),
						j.getIndex());
			} else {
				tuple.add(new ContinuousItem((ContinuousAttribute) temp, (Double) data.get(index).get(j.getIndex())),
						j.getIndex());
			}
		}
		return tuple;
	}

	/**
	 * Sceglie casualmente k centroidi. Restituisce un array di interi contenente
	 * gli indici delle tuple inizialmente scelte come centroidi casuali.
	 * 
	 * @param k
	 *            Il numero di centroidi da scegliere casualmente.
	 * @return L'array di k interi con gli indici delle tuple scelte come centroidi.
	 * @throws OutOfRangeSampleSize
	 *             Se il numero k di centroidi è maggiore del numero di tuple o
	 *             minore di 1.
	 */
	public int[] sampling(int k) throws OutOfRangeSampleSize {
		if (k <= 0 || k > numberOfExamples)
			throw new OutOfRangeSampleSize();
		int centroidIndexes[] = new int[k];
		// choose k random different centroids in data
		Random rand = new Random();
		for (int i = 0; i < k; i++) {
			boolean found = false;
			int c;
			do {
				found = false;
				c = rand.nextInt(getNumberOfExamples());
				/*
				 * verify that centroid[c] is not equal to a centroide already stored in
				 * CentroidIndexes
				 */
				for (int j = 0; j < i; j++)
					if (compare(centroidIndexes[j], c)) {
						found = true;
						break;
					}
			} while (found);
			centroidIndexes[i] = c;
		}
		return centroidIndexes;
	}

	/**
	 * Compara le due tuple specificate dagli indici in input.
	 * 
	 * @param i
	 *            L'indice della prima tupla.
	 * @param j
	 *            L'indice della seconda tupla.
	 * @return True se le due tuple contengono gli stessi valori, false altrimenti.
	 */
	private boolean compare(int i, int j) {
		for (int k = 0; k < attributeSet.size(); k++)
			if (!data.get(i).get(k).equals(data.get(j).get(k)))
				return false;
		return true;
	}

	/**
	 * Restituisce il valore centroide delle tuple riferite da idList rispetto ad
	 * attribute. Chiama computePrototype(Set of Integer, DiscreteAttribute) o
	 * computePrototype(Set of Integer, ContinuousAttribute) in base al tipo reale
	 * di attribute.
	 * 
	 * @param idList
	 *            L'insieme di interi contenente gli indici delle tuple da
	 *            considerare.
	 * @param attribute
	 *            Attributo rispetto al quale calcolare il centroide.
	 * @return Il valore centroide(il valore che compare più frequentemente) nelle
	 *         tuple rispetto ad attribute.
	 */
	Object computePrototype(Set<Integer> idList, Attribute attribute) {
		if (attribute instanceof DiscreteAttribute) {
			return computePrototype(idList, (DiscreteAttribute) attribute);
		} else {
			return computePrototype(idList, (ContinuousAttribute) attribute);
		}
	}

	/**
	 * Restituisce il valore centroide (il valore che compare più frequentemente)
	 * delle tuple riferite da idList rispetto ad attribute.
	 * 
	 * @param idList
	 *            L'insieme di interi contenente gli indici delle tuple da
	 *            considerare.
	 * @param attribute
	 *            Attributo discreto rispetto al quale calcolare il centroide.
	 * @return Il valore centroide nelle tuple rispetto ad attribute.
	 */
	private String computePrototype(Set<Integer> idList, DiscreteAttribute attribute) {
		String max = "";
		int max_freq = 0;
		ArrayList<String> controlled = new ArrayList<String>();
		for (Integer i : idList) {
			String to_control = (String) data.get(i).get(attribute.getIndex());
			boolean checked = false;
			for (String s : controlled) {
				if (s.equals(to_control)) {
					checked = true;
					break;
				}
			}
			if (!checked) {
				controlled.add(to_control);
				int freq = attribute.frequency(this, idList, to_control);
				if (max_freq < freq) {
					max = to_control;
					max_freq = freq;
				}
			}
		}
		return max;
	}

	/**
	 * Restituisce il valore centroide (il valore che compare più
	 * frequentemente)delle tuple riferite da idList rispetto ad attribute.
	 * 
	 * @param idList
	 *            L'insieme di interi contenente gli indici delle tuple da
	 *            considerare.
	 * @param attribute
	 *            Attributo continuo rispetto al quale calcolare il centroide.
	 * @return Il valore centroide nelle tuple rispetto ad attribute.
	 */
	private Double computePrototype(Set<Integer> idList, ContinuousAttribute attribute) {
		Double average = 0.0;
		for (Integer i : idList) {
			average += (Double) data.get(i).get(attribute.getIndex());
		}
		return average / idList.size();
	}
}
