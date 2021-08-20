package us.dontcareabout.gwt.client.google;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;

@Deprecated
public class Sheet<T extends SheetEntry> {
	//因為 JavaScriptObject subclass 不能有 field
	//又希望 entry 可以只 build 一次
	//所以另外包了一個標準 JavaScriptObject 的 instance 作 delegate
	private SheetJS<T> gsjs;
	private ArrayList<T> entry;
	private final HashMap<T, List<Throwable>> errorMap = new HashMap<>();

	public Sheet(String json) {
		gsjs = JsonUtils.safeEval(json);
	}

	public String getTitle() {
		return gsjs.getTitle();
	}

	public Date getUpdated() {
		return gsjs.getUpdated();
	}

	/**
	 * @see Validatable
	 */
	public ArrayList<T> getEntry() {
		if (entry == null) {
			entry = new ArrayList<>();
			JsArray<T> entryJS = gsjs.getEntry();

			if (entryJS == null || entryJS.length() == 0) { return entry; }

			//謠傳 instanceof 很耗效能（在 GWT 不確定），但是泛型又不能直接拿來作 instanceof
			//所以用一個 flag 節省時間
			//前面已經確保 length > 0 了，可以放心取
			boolean needValidate = 	entryJS.get(0) instanceof Validatable;

			for (int i = 0; i < entryJS.length(); i++) {
				T t = entryJS.get(i);
				//row 1 是 column name，row 2 才是真正的第一筆
				//也因此改為在這裡就加上 offset，而不是在 SheetEntry.setIndex() 裡頭作
				t.setIndex(i + 2);

				if (!needValidate) {
					entry.add(t);
					continue;
				}

				List<Throwable> error = ((Validatable) t).validate();
				if (error == null || error.isEmpty()) {
					entry.add(t);
				} else {
					errorMap.put(t, error);
				}
			}
		}

		return entry;
	}

	public HashMap<T, List<Throwable>> getErrors() {
		return errorMap;
	}

	static final class SheetJS<E extends SheetEntry> extends JavaScriptObject {
		protected SheetJS() {}

		Date getUpdated() {
			return DateTimeFormat.getFormat(PredefinedFormat.ISO_8601).parse(getUpdatedString());
		}

		private native String getTitle() /*-{
			return this.feed.title.$t;
		}-*/;

		private native String getUpdatedString() /*-{
			return this.feed.updated.$t;
		}-*/;

		native JsArray<E> getEntry() /*-{
			return this.feed.entry;
		}-*/;
	}
}