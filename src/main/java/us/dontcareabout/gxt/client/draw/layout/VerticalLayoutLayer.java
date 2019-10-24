package us.dontcareabout.gxt.client.draw.layout;

import us.dontcareabout.gxt.client.draw.LayerSprite;

/**
 * 一個特化的 {@link LayerSprite}，透過 {@link #addChild(LayerSprite, double)} 加入的 {@link LayerSprite}，
 * 會依序由上到下排列，並以 weight 值決定其高度，
 * 寬度則統一為 {@link HorizontalLayoutLayer} 當下的寬度。
 */
public class VerticalLayoutLayer extends WeightLayoutLayer {
	private double viewSize;

	@Override
	protected final void adjustMember() {
		double fixed = 0;

		for (double w : weightList) {
			if (w > 1) { fixed += w; }
		}

		double remain = getHeight() - fixed - margins.getTop() - margins.getBottom()
				- (children.size() - 1) * gap;
		double y = margins.getTop();
		double x = margins.getLeft();
		double width = getWidth() - margins.getLeft() - margins.getRight();

		for (int i = 0; i < children.size(); i++) {
			LayerSprite child = children.get(i);
			double weight = weightList.get(i);
			double height = weight > 1 ? weight : remain * weight;
			child.setLX(x);
			child.setLY(y);
			child.resize(width, height);
			y += height + gap;
		}

		viewSize = y + margins.getBottom();
	}

	@Override
	public double getViewSize() {
		return viewSize;
	}
}
