package de.co.armadillo;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;

public class GameWorld {

	// Helper Objects
	private Random r;
	
	// Entitites
	private Target target;
	private Enemy[] enemy;
	private Background background;
	private GameCharacter character;
	private GameState state;
	private Stage stage;
	private TextField tf;
	private Projectile fire;
	private boolean display;
	
	// Amount of enemies
	private int amount = 3;
	
	// Target index
	private int targetIndex = 0;
	
	// Target arrangement
	private int[] targetArrange;
	
	public GameWorld() {
		
		r = new Random();
		
		// Background object necessary for scrolling
		background = new Background(0, 0, 10);
		
		// Initialize array
		enemy = new Enemy[amount];
		
		// Initialize enemies
		for(int i = 0; i < enemy.length; i++)
			enemy[i] = new Enemy(50+r.nextInt(620), -r.nextInt(100), 50 + r.nextInt(50));
		
		// Get target aiming
		target = new Target();
		
		// Arrange Gegnerfolge (SEE: Test.java)
		targetArrange = new int[amount];
		
		// Make an arrangement from 0 - amount
		for(int i = 0; i < targetArrange.length; i++) {
			targetArrange[i] = i;
		}
		
		// Fisher-Yates Shuffle
		int temp, rand;
		for(int i = targetArrange.length; i > 0; i--) {
			rand = r.nextInt(i);
			temp = targetArrange[rand];
			targetArrange[rand] = targetArrange[i-1];
			targetArrange[i-1] = temp;
		}
		
		// Create Cannon, targeting the target
		character = new GameCharacter(310, 700, target);
		
		// Projectile which is destined to hit specific target
		fire = new Projectile(target);
		
		// Create Game State, i.e. health, stage, highscore
		state = new GameState(3);
		
		// Variable for displaying warnings
		display = false;
		
		// Create Stage
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);
		
		// Skin for UI Elements
		Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"));
		
		// Initialize Textfield
		tf = new TextField("", skin);
		tf.setPosition(265, 25);
		tf.setSize(150, 50);
		tf.setMaxLength(9);
		stage.setKeyboardFocus(tf);
		tf.setTextFieldListener(new TextFieldListener() {
			
			public void keyTyped (TextField textField, char key) {

				// Check if input matches answer of equation
				try {
					if(enemy[targetArrange[targetIndex]].getEquation().checkAnswer(Integer.valueOf(tf.getText()))) {
						tf.setText("");
						fire.shoot(enemy[targetArrange[targetIndex]]);
					}else if(key == '\n' || key == '\r') {
						tf.setText("");
					}
				}catch(Exception ex) {}
			}
		});
		stage.addActor(tf);
	}
	
	// Responsible for updating variables, controlling the game state
	public void update(float delta) {
		
		// Update background
		background.update(delta);
		
		// Update every enemy	
		for(int i = 0; i < enemy.length; i++) {
			enemy[i].update(delta);
		}
		
		// Update projectile
		fire.update(delta);
		
		// Check if projectile hit enemy
		if(fire.checkCollision(enemy[targetArrange[targetIndex]].getCircle())) {
			
			// Set enemy status
			enemy[targetArrange[targetIndex]].gotHit();
			enemy[targetArrange[targetIndex]].destroy();
			
			// Reset projectile
			fire.reset();
			
			// Focus next enemy
			targetIndex++;
			
			// Add to score
			state.addScore(2);
		}
		
		if(targetIndex == amount) {
			
			// Set game state
			state.nextStage();
			amount++;
			
			// Display message
			display = true;
			
			// Reset everything
			targetIndex = 0;
			
			//************************************************
			enemy = new Enemy[amount];
			
			for(int i = 0; i < enemy.length; i++)
				enemy[i] = new Enemy(50+r.nextInt(620), -r.nextInt(100), 50 + r.nextInt(50));
			
			targetArrange = new int[amount];
			
			for(int i = 0; i < targetArrange.length; i++) {
				targetArrange[i] = i;
			}
			
			int temp, rand;
			for(int i = targetArrange.length; i > 0; i--) {
				rand = r.nextInt(i);
				temp = targetArrange[rand];
				targetArrange[rand] = targetArrange[i-1];
				targetArrange[i-1] = temp;
			}
			
			for(int i = 0; i < enemy.length; i++)
				enemy[i] = new Enemy(50+r.nextInt(620), -r.nextInt(100), 50 + r.nextInt(50));
			
			//************************************************
		}
		
		// Update aiming
		target.update(enemy[targetArrange[targetIndex]]);
		
		// Check if enemy hit bottom
		for(int i = 0; i < enemy.length; i++)
			if(enemy[i].getCircle().y > 840) {
				
				// Get rid of it and lose hitpoints
				enemy[i].destroy();
				state.loseHealth();
				
				// In case it was designated target, switch
				if(enemy[i] == enemy[targetArrange[targetIndex]])
					targetIndex++;
			}
	}
	
	public boolean display() {
		return display;
	}
	
	public void setDisplay(boolean state) {
		display = state;
	}

	public GameCharacter getChar() {
		return character;
	}
	
	public Enemy[] getEnemy() {
		return enemy;
	}
	
	public Target getAim() {
		return target;
	}

	public Background getBackground() {
		return background;
	}
	
	public Stage getStage() {
		return stage;
	}
	
	public Projectile getProjectile() {
		return fire;
	}
	
	public Target getTarget() {
		return target;
	}
	
	public int getTargetIndex() {
		return targetIndex;
	}
	
	public GameState getGameState() {
		return state;
	}
}
