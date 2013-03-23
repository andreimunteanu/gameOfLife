import java.util.Vector;


public class Engine {
	private boolean debug = false;
	private Grid grid;
	private Integer workingPos = 0;
	private int coreN =Runtime.getRuntime().availableProcessors();
	private Integer runningSlaves = coreN;
	private boolean changedCoreN = false;
	private long time;
	private Thread[] slaves;

	public Engine(Grid grid){
		this.grid = grid;
		initThreads();
	}

	public void computeNextGen() {
		synchronized(grid){
			if(changedCoreN)
				initThreads();
			if(debug){
				System.out.println("================= COMPUTING NEXT GENERATION ================");
				time = System.currentTimeMillis();
			}
			runSlaves();
			grid.nextGeneration();
			if(debug)
				System.out.println("================= FINISHED COMPUTING NEXT GEN ================ \nTIME ELAPSED = " + (System.currentTimeMillis() - time));
		}

	}

	public void setCoreN(int coreN){
		if(debug)
			System.out.println("THREADS = " + coreN);
		if(coreN > 0){
			this.coreN = coreN;
			changedCoreN = true;
		}
	}

	public void toggleDebug(){
		grid.toggleDebug();
		debug = !debug;
		System.out.println("DEBUG " + ((debug)?"ENABLED":"DISABLED"));
	}

	private void initThreads(){
		if(debug)
			System.out.println("Threads = " + coreN);
		runningSlaves = coreN;
		Synchronizer sync = new Synchronizer();
		sync.start();
		workingPos = grid.getGridSize();
		slaves = new Slave[coreN];
		for(int i = 0; i < coreN; i++){
			slaves[i] = new Slave();
			slaves[i].start();
		}
		try {
			sync.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	private void runSlaves(){
		runningSlaves = coreN;
		Synchronizer sync = new Synchronizer();
		sync.start();
		try {
			sync.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		changedCoreN = false;
	}
	private class Synchronizer extends Thread{
		public void run(){
			workingPos = 0;
			synchronized(Engine.this){
				Engine.this.notifyAll();
			}
			while(true){
				if(runningSlaves <= 0){
					return;
				}
				else
					try {
						sleep(20);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
			}

		}
	}

	private class Slave extends Thread{
		private boolean canWork = false;
		public void run(){
			int x = 0;
			while(true){
				synchronized(Engine.this){
					if(workingPos < grid.getGridSize()){
						x = workingPos++;
						canWork = true;
					}
					else
						try {
							canWork = false;
							runningSlaves--;
							Engine.this.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
				}
				if(canWork)
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
						if(debug)
							System.out.println(getName() + "==> cell " + cell +" is ALIVE => " + cell.isAliveNow() + " count = " + count);	
					}
				}
				y++;
			}
		}

		private int watchNeighbors(Cell cell){
			int count = 0;
	//		int gridSize = grid.getGridSize();
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
