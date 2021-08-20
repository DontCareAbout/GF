package us.dontcareabout.gwt.client.google.sheet;

import java.util.Arrays;
import java.util.List;

public interface Validator<T extends Row> {
	/** 單純表示「沒有通過 validate」的空白便利回傳值。 */
	List<Throwable> DUMMY_FAIL = Arrays.asList(new Throwable());

	/**
	 * @return 若通過 validate，則可回傳 null 或是空的 {@link List}。
	 * @see #DUMMY_FAIL
	 */
	List<Throwable> validate(T entry);
}
