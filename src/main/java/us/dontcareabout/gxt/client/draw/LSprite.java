package us.dontcareabout.gxt.client.draw;

import com.sencha.gxt.chart.client.draw.sprite.Sprite;

/**
 * 提供 {@link Layer} 一個獨立的 interface 以操作實際的 {@link Sprite}。
 */
//實作邏輯請參考 LTextSprite
interface LSprite {
	void setLayer(Layer layer);

	/**
	 * 設定相對於 Layer 原點的 X 值。
	 */
	void setLX(double value);

	/**
	 * 設定相對於 Layer 原點的 Y 值。
	 */
	void setLY(double value);

	/**
	 * 設定相對於 Layer 的 zIndex 值。
	 */
	void setLZIndex(int value);

	/**
	 * 取得相對於 Layer 原點的 X 值。
	 */
	double getLX();

	/**
	 * 取得相對於 Layer 原點的 Y 值。
	 */
	double getLY();

	/**
	 * 取得相對於 Layer 的 zIndex 值。
	 */
	int getLZIndex();
}
