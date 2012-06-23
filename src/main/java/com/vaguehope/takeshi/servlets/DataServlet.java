package com.vaguehope.takeshi.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.vaguehope.takeshi.helpers.ServletHelper;
import com.vaguehope.takeshi.model.Castle;

public class DataServlet extends HttpServlet {

	public static final String CONTEXT = "/data";

	private static final Logger LOG = LoggerFactory.getLogger(DataServlet.class);
	private static final long serialVersionUID = 7860470592232818713L;
	private static final String DBNAME = "takeshi";

	private final ObjectMapper mapper = new ObjectMapper();
	private final DB db;

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
		String json = ServletHelper.validateStringParam(req, resp, "json");
		if (json == null) return;

		Castle castle = this.mapper.readValue(json, Castle.class);
		LOG.info("Save request: {}", castle);
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
