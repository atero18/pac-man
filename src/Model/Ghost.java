package src.Model;

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
	boolean inZone;
	static long timeMinInZone;
	
	
	public Ghost(String name, String color)
	{
		super();
		this.name = name;
		this.color = color;
		this.inZone = true;
		
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
	

	public void move(Graph g)
	{
		if(onVertex(g) != -1)
			dir.removeFirst();
		
		super.move();
	}
	
	@Override
	public abstract void manageMove(Graph g);

}
