package telran.net.games.exceptions;

public class IncorrectMoveSequenceException extends IllegalArgumentException {
	public IncorrectMoveSequenceException(String sequence) {
		super("Incorrect move sequence entered " + sequence);
	}
}
