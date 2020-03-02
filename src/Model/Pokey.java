package src.Model;

public class Pokey extends Ghost {

	public Pokey(String name, String color)
	{
		super(name, color);
	}

	@Override
	public void manageMove(Graph g)
	{
		if(alive && dir.size() == 0)
			this.randomMove(g, 5);

	}

}
