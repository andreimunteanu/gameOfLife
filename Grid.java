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
	private final static int SIZE_50 = 50;	//variabili inutili passiamo direttamente il parametro
	private final static int SIZE_100 = 100;	// nell'actionlistener
	private final static int SIZE_200 = 200;
	private int xSize;
	private int ySize;
	static int size = SIZE_50;
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

	public void addCells(Vector<Cell> newCells){ //solo LivingCells!
		synchronized(this){
			for(Cell cell : newCells){
				int x = cell.auxGetX();
				int y = cell.auxGetY();
				remove(cells[x][y]);
				cells[x][y] = cell;
				actualGeneration.add(cells[x][y]);
				add(cells[x][y]);
			}
		}
	}

	public void test(){
		for(int i=25;i < 28;i++){
			remove(cells[25][i]);
			cells[25][i] = new LivingCell(25,i);
			actualGeneration.add(cells[25][i]);
			add(cells[25][i]);
		}
	}

	private void addCellsToFrame() {
		for(int i = 0;i < size;i++)
			for(int j = 0;j < size;j++){
				add(cells[i][j]);
			}
	}

	private void initCells(){ // da togliere
		cells = new Cell[size][size];
		for(int i = 0;i < size;i++ )
			for(int j = 0;j < size;j++)
				cells[i][j] = new DeadCell(i,j);
	}

	private void switchCell(Cell cell){
		synchronized(this){
			int x = cell.auxGetX();
			int y = cell.auxGetY();
			remove(cell);
			if(cell instanceof LivingCell){
				cells[x][y] = new DeadCell(x,y);
				actualGeneration.remove(cell);
			}
			else{
				cells[x][y] = new LivingCell(x,y);
				actualGeneration.add(cells[x][y]);
			}
			add(cells[x][y]);
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
		int x = cell.auxGetX();
		int y = cell.auxGetY();
		removeCell(cell);
		cells[x][y] = new DeadCell(x,y);
		addCell(cells[x][y]);
	}

	public Cell createLivingCell(Cell cell) {
		int x=cell.auxGetX();
		int y=cell.auxGetY();
		removeCell(cell);
		cells[x][y] = new LivingCell(x,y);
		addCell(cells[x][y]);
		return cells[x][y];
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

	public void addCell(Cell cell){
		add(cell);

	}
	public void removeCell(Cell cell){
		remove(cell);
	}

	public void forceUpdate(){
		repaint();
	}

	public boolean isLivingCell(Cell neighborCell) {
		return neighborCell instanceof LivingCell;
	}

	public void incrementNumbOfN(Cell cell) {
		((DeadCell)cell).increment();
	}

	public int getNumbOfN(Cell cell) {
		return ((DeadCell)cell).get();
	}

	public void resetCell(Cell cell) {
		((DeadCell)cell).reset();
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

	private class DeadCell extends Cell{
		private Integer numberOfN = 0;
		private static final long serialVersionUID = 1L;
		protected DeadCell(int x, int y) {
			super(x, y, "deadCell.gif");
			setBackground(Color.BLACK);
			this.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					Grid.this.switchCell(DeadCell.this);
					System.out.println("(DEAD) CLICK ON => " + "(" + DeadCell.this.auxGetX() + ", " + DeadCell.this.auxGetY() + ")");
				}
			});
		}
		private synchronized void increment(){
			numberOfN++;
		}
		private synchronized int get(){
			return numberOfN;
		}
		private synchronized void reset(){
			numberOfN = 0;
		}
	}

	private class LivingCell extends Cell{
		private static final long serialVersionUID = 1L;
		public LivingCell(int x, int y) {
			super(x,y,"nocell.gif");
			setBackground(new Color(192,249,242));
			this.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					Grid.this.switchCell(LivingCell.this);
					System.out.println("(LIVING) CLICK ON => " + "(" + LivingCell.this.auxGetX() + ", " + LivingCell.this.auxGetY() + ")");
				}
			});
		}
	}

	public void saveSnapShot() {
		snapShot = actualGeneration;
	}

	public void setSnapShot(){
		initCells();
		addCells(snapShot);
	}
}
