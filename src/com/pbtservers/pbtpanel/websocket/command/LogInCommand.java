package com.pbtservers.pbtpanel.websocket.command;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.java_websocket.WebSocket;

import com.pbtservers.pbtpanel.auth.ConnectedUser;
import com.pbtservers.pbtpanel.auth.LoginManager;
import com.pbtservers.pbtpanel.config.ConfigManager;
import com.pbtservers.pbtpanel.config.UserData;
import com.pbtservers.pbtpanel.util.Internationalization;
import com.pbtservers.pbtpanel.websocket.WSServer;
import com.pbtservers.pbtpanel.websocket.response.LoggedIn;
import com.pbtservers.pbtpanel.websocket.response.LoginRequired;

public class LogInCommand implements WSCommand {
	
	@Override
	public void execute(WSServer wsServer, WebSocket conn, String password) {
		// If user is logged in, then return.
		if (LoginManager.getInstance().isSocketConnected(conn.getRemoteSocketAddress()))
			return;
		
		//Check if user exists
		for(UserData ud : ConfigManager.getInstance().getAllUsers()) {
			if(ud.getPassword().equals(password)) {
				ConnectedUser user = new ConnectedUser(conn.getRemoteSocketAddress(), ud.getUsername(), UUID.randomUUID().toString(), ud.getUserType());
				LoginManager.getInstance().logIn(user);
				
				wsServer.sendToClient(conn, new LoggedIn(Internationalization.getPhrase("login-sucessful-message"), "LOGIN ********", user.getUsername(), user.getUserType(), user.getToken()));
				Bukkit.getLogger().info(Internationalization.getPhrase("login-sucessful-console", user.toString()));
				return;
			}
		}
		wsServer.sendToClient(conn, new LoginRequired(Internationalization.getPhrase("login-failed-message")));
		Bukkit.getLogger().info(Internationalization.getPhrase("login-failed-console", conn.getRemoteSocketAddress()));
	}

}