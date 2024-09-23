package telran.net.games;

import java.time.*;
import java.util.*;

import org.hibernate.jpa.HibernatePersistenceProvider;

import jakarta.persistence.*;
import jakarta.persistence.spi.*;
import telran.net.games.exceptions.*;

public class BullsCowsRepositoryJpa implements BullsCowsRepository {
	private EntityManager em;
	public BullsCowsRepositoryJpa(PersistenceUnitInfo persistenceUnit,
			HashMap<String, Object> hibernateProperties) {
		EntityManagerFactory emf = new HibernatePersistenceProvider()
				.createContainerEntityManagerFactory(persistenceUnit, hibernateProperties);
		em = emf.createEntityManager();
		
	}
	@Override
	public Game getGame(long id) {
		Game game = em.find(Game.class, id);
		if(game == null) {
			throw new GameNotFoundException(id);
		}
		return game;
	}

	@Override
	public Gamer getGamer(String username) {
		Gamer gamer = em.find(Gamer.class, username);
		if(gamer == null) {
			throw new GamerNotFoundException(username);
		}
		return gamer;
	}

	@Override
	public long createNewGame(String sequence) {
		Game game = new Game(null, false, sequence);
			createObject(game);
		return game.getId();
	}
	private <T>void createObject(T obj) {
		EntityTransaction transaction = em.getTransaction();
		transaction.begin();
		em.persist(obj);
		transaction.commit();
	}

	@Override
	public void createNewGamer(String username, LocalDate birthdate) {
		try {
			Gamer gamer = new Gamer(username, birthdate);
			createObject(gamer);
		} catch (Exception e) {
			throw new GamerAlreadyExistsdException(username);
		}

	}

	@Override
	public boolean isGameStarted(long id) {
		Game game = getGame(id);
		return game.getDate() != null;
	}

	@Override
	public void setStartDate(long gameId, LocalDateTime dateTime) {
		EntityTransaction transaction = em.getTransaction();
		transaction.begin();
		Game game = getGame(gameId);
		game.setDate(dateTime);
		transaction.commit();

	}
	
	
	//HW#56
	@Override
	public boolean isGameFinished(long id) {
	    Game game = getGame(id);
	    return game.isfinished();
	}

	//HW#56
	@Override
	public void setIsFinished(long gameId) {
	    EntityTransaction transaction = em.getTransaction();
	    transaction.begin();
	    Game game = getGame(gameId);
	    game.setfinished(true);
	    transaction.commit();

	}

	//HW#56
	@Override
	public List<Long> getGameIdsNotStarted() {
	    TypedQuery<Long> query = em.createQuery(
	        "select game.id from Game game where game.dateTime is null", Long.class);
	    return query.getResultList();
	}

	@Override
	public List<String> getGameGamers(long id) {
		TypedQuery<String> query = em.createQuery(
				"select gamer.username from GameGamer where game.id=?1",
				String.class);
		return query.setParameter(1, id).getResultList();
	}
	
	@Override
	public void createGameGamer(long gameId, String username) {
		Game game = getGame(gameId);
		Gamer gamer = getGamer(username);
		GameGamer gameGamer = new GameGamer(false, game, gamer);
		createObject(gameGamer);

	}

	//HW#56
	@Override
	public void createGameGamerMove(MoveDto moveDto) {
	    Game game = getGame(moveDto.gameId());
	    Gamer gamer = getGamer(moveDto.username());
	    GameGamer gameGamer = getGameGamer(game.getId(), gamer.getUsername());
	    Move move = new Move(moveDto.sequence(), moveDto.bulls(), moveDto.cows(), gameGamer);
	    createObject(move);
	}

	//HW#56
	@Override
	public List<MoveData> getAllGameGamerMoves(long gameId, String username) {
	    GameGamer gameGamer = getGameGamer(gameId, username);
	    TypedQuery<MoveData> query = em.createQuery(
	        "select sequence, bulls, cows from Move " +
	        "where gameGamer.game.id = :gameGamerId", MoveData.class);
	    return query.setParameter("gameGamerId", gameGamer.getId()).getResultList();
	}

	//HW#56
	@Override
	public void setWinner(long gameId, String username) {
	    Game game = getGame(gameId);
	    Gamer gamer = getGamer(username);
	    GameGamer gameGamer = getGameGamer(game.getId(), gamer.getUsername());
	    if (gameGamer == null) {
	    	throw new GameGamerNotFoundException(game.getId(), gamer.getUsername());
	    }
	    EntityTransaction transaction = em.getTransaction();
	    transaction.begin();
	    gameGamer.setWinner(true);
	    transaction.commit();
	}

	//HW#56
	@Override
	public boolean isWinner(long gameId, String username) {
	    Game game = getGame(gameId);
	    Gamer gamer = getGamer(username);
	    GameGamer gameGamer = getGameGamer(game.getId(), gamer.getUsername());
	    if (gameGamer == null) {
	    	throw new GameGamerNotFoundException(game.getId(), gamer.getUsername());
	    }
	    return gameGamer.isWinner();
	}

	private GameGamer getGameGamer(long gameId, String username) {
	    return em.createQuery(
	        "select gameGamer from GameGamer gameGamer " +
	        "where game.id = :gameId and gamer.username = :username", 
	        GameGamer.class)
	        .setParameter("gameId", gameId)
	        .setParameter("username", username)
	        .getSingleResult();
	}
}
