package telran.net.games.exceptions;

@SuppressWarnings("serial")
public class GameGamerNotFoundException extends IllegalArgumentException {
	public GameGamerNotFoundException(long gameId, String username) {
		super("Not found gameGamer with game " + gameId + " and gamer " + username);
	}
}
