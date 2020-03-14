package src.Model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
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
	
	int[][] matrix;
	int rows, columns;
	Graph graph;
	private static Map<String, Integer> readParam = null;
	private static Map<Integer, String> typeBlocs = null;
	private static Map<Integer, Boolean> isAWay = null;
	
	Set<Point<Integer>> dots = null;
	Set<Point<Integer>> superDots = null;
	
	Map<Point<Integer>, Character> gZoneDoorsPos = null;
	
	/**
	 * @return the readParam
	 */
	public static Map<String, Integer> getReadParam() {
		return readParam;
	}

	/**
	 * @return the typeBlocs
	 */
	public static Map<Integer, String> getTypeBlocs() {
		return typeBlocs;
	}

	/**
	 * @return the isAWay
	 */
	public static Map<Integer, Boolean> getIsAWay() {
		return isAWay;
	}

	public Maze(int[][] matrix) throws ModelException
	{
		this.matrix = matrix;
		this.rows = matrix.length;
		this.columns = matrix[0].length;
		this.dots = new HashSet<>();
		this.superDots = new HashSet<>();
		this.gZoneDoorsPos = new HashMap<>();
		
		if(readParam == null || typeBlocs == null || isAWay == null)
			getParams();
		
		try
		{
			graph = Graph.matToGraph(this.matrix, readParam, isAWay);
		}
		catch(ModelException e)
		{
			System.out.println("Exception while creating Maze | " + e.getMessage());
			e.printStackTrace();
			System.exit(e.hashCode());
		}
		
		for(int i = 0; i < this.rows; i++)
		{
			for(int j = 0; j < this.columns; j++)
			{
				// Super dot management 
				if(matrix[i][j] == readParam.get("super_dot"))
					this.superDots.add(new Point<>(i,j));
				
				else if(isAWay.get(matrix[i][j]))
					this.dots.add(new Point<>(i,j));
				
				
				// Ghost zone management
				if(matrix[i][j] == readParam.get("ghost_out"))
				{
					char direction = 'O';
					if(j > 0 && isAWay.get(matrix[i][j-1]))
						direction = 'U';
					else if(j < columns - 1 && isAWay.get(matrix[i][j+1]))
						direction = 'D';
					else if(i < 0 && isAWay.get(matrix[i-1][j]))
						direction = 'L';
					else if(i < rows - 1 && isAWay.get(matrix[i+1][j]))
						direction = 'R';
					
					if(direction == 'O')
						throw new ModelException("Error creating Maze : Wrong ghost zone");
						
					gZoneDoorsPos.put(new Point<Integer>(i,j), direction);
				}
			}
		}
	}
	
	public Maze(String file) throws ModelException
	{
		this(fileToMat(file));
	}
	
	/**
	 * 
	 * @param url relative (or absolute) link to the maze_file
	 * @return the maze's Integer Matrix. null if error during opening file
	 * @throws ModelException if a value in the matrix is unknown
	 */
	public static int[][] fileToMat(String url) throws ModelException
	{
		int Mat[][];
		try
		{
			File file = new File(url);
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
			
			
			Mat = new int[matRows][matCols];
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
			
		}
		catch(FileNotFoundException e)
		{
			System.out.println("file not found -- loading Maze | " + e.getMessage());
			e.printStackTrace();
			return null;
		}
		catch(Exception e)
		{
			System.out.println("Error during opening maze file |" + e.getMessage());
			e.printStackTrace();
			return null;
		}
		
		simplifyMat(Mat,true,true);
		return Mat;
		
	}
	
	/**
	 * Get the reading parameters from the read_maze.txt file (use regexp)
	 * @see regex
	 */
	public static void getParams()
	{
		if(readParam != null && typeBlocs != null && isAWay != null)
		{
			System.out.println("Maze parameters : already charged");
			return;
		}
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
			System.out.println("file not found -- loading Maze reading parameters | " + e.getMessage());
			e.printStackTrace();
			System.exit(e.hashCode());
		}
		catch(Exception e)
		{
			System.out.println("Error during opening maze settings file |" + e.getMessage());
			e.printStackTrace();
			System.exit(e.hashCode());
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
	 */
	public void dispMaze()
	{
		HashMap<String, String> viz = new HashMap<>();
		
		File file = new File("settings/disp_maze.txt");
		try
		{
			int[][] matC = simplifyMat(this.matrix, false, true);
			
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
			System.out.println("file not found -- loading disp_Maze parameters | " + e.getMessage());
			e.printStackTrace();
			System.exit(e.hashCode());
		}
		catch(Exception e)
		{
			System.out.println("Error during opening disp_maze settings file | " + e.getMessage());
			e.printStackTrace();
			System.exit(e.hashCode());
		}
	}
	
	/**
	 * Return a matrix with eventually modified informations
	 * @param startP true if you want to replace pacman-start by an empty_bloc
	 * @param teleportD true if you want to erase informations about teleportation
	 * @throws ModelException if the loaded matrix is null or if a value is unknown
	 */
	public static int[][] simplifyMat(int[][] mat, boolean startP, boolean teleportD) throws ModelException
	{
		
		if(mat == null)
			throw new ModelException("simplifyMat : matrix loaded is NULL");
		
		int[][] matClone = new int[mat.length][mat[0].length];
		
		if(readParam == null || typeBlocs == null || isAWay == null)
			getParams();			
			
		Pattern regex = Pattern.compile("^" + readParam.get("teleport_prefix") + "\\d?$");
		for(int i = 0; i < matClone.length; i++)
		{
			for(int j = 0; j < matClone[0].length; j++)
			{
				// Pac-man start point replaced by empty-bloc
				if(startP && mat[i][j] == readParam.get("pacman_start"))
					matClone[i][j] = readParam.get("empty_bloc");
				else if(teleportD && regex.matcher(Integer.toString(mat[i][j])).matches())
					matClone[i][j] = readParam.get("teleport_prefix");
				
				// If the value is unknown
				else if(!regex.matcher(Integer.toString(mat[i][j])).matches() && !typeBlocs.containsKey(mat[i][j]))
					throw new ModelException ("Unknown value in the matrix");
					
				else
					matClone[i][j] = mat[i][j];
			}
		}
		
		return matClone;
	}
	
	/**
	 * 
	 * @return the number of dots (normal + super)
	 */
	public int nbDots()
	{
		if(this.dots == null || this.superDots == null)
			return 0;
		
		return this.dots.size() + this.superDots.size();
	}
	
	public static void main (String[] args)
	{		
		String urlMaze = "data/maze/maze_";
		if(args.length == 1)
		{
			urlMaze += args[0] + ".txt";
			System.out.println("Maze loaded : " + args[0]);
		}
		else
		{
			urlMaze += "1.txt";
			System.out.println("Maze loaded : " + 1);
		}
		
		try
		{
			Maze maze = new Maze(urlMaze);
			maze.printMat();
			maze.graph.disp();
			maze.dispMaze();
			
		}
		catch(Exception e)
		{
			System.out.println("Error, " + e.getMessage());
			e.printStackTrace();
		}
	}	
}
