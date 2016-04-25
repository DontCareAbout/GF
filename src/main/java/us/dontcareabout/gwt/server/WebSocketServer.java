package us.dontcareabout.gwt.server;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import com.google.common.collect.HashBiMap;

/**
 * 特化過的 Sprint TextWebSocketHandler，需搭配 Spring MVC 使用。
 * 處理 <code>/websocket</code> 的 WebSocket URL
 * （相對於 Spring DispatcherServlet 的 url-pattern）。
 * <p>
 * 會在 WebSocket 連線建立時將 HTTP session 與 WebSocket session 作 binding，
 * 以供後續可依 HTTP session 作 {@link #unicast(String, String)} 與 {@link #multicast(List, String)}。
 * <p>
 * Servlet 中的範例碼：
 * <pre>
 * WebSocketServer wsServer = WebApplicationContextUtils
 * 	.getWebApplicationContext(this.getServletContext())
 * 	.getBean(WebSocketServer.class);
 * </pre>
 */
@EnableWebSocket
public class WebSocketServer extends TextWebSocketHandler implements WebSocketConfigurer {
	public static final String SESSION_ID_NAME = "SESSIONID";
	private static final List<String> ATTRIBUTE_NAMES = Arrays.asList(SESSION_ID_NAME);

	/**
	 * key 值是 {@link HttpSession#getId()}。
	 * <p>
	 * Note：其實 {@link WebSocketSession#getAttributes()} 會一直保留
	 * {@link HttpSession#getId()}，另外維護 {@link #sessionMap} 純粹是空間換取時間的考量。
	 *
	 * @see #registerWebSocketHandlers(WebSocketHandlerRegistry)
	 */
	private HashBiMap<String, WebSocketSession> sessionMap = HashBiMap.create();

	/**
	 * 有加上 {@link HttpSessionHandshakeInterceptor}，
	 * 因此在建立連線時會複製 {@link HttpSession} 的 attribute
	 * （attribute name 在 {@link #ATTRIBUTE_NAMES} 有註冊）
	 * 到 {@link WebSocketSession} 中。
	 */
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(this, "/websocket")	//Refactory magic number
			.addInterceptors(new HttpSessionHandshakeInterceptor(ATTRIBUTE_NAMES));
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		String sessionId = session.getAttributes().get(SESSION_ID_NAME).toString();
		sessionMap.put(sessionId, session);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		sessionMap.inverse().remove(session);
	}

	/**
	 * @return 目前連線數量
	 */
	public int getConnectionSize() {
		return sessionMap.size();
	}

	/**
	 * 對所有已知的 client 發送訊息
	 */
	public void broadcast(String message) throws IOException {
		for (WebSocketSession session : sessionMap.values()) {
			send(session, message);
		}
	}

	/**
	 * 對指定的 client 群發送訊息。
	 * @param sessionList {@link HttpSession#getId()} 的 list
	 */
	public void multicast(List<String> sessionList, String message) throws IOException {
		for (String httpSession : sessionList) {
			unicast(httpSession, message);
		}
	}

	/**
	 * @param sessionSet {@link HttpSession#getId()} 的 set
	 */
	public void multicast(Set<String> sessionSet, String message) throws IOException {
		for (String httpSession : sessionSet) {
			unicast(httpSession, message);
		}
	}

	/**
	 * 對指定的單一 client 發送訊息。
	 * @param httpSession {@link HttpSession#getId()}
	 */
	public void unicast(String httpSession, String message) throws IOException {
		send(sessionMap.get(httpSession), message);
	}

	private void send(WebSocketSession session, String message) throws IOException {
		session.sendMessage(new TextMessage(message));
	}
}