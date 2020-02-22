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
	private static Map<Integer, Boolean> isAWay = null;
	
	//Simple constructor using fields
	public Maze(int[][] matrix) throws ModelException
	{
		this.matrix = matrix;
		this.rows = matrix.length;
		this.columns = matrix[0].length;
		
		if(readParam == null || typeBlocs == null || isAWay == null)
			getParams();
		graph = Graph.matToGraph(this.matrix, readParam, isAWay);
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
			String[] data;
			while(reader.hasNextLine())
			{
				matRows++;
				matCols = reader.nextLine().split(",").length;
			}
			reader.close();
			
			
			int[][] Mat = new int[matRows][matCols];
			reader = new Scanner(file);
			int i = 0;
			while(reader.hasNextLine() && i < matRows)
			{
				data = reader.nextLine().split(",");
				for(int j = 0; j < data.length; j++)
					Mat[i][j] = Integer.parseInt(data[j]);
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
		isAWay = new HashMap<>();
		
		try
		{
			Scanner reader = new Scanner(file);
		    Pattern regex = Pattern.compile("([\\w_]*)=(\\d+),([yn])");
		    Matcher m;
		    System.out.println("Reading maze parameters");
		    while(reader.hasNextLine())
		    {
		    	m = regex.matcher(reader.nextLine());
		    	if(m.matches())
		    	{
		    		readParam.put(m.group(1), Integer.parseInt(m.group(2)));
		    		typeBlocs.put(Integer.parseInt(m.group(2)), m.group(1));
		    		switch(m.group(3).charAt(0))
		    		{
		    		case 'y':
		    			isAWay.put(Integer.parseInt(m.group(2)), true);
		    		break;
		    		default:
		    			isAWay.put(Integer.parseInt(m.group(2)), false);
		    		break;
		    		}
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
			int[][] matC = cloneMat(this.matrix, true);
			
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
				{
					if (viz.get(typeBlocs.get(matC[i][j])) != null)
						System.out.print(viz.get(typeBlocs.get(matC[i][j])));
					else
						System.out.print("@");
				}
					
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
	
	public static int[][] cloneMat(int[][] mat, boolean withoutTel) throws ModelException
	{
		int[][] matClone = new int[mat.length][mat[0].length];
		
		if(readParam == null || typeBlocs == null || isAWay == null)
			getParams();			
			
		Pattern regex = Pattern.compile("^" + readParam.get("teleport_prefix") + "\\d?$");
		Matcher m;
		for(int i = 0; i < matClone.length; i++)
		{
			for(int j = 0; j < matClone[0].length; j++)
			{
				if(withoutTel)
				{
					m = regex.matcher(Integer.toString(mat[i][j]));
					if(m.matches())
						matClone[i][j] = readParam.get("teleport_prefix");
					else
						matClone[i][j] = mat[i][j];
				}
				else
					matClone[i][j] = mat[i][j];
					
			}
		}
		
		
		return matClone;
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
