package src.Model;


/**
 * @author Atero
 * @version 0.0.1
 * @since 0.20.2
 */
public class Point<E>
{
	E x,y;
	
	public Point(E x, E y)
	{
		this.x = x;
		this.y = y;
	}

	
	
	/**
	 * @return true if both points have same coordinates
	 * @override
	 */
	public boolean equals(Object o)
	{
		if(o instanceof Point)
		{
			@SuppressWarnings("rawtypes")
			Point p = (Point) o;
			return this.x.getClass() == p.x.getClass() && this.x == p.x && this.y == p.y; 
		}	
		return false;
	}
	
	/**
	 * @override
	 */
	public String toString()
	{
		return "(" + x + "," + y + ")";
	}
}
