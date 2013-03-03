import java.awt.Color;
import java.awt.Dimension;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class GameOfLife extends JFrame {

	private Integer workingPos = 0;

	private final static int initial_SIZE = 50;
	static int size=initial_SIZE;
	private final long serialVersionUID = 1L;
	private Cell[][] cells;
	private Vector<Cell> actualGeneration = new Vector<Cell>();
	private Vector<Cell> newGeneration = new Vector<Cell>();
	private Vector<Cell> toTerminateCells = new Vector<Cell>(); //da fare cambio di stato nel frame
	private Vector<Cell> possibleFutureGeneration = new Vector<Cell>(); //lista con cellule per la generazione futura non tutte ne fanno parte
	// quelle che cambiano stato n.b c'è da fare il cambio di stato nel frame			
	// forse c'è un alternativa. vado a dormire ciao.


	public GameOfLife(){
		super("Game of Life");
		setOff();
	}

	private void setOff(){
		int coreN = Runtime.getRuntime().availableProcessors();
		Generator[] generators = new Generator[coreN];
		Terminator[] terminators = new Terminator[coreN];

		initFrame();

		for(int i=0; i<size; i++)
			for(int j=0; j<size;j++){
				
				//getContentPane().remove(cells[i][j]); //causa eccezione
				cells[i][j] = new LivingCell(i,j);
				getContentPane().add(cells[i][j]);
			}
		
		getContentPane().repaint();

		/*while(true){ //dovra diventare while(play()) un metodo che permetta di fermare il gioco quando vine schiacciato un bottone
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
		int xSize = (Cell.CELL_SIZE * size);
		int ySize = (Cell.CELL_SIZE * size) + Cell.CELL_SIZE * 8;
		setSize(xSize,ySize);
		setResizable(false);
		initCells();
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
		JMenuItem exit = new JMenuItem("Exit");
		file.add(exit);
		menu.add(file);
		setJMenuBar(menu);
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
				getContentPane().add(cells[i][j]);
			}

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

		actualGeneration = newGeneration;
		newGeneration.clear();
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

	private class Generator extends Thread{
		int i;
		public void run(){
			while(true){
				synchronized (workingPos){
					if(workingPos<actualGeneration.size())
						i = workingPos++;
					else
						return;
				}
				upDate(i);//potrebbe esserci race condition nelle variabile newGen e deadCells sicuramente
			}
		}
	}
	private void upDate(int index){//race condition
		int aliveNeighbors = watchNeighbors(actualGeneration.get(index));

		if(aliveNeighbors == 2 || aliveNeighbors == 3)
			newGeneration.add(actualGeneration.get(index));

		else
			toTerminateCells.add(actualGeneration.get(index));
	}

	private int watchNeighbors(Cell cell) {// guarda i vicini di cell restituisce il numero di cellule vive e incrementa di 1 il campo numberOfN 
		int x = cell.getX();				// delle cellule morte
		int y = cell.getY();
		int count = 0;
		Cell neighborCell;
		for(int i = -1; i < 2; i++)
			for(int j = -1;j < 2;j++){
				neighborCell = cells[Math.abs(i + x) % (size-1)][Math.abs(j + y) % (size-1)];
				if((i != 0 || j != 0) && neighborCell instanceof LivingCell)
					count++;
				else if(i != 0 || j != 0){
					neighborCell.incrementNumbOfN();
					if(neighborCell.getNumbOfN() == 3)
						possibleFutureGeneration.add(neighborCell);
				}
			}

		return count;
	}

	private class Terminator extends Thread{
		int i;
		public void run(){
			while(true){
				synchronized (workingPos){
					if(workingPos<toTerminateCells.size())
						i = workingPos++;
					else
						return;
				}
				killCell(i);
			}
		}
	}
	private void killCell(int i) {
		int x = toTerminateCells.get(i).getX();
		int y = toTerminateCells.get(i).getY();
		cells[x][y] = new DeadCell(x,y);
	}

	public static void main(String[] args) {
		new GameOfLife();
	}

}
