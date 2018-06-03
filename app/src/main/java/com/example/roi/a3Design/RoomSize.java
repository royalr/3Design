package com.example.roi.a3Design;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class RoomSize extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE); // (NEW)
        getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,
                android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_room_size);

        Button nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                EditText roomWidthET = findViewById(R.id.roomWidthValue);
                EditText roomHeightET = findViewById(R.id.roomHeightValue);

                if (!isValid(roomWidthET) || !isValid(roomHeightET))
                    Toast.makeText(getApplicationContext(), "Please fill in the requested fields.\nMinimum value = 2",
                            Toast.LENGTH_LONG).show();
                else {
                    float width = Float.parseFloat(roomWidthET.getText().toString()),
                            length = Float.parseFloat(roomHeightET.getText().toString());
                    WallManager.registerWalls(new Wall(width), new Wall(length));
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            }
        });
        ImageButton back = findViewById(R.id.btnRoomBack);

        String s = getIntent().getStringExtra("EXTRA_SESSION_ID");
        if (s.equals("renderer")) {
            back.setVisibility(View.INVISIBLE);
        }
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Welcome.class);
                startActivity(intent);
            }
        });
    }

    private boolean isValid(EditText etVal) {
        String strVal = etVal.getText().toString();
        if (strVal.matches(""))
            return false;

        Double dblVal = Double.parseDouble(strVal);
        return dblVal < 2 ? false : true;
    }
}
