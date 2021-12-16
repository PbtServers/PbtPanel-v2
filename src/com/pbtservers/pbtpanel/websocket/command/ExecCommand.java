package com.pbtservers.pbtpanel.websocket.command;

import java.util.concurrent.ExecutionException;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.java_websocket.WebSocket;

import com.pbtservers.pbtpanel.PbtPanelv2;
import com.pbtservers.pbtpanel.auth.ConnectedUser;
import com.pbtservers.pbtpanel.auth.LoginManager;
import com.pbtservers.pbtpanel.config.ConfigManager;
import com.pbtservers.pbtpanel.config.UserData;
import com.pbtservers.pbtpanel.config.UserType;
import com.pbtservers.pbtpanel.util.Internationalization;
import com.pbtservers.pbtpanel.websocket.WSServer;

public class ExecCommand implements WSCommand {
	LoginManager loginManager = LoginManager.getInstance();

	@Override
	public void execute(WSServer wsServer, WebSocket conn, String command) {
		ConnectedUser u = LoginManager.getInstance().getUser(conn.getRemoteSocketAddress());
		if(u == null || u.getUserType() != UserType.ADMIN) {
			if(u != null)
				Bukkit.getLogger().warning(Internationalization.getPhrase("no-send-permission-console", u, command));
			return;
		}

		boolean allowCommand = checkWhitelist(conn, command);
		if (!allowCommand) {
			Bukkit.getLogger().warning(Internationalization.getPhrase("no-send-permission-console", u, command));
			return;
		}
		
		Bukkit.getLogger().info(Internationalization.getPhrase("cmd-executed-console", conn.getRemoteSocketAddress(), Internationalization.utf8ToIso(command)));
		ConsoleCommandSender sender = Bukkit.getServer().getConsoleSender();
		PbtPanelv2 plugin = (PbtPanelv2) Bukkit.getPluginManager().getPlugin("PbtPanelv2");
		try {
			@SuppressWarnings("unused")
			boolean success = Bukkit.getScheduler()
					.callSyncMethod(plugin, () -> Bukkit.dispatchCommand(sender, command)).get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

	}
	
	private boolean checkWhitelist(WebSocket conn, String command) {
		for(UserData ud : ConfigManager.getInstance().getAllUsers()) {
			if (ud.getUsername().equals(loginManager.getUser(conn.getRemoteSocketAddress()).getUsername())) {

				if (!ud.isWhitelistEnabled()) { //Skip whitelist check.
					return true;
				}

				String[] splitCommand = command.split(" ");

				for (String whitelistedCommand : ud.getWhitelistedCommands()) {
					String[] splitWhitelistedCommand = whitelistedCommand.split(" ");

					if(equalsArray(splitCommand, splitWhitelistedCommand)) {
						//Command matches the whitelist
						if(ud.isWhitelistActsAsBlacklist())
							return false; //If acts as blacklist, do not allow command
						else
							return true; //If acts as Whitelist, allow command
					}
				}
				
				//If execution reached this point, then the command is not in the blacklist.
				if(ud.isWhitelistActsAsBlacklist())
					return true; //If acts as blacklist, allow command
				else
					return false; //If acts as Whitelist, do not allow command
			}
		}
		throw new RuntimeException("Ning�n Usuario ha Pasado la Whitelist.");
	}
	
	/**
	 * Check if the user command matches the whitelisted command
	 * 
	 * @param splitCommand Command sent by user
	 * @param splitWhitelistedCommand Command in the whitelist
	 * @return true if the user command matches the whitelist command
	 */
	private boolean equalsArray(String[] splitCommand, String[] splitWhitelistedCommand) {
		for (int i = 0; i < splitWhitelistedCommand.length; i++)
			if (!splitCommand[i].equalsIgnoreCase(splitWhitelistedCommand[i])) 
				return false; //Does not match so far
		return true; //Matches the command
	}

}