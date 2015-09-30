package com.example.issues;

public class IssueObject {

	private final String mTitle;
	private final String mBody;
	private final String mCommentsUrl;


	public IssueObject(String title, String body, String commentsUrl)
	{
		mTitle = title;
		mBody = body;
		mCommentsUrl = commentsUrl;
	}

	public String getBody()
	{
		return mBody;
	}

	public String getTitle()
	{
		return mTitle;
	}

	public String getCommentsUrl() {
		return mCommentsUrl;
	}
	
	
}
