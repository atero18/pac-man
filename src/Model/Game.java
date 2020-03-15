package src.Model;

import java.util.ArrayList;
import java.util.List;

public class Game
{

	
	List<Maze> mazes;
	Pacman pm;
	int actualMaze;
	
	public Game()
	{
		this.mazes = new ArrayList<>();
		this.actualMaze = 0;
		
		try
		{
			Point<Integer> p = mazes.get(actualMaze).getPMStart();
			Point<Float> pbis = new Point<>((float) p.x, (float) p.y);
			pm = new Pacman(pbis);
		}
		catch(ModelException e)
		{
			e.fatalError();
		}
	}
	
	public Game(String file)
	{
		this();
		ArrayList<Maze> mazes = new ArrayList<>();
		try
		{
			mazes.add(new Maze(file));
		}
		catch(ModelException e)
		{
			e.fatalError();
		}
	}

	public void loop()
	{
		Maze m = mazes.get(actualMaze);
		pm.manageMove(m);
		Ghost[] ghosts = m.ghosts;
		for(int i = 0; i < ghosts.length; i++)
			ghosts[i].manageMove(m);
		
		// TODO collision between pm and ghosts
		// TODO check dots disappearing
		
		
	}
	
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

	}
}
