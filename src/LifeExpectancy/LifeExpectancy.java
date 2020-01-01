package LifeExpectancy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import java.util.HashMap;
//import java.util.Map;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.providers.OpenStreetMap;
import de.fhpotsdam.unfolding.utils.MapUtils;
import processing.core.PApplet;

public class LifeExpectancy extends PApplet{
	

	Map<String, Float> lifeExpByCountry;
	
	private Map<String, Float> loadLifeExpectancyFromCsv(String fileName)
	{
		Map<String, Float>lifeExpMap = new HashMap<String, Float>();
		String[] rows = loadStrings(fileName);
		for(String row: rows)
		{
			String[] columns = row.split(",");
			try
		    {
		      String string = columns[5];
		      float f = Float.parseFloat(string);
		      lifeExpMap.put(columns[4], f);
		      System.out.println(f);
		    }
		    catch (Exception e)
		    {
		      
		    }
			//System.out.println(Float.parseFloat((String)columns[5]));
			
		}
		return lifeExpMap;
	}


	private void shadeCountries()
	{
		for(Marker M: counteryMarkers)
        {
        	String countryId = M.getId();
        	if(lifeExpByCountry.containsKey(countryId))
        	{
        		float lifeExp = lifeExpByCountry.get(countryId);
        		int colorLevel = (int)map(lifeExp,40,90,10,255);
        		M.setColor(color(255 - colorLevel , 100 , colorLevel));
        	}
        	else
        	{
        		M.setColor(color(150,150,150));
        	}
        }
	}
	
	private static final long serialVersionUID = 1L;
	private UnfoldingMap map ;
	List<Feature> countries;
	List<Marker> counteryMarkers;

	
	
	public void setup()
	{
		size(950,600,OPENGL);
		map = new UnfoldingMap(this, 200, 50, 700, 500, new OpenStreetMap.OpenStreetMapProvider());
		map.zoomToLevel(2);
		MapUtils.createDefaultEventDispatcher(this, map);
        lifeExpByCountry = loadLifeExpectancyFromCsv("LifeExpectancyWorldBankModule3.csv");
        countries = GeoJSONReader.loadData(this,"countries.geo.json");
        counteryMarkers = MapUtils.createSimpleMarkers(countries);
        map.addMarkers(counteryMarkers);
        shadeCountries();
	}
	
	public void draw()
	{
		background(220);
		map.draw();
	}
	
}
