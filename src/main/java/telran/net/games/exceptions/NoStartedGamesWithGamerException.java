package telran.net.games.exceptions;

import java.util.NoSuchElementException;

@SuppressWarnings("serial")
public class NoStartedGamesWithGamerException extends NoSuchElementException {
	public NoStartedGamesWithGamerException(String username) {
		super("No started games with gamer: " + username);
	}
}
