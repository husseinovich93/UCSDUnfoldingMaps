# UCSDUnfoldingMaps
Real world earthquake data visualization using Unfolding Maps library and Processing GUI

- Earthquake data from https://earthquake.usgs.gov/earthquakes/
- Processing - https://processing.org

![FinalProject](https://user-images.githubusercontent.com/47123359/71643958-a5fcae80-2c8e-11ea-9d6e-2382373b2e4e.png)

# Module 6 contains the final output of the program.
Capabilities includes:

  **1. Keyboard Shortcut**
- Change map providers.
- Show only the recent earthquakes or show them all.

**2. Mouse Hover**

- Show a marker's detail drawn on top of other graphics.

- Dynamic latitude and longitude on the lower right side of the map.

**3. Mouse Click** 

   - When clicking a city marker
      - Shows the airports within 50km.
      - Shows an earthquake if the city is affected by its threat circle.
      - Draw a line between an Ocean Earthquake and the affected city.
      - Hides other city markers.
      - A popup menu appears on the left side of the map to show the count of nearby earthquakes, 
         average magnitude, and the most recent earthquake occurred.  

   - When clicking an earthquake marker
      - Shows a city marker if it is within its threat circle.
      - Shows airports affected by its threat circle.
      - Hides other earthquake markers.

   - When clicking an airport marker
      - Draw a line to its routes and hides other airports.     
