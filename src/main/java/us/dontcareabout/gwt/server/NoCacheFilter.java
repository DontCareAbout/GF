package com.dtc.autocare.gateway.server.api;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 由於 urlPatterns 無法進行詳細的 match 設定，所以必須在程式中再確認一次 URI 是 <b>.nocache.js</b> 結尾
 */
@WebFilter(urlPatterns="*.js")
public class NoCacheFilter implements Filter {
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		if (((HttpServletRequest)request).getRequestURI().endsWith(".nocache.js")) {
			HttpServletResponse resp = (HttpServletResponse) response;
			resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0, post-check=0, pre-check=0");
			resp.setHeader("Pragma", "no-cache");
		}

		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {}

	@Override
	public void destroy() {}
}
