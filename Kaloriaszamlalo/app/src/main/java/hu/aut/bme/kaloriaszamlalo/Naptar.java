package hu.aut.bme.kaloriaszamlalo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import hu.aut.bme.kaloriaszamlalo.Model.Day;
import hu.aut.bme.kaloriaszamlalo.Today.MainActivity;


public class Naptar extends AppCompatActivity {

    public List<Day> napok = Day.listAll(Day.class);
    private LinearLayout listOfRows;
    private LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.naptar);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        Button btn3= (Button) findViewById(R.id.gomb3);
        ItemComparator comp= new ItemComparator();
        Collections.sort(napok,comp);
        Context con=getApplicationContext();

        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listOfRows = (LinearLayout) findViewById(R.id.list_of_rows);
        int index=1;
        for(Day nap:napok){

            View rowItem = inflater.inflate(R.layout.nap, null);
            TextView date = (TextView)rowItem.findViewById(R.id.d_datum);
            TextView reggeli = (TextView)rowItem.findViewById(R.id.d_reggeli);
            TextView ebed = (TextView)rowItem.findViewById(R.id.d_ebed);
            TextView vacsora = (TextView)rowItem.findViewById(R.id.d_vacsora);
            TextView osszesen = (TextView)rowItem.findViewById(R.id.d_osszesen);
            if((index%2)==1)
                rowItem.setBackgroundColor(Color.WHITE);

            date.setText(nap.datum);
            reggeli.setText(Integer.toString(nap.reggeli)+ con.getString(R.string.kcal));
            ebed.setText(Integer.toString(nap.ebed)+ con.getString(R.string.kcal));
            vacsora.setText(Integer.toString(nap.vacsora)+ con.getString(R.string.kcal));
            osszesen.setText(con.getString(R.string.ossz)+ Integer.toString(nap.reggeli+nap.ebed+nap.vacsora)+ con.getString(R.string.kcal));
            listOfRows.addView(rowItem);
            index++;
        }

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Intent intent = new Intent(Naptar.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public class ItemComparator implements Comparator<Day> {

        public int compare(Day item1, Day item2)
        {
            return item2.datum.compareTo(item1.datum);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Naptar.this,MainActivity.class);
        startActivity(intent);
    }

}
