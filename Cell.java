import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;


public class Cell extends JButton{
	private int x;
	private int y;
	private static final long serialVersionUID = 1L;
	public static final int CELL_SIZE = 10;
	protected Cell(int x, int y, String fileName){
		super(new ImageIcon(fileName));
		setBounds(x * Cell.CELL_SIZE, y * Cell.CELL_SIZE, Cell.CELL_SIZE, Cell.CELL_SIZE);
		this.x = x;
		this.y = y;
		this.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
			System.out.println("CLICK FIGGA");
				}
			});
	}
	
	public int auxGetX(){
		return x;
	}
	
	public int auxGetY(){
		return y;
	}
}
