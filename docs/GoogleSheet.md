SheetDto
========

提供一個將 Google Sheet 資料轉為指定 class 的工具。


GWT Module
----------

`<inherits name="us.dontcareabout.gwt.GWT" />`


前置準備
--------

+ [申請一個 API 金鑰][Cloud Console]，以下稱為 `KEY`。
+ 從 sheet URL「https://docs.google.com/spreadsheets/d/SHEET_ID/edit 」中
	擷取出 sheet 的 ID，以下稱為 `SHEET_ID`。


假設 sheet 有一個工作表，名稱為 `TAB_NAME`。
第一個 row 的值為 column name，依序為「"date", "score", "note"」。
第二個以後的 row 為資料。

[Cloud Console]: https://console.cloud.google.com/apis/credentials


使用方式
--------

定義一個繼承 `Row` 的 class，例如：

```Java
public final class Foo extends Row {
	protected Foo() {}
	
	public Date getDate() { return dateField("date"); }
	public int getScore() { return intField("score"); }
	public String getNote() { return stringField("note"); }
}
```


new 一個 `SheetDto` 的 instance，
使用 fluent API 設定參數，然後呼叫 `fetch()`：

```Java
SheetDto<Foo> fooSheet = new SheetDto<Foo>()
	.key(KEY).sheetId(SHEET_ID).tabName(TAB_NAME)
	//如果需要過濾不合格資料，可以設定 validator
	//.validator()
	.fetch(new Callback<Foo>() {
		@Override
		public void onSuccess(Sheet<Foo> gs) {
			List<Foo> data = gs.getRows();
		}
		
		@Override
		public void onError(Throwable exception) {
		
		}
	});
```
