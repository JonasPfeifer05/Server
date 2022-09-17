package util;

import java.util.ArrayList;

/**
 * Created: 16.09.2022
 *
 * @author Jonas Pfeifer (jonas)
 */

public class Logger<T extends Enum<T>> {
	private ArrayList<Message<T>> messages = new ArrayList<>();
	private final boolean output;

	public Logger(boolean output) {
		this.output = output;
	}

	public void addMessage(T status, String text) {
		Message<T> message = new Message<>(status, text);
		messages.add(message);
		if (output) System.out.println(message);
	}

	public void printAll(ArrayList<T> statuses) {
		for (Message<T> message : messages) {
			if (statuses.contains(message.getStatus())) System.out.println(message);
		}
	}

	public void printAll() {
		printAll(new ArrayList<>());
	}
}

class Message<T> {
	private final T status;
	private final String text;

	public Message(T status, String text) {
		this.status = status;
		this.text = text;
	}

	@Override
	public String toString() {
		return String.format("[%s]: %s", status.toString(), text);
	}

	public T getStatus() {
		return status;
	}

	public String getText() {
		return text;
	}
}
