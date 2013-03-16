import java.util.Vector;


public class Engine {
  private boolean debug = false;  //prova ad abilitare il debug :D
	private Grid grid;
	private Vector<Cell> actualGeneration = new Vector<Cell>();
	private Vector<Cell> nextGeneration = new Vector<Cell>();
	private Vector<Cell> maybeNext = new Vector<Cell>();
	private Integer workingPos = 0;	

	public Engine(Grid grid){
		this.grid = grid;
	}

	public void reset(){
		//grid.setActualGeneration(actualGeneration);
		actualGeneration = new Vector<Cell>();
		nextGeneration = new Vector<Cell>();
		maybeNext = new Vector<Cell>();
	}

	public void computeNextGen(int coreN) {
		synchronized(grid){
			if(debug)
				System.out.println("================= COMPUTING NEXT GENERATION ================");

			workingPos = 0;
			actualGeneration = grid.getActualGeneration();
			compute(coreN);
			workingPos = 0;
			validate(coreN);
			actualGeneration = nextGeneration;
			grid.setActualGeneration(actualGeneration);

			if(debug)
				System.out.println("================= FINISHED COMPUTING NEXT GEN ================");
		}

	}

	private void validate(int coreN) {
		if(debug)
			System.out.println("JUDGES START ==> maybeNext = " + maybeNext.size());

		Judge[] judges = new Judge[coreN];
		for(int i = 0;i < judges.length;i++){
			judges[i] = new Judge();
			judges[i].start();
		}

		for(int i = 0;i < judges.length;i++){
			try {
				judges[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	private void compute(int coreN) {
		if(debug)
			System.out.println("SLAVE START ==> actualGeneration = " + actualGeneration.size());

		Slave[] slaves = new Slave[coreN];
		for(int i = 0;i < slaves.length;i++){
			slaves[i] = new Slave();
			slaves[i].start();
		}

		for(int i = 0;i < slaves.length;i++){
			try {
				slaves[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void initThreads(Slave[] slaves){

	}

	private class Judge extends Thread{
		public void run(){
			int pos;
			while(workingPos < maybeNext.size()){
				synchronized(workingPos){
					if(debug)
						System.out.println(getName() + " JUDGE maybeNext = " + maybeNext.size());

					if(workingPos < maybeNext.size())
						pos = workingPos++;
					else return;
				}
				Cell cell = maybeNext.get(pos);
				if(grid.isLivingCell(cell)){
					grid.kill(cell);
					if(debug)
						System.out.println("KILL REPORT: " + getName() + " just killed " + cell);
				}

				else{
					int n = grid.getNumbOfN(cell);
					if(debug)
						System.out.println("Cell " + cell + " has " + n + " neighbors");

					if(n == 3){
						Cell c = grid.createLivingCell(cell);
						synchronized(nextGeneration){//potrebbe non essere necessario
							nextGeneration.add(c);
						}
						if(debug)
							System.out.println(getName() + " adding " + cell + " to NEXT");
					}
					else
						grid.resetCell(cell);
				}
			}
		}
	}

	private class Slave extends Thread{
		public void run(){
			int pos;
			while(workingPos < actualGeneration.size()){
				synchronized(workingPos){
					if(debug)
						System.out.println(getName() + " SLAVE actualGen = " + actualGeneration.size());

					if(workingPos < actualGeneration.size())
						pos = workingPos++;
					else return;
				}
				watchNeighbors(actualGeneration.get(pos));
			}
		}

		private void addToNext(Cell cell){
			synchronized(nextGeneration){//potrebbe non essere necessario
				nextGeneration.add(cell);
				if(debug)
					System.out.println(getName() + " adding " + cell + " to NEXT");
			}
		}

		private void addToMaybe(Cell cell){
			synchronized(nextGeneration){//potrebbe non essere necessario
				maybeNext.add(cell);
				if(debug)
					System.out.println(getName() + " adding " + cell + " to MAYBE");
			}
		}

		private void watchNeighbors(Cell cell) {
			int count = 0;
			int gridSize = grid.getGridSize();
			int xStart = (cell.auxGetX() - 1) % gridSize;
			int xStop = xStart + 3;
			int yStart = (cell.auxGetY() - 1) % gridSize;
			int yStop = yStart + 3;
			for(int i = xStart;i < xStop;i++){
				for(int j = yStart;j < yStop;j++){
					if(i != cell.auxGetX() || j != cell.auxGetY()){
						Cell c = grid.getCell(i, j);
						if(!grid.isLivingCell(c)){
							if(debug)
								System.out.println("I'm " +  getName() + ", watching " + c  + " neighbor of => "  + cell );

							grid.incrementNumbOfN(c);

							if(grid.getNumbOfN(c) == 1){//metodo già sincronizzato
								addToMaybe(c);
							}
						}
						else{
							count++;
							if(debug)
								System.out.println("Cell " + cell + " COUNT = " + count + ", cause " + c + " lives");
						}
					}
				}
			}
			if(count == 2 || count == 3) 
				addToNext(cell);

			else
				addToMaybe(cell);
		}
	}
}
