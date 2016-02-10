package us.dontcareabout.gwt.client.websocket.event;

import com.google.gwt.event.shared.EventHandler;

public interface SendHandler extends EventHandler{
	void onSend(SendEvent e);
}