package com.example.adriene.festivent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.example.adriene.festivent.EventInfo;

public class list2 extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list2);


        //String[] listArray = {"One", "Two", "Three", "Four","Four","Four","Four","Four","Four","Four","Four","Four"};

        EventInfo[] events = {new EventInfo("Event 1"), new EventInfo("Event 2"), new EventInfo("Event 3")};


        ListAdapter listAdapter = new MyCustomAdapter(this,events);
        ListView theListView = (ListView) findViewById(R.id.listView);
        theListView.setAdapter(listAdapter);

        theListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView,View view, int i, long l) {
                EventInfo event = (EventInfo) adapterView.getItemAtPosition(i);

                Toast.makeText(list2.this, event.eventName, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
