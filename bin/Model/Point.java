/**
 * 
 */
package bin.Model;


public class Point
{
	private int x,y;
	
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
	
	public int x()
	{
		return this.x;
	}
	
	public int y()
	{
		return this.y;
	}
	
	/**
	 * @return true if both points have same coordinates
	 * @version 0.0.1
	 * @author Atero
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
