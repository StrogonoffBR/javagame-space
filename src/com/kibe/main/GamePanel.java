package com.kibe.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

import entitites.Bullet;
import entitites.Enemy;
import entitites.Player;

public class GamePanel extends JPanel implements Runnable, KeyListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6814800263723295598L;
	
	public static int WIDTH = 640;
	public static int HEIGHT = 480;
	
	private Thread thread;
	private boolean running;
	
	private int FPS = 30;
	private double averageFPS;
	
	private BufferedImage image;
	private Graphics2D g;
	
	private Player player;
	public static ArrayList<Bullet> bullets;	
	public static ArrayList<Enemy> enemys;
	public static ArrayList<PowerUp> powerUps;
	
	private long waveStartTimer;
	private long waveStartTimerDiff;
	private int waveNumber;
	private boolean waveStart;
	private int waveDelay = 2000;
	
	// game loop things
	
	
	// CONSTRUCTOR
	public GamePanel()
	{
		super();
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setFocusable(true);
		requestFocus();
	}
	
	// FUNCTIONS
	
	public void addNotify(){
		super.addNotify();
		if(thread == null){
			thread = new Thread(this);
			thread.start();
		}
		addKeyListener(this);
	}
	

    public void run()
    {
        running = true;

        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        g = (Graphics2D) image.getGraphics();
        g.setRenderingHint(
        		RenderingHints.KEY_ANTIALIASING,
        		RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(
        		RenderingHints.KEY_TEXT_ANTIALIASING,
        		RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        player = new Player();
        bullets = new ArrayList<Bullet>();
        enemys = new ArrayList<Enemy>();
        powerUps = new ArrayList<PowerUp>();
        
        waveStartTimer = 0;
        waveStartTimerDiff = 0;
        waveStart = true;
        waveNumber = 0;
        
        long startTime;
        long URDTimeMillis;
        long waitTime;
        long totalTime = 0;

        int frameCount = 0;
        int maxFrameCount = 30;

        long targetTime = 1000 / FPS;
        


        // GAME LOOP
        while(running){

            startTime = System.nanoTime();

            gameUpdate();
            gameRender();
            gameDraw();

            URDTimeMillis = (System.nanoTime() - startTime) / 1000000;

            waitTime = targetTime - URDTimeMillis;

            try{
                Thread.sleep(waitTime);
            }catch(Exception e){
            }

            totalTime += System.nanoTime() - startTime;
            frameCount++;

            if(frameCount == maxFrameCount){
                averageFPS = 1000.0 / ((totalTime / frameCount) / 1000000);
                frameCount = 0;
                totalTime = 0;
            }

        }
    }
		
	
	private void gameUpdate()
	{
		// new wave
		
		if(waveStartTimer == 0 && enemys.size() == 0){
			waveNumber++;
			waveStart = false;
			waveStartTimer = System.nanoTime();
			
		}
		else{
			waveStartTimerDiff = (System.nanoTime() - waveStartTimer) / 1000000;
			if(waveStartTimerDiff > waveDelay){
				waveStart = true;
				waveStartTimer = 0;
				waveStartTimerDiff = 0;
			}
		}
		
		// create enemies
		
		if(waveStart && enemys.size() == 0){
			createNewEnemies();
		}
		
		
		// player update
		player.update();
		
		// bullet update
		for(int i = 0; i < bullets.size(); i++){
			boolean remove = bullets.get(i).update();
			if(remove){
				bullets.remove(i);
				i--;
			}
		}
		
		// enemy update
		for(int i = 0; i < enemys.size(); i++){
			enemys.get(i).update();
		}
		
		// bullet-enemy colision
		for(int i = 0; i < bullets.size(); i++){
			Bullet b = bullets.get(i);
			double bx = bullets.get(i).getX();
			double by = bullets.get(i).getY();
			double br = bullets.get(i).getR();
			
			for(int j = 0; j < enemys.size(); j++){
				Enemy e = enemys.get(j);
				double ex = enemys.get(j).getX();
				double ey = enemys.get(j).getY();
				double er = enemys.get(j).getR();
				
				double dx = bx - ex;
				double dy = by - ey;
				double dist = Math.sqrt(dx * dx + dy * dy);
				
				if(dist < br + er){
					e.hit();
					bullets.remove(i);
					i--;
					break;
				}
			}
		}
		
		// check dead enemies
		for(int i = 0; i < enemys.size(); i++){
			
			if(enemys.get(i).isDead()){
				
				Enemy e = enemys.get(i);
				
				// chance for powerup
				double rand = Math.random();
				if(rand < 0.001) powerUps.add(new PowerUp(1, e.getX(), e.getY()));
				else if(rand < 0.020) powerUps.add(new PowerUp(2, e.getX(), e.getY()));
				else if(rand < 0.120) powerUps.add(new PowerUp(3, e.getX(), e.getY()));
				
				player.addScore(e.getValue());
				enemys.remove(i);
				i--;
				
			}
		}
		
		// check player-enemy collision
		if(!player.isRecovering()){
			int px = player.getX();
			int py = player.getY();
			int pr = player.getR();
			
			for(int i = 0; i < enemys.size(); i++){
				Enemy e = enemys.get(i);
				double ex = e.getX();
				double ey = e.getY();
				double er = e.getR();
				
				double dx = px - ex;
				double dy = py - ey;
				double dist = Math.sqrt(dx * dx + dy * dy);
				
				if(dist < pr + er){
					player.loseLife();
				}
			}
		}
		
		// powerups update
		for(int i = 0; i < powerUps.size(); i++){
			boolean remove = powerUps.get(i).update();
			if(remove){
				powerUps.remove(i);
				i--;
			}
		}
	}
	
	
	
	private void gameRender()
	{
		// background draw
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		// player draw
		player.draw(g);
		
		// bullet draw
		for(int i = 0; i < bullets.size(); i++){
			bullets.get(i).draw(g);
		}
		
		// enemy draw
		for(int i = 0; i < enemys.size(); i++){
			enemys.get(i).draw(g);
		}
		
		// draw wave number (THIS IS AWESOME, DAMN)
		g.setFont(new Font("Century Gothic", Font.PLAIN, 30));
		String s = "- W A V E   " + waveNumber + "   -";
		int lenght = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
		int alpha = (int) (255 * Math.sin(3.14 * waveStartTimerDiff / waveDelay));
		if(alpha > 255) alpha = 255;
		g.setColor(new Color(255, 255, 255, alpha));
		g.drawString(s, WIDTH / 2 - lenght / 2, HEIGHT / 2);
				
		// player lives draw
		for(int i = 0; i < player.getLives(); i++){
			g.setColor(Color.RED);
			g.fillOval(20 + (20 * i), 13, player.getR() * 2, player.getR() * 2);
		}
		
		// score draw
		g.setColor(Color.WHITE);
		g.setFont(new Font("Century Gothic", Font.PLAIN, 14));
		g.drawString("Score: " + player.getScore(), WIDTH - 80, 20);
		
		// draw powerups
		for(int i = 0; i < powerUps.size(); i++){
			powerUps.get(i).draw(g);
		}
	}
	
	private void gameDraw()
	{	
		Graphics g2 = this.getGraphics();
		g2.drawImage(image, 0, 0, null);
		g2.dispose();
	}
	
	private void createNewEnemies(){
		enemys.clear();
		Enemy e;
		
		switch(waveNumber){
		case 1:
			for(int i = 0; i < 4; i++){
				enemys.add(new Enemy(1, 1));
			}
			break;
		case 2:
			for(int i = 0; i < 8; i++){
				enemys.add(new Enemy(1, 1));
			}
			break;
		case 3:
			for(int i = 0; i < 16; i++){
				enemys.add(new Enemy(1, 1));
			}
			break;
		case 4:
			for(int i = 0; i < 24; i++){
				enemys.add(new Enemy(1, 1));
			}
			break;
		}
	}
	
	// KEY FUNCTIONS
	
	@Override
	public void keyTyped(KeyEvent key) {
	}

	@Override
	public void keyPressed(KeyEvent key) {
		int keyCode = key.getKeyCode();
		if(keyCode == KeyEvent.VK_LEFT){
			player.setLeft(true);
		}
		else if(keyCode == KeyEvent.VK_RIGHT){
			player.setRight(true);
		}
		else if(keyCode == KeyEvent.VK_UP){
			player.setUp(true);
		}
		else if(keyCode == KeyEvent.VK_DOWN){
			player.setDown(true);
		}
		else if(keyCode == KeyEvent.VK_SPACE){
			player.setFiring(true);
		}	
	}

	@Override
	public void keyReleased(KeyEvent key) {
		int keyCode = key.getKeyCode();
		if(keyCode == KeyEvent.VK_LEFT){
			player.setLeft(false);
		}
		if(keyCode == KeyEvent.VK_RIGHT){
			player.setRight(false);
		}
		if(keyCode == KeyEvent.VK_UP){
			player.setUp(false);
		}
		if(keyCode == KeyEvent.VK_DOWN){
			player.setDown(false);
		}
		if(keyCode == KeyEvent.VK_SPACE){
			player.setFiring(false);
		}
		
	}
}
