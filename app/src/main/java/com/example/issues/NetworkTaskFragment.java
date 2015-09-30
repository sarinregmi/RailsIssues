package com.example.issues;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class NetworkTaskFragment extends Fragment {

    static interface TaskCallbacks {
        void onPostExecute(List<IssueObject> output);
    }

    private static final String API_URL = "https://api.github.com/repos/rails/rails/issues";
    private TaskCallbacks mCallbacks;
    private GetIssuesTask mTask;
    private boolean mIsRunning;
    private ArrayList<IssueObject> mIssues;



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (TaskCallbacks) activity;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);

        mIssues = new ArrayList<IssueObject>();
        // Create and execute the background task.
        mTask = new GetIssuesTask();
        mTask.execute(API_URL);
        mIsRunning = true;
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    public boolean isRunning() {
        return mIsRunning;
    }

    public List<IssueObject> getIssueItems () {
        return mIssues;
    }

    public void start() {
        if (!mIsRunning) {
            mTask = new GetIssuesTask();
            mTask.execute(API_URL);
            mIsRunning = true;
        }
    }

    private class GetIssuesTask extends AsyncTask<String, Void, ArrayList<IssueObject>> {

        @Override
        protected ArrayList<IssueObject> doInBackground(String... url) {
            ArrayList<IssueObject> issues = new ArrayList<IssueObject>();
            try {
                String message = getJSONResponse(url[0]);
                JSONArray jsonArray = new JSONArray(message);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    final String title = jsonObject.getString("title");
                    final String body = jsonObject.getString("body");
                    final String commentsUrl = jsonObject.getString("comments_url");
                    issues.add(new IssueObject(title, body, commentsUrl));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return issues;
        }


        @Override
        protected void onPostExecute(ArrayList<IssueObject> output) {
            if (mCallbacks != null) {
                mCallbacks.onPostExecute(output);
            }
            mIssues = output;
            mIsRunning = false;
        }
    }


    public static String getJSONResponse(String url) {
        HttpURLConnection conn = null;
        try {
            URL apiUrl = new URL(url);
            conn = (HttpURLConnection) apiUrl.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.connect();

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            br.close();
            String message = sb.toString();
            return message;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        } finally {
            if (conn != null) {
                try {
                    conn.disconnect();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}