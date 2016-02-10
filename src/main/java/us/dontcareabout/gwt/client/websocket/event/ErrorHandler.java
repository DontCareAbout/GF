package us.dontcareabout.gwt.client.websocket.event;

import com.google.gwt.event.shared.EventHandler;

public interface ErrorHandler extends EventHandler{
	void onError(ErrorEvent e);
}