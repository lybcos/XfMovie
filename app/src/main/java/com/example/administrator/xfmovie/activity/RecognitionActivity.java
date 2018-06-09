package com.example.administrator.xfmovie.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.xfmovie.PowerImageView;
import com.example.administrator.xfmovie.R;
import com.example.administrator.xfmovie.library.ItemWave;
import com.example.administrator.xfmovie.library.WaveHelper;
import com.example.administrator.xfmovie.library.WaveView;
import com.iflytek.aiui.AIUIAgent;
import com.iflytek.aiui.AIUIConstant;
import com.iflytek.aiui.AIUIEvent;
import com.iflytek.aiui.AIUIListener;
import com.iflytek.aiui.AIUIMessage;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class RecognitionActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageView close;
    private TextView tv_status,tv_show;

    private  WaveView wave;
    private  WaveHelper helper;
    //交互状态
    private int mAIUIState = AIUIConstant.STATE_IDLE;
    private PowerImageView begin_luyin;
    private AIUIAgent aiuiAgent=null;
    private Toast mToast;
    private SpeechSynthesizer mTts;     // 语音朗读模块

    //录音权限
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};
    private static String TAG = RecognitionActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognition);
        findId();
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        requestPermission();
    }

    private void findId() {
        tv_status = findViewById(R.id.tv_status);
        tv_show = findViewById(R.id.tv_show);
        begin_luyin = findViewById(R.id.begin_luyin);
        close = findViewById(R.id.close);
        wave = findViewById(R.id.wave);
        ArrayList<ItemWave> list = new ArrayList<>();
        list.add(ItemWave.getRandomItemWave());
        list.add(ItemWave.getRandomItemWave());
        list.add(ItemWave.getRandomItemWave());
        list.add(ItemWave.getRandomItemWave());
        wave.setWaveColor(list);
        wave.setAmplitudeRatio(60*0.1f/100f);
        helper = new WaveHelper(wave);
        close.setOnClickListener(this);
        begin_luyin.setOnClickListener(this);
    }

    //AIUI事件监听器
    private AIUIListener mAIUIListener = new AIUIListener() {

        @Override
        public void onEvent(AIUIEvent event) {
            switch (event.eventType) {
                case AIUIConstant.EVENT_WAKEUP:
                    //唤醒事件
                    Log.i( TAG,  "on event: "+ event.eventType );
                    showTip( "进入识别状态" );
                    break;

                case AIUIConstant.EVENT_RESULT: {
                    //结果事件
                    begin_luyin.setVisibility(View.VISIBLE);
                    wave.setVisibility(View.GONE);
                    Log.i( TAG,  "on event: "+ event.eventType );
                    try {
                        JSONObject bizParamJson = new JSONObject(event.info);
                        JSONObject data = bizParamJson.getJSONArray("data").getJSONObject(0);
                        JSONObject params = data.getJSONObject("params");
                        JSONObject content = data.getJSONArray("content").getJSONObject(0);

                        if (content.has("cnt_id")) {
                            String cnt_id = content.getString("cnt_id");
                            JSONObject cntJson = new JSONObject(new String(event.data.getByteArray(cnt_id), "utf-8"));

                            String sub = params.optString("sub");
                            JSONObject result = cntJson.optJSONObject("intent");
                            if ("nlp".equals(sub) && result.length() > 2) {
                                // 解析得到语义结果
                                String str = "";
                                //在线语义结果
                                if(result.optInt("rc") == 0){
                                    JSONObject answer = result.optJSONObject("answer");
                                    if(answer != null){
                                       str = answer.optString("text");
                                    }
                                }else{
                                    str = "无法进行识别";
                                    if (str.equals("无法进行识别")){
                                        begin_luyin.setVisibility(View.VISIBLE);
                                        wave.setVisibility(View.GONE);
                                    }
                                }
                                if (!TextUtils.isEmpty(str)){
                                    tv_status.append( "\n" );
                                    tv_status.append(str);//显示识别结果
                                }

                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                        tv_status.append( "\n" );
                        tv_status.append( e.getLocalizedMessage() );
                    }

                    tv_status.append( "\n" );
                } break;

                case AIUIConstant.EVENT_ERROR: {
                    //错误事件
                    Log.i( TAG,  "on event: "+ event.eventType );
                    begin_luyin.setVisibility(View.VISIBLE);
                    wave.setVisibility(View.GONE);
                    tv_status.append( "\n" );
                    tv_status.append( "错误: "+event.arg1+"\n"+event.info );
                } break;

                case AIUIConstant.EVENT_VAD: {
                    //vad事件
                    if (AIUIConstant.VAD_BOS == event.arg1) {
                        //找到语音前端点
                        showTip("找到vad_bos");
                    } else if (AIUIConstant.VAD_EOS == event.arg1) {
                        //找到语音后端点
                        showTip("找到vad_eos");
                    } else {
                        showTip("" + event.arg2);
                    }
                } break;

                case AIUIConstant.EVENT_START_RECORD: {
                    //开始录音事件
                    Log.i( TAG,  "on event: "+ event.eventType );
                    showTip("开始录音");
                } break;

                case AIUIConstant.EVENT_STOP_RECORD: {
                    //停止录音事件
                    Log.i( TAG,  "on event: "+ event.eventType );

                    showTip("停止录音");
                } break;

                case AIUIConstant.EVENT_STATE: {	// 状态事件
                    mAIUIState = event.arg1;

                    if (AIUIConstant.STATE_IDLE == mAIUIState) {
                        // 闲置状态，AIUI未开启
                        showTip("闲置状态");
                    } else if (AIUIConstant.STATE_READY == mAIUIState) {
                        // AIUI已就绪，等待唤醒

                        showTip("AIUI已就绪，等待唤醒");
                    } else if (AIUIConstant.STATE_WORKING == mAIUIState) {
                        // AIUI工作中，可进行交互
                        showTip("AIUI工作中:请开口说话");
                    }
                } break;
                case AIUIConstant.EVENT_PRE_SLEEP:
                    showTip("准备进入休眠状态");

                    break;
                default:
                    break;
            }
        }

    };
    @Override
    public void onClick(View v) {
        if (!checkAIUIAgent()) {
            return;
        }
        if (v.getId() == R.id.begin_luyin) {
            wave.setVisibility(View.VISIBLE);
            tv_show.setVisibility(View.GONE);
            begin_luyin.setVisibility(View.GONE);
            startVoiceNlp();
        }else if (v.getId()==R.id.close){
            RecognitionActivity.this.finish();
        }
    }

    private void startVoiceNlp() {
        tv_status.setText("");
        //AIUIMessage开发者可以发送不同的AIUIMessage来控制AIUI的运行，如发送CMD_WAKEUP使AIUI进入唤醒就绪状态，
        // 发送CMD_RESET_WAKEUP使AIUI进入休眠状态。同时通过AIUIListener监听接收AIUI抛出的AIUIEvent进行解析，
        // 如通过EVENT_RESULT解析AIUI返回的听写和语义结果，通过EVENT_SLEEP得知AIUI进入休眠状态。
        // 先发送唤醒消息，改变AIUI内部状态，只有唤醒状态才能接收语音输入
        // 默认为oneshot 模式，即一次唤醒后就进入休眠，如果语音唤醒后，需要进行文本语义，请将改段逻辑copy至startTextNlp()开头处
        if( AIUIConstant.STATE_WORKING != 	this.mAIUIState ){
            AIUIMessage wakeupMsg = new AIUIMessage(AIUIConstant.CMD_WAKEUP, 0, 0, "", null);
            aiuiAgent.sendMessage(wakeupMsg);
        }

        // 打开AIUI内部录音机，开始录音
        String params = "sample_rate=16000,data_type=audio";
        AIUIMessage writeMsg = new AIUIMessage( AIUIConstant.CMD_START_RECORD, 0, 0, params, null );
        aiuiAgent.sendMessage(writeMsg);
    }

    private void showTip(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mToast.setText(str);
                mToast.show();
            }
        });
    }

    private boolean checkAIUIAgent() {
        if (aiuiAgent == null) {
            aiuiAgent = AIUIAgent.createAgent(this, getAIUIParams(),mAIUIListener);
        }
        if (aiuiAgent == null) {
            final  String  errorText="检测失败，请重新录音";
                showTip(errorText);

                tv_status.setText(errorText);
        }
        return null!= aiuiAgent;
    }

    /**
     * 读取配置
     */
    private String getAIUIParams() {
        String params = "";

        AssetManager assetManager = getResources().getAssets();
        try {
            InputStream ins = assetManager.open( "cfg/aiui_phone.cfg" );
            byte[] buffer = new byte[ins.available()];

            ins.read(buffer);
            ins.close();

            params = new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return params;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if( null != this.aiuiAgent ){
            AIUIMessage stopMsg = new AIUIMessage(AIUIConstant.CMD_STOP, 0, 0, null, null);
            aiuiAgent.sendMessage( stopMsg );

            this.aiuiAgent.destroy();
            this.aiuiAgent = null;
        }
    }



    //申请录音权限
    public void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int i = ContextCompat.checkSelfPermission(this, permissions[0]);
            if (i != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, 321);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 321) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PERMISSION_GRANTED) {
                    this.finish();
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        helper.cancel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        helper.start();
    }
}
