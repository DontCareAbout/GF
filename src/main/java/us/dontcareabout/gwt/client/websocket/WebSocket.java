package us.dontcareabout.gwt.client.websocket;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

import us.dontcareabout.gwt.client.websocket.event.CloseEvent;
import us.dontcareabout.gwt.client.websocket.event.CloseHandler;
import us.dontcareabout.gwt.client.websocket.event.ErrorEvent;
import us.dontcareabout.gwt.client.websocket.event.ErrorHandler;
import us.dontcareabout.gwt.client.websocket.event.MessageEvent;
import us.dontcareabout.gwt.client.websocket.event.MessageHandler;
import us.dontcareabout.gwt.client.websocket.event.OpenEvent;
import us.dontcareabout.gwt.client.websocket.event.OpenHandler;

public class WebSocket {
	public static native boolean isSupported() /*-{
		if ($wnd.WebSocket) {
			return true;
		} else {
			return false;
		}
	}-*/;

	private HandlerManager eventBus = new HandlerManager(null);
	private String uri;
	private JsWebSocket jsWebSocket;

	public WebSocket(String uri) {
		this.uri = uri;
	}

	/**
	 * 在 instance 建立之後、{@link #open()} 之前，
	 * 並未實際建立 WebSocket 連線，以 JS 的觀點理應是無法取得 readyState；
	 * 但 GF 將其視為中斷連線的狀態，所以仍然會回傳 {@link ReadyState#CLOSED} 值。
	 */
	public ReadyState getReadyState() {
		return ReadyState.valueOf(jsWebSocket.readyState());
	}

	public void send(String msg) {
		jsWebSocket.send(msg);
	}

	public HandlerRegistration addOpenHandler(OpenHandler h) {
		return eventBus.addHandler(OpenEvent.TYPE, h);
	}

	private void onOpen() {
		eventBus.fireEvent(new OpenEvent());
	}

	public HandlerRegistration addMessageHandler(MessageHandler h) {
		return eventBus.addHandler(MessageEvent.TYPE, h);
	}

	private void onMessage(String msg) {
		eventBus.fireEvent(new MessageEvent(msg));
	}

	public HandlerRegistration addErrorHandler(ErrorHandler h){
		return eventBus.addHandler(ErrorEvent.TYPE, h);
	}

	private void onError(SocketError se) {
		eventBus.fireEvent(new ErrorEvent(se));
	}

	public HandlerRegistration addCloseHandler(CloseHandler h){
		return eventBus.addHandler(CloseEvent.TYPE, h);
	}

	private void onClose() {
		eventBus.fireEvent(new CloseEvent());
	}

	public native void open() /*-{
		var websocket = new $wnd.WebSocket(this.@us.dontcareabout.gwt.client.websocket.WebSocket::uri);
		this.@us.dontcareabout.gwt.client.websocket.WebSocket::jsWebSocket = websocket;

		var self = this;
		websocket.onopen = function(event) {
			self.@us.dontcareabout.gwt.client.websocket.WebSocket::onOpen()();
		}
		websocket.onmessage = function(event) {
			self.@us.dontcareabout.gwt.client.websocket.WebSocket::onMessage(Ljava/lang/String;)(event.data);
		}
		websocket.onerror = function(event) {
			self.@us.dontcareabout.gwt.client.websocket.WebSocket::onError(Lus/dontcareabout/gwt/client/websocket/SocketError;)(event);
		}
		websocket.onclose = function(event) {
			self.@us.dontcareabout.gwt.client.websocket.WebSocket::onClose()();
		}
	}-*/;
}

class JsWebSocket extends JavaScriptObject {
	protected JsWebSocket() {}
	public native final void send(String msg) /*-{
		this.send(msg);
	}-*/;
	native final int readyState() /*-{
		//WebSocket 有可能在呼叫 open() 之前就作 getReadyState()
		//此時 this（WebSocket.jsWebSocket）是 null
		//設計上視為連線中斷，因此回傳 CLOSED 值。
		return this == null ? 3 : this.readyState;
	}-*/;
}