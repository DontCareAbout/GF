package us.dontcareabout.gwt.client.google.sheet;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;

/**
 * 讀取 sheet 並將每一行轉成特定 object（{@link Row}）。
 */
public class SheetDto {
	public static String url(String sheetId, String range, String key) {
		return "https://sheets.googleapis.com/v4/spreadsheets/"+ sheetId + "/values/" + range + "?key=" + key;
	}

	public static <T extends Row> void get(String key, String sheetId, String range, Callback<T> callback) {
		get(key, sheetId, range, null, callback);
	}

	public static <T extends Row> void get(String key, String sheetId, String range, Validator<T> validator, Callback<T> callback) {
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url(sheetId, range, key));

		try {
			builder.sendRequest(null, new RequestCallback() {
				@Override
				public void onResponseReceived(Request request, Response response) {
					if (response.getStatusCode() == 200) {
						callback.onSuccess(new Sheet<T>(response.getText(), validator));
					} else {
						//status code 有 memo 在 Callback.onError()
						//目前沒打算做進一步細分... [逃]
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

	public static interface Callback<T extends Row> {
		void onSuccess(Sheet<T> gs);

		/**
		 * status code 原因參考：
		 * <ul>
		 * 	<li>400：range 無法正常解析</li>
		 * 	<li>403：key 值不正確</li>
		 * 	<li>404：sheetId 不正確</li>
		 * </ul>
		 */
		void onError(Throwable exception);
	}
}
