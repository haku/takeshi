package com.vaguehope.takeshi.servlets;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.vaguehope.takeshi.util.Http;

public abstract class CacheingServlet extends HttpServlet {

	private static final int CACHE_TIMEOUT_SECONDS = 30;
	private static final long serialVersionUID = -6625623106216109951L;

	private final Supplier<HttpData> supplier;

	public CacheingServlet () {
		this.supplier = Suppliers.memoizeWithExpiration(new Supplier<HttpData>() {
			@Override
			public HttpData get () {
				try {
					return doRealGet();
				}
				catch (ServletException e) {
					return new HttpData(e);
				}
				catch (IOException e) {
					return new HttpData(e);
				}
			}
		}, CACHE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
	}

	protected abstract HttpData doRealGet () throws ServletException, IOException;

	@Override
	protected final void doGet (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.supplier.get().apply(resp);
	}

	protected static class HttpData {

		private final int code;
		private final String contentType;
		private final String body;

		public HttpData (int code, String contentType, String body) {
			this.code = code;
			this.contentType = contentType;
			this.body = body;
		}

		public HttpData (String contentType, String body) {
			this(HttpServletResponse.SC_OK, contentType, body);
		}

		public HttpData (int code, String body) {
			this(code, Http.CONTENT_TYPE_PLAIN, body);
		}

		public HttpData (Exception e) {
			this(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Http.CONTENT_TYPE_PLAIN, "HTTP internal server error: " + e.getMessage());
		}

		public void apply (HttpServletResponse resp) throws IOException {
			resp.setStatus(this.code);
			resp.setContentType(this.contentType);
			resp.getWriter().write(this.body);
		}

	}

}
