package bin.Model;


/**
 * @author Atero
 * @version 0.0.1
 * @since 0.20.2
 */
public class Point
{
	int x,y;
	
	public Point(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public int[] getCoord()
	{
		int[] t = {this.x,this.y};
		return t;
	}
	
	
	/**
	 * @return true if both points have same coordinates
	 * @override
	 */
	public boolean equals(Object o)
	{
		if(o instanceof Point)
		{
			Point p = (Point) o;
			return this.x == p.x && this.y == p.y; 
		}
		
		return false;
	}

}
