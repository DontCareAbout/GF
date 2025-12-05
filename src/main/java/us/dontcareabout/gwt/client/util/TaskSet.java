package us.dontcareabout.gwt.client.util;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.shared.HandlerRegistration;
import com.sencha.gxt.core.shared.event.GroupingHandlerRegistration;

/**
 * 可收集一堆 sync task（{@link #addSyncTask(SyncTask)}）與
 * async task（{@link #addAsyncTask(Runnable, HandlerRegistration)}），然後依序執行。
 * task 全部做完會執行 {@link #addFinalTask(Runnable)} 的內容，
 * 並且移除相關 handler。
 * <p>
 * <pre>
	TaskSet ts = new TaskSet();	//有 async task 的話，必須宣告一個變數
	ts.addAsyncTask(
		() -> asyncMethd(),
		returnHandlerRegistration(e -> ts.check())	//handler 最後需要呼叫 ts.check()
	).addSyncTask(
		() -> foo()
	).addFinalTask(() -> doComplete())
	.start();
 * </pre>
 */
public class TaskSet {
	private List<Runnable> taskList = new ArrayList<>();
	private Runnable finalTask;
	private GroupingHandlerRegistration ghr = new GroupingHandlerRegistration();
	int taskCount;

	public TaskSet addSyncTask(SyncTask task) {
		taskList.add(task);
		return this;
	}

	/**
	 * <b>注意</b>：handler 最後必須呼叫 {@link TaskSet#check()}。
	 */
	public TaskSet addAsyncTask(Runnable task, HandlerRegistration handler) {
		taskList.add(task);
		ghr.add(handler);
		return this;
	}

	public void start() {
		taskList.stream().forEach(task -> {
			task.run();

			if (task instanceof SyncTask) {
				check();
			}
		});
	}

	public TaskSet addFinalTask(Runnable task) {
		finalTask = task;
		return this;
	}

	public void check() {
		taskCount++;
		if (taskCount != taskList.size()) { return; }
		ghr.removeHandler();

		if (finalTask == null) { return; }
		finalTask.run();
	}

	//為了讓 start() 可以判斷是不是 SyncTask 而被迫生出來的東西... Orz
	public interface SyncTask extends Runnable {}
}