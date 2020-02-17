package bin.Model;
import java.util.*;

public class Edge {
	
	float val;
	char dir;
	
	private static HashSet<Character> directions;
	private static HashMap<Character, Character> opposites;
	
	
	
	public Edge(float val, char dir) throws Exception
	{
		this.val = val;
		if(directions.contains(dir))
			this.dir = dir;
		
		else
			throw new Exception("incorrect direction indicated");
		
	}
	
	/**
	 * Initialize the static Collections (Map, Set)
	 * Initialized in Graph
	 * @since 0.20.2
	 * @version 0.0.1
	 * @author Atero
	 */
	public static void initialize()
	{
		// Condition could be improved
		if(directions == null || directions.size() == 0)
		{
			directions.add('U');
			directions.add('D');
			directions.add('L');
			directions.add('R');
		}
		if(opposites == null || opposites.size() == 0)
		{
			opposites.put('U','D');
			opposites.put('D','U');
			opposites.put('L', 'R');
			opposites.put('R', 'L');
		}
	}
	
	/**
	 * 
	 * @return same edge but with opposite direction
	 * @version 0.0.1
	 * @since 0.20.2
	 * @author Atero
	 */
	public Edge reverse()
	{
		try
		{
			return new Edge(this.val, opposites.get(this.dir));
		}
		
		catch(Exception e)
		{
			System.exit(-1);
			return null;
		}
	}

}
