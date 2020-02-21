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
	int rows, columns;
	private Graph graph;
	private static Map<String, Integer> readParam = null;
	private static Map<Integer, String> typeBlocs = null;
	
	//Simple constructor using fields
	public Maze(int[][] matrix) throws ModelException
	{
		this.matrix = matrix;
		this.rows = matrix.length;
		this.columns = matrix[0].length;
		if(readParam == null || typeBlocs == null)
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
			int matRows = 0;
			int matCols = 0;
			String data;
			while(reader.hasNextLine())
			{
				matRows++;
				data = reader.nextLine();
				matCols = data.length();
			}
			reader.close();
			
			
			int[][] Mat = new int[matRows][matCols];
			reader = new Scanner(file);
			int i = 0;
			while(reader.hasNextLine() && i < matRows)
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
		readParam = new HashMap<>();
		typeBlocs = new HashMap<>();
		
		try
		{
			Scanner reader = new Scanner(file);
		    Pattern regex = Pattern.compile("([\\w_]*)=(\\d+)");
		    Matcher m;
		    System.out.println("Reading maze parameters");
		    while(reader.hasNextLine())
		    {
		    	m = regex.matcher(reader.nextLine());
		    	if(m.matches())
		    	{
		    		readParam.put(m.group(1), Integer.parseInt(m.group(2)));
		    		typeBlocs.put(Integer.parseInt(m.group(2)), m.group(1));
		    	}
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
	
	/**
	 * Print on console the matrix
	 */
	public void printMat()
	{
		System.out.println("\nDimensions of the matrix : " + matrix.length + ", " + matrix[0].length);
		for(int i = 0; i < rows; i++)
		{
			System.out.print(i  + " : ");
			for(int j = 0; j < columns - 1; j++)
				System.out.print(matrix[i][j] + ", ");
			System.out.print(matrix[i][columns - 1] + "\n");
		}
	}
	
	/**
	 * Print on console the maze
	 * @see regex
	 * @throws ModelException
	 */
	public void dispMaze() throws ModelException
	{
		HashMap<String, String> viz = new HashMap<>();
		
		File file = new File("settings/disp_maze.txt");
		try
		{
			Scanner reader = new Scanner(file);
		    Pattern regex = Pattern.compile("([\\w_]*)=([\\w\\s_])$");
		    Matcher m;
		    System.out.println("\nReading disp_maze parameters");
		    
		    while(reader.hasNextLine())
		    {
		    	m = regex.matcher(reader.nextLine());
		    	if(m.matches())
		    		viz.put(m.group(1), m.group(2));
		    }
		    System.out.println("disp_maze parameters ok");
			reader.close();
			
			for(int i = 0; i < rows; i++)
			{	
				System.out.print("\n");
				for(int j = 0; j < columns; j++)
					System.out.print(viz.get(typeBlocs.get(matrix[i][j])));
			}
		}
		catch(FileNotFoundException e)
		{
			throw new ModelException("file not found -- loading disp_Maze parameters");
		}
		catch(Exception e)
		{
			throw new ModelException("Error during opening disp_maze settings file |" + e.getMessage());
		}
	}
	
	public static void main (String[] args)
	{
		try
		{
			Maze maze = new Maze("data/maze/maze_1.txt");
			maze.printMat();
			maze.graph.disp();
			maze.dispMaze();
		}
		catch(Exception e)
		{
			System.out.println("Error, " + e.getMessage());
		}
	}
	
}
