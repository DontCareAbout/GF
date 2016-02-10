package us.dontcareabout.gwt.client.websocket.event;

import com.google.gwt.event.shared.GwtEvent;

public class SendEvent extends GwtEvent<SendHandler>{
	public static final GwtEvent.Type<SendHandler> TYPE = new GwtEvent.Type<SendHandler>();

	private String message;

	public SendEvent(String msg){
		this.message = msg;
	}

	@Override
	public GwtEvent.Type<SendHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SendHandler handler) {
		handler.onSend(this);
	}

	public String getMessage() {
		return message;
	}
}