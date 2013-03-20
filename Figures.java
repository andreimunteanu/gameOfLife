public final class Figures {
  private static int[][] blinker = {{0,-1},{0,0},{0,1} };
	
	
	public static int[][] getCoordinates(String figureName){
		int[][] temp = null;
		switch(figureName){
		case "blinker": temp = blinker;break;
		default:break;
		}
			
			return temp;
	}
}
