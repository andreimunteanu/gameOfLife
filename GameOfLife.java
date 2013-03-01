package it.univr.GameOfLife;

import java.util.Vector;

import javax.swing.JFrame;

public class GameOfLife extends JFrame {
  private final static int SIZE_1 = 50;
	private static final long serialVersionUID = 1L;
	private static Vector<Cell> actualGen = new Vector<Cell>();
	private static Vector<Cell> newGen = new Vector<Cell>();
	private static Vector<Cell> deadCells = new Vector<Cell>();
	static Cell[][] cells;
	static int size;
	public static void main(String[] args) {
		init();
	}
	private static void init(){
		int workingPos = 0;
		size = SIZE_1;
		cells = new Cell[size][size];
		for(int i = 0;i < size;i++ )
			for(int j = 0;j < size;j++)
				cells[i][j] = new Cell(i,j);
		
		while(true){
			for(Cell cell : actualGen){
				if(countAliveNeighbors(cell) == 2 || countAliveNeighbors(cell) == 3)
					newGen.add(cell);
			
				else
					deadCells.add(cell);					
			}	
		}
	}
	private static int countAliveNeighbors(Cell cell) {
		int x = cell.getX();
		int y = cell.getY();
		int count = 0;
		for(int i = -1; i < 2; i++)
			for(int j = -1;j < 2;j++)
				if((i != 0 || j != 0) && cells[Math.abs(i + x) % size][Math.abs(j + y) % size].getState())
					count++;
		return count;
	}
}
