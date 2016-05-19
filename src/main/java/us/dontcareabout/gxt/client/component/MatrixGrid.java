package us.dontcareabout.gxt.client.component;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

import us.dontcareabout.gxt.client.model.GetValueProvider;

/**
 * 使用 {@link Grid} 來呈現一個字串矩陣的資料。
 * <p>
 * 在 constructor 時決定 column 的 數量、寬度與 header 的文字。
 * 字串矩陣以 {@code List<List<String>>} 儲存，可透過 {@link #setData(List)} 設定、
 * 或是使用 {@link #addData(List)} 增加一個 tuple。
 * 若 tuple 的 element 個數小於 column 的數量，則會以空字串填補。
 */
public class MatrixGrid extends Grid<List<String>> {
	private static final ModelKeyProvider<List<String>> keyProvider = new ModelKeyProvider<List<String>>() {
		@Override
		public String getKey(List<String> item) {
			return item.toString();
		}
	};

	/**
	 * 只決定每個 column 的 header 文字，每個 column 會有相同寬度。
	 */
	public MatrixGrid(List<String> header) {
		this(header, null);
	}

	/**
	 * 除了決定每個 column 的 header 文字外，也決定每個 column 的寬度。
	 * 若給定的寬度數量不足，會以最後一個寬度填補。
	 */
	public MatrixGrid(List<String> header, List<Integer> width) {
		super(new ListStore<>(keyProvider), createColumnModel(header, width));
		this.getView().setAutoFill(true);
	}

	public void setData(List<List<String>> matrix) {
		getStore().clear();
		getStore().addAll(matrix);
	}

	public void addData(List<String> tuple) {
		getStore().add(tuple);
	}

	private static ColumnModel<List<String>> createColumnModel(List<String> header, List<Integer> width) {
		Preconditions.checkNotNull(header);
		Preconditions.checkArgument(!header.isEmpty());

		if (width == null || width.isEmpty()) {
			width = new ArrayList<>(header.size());

			for (int i = 0; i < header.size(); i++) {
				width.add(1);
			}
		} else {
			int last = width.get(width.size() - 1);
			width = new ArrayList<>(width);

			for (int i = width.size(); i < header.size(); i++) {
				width.add(last);
			}
		}

		List<ColumnConfig<List<String>, ?>> columns = new ArrayList<>();

		for (int i = 0; i < header.size(); i++) {
			columns.add(new ColumnConfig<>(new VP(i), width.get(i), header.get(i)));
		}

		return new ColumnModel<List<String>>(columns);
	}

	private static class VP extends GetValueProvider<List<String>, String> {
		final int index;

		VP(int i) { index = i; }

		@Override
		public String getValue(List<String> object) {
			return index >= object.size() ? "" : object.get(index);
		}
	}
}
