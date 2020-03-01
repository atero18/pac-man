package src.Model;

/**
 * @override
 * @author Atero
 */
public class ModelException extends Exception
{
	private static final long serialVersionUID = 8057217694102295832L;

	public ModelException(String errorMessage)
	{
		super("Model Exception - " + errorMessage);
	}
}
