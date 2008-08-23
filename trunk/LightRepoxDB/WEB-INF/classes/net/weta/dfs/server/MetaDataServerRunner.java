package net.weta.dfs.server;

import java.io.IOException;
import net.weta.dfs.config.Configuration;
import net.weta.dfs.server.mds.MetaDataServer;

public class MetaDataServerRunner {

	public static MetaDataServer mn = null;
	
	public final static Thread dmetas = new Thread() {
		public void run() {
			try {
				Configuration cfg = Configuration.getInstance();
				mn = new MetaDataServer(cfg);
				mn.startServer();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	};
	
}
