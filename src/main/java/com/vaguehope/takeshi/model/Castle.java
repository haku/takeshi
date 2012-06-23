package com.vaguehope.takeshi.model;

import java.util.List;

import com.google.common.base.Objects;

public class Castle {

	private List<CastleNode> nodes;

	public List<CastleNode> getNodes () {
		return this.nodes;
	}

	@Override
	public String toString () {
		return Objects.toStringHelper(this)
				.add("nodes", this.nodes)
				.toString();
	}

}
