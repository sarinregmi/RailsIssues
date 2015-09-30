package com.example.issues;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class IssuesListAdapter extends BaseAdapter implements ListAdapter {

	private final Context mContext;
	private final LayoutInflater mInflater;
	private final List<IssueObject> mIssueItems;

	public IssuesListAdapter(Context context) {
		mContext = context;
		mIssueItems = new ArrayList<IssueObject>();
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return mIssueItems.size();
	}

	@Override
	public IssueObject getItem(int position) {
		return mIssueItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void updateIssueItems(List<IssueObject> issues)
	{
		mIssueItems.clear();
		mIssueItems.addAll(issues);
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null || convertView.getTag() == null) {
			convertView = mInflater.inflate(R.layout.issues_item, parent, false);

			// Save references to views so we don't have to look them up later
			ListViewHolder holder = new ListViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.body = (TextView) convertView.findViewById(R.id.body);
			convertView.setTag(holder);
		}

		// Bind data
		IssueObject issue = getItem(position);
		ListViewHolder holder = (ListViewHolder) convertView.getTag();
		holder.title.setText(issue.getTitle());
		holder.body.setText(issue.getBody());

		return convertView;
	}

	private static class ListViewHolder {
		public TextView title;
		public TextView body;
	}
}
