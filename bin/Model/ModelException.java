package bin.Model;

/**
 * @override
 * @author Atero
 */
public class ModelException extends Exception
{
	public ModelException(String errorMessage)
	{
		super("Model Exception - " + errorMessage);
	}
}
