

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.JMapViewerTree;
import org.openstreetmap.gui.jmapviewer.Layer;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import org.openstreetmap.gui.jmapviewer.MapPolygonImpl;
import org.openstreetmap.gui.jmapviewer.OsmTileLoader;
import org.openstreetmap.gui.jmapviewer.events.JMVCommandEvent;
import org.openstreetmap.gui.jmapviewer.interfaces.JMapViewerEventListener;
import org.openstreetmap.gui.jmapviewer.interfaces.TileLoader;
import org.openstreetmap.gui.jmapviewer.interfaces.TileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.BingAerialTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;

public class Map extends JFrame implements JMapViewerEventListener {

 private static final long serialVersionUID = 1L;
 private final JMapViewerTree treeMap;
 private final JLabel zoomLabel;
 private final JLabel zoomValue;
 private final JLabel mperpLabelName;
 private final JLabel mperpLabelValue;
 
 MySQL mysql = new MySQL();
 General general = new General();

 public Map() throws IOException{

     super("MEMO");
     setSize(400, 400);

     treeMap = new JMapViewerTree("MEMO");

     // Listen to the map viewer for user operations so components will
     // receive events and update
     map().addJMVListener(this);

     setLayout(new BorderLayout());
     setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     setExtendedState(JFrame.MAXIMIZED_BOTH);
     JPanel panel = new JPanel(new BorderLayout());
     JPanel panelTop = new JPanel();
     JPanel panelBottom = new JPanel();
     JPanel helpPanel = new JPanel();

     mperpLabelName = new JLabel("Meters/Pixels: ");
     mperpLabelValue = new JLabel(String.format("%s", map().getMeterPerPixel()));

     zoomLabel = new JLabel("Zoom: ");
     zoomValue = new JLabel(String.format("%s", map().getZoom()));

     add(panel, BorderLayout.NORTH);
     add(helpPanel, BorderLayout.SOUTH);
     panel.add(panelTop, BorderLayout.NORTH);
     panel.add(panelBottom, BorderLayout.SOUTH);

     //JLabel helpLabel = new JLabel("Use right mouse button to move,\n "
     //        + "left double click or mouse wheel to zoom.");
     //helpPanel.add(helpLabel);
     JButton button = new JButton("setDisplayToFitMapMarkers");
     button.addActionListener(e -> map().setDisplayToFitMapMarkers());
     JComboBox<TileSource> tileSourceSelector = new JComboBox<>(new TileSource[] {
             new OsmTileSource.Mapnik(),
             new OsmTileSource.TransportMap(),
             new BingAerialTileSource(),
     });
     tileSourceSelector.addItemListener(new ItemListener() {
         @Override
         public void itemStateChanged(ItemEvent e) {
             map().setTileSource((TileSource) e.getItem());
         }
     });
     JComboBox<TileLoader> tileLoaderSelector;
     tileLoaderSelector = new JComboBox<>(new TileLoader[] {new OsmTileLoader(map())});
     tileLoaderSelector.addItemListener(new ItemListener() {
         @Override
         public void itemStateChanged(ItemEvent e) {
             map().setTileLoader((TileLoader) e.getItem());
         }
     });
     map().setTileLoader((TileLoader) tileLoaderSelector.getSelectedItem());
     panelTop.add(tileSourceSelector);
     panelTop.add(tileLoaderSelector);
     final JCheckBox showMapMarker = new JCheckBox("Map markers visible");
     showMapMarker.setSelected(map().getMapMarkersVisible());
     showMapMarker.addActionListener(e -> map().setMapMarkerVisible(showMapMarker.isSelected()));
     panelBottom.add(showMapMarker);
     ///
     final JCheckBox showTreeLayers = new JCheckBox("Tree Layers visible");
     showTreeLayers.addActionListener(e -> treeMap.setTreeVisible(showTreeLayers.isSelected()));
     panelBottom.add(showTreeLayers);
     ///
     final JCheckBox showToolTip = new JCheckBox("ToolTip visible");
     showToolTip.addActionListener(e -> map().setToolTipText(null));
     panelBottom.add(showToolTip);
     ///
     final JCheckBox showTileGrid = new JCheckBox("Tile grid visible");
     showTileGrid.setSelected(map().isTileGridVisible());
     showTileGrid.addActionListener(e -> map().setTileGridVisible(showTileGrid.isSelected()));
     panelBottom.add(showTileGrid);
     final JCheckBox showZoomControls = new JCheckBox("Show zoom controls");
     showZoomControls.setSelected(map().getZoomControlsVisible());
     showZoomControls.addActionListener(e -> map().setZoomControlsVisible(showZoomControls.isSelected()));
     panelBottom.add(showZoomControls);
     final JCheckBox scrollWrapEnabled = new JCheckBox("Scrollwrap enabled");
     scrollWrapEnabled.addActionListener(e -> map().setScrollWrapEnabled(scrollWrapEnabled.isSelected()));
     panelBottom.add(scrollWrapEnabled);
     panelBottom.add(button);

     panelTop.add(zoomLabel);
     panelTop.add(zoomValue);
     panelTop.add(mperpLabelName);
     panelTop.add(mperpLabelValue);

     add(treeMap, BorderLayout.CENTER);
     //////////////////////////////////////////////////////
     
     //////////////////////////////////////////////////////
     ArrayList<Coordinate> route_coordinates = general.read_coordinates_from_txt("route_coordinates.txt");
     System.out.println(route_coordinates);
	 for(int j=0;j<route_coordinates.size()-1;j++) {
		 ArrayList<Coordinate> temp = new ArrayList<Coordinate>();
		 temp.add(route_coordinates.get(j));
		 temp.add(route_coordinates.get(j+1));
		 temp.add(route_coordinates.get(j+1));
		 MapPolygonImpl poly_j = new MapPolygonImpl(temp);
		 poly_j.setColor(Color.MAGENTA);
		 map().addMapPolygon(poly_j);
	 }
     //////////////////////////////////////////////////////
     Layer pois_layer = treeMap.addLayer("places of interest");
     ArrayList<String> places_details = mysql.select_places_with_coordinates();
     int num_of_places = places_details.size()/2;
     for(int i=0; i<num_of_places; i++) {
    	 Float lat = Float.parseFloat(places_details.get(i*2+1).split(", ")[0]);
    	 Float lon = Float.parseFloat(places_details.get(i*2+1).split(", ")[1]);
    	 MapMarkerDot pois = new MapMarkerDot(pois_layer, places_details.get(i*2), lat, lon);
    	 pois.setColor(Color.blue);
    	 pois.setBackColor(Color.blue);
    	 map().addMapMarker(pois);
     }
     //////////////////////////////////////////////////////
     MapMarkerDot origin = new MapMarkerDot(pois_layer, "work", 51.5032725, -0.1262261);
     MapMarkerDot desti = new MapMarkerDot(pois_layer, "home", 51.5038173, -0.0785951);
     map().addMapMarker(origin);
     map().addMapMarker(desti);
     //////////////////////////////////////////////////////
     map().setDisplayToFitMapMarkers();
     ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
     map().addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
             if (e.getButton() == MouseEvent.BUTTON1) {
                 map().getAttribution().handleAttribution(e.getPoint(), true);
             }
         }
     });

     map().addMouseMotionListener(new MouseAdapter() {
         @Override
         public void mouseMoved(MouseEvent e) {
             Point p = e.getPoint();
             boolean cursorHand = map().getAttribution().handleAttributionCursor(p);
             if (cursorHand) {
                 map().setCursor(new Cursor(Cursor.HAND_CURSOR));
             } else {
                 map().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
             }
             if (showToolTip.isSelected()) map().setToolTipText(map().getPosition(p).toString());
         }
     });
 }

 private JMapViewer map() {
     return treeMap.getViewer();
 }


 
 private void updateZoomParameters() {
     if (mperpLabelValue != null)
         mperpLabelValue.setText(String.format("%s", map().getMeterPerPixel()));
     if (zoomValue != null)
         zoomValue.setText(String.format("%s", map().getZoom()));
 }

 @Override
 public void processCommand(JMVCommandEvent command) {
     if (command.getCommand().equals(JMVCommandEvent.COMMAND.ZOOM) ||
             command.getCommand().equals(JMVCommandEvent.COMMAND.MOVE)) {
         updateZoomParameters();
     }
 }
 
 	//help functions
	ArrayList<String> read_lines_from_file(String file_name) throws IOException{
		File file = new File(file_name);
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line = null;
		ArrayList<String> content = new ArrayList<String>();
		while( (line = reader.readLine()) != null) {
			//System.out.println(line);
			content.add(line);
		}
		reader.close();
		//System.out.println(content);
		return content;
	}
	
	ArrayList<Coordinate> read_coordinates_from_file(String file_name) throws IOException{
		ArrayList<String> coor_str = read_lines_from_file(file_name);
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
	
	ArrayList<Coordinate> get_midpoints_between_destinations() throws IOException{
		ArrayList<Coordinate> midpoints = new ArrayList<Coordinate>();
		ArrayList<String> destinations_points = read_lines_from_file("user/destinations/optimized_destinations_coordinates.txt");////////
		for(int i=0;i<destinations_points.size()-1;i++) {
			String line1 = destinations_points.get(i); String line2 = destinations_points.get(i+1);
			line1 = line1.substring(1, line1.length()-1); line2 = line2.substring(1, line2.length()-1);
			String[] split1 = line1.split(","); String[] split2 = line2.split(",");
			Double lon1 = Double.parseDouble(split1[0]);
			Double lat1 = Double.parseDouble(split1[1]);
			Double lon2 = Double.parseDouble(split2[0]);
			Double lat2 = Double.parseDouble(split2[1]);
			Double lon_mid = (lon1+lon2)/2;
			Double lat_mid = (lat1+lat2)/2;
			midpoints.add(new Coordinate(lon_mid, lat_mid));
		}
		return midpoints;
	}
	
	int get_trip_interval() throws IOException {
		ArrayList<String> destinations = read_lines_from_file("user/destinations/optimized_destinations.txt");
		return destinations.size()-1;
	}
}

