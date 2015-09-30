package com.example.issues;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class IssuesActivity extends FragmentActivity implements NetworkTaskFragment.TaskCallbacks {

	private ListView mIssuesListView;
	private IssuesListAdapter mIssuesAdapter;
	private NetworkTaskFragment mTaskFragment;

	private static final String TAG_TASK_FRAGMENT = "task_fragment";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.issues_activity);
		mIssuesAdapter = new IssuesListAdapter(this);
		mIssuesListView = (ListView) findViewById(R.id.issuesList);
		mIssuesListView.setAdapter(mIssuesAdapter);
		mIssuesListView.setOnItemClickListener(mOnItemClickListener);

		FragmentManager fm = getSupportFragmentManager();
		mTaskFragment = (NetworkTaskFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);

		if (mTaskFragment == null) {
			mTaskFragment = new NetworkTaskFragment();
			fm.beginTransaction().add(mTaskFragment, TAG_TASK_FRAGMENT).commit();
		}

		if(!mTaskFragment.isRunning()) {
			mTaskFragment.start();
		}

	}

	@Override
	public void onPostExecute(List<IssueObject> output) {
		mIssuesAdapter.updateIssueItems(output);

	}


	private ListView.OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			IssueObject clickedItem = mIssuesAdapter.getItem(position);
			String url = clickedItem.getCommentsUrl();

			final Dialog dialog = new Dialog(IssuesActivity.this);
			dialog.setContentView(R.layout.comments_dialog);
			dialog.setTitle(R.string.comments_title); // can be read from strings.xml
			dialog.show();

			new AsyncTask<String, Void, String>() {
				@Override
				protected String doInBackground(String... url) {
					StringBuilder sb = new StringBuilder();
					try {
						String message = NetworkTaskFragment.getJSONResponse(url[0]);
						JSONArray jsonArray = new JSONArray(message);
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonObject = jsonArray.getJSONObject(i);
							final String author = jsonObject.getJSONObject("user").getString("login");
							final String comment = jsonObject.getString("body");
							sb.append(author + "\n");
							sb.append(comment + "\n");
							sb.append("----------------------------------------- \n");
						}
					} catch (JSONException e) {
						e.printStackTrace();
						String result = "Error fetching comments";
						return result;
					}
					return sb.toString();
				}

				protected void onPostExecute(String result) {
					 if(!result.isEmpty()) {
						 ((TextView) dialog.findViewById(R.id.comments)).setText(result);
					 } else {
						 ((TextView) dialog.findViewById(R.id.comments)).setText(R.string.no_comments);
					 }
				}
			}.execute(url);

		}
	};
}
