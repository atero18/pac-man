package bin.Model;
import java.util.*;
import java.lang.Math;


public class Graph
{
	// This is a 1-graph.
	private Map<Integer,Point> verPos;
	private Map edges;
	private boolean oriented;
	
	public Graph(boolean oriented)
	{
		this.verPos = new HashMap<>();
		this.edges = new HashMap<Integer, HashMap<Integer,Float>>();
		this.oriented = oriented;
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
	 * @param k number of the considered vertice
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
	 * Add a new vertices if it doens't already exist and if the coordinates ain't already taken
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
	 * @param val
	 * @param symetrical 0 : nothing ; 1 : symmetrical ; -1 : anti-symmetrical 
	 * @since 0.20.2
	 * @author Atero
	 */
	public void addEdge(int i, int j, float val, int symmetrical)
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
			Map data = (Map) this.edges.get(i);
			data.put(j, val);
			edges.put(i, data);
		}
		
		else
		{
			HashMap m = new HashMap<Integer, Float>();
			m.put(j, val);
			this.edges.put(i, m);
		}
		

		switch(symmetrical)
		{
		case 1:
			this.addEdge(j, i, val,0);
		break;
		case -1:
			this.rmEdge(j,i);
		break;
		}
	}
	
	public void addEdge(int i, int j, float val)
	{
		this.addEdge(i,j,val,0);
	}
	
	/**
	 * Remove the edge from i to j (and j to i if this graph is non-oriented)
	 * @param i vertices 1
	 * @param j vertices 2
	 * @version 0.0.1
	 * @author Atero
	 * @since 0.20.2
	 */
	public void rmEdge(int i, int j)
	{
		if(hasNeighbour(i))
		{
			Map data = (Map) this.edges.get(i);
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
			Map data = (Map) this.edges.get(i);
			return data.size() > 0;
		}
			return false;
	}
}