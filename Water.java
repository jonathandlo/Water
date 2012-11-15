import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import javax.swing.*;

public class Water extends JFrame implements MouseListener, MouseMotionListener, WindowListener, KeyListener
{
	private static final 	long			serialVersionUID = -813775451752793379L;
	private static final	int				updaterate = 50;
	private static 			float 			wallgap = 4f;
	public static final		int				maxx = 1200, maxy = 400;
	public static			Random 			rand;
	public static 		 	float 			gravityx = 0f;
	public static 		 	float 			gravityy = 0f;
	protected 				BufferStrategy	buffer;
	
	protected final 		float 			waterforce = 5f;
	protected 				ArrayList<Drop>	drops;
	protected 				boolean			running;
	protected 				int				mousedown; //0 - none, 1 - left, 2 - right
	protected 				boolean			keydownA;
	protected 				float			lastwallx, lastwally;
	
	public static void main(String[] args)
	{
		Water frame = new Water();
		frame.setSize					(maxx, maxy);
		frame.setMinimumSize			(new Dimension(maxx + 6, maxy + 25));
		frame.setDefaultCloseOperation	(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo		(null);
		frame.pack						();
		frame.setVisible				(true);

		frame.start();
	}
	
	public Water()
	{
		rand = new Random();
		drops = new ArrayList<Drop>();

		for (int i = 0; i < 500; i++)
			drops.add(new LightDrop(rand.nextInt(200), rand.nextInt(300)));
		for (int i = 0; i < 700; i++)
			drops.add(new Drop(-rand.nextInt(200) + maxx, rand.nextInt(300)));
		
		mousedown = 0;
		running = true;
	}
	public void start()
	{
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addKeyListener(this);
		repaint();
		createBufferStrategy(2);
		buffer = getBufferStrategy();
		gameloop();
	}
	public void gameloop()
	{
		Graphics graphics = buffer.getDrawGraphics();
		long lastdraw;
		long now;
		loop();
		
		for (int i = 0; i < 5; i++)
		{
			draw(graphics, 0);
		}
		
		while (running)
		{
			try	{Thread.sleep(600L);} catch (InterruptedException e){} 
			lastdraw = System.currentTimeMillis();
			graphics = buffer.getDrawGraphics();
			
			while (this.isVisible())
			{
				now = System.currentTimeMillis();
				
				do
				{
					draw(graphics, 1 + ((float)Math.min(now - lastdraw, 0) / updaterate));
					now = System.currentTimeMillis();
				} while (lastdraw > now);
				
				lastdraw += updaterate;
				loop();
			}
		}
	}

	public void loop()
	{
		gravityx = 0;
		gravityy = 0.6f;
		Point mousepos = getMousePosition();
		
		if (mousedown > 0 && (mousepos != null))
			switch (mousedown)
			{
				case 1 :
					gravityx = 0.6f;
					gravityy = 0;
					break;
				case 2 :
					float dist2mouse = dist(lastwallx, lastwally, mousepos.x, mousepos.y);
					
					while (dist2mouse > wallgap)
					{
						float newx = lastwallx + (wallgap * (mousepos.x - lastwallx)) / dist2mouse;
						float newy = lastwally + (wallgap * (mousepos.y - lastwally)) / dist2mouse;
						
						drops.add(new StaticDrop(newx, newy));
						lastwallx = newx;
						lastwally = newy;

						dist2mouse = dist(lastwallx, lastwally, mousepos.x, mousepos.y);
					}
					break;
			}
		if (keydownA)
		{
			keydownA = false;
			for (int i = drops.size() - 1; i >= 0; i--)
				if (drops.get(i).getClass().equals(StaticDrop.class))
					drops.remove(i);
		}
		
		for (int i = 0; i < drops.size(); i++)
		{
			for (int j = i + 1; j < drops.size(); j++)
			{
				force(drops.get(i), drops.get(j));
			}
		}

		for (int i = 0; i < drops.size(); i++)
		{
			forcewall(drops.get(i));
			drops.get(i).loop();
		}
	}
	public void draw(Graphics g, float p)
	{
		g.setColor(new Color(0, 0, 0));
		g.fillRect(0, 0, maxx, maxy);
		g.setColor(Color.CYAN);
	
		for (Drop i : drops)
		{
			i.draw(g, p);
		}
		
		buffer.show();
	}
	
	private void force(Drop d1, Drop d2)
	{
		float maxdist = 12 * waterforce;
		float dist = (float) Math.sqrt((d1.x - d2.x) * (d1.x - d2.x) + (d1.y - d2.y) * (d1.y - d2.y));
		
		if (dist > maxdist)
			return;
		if (dist < 0.0001)
			return;
		
		float dx = (d1.x - d2.x) / dist;
		float dy = (d1.y - d2.y) / dist;
		dist /= maxdist;
		
		float fx = (float) (1 - 3 * dist /(2 * dist * dist + 1)) * (1 - dist);

		d1.force(waterforce * fx * dx, waterforce * fx * dy);
		d2.force(-waterforce * fx * dx, -waterforce * fx * dy);

		if (dist < 0.03)
		{
			
			float main = 0.8f;
			float speedmult = 0.3f;
			
			d1.force(speedmult * (d1.dx * main + d2.dx * (1 - main)) - d1.dx, speedmult * (d1.dy * main + d2.dy * (1 - main)) - d1.dy);
			d2.force(speedmult * (d1.dx * (1 - main) + d2.dx * main) - d2.dx, speedmult * (d1.dy * (1 - main) + d2.dy * main) - d2.dy);
		}
		else if (dist < 0.3)
		{
			float distpercent = dist - 0.3f;
			
			float main = 1 + distpercent * 0.3f;
			float speedmult = 1 - distpercent * 0.01f;
			
			d1.force(speedmult * (d1.dx * main + d2.dx * (1 - main)) - d1.dx, speedmult * (d1.dy * main + d2.dy * (1 - main)) - d1.dy);
			d2.force(speedmult * (d1.dx * (1 - main) + d2.dx * main) - d2.dx, speedmult * (d1.dy * (1 - main) + d2.dy * main) - d2.dy);
		}
	}
	private void forcewall(Drop d)
	{
		float maxdist = 20;
		float fx = 0, fy = 0, dx = d.pdx + d.dx, dy = d.pdy + d.dy;

		if (d.x < maxdist)
		{
			dx = 1f + Math.max(Math.abs(gravityx), Math.abs(dx) + gravityx);
			fx = 1;
		}
		else if(d.x > maxx - maxdist)
		{
			dx = -1f - Math.max(Math.abs(gravityx), Math.abs(dx) + gravityx);
			fx = 1;
		}	
		if (d.y < maxdist)
		{
			dy = 1f + Math.max(Math.abs(gravityy), Math.abs(dy) + gravityy);
			fy = 1;
		}
		else if(d.y > maxy - maxdist)
		{
			dy = -1f - Math.max(Math.abs(gravityy), Math.abs(dy) + gravityy);
			fy = 1;
		}
		
		if (fx + fy == 0) return;
		
		d.force(fx * dx, fy * dy);
	}
	private float dist(float x1, float y1, float x2, float y2)
	{
		return (float)Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	}
	
	@Override
	public void paint(Graphics g)
	{
		
	}
	@Override
	public void mouseClicked(MouseEvent e)
	{

	}
	@Override
	public void mouseEntered(MouseEvent e)
	{

	}
	@Override
	public void mouseExited(MouseEvent e)
	{

	}
	@Override
	public void mousePressed(MouseEvent e)
	{
		if (e.getButton() == MouseEvent.BUTTON1)
		{
			mousedown = 1;
		}
		else
		{
			mousedown = 2;
			lastwallx = e.getX();
			lastwally = e.getY();
		}
	
	}
	@Override
	public void mouseReleased(MouseEvent e)
	{
		mousedown = 0;
	}
	@Override
	public void windowActivated(WindowEvent arg0)
	{

	}
	@Override
	public void windowClosed(WindowEvent arg0)
	{
		running = false;
		setVisible(false);
	}
	@Override
	public void windowClosing(WindowEvent arg0)
	{

	}
	@Override
	public void windowDeactivated(WindowEvent arg0)
	{

	}
	@Override
	public void windowDeiconified(WindowEvent arg0)
	{

	}
	@Override
	public void windowIconified(WindowEvent arg0)
	{

	}
	@Override
	public void windowOpened(WindowEvent arg0)
	{
		
	}

	@Override
	public void mouseDragged(MouseEvent arg0)
	{
		
	}

	@Override
	public void mouseMoved(MouseEvent arg0)
	{
		
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		
		if (e.getKeyCode() == KeyEvent.VK_A)
			keydownA = true;
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
	}
}