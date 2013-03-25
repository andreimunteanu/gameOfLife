import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 * 
 * 
 *
 */
public abstract class Cell extends JButton{
	/*
	 * 
	 */
	private int x;
	private int y;
	private static final long serialVersionUID = 1L;
	public static final int CELL_SIZE = 10;
	
	/**
	 * 
	 * @param x
	 * @param y
	 */
	protected Cell(int x, int y){
		super();
		setBounds(x * Cell.CELL_SIZE, y * Cell.CELL_SIZE, Cell.CELL_SIZE, Cell.CELL_SIZE);
		this.x = x;
		this.y = y;

	}

	/**
	 * 
	 * @return
	 */
	public int auxGetX(){
		return x;
	}
	
	/**
	 * 
	 * @return
	 */
	public int auxGetY(){
		return y;
	}
	/**
	 * 
	 * @return
	 */
	public abstract boolean isAliveNow();
	
	/**
	 * 
	 * @return
	 */
	public abstract boolean isAliveNext();
	
	/**
	 * 
	 */
	public abstract void changeNow();
	
	/**
	 * 
	 */
	public abstract void swap();
	
	/**
	 * 
	 */
	public abstract void reset();
	
	/**
	 * 
	 * @return
	 */
	public abstract boolean isDefDead(); 
	
	/**
	 * 
	 */
	@Override
	public String toString(){
		return "(" + auxGetX() + ", " + auxGetY() + ")";
	}

	


}
