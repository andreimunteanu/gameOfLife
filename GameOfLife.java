import java.awt.Color;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.swing.JFrame;

public class GameOfLife extends JFrame {
	private final static int SIZE_1 = 50;
	private final long serialVersionUID = 1L;
	private Vector<Cell> actualGen = new Vector<Cell>();
	private Vector<Cell> newGen = new Vector<Cell>();
	private Vector<Cell> deadCells = new Vector<Cell>(); //da fare cambio di stato nel frame
	private Set<Cell> possibleFutureGeneration = new HashSet(); //lista con cellule per la generazione futura non tutte ne fanno parte
															// quelle che cambiano stato n.b c'è da fare il cambio di stato nel frame			
															// forse c'è un alternativa. vado a dormire ciao.
	private Cell[][] cells;
	private Integer workingPos = 0;
	static int size;
	public GameOfLife(){
		super("Game of Life");
		size = SIZE_1;
		//	initFrame();
		init();
	}

	private void init(){
		size = SIZE_1;
		int coreN = Runtime.getRuntime().availableProcessors();
		Generator[] generators = new Generator[coreN];
		Terminator[] terminators = new Terminator[coreN];

		initFrame();
		
		for(int i=0; i<size; i++)
			for(int j=0; j<size;j++){
				//getContentPane().remove(cells[i][j]); //qui si genera l'exception
				cells[i][j] = new LivingCell(i,j);
				getContentPane().add(cells[i][j]);
			}
		
		getContentPane().repaint();
				
		/*while(true){
			for(int i = 0; i < coreN; i++){
				generators[i] = new Generator();
				terminators[i] = new Terminator();
			}
			newGeneration(generators,terminators, coreN);
			addCellsToFrame();
			
		}*/
	}
	
	private void initFrame(){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
		int xSize = Cell.CELL_SIZE * size;
		int ySize = Cell.CELL_SIZE * size;
		setSize(xSize,ySize);
		setResizable(false);

		initCells();
		
		addCellsToFrame();
		setVisible(true);
	}
	
	private void initCells(){
		cells = new Cell[size][size];
		for(int i = 0;i < size;i++ )
			for(int j = 0;j < size;j++)
				cells[i][j] = new DeadCell(i,j);
	}

	private void addCellsToFrame() {
		for(int i = 0;i < size;i++)
			for(int j = 0;j < size;j++){
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
				System.err.println("Error in newGeneration(generators) => " + e.getMessage());
			}
		}

		actualGen = newGen;
		newGen.clear();
		workingPos = 0;
		
		for(int i = 0; i < coreN; i++)
			terminators[i].start();

		for(int i = 0; i < coreN; i++){
			try {
				terminators[i].join();
			} catch (InterruptedException e) {
				System.err.println("Error in newGeneration(terminators) => " + e.getMessage());
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
				else if(i != 0 || j != 0){
					cells[Math.abs(i + x) % (size-1)][Math.abs(j + y) % (size-1)].incrementNumbOfN();
					if(cells[Math.abs(i + x) % (size-1)][Math.abs(j + y) % (size-1)].getNumbOfN()==3)
						possibleFutureGeneration.add(cells[Math.abs(i + x) % (size-1)][Math.abs(j + y) % (size-1)]);
				}
					
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
