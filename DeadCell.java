
public class DeadCell extends Cell{
	private int numberOfN;
	private static final long serialVersionUID = 1L;
	protected DeadCell(int x, int y) {
		super(x, y, "deadCell.gif");		
	}
	@Override 
	public int getNumbOfN(){
		return numberOfN;
	}
	@Override
	public void incrementNumbOfN(){
		numberOfN++;
	}
}
