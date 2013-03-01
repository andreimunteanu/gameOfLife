import javax.swing.ImageIcon;
import javax.swing.JButton;

public class Cell extends JButton{
	private static final long serialVersionUID = 1L;
	public static final int CELL_SIZE = 10;
	private int x;
	private int y;
	protected Cell(int x, int y, String fileName){
		super(new ImageIcon(fileName));
		this.x = x;
		this.y = y;
	}
	
	public int getX(){
		return x;
	}
	
	public int getY(){
		return y;
	}
}
