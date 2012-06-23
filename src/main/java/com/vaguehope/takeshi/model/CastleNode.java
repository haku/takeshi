package com.vaguehope.takeshi.model;

import com.google.common.base.Objects;

public class CastleNode {

	private String id;
	private Position pos;
	private String label;

	public String getId () {
		return this.id;
	}

	public Position getPos () {
		return this.pos;
	}

	public String getLabel () {
		return this.label;
	}

	@Override
	public String toString () {
		return Objects.toStringHelper(this)
				.add("id", this.id)
				.add("pos", this.pos)
				.add("label", this.label)
				.toString();
	}

	public static class Position {

		private int left;
		private int top;

		public int getLeft () {
			return this.left;
		}

		public int getTop () {
			return this.top;
		}

		@Override
		public String toString () {
			return Objects.toStringHelper(this)
					.add("left", this.left)
					.add("top", this.top)
					.toString();
		}

	}

}
