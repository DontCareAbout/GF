package us.dontcareabout.gxt.client.draw;

import com.sencha.gxt.chart.client.draw.Color;
import com.sencha.gxt.chart.client.draw.DrawComponent;

/**
 * 特化版的 {@link Layer}。
 * 使其可以如 {@link LTextSprite} 等 sprite 一樣加在 {@link Layer} 上。
 * <p>
 * 有別於單純作為 sprite 集合的 {@link Layer}，
 * LayerSprite 內建一個 {@link LRectangleSprite} 作為 background，
 * 因此有實際的大小與可視範圍（但是並沒有防止 member sprite 超出範圍 XD）。
 * caller 可在 {@link DrawComponent} onResize() 時
 * 呼叫 {@link #resize(double, double)} 來調整 LayerSprite 的大小，
 * 並 override {@link #adjustMember()} 以實作各 member sprite 的對應調整。
 * <p>
 * 如果要調整 background 的樣式，請使用 setBg*() 系列 method，
 * 例如 {@link #setBgRadius(double)}。
 */
public class LayerSprite extends Layer implements LSprite {
	private Parameter parameter = new Parameter();
	private Layer layer;
	private LRectangleSprite bg = new LRectangleSprite();
	private Cursor cursor;

	public LayerSprite() {
		add(bg);

		bg.setFill(Color.NONE);
	}

	public final void resize(double width, double height) {
		if (width < 0 || height < 0) { return; }

		bg.setWidth(width);
		bg.setHeight(height);
		adjustMember();
	}

	public double getWidth() {
		return bg.getWidth();
	}

	public double getHeight() {
		return bg.getHeight();
	}

	public void setBgColor(Color color) {
		bg.setFill(color);
	}

	public Color getBgColor() {
		return bg.getFill();
	}

	public void setBgOpacity(double opacity) {
		bg.setOpacity(opacity);
	}

	public double getBgOpacity() {
		return bg.getOpacity();
	}

	public void setBgRadius(double radius) {
		bg.setRadius(radius);
	}

	public double getBgRadius() {
		return bg.getRadius();
	}

	@Override
	public void setLayer(Layer layer) {
		this.layer = layer;
	}

	@Override
	public void setLX(double value) {
		parameter.x = value;
		parameter.lock = false;
		setX((layer == null ? 0 : layer.getX()) + parameter.x);
		parameter.lock = true;
	}

	@Override
	public void setLY(double value) {
		parameter.y = value;
		parameter.lock = false;
		setY((layer == null ? 0 : layer.getY()) + parameter.y);
		parameter.lock = true;
	}

	@Override
	public void setLZIndex(int value) {
		parameter.zIndex = value;
		parameter.lock = false;
		setZIndex((layer == null ? 0 : layer.getZIndex()) + parameter.zIndex);
		parameter.lock = true;
	}

	@Override
	public double getLX() {
		return parameter.x;
	}

	@Override
	public double getLY() {
		return parameter.y;
	}

	@Override
	public int getLZIndex() {
		return parameter.zIndex;
	}

	@Override
	public void setX(double value) {
		//不會有 parent class 呼叫 setX() 的問題，所以只判斷 lock
		if (parameter.lock) {
			throw new UnsupportedOperationException("Use setLX() instead.");
		}

		super.setX(value);
	}

	@Override
	public void setY(double value) {
		if (parameter.lock) {
			throw new UnsupportedOperationException("Use setLY() instead.");
		}

		super.setY(value);
	}

	@Override
	public void setZIndex(int value) {
		if (parameter.lock) {
			throw new UnsupportedOperationException("Use setLZIndex() instead.");
		}

		super.setZIndex(value);
	}

	@Override
	public void setCursor(Cursor cursor) {
		this.cursor = cursor;

		if (bg.getSurface() != null) {
			bg.setCursor(cursor == null ? (String)null : cursor.getName());
		}
	}

	@Override
	public Cursor getCursor() {
		return cursor;
	}

	/**
	 * 在 {@link #resize(double, double)} 時提供 child class 調整 member sprite 的時機點。
	 */
	protected void adjustMember() {}
}
