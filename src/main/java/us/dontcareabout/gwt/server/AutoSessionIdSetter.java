package us.dontcareabout.gwt.server;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 為了 {@link WebSocketServer} 在 hand shake 時能取得 {@link HttpSession#getId()}，
 * 因此用 {@link Filter} 在處理 request 時塞入 sessionId。
 */
@WebFilter(filterName="setIdFilter", urlPatterns="*")
public class AutoSessionIdSetter implements Filter {
	@Override
	public void init(FilterConfig arg0) throws ServletException {}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpSession session = ((HttpServletRequest) req).getSession();
		session.setAttribute(WebSocketServer.SESSION_ID_NAME, session.getId());
		chain.doFilter(req, res);
	}

	@Override
	public void destroy() {}
}