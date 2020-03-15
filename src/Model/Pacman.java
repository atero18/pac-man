package src.Model;

import java.util.Map;

/**
 * @author Atero
 * @version 0.0.1
 * @since 0.20.2
 */
public class Pacman extends Being
{
	boolean isSuper;
	long timeSuperMS;
	// The duration of the "super" state (in ms)
	static long superDuration;
	
	public Pacman(Point<Float> pos)
	{
		super(pos);
		isSuper = false;
	}
	
	public boolean isSuper()
	{
		return isSuper;
	}

	@Override
	public void manageMove(Maze m)
	{
		Graph g = m.graph;
		if(alive)
			move();
		
		int k = onVertex(g);
		
		

		//TODO super dot manage
		if(k != -1 && alive)
		{
			// If pm is on a bloc with a dot, it's erased
			if(m.matrix[Math.round(pos.x)][Math.round(pos.y)] == Maze.readParam.get("empty_bloc"))
				m.dots.remove(new Point<Integer>(Math.round(pos.x),Math.round(pos.y)));
			
			try
			{
				char[] availDir = g.getDir(k);
				boolean change = false;
				
				// If pacman has two directions recorded
				if(dir.size() == 2)
				{
					int i = 0;
					while(i < availDir.length && availDir[i] != dir.getLast())
						i++;
					
					if(i != availDir.length)
					{
						dir.removeFirst();
						change = true;
					}
					else
						dir.removeLast();
					
				}
				
				// If pacman has only one direction recorded
				if(!change && dir.size() == 1)
				{
					int i = 0;
					while(i < availDir.length && availDir[i] != dir.getFirst())
						i++;
					
					if(i != availDir.length)
						change = true;
					
					else
						dir.removeFirst();
				}
			}
			catch(ModelException e)
			{
				System.out.println("Error while managing Pacman move : the vertex " + k + "doesn't exist.");
				e.printStackTrace();
				System.exit(e.hashCode());
			}
		}
		this.manageSuper();
	}
	
	public void startSuper()
	{
		this.isSuper = true;
		this.timeSuperMS = System.currentTimeMillis();
	}
	
	public boolean manageSuper()
	{
		if (!this.isSuper)
			return false;
		
		if(System.currentTimeMillis() - this.timeSuperMS >= superDuration)
			this.isSuper = false;
		
		return this.isSuper;
	}
	
	public boolean addMove(Graph g, char newDir) throws ModelException
	{
		if(dir == null)
			return false;
		
		else if(dir.size() == 1)
		{
			int k = onVertex(g);
			if(k == -1 && newDir == Edge.opposites.get(dir.getFirst()))
			{
				dir.removeFirst();
				dir.addFirst(newDir);
			}
				
			else
				dir.addLast(newDir);
			
			return true;
		}
		
		else if(dir.size() == 0)
		{
			if(onVertex(g) != -1)
				return true;
			
			else
			{
				int k = onVertex(g);
				if(k == -1)
					throw new ModelException("Error while trying move Pac-Man : not on a vertex");
				
				Map<Integer,Edge> edges = g.edges.get(k);
				for(Integer i : edges.keySet())
				{
					if(edges.get(i).dir == newDir)
					{
						dir.addFirst(newDir);
						return true;
					}
				}
				
			}
		}
		return false;
	}
}