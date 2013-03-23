import java.util.Vector;


public class Engine {
	private boolean debug = true;
	private Grid grid;
	private Vector<Cell> actualGeneration = new Vector<Cell>();
	private Vector<Cell> nextGeneration = new Vector<Cell>();
	private Vector<Cell> maybeNext = new Vector<Cell>();
	private Integer workingPos = 0;	
	private int coreN = Runtime.getRuntime().availableProcessors();
	private long time;

	public Engine(Grid grid){
		this.grid = grid;
	}

	public void computeNextGen() {
		synchronized(grid){
			if(debug){
				System.out.println("================= COMPUTING NEXT GENERATION ================");
				time = System.currentTimeMillis();
			}

			initThreads();
			grid.nextGeneration();
			//		actualGeneration = grid.getActualGeneration();
			//			actualGeneration = nextGeneration;
			//grid.setActualGeneration(actualGeneration);
			if(debug)
				System.out.println("================= FINISHED COMPUTING NEXT GEN ================ \nTIME ELAPSED = " + (System.currentTimeMillis() - time));
		}

	}

	public void setCoreN(int coreN){
		if(coreN > 0)
			this.coreN = coreN;
	}
	
	public void toggleDebug(){
		debug = (debug)?false:true;
	}

	private void initThreads(){
		if(debug)
			System.out.println("Threads = " + coreN);
		
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
				//System.out.println(x +"  "+ y);
				Cell cell = grid.getCell(x, y);
				if(!cell.isDefDead()){
					count = watchNeighbors(cell);
					if((cell.isAliveNow() && !(count == 3 || count == 2)) || (!cell.isAliveNow() && count == 3)) {
						grid.changeState(cell);
						//System.out.println("Cell " + cell +" is ALIVE => " + cell.isAliveNow() + " count = " + count);	
					}
				}
				y++;
			}
		}

		private int watchNeighbors(Cell cell){

			int count = 0;
			int gridSize = grid.getGridSize();
			int xStart = (cell.auxGetX() - 1);
			int xStop = xStart + 3;
			int yStart = (cell.auxGetY() - 1);
			int yStop = yStart + 3;
			for(int i = xStart;i < xStop;i++)
				for(int j = yStart;j < yStop;j++){
					if((i != cell.auxGetX() || j != cell.auxGetY()) 
							&& grid.getCell(i,j).isAliveNow())
						count++;
				}
			return count;
		}
	}

}
