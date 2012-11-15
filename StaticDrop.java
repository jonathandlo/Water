import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;


public class StaticDrop extends Drop
{

	public StaticDrop(float inx, float iny)
	{
		super(inx, iny);
	}

	public StaticDrop(Point position)
	{
		super(position);
	}
	public void force(float indx, float indy)
	{

	}
	public void loop()
	{
		pdx = 0;
		pdy = 0;
		dx = 0;
		dy = 0;
	}
	public void draw(Graphics g, float p)
	{
		g.setColor(Color.BLUE);
		g.drawOval((int)x - 2, (int)y - 2, 5, 5);
	}
}
