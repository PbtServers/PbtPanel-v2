package com.pbtservers.pbtpanel.websocket.command;

import org.java_websocket.WebSocket;

import com.pbtservers.pbtpanel.util.Internationalization;
import com.pbtservers.pbtpanel.websocket.WSServer;
import com.pbtservers.pbtpanel.websocket.response.RamUsage;

public class RamUsageCommand implements WSCommand {

	@Override
	public void execute(WSServer wsServer, WebSocket conn, String params) {
		Runtime r = Runtime.getRuntime();
		
		long free = r.freeMemory() / 1024 / 1024;
		long max = r.maxMemory() / 1024 / 1024;
		long used = r.totalMemory() / 1024 / 1024 - free;
		
		wsServer.sendToClient(conn,
			new RamUsage(
				Internationalization.getPhrase("ram-usage-message", free, used, max),
				free,
				used,
				max
			));
	}

}