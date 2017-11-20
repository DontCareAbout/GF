package us.dontcareabout.gxt.client.draw;

/**
 * 特化版的 {@link Layer}。
 * 使其可以如 {@link LTextSprite} 等 sprite 一樣加在 {@link Layer} 上。
 */
public class LayerSprite extends Layer implements LSprite {
	private Parameter parameter = new Parameter();
	private Layer layer;

	@Override
	public void setLayer(Layer layer) {
		this.layer = layer;
	}

	@Override
	public void setLX(double value) {
		parameter.x = value;

		if (layer == null) { return; }

		parameter.lock = false;
		setX(layer.getX() + parameter.x);
		parameter.lock = true;
	}

	@Override
	public void setLY(double value) {
		parameter.y = value;

		if (layer == null) { return; }

		parameter.lock = false;
		setY(layer.getY() + parameter.y);
		parameter.lock = true;
	}

	@Override
	public void setLZIndex(int value) {
		parameter.zIndex = value;

		if (layer == null) { return; }

		parameter.lock = false;
		setZIndex(layer.getZIndex() + parameter.zIndex);
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
}
