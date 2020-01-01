package module6;

import java.util.ArrayList;
import java.util.List;

import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import processing.core.PConstants;
import processing.core.PGraphics;

/** 
 * A class to represent AirportMarkers on a world map.
 *   
 * @author Adam Setters and the UC San Diego Intermediate Software Development
 * MOOC team
 *
 */
public class AirportMarker extends CommonMarker {
	public  List<SimpleLinesMarker> routes;
	
	public AirportMarker(Feature city) {
		super(((PointFeature)city).getLocation(), city.getProperties());
		routes = new ArrayList<SimpleLinesMarker>();
	}
	
	@Override
	public void drawMarker(PGraphics pg, float x, float y) {
		pg.fill(11);
		pg.ellipse(x, y, 5, 5);
		
		
	}

	@Override
	public void showTitle(PGraphics pg, float x, float y) {
		//////////////////////////////////////////////////
	   // show rectangle with title : 
		String Title = getName() + "-" + getCity() + ","+ getCountry();
		String code = "IATA code:" + getCode();
		String altitude = "Altitude: " + getAltitude();
		float latitude = getLocation().getLat();
		float longitude = getLocation().getLon();
		Title = Title.replace("\"", "");
		code = code.replace("\"", "");
		float width = pg.textWidth(Title);
		String Lat_Lon = latitude + ", "+longitude;
		
		pg.pushStyle();
		
		
		pg.rectMode(PConstants.CORNER);
		pg.fill(255);
		pg.textSize(12);
		pg.rect(x+15, y-8, Math.max(width, pg.textWidth(Lat_Lon)+12),65,5,5,5,5);
		pg.textAlign(PConstants.LEFT,PConstants.TOP);
		pg.fill(20, 24, 35);
		pg.text(code, x+22, y - 5);
		pg.text(Title, x+22, y + 8);
		pg.text(altitude, x+22, y + 22);
		pg.text(Lat_Lon, x+22, y + 37);
		
		pg.popStyle();
	}
	
	
	public void addRoute(SimpleLinesMarker s)
	{
		routes.add(s);
	}
	
	public String getName()
	{
		return this.getStringProperty("name");
	}
	
	public String getCity()
	{
		return this.getStringProperty("city");
	}
	
	public String getCountry()
	{
		return this.getStringProperty("country");
	}
	
	public String getCode()
	{
		return this.getStringProperty("code");
	}
	
	public String getAltitude()
	{
		return this.getStringProperty("altitude");
	}
}
