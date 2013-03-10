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
	private Vector<Cell> actualGeneration;
	public Grid(Vector<Cell> actualGeneration){
		this.actualGeneration = actualGeneration;
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
	/*
	private void initMenu() {
		JMenuBar menu = new JMenuBar();
		menu.setOpaque(true);
		menu.setBackground(Color.WHITE);
		menu.setPreferredSize(new Dimension(0,20));
		JMenu file = new JMenu("File");
		JMenu size = new JMenu("Size");
		JMenu edit = new JMenu("Edit");
		JMenuItem exit = new JMenuItem("Exit");
		JMenuItem size1 = new JMenuItem("50 x 50");
		JMenuItem size2 = new JMenuItem("100 x 100");
		JMenuItem size3 = new JMenuItem("200 x 200");
		edit.add(size);
		size.add(size1);
		size.add(size2);
		size.add(size3);
		file.addSeparator();
		file.add(exit);
		menu.add(file);
		menu.add(edit);
		setJMenuBar(menu);
	}
	 */
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
		((DeadCell)cell).numberOfN++;
	}

	public int getNumbOfN(Cell cell) {
		return ((DeadCell)cell).numberOfN;
	}

	public void resetCell(Cell cell) {
		((DeadCell)cell).numberOfN = 0;
	}	

	public int getXSize(){
		return xSize;
	}

	public int getYSize(){
		return ySize;
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
					//System.out.println("CLICK FIGGA");
				}
			});
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

	public Cell getCell(int i, int j) {
		return cells[Math.abs(i) % (size - 1)][Math.abs(j) % (size - 1)];
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
		//forceUpdate();
	}

	public void setActualGeneration(Vector<Cell> actualGeneration) {
		this.actualGeneration = actualGeneration;
	}

	public Vector<Cell> getActualGeneration() {
		return actualGeneration;
	}
}
