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
import net.vz.mongodb.jackson.WriteResult;

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

/**
 * http://wiki.fasterxml.com/JacksonInFiveMinutes
 * https://github.com/vznet/mongo-jackson-mapper
 */
public class DataServlet extends HttpServlet {



	public static final String CONTEXT = "/data";

	private static final Logger LOG = LoggerFactory.getLogger(DataServlet.class);
	private static final long serialVersionUID = 7860470592232818713L;
	private static final String DBNAME = "takeshi";
	private static final String COLL_CASTLES = "castles";
	private static final String PARAM_ID = "id";
	private static final String CONTENT_TYPE_JSON = "text/json;charset=UTF-8";
	private static final String CONTENT_TYPE_PLAIN = "text/plain;charset=UTF-8";

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
				resp.setContentType(CONTENT_TYPE_JSON);
				this.mapper.writeValue(w, result);
			}
			else {
				ServletHelper.error(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid ID: " + id);
			}
		}
		else {
			resp.setContentType(CONTENT_TYPE_PLAIN);
			List<CastleId> ids = Lists.newArrayList();
			DBCursor<Castle> cursor = this.collCastles.find();
			while (cursor.hasNext()) {
				Castle next = cursor.next();
				ids.add(new CastleId(next));
			}
			resp.setContentType(CONTENT_TYPE_JSON);
			this.mapper.writeValue(w, ids);
		}
	}

	@SuppressWarnings("boxing")
	@Override
	protected void doPost (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String json = ServletHelper.validateStringParam(req, resp, "json");
		if (json == null) return;

		Castle castle = this.mapper.readValue(json, Castle.class);
		if (castle.getId() == null || castle.getId().longValue() < 1L) {
			ServletHelper.error(resp, HttpServletResponse.SC_BAD_REQUEST, "Castle ID is not valid: " + castle.getId());
			return;
		}

		WriteResult<Castle, Long> result = this.collCastles.update(
				new Castle(castle.getId()),
				castle,
				true, false);
		if (result.getN() != 1) {
			ServletHelper.error(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to save castle: " + castle.getId());
			return;
		}
		LOG.info("Saved castle: id={} n={}", castle.getId(), castle.getNodes().size());
	}

}
