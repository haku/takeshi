package com.vaguehope.takeshi.servlets;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaguehope.takeshi.util.Http;

public class StatusServlet extends CacheingServlet {

	public static final String CONTEXT = "/status";

	private static final Logger LOG = LoggerFactory.getLogger(StatusServlet.class);
	private static final long serialVersionUID = 5567588027933022522L;
	private static final HttpData LOOKFAR_UNAVAILABLE = new HttpData(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Lookfar integration not available.");

	private final URL lookfarUrl;
	private final DefaultHttpClient httpClient;

	public StatusServlet () {
		URL url = safeUrlParse(System.getenv("LOOKFAR_URL"));
		DefaultHttpClient hc = null;
		if (url == null) {
			LOG.warn("Env var LOOKFAR_URL not set or invalid.  Lookfar integration disabled.");
		}
		else {
			String[] userInfo = (url.getUserInfo() == null ? "" : url.getUserInfo()).split(":");
			String username = userInfo.length >= 1 ? userInfo[0] : null;
			String password = userInfo.length >= 2 ? userInfo[1] : null;
			if (username == null || password == null) {
				LOG.warn("Env var LOOKFAR_URL '{}' is missing user info.  Lookfar integration disabled.", url);
				url = null;
			}
			else {
				hc = new DefaultHttpClient();
				hc.getCredentialsProvider().setCredentials(
						new AuthScope(new HttpHost(url.getHost(), url.getPort(), url.getProtocol())),
						new UsernamePasswordCredentials(username, password));
				LOG.info("Lookfar configured: {}", new HttpHost(url.getHost(), url.getPort(), url.getProtocol()).toString());
			}
		}
		this.lookfarUrl = url;
		this.httpClient = hc;
	}

	@Override
	protected HttpData doRealGet () throws ServletException, IOException {
		if (this.lookfarUrl == null) return LOOKFAR_UNAVAILABLE;

		HttpGet get = new HttpGet(this.lookfarUrl + "/update");
		final long statTime = System.currentTimeMillis();
		HttpResponse result = this.httpClient.execute(get);
		StatusLine status = result.getStatusLine();
		if (status.getStatusCode() == HttpServletResponse.SC_OK) {
			LOG.info("Lookfar data fetched in {} millis.", String.valueOf(System.currentTimeMillis() - statTime));
			return new HttpData(Http.CONTENT_TYPE_JSON, EntityUtils.toString(result.getEntity()));
		}

		LOG.warn("Failed to fetch Lookfar data: {} {}", String.valueOf(status.getStatusCode()), status.getReasonPhrase());
		EntityUtils.consumeQuietly(result.getEntity());
		return LOOKFAR_UNAVAILABLE;
	}

	private static URL safeUrlParse (String urlRaw) {
		if (urlRaw == null) return null;
		try {
			return new URL(urlRaw);
		}
		catch (final MalformedURLException e) {
			return null;
		}
	}

}
