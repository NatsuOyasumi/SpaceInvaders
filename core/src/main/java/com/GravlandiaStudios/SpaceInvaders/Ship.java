package com.GravlandiaStudios.SpaceInvaders;

import java.util.ArrayList;

import org.mini2Dx.core.graphics.Graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

public class Ship {

	public Texture shipTexture;//200w x 175h,  45cw x 130ch
	public Position shipPos;
	public ArrayList<Bullet> shipBullets;
	public int score = 0;//no high score
	public int lives = 3;//num of lives
	public int shipHP = 50;//hp until lose a live
	public int bulletTimeLimit = 35;//35;//75 is slow...
	public int bulletCounter = bulletTimeLimit;
	public int bulletDmg = 7;
	
	public boolean activePowerUp = false;//handle timer, etc.
	public boolean speedPowerUp = false;
	public boolean infiniteBulletPowerUp = false;//no time limit before can shoot again 
	public boolean unbreakingBulletPowerUp = false;//bullets go through enemies, could hit 2+
	public boolean reboundingBulletPowerUp = false;//rebound off walls
	public boolean armorPiercingBulletPowerUp = false;//1-hit KO
	public boolean indestructiblePowerUp = false;//take no damage
	
	/*
	public int powerUpTimer = 0;
	public int powerUpEndTime = 150;
	*/
	
	public int deathAnimationCounter = 200;
	public boolean died = false;
	
	public int tempCount = 0;
	
	public ArrayList<PowerUp> shipPowerUps = new ArrayList<PowerUp>();//I'm ready to cry
	
	//*****If speed powerup, double speed, but only 1/2 it once before setting powerup false
	
	public Ship() {
		shipTexture = new Texture("ship.jpg");
		shipPos = new Position(SpaceInvaders.WINDOW_WIDTH/2, SpaceInvaders.WINDOW_HEIGHT-180, 200, 175, SpaceInvaders.playerSpeed);
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
			if(Gdx.input.isKeyJustPressed(Keys.I)) {
				infiniteBulletPowerUp = !infiniteBulletPowerUp;
			}
			if(Gdx.input.isKeyJustPressed(Keys.R)) {
				armorPiercingBulletPowerUp = !armorPiercingBulletPowerUp;
			}
			if(Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.LEFT)) {
				shipPos.moveLeft();
				if(speedPowerUp) {
					shipPos.moveLeft();
				}
			}
			if(Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.RIGHT)) {
				shipPos.moveRight();
				if(speedPowerUp) {
					shipPos.moveRight();
				}
			}
			if(Gdx.input.isKeyPressed(Keys.W) || Gdx.input.isKeyPressed(Keys.UP)) {
				shipPos.moveUp();
				if(speedPowerUp) {
					shipPos.moveUp();
				}
			}
			if(Gdx.input.isKeyPressed(Keys.S) || Gdx.input.isKeyPressed(Keys.DOWN)) {
				shipPos.moveDown();
				if(speedPowerUp) {
					shipPos.moveDown();
				}
			}

			bulletCounter++;
			if((Gdx.input.isKeyPressed(Keys.SPACE) || Gdx.input.isKeyPressed(Keys.ENTER)) && (bulletCounter > bulletTimeLimit || infiniteBulletPowerUp == true) ) {
				//x --> ship (collision) x + halfway over, y-bulletHeight
				System.out.println("Every time I say guh, that means bullet. Bullet bullet bullet. . .");
				
				if(armorPiercingBulletPowerUp) {
					shipBullets.add(new Bullet(shipPos.collision_x+(shipPos.collision_width/2), shipPos.collision_y-30, shipPos.speed, 10000, true ));
				}
				else {
					shipBullets.add(new Bullet(shipPos.collision_x+(shipPos.collision_width/2), shipPos.collision_y-30, shipPos.speed, bulletDmg, true ));
				}
				//Sets collision boundary in constructor. Calling it twice might be messing it up.
				//shipBullets.get(shipBullets.size()-1).bulletPos.setColisionBoundary(shipPos.collision_x+(shipPos.collision_width/2), shipPos.collision_y+(shipPos.collision_height/2), 15, 30);
				bulletCounter = 0;
			}
		}//if not dead, can check buttons

		if(SpaceInvaders.drawWithBullets == false) {
			moveBullets();
		}
		//should do this even if die...
		
		updatePowerUps();
		
	}//update
	
	public void updatePowerUps() {
		for(int i = 0; i < shipPowerUps.size(); i++) {
			PowerUp p = shipPowerUps.get(i);
			if(p.duration <= 0) {
				deactivatePowerUp(p.identifier);
				shipPowerUps.remove(i);
			}
			else {
				//else b/c when picking up, don't immediately decrease timer, so don't update.
				p.update();
				p.textPosition = i;
			}
		}
	}//update powerups
	
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
		if(died == true && deathAnimationCounter > 0) {
			g.setColor(Color.WHITE);
			g.scale(3, 3);
			g.drawString("YOU DIED. . .\n" + deathAnimationCounter, ((SpaceInvaders.WINDOW_WIDTH/2)-50)/3, ((SpaceInvaders.WINDOW_HEIGHT/2)-10)/3);
			g.clearScaling();
			deathAnimationCounter--;
		}
		else if(died == true && deathAnimationCounter <= 0) {
			died = false;
			deathAnimationCounter = 200;
		}
		else {
			g.drawTexture(shipTexture, shipPos.x, shipPos.y);
			//drawCollisionBoundaries(g);
			for(PowerUp p: shipPowerUps) {
				p.render(g);
			}
		}
		
		//I want this regardless...
		if(lives == 1) {
			g.drawString(lives + " life", 10, SpaceInvaders.WINDOW_HEIGHT-25);
		}
		else {
			g.drawString(lives + " lives", 10, SpaceInvaders.WINDOW_HEIGHT-25);
		}
		renderHPBar(g);
		g.drawString(score + " points", SpaceInvaders.WINDOW_WIDTH-100, SpaceInvaders.WINDOW_HEIGHT-25);
	}
	public void renderBullets(Graphics g) {
		for(Bullet b: shipBullets) {
			b.render(g);
		}
	}
	public void drawCollisionBoundaries(Graphics g) {
		g.drawRect(shipPos.collision_x, shipPos.collision_y, shipPos.collision_width, shipPos.collision_height);
	}

	public void renderHPBar(Graphics g) {
		g.setColor(Color.WHITE);
		g.drawRect(9, SpaceInvaders.WINDOW_HEIGHT-16, 102, 12);
		//HP is 50
		float percentage = (float)((shipHP/50.0)*100);//*100 to make out of 100 percent instead of decimal
		g.setColor(Color.BLUE);
		g.fillRect(10f, (SpaceInvaders.WINDOW_HEIGHT-15), percentage, 10f);
		g.setColor(Color.WHITE);
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
					//if(unbreakingBulletPowerUp) { System.out.println(" removed off-screen bullet "); }
				}
			}
		}//if not rebounding powerup
		else {
			//rebounding, so flip if off-screen
			for(int i = 0; i < shipBullets.size(); i++) {
				if(shipBullets.get(i).bulletPos.offScreen == true) {
					shipBullets.get(i).toggleFlip();
					shipBullets.get(i).bulletPos.offScreen = false;
					//shipBullets.get(i).bulletPos.doNotSetOffScreen = true;
				}
				else if(shipBullets.get(i).bulletPos.checkOnScreen() == true) {
					//shipBullets.get(i).bulletPos.doNotSetOffScreen = false;
				}
			}
		}
	}//delete used bullets
	
	public void addPowerUp(PowerUp p) {
		int powerIdentifier = p.identifier;
		boolean duplicate = false;
		//check for duplicates
		for(int i = 0; i < shipPowerUps.size(); i++) {
			if(shipPowerUps.get(i).identifier == powerIdentifier) {
				shipPowerUps.get(i).resetDuration();
				duplicate = true;
				System.out.println("found duplicate");
			}
		}
		
		if(!duplicate) {
			shipPowerUps.add(p);
			activatePowerUp(powerIdentifier);
		}
	}
	
	public void activatePowerUp(int p) {
		//The way this is set up, there could be multiple active at a time
		activePowerUp = true;
		if(p == 1) {
			speedPowerUp = true;
		}
		else if(p == 2) {
			infiniteBulletPowerUp = true;
		}
		else if(p == 3) {
			unbreakingBulletPowerUp = true;
			System.out.println();
			System.out.println();
			System.out.println("Unbreaking bullet powerup");
		}
		else if(p == 4) {
			reboundingBulletPowerUp = true;
		}
		else if(p == 5) {
			armorPiercingBulletPowerUp = true;
		}
		else if(p == 6) {
			indestructiblePowerUp = true;
		}
		else {
			System.out.println("Ship didn't recognize PowerUp...");
			activePowerUp = false;
		}
	}//activate power up
	
	public void deactivatePowerUp(int p) {
		if(p == 1) {
			speedPowerUp = false;
		}
		else if(p == 2) {
			infiniteBulletPowerUp = false;
		}
		else if(p == 3) {
			unbreakingBulletPowerUp = false;
			System.out.println("end of unbreakable bullets");
			System.out.println();
			System.out.println();
		}
		else if(p == 4) {
			reboundingBulletPowerUp = false;
		}
		else if(p == 5) {
			armorPiercingBulletPowerUp = false;
		}
		else if(p == 6) {
			indestructiblePowerUp = false;
		}
		else {
			System.out.println("Ship didn't recognize PowerUp for deactivation...");
		}
		activePowerUp = false;
		if(speedPowerUp || infiniteBulletPowerUp || unbreakingBulletPowerUp || reboundingBulletPowerUp || armorPiercingBulletPowerUp || indestructiblePowerUp) {
			activePowerUp = true;//other powerup still active
		}
	}
	
}