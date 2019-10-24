package us.dontcareabout.gxt.client.draw.layout;

import us.dontcareabout.gxt.client.draw.LayerSprite;

/**
 * 一個特化的 {@link LayerSprite}，透過 {@link #addChild(LayerSprite, double)} 加入的 {@link LayerSprite}，
 * 會依序由左到右排列，並以 weight 值決定其寬度，
 * 高度則統一為 {@link HorizontalLayoutLayer} 當下的高度。
 */
public class HorizontalLayoutLayer extends WeightLayoutLayer {
	private double viewSize;

	@Override
	protected final void adjustMember() {
		double fixed = 0;

		for (double w : weightList) {
			if (w > 1) { fixed += w; }
		}

		double remain = getWidth() - fixed - margins.getLeft() - margins.getRight()
				- (children.size() - 1) * gap;
		double y = margins.getTop();
		double x = margins.getLeft();
		double height = getHeight() - margins.getTop() - margins.getBottom();

		for (int i = 0; i < children.size(); i++) {
			LayerSprite child = children.get(i);
			double weight = weightList.get(i);
			double width = weight > 1 ? weight : remain * weight;
			child.setLX(x);
			child.setLY(y);
			child.resize(width, height);
			x += width + gap;
		}

		viewSize = x + margins.getRight();
	}

	@Override
	public double getViewSize() {
		return viewSize;
	}
}
