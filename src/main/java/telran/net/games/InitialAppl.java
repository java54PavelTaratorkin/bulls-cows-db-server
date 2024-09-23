package telran.net.games;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;

import org.hibernate.jpa.HibernatePersistenceProvider;

import jakarta.persistence.*;
public class InitialAppl {

	public static void main(String[] args) {
		HashMap<String, Object> map = new HashMap<>();
		map.put("hibernate.hbm2ddl.auto", "update");//using existing table
		map.put("hibernate.show_sql", true);
		map.put("hibernate.format_sql", true);
		EntityManagerFactory emFactory = new HibernatePersistenceProvider()
				.createContainerEntityManagerFactory(new BullsCowsPersistenceUnitInfo(), map);
		EntityManager em = emFactory.createEntityManager();
		JpqlQueriesRepository repository = new JpqlQueriesRepository(em);
//		List<Game> games = repository.getGamesFinished(false);
//		displayResult(games);
//		List<DateTimeSequence> list =
//				repository.getDateTimeSequence(LocalTime.of(12, 0));
//		displayResult(list);
//		List<Integer> list =
//				repository.getBullsInMovesGamersBornAfter(LocalDate.ofYearDay(2000, 1));
//		displayResult(list);
//		List<MinMaxAmount> list = 
//				repository.getDistributionGamesMoves(6);
//		displayResult(list);

		//HW#55
		System.out.println("1. display all data about games, average age of gamers in which \n"
				+ "greater than 60 (consider constraction where id in (select with group by)");
		List<Game> task1 = 
		repository.getGamesWithGamersAvgAgeGreaterThan(60);
		displayResult(task1);
		
		System.out.println("2. display game_id and number of moves made by winner of games with \n"
				+ "number of moves made by winner less than 5");
		List<GameMoves> task2 = 
				repository.getGameWinnerMovesLessThen(5);
		displayResult(task2);
		
		System.out.println("3. display gamer names who made in one game number of moves less than 4");
		List<String> task3 = 
				repository.getGamerMovesLessThen(5);
		displayResult(task3);
		
		System.out.println("4. display game_id and average number of moves made by each gamer. \n"
				+ "Example in game 100000 there are three gamers (gamer1, gamer2, gamer3)");
		List<GameAvgMoves> task4 = 
				repository.getGameGamerAvgMoves();
		displayResult(task4);

	}

	private static <T >void displayResult(List<T> list) {
		list.forEach(System.out::println);
		
	}

}
