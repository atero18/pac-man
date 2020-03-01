package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.Model.Maze;
import src.Model.ModelException;

class MazeTest {

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testFileToMat(){
		// Try load a maze which doesn't exist
		try
		{
			assertNull(Maze.fileToMat("bla.txt"));
		}
		catch(ModelException e)
		{
			System.out.println("Error while using fileToMat with inexistant file (unexpected)");
		}
		
		
		// Loading a maze from a file and check if it matches
		int[][] mat = { {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
						{1,5,0,0,1,0,0,0,0,0,0,0,0,1,0,0,5,1},
						{1,0,1,0,0,0,1,1,0,0,1,1,0,0,0,1,0,1},
						{1,0,1,1,0,1,0,0,0,0,0,0,1,0,1,1,0,1},
						{1,0,0,1,0,0,0,3,4,4,3,0,0,0,1,0,0,1},
						{1,1,0,1,1,0,3,3,3,3,3,3,0,1,1,0,1,1},
						{990,2,0,0,0,0,3,3,3,3,3,3,0,0,0,0,0,990},
						{1,1,0,1,1,0,0,0,0,0,0,0,0,1,1,0,1,1},
						{1,1,0,1,1,1,1,1,0,0,1,1,1,1,1,0,1,1},
						{1,0,0,0,1,1,0,0,0,0,0,0,1,1,0,0,0,1},
						{1,0,1,0,0,0,0,1,1,1,1,0,0,0,0,1,0,1},
						{1,5,0,0,1,1,0,0,0,0,0,0,1,1,0,0,5,1},
						{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}};
		
		try
		{
			@SuppressWarnings("unused")
			Maze m = new Maze("data/maze/maze_1.txt"); // Only here to test the constructor
			
			int[][] matFile = Maze.fileToMat("data/maze/maze_1.txt");
			if(mat.length != matFile.length || mat[0].length != matFile[0].length)
				fail("dimensions aren't the same");
			
			
			for(int i = 0; i < mat.length; i++)
			{
				for(int j = 0; j < mat[i].length; j++)
				{
					if(mat[i][j] != matFile[i][j])
						fail("values at (" + i +"," + j + ") aren't the same");
				}
			}
		}
		catch(ModelException e)
		{
			fail("Error while using fileToMat. Unexpected");
		}
		
		// Check if a wrong maze return Exception		
		assertThrows(ModelException.class, () -> {Maze.fileToMat("test/wrongmat.txt");});
		
	}

	@Test
	void testSimplifyMat() {
		assertThrows(ModelException.class, () -> {Maze.simplifyMat(null, true, true);});
		int[][] mat = { {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
						{1,5,0,0,1,0,0,0,0,0,0,0,0,1,0,0,5,1},
						{1,0,1,0,0,0,1,1,0,0,1,1,0,0,0,1,0,1},
						{1,0,1,1,0,1,0,0,0,0,0,0,1,0,1,1,0,1},
						{1,0,0,1,0,0,0,3,4,4,3,0,0,0,1,0,0,1},
						{1,1,0,1,1,0,3,3,3,3,3,3,0,1,1,0,1,1},
						{990,2,0,0,0,0,3,3,3,3,3,3,0,0,0,0,0,990},
						{1,1,0,1,1,0,0,0,0,0,0,0,0,1,1,0,1,1},
						{1,1,0,1,1,1,1,1,0,0,1,1,1,1,1,0,1,1},
						{1,0,0,0,1,1,0,0,0,0,0,0,1,1,0,0,0,1},
						{1,0,1,0,0,0,0,1,1,1,1,0,0,0,0,1,0,1},
						{1,5,0,0,1,1,0,0,0,0,0,0,1,1,0,0,5,1},
						{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}};
		try
		{
			int[][] matC = Maze.simplifyMat(mat, true, true);
			assertEquals(matC[6][0], matC[6][17]);
			assertEquals(matC[6][0], Maze.getReadParam().get("teleport_prefix"));
		}
		catch(ModelException e)
		{
			fail("Error while simplifying matrix");
		}
	}

}
