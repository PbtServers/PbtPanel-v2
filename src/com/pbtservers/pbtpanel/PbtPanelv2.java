package com.pbtservers.pbtpanel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.java_websocket.server.DefaultSSLWebSocketServerFactory;

import com.pbtservers.pbtpanel.config.ConfigManager;
import com.pbtservers.pbtpanel.minecraft.WebConsoleCommand;
import com.pbtservers.pbtpanel.util.Internationalization;
import com.pbtservers.pbtpanel.util.LogFilter;
import com.pbtservers.pbtpanel.websocket.WSServer;

public class PbtPanelv2 extends JavaPlugin {
	// Websocket server and thread
	private WSServer server;
	private Thread wsThread;

	@Override
	public void onEnable() {
		//Change language to user-specified language.
		Internationalization.setCurrentLocale(ConfigManager.getInstance().getLanguage());
		
		//Start WebSocket Server
		try {
			startWS();
		} catch (Exception e) {
			Bukkit.getLogger().warning(Internationalization.getPhrase("boot-error"));
			e.printStackTrace();
		}
		
		//This filter is used to read the whole console.
		Filter f = new LogFilter(getWSServer());
		((org.apache.logging.log4j.core.Logger) LogManager.getRootLogger()).addFilter(f);
		
		//Register /WebConsole command
		getCommand("PbtPanel").setExecutor(new WebConsoleCommand(this.getDescription().getVersion()));
	}

	@Override
	public void onDisable() {
		try {
			server.stop();
			wsThread = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Start WebSocket server
	 */
	private void startWS() throws Exception {
		// Create WebSocket server
		server = new WSServer(ConfigManager.getInstance().getSocketAdress());
		
		if(ConfigManager.getInstance().isSslEnabled()) {
			// Configure SSL
			String STORETYPE = ConfigManager.getInstance().getStoreType();
			String KEYSTORE = ConfigManager.getInstance().getKeyStore();
			String STOREPASSWORD = ConfigManager.getInstance().getStorePassword();
			String KEYPASSWORD = ConfigManager.getInstance().getKeyPassword();
			
			KeyStore ks = KeyStore.getInstance(STORETYPE);
			File kf = new File(KEYSTORE);
			ks.load(new FileInputStream(kf), STOREPASSWORD.toCharArray());
			
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			kmf.init(ks, KEYPASSWORD.toCharArray());
			TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
			tmf.init(ks);
			
			SSLContext sslContext = null;
			sslContext = SSLContext.getInstance("TLS");
			sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
			
			server.setWebSocketFactory(new DefaultSSLWebSocketServerFactory(sslContext));
		}

		// Start Server
		wsThread = new Thread(new Runnable() {
			@Override
			public void run() {
				server.run();
			}
		});
		wsThread.start();
	}

	public WSServer getWSServer() {
		return server;
	}
}