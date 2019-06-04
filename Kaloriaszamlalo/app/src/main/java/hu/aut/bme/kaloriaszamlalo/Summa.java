package hu.aut.bme.kaloriaszamlalo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import hu.aut.bme.kaloriaszamlalo.Today.MainActivity;


public class Summa extends AppCompatActivity {

    public final static String summareggeli="summareggeli" ;
    public final static String summaebed="summaebed";
    public  final static String summavacsora="summavacsora";
    public final static String datum="datum";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ring);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        intent.getExtras();
        Context con= getApplicationContext();

        Button btn2= (Button) findViewById(R.id.btnVissza);
        PieChart chartHoliday = (PieChart) findViewById(R.id.chart);
        TextView datum_text =(TextView) findViewById(R.id.datum);
        datum_text.setText(intent.getStringExtra(datum));


        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Intent intent = new Intent(Summa.this, MainActivity.class);
                startActivity(intent);
            }
        });

        List<PieEntry> entries = new ArrayList<>();
        if(Integer.parseInt(intent.getStringExtra(summareggeli))!=0)
            entries.add(new PieEntry(Integer.parseInt(intent.getStringExtra(summareggeli)),  con.getString(R.string.Reggeli)));
        if(Integer.parseInt(intent.getStringExtra(summaebed))!=0)
            entries.add(new PieEntry(Integer.parseInt(intent.getStringExtra(summaebed)),  con.getString(R.string.Ebed)));
        if(Integer.parseInt(intent.getStringExtra(summavacsora))!=0)
            entries.add(new PieEntry(Integer.parseInt(intent.getStringExtra(summavacsora)),  con.getString(R.string.Vacsora)));

        PieDataSet dataSet = new PieDataSet(entries, con.getString(R.string.Napi));
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        PieData data = new PieData(dataSet);
        chartHoliday.setData(data);
        chartHoliday.invalidate();


    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Summa.this,MainActivity.class);
        startActivity(intent);
    }

}
