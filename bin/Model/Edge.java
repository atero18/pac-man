package bin.Model;
import java.util.*;

/**
 * @author Atero
 * @author Aleam
 * @version 0.0.1
 * @since 0.20.2
 */
public class Edge {
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + dir;
		result = prime * result + Float.floatToIntBits(val);
		return result;
	}
	
	public float getVal() {
		return val;
	}

	public void setVal(float val) {
		this.val = val;
	}

	public char getDir() {
		return dir;
	}

	public void setDir(char dir) {
		this.dir = dir;
	}

	float val;
	char dir;
	
	private static Set<Character> directions = null;
	private static Map<Character, Character> opposites = null;
	
	
	
	public Edge(float val, char dir) throws ModelException
	{
		this.val = val;
		if(directions == null || opposites == null)
			initialize();
		
		if(directions.contains(dir))
			this.dir = dir;
		
		else
			throw new ModelException("incorrect direction indicated");
		
	}
	
	/**
	 * Initialize the static Collections (Map, Set)
	 */
	public static void initialize()
	{
		directions = new HashSet<Character>();
		opposites = new HashMap<Character, Character>();
		
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
	
	public String toString()
	{
		return "(" + val + "," + dir + ")";
	}

}
