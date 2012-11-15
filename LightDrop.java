import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;


public class LightDrop extends Drop
{

	public LightDrop(float inx, float iny)
	{
		super(inx, iny);
	}

	public LightDrop(Point position)
	{
		super(position);
	}
	public void loop()
	{
		ox = x;
		oy = y;
		odx = dx;
		ody = dy;
		
		dx += pdx;
		dy += pdy;

		dx += Water.gravityx * 0.6f;
		dy += Water.gravityy * 0.6f;
		
		//dx *= 1.01;
		//dy *= 1.001;
		
		x += dx;
		y += dy;
		pdx = 0;
		pdy = 0;
	}
	public void draw(Graphics g, float p)
	{
		float xxx = 1 - 1f/(float)(Methods.linstep((odx * odx + ody * ody), (dx * dx + dy * dy), p) * 0.1f + 1.001f);

		g.setColor(new Color(0.6f + 0.4f * xxx, xxx * 0.8f, xxx * 0.8f));
		
		float smoothx = Methods.smoothstep(ox - odx, odx, x - dx, dx, p);
		float smoothy = Methods.smoothstep(oy - ody, ody, y - dy, dy, p);
		//System.out.println(smoothx + " " + smoothy);
		//g.translate(x, y);
		g.drawLine((int)smoothx, (int)smoothy, (int)(smoothx + 1 * Methods.linstep(odx, dx, p)), (int)(smoothy + 1 * Methods.linstep(ody, dy, p)));
		//g.fillOval((int)smoothx - 4, (int)smoothy - 4, 8, 8);
	}
}
