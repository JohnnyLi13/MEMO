
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class PlaceSearch {
		
	
	ArrayList<String> get_place_details_from_placeID(String placeID){
		String request_url = get_place_details_request_url(placeID);
		System.out.println(request_url);
		ArrayList<String> place_details = new ArrayList<String>();
		try {
			URL url = new URL(request_url);
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			boolean atGeometry = false;
			boolean atLocation = false;
			boolean atOpeningHours = false;
			boolean gotLat = false;
			boolean gotLng = false;
			String line = null;
			while((line = in.readLine()) != null) {
				if(line.contains("formatted_address")) {
					String address = line.split("\" : \"")[1].substring(0, line.split("\" : \"")[1].length()-2);
					//System.out.println(address);
					place_details.add(address);
					continue;
				}
				if(line.contains("geometry")) {
					atGeometry = true;
					continue;
				}
				if(line.contains("location") && atGeometry) {
					atLocation = true;
					continue;
				}
				if(atLocation && atGeometry && line.contains("lat") && !gotLat) {
					String latitude = line.split(" : ")[1].substring(0, line.split(" : ")[1].length()-1);
					//System.out.print("lat: " + latitude + "		");
					gotLat = true;
					place_details.add(latitude);
					continue;
				}
				if(atLocation && atGeometry && line.contains("lng") && !gotLng) {
					gotLng = true;
					String longitude = line.split(" : ")[1].substring(0, line.split(" : ")[1].length()-1);
					//System.out.println("lng" + longitude);
					place_details.add(longitude);
					continue;
				}
				if(line.contains("opening_hours")) {
					atOpeningHours = true;
					continue;
				}
				if(atOpeningHours && line.contains("open_now")) {
					String status = line.split(" : ")[1].substring(0, line.split(" : ")[1].length()-1);
					if(status.equals("true")) {
						status = "open";
						//System.out.println("open");
					}else {
						status = "closed";
						//System.out.println("close");
					}
					place_details.add(status);
					continue;
				}

			}
			//System.out.println(place_details);
			return place_details;
		}catch(Exception e) {
			System.out.println("fail to retreive candidates results");
			e.printStackTrace();
		}
		return place_details;
	}
	
	
	String get_place_details_request_url(String placeID){
		String base_url = "https://maps.googleapis.com/maps/api/place/details/json?";
		String API_Key = "AIzaSyDQk2KKNrMTKbTWOQmlOkU0LwI2UERVYIg";
		String fields_options = "formatted_address,geometry,opening_hours";
		String request_url = base_url + "place_id=" + placeID + "&fields=" + fields_options + "&key=" + API_Key;
		return request_url;
	}
	
	
	ArrayList<String> get_placeID_candidates(String request_url) {
		ArrayList<String> result_candidates = new ArrayList<String>();
		try {
			URL url = new URL(request_url);
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			boolean atCandidates = false;
			String line = null;
			String place_id = null;
			while((line = in.readLine()) != null) {
				if(line.contains("candidates")) {
					atCandidates = true;
					continue;
				}
				if(atCandidates && line.contains("place_id")) {
					place_id = line.split("\" : \"")[1].substring(0, line.split("\" : \"")[1].length()-1);
					//System.out.println(place_id);
					result_candidates.add(place_id);
					continue;
				}
			}
			if(!atCandidates) {
				System.out.println("no results returned on Google Maps");
				System.exit(0);
			}
			return result_candidates;
		}catch(Exception e) {
			System.out.println("fail to retreive candidates results");
			e.printStackTrace();
		}
		return result_candidates;
	}
	
	
	String get_placeID_search_request_url(String place_of_interest) {
		String formatted_POI = format_DirectionsAPI_place(place_of_interest);
		String request_url = null;
		String base_url = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json?";
		String text_input = formatted_POI;
		String text_input_type = "textquery";
		String API_Key = "AIzaSyDQk2KKNrMTKbTWOQmlOkU0LwI2UERVYIg";
		request_url = base_url + "input=" + text_input + "&" + "inputtype=" + text_input_type + "&" + "key=" + API_Key;
		//System.out.println(request_url);
		return request_url;
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	String format_DirectionsAPI_place(String place) {
		StringBuilder sb = new StringBuilder(place);
		for(int i=0;i<place.length();i++) {
			if(Character.isWhitespace(sb.charAt(i)) == true) {
				sb.setCharAt(i, '+');
			}
		}
		place = String.valueOf(sb);
		return place;
	}
	
	
}
