package us.dontcareabout.gxt.client.draw;

import com.sencha.gxt.chart.client.draw.sprite.TextSprite;

/**
 * {@link Layer} 專用的 {@link TextSprite}。
 */
//因為邏輯都一樣，所以註解只寫 x 那一組。
public class LTextSprite extends TextSprite implements LSprite{
	private Parameter parameter = new Parameter();
	private Layer layer;

	@Override
	public void setLayer(Layer layer) {
		this.layer = layer;
	}

	@Override
	public void setX(double value) {
		//不讓 caller 有自己決定實際 x 值的能力
		//所以透過 parameter.lock 來把關
		//有可能在 parent 的 constructor 當中就呼叫過（setZIndex() 必定會）
		//所以必須先阻攔 parameter 還沒有 instance 的狀況
		if (parameter != null && parameter.lock) {
			throw new UnsupportedOperationException("Use setLX() instead.");
		}

		super.setX(value);
	}

	@Override
	public void setLX(double value) {
		parameter.x = value;

		//允許 caller 在還沒作 Layer.add() 前就設定 setLX()
		if (layer == null) { return; }

		parameter.lock = true;
		setX(layer.getX() + parameter.x);
		parameter.lock = false;
	}

	@Override
	public double getLX() {
		return parameter.x;
	}

	@Override
	public void setY(double value) {
		if (parameter != null && parameter.lock) {
			throw new UnsupportedOperationException("Use setLY() instead.");
		}

		super.setY(value);
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
