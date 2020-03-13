package com.GravlandiaStudios.SpaceInvaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import java.util.ArrayList;
import org.mini2Dx.core.game.BasicGame;
import org.mini2Dx.core.graphics.Graphics;

public class SpaceInvaders extends BasicGame {
	public static final String GAME_IDENTIFIER = "com.GravlandiaStudios.SpaceInvaders";
	public static final int WINDOW_WIDTH = 1920;
	public static final int WINDOW_HEIGHT = 1020;
	public static int highScore = 412300;

	public boolean enemiesMoveRight = true;
	
	//Settings/preferences
	public static boolean BulletTailSetting = false;//box follows 105p behind bullet
	public static boolean drawWithBullets = false;//bullets are stationary until death
	public static boolean bulletTextTexture = true;//Shoot "bullet" instead of texture
	public static boolean displayMessage = true;//Every time I say guh, that means bullet.

	public Ship player;
	public ArrayList<Alien> enemies;
	public int numEnemiesAcross = 7;

	public static Texture bulletTexture;//so not creating for each bullet/alien
	public static Texture alienTexture;

	public static int level = 0;

	public int enemyFurthestLeft = -1;//0
	public int enemyFurthestRight = -1;//6
	public int maxNumOfEnemies = 21;//3 rows. Consider doing 2 rows max.

	@Override
	public void initialise() {
		alienTexture = new Texture("alien.JPG");
		bulletTexture = new Texture("bullet.png");
		player = new Ship();
		enemies = new ArrayList<Alien>();
	}

	@Override
	public void update(float delta) {
		if(player.died == false) {
			updateFurthestLeftRight();//if set -1, will fix here...

			if(enemies.size() == 0) {
				player.score += 50*level;

				//first time will set to 1
				level++;

				int max = 4+(3*level);//7 on level 1
				if(max > maxNumOfEnemies)
					max = maxNumOfEnemies;

				for(int i = 0; i < max; i++) {
					//%numEnemiesAcross is however many in a row; i/6 is how many rows down
					enemies.add(new Alien( 250+(200*(i%numEnemiesAcross)), (15+(i/numEnemiesAcross)*180), 200, 175 ));
				}
				//System.out.println("alien0: " + enemies.get(0).alienPos.toString());
				updateFurthestLeftRight();//update, new group of aliens

				//Give player extra life every 10-ish rounds
				if(level % 10 == 0) {
					player.lives++;
				}

				//reset far left/right
				enemyFurthestLeft = 0;
				enemyFurthestRight = 6;
			}

			//I guess update player, then take damage, then alien, bullets/etc. as part of that
			player.update();//moving and shooting

			//update enemies
			for(int i = 0; i < enemies.size(); i++) {
				//take damage, then shoot (if not destroyed)
				ArrayList<Bullet> temp = enemies.get(i).takeDamage(player.shipBullets);//need to be able to access player bullets
				player.deleteUsedBullets(temp);
				if(enemies.get(i).alienHP <= 0) {
					//remove enemy
					enemies.remove(i);
					i--;
					player.score += 25;
				}//if 0 HP
				else { 
					enemies.get(i).update();	
				}
			}//for all enemies

			calcPlayerDamage();

			if( (enemyFurthestLeft > -1 && enemyFurthestRight > -1) 
					&& (enemyFurthestLeft < enemies.size() && enemyFurthestRight < enemies.size()) ) {
				moveEnemies();
			}

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
		if(enemiesMoveRight) {
			for(int i = 0; i < enemies.size(); i++) {
				enemies.get(i).alienPos.moveRight();
			}
			if(enemies.get(enemyFurthestRight).alienPos.collision_x+enemies.get(enemyFurthestRight).alienPos.collision_width > WINDOW_WIDTH-10) {
				enemiesMoveRight = false;
			}
		}//move right
		else {
			for(int i = 0; i < enemies.size(); i++) {
				enemies.get(i).alienPos.moveLeft();
			}
			if(enemies.get(enemyFurthestLeft).alienPos.collision_x < 10) {
				enemiesMoveRight = true;
			}
		}
	}//move enemies

	public void calcPlayerDamage() {
		//enemies take damage before updating
		//so this is for player taking damage
		for(int i = 0; i < enemies.size(); i++) {
			for(int j = 0; j < enemies.get(i).alienBullets.size(); j++) {
				//for each bullet for each enemy
				if(player.shipPos.overlapping(enemies.get(i).alienBullets.get(j).bulletPos)) {
					player.shipHP -= enemies.get(i).alienBullets.get(j).damage;
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

			//but actually bullets on top of everything
			for(Alien a: enemies) {
				a.renderBullets(g);
			}
			player.renderBullets(g);
		}
		else {
			g.setColor(Color.RED);
			g.scale(4, 4);
			g.drawString("Game Over", ((WINDOW_WIDTH/2)-130)/4, ((WINDOW_HEIGHT/2)-10)/4);//adjust for scale
			g.clearScaling();
		}
	}//render

	public void updateFurthestLeftRight() {
		//Stops moving if only one left, so going to try changing it...
		if(enemies.size() == 0) {
			enemyFurthestLeft = -1;
			enemyFurthestRight = -1;
		}
		else if(enemies.size() == 1) {
			enemyFurthestLeft = 0;
			enemyFurthestRight = 0;
		}
		else {
			float x_max = 0;//set to opposite sides
			float x_min = 2000;//set to other side
			for(int i = 0; i < enemies.size(); i++) {
				if(enemies.get(i).alienPos.x > x_max) {
					enemyFurthestRight = i;
					x_max = enemies.get(i).alienPos.x;
				}//if further right / max
				if(enemies.get(i).alienPos.x < x_min) {
					enemyFurthestLeft = i;
					x_min = enemies.get(i).alienPos.x;
				}//if further left / min
			}//for all enemies
		}//else size > 1
	}//update furthest left/right
}
