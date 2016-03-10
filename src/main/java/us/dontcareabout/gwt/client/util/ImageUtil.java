package us.dontcareabout.gwt.client.util;

import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.ui.Image;

public class ImageUtil {
	/** 將一個 {@link Image} 轉成 {@link ImageResource} */
	public static ImageResource toResource(final Image image) {
		return new ImageResource() {
			@Override
			public int getHeight() {
				return image.getHeight();
			}

			@Override
			public SafeUri getSafeUri() {
				return UriUtils.fromSafeConstant(image.getUrl());
			}

			@Deprecated
			@Override
			public String getURL() {
				return image.getUrl();
			}

			@Override
			public int getWidth() {
				return image.getWidth();
			}

			// ==== Don't Care 區 ==== //
			@Override public String getName() { return null; }
			@Override public int getLeft() { return 0; }
			@Override public int getTop() { return 0; }
			@Override public boolean isAnimated() { return false; }
		};
	}

	public static ImageElement asElement(Image image) {
		return ImageElement.as(image.getElement());
	}
}