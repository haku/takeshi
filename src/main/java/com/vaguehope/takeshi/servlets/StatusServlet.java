package com.vaguehope.takeshi.servlets;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
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
import com.vaguehope.takeshi.util.ServletHelper;

public class StatusServlet extends HttpServlet {

	public static final String CONTEXT = "/status";

	private static final Logger LOG = LoggerFactory.getLogger(StatusServlet.class);
	private static final long serialVersionUID = 5567588027933022522L;

	private final URL lookfarUrl;
	private final DefaultHttpClient httpClient;

	public StatusServlet () {
		URL url = safeUrlParse(System.getenv("LOOKFAR_URL"));
		DefaultHttpClient hc = null;
		if (url == null) {
			LOG.info("Env var LOOKFAR_URL not set or invalid.  Lookfar integration disabled.");
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
	protected void doGet (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		if (this.lookfarUrl == null) {
			ServletHelper.error(resp, HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Lookfar integration not available.");
		}
		else {
			// TODO basic auth.
			HttpGet get = new HttpGet(this.lookfarUrl + "/update");
			HttpResponse result = this.httpClient.execute(get);
			StatusLine status = result.getStatusLine();
			if (status.getStatusCode() == HttpServletResponse.SC_OK) {
				resp.setContentType(Http.CONTENT_TYPE_JSON);
				resp.getWriter().write(EntityUtils.toString(result.getEntity()));
			}
			else {
				LOG.warn("Failed to fetch Lookfar status: {} {}", String.valueOf(status.getStatusCode()), status.getReasonPhrase());
				EntityUtils.consumeQuietly(result.getEntity());
				ServletHelper.error(resp, HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Lookfar integration not available.");
			}
		}
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
