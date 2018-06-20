package servlet;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import data.Data;
import data.OutOfRangeSampleSize;
import database.DatabaseConnectionException;
import database.EmptySetException;
import database.EmptyTypeException;
import database.NoValueException;
import mining.KMeansMiner;

/**
 * La classe Servlet implementa i metodi di HttpServlet per la comunicazione con
 * i client.
 */
@WebServlet("/Servlet")
public class Servlet extends HttpServlet {

	public Servlet() {
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		KMeansMiner kmeans;
		Data data;
		ObjectOutputStream out = new ObjectOutputStream(response.getOutputStream());
		String filePath = "C:/Users/PC/Desktop/FILE_SERVER/";
		try {
			if ("DB".equals(request.getParameter("command"))) {
				String tabName = request.getParameter("tabName");
				String nCluster = request.getParameter("nCluster");
				String fileName = request.getParameter("fileName");
				String[] result = new String[2];
				boolean fileExist = false;
				synchronized(this){
					File folder = new File(filePath);
					String[] fileInFolder = folder.list();
					String tempFileName = fileName + ".dat";
					for(int i = 0; i < fileInFolder.length; i++) {
						if(fileInFolder[i].equals(tempFileName)) {
							fileExist = true;
							break;
						}
					}
				}
				try {
					data = new Data(tabName, request.getRemoteAddr());
					try {
						kmeans = new KMeansMiner(new Integer(nCluster), tabName);
						int iterations = kmeans.kmeans(data);
						try {
							synchronized (this) {
								kmeans.salva(filePath + fileName + ".dat", tabName);
							}
							StringBuffer buf = new StringBuffer("Numero iterazioni: ");
							buf.append(iterations).append("\n");
							buf.append(kmeans.getC().toString(data));
							if(fileExist)
								result[0] = "Attenzione, il file verrà sovrascritto!";
							else
								result[0] = "OK";
							result[1] = buf.toString();
							out.writeObject(result);
						} catch (IOException e) {
							result[0] = "Errore nell'esecuzione";
							out.writeObject(result);
						}
					} catch (NumberFormatException | OutOfRangeSampleSize e) {
						result[0] = "Errore nel numero di cluster";
						out.writeObject(result);
					}
				} catch (NoValueException | DatabaseConnectionException | SQLException | EmptySetException
						| EmptyTypeException e) {
					result[0] = "Errore nell'acquisizione della tabella";
					out.writeObject(result);
				}
			} else if ("FILE".equals(request.getParameter("command"))) {
				try {
					synchronized (this) {
						kmeans = new KMeansMiner(filePath + request.getParameter("fileName") + ".dat");
					}
					data = new Data(kmeans.getTabName(), request.getRemoteAddr());
					out.writeObject(kmeans.getC().toString(data));
				} catch (NoValueException | DatabaseConnectionException | SQLException | EmptySetException
						| EmptyTypeException e) {
					out.writeObject("Errore nell'acquisizione della tabella");
				} catch (ClassNotFoundException e) {
					out.writeObject("Errore nell'esecuzione");
				}

			} else if ("SAVED".equals(request.getParameter("command"))) {
				synchronized (this) {
					File saved = new File(filePath);
					String[] savedList = saved.list();
					for (int i = 0; i < savedList.length; i++) {
						savedList[i] = savedList[i].substring(0, savedList[i].length() - 4);
					}
					out.writeObject(savedList);
				}
			} else {
				out.writeObject("Errore: impossibile eseguire la richiesta.");
			}
		} finally {
			out.close();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	// protected void doPost(HttpServletRequest request, HttpServletResponse
	// response)
	// throws ServletException, IOException {
	// // TODO Auto-generated method stub
	// doGet(request, response);
	// }

}
