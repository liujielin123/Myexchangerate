package com.example.myexchangerate;

import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.renderscript.Element;

import androidx.annotation.NonNull;

import org.jsoup.select.Elements;

        import java.io.IOException;
        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Date;
        import java.util.List;
public class RateListActivity extends ListActivity implements Runnable {
    String data[] = {"wait...."};
    String TAG = "RateList";
    Handler handler;
    private String logDate = "";
    private final String DATE_SP_KEY = "lastRateDateStr";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_rate_list);
        SharedPreferences sp = getSharedPreferences("myrate", Context.MODE_PRIVATE);
        logDate = sp.getString(DATE_SP_KEY, "");
        List<String> list1 = new ArrayList<String>();
        for (int i = 1; i < 100; i++) {
            list1.add("item" + i);
            public void run () {
                List<String> retList = new ArrayList<String>();
                DocumentsContract.Document doc = null;
                try {
                    doc = Jsoup.connect("https://www.boc.cn/sourcedb/whpj/").get();
                    Elements tables = doc.getElementsByTag("table");
                    Element table2 = tables.get(1);
                    //Log.i(TAG,"run:table0"+table0);
                    Elements tds = table2.getElementsByTag("td");
                    for (int i = 0; i < tds.size(); i += 8) {
                        Element td1 = tds.get(i);
                        Element td2 = tds.get(i + 5);
                        String str1 = td1.text();
                        String val = td2.text();
                        retList.add(str1 + "==>" + val);
                        String curDateStr = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
                        if (curDateStr.equals(logDate)) {
                            RateManager manager = new RateManager(this);
                            for (RateItem item : manager.listAll()) {
                                retList.add(item.getCurName() + "-->" + item.getCurRate());
                            }
                        } else {
                            Document doc = null;
                            try {
                                doc = Jsoup.connect("https://www.boc.cn/sourcedb/whpj/").get();
                                Elements tables = doc.getElementsByTag("table");
                                Element table2 = tables.get(1);
                                //Log.i(TAG,"run:table0"+table0);
                                Elements tds = table2.getElementsByTag("td");
                                List<RateItem> rateList = new ArrayList<RateItem>();
                                for (int i = 0; i < tds.size(); i += 8) {
                                    Element td1 = tds.get(i);
                                    Element td2 = tds.get(i + 5);
                                    String str1 = td1.text();
                                    String val = td2.text();
                                    retList.add(str1 + "==>" + val);
                                    rateList.add(new RateItem(str1, val));
                                }
                                RateManager manager = new RateManager(this);
                                manager.deleteAll();
                                manager.addAll(rateList);
                                SharedPreferences sp = getSharedPreferences("myrate", Context.MODE_PRIVATE);
                                SharedPreferences.Editor edit = sp.edit();
                                edit.putString(DATE_SP_KEY, curDateStr);
                                edit.commit();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        Message msg = handler.obtainMessage(7);
                        //msg.obj ="Hello from run()";
                        msg.obj = retList;
                    }
                }
            }
        }
    }
}
