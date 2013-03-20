import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;


public abstract class Cell extends JButton{
	private int x;
	private int y;
	private static final long serialVersionUID = 1L;
	public static final int CELL_SIZE = 10;
	protected Cell(int x, int y){
		super();
		setBounds(x * Cell.CELL_SIZE, y * Cell.CELL_SIZE, Cell.CELL_SIZE, Cell.CELL_SIZE);
		this.x = x;
		this.y = y;

	}

	public int auxGetX(){
		return x;
	}

	public int auxGetY(){
		return y;
	}

	public abstract boolean isAliveNow();
	public abstract boolean isAliveNext();
	public abstract void swap();
	public abstract void reset();

	@Override
	public String toString(){
		return "(" + auxGetX() + ", " + auxGetY() + ")";
	}

	


}
