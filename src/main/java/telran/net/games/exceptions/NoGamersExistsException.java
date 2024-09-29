package telran.net.games.exceptions;

import java.util.NoSuchElementException;

@SuppressWarnings("serial")
public class NoGamersExistsException extends NoSuchElementException {
	public NoGamersExistsException() {
		super("There is no any Gamers records exists");
	}
}
