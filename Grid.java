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
	private static int generation = 0;
	private final static boolean  ALIVE = true;
	private final static boolean DEAD = false;
	private final static int SIZE_50 = 50;	//variabili inutili passiamo direttamente il parametro
	private final static int SIZE_100 = 100;	// nell'actionlistener
	private final static int SIZE_200 = 200;
	private int xSize;
	private int ySize;
	static int size = 60;
	private Vector<Cell> actualGeneration = new Vector<Cell>();
	private Vector<Cell> snapShot = new Vector<Cell>();

	public Grid(Vector<Cell> actualGeneration){
		this.actualGeneration = actualGeneration;
		initCells();
		initFrame();
	}

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

	public void addCells(){ //solo LivingCells!
		synchronized(this){
			for(Cell cell : actualGeneration){
				int x = cell.auxGetX();
				int y = cell.auxGetY();
				remove(cells[x][y]);
				cells[x][y] = cell;
				add(cells[x][y]);
			}
		}
	}

	public void test(){
		for(int i = 0;i < size;i++)
			for(int j = 0;j < size;j++)
				((GridCell)cells[i][j]).bringToLife();
				
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

	private void initCells(){ 
		cells = new Cell[size][size];
		for(int i = 0;i < size;i++ )
			for(int j = 0;j < size;j++)
				cells[i][j] = new GridCell(i,j);
	}

	private void switchCell(Cell cell){
		synchronized(this){
			int x = cell.auxGetX();
			int y = cell.auxGetY();
			if(cells[x][y].isLivingCell()){
				kill(cell);
				//actualGeneration.remove(cell);
			}
			else{
				((GridCell)cell).bringToLife();
				//actualGeneration.add(cell);
			}
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

	public void kill(Cell cell) {
		((GridCell)cell).kill();
	}

	public void createLivingCell(Cell cell) {
		((GridCell)cell).bringToLife();
	}

	public void clearGrid() {
		System.out.println("Actual Generation = " + actualGeneration.size());
		for(Cell c : actualGeneration){
			System.out.println("REMOVING => " + "(" + c.auxGetX() + ", " + c.auxGetY() + ")");
			kill(c);
		}	
		actualGeneration = new Vector<Cell>();
		//forceUpdate();
	}

	public void setActualGeneration(Vector<Cell> actualGeneration) {
		this.actualGeneration = actualGeneration;
	}

	public Vector<Cell> getActualGeneration() {
		return actualGeneration;
	}

	public void addCell(Cell cell){// dprecato
		add(cell);

	}
	public void removeCell(Cell cell){//deprecato2
		remove(cell);
	}

	public void forceUpdate(){
		repaint();
	}

	public boolean isLivingCell(Cell neighborCell) {
		return ((GridCell)neighborCell).isLivingCell();
	}
	public boolean isLivingCell1(Cell neighborCell) {
		return ((GridCell)neighborCell).isLivingCell1();
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
		generation = (generation + 1) % 2;
	}

	private class GridCell extends Cell{
		private boolean generation_0 = DEAD;
		private boolean generation_1 = DEAD;
		private static final long serialVersionUID = 1L;
		protected GridCell(int x, int y) {
			super(x, y);
			setBackground(Color.BLACK);
			this.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					Grid.this.switchCell(GridCell.this);
					System.out.println("("+ isLivingCell()+") CLICK ON => " + "(" + GridCell.this.auxGetX() + ", " + GridCell.this.auxGetY() + ")");
				}
			});
		}		
		private void kill(){//NB gli actionListener Lavoro su actual generation mentre le thread lavorano su quella futura 
			if(generation == 0)
				generation_0 = DEAD;
			else
				generation_1 = DEAD;
			
			setBackground(Color.BLACK);
				
		}
		private void bringToLife(){
			if(generation == 0)
				generation_0 = ALIVE;
			else
				generation_1 = ALIVE;
			
			setBackground(new Color(192,249,242));
		}
		@Override 
		public boolean isLivingCell(){
			return generation == 1 ? generation_0 : generation_1;
		}
		public boolean isLivingCell1(){
			return generation == 0 ? generation_0 : generation_1;
		}
	}

	public void saveSnapShot() {
		snapShot = actualGeneration;
	}

	public void loadSnapShot(){
		clearGrid();
		actualGeneration = snapShot;
		addCells();
	}
}
