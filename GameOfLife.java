import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;


public class GameOfLife extends JFrame {
	private Grid grid;
	private boolean running = false;
	private boolean finish = true;
	private Integer generatorWorkingPos = 0;
	private Integer cleanerWorkingPos = 0;
	private Integer terminatorWorkingPos = 0;
	private Integer runningThreads = 4;
	private boolean initialization = true;
	private int speed = 200; //default
	//private Cell[][] cells;
	private  Vector<Cell> actualGeneration = new Vector<Cell>();
	private Vector<Cell> newGeneration = new Vector<Cell>();
	private Vector<Cell> toTerminateCells = new Vector<Cell>(); 
	private Vector<Cell> possibleFutureGeneration = new Vector<Cell>(); 

	public static void main(String[] args) {
		new GameOfLife();
	}

	public GameOfLife(){
		super("Game Of Life");
		grid = new Grid(actualGeneration);
		initFrame();
		getContentPane().add(grid);
		getContentPane().add(new startButton());
		getContentPane().add(new pauseButton());
		getContentPane().add(new clearButton());
		initMenu();
		setVisible(true);
		setOff();
	}

	private void initFrame(){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
		setSize(grid.getXSize(), grid.getYSize() + (8 * Cell.CELL_SIZE));
		setResizable(false);
		setLocation(10,10);
		//addCellsToFrame();
		//initMenu();
		//setVisible(true);
	}


	private void initMenu() {
		JMenuBar menu = new JMenuBar();
		menu.setOpaque(true);
		menu.setBackground(Color.WHITE);
		menu.setPreferredSize(new Dimension(0,20));
		JMenu file = new JMenu("File");
		JMenu size = new JMenu("Size");
		JMenu edit = new JMenu("Edit");
		JMenuItem exit = new JMenuItem("Exit");
		JMenuItem size1 = new JMenuItem("50 x 50");
		JMenuItem size2 = new JMenuItem("100 x 100");
		JMenuItem size3 = new JMenuItem("200 x 200");
		edit.add(size);
		size.add(size1);
		size.add(size2);
		size.add(size3);
		file.addSeparator();
		file.add(exit);
		menu.add(file);
		menu.add(edit);
		setJMenuBar(menu);
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
		int coreN =2; //Runtime.getRuntime().availableProcessors();
		Cleaner[] cleaners = new Cleaner[coreN];
		Terminator[] terminators = new Terminator[coreN];
		Generator[] generators = new Generator[coreN];
		initThreads(cleaners,generators,terminators);
		//grid = new Grid(actualGeneration);
		//	grid.forceUpdate();
		grid.test();
		while(true){
			try {
				Thread.sleep(speed);
			} catch (InterruptedException e) {
				System.err.println("Error in setOff() => " + e.getMessage());
			}
			if(running){

				//grid.removeCells(toTerminateCells);
				//grid.addCells(newGeneration);
				//grid.forceUpdate(); //fa grid.repaint(); ogni 4 secondi (aggiustiamo poi);
				//System.out.println("new" + newGeneration.size());
				//System.out.println("to" + toTerminateCells.size());
				//System.out.println("possible" + possibleFutureGeneration.size());
				finish = false;
				synchronized(grid){
					actualGeneration = grid.getActualGeneration();
					//se l'utente clicca qui sono cazzi
					newGeneration(cleaners,generators,terminators); //o anche durante questo
					actualGeneration = newGeneration;
					grid.setActualGeneration(actualGeneration); //questo risolve il bug del CLEAR
				}
				//prima cambiavamo solo il riferimento locale ad actualGeneration, lasciando invariato quello in Grid
				toTerminateCells = new Vector<Cell>();
				possibleFutureGeneration = new Vector<Cell>();
				newGeneration=new Vector<Cell>();
				//System.out.println(actualGeneration.size());
				grid.forceUpdate();
				finish = true;
			}
		}
	}

	private void initThreads(Cleaner[] cleaners,Generator[] generators,Terminator[] terminators){ //metodo per inizializzare le thread 
		for(int i=0;i < cleaners.length;i++){														
			cleaners[i] = new Cleaner();
			generators[i] = new Generator();
			terminators[i] = new Terminator();
		}
		startThreads(cleaners,cleanerWorkingPos);

		//System.out.println("Cleaner done");

		startThreads(generators,generatorWorkingPos);


		startThreads(terminators, terminatorWorkingPos);

		initialization = false;
		resetPositions();

	}

	private void resetPositions() {
		cleanerWorkingPos = 0;
		generatorWorkingPos = 0;
		terminatorWorkingPos = 0;
	}

	private void startThreads(Thread[] slaves,Integer workingPos){
		runningThreads = 2;
		Synchronizer sync = new Synchronizer(workingPos); 	
		for(int i=0; i < slaves.length;i++)
			slaves[i].start();
		sync.start();
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
		runThreads(cleanerWorkingPos);
		System.out.println("runTs "+runningThreads);


		runThreads(generatorWorkingPos);
		System.out.println("runTs "+runningThreads);

		runThreads(terminatorWorkingPos);
		System.out.println("runTs "+runningThreads);
		resetPositions();
	}

	private void runThreads(Integer workingPos){
		runningThreads = 2;
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
			System.out.println("Syn started");
			synchronized (workingPos){
				if(!initialization)
					workingPos.notifyAll();
			}
			while(true){
				//System.out.println("syncher: thread attive" +runningThreads);
				if(runningThreads <= 0)
					return;
				else
					try {
						sleep(40);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		}
	}

	private class Cleaner extends Thread{
		private int i;
		private boolean work = true;
		public void run(){
			while(true){
				synchronized(cleanerWorkingPos){
					System.out.println("Cleaner "+ this.getName() +" holdsLock "+ holdsLock(cleanerWorkingPos));
					if(cleanerWorkingPos < actualGeneration.size()){
						work=true;
					System.out.println("Cleaner "+ this.getName() + " in");
						i = cleanerWorkingPos++;

					}
					else
						try {
							work=false;
							runningThreads--;
							//System.out.println("runningThreads " + runningThreads);
							//System.out.println("Cleaner "+ this.getName() +" holdsLock "+ holdsLock(cleanerWorkingPos));

							cleanerWorkingPos.wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					System.out.println("Cleaner "+ this.getName() +" holdsLock "+ holdsLock(cleanerWorkingPos) +" exiting");
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
	private class startButton extends JButton{
		protected startButton(){
			super("START");
			setBounds(0, grid.getYSize(), grid.getXSize() / 3, 3 * Cell.CELL_SIZE	);
			this.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					running = true;
				}
			});
		}
	}

	private class pauseButton extends JButton{
		protected pauseButton(){
			super("PAUSE");
			setBounds(grid.getXSize() / 3, grid.getYSize(), grid.getXSize() / 3, 3 * Cell.CELL_SIZE	);
			this.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					running = false;
				}
			});
		}
	}

	private class clearButton extends JButton{
		protected clearButton(){
			super("CLEAR");
			setBounds(2 * (grid.getXSize() / 3), grid.getYSize(), grid.getXSize() / 3, 3 * Cell.CELL_SIZE	);
			this.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					boolean temp = running;
					running = false;
					while(!finish); //BUSY WAITING :D :D :) :| :( D: anche se poco
					//e non risolve comunque il bug (prova a fare clear dopo la pausa)
					//c'è pieno di bug in realtà, forse la logica è da rivedere completamente
					//per esempio, se facciamo una figura che poi scompare completamente, poi premi su "CLEAR"
					//e dice che actual generation ha 9 elementi invece di 0
					possibleFutureGeneration = new Vector<Cell>();
					newGeneration = new Vector<Cell>();
					toTerminateCells = new Vector<Cell>();
					grid.clearGrid();
					actualGeneration = new Vector<Cell>();
					grid.setActualGeneration(actualGeneration);
					running = temp;
					grid.forceUpdate();
					getContentPane().repaint();
				}
			});
		}
	}
}
