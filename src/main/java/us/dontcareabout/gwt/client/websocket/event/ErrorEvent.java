package us.dontcareabout.gwt.client.websocket.event;

import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.event.shared.GwtEvent;

import us.dontcareabout.gwt.client.websocket.SocketError;

public class ErrorEvent extends GwtEvent<ErrorHandler>{
	public static final GwtEvent.Type<ErrorHandler> TYPE = new GwtEvent.Type<ErrorHandler>();

	/**
	 * 若為 true，表示此 instance 是由 {@link JavaScriptException} 而引發，
	 * 此時 {@link #error} 會是 null。以此類推 false。
	 */
	public final boolean byException;
	public final SocketError error;
	public final JavaScriptException exception;

	public ErrorEvent(SocketError err){
		byException = false;
		this.error = err;
		this.exception = null;
	}

	public ErrorEvent(JavaScriptException jse) {
		byException = true;
		this.error = null;
		this.exception = jse;
	}

	@Override
	public GwtEvent.Type<ErrorHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ErrorHandler handler) {
		handler.onError(this);
	}
}
