

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;

import javax.swing.JFrame;
import javax.swing.JOptionPane;


public class AutoDetect {
	
	String origin, destination;
	String region = "uk";
	//String waypoint = "460 Gardiners Road, Kingston";	//check automatically all the waypoint
	MySQL mysql = new MySQL();
	PlaceSearch search_place = new PlaceSearch();
	
	final String directions_base_url = "https://maps.googleapis.com/maps/api/directions/json?";
	
	public AutoDetect() {
		JFrame frame = new JFrame("route");
		origin = JOptionPane.showInputDialog(frame, "origin: ", "MEMO", JOptionPane.PLAIN_MESSAGE);
		destination = JOptionPane.showInputDialog(frame, "destination: ", "MEMO", JOptionPane.PLAIN_MESSAGE);
		String url1 = get_straight_direction_request_url();
		String[] results1 = get_direct_driving_info(url1);
		ArrayList<String> waypoints = mysql.select_all_places();
		//
		String message = "";
		for(String waypoint : waypoints) {
			String placeID_request_url = search_place.get_placeID_search_request_url(waypoint);
			ArrayList<String> candidates_IDs = search_place.get_placeID_candidates(placeID_request_url);
			System.out.println(candidates_IDs);
			//choose the best results if given several candidates
			int fastest_time = 999;
			int fastest_index = 0;
			for(int i=0; i<candidates_IDs.size(); i++) {
				//String place_ID = candidates_IDs.get(0); //want to get the nearest candidate to visit
				String url = get_direction_with_waypoint_request_url(candidates_IDs.get(i));
				String[] results2 = get_detour_driving_info(url);
				System.out.println("results1: " + Arrays.toString(results1));
				System.out.println("results2: " + Arrays.toString(results2));
				int dura_diff = compare_results(results1, results2, waypoint);
				if(dura_diff < fastest_time) {
					fastest_time = dura_diff;
					fastest_index = i;
				}
			}
			String place_ID = candidates_IDs.get(fastest_index);
			ArrayList<String> place_details = search_place.get_place_details_from_placeID(place_ID);
			Double latitude = Double.parseDouble(place_details.get(1));
			Double longitude = Double.parseDouble(place_details.get(2));
			DecimalFormat df = new DecimalFormat("#.#####");
			df.setRoundingMode(RoundingMode.HALF_UP);
			String lat = df.format(latitude);
			String lon = df.format(longitude);
			//System.out.println(latitude + " " + longitude);
			///pass in place name, lat, long in for updates in MySQL
			mysql.set_coordinates(waypoint, lat, lon);
			///
			System.out.println(	String.format("if stop by %s on your way from %s to %s, "
					+ "it will be %s minutes extra time", waypoint, origin, destination, Integer.toString(fastest_time)));
			message = message + String.format("if stop by %s on your way from %s to %s, "
					+ "it will be %s minutes extra time", waypoint, origin, destination, Integer.toString(fastest_time)) + "\n";
		}
		System.out.println(message);
		JOptionPane.showMessageDialog(frame, message);
	}
	
	
	int compare_results(String[] results1, String[] results2, String waypoint) {
		//String dist1 = results1[0].substring(0, results1[0].length()-3);
		//String dist2 = results2[0].substring(0, results2[0].length()-3);
		String dura1 = results1[1];
		String dura2 = results2[1];
		DecimalFormat df = new DecimalFormat("#.#");
		df.setRoundingMode(RoundingMode.HALF_UP);
		//Float dist_diff = Float.parseFloat(dist2) - Float.parseFloat(dist1);
		String dura_diff = perform_time_subtraction(dura2, dura1);
		//System.out.println(df.format(dist_diff) + " km");
		//System.out.println(dura_diff + " mins");
		/*
		System.out.println( String.format("if stop by %s on your way from %s to %s, "
				+ "it will be %s extra distance and %s extra time",
				waypoint, origin, destination, df.format(dist_diff) + " km", dura_diff + " mins") );
		*/
		return Integer.parseInt(dura_diff);
	}
	
	
	String get_straight_direction_request_url() {
		String formatted_origin = format_DirectionsAPI_place(origin);
		String formatted_desti = format_DirectionsAPI_place(destination);
		///
		String request_url = directions_base_url + "origin=" + formatted_origin + "&destination=" + formatted_desti 
				+ "&region=" + region
				+ "&key=";
		System.out.println(request_url);
		return request_url;
	}
	
	
	String[] get_detour_driving_info(String request_url) {
		try {
			URL url = new URL(request_url);
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String line;
			boolean gotDist = false;
			boolean gotDura = false;
			String distance_str = null, duration_str = null;
			while( (line=in.readLine()) != null && (!gotDist || !gotDura)  ){
				if(line.contains("distance") && !gotDist) {
					line = in.readLine();
					String split[] = line.split(":");
					distance_str = split[1];
					distance_str = distance_str.substring(2,distance_str.length()-2);
					gotDist = true;
					//System.out.println("driving distance information found");
				}
				if(line.contains("duration") && !gotDura) {
					line = in.readLine();
					String split[] = line.split(":");
					duration_str = split[1];
					duration_str = duration_str.substring(2,duration_str.length()-3);
					gotDura = true;
				}
			}
			in.close();
			if(!gotDist || !gotDura) {
				System.out.println("fail to get driving info");
				return null;
			}else {
				//System.out.println("distance: " + distance_str + "	duration: " + duration_str);
				return new String[] {distance_str, duration_str};
			}
		}catch(Exception e) {
			System.out.println("fail to get straight direction info");
			e.printStackTrace();
		}
		return null;
	}
	
	
	String[] get_direct_driving_info(String request_url) {
		try {
			URL url = new URL(request_url);
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String line;
			boolean gotDist = false;
			boolean gotDura = false;
			boolean atSteps = false;
			boolean gotSteps = false;
			Formatter coordinates_fmtr = new Formatter("route_coordinates.txt");
			String distance_str = null, duration_str = null;
			while( (line=in.readLine()) != null){
				if(line.contains("steps")) {
					atSteps = true;
				}
				if(line.contains("distance") && !gotDist) {
					line = in.readLine();
					String split[] = line.split(":");
					distance_str = split[1];
					distance_str = distance_str.substring(2,distance_str.length()-2);
					gotDist = true;
					//System.out.println("driving distance information found");
				}
				if(line.contains("duration") && !gotDura) {
					line = in.readLine();
					String split[] = line.split(":");
					duration_str = split[1];
					duration_str = duration_str.substring(2,duration_str.length()-3);
					gotDura = true;
				}
				///
				if(line.contains("end_location") && atSteps) {
					line = in.readLine();
					String latsplit[] = line.split(":");
					String lat_str = latsplit[1];
					lat_str = lat_str.substring(1, lat_str.length()-1);
					Double lat_double = Double.parseDouble(lat_str);
					//lat_double = round(lat_double, 2);
					//System.out.println(lat_double);
					line = in.readLine();
					String longsplit[] = line.split(":");
					String long_str = longsplit[1];
					long_str = long_str.substring(1);
					Double long_double = Double.parseDouble(long_str);
					//long_double = round(long_double, 2);
					//System.out.println(long_double);
					//coordinates_fmtr.format("[%s,%s]\n", lat_str, long_str);
					coordinates_fmtr.format("[%f,%f]\n", lat_double, long_double);
				}
				///
			}
			in.close();
			coordinates_fmtr.close();
			if(!gotDist || !gotDura) {
				System.out.println("fail to get driving info");
				return null;
			}else {
				//System.out.println("distance: " + distance_str + "	duration: " + duration_str);
				return new String[] {distance_str, duration_str};
			}
		}catch(Exception e) {
			System.out.println("fail to get straight direction info");
			e.printStackTrace();
		}
		return null;
	}
	
	
	String get_direction_with_waypoint_request_url(String place_ID) {
		String formatted_origin = format_DirectionsAPI_place(origin);
		String formatted_desti = format_DirectionsAPI_place(destination);
		//String formatted_waypoint = format_DirectionsAPI_place(waypoint);
		///
		String request_url = directions_base_url + "origin=" + formatted_origin + "&destination=" + formatted_desti 
				+ "&waypoints=via:place_id:" + place_ID
				+ "&region=" + region
				+ "&key=";
		//System.out.println(request_url);
		return request_url;
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
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
	
	
	String perform_minutes_subtraction(String minuend, String subtrahend) {
		minuend = minuend.substring(0, minuend.length()-3);
		subtrahend = subtrahend.substring(0, subtrahend.length()-3);
		float diff = Float.parseFloat(minuend) - Float.parseFloat(subtrahend);
		return Float.toString(diff);
	}
	
	
	String perform_time_subtraction(String minuend, String subtrahend) {
		int diff = 0;
		int hr_diff = 0, min_diff = 0;
		if(minuend.contains("hour") && subtrahend.contains("hour")){
			int minuend_hr = Integer.parseInt(minuend.split(" ")[0]);
			int subtrahend_hr = Integer.parseInt(subtrahend.split(" ")[0]);
			int minuend_min = Integer.parseInt(minuend.split(" ")[2]);
			int subtrahend_min = Integer.parseInt(subtrahend.split(" ")[2]);
			if( (minuend_hr >= subtrahend_hr) && (minuend_min > subtrahend_min) ) {
				hr_diff = minuend_hr - subtrahend_hr;
				min_diff = minuend_min - subtrahend_min;
			}else if( (minuend_hr > subtrahend_hr) && (minuend_min < subtrahend_min) ) {
				hr_diff = minuend_hr - subtrahend_hr - 1;
				min_diff = minuend_min + 60 - subtrahend_min;
			}else {
				System.out.println("can not perform calculation: data error");
				return null;
			}
			diff = hr_diff * 60 + min_diff;
		}else if(minuend.contains("hour") && !subtrahend.contains("hour")) {
			int minuend_hr = Integer.parseInt(minuend.split(" ")[0]);
			int minuend_min = Integer.parseInt(minuend.split(" ")[2]);
			int subtrahend_min = Integer.parseInt(subtrahend.split(" ")[0]);
			if(minuend_min >= subtrahend_min) {
				diff = hr_diff * 60 + minuend_min - subtrahend_min;
			}else {
				diff = (hr_diff - 1) * 60 + 60 + minuend_min - subtrahend_min;
			}
		}else if( !minuend.contains("hour") && !subtrahend.contains("hour")) {
			minuend = minuend.substring(0, minuend.length()-4);
			subtrahend = subtrahend.substring(0, subtrahend.length()-4);
			diff = Integer.parseInt(minuend) - Integer.parseInt(subtrahend);
		}
		return Integer.toString(diff);
	}
	
}
