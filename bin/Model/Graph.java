package bin.Model;
import java.util.*;


public class Graph
{
	// This is a 1-graph.
	// perhaps remove the "private" states
	
	// Contains all the vertices and their positions
	private Map<Integer,Point> verPos;
	@SuppressWarnings("rawtypes")
	
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
	 * 
	 * @return the number of vertices
	 * @version 0.0.1
	 * @author Atero
	 * @since 0.20.2
	 */
	public int nbVer()
	{
		return this.verPos.size();
	}
	
	/**
	 * 
	 * @param k number of the considered vertex
	 * @return the [x,y] array;
	 * @version 0.0.1
	 * @author Atero
	 * @since 0.20.2
	 */
	
	public boolean existsVer(int k)
	{
		return this.verPos.containsKey(k);
	}
	
	public boolean existsVer(int k,int l)
	{
		return this.existsVer(k) && this.existsVer(l);
	}
	
	public int[] getPosVer(int k)
	{
		if (existsVer(k))
			return ((Point) this.verPos.get(k)).getCoord();
		
		else
			return null;
	}
	
	/**
	 * Add a new vertex if it doens't already exist and if the coordinates ain't already taken
	 * 
	 * @param k number of the new vertices
	 * @param x x-position of the vertices
	 * @param y y-position of the vertices
	 * @version 0.0.1
	 * @author Atero
	 * @since 0.20.2
	 * 
	 */
	public void addVer(int k, int x, int y)
	{
		if (!existsVer(k))
		{
			Point newPoint = new Point(x,y);
			for(Point p : this.verPos.values())
			{
				if (newPoint.equals(p))
					return;
			}
			
			this.verPos.put(k, newPoint);
		}
	}
	
	/**
	 * 
	 * @param i
	 * @param j
	 * @param edge
	 * @param symetrical 0 : nothing ; 1 : symmetrical ; -1 : anti-symmetrical 
	 * @since 0.20.2
	 * @author Atero
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addEdge(int i, int j, Edge edge, int symmetrical)
	{
		if(!existsVer(i,j))
		{
			System.out.println("Error : one of the vertices doesn't exist");
			return;
		}
		
		if(!this.oriented)
			symmetrical = 1;
		
		if(hasNeighbour(i))
		{
			HashMap data = this.edges.get(i);
			data.put(j, edge);
			edges.put(i, data);
		}
		
		else
		{
			HashMap m = new HashMap<Integer, Float>();
			m.put(j, edge);
			this.edges.put(i, m);
		}
		

		switch(symmetrical)
		{
		case 1:
			this.addEdge(j, i, edge.reverse(),0);
		break;
		case -1:
			this.rmEdge(j,i);
		break;
		}
	}
	
	public void addEdge(int i, int j, Edge edge)
	{
		this.addEdge(i,j,edge,0);
	}
	
	/**
	 * Remove the edge from i to j (and j to i if this graph is non-oriented)
	 * @param i vertex 1
	 * @param j vertex 2
	 * @version 0.0.1
	 * @author Atero
	 * @since 0.20.2
	 */
	@SuppressWarnings("unchecked")
	public void rmEdge(int i, int j)
	{
		if(hasNeighbour(i))
		{
			@SuppressWarnings("rawtypes")
			HashMap data = this.edges.get(i);
			data.remove(j);
			if(data.size() == 0)
				edges.remove(i);
			
			else
				edges.put(i, data);
		}
		
		if(!this.oriented)
			rmEdge(j,i);
	}
	
	public void rmEdge(int i, int j, boolean bothWays)
	{
		//bothways automatically true for a non-oriented graph
		rmEdge(i,j);
		if(bothWays)
			rmEdge(j,i);
	}
	
	/**
	 * 
	 * @param i number of the vertices
	 * @return true if i has any neighbour (considered in an oriented way)
	 * @version 0.0.1
	 * @author Atero
	 * @since 0.20.2
	 */
	public boolean hasNeighbour(int i)
	{
		if(this.edges.containsKey(i))
		{
			@SuppressWarnings("rawtypes")
			Map data = (Map) this.edges.get(i);
			return data.size() > 0;
		}
			return false;
	}
	
	/**
	 * Determine all the predecessors for the shortest way from any point to another.
	 * @version 0.0.1
	 * @since 0.20.2
	 * @author Atero
	 */
	public void calculPred()
	{
		this.predecessors = new HashMap<>();
		for (Integer i : verPos.keySet())
			predecessors.put(i, Dijkstra(i));
		
	}
	
	/**
	 * 
	 * @param i the number of the vertex
	 * @return All the predecessors for having the shortest way from i to another vertex
	 * @version 0.0.1
	 * @since 0.20.2
	 * @author Atero
	 * @see calculPred
	 */
	public HashMap<Integer,Integer> Dijkstra(int i)
	{
		if(!existsVer(i))
			return null;
		
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
}