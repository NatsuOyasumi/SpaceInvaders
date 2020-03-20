package com.GravlandiaStudios.SpaceInvaders;

import java.util.ArrayList;

import org.mini2Dx.core.graphics.Graphics;

import com.badlogic.gdx.graphics.Texture;

public class Alien {
	
	public Texture alienShip = SpaceInvaders.alienTexture;//200w x 175h
	public Position alienPos;
	public ArrayList<Bullet> alienBullets = new ArrayList<Bullet>();
	public int timeCount = (int)(Math.random()*394820) % 250;
	public int alienHP = 30;//100	
	public int alienDmg = 7;
	
	public Alien() {
		alienPos = new Position();
		alienPos.speed = SpaceInvaders.alienSpeed;
		alienHP = 30;
		alienPos.setColisionBoundary(alienPos.x+40, alienPos.y+40, 130, 120);
	}
	public Alien(Position pos) {
		alienPos = pos;
		alienPos.setColisionBoundary(alienPos.x+40, alienPos.y+40, 130, 120);
	}
	public Alien(float x, float y, float s) {
		alienPos = new Position(x, y, s);
		alienPos.setColisionBoundary(alienPos.x+40, alienPos.y+40, 130, 120);
	}
	public Alien(float x, float y, int w, int h, float s) {
		alienPos = new Position(x, y, w, h, s);
		alienPos.setColisionBoundary(alienPos.x+40, alienPos.y+40, 130, 120);
	}
	
	
	public void update() {
		shoot();
		moveBullets();
		//deleteUsedBullets();//called in moveBullets
	}//end update
	
	public void shoot() {
		if(timeCount > 250) {
			alienBullets.add(new Bullet(alienPos.collision_x+(alienPos.collision_width/2), alienPos.collision_y+(alienPos.collision_height/2), alienPos.speed, alienDmg, false  ));
			alienBullets.get(alienBullets.size()-1).bulletPos.setColisionBoundary(alienPos.collision_x+(alienPos.collision_width/2), alienPos.collision_y+(alienPos.collision_height/2), 15, 30);
			timeCount = 0;
		}
		timeCount++;
	}//end shoot
	public void moveBullets() {
		for(int i = 0; i < alienBullets.size(); i++) {
			alienBullets.get(i).update(2);//2 means from alien
		}
		deleteUsedBullets();
	}

	public ArrayList<Bullet> takeDamage(ArrayList<Bullet> playerBullets) {
		//take damage from player's bullets, return ArrayList so player can delete bullets
		for(int i = 0; i < playerBullets.size(); i++) {
			if(alienPos.overlapping(playerBullets.get(i).bulletPos)) {
				alienHP -= playerBullets.get(i).damage;
				playerBullets.remove(i);
				i--;
			}
		}

		return playerBullets;
	}//take Damage
	
	public void deleteUsedBullets() {
		//Any bullets that are off-screen, delete
		for(int i = 0; i < alienBullets.size(); i++) {
			if(alienBullets.get(i).bulletPos.offScreen == true) {
				alienBullets.remove(i);
				i--;
			}
		}
	}
	
	public void render(Graphics g) {
		g.drawTexture(alienShip, alienPos.x, alienPos.y);
	}
	public void renderBullets(Graphics g) {
		for(Bullet b: alienBullets) {
			b.render(g);
		}
	}
	
	public void drawCollisionSquare(Graphics g) {
		g.drawRect(alienPos.collision_x, alienPos.collision_y, alienPos.collision_width, alienPos.collision_height);
	}
}