package be.ugent.androidintroductie;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private EditText nameEditText;
    private TextView nameTextView;
    private EditText naamInvoer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nameEditText = findViewById(R.id.nameEditText);
        nameTextView = findViewById(R.id.nameTextView);

    }
}
//    Button button = findViewById(R.id.button_id);
//button.setOnClickListener(
  //      new View.OnClickListener() {
//public void onClick(View v) {
//here the code to be executed on button click
  //      }
    //    }
      //  );
