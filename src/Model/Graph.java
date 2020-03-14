package src.Model;
import java.util.*;


/**
 * @author Atero
 * @version 0.0.1
 * @since 0.20.2
 */

public class Graph
{
	// This is a 1-graph.
	
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
	HashMap<Integer,Point<Integer>> verPos;
	
	// Contain all the edges (keys : the first vertices. Data : all the goals vertices and value of the edge
	Map<Integer, HashMap<Integer,Edge>> edges;
	private boolean oriented;
	
	// Contain all predecessors
	/**
	 * @see calculPred
	 */
	private Map<Integer, Map<Integer,Integer>> predecessors;
	
	static final float infty = 1000000f;
	

	/**
	 * @return the edges
	 */
	public Map<Integer, HashMap<Integer, Edge>> getEdges() {
		return edges;
	}


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
	 * Add a new vertex if it doens't already exist and if the coordinates ain't already taken
	 * @param k number of the new vertices
	 * @param x x-position of the vertices
	 * @param y y-position of the vertices
	 */
	public boolean addVer(int k, int x, int y)
	{
		if (!existsVer(k))
		{
			Point<Integer> newPoint = new Point<>(x,y);
			for(Point<Integer> p : verPos.values())
			{
				if (newPoint.equals(p))
					return false;
			}
			
			this.verPos.put(k, newPoint);
			return true;
		}
		return false;
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
	 * @param pos Position in the maze
	 * @param dir The direction the being is on
	 * @return the closest Vertex, 0 if not found
	 */
	public int closestVertex(Point<Float> pos, char dir)
	{
		float min = infty;
		int closest = 0;
		for(Integer k : verPos.keySet())
		{
			Point<Integer> p = verPos.get(k);
			switch(dir)
			{
			case 'R':
				if(p.y == Math.round(pos.y) && p.x - pos.x < min)
				{
					min = p.x - pos.x;
					closest = k;
				}
			break;
			case 'L':
				if(p.y == Math.round(pos.y) && pos.x - p.x < min)
				{
					min = pos.x - p.x;
					closest = k;
				}
			break;
			case 'U':
				if(p.x == Math.round(pos.x) && pos.y - p.y < min)
				{
					min = pos.y - p.y;
					closest = k;
				}
			break;
			case 'D':
				if(p.x == Math.round(pos.x) && p.y - pos.y < min)
				{
					min = p.y - pos.y;
					closest = k;
				}
			break;
			}
		}
		return closest;
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
	
	public Point<Integer> getPosVer(int k)
	{
		if (existsVer(k))
			return verPos.get(k);
		
		else
			return null;
	}
	
	/**
	 * Add new edge to the graph (with symmetrical parameter)
	 * @param symetrical 0 : nothing ; 1 : symmetrical ; -1 : anti-symmetrical ; -2 : Cancel auto-(j,i) in oriented
	 * @throws ModelException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addEdge(int i, int j, Edge edge, int symmetrical) throws ModelException
	{
		if(!existsVer(i,j))
		{
			throw new ModelException("At least one of the vertices doesn't exist");
		}
		
		
		if(symmetrical != -2 && !this.oriented && (!edges.containsKey(j) || !edges.get(j).containsKey(i)))
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
			rmEdge(j,i,false);
		break;
		}
	}
	
	/**
	 * Add new edge to the graph
	 * @throws ModelException
	 */
	public void addEdge(int i, int j, Edge edge) throws ModelException
	{
		this.addEdge(i,j,edge,0);
	}
	
	
	/**
	 * @param pA first point
	 * @param pB second point
	 * @param order true if you also want to check if an edge exists pB-pA 
	 * @return true if it exists an edge pA-pB
	 */
	public boolean existsEdge(Point<Integer> pA, Point<Integer> pB, boolean order)
	{
		
		if(verPos == null || verPos.size() == 0 || edges == null || edges.size() == 0)
			return false;
		
		int a = -1, b = -1;
		
		for(Integer i : verPos.keySet())
		{
			if(pA.equals(verPos.get(i)))
				a = i;
			else if(pB.equals(verPos.get(i)))
				b = i;
		}
		if(a == -1 || b == -1)
			return false;
		
		if(edges.containsKey(a) && edges.get(a).containsKey(b))
			return true;
		
		else if(order && edges.containsKey(b) && edges.get(b).containsKey(a))
			return true;
		
		return false;
	}
	
	/**
	 * @param k the considered vertex
	 * @return all the direction we can go to from the vertex k
	 */
	public char[] getDir(int k) throws ModelException
	{
		ArrayList<Character> liste = new ArrayList<>();
		if(!verPos.containsKey(k))
			throw new ModelException("getDir : this vertex doesn't exist");
		
		if(edges.get(k).size() == 0)
			return null;
		
		for(Integer i : edges.get(k).keySet())
			liste.add(edges.get(k).get(i).getDir());
		
		char[] t = new char[liste.size()];
		for(int i = 0; i < t.length; i++)
			t[i] = liste.get(i);
		
		return t;
		
	}
	
	/**
	 * Remove the edge from i to j (and j to i if this graph is non-oriented)
	 * Set bothWays value = !oriented
	 * @param i vertex 1
	 * @param j vertex 2
	 */
	public void rmEdge(int i, int j)
	{
		rmEdge(i,j,!this.oriented);
	}
	
	/**
	 * Remove the edge from i to j with bothWays parameter (and j to i if this graph is non-oriented)
	 * @param bothWays if true, j-i edge is also removed (automatically true for a non-oriented graph)
	 */
	@SuppressWarnings("unchecked")
	public void rmEdge(int i, int j, boolean bothWays)
	{
		//bothways automatically true for a non-oriented graph
		
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
		if(bothWays)
			rmEdge(j,i,false);
	}
	
	/**
	 * Check if a vertex has some neighbor
	 * @param i number of the vertex
	 * @return true if i has any neighbor (considered in an oriented way)
	 */
	public boolean hasNeighbour(int i)
	{
		if(this.edges != null && this.edges.containsKey(i))
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
	public void calculPred()
	{
		try
		{
			this.predecessors = new HashMap<>();
			for (Integer i : verPos.keySet())			
				predecessors.put(i, Dijkstra(i));
		}
		catch(ModelException e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
			System.exit(e.hashCode());
		}
	}
	
	/**
	 * @param i the number of the vertex
	 * @return All the predecessors for having the shortest way from i to another vertex
	 * @see calculPred
	 * @throws ModelException
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
	
	public ArrayDeque<Character> goTo(int from, Point<Integer> p) throws ModelException
	{
		if(!verPos.containsValue(p))
			throw new ModelException("Those vertices don't exist");
		
		for(Integer k : verPos.keySet())
		{
			if(verPos.get(k).equals(p))
				return goTo(from,k);
		}
		return null;
	}
	
	/**
	 * Transform the matrix-maze in a graph
	 * @param Mat The input matrix-maze
	 * @param codeMat The code use to translate the matrix
	 * @return the new Graph
	 * @throws ModelException
	 */
	public static Graph matToGraph(int[][] Mat, Map<String,Integer> codeMat, Map<Integer, Boolean> aWay) throws ModelException
	{
		
		
		// For later : Add a vertex at the ghost regeneration point.
		// And on teleportations.
		Graph g = new Graph();
		ArrayList<Integer> tpPoints = new ArrayList<>();

		int nrows = Mat.length;
		int ncols = Mat[0].length;
		int[][] matC = null;
		try
		{
			matC = Maze.simplifyMat(Mat, true, true); // Matrix without teleportation informations
		}
		catch(ModelException e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
			System.exit(e.hashCode());
		}
		int k = 1;
		
		
		
		for(int i = 0; i < nrows; i++)
		{
			// Used for create edges.
			boolean sameLineCo = false; // Sameline and connected (no walls between)
			int previousJ = 0;
			boolean test = false;
			
			for(int j = 0; j < ncols; j++)
			{
				if(aWay.get(matC[i][j]) == null)
					throw new ModelException("matToGraph : Unknown value at (" + i + "," + j + ") position");
				
				if(aWay.get(matC[i][j]))
				{
					
					// If it exists the equivalent of a 'corner', then it's a vertex
					if(((j > 0 && aWay.get(matC[i][j-1])) || (j + 1 < ncols && aWay.get(matC[i][j+1])))
							&& ((i > 0 && aWay.get(matC[i-1][j])) || (i + 1 < nrows && aWay.get(matC[i+1][j]))))
							test = true;
					
					
					// Or if there is an "IMPASSE" (if the bloc is surrounded by exactly 3 blocs
					if(!test)
					{
						int sides = 0;
						if((j > 0 && !aWay.get(matC[i][j-1])))
							sides++;
						
						if(j + 1 < ncols && !aWay.get(matC[i][j+1]))
							sides++;
						
						if(i > 0 && !aWay.get(matC[i-1][j]))
							sides++;
						
						if(i + 1 < nrows && !aWay.get(matC[i+1][j]))
							sides++;
						
						if(sides == 3)
							test = true;
					}
					
					
					//Or if it's a teleportation point
					if(!test && codeMat.get("teleport_prefix") == matC[i][j])
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
							try
							{
								g.addEdge(k-1, k, new Edge((float) j - previousJ, 'R'));
							}
							catch(ModelException e)
							{
								System.out.println(e.getMessage());
								e.printStackTrace();
								System.exit(e.hashCode());
							}
							
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
						if(!aWay.get(matC[i][col]))
							noWalls = false;
						i++;
					}
					if(noWalls)
					{
						try
						{
							g.addEdge(p, k, new Edge((float) g.verPos.get(k).x - g.verPos.get(p).x, 'D'));
							break;
						}
						catch(ModelException e)
						{
							System.out.println(e.getMessage());
							e.printStackTrace();
							System.exit(e.hashCode());
						}
						
					}
				}
			}
		}
		
		
		//Teleportation points management (edges between them)
		//TODO
		while(tpPoints.size() >= 2)
		{
			int i = tpPoints.get(0);
			int xI = g.verPos.get(i).x;
			int yI = g.verPos.get(i).y;
			tpPoints.remove(0);
			int j;

			
			int p = 0;
			while(p < tpPoints.size() && Mat[xI][yI] != Mat[g.verPos.get(tpPoints.get(p)).x][g.verPos.get(tpPoints.get(p)).y])
				p++;
			
			// If we found a peer for i
			if(p != tpPoints.size())
			{
				j = tpPoints.get(p);
				tpPoints.remove(p);
				
				//Adding the i-j edge				
				g.addEdge(i, j, new Edge(1, g.teleportDirection(i, matC, aWay)), -2);
				
				//Adding the j-i edge
				g.addEdge(j,  i, new Edge(1, g.teleportDirection(j, matC, aWay)), -2);
			}
			
		}
		
		g.calculPred();
		return g;
	}
	
	/**
	 * Return the direction for the edge created from a teleporation bloc
	 * Actually only handle the blocs at the edge of the maze.
	 * @param mat the matrix. !!! This matrix must be teleportation-informationless
	 * @return
	 */
	private char teleportDirection(int k, int[][] matC, Map<Integer, Boolean> aWay)
	{
		int x = this.verPos.get(k).x, y = this.verPos.get(k).y; 
		int nrows = matC.length;
		
		if(x <= 0)
			return 'U';
		else if (x >= nrows - 1)
			return 'D';
		else if(y <= 0)
			return 'L';
		else
			return 'R';
	
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