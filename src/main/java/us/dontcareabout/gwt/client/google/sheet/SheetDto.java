package us.dontcareabout.gwt.client.google.sheet;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;

/**
 * 讀取 sheet 並將每一列轉成特定 object（{@link Row}）。
 * 除了提供 {@link #get(String, String, String, Validator, Callback)} 外，
 * 也可透過 fluent API 設定參數然後呼叫 {@link #fetch(Callback)}。
 * <p>
 * 若有指定 {@link Validator}，則只有通過 validate 的資料才會出現在 {@link Sheet#getRows()} 中。
 * <p>
 * 注意：取回資料的第一列的值會視為 column 值，對應到 {@link Row} 的 *Field() 的傳入值。
 */
public class SheetDto<T extends Row> {
	private String sheetId;
	private String key;
	private String tabName;
	private Validator<T> validator;

	public String getSheetId() {
		return sheetId;
	}

	public SheetDto<T> sheetId(String sheetId) {
		this.sheetId = sheetId;
		return this;
	}

	public String getKey() {
		return key;
	}

	public SheetDto<T> key(String key) {
		this.key = key;
		return this;
	}

	public String getTabName() {
		return tabName;
	}

	public SheetDto<T> tabName(String tabName) {
		this.tabName = tabName;
		return this;
	}

	public Validator<T> getValidator() {
		return validator;
	}

	public SheetDto<T> validator(Validator<T> validator) {
		this.validator = validator;
		return this;
	}

	public void fetch(Callback<T> callback) {
		get(key, sheetId, tabName, validator, callback);
	}

	////////////////

	public static String url(String sheetId, String range, String key) {
		return "https://sheets.googleapis.com/v4/spreadsheets/"+ sheetId + "/values/" + range + "?key=" + key;
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

	/**
	 * {@link #onError(Throwable)} 的 status code 原因參考：
	 * <ul>
	 * 	<li>400：range（tabName）無法正常解析</li>
	 * 	<li>403：key 值不正確、sheet 的「共用」沒有設定為「知道連結的使用者」</li>
	 * 	<li>404：sheetId 不正確</li>
	 * </ul>
	 */
	public static interface Callback<T extends Row> extends us.dontcareabout.gwt.client.data.Callback<Sheet<T>>{}
}
