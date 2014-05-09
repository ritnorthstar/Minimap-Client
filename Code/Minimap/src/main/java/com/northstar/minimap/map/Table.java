package com.northstar.minimap.map;

import com.northstar.minimap.Position;

public class Table extends Barrier{
	
	private int widthSubdivisions;
	private int heightSubdivisions;
	
	/**
	 * Constructor for Table
	 * @param widthSubdivisions An int of width subdivisions.
	 * @param heightSubdivisions An int of height subdivisions.
	 * @param pos A Position object containing the x,y coordinates of the top left corner of the table.
	 * @param width An int of how wide the tables are.
	 * @param height An int of how tall the tables are.
	 */
	public Table(int widthSubdivisions, int heightSubdivisions, Position pos, int width, int height){
		super(pos, width, height);
		this.widthSubdivisions = widthSubdivisions;
		this.heightSubdivisions = heightSubdivisions;
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
	
}
