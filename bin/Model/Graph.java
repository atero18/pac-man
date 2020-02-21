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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((edges == null) ? 0 : edges.hashCode());
		result = prime * result + (oriented ? 1231 : 1237);
		result = prime * result + ((predecessors == null) ? 0 : predecessors.hashCode());
		result = prime * result + ((verPos == null) ? 0 : verPos.hashCode());
		return result;
	}
	
	// Contains all the vertices and their positions
	private HashMap<Integer,Point> verPos;
	
	// Contain all the edges (keys : the first vertices. Data : all the goals vertices and value of the edge
	private Map<Integer, HashMap<Integer,Edge>> edges;
	private boolean oriented;
	
	// Contain all predecessors
	/**
	 * @see calculPred
	 */
	private Map<Integer, Map<Integer,Integer>> predecessors;
	
	static final float infty = 10000f;
	
	public Graph(boolean oriented)
	{
		this.verPos = new HashMap<>();
		this.edges = new HashMap<>();
		this.oriented = oriented;
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
		
		
		if(!this.oriented && (!edges.containsKey(j) || !edges.get(j).containsKey(i)))
			symmetrical = 1;
		
		
		
		if(hasNeighbour(i))
		{
			
			HashMap data = (HashMap) edges.get(i).clone();
			edges.remove(i);
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
	public void calculPred() throws ModelException
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
	private Map<Integer,Integer> Dijkstra(int i) throws ModelException
	{
		if(!existsVer(i))
			throw new ModelException("This vertex doesn't exist");
		
		HashMap<Integer,Float> distances = new HashMap<>();
		HashMap<Integer,Integer> predess = new HashMap<>();
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Set<Integer> M = ((HashMap) verPos.clone()).keySet();
		
		
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
	 * Give a Queue with U-D-L-R composed of the shortest way between two vertices
	 * @param from your start
	 * @param to your goal
	 * @return Queue composed of the directions have to be taken.
	 */
	public ArrayDeque<Character> goTo(int from, int to) throws ModelException
	{
		if(!existsVer(from,to))
			throw new ModelException("Those vertices don't exist");
		
		if (predecessors == null)
			calculPred();
		
		ArrayDeque<Character> way = new ArrayDeque<>();
		
		int k = to;
		int pred;
		while(k != from)
		{
			pred = predecessors.get(from).get(k);
			way.addFirst(edges.get(pred).get(k).dir);
			k = pred;
		}
		return way;
	}
	
	/**
	 * Transform the matrix-maze in a graph
	 * @param Mat The input matrix-maze
	 * @param codeMat The code use to translate the matrix
	 * @return the new Graph
	 * @throws ModelException
	 */
	public static Graph matToGraph(int[][] Mat, Map<String,Integer> codeMat) throws ModelException
	{
		
		
		// For later : Add a vertex at the ghost regeneration point.
		// And on teleportations.
		Graph g = new Graph();
		HashSet<Integer> notaway = new HashSet<>();
		ArrayList<Integer> tpPoints = new ArrayList<>();

		int nrows = Mat.length;
		int ncols = Mat[0].length;
		
		int k = 1;
		
		for(int i = 0; i < nrows; i++)
		{
			// Used for create edges.
			boolean sameLineCo = false; // Sameline and connected (no walls between)
			int previousJ = 0;
			boolean test = false;
			
			
			for(int j = 0; j < ncols; j++)
			{
				if(!notaway.contains(Mat[i][j]))
				{
					
					// If it exists the equivalent of a 'corner', then it's a vertex
					if(((j > 0 && !notaway.contains(Mat[i][j-1])) || (j + 1 < ncols && !notaway.contains(Mat[i][j+1])))
							&& ((i > 0 && !notaway.contains(Mat[i-1][j])) || (i + 1 < nrows && !notaway.contains(Mat[i+1][j]))))
							test = true;
					
					
					// Or if there is an "IMPASSE" (if the bloc is surrounded by exactly 3 blocs
					if(!test)
					{
						int sides = 0;
						if((j > 0 && notaway.contains(Mat[i][j-1])))
							sides++;
						
						if(j + 1 < ncols && notaway.contains(Mat[i][j+1]))
							sides++;
						
						if(i > 0 && notaway.contains(Mat[i-1][j]))
							sides++;
						
						if(i + 1 < nrows && notaway.contains(Mat[i+1][j]))
							sides++;
						
						if(sides == 3)
							test = true;
					}
					
					
					//Or if it's a teleportation point
					if(!test && codeMat.get("teleport_bloc") == Mat[i][j])
					{
						test = true;
						tpPoints.add(k);
					}
					
					
					if(test)
					{
						test = false;
						g.addVer(k, i, j);
						/* If the previous vertex is on the same row, 
						 * then we create an edge between the previous and the new
						 */
						if(sameLineCo)
						{
							g.addEdge(k-1, k, new Edge((float) j - previousJ, 'R'));
							
						}
						sameLineCo = true;
						previousJ = j;
						k++;
					}
				}
				// In the other case, it means there is a wall between previous and next vertex
				else
					sameLineCo = false;
			}
		}
		
		k = k-1;
		// Now we link them if they are close and on the same column
		for(; k >= 2; k--)
		{
			int col = g.verPos.get(k).y;
			for(int p = k - 1; p >= 1; p--)
			{
				if(g.verPos.get(p).y == col)
				{
					boolean noWalls = true;
					int i = g.verPos.get(p).x + 1;
					while(i < g.verPos.get(k).x && noWalls)
					{
						if(notaway.contains(Mat[i][col]))
							noWalls = false;
						i++;
					}
					if(noWalls)
					{
						g.addEdge(p, k, new Edge((float) g.verPos.get(k).x - g.verPos.get(p).x, 'D'));
						break;
					}
				}
			}
		}
		
		
		//Teleportation points gestion (edges between them)
		//TODO
		
		
		
		g.calculPred();
		return g;
	}
	
	
	public void disp()
	{
		System.out.println("List of all the vertices with their edge(s)");
		for(Integer i : verPos.keySet())
		{
			System.out.print("\n" + i + " " + verPos.get(i) + " : ");
			for(Integer j : edges.get(i).keySet())
			{
				System.out.print(j + " " + edges.get(i).get(j) + "| ");
			}
		}
	}
	
}