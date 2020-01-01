package module5;

import java.awt.List;
import java.util.ArrayList;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.utils.ScreenPosition;
import processing.core.PGraphics;

/** Implements a visual marker for ocean earthquakes on an earthquake map
 * 
 * @author UC San Diego Intermediate Software Development MOOC team
 * @author Your name here
 *
 */
public class OceanQuakeMarker extends EarthquakeMarker {
	private UnfoldingMap map2;
	private java.util.List<Marker> l1 = new ArrayList<Marker>();
	public OceanQuakeMarker(PointFeature quake , UnfoldingMap map1, java.util.List<Marker> cityMarkers) {
		super(quake);
		
		// setting field in earthquake marker
		isOnLand = false;
		map2 = map1;
		l1 = cityMarkers;
	}
	

	/** Draw the earthquake as a square */
	@Override
	public void drawEarthquake(PGraphics pg, float x, float y) {
		pg.rect(x-radius, y-radius, 2*radius, 2*radius);
		
		
			if((this).clicked == true)
			{
				for(Marker m : l1)
				{
					if(((CityMarker)m).isHidden() == false)
					{
						System.out.println(m.getStringProperty("name"));
						//ScreenPosition sp1 = map2.getScreenPosition(this.getLocation());
						//ScreenPosition sp2 = map2.getScreenPosition(((CityMarker)m).getLocation());
						
						
						float x2 = ((CityMarker)m).xCord;
						float y2 = ((CityMarker)m).yCord;
						pg.line(x, y, x2, y2);
					}
				}
			}
		
	}
}
