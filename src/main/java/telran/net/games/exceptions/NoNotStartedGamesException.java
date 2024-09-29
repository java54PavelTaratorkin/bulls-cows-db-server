package telran.net.games.exceptions;

import java.util.NoSuchElementException;

@SuppressWarnings("serial")
public class NoNotStartedGamesException extends NoSuchElementException {
	public NoNotStartedGamesException() {
		super("No not started games");
	}
}
