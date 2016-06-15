import java.io.File;
import java.nio.file.Files;
import java.util.List;


public class VerticeAdjustments {
	public static void main(String[] args) throws Exception{
		List<String> changeData = Files.readAllLines((new File("D:\\Dropbox\\Orbital 2016\\Map Plot\\Clashing Vertices.txt")).toPath());
		List<String> data = Files.readAllLines((new File("D:\\Dropbox\\Orbital 2016\\Map Plot\\Vertices&Edges.kml")).toPath());
		for (String s : changeData) {
			if (s.contains("move to") && s.contains(",")) {
				String id = s.substring(0, s.indexOf("move to")).trim();
				String[] geo = s.substring(s.indexOf("move to") + "move to".length(), s.length()).split(",");
				String lat = geo[0].trim();
				String lon = geo[1].trim();
				//System.out.println(id + " : " + lat + ":" + lon);
				for (int i=0; i<data.size(); i++) {
					if(data.get(i).startsWith("\t\t\t\t<name>"+id+" : ")) {
						data.set(i+5, "\t\t\t\t\t<coordinates>"+lon+","+lat+"</coordinates>");
					}
				}
			} else if (s.contains("remove")) {
				String id = s.replace("remove ", "").trim();
				for (int i=0; i<data.size(); i++) {
					if(data.get(i).startsWith("\t\t\t\t<name>"+id+" : ")) {
						for (int j=0; j<9; j++) {
							data.remove(i-1);
						}
					}
				}
			}
		}
		
		Files.write((new File("D:\\Dropbox\\Orbital 2016\\Map Plot\\vertices2.kml")).toPath(), data);
	}
	
	public static void main2(String[] args) throws Exception{
		List<String> data = Files.readAllLines((new File("D:\\Dropbox\\Orbital 2016\\Map Plot\\Clashing Vertices.txt")).toPath());
		List<String> vertices = Files.readAllLines((new File("D:\\Dropbox\\Orbital 2016\\Map Plot\\vertices")).toPath());
		for (String s : data) {
			if (s.contains("move to") && s.contains(",")) {
				String id = s.substring(0, s.indexOf("move to")).trim();
				String[] geo = s.substring(s.indexOf("move to") + "move to".length(), s.length()).split(",");
				String lat = geo[0].trim();
				String lon = geo[1].trim();
				//System.out.println(id + " : " + lat + ":" + lon);
				for (int i=0; i<vertices.size(); i++) {
					if(vertices.get(i).startsWith(id+";")) {
						String[] vertice = vertices.get(i).split(";");
						vertices.set(i, vertice[0]+";"+vertice[1]+";"+lat+";"+lon);
					}
				}
			} else if (s.contains("remove")) {
				String id = s.replace("remove ", "").trim();
				for (int i=0; i<vertices.size(); i++) {
					if(vertices.get(i).startsWith(id+";")) {
						vertices.remove(i);
					}
				}
			}
		}
		
		Files.write((new File("D:\\Dropbox\\Orbital 2016\\Map Plot\\vertices2")).toPath(), vertices);
	}
}
