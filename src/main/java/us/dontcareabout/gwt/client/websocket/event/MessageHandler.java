package us.dontcareabout.gwt.client.websocket.event;

import com.google.gwt.event.shared.EventHandler;

public interface MessageHandler extends EventHandler{
	void onMessage(MessageEvent e);
}