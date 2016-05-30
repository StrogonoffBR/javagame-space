package com.kibe.main;

import java.awt.*;

public class PowerUp {
	// FIELDS
	private double x;
	private double y;
	private int r;
	
	private Color color1;
	
	private int type;
	
	/* -- POWER-UPS TYPES:
	 * 1 -> +1 life
	 * 2 -> +1 power
	 * 3 -> +2 power
	 */
	
	// CONSTRUCTOR
	public PowerUp(int type, double x, double y){
		this.type = type;
		this.x = x;
		this.y = y;
		
		switch(type){
		case 1:
			color1 = Color.PINK;
			r = 2;
			break;
		case 2:
			color1 = Color.YELLOW;
			r = 2;
			break;
		case 3:
			color1 = Color.GREEN;
			r = 3;
			break;
		}
		
	}
	
	// FUNCTIONS
	public double getX() { return x; }
	public double getY() { return y; }
	public double getR() { return r; }
	public int getType() { return type; }
	
	public boolean update(){
		y += 2;
		
		if(y > GamePanel.HEIGHT + r){
			return true;
		}
		
		return false;
	}
	
	public void draw(Graphics2D g){
		g.setColor(color1);
		g.fillOval((int) (x - r), (int) (y - r), 2 * r, 2 * r);
		
		g.setStroke(new BasicStroke(3));
		g.setColor(color1.darker());
		g.drawOval((int) (x - r), (int) (y - r), 2 * r, 2 * r);
		g.setStroke(new BasicStroke(1));

	}
}
