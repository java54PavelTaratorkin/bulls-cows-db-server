package telran.net.games;
import java.time.*;
import java.util.List;

import jakarta.persistence.*;
public class JpqlQueriesRepository {
	private EntityManager em;

	public JpqlQueriesRepository(EntityManager em) {
		this.em = em;
	}
	public List<Game> getGamesFinished(boolean isFinished) {
		TypedQuery<Game> query = em.createQuery(
				"select game from Game game where is_finished=?1",
				Game.class);
		List<Game> res = query.setParameter(1, isFinished).getResultList();
		return res;
	}
	public List<DateTimeSequence> getDateTimeSequence(LocalTime time) {
		TypedQuery<DateTimeSequence> query =
				em.createQuery("select date, sequence "
						+ "from Game where cast(date as time) < :time",
						DateTimeSequence.class);
		List<DateTimeSequence> res = query.setParameter("time", time).getResultList();
		return res;
	}
	public List<Integer> getBullsInMovesGamersBornAfter(LocalDate afterDate) {
		TypedQuery<Integer> query = em.createQuery(
				"select bulls from Move where gameGamer.gamer.birthdate > "
				+ "?1", Integer.class);
		List<Integer> res = query.setParameter(1, afterDate).getResultList();
		return res;
		
	}
	public List<MinMaxAmount> getDistributionGamesMoves(int interval) {
//		select floor(game_moves / 5) * 5 as min_moves, floor(game_moves / 5) * 5 + 4 as max_moves,
//		count(*) as amount
//		from
//		(select count(*) as game_moves from game_gamer join move on game_gamer.id=game_gamer_id 
//		group by game_id) group by min_moves order by min_moves
		TypedQuery<MinMaxAmount> query = em.createQuery(
				"select floor(game_moves / :interval) * :interval as min_moves, "
				+ "floor(game_moves / :interval) * :interval + (:interval - 1) as max_moves, "
				+ "count(*) as amount "
				+ "from "
				+ "(select count(*) as game_moves from Move "
				+ "group by gameGamer.game.id) "
				+ "group by min_moves, max_moves order by min_moves", MinMaxAmount.class);
		List<MinMaxAmount> res = query.setParameter("interval", interval).getResultList();
		return res;
	}
	
	//HW55
	public List<Game> getGamesWithGamersAvgAgeGreaterThan(int age) {
		// 1. display all data about games, average age of gamers in which greater than 60 
		// (consider constraction where id in (select with group by)
		//
		// select * from game where id in (select game_id 
		// from game_gamer
		// join gamer on gamer_id=username
		// group by game_id
		// having avg(extract(year from age(birthdate))) > 60)
	    TypedQuery<Game> query = em.createQuery(
	        "select game from Game game where id in " +
	        "(select gameGamer.game.id from GameGamer gameGamer " +
	        "group by gameGamer.game.id " +
	        "having avg(extract(year from current_date) - extract(year from gamer.birthdate)) > :age)",
	        Game.class);
	    query.setParameter("age", age);
	    List<Game> res = query.getResultList();
	    return res;
	}
	
	public List<GameMoves> getGameWinnerMovesLessThen(int numMoves){
		// 2. display game_id and number of moves made by winner of games with number 
		// of moves made by winner less than 5
		//
		// select game_id, count(*)as moves
		// from game_gamer
		// join move on game_gamer.id=game_gamer_id
		// where is_winner
		// group by game_id having count(*) < 5
	    TypedQuery<GameMoves> query = em.createQuery(
		        "select gameGamer.game.id as gameId, count(*) as moves from Move " +
		        "where gameGamer.is_winner = true " +
		        "group by gameId having count(*) < :numMoves",
		        GameMoves.class);
		query.setParameter("numMoves", numMoves);
		List<GameMoves> res = query.getResultList();
		return res;
	}
	
	public List<String> getGamerMovesLessThen(int numMoves){
		// 3. display gamer names who made in one game number of moves less than 4
		//
		// select distinct gamer_id
		// from game_gamer
		// join move on game_gamer.id=game_gamer_id 
		// group by game_id, gamer_id having count(*) < 4
	    TypedQuery<String> query = em.createQuery(
		        "select distinct gameGamer.gamer.username as gamerName from Move " +
		        "group by gameGamer.game.id, gamerName having count(*) < :numMoves",
		        String.class);
		query.setParameter("numMoves", numMoves);
		List<String> res = query.getResultList();
		return res;
	}
	

	
	public List<GameAvgMoves> getGameGamerAvgMoves(){
		// 4. display game_id and average number of moves made by each gamer. Example in 
		// game 100000 there are three gamers (gamer1, gamer2, gamer3)
		// 
		// select game_id, round(avg(moves), 1) from (select game_id, gamer_id, count(*) moves
		// from game_gamer
		// join move
		// on game_gamer.id=game_gamer_id 
		// group by game_id, gamer_id
		// order by game_id)
		// group by game_id
	    TypedQuery<GameAvgMoves> query = em.createQuery(
		        "select gameId, round(avg(moves), 1) from " +
		        "(select gameGamer.game.id as gameId, count(*) as moves " +
		        "from Move group by gameId, gameGamer.gamer.username order by gameId) " +
		        "group by gameId",
		        GameAvgMoves.class);
		List<GameAvgMoves> res = query.getResultList();
		return res;
	}


	
	
}
