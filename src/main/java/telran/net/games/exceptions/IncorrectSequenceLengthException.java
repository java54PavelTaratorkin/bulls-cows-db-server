package telran.net.games.exceptions;

@SuppressWarnings("serial")
public class IncorrectSequenceLengthException extends IllegalStateException {
	public IncorrectSequenceLengthException(int minLength, int maxLength) {
		super(String.format("Sequence length must be %d - %d digits", minLength, maxLength));
	}
}
