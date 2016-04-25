package us.dontcareabout.gwt.server;

import javax.servlet.http.HttpSession;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class GFServiceServlet extends RemoteServiceServlet {
	private static final long serialVersionUID = 1L;

	protected HttpSession getSession() {
		return getThreadLocalRequest().getSession();
	}

	protected String getSessionId() {
		return getSession().getId();
	}
}
