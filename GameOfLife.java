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
import javax.swing.JTextArea;


public class GameOfLife extends JFrame {
	private Grid grid;
	private Engine engine;
	private int coreN = 4;
	private boolean running = false;
	private boolean finish = true;
	private int nButtons = 5;
	private Integer workingPosition = 0;// serve ancora?? NO :D
	private int speed = 100; //default
	private JMenuBar menu;
	private JTextArea textGen;
	private startButton Start;
	private pauseButton Pause;
	private stepButton Step;
	private resetButton Reset;
	private clearButton Clear;
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
		Start = new startButton();
		Pause = new pauseButton();
		Step = new stepButton();
		Reset = new resetButton();
		Clear = new clearButton();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
		setSize(initialSize, initialSize + (8 * Cell.CELL_SIZE));
		setResizable(true);
		
		setLocation(10,10);
		getContentPane().add(grid);
		getContentPane().add(Start);
		getContentPane().add(Pause);
		getContentPane().add(Step);
		getContentPane().add(Reset);
		getContentPane().add(Clear);
		initMenu();
		resizeGame(40);
		setVisible(true);
	}

	private void removeFrame(){
		getContentPane().remove(grid);
		getContentPane().remove(Start);
		getContentPane().remove(Pause);
		getContentPane().remove(Step);
		getContentPane().remove(Reset);
		getContentPane().remove(Clear);
		getContentPane().repaint();
	}

	private void die(){
		System.exit(0);
	}

	private void resizeGame(int size){
		grid.setGridSize(size);
		setSize(grid.getXSize(), grid.getYSize() + (8 * Cell.CELL_SIZE));
		setResizable(true);
		setLocation(10,10);
		resizeButtons(size);
		grid.forceUpdate();
		getContentPane().repaint();
	}

	private void resizeButtons(int size) {
		int newSize = Cell.CELL_SIZE * size;
		Start.setBounds(0, newSize, newSize / 5, 3 * Cell.CELL_SIZE	);
		Pause.setBounds(newSize / nButtons, newSize, newSize / nButtons, 3 * Cell.CELL_SIZE	);
		Step.setBounds(2 * (newSize  / nButtons), newSize , newSize  / nButtons, 3 * Cell.CELL_SIZE	);
		Reset.setBounds(3 * (newSize  / nButtons), newSize , newSize  / nButtons, 3 * Cell.CELL_SIZE	);
		Clear.setBounds(4 * (newSize  / nButtons), newSize , newSize  / nButtons, 3 * Cell.CELL_SIZE	);
	}

	private void initMenu() {
		menu = new JMenuBar();
		menu.setOpaque(true);
		menu.setBackground(Color.WHITE);
		menu.setPreferredSize(new Dimension(0,20));
		JMenu file = new JMenu("File");
		JMenu size = new JMenu("Size");
		JMenu edit = new JMenu("Edit");
		JMenuItem exit = new JMenuItem("Exit");
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
		size.add(size1);
		size.add(size2);
		size.add(size3);
		file.addSeparator();
		file.add(exit);
		menu.add(file);
		menu.add(edit);
		menu.add(textGen);
		setJMenuBar(menu);
	}

	private void setOff(){
		coreN = 4 ;//Runtime.getRuntime().availableProcessors();
		while(true){
			textGen.setText("Gen " + grid.getGeneration());
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

	private class startButton extends JButton{
		protected startButton(){
			super("START");
			setBounds(0, grid.getYSize(), grid.getXSize() / 5, 3 * Cell.CELL_SIZE	);
			this.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					if(!running){
						//	grid.saveSnapShot();
						grid.resetGeneration();
						running = true;
					}
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
					if(running)
						running = false;

					else{
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
					grid.loadSnapShot();
					grid.forceUpdate();
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
					running = false;
					while(!finish);
					grid.clearGrid();
					grid.resetGeneration();
					grid.forceUpdate();
				}
			});
		}
	}
}


