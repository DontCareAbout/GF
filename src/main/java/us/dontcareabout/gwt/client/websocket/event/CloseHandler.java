package us.dontcareabout.gwt.client.websocket.event;

import com.google.gwt.event.shared.EventHandler;

public interface CloseHandler extends EventHandler{
	void onClose(CloseEvent e);
}