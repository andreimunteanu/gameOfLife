import javax.swing.ImageIcon;
import javax.swing.JButton;

public abstract class Cell extends JButton{
	private static final long serialVersionUID = 1L;
	public static final int CELL_SIZE = 10;
	protected Cell(int x, int y, String fileName){
		super(new ImageIcon(fileName));
		setBounds(x * Cell.CELL_SIZE, y * Cell.CELL_SIZE, Cell.CELL_SIZE, Cell.CELL_SIZE);
	}
	public abstract void incrementNumbOfN();
	public abstract int getNumbOfN();
}
