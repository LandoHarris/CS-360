package com.example.landonharriscs360;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), this::onApplyWindowInsets);
    }

    private WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
        Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

        //Initialize Views from the activity_main.xml
        TextView nameText = findViewById(R.id.textGreeting);
        EditText editText = findViewById(R.id.nameText);
        Button myButton = findViewById(R.id.buttonSayHello);
        //When Button is pressed
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v ) {
                SayHello sayHello = new SayHello();
                sayHello.sayHello(nameText, editText);
            }
        });

        //Add in the Text Change Listener
        // I added the check on each change listener though they are probably unnecessary
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //if editText is not Empty button is on
                if(!editText.getText().toString().isEmpty()){
                    myButton.setEnabled(true);
                }
                //if editText is Empty button is off
                if(editText.getText().toString().isEmpty())
                    myButton.setEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //if editText is not Empty button is on
                if(!editText.getText().toString().isEmpty()){
                    myButton.setEnabled(true);
                }
                //if editText is Empty button is off
                if(editText.getText().toString().isEmpty())
                    myButton.setEnabled(false);
            }


            @Override
            public void afterTextChanged(Editable s) {
                //if editText is not Empty button is on
                if(!editText.getText().toString().isEmpty()){
                    myButton.setEnabled(true);
                }
                //if editText is Empty button is off
                if(editText.getText().toString().isEmpty())
                    myButton.setEnabled(false);

            }


        });

        return insets;
    }

    public class SayHello {
        public void sayHello(TextView textView, EditText editText) {
            // Greeting = textGreeting = ""
            String Greeting = textView.getText().toString();
            //Name = name text = "Name"
            String Name= editText.getText().toString();

            // if the editText(Name) is not empty
            if (!editText.getText().toString().isEmpty()) {
                Name = editText.getText().toString();
            }
            // checks if the Text view (Greeting) is empty
            if (textView.getText().toString().isEmpty()) {
                Greeting = "Hello";
            }
            // For some reason it wouldn't clear the previous entry without this
            // checks if the Text view (Greeting) is not empty
            if (!textView.getText().toString().isEmpty()) {
                Greeting = "Hello";
            }

            //  textGreeting and "Hello" and "Name"
            textView.setText(Greeting+" " + Name + "\n");


            // if the editText is null
            if (editText.getText().toString().isEmpty())
                return;
        }
    }
}
