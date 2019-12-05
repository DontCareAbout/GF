package us.dontcareabout.gwt.client.websocket;

import com.google.gwt.core.client.JavaScriptObject;


public final class SocketError extends JavaScriptObject {
	protected SocketError() {}

	//雖然網路上看到的範例都是取 event.data（也有一個是 event.message）
	//但小範圍測試下都沒看到這些 field（一方面是也不知道怎麼弄出 error... Orz）
	//因此改為這種「只要知道名字，想取啥都可以」的 method
	public final native String getField(String name) /*-{
		return this[name];
	}-*/;
}