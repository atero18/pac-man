package bin.Model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * 
 * @author Aleam
 * @author Atero
 * @since 0.20.2
 * @version 0.0.1
 *
 */
public class Maze {
	
	private int[][] matrix;
	private Graph graph;
	private static Map<String, Integer> readParam = null;
	
	//Simple constructor using fields
	public Maze(int[][] matrix) throws ModelException
	{
		this.matrix = matrix;
		if(readParam == null)
			getParams();
		graph = Graph.matToGraph(this.matrix, readParam);
	}
	
	public Maze(String file) throws ModelException
	{
		this(fileToMat(file));
		
	}
	
	/**
	 * 
	 * @param url relative (or absolute) link to the maze_file
	 * @return the maze's Integer Matrix
	 * @throws ModelException
	 */
	public static int[][] fileToMat(String url) throws ModelException
	{
		File file = new File(url);
		try
		{
			Scanner reader = new Scanner(file);
			//First reading, knowing the dims.
			int rows = 0;
			int cols = 0;
			String data;
			while(reader.hasNextLine())
			{
				rows++;
				data = reader.nextLine();
				cols = data.length();
			}
			reader.close();
			
			
			int[][] Mat = new int[rows][cols];
			reader = new Scanner(file);
			int i = 0;
			while(reader.hasNextLine() && i < rows)
			{
				data = reader.nextLine();
				for(int j = 0; j < data.length(); j++)
					Mat[i][j] = Character.getNumericValue(data.charAt(j));
				i++;
			}
			reader.close();
			return Mat;
		}
		catch(FileNotFoundException e)
		{
			throw new ModelException("file not found -- loading Maze");
		}
		catch(Exception e)
		{
			throw new ModelException("Error during opening maze file |" + e.getMessage());
		}
	}
	
	/**
	 * Get the reading parameters from the read_maze.txt file (use regexp)
	 * @see regex
	 * @throws ModelException
	 */
	public static void getParams() throws ModelException
	{
		File file = new File("settings/read_maze.txt");
		readParam = new HashMap<String,Integer>();
		
		try
		{
			Scanner reader = new Scanner(file);
		    Pattern regex = Pattern.compile("(\\w+_?\\w*)=(\\d+)");
		    Matcher m;
		    System.out.println("Reading maze parameters");
		    while(reader.hasNextLine())
		    {
		    	m = regex.matcher(reader.nextLine());
		    	if(m.matches())
		    		readParam.put(m.group(1), Integer.parseInt(m.group(2)));
		    }
		    System.out.println("maze parameters ok");
			reader.close();
		}
		catch(FileNotFoundException e)
		{
			throw new ModelException("file not found -- loading Maze reading parameters");
		}
		catch(Exception e)
		{
			throw new ModelException("Error during opening maze settings file |" + e.getMessage());
		}
	}
	
	public void printMat()
	{
		System.out.println("\nDimensions of the matrix : " + matrix.length + ", " + matrix[0].length);
		for(int i = 0; i < matrix.length; i++)
		{
			System.out.print(i  + " : ");
			for(int j = 0; j < matrix[i].length - 1; j++)
				System.out.print(matrix[i][j] + ", ");
			System.out.print(matrix[i][matrix[i].length - 1] + "\n");
		}
	}
	
	public static void main (String[] args)
	{
		try
		{
			Maze maze = new Maze("data/maze/maze_0.txt");
			maze.printMat();
			maze.graph.disp();
		}
		catch(Exception e)
		{
			System.out.println("Error, " + e.getMessage());
		}
	}
	
}
