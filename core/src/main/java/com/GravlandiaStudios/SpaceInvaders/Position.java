package com.GravlandiaStudios.SpaceInvaders;

public class Position {
	
	public float x;
	public float y;
	public int width;
	public int height;
	
	public float collision_x;
	public float collision_y;
	public int collision_width;
	public int collision_height;
	
	public boolean offScreen = false;//for bullets specifically
	
	public Position() {
		//default is alien
		x = 15;
		y = 15;
		width = 200;
		height = 175;
		collision_x = x+40;
		collision_y = y+40;
		collision_width = 130;
		collision_height = 120;
	}
	public Position(float in_x, float in_y) {
		x = in_x;
		y = in_y;
	}
	public Position(int in_width, int in_height) {
		width = in_width;
		height = in_height;
	}
	public Position(float in_x, float in_y, int in_width, int in_height) {
		x = in_x;
		y = in_y;
		width = in_width;
		height = in_height;
	}
	
	public void setColisionBoundary(float in_x, float in_y, int in_width, int in_height) {
		collision_x = in_x;
		collision_y = in_y;
		collision_width = in_width;
		collision_height = in_height;
	}
	
	public boolean checkOnScreen() {
		//shouldn't be able to go out of screen at all
		if(collision_x > 0 && collision_y > 0 && collision_x+collision_width < SpaceInvaders.WINDOW_WIDTH && collision_y+collision_height < SpaceInvaders.WINDOW_HEIGHT) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public void moveLeft() {
		x -= 5;
		collision_x -= 5;
		if(checkOnScreen() == false) {
			x += 5;
			collision_x += 5;
			offScreen = true;
		}
	}
	public void moveRight() {
		x += 5;
		collision_x += 5;
		if(checkOnScreen() == false) {
			x -= 5;
			collision_x -= 5;
			offScreen = true;
		}
	}
	public void moveUp() {
		y -= 5;
		collision_y -= 5;
		if(checkOnScreen() == false) {
			y += 5;
			collision_y += 5;
			offScreen = true;
		}
	}
	public void moveDown() {
		y += 5;
		collision_y += 5;
		if(checkOnScreen() == false) {
			y -= 5;
			collision_y -= 5;
			offScreen = true;
		}
	}
	
	public String toString() {
		String s = "x: " + x + "  y: " + y + "  cx: " + collision_x + "  cy: " + collision_y;
		s = s.concat("\n");
		s = s.concat("w: " + width + "  h: " + height + "  cw: " + collision_width + "  ch: " + collision_height);
		return s;
	}
	
	
	public boolean overlapping(Position pos) {
		//if overlapping, not just contained fully inside
		boolean b = false;
		if(collision_x <= (pos.collision_x+pos.collision_width) && pos.collision_x <= (collision_x+collision_width)
		&& collision_y <= (pos.collision_y+pos.collision_height) && pos.collision_y <= (collision_y+collision_height)) {
			b = true;
		}
		return b;
	}
}