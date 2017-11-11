package us.dontcareabout.gxt.client.draw;

import com.sencha.gxt.chart.client.draw.DrawComponent;

/**
 * {@link LSprite} 共用的參數 set。
 */
class Parameter {
	/**
	 * 讓原本 sprite 的 setX() 等 method 無法被呼叫的 flag。
	 */
	boolean lock = true;

	/**
	 * 相對於 {@link Layer} 的 x 值，
	 * 也就是說 x + {@link Layer#getX()} 才是真正在 {@link DrawComponent} 上的值。
	 */
	double x;

	/**
	 * 相對於 {@link Layer} 的 y 值，
	 * 也就是說 y + {@link Layer#getY()} 才是真正在 {@link DrawComponent} 上的值。
	 */
	double y;

	/**
	 * 相對於 {@link Layer} 的 zIndex 值，
	 * 也就是說 zIndex + {@link Layer#getZIndex()()} 才是真正在 {@link DrawComponent} 上的值。
	 */
	int zIndex;
}
