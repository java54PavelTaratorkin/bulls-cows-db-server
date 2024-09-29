package telran.net.games.exceptions;

import java.util.NoSuchElementException;

@SuppressWarnings("serial")
public class NoNotStartedGamesWithNoGamerException extends NoSuchElementException {
	public NoNotStartedGamesWithNoGamerException(String username) {
		super("No not started games without gamer: " + username);
	}
}
