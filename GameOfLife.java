import java.awt.Color;
import java.util.Vector;

import javax.swing.JFrame;

public class GameOfLife extends JFrame {
	private final static int SIZE_1 = 50;
	private final long serialVersionUID = 1L;
	private Vector<Cell> actualGen = new Vector<Cell>();
	private Vector<Cell> newGen = new Vector<Cell>();
	private Vector<Cell> deadCells = new Vector<Cell>();
	private Cell[][] cells;
	private Integer workingPos = 0;
	static int size;
	public GameOfLife(){
		super("Game of Life");
		size = SIZE_1;
		//	initFrame();
		init();
	}

	private void initCells(){
		cells = new Cell[size][size];
		for(int i = 0;i < size;i++ )
			for(int j = 0;j < size;j++)
				cells[i][j] = new LivingCell(i,j);
	}
	private void init(){
		size = SIZE_1;
		int coreN = Runtime.getRuntime().availableProcessors();
		Generator[] generators = new Generator[coreN];
		Terminator[] terminators = new Terminator[coreN];

		initFrame();
		/*
		while(true){
			for(int i = 0; i < coreN; i++){
				generators[i] = new Generator();
				terminators[i] = new Terminator();
			}
			newGeneration(generators,terminators, coreN);
			addCellsToFrame();
		}
		 */
	}
	private void initFrame(){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
		int xSize = Cell.CELL_SIZE * size;
		int ySize = Cell.CELL_SIZE * size;
		setSize(xSize,ySize);
		setResizable(false);

		initCells();
		/*cells[0][0].setBounds(0 * Cell.CELL_SIZE, 0 * Cell.CELL_SIZE,
				Cell.CELL_SIZE, Cell.CELL_SIZE);
		cells[0][0].setBackground(Color.BLUE);
		this.getContentPane().add(cells[0][0]);
		cells[1][1].setBounds(20, 20,
				20, 20);
		cells[1][1].setBackground(Color.BLUE);
		this.getContentPane().add(cells[1][1]);*/
		addCellsToFrame();
		setVisible(true);
	}

	private void addCellsToFrame() {
		for(int i = 0;i < size;i++)
			for(int j = 0;j < size;j++){
				//cells[i][j].setBounds(i * Cell.CELL_SIZE, j * Cell.CELL_SIZE,
						//Cell.CELL_SIZE, Cell.CELL_SIZE);
				//cells[i][j].setBackground(Color.BLUE);
				this.getContentPane().add(cells[i][j]);
			}

	}
	private void upDate(int index){//race condition

		if(countAliveNeighbors(actualGen.get(index)) == 2 || countAliveNeighbors(actualGen.get(index)) == 3)
			newGen.add(actualGen.get(index));

		else
			deadCells.add(actualGen.get(index));
	}
	private void newGeneration(Generator[] generators,Terminator[] terminators, int coreN){
		for(int i = 0; i < coreN; i++)
			generators[i].start();

		for(int i = 0; i < coreN; i++){
			try {
				generators[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		actualGen = newGen;
		newGen.clear();
		workingPos = 0;
		for(int i = 0; i < coreN; i++)
			terminators[i].start();

		for(int i = 0; i < coreN; i++){
			try {
				generators[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private int countAliveNeighbors(Cell cell) {
		int x = cell.getX();
		int y = cell.getY();
		int count = 0;
		for(int i = -1; i < 2; i++)
			for(int j = -1;j < 2;j++)
				if((i != 0 || j != 0) && cells[Math.abs(i + x) % (size-1)][Math.abs(j + y) % (size-1)] instanceof LivingCell)
					count++;
		return count;
	}

	private void killCell(int i) {
		int x = deadCells.get(i).getX();
		int y = deadCells.get(i).getY();
		cells[x][y] = new DeadCell(x,y);
	}

	public static void main(String[] args) {
		new GameOfLife();
	}

	private class Terminator extends Thread{
		int i;
		public void run(){
			while(true){
				synchronized (workingPos){
					if(workingPos<deadCells.size())
						i = workingPos++;
					else
						return;
				}
				killCell(i);
			}
		}
	}

	private class Generator extends Thread{
		int i;
		public void run(){
			while(true){
				synchronized (workingPos){
					if(workingPos<actualGen.size())
						i = workingPos++;
					else
						return;
				}
				upDate(i);//potrebbe esserci race condition nelle variabile newGen e deadCells sicuramente
			}
		}
	}
}
