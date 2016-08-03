package us.dontcareabout.gxt.client.util;

import com.sencha.gxt.widget.core.client.form.ValueBaseField;

public class ValueBaseFieldUtil {
	public static <T> void setMaxLength(ValueBaseField<T> field, int length) {
		field.getCell().getAppearance().getInputElement(field.getElement())
			.setAttribute("maxLength", "" + length);
	}
}
