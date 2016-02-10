package us.dontcareabout.gwt.client.websocket.event;

import us.dontcareabout.gwt.client.websocket.SocketError;

import com.google.gwt.event.shared.GwtEvent;

public class ErrorEvent extends GwtEvent<ErrorHandler>{
	public static final GwtEvent.Type<ErrorHandler> TYPE = new GwtEvent.Type<ErrorHandler>();

	private SocketError err;

	public ErrorEvent(SocketError err){
		this.err = err;
	}

	@Override
	public GwtEvent.Type<ErrorHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ErrorHandler handler) {
		handler.onError(this);
	}

	public String getData(){
		return err.getData();
	}
}
