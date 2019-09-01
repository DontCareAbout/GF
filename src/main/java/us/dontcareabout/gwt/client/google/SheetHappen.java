package us.dontcareabout.gwt.client.google;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;

public class SheetHappen {
	private static final String URL_HEAD = "https://spreadsheets.google.com/feeds/list/";
	private static final String URL_TAIL = "/public/values?alt=json";

	public static String url(String sheetId, int tabIndex) {
		return URL_HEAD + sheetId + "/" + tabIndex + URL_TAIL;
	}

	/**
	 * 取得指定試算表、第一個工作表的 JSON 字串。
	 *
	 * @see #get(String, int, Callback)
	 */
	public static <T extends SheetEntry> void get(final String sheetId, final Callback<T> callback) {
		get(sheetId, 1, callback);
	}

	/**
	 * 取得指定試算表、指定工作表的 JSON 字串。
	 * <p>
	 * <b>注意：</b>
	 * 如果試算表沒有作「發布到網路...」的動作，
	 * 雖然 console 會看到 error（CORS / CORB），但卻沒有炸任何 exception。
	 * 只有 {@link Response#getStatusCode()} 會得到 0。
	 * 此時還是設計成會觸發 {@link Callback#onError(Throwable)}。
	 *
	 * @param tabIndex 工作表在試算表中的順序，起始值為 1
	 */
	public static <T extends SheetEntry> void get(final String sheetId, int tabIndex, final Callback<T> callback) {
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url(sheetId, tabIndex));

		try {
			builder.sendRequest(null, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					if (response.getStatusCode() == 200) {
						callback.onSuccess(new Sheet<T>(response.getText()));
					} else {
						callback.onError(new Exception("Error response status code : " + response.getStatusCode()));
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					callback.onError(exception);
				}

			});
		} catch (RequestException e) {
			callback.onError(e);
		}
	}

	public static interface Callback<T extends SheetEntry> {
		void onSuccess(Sheet<T> gs);
		void onError(Throwable exception);
	}
}
