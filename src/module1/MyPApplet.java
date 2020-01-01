package module1;
import processing.core.*;
public class MyPApplet extends PApplet{
	private String URL = "https://upload.wikimedia.org/wikipedia/commons/thumb/1/1b/The_Narrows_-_panoramio.jpg/1024px-The_Narrows_-_panoramio.jpg";
	private PImage backgroundImg;
	
	public void setup()
	{
		size(400, 400);
		background(200,200,200);
		backgroundImg = loadImage(URL , "jpg");
		backgroundImg.resize(0,height);
		image(backgroundImg,0,0);
		
	}
	
	public void draw()
	{
		int [] color = setSunCollor(second());
		fill(color[0],color[1],color[2]);
		ellipse(width/4, height/5, width/5, height/5 );
	}
	public int[] setSunCollor(float seconds)
	{
		int [] rgb = new int[3];
		float awayFrom30 = Math.abs(30-seconds);
		float ratio = awayFrom30 / 30 ; 
		rgb[0] = (int)(ratio * 255);
		rgb[1] = (int)(ratio * 255);
		rgb[2] = 0;
		 return rgb;
	}
}
