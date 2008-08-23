package net.weta.dfs.server;

import java.io.IOException;
import net.weta.dfs.config.Configuration;
import net.weta.dfs.server.dns.DataNodeServer;

public class DataNodeServerRunner {

	public static DataNodeServer dn = null;
	
	public final static Thread dnodes = new Thread() {
		public void run() {
			try {
				Configuration cfg = Configuration.getInstance();
				dn = new DataNodeServer(cfg);
				dn.startServer();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	};
	
}
