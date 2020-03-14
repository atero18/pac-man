package src.Model;

public class Pokey extends Ghost {

	public Pokey(String name, String color)
	{
		super(name, color);
	}

	@Override
	public void manageMove(Maze m)
	{
		super.manageMove(m);
		Graph g = m.graph;
		if(alive && dir.size() == 0)
			this.randomMove(g, 5);
	}
}
