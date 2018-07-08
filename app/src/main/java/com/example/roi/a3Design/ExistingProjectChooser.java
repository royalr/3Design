package com.example.roi.a3Design;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class ExistingProjectChooser extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE); // (NEW)
        getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,
                android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_existing_project_chooser);


        ImageButton back = findViewById(R.id.btnLoadBack);
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Welcome.class);
                startActivity(intent);
            }
        });

        Button btn1 = findViewById(R.id.slt1);
        Button btn2 = findViewById(R.id.slt2);
        Button btn3 = findViewById(R.id.slt3);
        Button btn4 = findViewById(R.id.slt4);
        Button btn5 = findViewById(R.id.slt5);

        ArrayList<Button> buttons = new ArrayList<>();
        buttons.add(btn1);
        buttons.add(btn2);
        buttons.add(btn3);
        buttons.add(btn4);
        buttons.add(btn5);


        for (int i = 0; i < 5; i++) {
            File file = getFileStreamPath("save" + i);
            if (file == null || !file.exists()) {
                buttons.get(i).setText("Empty Slot " + (i + 1));
                buttons.get(i).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        Toast.makeText(view.getContext(), "Nothing to load!", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                buttons.get(i).setText("Saved Data " + (i + 1));
                buttons.get(i).setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        ProjectStatesManager.init(false);
                        Button b = (Button) view;
                        String buttonText = b.getText().toString();
                        buttonText = buttonText.replaceAll("\\D", "");
                        ProjectStatesManager.regLoadId(Integer.parseInt(buttonText) -1);
                        ProjectStatesManager.init(false);
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                });
            }
        }
    }
}
