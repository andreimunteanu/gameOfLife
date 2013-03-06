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


public class Grid extends JFrame{
	private Cell[][] cells;
	private final static int SIZE_50 = 50;	//variabili inutili passiamo direttamente il parametro
	private final static int SIZE_100 = 100;	// nell'actionlistener
	private final static int SIZE_200 = 200;
	static int size = SIZE_50;
	private Vector<Cell> actualGeneration;
	public Grid(Vector<Cell> actualGeneration){
		super("Game Of Life");
		this.actualGeneration = actualGeneration;
		initCells();
		initFrame();
	}

	private void initFrame(){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
		int xSize = (Cell.CELL_SIZE * size);
		int ySize = (Cell.CELL_SIZE * size) + Cell.CELL_SIZE * 8;
		setSize(xSize,ySize);
		setResizable(false);
		addCellsToFrame();
		initMenu();
		setVisible(true);
	}

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
	
	public void test(){
		for(int i=25;i < 28;i++){
			getContentPane().remove(cells[25][i]);
			cells[25][i] = new LivingCell(25,i);
			actualGeneration.add(cells[25][i]);
			getContentPane().add(cells[25][i]);
		}
	}

	private void addCellsToFrame() {
		for(int i = 0;i < size;i++)
			for(int j = 0;j < size;j++){
				getContentPane().add(cells[i][j]);
			}
	}
	
	private void initCells(){ // da togliere
		cells = new Cell[size][size];
		for(int i = 0;i < size;i++ )
			for(int j = 0;j < size;j++)
				cells[i][j] = new DeadCell(i,j);
	}

	public void switchCell(Cell cell){
		int x = cell.auxGetX();
		int y = cell.auxGetY();
		getContentPane().remove(cell);
		cells[x][y] = (cell instanceof DeadCell)?new LivingCell(x,y):new DeadCell(x,y);
		getContentPane().add(cells[x][y]);
	}
	
	
	public void addCell(Cell cell){
		getContentPane().add(cell);

	}
	public void removeCell(Cell cell){
		getContentPane().remove(cell);
	}

	public void forceUpdate(){
		getContentPane().repaint();
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
	
	private class DeadCell extends Cell{
		private Integer numberOfN = 0;
		private static final long serialVersionUID = 1L;
		protected DeadCell(int x, int y) {
			super(x, y, "deadCell.gif");
		}
	}

	private class LivingCell extends Cell{
		private static final long serialVersionUID = 1L;
		public LivingCell(int x, int y) {
			super(x,y,"cell.gif");
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
}
