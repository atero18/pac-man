package test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.Model.Point;
import src.Model.Graph;
import src.Model.Maze;
import src.Model.Edge;
import src.Model.ModelException;

/**
 * @author Atero
 * @author Aleam
 * @version 0.0.1
 * @since 0.20.2
 */
class GraphTest {

	Graph g;
	static Edge e;
	Map<Integer, HashMap<Integer,Edge>> edges;
	
	@BeforeAll
	static void set() throws ModelException
	{
		e = new Edge(2f, 'R');
	}
	
	@BeforeEach
	void setUp() throws Exception {
		g = new Graph(true);
	}
	
	
	@Test
	void testAddVer() {
		assertTrue(g.addVer(1, 2, 3));
		assertFalse(g.addVer(2, 2, 3));
		assertFalse(g.addVer(1,5,6));
	}
	
	@Test
	void testNbVer() {
		assertEquals(0,g.nbVer());
		g.addVer(1, 3, 4);
		g.addVer(2, -5, 12);
		assertEquals(2,g.nbVer());
	}

	@Test
	void testExistsVerInt() {
		assertFalse(g.existsVer(1));
		g.addVer(1,2,3);
		assertTrue(g.existsVer(1));
	}

	@Test
	void testExistsVerIntInt() {
		g.addVer(1, 2, 3);
		assertFalse(g.existsVer(1,3));
		g.addVer(3, 1, 6);
		assertTrue(g.existsVer(1,3));
	}

	@Test
	void testGetPosVer() {
		assertNull(g.getPosVer(1));
		g.addVer(1, 2, 3);
		int[] p = g.getPosVer(1);
		assertEquals(2, p[0]);
		assertEquals(3, p[1]);
	}

	@Test
	void testAddEdgeIntIntEdgeInt() throws ModelException{
		Map<Integer, HashMap<Integer,Edge>> edges;
		
		// both vertices don't exist
		assertThrows(ModelException.class, () -> {g.addEdge(1, 3, e, 0);});
		
		
		// 2-1 edge isn't created in oriented graph
		g.addVer(1, 0, 0);
		g.addVer(2, 0, 1);
		g.addEdge(1, 2, e, 0);
		edges = g.getEdges();
		assertTrue(edges.containsKey(1));
		assertFalse(edges.containsKey(2));
		
		
		g = new Graph(false);
		// 2-1 edge isn't created in an non-oriented graph
		g.addVer(1, 0, 0);
		g.addVer(2, 0, 1);
		g.addEdge(1, 2, e, -2);
		edges = g.getEdges();
		assertTrue(edges.containsKey(1));
		assertFalse(edges.containsKey(2));
		
		
		// 2-1 edge created automatically in non-oriented graph
		g.addEdge(1, 2, e, 0);
		edges = g.getEdges();
		assertTrue(edges.containsKey(1));
		assertTrue(edges.containsKey(2));
		
		// 1-2 automatically removed
		g.addEdge(2, 1, e, -1);
		assertTrue(edges.containsKey(2));
		assertFalse(edges.containsKey(1));
	}


	@Test
	void testRmEdgeIntInt() throws ModelException{
		g.addVer(1, 0, 0);
		g.addVer(2, 0, 1);
		g.addEdge(1, 2, e, 1);
		
		// Check if in oriented graph the 2-1 edge isn't removed
		g.rmEdge(1, 2);
		edges = g.getEdges();
		assertFalse(edges.containsKey(1));
		assertTrue(edges.containsKey(2));
		
		// Check if in non-oriented graph the 2-1 edge IS removed
		g = new Graph(false);
		g.addVer(1, 0, 0);
		g.addVer(2, 0, 1);
		g.addEdge(1, 2, e, 1);
		g.rmEdge(1, 2);
		edges = g.getEdges();
		assertFalse(edges.containsKey(1));
		assertFalse(edges.containsKey(2));
		
	}

	@Test
	void testRmEdgeIntIntBoolean() throws ModelException{
		// Check if an edge is correctly removed
		g.addVer(1, 0, 0);
		g.addVer(2, 0, 1);
		g.addVer(3, 1, 1);
		g.addEdge(1, 2, e, 1);
		g.rmEdge(1, 2, false);
		edges = g.getEdges();
		assertFalse(edges.containsKey(1));
		assertTrue(edges.containsKey(2));
		
		// Check if the edge is removed but not all edges (1,-)
		g.addEdge(1, 2, e, 1);
		g.addEdge(1, 3, e, 1);
		g.rmEdge(1, 2, false);
		edges = g.getEdges();
		assertTrue(edges.containsKey(1));		
	}

	@Test
	void testHasNeighbour() throws ModelException{
		// When vertex doesn't exist
		assertFalse(g.hasNeighbour(1));
		
		// When vertex exists but doesn't have any neighbour
		g.addVer(1, 0, 0);
		assertFalse(g.hasNeighbour(1));
		
		// When vertex exists and has a neighbour
		g.addVer(2, 0, 1);
		g.addEdge(1, 2, e);
		assertTrue(g.hasNeighbour(1));
		assertFalse(g.hasNeighbour(2));
	}

	@Test
	void testGoTo() throws ModelException{
		this.testMatToGraph();
		g.calculPred();
		char[] t2 = {'R'};
		assertTrue(compareCharArray(g.goTo(1, 2).toArray(), t2));
		
		char[] t3 = {'R','R','U'};
		assertTrue(compareCharArray(g.goTo(13, 11).toArray(), t3));
		
	}

	@Test
	void testMatToGraph() throws ModelException{
		int[][] mat = { {1,1,1,1,1,1,1},
						{1,0,0,0,0,0,1},
						{1,0,1,0,1,0,1},
						{1,0,1,0,1,0,1},
						{1,0,0,0,0,0,1},
						{1,0,0,1,0,0,1},
						{1,0,0,0,0,0,1},
						{1,1,1,1,1,1,1}};
		
		Maze.getParams();
		
		g = Graph.matToGraph(mat, Maze.getReadParam(), Maze.getIsAWay());
		assertEquals(g.nbVer(),16);
		assertTrue(g.getEdges().containsKey(1));
		assertTrue(g.getEdges().get(1).containsKey(2));
	
	}
	
	@Test
	void testTPPeering() throws ModelException
	{
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
		g = Graph.matToGraph(mat, Maze.getReadParam(), Maze.getIsAWay());
		assertTrue(g.existsEdge(new Point(6,0), new Point(6,17), false));
	}
	
	public boolean compareCharArray(Object[] t1, char[] t2)
	{
		if(t1.length != t2.length)
			return false;
		
		for(int i = 0; i < t1.length; i++)
		{
			if(!(t1[i] instanceof Character) || (char) t1[i] != t2[i])
				return false;
		}
		return true;
	}
}
