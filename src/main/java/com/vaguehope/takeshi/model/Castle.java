package com.vaguehope.takeshi.model;

import java.util.List;

import javax.persistence.Id;

import com.google.common.base.Objects;

public class Castle {

	@Id public Long id;
	private List<CastleNode> nodes;

	public Castle () {}

	public Castle (Long id) {
		this.id = id;
	}

	public Long getId () {
		return this.id;
	}

	public List<CastleNode> getNodes () {
		return this.nodes;
	}

	@Override
	public String toString () {
		return Objects.toStringHelper(this)
				.add("id", this.id)
				.add("nodes", this.nodes)
				.toString();
	}

}
