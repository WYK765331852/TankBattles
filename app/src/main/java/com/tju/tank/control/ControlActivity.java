package com.tju.tank.control;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Timer;
import java.util.TimerTask;

import com.tju.tank.R;
import com.tju.tank.view.LifeView;
import com.tju.tank.view.RockerView;
import com.tju.tank.view.XCRoundImage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ControlActivity extends Activity {

    private int life_n = 100;
    //    private int eLife_n = 100;
    private int bullet_n = 100;
    private LifeView life;
    private LifeView bullet;
    private RockerView rockerView1;
    //private XCRoundImage fire;
    private Button Cannon_Fire;
    private Button Cannon_Left;
    private Button Cannon_Right;
    private Button Cannon_Load;
    private Button Reset;
    //    private ProgressBar wait;
    InputStream in;
    PrintWriter printWriter = null;
    BufferedReader reader;
    private PrintStream out = null;
    Socket mSocket = null;
    public boolean isConnected = false;
    private String serverIP = "192.168.3.3";
    private int serverPort = 8080;
    Thread receiverThread;
    int screenWidth;
    int screenHeight;
    private float mPosX, mPosY, mCurPosX, mCurPosY;
    private int count = 0;
    private long firClick = 0;
    private long secClick = 0;
    private final int interval = 500;
    private String tankID, WaitID;
    private char MyID, EnemyID;
    private ProgressDialog wait1;
    public String cmd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// 设置全屏
        // ,																		// 屏幕长亮
        setContentView(R.layout.activity_main);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
        initViews();
//        elife.setVisibility(View.GONE);
        Timer timer = new Timer();

        //  mclient = new Mclient();
        rockerView1.setRockerChangeListener(new RockerView.RockerChangeListener() {
            @Override
            public void report(float x, float y) {
                // TODO Auto-generated method stub
                setDirection(rockerView1);
            }
        });
        //    Intent intentd = getIntent();
        //    String data = intentd.getStringExtra("data");
        //    serverIP = intentd.getStringExtra(Key.SERVER_IP_KEY);
        //    char[] dataarray = data.toCharArray();
        connectThread();
        //connectServer();
        //    MyID = dataarray[0];
        MyID = 1;
        tankID = "A" + MyID + "1";
        WaitID = "W" + "1" + MyID;
        //send(WaitID);
        wait1 = new ProgressDialog(ControlActivity.this);
        wait1.setTitle("匹配对手中");
        wait1.setMessage("请耐心等候");
        wait1.setCancelable(true);
        wait1.show();
        Cannon_Fire.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                cmd = "C" + MyID + "5";
            }
        });
        Cannon_Right.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                cmd = "C" + MyID + "7";
            }
        });
        Cannon_Left.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                cmd = "C" + MyID + "6";
            }
        });
        Cannon_Load.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                cmd = "C" + MyID + "8";
                bullet_n = 100;
                bullet.getlife(bullet_n);
                bullet.invalidate();
            }
        });
        Reset.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                cmd = "C" + MyID + "9";
                life.getlife(life_n = 100);
                life.invalidate();
                bullet.getlife(bullet_n = 100);
                bullet.invalidate();
            }
        });
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (cmd == null) {
                    send("C" + MyID + "0");
                    return;
                }
                //int x = cmd.indexOf(2);
                if (cmd.charAt(2) == '5') {
                    bullet_n = bullet_n - 10 >= 0 ? bullet_n - 10 : 0;
                    bullet.post(new Runnable() {
                        @Override
                        public void run() {
                            bullet.getlife(bullet_n);
                            bullet.invalidate();
                        }
                    });
                }
                send(cmd);
                cmd = null;
            }
        };
        timer.schedule(timerTask, 1000, 100);//周期时间100ms*/
    }

    /*    private ScheduledExecutorService scheduledExecutor;
        private void updateAddOrSubtract(int viewId) {
            final int vid = viewId;
            scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
            scheduledExecutor.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    Message msg = new Message();
                    msg.what = vid;
                    handler.sendMessage(msg);
                }
            }, 0, 100, TimeUnit.MILLISECONDS);    //每间隔100ms发送Message
        }
        private void stopAddOrSubtract() {
            if (scheduledExecutor != null) {
                scheduledExecutor.shutdownNow();
                scheduledExecutor = null;
            }
        }*/
    public void initViews() {
        rockerView1 = (RockerView) findViewById(R.id.rockerView1);
        life = (LifeView) findViewById(R.id.life);
        bullet = (LifeView) findViewById(R.id.bullet);
//        elife = (LifeView) findViewById(R.id.elife);
        Cannon_Fire = (Button) findViewById(R.id.Cannon_Fire);
        Cannon_Left = (Button) findViewById(R.id.Cannon_Right);
        Cannon_Right = (Button) findViewById(R.id.Cannon_Left);
        Cannon_Load = (Button) findViewById(R.id.Cannon_Load);
        Reset = (Button) findViewById(R.id.Reset);
//        wait = (ProgressBar) findViewById(R.id.wait);
        life.getColor(Color.RED);
        //       elife.getColor(Color.RED);
        bullet.getColor(Color.YELLOW);
        life.getlife(life_n);
        life.getSize(screenWidth / 3, screenHeight / 20);
//        elife.getSize(screenWidth / 3, screenHeight / 20);
//        elife.getlife(eLife_n);
        bullet.getlife(bullet_n);
        bullet.getSize(screenWidth / 3, screenHeight / 20);
    }

    public void setDirection(RockerView v) {
        int a = v.a;
        //String str = "DFG";
        switch (a) {
            case 1:
                cmd = "C" + MyID + "4";
                //Toast.makeText(this,"right",Toast.LENGTH_SHORT).show();
                break;
            case 2:
                //a=0x29;
                cmd = "C" + MyID + "2";
                //Toast.makeText(this,"down",Toast.LENGTH_SHORT).show();
                break;
            case 3:
                //a=0x3d;
                cmd = "C" + MyID + "3";
                //Toast.makeText(this,"left",Toast.LENGTH_SHORT).show();
                break;
            case 4:
                //a=0x46;
                cmd = "C" + MyID + "1";
                //Toast.makeText(this,"up",Toast.LENGTH_SHORT).show();
                break;
        }
        //cmd = "C" + MyID + "5";
        //str = "dfg";
        a = 0;
    }

    private class MyReceiverRunnable implements Runnable {
        public void run() {
            while (true) {
                if (isConnected) {
                    if (mSocket != null && mSocket.isConnected()) {
                        String result = readFromInputStream(in);
                        if (result.equals("OK")) {
                            isConnected = true;
                            wait1.dismiss();
                        } else if (result.equals("G")) {
                        } else if (result.equals("D")) {
                            life_n = life_n - 10 >= 0 ? life_n - 10 : 0;

                            life.post(new Runnable() {
                                @Override
                                public void run() {
                                    life.getlife(life_n);
                                    life.invalidate();
                                }
                            });
                        }
                    }
                }
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public String readFromInputStream(InputStream in) {
        int count = 0;
        byte[] inData = null;
        try {
            while (count == 0) {
                count = in.available();
            }
            inData = new byte[count];
            in.read(inData);
            return new String(inData, "gb2312");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void receiverData() {
        // mTask = new ReceiverTask();
        receiverThread = new Thread(new MyReceiverRunnable());
        receiverThread.start();
        isConnected = true;
    }

    public void send(String str) {
        char len = (char) str.length();
        // sendThread.start();
        try {
            out = new PrintStream(mSocket.getOutputStream());
            out.print(len);
            out.print(str);
            out.flush();
            //printWriter.print(str);
            //printWriter.flush();
            //	Log.i(tag, "--->> client send data!");
        } catch (Exception e) {
            //Log.e(tag, "--->> send failure!" + e.toString());
        }
        //Toast.makeText(ControlActivity.this, "send", Toast.LENGTH_LONG).show();
    }

    private void connectThread() {
        if (!isConnected) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    connectServer();
                }
            }).start();
        } else {
            try {
                if (mSocket != null) {
                    mSocket.close();
                    mSocket = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            isConnected = false;
        }
    }

    protected void connectServer() {
        try {
            if (mSocket == null) {
                mSocket = new Socket(serverIP, serverPort);
            }
            OutputStream outputStream = mSocket.getOutputStream();
            printWriter = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(outputStream,
                            Charset.forName("gb2312"))));
            in = mSocket.getInputStream();
            send("player");
            send("playerX");
            receiverData();
        } catch (Exception e) {
            //isConnected = false;
            e.printStackTrace();
        }
    }

}
