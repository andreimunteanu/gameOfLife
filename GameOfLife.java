import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

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
 * 
 * @author 
 *
 */
public class GameOfLife extends JFrame {// regole del gioco: premi kill e rimane attivo fin tanto che no lo ripremi o prendi una figura dell'elenco
	/*
	 * 
	 */
	private Grid grid;					//premi su una figura e puoi posizionarla finchè non hai premuto su un altro bottone
	private Engine engine;				//una cella rimane morta per sempre finchè non fai clear ci ripremi in modalità non killing o ci metti sopra un figura
	private boolean running = false;	// p.s. puoi posizionare solo il blinker
	private boolean finish = true;
	private int nButtons = 5;
	private final int baseSpeed = 202; 
	private int speed = 80; //default
	private JMenuBar menu;
	private JTextArea textGen;
	private JTextArea textSpeed;
	private JTextField threadText;
	private startButton start;
	private pauseButton pause;
	private stepButton step;
	private resetButton reset;
	private clearButton clear;
	private killButton Kill;
	private Oscillators oscillators;
	private Spaceships spaceships;
	private SpeedSlider speedSelect;
	private int initialSize =  60 * Cell.CELL_SIZE;
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new GameOfLife();
	}
	
	/*
	 * 
	 */
	public GameOfLife(){
		grid = new Grid(80);
		engine = new Engine(grid);
		initFrame();
		setOff();
	}

	/*
	 * 
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
		textGen = new JTextArea(); // bisogna trovare un modo per allinearla a destra
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
		setResizable(true);

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
	 * 
	 */
	private void die(){
		System.exit(0);
	}
	
	/*
	 * 
	 */
	private void forceUpdate(){
		getContentPane().repaint();
	}
	
	/*
	 * 
	 * @param size
	 */
	private void resizeGame(int size){
		grid.setGridSize(size);
		setSize(grid.getXSize()+ 115, grid.getYSize() + (8 * Cell.CELL_SIZE));
		setResizable(true);
		setLocation(10,10);
		resizeButtons(size);
		grid.forceUpdate();
		getContentPane().repaint();
	}
	
	/*
	 * 
	 * 
	 */
	private void resizeButtons(int size) {
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
	 * 
	 * 
	 *
	 */
	private class textActionListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			String text = threadText.getText();
			engine.setCoreN(Integer.parseInt(text));
		}
	}
	
	/*
	 * 
	 * 
	 *
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
	 * 
	 * @author 
	 *
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
	 * 
	 */
	private void initMenu() {
		menu = new JMenuBar();
		menu.setOpaque(true);
		menu.setBackground(Color.WHITE);
		menu.setPreferredSize(new Dimension(0,20));
		JMenu file = new JMenu("File");
		JMenu size = new JMenu("Size");
		JMenu threads = new JMenu("Threads");
		JMenu edit = new JMenu("Edit");

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
		file.addSeparator();
		file.add(exit);
		menu.add(file);
		menu.add(edit);
		setJMenuBar(menu);
	}
	
	/*
	 * 
	 */
	private void setOff(){
		while(true){
			
			textGen.setText("       | Gen " + grid.getGeneration() + " |");
			
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
	 * 
	 * 
	 *
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
	 * 
	 * 
	 *
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
	 * 
	 * 
	 *
	 */
	private class Spaceships extends JButton{
		protected Spaceships(){
			super("Spaceships");
			setBounds(grid.getXSize(),30,115,20);
			setBackground(Color.WHITE);
			final JPopupMenu spaceshipsMenu = new JPopupMenu();
			final JMenuItem glider = new JMenuItem("Glider");
			final JMenuItem lwss = new JMenuItem("LWSS");			
			glider.addActionListener(new figuresActionListener("glider"));
			lwss.addActionListener(new figuresActionListener("lwss"));
			spaceshipsMenu.add(glider);
			spaceshipsMenu.add(lwss);
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
	 * 
	 * 
	 *
	 */
	private class SpeedSlider extends JSlider{
		private SpeedSlider(){
			addChangeListener(new ChangeListener(){
				@Override
				public void stateChanged(ChangeEvent e) {
					JSlider source = (JSlider)e.getSource();
					if (!source.getValueIsAdjusting()) {
						int modifier = (int)source.getValue();
						speed = (-2 * modifier) + baseSpeed + (1000 / (modifier + 1)) - 8; // D:
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
	 * 
	 * 
	 *
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
	 * 
	 * 
	 *
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
	 * 
	 * 
	 *
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
	 * 
	 * 
	 *
	 */
	private class resetButton extends JButton{
		protected resetButton(){
			super("RESET");
			setBounds(3 * (grid.getXSize() / nButtons), grid.getYSize(), grid.getXSize() / nButtons, 3 * Cell.CELL_SIZE	);
			this.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					reset.setEnabled(false);
					pause.setEnabled(false);
					start.setEnabled(true);
					running = false;
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
	 * 
	 *
	 *
	 */
	private class clearButton extends JButton{
		protected clearButton(){
			super("CLEAR");
			setBounds(4 * (grid.getXSize() / nButtons), grid.getYSize(), grid.getXSize() / nButtons, 3 * Cell.CELL_SIZE	);
			this.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					pause.setEnabled(false);
					start.setEnabled(true);
					running = false;
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
	 * 
	 * 
	 *
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

