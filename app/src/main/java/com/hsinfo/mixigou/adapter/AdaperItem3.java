package com.hsinfo.mixigou.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hsinfo.mixigou.R;
import com.hsinfo.mixigou.item.FinishPeddingItem;

import java.util.ArrayList;
import java.util.List;

// 档案管理用，行高度窄一些
public class AdaperItem3 extends BaseAdapter
{
	private List<FinishPeddingItem> listItems =new ArrayList<FinishPeddingItem>();
	private LayoutInflater inflater;
	private ListView mListView;

	public AdaperItem3(Context context, List<FinishPeddingItem> list)
	{
	   inflater=LayoutInflater.from(context);
	   this.listItems=list;
	}

	public void setListView(ListView listv)
	{
		this.mListView = listv;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		int items = 0;
		if(listItems!=null){
			items =listItems.size();
		}
		return items;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if(convertView==null)
		{
			convertView=inflater.inflate(R.layout.homelist_item3, parent, false);
			holder=new ViewHolder();
			holder.iv_icon1=(ImageView) convertView.findViewById(R.id.iv_icon1);
			holder.tv_title=(TextView) convertView.findViewById(R.id.tv_title);
			holder.tv_date=(TextView) convertView.findViewById(R.id.tv_title2);
			
			convertView.setTag(holder);	
		}
		else {
			holder=(ViewHolder) convertView.getTag();
		}
		FinishPeddingItem listItem=listItems.get(position);
		holder.iv_icon1.setImageResource(listItem.getIv_icon1());
		holder.tv_title.setText(listItem.getTv_title());
		holder.tv_date.setText(listItem.getTv_date());
		
		return convertView;
	}

	public void updateItem(int index,String strname )
	{
		if (mListView == null)
		{
			return;
		}

		// 获取当前可以看到的item位置
		int visiblePosition = mListView.getFirstVisiblePosition();

		// 获取点击的view， 或者该行的TextView
		View view = mListView.getChildAt(index - visiblePosition);
		TextView tv_count = (TextView) view.findViewById(R.id.tv_title2);

		// 重新设置界面显示数据
		tv_count.setText( strname );

		// 测试内容
		FinishPeddingItem data = (FinishPeddingItem) getItem(index);
		String name = data.getTv_date();
	}

	private class ViewHolder {
		ImageView iv_icon1;
		TextView tv_title;
		TextView tv_date;
	}
}
