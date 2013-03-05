import java.awt.Color;
import java.awt.Dimension;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;


public class Grid extends JFrame{
	Cell[][] cells;
	int size;
	public Grid(Cell[][] cells, int size){
		super("Game Of Life");
		this.cells = cells;
		this.size = size;
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

	private void addCellsToFrame() {
		for(int i = 0;i < size;i++)
			for(int j = 0;j < size;j++){
				getContentPane().add(cells[i][j]);
			}

	}

	public void removeCells(Vector<Cell> removedCells){
		for(Cell cell : removedCells)
			getContentPane().remove(cell);
	}

	public void addCells(Vector<Cell> addedCells){
		for(Cell cell : addedCells)
			getContentPane().add(cell);
	}
	
	public void setAliveCells(Vector<Cell> cells){
		for(Cell cell : cells){
			getContentPane().remove(cell);
			getContentPane().add(new LivingCell(cell.getX()/10,cell.getY()/10));
		}
			
	}
	public void setDeadCells(Vector<Cell> cells){
		for(Cell cell : cells){
			getContentPane().remove(cell);
			getContentPane().add(new DeadCell(cell.getX()/10,cell.getY()/10));
		}
	}
			
	public void forceUpdate(){
		getContentPane().repaint();
	}
}
