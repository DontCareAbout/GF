package us.dontcareabout.gxt.client.component;

import com.google.gwt.dom.client.Element;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridSelectionModel;
import com.sencha.gxt.widget.core.client.grid.GridView;
import com.sencha.gxt.widget.core.client.grid.RowExpander;

/**
 * 提供一個可由 caller 自行決定 store、column model 初始化時間點的 {@link Grid} 替代品。
 * <p>
 * 原本的 {@link Grid} 是透過 constructor 的參數讓 caller 設定 store 與 column model，
 * 這在某些場景下（例如需要 {@link RowExpander}）變得十分難以撰寫。
 * Grid2 則是藉由 {@link #init()} 取代原本的 {@link Grid#Grid(ListStore, ColumnModel, GridView)}，
 * 相關參數改由 {@link #genListStore()}、{@link #genColumnModel()}、{@link #genGridView()} 的回傳值決定。
 * 這讓 field 有時間點可建立 instance 而能在 {@link #genColumnModel()} 等處使用。
 * <p>
 * 例如：
 * <pre>
 * public class RowExpanderGrid extends Grid2<Foo> {
 * 	private RowExpander<Foo> rowExpander = new RowExpander<Foo>(
 * 		new AbstractCell<Foo>() {
 * 			public void render(Context context, Foo value, SafeHtmlBuilder sb) {
 * 				//skip
 * 			}
 * 		}
 * 	);
 *
 * 	public RowExpanderGrid() {
 * 		init();
 * 		rowExpander.initPlugin(this);
 * 	}
 *
 * 	protected ColumnModel<Foo> genColumnModel() {
 * 		ArrayList<ColumnConfig<Foo, ?>> list = new ArrayList<>();
 * 		list.add(rowExpander);
 * 		//skip
 * 		return new ColumnModel<>(list);
 * 	}
 * }
 * </pre>
 */
public abstract class Grid2<M> extends Grid<M> {
	protected Grid2() {}

	protected abstract ListStore<M> genListStore();
	protected abstract ColumnModel<M> genColumnModel();

	protected GridView<M> genGridView() {
		return new GridView<>();
	}

	//實作邏輯等同於 Grid(ListStore, ColumnModel, GridView)
	protected void init() {
		this.store = genListStore();
		this.cm = genColumnModel();
		this.view = genGridView();

		disabledStyle = null;
		setSelectionModel(new GridSelectionModel<M>());

		setAllowTextSelection(false);

		SafeHtmlBuilder builder = new SafeHtmlBuilder();
		view.getAppearance().render(builder);

		setElement((Element) XDOM.create(builder.toSafeHtml()));
		getElement().makePositionable();

		sinkCellEvents();
	}
}
