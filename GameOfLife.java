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

	private static Integer workingPosition = 0;
	//private Cell[][] cells;
	private volatile Vector<Cell> actualGeneration = new Vector<Cell>();
	private volatile Vector<Cell> newGeneration = new Vector<Cell>();
	private volatile Vector<Cell> toTerminateCells = new Vector<Cell>(); //da fare cambio di stato nel frame
	private volatile Vector<Cell> possibleFutureGeneration = new Vector<Cell>(); //lista con cellule per la generazione futura non tutte ne fanno parte
	// quelle che cambiano stato n b c'e da fare il cambio di stato nel frame
	private JFrame main;

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
		int coreN =1; //Runtime.getRuntime().availableProcessors();
		Cleaner[] cleaners = new Cleaner[coreN];
		Terminator[] terminators = new Terminator[coreN];
		Generator[] generators = new Generator[coreN];
		initThreads(cleaners,generators,terminators);
		//grid = new Grid(actualGeneration);
		//	grid.forceUpdate();
		grid.test();
		while(true){
			try {
				Thread.sleep(400);
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
			grid.forceUpdate();
			newGeneration=new Vector<Cell>();
		}
	}

	private void initThreads(Cleaner[] cleaners,Generator[] generators,Terminator[] terminators){
		for(int i=0;i < cleaners.length;i++){
			cleaners[i] = new Cleaner();
			generators[i] = new Generator();
			terminators[i] = new Terminator();
		}
	}
	private void newGeneration(Cleaner[] cleaners,Generator[] generators,Terminator[] terminators){
		//System.out.println(workingPosition+" "+actualGeneration.size());
		workingPosition = 0;
		//System.out.println(cleaners[0].getState());
		runThreads(cleaners);
		//System.out.println("dopo la morte dei cleaners");
		workingPosition = 0;

		runThreads(terminators);
		workingPosition = 0;
		runThreads(generators);
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
					if(workingPosition < actualGeneration.size()){
						//System.out.println("ciao non smetto" +actualGeneration.size());
						i = workingPosition++;

					}
					else{
						//System.out.println("Morto un cleaner se ne fa un altro");
						return;
					}
				}
				upDate(i);//potrebbe esserci race condition nelle variabile newGen e deadCells sicuramente
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
		public void run(){
			//System.out.println("Terminator online");
			while(true){
				synchronized (workingPosition){
					if(workingPosition < toTerminateCells.size()){
						i = workingPosition++;
					}
					else
						return;
				}
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
		public void run(){
			//System.out.println("Generator online");
			while(true){
				synchronized(workingPosition){
					if(workingPosition < possibleFutureGeneration.size()){
						i = workingPosition++;
					}
					else
						return;
				}
				checkDeadCell(i);
			}
		}

		private void checkDeadCell(int index){
			Cell cell = possibleFutureGeneration.get(index);
			if(grid.getNumbOfN(cell) == 3){
				newGeneration.add(grid.createLivingCell(cell));
			}
			else
				grid.resetCell(cell);
		}
	}
	
	private class startButton extends JButton{
		protected startButton(){
			super("START");
			setBounds(0, grid.getYSize(), grid.getXSize() / 3, 3 * Cell.CELL_SIZE	);
			this.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					System.out.println("CLICK START");
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
					System.out.println("CLICK PAUSE");
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
					System.out.println("CLICK CLEAR");
				}
			});
		}
	}
}
