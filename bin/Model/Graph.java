package bin.Model;
import java.util.*;


/**
 * @author Atero
 * @version 0.0.1
 * @since 0.20.2
 */
public class Graph
{
	// This is a 1-graph.
	// perhaps remove the "private" states
	
	// Contains all the vertices and their positions
	private Map<Integer,Point> verPos;
	
	// Contain all the edges (keys : the first vertices. Data : all the goals vertices and value of the edge
	private Map<Integer, HashMap<Integer,Edge>> edges;
	private boolean oriented;
	
	// Contain all predecessors
	/**
	 * @see calculPred
	 */
	private Map<Integer, HashMap<Integer,Integer>> predecessors;
	
	static final float infty = 10000f;
	
	public Graph(boolean oriented)
	{
		this.verPos = new HashMap<>();
		this.edges = new HashMap<>();
		this.oriented = oriented;
		Edge.initialize();
		this.predecessors = null;
	}
	
	public Graph()
	{
		this(false);
	}
	
	
	/**
	 * Get number of vertices
	 * @return the number of vertices
	 */
	public int nbVer()
	{
		return verPos.size();
	}
	
	/**
	 * Check if a vertex exists
	 * @param k number of the considered vertex
	 */
	
	public boolean existsVer(int k)
	{
		return verPos.containsKey(k);
	}
	
	/**
	 * Check if the two vertices exist
	 */
	public boolean existsVer(int k,int l)
	{
		return existsVer(k) && existsVer(l);
	}
	
	public int[] getPosVer(int k)
	{
		if (existsVer(k))
			return ((Point) verPos.get(k)).getCoord();
		
		else
			return null;
	}
	
	/**
	 * Add a new vertex if it doens't already exist and if the coordinates ain't already taken
	 * @param k number of the new vertices
	 * @param x x-position of the vertices
	 * @param y y-position of the vertices
	 */
	public void addVer(int k, int x, int y)
	{
		if (!existsVer(k))
		{
			Point newPoint = new Point(x,y);
			for(Point p : verPos.values())
			{
				if (newPoint.equals(p))
					return;
			}
			
			this.verPos.put(k, newPoint);
		}
	}
	
	/**
	 * Add new edge to the graph (with symmetrical parameter)
	 * @param symetrical 0 : nothing ; 1 : symmetrical ; -1 : anti-symmetrical 
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addEdge(int i, int j, Edge edge, int symmetrical) throws ModelException
	{
		if(!existsVer(i,j))
		{
			throw new ModelException("At least one of the vertices doesn't exist");
		}
		
		if(!this.oriented)
			symmetrical = 1;
		
		if(hasNeighbour(i))
		{
			HashMap data = edges.get(i);
			data.put(j, edge);
			edges.put(i, data);
		}
		
		else
		{
			HashMap m = new HashMap<Integer, Float>();
			m.put(j, edge);
			edges.put(i, m);
		}
		

		switch(symmetrical)
		{
		case 1:
			addEdge(j, i, edge.reverse(),0);
		break;
		case -1:
			rmEdge(j,i);
		break;
		}
	}
	
	/**
	 * Add new edge to the graph
	 */
	public void addEdge(int i, int j, Edge edge) throws ModelException
	{
		this.addEdge(i,j,edge,0);
	}
	
	/**
	 * Remove the edge from i to j (and j to i if this graph is non-oriented)
	 * @param i vertex 1
	 * @param j vertex 2
	 */
	@SuppressWarnings("unchecked")
	public void rmEdge(int i, int j)
	{
		if(hasNeighbour(i))
		{
			@SuppressWarnings("rawtypes")
			HashMap data = edges.get(i);
			data.remove(j);
			if(data.size() == 0)
				edges.remove(i);
			
			else
				edges.put(i, data);
		}
		
		if(!this.oriented)
			rmEdge(j,i);
	}
	
	/**
	 * Remove the edge from i to j with bothWays parameter (and j to i if this graph is non-oriented)
	 * @param bothWays if true, j-i edge is also removed
	 */
	public void rmEdge(int i, int j, boolean bothWays)
	{
		//bothways automatically true for a non-oriented graph
		rmEdge(i,j);
		if(bothWays)
			rmEdge(j,i);
	}
	
	/**
	 * Check if a vertex has some neighbor
	 * @param i number of the vertex
	 * @return true if i has any neighbor (considered in an oriented way)
	 */
	public boolean hasNeighbour(int i)
	{
		if(this.edges.containsKey(i))
		{
			@SuppressWarnings("rawtypes")
			Map data = (Map) edges.get(i);
			return data.size() > 0;
		}
			return false;
	}
	
	/**
	 * Determine all the predecessors for the shortest way from any point to another.
	 */
	private void calculPred() throws ModelException
	{
		this.predecessors = new HashMap<>();
		for (Integer i : verPos.keySet())
			predecessors.put(i, Dijkstra(i));
		
	}
	
	/**
	 * @param i the number of the vertex
	 * @return All the predecessors for having the shortest way from i to another vertex
	 * @see calculPred
	 */
	private HashMap<Integer,Integer> Dijkstra(int i) throws ModelException
	{
		if(!existsVer(i))
			throw new ModelException("This vertex doesn't exist");
		
		HashMap<Integer,Float> distances = new HashMap<>();
		HashMap<Integer,Integer> predess = new HashMap<>();
		Set<Integer> M = verPos.keySet();
		
		for(Integer k : M)
		{
			distances.put(k, infty);
			predess.put(k,k);
		}
		distances.put(i, 0f);
		
		while(!M.isEmpty())
		{
			int iMin = -1;
			for(Integer k : M)
			{
				if (iMin == -1 || distances.get(k) < distances.get(iMin))
					iMin = k;
			}
			M.remove(iMin);
			for(Integer k : edges.get(iMin).keySet())
			{
				if (distances.get(iMin) + edges.get(iMin).get(k).val < distances.get(k))
				{
					distances.put(k, distances.get(iMin) + edges.get(iMin).get(k).val);
					predess.put(k,iMin);
				}
			}
		}
		
		return predess;
	}
	
	/**
	 * Give a String with U-D-L-R composed of the shortest way between two vertices
	 * @param from your start
	 * @param to your goal
	 * @return string composed of the directions have to be taken.
	 */
	public String goTo(int from, int to) throws ModelException
	{
		if(!existsVer(from,to))
			throw new ModelException("Those vertices don't exist");
		
		if (predecessors == null)
			calculPred();
		
		String way = "";
		
		int k = to;
		int pred;
		while(k != from)
		{
			pred = predecessors.get(from).get(k);
			way =  edges.get(pred).get(k).dir + way;
			k = pred;
		}
		return way;
	}
}