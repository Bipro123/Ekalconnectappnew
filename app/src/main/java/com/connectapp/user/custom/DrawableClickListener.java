package com.connectapp.user.custom;

public interface DrawableClickListener {
	public static enum DrawablePosition {
		TOP, BOTTOM, LEFT, RIGHT
	};


	public void onClick(DrawablePosition target);
}
