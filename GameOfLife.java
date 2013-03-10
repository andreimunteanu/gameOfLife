import java.util.Vector;

public class GameOfLife/* extends JFrame*/ {
	private Grid grid;

	private volatile Integer cleanerWorkingPos = 0;
	private Integer terminatorWorkingPos=0;
	private Integer generatorWorkingPos=0;
	private volatile Integer runningThreads=4; 
	private boolean initialization=true;

	//private Cell[][] cells;
	private Vector<Cell> actualGeneration = new Vector<Cell>();
	private Vector<Cell> newGeneration = new Vector<Cell>();
	private Vector<Cell> toTerminateCells = new Vector<Cell>(); //da fare cambio di stato nel frame
	private Vector<Cell> possibleFutureGeneration = new Vector<Cell>(); 
	// ho aggiunto la classe thread Synchronizer su cui viene fatta la join di fatto aspetta che ciascuna tipologia di thread finisca e crepa 
	//così posso fare la wait sulle altre, Synchronizer riceve come parametro il workingPos di ciascun tipo di thread così può chiamare la notify 
	// e svgliare le thread Corrispondenti
	// ho aggiunto il metodo startThreads che riceve come parametro il workingPos e il vettore di thread corrispondente, serve solo a far partire 
	// le thread
	// il metodo runThread crea un nuovo siìync ci fa il la start join e questo fa notifyAll sul workinpos 
	// bug nel decremento della variabile runningThreads che va a -8 dopo che le cleaner hanno lavorato
	// è tutto un po' spartano perchè volevo vedere se anadava [:

	public static void main(String[] args) {
		new GameOfLife();
	}

	public GameOfLife(){
		setOff();
	}
	/*
	private void test(){
		for(int i=25;i < 28;i++){
			cells[25][i] = grid.new LivingCell(25,i);
			actualGeneration.add(cells[25][i]);
		}
	}
	 */
	private void setOff(){
		int coreN =4;//Runtime.getRuntime().availableProcessors();
		Cleaner[] cleaners = new Cleaner[coreN];
		Terminator[] terminators = new Terminator[coreN];
		Generator[] generators = new Generator[coreN];
		System.out.println("Partito");
		grid = new Grid(actualGeneration);
		initThreads(cleaners,generators,terminators);
		//	grid.forceUpdate();
		grid.test();
		while(true){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				System.err.println("Error in setOff() => " + e.getMessage());
			}
			newGeneration(cleaners,generators,terminators);
			//grid.removeCells(toTerminateCells);
			//grid.addCells(newGeneration);
			grid.forceUpdate(); //fa grid.repaint(); ogni 4 secondi (aggiustiamo poi);
			//System.out.println("new" + newGeneration.size());
			//System.out.println("to" + toTerminateCells.size());
			//System.out.println("possible" + possibleFutureGeneration.size());
			actualGeneration = newGeneration;
			toTerminateCells=new Vector<Cell>();
			possibleFutureGeneration=new Vector<Cell>();
			//System.out.println(actualGeneration.size());
			newGeneration=new Vector<Cell>();
			cleanerWorkingPos=0;
			terminatorWorkingPos=0;
			generatorWorkingPos=0;
		}
	}

	private void initThreads(Cleaner[] cleaners,Generator[] generators,Terminator[] terminators){ //metodo per inizializzare le thread 
		for(int i=0;i < cleaners.length;i++){														
			cleaners[i] = new Cleaner();
			generators[i] = new Generator();
			terminators[i] = new Terminator();
		}
		startThreads(cleaners,cleanerWorkingPos);

		if(runningThreads !=0)
			System.out.println("runningThreads=> " + runningThreads);
		runningThreads = 4;
		//System.out.println("Cleaner done");

		startThreads(generators,generatorWorkingPos);

		if(runningThreads !=0)
			System.out.println("runningThreads=> " + runningThreads);
		runningThreads = 4;

		startThreads(terminators, terminatorWorkingPos);

		if(runningThreads !=0)
			System.out.println("runningThreads=> " + runningThreads);
		runningThreads = 4;
		initialization = false;

	}

	private void startThreads(Thread[] slaves,Integer workingPos){
		Synchronizer sync = new Synchronizer(workingPos); 	
		sync.start();
		for(int i=0; i < slaves.length;i++)
			slaves[i].start();
		try {
			sync.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void newGeneration(Cleaner[] cleaners,Generator[] generators,Terminator[] terminators){ 
		//System.out.println(workingPosition+" "+actualGeneration.size());
		//System.out.println(cleaners[0].getState());

		System.out.println("runTs "+runningThreads);
		runThreads(cleanerWorkingPos);
		System.out.println("Ciao mam");
		if(runningThreads !=0)
			System.out.println("runningThreads=> " + runningThreads);

		runningThreads = 4;
		runThreads(generatorWorkingPos);

		if(runningThreads !=0)
			System.out.println("runningThreads=> " + runningThreads);

		runningThreads = 4;
		runThreads(terminatorWorkingPos);

		if(runningThreads !=0)
			System.out.println("runningThreads=> " + runningThreads);
		runningThreads = 4;
	}

	private void runThreads(Integer workingPos){
		Synchronizer sync = new Synchronizer(workingPos); 
		sync.start();
		try {
			sync.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private class Synchronizer extends Thread{
		private Integer workingPos;
		public Synchronizer(Integer workingPos){
			this.workingPos=workingPos;
		}
		public void run(){
			synchronized (workingPos){
				if(!initialization)
					workingPos.notifyAll();
			}
			while(true){
				System.out.println("syncher: thread attive" +runningThreads);
				if(runningThreads == 0)
					return;
				else
					try {
						sleep(20);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		}
	}

	private class Cleaner extends Thread{
		private int i;
		private boolean work=true;
		public void run(){
			while(true){
				synchronized (cleanerWorkingPos){
					if(cleanerWorkingPos < actualGeneration.size()){
						work=true;
						System.out.println("Cleaner "+ this.getName() + (cleanerWorkingPos+1) + " size :"+actualGeneration.size());
						i = cleanerWorkingPos++;

					}
					else
						try {
							work=false;
							runningThreads--;
							System.out.println("runningThreads " + runningThreads);
							cleanerWorkingPos.wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

				}
				if(work)
					upDate(i);//potrebbe esserci race condition nelle variabile newGen e deadCells sicuramente
			}
		}

		private void upDate(int index){//race condition
			if(actualGeneration.size() > 0 && index < actualGeneration.size()){
				int aliveNeighbors = watchNeighbors(actualGeneration.get(index));
				if(aliveNeighbors == 2 || aliveNeighbors == 3)
					newGeneration.add(actualGeneration.get(index));

				else{
					toTerminateCells.add(actualGeneration.get(index));

				}
			}
		}

		private int watchNeighbors(Cell cell) {// guarda i vicini di cell restituisce il numero di cellule vive e incrementa di 1 il campo numberOfN
			int x = cell.auxGetX(); // delle cellule morte
			int y = cell.auxGetY();
			int count = 0;
			Cell neighborCell;
			for(int i = -1; i < 2; i++)
				for(int j = -1;j < 2;j++){
					neighborCell = grid.getCell(i + x, j + y);
					if((i != 0 || j != 0) && grid.isLivingCell(neighborCell))
						count++;
					else if(i != 0 || j != 0){
						checkDeadCell(neighborCell);
					}
				}

			return count;
		}
		private void checkDeadCell(Cell cell){
			grid.incrementNumbOfN(cell);
			if(grid.getNumbOfN(cell) == 1)
				possibleFutureGeneration.add(cell);
		}
	}



	private class Terminator extends Thread{
		private int i;
		private boolean work;
		public void run(){
			//System.out.println("Terminator online");
			while(true){
				synchronized (terminatorWorkingPos){
					if(terminatorWorkingPos < toTerminateCells.size()){
						work=true;
						i = terminatorWorkingPos++;
					} else{
						work=false;
						runningThreads--;
						try {
							terminatorWorkingPos.wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}	
				}
				if(work)
					killCell(i);
			}
		}
		private void killCell(int index) {
			if(toTerminateCells.size() > 0){

				grid.kill(toTerminateCells.get(index));

			}
		}
	}

	private class Generator extends Thread{
		private int i;
		private boolean work;
		public void run(){
			//System.out.println("Generator online");
			while(true){
				synchronized(generatorWorkingPos){
					if(generatorWorkingPos < possibleFutureGeneration.size()){
						work=true;
						i = generatorWorkingPos++;
					} else
						try {
							work=false;
							runningThreads--;
							generatorWorkingPos.wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
				if(work)
					checkDeadCell(i);
			}
		}

		private void checkDeadCell(int index){
			if(possibleFutureGeneration.size() > 0){
				Cell cell = possibleFutureGeneration.get(index);
				if(grid.getNumbOfN(cell) == 3){
					newGeneration.add(grid.createLivingCell(cell));
				}
				else
					grid.resetCell(cell);
			}
		}
	}
}
