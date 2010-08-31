// JFC
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

// GTGE
import com.golden.gamedev.*;
import com.golden.gamedev.object.*;
import com.golden.gamedev.object.background.*;
import com.golden.gamedev.object.sprite.*;
import com.golden.gamedev.object.collision.*;

/**
 * It's time to play!
 * 
 * Objective: show how to use playfield to automate all things!
 */
public class Prototype extends Game {
	// Cliente/Servidor
	BufferedReader inFromUser;
	Socket clientSocket;
	DataOutputStream outToServer;
	BufferedReader inFromServer;
	String modifiedSentence;
	// /
	PlayField playfield; // the game playfield
	Background background;

	SpriteGroup PLAYER_GROUP;
	SpriteGroup PROJECTILE_GROUP;
	SpriteGroup ENEMY_GROUP;

	AnimatedSprite plane;
	BufferedImage[] plane_left, plane_right, plane_down, plane_up;

	Timer moveTimer; // to set enemy behaviour
						// for moving left to right, right to left

	ProjectileEnemyCollision2 collision;

	GameFont font;

	/********************************************************************************
	 * Teste Cliente/Servidor
	 * ***************************************************
	 * ***************************
	 */
	public void enviaMSGServidor(String sentence) throws Exception {
		// cria o stream do teclado
		inFromUser = new BufferedReader(new InputStreamReader(System.in));
		outToServer = new DataOutputStream(clientSocket.getOutputStream());
		inFromServer = new BufferedReader(new InputStreamReader(
				clientSocket.getInputStream()));
		// envia a linha para o server
		outToServer.writeBytes(sentence + '\n');
		// lê uma linha do server
		modifiedSentence = inFromServer.readLine();
		// apresenta a linha do server no vídeo
//		System.out.println("FROM SERVER " + modifiedSentence);
	}

	/****************************************************************************/
	/**************************** GAME SKELETON *********************************/
	/****************************************************************************/

	public void initResources() {

		// cria o socket de acesso ao server hostname na porta 6789
		try {
			clientSocket = new Socket("localhost", 6789);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// cria os streams (encadeamentos) de entrada e saida com o servidor

		// create the game playfield
		playfield = new PlayField();

		// associate the playfield with a background
		background = new ImageBackground(getImage("resources/background.jpg"),
				640, 480);
		playfield.setBackground(background);

		// create our plane sprite
		plane = new AnimatedSprite(getImages("resources/tank_red.png", 1, 1),
				287.5, 390);
		plane.setAnimate(false);
		plane.setLoopAnim(false);
		
		plane_left = getImages("resources/tank_red_left.png", 1, 1);
		plane_right = getImages("resources/tank_red_right.png", 1, 1);
		plane_down = getImages("resources/tank_red_down.png", 1, 1);
		plane_up = getImages("resources/tank_red.png", 1, 1);
		// ///// create the sprite group ///////
		PLAYER_GROUP = new SpriteGroup("Player");
		// no need to set the background for each group, we delegated it to
		// playfield
		// PLAYER_GROUP.setBackground(background);
		PROJECTILE_GROUP = new SpriteGroup("Projectile");

		// add all groups into our playfield
		playfield.addGroup(PLAYER_GROUP);
		playfield.addGroup(PROJECTILE_GROUP);
		// use shortcut, creating group and adding it to playfield in one step
		ENEMY_GROUP = playfield.addGroup(new SpriteGroup("Enemy"));

		// ///// insert sprites into the sprite group ///////
		PLAYER_GROUP.add(plane);
		
		// inserts sprites in rows to ENEMY_GROUP
		BufferedImage image = getImage("resources/tank_enemy.png");
		int startX = 10, startY = 30; // starting coordinate
		for (int j = 0; j < 4; j++) { // 4 rows
			for (int i = 0; i < 7; i++) { // 7 sprites in a row
				Sprite enemy = new Sprite(image, startX + (i * 80), startY
						+ (j * 70));
				enemy.setHorizontalSpeed(0.04);
				ENEMY_GROUP.add(enemy);
			}
		}

		// init the timer to control enemy sprite behaviour
		// (moving left-to-right, right-to-left)
		moveTimer = new Timer(2000); // every 2 secs the enemies reverse its
										// speed

		// ///// register collision ///////
		collision = new ProjectileEnemyCollision2(this);
		// register collision to playfield
		playfield.addCollisionGroup(PROJECTILE_GROUP, ENEMY_GROUP, collision);

		font = fontManager.getFont(getImages("resources/font.png", 20, 3),
				" !            .,0123" + "456789:   -? ABCDEFG"
						+ "HIJKLMNOPQRSTUVWXYZ ");
	}

	public void update(long elapsedTime) {
		// no need to update the background and the group one by one
		// the playfield has taken this job!
		// background.update(elapsedTime);
		// PLAYER_GROUP.update(elapsedTime);
		// ENEMY_GROUP.update(elapsedTime);
		// PROJECTILE_GROUP.update(elapsedTime);

		// collision.checkCollision();

		// playfield update all things and check for collision
		playfield.update(elapsedTime);

		// enemy sprite movement timer
		if (moveTimer.action(elapsedTime)) {
			// reverse all enemies' speed
			Sprite[] sprites = ENEMY_GROUP.getSprites();
			int size = ENEMY_GROUP.getSize();

			// iterate the sprites
			for (int i = 0; i < size; i++) {
				// reverse sprite velocity
				sprites[i].setHorizontalSpeed(-sprites[i].getHorizontalSpeed());
			}
		}

		// control the sprite with arrow key
		double speedX = 0;
		if (keyDown(KeyEvent.VK_LEFT)) {
			speedX = -0.1;
			
			plane.setImages(plane_left);
			
//			try {
//				this.enviaMSGServidor("Esquerda");
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
		if (keyDown(KeyEvent.VK_RIGHT)) {
			speedX = 0.1;
			
			plane.setImages(plane_right);
			
//			try {
//				this.enviaMSGServidor("Direita");
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
		double speedY = 0;
		if (keyDown(KeyEvent.VK_UP)) {
		speedY = -0.1;
		
		plane.setImages(plane_up);
			
		
//		try {
//			this.enviaMSGServidor("Cima");
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
		if (keyDown(KeyEvent.VK_DOWN)) {
			speedY = 0.1;
			
			plane.setImages(plane_down);
			
//			try {
//				this.enviaMSGServidor("Baixo");
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
		plane.setHorizontalSpeed(speedX);
		plane.setVerticalSpeed(speedY);

		// firing!!
		if (keyPressed(KeyEvent.VK_CONTROL)) {
			// create projectile sprite
			Sprite projectile = new Sprite(getImage("resources/projectile.png"));
			
			if (plane.getImages() == plane_up) { 
				projectile.setLocation(plane.getX() +0, plane.getY() - 30);
				projectile.setVerticalSpeed(-0.2);
			}
			else if ((plane.getImages() == plane_down)) {
				projectile.setLocation(plane.getX() +0, plane.getY()+75);
				projectile.setVerticalSpeed(0.2);
			}
			else if ((plane.getImages() == plane_left)) {
				projectile.setLocation(plane.getX() -30, plane.getY());
				projectile.setHorizontalSpeed(-0.2);
			}
			else if ((plane.getImages() == plane_right)) {
				projectile.setLocation(plane.getX() +70, plane.getY());
				projectile.setHorizontalSpeed(0.2);
			}

			// add it to PROJECTILE_GROUP
			PROJECTILE_GROUP.add(projectile);

			// play fire sound
			playSound("resources/sound1.wav");
		}

		// toggle ppc
		if (keyPressed(KeyEvent.VK_ENTER)) {
			collision.pixelPerfectCollision = !collision.pixelPerfectCollision;
		}

		background.setToCenter(plane);
	}

	public void render(Graphics2D g) {
		// (once again) no need to render the background and the group one by
		// one
		// the playfield has taken this job!
		// background.render(g);
		// PLAYER_GROUP.render(g);
		// ENEMY_GROUP.render(g);
		// PROJECTILE_GROUP.render(g);
		playfield.render(g);

		// draw info text
//		font.drawString(g, "ARROW KEY : MOVE", 10, 10);
//		font.drawString(g, "CONTROL   : FIRE", 10, 30);
//		font.drawString(g, "ENTER     : TOGGLE PPC", 10, 50);

		if (collision.pixelPerfectCollision) {
			font.drawString(g, " USE PIXEL PERFECT COLLISION ", GameFont.RIGHT,
					0, 460, getWidth());
		}
	}

	/****************************************************************************/
	/***************************** START-POINT **********************************/
	/****************************************************************************/

	public static void main(String[] args) {
		GameLoader game = new GameLoader();
		game.setup(new Prototype(), new Dimension(640, 480), false);
		game.start();
		// // fecha o cliente
		// try {
		// clientSocket.close();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

	}

}

class ProjectileEnemyCollision2 extends BasicCollisionGroup {

	Prototype owner;

	public ProjectileEnemyCollision2(Prototype owner) {
		this.owner = owner; // set the game owner
							// we use this for getting image and
							// adding explosion to playfield
	}

	// when projectiles (in group a) collided with enemy (in group b)
	// what to do?
	public void collided(Sprite s1, Sprite s2) {
		// we kill/remove both sprite!
		s1.setActive(false); // the projectile is set to non-active
		s2.setActive(false); // the enemy is set to non-active

		// show explosion on the center of the exploded enemy
		// we use VolatileSprite -> sprite that animates once and vanishes
		// afterward
		BufferedImage[] images = owner.getImages("resources/explosion.png", 7,
				1);
		VolatileSprite explosion = new VolatileSprite(images, s2.getX(),
				s2.getY());

		// directly add to playfield without using SpriteGroup
		// the sprite is added into a reserved extra sprite group in playfield
		// extra sprite group is used especially for animation effects in game
		owner.playfield.add(explosion);
	}

}