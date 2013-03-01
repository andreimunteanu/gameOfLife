import javax.swing.ImageIcon;
import javax.swing.JButton;

public class Cell extends JButton{
	private static final long serialVersionUID = 1L;
	public static final boolean alive = true;
	private boolean state;
	public Cell(){
		super(new ImageIcon("cell.gif"));
		state = alive;
	}
	private int x;
	private int y;
	public boolean getState(){
		return state;
	}
	public int getX(){
		return x;
	}
	public int getY(){
		return y;
	}

	public Cell(int x, int y){
		this.x = x;
		this.y = y;
	}
	public void setState(boolean state ){
		this.state = state; 
	}
}
