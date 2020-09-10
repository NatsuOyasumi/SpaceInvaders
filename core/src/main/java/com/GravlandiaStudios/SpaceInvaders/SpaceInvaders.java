package com.GravlandiaStudios.SpaceInvaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import java.util.ArrayList;
import org.mini2Dx.core.game.BasicGame;
import org.mini2Dx.core.graphics.Graphics;

public class SpaceInvaders extends BasicGame {
	public static final String GAME_IDENTIFIER = "com.GravlandiaStudios.SpaceInvaders";
	public static final int WINDOW_WIDTH = 1920;
	public static final int WINDOW_HEIGHT = 1080;//1020;
	public static int highScore = 412300;

	public boolean enemiesMoveRight0 = true;
	public boolean enemiesMoveRight1 = true;//I can't think of a better way at the moment, so variables are per row
	public boolean enemiesMoveRight2 = true;

	//Settings/preferences
	public static boolean BulletTailSetting = false;//box follows 105p behind bullet
	public static boolean drawWithBullets = false;//bullets are stationary until death
	public static boolean bulletTextTexture = false;//Shoot "bullet" instead of texture
	public static boolean displayMessage = bulletTextTexture;//Every time I say guh, that means bullet.
	public static int difficultyLevel = 2;//easy, medium, hard, and as high as I want to go //0 == broken, add settings for powerups

	public Ship player;
	public ArrayList<Alien> enemies;
	public int numEnemiesAcross = 7;
	public ArrayList<PowerUp> powerUps;

	public static Texture bulletTexture;//so not creating for each bullet/alien
	public static Texture alienTexture;
	public static Texture speedPowerUpTexture;
	public static Texture infinitebulletPowerUpTexture;
	public static Texture unbreakingBulletPowerUpTexture;
	public static Texture reboundingBulletPowerUpTexture;
	public static Texture armorPiercingBulletPowerUpTexture;
	public static Texture indestructiblePowerUp;

	public static int alienSpeed = 5;
	public static int playerSpeed = 7;

	public static int level = 0;
	
	public int topRowStart = 0;//yes need this, if top row defeated but mid/low rows still exist
	public int topRowEnd = 6;
	public int midRowStart = 7;
	public int midRowEnd = 13;
	public int lowRowStart = 14;
	public int lowRowEnd = 20;
	public int tempCounter = 34;//will increment to 35 at start of update
	
	
	public int maxNumOfEnemies = 21;//3 rows. Consider doing 2 rows max.
	
	public double chanceOfDroppingPowerUp = 0.25;

	@Override
	public void initialise() {
		alienTexture = new Texture("alien.JPG");
		bulletTexture = new Texture("bullet.png");
		speedPowerUpTexture = new Texture("Speed Powerup.png");
		infinitebulletPowerUpTexture = new Texture("Infinite Bullets Powerup.png");
		unbreakingBulletPowerUpTexture = new Texture("Unbreaking Bullets Powerup.png");
		reboundingBulletPowerUpTexture = new Texture("Rebound Powerup.png");
		armorPiercingBulletPowerUpTexture = new Texture("Armor Piercing Powerup.png");
		indestructiblePowerUp = new Texture("Indestructible Powerup.png");
		player = new Ship();
		enemies = new ArrayList<Alien>();
		powerUps = new ArrayList<PowerUp>();
	}

	@Override
	public void update(float delta) {
		if(Gdx.input.isKeyJustPressed(Keys.Q)) {
			generatePowerUp(new Position(true));
		}
		while(powerUps.size() > 5) { //8 looks like a lot
			player.gold += 15;
			powerUps.remove(powerUps.size()-1);
		}
		
		tempCounter++;
		if(player.died == false) {
			updateFurthestLeftRight();//if set -1, will fix here...

			if(enemies.size() == 0) {
				player.score += 50*level;

				//first time will set to 1
				level++;

				int max = 5+(difficultyLevel*level);//7 on level 1
				if(max > maxNumOfEnemies)
					max = maxNumOfEnemies;

				for(int i = 0; i < max; i++) {
					//only increase speed depending on difficulty level?
					//%numEnemiesAcross is however many in a row; i/6 is how many rows down
					int s = 5 + (int)( 2.5*(i/numEnemiesAcross) );
					enemies.add(new Alien( 250+(200*(i%numEnemiesAcross)), (15+(i/numEnemiesAcross)*180), 200, 175, s ));
				}
				//System.out.println("alien0: " + enemies.get(0).alienPos.toString());

				//Give player extra life every 10-ish rounds
				if(level % 10 == 0) {
					player.lives++;
				}

				//reset row measurements
				System.out.println("reset row measurements");
				topRowStart = 0;
				topRowEnd = 6;
				midRowStart = 7;
				midRowEnd = 13;
				lowRowStart = 14;
				lowRowEnd = 20;
				
				updateFurthestLeftRight();//update, new group of aliens

			}//if enemies.size() == 0

			//I guess update player, then take damage, then alien, bullets/etc. as part of that
			player.update();//moving and shooting

			//add this later - if enemies defeated, keep bullets depending on difficulty
			//update enemies
			for(int i = 0; i < enemies.size(); i++) {
				//take damage, then shoot (if not destroyed)
				int row = 0;
				if(topRowStart <= i && i <= topRowEnd)
					row = 1;
				else if(midRowStart <= i && i <= midRowEnd)
					row = 2;
				else if(lowRowStart <= i && i <= lowRowEnd)
					row = 3;
				
				//I think sending in the ArrayList makes changes directly to list, so send copy
				ArrayList<Bullet> copy = player.shipBullets;
				
				ArrayList<Bullet> temp = enemies.get(i).takeDamage(copy, player.unbreakingBulletPowerUp, row);//need to be able to access player bullets
				player.deleteUsedBullets(temp);
				if(enemies.get(i).alienHP <= 0) {
					//will change row vals as size/positions update
					//if equals, must reset - but these aren't impacted during update, only when new group of enemies
					//if ==, then 8 becomes 7, no change. If 8-12 destroyed, 13 becomes 7...
					//if last in row destroyed, then --; if first destroyed, next one slides down
					if(i <= topRowEnd)
						topRowEnd--;//I suppose this is needed too. Oops...
					if(i < midRowStart) 
						midRowStart--;
					if(i <= midRowEnd)
						midRowEnd--;
					if(i < lowRowStart)
						lowRowStart--;
					if(i <= lowRowEnd)
						lowRowEnd--;
					
					//possibly drop powerup (before position is deleted)
					double percentage = Math.random();
					if(percentage <= chanceOfDroppingPowerUp) {
						generatePowerUp(enemies.get(i).alienPos);
					}
					
					//remove enemy
					enemies.remove(i);
					i--;
					player.score += 25;
					player.gold += 7;
				}//if 0 HP
				else { 
					enemies.get(i).update();	
				}
			}//for all enemies
			
			for(int i = 0; i < powerUps.size(); i++) {
				PowerUp p = powerUps.get(i);//smaller to call...  -_-
				if(player.shipPos.overlapping(p.powerPos) && p.pickedUp == false) {
					p.pickedUp = true;
					player.addPowerUp(p);
					player.activatePowerUp(p.identifier);
					powerUps.remove(p);
				}
				else if(p.powerPos.offScreen) {
					powerUps.remove(i);
				}
				else {
					//else b/c when picking up, don't immediately decrease timer, so don't update.
					p.update();
					//p.textPosition = i;//in ship
				}
			}//for all powerups

			calcPlayerDamage();

			//possibly need to fix this later !!!
			//			if( (topRowStart > -1 && topRowEnd > -1) 
			//					&& (topRowStart < enemies.size() && topRowEnd < enemies.size()) ) {
			moveEnemies();
			//			}

		}//if player not dead
		else {
			for(int i = 0; i < enemies.size(); i++) {
				enemies.get(i).moveBullets();
			}
			player.moveBullets();//auto-deletes off-screen bullets
		}
	}

	public void moveEnemies() {
		//move enemy group side to side
		//if not in margin of 50p on either side

		//Top Row
		if( (topRowStart > -1 && topRowEnd > -1) 
				&& (topRowStart < enemies.size() && topRowEnd < enemies.size()) ) {
			if(tempCounter%35 == 0) {
				//System.out.println("moving top " + topRowEnd);
			}
			if(enemiesMoveRight0) {
				for(int i = 0; i <= topRowEnd && i < enemies.size(); i++) {
					enemies.get(i).alienPos.moveRight();
				}
				if(enemies.get(topRowEnd).alienPos.collision_x+enemies.get(topRowEnd).alienPos.collision_width > WINDOW_WIDTH-20) {
					enemiesMoveRight0 = false;
				}
			}//move right
			else {
				for(int i = 0; i <= topRowEnd && i < enemies.size(); i++) {
					enemies.get(i).alienPos.moveLeft();
				}
				if(enemies.get(topRowStart).alienPos.collision_x < 20) {
					enemiesMoveRight0 = true;
				}
			}
		}//if top row ends are still in list
		else {
			//System.out.println("top " + (topRowStart > -1) + " " + (topRowEnd > -1) + " " + (topRowStart < enemies.size()) + " " + (topRowEnd < enemies.size()));
		}

		//Second row
		if( (midRowStart > -1 && midRowEnd > -1) 
				&& (midRowStart < enemies.size() && midRowEnd < enemies.size()) ) {
			if(tempCounter%35 == 0) {
				//System.out.println("moving mid " + midRowStart + " " + midRowEnd);
			}
			if(enemiesMoveRight1) {
				for(int i = midRowStart; i <= midRowEnd && i < enemies.size(); i++) {
					enemies.get(i).alienPos.moveRight();
				}
				if(enemies.get(midRowEnd).alienPos.collision_x+enemies.get(midRowEnd).alienPos.collision_width > WINDOW_WIDTH-20) {
					enemiesMoveRight1 = false;
				}
			}//move right
			else {
				for(int i = midRowStart; i <= midRowEnd && i < enemies.size(); i++) {
					enemies.get(i).alienPos.moveLeft();
				}
				if(enemies.get(midRowStart).alienPos.collision_x < 20) {
					enemiesMoveRight1 = true;
				}
			}
		}//if middle/2nd row ends are still in list
		else {
			/*
			if(tempCounter%35 == 0) {
				System.out.print("mid " + (midRowStart > -1) + " " + (midRowEnd > -1) + " " + (midRowStart < enemies.size()) + " " + (midRowEnd < enemies.size()));
				if(!(midRowStart > -1))
					System.out.print(" " + midRowStart);
				if(!(midRowEnd > -1))
					System.out.print(" " + midRowEnd);
				if(!(midRowStart < enemies.size()))
					System.out.print(" " + midRowStart);
				if(!(midRowEnd < enemies.size()))
					System.out.print(" " + midRowEnd);
				System.out.println();
			}
			*/
		}

		//Third row
		if( (lowRowStart > -1 && lowRowEnd > -1) 
				&& (lowRowStart < enemies.size() && lowRowEnd < enemies.size()) ) {
			if(tempCounter%35 == 0) {
				//System.out.println("moving low " + lowRowStart + " " + lowRowEnd);
			}
			if(enemiesMoveRight2) {
				for(int i = lowRowStart; i <= lowRowEnd && i < enemies.size(); i++) {
					enemies.get(i).alienPos.moveRight();
				}
				if(enemies.get(lowRowEnd).alienPos.collision_x+enemies.get(lowRowEnd).alienPos.collision_width > WINDOW_WIDTH-20) {
					enemiesMoveRight2 = false;
				}
			}//move right
			else {
				for(int i = lowRowStart; i <= lowRowEnd && i < enemies.size(); i++) {
					enemies.get(i).alienPos.moveLeft();
				}
				if(enemies.get(lowRowStart).alienPos.collision_x < 20) {
					enemiesMoveRight2 = true;
				}
			}
		}//if bottom/3rd row ends are still in list
		else {
			//System.out.println("low " + (lowRowStart > -1) + " " + (lowRowEnd > -1) + " " + (lowRowStart < enemies.size()) + " " + (lowRowEnd < enemies.size()));
		}
	}//move enemies

	public void calcPlayerDamage() {
		//enemies take damage before updating
		//so this is for player taking damage
		for(int i = 0; i < enemies.size(); i++) {
			for(int j = 0; j < enemies.get(i).alienBullets.size(); j++) {
				//for each bullet for each enemy
				if(player.shipPos.overlapping(enemies.get(i).alienBullets.get(j).bulletPos)) {
					if(player.indestructiblePowerUp == false) {
						player.shipHP -= enemies.get(i).alienBullets.get(j).damage;
					}
					enemies.get(i).alienBullets.remove(j);
					j--;
				}
			}//each bullet
		}//each enemy
	}//take Damage

	@Override
	public void interpolate(float alpha) {

	}

	@Override
	public void render(Graphics g) {
		//temp - fix this later? Still some flickering of powerups, not sure why
		while(powerUps.size() > 5) { //8 looks like a lot
			player.gold += 15;
			powerUps.remove(powerUps.size()-1);
		}
		
		if(displayMessage) {
			int scale = 3;
			g.setScale(scale, scale);
			g.drawString("Every time I say guh, that means bullet.", ((WINDOW_WIDTH/2)-400)/scale, (WINDOW_HEIGHT-8)/scale);
			g.clearScaling();
		}
		
		if(player.lives > 0) { //-1?
			for(Alien a: enemies) {
				a.render(g);
				//a.drawCollisionSquare(g);
			}
			//player last so on top
			player.render(g);
			
			for(PowerUp p: powerUps) {
				p.render(g);//if these are before player, the black background covers them up
			}

			//but actually bullets on top of everything
			for(Alien a: enemies) {
				a.renderBullets(g);
			}
			player.renderBullets(g);
		}
		else {
			g.setColor(Color.RED);
			g.scale(4, 4);
			g.drawString("Game Over", ((WINDOW_WIDTH/2)-130)/4, ((WINDOW_HEIGHT/2)-20)/4);//adjust for scale
			g.setColor(Color.WHITE);
			g.drawString("Score: " + player.score, ((WINDOW_WIDTH/2)-130)/4, ((WINDOW_HEIGHT/2)+45)/4);//adjust for scale
			g.clearScaling();
		}
	}//render

	public void updateFurthestLeftRight() {
		//Stops moving if only one left, so going to try changing it...
		if(enemies.size() == 0) {
			topRowStart = -1;
			topRowEnd = -1;
			midRowStart = -1;
			midRowEnd = -1;
			lowRowStart = -1;
			lowRowEnd = -1;
		}
		else if(enemies.size() == 1) {
			topRowStart = 0;
			topRowEnd = 0;
			midRowStart = -1;//fix this later --> 0 or -1
			midRowEnd = -1;//if -1 it breaks, so just set to 0 I guess...
			lowRowStart = -1;
			lowRowEnd = -1;
		}
		else {
			//y-vals: 15+(i/numEnemiesAcross)*180
			float x_max0 = 0;//set to opposite sides
			float x_min0 = 2000;//set to other side
			float x_max1 = 0;
			float x_min1 = 2000;
			float x_max2 = 0;
			float x_min2 = 2000;
			/*
			if(tempCounter%35 == 0) {
			System.out.println("\n\nStart \n" + enemies.size());
			System.out.println("top " + topRowStart + " " + topRowEnd);
			System.out.println("mid " + midRowStart + " " + midRowEnd);
			System.out.println("low " + lowRowStart + " " + lowRowEnd);
			}
			*/
			for(int i = 0; i < enemies.size(); i++) {
				if(tempCounter%35 == 0) {
					//System.out.print(i);
				}
				if(enemies.get(i).alienPos.y == 15) {
					if(enemies.get(i).alienPos.x >= x_max0) {
						//topRowEnd = i%numEnemiesAcross;
						topRowEnd = i;
						x_max0 = enemies.get(i).alienPos.x;
						//topRowEnd = i;
						if(tempCounter%35 == 0) {
							//System.out.println(" topRowEnd");
						}
					}//if further right / max
					if(enemies.get(i).alienPos.x <= x_min0) {
						//topRowStart = i%numEnemiesAcross;
						topRowStart = i;
						x_min0 = enemies.get(i).alienPos.x;
						if(tempCounter%35 == 0) {
							//System.out.println(" topRowStart");
						}
					}//if further left / min
				}//1st/top row
				else if(enemies.get(i).alienPos.y == 195) {
					if(enemies.get(i).alienPos.x >= x_max1) {
						//midRowEnd = (i%numEnemiesAcross) + 7;//i%num == 0, i = 7
						midRowEnd = i;
						x_max1 = enemies.get(i).alienPos.x;
						if(tempCounter%35 == 0) {
							//System.out.println(" midRowEnd");
						}
					}//if further right / max
					if(enemies.get(i).alienPos.x <= x_min1) {
						//midRowStart = (i%numEnemiesAcross) + 7;
						midRowStart = i;
						x_min1 = enemies.get(i).alienPos.x;
						if(tempCounter%35 == 0) {
							//System.out.println(" midRowStart");
						}
					}//if further left / min
				}//2nd row
				else if(enemies.get(i).alienPos.y == 375) {
					if(enemies.get(i).alienPos.x >= x_max2) {
						//lowRowEnd = (i%numEnemiesAcross) + 14;//first is 14
						lowRowEnd = i;
						x_max2 = enemies.get(i).alienPos.x;
						if(tempCounter%35 == 0) {
							//System.out.println(" lowRowEnd");
						}
					}//if further right / max
					if(enemies.get(i).alienPos.x <= x_min2) {
						//lowRowStart = (i%numEnemiesAcross) + 14;
						lowRowStart = i;
						x_min2 = enemies.get(i).alienPos.x;
						if(tempCounter%35 == 0) {
							//System.out.println(" lowRowStart");
						}
					}//if further left / min
				}//3rd row
				else {
					if(tempCounter%35 == 0) {
						//System.out.println("*** Was greater than lowRowEnd " + lowRowEnd);
					}
				}
			}//for all enemies
		}//else size > 1
		/*
		if(midRowStart == -1)
			midRowStart = topRowStart;//prob no second row yet
		if(midRowEnd == -1)
			midRowEnd = topRowEnd;//prob no second row yet
		if(lowRowStart == -1)
			lowRowStart = topRowStart;//prob no third row yet
		if(lowRowEnd == -1)
			lowRowEnd = topRowEnd;//prob no second row yet
		*/
	}//update furthest left/right
	
	public void generatePowerUp(Position p) {
		/*
		1 speed PowerUp (ship / bullets move faster)
		2 infinite bullets (laser... :D)
		3 unbreaking bullets (can hit multiple aliens)
		4 rebounding - bounce off edge of screen
		5 armor piercing - one-hit KO
		6 indestructible - ship doesn't take damage
		*/
		
		int powerIdentifier = (((int)(Math.random()*3820028))%6)+1;
		if(powerUps.size() <= 5) { //8 looks like a lot
			powerUps.add(new PowerUp(p, powerIdentifier, powerUps.size()+1));
		}
		else {
			player.gold += 15;
		}
	}//generatePowerUp
	
	public void generatePowerUp(Position p, int PI) {
		/*
		1 speed PowerUp (ship / bullets move faster)
		2 infinite bullets (laser... :D)
		3 unbreaking bullets (can hit multiple aliens)
		4 rebounding - bounce off edge of screen
		5 armor piercing - one-hit KO
		6 indestructible - ship doesn't take damage
		*/
		
		//int powerIdentifier = (((int)(Math.random()*3820028))%6)+1;
		powerUps.add(new PowerUp(p, PI, powerUps.size()+1));
	}//generatePowerUp
	
}
