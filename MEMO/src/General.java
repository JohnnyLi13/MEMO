
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.openstreetmap.gui.jmapviewer.Coordinate;

public class General {
	
	ArrayList<String> read_lines_from_txt(String path) throws IOException{
		File file = new File(path);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line_content;
		ArrayList<String> content = new ArrayList<String>();
		while( (line_content = reader.readLine()) != null) {
			content.add(line_content);
		}
		reader.close();
		//System.out.println(content);
		return content;
	}
	
	ArrayList<Coordinate> read_coordinates_from_txt(String file_name) throws IOException{
		ArrayList<String> coor_str = read_lines_from_txt(file_name);
		ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
		for(int i=0;i<coor_str.size();i++) {
			String line = coor_str.get(i);
			line = line.substring(1, line.length()-1);
			double lon = Double.parseDouble(line.split(",")[0]);
			double lat = Double.parseDouble(line.split(",")[1]);
			coordinates.add(new Coordinate(lon,lat));
		}
		return coordinates;
	}
	
}
