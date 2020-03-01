package src.Model;

import java.util.ArrayDeque;
import java.util.Deque;

public abstract class Ghost implements Being {
	
	String name;
	String color;
	Point<Float> pos;
	Deque<Character> dir;
	boolean alive;
	
	public Ghost(String name, String color)
	{
		dir = new ArrayDeque<>();
		alive = false;
		this.name = name;
		this.color = color;
	}
	
	@Override
	public boolean isAlive() {
		return this.alive;
	}
	
	/**
	 * @param g the Graph considered
	 */
	//TODO
	public void randomMove(Graph g)
	{
		int vertex = g.closestVertex(this.pos, this.dir.getLast());
		try
		{
			char[] directions = g.getDir(vertex);
			
		}
		catch(ModelException e)
		{
			System.out.println(e.getMessage());
			e.printStackTrace();
			System.exit(e.hashCode());
		}
	}

	@Override
	public abstract void manageMove();

	public char getActMove()
	{
		if(this.dir.size() != 0)
			return this.dir.getFirst();
		
		else
			return '0';
	}
	

	@Override
	public Point<Float> getPos() {
		return this.pos;
	}

}
