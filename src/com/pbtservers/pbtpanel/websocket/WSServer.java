package com.pbtservers.pbtpanel.websocket;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.java_websocket.WebSocket;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.pbtservers.pbtpanel.auth.LoginManager;
import com.pbtservers.pbtpanel.util.DateTimeUtils;
import com.pbtservers.pbtpanel.util.Internationalization;
import com.pbtservers.pbtpanel.util.JsonUtils;
import com.pbtservers.pbtpanel.websocket.command.WSCommand;
import com.pbtservers.pbtpanel.websocket.command.WSCommandFactory;
import com.pbtservers.pbtpanel.websocket.response.ConsoleOutput;
import com.pbtservers.pbtpanel.websocket.response.JSONOutput;
import com.pbtservers.pbtpanel.websocket.response.LoggedIn;
import com.pbtservers.pbtpanel.websocket.response.LoginRequired;
import com.pbtservers.pbtpanel.websocket.response.UnknownCommand;

public class WSServer extends WebSocketServer {
	private HashMap<String, WSCommand> commands = WSCommandFactory.getCommandsHashMap();

	public WSServer(InetSocketAddress address) {
		super(address);
		setReuseAddr(true);
	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		if (LoginManager.getInstance().isSocketConnected(conn.getRemoteSocketAddress())) {
			sendToClient(conn, new LoggedIn(Internationalization.getPhrase("connection-resumed-message")));
			Bukkit.getLogger().info(Internationalization.getPhrase("connection-resumed-console", conn.getRemoteSocketAddress()));
		} else {
			sendToClient(conn, new LoginRequired(Internationalization.getPhrase("connection-login-message")));
			Bukkit.getLogger().info(Internationalization.getPhrase("connection-login-console", conn.getRemoteSocketAddress()));
		}
	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		if(!JsonUtils.containsStringProperty(message, "command") //Contains a command
				|| ( !JsonUtils.containsStringProperty(message, "token") && !JsonUtils.getStringProperty(message, JsonUtils.COMMAND_PROPERTY).equals("LOGIN")) //Contains a token or it is a login command
			)
			return;
		
		// Get command and params
		String wsCommand = JsonUtils.getStringProperty(message, JsonUtils.COMMAND_PROPERTY);
		String wsToken = JsonUtils.getStringProperty(message, JsonUtils.TOKEN_PROPERTY);
		String wsCommandParams = JsonUtils.getStringProperty(message, JsonUtils.PARAMS_PROPERTY);

		// Run command
		WSCommand cmd = commands.get(wsCommand);

		if (cmd == null) {
			// Command does not exist
			sendToClient(conn, new UnknownCommand(Internationalization.getPhrase("unknown-command-message"), message));
			Bukkit.getLogger().info(Internationalization.getPhrase("unknown-command-console", message));
		} else if (!wsCommand.equals("LOGIN")
				&& !LoginManager.getInstance().isLoggedIn(conn.getRemoteSocketAddress(), wsToken)) {
			// User is not authorised. DO NOTHING, IMPORTANT!
			sendToClient(conn, new LoginRequired(Internationalization.getPhrase("forbidden-message")));
			Bukkit.getLogger().warning(Internationalization.getPhrase("forbidden-console", conn.getRemoteSocketAddress(), message));
		} else {
			cmd.execute(this, conn, wsCommandParams);
		}
	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		LoginManager.getInstance().logOut(conn.getRemoteSocketAddress());
		Bukkit.getLogger().info(Internationalization.getPhrase("closed-connection", conn.getRemoteSocketAddress()));
	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		Bukkit.getLogger().warning(Internationalization.getPhrase("error-on-connection", conn.getRemoteSocketAddress(), ex));
	}

	@Override
	public void onStart() {
		Bukkit.getLogger().info(Internationalization.getPhrase("started-websocket"));
	}

	/**
	 * Sends the message to all connected AND logged-in users
	 */
	public void onNewConsoleLinePrinted(String line) {
		Collection<WebSocket> connections = getConnections();
		for (WebSocket connection : connections) {
			if (LoginManager.getInstance().isSocketConnected(connection.getRemoteSocketAddress()))
				sendToClient(connection, new ConsoleOutput(line, DateTimeUtils.getTimeAsString()));
		}
	}

	/**
	 * Sends this JSONOutput to client
	 * @param conn    Connection to client
	 * @param content JSONOutput object
	 */
	public void sendToClient(WebSocket conn, JSONOutput content) {
		try {
			conn.send(content.toJSON());
		}catch(WebsocketNotConnectedException e) {
			LoginManager.getInstance().logOut(conn.getRemoteSocketAddress());
			Bukkit.getLogger().warning(Internationalization.getPhrase("error-disconnected-client"));
		}
		
	}

}