package com.vaguehope.takeshi.config;

public final class Modes {

	private static final String MODE_DEBUG = "DEBUG";

	private Modes () {}

	public static boolean isDebug () {
		return Boolean.parseBoolean(System.getenv(MODE_DEBUG));
	}

}
