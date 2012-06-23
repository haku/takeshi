package com.vaguehope.takeshi.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.vz.mongodb.jackson.DBCursor;
import net.vz.mongodb.jackson.JacksonDBCollection;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.mongodb.DB;
import com.mongodb.Mongo;
import com.vaguehope.takeshi.helpers.Numbers;
import com.vaguehope.takeshi.helpers.ServletHelper;
import com.vaguehope.takeshi.model.Castle;
import com.vaguehope.takeshi.model.CastleId;

public class DataServlet extends HttpServlet {


	public static final String CONTEXT = "/data";

	private static final Logger LOG = LoggerFactory.getLogger(DataServlet.class);
	private static final long serialVersionUID = 7860470592232818713L;
	private static final String DBNAME = "takeshi";
	private static final String COLL_CASTLES = "castles";
	private static final String PARAM_ID = "id";
	private static final Long SINGLETON_ID = Long.valueOf(123456L); // FIXME support multiple docs.

	private final ObjectMapper mapper = new ObjectMapper();
	private final DB db;
	private final JacksonDBCollection<Castle, Long> collCastles;

	public DataServlet (Mongo mongo) {
		this.db = mongo.getDB(DBNAME);
		this.collCastles = JacksonDBCollection.wrap(this.db.getCollection(COLL_CASTLES), Castle.class, Long.class);
	}

	@Override
	protected void doGet (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PrintWriter w = resp.getWriter();
		String idRaw = req.getParameter(PARAM_ID);
		if (idRaw != null && !idRaw.isEmpty() && Numbers.isNumeric(idRaw)) {
			Long id = Long.valueOf(idRaw);
			Castle result = this.collCastles.findOneById(id);
			if (result != null) {
				resp.setContentType("text/json;charset=UTF-8");
				this.mapper.writeValue(w, result);
			}
			else {
				ServletHelper.error(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid ID: " + id);
			}
		}
		else {
			resp.setContentType("text/plain;charset=UTF-8");
			List<CastleId> ids = Lists.newArrayList();
			DBCursor<Castle> cursor = this.collCastles.find();
			while (cursor.hasNext()) {
				Castle next = cursor.next();
				ids.add(new CastleId(next));
			}
			resp.setContentType("text/json;charset=UTF-8");
			this.mapper.writeValue(w, ids);
		}
	}

	@Override
	protected void doPost (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String json = ServletHelper.validateStringParam(req, resp, "json");
		if (json == null) return;

		Castle castle = this.mapper.readValue(json, Castle.class);
		castle.setId(SINGLETON_ID); //FIXME
		this.collCastles.update(
				new Castle(SINGLETON_ID),
				castle,
				true, false);
		LOG.info("Saved: {} {}", SINGLETON_ID, castle);
	}

}
