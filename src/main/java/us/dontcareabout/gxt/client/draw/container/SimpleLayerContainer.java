package us.dontcareabout.gxt.client.draw.container;

import us.dontcareabout.gxt.client.draw.LayerContainer;
import us.dontcareabout.gxt.client.draw.LayerSprite;

/**
 * 只能加入一個 {@link LayerSprite}，
 * 並且會讓該 {@link LayerSprite} 的大小與自身大小一樣。
 */
public class SimpleLayerContainer extends LayerContainer {
	private final LayerSprite layer;

	public SimpleLayerContainer(LayerSprite layer) {
		this.layer = layer;
		super.addLayer(layer);
	}

	@Override
	public void addLayer(LayerSprite layer) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void adjustMember(int width, int height) {
		layer.resize(width, height);
	}
}
