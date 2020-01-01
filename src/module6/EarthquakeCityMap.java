package module6;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.data.ShapeFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.AbstractShapeMarker;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.MultiMarker;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.providers.AbstractMapProvider;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.providers.Microsoft;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.utils.ScreenPosition;
import parsing.ParseFeed;
import processing.core.PApplet;
import processing.core.PGraphics;

/** EarthquakeCityMap
 * An application with an interactive map displaying earthquake data.
 * Author: UC San Diego Intermediate Software Development MOOC team
 * @author Your name here
 * Date: July 17, 2015
 * */
public class EarthquakeCityMap extends PApplet {
	
	// We will use member variables, instead of local variables, to store the data
	// that the setUp and draw methods will need to access (as well as other methods)
	// You will use many of these variables, but the only one you should need to add
	// code to modify is countryQuakes, where you will store the number of earthquakes
	// per country.
	
	// You can ignore this.  It's to get rid of eclipse warnings
	private static final long serialVersionUID = 1L;

	// IF YOU ARE WORKING OFFILINE, change the value of this variable to true
	private static final boolean offline = false;
	
	/** This is where to find the local tiles, for working without an Internet connection */
	public static String mbTilesString = "blankLight-1-3.mbtiles";
	
	

	//feed with magnitude 2.5+ Earthquakes
	private String earthquakesURL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";
	
	// The files containing city names and info and country names and info
	private String cityFile = "city-data.json";
	private String countryFile = "countries.geo.json";
	
	// The map
	private UnfoldingMap map;
	//////////////////////////////////
	// About the Provider:
	AbstractMapProvider Provider1;
	AbstractMapProvider Provider2;
	AbstractMapProvider Provider3;
	//////////////////////////////////
	// Markers for each city
	private List<Marker> cityMarkers;
	// Markers for each earthquake
	private List<Marker> quakeMarkers;

	// A List of country markers
	private List<Marker> countryMarkers;
	
	// For Airport and routes:
	private List<Marker> airportList;
	List<Marker> routeList;
	
	// NEW IN MODULE 5
	private CommonMarker lastSelected;
	private CommonMarker lastClicked;
	
	
	// Here is to create a buffer:
	private PGraphics buffer;
	
	
	//////////////////////////////////////
	private int numOfNearbyEarthQuake;
	private int magnitudeSum;
	private EarthquakeMarker mostRecent;
	private HashMap<Integer, AirportMarker>airportRoutes;
	public void setup() {		
		// (1) Initializing canvas and map tiles
		size(900, 700, OPENGL);
		buffer = createGraphics(900, 700);
		if (offline) {
		    map = new UnfoldingMap(this, 200, 50, 650, 600, new MBTilesMapProvider(mbTilesString));
		    earthquakesURL = "2.5_week.atom";  // The same feed, but saved August 7, 2015
		}
		else {
			Provider1 = new Google.GoogleMapProvider();
			Provider2 = new Microsoft.HybridProvider();
			Provider3 = new Microsoft.RoadProvider();
			
			// IF YOU WANT TO TEST WITH A LOCAL FILE, uncomment the next line
		    //earthquakesURL = "2.5_week.atom";
		}
		
		map = new UnfoldingMap(this, 200, 50, 650, 600, Provider1);
		MapUtils.createDefaultEventDispatcher(this, map);
		// FOR TESTING: Set earthquakesURL to be one of the testing files by uncommenting
		// one of the lines below.  This will work whether you are online or offline
		//earthquakesURL = "test1.atom";
		//earthquakesURL = "test2.atom";
		
		// Uncomment this line to take the quiz
		earthquakesURL = "quiz2.atom";
		
		
		// (2) Reading in earthquake data and geometric properties
	    //     STEP 1: load country features and markers
		List<Feature> countries = GeoJSONReader.loadData(this, countryFile);
		countryMarkers = MapUtils.createSimpleMarkers(countries);
		
		//     STEP 2: read in city data
		List<Feature> cities = GeoJSONReader.loadData(this, cityFile);
		cityMarkers = new ArrayList<Marker>();
		for(Feature city : cities) {
		  cityMarkers.add(new CityMarker(city));
		}
	    
		//     STEP 3: read in earthquake RSS feed
	    List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
	    quakeMarkers = new ArrayList<Marker>();
	    
	    for(PointFeature feature : earthquakes) {
		  //check if LandQuake
		  if(isLand(feature)) {
		    quakeMarkers.add(new LandQuakeMarker(feature));
		  }
		  // OceanQuakes
		  else {
		    quakeMarkers.add(new OceanQuakeMarker(feature));
		  }
	    }

	    
	    //////////////////////////////////
	    // Airport : get features from airport data.
	 	List<PointFeature> features = ParseFeed.parseAirports(this, "airports.dat");
	 	// list for markers, hashmap for quicker access when matching with routes
	 	airportList = new ArrayList<Marker>();
	 	HashMap<Integer, Location> airports = new HashMap<Integer, Location>();
	 	airportRoutes = new HashMap<Integer , AirportMarker>();
	 	
	 	// create markers from features
	 	for(PointFeature feature : features) {
	 	AirportMarker m = new AirportMarker(feature);
	 	m.setRadius(5);
	 	airportList.add(m);
	 	// put airport in hashmap with OpenFlights unique id for key
	 	airports.put(Integer.parseInt(feature.getId()), feature.getLocation());
	 	airportRoutes.put(Integer.parseInt(feature.getId()), m);
	 	}
	 		
	 	List<ShapeFeature> routes = ParseFeed.parseRoutes(this, "routes.dat");
	 	routeList = new ArrayList<Marker>();
		for(ShapeFeature route : routes) {
		// get source and destination airportIds
		int source = Integer.parseInt((String)route.getProperty("source"));
		int dest = Integer.parseInt((String)route.getProperty("destination"));
		// get locations for airports on route
		if(airports.containsKey(source) && airports.containsKey(dest)) {
			route.addLocation(airports.get(source));
			route.addLocation(airports.get(dest));
		}			
		
		SimpleLinesMarker sl = new SimpleLinesMarker(route.getLocations(), route.getProperties());
		sl.setHidden(true);
		routeList.add(sl);
		
		if(airports.containsKey(source) && airports.containsKey(dest)) {
			airportRoutes.get(source).addRoute(sl);
			airportRoutes.get(dest).addRoute(sl);
		}
		//System.out.println(route.getProperties());
		}
	 		
	    // could be used for debugging
	    printQuakes();
	 		
	    // (3) Add markers to map
	    //     NOTE: Country markers are not added to the map.  They are used
	    //           for their geometric properties
	    map.addMarkers(routeList);
	    map.addMarkers(airportList);
	    map.addMarkers(quakeMarkers);
	    map.addMarkers(cityMarkers);
	    
	    sortAndPrint(20);
	}  // End setup
	
	
	public void draw() {
		/////////////////////////////////////////////
		// Start The Buffer
		buffer.beginDraw();
		background(0);
		map.draw();
		buffer.endDraw();
		image(buffer,0,0);
		addKey();
		buffer.clear();
		// End Of The Buffer
		///////////////////////////////////////////////
		
		
		
		/////////////////////////////////////////
		//To Show Latitude , Longitude
		showLat_Long();
		////////////////////////////////////////
		
		
		
		///////////////////////////////////////////////////////////////////
		// This To draw info over the marker no overlapping.
		if(lastSelected != null)
		{
			lastSelected.drawTitleOnTopOfTheBuffer(buffer, mouseX, mouseY);
		}
		/////////////////////////////////////////////////////////////////////
		
		
		
		
		////////////////////////////////////////////////////////////////////
		//To Draw Lines between Ocean Quakes and the effected cities.
		if( lastClicked instanceof OceanQuakeMarker)
		{
			for(Marker m : cityMarkers)
			{
				if (lastClicked.getDistanceTo(m.getLocation()) 
						<= ((EarthquakeMarker) lastClicked).threatCircle())
				{
					ScreenPosition loc1 = map.getScreenPosition(m.getLocation());
					ScreenPosition loc2 = map.getScreenPosition(lastClicked.getLocation());
					((OceanQuakeMarker)lastClicked).drawLines(buffer, loc1.x , loc1.y , loc2.x,loc2.y);
					
				}
			}
		}
		////////////////////////////////////////////////////////////////////////
		
		
		
		////////////////////////////////////////////
		// To draw The PopUp Menu:
		else if(lastClicked instanceof CityMarker)
		{
			popUpMenu();
		}
		
		
		
	}
	
	
	// TODO: Add the method:
	   private void sortAndPrint(int numToPrint) {
		   
		   	List<EarthquakeMarker> unsorted = new ArrayList<EarthquakeMarker>();
		   	for(Marker quake : quakeMarkers)
		   	{
		   		unsorted.add(((EarthquakeMarker)quake));
		   	}
		   	Collections.sort(unsorted);
		   	Object [] sorted = unsorted.toArray();
		   	int arrLen = sorted.length;
		   	if(numToPrint > arrLen)
		   	{
		   		for(int i = 0; i < arrLen ; i++)
		   		{
		   			System.out.println(sorted[i]);
		   		}
		   	}else
		   	{
		   		for(int i = 0 ; i < numToPrint ; i++)
		   		{
		   			System.out.println(sorted[i]);
		   		}
		   	}
		   	
		  
	   }
	// and then call that method from setUp
	
	/** Event handler that gets called automatically when the 
	 * mouse moves.
	 */
	   /////////////////////////////////////////
	   //    KeyBoard Access:
	   public void keyPressed()
	   {
		   if(key == '1')
		   {
			   map.mapDisplay.setMapProvider(Provider1);
		   }
		   else if(key == '2')
		   {
			   map.mapDisplay.setMapProvider(Provider2);
		   }
		   else if(key == '3')
		   {
			   map.mapDisplay.setMapProvider(Provider3);
		   }
		   else if(key == '4')
		   {
			   showRecentQuake();
		   }
		   else if(key == '5')
		   {
			   unhideMarkers();
		   }
	   }
	   ////////////////////////////////////////
	   
	@Override
	public void mouseMoved()
	{
		// clear the last selection
		if (lastSelected != null) {
			lastSelected.setSelected(false);
			lastSelected = null;
		
		}
		selectMarkerIfHover(quakeMarkers);
		selectMarkerIfHover(cityMarkers);
		selectMarkerIfHover(airportList);
		
		//loop();
	}
	
	// If there is a marker selected 
	private void selectMarkerIfHover(List<Marker> markers)
	{
		// Abort if there's already a marker selected
		if (lastSelected != null) {
			return;
		}
		
		for (Marker m : markers) 
		{
			CommonMarker marker = (CommonMarker)m;
			if (marker.isInside(map,  mouseX, mouseY)) {
				lastSelected = marker;
				marker.setSelected(true);
				return;
			}
		}
	}
	
	/** The event handler for mouse clicks
	 * It will display an earthquake and its threat circle of cities
	 * Or if a city is clicked, it will display all the earthquakes 
	 * where the city is in the threat circle
	 */
	@Override
	public void mouseClicked()
	{
		if (lastClicked != null) {
			lastClicked.setClicked(false);
			lastClicked = null;
			unhideMarkers();
			numOfNearbyEarthQuake = 0;
			magnitudeSum = 0;
			mostRecent = null;
			hideRoutes();
			
		}
		else if (lastClicked == null) 
		{
			checkEarthquakesForClick();
			//showAirportByThreatCircle();
			if (lastClicked == null) {
				checkCitiesForClick();
				//showNearAirports();
				if(lastClicked == null )
				{
					checkForAirport();
				}
			}
			
		}
	}
	
	// Helper method that will check if a city marker was clicked on
	// and respond appropriately
	private void checkCitiesForClick()
	{
		if (lastClicked != null) return;
		// Loop over the earthquake markers to see if one of them is selected
		for (Marker marker : cityMarkers) {
			if (!marker.isHidden() && marker.isInside(map, mouseX, mouseY)) {
				lastClicked = (CommonMarker)marker;
				showNearAirports(airportList);
				// Hide all the other earthquakes and hide
				for (Marker mhide : cityMarkers) {
					if (mhide != lastClicked) {
						mhide.setHidden(true);
					}
				}
				for (Marker mhide : quakeMarkers) {
					EarthquakeMarker quakeMarker = (EarthquakeMarker)mhide;
					if (quakeMarker.getDistanceTo(marker.getLocation()) 
							> quakeMarker.threatCircle()) {
						quakeMarker.setHidden(true);
					}
					else
					{
						quakeMarker.setHidden(false);
						numOfNearbyEarthQuake++;
						magnitudeSum+= quakeMarker.getMagnitude();
						String age = quakeMarker.getStringProperty("age");
						if ("Past Hour".equals(age) || "Past Day".equals(age)) {
							mostRecent = quakeMarker;
						}
					}
				}
				return;
			}
		}		
	}
	
	// Helper method that will check if an earthquake marker was clicked on
	// and respond appropriately
	private void checkEarthquakesForClick()
	{
		if (lastClicked != null) return;
		// Loop over the earthquake markers to see if one of them is selected
		for (Marker m : quakeMarkers) {
			EarthquakeMarker marker = (EarthquakeMarker)m;
			if (!marker.isHidden() && marker.isInside(map, mouseX, mouseY)) {
				lastClicked = marker;
				lastClicked.setClicked(true);
				//showAirportByThreatCircle(airportList);
				
				// Hide all the other earthquakes and hide
				for (Marker mhide : quakeMarkers) {
					if (mhide != lastClicked) {
						mhide.setHidden(true);
					}
				}
				for (Marker mhide : cityMarkers) {
					if (mhide.getDistanceTo(marker.getLocation()) 
							> marker.threatCircle()) {
						mhide.setHidden(true);
					}
				}
				
				for (Marker mhide : airportList) {
					if (mhide.getDistanceTo(marker.getLocation()) 
							> marker.threatCircle()) {
						mhide.setHidden(true);
					}
				}
				//lastClicked.setClicked(false);
				
				return;
			}
		}
	}
	
	private void checkForAirport()
	{
		List<String>routes = new ArrayList<String>();
		if (lastClicked != null) return;
		for (Marker m : airportList) {
			AirportMarker marker = (AirportMarker)m;
			if (!marker.isHidden() && marker.isInside(map, mouseX, mouseY)) {
				lastClicked = marker;
				lastSelected.setSelected(true);
				//lastClicked.setClicked(true);
				for(Marker route : ((AirportMarker)lastClicked).routes)
				{
						route.setHidden(false);
						route.setColor(color(255,0,0));
						if(!routes.contains(route.getStringProperty("destination")))
						{
							routes.add(route.getStringProperty("destination"));
						}
								
					}
				}
				
			}
			for(Marker m : airportList)
			{
				if(!routes.contains(m.getStringProperty("id")) && m != lastClicked )
				{
					m.setHidden(true);
				}
			}
	}
	


	private void showRecentQuake()
	{
		for(Marker quake : quakeMarkers)
		{
			String age = quake.getStringProperty("age");
			if ("Past Hour".equals(age) || "Past Day".equals(age)) {
				quake.setHidden(false);
			}
			else
			{
				quake.setHidden(true);
			}
		}
	}
	
	
	
	////////////////////////////////////
	// Show Nearest Airport  within 50 Km
	public void showNearAirports(List<Marker>airports)
	{
		for(Marker airport : airports)
		{
			if(airport.getDistanceTo(lastClicked.getLocation()) < 50)
			{
				airport.setHidden(false);
			}
			else
			{
				airport.setHidden(true);
			}
		}
	}
	////////////////////////////////////////
	
	
	/////////////////////////////////////////////////////////
	// Show the routes to another Airports and Hide The rest

	////////////////////////////////////////////////////////
	
	//////////////////////////////////////
	// To Show All The Airports that effected by ThreatCircle
	public void showAirportByThreatCircle(List<Marker>airports)
	{
		for(Marker a : airports)
		{
			if(a.getDistanceTo(lastClicked.getLocation()) > ((EarthquakeMarker)lastClicked).threatCircle()) 
			{
				a.setHidden(true);
			}
			else
			{
				a.setHidden(true);
			}
		}
	}
	///////////////////////////////////////
	
	
	// To hide All Routes :
	public void hideRoutes()
	{
		for(Marker m : routeList)
		{
			m.setHidden(true);
		}
	}
	///////////////////////////////////////
		
	

	
	
	
	
	
	
	//
	// loop over and unhide all markers
	private void unhideMarkers() {
		for(Marker marker : quakeMarkers) {
			marker.setHidden(false);
		}
			
		for(Marker marker : cityMarkers) {
			marker.setHidden(false);
		}
		
		for(Marker marker: airportList)
		{
			marker.setHidden(false);
		}
	}
	
	// helper method to draw key in GUI
	private void addKey() {	
		// Remember you can use Processing's graphics methods here
		fill(255, 250, 240);
		
		int xbase = 25;
		int ybase = 50;
		
		rect(xbase, ybase, 150, 350);
		
		fill(0);
		textAlign(LEFT, CENTER);
		textSize(12);
		text("Earthquake Key", xbase+25, ybase+25);
		
		fill(150, 30, 30);
		int tri_xbase = xbase + 35;
		int tri_ybase = ybase + 50;
		triangle(tri_xbase, tri_ybase-CityMarker.TRI_SIZE, tri_xbase-CityMarker.TRI_SIZE, 
				tri_ybase+CityMarker.TRI_SIZE, tri_xbase+CityMarker.TRI_SIZE, 
				tri_ybase+CityMarker.TRI_SIZE);

		fill(0, 0, 0);
		textAlign(LEFT, CENTER);
		text("City Marker", tri_xbase + 15, tri_ybase);
		
		text("Land Quake", xbase+50, ybase+70);
		text("Ocean Quake", xbase+50, ybase+90);
		text("Size ~ Magnitude", xbase+25, ybase+110);
		
		fill(255, 255, 255);
		ellipse(xbase+35, 
				ybase+70, 
				10, 
				10);
		rect(xbase+35-5, ybase+90-5, 10, 10);
		
		fill(color(255, 255, 0));
		ellipse(xbase+35, ybase+140, 12, 12);
		fill(color(0, 0, 255));
		ellipse(xbase+35, ybase+160, 12, 12);
		fill(color(255, 0, 0));
		ellipse(xbase+35, ybase+180, 12, 12);
		
		textAlign(LEFT, CENTER);
		fill(0, 0, 0);
		text("Shallow", xbase+50, ybase+140);
		text("Intermediate", xbase+50, ybase+160);
		text("Deep", xbase+50, ybase+180);

		text("Past hour", xbase+50, ybase+200);
		
		fill(255, 255, 255);
		int centerx = xbase+35;
		int centery = ybase+200;
		ellipse(centerx, centery, 12, 12);

		strokeWeight(2);
		line(centerx-8, centery-8, centerx+8, centery+8);
		line(centerx-8, centery+8, centerx+8, centery-8);
		
		textAlign(LEFT, CENTER);
		fill(0, 0, 0);
		text("Key Shortcuts", xbase+17, ybase+230);
		text("1 GoogleMap", xbase+17, ybase+250);
		text("2 Hybrid", xbase+17, ybase+270);
		text("3 Road", xbase+17, ybase+290);
		text("4 Recent Earthquakes", xbase+17, ybase+310);
		text("5 Show All", xbase+17, ybase+330);
	}

	private void showLat_Long()
	{
		int xbase = 25;
		int ybase = 50;
		fill(255);
		rect(xbase+650 , ybase+610 , 175 , 30);
		fill(0);
		Location pos = map.getLocation(mouseX, mouseY);
		text(pos.getLat()+" , "+ pos.getLon(), 685, 675);
	}
	
	
	private void popUpMenu()
	{
		fill(250);
		int xbase = 25;
		int ybase = 420;
		rect(xbase,ybase,150,190);
		
		fill(255,0,0);
		textAlign(LEFT,CENTER);
		textSize(20);
		text(numOfNearbyEarthQuake, xbase+100, ybase+20);
		fill(0);
		textSize(12);
		text("Nearby", xbase+17, ybase+15);
		text("Earthquakes", xbase+17, ybase+30);
		text("Average", xbase+17, ybase+55);
		text("Magnitude", xbase+17, ybase+70);
		textSize(20);
		float avg;
		if(numOfNearbyEarthQuake == 0)
		{
			avg = 0;
		}
		else
		{
			avg = magnitudeSum / numOfNearbyEarthQuake ;
		}
		text(avg, xbase+80, ybase+60);
		textSize(12);
		text("Most Recent", xbase+17, ybase+105);
		text("Earthquake", xbase+17, ybase+120);
		if(mostRecent != null)
		{
			String[] title = mostRecent.getTitle().split("-");
			String magnitude = title[0].trim();
			String distanceKM = title[1].trim().substring(0, title[1].indexOf("of")+1);
			String location = title[1].substring(title[1].indexOf("of")+2).trim();
			text(magnitude, xbase+17, ybase+140);
			text(distanceKM, xbase+17, ybase+155);
			text(location, xbase+17, ybase+170);
		} else {
			textSize(14);
			text("None", xbase+17, ybase+140);
		
		}
	}
	// Checks whether this quake occurred on land.  If it did, it sets the 
	// "country" property of its PointFeature to the country where it occurred
	// and returns true.  Notice that the helper method isInCountry will
	// set this "country" property already.  Otherwise it returns false.
	private boolean isLand(PointFeature earthquake) {
		
		// IMPLEMENT THIS: loop over all countries to check if location is in any of them
		// If it is, add 1 to the entry in countryQuakes corresponding to this country.
		for (Marker country : countryMarkers) {
			if (isInCountry(earthquake, country)) {
				return true;
			}
		}
		
		// not inside any country
		return false;
	}
	
	// prints countries with number of earthquakes
	// You will want to loop through the country markers or country features
	// (either will work) and then for each country, loop through
	// the quakes to count how many occurred in that country.
	// Recall that the country markers have a "name" property, 
	// And LandQuakeMarkers have a "country" property set.
	private void printQuakes() {
		int totalWaterQuakes = quakeMarkers.size();
		for (Marker country : countryMarkers) {
			String countryName = country.getStringProperty("name");
			int numQuakes = 0;
			for (Marker marker : quakeMarkers)
			{
				EarthquakeMarker eqMarker = (EarthquakeMarker)marker;
				if (eqMarker.isOnLand()) {
					if (countryName.equals(eqMarker.getStringProperty("country"))) {
						numQuakes++;
					}
				}
			}
			if (numQuakes > 0) {
				totalWaterQuakes -= numQuakes;
				System.out.println(countryName + ": " + numQuakes);
			}
		}
		System.out.println("OCEAN QUAKES: " + totalWaterQuakes);
	}
	
	
	
	// helper method to test whether a given earthquake is in a given country
	// This will also add the country property to the properties of the earthquake feature if 
	// it's in one of the countries.
	// You should not have to modify this code
	private boolean isInCountry(PointFeature earthquake, Marker country) {
		// getting location of feature
		Location checkLoc = earthquake.getLocation();

		// some countries represented it as MultiMarker
		// looping over SimplePolygonMarkers which make them up to use isInsideByLoc
		if(country.getClass() == MultiMarker.class) {
				
			// looping over markers making up MultiMarker
			for(Marker marker : ((MultiMarker)country).getMarkers()) {
					
				// checking if inside
				if(((AbstractShapeMarker)marker).isInsideByLocation(checkLoc)) {
					earthquake.addProperty("country", country.getProperty("name"));
						
					// return if is inside one
					return true;
				}
			}
		}
			
		// check if inside country represented by SimplePolygonMarker
		else if(((AbstractShapeMarker)country).isInsideByLocation(checkLoc)) {
			earthquake.addProperty("country", country.getProperty("name"));
			
			return true;
		}
		return false;
	}

}
