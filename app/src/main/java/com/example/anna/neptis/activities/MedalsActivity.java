package com.example.anna.neptis.activities;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.anna.neptis.R;
import com.example.anna.neptis.adapters.ImageAdapterHistoricalPeriod;
import com.example.anna.neptis.adapters.ImageAdapterRegions;
import com.example.anna.neptis.adapters.ImageAdapterTypology;

public class MedalsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medals);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


         /*__________________________gestione gridRegions________________________*/
        final GridView gridRegions = (GridView) findViewById(R.id.grid_regions);
        gridRegions.setAdapter(new ImageAdapterRegions(this));
        /*__________________________fine gestione gridRegions________________________*/

        /*___________________________On Click event for Single Gridview Item___________________________*/
        gridRegions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if(position == 0)
                    Toast.makeText(getApplicationContext(),"Lazio",Toast.LENGTH_SHORT).show();

                if(position == 1)
                    Toast.makeText(getApplicationContext(),"Trentino" ,Toast.LENGTH_SHORT).show();

                if(position == 2)
                    Toast.makeText(getApplicationContext(),"Sicilia" ,Toast.LENGTH_SHORT).show();

                if(position == 3)
                    Toast.makeText(getApplicationContext(),"Marche" ,Toast.LENGTH_SHORT).show();
            }
        });
        /*___________________fine gestione click sugli item all'interno di Regions____________________*/


        /*__________________________gestione gridHistoricalPeriod________________________*/
        final  GridView gridHistoricalPeriod = (GridView)findViewById(R.id.grid_historical_periods);
        gridHistoricalPeriod.setAdapter(new ImageAdapterHistoricalPeriod(this));
        /*__________________________fine gestione gridHistoricalPeriod________________________*/


        /*___________________________On Click event for Single GridHistoricalPeriod Item___________________________*/
        gridHistoricalPeriod.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if(position == 0)
                    Toast.makeText(getApplicationContext(),"Barocco",Toast.LENGTH_SHORT).show();

                if(position == 1)
                    Toast.makeText(getApplicationContext(),"Grecia",Toast.LENGTH_SHORT).show();

                if(position == 2)
                    Toast.makeText(getApplicationContext(),"Romani" ,Toast.LENGTH_SHORT).show();

                if(position == 3)
                    Toast.makeText(getApplicationContext(),"Medioevo" ,Toast.LENGTH_SHORT).show();
            }
        });
        /*___________________________fine On Click event for Single GridHistoricalPeriod Item___________________________*/


        /*__________________________gestione gridTypology________________________*/
        final  GridView gridTypology = (GridView)findViewById(R.id.grid_typology);
        gridTypology.setAdapter(new ImageAdapterTypology(this));
        /*__________________________fine gestione gridTypology________________________*/


        /*___________________________On Click event for Single GridTypology Item___________________________*/
        gridTypology.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if(position == 0)
                    Toast.makeText(getApplicationContext(),"Basilica",Toast.LENGTH_SHORT).show();

                if(position == 1)
                    Toast.makeText(getApplicationContext(),"Chiesa",Toast.LENGTH_SHORT).show();

                if(position == 2)
                    Toast.makeText(getApplicationContext(),"Museo" ,Toast.LENGTH_SHORT).show();

                if(position == 3)
                    Toast.makeText(getApplicationContext(),"Castello" ,Toast.LENGTH_SHORT).show();
            }
        });
        /*___________________________fine On Click event for Single GridTypology Item___________________________*/


    }
}
