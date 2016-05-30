package entitites;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import com.kibe.main.GamePanel;

public class Enemy {
	
	// FIELDS
	private double x;
	private double y;
	private int r;
	
	private double dx;
	private double dy;
	private double rad;
	private double speed;
	
	private int health;
	private int type;
	private int rank;
	private int value;
	
	private Color color1;
	
	private boolean ready;
	private boolean dead;
	
	private boolean hit;
	private long hitTimer;
	
	private boolean slow;
	
	// CONSTRUCTOR
	public Enemy(int type, int rank) {
		
		this.type = type;
		this.rank = rank;
		
		// default enemy
		if(type == 1) {
			color1 = Color.BLUE;
			if(rank == 1) {
				speed = 2;
				r = 5;
				health = 1;
				value = 5;
			}
			if(rank == 2) {
				speed = 2;
				r = 10;
				health = 2;
				value = 10;
			}
			if(rank == 3) {
				speed = 1.5;
				r = 20;
				health = 3;
				value = 15;
			}
			if(rank == 4) {
				speed = 1.5;
				r = 30;
				health = 4;
				value = 20;
			}	
		}
		
		x = Math.random() * GamePanel.WIDTH / 2 + GamePanel.WIDTH / 4;
		y = -r;
		
		double angle = Math.random() * 140 + 20;
		rad = Math.toRadians(angle);
		
		dx = Math.cos(rad) * speed;
		dy = Math.sin(rad) * speed;
		
		ready = false;
		dead = false;
		
		hit = false;
		hitTimer = 0;
		
	}
	
	// FUNCTIONS
	public double getX() { return x; }
	public double getY() { return y; }
	public int getR() { return r; }
	
	public int getValue() { return value; }
		
	public boolean isDead() { return dead; }
	
	public void hit() {
		health--;
		if(health <= 0) {
			dead = true;
		}
		hit = true;
		hitTimer = System.nanoTime();	
	}
	
	public void update() {
		x += dx;
		y += dy;
		
		if(!ready) {
			if(x > r && x < GamePanel.WIDTH - r &&
				y > r && y < GamePanel.HEIGHT - r) {
				ready = true;
			}
		}
		
		if(x < r && dx < 0) dx = -dx;
		if(y < r && dy < 0) dy = -dy;
		if(x > GamePanel.WIDTH - r && dx > 0) dx = -dx;
		if(y > GamePanel.HEIGHT - r && dy > 0) dy = -dy;
		
		long elapsed = (System.nanoTime() - hitTimer) / 1000000;
		if(elapsed > 50) {
			hit = false;
			hitTimer = 0;
		}
		
	}
	
	public void draw(Graphics2D g) {
		g.setColor(color1);
		g.fillOval((int) x - r, (int) y - r, 2 * r, 2 * r);
		
		g.setStroke(new BasicStroke(3));
		g.setColor(color1.darker());
		g.drawOval((int) x - r, (int) y - r, 2 * r, 2 * r);
	}
	
	
}

























