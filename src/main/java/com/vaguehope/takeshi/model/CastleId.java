package com.vaguehope.takeshi.model;

public class CastleId {

	private final String id;
	private final String name;

	public CastleId (String id, String name) {
		this.id = id;
		this.name = name;
	}

	public CastleId (Castle c) {
		this(c.getId(), c.getName());
	}

	public String getId () {
		return this.id;
	}

	public String getName () {
		return this.name;
	}

}
