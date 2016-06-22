package edu.princeton.cs.algs4;
import java.io.Serializable;
import java.util.LinkedList;

/******************************************************************************
 *  Compilation:  javac FloydWarshall.java
 *  Execution:    java FloydWarshall V E
 *  Dependencies: AdjMatrixEdgeWeightedDigraph.java
 *
 *  Floyd-Warshall all-pairs shortest path algorithm.
 *
 *  % java FloydWarshall 100 500
 *
 *  Should check for negative cycles during triple loop; otherwise
 *  intermediate numbers can get exponentially large.
 *  Reference: "The Floyd-Warshall algorithm on graphs with negative cycles"
 *  by Stefan Hougardy
 *
 ******************************************************************************/


/**
 *  The <tt>FloydWarshall</tt> class represents a data type for solving the
 *  all-pairs shortest paths problem in edge-weighted digraphs with
 *  no negative cycles.
 *  The edge weights can be positive, negative, or zero.
 *  This class finds either a shortest path between every pair of vertices
 *  or a negative cycle.
 *  <p>
 *  This implementation uses the Floyd-Warshall algorithm.
 *  The constructor takes time proportional to <em>V</em><sup>3</sup> in the
 *  worst case, where <em>V</em> is the number of vertices.
 *  Afterwards, the <tt>dist()</tt>, <tt>hasPath()</tt>, and <tt>hasNegativeCycle()</tt>
 *  methods take constant time; the <tt>path()</tt> and <tt>negativeCycle()</tt>
 *  method takes time proportional to the number of edges returned.
 *  <p>
 *  For additional documentation,    
 *  see <a href="http://algs4.cs.princeton.edu/44sp">Section 4.4</a> of    
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne. 
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */
public class FloydWarshall implements Serializable {
	public boolean hasNegativeCycle;  // is there a negative cycle?
    public double[][] distTo;  // distTo[v][w] = length of shortest v->w path
    public DirectedEdge[][] edgeTo;  // edgeTo[v][w] = last edge on shortest v->w path
    
    /**
     * Computes a shortest paths tree from each vertex to to every other vertex in
     * the edge-weighted digraph <tt>G</tt>. If no such shortest path exists for
     * some pair of vertices, it computes a negative cycle.
     * @param G the edge-weighted digraph
     */
    public FloydWarshall(AdjMatrixEdgeWeightedDigraph G) {
        int V = G.V();
        distTo = new double[V][V];
        edgeTo = new DirectedEdge[V][V];

        // initialize distances to infinity
        for (int v = 0; v < V; v++) {
            for (int w = 0; w < V; w++) {
                distTo[v][w] = Double.POSITIVE_INFINITY;
            }
        }

        // initialize distances using edge-weighted digraph's
        for (int v = 0; v < G.V(); v++) {
            for (DirectedEdge e : G.adj(v)) {
                distTo[e.from()][e.to()] = e.weight();
                edgeTo[e.from()][e.to()] = e;
            }
            // in case of self-loops
            if (distTo[v][v] >= 0.0) {
                distTo[v][v] = 0.0;
                edgeTo[v][v] = null;
            }
        }

        // Floyd-Warshall updates
        for (int i = 0; i < V; i++) {
            // compute shortest paths using only 0, 1, ..., i as intermediate vertices
            for (int v = 0; v < V; v++) {
                if (edgeTo[v][i] == null) continue;  // optimization
                for (int w = 0; w < V; w++) {
                    if (distTo[v][w] > distTo[v][i] + distTo[i][w]) {
                        distTo[v][w] = distTo[v][i] + distTo[i][w];
                        edgeTo[v][w] = edgeTo[i][w];
                    }
                }
                // check for negative cycle
                if (distTo[v][v] < 0.0) {
                    hasNegativeCycle = true;
                    return;
                }
            }
        }
    }

    /**
     * Is there a negative cycle?
     * @return <tt>true</tt> if there is a negative cycle, and <tt>false</tt> otherwise
     */
    public boolean hasNegativeCycle() {
        return hasNegativeCycle;
    }

    /**
     * Returns a negative cycle, or <tt>null</tt> if there is no such cycle.
     * @return a negative cycle as an iterable of edges,
     * or <tt>null</tt> if there is no such cycle
     */
    public Iterable<DirectedEdge> negativeCycle() {
        for (int v = 0; v < distTo.length; v++) {
            // negative cycle in v's predecessor graph
            if (distTo[v][v] < 0.0) {
                int V = edgeTo.length;
                EdgeWeightedDigraph spt = new EdgeWeightedDigraph(V);
                for (int w = 0; w < V; w++)
                    if (edgeTo[v][w] != null)
                        spt.addEdge(edgeTo[v][w]);
                EdgeWeightedDirectedCycle finder = new EdgeWeightedDirectedCycle(spt);
                assert finder.hasCycle();
                return finder.cycle();
            }
        }
        return null;
    }

    /**
     * Is there a path from the vertex <tt>s</tt> to vertex <tt>t</tt>?
     * @param s the source vertex
     * @param t the destination vertex
     * @return <tt>true</tt> if there is a path from vertex <tt>s</tt>
     * to vertex <tt>t</tt>, and <tt>false</tt> otherwise
     */
    public boolean hasPath(int s, int t) {
        return distTo[s][t] < Double.POSITIVE_INFINITY;
    }

    /**
     * Returns the length of a shortest path from vertex <tt>s</tt> to vertex <tt>t</tt>.
     * @param s the source vertex
     * @param t the destination vertex
     * @return the length of a shortest path from vertex <tt>s</tt> to vertex <tt>t</tt>;
     * <tt>Double.POSITIVE_INFINITY</tt> if no such path
     * @throws UnsupportedOperationException if there is a negative cost cycle
     */
    public double dist(int s, int t) {
        if (hasNegativeCycle())
            throw new UnsupportedOperationException("Negative cost cycle exists");
        return distTo[s][t];
    }

    /**
     * Returns a shortest path from vertex <tt>s</tt> to vertex <tt>t</tt>.
     * @param s the source vertex
     * @param t the destination vertex
     * @return a shortest path from vertex <tt>s</tt> to vertex <tt>t</tt>
     * as an iterable of edges, and <tt>null</tt> if no such path
     * @throws UnsupportedOperationException if there is a negative cost cycle
     */
    public LinkedList<DirectedEdge> path(int s, int t) {
        if (hasNegativeCycle())
            throw new UnsupportedOperationException("Negative cost cycle exists");
        if (!hasPath(s, t)) return null;
        LinkedList<DirectedEdge> path = new LinkedList<DirectedEdge>();
        for (DirectedEdge e = edgeTo[s][t]; e != null; e = edgeTo[s][e.from()]) {
            path.push(e);
        }
        return path;
    }
    
    /*
    public void saveDistToTable(String path) {
    	StringBuffer data = new StringBuffer();
    	for (int row=0; row<distTo.length; row++) {
    		for (int column=0; column<distTo.length; column++) {
    			data.append(distTo[row][column] + " ");
    		}
    		data.append("\r\n");
    	}
		try {
			Files.write((new File(path)).toPath(), data.toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    */

}