package com.ombre.webscrapper;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
TextView title,time,name,link,image,genere,fbpage;
    String title1,time1,name1,link1,image1,genere1,fbpage1;
ProgressDialog progressDialog;
int writestate=0;
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        title = findViewById(R.id.title);
        time = findViewById(R.id.time);
        getSupportActionBar().hide();

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Scraping In Progress....");
       findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               new maintask().execute();
           }
       });

    }
    public  class maintask extends AsyncTask<Void,Void,Void>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                Document mdocument= Jsoup.connect("https://www.bandsintown.com/")
                        .get();
                Elements emain=mdocument.select("div._2edNTg-3p-8HUsCPjCUBp_");
                for(Element me:emain) {
                    new doinbackground().execute(me);
                    Log.e("url", me.select("a").attr("href"));

                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
    public  class doinbackground extends AsyncTask<Element,Void,Void>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            String PATH = Environment.getExternalStorageDirectory()+"/Ombre/"+title1+".jpg";
            File filecheck=new File(PATH);
            if (filecheck.exists())
            {


            }
            else{
                File direct = new File(Environment.getExternalStorageDirectory()
                        + "/Ombre");

                if (!direct.exists()) {
                    direct.mkdirs();
                }

                DownloadManager mgr = (DownloadManager) getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);

                Uri downloadUri = Uri.parse(image1);

                DownloadManager.Request request = new DownloadManager.Request(
                        downloadUri);


                request.setAllowedNetworkTypes(
                        DownloadManager.Request.NETWORK_WIFI
                                | DownloadManager.Request.NETWORK_MOBILE)
                        .setAllowedOverRoaming(false).setTitle("Downloading")
                        .setDescription(title1)
                        .setDestinationInExternalPublicDir("/Ombre", title1+".jpg");

                mgr.enqueue(request);

                try {
                    write(title1,time1,name1,link1,image1,genere1,fbpage1);
                    progressDialog.dismiss();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            //  progressDialog.dismiss();
//            try {
//                write();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

        }

        @Override
        protected Void doInBackground(Element... params) {

            try {

                    String main=params[0].select("a").attr("href");


                    Document document= Jsoup.connect(main)
                            .timeout(30*1000)
                            .get();
                    Elements element=document.select("div._31N2YODa7GUJWZgNSc_AWl");
                    title1=element.text();
                    Elements element2=document.select("div._1pJ33vJuFJKauIgYOkCleu");
                    time1=element2.text();
                    int i=0;
                    Elements egenere=document.select("div._1v6hYzlTV-hB2ZkAb6CiCv");

                    for(Element e:egenere) {
                        if (i == 0)
                            genere1 = e.text();
                        i++;
                    }
                    Elements ename=document.select("h1._2ewREFNd4qGa6u_PLBCY9F");

                    name1 = ename.text();
                    Elements eimageurl=document.select("a._3FxoLllHIYDsTLMcW1mAl8");
                    image1=eimageurl.select("img").attr("src");

                    Elements efbpage=document.select("div._3EAC_52CXB3SEGlNmW1zZM");
                    fbpage1=efbpage.select("a").attr("href");

                    Elements elink=document.select("div.Wla7qETMG4RlwfQQMTIqx");
                    link1=elink.select("a").attr("href");
                Log.e("data", title1+" "+name1+" "+link1+" "+image1+" "+fbpage1+" "+genere1+" "+time);



            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }



    public  void write(String title2,String time2,String name2,String link2,String image2,String genere2,String fbpage2) throws IOException {

        String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        String fileName = "Ombre.csv";
      //  String PATH = baseDir + File.separator + fileName;;
     //   File filecheck=new File(PATH);
//        if(filecheck.exists())
//        {
//          //  Toast.makeText(this, "CSV Exists", Toast.LENGTH_SHORT).show();}
//        else {
//            Toast.makeText(this, "Csv Not found" +
//                    "", Toast.LENGTH_SHORT).show();
//        }
        String filePath = baseDir + File.separator + fileName;
        File f = new File(filePath);
        CSVWriter writer;
       FileWriter mFileWriter = null;
        // File exist
        if(f.exists()&&!f.isDirectory())
        {
            try {
                mFileWriter = new FileWriter(filePath, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            writer = new CSVWriter(mFileWriter);
        }
        else
        {
            writer = new CSVWriter(new FileWriter(filePath));
        }
if(writestate==0) {
    String[] data = {"Title", "Time", "Artist Name", "Stream Link", "Event Poster", "Genres", "Artist FBPage"};
    writer.writeNext(data);
    writestate++;
}
else {
    String[] data1 = {title2, time2, name2, link2, image2, genere2, fbpage2};


    writer.writeNext(data1);
}

        writer.close();
    }


}
