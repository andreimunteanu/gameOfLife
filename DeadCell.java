
public class DeadCell extends Cell{
	private int numberOfN;
	private static final long serialVersionUID = 1L;
	protected DeadCell(int x, int y) {
		super(x, y, "deadCell.gif");
	}
	
	public synchronized int getNumbOfN(){
		return numberOfN;
	}
	
	public synchronized void incrementNumbOfN(){
		numberOfN++;
	}
}
