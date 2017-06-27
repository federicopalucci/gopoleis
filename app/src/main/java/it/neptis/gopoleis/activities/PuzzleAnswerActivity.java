package it.neptis.gopoleis.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import it.neptis.gopoleis.R;

public class PuzzleAnswerActivity extends Activity {

    private EditText answer;
    private TextView title;
    private TextView display_answer_size;
    private Button send;

    private String solution = " ";
    private int size = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle_answer);

        answer = (EditText) findViewById(R.id.et_insert_answer);
        title = (TextView) findViewById(R.id.l_puzzle_answer_title);
        display_answer_size = (TextView) findViewById(R.id.l_length_answer);

        solution = getIntent().getExtras().getString("answer");
        size = solution.length();
        String display = size+" caratteri:";
        display_answer_size.setText(display);

        send = (Button) findViewById(R.id.b_send_answer);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String my_answer = answer.getText().toString();
                if (my_answer.equals(solution)){

                    setResult(RESULT_OK);
                    finish();

                }
                else {
                    Toast.makeText(PuzzleAnswerActivity.this,"Hai sbagliato! Ritenta!",Toast.LENGTH_LONG).show();
                }
            }
        });


    }
}
