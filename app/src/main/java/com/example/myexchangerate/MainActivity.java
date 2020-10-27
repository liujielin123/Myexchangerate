package com.example.myexchangerate;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        @Override public boolean onCreateOptionsMenu(Menu menu)
        {     getMenuInflater().inflate(R.menu.rate,menu);
                  return true; }
        @Override public boolean onOptionsItemSelected(MenuItem item) {
            if(item.getItemId()==R.id.menu_set){


        }

            return super.onOptionsItemSelected(item); }
        @Override
        public boolean onOptionsItemSelected(@NonNull MenuItem item){
            if(item.getItemId()==R.id.menu_set){
                openCfg();
            }
            else if(item.getItemId()==R.id.open_list){

                Intent list = new Intent(this, RateListActivity.class);
                startActivityForResult(list, 1);
            }
            else if(item.gcetItemId()==R.id.settings){
                Intent settings=new Intent(this,SettingsActivity.class);
                startActivity(settings);
            }
            return super.onOptionsItemSelected(item);
        }

        public void onClick(View btn){
            try {
                String r=rmb.getText().toString();
                double rmb=Double.valueOf(r);
                if (btn.getId()==R.id.btn_dollar){
                    show.setText(String.format("%.2f",rmb*dollarRate));
                }else if(btn.getId()==R.id.btn_euro){
                    show.setText(String.format("%.2f",rmb*euroRate));
                }else {
                    show.setText(String.format("%.2f",rmb*wonRate));
                }
            }
            catch (Exception e){
                Toast.makeText(this,"Please input your money!",Toast.LENGTH_SHORT).show();
                return;
            }
        }

        public void openOne(View btn){
            openCfg();
        }

        private void openCfg() {
            Log.i(Tag, "openOne");
            Intent config = new Intent(this, ConfigActivity.class);
            config.putExtra("dollar_rate_key", dollarRate);
            config.putExtra("euro_rate_key", euroRate);
            config.putExtra("won_rate_key", wonRate);
            Log.i(Tag, "openOne:dollarRate" + dollarRate);
            Log.i(Tag, "openOne:euroRate" + euroRate);
            Log.i(Tag, "openOne:wonRate" + wonRate);

            //startActivity(config);
            startActivityForResult(config, 1);
        }

        protected void onActivityResult(int requestCode,int resultCode,Intent data) {
            if (requestCode == 1 && resultCode == 2) {
                Bundle bdl = data.getExtras();
                dollarRate = bdl.getDouble("key_dollar", 0.0);
                euroRate = bdl.getDouble("key_euro", 0.0);
                wonRate = bdl.getDouble("key_won", 0.0);

                Log.i(Tag, "onActivityResult:dollarRate=" + dollarRate);
                Log.i(Tag, "onActivityResult:euroRate=" + euroRate);
                Log.i(Tag, "onActivityResult:wonRate=" + wonRate);
            }
            super.onActivityResult(requestCode, resultCode, data);
        }

        @Override
        public void run() {
            Log.i(Tag,"run:run()....");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //用于保存从网络获取的汇率
            Bundle bdl_rate=new Bundle();

            //获取网络数据
            //方法1
            URL url= null;
            try {
                url = new URL("http://www.usd-cny.com/bankofchina.htm");
                HttpURLConnection http= (HttpURLConnection) url.openConnection();
                InputStream in =http.getInputStream();
                String html=inputStream2String(in);
                Log.i(Tag,"run:html="+html);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }5ththth

            //方法2
            Document doc = null;
            try {
                doc = Jsoup.connect("http://www.usd-cny.com/bankofchina.htm").get();
                Log.i(Tag,"run:"+doc.title());
                //获取td中的数据
                Elements tds=doc.getElementsByTag("td");
                for(Element td:tds){
                    Log.i(Tag,"run:"+td);
                }
                Element td1,td2;
                for(int i=0;i<tds.size();i+=6){
                    td1=tds.get(i);//货币名称
                    td2=tds.get(i+5);//td1对应的汇率
                    Log.i(Tag,"run:"+td1.text()+"==>"+td2.text());

                    SharedPreferences sp = getSharedPreferences("myrate", Activity.MODE_PRIVATE);

                    SharedPreferences.Editor editor = sp.edit();
                    editor.putFloat("dollar_rate",dollarRate);
                    editor.putFloat("euro_rate",euroRate);
                    editor.putFloat("won_rate",wonRate);
                    editor.apply();
                }
            }
            SharedPreferences sharedPreferences =  getSharedPreferences("myrate", Activity.MODE_PRIVATE);

            PreferenceManager.getDefaultSharedPreferences(this);

            dollarRate = sharedPreferences.getFloat("dollar_rate",0.0f);
  7u                euroRate = sharedPreferences.getFloat("euro_rate",0.0f);
            wonRate = sharedPreferences.getFloat("won_rate",0.0f);
            Message msg=handler.obtainMessage();
            msg.what=5;
            msg.obj="Hello from run()";
            msg.obj=bdl_rate;
            handler.sendMessage(msg);

        }

        private String inputStream2String(InputStream inputStream)   throws IOException {     final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(inputStream, "gb2312");
        while (true) {         int rsz = in.read(buffer, 0, buffer.length);
        if (rsz < 0)             break;         out.append(buffer, 0, rsz);
        }     return out.toString();

        }
        public void Test() {
            Document doc = null;
            try {
                String url = "https://www.chinabond.com.cn/cb/cn/xwgg/ggtz/zyjsgs/zytz/list.shtml";
                doc = Jsoup.connect(url).get();
                Log.i(Tag, "run: " + doc.title());
                Elements tables = doc.getElementsByTag("li").select("a");

                String title,detail;
                for (int i = 0; i < tables.size(); i ++) {
                    title = tables.get(i).attr("title");
                    detail = tables.get(i).attr("href");


                    Log.i(Tag, "run: " + title + "==>" + detail);

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            public class RateListActivity extends ListActivity{
            @Override
            protected void onCreate(Bundle savedInstanceState){
                super.onCreate(savedInstanceState);
                //setContentView(R.layout.activity_rate_List);
                List<String> list1 = new ArrayList<String>();
                for(int i = 1;i<100;i++){
                    list1.add("item" + i);
                }
                String[] list_data ={"one","tow","three","four"};
                ListAdapter adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,
                        list_data)
                        setListAdapter(adapter);
                public class MyListActivity extends AppCompatActivity{
                    @Override
                    protected void onCreate(Bundle savedInstanceState){
                        super.onCreate(savedInstanceState);
                        setContentView(R.layout.activity_my_list);

                        ListView listView = (ListView) findViewById(R.id.mylist);
                        String data[] = {"one","two","three","four"};

                        ListAdapter adapter = new ArrayAdapter<String>(this,
                                android.R.layout.simple_list_item_1,data);
                         listView.setAdapter(adapter);
                    }
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                    String dateTime=df.format(new Date());

                }

            }
        }

    }

}
}