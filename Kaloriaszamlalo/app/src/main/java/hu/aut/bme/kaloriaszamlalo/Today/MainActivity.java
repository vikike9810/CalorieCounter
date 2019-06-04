package hu.aut.bme.kaloriaszamlalo.Today;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;

import java.util.List;

import hu.aut.bme.kaloriaszamlalo.Model.Day;
import hu.aut.bme.kaloriaszamlalo.Model.Food.Hints;
import hu.aut.bme.kaloriaszamlalo.Model.Item;
import hu.aut.bme.kaloriaszamlalo.Naptar;
import hu.aut.bme.kaloriaszamlalo.R;
import hu.aut.bme.kaloriaszamlalo.Summa;
import hu.aut.bme.kaloriaszamlalo.Today.Adapter.Adapter;


public class MainActivity extends AppCompatActivity implements NewItemDialogFragment.INewItemDialogListener {

    private RecyclerView recyclerView;
    private Adapter adapter;
    public List<Day> naptar=Day.listAll(Day.class);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
    String currentDateandTime = sdf.format(new Date());
    //String currentDateandTime="2017.12.05";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView Date =(TextView) findViewById(R.id.date);
        Date.setText(currentDateandTime);
        final Button btn = (Button) findViewById(R.id.gomb);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new NewItemDialogFragment().show(getSupportFragmentManager(), NewItemDialogFragment.TAG);
            }
        });
        if(!(letezik(currentDateandTime))){
            Item.deleteAll(Item.class);
        }
        initRecyclerView();
        adapter.con=getApplicationContext();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                String summareggeli;
                String summaebed;
                String summavacsora;
                Intent intent;
                if (adapter.items.isEmpty()) {

                    if (letezik(currentDateandTime)) {
                        int index= naptar.indexOf(holvan(currentDateandTime));
                        holvan(currentDateandTime).delete();
                        naptar.remove(index);
                    }
                    Context context = getApplicationContext();
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, R.string.pop1, duration);
                    toast.show();
                } else {
                    summareggeli = Integer.toString(adapter.osszreggeli);
                    summaebed = Integer.toString(adapter.osszebed);
                    summavacsora = Integer.toString(adapter.osszvacsora);

                    if (letezik(adapter.items.get(0).date)) {
                        holvan(adapter.items.get(0).date).reggeli = adapter.osszreggeli;
                        holvan(adapter.items.get(0).date).ebed = adapter.osszebed;
                        holvan(adapter.items.get(0).date).vacsora = adapter.osszvacsora;
                        holvan(adapter.items.get(0).date).save();
                    } else {
                        Day nap = new Day();
                        nap.datum = adapter.items.get(0).date;
                        nap.vacsora = adapter.osszvacsora;
                        nap.ebed = adapter.osszebed;
                        nap.reggeli = adapter.osszreggeli;
                        naptar.add(nap);
                        nap.save();
                    }
                    intent = new Intent(MainActivity.this, Summa.class);
                    intent.putExtra(Summa.summareggeli, summareggeli);
                    intent.putExtra(Summa.summaebed, summaebed);
                    intent.putExtra(Summa.summavacsora, summavacsora);
                    intent.putExtra(Summa.datum, adapter.items.get(0).date);
                    startActivity(intent);
                }
            }
        });
    }


    boolean letezik(String date){
        for(Day item: naptar){
            if(item.datum.equals(date))
                return true;
        }
        return false;
    }

    Day holvan(String date){
        Day ret=new Day();
        for(Day item: naptar){
            if(item.datum.equals(date))
                return item;
        }
        return ret;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_delete_all) {
                adapter.delete();
            if(letezik(currentDateandTime)){
                int index = naptar.indexOf(holvan(currentDateandTime));
                holvan(currentDateandTime).delete();
                naptar.remove(index);
            }
        }

        if (id == R.id.naplo) {
            if(naptar.isEmpty()){
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, R.string.pop2, duration);
                toast.show();
            }
            else {
                Intent intent = new Intent(MainActivity.this, Naptar.class);
                startActivity(intent);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void initRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.MainRecyclerView);
        adapter = new Adapter();
        loadItemsInBackground();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadItemsInBackground() {
        new AsyncTask<Void, Void, List<Item>>() {

            @Override
            protected List<Item> doInBackground(Void... voids) {
                return Item.listAll(Item.class);
            }

            @Override
            protected void onPostExecute(List<Item> Items) {
                super.onPostExecute(Items);
                adapter.update(Items);

            }
        }.execute();
    }

    @Override
    public void onItemCreated(Item newItem) {
        adapter.addItem(newItem);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(false);
    }
}
