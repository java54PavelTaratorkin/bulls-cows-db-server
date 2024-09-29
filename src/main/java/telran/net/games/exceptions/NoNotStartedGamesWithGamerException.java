package telran.net.games.exceptions;

import java.util.NoSuchElementException;

@SuppressWarnings("serial")
public class NoNotStartedGamesWithGamerException extends NoSuchElementException {
	public NoNotStartedGamesWithGamerException(String username) {
		super("No not started games with gamer: " + username);
	}
}
