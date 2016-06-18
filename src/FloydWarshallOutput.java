
public class FloydWarshallOutput {
	public boolean hasNegativeCycle;  // is there a negative cycle?
    public int[][] distTo;  // distTo[v][w] = length of shortest v->w path
    public DirectedEdge[][] edgeTo;  // edgeTo[v][w] = last edge on shortest v->w path
    
	public FloydWarshallOutput(FloydWarshall fw) {
		hasNegativeCycle = fw.hasNegativeCycle;
		edgeTo = fw.edgeTo;
		int v = fw.distTo.length;
		distTo = new int[v][v];
		for (int i=0; i<v; i++) {
			for (int j=0; j<v; j++) {
				if (Double.isInfinite(fw.distTo[i][j])) {
					distTo[i][j] = 0;
				} else {
					distTo[i][j] = 0;//(int)(fw.distTo[i][j] * 1000);
				}
			}
		}
	}
	
}
