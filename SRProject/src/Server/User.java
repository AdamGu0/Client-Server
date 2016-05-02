package Server;

import Server.Server.MessageThread;

public class User {
	public String id;
	public String group;
	public MessageThread thread;
	
	public User(String id) {
		this.id = id;
	}
}
