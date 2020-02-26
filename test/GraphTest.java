package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bin.Model.Graph;
import bin.Model.ModelException;

/**
 * @author Atero
 * @version 0.0.1
 * @since 0.20.2
 */
class GraphTest {

	Graph g;
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
		assertTrue(p[0] == 2 && p[1] == 3);
	}

	

	@Test
	void testAddEdgeIntIntEdgeInt() {
		fail("Not yet implemented");
	}

	@Test
	void testAddEdgeIntIntEdge() {
		fail("Not yet implemented");
	}

	@Test
	void testRmEdgeIntInt() {
		fail("Not yet implemented");
	}

	@Test
	void testRmEdgeIntIntBoolean() {
		fail("Not yet implemented");
	}

	@Test
	void testHasNeighbour() {
		fail("Not yet implemented");
	}

	@Test
	void testCalculPred() {
		fail("Not yet implemented");
	}

	@Test
	void testGoTo() {
		fail("Not yet implemented");
	}

	@Test
	void testMatToGraph() {
		fail("Not yet implemented");
	}

	@Test
	void testDisp() {
		fail("Not yet implemented");
	}

}
