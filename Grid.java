import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;


public class Grid extends JPanel{
	/*
	 * 
	 */
	private Cell[][] cells;
	private final static boolean ALIVE = true;
	private final static boolean DEAD = false;
	private static boolean killing = false;
	private static boolean addingFigure = false;
	private boolean debug = false;
	private static String figureName ="";
	private int xSize;
	private int ySize;
	private int size = 60; // default
	private Vector<Cell> snapShot = new Vector<Cell>();
	private Vector<Cell> changedCells = new Vector<Cell>();
	private int generation = 0;
	
	/**Constructs a frame based on integer variable "size", with a specified number of cell, each one initialized as a dead cell.
	 * 
	 * @param size
	 * 				size of the frame
	 */
	public Grid(int size){
		this.size = size;
		initCells();
		initFrame();
	}
	
	/**Sets the grid size based on integer variable.
	 * 
	 * @param newSize
	 * 				new size of the grid
	 * 					
	 */
	public void setGridSize(int newSize){
		//serve un metodo per uccidere le cellule vive fuori dal frame
		//quando si fa il resize in diminuire
		size = newSize;
		xSize = (Cell.CELL_SIZE * size);
		ySize = (Cell.CELL_SIZE * size);
		setSize(xSize,ySize);
	}

	/*Initializes each cell on the array cells.
	 * 
	 */
	private void initCells(){ 
		cells = new Cell[size][size];
		for(int i = 0;i < size;i++ )
			for(int j = 0;j < size;j++)
				cells[i][j] = new GridCell(i,j);
	}
	
	/*Initializes the size off the cell and adds all the cells on it.
	 * 
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
	 * Adds the cells to the grid
	 */
	private void addCellsToFrame() {
		for(int i = 0;i < size;i++)
			for(int j = 0;j < size;j++){
				add(cells[i][j]);
			}
	}
	
	/*
	private void removeCellsFromFrame(){ la usiamo??????
		for(int i = 0;i < size;i++)
			for(int j = 0;j < size;j++){
				remove(cells[i][j]);
			}		
	}*/
	
	/*	Switches the state of the variable cell between dead or alive.
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

	/**Returns a cell from the grid in a specified position.
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

	/**Changes the state of a cell in the future generation.
	 * 
	 * @param cell
	 * 				cell on the grid
	 */
	public void changeState(Cell cell){
		changedCells.add(cell);
		((GridCell)cell).changeNext();
	}
	
	/**Repaints the frame.
	 * 
	 */
	public void forceUpdate(){
		repaint();
	}
	
	/**Tests if a cell is alive.
	 * 
	 * @param 
	 * 				cell on the grid
	 * @return
	 * 				true if the cell is alive in the actual generation
	 */
	public boolean isLivingCell(Cell cell) {
		return cell.isAliveNow();
	}
	
	/**Returns an integer which is the xSize of the grid.
	 * 
	 * @return
	 * 				xSize
	 */
	public int getXSize(){
		return xSize;
	}
	
	/**Returns an integer which is the ySize of the grid.
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
	
	/**Changes the state of the cells to the next generation state;
	 * if is the first generation, makes a list of the living cell.
	 * 
	 */
	public void nextGeneration(){
		generation++;

		if(generation == 1){
			for(Cell[] c : cells){
				for(Cell cell : c)
					if(cell.isAliveNow())
						snapShot.add(cell);
			}
		}

		synchronized(this){
			for(Cell cell : changedCells)
				cell.swap();
		}

		changedCells = new Vector<Cell>();
		forceUpdate();
	}
	
	/**
	 * 
	 * 
	 * 
	 */
	private class GridCell extends Cell{
		
		/*
		 * 
		 */
		private boolean definitelyDead = false;
		private boolean actualGeneration = DEAD;
		private boolean nextGeneration = DEAD;
		private static final long serialVersionUID = 1L;
		
		/**
		 * 
		 * @param x
		 * @param y
		 */
		protected GridCell(int x, int y) {
			super(x, y);
			setBackground(Color.BLACK);
			this.addActionListener(new ActionListener(){
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
		
		/*
		 * 
		 */
		public void changeNow(){
			nextGeneration = (actualGeneration)?DEAD:ALIVE;
			actualGeneration = nextGeneration;
			setBackground((actualGeneration)?Color.WHITE:Color.BLACK);
		}
		
		/*
		 * 
		 */
		private void changeNext(){
			nextGeneration = (actualGeneration)?DEAD:ALIVE;
		}
		
		/**
		 * 
		 */
		@Override
		public boolean isAliveNow(){
			return actualGeneration;
		}
		
		/*
		 * 
		 */
		@Override
		public boolean isAliveNext(){
			return nextGeneration;
		}
		
		/*
		 * 
		 */
		@Override
		public void swap() {//fai modifica solo se necessario
			if(nextGeneration != actualGeneration){
				actualGeneration = nextGeneration;
				setBackground((nextGeneration)?Color.WHITE:Color.BLACK);	
			}
		}
		
		/*
		 * 
		 */
		@Override
		public void reset(){
			if(definitelyDead)
				definitelyDead = false;
			actualGeneration = DEAD;
			setBackground(Color.BLACK);
		}
		
		/*
		 * 
		 */
		@Override
		public boolean isDefDead(){
			return definitelyDead;
		}

	}
	
	/**Sets the figure that will be printed on the grid based on a string variable.
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
	
	/**Brings alive a variable number of cells starting from an initial cell. 
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
	
	/**Tests if the grid is waiting for figure's position.
	 * 
	 * @return
	 * 				true if the grid is waiting for a position
	 */				
	public boolean isAddingFigure(){
		return addingFigure;
	}
	
	/**Stops the "waiting for figure's position" mode of the grid.
	 * 
	 */
	public void stopAddingFigure(){
		addingFigure = false;
	}
	
	/**Enables or disables the killing cell mode.
	 * A killed cell is definitely dead.
	 * 
	 */
	public void setKilling(){
		killing = !killing;
	}
	
	/**Reinitializes the grid's cells. 
	 * 
	 */
	public void clearGrid(){ 
		for(int i = 0;i < size;i++)
			for(int j = 0;j < size;j++)
				reset(cells[i][j]);
	}
	
	/**Resets a specified cell's state(dead).
	 * 
	 * @param cell
	 * 				cell to reset
	 */	
	private void reset(Cell cell){
		if(cell.isAliveNow() || cell.isDefDead())
			cell.reset();
	}

	/**Loads the first genertion's cells to the grid. 
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
	
	/**Resets the number of generations and clears the list of the alive cells of the first generation.
	 * 
	 * 
	 */
	public void resetGeneration() {
		snapShot = new Vector<Cell>();
		generation = 0;		
	}
	
	/**Returns an integer which is the number of generations.
	 * 
	 * @return
	 * 				generations number
	 */
	public int getGeneration() {
		return generation;
	}
	
	/**Enables or disables the "debug mode".
	 * 
	 */
	public void toggleDebug(){
		debug = !debug;
		}
}
