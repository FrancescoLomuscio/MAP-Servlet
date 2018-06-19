package servlet;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.SQLException;

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
		try {
			if ("DB".equals(request.getParameter("command"))) {
				String tabName = request.getParameter("tabName");
				String nCluster = request.getParameter("nCluster");
				String fileName = request.getParameter("fileName");
				try {
					data = new Data(tabName);
					try {
						kmeans = new KMeansMiner(new Integer(nCluster));
						int iterations = kmeans.kmeans(data);
						try {
							String filePath = "C:/Users/PC/Desktop/FILE_SERVER/";
							kmeans.salva(filePath + fileName + ".dat");
							StringBuffer buf = new StringBuffer("Numero iterazioni: ");
							buf.append(iterations).append("\n");
							buf.append(kmeans.getC().toString(data));
							out.writeObject(buf.toString());
						} catch (IOException e) {
							out.writeObject("Errore nell'esecuzione");
						}
					} catch (NumberFormatException | OutOfRangeSampleSize e) {
						out.writeObject("Errore nel numero di cluster");
					}
				} catch (NoValueException | DatabaseConnectionException | SQLException | EmptySetException
						| EmptyTypeException e) {
					out.writeObject("Errore nell'acquisizione della tabella " + e.getClass().getName());
				}
			} else if ("FILE".equals(request.getParameter("command"))) {

			} else {
				out.writeObject("ELSE");
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
