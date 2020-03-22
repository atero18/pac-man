package src.Model;

import java.util.ArrayList;
import java.util.List;

public class Game
{
	List<Maze> mazes;
	Pacman pm;
	int actualMaze;
	static int FREQ = 24;
	
	public Game()
	{
		this.mazes = new ArrayList<>();
		this.actualMaze = 0;
		
		try
		{
			if(!Being.paramCharged)
			{
				Being.loadParams();
				Being.velocity /= FREQ;
			}
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

	public boolean loop()
	{
		Maze m = mazes.get(actualMaze);
		Ghost[] ghosts = m.ghosts;
		for(int i = 0; i < ghosts.length; i++)
		{
			ghosts[i].manageMove(m);
			
			// Collisions between pacman and ghosts
			if(pm.alive && ghosts[i].alive && pm.samePos(ghosts[i]))
			{
				if(!pm.isSuper)
				{
					pm.alive = false;
					pm.nbLifes--;
				}
				else
					ghosts[i].alive = false;
			}
		}
		pm.manageMove(m);
		return pm.alive;
	}
	
	public void start_game()
	{
		long tRefresh;
		int sumDots;
		do
		{
			tRefresh = System.currentTimeMillis();
			loop();
			sumDots = 0;
			for(int i = 0; i < mazes.size(); i++)
			{
				sumDots += mazes.get(i).nbDots();
			}
			
			while(System.currentTimeMillis() - tRefresh <= 1000 / FREQ) {}
		}while(pm.nbLifes >= 0 && sumDots > 0);
		// TODO consequences at the end
	}
	
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

	}
}