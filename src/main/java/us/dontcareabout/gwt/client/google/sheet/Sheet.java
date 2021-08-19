package us.dontcareabout.gwt.client.google.sheet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;

public class Sheet<T extends Row> {
	//因為 JavaScriptObject subclass 不能有 field
	//又希望 row 可以只 build 一次
	//所以另外包了一個標準 JavaScriptObject 的 instance 作 delegate
	private final SheetJS<T> gsjs;
	private final Validator<T> validator;
	private final HashMap<T, List<Throwable>> errorMap = new HashMap<>();

	private List<T> row;

	public Sheet(String json) {
		this(json, null);
	}

	public Sheet(String json, Validator<T> v) {
		gsjs = JsonUtils.safeEval(json);
		validator = v;
	}

	public String getRange() {
		return gsjs.getRange();
	}

	public String getMajorDimension() {
		return gsjs.getMajorDimension();
	}

	public List<T> getRows() {
		if (row != null) { return row; }

		row = new ArrayList<>();

		if (gsjs.getSize() == 0) { return row; }

		//沿襲舊版的 sheet 結構，對應到 v4 第一筆是欄位名稱
		String[] columns = gsjs.getValue(0);

		for (int i = 1; i < gsjs.getSize(); i++) {
			String[] value = gsjs.getValue(i);

			//跳過完全空白的 row
			if (value.length == 0) { continue; }

			T t = gsjs.getRow(i);

			//row 1 是 column name，row 2 才是真正的第一筆
			t.setIndex(i + 1);

			for (int i2 = 0; i2 < columns.length; i2++) {
				//假設 column 有 5 個，但是只有第 2 個 column 有值
				//則 value 會長這樣 ["", "", "blahblah"]
				//這裡一律幫補滿空字串以防萬一...
				t.set(columns[i2], value.length > i2 ? value[i2] : "");
			}

			if (validator == null) {
				row.add(t);
				continue;
			}

			List<Throwable> error = validator.validate(t);
			if (error == null || error.isEmpty()) {
				row.add(t);
			} else {
				errorMap.put(t, error);
			}
		}

		return row;
	}

	/**
	 * @return key 為沒有通過 {@link #validator} 的 instance
	 * 	（此時 {@link Row#getIndex()} 已經有值）；
	 * 	value 為 {@link Validator#validate(Row)} 的回傳結果。
	 */
	public HashMap<T, List<Throwable>> getErrors() {
		return errorMap;
	}

	static final class SheetJS<E extends Row> extends JavaScriptObject {
		protected SheetJS() {}

		native String getRange() /*-{
			return this.range;
		}-*/;

		native String getMajorDimension() /*-{
			return this.majorDimension;
		}-*/;

		native String[] getValue(int index) /*-{
			return this.values[index];
		}-*/;

		//對，跟 getValue() 其實一樣
		//純粹就是為了 GWT 沒有 reflection 招數
		//然後又要能簡單弄出一個 instance，所以... （反正 JS 嘛... [逃]）
		native E getRow(int index)  /*-{
			return this.values[index];
		}-*/;

		native int getSize() /*-{
			return this.values.length
		}-*/;
	}
}