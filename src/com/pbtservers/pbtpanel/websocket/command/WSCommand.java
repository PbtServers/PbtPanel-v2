package com.pbtservers.pbtpanel.websocket.command;

import org.java_websocket.WebSocket;

import com.pbtservers.pbtpanel.websocket.WSServer;

public interface WSCommand {
	void execute(WSServer wsServer, WebSocket conn, String params);
}