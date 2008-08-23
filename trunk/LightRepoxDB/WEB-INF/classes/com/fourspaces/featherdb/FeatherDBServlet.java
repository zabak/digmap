package com.fourspaces.featherdb;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import net.weta.dfs.server.DataNodeServerRunner;
import net.weta.dfs.server.MetaDataServerRunner;

public class FeatherDBServlet implements ServletContextListener, HttpSessionListener {

    private Set<HttpSession> activeSessions = new HashSet<HttpSession>();
    private ServletContext servletContext;
	private FeatherDB app;

	final Thread hook = new Thread() {
		public void run() {
			app.internalShutdown();
		}
	};
	
    public void contextInitialized(ServletContextEvent servletContextEvent) {
    	this.servletContext = servletContextEvent.getServletContext();
        this.servletContext.setAttribute("ContextManager", this);
		Runtime.getRuntime().addShutdownHook(hook);
		app = new FeatherDB();
		String initWetaDFS = app.getProperty("start.weta.dfa");
		if(initWetaDFS!=null && initWetaDFS.equals("true")) {
				try { MetaDataServerRunner.dmetas.start(); } catch (Exception e) { }
				try { DataNodeServerRunner.dnodes.start(); } catch (Exception e) { }
		}
    	app.init();
	}

    public void contextDestroyed(ServletContextEvent evt) {
        servletContext.setAttribute("ContextManager", null);
        String initWetaDFS = app.getProperty("start.weta.dfa");
        app.shutdown();
		app.internalShutdown();
		if(initWetaDFS!=null && initWetaDFS.equals("true")) {
			try { DataNodeServerRunner.dn.stopServer(); } catch (Exception e) { }
			try { MetaDataServerRunner.mn.stopServer(); } catch (Exception e) { }
			try { DataNodeServerRunner.dnodes.stop(); } catch (Exception e) { }
			try { MetaDataServerRunner.dmetas.stop(); } catch (Exception e) { }
		}
		Runtime.getRuntime().removeShutdownHook(hook);
    }

    public void sessionCreated(final HttpSessionEvent httpSessionEvent) {
        activeSessions.add(httpSessionEvent.getSession());
    }

    public void sessionDestroyed(final HttpSessionEvent httpSessionEvent) {
        final HttpSession theSession = httpSessionEvent.getSession();
        activeSessions.remove(theSession);
    }

	public Set<HttpSession> getActiveSessions() {
	    return Collections.unmodifiableSet(activeSessions);
	}

}
