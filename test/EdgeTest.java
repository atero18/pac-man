package test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bin.Model.Edge;
import bin.Model.ModelException;


/**
 * @author Aleam
 * @version 0.0.1
 * @since 0.20.2
 */

class EdgeTest {
	Edge ed; 

	@BeforeEach
	void setUp() throws Exception {
		Edge.initialize();
		ed = new Edge (1.0f, 'D');
	}

	@Test
	void testConstruct() throws ModelException {
		Edge e1 = new Edge(4.2f, 'U');
		assertEquals(4.2f, e1.getVal());
		assertEquals('U', e1.getDir());
	}
	
	@Test
	void testConstructExcp() {
		assertThrows(ModelException.class, () -> {Edge e1 = new Edge(4.2f, 'E');});
	}
	
	@Test
	void testReverse() throws ModelException {
		Edge e1 = ed.reverse();
		assertEquals(1.0f, e1.getVal());
		assertEquals('U', e1.getDir());
	}

}
