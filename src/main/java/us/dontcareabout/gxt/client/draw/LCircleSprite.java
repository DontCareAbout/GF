package us.dontcareabout.gxt.client.draw;

import com.sencha.gxt.chart.client.draw.sprite.CircleSprite;

public class LCircleSprite extends CircleSprite implements LSprite {
	private Parameter parameter = new Parameter();
	private Layer layer;

	@Override
	public void setLayer(Layer layer) {
		this.layer = layer;
	}

	@Override
	public void setCenterX(double value) {
		if (parameter != null && parameter.lock) {
			throw new UnsupportedOperationException("Use setLX() instead.");
		}

		super.setCenterX(value);
	}

	@Override
	public void setLX(double value) {
		parameter.x = value;

		if (layer == null) { return; }

		parameter.lock = false;
		setCenterX(layer.getX() + parameter.x);
		parameter.lock = true;
	}

	@Override
	public double getLX() {
		return parameter.x;
	}

	@Override
	public void setCenterY(double value) {
		if (parameter != null && parameter.lock) {
			throw new UnsupportedOperationException("Use setLY() instead.");
		}

		super.setCenterY(value);
	}

	@Override
	public void setLY(double value) {
		parameter.y = value;

		if (layer == null) { return; }

		parameter.lock = false;
		setCenterY(layer.getY() + parameter.y);
		parameter.lock = true;
	}

	@Override
	public double getLY() {
		return parameter.y;
	}

	@Override
	public void setZIndex(int zIndex) {
		if (parameter != null && parameter.lock) {
			throw new UnsupportedOperationException("Use setLZIndex() instead.");
		}

		super.setZIndex(zIndex);
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
	public int getLZIndex() {
		return parameter.zIndex;
	}
}
