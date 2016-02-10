package us.dontcareabout.gwt.client.websocket.event;

import com.google.gwt.event.shared.EventHandler;

public interface OpenHandler extends EventHandler{
	void onOpen(OpenEvent e);
}