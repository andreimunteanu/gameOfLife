
/**
 * The Engine class runs the core computations of the game by providing:
 * <ul>
 * <li> the method "computeNextGen()" that is used by the main game loop and the "Step" button. 
 * <li> the "debug" function, that will print to the standard output <i>hopefully</i> helpful information about the compute times and the workings of
 * the threads
 * <li> the "setCoreN()" method, that will change the number of working threads, for testing purposes.
 * </ul>
 * 
 * @author <A HREF="mailto:niccolo.marastoni@studenti.univr.it">Niccolò Marastoni</A>
 * @author <A HREF="mailto:andrei.munteanu@studenti.univr.it">Andrei Munteanu</A>
 * @version 1.0
 * 
 */
public class Engine {
	
	/*
	 * Used to enable/disable debug mode.
	 */
	private boolean debug = false;
	
	/*
	 * The grid, where all cells will spawn and eventually die.
	 */
	private Grid grid;
	
	/*
	 * Working position, number of the column where a slave will directly compute.
	 */
	private Integer workingPos = 0;
	
	/*
	 * Number of threads, initialized by default as the number of cores in the user's computer.
	 */
	private int coreN = Runtime.getRuntime().availableProcessors();
	private Integer runningSlaves = coreN;
	private boolean changedCoreN = false;
	private long time;
	private Thread[] slaves;
	
	/**
	 * Constructs an engine, sets the grid where it will work and initializes the threads.
	 * 
	 * @param grid
	 * 			the game's grid
	 */
	public Engine(Grid grid){
		this.grid = grid;
		initThreads();
	}
	
	/**
	 * Computes the next generation of living cells by calling the slaves and initializes the
	 * threads again in case the user has changed their number mid-game.
	 */
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
				System.out.println("================= FINISHED COMPUTING NEXT GEN ================ \nTIME ELAPSED = " 
						+ (System.currentTimeMillis() - time) );
		}

	}
	
	/**
	 * Sets the new number of threads, if the user changes it.
	 * 
	 * @param coreN number of threads to be used to compute the next generation.
	 */
	public void setCoreN(int coreN){
		if(debug)
			System.out.println("THREADS = " + coreN);
		if(coreN > 0){
			this.coreN = coreN;
			changedCoreN = true;
		}
	}
	
	/**
	 * Toggles the debug function and prints to the standard output whether it has been enabled or disabled.
	 */
	public void toggleDebug(){
		grid.toggleDebug();
		debug = !debug;
		System.out.println("DEBUG " + ((debug)?"ENABLED":"DISABLED"));
	}
	
	/*
	 * Starts a new Synchronizer object, then it creates as many slaves as set in coreN and starts them all,
	 * then waits for the Synchronizer to end.
	 */
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
	
	/*
	 * Creates and starts a new Synchronizer object and waits for it to end.
	 */
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
	
	/*
	 * Synchronizer object, needed to wake up waiting threads before computing a new generation
	 */
	private class Synchronizer extends Thread{
		
		/*
		 * Wakes up every thread that is waiting, then checks every 20 ms to see if they have finished
		 * and if they have, it returns.
		 */
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
	
	/*
	 * Slaves are threads that will actually work on the evolution of every generation.
	 */
	private class Slave extends Thread{
		private boolean canWork = false;
		
		/*
		 * A slave takes the number of a column and processes (with compute(x)) every cell in that subset, until the number
		 * exceeds the size of the grid, then it decreases the number of running slaves and waits to be notified.
		 */
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
		
		/*
		 * Takes a column on the grid and for every living cell it checks if it has enough neighbors to stay alive (2 or 3)
		 * and for every dead cell it checks if it has enough neighbors to come back to life (3).
		 * 
		 * @param x
		 * 		number of the column where the thread is working.
		 */
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
		
		/*
		 * Looks around the given cell to count how many living neighbors there are, then returns that number.
		 * 
		 * @param cell
		 * 			cell that is being computed
		 * @return the number of neighbors that are living cells.
		 */
		private int watchNeighbors(Cell cell){
			int count = 0;
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
