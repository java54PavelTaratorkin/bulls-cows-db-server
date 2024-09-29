package telran.net.games.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import telran.net.games.entities.*;
import telran.net.games.exceptions.*;
import telran.net.games.model.GameDefaultData;
import telran.net.games.model.MoveData;
import telran.net.games.model.MoveDto;
import telran.net.games.repo.BullsCowsRepository;

public class BullsCowsServiceImpl implements BullsCowsService {
	private BullsCowsRepository bcRepository;
	private BullsCowsGameRunner bcRunner;
	
	
	public BullsCowsServiceImpl(BullsCowsRepository bcRepository, BullsCowsGameRunner bcRunner) {
		this.bcRepository = bcRepository;
		this.bcRunner = bcRunner;
	}
	@Override
	/**
	 * Creates new game
	 * returns ID of the created game
	 */
	public long createGame(int sequenceLength) {
		
		return bcRepository.createNewGame(bcRunner.getRandomSequence(sequenceLength));
	}
	@Override
	/**
	 * starts game
	 * returns list of gamers (user names)
	 * exceptions:
	 * GameNotFoundException
	 * GameAlreadyStartedException
	 * NoGamerInGameException
	 */
	public List<String> startGame(long gameId) {
		if(bcRepository.isGameStarted(gameId)) {
			throw new GameAlreadyStartedException(gameId);
		}
		List<String> result = bcRepository.getGameGamers(gameId);
		if (result.isEmpty()) {
			throw new NoGamerInGameException(gameId);
		}
		bcRepository.setStartDate(gameId, LocalDateTime.now());
		return result;
	}
	@Override
	/**
	 * adds new gamer
	 * Exceptions:
	 * GamerAlreadyExistsException
	 */
	public void registerGamer(String username, LocalDate birthDate) {
		bcRepository.createNewGamer(username, birthDate);
	}
	@Override
	/**
	 * join a given gamer to a given game
	 * Exceptions:
	 * GameNotFoundException
	 * GameAlreadyStartedException
	 * GamerNotFoundException
	 * GameGamerAlreadyExists
	 */
	public void gamerJoinGame(long gameId, String username) {
		if(bcRepository.isGameStarted(gameId)) {
			throw new GameAlreadyStartedException(gameId);
		}
		bcRepository.createGameGamer(gameId, username);
		
	}
	@Override
	/**
	 * returns list of ID's for not started games
	 * no exceptions (empty list is allowed)
	 */
	public List<Long> getNotStartedGames() {
		List<Long> result = bcRepository.getGameIdsNotStarted();
		if (result.isEmpty()) {
			throw new NoNotStartedGamesException();
		}
		return result;
	}
	@Override
	/**
	 * returns all objects of MoveData of a given game and given gamer
	 * including the last with the given parameters
	 * in the case of the winner's move the game should be set as finished
	 * and the gamer in the game should be set as the winner
	 * Exceptions:
	 * IncorrectMoveSequenceException (extends IllegalArgumentException)_
	 * GameNotFoundException
	 * GamerNotFoundException
	 * GameNotStartedException (extends IllegalStateException)
	 * GameFinishedException (extends IllegalStateException)
	 * GameGamerNotFounException
	 */
	public List<MoveData> moveProcessing(String moveSequence, long gameId, String username) {
		int sequenceLength = getGameSequenceLength(gameId);
		if(!bcRunner.checkGuess(moveSequence, sequenceLength)) {
			throw new IncorrectMoveSequenceException(moveSequence, sequenceLength);
		}
		bcRepository.getGamer(username);//only for checking whether the gamer exists
		if (!bcRepository.isGameStarted(gameId)) {
			throw new GameNotStartedException(gameId);
		}
		if (bcRepository.isGameFinished(gameId)) {
			throw new GameFinishedException(gameId);
		}
		
		String toBeGuessedSequence = getSequence(gameId);
		MoveData moveData = bcRunner.moveProcessing(moveSequence,
				toBeGuessedSequence, sequenceLength);
		MoveDto moveDto = new MoveDto(gameId, username, moveSequence,
				moveData.bulls(), moveData.cows());
		bcRepository.createGameGamerMove(moveDto);
		if(bcRunner.checkGameFinished(moveData, sequenceLength)) {
			finishGame(gameId, username);
		}
		return bcRepository.getAllGameGamerMoves(gameId, username);
	}
	private void finishGame(long gameId, String username) {
		bcRepository.setIsFinished(gameId);
		bcRepository.setWinner(gameId, username);
		
	}
	@Override
	/**
	 * returns true if game is finished
	 * Exceptions:
	 * GameNotFoundException
	 */
	public boolean gameOver(long gameId) {
		
		return bcRepository.isGameFinished(gameId);
	}
	@Override
	/**
	 * returns list of gamers in a given game
	 * Exceptions: 
	 * GameNotFoundException
	 */
	public List<String> getGameGamers(long gameId) {
		bcRepository.getGame(gameId);
		return bcRepository.getGameGamers(gameId);
	}
	/**
	 * Implied that the test class resides at the same package (to access the method)
	 * 
	 * @param gameId
	 * @return To be guessed sequence
	 * No Exceptions, that is implied that at the test gameId exists
	 */
	String getSequence(long gameId) {
		Game game = bcRepository.getGame(gameId);
		return game.getSequence();
	}
	
    public BullsCowsServiceImpl(BullsCowsRepository bcRepository) {
        this.bcRepository = bcRepository;
    }
    /**
     * Returns a list of game IDs for not-started games where the specified gamer has joined.
     * Throws NoNotStartedGamesWithGamerException if no such games are found.
     * 
     * @param username the username of the gamer
     * @return list of game IDs
     * Exceptions:
     * NoNotStartedGamesWithGamerException
     */
    @Override
    public List<Long> getNotStartedGamesWithGamer(String username) {
    	List<Long> result = bcRepository.getNotStartedGamesWithGamer(username);
    	if (result.isEmpty()) {
    		throw new NoNotStartedGamesWithGamerException(username);
    	}
        return result;
    }

    /**
     * Returns a list of game IDs for not-started games where the specified gamer has not joined.
     * Throws NoNotStartedGamesWithNoGamerException if no such games are found.
     * 
     * @param username the username of the gamer
     * @return list of game IDs
     * Exceptions:
     * NoNotStartedGamesWithNoGamerException
     */
    @Override
    public List<Long> getNotStartedGamesWithNoGamer(String username) {    	
    	List<Long> result = bcRepository.getNotStartedGamesWithNoGamer(username);
    	if (result.isEmpty()) {
    		throw new NoNotStartedGamesWithNoGamerException(username);
    	}
    	return result;
    }

    /**
     * Returns a list of game IDs for started games where the specified gamer has joined.
     * Throws NoStartedGamesWithGamerException if no such games are found.
     * 
     * @param username the username of the gamer
     * @return list of game IDs
     * Exceptions:
     * NoStartedGamesWithGamerException
     */
    @Override
    public List<Long> getStartedGamesWithGamer(String username) {
    	List<Long> result = bcRepository.getStartedGamesWithGamer(username);
    	if (result.isEmpty()) {
    		throw new NoStartedGamesWithGamerException(username);
    	}
    	return result;
    }
    
    /**
     * Logs in the specified gamer by username and returns the logged-in username.
     * Throws GamerNotFoundException if the gamer does not exist.
     * 
     * @param username the username of the gamer
     * @return the logged-in username
     * Exceptions:
     * GamerNotFoundException
     */
    @Override
    public String loginGamer(String username) {
        Gamer gamer = bcRepository.getGamer(username);
        return gamer.getUsername();
    }
    
    /**
     * Returns a list of all registered gamers.
     * Throws NoGamersExistsException if no gamers are registered.
     * 
     * @return list of gamer usernames
     * Exceptions:
     * NoGamersExistsException
     */
    @Override
    public List<String> getAllGamers() {
    	List<String> result = bcRepository.getAllGamers();
        if (result.isEmpty()) {
        	throw new NoGamersExistsException();
        }
        return result;
    }

    /**
     * Returns the sequence length of the game with the given game ID.
     * 
     * @param gameId the ID of the game
     * @return the sequence length
     */
    @Override
    public int getGameSequenceLength(long gameId) {
		Game game = bcRepository.getGame(gameId);
		return game.getSequence().toString().length();
	}

    /**
     * Retrieves the default game settings including the minimum and maximum sequence size, 
     * default sequence size, and minimum age for gamers.
     * 
     * @return the default game data (min/max sequence size, default sequence size, and minimum age)
     */
    @Override
    public GameDefaultData getGameDefaults() {
		return new GameDefaultData(bcRunner.getMinSequenceLength(),
				bcRunner.getMaxSequenceLength(), bcRunner.getDefSequenceLength(),
				bcRunner.getMinAge());
	}
	
}
