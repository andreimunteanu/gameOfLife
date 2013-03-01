import java.util.Vector;

import javax.swing.JFrame;

public class GameOfLife extends JFrame {
	private final static int SIZE_1 = 50;
	private  final long serialVersionUID = 1L;
	private Vector<Cell> actualGen = new Vector<Cell>();
	private Vector<Cell> newGen = new Vector<Cell>();
	private Vector<Cell> deadCells = new Vector<Cell>();
	private static Cell[][] cells;
	private Integer workingPos = 0;
	static int size;
	private boolean cleanUp = false;
	public GameOfLife(){
		init();
	}
	private void init(){
		size = SIZE_1;
		int coreN = Runtime.getRuntime().availableProcessors();
		Slave[] slaves = new Slave[coreN];
		cells = new Cell[size][size];
		for(int i = 0;i < size;i++ )
			for(int j = 0;j < size;j++)
				cells[i][j] = new Cell(i,j);

		for(int i = 0; i < coreN; i++)
			slaves[i] = new Slave();

		while(true){
			newGeneration(slaves, coreN);
		}
	}
	private void upDate(int index){

		if(countAliveNeighbors(actualGen.get(index)) == 2 || countAliveNeighbors(actualGen.get(index)) == 3)
			newGen.add(actualGen.get(index));

		else
			deadCells.add(actualGen.get(index));
	}
	private void newGeneration(Slave[] slaves, int coreN){

		for(int i = 0; i < coreN; i++)
			slaves[i].start();

		for(int i = 0; i < coreN; i++)
			try {
				slaves[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			actualGen = newGen;
			newGen.clear();
			workingPos = 0;
			cleanUp = true;
			for(int i = 0; i < coreN; i++)
				slaves[i].start();

			for(int i = 0; i < coreN; i++)
				try {
					slaves[i].join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}
	private int countAliveNeighbors(Cell cell) {
		int x = cell.getX();
		int y = cell.getY();
		int count = 0;
		for(int i = -1; i < 2; i++)
			for(int j = -1;j < 2;j++)
				if((i != 0 || j != 0) && cells[Math.abs(i + x) % (size-1)][Math.abs(j + y) % (size-1)].getState())
					count++;
		return count;
	}
	private void killCell(int i) {
		deadCells.get(i).setState(false);
	}

	public static void main(String[] args) {
		new GameOfLife();
	}
	private class Slave extends Thread{
		int i;
		public void run(){
			while(true){
				if(!cleanUp){
					synchronized (workingPos){

						if(workingPos<actualGen.size())
							i=workingPos++;	
						else 
							return;

					}

					upDate(i);//potrebbe esserci race condition nelle variabile newGen e deadCells sicuramente
				}
				else{
					synchronized (workingPos){

						if(workingPos<actualGen.size())
							i=workingPos++;	
						else 
							return;

					}
					killCell(i);
				}

			}
		}


	}
}
