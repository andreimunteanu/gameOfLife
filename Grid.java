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
	private Cell[][] cells;
	private final static boolean ALIVE = true;
	private final static boolean DEAD = false;
	private int xSize;
	private int ySize;
	private int size = 60; // default
	private Vector<Cell> snapShot = new Vector<Cell>();
	private Vector<Cell> changedCells = new Vector<Cell>();
	private int generation = 0;

	public Grid(int size){
		this.size = size;
		initCells();
		initFrame();
	}
	
	public void setGridSize(int newSize){
		//serve un metodo per uccidere le cellule vive fuori dal frame
		//quando si fa il resize in diminuire
		size = newSize;
		xSize = (Cell.CELL_SIZE * size);
		ySize = (Cell.CELL_SIZE * size);
		setSize(xSize,ySize);
	}

	private void initCells(){ 
		cells = new Cell[size][size];
		for(int i = 0;i < size;i++ )
			for(int j = 0;j < size;j++)
				cells[i][j] = new GridCell(i,j);
	}
	
	private void initFrame(){
		setLayout(null);
		xSize = (Cell.CELL_SIZE * size);
		ySize = (Cell.CELL_SIZE * size);
		setSize(xSize,ySize);
		addCellsToFrame();
		setVisible(true);
	}	
	
	private void addCellsToFrame() {
		for(int i = 0;i < size;i++)
			for(int j = 0;j < size;j++){
				add(cells[i][j]);
			}
	}

	private void removeCellsFromFrame(){
		for(int i = 0;i < size;i++)
			for(int j = 0;j < size;j++){
				remove(cells[i][j]);
			}		
	}

	private void switchCell(Cell cell){
		synchronized(this){
			((GridCell)cell).changeNow();
			forceUpdate();
		}
	}

	public Cell getCell(int i, int j) {
		if(i == -1)
			i = size-1;
		if(j == -1)
			j = size -1;

		return cells[i%size][j%size];
	}

	public void changeState(Cell cell){
		changedCells.add(cell);
		((GridCell)cell).changeNext();
	}

	public void forceUpdate(){
		repaint();
	}


	public boolean isLivingCell(Cell neighborCell) {
		return ((GridCell)neighborCell).isAliveNow();
	}

	public int getXSize(){
		return xSize;
	}

	public int getYSize(){
		return ySize;
	}

	public int getGridSize(){
		return size;
	}

	public void nextGeneration(){
		generation++;

		if(generation == 1){
			System.out.println("Sono1");
			for(Cell[] c : cells){
				for(Cell cell : c)
					if(((GridCell)cell).isAliveNow())
						snapShot.add(cell);
			}
		}

		synchronized(this){
			for(Cell cell : changedCells)
				((GridCell)cell).swap();
		}

		changedCells = new Vector<Cell>();
		forceUpdate();
	}

	private class GridCell extends Cell{
		private boolean actualGeneration = DEAD;
		private boolean nextGeneration = DEAD;
		private static final long serialVersionUID = 1L;
		protected GridCell(int x, int y) {
			super(x, y);
			setBackground(Color.BLACK);
			this.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					Grid.this.switchCell(GridCell.this);
					System.out.println("("+ isAliveNow()+") CLICK ON => " + "(" + GridCell.this.auxGetX() + ", " + GridCell.this.auxGetY() + ")");
				}
			});
		}		

		private void changeNow(){
				nextGeneration = (actualGeneration)?DEAD:ALIVE;
				actualGeneration = nextGeneration;
				setBackground((actualGeneration)?Color.WHITE:Color.BLACK);
		}

		private void changeNext(){
			nextGeneration = (actualGeneration)?DEAD:ALIVE;
		}

		@Override
		public boolean isAliveNow(){
			return actualGeneration;
		}

		@Override
		public boolean isAliveNext(){
			return nextGeneration;
		}
		@Override
		public void swap() {//fai modifica solo se necessario
			if(nextGeneration != actualGeneration){
				actualGeneration = nextGeneration;
				setBackground((nextGeneration)?Color.WHITE:Color.BLACK);	
			}
		}

		@Override
		public void reset(){
			actualGeneration = DEAD;
			setBackground(Color.BLACK);
		}

	}

	public void clearGrid(){ 
		for(int i = 0;i < size;i++)
			for(int j = 0;j < size;j++)
				reset(cells[i][j]);
	}

	private void reset(Cell cell){
		if(cell.isAliveNow())
			cell.reset();
	}


	public void loadSnapShot(){
		clearGrid();
		for(Cell cell : snapShot){
			int x = cell.auxGetX();
			int y = cell.auxGetY();
			((GridCell)cells[x][y]).changeNow();
		}
		resetGeneration();
	}

	public void resetGeneration() {
		snapShot = new Vector<Cell>();
		generation = 0;		
	}

	public int getGeneration() {
		return generation;
	}
}


