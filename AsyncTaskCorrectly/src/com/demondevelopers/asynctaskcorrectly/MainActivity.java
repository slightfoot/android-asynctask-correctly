package com.demondevelopers.asynctaskcorrectly;

import java.util.Date;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import com.demondevelopers.asynctaskcorrectly.R;


public class MainActivity extends Activity implements MyAsyncUpdate
{
	private MyAsyncTask mMyAsyncTask;
	private ScrollView mScroll;
	private TextView mStatus;
	
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		findViewById(R.id.refresh).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				MainActivity.this.onMyAsyncUpdate(new Date() + ": " + "Clicked");
				if(mMyAsyncTask == null || mMyAsyncTask.getStatus() == AsyncTask.Status.FINISHED){
					mMyAsyncTask = new MyAsyncTask();
					mMyAsyncTask.setAsyncUpdate(MainActivity.this);
					mMyAsyncTask.execute();
				}
			}
		});
		
		mScroll = (ScrollView)findViewById(R.id.scroll);
		mStatus = (TextView)findViewById(R.id.status);
		
		mMyAsyncTask = (MyAsyncTask)getLastNonConfigurationInstance();
		if(mMyAsyncTask == null){
			onMyAsyncUpdate(new Date() + ": " + "Ready");
		}
	}
	
	@Override
	public void onMyAsyncUpdate(String text)
	{
		mStatus.setText(mStatus.getText() + "\n" + text);
		mScroll.post(new Runnable()
		{
			@Override
			public void run()
			{
				mScroll.fullScroll(View.FOCUS_DOWN);
			}
		});
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		if(mMyAsyncTask != null){
			mMyAsyncTask.setAsyncUpdate(this);
		}
	}
	
	@Override
	public Object onRetainNonConfigurationInstance()
	{
		return mMyAsyncTask;
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		if(mMyAsyncTask != null){
			mMyAsyncTask.setAsyncUpdate(null);
		}
	}
	
	
	private static class MyAsyncTask extends AsyncTask<Void, Void, Void>
	{
		private MyAsyncUpdate mAsyncUpdate;
		
		
		public void setAsyncUpdate(MyAsyncUpdate asyncUpdate)
		{
			mAsyncUpdate = asyncUpdate;
		}
		
		private void dispatchUpdate(String text)
		{
			if(mAsyncUpdate != null){
				mAsyncUpdate.onMyAsyncUpdate(text);
			}
		}
		
		@Override
		protected void onPreExecute()
		{
			dispatchUpdate(new Date() + ": " + "onPreExecute");
		}
		
		@Override
		protected Void doInBackground(Void... params)
		{
			for(int i = 0; i < 5; i++){
				try{
					Thread.sleep(1000);
				}
				catch(InterruptedException e){
					//
				}
				publishProgress();
			}
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Void... values)
		{
			dispatchUpdate(new Date() + ": " + "onProgressUpdate");
		}
		
		@Override
		protected void onPostExecute(Void result)
		{
			dispatchUpdate(new Date() + ": " + "onPostExecute");
		}
	}
}


interface MyAsyncUpdate
{
	public void onMyAsyncUpdate(String text);
}
