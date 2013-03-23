public final class Figures {
	private static final int[][] blinker = {{0,-1},{0,0},{0,1} };
	private static final int[][] pulsar = {{0,-2},{0,2},{2,0},{2,1},{2,-1},{2,2},{2,-2},{-2,0},{-2,1},{-2,-1},{-2,2},{-2,-2}};
	private static final int[][] glider = {{0,0},{0,-1},{0,-2},{-1,0},{-2,-1}};
	private static final int[][] lwss = {{0,0},{1,0},{2,0},{3,0},{4,-1},{0,-1},{0,-2},{1,-3},{4,-3}};			

	public static int[][] getCoordinates(String figureName){
		if(figureName.equals("blinker"))
			return blinker;
		else if(figureName.equals("pulsar"))
			return pulsar;
		else if(figureName.equals("glider"))
			return glider;
		
		return lwss;
	}
}
