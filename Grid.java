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
	private final static int SIZE_50 = 50;	//variabili inutili passiamo direttamente il parametro
	private final static int SIZE_100 = 100;	// nell'actionlistener
	private final static int SIZE_200 = 200;
	private int xSize;
	private int ySize;
	static int size = 60;
	private Vector<Cell> snapShot = new Vector<Cell>();
	private Vector<Cell> changedCells = new Vector<Cell>();
	private int generation = 0;

	public Grid(){
		initCells();
		initFrame();
	}

	private void initFrame(){
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);
		xSize = (Cell.CELL_SIZE * size);
		ySize = (Cell.CELL_SIZE * size);
		setSize(xSize,ySize);
		//setResizable(false);
		addCellsToFrame();
		//initMenu();
		setVisible(true);
	}

	public void test(){
		for(int i = 0;i < size;i++)
			for(int j = 0;j < size;j++)
				((GridCell)cells[i][j]).changeNext();

		for(int i = 0;i < size;i++)
			for(int j = 0;j < size;j++)
				((GridCell)cells[i][j]).swap();

		nextGeneration();
		/*
		for(int i=25;i < 28;i++){
			((GridCell)cells[25][i]).bringToLife();
			//actualGeneration.add(cells[25][i]);
		}
			((GridCell)cells[26][27]).bringToLife();
			//actualGeneration.add(cells[26][27]);
			((GridCell)cells[27][26]).bringToLife();
			//actualGeneration.add(cells[27][26]);*/
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

	private void initCells(){ 
		cells = new Cell[size][size];
		for(int i = 0;i < size;i++ )
			for(int j = 0;j < size;j++)
				cells[i][j] = new GridCell(i,j);
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

		//	System.out.println("colonna "+i+" riga "+j);
		return cells[i%size][j%size];
	}

	public void changeState(Cell cell){
		changedCells.add(cell);
		((GridCell)cell).changeNext();
	}

	public void forceUpdate(){
		repaint();
	}

	public void swapNextGeneration(){

	}


	public boolean isLivingCell(Cell neighborCell) {
		return ((GridCell)neighborCell).isAliveNow();
	}

	/*public void incrementNumbOfN(Cell cell) {
		((GridCell)cell).increment();
	}

	public int getNumbOfN(Cell cell) {
		return ((GridCell)cell).get();
	}

	public void resetCell(Cell cell) {
		((GridCell)cell).reset();
	}	*/

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
		System.out.println(generation);
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

	public void clearGrid(){ //pesantissimo, bisogna pensare a qualcos'altro
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
	}

	public void resetGeneration() {
		snapShot = new Vector<Cell>();
		generation = 0;		
	}
}
