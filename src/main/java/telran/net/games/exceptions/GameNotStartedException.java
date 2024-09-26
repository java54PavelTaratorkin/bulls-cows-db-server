package telran.net.games.exceptions;

public class GameNotStartedException extends IllegalStateException {
	public GameNotStartedException(long gameId) {
		super("Not started game " + gameId);
	}
}
