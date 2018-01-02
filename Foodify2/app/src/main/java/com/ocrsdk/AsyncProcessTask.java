package com.ocrsdk;

import java.io.FileOutputStream;

import android.app.*;
import android.content.Context;
import android.os.AsyncTask;

public class AsyncProcessTask extends AsyncTask<String, String, Boolean> {

    public AsyncProcessTask(ResultsActivity activity) {
        this.activity = activity;
        dialog = new ProgressDialog(activity);
    }

    private ProgressDialog dialog;
    /** application context. */
    private final ResultsActivity activity;

    protected void onPreExecute() {
        dialog.setMessage("Processing");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    protected void onPostExecute(Boolean result) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }

        activity.returnResults(result);

        //activity.updateResults(result);
    }

    @Override
    protected Boolean doInBackground(String... args) {

        String inputFile = args[0];
        String outputFile = args[1];

        System.out.println(inputFile);
        System.out.println(outputFile);

        try {
            Client restClient = new Client();


            restClient.applicationId = "StillFresh";
            // You should get e-mail from ABBYY Cloud OCR SDK service with the application password
            restClient.password = "mV3cmXCue7izcHAfX2OR8+H7";

            publishProgress( "Uploading image...");

            String language = "English"; // Comma-separated list: Japanese,English or German,French,Spanish etc.

            ProcessingSettings processingSettings = new ProcessingSettings();
            processingSettings.setOutputFormat( ProcessingSettings.OutputFormat.txt );
            processingSettings.setLanguage(language);

            publishProgress("Uploading..");

            // If you want to process business cards, uncomment this
			/*
			BusCardSettings busCardSettings = new BusCardSettings();
			busCardSettings.setLanguage(language);
			busCardSettings.setOutputFormat(BusCardSettings.OutputFormat.xml);
			Task task = restClient.processBusinessCard(filePath, busCardSettings);
			*/
            Task task = restClient.processImage(inputFile, processingSettings);

            while( task.isTaskActive() ) {
                // Note: it's recommended that your application waits
                // at least 2 seconds before making the first getTaskStatus request
                // and also between such requests for the same task.
                // Making requests more often will not improve your application performance.
                // Note: if your application queues several files and waits for them
                // it's recommended that you use listFinishedTasks instead (which is described
                // at http://ocrsdk.com/documentation/apireference/listFinishedTasks/).

                Thread.sleep(5000);
                publishProgress( "Waiting.." );
                task = restClient.getTaskStatus(task.Id);
            }

            if( task.Status == Task.TaskStatus.Completed ) {
                publishProgress( "Downloading.." );
                FileOutputStream fos = activity.openFileOutput(outputFile,Context.MODE_PRIVATE);

                try {
                    restClient.downloadResult(task, fos);
                } finally {
                    fos.close();
                }

                publishProgress( "Ready" );
            } else if( task.Status == Task.TaskStatus.NotEnoughCredits ) {
                throw new Exception( "Not enough credits to process task. Add more pages to your application's account." );
            } else {
                throw new Exception( "Task failed" );
            }

            return true;
        } catch (Exception e) {
            final String message = "Error: " + e.getMessage();
            publishProgress( message);
            activity.displayMessage(message);
            return false;
        }
    }

    @Override
    protected void onProgressUpdate(String... values) {
        // TODO Auto-generated method stub
        String stage = values[0];
        dialog.setMessage(stage);
        // dialog.setProgress(values[0]);
    }

}