package client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created: 16.09.2022
 *
 * @author Jonas Pfeifer (jonas)
 */

public class Client {

	public static void main(String[] args) {
		try {
			Socket socket = new Socket("localhost", 123);
			ObjectInputStream ois;
			ObjectOutputStream oos;

			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());

			oos.writeObject("Hallo my name Jonas");

			System.out.println("Press enter to leave");
			new Scanner(System.in).nextLine();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
