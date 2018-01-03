package us.dontcareabout.gxt.client.draw;

/* 目前是以 SVG 可接受字串為主
 * （畢竟 Canvas2D 的 setCursor() 實作還是一片空白 XD）
 * ref: https://developer.mozilla.org/zh-TW/docs/Web/SVG/Attribute/cursor
 *
 * funciri 沒有實驗成功，所以用跟 GWT 一樣採用 enum 形式。
 * 對，GWT 也有一個 Cursor enum，不過那是給 CSS 用的，
 * 有多一些 SVG 沒有的部份，所以忽略不使用。
 *
 * 有 Java 關鍵字（default）跟減號，所以得再弄個 getName()
 */
public enum Cursor {
	AUTO("auto"),
	CROSSHAIR("crosshair"),
	DEFAULT("default"),
	POINTER("pointer"),
	MOVE("move"),
	E_RESIZE("e-resize"),
	NE_RESIZE("ne-resize"),
	NW_RESIZE("nw-resize"),
	N_RESIZE("n-resize"),
	SE_RESIZE("se-resize"),
	SW_RESIZE("sw-resize"),
	S_RESIZE("s-resize"),
	W_RESIZE("w-resize"),
	TEXT("text"),
	WAIT("wait"),
	HELP("help");

	private String name;

	private Cursor(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
