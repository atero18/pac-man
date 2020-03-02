package src.Model;

import java.util.ArrayDeque;
import java.util.Deque;

public abstract class AbstBeing implements Being {

	Point<Float> pos;
	Deque<Character> dir;
	boolean alive;
	static final float velocity = 0.5f;
	
	public AbstBeing() {
		dir = new ArrayDeque<>();
		alive = false;
	}

	@Override
	public boolean isAlive() {
		return this.alive;
	}

	@Override
	public abstract void manageMove(Graph g);
	/**
	 * @return the actual direction, '0' if there isn't.
	 */
	
	public void move(Graph g)
	{
		if(onVertex(g))
			dir.removeFirst();
		
		switch(dir.getFirst())
		{
		case 'U':
			this.pos.setY(this.pos.y - velocity);
		break;
		case 'D':
			this.pos.setY(this.pos.y + velocity);
		break;
		case 'L':
			this.pos.setX(this.pos.x - velocity);
		break;
		case 'R':
			this.pos.setX(this.pos.x + velocity);
		break;
		}
			
	}
	
	public char getActMove()
	{
		if(this.dir.size() != 0)
			return this.dir.getFirst();
		
		else
			return '0';
	}

	@Override
	public Point<Float> getPos()
	{
		return this.pos;
	}
	
	/**
	 * If the being is on a vertex
	 * @param g
	 * @return true if the being is on the vertex, and true by default if the being as no direction.
	 */
	public boolean onVertex(Graph g)
	{
		if(this.dir.size() == 0)
			return true;
		
		if(pos.x == Math.round(pos.x) && pos.y == Math.round(pos.y))
		{
			int k = g.closestVertex(this.pos, dir.getFirst());
			if(g.verPos.get(k).x == (float) pos.x && g.verPos.get(k).y == (float) pos.y)
				return true;
		}
		
		return false;
	}

}
