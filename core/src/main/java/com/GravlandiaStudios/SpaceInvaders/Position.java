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
	
	public float speed = 5;//default
	
	public boolean offScreen = false;//for bullets specifically
	public boolean doNotSetOffScreen = false;
	
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
	public Position(boolean random) {
		//just make a random one, boolean so distinct
		//this is specifically for powerups
		while(x < 15 || x > 1900)
			x = (float)((Math.random()*29472345)%1900);
		while(y < 15 || y > 300)
			y = (float)((Math.random()*29472345)%300);
		width = 64;
		height = 64;
		collision_x = x;
		collision_y = y;
		collision_width = 64;
		collision_height = 64;
	}
	public Position(float in_x, float in_y, float s) {
		x = in_x;
		y = in_y;
	}
	public Position(int in_width, int in_height, float s) {
		width = in_width;
		height = in_height;
	}
	public Position(float in_x, float in_y, int in_width, int in_height, float s) {
		x = in_x;
		y = in_y;
		width = in_width;
		height = in_height;
		speed = s;
	}
	
	public void setColisionBoundary(float in_x, float in_y, int in_width, int in_height) {
		collision_x = in_x;
		collision_y = in_y;
		collision_width = in_width;
		collision_height = in_height;
	}

	public boolean checkOnScreen() {
		//shouldn't be able to go out of screen at all
		if(collision_x > 0 && collision_y > 0 && collision_x+collision_width < SpaceInvaders.WINDOW_WIDTH && collision_y+collision_height < SpaceInvaders.WINDOW_HEIGHT-100) {
			return true;
			//Can't go all the way to bottom of the screen by the way
		}
		else {
			return false;
		}
	}
	
	public void moveLeft() {
		x -= speed;
		collision_x -= speed;
		if(checkOnScreen() == false && doNotSetOffScreen == false) {
			x += speed;
			collision_x += speed;
			offScreen = true;
		}
	}
	public void moveRight() {
		x += speed;
		collision_x += speed;
		if(checkOnScreen() == false && doNotSetOffScreen == false) {
			x -= speed;
			collision_x -= speed;
			offScreen = true;
		}
	}
	public void moveUp() {
		y -= speed;
		collision_y -= speed;
		if(checkOnScreen() == false && doNotSetOffScreen == false) {
			y += speed;
			collision_y += speed;
			offScreen = true;
			if(checkOnScreen() == false && doNotSetOffScreen == false) {
				y += speed;
				collision_y += speed;
				offScreen = true;
			}
		}
	}
	public void moveDown() {
		y += speed;
		collision_y += speed;
		if(checkOnScreen() == false && doNotSetOffScreen == false) {
			y -= speed;
			collision_y -= speed;
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