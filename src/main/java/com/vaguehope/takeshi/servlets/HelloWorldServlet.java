package com.vaguehope.takeshi.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HelloWorldServlet extends HttpServlet {

	public static final String CONTEXT = "/hello";

	private static final long serialVersionUID = 854432110734079716L;

	public HelloWorldServlet () {}

	@Override
	protected void doGet (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/plain;charset=UTF-8");
		resp.getWriter().println("Hello World");
	}

}
