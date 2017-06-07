package base;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class HBaseAdapter<T> extends BaseAdapter {
	protected Context context;
	protected List<T> mList; // 数据

	public HBaseAdapter(Context context) {
		this.context = context;
		this.mList = new ArrayList<T>();
	}

	public abstract int getContentView();

	/**
	 * 璁剧疆鏁版嵁
	 */
	public void setData(List<T> data) {
		if (data != null) {
			this.mList.clear();
			this.mList.addAll(data);
			notifyDataSetChanged();
		}
	}

	/**
	 * 鍦ㄥ師鏈夋暟鎹殑鍩虹涓婂啀娣诲姞鏁版嵁
	 */
	public void addMoreByData(List<T> data) {
		if (data != null) {
			this.mList.addAll(data);
			notifyDataSetChanged();
		}
	}

	/**
	 * 娓呴櫎鏁版嵁
	 */
	public void clearData() {
		this.mList.clear();
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mList == null ? 0 : mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList == null ? null : mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = ViewHolder.getInstance(context, convertView,
				getContentView());
		convert(holder, mList.get(position), position); // 子类重写的方法，完成holder中view的初始化
		return holder.getConvertView();
	}

	/**
	 * 子类必须重写的方法，通过这个方法初始化holder中的View即可
	 */
	public abstract void convert(ViewHolder holder, T t, int position);

	protected static class ViewHolder {
		public View convertView;
		private SparseArray<View> views;

		private ViewHolder(View convertView) {
			this.views = new SparseArray<View>();
			this.convertView = convertView;
			convertView.setTag(this);
		}

		public static ViewHolder getInstance(Context context, View convertView,
				int layout) {
			if (convertView == null) {
				convertView = LayoutInflater.from(context)
						.inflate(layout, null);
				return new ViewHolder(convertView);
			}
			return (ViewHolder) convertView.getTag(); // 重用convertView
		}

		@SuppressWarnings("unchecked")
		public <T extends View> T findViewById(int id) {
			View view = views.get(id); // 同id的控件可重复使用，无需再次findViewById
			if (view == null) {
				view = convertView.findViewById(id);
				views.append(id, view); // 将此id的控件添加进sparseArray
			}
			return (T) view;
		}

		private View getConvertView() {
			return convertView;
		}
	}
}
