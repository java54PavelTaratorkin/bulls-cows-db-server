package telran.net.games.service;

import java.time.*;
import java.util.List;

import telran.net.games.entities.*;
import telran.net.games.exceptions.*;
import telran.net.games.model.*;
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
	public long createGame() {
		
		return bcRepository.createNewGame(bcRunner.getRandomSequence());
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
		//HW#57
	    bcRepository.createNewGamer(username, birthDate);
	}
	@Override
	/**
	 * join a given gamer to a given game
	 * Exceptions:
	 * GameNotFoundException
	 * GameAlreadyStartedException
	 * GamerNotFoundException
	 */
	public void gamerJoinGame(long gameId, String username) {
		//HW#57
	    if (bcRepository.isGameStarted(gameId)) {
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
		//HW#57
		return bcRepository.getGameIdsNotStarted();
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
	 */
	public List<MoveData> moveProcessing(String sequence, long gameId, String username) {


	    if (!bcRunner.checkGuess(sequence)) {
	        throw new IncorrectMoveSequenceException(sequence);
	    }
	    Game game = bcRepository.getGame(gameId);
	    bcRepository.getGamer(username);
	    validateGameNotFinishedStarted(gameId);


	    MoveData moveData = bcRunner.moveProcessing(sequence, game.getSequence());
	    bcRepository.createGameGamerMove(new MoveDto(gameId, username, moveData.sequence(), moveData.bulls(), moveData.cows()));

	    if (bcRunner.checkGameFinished(moveData)) {
	        bcRepository.setIsFinished(gameId);
	        bcRepository.setWinner(gameId, username);
	    }
	    
	    return bcRepository.getAllGameGamerMoves(gameId, username);
	}

	private void validateGameNotFinishedStarted(long gameId) {
	    if (bcRepository.isGameFinished(gameId)) {
	        throw new GameFinishedException(gameId);
	    }
	    if (!bcRepository.isGameStarted(gameId)) {
	        throw new GameNotStartedException(gameId);
	    }
	}
	
	@Override
	/**
	 * returns true if game is finished
	 * Exceptions:
	 * GameNotFoundException
	 */
	public boolean gameOver(long gameId) {
		//HW#57
		return bcRepository.isGameFinished(gameId);
	}
	@Override
	/**
	 * returns list of gamers in a given game
	 * Exceptions: 
	 * GameNotFoundException
	 */
	public List<String> getGameGamers(long gameId) {
		//HW#57
		bcRepository.getGame(gameId);
		return bcRepository.getGameGamers(gameId);
	}
	/**
	 * Only for testing
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
	
}
