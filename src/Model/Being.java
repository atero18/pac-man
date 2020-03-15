package src.Model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Atero
 * @version 0.0.1
 * @since 0.20.2
 */

public abstract class Being
{
	
	Point<Float> pos;
	Deque<Character> dir;
	boolean alive;
	static float velocity;
	static boolean paramCharged = false;
	static float deltaPos;
	
	public Being()
	{
		dir = new ArrayDeque<>();
		alive = false;
		Being.loadParams();
	}
	
	public Being(Point<Float> pos)
	{
		this();
		this.pos = pos;
	}

	public boolean isAlive() {
		return this.alive;
	}
	
	public static void loadParams()
	{
		if(paramCharged)
			return;
		
		File file = new File("settings/beings.txt");
		Pattern regex = Pattern.compile("([\\w_]*):(\\d+)");
	    Matcher m;
		try
		{
			Scanner reader = new Scanner(file);
			while(reader.hasNextLine())
			{
				m = regex.matcher(reader.nextLine());
				if(m.matches())
				{
					switch(m.group(1))
					{
					case "velocity":
						float v = Float.parseFloat(m.group(2));
						if(v >= 0 && ((v < 1 && 1 % v == 0) || (v >= 1 && v % 1 == 0)))
							Being.velocity = v;
						else
						{
							System.out.println("Error - velocity value unauthorized. Set to 1");
							Being.velocity = 1;
						}
					break;
					case "deltaPos":
						Being.deltaPos = Float.parseFloat(m.group(2));
					break;
					case "G_timeMinInZoneMS":
						Ghost.timeMinInZone = Long.parseLong(m.group(2));
					break;
					case "P_superDurationMS":
						Pacman.superDuration = Long.parseLong(m.group(2));
					break;
					default:
						System.out.println("Beings Params - unknown parameter : " + m.group(2));
					break;
					}
				}
			}
			reader.close();
		}
		catch(FileNotFoundException e)
		{
			System.out.println("file not found -- loading Beings parameters | " + e.getMessage());
			e.printStackTrace();
			System.exit(e.hashCode());
		}
		catch(Exception e)
		{
			System.out.println("Error during opening Beings settings file |" + e.getMessage());
			e.printStackTrace();
			System.exit(e.hashCode());
		}
		
		paramCharged = true;
	}

	public abstract void manageMove(Maze m);
	/**
	 * @return the actual direction, '0' if there isn't.
	 */
	
	public void move()
	{
		if(dir == null || dir.size() == 0)
				return;
		
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

	public Point<Float> getPos()
	{
		return this.pos;
	}
	
	/**
	 * If the being is on a vertex
	 * @param g
	 * @return the vertex ID if the being is on the vertex, -1 otherwise.
	 */
	public int onVertex(Graph g)
	{
		if(this.dir.size() == 0)
			return -1;
		
		if(pos.x == Math.round(pos.x) && pos.y == Math.round(pos.y))
		{
			int k = g.closestVertex(this.pos, dir.getFirst());
			if(g.verPos.get(k).x == (float) pos.x && g.verPos.get(k).y == (float) pos.y)
				return k;
		}
		
		return -1;
	}

	
	public boolean samePos(Being b)
	{
		// Manhattan distance (L1-norm) variation		
		return Math.abs(this.pos.x - b.pos.x) < deltaPos && Math.abs(this.pos.y - b.pos.y) < deltaPos;
	}
}
