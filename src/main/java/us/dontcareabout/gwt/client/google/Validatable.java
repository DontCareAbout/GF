package us.dontcareabout.gwt.client.google;

import java.util.List;

/**
 * {@link Sheet} 會檢查對應的 {@link SheetEntry} 是否有 implement {@link Validatable}。
 * 若有，則會執行 {@link #validate()}，通過的才會出現在 {@link Sheet#getEntry()} 當中。
 */
@Deprecated
public interface Validatable {
	/**
	 * @return 對該 instance 作 validate，若通過 validate，則回傳 null 或是空的 {@link List}。
	 */
	List<Throwable> validate();
}
