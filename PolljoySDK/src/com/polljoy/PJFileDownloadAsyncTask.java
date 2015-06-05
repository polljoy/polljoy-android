package com.polljoy;

import android.os.AsyncTask;

import com.polljoy.internal.Log;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class PJFileDownloadAsyncTask extends AsyncTask<Void, Void, String> {
	public interface PJFileDownloadAsyncTaskListener {
		public void taskCompletedCallback(String filename);

		public void taskFailedCallback(Exception e);
	}

    private static final int BUFFER_SIZE = 4096;

	String TAG = "PJFileDownloadAsyncTask";
    String downloadUrl = null;
    String outputFile = null;
    PJFileDownloadAsyncTaskListener taskListener = null;
	Exception failureException = null;

    PJFileDownloadAsyncTask(String downloadUrl, String outputFile) {
        this.downloadUrl = downloadUrl;
        this.outputFile = outputFile;
    }

    protected void onPreExecute()
    {
        Log.d(TAG, "Wait for downloading url : " + this.downloadUrl);
    }

	@Override
	protected String doInBackground(Void... params) {
        try
        {
            URL url = new URL(downloadUrl);

            Log.d(TAG, " URL : " + url.toString());
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            int responseCode = urlConnection.getResponseCode();

            FileOutputStream fileOutput = new FileOutputStream(this.outputFile);
            InputStream inputStream = urlConnection.getInputStream();

            int totalSize = urlConnection.getContentLength();
            int downloadedSize = 0;

            //create a buffer...
            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                fileOutput.write(buffer, 0, bytesRead);
                downloadedSize += bytesRead;
                Log.d(TAG, " progress " + downloadedSize + " / " + totalSize);
            }

            //close the output stream when done
            fileOutput.close();
            inputStream.close();

            urlConnection.disconnect();
        }
        catch (MalformedURLException e)
        {
            Log.e( TAG , " MalformedURLException ERROR : " + e );
        }
        catch (IOException e)
        {
            Log.e( TAG , " IOException ERROR : " + e );
        }
        return this.outputFile;
	}

	@Override
	protected void onPostExecute(String result) {
		if (this.failureException != null) {
			if (this.taskListener != null) {
				this.taskListener.taskFailedCallback(this.failureException);
			}
		} else {
			if (this.taskListener != null) {
				this.taskListener.taskCompletedCallback(result);
			}
		}


	}
}
