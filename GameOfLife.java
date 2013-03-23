import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class GameOfLife extends JFrame {// regole del gioco: premi kill e rimane attivo fin tanto che no lo ripremi o prendi una figura dell'elenco
	private Grid grid;					//premi su una figura e puoi posizionarla finchè non hai premuto su un altro bottone
	private Engine engine;				//una cella rimane morta per sempre finchè non fai clear ci ripremi in modalità non killing o ci metti sopra un figura
	private boolean running = false;	// p.s. puoi posizionare solo il blinker
	private boolean finish = true;
	private int nButtons = 5;
	private final int baseSpeed = 202; 
	private int speed = 40; //default
	private JMenuBar menu;
	private JTextArea textGen;
	private JTextArea textSpeed;
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

	public static void main(String[] args) {
		new GameOfLife();
	}

	public GameOfLife(){
		grid = new Grid(80);
		engine = new Engine(grid);
		initFrame();
		setOff();
	}

	private void initFrame(){
		start = new startButton();
		pause = new pauseButton();
		step = new stepButton();
		reset = new resetButton();
		clear = new clearButton();
		Kill = new killButton();
		oscillators = new Oscillators();
		spaceships = new Spaceships();
		textSpeed = new JTextArea();
		speedSelect = new SpeedSlider();

		pause.setEnabled(false);
		reset.setEnabled(false);

		textSpeed.setText("____________________\n    Select Speed");
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
		getContentPane().add(textSpeed);
		getContentPane().add(speedSelect);
		initMenu();
		resizeGame(40);
		setVisible(true);
	}

	private void removeFrame(){
		getContentPane().remove(grid);
		getContentPane().remove(start);
		getContentPane().remove(pause);
		getContentPane().remove(step);
		getContentPane().remove(reset);
		getContentPane().remove(clear);
		getContentPane().remove(Kill);
		getContentPane().remove(textSpeed);
		getContentPane().remove(speedSelect);
		getContentPane().repaint();
	}

	private void die(){
		System.exit(0);
	}

	private void forceUpdate(){
		getContentPane().repaint();
	}

	private void resizeGame(int size){
		grid.setGridSize(size);
		setSize(grid.getXSize()+ 115, grid.getYSize() + (8 * Cell.CELL_SIZE));
		setResizable(true);
		setLocation(10,10);
		resizeButtons(size);
		grid.forceUpdate();
		getContentPane().repaint();
	}

	private void resizeButtons(int size) {
		int newSize = Cell.CELL_SIZE * size;
		start.setBounds(0, newSize, newSize / 5, 3 * Cell.CELL_SIZE	);
		pause.setBounds(newSize / nButtons, newSize, newSize / nButtons, 3 * Cell.CELL_SIZE	);
		step.setBounds(2 * (newSize  / nButtons), newSize , newSize  / nButtons, 3 * Cell.CELL_SIZE	);
		reset.setBounds(3 * (newSize  / nButtons), newSize , newSize  / nButtons, 3 * Cell.CELL_SIZE	);
		clear.setBounds(4 * (newSize  / nButtons), newSize , newSize  / nButtons, 3 * Cell.CELL_SIZE	);
		Kill.setBounds(newSize,60,115,30);
		oscillators.setBounds(newSize,0,115,30);
		spaceships.setBounds(newSize,30,115,30);
		textSpeed.setBounds(newSize,90,115,30);
		speedSelect.setBounds(newSize,120,115,200);
	}

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
		JMenuItem threadMan = new JMenuItem("Manual");
		JMenuItem size1 = new JMenuItem("40 x 40");
		JMenuItem size2 = new JMenuItem("60 x 60");
		JMenuItem size3 = new JMenuItem("80 x 80");
		textGen = new JTextArea(); // bisogna trovare un modo per allinearla a destra
		textGen.setEditable(false);
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

		thread2.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				synchronized(grid){
					engine.setCoreN(2);
				}
			}});

		thread4.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				synchronized(grid){
					engine.setCoreN(4);
				}
			}});

		thread8.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				synchronized(grid){
					engine.setCoreN(8);
				}
			}});

		thread16.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				synchronized(grid){
					engine.setCoreN(16);
				}
			}});

		size1.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				GameOfLife.this.resizeGame(40);				
			}			
		});

		size2.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				GameOfLife.this.resizeGame(60);				
			}			
		});

		size3.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				GameOfLife.this.resizeGame(80);				
			}			
		});

		edit.add(size);
		edit.add(threads);
		edit.add(debug);
		size.add(size1);
		size.add(size2);
		size.add(size3);
		threads.add(thread2);
		threads.add(thread4);
		threads.add(thread8);
		threads.add(thread16);
		threads.addSeparator();
		threads.add(threadMan);
		file.addSeparator();
		file.add(exit);
		menu.add(file);
		menu.add(edit);
		menu.add(textGen);
		setJMenuBar(menu);
	}

	private void setOff(){
		while(true){
			String spaces = "        ";
			for(int i = 0;i < (grid.getXSize() / Cell.CELL_SIZE);i++)
				spaces += " ";
			textGen.setText(spaces + "Gen " + grid.getGeneration());
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



	private class Oscillators extends JButton{
		protected Oscillators(){
			super("Oscillators");
			setBounds(grid.getXSize(),0,115,20);
			setBackground(Color.WHITE);
			final JPopupMenu oscillators1 = new JPopupMenu();
			final JMenuItem blinker = new JMenuItem("Blinker");
			final JMenuItem pulsar = new JMenuItem("Pulsar");
			blinker.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e){
					grid.setFigure("blinker");
				}
			});
			pulsar.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e){
					grid.setFigure("pulsar");
				}
			});
			oscillators1.add(blinker);
			oscillators1.add(pulsar);
			this.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e){
					running = false;
					while(!finish);
					start.setEnabled(true);
					pause.setEnabled(false);
					oscillators1.show(Oscillators.this,115,0);
				}
			});
		}
	}

	private class Spaceships extends JButton{
		protected Spaceships(){
			super("Spaceships");
			setBounds(grid.getXSize(),30,115,20);
			setBackground(Color.WHITE);
			final JPopupMenu spaceships1 = new JPopupMenu();
			final JMenuItem glider = new JMenuItem("Glider");
			final JMenuItem lwss = new JMenuItem("LWSS");			
			glider.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e){
					grid.setFigure("glider");
				}
			});
			lwss.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e){
					grid.setFigure("lwss");
				}
			});

			spaceships1.add(glider);
			spaceships1.add(lwss);
			this.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e){
					running = false;
					while(!finish);
					start.setEnabled(true);
					pause.setEnabled(false);
					spaceships1.show(Spaceships.this,115,0);
				}
			});
		}
	}

	private class SpeedSlider extends JSlider{
		private SpeedSlider(){
			addChangeListener(new ChangeListener(){
				@Override
				public void stateChanged(ChangeEvent e) {
					JSlider source = (JSlider)e.getSource();
					if (!source.getValueIsAdjusting()) {
						int modifier = (int)source.getValue();
						speed = (-2 * modifier) + baseSpeed + (1000 / (modifier + 1)) - 8; // :D
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

	private class resetButton extends JButton{
		protected resetButton(){
			super("RESET");
			setBounds(3 * (grid.getXSize() / nButtons), grid.getYSize(), grid.getXSize() / nButtons, 3 * Cell.CELL_SIZE	);
			this.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					running = false;
					while(!finish);

					if(grid.isAddingFigure())
						grid.stopAddingFigure();

					grid.loadSnapShot();
					grid.forceUpdate();
					reset.setEnabled(false);
				}
			});
		}
	}

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

