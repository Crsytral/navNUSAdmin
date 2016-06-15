public class Vertex {
	public int id;
	public String name;
	public GeoCoordinate coordinate;
	
	public Vertex(int id, String name, GeoCoordinate coordinate) {
		super();
		this.id = id;
		this.name = name;
		this.coordinate = coordinate;
	}
	
	public String toString() {
		return id + " : " + name + " (" + coordinate.latitude + ", " + coordinate.longitude + ")";
	}
}
