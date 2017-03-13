package edu.asu.msama1.falldetector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Mitikaa on 2/24/17.
 */

public class MainActivity extends Activity {

    public static String TAG = "MainActivity";

    private static String phoneNumber1, phoneNumber2, phoneNumber3, message;

    EditText phoneEditText1, phoneEditText2, phoneEditText3, messageText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //register contacts to send emergency messages
        phoneEditText1 = (EditText) findViewById(R.id.phoneText1);
        phoneEditText2 = (EditText) findViewById(R.id.phoneText2);
        phoneEditText3 = (EditText) findViewById(R.id.phoneText3);
        messageText = (EditText) findViewById(R.id.messageText);

        final Button button = (Button) findViewById(R.id.register);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                phoneNumber1 = phoneEditText1.getText().toString();
                phoneNumber2 = phoneEditText2.getText().toString();
                phoneNumber3 = phoneEditText3.getText().toString();
                message = messageText.getText().toString();
                if("".equals(phoneNumber1)||"".equals(phoneNumber2)||"".equals(phoneNumber3)){
                    Toast.makeText(MainActivity.this, "Please enter valid phone numbers", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "Phone numbers registered", Toast.LENGTH_LONG).show();
                }
                if("".equals(message)){
                    Toast.makeText(MainActivity.this, "Please enter a valid messgae to be sent", Toast.LENGTH_LONG).show();
                    message = "Hello, an unintended fall has been detected at the following location. Please get help!";
                }

                startGraphActivity();
            }
        });

    }

    private void startGraphActivity(){
        Log.i(TAG, "Starting Graph Activity");
        Intent i = new Intent(this, GraphActivity.class);
        startActivity(i);
    }

    public String getPhoneNumber1() {
        return phoneNumber1;
    }

    public void setPhoneNumber1(String phoneNumber1) {
        this.phoneNumber1 = phoneNumber1;
    }

    public String getPhoneNumber2() {
        return phoneNumber2;
    }

    public void setPhoneNumber2(String phoneNumber2) {
        this.phoneNumber2 = phoneNumber2;
    }

    public String getPhoneNumber3() {
        return phoneNumber3;
    }

    public void setPhoneNumber3(String phoneNumber3) {
        this.phoneNumber3 = phoneNumber3;
    }

    public static String getMessage() {
        return message;
    }

    public static void setMessage(String message) {
        MainActivity.message = message;
    }
}
