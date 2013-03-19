import java.util.Vector;


public class Engine {
	private boolean debug = false;  //prova ad abilitare il debug :D
	private Grid grid;
	private Vector<Cell> actualGeneration = new Vector<Cell>();
	private Vector<Cell> nextGeneration = new Vector<Cell>();
	private Vector<Cell> maybeNext = new Vector<Cell>();
	private Integer workingPos = 0;	
	private int coreN = 4;

	public Engine(Grid grid){
		this.grid = grid;
	}

	public void reset(){
		//grid.setActualGeneration(actualGeneration);
		actualGeneration = new Vector<Cell>();
		nextGeneration = new Vector<Cell>();
		maybeNext = new Vector<Cell>();
	}

	public void computeNextGen() {
		synchronized(grid){
			grid.nextGeneration();
			if(debug)
				System.out.println("================= COMPUTING NEXT GENERATION ================");

			initThreads();
			//		actualGeneration = grid.getActualGeneration();
			//			actualGeneration = nextGeneration;
			//grid.setActualGeneration(actualGeneration);
			if(debug)
				System.out.println("================= FINISHED COMPUTING NEXT GEN ================");
		}

	}
	private void initThreads(){
		workingPos = 0;
		Thread[] slaves = new Slave[coreN];
		for(int i = 0; i < coreN; i++){
			slaves[i] = new Slave();
			slaves[i].start();
		}
		for(int i = 0; i < coreN; i++){
			try {
				slaves[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	private class Slave extends Thread{

		public void run(){
			int x;
			while(true){
				synchronized(Engine.this){
					if(workingPos < grid.getGridSize())
						x = workingPos++;
					else return;
				}
				compute(x);
			}
		}
		private void compute(int x){
			int y = 0;
			int count;
			while(y < grid.getGridSize()){
			//	System.out.println(x +"  "+ y);
				Cell cell = grid.getCell(x, y);
				count = watchNeighbors(cell);
				if(cell.isLivingCell() && !(count == 3 || count == 2))
					grid.kill(cell);
				else if(cell.isLivingCell() && (count == 3 || count == 2))
					grid.createLivingCell(cell);
				else if(!cell.isLivingCell() && count == 3)
					grid.createLivingCell(cell);
				else if(!cell.isLivingCell() && count != 3)
					grid.kill(cell);
				y++;
			}
		}

		private int watchNeighbors(Cell cell){

			int count = 0;
			int gridSize = grid.getGridSize();
			int xStart = (cell.auxGetX() - 1) % gridSize;
			int xStop = xStart + 3;
			int yStart = (cell.auxGetY() - 1) % gridSize;
			int yStop = yStart + 3;
			for(int i = xStart;i < xStop;i++)
				for(int j = yStart;j < yStop;j++)
					if((i != cell.auxGetX() || j != cell.auxGetY()) 
							&& grid.getCell(i,j).isLivingCell())
						count++;

			return count;
		}
	}

}
