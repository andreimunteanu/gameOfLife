import javax.swing.JButton;

/**
 * 
 * 
 *
 */
public abstract class Cell extends JButton{
	
	/*
	 * Horizontal position.
	 */
	private int x;
	
	/*
	 * Vertical position.
	 */
	private int y;
	private static final long serialVersionUID = 1L;
	
	/*
	 * Cell's size.
	 */
	public static final int CELL_SIZE = 10;
	
	/**
	 * Constructs a cell in a specific position with a specific size. 
	 * 
	 * @param x
	 * 				vertical position
	 * @param y
	 * 				horizontal position
	 */
	protected Cell(int x, int y){
		super();
		setBounds(x * Cell.CELL_SIZE, y * Cell.CELL_SIZE, Cell.CELL_SIZE, Cell.CELL_SIZE);
		this.x = x;
		this.y = y;

	}

	/**
	 *  Returns the horizontal position of the cell on the grid.
	 * 
	 * @return
	 * 				horizontal position
	 */
	public int auxGetX(){
		return x;
	}
	
	/**
	 * Returns the vertical position of the cell on the grid.
	 * 
	 * @return
	 * 				vertical position
	 */
	public int auxGetY(){
		return y;
	}
	
	/**
	 * Tests if the cell is alive in the actual generation.
	 * 
	 * @return
	 * 				true if the cell is alive
	 */				
	public abstract boolean isAliveNow();
	
	/**
	 * Changes the cell's state in the actual generation.
	 */
	public abstract void changeNow();
	
	/**
	 * Changes the cell's state in the next generation.
	 */
	public abstract void changeNext();
	
	/**
	 * Swaps the cell's state from the next generation to the actual.
	 */
	public abstract void swap();
	
	/**
	 * Reinitializes the cell's state: dead and not definitely dead.
	 * 
	 */
	public abstract void reset();
	
	/**
	 * Tests if the cell is definitely dead.
	 * 
	 * @return
	 * 				true if the cell is definitely dead
	 */
	public abstract boolean isDefDead(); 
	
	/**
	 * Returns a string representation of this cell, containing it's position.
	 * 
	 */
	@Override
	public String toString(){
		return "(" + auxGetX() + ", " + auxGetY() + ")";
	}
}
