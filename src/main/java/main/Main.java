package main;

import Server.Server;
import exception.ServerConstructionException;

/**
 * Created: 16.09.2022
 *
 * @author Jonas Pfeifer (jonas)
 */

public class Main {
	private static Server server;
	public static void main(String[] args) {
		try {
			server = new Server(1000000000, 10);
		} catch (ServerConstructionException e) {
			System.out.println(e.getMessage());
		}
	}
}
