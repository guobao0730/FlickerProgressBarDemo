package com.casic.flickerprogressbardemo;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * 开始下载--下载中--继续下载（暂停）--下载完成（打开）
 */
public class MainActivity extends AppCompatActivity {

    FlikerProgressBar roundProgressbar;

    Thread downLoadThread;


    private static final String TAG = "MainActivity";

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //设置当前的进入
            roundProgressbar.setProgress(msg.arg1);

            //如果当前已经下载完毕了，那么结束当前的加载进度
            if(msg.arg1 == 100){
                roundProgressbar.finishLoad();
            }
        }
    };
    private Button mBtn_reDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        initEvent();
    }

    private void initData() {

    }

    private void initEvent() {
        roundProgressbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //如果当前进度条没有结束
                if(!roundProgressbar.isFinish()){
                    if (roundProgressbar.isFrist()){
                        downLoad();
                    }else {
                        //那么切换当前的下载状态（继续下载/暂停下载）
                        roundProgressbar.toggle();

                        //如果当前下载的进度停止了，那么线程中断
                        if(roundProgressbar.isStop()){
                            downLoadThread.interrupt();
                        } else {
                            //否则就开启线程
                            downLoad();
                        }
                    }
                }else {
                    Log.i(TAG,"下载完成了");

                }
            }
        });
    }

    private void initView() {

        roundProgressbar = (FlikerProgressBar) findViewById(R.id.round_flikerbar);

    }

    private void downLoad() {
        downLoadThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //如果当前子线程没有中断
                    while( ! downLoadThread.isInterrupted()){
                        //获取当前的进度
                        float progress = roundProgressbar.getProgress();
                        progress  += 2;
                        Thread.sleep(200);
                        Message message = handler.obtainMessage();
                        message.arg1 = (int) progress;
                        handler.sendMessage(message);
                        if(progress == 100){
                            break;
                        }
                    }
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        downLoadThread.start();
    }

}
