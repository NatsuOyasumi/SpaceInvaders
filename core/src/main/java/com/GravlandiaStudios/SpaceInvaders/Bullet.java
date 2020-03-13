package com.GravlandiaStudios.SpaceInvaders;

import org.mini2Dx.core.graphics.Graphics;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

public class Bullet {

	public Texture bulletTexture = SpaceInvaders.bulletTexture;
	public Position bulletPos;
	public boolean onScreen = true;//if not on screen anymore, delete
	public int damage = 7;
	public boolean fromPlayer = false;//otherwise need to invert texture
	public boolean drawBulletTailSetting;
	
	public Bullet() {
		bulletPos = new Position();
		bulletPos.setColisionBoundary(bulletPos.x, bulletPos.y, bulletPos.width, bulletPos.height);
	}
	public Bullet(Position p) {
		bulletPos = p;
		bulletPos.setColisionBoundary(bulletPos.x, bulletPos.y, bulletPos.width, bulletPos.height);
	}
	public Bullet(Position p, int d) {
		bulletPos = p;
		bulletPos.setColisionBoundary(bulletPos.x, bulletPos.y, bulletPos.width, bulletPos.height);
		damage = d;
		//could be alien or player...
	}
	public Bullet(float in_x, float in_y) {
		bulletPos = new Position(in_x, in_y, 15, 30);
		//System.out.println(bulletPos.toString()); System.out.println();
		bulletPos.setColisionBoundary(bulletPos.x, bulletPos.y, bulletPos.width, bulletPos.height);
		//System.out.println(bulletPos.toString()); System.out.println();
		fromPlayer = true;
	}
	
	public void setFromPlayer(boolean b) {
		fromPlayer = b;
	}
	
	public void update(int i) {
		drawBulletTailSetting = SpaceInvaders.BulletTailSetting;
		
		//1 is from player, 2 is from alien
		if(i == 1) {
			fromPlayer = true;//don't have this fully implemented yet...
			bulletPos.moveUp();
		}
		else if(i == 2) {
			fromPlayer = false;
			bulletPos.moveDown();
		}
		else {
			System.out.println("Your bullet has exploded...");
		}
	}
	
	public void render(Graphics g) {
		if(SpaceInvaders.bulletTextTexture == false) {
			if(fromPlayer) {
				g.drawTexture(bulletTexture, bulletPos.x, bulletPos.y);
			}
			else {
				//true for flipping vertically does not flip...
				//g.drawTexture(bulletTexture, bulletPos.x+50, bulletPos.y, true);
				g.drawTexture(bulletTexture, bulletPos.x, bulletPos.y, false);
			}
			//drawCollisionSquare(g);
			//drawCoordinateSquare(g);
			if(drawBulletTailSetting) {
				drawBulletTailEffect(g);
			}
		}
		else {
			float scale = 1.2f;
			g.setScale(scale, scale);
			if(fromPlayer) {
				g.drawString("bullet", bulletPos.x/scale, bulletPos.y/scale);
			}
			else {
				//true for flipping vertically does not flip...
				//g.drawTexture(bulletTexture, bulletPos.x+50, bulletPos.y, true);
				g.drawString("bullet", bulletPos.x/scale, bulletPos.y/scale);
			}
			//drawCollisionSquare(g);
			//drawCoordinateSquare(g);
			if(drawBulletTailSetting) {
				drawBulletTailEffect(g);
			}
			g.clearScaling();
		}

	}
	
	public void drawBulletTailEffect(Graphics g) {
		float mod = 105;//how far it was off when the collision boundaries were off
		if(!fromPlayer)
			mod *= -1;
		g.drawRect(bulletPos.x, bulletPos.y+mod, bulletPos.width, bulletPos.height);
	}
	public void drawCollisionSquare(Graphics g) {
		//Collision XY lags behind, which is why collisions isn't working
		g.drawRect(bulletPos.collision_x, bulletPos.collision_y, bulletPos.collision_width, bulletPos.collision_height);
	}
	public void drawCoordinateSquare(Graphics g) {
		//Collision XY lags behind, which is why collisions isn't working
		g.setColor(Color.GREEN);
		g.drawRect(bulletPos.x, bulletPos.y, bulletPos.width, bulletPos.height);
		g.setColor(Color.WHITE);
	}
	
	public String boundaryCheck() {
		//System.out.println(bulletPos.collision_y-bulletPos.y);
		return "" + (bulletPos.toString()) + "\n";
	}
	
}