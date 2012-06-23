package com.vaguehope.takeshi.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.DB;
import com.mongodb.Mongo;

public class DataServlet extends HttpServlet {

	public static final String CONTEXT = "/data";

	private static final Logger LOG = LoggerFactory.getLogger(DataServlet.class);
	private static final long serialVersionUID = 7860470592232818713L;
	private static final String DBNAME = "takeshi";

	private DB db;

	public DataServlet (Mongo mongo) {
		this.db = mongo.getDB(DBNAME);
	}

	@Override
	protected void doGet (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html;charset=UTF-8");
		PrintWriter w = resp.getWriter();
		w.println("<h1>Takeshi</h1>");
		printCollection(w, "Collections", this.db.getCollectionNames());
	}

	@Override
	protected void doPost (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String json = req.getParameter("json");
		LOG.info("Save request: {}", json);
	}

	public <T> void printCollection (PrintWriter w, String title, Collection<T> list) {
		w.println("<h3>" + title + "</h3>");
		w.println("<ul>");
		for (T item : list) {
			w.print("<li>");
			w.print(item.toString());
			w.print("</li>");
		}
		w.println("</ul>");
	}

}
