package it.neptis.gopoleis.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import it.neptis.gopoleis.R;

public class TutorialDialogActivity extends Activity {

    Dialog tpp1_dialog, tpp2_dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_dialog_activity);

        ImageButton p_giallo = (ImageButton) findViewById(R.id.portale_giallo);
        ImageButton p_verde = (ImageButton) findViewById(R.id.portale_verde);
        ImageButton p_rosso = (ImageButton) findViewById(R.id.portale_rosso);
        ImageButton p_blu = (ImageButton) findViewById(R.id.portale_blu);


        p_giallo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tpp1_dialog = new Dialog(TutorialDialogActivity.this);
                tpp1_dialog.setCancelable(false);
                tpp1_dialog.setContentView(R.layout.tutorial_tpp1);
                tpp1_dialog.show();

            }
        });


        p_verde.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tpp1_dialog = new Dialog(TutorialDialogActivity.this);
                tpp1_dialog.setCancelable(false);
                tpp1_dialog.setContentView(R.layout.tutorial_tpp1);
                tpp1_dialog.show();
            }
        });


    }


    public void avanti_tpp1(View view) {
        tpp1_dialog.cancel();
        tpp2_dialog = new Dialog(TutorialDialogActivity.this);
        tpp2_dialog.setCancelable(false);
        tpp2_dialog.setContentView(R.layout.tutorial_tpp2);
        tpp2_dialog.show();
    }

    public void avanti_tpp2(View view) {

        tpp2_dialog.cancel();
        AlertDialog.Builder inizia = new AlertDialog.Builder(this);
        inizia.setTitle("Inizia la tua avventura!");
        inizia.setMessage("Ora tocca a te! Troviamo più tesori possibili per collezionare il maggior numero di carte!\nGoPoleis!");
        inizia.setIcon(R.drawable.logo);

        inizia.setCancelable(false);
        inizia.setPositiveButton("Inizia", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });


        AlertDialog alert = inizia.create();
        alert.show();
    }

    public void avanti_travel1(View view) {
        tpp1_dialog.cancel();
        tpp2_dialog = new Dialog(TutorialDialogActivity.this);
        tpp2_dialog.setCancelable(false);
        tpp2_dialog.setContentView(R.layout.tutorial_tpp2);
        tpp2_dialog.show();
    }

    public void avanti_travel2(View view) {

        tpp2_dialog.cancel();
        AlertDialog.Builder inizia = new AlertDialog.Builder(this);
        inizia.setTitle("Inizia la tua avventura!");
        inizia.setMessage("Ora tocca a te! Troviamo più tesori possibili per collezionare il maggior numero di carte!\nGoPoleis!");
        inizia.setIcon(R.drawable.logo);

        inizia.setCancelable(false);
        inizia.setPositiveButton("Inizia", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });


        AlertDialog alert = inizia.create();
        alert.show();
    }

}