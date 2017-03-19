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
 * This class is where the fall detector application starts from.
 * It collects the emergency phone numbers from the user to which text message should be sent in case of an incident
 * This class also requests for a personalized message the user wants to send to his contacts in casea  fall is detected.
 * Once the user registers the message and phone numbers, the graph activity is started.
 */
public class MainActivity extends Activity {

    public static String TAG = "MainActivity";

    /**
     * Phone numbers and messgae body to be used for sending SMS
     */
    private static String phoneNumber1, phoneNumber2, phoneNumber3, message;

    /**
     * EditText to get 3 emergency contact numbers and a personalized message to be send as an SMS
     */
    EditText phoneEditText1, phoneEditText2, phoneEditText3, messageText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get emergency phone numbers and text message to be sent
        phoneEditText1 = (EditText) findViewById(R.id.phoneText1);
        phoneEditText2 = (EditText) findViewById(R.id.phoneText2);
        phoneEditText3 = (EditText) findViewById(R.id.phoneText3);
        messageText = (EditText) findViewById(R.id.messageText);

        //register contacts to send emergency messages in case of an incident
        final Button button = (Button) findViewById(R.id.register);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                phoneNumber1 = phoneEditText1.getText().toString();
                phoneNumber2 = phoneEditText2.getText().toString();
                phoneNumber3 = phoneEditText3.getText().toString();
                message = messageText.getText().toString();
                //prompts users in case no phone numbers are registered
                if("".equals(phoneNumber1)||"".equals(phoneNumber2)||"".equals(phoneNumber3)){
                    Toast.makeText(MainActivity.this, "Please enter valid phone numbers", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "Phone numbers registered", Toast.LENGTH_LONG).show();
                }
                if("".equals(message)){
                    Toast.makeText(MainActivity.this, "Please enter a valid messgae to be sent", Toast.LENGTH_LONG).show();
                    //default text message to be sent in case the user does not enter a personalized text message
                    message = "Hello, an unintended fall has been detected at the following location. Please get help!";
                }

                //starts to display line graph after phone numbers and personalized text message are registered
                startGraphActivity();
            }
        });

    }

    /**
     * Method to start new GraphActicity to display line graph of accelerometer and gyroscope norms calculated using the wearable motion sensors
     */
    private void startGraphActivity(){
        Log.i(TAG, "Starting Graph Activity");
        Intent i = new Intent(this, GraphActivity.class);
        startActivity(i);
    }

    /**
     * PhoneNumber1 getter
     * @return phoneNumber to which text messgae (SMS) needs to be sent in case of an incident
     */
    public String getPhoneNumber1() {
        return phoneNumber1;
    }

    /**
     * PhoneNumber1 setter
     * @param phoneNumber1
     */
    public void setPhoneNumber1(String phoneNumber1) {
        this.phoneNumber1 = phoneNumber1;
    }

    /**
     * PhoneNumber2 getter
     * @return phoneNumber to which text messgae (SMS) needs to be sent in case of an incident
     */
    public String getPhoneNumber2() {
        return phoneNumber2;
    }

    /**
     * PhoneNumber2 setter
     * @param phoneNumber2
     */
    public void setPhoneNumber2(String phoneNumber2) {
        this.phoneNumber2 = phoneNumber2;
    }

    /**
     * PhoneNumber3 getter
     * @return phoneNumber to which text messgae (SMS) needs to be sent in case of an incident
     */
    public String getPhoneNumber3() {
        return phoneNumber3;
    }

    /**
     * PhoneNumber3 setter
     * @param phoneNumber3
     */
    public void setPhoneNumber3(String phoneNumber3) {
        this.phoneNumber3 = phoneNumber3;
    }

    /**
     * Personalized text message getter
     * @return message to be sent in case of an incident
     */
    public static String getMessage() {
        return message;
    }

    /**
     * Personalized text message setter
     * @param message
     */
    public static void setMessage(String message) {
        MainActivity.message = message;
    }
}
