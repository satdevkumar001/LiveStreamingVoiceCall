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

public class LiveStreamingAndVoiceCall extends AppCompatActivity {

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
         viedo = (FrameLayout) findViewById(R.id.remote_video_view_container);

        Intent intent = getIntent();
        channelName = intent.getStringExtra(MainActivity.channelMessage);
        channelProfile = intent.getIntExtra(MainActivity.profileMessage, -1);
        if (channelProfile!=Constants.CLIENT_ROLE_AUDIENCE){
            findViewById(R.id.btAddCall).setVisibility(View.GONE);

        }

        initAgoraEngineAndJoinChannel();



    }


    private void initAgoraEngineAndJoinChannel() {
        Log.e("InitAgoraEngineAndJoinChannel", "initAgoraEngineAndJoinChannel: Done" );
        initalizeAgoraEngine();
        mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
        mRtcEngine.setClientRole(channelProfile);
        setupVideoProfile();
        setupLocalVideo();
        joinChannel();
    }

    private void initalizeAgoraEngine() {
        try {
            Log.e("setAgoraEngin", "initalizeAgoraEngine: " );
            getInt();
            mRtcEngine = RtcEngine.create(getBaseContext(),
                    getString(R.string.private_app_id), mRtcEventHandler);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupVideoProfile() {
        mRtcEngine.enableVideo();

        mRtcEngine.setVideoEncoderConfiguration(
                new VideoEncoderConfiguration(VideoEncoderConfiguration.VD_640x480,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));
    }

    private void setupLocalVideo() {
        if (channelProfile!=Constants.CLIENT_ROLE_AUDIENCE) {
            SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
            surfaceView.setZOrderMediaOverlay(true);
            viedo.addView(surfaceView);
            mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, 0));
        }
    }

    private void joinChannel() {
        mRtcEngine.joinChannel(null, channelName, "Optional Data", 0);
    }

    private void getInt() {
        mRtcEventHandler = new IRtcEngineEventHandler() {



            @Override
            public void onFirstRemoteVideoDecoded
                    (final int uid, int width, int height, int elapsed) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("TAG", "run: User First Remote  "+uid);
                        Toast.makeText(LiveStreamingAndVoiceCall.this, "User FirstRemote  " + uid, Toast.LENGTH_SHORT).show();


                        setupRemoteVideo(uid);
                    }
                });
            }

            @Override
            public void onUserOffline(int uid, final int reason) {
                Toast.makeText(LiveStreamingAndVoiceCall.this, "User Left  " + reason + "....  " + uid, Toast.LENGTH_SHORT).show();
                Log.e("TAG", "run: User offline  "+uid+reason);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onRemoteUserLeft();
                        Log.e("TAG", "run: User run offline  "+uid+reason);


                    }
                });
            }

            @Override
            public void onConnectionStateChanged(int state, int reason) {
                super.onConnectionStateChanged(state, reason);
                Log.e("TAG", "onConnectionStateChanged: "+state+"   >>>>  "+reason );
            }

            @Override
            public void onUserMuteVideo(final int uid, final boolean muted) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LiveStreamingAndVoiceCall.this, "User Mute  " + muted + "....  " + uid, Toast.LENGTH_SHORT).show();
                        Log.e("TAG", "onUserMuteViedo: "+uid+"   >>>>  "+muted );


                        //                    onRemoteUserVideoMuted(uid, muted);
                    }
                });
            }

            @Override
            public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
                super.onJoinChannelSuccess(channel, uid, elapsed);
                Toast.makeText(LiveStreamingAndVoiceCall.this, "User Join Channel SuccessFully " + channel + "....  " + uid, Toast.LENGTH_SHORT).show();
                Log.e("TAG", "onJoinChannelSuccess: "+channel+uid+"  >>>>> "+elapsed );
            }

            @Override
            public void onUserJoined(int uid, int elapsed) {
                super.onUserJoined(uid, elapsed);
                Log.e("TAG", "onUserJoined: "+uid+"  >>>>> "+elapsed );

                Toast.makeText(LiveStreamingAndVoiceCall.this, "User Join  " + elapsed + "....  " + uid, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onLeaveChannel(RtcStats stats) {
                super.onLeaveChannel(stats);
                Log.e("TAG", "onLeaveChannel: "+stats.cpuTotalUsage );
                Toast.makeText(LiveStreamingAndVoiceCall.this, "User Leave Channel  " + stats.cpuTotalUsage + "....  ", Toast.LENGTH_SHORT).show();

            }
        };
    }

    private void setupRemoteVideo(int uid) {
        if (channelProfile==Constants.CLIENT_ROLE_AUDIENCE){
            Log.e("TAG", "setupRemoteVideo: channelProfile is audience ");
            if (addCall.equals("added")){

            }else {
                SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
                viedo.addView(surfaceView);
                mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT,
                        uid));
            }
        }
        else {
            Log.e("TAG", "setupRemoteVideo: channelProfile is host " );


        }

//        if (container.getChildCount() > 1) {
//            return;
//        }

    }

    private void onRemoteUserLeft() {
        Log.e("TAG", "onRemoteUserLeft: method" );
        viedo.removeAllViews();
    }

    public void onLocalVideoMuteClicked(View view) {
        ImageView iv = (ImageView) view;
        if (iv.isSelected()) {
            iv.setSelected(false);
            iv.clearColorFilter();
        }
        else {
            iv.setSelected(true);
            iv.setColorFilter(getResources().getColor(R.color.design_default_color_error), PorterDuff.Mode.MULTIPLY);
        }

        mRtcEngine.muteLocalVideoStream(iv.isSelected());

        SurfaceView surfaceView = (SurfaceView) viedo.getChildAt(0);
        surfaceView.setZOrderMediaOverlay(!iv.isSelected());
        surfaceView.setVisibility(iv.isSelected() ? View.GONE : View.VISIBLE);
        }

    public void onLocalAudioMuteClicked(View view) {
        ImageView iv = (ImageView) view;
        if (iv.isSelected()) {
            iv.setSelected(false);
            iv.clearColorFilter();
        }
        else {
            iv.setSelected(true);
            iv.setColorFilter(getResources().getColor(R.color.design_default_color_background), PorterDuff.Mode.MULTIPLY);
        }

        mRtcEngine.muteLocalAudioStream(iv.isSelected());

    }

    public void onSwitchCameraClicked(View view) {
        mRtcEngine.switchCamera();

    }

    public void onEndCallClicked(View view) {
        finish();
    }


    // voice call in live streaming

    // add a voice call with host user to users
    public void addNewCall(View view) {
        initAgoraEngineAndJoinChannel2();
        findViewById(R.id.btEndCall).setVisibility(View.VISIBLE);
//        startActivity(new Intent(this,VoiceCall.class));


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
                    Toast.makeText(LiveStreamingAndVoiceCall.this, "Calling User offline  "+reason+"....  "+uid, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(LiveStreamingAndVoiceCall.this, "Calling User Mute Auto  "+muted+"....  "+uid, Toast.LENGTH_SHORT).show();
                    onRemoteUserVoiceMuted(uid, muted);

                }
            });
        }


        @Override
        public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
            Toast.makeText(LiveStreamingAndVoiceCall.this, "Call is connected >>"+channel+"... "+uid, Toast.LENGTH_SHORT).show();
            super.onJoinChannelSuccess(channel, uid, elapsed);
        }

        @Override
        public void onUserJoined(int uid, int elapsed) {
            Toast.makeText(LiveStreamingAndVoiceCall.this, "Call User Join  "+elapsed+"....  "+uid, Toast.LENGTH_SHORT).show();
            super.onUserJoined(uid, elapsed);

        }

        @Override
        public void onLeaveChannel(RtcStats stats) {
            Toast.makeText(LiveStreamingAndVoiceCall.this, "Call User Leave  "+stats.cpuTotalUsage+"....  ", Toast.LENGTH_SHORT).show();
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
        } catch (Exception e) {
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

    public void EndVoiceCall(View view) {
        Toast.makeText(this, "You are disconneted succesfully.", Toast.LENGTH_SHORT).show();
        findViewById(R.id.btEndCall).setVisibility(View.GONE);
        mRtcEngineVoice.leaveChannel();
        mRtcEngineVoice.destroy();
    }
}