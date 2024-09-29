package telran.net.games;

import java.util.HashMap;

import telran.net.Protocol;
import telran.net.TcpServer;
import telran.net.games.config.BullsCowsPersistenceUnitInfo;
import telran.net.games.controller.BullsCowsProtocol;
import telran.net.games.repo.*;
import telran.net.games.service.*;

public class BullsCowsServerAppl {

	private static final int PORT = 4000;

	public static void main(String[] args) {
		int port = args.length > 0 ? Integer.parseInt(args[0]) : PORT;
		Protocol bullsCowsProtocol = getBullsCowsProtocol();
		TcpServer server = new TcpServer(bullsCowsProtocol , port);
		server.run();

	}

	private static Protocol getBullsCowsProtocol() {
		HashMap<String, Object> hibernateProperties = new HashMap<>();
		hibernateProperties.put("hibernate.hbm2ddl.auto", "update");
		BullsCowsRepository repository = new BullsCowsRepositoryJpa
				(new BullsCowsPersistenceUnitInfo(), hibernateProperties);
		BullsCowsGameRunner bcRunner = new BullsCowsGameRunner();
		BullsCowsService bcService = new BullsCowsServiceImpl(repository, bcRunner);
		return new BullsCowsProtocol(bcService);
	}

}
