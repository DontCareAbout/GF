package us.dontcareabout.gwt.client.websocket;

import com.google.gwt.core.client.JavaScriptObject;


public final class SocketError extends JavaScriptObject {
	protected SocketError() {}

	public final native String getData() /*-{
		return this.data;
  	}-*/;
}