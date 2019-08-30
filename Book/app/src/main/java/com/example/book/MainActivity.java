package com.example.book;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static java.lang.System.in;

public class MainActivity extends AppCompatActivity {

    WebView webView;
    Toolbar toolbar_setting;

    //Mediarecorder/Mediaplayer setting
    PopupMenu popupMenu;
    int count=0;
    int totalCount = 0;
    String file;
    String fileplay;
    MediaRecorder mediaRecorder;
    MediaPlayer mediaPlayer;
    boolean playMode = false,recordMode;

    //For permission setting
    public static final int RequestPermissionCode = 1;

    String[] words;
    ArrayList<Integer> map = new ArrayList<>();
    ArrayList<Integer> area = new ArrayList<>();
    ArrayList<String> record = new ArrayList<>();

    int nowPage = 1;
    int minPage = 1;
    int maxPage = 51;
    String[] imgHeigh;
    String totalSize;

    //getscreensize
    int wvWidth, wvHeight;

    double fontSize;
    List<String> listFontSize;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setWebView();

        //toolbar setting
        setToolbar();
        toolbar_setting.setTitle("The Walking Dead");

        copyAssets();

        File files = new File(Environment.getExternalStorageDirectory() + "/Book/TheWalkingDead/record.txt");
        StringBuilder stringBuilder = new StringBuilder();

        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(files));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String line;
        try {
            while((line = bufferedReader.readLine())!= null){
                stringBuilder.append(line);
                words = line.split(" ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(int i=0; i < words.length; i+=3) {
            map.add(Integer.parseInt(words[i]));
            area.add(Integer.parseInt(words[i+1]));
            record.add(words[i+2]);
        }

        getScreenSize();
       /* try {
            writeChange();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    private void setToolbar() {

        toolbar_setting = (Toolbar) findViewById(R.id.toolbar);
        toolbar_setting.inflateMenu(R.menu.toolbar_setting);

        toolbar_setting.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    /*case R.id.books:
                        setBook();
                        break;*/

                    case R.id.play_records:
                        //Log.e("log", "" + item.getItemId());
                        File getdirfile = new File(Environment.getExternalStorageDirectory() + "/Book/ThewalkingDead/records/");
                        if(!getdirfile.exists()) getdirfile.mkdirs();
                        int countdir = getTotalFiles(getdirfile);

                        playMode = !playMode;

                        if(countdir == 0){
                            playMode = false;
                            Toast.makeText(MainActivity.this, "No records exist", Toast.LENGTH_LONG).show();
                            item.setIcon(android.R.drawable.ic_media_play);
                        }

                        if(playMode){
                            if(mediaPlayer == null) {
                                Toast.makeText(MainActivity.this, "Play all records", Toast.LENGTH_SHORT).show();
                                item.setIcon(android.R.drawable.ic_media_pause);

                                //count = 0;
                                //Toast.makeText(MainActivity.this, record.get(count), Toast.LENGTH_LONG).show();
                                mediaPlayer = new MediaPlayer();

                                file = Environment.getExternalStorageDirectory() + "/Book/ThewalkingDead/records/" + nowPage + ".3gp";
                                try {
                                    mediaPlayer.setDataSource(file);
                                    mediaPlayer.prepare();
                                    mediaPlayer.start();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "You are already playing records now.", Toast.LENGTH_SHORT).show();
                            }



                                /*totalCount = 0;
                                for(int i=0; i<map.size(); i++){ //all map
                                    for(int j=1; j<=51; j++){ //page
                                        if(map.get(i)==j && j<nowPage){
                                            totalCount++;
                                        }
                                    }
                                }
                                try {
                                    Log.e("log", totalCount + " " + count);
                                    if(map.get(totalCount+count) == nowPage){
                                        //Toast.makeText(MainActivity.this, Integer.toString(count), Toast.LENGTH_SHORT).show();
                                        playAudio(Environment.getExternalStorageDirectory() + "/Book/ThewalkingDead/records/" + record.get(totalCount + count) + ".3gp");
                                        //Toast.makeText(MainActivity.this, Integer.toString(nowPage), Toast.LENGTH_LONG).show();
                                    }

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }*/
                        } else {
                            if(mediaPlayer !=null){
                                Toast.makeText(MainActivity.this, "Stop playing records", Toast.LENGTH_SHORT).show();
                                item.setIcon(android.R.drawable.ic_media_play);
                                mediaPlayer.stop();
                                mediaPlayer.release();
                                mediaPlayer = null;
                                count = words.length;
                            }
                        }
                        break;

                    case R.id.stop_recording:
                        recordMode = !recordMode;
                        //Log.e("log", "" + item.getItemId());

                        if(recordMode) {
                            //要開始錄音必須確認現在沒有在錄音、播放以及可以使用麥克風及寫入檔案
                            if(mediaRecorder == null && mediaPlayer == null && checkPermission()){
                                Toast.makeText(MainActivity.this, "Start recording", Toast.LENGTH_SHORT).show();
                                item.setIcon(android.R.drawable.picture_frame);

                                File getfile = new File(Environment.getExternalStorageDirectory() + "/Book/ThewalkingDead/records/");
                                if(!getfile.exists()) getfile.mkdirs();
                                //設定錄音存取位置
                                file = Environment.getExternalStorageDirectory() + "/Book/ThewalkingDead/records/" + nowPage + ".3gp";
                                MediaPrepare();

                                try {
                                    mediaRecorder.prepare();
                                    mediaRecorder.start();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                if(mediaRecorder != null){
                                    Toast.makeText(MainActivity.this, "You are recording now.", Toast.LENGTH_SHORT).show();
                                }
                                else if(mediaPlayer != null){
                                    Toast.makeText(MainActivity.this, "You are playing records now.", Toast.LENGTH_SHORT).show();
                                }
                                else requestPermission();
                            }
                        } else {
                            if(mediaRecorder != null){
                                Toast.makeText(MainActivity.this, "Stop recording", Toast.LENGTH_SHORT).show();
                                item.setIcon(android.R.drawable.ic_notification_overlay);

                                mediaRecorder.stop();
                                mediaRecorder.release();

                                mediaRecorder = null;
                            }
                            else{
                                Toast.makeText(MainActivity.this, "No recording now", Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;

                    case R.id.page_up:
                        //Log.e("log", "" + item.getItemId());
                        if(nowPage != 1){
                            nowPage --;
                            //Toast.makeText(MainActivity.this, Integer.toString(nowPage), Toast.LENGTH_LONG).show();
                            //int index = nowPage-1;
                            //int totalHeight = (int) (webView.getScrollY() +  Integer.parseInt(imgHeigh[index])*2.65);
                            int totalHeight = 8;

                            /*if(wvWidth == 1536 && wvHeight == 1952){
                                for (int i=0; i<nowPage-1; i++){
                                    totalHeight += Integer.parseInt(imgHeigh[i])*2.01 - 0.2;
                                    //2.65-3.7
                                    //Log.e("log", "total: "+ Integer.parseInt(imgHeigh[i])*2.65);
                                }
                            }
                            if(wvWidth == 1080 && wvHeight == 1794){
                                for(int i=0; i< nowPage-1; i++){
                                    totalHeight += Integer.parseInt(imgHeigh[i])*2.65 - 3.7;
                                }
                            }*/
                            for(int i=0; i<nowPage-1; i++){
                                totalHeight += Integer.parseInt(imgHeigh[i])*webView.getScale() + 8;
                            }
                            //Log.e("log", "total "+ nowPage);
                            //webView.setScrollY(Integer.parseInt(String.valueOf(totalHeight)));
                            webView.scrollTo(webView.getScrollX(),totalHeight);
                            //Log.e("log", "total "+ webView.getScrollY());
                        }
                        else{
                            Toast.makeText(MainActivity.this,"In the first page", Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case R.id.page_down:
                        //R.id.record_stop.setIcon(android.R.drawable.ic_media_play);
                        //Log.e("log", "" + item.getItemId());
                        if(nowPage != 51){
                            nowPage ++;
                            //Toast.makeText(MainActivity.this, Integer.toString(nowPage), Toast.LENGTH_LONG).show();
                            // int index = nowPage-1;
                            //int totalHeight = (int) (webView.getScrollY() +  Integer.parseInt(imgHeigh[index])*2.65);
                            int totalHeight = 8;

                            /*if(wvWidth == 1536 && wvHeight == 1952){
                                for (int i=0; i<nowPage-1; i++){
                                    totalHeight += Integer.parseInt(imgHeigh[i])*2.01;
                                    //2.65-3.5
                                    //Log.e("log", "total: "+ Integer.parseInt(imgHeigh[i])*2.65);
                                }
                            }
                            if(wvWidth == 1080 && wvHeight == 1794){
                                for(int i=0; i<nowPage-1; i++){
                                    totalHeight += Integer.parseInt(imgHeigh[i])*2.65 - 3.5;
                                }
                            }*/

                            for(int i=0; i<nowPage-1; i++){
                                totalHeight += Integer.parseInt(imgHeigh[i])*webView.getScale() + 8;
                            }

                            //Log.e("log", "total "+ Integer.parseInt(imgHeigh[index])*2.65);
                            //webView.setScrollY(Integer.parseInt(String.valueOf(totalHeight)));
                            webView.scrollTo(webView.getScrollX(),totalHeight);
                            //Log.e("log", "total "+ webView.getScrollY());
                        }
                        else{
                            Toast.makeText(MainActivity.this,"The End", Toast.LENGTH_SHORT).show();
                        }
                        break;
                } return false;
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setWebView() {
        webView = new WebView(this);
        //webView.setWebChromeClient(new WebChromeClient());
        webView = (WebView) findViewById(R.id.wbook);
        webView.loadUrl("file:///android_asset/thewalkingdead4.html");

        webView.setVerticalScrollBarEnabled(true);
        webView.setHorizontalScrollBarEnabled(false);

        //do everything inside webview.
        webView.setWebViewClient(new WebViewClient());

        webView.setBackgroundColor(Color.TRANSPARENT);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.addJavascriptInterface(new myJsInterface(this), "Android");
        webView.setOnScrollChangeListener(scrollchange);
    }
    private View.OnScrollChangeListener scrollchange = new View.OnScrollChangeListener() {
        @Override
        public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
            //Log.d("MainActivity", "setOnScrollChangeListener - onScrollChange");
            //Log.e("log", "!!!!!!!!!");
            //Toast.makeText(MainActivity.this, "change", Toast.LENGTH_SHORT).show();
            int totalHeight = 8;
            for(int i=0; i<nowPage-1; i++){
                totalHeight += Integer.parseInt(imgHeigh[i])*webView.getScale() + 8;
            }
            if(nowPage < 51){   //避免array過大發生錯誤
                int nextPageHeight = (int) (totalHeight + Integer.parseInt(imgHeigh[nowPage])*webView.getScale() + 8);
                if(webView.getScrollY() >= nextPageHeight){
                    nowPage++;
                    Log.e("log", "" + nowPage);
                }
                else {
                    if (nowPage == 50 && webView.getScrollY() + webView.getHeight() >= webView.getContentHeight()*webView.getScale()) {    //when screen height > img size
                        //Log.e("log", "in the end" + webView.getHeight() + " " + wvHeight + " " + toolbar_setting.getHeight() + " " + webView.getContentHeight());
                        //Log.e("log", "in the end, screen too large " + nowPage);
                        nowPage ++;
                        Log.e("log", "" + nowPage);
                    }
                }
            }
            if(nowPage > 1){    //避免array過小發生錯誤, 好像不會錯誤XD
                int previousPageHeight = (int) (totalHeight - webView.getHeight());
            /*if(wvHeight > wvWidth){
                previousPageHeight += 50;
            }*/
                Log.e("log", "previous: " + previousPageHeight + " total: " + totalHeight + "y: " + webView.getScrollY());
                if(webView.getScrollY() <= previousPageHeight){
                    nowPage--;
                    //Log.e("log", wvHeight + "   " + nowPage + " " + toolbar_setting.getHeight());
                    Log.e("log", "" + nowPage);
                }
                else if (nowPage == 2 && webView.getScrollY() == 0){    //when screen height > img size
                    nowPage--;
                    Log.e("log", " " + nowPage);
                }
            }
        }
    };

    //要求可以使用寫入內部資料夾及允許使用播放器
    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                } break;
        }
    }

    //recorder setting
    private void MediaPrepare() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);

        mediaRecorder.setOutputFile(file);
    }

    private class myJsInterface {
        String[] words;
        Context context;

        myJsInterface(Context c) {
            /* constructor */
            context = c;
        }
        @android.webkit.JavascriptInterface
        public void getImageSize(String size){
            imgHeigh = size.split(",");
        }
        @android.webkit.JavascriptInterface
        public void makeToast(String mapnumber, String areanumber){
            //Toast.makeText(MainActivity.this,"map number :" + mapnumber + " , area number :" + areanumber, Toast.LENGTH_SHORT).show();

            int onX = Integer.parseInt(mapnumber);
            int onY = Integer.parseInt(areanumber);

            File files = new File(Environment.getExternalStorageDirectory() + "/Book/TheWalkingDead/record.txt");
            StringBuilder stringBuilder = new StringBuilder();

            BufferedReader bufferedReader = null;
            try {
                bufferedReader = new BufferedReader(new FileReader(files));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            String line;
            try {
                while((line = bufferedReader.readLine())!= null){
                    stringBuilder.append(line);
                    words = line.split(" ");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            for(int i=0; i < (words.length/3); i++){
                /*int map = Integer.parseInt(words[i]);
                int area = Integer.parseInt(words[i+1]);
                final String record = words[i+2];*/

                //Toast.makeText(MainActivity.this, words[i], Toast.LENGTH_SHORT).show();

                if(onX == map.get(i) && onY == area.get(i)){
                    popupMenu = new PopupMenu(MainActivity.this, webView);
                    popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                    final int finalI = i;
                    Toast.makeText(MainActivity.this, record.get(i),Toast.LENGTH_LONG).show();
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            switch (item.getItemId()) {
                                case R.id.record:
                                    if (checkPermission()) {
                                        Toast.makeText(MainActivity.this, "Start recording", Toast.LENGTH_SHORT)
                                                .show();
                                        File getfile = new File(Environment.getExternalStorageDirectory() + "/Book/ThewalkingDead/records/");
                                        if(!getfile.exists()) getfile.mkdirs();
                                        //count = getTotalFiles(getfile);
                                        file = Environment.getExternalStorageDirectory() + "/Book/ThewalkingDead/records/" ;
                                        file += record.get(finalI) + ".3gp";
                                        MediaPrepare();

                                        try {
                                            mediaRecorder.prepare();
                                            mediaRecorder.start();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        requestPermission();
                                    }
                                    break;

                                case R.id.play:

                                    File playnow = new File(Environment.getExternalStorageDirectory() + "/Book/ThewalkingDead/records/" + record.get(finalI) +".3gp");
                                    if(playnow.exists()){
                                        Toast.makeText(MainActivity.this, "Start playing", Toast.LENGTH_SHORT)
                                                .show();

                                        mediaPlayer = new MediaPlayer();
                                        fileplay = Environment.getExternalStorageDirectory() + "/Book/ThewalkingDead/records/" ;
                                        fileplay += record.get(finalI) + ".3gp";

                                        try {
                                            mediaPlayer.setDataSource(fileplay);
                                            mediaPlayer.prepare();
                                            mediaPlayer.start();

                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        Toast.makeText(MainActivity.this, "No record exists" , Toast.LENGTH_LONG).show();
                                    }
                                    break;
                                default:
                                    break;
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }

            }
        }
    }

    private void copyAssets(){
        if(checkPermission()){
            AssetManager assetManager = this.getAssets();
            String[] files = null;

            try {
                files = assetManager.list("");
                File outFile = new File(Environment.getExternalStorageDirectory() + "/Book/ThewalkingDead/");
                if(!outFile.exists())outFile.mkdirs();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (files != null)
                for (String filename : files) {
                    InputStream in = null;
                    OutputStream out = null;
                    try {
                        in = assetManager.open(filename);
                        File outFile = new File(Environment.getExternalStorageDirectory() + "/Book/ThewalkingDead/", filename);
                        out = new FileOutputStream(outFile);
                        copyFile(in, out);
                    } catch (IOException e) {
                        Log.e("tag", "Failed to copy asset file:" + filename, e);
                    } finally {
                        if (in != null) {
                            try {
                                in.close();
                                in = null;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        if (out != null) {
                            try {
                                out.flush();
                                out.close();
                                out = null;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                        }
                    }
                }
        } else{
            requestPermission();
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    public void playAudio(String path) throws IOException{
        count++;
        final MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(path);
        mediaPlayer.prepare();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.stop();
                Log.e("Log", totalCount+" " + count + " " + map.size());

                if(totalCount+count < (words.length/3)-1){
                    Toast.makeText(MainActivity.this, Integer.toString(map.get(totalCount+count)),Toast.LENGTH_LONG).show();
                    if(map.get(totalCount+count) == nowPage){
                        Toast.makeText(MainActivity.this, Integer.toString(map.get(totalCount+count)), Toast.LENGTH_LONG).show();

                        File play = new File(Environment.getExternalStorageDirectory() + "/Book/ThewalkingDead/records/" + record.get(totalCount+count) +".3gp");
                        if(play.exists()){
                            try {
                                playAudio(Environment.getExternalStorageDirectory() + "/Book/ThewalkingDead/records/" + record.get(totalCount+count) +".3gp");
                            }
                            catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else if(!play.exists()){
                            count++;
                            try {
                                if(map.get(totalCount+count) == nowPage){
                                    playAudio(Environment.getExternalStorageDirectory() + "/Book/ThewalkingDead/records/" + record.get(totalCount+count) +".3gp");
                                }
                            }
                            catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } else mediaPlayer.stop();
                } else mediaPlayer.stop();
            }
        });
        mediaPlayer.start();
    }

    private int getTotalFiles(File dir) {
        int counter = 0;
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                counter += getTotalFiles(file);
            }
            counter++;
        }
        return counter;
    }

    private void getScreenSize(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        wvWidth = size.x;
        wvHeight = size.y;
        Log.e("log", "screen size:" + wvWidth + "*" + wvHeight);
    }

    private void setBook() {

    }


    private void writeChange() throws IOException {
        File res = new File(Environment.getExternalStorageDirectory() + "/Book/TestWriteFile");
        //File res = new File(Environment.getExternalStorageDirectory() + "/Book/ThewalkingDead/change.js");
        if(res.exists()){
            FileWriter fileWriter = new FileWriter(Environment.getExternalStorageDirectory() + "/Book/TestWriteFile/Test3.html",true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            String imgString = "";
            for(int i=0; i<51;i++){
                imgString += "<img src=\""+ (i+1) +".jpg\" id=\"1\" style=\"width:100%\">\n";
            }
            bufferedWriter.write("<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "\t<title> The Walking Dead </title>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "\n" +
                    //"<img src=\"1.jpg\" id=\"1\" style=\"width:755px; height:885.88px\">\n" +
                    imgString +
                    "\n" +
                    "</body>\n" +
                    "</html>");
            //bufferedWriter.write(a);
            bufferedWriter.newLine();
            bufferedWriter.close();

            //webView.loadDataWithBaseURL(null,"Test3.html","text/html","utf-8",null);

        }
        else{
            boolean success = res.mkdirs();
            writeChange();
            Log.e("log", "res doesn't exits");
        }

    }
    private void readChange() throws IOException{
        //webView.loadUrl("file:///" + Environment.getExternalStorageDirectory() + "/Book/ThewalkingDead/change.html");

        File orifile = new File(Environment.getExternalStorageDirectory() + "/Book/ThewalkingDead/thewalkingdead.html");
        File file = new File(Environment.getExternalStorageDirectory() + "/Book/ThewalkingDead/change.html");
        StringBuilder stringBuilder = new StringBuilder();

        BufferedReader bufferedReader = new BufferedReader(new FileReader(orifile));
        String line;
        String html ="";

        while ((line = bufferedReader.readLine())!= null){
            stringBuilder.append(line);
            stringBuilder.append('\n');
            html += line;

            //Toast.makeText(MainActivity.this,line,Toast.LENGTH_LONG).show();
        }
        //webView.loadDataWithBaseURL("file:///android_asset/",html,"text/html","utf-8",null);
        bufferedReader.close();

        bufferedReader = new BufferedReader(new FileReader(file));
        while((line = bufferedReader.readLine())!=null){
            stringBuilder.append(line);
            stringBuilder.append('\n');
            html+= line;
        }

        webView.loadDataWithBaseURL("file:///android_asset/",html,"text/html","utf-8",null);
    }
}








