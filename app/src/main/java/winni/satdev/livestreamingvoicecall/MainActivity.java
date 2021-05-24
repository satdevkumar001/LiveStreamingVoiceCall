package winni.satdev.livestreamingvoicecall;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import io.agora.rtc.Constants;

public class MainActivity extends AppCompatActivity {
EditText etChannel;
    int channelProfile;
    public static final String channelMessage = "Live.CHANNEL";
    public static final String profileMessage = "Live.PROFILE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etChannel=findViewById(R.id.etChannel);
        getPermission();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("TAG1", "onStart: " );
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("TAG1", "onStop: " );

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("TAG1", "onStart: " );

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("TAG1", "onPause: " );

    }

    @Override
    protected void onResume() {
        Log.e("TAG1", "onResume: " );

        super.onResume();
    }
    
    public void getPermission(){
        int MY_PERMISSIONS_REQUEST_CAMERA = 0;
        // Here, this is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission
                (this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.RECORD_AUDIO},
                    MY_PERMISSIONS_REQUEST_CAMERA);

        }
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.host:
                if (checked) {
                    channelProfile = Constants.CLIENT_ROLE_BROADCASTER;
                }
                break;
            case R.id.audience:
                if (checked) {
                    channelProfile = Constants.CLIENT_ROLE_AUDIENCE;
                }
                break;
        }

    }

    public void onSubmit(View view) {
        if (!etChannel.getText().toString().isEmpty()){
            String channelName = etChannel.getText().toString();
            Intent intent = new Intent(this, LiveStreamingAndVoiceCall.class);
            intent.putExtra(channelMessage, channelName);
            intent.putExtra(profileMessage, channelProfile);
            startActivity(intent);
        }else {
            Toast.makeText(this, "please enter channel name", Toast.LENGTH_SHORT).show();
        }


    }
}