package us.dontcareabout.gxt.client.util;

import com.google.common.base.Preconditions;
import com.sencha.gxt.chart.client.draw.Color;
import com.sencha.gxt.chart.client.draw.HSL;
import com.sencha.gxt.chart.client.draw.RGB;

public class ColorUtil {
	/**
	 * @param index 當值為 0～45 時，會回傳固定的顏色：
	 * 	當值超過 45 時，則每次回傳由亂數組成的顏色。
	 * @return 視覺上可辨認出不同的顏色
	 */
	public static RGB differential(int index) {
		Preconditions.checkArgument(index >= 0, "index must bigger than 0");

		if (index < 9) {
			return new RGB(new HSL(index * 2 * 20, 1, 0.5));
		}
		if (index < 18) {
			return new RGB(new HSL((index * 2 + 1) * 20, 1, 0.5));
		}
		////////
		if (index < 27) {
			return new RGB(new HSL(index * 2 * 20 + 10, 0.9, 0.7));
		}
		if (index < 36) {
			return new RGB(new HSL((index * 2 + 1) * 20 + 10, 0.9, 0.7));
		}
		////////
		if (index < 46) {
			//飽和變低、亮度變暗會讓顏色差異感變小，所以 H 的差別就大一點
			return new RGB(new HSL(index * 36, 0.8, 0.4));
		}

		//已經多到這種程度就無所謂了，都亂數就算了...... [眼神死]
		return new RGB(
			(int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255)
		);
	}

	/** @return 指定顏色應搭配黑色（明亮度高）或是白色（明亮度低） */
	public static Color blackOrWhite(String hexCode) {
		return blackOrWhite(new RGB(hexCode));
	}

	/** @return 指定顏色應搭配黑色（明亮度高）或是白色（明亮度低） */
	public static Color blackOrWhite(RGB rgb) {
		return new HSL(rgb).getLightness() > 0.55 ? RGB.BLACK : RGB.WHITE;
	}
}
