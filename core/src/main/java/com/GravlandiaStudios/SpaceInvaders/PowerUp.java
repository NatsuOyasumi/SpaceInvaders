package com.GravlandiaStudios.SpaceInvaders;

import org.mini2Dx.core.graphics.Graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

public class PowerUp {

	public Position powerPos;
	public int identifier;//1-6
	public int duration = 350;
	public Texture powerUpTexture;
	public boolean pickedUp = false;
	public String name;
	public int textPosition;
	
	/*
	1 speed PowerUp (ship / bullets move faster)
	2 infinite bullets (laser... :D)
	3 unbreaking bullets (can hit multiple aliens)
	4 rebounding - bounce off edge of screen
	5 armor piercing - one-hit KO
	*/
	
	public PowerUp(Position p) {
		powerPos = p;
		powerPos.setColisionBoundary(powerPos.x, powerPos.y, 64, 64);
		identifier = (((int)(Math.random()*3835028))%6)+1;
		pickTexture();
	}
	public PowerUp(Position p, int i) {
		powerPos = p;
		powerPos.setColisionBoundary(powerPos.x, powerPos.y, 64, 64);
		identifier = i;
		pickTexture();
	}
	public PowerUp(Position p, int i, int t) {
		powerPos = p;
		powerPos.setColisionBoundary(powerPos.x, powerPos.y, 64, 64);
		identifier = i;
		textPosition = t;
		pickTexture();
	}
	public PowerUp(Position p, int i, int t, int d) {
		powerPos = p;
		powerPos.setColisionBoundary(powerPos.x, powerPos.y, 64, 64);
		identifier = i;
		textPosition = t;
		duration = d;
		pickTexture();
	}
	
	public void update() {
		if(powerPos.speed != 5) {
			powerPos.speed = 5;
			powerPos.setColisionBoundary(powerPos.x, powerPos.y, 64, 64);
		}
		
		if(pickedUp) {
			duration--;
		}
		else {
			powerPos.moveDown();
		}
		
		if(name.toLowerCase().indexOf("rebound") > -1) {
			//duration = Integer.MAX_VALUE;
		}
	}
	
	public void render(Graphics g) {
		if(!pickedUp) {
			if(powerUpTexture == null) {
				System.out.println("null texture");
				pickTexture();
			}
			if(powerPos == null) {
				System.out.println("position is null");
				powerPos = new Position();
			}
			if(powerPos.x == 0 || powerPos.x == 15) {
				System.out.println("x is " + powerPos.x);
			}
			if(powerPos.y == 0 || powerPos.y == 15) {
				System.out.println("y is " + powerPos.y);
			}
			g.drawTexture(powerUpTexture, powerPos.x, powerPos.y);
			//drawCollisionBoundaries(g);
		}
		else {
			float scale = 3;
			g.scale(scale, scale);
			g.setColor(Color.WHITE);//just in case
			g.drawString(name + " ACTIVE " + duration, ((SpaceInvaders.WINDOW_WIDTH/2)-250)/scale, (SpaceInvaders.WINDOW_HEIGHT-(35*textPosition))/scale);
			g.clearScaling();
		}
	}
	
	public void pickTexture() {
		if(identifier == 1) {
			powerUpTexture = SpaceInvaders.speedPowerUpTexture;
			name = "Speed Power Up";
		}
		else if(identifier == 2) {
			powerUpTexture = SpaceInvaders.infinitebulletPowerUpTexture;
			name = "Infinite Bullet Power Up";
		}
		else if(identifier == 3) {
			powerUpTexture = SpaceInvaders.unbreakingBulletPowerUpTexture;
			name = "Unbreaking Bullet Power Up";
			//duration = 2000;
		}
		else if(identifier == 4) {
			powerUpTexture = SpaceInvaders.reboundingBulletPowerUpTexture;
			name = "Rebounding Bullet Power Up";
			duration = 400;
			//duration = 900;//testing
			//duration = Integer.MAX_VALUE;//Sam
		}
		else if(identifier == 5) {
			powerUpTexture = SpaceInvaders.armorPiercingBulletPowerUpTexture;
			name = "Armor Piercing Power Up";
		}
		else if(identifier == 6) {
			powerUpTexture = SpaceInvaders.indestructiblePowerUp;
			name = "Indestructible Power Up";
			duration = 500;
		}
		else {
			System.out.println("bad PowerUp identifier: " + identifier);
			identifier = (((int)(Math.random()*3835028))%6)+1;
			pickTexture();
		}
	}//pick Texture based on identifier
	
	public void resetDuration() {
		duration = 350;
	}
	
	public void drawCollisionBoundaries(Graphics g) {
		g.drawRect(powerPos.collision_x, powerPos.collision_y, powerPos.collision_width, powerPos.collision_height);
	}
	
}//end class