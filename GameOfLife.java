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
	private Integer workingPosition = 0;
	private static int stage = 0;
	private static final int CLEAN_STAGE = 0;
	private static final int TERMINATE_STAGE = 1;
	private static final int GENERATE_STAGE = 2;
	private int speed = 200; //default
	private Vector<Cell> actualGeneration = new Vector<Cell>();
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
		int coreN = 4;//Runtime.getRuntime().availableProcessors();
		Slave[] slaves = new Slave[coreN];
		//initThreads(slaves);
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
					newGeneration(slaves);
					actualGeneration = newGeneration;
					grid.setActualGeneration(actualGeneration); //questo risolve il bug del CLEAR
				}
				//prima cambiavamo solo il riferimento locale ad actualGeneration, lasciando invariato quello in Grid
				toTerminateCells = new Vector<Cell>();
				possibleFutureGeneration = new Vector<Cell>();
				newGeneration=new Vector<Cell>();
				System.out.println(actualGeneration.size());
				grid.forceUpdate();
				finish = true;
			}
		}
	}

	private void newGeneration(Slave[] slaves){ 
		initThreads(slaves);
		startThreads(slaves);
		nextStage();
		workingPosition=0;
		
		initThreads(slaves);
		startThreads(slaves);
		nextStage();
		workingPosition=0;
		System.out.println( stage+" toTerminate" + toTerminateCells.size());
		
		initThreads(slaves);
		startThreads(slaves);
		nextStage();
		workingPosition=0;
		System.out.println(stage +" possible" + possibleFutureGeneration.size());
		System.out.println("newGen" + newGeneration.size());
	}
	
	private void nextStage(){
		stage = (stage + 1) % 3;
	}

	private void initThreads(Slave[] slaves){ //metodo per inizializzare le thread 
		for(int i=0;i < slaves.length;i++)														
			slaves[i] = new Slave();
	}


	private void startThreads(Slave[] slaves){
		//		Synchronizer sync = new Synchronizer(workingPos); 	
		for(int i=0; i < slaves.length;i++)
			slaves[i].start();
		for(int i=0; i < slaves.length;i++)
			try {
				slaves[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	/*private class Synchronizer extends Thread{
		private Integer workingPos;
		public Synchronizer(Integer workingPos){
			this.workingPos=workingPos;
		}
		public void run(){
			System.out.println("Syn started");
			synchronized (workingPos){
				//	if(!initialization)
				workingPos.notifyAll();
			}
			while(true){
				//System.out.println("syncher: thread attive" +runningThreads);
				//if(runningThreads <= 0)
				//return;
				//else
				try {
					sleep(40);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}*/

	private class Slave extends Thread{

		public void run(){
			switch(stage){
			case CLEAN_STAGE:clean(); break;
			case TERMINATE_STAGE:terminate();break;
			case GENERATE_STAGE:generate();break;
			default: break;				
			}

		}
		
		private void clean(){
			int index;
			while(true){
				synchronized(GameOfLife.this){
				//	System.out.println("Cleaner "+ this.getName() +" holdsLock "+ holdsLock(GameOfLife.this));
					if(workingPosition < actualGeneration.size()){
						//System.out.println("Cleaner "+ this.getName() + " in");
						index = workingPosition++;

					}
					else
						return;
					//System.out.println("runningThreads " + runningThreads);
					//System.out.println("Cleaner "+ this.getName() +" holdsLock "+ holdsLock(cleanerWorkingPos));
					//System.out.println("Cleaner "+ this.getName() +" holdsLock "+ holdsLock(GameOfLife.this) +" exiting");
				}
				upDate(index);//potrebbe esserci race condition nelle variabile newGen e deadCells sicuramente
			}
		}

		private void upDate(int index){//race condition
			if(actualGeneration.size() > 0){
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
						watchDeadCell(neighborCell);
					}
				}

			return count;
		}

		private void watchDeadCell(Cell cell){
			grid.incrementNumbOfN(cell);
			if(grid.getNumbOfN(cell) == 1)
				possibleFutureGeneration.add(cell);
		}

		private void terminate(){
			int index;
			while(true){
				synchronized (GameOfLife.this){

					if(workingPosition < toTerminateCells.size())
						index = workingPosition++;
					else
						return;
				}	
				killCell(index);
			}
		}

		private void killCell(int index) {
			if(toTerminateCells.size() > 0){

				grid.kill(toTerminateCells.get(index));

			}
		}

		private void generate(){
			int index;
			while(true){
				synchronized(GameOfLife.this){
					if(workingPosition < possibleFutureGeneration.size()){
						index = workingPosition++;
						//System.out.println("Deadlock");
						System.out.println("Generator "+ this.getName() +" holdsLock "+ holdsLock(GameOfLife.this));
					}
					else
						return;
				}
				checkDeadCell(index);
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
