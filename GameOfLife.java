import java.util.Vector;

public class GameOfLife/* extends JFrame*/ {
	private Grid grid;
	private final static int SIZE_50 = 50;	//variabili inutili passiamo direttamente il parametro
	private final static int SIZE_100 = 100;	// nell'actionlistener
	private final static int SIZE_200 = 200;
	static int size = SIZE_50;
	private static Integer workingPosition = 0;
	private Cell[][] cells;
	private Vector<Cell> actualGeneration = new Vector<Cell>();
	private Vector<Cell> newGeneration = new Vector<Cell>();
	private Vector<Cell> toTerminateCells = new Vector<Cell>(); //da fare cambio di stato nel frame
	private Vector<DeadCell> possibleFutureGeneration = new Vector<DeadCell>(); //lista con cellule per la generazione futura non tutte ne fanno parte
	// quelle che cambiano stato n b c'e da fare il cambio di stato nel frame


	public static void main(String[] args) {
		new GameOfLife();
	}

	public GameOfLife(){
		setOff();
	}
	private void test(){
		for(int i=25;i < 28;i++){
			cells[i][25] = new LivingCell(i,25);
			actualGeneration.add(cells[i][25]);
		}
	}
	private void setOff(){
		int coreN = Runtime.getRuntime().availableProcessors();
		Cleaner[] cleaners = new Cleaner[coreN];
		Terminator[] terminators = new Terminator[coreN];
		Generator[] generators = new Generator[coreN];
		initCells();
		initThreads(cleaners,generators,terminators);
		test();
		grid = new Grid(cells, size);
		grid.forceUpdate();
		while(true){
			try {
				Thread.sleep(400);
			} catch (InterruptedException e) {
				System.err.println("Error in setOff() => " + e.getMessage());
			}
			newGeneration(cleaners,generators,terminators);
			grid.setDeadCells(toTerminateCells);
			grid.setAliveCells(newGeneration);
			grid.forceUpdate(); //fa grid.repaint(); ogni 4 secondi (aggiustiamo poi);
			System.out.println("new" + newGeneration.size());
			System.out.println("to" + toTerminateCells.size());
			System.out.println("possible" + possibleFutureGeneration.size());
			toTerminateCells.clear();
			possibleFutureGeneration.clear();
			System.out.println(actualGeneration.size());
			grid.forceUpdate();
		}
	}
	
	private void initCells(){
		cells = new Cell[size][size];
		for(int i = 0;i < size;i++ )
			for(int j = 0;j < size;j++)
				cells[i][j] = new DeadCell(i,j);
	}
	
	private void initThreads(Cleaner[] cleaners,Generator[] generators,Terminator[] terminators){
		for(int i=0;i < cleaners.length;i++){
			cleaners[i] = new Cleaner();
			generators[i] = new Generator();
			terminators[i] = new Terminator();
		}
	}
	private void newGeneration(Cleaner[] cleaners,Generator[] generators,Terminator[] terminators){
		runThreads(cleaners);
		workingPosition = 0;
		runThreads(generators);
		workingPosition = 0;
		runThreads(terminators);
		workingPosition = 0;
		actualGeneration = newGeneration;
	}

	private void runThreads(Thread[] slaves){
		for(int i=0; i < slaves.length;i++)
			slaves[i].run();
		
		for(int i=0; i < slaves.length;i++)
			try {
				slaves[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
	}
	private class Cleaner extends Thread{
		private int i;
		public void run(){
			while(true){
				synchronized (workingPosition){
					if(workingPosition<actualGeneration.size()){
						i = workingPosition++;
					}
					else
						return;
				}
				upDate(i);//potrebbe esserci race condition nelle variabile newGen e deadCells sicuramente
			}
		}

		private void upDate(int index){//race condition
			if(actualGeneration.size() > 0){
				int aliveNeighbors = watchNeighbors(actualGeneration.get(index));
				if(aliveNeighbors == 2 || aliveNeighbors == 3)
					newGeneration.add(actualGeneration.get(index));

				else
					toTerminateCells.add(actualGeneration.get(index));
			}
		}

		private int watchNeighbors(Cell cell) {// guarda i vicini di cell restituisce il numero di cellule vive e incrementa di 1 il campo numberOfN
			int x = cell.getX()/10; // delle cellule morte
			int y = cell.getY()/10;
			int count = 0;
			Cell neighborCell;
			for(int i = -1; i < 2; i++)
				for(int j = -1;j < 2;j++){
					neighborCell = cells[Math.abs(i + x) % (size-1)][Math.abs(j + y) % (size-1)];
					if((i != 0 || j != 0) && neighborCell instanceof LivingCell)
						count++;
					else if(i != 0 || j != 0){
						checkDeadCell((DeadCell)neighborCell);
					}
				}

			return count;
		}
		private void checkDeadCell(DeadCell cell){
			cell.incrementNumbOfN();
			if(cell.getNumbOfN() == 3)
				possibleFutureGeneration.add(cell);
		}
	}



	private class Terminator extends Thread{
		private int i;
		public void run(){
			while(true){
				synchronized (workingPosition){
					if(workingPosition < toTerminateCells.size())
						i = workingPosition++;
					else
						return;
				}
				killCell(i);
			}
		}
		private void killCell(int index) {
			if(toTerminateCells.size() > 0){
				int x = toTerminateCells.get(index).getX()/10;
				int y = toTerminateCells.get(index).getY()/10;
				cells[x][y] = new DeadCell(x,y);
			}
		}
	}
	
	private class Generator extends Thread{
		private int i;
		public void run(){
			while(true){
				synchronized(workingPosition){
					if(workingPosition < possibleFutureGeneration.size())
						i = workingPosition++;
					else
						return;
				}
				checkDeadCell(i);
			}
		}

		private void checkDeadCell(int index){
			DeadCell cell = possibleFutureGeneration.get(index);
			if(cell.getNumbOfN() == 3){
				int x=cell.getX()/10;
				int y=cell.getY()/10;
				Cell newCell=new LivingCell(x,y);
				newGeneration.add(newCell);
				cells[x][y] = newCell;
			}
		}
	}

}
