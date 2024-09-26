package telran.net.games.service;

import static org.junit.jupiter.api.Assertions.*;

import java.time.*;
import java.util.*;
import java.util.stream.*;

import org.junit.jupiter.api.*;

import telran.net.games.BullsCowsTestPersistenceUnitInfo;
import telran.net.games.entities.*;
import telran.net.games.exceptions.*;
import telran.net.games.model.*;
import telran.net.games.repo.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BullsCowsServiceTest {
	private static final int N_DIGITS = 4;
	static BullsCowsRepository repository;
	static BullsCowsService bcService;
	static BullsCowsGameRunner bcRunner;
	static long gameId;
	static String username = "test_gamer";
	static {
		HashMap<String, Object> hibernateProperties = new HashMap<>();
		hibernateProperties.put("hibernate.hbm2ddl.auto", "create");
		repository = new BullsCowsRepositoryJpa
				(new BullsCowsTestPersistenceUnitInfo(), hibernateProperties);
		bcRunner = new BullsCowsGameRunner(N_DIGITS);
		bcService = new BullsCowsServiceImpl(repository, bcRunner);
		
	}
	
	// Normal Flow Tests
	@Order(1)
	@Test
	void createGameTest() {
		gameId = bcService.createGame();
		assertTrue(gameId > 0);
		Game game = repository.getGame(gameId);
		assertEquals(gameId, game.getId());
	}
	
	@Order(2)
	@Test
	void registerGamerTest() {		
		assertDoesNotThrow(() -> bcService.registerGamer(username, LocalDate.of(2000, 1, 1)));
		Gamer gamer = repository.getGamer(username);
		assertEquals(username, gamer.getUsername());
	}
	
	@Order(3)
	@Test
	void gamerJoinGameTest() {
		assertDoesNotThrow(() -> bcService.gamerJoinGame(gameId, username));
		List<String> gamers = bcService.getGameGamers(gameId);
		assertTrue(gamers.contains(username));
	}
	
	@Order(4)
	@Test
	void startGameTest() {
		bcService.startGame(gameId);
		assertTrue(repository.isGameStarted(gameId));
	}
	
	@Order(5)
	@Test
	void nonWinnerMoveProcessingTest() {
	    String actualSequence = ((BullsCowsServiceImpl)bcService).getSequence(gameId);    
	    IntStream.range(0, 3).forEach(i -> {
	        String randomSequence = getNonWinnerMove(actualSequence);
	        List<MoveData> moves = bcService.moveProcessing(randomSequence, gameId, username);
	        assertFalse(moves.isEmpty());
	        assertEquals(randomSequence, moves.get(moves.size() - 1).sequence());
	        assertNotEquals(N_DIGITS, moves.get(moves.size() - 1).bulls());
	    });
	}

	private String getNonWinnerMove(String actualSequence) {
	    return Stream.generate(() -> bcRunner.getRandomSequence())
	                 .filter(seq -> !seq.equals(actualSequence))
	                 .findFirst()
	                 .orElseThrow();
	}
	
	@Order(5)
	@Test
	void winnerMoveProcessingTest() {
	    String actualSequence = ((BullsCowsServiceImpl)bcService).getSequence(gameId);    
	    List<MoveData> moves = bcService.moveProcessing(actualSequence, gameId, username);
	    assertFalse(moves.isEmpty());
	    assertEquals(actualSequence, moves.get(moves.size() - 1).sequence());
	    assertEquals(N_DIGITS, moves.get(moves.size() - 1).bulls());
	}
	
	@Order(6)
	@Test
	void gameOverTest() {
		assertTrue(bcService.gameOver(gameId));
	}
	
	@Order(7)
	@Test
	void checkWinnerGamerTest() {
		assertTrue(repository.isWinner(gameId, username));
	}

	// Non-Normal Flow Tests	
	@Test
	void incorrectStartGameTest() {
		assertThrowsExactly(GameNotFoundException.class,
				() -> bcService.startGame(gameId + 1000));
		assertThrowsExactly(GameAlreadyStartedException.class,
				() -> bcService.startGame(gameId));
		Long notStartedGameId = bcService.createGame();
		assertThrowsExactly(NoGamerInGameException.class,
				() -> bcService.startGame(notStartedGameId));	
	}
	
	@Test
	void incorrectRegisterGamerTest() {
		assertThrowsExactly(GamerAlreadyExistsdException.class,
				() -> bcService.registerGamer(username, LocalDate.of(2000, 1, 1)));
	}
	
	@Test
	void incorrectGamerJoinGame() {
		assertThrowsExactly(GameAlreadyStartedException.class, 
				() -> bcService.gamerJoinGame(gameId, username));
	    long newGameId = bcService.createGame();
	    bcService.gamerJoinGame(newGameId, username);
	    assertThrowsExactly(GameGamerAlreadyExistsException.class, 
	            () -> bcService.gamerJoinGame(newGameId, username));
	}
	
	@Test
	void incorrectMoveProcessingTest() {
		assertThrowsExactly(GameNotFoundException.class, 
				() -> bcService.moveProcessing("5678", gameId + 1000, username));
		String nonExistingGamer = "non_existent_gamer";
		assertThrowsExactly(GamerNotFoundException.class, 
				() -> bcService.moveProcessing("5678", gameId, nonExistingGamer));
		long notStartedGameId = bcService.createGame();
		assertThrowsExactly(GameNotStartedException.class, 
				() -> bcService.moveProcessing("5678", notStartedGameId, username));
		String guess = "1234";
		assertThrowsExactly(GameFinishedException.class, 
				() -> bcService.moveProcessing(guess, gameId, username));
	    
		long startedNotFinishedGameId = bcService.createGame();
	    bcService.gamerJoinGame(startedNotFinishedGameId, username);
	    bcService.startGame(startedNotFinishedGameId);
	    String incorrectGuess = "1123";
	    assertThrowsExactly(IncorrectMoveSequenceException.class, 
	        () -> bcService.moveProcessing(incorrectGuess, startedNotFinishedGameId, username));
	}

	@Test
	void incorrectGameOver() {
		assertThrowsExactly(GameNotFoundException.class, 
		        () -> bcService.gameOver(gameId + 1000));
	}
	
	//Other tests
	@Test
	void getNotStartedGamesTest() {
	    long notStartedGameId = bcService.createGame();
	    List<Long> notStartedGames = bcService.getNotStartedGames();
	    assertTrue(notStartedGames.contains(notStartedGameId));
	}
	
	@Test
	void getGameGamersTest() {
	    List<String> gamers = bcService.getGameGamers(gameId);
	    assertNotNull(gamers);
	    assertFalse(gamers.isEmpty());
	    assertTrue(gamers.contains(username));
	}
}

