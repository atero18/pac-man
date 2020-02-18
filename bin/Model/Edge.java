package bin.Model;
import java.util.*;

/**
 * @author Atero
 * @version 0.0.1
 * @since 0.20.2
 */
public class Edge {
	
	float val;
	char dir;
	
	private static HashSet<Character> directions;
	private static HashMap<Character, Character> opposites;
	
	
	
	public Edge(float val, char dir) throws ModelException
	{
		this.val = val;
		if(directions.contains(dir))
			this.dir = dir;
		
		else
			throw new ModelException("incorrect direction indicated");
		
	}
	
	/**
	 * Initialize the static Collections (Map, Set)
	 * Initialized in Graph
	 */
	public static void initialize()
	{
		directions.add('U');
		directions.add('D');
		directions.add('L');
		directions.add('R');
	
		opposites.put('U','D');
		opposites.put('D','U');
		opposites.put('L', 'R');
		opposites.put('R', 'L');
	
	}
	
	/**
	 * Give a new edge with same value, opposite direction
	 * @return same edge but with opposite direction
	 */
	public Edge reverse() throws ModelException
	{
		return new Edge(this.val, opposites.get(this.dir));
	}

}
