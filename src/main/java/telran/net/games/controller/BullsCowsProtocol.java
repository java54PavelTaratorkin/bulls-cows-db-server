package telran.net.games.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONObject;

import telran.net.*;
import telran.net.games.model.*;
import telran.net.games.service.BullsCowsService;

public class BullsCowsProtocol implements Protocol {
	private BullsCowsService bcService;
	
	public BullsCowsProtocol(BullsCowsService bcService) {
		this.bcService = bcService;
	}

	@Override
	public Response getResponse(Request request) {
		String requestType = request.requestType();
		String requestData = request.requestData();
		Response response;
		try {
			response = switch (requestType) {
			case "createGame" -> createGame(requestData);
			case "startGame" -> startGame(requestData);
			case "registerGamer" -> registerGamer(requestData);
			case "gamerJoinGame" -> gamerJoinGame(requestData);
			case "getNotStartedGames" -> getNotStartedGames(requestData);
			case "moveProcessing" -> moveProcessing(requestData);
			case "gameOver" -> gameOver(requestData);
			case "getGameGamers" -> getGameGamers(requestData);
            case "getNotStartedGamesWithGamer" -> getNotStartedGamesWithGamer(requestData);
            case "getNotStartedGamesWithNoGamer" -> getNotStartedGamesWithNoGamer(requestData);
            case "getStartedGamesWithGamer" -> getStartedGamesWithGamer(requestData);
            case "loginGamer" -> loginGamer(requestData);
            case "getAllGamers" -> getAllGamers(requestData);
            case "getGameSequenceLength" -> getGameSequenceLength(requestData);
            case "getGameDefaults" -> getGameDefaults(requestData);
			default -> new Response(ResponseCode.WRONG_REQUEST_TYPE,
					requestType);
			};
		} catch (Exception e) {
			response = new Response(ResponseCode.WRONG_REQUEST_DATA,
					e.getMessage());
		}
		return response;
	}

	Response createGame(String requestData) {
		long gameId = bcService.createGame(Integer.parseInt(requestData));
		String responseString = Long.toString(gameId);
		return getResponseOk(responseString);
	}
	
	Response startGame(String requestData) {
		long gameId = Long.parseLong(requestData);
		List<String> gamers = bcService.startGame(gameId);
		String responseString = resultsToJSON(gamers);
		return getResponseOk(responseString);
	}

	Response registerGamer(String requestData) {
	    GamerData gamer = new GamerData(new JSONObject(requestData));
	    bcService.registerGamer(gamer.username(), gamer.birthdate());
	    String responseString = "";
	    return getResponseOk(responseString);
	}
	
	Response gamerJoinGame(String requestData) {
		GameGamerDto gameGamer = new GameGamerDto(new JSONObject(requestData)) ;
		bcService.gamerJoinGame(gameGamer.gameId(), gameGamer.username());
		String responseString = "";
		return getResponseOk(responseString);
	}
	
	Response getNotStartedGames(String requestData) {
		List<Long> gameIdsNotStarted = bcService.getNotStartedGames();
		String responseString = resultsToJSON(gameIdsNotStarted);
		return getResponseOk(responseString);
	}
	
	Response moveProcessing(String requestData) {
		SequenceGameGamerDto sggd =
				new SequenceGameGamerDto(new JSONObject(requestData));
		String moveSequence = sggd.sequence();
		long gameId = sggd.gameId();
		String username = sggd.username();
		List<MoveData> results = bcService.moveProcessing(moveSequence, gameId, username);
		String responseString = resultsToJSON(results);
		return getResponseOk(responseString);
	}
	
	Response gameOver(String requestData) {
		long gameId = Long.parseLong(requestData);
		boolean result = bcService.gameOver(gameId);
		String responseString = String.valueOf(result);
		return getResponseOk(responseString);
	}
	
	Response getGameGamers(String requestData) {
		long gameId = Long.parseLong(requestData);
		List<String> gameGamers = bcService.getGameGamers(gameId);
		String responseString = resultsToJSON(gameGamers);
		return getResponseOk(responseString);
	}

	private Response getResponseOk(String responseString) {		
		return new Response(ResponseCode.OK, responseString);
	}
	
	private <T> String resultsToJSON(List<T> res) {		
		return res.stream().map(T::toString)
				.collect(Collectors.joining(";"));
	}
	
    private Response getNotStartedGamesWithGamer(String requestData) {
        String username = requestData;
        List<Long> gameIds = bcService.getNotStartedGamesWithGamer(username);
        String responseString = resultsToJSON(gameIds);
        return getResponseOk(responseString);
    }

    private Response getNotStartedGamesWithNoGamer(String requestData) {
        String username = requestData;
        List<Long> gameIds = bcService.getNotStartedGamesWithNoGamer(username);
        String responseString = resultsToJSON(gameIds);
        return getResponseOk(responseString);
    }

    private Response getStartedGamesWithGamer(String requestData) {
        String username = requestData;
        List<Long> gameIds = bcService.getStartedGamesWithGamer(username);
        String responseString = resultsToJSON(gameIds);
        return getResponseOk(responseString);
    }
    
	private Response loginGamer(String requestData) {		
		String username = requestData;
		String loggedInUsername = bcService.loginGamer(username);
		return getResponseOk(loggedInUsername);
	}
	
	private Response getAllGamers(String requestData) {
        List<String> gamers = bcService.getAllGamers();
        String responseString = resultsToJSON(gamers);
        return getResponseOk(responseString);
	}	
	
	private Response getGameSequenceLength(String requestData) {
		long gameId = Long.parseLong(requestData);
		int sequenceLength = bcService.getGameSequenceLength(gameId);
		return getResponseOk(String.valueOf(sequenceLength));
	}
	
	private Response getGameDefaults(String requestData) {
		return getResponseOk(bcService.getGameDefaults().toString());
	}
}
