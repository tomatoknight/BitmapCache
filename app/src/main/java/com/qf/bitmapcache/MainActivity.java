package com.qf.bitmapcache;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    List<String> data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        data = new ArrayList<>();
        ListView lv = (ListView) findViewById(R.id.lv);
        for (int i = 0; i < 20; i++) {
            data.add(">>>>>>" + i);
        }

        MyAdapter madapter = new MyAdapter(data, this);

        lv.setAdapter(madapter);

        Log.e("TAG", "1111111111111111111111111111111111111111111");
        Log.e("TAG", "2222222222222222222222222222222222222222222");
    }
}
