package com.vaguehope.takeshi.config;

public interface Config {

	// Layout.
	String HOME_PAGE = "/";

	// Server.
	int SERVER_ACCEPTORS = 2;
	int SERVER_MAX_IDLE_TIME_MS = 25000; // 25 seconds in milliseconds.
	int SERVER_SESSION_INACTIVE_TIMEOUT_SECONDS = 60 * 60; // 60 minutes in seconds.
	int SERVER_LOW_RESOURCES_CONNECTIONS = 100;
	int SERVER_LOW_RESOURCES_MAX_IDLE_TIME_MS = 5000; // 5 seconds in milliseconds.

}
