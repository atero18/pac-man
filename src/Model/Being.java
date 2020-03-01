package src.Model;


/**
 * @author Atero
 * @version 0.0.1
 * @since 0.20.2
 */

public interface Being
{
	
	boolean isAlive();
	
	void manageMove();
	
	char getActMove();
	
	Point<Float> getPos();
	
	String toString();
}
