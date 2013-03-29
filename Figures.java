/**
 * The Figures class provides a static method to get the positions of the cells for
 * predetermined figures that will be chosen with a button in the main frame.
 * 
 * @author <A HREF="mailto:niccolo.marastoni@studenti.univr.it">Niccolò Marastoni</A>
 * @author <A HREF="mailto:andrei.munteanu@studenti.univr.it">Andrei Munteanu</A>
 * @version 1.0
 * 
 */
public final class Figures {
	
	/**
	 * Every bidimensional array represents an array of coordinates for the cells that will be positioned on the grid.
	 */
	private static final int[][] blinker = {{0,-1},{0,0},{0,1} };
	private static final int[][] pulsar = {{0,-2},{0,2},{2,0},{2,1},{2,-1},{2,2},{2,-2},{-2,0},{-2,1},{-2,-1},{-2,2},{-2,-2}};
	private static final int[][] glider = {{0,0},{0,-1},{0,-2},{-1,0},{-2,-1}};
	private static final int[][] lwss = {{0,0},{1,0},{2,0},{3,0},{4,-1},{0,-1},{0,-2},{1,-3},{4,-3}};
	private static final int[][] fireworks= {{0,-3},{-1,-4},{1,-4},{-1,-5},{1,-5},{0,-6},{1,-6},
						{0,3},{-1,4},{1,4},{-1,5},{1,5},{0,6},{-1,6},
						{-3,0},{-4,-1},{-4,1},{-5,-1},{-5,1},{-6,0},{-6,-1},
						{3,0},{4,-1},{4,1},{5,-1},{5,1},{6,0},{6,1}};
			

	/**
	 * Static method that will provide Grid with an array of coordinates, to position the figures.
	 * 
	 * @param figureName the name of the figure to be printed.
	 * @return
	 * 			array of coordinates for the cells that will be positioned on the grid.
	 */
	public static int[][] getCoordinates(String figureName){
		if(figureName.equals("blinker"))
			return blinker;
		else if(figureName.equals("pulsar"))
			return pulsar;
		else if(figureName.equals("glider"))
			return glider;
		else if (figureName.equals("fireworks"))
			return fireworks;
		
		return lwss;
	}
}
