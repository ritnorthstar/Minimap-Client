package com.northstar.minimap.map;

import com.northstar.minimap.Position;

public class Barrier {
	
	private Position pos;
	private int width;
	private int height;
	
	/**
	 * Constructor for Barrier
	 * @param pos A Position object containing the x,y coordinates of the top left corner of the table.
	 * @param width An int of how wide the tables are.
	 * @param height An int of how tall the tables are.
	 */
	public Barrier(Position pos, int width, int height){
		this.pos = pos;
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Method to obtain the Position of the barrier.
	 * @return A Position object containing the x,y coordinates of the top left position of the barrier.
	 */
	public Position getPosition(){
		return pos;
	}
	
	/**
	 * Method to obtain the width of the barrier.
	 * @return An int representing the width of the barrier.
	 */
	public int getWidth(){
		return width;
	}
	
	/**
	 * Method to obtain the height of the barrier. 
	 * @returnan An int representing the height of the barrier.
	 */
	public int getHeight(){
		return height;
	}
	
}
