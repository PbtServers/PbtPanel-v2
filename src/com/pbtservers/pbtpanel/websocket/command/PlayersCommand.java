package com.pbtservers.pbtpanel.websocket.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.java_websocket.WebSocket;

import com.pbtservers.pbtpanel.util.Internationalization;
import com.pbtservers.pbtpanel.websocket.WSServer;
import com.pbtservers.pbtpanel.websocket.response.Players;

public class PlayersCommand implements WSCommand{

	@Override
	public void execute(WSServer wsServer, WebSocket conn, String params) {
		List<String> connectedPlayersList = new ArrayList<String>();
		for(Player player : Bukkit.getOnlinePlayers()) {
			connectedPlayersList.add(player.getName());
		}
		
		int connectedPlayers = connectedPlayersList.size();
		int maxPlayers = Bukkit.getMaxPlayers();
		
		wsServer.sendToClient(conn, 
			new Players(
				Internationalization.getPhrase("players-message", connectedPlayers, maxPlayers),
				connectedPlayers,
				maxPlayers,
				connectedPlayersList
			));
	}
	
}