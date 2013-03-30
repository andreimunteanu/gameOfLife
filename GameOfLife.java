import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * The GameOfLife class implements a resizable frame that contains: 
 * <ul>
 * <li> a grid of cells
 * <li> buttons to interact with the game's state (start, pause, reset, clear)
 * <li> a slider to modify the change of generation speed. 
 * </ul>
 * It allows to: 
 * <ul>
 * <li> toggle the "killing" mode
 * <li> resize the frame
 * <li> change the number of the working threads
 * <li> toggle the debug mode, 
 * <li> load and save on file the actual generation
 * <li> position on the grid a "figure", particular generation of cells.
 * </ul>
 * 
 * @author <A HREF="mailto:niccolo.marastoni@studenti.univr.it">Niccolò Marastoni</A>
 * @author <A HREF="mailto:andrei.munteanu@studenti.univr.it">Andrei Munteanu</A>
 * @version 1.0
 * 
 */

public class GameOfLife extends JFrame {

	/*
	 * The grid where all the cells will roam
	 */
	private Grid grid;

	/*
	 * The game engine
	 */
	private Engine engine;			

	/*
	 * Toggles the "running" state of the game, used mainly by the "Start" and "Pause" buttons
	 */
	private boolean running = false;	

	/*
	 * Set by the game main loop, used to not interfere with the engine while it's working
	 */
	private boolean finish = true;

	/*
	 * Number of buttons in the lower area, used to customize the size while keeping proportions
	 */
	private int nButtons = 5;

	/*
	 * variable used to customize the speed via the speed selector slider
	 */
	private final int baseSpeed = 202;

	/*
	 * initial speed of the game, measured in milliseconds between each generation
	 * (smaller number equals faster speed)
	 */
	private int speed = 80;

	/*
	 * menu bar
	 */
	private JMenuBar menu;

	/*
	 * shows the number of the actual generation, constantly refreshed
	 */
	private JTextArea textGen;

	/*
	 * static text area 
	 */
	private JTextArea textSpeed;

	/*
	 * used to get the user's input on a custom number of threads to be used by the engine
	 */
	private JTextField threadText;

	/*
	 * the "Start" button
	 */
	private startButton start;

	/*
	 * the "Pause" button
	 */
	private pauseButton pause;

	/*
	 * the "Step" button
	 */
	private stepButton step;

	/*
	 * the "Reset" button
	 */
	private resetButton reset;

	/*
	 * the "Clear" button
	 */
	private clearButton clear;

	/*
	 * the "Kill" button
	 */
	private killButton Kill;

	/*
	 * the "Oscillators" button
	 */
	private Oscillators oscillators;

	/*
	 * the "Spaceships" button
	 */
	private Spaceships spaceships;

	/*
	 * the "Speed" slider
	 */
	private SpeedSlider speedSelect;

	/*
	 * initial size of the grid
	 */
	private int initialSize =  60 * Cell.CELL_SIZE;

	/*
	 * 
	 */
	private JMenu load;

	/*
	 * 
	 */
	private File dir;

	/*
	 * 
	 */
	private Set<String> savedFiles;

	/**
	 * Starts the game.
	 * 
	 * @param args
	 */
	JMenu file;
	public static void main(String[] args) {
		new GameOfLife();
	}

	/**
	 * Constructs a the game: it initializes the frame and its components, 
	 * the grid, the engine and the saving directory.
	 */
	public GameOfLife(){
		grid = new Grid(80);
		engine = new Engine(grid);
		dir = new File(grid.getSaveDir());
		savedFiles = new HashSet<String>();
		initFrame();
		runGame();
	}

	/*
	 * Initializes the frame's size and components.
	 */
	private void initFrame(){
		start = new startButton();
		pause = new pauseButton();
		step = new stepButton();
		reset = new resetButton();
		clear = new clearButton();
		Kill = new killButton();
		oscillators = new Oscillators();
		spaceships = new Spaceships();
		textGen = new JTextArea(); 
		textSpeed = new JTextArea();
		speedSelect = new SpeedSlider();

		textGen.setEditable(false);
		pause.setEnabled(false);
		reset.setEnabled(false);

		textSpeed.setText("____________________\n    Select Speed");
		textSpeed.setEditable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
		setSize(initialSize, initialSize + (8 * Cell.CELL_SIZE));
		setResizable(false);

		setLocation(10,10);
		getContentPane().add(grid);
		getContentPane().add(start);
		getContentPane().add(pause);
		getContentPane().add(step);
		getContentPane().add(reset);
		getContentPane().add(clear);
		getContentPane().add(Kill);
		getContentPane().add(oscillators);
		getContentPane().add(spaceships);
		getContentPane().add(textGen);
		getContentPane().add(textSpeed);
		getContentPane().add(speedSelect);
		initMenu();
		resizeGame(40);
		setVisible(true);
	}

	/*
	 * Closes the game.
	 */
	private void die(){
		System.exit(0);
	}

	/*
	 * Resizes the frame's dimension based on a variable integer.
	 * 
	 * @param size
	 * 				frame's size
	 */		
	private void resizeGame(int size){
		grid.setGridSize(size);
		setSize(grid.getXSize()+ 115, grid.getYSize() + (8 * Cell.CELL_SIZE));
		setResizable(true);
		setLocation(10,10);
		positionButtons(size);
		grid.forceUpdate();
		getContentPane().repaint();
	}

	/*
	 * Positions the frame's buttons according to its size.
	 * 
	 */
	private void positionButtons(int size) {
		int newSize = Cell.CELL_SIZE * size;
		start.setBounds(0, newSize, newSize / 5, 3 * Cell.CELL_SIZE	);
		pause.setBounds(newSize / nButtons, newSize, newSize / nButtons, 3 * Cell.CELL_SIZE	);
		step.setBounds(2 * (newSize  / nButtons), newSize , newSize  / nButtons, 3 * Cell.CELL_SIZE	);
		reset.setBounds(3 * (newSize  / nButtons), newSize , newSize  / nButtons, 3 * Cell.CELL_SIZE	);
		clear.setBounds(4 * (newSize  / nButtons), newSize , newSize  / nButtons, 3 * Cell.CELL_SIZE	);
		oscillators.setBounds(newSize,0,115,30);
		spaceships.setBounds(newSize,30,115,30);
		Kill.setBounds(newSize,60,115,30);
		textGen.setBounds(newSize,90, 115, 20);
		textSpeed.setBounds(newSize,111,115,30);
		speedSelect.setBounds(newSize,141,115,200);
	}

	/*
	 * Takes the manual input from the user to set an arbitrary number of threads.
	 */
	private class textActionListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			String text = threadText.getText();
			engine.setCoreN(Integer.parseInt(text));
		}
	}

	/*
	 * Action listener for the various resize options of the game.
	 */
	private class sizeActionListener implements ActionListener{
		private int size;
		public sizeActionListener(int size){
			this.size = size;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			GameOfLife.this.resizeGame(size);				
		}	
	}

	/*
	 * Sets the number of the threads which work on the cells.
	 */
	private class threadActionListener implements ActionListener{
		private int coreN;
		public threadActionListener(int coreN){
			this.coreN = coreN;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			synchronized(grid){
				engine.setCoreN(coreN);
			}
		}
	}

	/*
	 * Pauses the game and calls functions in grid to save the game to a text file.
	 */
	private class saveActionListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			running = false;
			while(!finish);
			grid.saveSnapshot();
			grid.saveToDisk();
		}
	}
	
	/*
	 * Initializes the menu bar.
	 */
	private void initMenu() {
		menu = new JMenuBar();
		menu.setOpaque(true);
		menu.setBackground(Color.WHITE);
		menu.setPreferredSize(new Dimension(0,20));
		JMenu file = new JMenu("File");
		this.file= file;
		JMenu size = new JMenu("Size");
		JMenu threads = new JMenu("Threads");
		JMenu edit = new JMenu("Edit");
		JMenuItem save = new JMenuItem("Save");
		load = new JMenu("Load");
		JMenuItem exit = new JMenuItem("Exit");
		JMenuItem debug = new JMenuItem("Debug");
		JMenuItem thread2 = new JMenuItem("2");
		JMenuItem thread4 = new JMenuItem("4");
		JMenuItem thread8 = new JMenuItem("8");
		JMenuItem thread16 = new JMenuItem("16");
		JMenu threadMan = new JMenu("Manual");
		threadText = new JTextField(3);
		JMenuItem size1 = new JMenuItem("40 x 40");
		JMenuItem size2 = new JMenuItem("50 x 50");
		JMenuItem size3 = new JMenuItem("60 x 60");
		JMenuItem size4 = new JMenuItem("70 x 70");
		threadText.addActionListener(new textActionListener());

		load.setEnabled(false);
		save.addActionListener(new saveActionListener());
		exit.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				GameOfLife.this.die();
			} });

		debug.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				engine.toggleDebug();				
			}});

		thread2.addActionListener(new threadActionListener(2));
		thread4.addActionListener(new threadActionListener(4));
		thread8.addActionListener(new threadActionListener(8));
		thread16.addActionListener(new threadActionListener(16));

		size1.addActionListener(new sizeActionListener(40));
		size2.addActionListener(new sizeActionListener(50));
		size3.addActionListener(new sizeActionListener(60));
		size4.addActionListener(new sizeActionListener(70));

		edit.add(size);
		edit.add(threads);
		edit.add(debug);
		size.add(size1);
		size.add(size2);
		size.add(size3);
		size.add(size4);
		threads.add(thread2);
		threads.add(thread4);
		threads.add(thread8);
		threads.add(thread16);
		threads.addSeparator();
		threads.add(threadMan);
		threadMan.add(threadText);
		file.add(save);
		file.add(load);
		file.addSeparator();
		file.add(exit);
		menu.add(file);
		menu.add(edit);
		setJMenuBar(menu);
	}

	/*
	 * Each save file in the sub-menu "load" is a SaveButton
	 */
	private class SaveButton extends JMenuItem{
		public SaveButton(String name){
			super(name);
		}
	}
	
	/*
	 * Calls a function in grid to load from disk the corresponding file.
	 */
	private class saveButtonActionListener implements ActionListener{
		private String name;
		public saveButtonActionListener(String name){
			this.name = name;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			grid.loadFromDisk(name);
		}
	}

	/*
	 * Runs the game, prints the number of the generation.
	 */
	private void runGame(){
		while(true){			
			textGen.setText("       | Gen " + grid.getGeneration() + " |");
			
			if(load.isShowing())
				checkFiles();

			try {
				Thread.sleep(speed);
			} catch (InterruptedException e) {
				System.err.println("Error in setOff() => " + e.getMessage());
			}
			if(running){
				finish = false;
				synchronized(grid){
					engine.computeNextGen();					
				}				
				menu.repaint();
				grid.forceUpdate();

				finish = true;
			}
		}
	}
	
	/*
	 * Verifies if there are saved files and adds them to the load menu.
	 */
	private void checkFiles(){
		File[] list = dir.listFiles();
		if(grid.checkSaved() && list != null && list.length != 0){
			load.setEnabled(true);
			for(File f : list){
				String name = f.toString();
				if(!savedFiles.contains(name)){
					savedFiles.add(name);
					SaveButton save = new SaveButton(name);
					save.addActionListener(new saveButtonActionListener(name));
					load.add(save);
				}
			}
		}
	}

	/*
	 * Sets the figure's name and shows a pop-up window to inform the user 
	 * that the game is waiting for the position of the figure.
	 */
	private class figuresActionListener implements ActionListener{
		private String figureName;
		final JPopupMenu waitingWindow;
		public figuresActionListener( String figureName){
			this.figureName = figureName;
			waitingWindow = new JPopupMenu();
			waitingWindow.add(new JMenuItem("Waiting for\n  position"));
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			grid.setFigure(figureName);
			waitingWindow.show(GameOfLife.this, grid.getXSize(), 0);
		}
	}

	/*
	 * Oscillators button. When pressed it shows a list of oscillators
	 * that the user can put on the grid.
	 */
	private class Oscillators extends JButton{
		protected Oscillators(){
			super("Oscillators");
			setBounds(grid.getXSize(),0,115,20);
			setBackground(Color.WHITE);
			final JPopupMenu oscillatorsMenu = new JPopupMenu();
			final JMenuItem blinker = new JMenuItem("Blinker");
			final JMenuItem pulsar = new JMenuItem("Pulsar");
			blinker.addActionListener(new figuresActionListener("blinker"));
			pulsar.addActionListener(new figuresActionListener("pulsar"));
			oscillatorsMenu.add(blinker);
			oscillatorsMenu.add(pulsar);
			this.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e){
					running = false;
					while(!finish);
					start.setEnabled(true);
					pause.setEnabled(false);
					oscillatorsMenu.show(Oscillators.this,115,0);
				}
			});
		}
	}

	/*
	 * Spaceships button. When pressed it shows a list of spaceships 
	 * that the user can print on the screen.
	 */
	private class Spaceships extends JButton{
		protected Spaceships(){
			super("Spaceships");
			setBounds(grid.getXSize(),30,115,20);
			setBackground(Color.WHITE);
			final JPopupMenu spaceshipsMenu = new JPopupMenu();
			final JMenuItem glider = new JMenuItem("Glider");
			final JMenuItem lwss = new JMenuItem("LWSS");
			final JMenuItem fireworks = new JMenuItem("Fireworks");

			glider.addActionListener(new figuresActionListener("glider"));
			lwss.addActionListener(new figuresActionListener("lwss"));
			fireworks.addActionListener(new figuresActionListener("fireworks"));

			spaceshipsMenu.add(glider);
			spaceshipsMenu.add(lwss);
			spaceshipsMenu.add(fireworks);
			this.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e){
					running = false;
					while(!finish);
					start.setEnabled(true);
					pause.setEnabled(false);
					spaceshipsMenu.show(Spaceships.this,115,0);
				}
			});
		}
	}

	/*
	 * Slider that modifies the changing generation speed.
	 * min = 1 sec, max = 3 millisec.
	 */
	private class SpeedSlider extends JSlider{
		private SpeedSlider(){
			addChangeListener(new ChangeListener(){
				@Override
				public void stateChanged(ChangeEvent e) {
					JSlider source = (JSlider)e.getSource();
					if (!source.getValueIsAdjusting()) {
						int modifier = (int)source.getValue();
						speed = (-2 * modifier) + baseSpeed + (1000 / (modifier + 1)) - 8; 
					}
				}});
			Hashtable labelTable = new Hashtable();
			labelTable.put( new Integer( 1 ), new JLabel("1") );
			labelTable.put( new Integer( 50 ), new JLabel("50") );
			labelTable.put( new Integer( 100 ), new JLabel("100") );
			setLabelTable( labelTable );
			setMajorTickSpacing(10);
			setPaintTicks(true);
			setPaintLabels(true);
			setOrientation(JSlider.VERTICAL);
		}
	}

	/*
	 * Start button that makes the game run.
	 */
	private class startButton extends JButton{
		protected startButton(){
			super("START");
			setBounds(0, grid.getYSize(), grid.getXSize() / 5, 3 * Cell.CELL_SIZE	);
			this.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					pause.setEnabled(true);
					reset.setEnabled(true);
					if(grid.isAddingFigure())
						grid.stopAddingFigure();


					grid.resetGeneration();
					running = true;
					start.setEnabled(false);
				}
			});
		}
	}

	/*
	 * Pause button. Pauses the game.
	 */
	private class pauseButton extends JButton{
		protected pauseButton(){
			super("PAUSE");
			setBounds(grid.getXSize() / nButtons, grid.getYSize(), grid.getXSize() / nButtons, 3 * Cell.CELL_SIZE	);
			this.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					running = false;
					start.setEnabled(true);
					if(grid.isAddingFigure())
						grid.stopAddingFigure();
					pause.setEnabled(false);
				}
			});
		}
	}

	/*
	 * Step button. When pressed, it changes the actual generation to the next and pauses the game.
	 */
	private class stepButton extends JButton{
		protected stepButton(){
			super("STEP");
			setBounds(2 * (grid.getXSize() / nButtons), grid.getYSize(), grid.getXSize() / nButtons, 3 * Cell.CELL_SIZE	);
			this.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					reset.setEnabled(true);
					pause.setEnabled(false);
					start.setEnabled(true);
					if(running)
						running = false;

					else{
						if(grid.isAddingFigure())
							grid.stopAddingFigure();
						engine.computeNextGen();					
						grid.forceUpdate();						
					}
				}
			});
		}
	}

	/*
	 * Reset button. Resets the game to the first generation.
	 */
	private class resetButton extends JButton{
		protected resetButton(){
			super("RESET");
			setBounds(3 * (grid.getXSize() / nButtons), grid.getYSize(), grid.getXSize() / nButtons, 3 * Cell.CELL_SIZE	);
			this.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					running = false;
					try {
						Thread.sleep(20);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					reset.setEnabled(false);
					pause.setEnabled(false);
					start.setEnabled(true);
					
					while(!finish);

					if(grid.isAddingFigure())
						grid.stopAddingFigure();

					grid.loadSnapShot();
					grid.forceUpdate();
				}
			});
		}
	}

	/*
	 *Clear button. When pressed it clears the grid.
	 */
	private class clearButton extends JButton{
		protected clearButton(){
			super("CLEAR");
			setBounds(4 * (grid.getXSize() / nButtons), grid.getYSize(), grid.getXSize() / nButtons, 3 * Cell.CELL_SIZE	);
			this.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					running = false;
					try {
						Thread.sleep(20);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					reset.setEnabled(false);
					pause.setEnabled(false);
					start.setEnabled(true);
					while(!finish);

					if(grid.isAddingFigure())
						grid.stopAddingFigure();

					grid.clearGrid();
					grid.resetGeneration();
					grid.forceUpdate();
				}
			});
		}
	}

	/*
	 * Kill button. Toggles the "killing" mode.
	 */
	private class killButton extends JButton{
		protected killButton(){
			super("KILL");
			setBounds(grid.getXSize(),60,115,20);
			this.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					if(grid.isAddingFigure())
						grid.stopAddingFigure();
					grid.setKilling();
				}
			});
		}
	}
}
