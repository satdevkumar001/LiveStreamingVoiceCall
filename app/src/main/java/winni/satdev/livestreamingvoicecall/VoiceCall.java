package winni.satdev.livestreamingvoicecall;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcChannel;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;

public class VoiceCall extends AppCompatActivity {

    private String channelName;
    private int channelProfile;
    FrameLayout viedo;
    private IRtcEngineEventHandler mRtcEventHandler ;
    private RtcEngine mRtcEngine;
    RtcChannel rtcChannel;
    String addCall="notAdded";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("TAG", "onCreate: " );

        setContentView(R.layout.live_streaming_voice_call);


    }


  // voice call in live streaming

    // add a voice call with host user to users
    public void addNewCall(View view) {
         addCall="added";
                initAgoraEngineAndJoinChannel2();


    }









    private final IRtcEngineEventHandler mRtcEventHandlerVoice = new IRtcEngineEventHandler() {
        // Tutorial Step 1

        /**
         * Occurs when a remote user (Communication)/host (Live Broadcast) leaves the channel.
         *
         * There are two reasons for users to become offline:
         *
         *     Leave the channel: When the user/host leaves the channel, the user/host sends a goodbye message. When this message is received, the SDK determines that the user/host leaves the channel.
         *     Drop offline: When no data packet of the user or host is received for a certain period of time (20 seconds for the communication profile, and more for the live broadcast profile), the SDK assumes that the user/host drops offline. A poor network connection may lead to false detections, so we recommend using the Agora RTM SDK for reliable offline detection.
         *
         * @param uid ID of the user or host who
         * leaves
         * the channel or goes offline.
         * @param reason Reason why the user goes offline:
         *
         *     USER_OFFLINE_QUIT(0): The user left the current channel.
         *     USER_OFFLINE_DROPPED(1): The SDK timed out and the user dropped offline because no data packet was received within a certain period of time. If a user quits the call and the message is not passed to the SDK (due to an unreliable channel), the SDK assumes the user dropped offline.
         *     USER_OFFLINE_BECOME_AUDIENCE(2): (Live broadcast only.) The client role switched from the host to the audience.
         */
        @Override
        public void onUserOffline(final int uid, final int reason) { // Tutorial Step 4
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(VoiceCall.this, "Calling User offline  "+reason+"....  "+uid, Toast.LENGTH_SHORT).show();
                    onRemoteUserLeft(uid, reason);

                }
            });
        }

        /**
         * Occurs when a remote user stops/resumes sending the audio stream.
         * The SDK triggers this callback when the remote user stops or resumes sending the audio stream by calling the muteLocalAudioStream method.
         *
         * @param uid ID of the remote user.
         * @param muted Whether the remote user's audio stream is muted/unmuted:
         *
         *     true: Muted.
         *     false: Unmuted.
         */
        @Override
        public void onUserMuteAudio(final int uid, final boolean muted) { // Tutorial Step 6
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(VoiceCall.this, "Calling User Mute Auto  "+muted+"....  "+uid, Toast.LENGTH_SHORT).show();
                    onRemoteUserVoiceMuted(uid, muted);

                }
            });
        }


        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            Toast.makeText(VoiceCall.this, "Call is connected >>"+channel+"... "+uid, Toast.LENGTH_SHORT).show();
            super.onJoinChannelSuccess(channel, uid, elapsed);
        }

        @Override
        public void onUserJoined(int uid, int elapsed) {
            Toast.makeText(VoiceCall.this, "Call User Join  "+elapsed+"....  "+uid, Toast.LENGTH_SHORT).show();
            super.onUserJoined(uid, elapsed);

        }

        @Override
        public void onLeaveChannel(RtcStats stats) {
            Toast.makeText(VoiceCall.this, "Call User Leave  "+stats.cpuTotalUsage+"....  ", Toast.LENGTH_SHORT).show();
            super.onLeaveChannel(stats);

        }
    };
    private void initAgoraEngineAndJoinChannel2() {


        initializeAgoraEngine();     // Tutorial Step 1
        joinChannelVoice();               // Tutorial Step 2
    }



    private RtcEngine mRtcEngineVoice; // Tutorial Step 1

    // Tutorial Step 1
    private void initializeAgoraEngine() {
        try {
            mRtcEngineVoice = RtcEngine.create(getBaseContext(),
                    getString(R.string.private_app_id), mRtcEventHandlerVoice);
        }
        catch (Exception e) {
            Log.e("LOG_TAG", Log.getStackTraceString(e));

            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    // Tutorial Step 2
    private void joinChannelVoice() {
        String accessToken = "";
        if (TextUtils.equals(accessToken, "") ||
                TextUtils.equals(accessToken, "#YOUR ACCESS TOKEN#")) {
            accessToken = null; // default, no token
        }

        // Sets the channel profile of the Agora RtcEngine.
        // CHANNEL_PROFILE_COMMUNICATION(0): (Default) The Communication profile. Use this profile in one-on-one calls or group calls, where all users can talk freely.
        // CHANNEL_PROFILE_LIVE_BROADCASTING(1): The Live-Broadcast profile. Users in a live-broadcast channel have a role as either broadcaster or audience. A broadcaster can both send and receive streams; an audience can only receive streams.
        mRtcEngineVoice.setChannelProfile(Constants.AUDIO_PROFILE_DEFAULT);
//        mRtcEngineVoice.enableVideo();
        mRtcEngineVoice.disableVideo();

        // Allows a user to join a channel.
        mRtcEngineVoice.joinChannel(accessToken,
                "voice", "Extra Optional Data", 0); // if you do not specify the uid, we will generate the uid for you
    }


    // Tutorial Step 4
    private void onRemoteUserLeft(int uid, int reason) {
        showLongToast(String.format(Locale.US, "user %d left %d", (uid & 0xFFFFFFFFL), reason));
//        View tipMsg = findViewById(R.id.quick_tips_when_use_agora_sdk); // optional UI
//        tipMsg.setVisibility(View.VISIBLE);
    }

    // Tutorial Step 6
    private void onRemoteUserVoiceMuted(int uid, boolean muted) {
        showLongToast(String.format(Locale.US, "user %d muted or unmuted %b", (uid & 0xFFFFFFFFL), muted));
    }

    public final void showLongToast(final String msg) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRtcEngine.leaveChannel();
        RtcEngine.destroy();
        mRtcEngine = null;

    }

}