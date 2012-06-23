package com.vaguehope.takeshi.model;

public class CastleId {

	private final Long id;
	private final String name;

	public CastleId (Long id, String name) {
		this.id = id;
		this.name = name;
	}

	public CastleId (Castle c) {
		this(c.getId(), "(" + c.getNodes().size() + " nodes)"); // FIXME better name.
	}

	public Long getId () {
		return this.id;
	}

	public String getName () {
		return this.name;
	}

}
