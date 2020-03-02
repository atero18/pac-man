package src.Model;


/**
 * @author Atero
 * @version 0.0.1
 * @since 0.20.2
 */

public interface Being
{
	
	boolean isAlive();
	
	void manageMove(Graph g);
	
	char getActMove();
	
	void move(Graph g);
	
	Point<Float> getPos();
	
	String toString();
}
