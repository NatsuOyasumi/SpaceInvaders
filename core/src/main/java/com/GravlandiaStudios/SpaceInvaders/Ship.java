package com.GravlandiaStudios.SpaceInvaders;

import java.util.ArrayList;

import org.mini2Dx.core.graphics.Graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

public class Ship {

	public Texture shipTexture;//200w x 175h
	public Position shipPos;
	public ArrayList<Bullet> shipBullets;
	public int score = 0;//no high score
	public int lives = 3;//num of lives
	public int shipHP = 50;//hp until lose a live
	public int bulletTimeLimit = 35;//35;//75 is slow...
	public int bulletCounter = bulletTimeLimit;
	
	public boolean activePowerUp = false;//handle timer, etc.
	public boolean speedPowerUp = false;
	public boolean infiniteBulletPowerUp = false;//no time limit before can shoot again 
	public boolean unbreakingBulletPowerUp = false;//bullets go through enemies, could hit 2+
	public boolean reboundingBulletPowerUp = false;//rebound off walls
	public boolean armorPiercingBulletPowerUp = false;//1-hit KO
	
	public int powerUpTimer = 0;
	public int powerUpEndTime = 150;
	
	public int deathAnimationCounter = 0;
	public boolean died = false;
	
	public int tempCount = 0;
	
	public Ship() {
		shipTexture = new Texture("ship.jpg");
		shipPos = new Position(SpaceInvaders.WINDOW_WIDTH/2, SpaceInvaders.WINDOW_HEIGHT-180, 200, 175);
		shipPos.setColisionBoundary(shipPos.x+50, shipPos.y+20, 95, 150);
		shipBullets = new ArrayList<Bullet>();
	}
	
	public void update() {
		if(SpaceInvaders.bulletTextTexture) {
			bulletTimeLimit = 5;
		}
		
		if(shipHP <= 0) {
			lives--;
			shipHP = 50;
			died = true;
		}
		else if(died == false) { //don't work until officially alive again
			//I forgot - if use else ifs, can only move one direction at a time
			if(Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.LEFT)) {
				shipPos.moveLeft();
			}
			if(Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.RIGHT)) {
				shipPos.moveRight();
			}
			if(Gdx.input.isKeyPressed(Keys.W) || Gdx.input.isKeyPressed(Keys.UP)) {
				shipPos.moveUp();
			}
			if(Gdx.input.isKeyPressed(Keys.S) || Gdx.input.isKeyPressed(Keys.DOWN)) {
				shipPos.moveDown();
			}

			bulletCounter++;
			if((Gdx.input.isKeyPressed(Keys.SPACE) || Gdx.input.isKeyPressed(Keys.ENTER)) && (bulletCounter > bulletTimeLimit || infiniteBulletPowerUp == true) ) {
				//x --> ship (collision) x + halfway over, y-bulletHeight
				System.out.println("Every time I say guh, that means bullet. Bullet bullet bullet. . .");
				shipBullets.add(new Bullet(shipPos.collision_x+(shipPos.collision_width/2), shipPos.collision_y-30));
				//Sets collision boundary in constructor. Calling it twice might be messing it up.
				//shipBullets.get(shipBullets.size()-1).bulletPos.setColisionBoundary(shipPos.collision_x+(shipPos.collision_width/2), shipPos.collision_y+(shipPos.collision_height/2), 15, 30);
				bulletCounter = 0;
			}
		}//if not dead, can check buttons

		if(SpaceInvaders.drawWithBullets == false) {
			moveBullets();
		}
		//should do this even if die...
		
	}//update
	
	public void moveBullets() {
		for(int i = 0; i < shipBullets.size(); i++) {
			shipBullets.get(i).update(1);//1 means from player
		}
		if(shipBullets.size() > 0) {
			//System.out.println(tempCount + " " + shipBullets.get(0).boundaryCheck());
			tempCount++;
		}
		deleteUsedBullets();
	}

	public void render(Graphics g) {
		if(died == true && deathAnimationCounter < 200) {
			g.setColor(Color.WHITE);
			g.scale(3, 3);
			g.drawString("YOU DIED. . .\n" + deathAnimationCounter, ((SpaceInvaders.WINDOW_WIDTH/2)-50)/3, ((SpaceInvaders.WINDOW_HEIGHT/2)-10)/3);
			g.clearScaling();
			deathAnimationCounter++;
		}
		else if(died == true && deathAnimationCounter >= 200) {
			died = false;
			deathAnimationCounter = 0;
		}
		else {
			g.drawTexture(shipTexture, shipPos.x, shipPos.y);
		}
		
		//I want this regardless...
		g.drawString(lives + " lives", 10, SpaceInvaders.WINDOW_HEIGHT-50);
		g.drawString(score + " points", SpaceInvaders.WINDOW_WIDTH-100, SpaceInvaders.WINDOW_HEIGHT-50);
	}
	public void renderBullets(Graphics g) {
		for(Bullet b: shipBullets) {
			b.render(g);
		}
	}
	
	public void deleteUsedBullets(ArrayList<Bullet> list) {
		if(unbreakingBulletPowerUp == false) {
			//Should already be deleted for me..?
			shipBullets = list;
		}//if not powerup
	}//delete used bullets
	
	public void deleteUsedBullets() {
		//delete ones off-screen
		if(reboundingBulletPowerUp == false) {
			for(int i = 0; i < shipBullets.size(); i++) {
				if(shipBullets.get(i).bulletPos.offScreen == true) {
					if(i == 0) {
						tempCount = 0;
					}
					shipBullets.remove(i);
					i--;
				}
			}
		}//if not powerup
	}//delete used bullets
	
}