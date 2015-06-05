package com.example.johnnyliang.multitreadnew;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;


public class MyActivity extends ActionBarActivity {

    private ListAdapter numberAdapter;
    private String fileName = "numbers.txt";
    private List<String> numberLoad = new ArrayList<String>();
    private ListView listView;
    private FileOutputStream numberOut;

    private ProgressBar progressBar;
    private int progressStatus;
    private TextView status;

    private int countLoadTimes = 0;


    Handler createHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            createFile();
        }

    };

    Handler loadHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            loadList();
        }
    };

    Handler progressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            progressBar.setProgress(progressStatus);
            status.setText(progressStatus + "/" + progressBar.getMax());
        }
    };

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        //numberAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, numberLoad);
        numberAdapter = new ArrayAdapter<String>(this, R.layout.row, R.id.label, numberLoad);
        listView = (ListView) findViewById(R.id.numberList);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.getProgressDrawable().setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
        progressStatus = 0;
        progressBar.setProgress(0);
        status = (TextView) findViewById(R.id.textView);

    }


    /**
     * createFiles function
     * This function will be triggered when the user click the Create File button
     * @param view
     */
    public void createFiles(View view) {
        //progressStatus = 0;
        Thread myThread = new Thread(new Runnable(){

            @Override
            public void run() {
                // TODO Auto-generated method stub
                createFile();
                writeToFile();
            }
        });
        myThread.start();
    }

    /**
     * createFile function
     * This function will create one internal files. It's for storing numbers
     */
    public void createFile() {
        try {
            numberOut= openFileOutput(fileName, MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * writeFile function
     * This function will write the first 10 numbers to the file
     */
    public void writeToFile() {
        String[] numberArray = new String[10];
        int index = 0;

        for (int i = 1; i < 11; i++) {
            numberArray[index++] = "" + i;
        }

        try {
            OutputStreamWriter outputWriter = new OutputStreamWriter(numberOut);

            for (int i = 0; i < 10; i++) {
                outputWriter.write(numberArray[i]);
                outputWriter.write("\n");
                progressStatus += 10;
                progressHandler.sendEmptyMessage(0);
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            outputWriter.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * loadFiles function
     * This function will be triggered when the user click the Load File
     * button. Once it's triggered, it will start loading numbers to the
     * ListView
     * @param view
     */
    public void loadFiles(View view) {
        countLoadTimes++;
        loadList();
        progressStatus = 0;
        progressBar.setProgress(progressStatus);

        Thread myThread = new Thread(new Runnable(){

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    loadAndParse();
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        myThread.start();

    }

    /**
     * loadList function
     * This function will assign the ArrayAdapter to the ListView in
     * order for numbers to be displayed
     */
    public void loadList() {
        if (countLoadTimes % 2 == 0)
            numberAdapter = new ArrayAdapter<String>(this, R.layout.row, R.id.label, numberLoad);
        else
            numberAdapter = new ArrayAdapter<String>(this, R.layout.row_layout, R.id.textView1, numberLoad);
        listView.setAdapter(numberAdapter);
    }

    /**
     * loadAndParse function
     * This function will start loading numbers from the file
     * , then have the handler handle the display to the ListView
     */
    public void loadAndParse() {
        try {
            FileInputStream fileInput = openFileInput(fileName);
            InputStreamReader InputRead = new InputStreamReader(fileInput);

            BufferedReader bf = new BufferedReader(InputRead);
            String receiveString = "";

            String[] loadArray = new String[10];

            int loadIndex = 0;

            while ((receiveString = bf.readLine()) != null) {
                numberLoad.add(receiveString);
                progressStatus += 10;
                // The handler will send message back every time when one number gets loaded
                loadHandler.sendEmptyMessage(0);
                progressHandler.sendEmptyMessage(0);
                Thread.sleep(250);
            }

            InputRead.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * clearList function
     * This function will be triggered when the user click the clear button
     * It will first set the listApater to null and clear the ListView
     * @param view
     */
    public void clearList(View view) {
        numberAdapter = null;
        numberLoad.clear();

        progressStatus = 0;
        progressBar.setProgress(progressStatus);
        status.setText(progressStatus + "/" + progressBar.getMax());

        listView.setAdapter(numberAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
