package src.Model;

import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

/**
 * @author Atero
 * @version 0.0.1
 * @since 0.20.2
 */
public abstract class Ghost extends Being {
	
	String name;
	String color;
	
	String state; // inZone, goingOut, out, goingIn, goToZone
	static boolean anyGoingOut = false; // If a ghost is going outside
	
	
	static long timeBeforeLastExit = 10000; // Time before the last ghost exit 
	static long timeMinInZone;
	static long timeOddOutMax = 3000;
	long dateSinceInZone;
	
	public Ghost(String name, String color)
	{
		super();
		this.name = name;
		this.color = color;
		this.state = "inZone";
		//TODO
		dateSinceInZone = System.currentTimeMillis(); // Check later the interest of this line
		
	}
	
	/**
	 * @param g the Graph considered
	 * @return an int[] with [0] first vertex and [1] the second
	 */
	public int[] randomDir(Graph g)
	{	
		return this.randomDir(g, g.closestVertex(this.pos, this.dir.getLast()));
	}
	
	public int[] randomDir(Graph g, int vertex)
	{
			int[] tab = new int[2];
			tab[0] = vertex;
			
			Random ran = new Random();
			Set<Integer> list = g.edges.get(vertex).keySet();
			Integer[] direction = new Integer[list.size()];
			
			tab[1] = direction[ran.nextInt(direction.length)];
			return tab;
	}
	
	public void randomMove(Graph g, int nbDir)
	{
		int[] tab = new int[2];
		tab = this.randomDir(g);
		
		this.dir.addLast(g.edges.get(tab[0]).get(tab[1]).dir);
		randomMove(g, nbDir - 1, tab[1]);
		
	}
	/**
	 * add new directions to the queue
	 * @param g the graph considered
	 * @param nbDir the number of directions you want to add
	 * @param verStart the vertex from where you start
	 */
	public void randomMove(Graph g, int nbDir, int verStart)
	{
		if(nbDir <= 0)
			return;
		
		int[] tab = new int[2];
		tab[1] = verStart;
		
		while(nbDir > 0)
		{
			tab = this.randomDir(g, tab[1]);
			this.dir.addLast(g.edges.get(tab[0]).get(tab[1]).dir);
			nbDir--;
		}
	}
	
	public boolean goingOut(Maze m)
	{
		if(state != "inZone" || anyGoingOut)
			return false;
		
		if(System.currentTimeMillis() - dateSinceInZone >= timeMinInZone)
		{
			// The ghost has an odd to go out, which increase with time
			if(Math.random() < (System.currentTimeMillis() - (dateSinceInZone + timeMinInZone)) / timeOddOutMax)
			{
				anyGoingOut = true;
				state = "goingOut";
				
				//TODO Let the ghost going outside
				Iterator<Point<Integer>> it = m.gZoneDoorsPos.keySet().iterator();
				boolean cont = false;
				while(it.hasNext() && !cont)
				{
					Point<Integer> p = it.next();
					int x = p.x;
					int y = p.y;
					while(!cont && x > 0 && x < m.rows-1 && y > 0 && y < m.columns-1 && 
							(m.matrix[x][y] == Maze.readParam.get("ghost_zone") || m.matrix[x][y] == Maze.readParam.get("ghost_out")))
					{
						switch(m.gZoneDoorsPos.get(p))
						{
						case 'U':
							y++;
						break;
						case 'D':
							y--;
						break;
						case 'L':
							x++;
						break;
						case 'R':
							x--;
						}
						if(pos.x == x && pos.y == y)
						{
							cont = true;
							dir.addFirst(m.gZoneDoorsPos.get(p));
						}
					}
				}
				
				return true;
				
			}
		}
		return false;
	}	

	public void move(Graph g)
	{
		if(onVertex(g) != -1)
			dir.removeFirst();
		
			super.move();
	}
	
	@Override
	public void manageMove(Maze m)
	{
		Graph g = m.graph;
		move();
		goingOut(m);
		
		if(alive)
		{
			if(state == "goingOut" &&  onVertex(g) != -1)
			{
					state = "out";
					anyGoingOut = false;
			}
		}
		
		// If the ghost is dead only since the last update
		else if(state == "out")
		{
			
			state = "goToZone";
			int k = g.closestVertex(pos, dir.getFirst());

			while(dir.size() != 1)
				dir.removeFirst();
			
			if(onVertex(g) != -1)
				dir.removeFirst();

			int min = g.verPos.keySet().size();
			ArrayDeque<Character> queueMin = new ArrayDeque<>();
			try
			{
				for(Point<Integer> p : m.gZoneDoorsPos.keySet())
				{
					int x = p.x;
					int y = p.y;
					switch(m.gZoneDoorsPos.get(p))
					{
					case 'U':
						y--;
					break;
					case 'D':
						y++;
					break;
					case 'L':
						x--;
					break;
					case 'R':
						x++;
					break;
					}
					ArrayDeque<Character> queue = g.goTo(k, new Point<Integer>(x,y));
					if(queue.size() <= min)
						queueMin = queue;
				}
				while(queueMin.size() != 0)
					dir.add(queueMin.pollFirst());
			}
			catch(ModelException e)
			{
				System.out.println(e.getMessage());
				e.printStackTrace();
				System.exit(e.hashCode());
			}
		}
		
		// If the ghost is dead and close to the zone
		else if(state == "goToZone" && dir.size() == 0)
		{
			state = "goingIn";

			Iterator<Point<Integer>> it = m.gZoneDoorsPos.keySet().iterator();
			boolean cont = false;
			while(it.hasNext() && !cont)
			{
				Point<Integer> p = it.next();
				int x = p.x;
				int y = p.y;
				switch(m.gZoneDoorsPos.get(p))
				{
				case 'U':
					y--;
				break;
				case 'D':
					y++;
				break;
				case 'L':
					x--;
				break;
				case 'R':
					x++;
				break;
				}
				if(pos.x == x && pos.y == y)
				{
					dir.addFirst(Edge.opposites.get(m.gZoneDoorsPos.get(p)));
					cont = true;
				}
			}
		}
		
		// If the ghost is going inside the zone (and has just arrived)
		else if(state == "goingIn")
		{
			Iterator<Point<Integer>> it = m.gZoneDoorsPos.keySet().iterator();
			boolean cont = false;
			while(it.hasNext() && !cont)
			{
				Point<Integer> p = it.next();
				int x = p.x;
				int y = p.y;
				switch(m.gZoneDoorsPos.get(p))
				{
				case 'U':
					y++;
				break;
				case 'D':
					y--;
				break;
				case 'L':
					x++;
				break;
				case 'R':
					x--;
				break;
				}
				if(pos.x == Math.round(pos.x) && pos.y == Math.round(pos.y) && pos.x == x && pos.y == y)
				{
					dir.removeFirst();
					cont = true;
					state = "inZone";
					dateSinceInZone = System.currentTimeMillis();
				}
			}
		}
		else if(state == "goingOut")
		{
			alive = true;
			state = "out";
		}
	}
}
