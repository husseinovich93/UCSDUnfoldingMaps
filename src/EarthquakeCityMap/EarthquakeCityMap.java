package EarthquakeCityMap;
import java.util.ArrayList;
import java.util.List;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.OpenStreetMap;
import de.fhpotsdam.unfolding.utils.MapUtils;
import parsing.ParseFeed;
import processing.core.PApplet;
public class EarthquakeCityMap extends PApplet {
	
	private static final long serialVersionUID = 1L;
	private UnfoldingMap map ;

	
	private void addKey()
	{
		fill(255);
		rect(10, 50, 180, height/2);
		
		textSize(20);
		fill(0);
		text("Earthquake Key",30,100);
		textSize(10);
		text("5.0+ Magnitude",60,150);
		text("4.0+ Magnitude",60,200);
		text("Below 4.0 ",60,250);
		
		
		
		fill(255,0,0);
		ellipse(30,150,10,10);
		
		fill(255,255,0);
		ellipse(30,200,8,8);
		
		fill(0,0, 255);
		ellipse(30,250,5,5);
		
	}
	public void setup()
	{
		size(950,600,OPENGL);
		map = new UnfoldingMap(this, 200, 50, 700, 500, new OpenStreetMap.OpenStreetMapProvider());
		map.zoomToLevel(2);
		MapUtils.createDefaultEventDispatcher(this, map);
		List<PointFeature> bigEqs = new ArrayList<PointFeature>();
		
		bigEqs = ParseFeed.parseEarthquake(this, "2.5_week.atom");
		
		//Location valLoc1 = new Location(-38.29f,-73.05f);
		//Location valLoc2 = new Location(61.02f,-147.65f);
		//Location valLoc3 = new Location(3.30f, 95.75f);
		//Location valLoc4 = new Location(38.322f,142.369f);
		//Location valLoc5 = new Location(52.76f,160.06f);
//		
//		Feature valEq = new PointFeature();
//		Feature alaskaEq = new PointFeature();
//		Feature sumatraEq = new PointFeature();
//		Feature japanEq = new PointFeature();
//		Feature kamchatkaEq = new PointFeature();
//		
//		bigEqs.add((PointFeature) valEq);
//		bigEqs.add((PointFeature) alaskaEq);
//		bigEqs.add((PointFeature) sumatraEq);
//		bigEqs.add((PointFeature) japanEq);
//		bigEqs.add((PointFeature) kamchatkaEq);
//		
//		bigEqs.get(0).setLocation(new Location(-38.29f,-73.05f));
//		bigEqs.get(0).addProperty("title", "Valdivia Chile");
//		bigEqs.get(0).addProperty("magnitude", "9.5");
//		bigEqs.get(0).addProperty("date", "May 22, 1960");
//		bigEqs.get(0).addProperty("year", "1960");
//		
//		bigEqs.get(1).setLocation(new Location(61.02f,-147.65f));
//		bigEqs.get(1).addProperty("title", "Alaska");
//		bigEqs.get(1).addProperty("magnitude", "9.2");
//		bigEqs.get(1).addProperty("date", "Mar 28, 1964");
//		bigEqs.get(1).addProperty("year", "1964");
//		
//		bigEqs.get(2).setLocation(new Location(3.30f, 95.75f));
//		bigEqs.get(2).addProperty("title", "sumatraEq");
//		bigEqs.get(2).addProperty("magnitude", "9.1");
//		bigEqs.get(2).addProperty("date", "Dec 26, 2004");
//		bigEqs.get(2).addProperty("year", "2004");
//		
//		bigEqs.get(3).setLocation(new Location(38.322f,142.369f));
//		bigEqs.get(3).addProperty("title", "Japan");
//		bigEqs.get(3).addProperty("magnitude", "9.0");
//		bigEqs.get(3).addProperty("date", "Mar 11, 2011");
//		bigEqs.get(3).addProperty("year", "2011");
//		
//		bigEqs.get(4).setLocation(new Location(52.76f,160.06f));
//		bigEqs.get(4).addProperty("title", "kamchatkaEq");
//		bigEqs.get(4).addProperty("magnitude", "9.0");
//		bigEqs.get(4).addProperty("date", "Nov 04, 1960");
//		bigEqs.get(4).addProperty("year", "1952");
//		
		

		List<Marker> bigEqMarker = new ArrayList<Marker>();
		for(PointFeature PF : bigEqs)
		{
			bigEqMarker.add(new SimplePointMarker(PF.getLocation() , PF.getProperties()));
		}
//		int yellow = color(255, 255, 0);
//		int gray = color(150,150,150);
		SimplePointMarker s;
		for(Marker MK: bigEqMarker)
		{
			try
			{
				s = (SimplePointMarker) MK;
				Object mag = MK.getProperty("magnitude");
				float magFloat = Float.parseFloat(mag.toString());
				if(magFloat > 5.0)
				{
					MK.setColor(color(255,0,0));
					s.setRadius(10);
				}
				else if(magFloat > 4.0 && magFloat < 5.0)
				{
					MK.setColor(color(255,255,0));
					s.setRadius(8);
				}
				else
				{
					MK.setColor(color(0,0,255));
					s.setRadius(5);
				}
				//int magColorLevel = (int)(map(magFloat,2,6,10,255));
				//MK.setColor(color(255, 255-magColorLevel, magColorLevel));
				//int MagColorLevel = (int)map(mag,)
				//System.out.println(magFloat);

			}
			catch(Exception e)
			{
				
			}
			
						//System.out.println(MK.getId());
//			int year = Integer.parseInt((String) MK.getProperty("year"));
//			//System.out.println(year);
//
//			if(year > 2000 )
//			{
//				MK.setColor(yellow);
//			}
//			else
//			{
//				MK.setColor(gray);
//			}
			
		}
		map.addMarkers(bigEqMarker);
		
	}
	
	public void draw()
	{
		background(0);
		addKey();
		map.draw();
	}
}
