package src.Model;


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
		if(dir.size() != 0)
			move();
		
		int k = onVertex(g);
		if(k != -1)
		{
			try
			{
				char[] availDir = g.getDir(k);
				boolean change = false;
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
}
