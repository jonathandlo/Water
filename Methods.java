public class Methods
{
	public static float smoothstep(float p1, float m1, float p2, float m2, float t)
	{
	   float t2 = t * t;
	   float t3 = t2 * t;

	   return (2 * t3 - 3 * t2 + 1) * p1 + (t3 - 2 * t2 + t) * m1 - (2 * t3 - 3 * t2) * p2 + (t3 - t2) * m2;
	}
	public static float linstep(float y0, float y1, float percent)
	{
		return (y0 + percent * (y1 - y0));
	}
	
	public enum Direction
	{
		UP, DOWN, LEFT, RIGHT;
	}
}
