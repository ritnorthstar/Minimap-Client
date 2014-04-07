package com.northstar.minimap.map;

import com.northstar.minimap.Position;

public class Table {
	
	private int widthSubdivisions;
	private int heightSubdivisions;
	private Position pos;
	private int width;
	private int height;
	
	/**
	 * Constructor for Table
	 * @param widthSubdivisions An int of width subdivisions.
	 * @param heightSubdivisions An int of height subdivisions.
	 * @param pos A Position object containing the x,y coordinates of the top left corner of the table.
	 * @param width An int of how wide the tables are.
	 * @param height An int of how tall the tables are.
	 */
	public Table(int widthSubdivisions, int heightSubdivisions, Position pos, int width, int height){
		this.widthSubdivisions = widthSubdivisions;
		this.heightSubdivisions = heightSubdivisions;
		this.pos = pos;
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Method to obtain the number of width subdivisions
	 * @return An int representing number of width subdivisions
	 */
	public int getWidthSubdivisions(){
		return widthSubdivisions;
	}
	
	/**
	 * Method to obtain the number of height subdivisions
	 * @return An int representing number of height subdivisions
	 */
	public int getHeightSubdivisions(){
		return heightSubdivisions;
	}
	/**
	 * Method to obtain the Position of the table
	 * @return A Position object containing the x,y coordinates of the top left position of the table
	 */
	public Position getPosition(){
		return pos;
	}
	
	/**
	 * Method to obtain the width of the table.
	 * @return An int representing the width of the tables.
	 */
	public int getWidth(){
		return width;
	}
	
	/**
	 * Method to obtain the height of the table. 
	 * @returnan An int representing the height of the table.
	 */
	public int getHeight(){
		return height;
	}
	
}
