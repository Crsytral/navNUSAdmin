import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class KMLParser {
	public static void main(String[] args) throws Exception{
		//Unused IDs so far
		boolean[] idInUse = new boolean[826];
		//List of Vertices and Edges
		LinkedList<Vertex> vertices = new LinkedList<Vertex>();
		LinkedList<DirectedEdge> edges = new LinkedList<DirectedEdge>();
		
		List<String> data = Files.readAllLines((new File("D:\\Downloads\\features.kml")).toPath());
		for (int i=0; i<data.size(); i++) {
			String line = data.get(i);
			if (line.equals("\t\t\t<LineString>")) {
				//Track
				//Get the list of coordinates for the track (array since track can have multiple coordinates)
				int j = i;
				while (!data.get(j).contains("<coordinates>")) j++;
				//Format of each entry long,lat
				String[] coordinateList = data.get(j).replace("<coordinates>", "").replace("</coordinates>", "").trim().split(" ");
				LinkedList<GeoCoordinate> coordinates = new LinkedList<GeoCoordinate>();
				
				for (String coordinate : coordinateList) {
					double lon = Double.parseDouble(coordinate.split(",")[0]);
					double lat = Double.parseDouble(coordinate.split(",")[1]);
					coordinates.add(new GeoCoordinate(lat, lon));
				}
						
				//Extract vertices id, end points of the edges
				boolean hasName = true;
				j = i;
				while (!data.get(j).contains("<name>") && !data.get(j).contains("</name>")) {
					if (data.get(j).contains("<Placemark>")) {
						//System.out.println("No Name -> " + i);
						hasName = false;
						break;
					}
					j--;
					
				}
				String[] idList = new String[0];
				boolean validTrackIds = false;
				if (hasName) {
					idList = data.get(j).replace("<name>", "").replace("</name>", "").trim().split(":");
					//Ensure the idList has 2 ids (Start & End)
					if (idList.length == 2) {
						validTrackIds = true;
					} else {
						System.out.println("Track not valid! -> " + i);
					}
				}
				
				if (validTrackIds) {
					//Extract descriptions of the edges for the front/back
					LinkedList<String> description1 = new LinkedList<String>();
					LinkedList<String> description2 = new LinkedList<String>();
					
					boolean hasDescription = true;
					j = i;
					while (!data.get(j).contains("</description>")) {
						if (data.get(j).contains("<Placemark>")) {
							//System.out.println("No Description");
							hasDescription = false;
							break;
						}
						j--;
					}
					
					boolean hasOppositeTag = true;
					if (hasDescription) {
						while (!(data.get(j).contains("--Opposite--") || data.get(j).contains("--opposite--"))) {
							if (data.get(j).contains("<Placemark>")) {
								System.out.println("no --opposite-- tag");
								hasOppositeTag = false;
								break;
							}
							String description = data.get(j).replace("</description>", "").replace("<description>", "").trim();
							if (!description.equals("")) {
								description2.addFirst(description);
							}
							
							j--;
						}
						
						if (hasOppositeTag) {
							while (!data.get(j).contains("<description>")) {
								String description = data.get(j).replace("</description>", "").replace("<description>", "").replace("--opposite--", "").replace("--Opposite--", "").trim();
								if (!description.equals("")) {
									description1.addFirst(description);
								}
								j--;
							}

						}
					}
					
					//System.out.println("Track " + Arrays.asList(coordinateList) + " : " + Arrays.asList(idList) + " : " + description1 + " : " + description2);
					double distance = 0;
					double prevLat = Double.parseDouble(coordinateList[0].split(",")[0]);
					double prevLon = Double.parseDouble(coordinateList[0].split(",")[1]);
					for (int x=1; x<coordinateList.length; x++) {
						double lat = Double.parseDouble(coordinateList[x].split(",")[0]);
						double lon = Double.parseDouble(coordinateList[x].split(",")[1]);
						distance += estimateDistance(prevLat, prevLon, lat, lon);
						prevLat = lat;
						prevLon = lon;
					}
					edges.add(new DirectedEdge(Integer.parseInt(idList[0].trim()), Integer.parseInt(idList[1].trim()), distance, description1, coordinates));
					Collections.reverse(coordinates);
					edges.add(new DirectedEdge(Integer.parseInt(idList[1].trim()), Integer.parseInt(idList[0].trim()), distance, description2, coordinates));
				}
			} else if (line.equals("\t\t\t<Point>")) {
				//Extract the id and name
				int j = i;
				while (!data.get(j).startsWith("\t\t\t<name>")) j--;
				String id = data.get(j).substring(0, data.get(j).indexOf(":")).replace("<name>", "").replace("</name>", "").trim();
				String name = data.get(j).substring(data.get(j).indexOf(": ")+2, data.get(j).length()).replace("<name>", "").replace("</name>", "").trim();
				
				//Extract coordinates
				j = i;
				while (!data.get(j).startsWith("\t\t\t\t<coordinates>")) j++;
				String longitude = data.get(j).replace("<coordinates>", "").replace("</coordinates>", "").trim().split(",")[0].trim();
				String latitude = data.get(j).replace("<coordinates>", "").replace("</coordinates>", "").trim().split(",")[1].trim();
				
				//System.out.println("Way Point " + id + ";" + name + ";" + latitude + ";" + longitude);
				vertices.add(new Vertex(Integer.parseInt(id), name, new GeoCoordinate(Double.parseDouble(latitude), Double.parseDouble(longitude))));
				idInUse[Integer.parseInt(id)] = true;
			}
		}
		
		System.out.println(vertices);
		System.out.println(edges);
		System.out.print("Unused Vertices: ");
		for(int i=0; i<826; i++) {
			if (!idInUse[i]) {
				System.out.print(i + " ");
			}
		}
		System.out.println();
		System.out.print("Vertices with edges: ");
		boolean[] idWithEdge = new boolean[826];
		for (DirectedEdge edge : edges) {
			idWithEdge[edge.from()] = true;
			idWithEdge[edge.to()] = true;
		}
		int count = 0;
		for (int i=0; i<826; i++) {
			if (idWithEdge[i]) {
				System.out.print(i + " ");
				count++;
			}
		}
		System.out.println();
		System.out.println("Total vertices with edges: " + count);
		System.out.println("Total Edges: " + edges.size());
		
		//Write out data for reading
		//Write edges
		StringBuffer edgeList = new StringBuffer();
		for (DirectedEdge edge : edges) {
			edgeList.append(edge + "\r\n");
		}
		Files.write((new File("D:\\Downloads\\edges")).toPath(), edgeList.toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		//Write vertices that has edges
		StringBuffer verticeList = new StringBuffer();
		for (Vertex vertex : vertices) {
			if (idWithEdge[vertex.id]) {
				verticeList.append(vertex + "\r\n");
			}
		}
		Files.write((new File("D:\\Downloads\\vertex")).toPath(), verticeList.toString().getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		//Calculate path using floyd-warshall algorithm
		//Save the table
	}
	
	/*
     * Estimates distance between two points in latitude and longitude
     *
     * lat1, lon1 Start point, lat2, lon2 End point
     * @returns Distance in Meters
     */
    public static double estimateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth

        Double latDistance = Math.toRadians(lat2 - lat1);
        Double lonDistance = Math.toRadians(lon2 - lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        return Math.sqrt(distance);
    }
}
