package edu.princeton.cs.algs4;
import java.io.Serializable;
import java.util.LinkedList;

import com.navnus.entity.GeoCoordinate;

/******************************************************************************
 *  Compilation:  javac DirectedEdge.java
 *  Execution:    java DirectedEdge
 *  Dependencies: StdOut.java
 *
 *  Immutable weighted directed edge.
 *
 ******************************************************************************/

/**
 *  The <tt>DirectedEdge</tt> class represents a weighted edge in an
 *  {@link EdgeWeightedDigraph}. Each edge consists of two integers
 *  (naming the two vertices) and a real-value weight. The data type
 *  provides methods for accessing the two endpoints of the directed edge and
 *  the weight.
 *  <p>
 *  For additional documentation, see <a href="http://algs4.cs.princeton.edu/44sp">Section 4.4</a> of
 *  <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 */

public class DirectedEdge implements Serializable {
    private final int from;
    private final int to;
    private final double weight;
    public final LinkedList<GeoCoordinate> coordinates;
    
    //add
    public LinkedList<String> description;

    /**
     * Initializes a directed edge from vertex <tt>v</tt> to vertex <tt>w</tt> with
     * the given <tt>weight</tt>.
     * @param v the tail vertex
     * @param w the head vertex
     * @param weight the weight of the directed edge
     * @throws IndexOutOfBoundsException if either <tt>v</tt> or <tt>w</tt>
     *    is a negative integer
     * @throws IllegalArgumentException if <tt>weight</tt> is <tt>NaN</tt>
     */
    public DirectedEdge(int v, int w, double weight, LinkedList<String> description, LinkedList<GeoCoordinate> coordinates) {
        if (v < 0) throw new IndexOutOfBoundsException("Vertex names must be nonnegative integers");
        if (w < 0) throw new IndexOutOfBoundsException("Vertex names must be nonnegative integers");
        if (Double.isNaN(weight)) throw new IllegalArgumentException("Weight is NaN");
        this.from = v;
        this.to = w;
        this.weight = weight;
        this.description = description;
        this.coordinates = coordinates;
    }
    
    public DirectedEdge(String dataString) {
    	String[] data = dataString.split(";");
    	from = Integer.parseInt(data[0]);
    	to = Integer.parseInt(data[1]);;
    	weight = Double.parseDouble(data[2]);
    	description = new LinkedList<String>(); 
    	for (String desc : data[3].split("--next--")) {
    		description.add(desc);
    	}
    	coordinates = new LinkedList<GeoCoordinate>();
    	for (String geoData : data[4].split("--next--")) {
    		String[] geo = geoData.split(",");
    		double lat = Double.parseDouble(geo[0]);
    		double lon = Double.parseDouble(geo[1]);
    		coordinates.add(new GeoCoordinate(lat, lon));
    	}
    }

    /**
     * Returns the tail vertex of the directed edge.
     * @return the tail vertex of the directed edge
     */
    public int from() {
        return from;
    }

    /**
     * Returns the head vertex of the directed edge.
     * @return the head vertex of the directed edge
     */
    public int to() {
        return to;
    }

    /**
     * Returns the weight of the directed edge.
     * @return the weight of the directed edge
     */
    public double weight() {
        return weight;
    }

    /**
     * Returns a string representation of the directed edge.
     * @return a string representation of the directed edge
     */
    public String toString() {
        //Description & Coordinates are list, have to manually split them up for output
    	//Description
    	StringBuffer descriptionList = new StringBuffer();
    	for (String description : this.description) {
    		descriptionList.append(description);
    		descriptionList.append("--next--");
    	}
    	if (descriptionList.lastIndexOf("--next--") == descriptionList.length()-8) {
    		descriptionList.delete(descriptionList.length()-8, descriptionList.length());
    	}

    	//Coordinates
    	StringBuffer coordinateList = new StringBuffer();
    	for (GeoCoordinate coordinate : coordinates) {
    		coordinateList.append(coordinate);
    		coordinateList.append("--next--");
    	}
    	if (coordinateList.lastIndexOf("--next--") == coordinateList.length()-8) {
    		coordinateList.delete(coordinateList.length()-8, coordinateList.length());
    	}
    	
    	//return from + "->" + to + " " + String.format("(%.2f)", weight) + description + " " + coordinates;
        return from + ";" + to + ";" + String.format("%.2f", weight) + ";" + descriptionList + ";" + coordinateList;
    }

}

/******************************************************************************
 *  Copyright 2002-2015, Robert Sedgewick and Kevin Wayne.
 *
 *  This file is part of algs4.jar, which accompanies the textbook
 *
 *      Algorithms, 4th edition by Robert Sedgewick and Kevin Wayne,
 *      Addison-Wesley Professional, 2011, ISBN 0-321-57351-X.
 *      http://algs4.cs.princeton.edu
 *
 *
 *  algs4.jar is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  algs4.jar is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with algs4.jar.  If not, see http://www.gnu.org/licenses.
 ******************************************************************************/