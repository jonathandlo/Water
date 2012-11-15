import java.awt.*;

public class Drop
{
	protected float x, y;
	protected float dx, dy;
	protected float pdx, pdy; //burst modifiers
	protected float ox, oy, odx, ody;
	
	protected float forcemod;
	
	public Drop(float inx, float iny)
	{
		x = inx;
		y = iny;
		
		dx = 0;
		dy = 0;
		
		pdx = 0;
		pdy = 0;
		
		forcemod = 1;
	}
	public Drop(Point position)
	{
		x = position.x;
		y = position.y;
		
		dx = 0;
		dy = 0;
		
		pdx = 0;
		pdy = 0;
		
		forcemod = 1;
	}
	public void force(float indx, float indy)
	{
		pdx += indx;
		pdy += indy;
	}
	public void loop()
	{
		ox = x;
		oy = y;
		odx = dx;
		ody = dy;
		
		dx += pdx;
		dy += pdy;

		dx += Water.gravityx;
		dy += Water.gravityy;
		
		//dx *= 1.01;
		//dy *= 1.001;
		
		x += dx;
		y += dy;
		pdx = 0;
		pdy = 0;
	}
	public void draw(Graphics g, float p)
	{
		float xxx = 1 - 1f/(float)(Methods.linstep((odx * odx + ody * ody), (dx * dx + dy * dy), p) * 0.2f + 1.001f);
		
		g.setColor(new Color(xxx * 0.8f, xxx * 0.8f, 0.6f + 0.4f * xxx));
		
		float smoothx = Methods.smoothstep(ox - odx, odx, x - dx, dx, p);
		float smoothy = Methods.smoothstep(oy - ody, ody, y - dy, dy, p);
		//System.out.println(smoothx + " " + smoothy);
		//g.translate(x, y);
		g.drawLine((int)smoothx, (int)smoothy, (int)(smoothx + 1 * Methods.linstep(odx, dx, p)), (int)(smoothy + 1 * Methods.linstep(ody, dy, p)));
		//g.fillOval((int)smoothx - 4, (int)smoothy - 4, 8, 8);
	}
}
