import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.JPanel;

/**
 * The Grid class implements a resizable frame that contains a size*size number of 
 * cells, each of them with a unique position. It provides methods to load a saved 
 * generation and interact with the contained cells in order to check or change
 * their state and killing them definitely.
 * 
 * @author <A HREF="mailto:niccolo.marastoni@studenti.univr.it">Niccolò Marastoni</A>
 * @author <A HREF="mailto:andrei.munteanu@studenti.univr.it">Andrei Munteanu</A>
 * @version 1.0
 * 
 */

public class Grid extends JPanel{

	/*
	 * Array that contains size x size cells. 
	 */
	private Cell[][] cells;

	/*
	 * The possible states of the cells.
	 */
	private final static boolean ALIVE = true;
	private final static boolean DEAD = false;

	/*
	 * Toggles the "killing" mode, where cells are set as definitely dead, as per assignment.
	 */
	private static boolean killing = false;

	/*
	 * Toggles the "adding figure" mode.
	 * If true the game waits for the user to position the figure.
	 */
	private static boolean addingFigure = false;

	/*
	 * Toggles the "debug" mode.
	 * If true prints debugging information to the standard output.
	 */
	private boolean debug = false;

	/*
	 * The name of the figure that can be printed on the grid.
	 * A figure is a specific number of alive cells which have specified position.
	 */
	private static String figureName ="";

	/*
	 * The sizes of the grid.
	 */
	private int xSize;
	private int ySize;

	/*
	 * Dimensions of the array.
	 * Number of the game's cells: size*size.
	 */
	private int size;

	/*
	 * list of living cells saved each time the user presses "Start".
	 */
	private Vector<Cell> snapShot = new Vector<Cell>();

	/*
	 * list of cells whose state has been changed by the game engine.
	 */
	private Vector<Cell> changedCells = new Vector<Cell>();

	/*
	 * number of the generation since "Start" has been pressed.
	 */
	private int generation = 0;

	/*
	 * checks if it's the first time that the user is saving a file.
	 */
	private boolean firstSave = true;
	
	/*
	 * checks if any new file was saved.
	 */
	private boolean saved = true;
	
	/*
	 * name of the directory where the save files will be stored.
	 */
	private final String saveDir = "saved";

	/**
	 * Constructs a frame based on the integer variable "size", with a specified number of cell, each one initialized as a dead cell.
	 * 
	 * @param size
	 * 				size of the frame
	 */
	public Grid(int size){
		this.size = size;
		initCells();
		initFrame();
	}

	/*
	 * Initializes each cell on the array cells.
	 */
	private void initCells(){ 
		cells = new Cell[size][size];
		for(int i = 0;i < size;i++ )
			for(int j = 0;j < size;j++)
				cells[i][j] = new GridCell(i,j);
	}

	/*
	 * Initializes the size of the grid based on the number of cells and their dimension.
	 */
	private void initFrame(){
		setLayout(null);
		xSize = (Cell.CELL_SIZE * size);
		ySize = (Cell.CELL_SIZE * size);
		setSize(xSize,ySize);
		addCellsToFrame();
		setVisible(true);
	}	

	/* 
	 * Adds the cells to the grid.
	 */
	private void addCellsToFrame() {
		for(int i = 0;i < size;i++)
			for(int j = 0;j < size;j++){
				add(cells[i][j]);
			}
	}

	/**
	 * Sets the grid size based on a new integer variable.
	 * 
	 * @param newSize
	 * 				new size of the grid
	 * 					
	 */
	public void setGridSize(int newSize){
		size = newSize;
		xSize = (Cell.CELL_SIZE * size);
		ySize = (Cell.CELL_SIZE * size);
		setSize(xSize,ySize);
	}

	/**
	 * Returns the size of the grid on the x axis.
	 * 
	 * @return
	 * 				xSize
	 */
	public int getXSize(){
		return xSize;
	}

	/**
	 * Returns the size of the grid on the y axis.
	 * 
	 * @return
	 * 				ySize
	 */
	public int getYSize(){
		return ySize;
	}

	/**Returns the size of the array of cells.
	 * 
	 * @return
	 * 				size
	 */
	public int getGridSize(){
		return size;
	}

	/**
	 * Returns a cell from the grid in a specified position.
	 * 
	 * @param i
	 * 				column
	 * @param j
	 * 				row
	 * @return
	 * 				the cell in the position i,j 
	 */
	public Cell getCell(int i, int j) {
		return cells[(i+size)%size][(j+size)%size];
	}

	/*	
	 * Switches the state of the variable cell between dead or alive in the actual generation.
	 * Used for the mouse input.
	 * 
	 * @param 
	 *				cell on the grid
	 */
	private void switchCell(Cell cell){
		synchronized(this){
			cell.changeNow();
			forceUpdate();
		}
	}

	/**
	 * Changes the state of a cell in the future generation.
	 * used by the game engine.
	 * 
	 * @param cell
	 * 				cell on the grid
	 */
	public void changeState(Cell cell){
		changedCells.add(cell);
		((GridCell)cell).changeNext();
	}

	/**
	 * Repaints the frame.
	 */
	public void forceUpdate(){
		repaint();
	}

	/**
	 * Tests if a cell is alive.
	 * 
	 * @param 
	 * 				cell on the grid
	 * @return
	 * 				true if the cell is alive in the actual generation
	 */
	public boolean isLivingCell(Cell cell) {
		return cell.isAliveNow();
	}

	/**
	 * Changes the state of the cells to the next generation state.
	 * if it's the first generation, it makes a list of the living cells to be used later by the reset button
	 * 
	 */
	public void nextGeneration(){
		generation++;

		if(generation == 1){
			saveSnapshot();
		}

		synchronized(this){
			for(Cell cell : changedCells)
				cell.swap();
		}

		changedCells = new Vector<Cell>();
		forceUpdate();
	}

	/*
	 * The GridCell class implements all the methods of the class Cell.
	 * Provides an action listener which allows to changes the cell's state: dead, alive or definitely Killed. 
	 * 
	 */
	private class GridCell extends Cell{

		/*
		 * If true the cell is definitely killed.
		 */
		private boolean definitelyDead = false;

		/*
		 * State in the actual generation. 
		 */
		private boolean actualGeneration = DEAD;

		/*
		 * State in the next generation.
		 */
		private boolean nextGeneration = DEAD;
		private static final long serialVersionUID = 1L;

		/**
		 * Constructs a cell in a specified position on the grid.
		 * Initialized as dead.
		 * 
		 * @param x
		 * 			column on the grid
		 * @param y
		 * 			row on the grid
		 */
		protected GridCell(int x, int y) {
			super(x, y);
			setBackground(Color.BLACK);
			this.addActionListener(new ActionListener(){
				/**
				 * Allows interaction in "adding figure" or "killing" mode or simply changes the state of the cell.
				 * 
				 * 
				 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
				 */
				@Override
				public void actionPerformed(ActionEvent e) {
					if(addingFigure)
						printFigure(GridCell.this);
					else if(killing){
						GridCell.this.definitelyDead = true;
						GridCell.this.actualGeneration = DEAD;
						GridCell.this.setBackground(Color.RED);
					}
					else{
						if(GridCell.this.definitelyDead)
							GridCell.this.definitelyDead = false;
						Grid.this.switchCell(GridCell.this);
					}
					if(debug)
						System.out.println("(AliveNow = "+ isAliveNow()+") CLICK ON => " + GridCell.this);
				}
			});
		}

		/**
		 * Changes the cell's state in the actual generation.
		 */
		public void changeNow(){
			nextGeneration = (actualGeneration)?DEAD:ALIVE;
			actualGeneration = nextGeneration;
			setBackground((actualGeneration)?Color.WHITE:Color.BLACK);
		}

		/**
		 * Changes the cell's state in the next generation.
		 */
		@Override
		public void changeNext(){
			nextGeneration = (actualGeneration)?DEAD:ALIVE;
		}

		/**
		 * Tests if the cell is alive in the actual generation.
		 */
		@Override
		public boolean isAliveNow(){
			return actualGeneration;
		}

		/**
		 * Swaps the cell's state from the next generation to the actual.
		 */
		@Override
		public void swap() {//fai modifica solo se necessario
			if(nextGeneration != actualGeneration){
				actualGeneration = nextGeneration;
				setBackground((nextGeneration)?Color.WHITE:Color.BLACK);	
			}
		}

		/**
		 * Reinitializes the cell's state: dead and not definitely dead.
		 */
		@Override
		public void reset(){
			if(definitelyDead)
				definitelyDead = false;
			actualGeneration = DEAD;
			setBackground(Color.BLACK);
		}

		/**
		 * Tests if the cell is definitely dead.
		 */
		@Override
		public boolean isDefDead(){
			return definitelyDead;
		}

	}

	/**
	 * Sets the figure that will be printed on the grid based on a string variable.
	 * A figure is a variable number of alive cells with a specific position.
	 * 
	 * @param figureName
	 * 				name of the figure to print
	 */
	public void setFigure(String figureName){
		addingFigure = true;
		killing = false;
		this.figureName = figureName;
	}

	/**
	 * Brings to life a variable number of cells starting from an initial cell. 
	 * 
	 * @param cell
	 * 				initial cell
	 */				
	public void printFigure(Cell cell){
		addingFigure = false;
		int x = cell.auxGetX();
		int y = cell.auxGetY();
		int posX = 0;
		int posY = 0;

		int[][] coordinates = Figures.getCoordinates(figureName);
		for(int[] pos : coordinates){
			posX = pos[0];
			posY = pos[1];
			cell = getCell(x + posX, y + posY );
			cell.reset();
			cell.changeNow();
		}
	}

	/**
	 * Tests if the grid is waiting for a figure's position.
	 * 
	 * @return
	 * 				true if the grid is waiting for a position
	 */				
	public boolean isAddingFigure(){
		return addingFigure;
	}

	/**
	 * Stops the "waiting for figure's position" mode of the grid.
	 * 
	 */
	public void stopAddingFigure(){
		addingFigure = false;
	}

	/**
	 * Enables or disables the killing cell mode.
	 * A killed cell is definitely dead.
	 * 
	 */
	public void setKilling(){
		killing = !killing;
	}

	/**
	 * Reinitializes the grid's cells. 
	 * 
	 */
	public void clearGrid(){ 
		for(int i = 0;i < size;i++)
			for(int j = 0;j < size;j++)
				reset(cells[i][j]);
	}

	/*
	 * Resets a specified cell's state(dead).
	 * 
	 * @param cell
	 * 				cell to reset
	 */	
	private void reset(Cell cell){
		if(cell.isAliveNow() || cell.isDefDead())
			cell.reset();
	}

	/**
	 * Loads the first generation's cells to the grid. 
	 * 
	 */
	public void loadSnapShot(){
		clearGrid();
		for(Cell cell : snapShot){
			int x = cell.auxGetX();
			int y = cell.auxGetY();
			((GridCell)cells[x][y]).changeNow();
		}
		resetGeneration();
	}

	/**
	 * Resets the number of generations and clears the list of the alive cells of the first generation. 
	 * 
	 */
	public void resetGeneration() {
		snapShot = new Vector<Cell>();
		generation = 0;		
	}

	/**
	 * Returns the number of generations.
	 * 
	 * @return
	 * 				generations number
	 */
	public int getGeneration() {
		return generation;
	}

	/**
	 * Enables or disables the "debug mode".
	 * 
	 */
	public void toggleDebug(){
		debug = !debug;
	}
	
	/**
	 * Saves a snapshot of all living cells in a vector, which can then be saved to disk in a text file
	 * or be used to reset the cells to a previous state.
	 */
	public void saveSnapshot() {
		snapShot = new Vector<Cell>();
		for(Cell[] c : cells){
			for(Cell cell : c)
				if(cell.isAliveNow())
					snapShot.add(cell);
		}
	}
	
	/**
	 * Used by GameOfLife to keep the "load" button updated with the latest save files.
	 * 
	 * @return 
	 *			true if the state has been saved, false otherwise
	 */
	public boolean checkSaved() {
		boolean temp = saved;
		saved = false;
		return temp;
	}
	
	/**
	 * Return a string which contains the name of the directory of the saved files. 
	 * 
	 * @return
	 * 			the save directory's name
	 */	
	public String getSaveDir(){
		return saveDir;
	}
	
	/**
	 * Loads from a specific path, contained in a string, a file
	 * which contains a specific generation of cells with a specified position.
	 * 
	 * @param path
	 * 				position of the files 	
	 */
	public void loadFromDisk(String path){
		snapShot = new Vector<Cell>();
		Scanner scanner;
		int x = 0;
		int y = 0;
		int i = 0;
		
		try {
			scanner = new Scanner(new File(path));
			while(scanner.hasNextInt()){
				if(i % 2 == 0)
					x = scanner.nextInt();
				else{
					y = scanner.nextInt();
					Cell c = new GridCell(x,y);
					snapShot.add(c);
				}
				i++;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		loadSnapShot();
	}
	
	/**
	 * Saves the actual generation to the disk in the directory saveDir
	 * located in the game's working directory.
	 */
	public void saveToDisk() {
		if(snapShot.isEmpty()){
			return;
		}
		if(firstSave){
			File dir = new File(saveDir);
			dir.mkdirs();
			firstSave = false;
		}

		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		String fileName = "" + saveDir + "/" + dateFormat.format(date) + "" + System.currentTimeMillis() + ".txt";

		try {
			FileWriter out = new FileWriter(fileName);
			for(Cell c : snapShot)
				out.write(c.auxGetX() + " " + c.auxGetY() + " ");
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		saved = true;
	}
}
