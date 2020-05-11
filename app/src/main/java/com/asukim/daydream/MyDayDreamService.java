package com.asukim.daydream;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.service.dreams.DreamService;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.logging.Logger;

public class MyDayDreamService extends DreamService {
    Thread myThread = null;

    public static final String MESSAGE_TYPE_INBOX = "1";
    public static final String MESSAGE_TYPE_SENT = "2";
    public static final String MESSAGE_TYPE_CONVERSATIONS = "3";
    public static final String MESSAGE_TYPE_NEW = "new";

    final static private String[] CALL_PROJECTION = {CallLog.Calls.TYPE,
            CallLog.Calls.CACHED_NAME, CallLog.Calls.NUMBER,
            CallLog.Calls.DATE, CallLog.Calls.DURATION};

    private static final String TAG = "Victor-Manage_Clique";

    private Cursor getCallHistoryCursor(Context context) {
        Cursor cursor = context.getContentResolver().query(
                CallLog.Calls.CONTENT_URI, CALL_PROJECTION,
                null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
        return cursor;
    }


    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        setInteractive(true);
        setFullscreen(true);

        RelativeLayout r1 = new RelativeLayout(this);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(300, 250);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);

        TextView textView = new TextView(this);
        textView.setId(1);
        textView.setTextSize(15);
        textView.setLayoutParams(params);

        r1.addView(textView, params);

        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(300, 250);
        params2.addRule(RelativeLayout.BELOW, 1);
        params2.addRule(RelativeLayout.CENTER_HORIZONTAL);
        ImageView iv = new ImageView(this);
        iv.setImageResource(R.drawable.android);
        iv.setLayoutParams(params2);

        r1.addView(iv, params2);

        setContentView(r1);
        setContentView(R.layout.activity_main);

        TextView dateText = (TextView) findViewById(R.id.dateText);
        TextView timeText = (TextView) findViewById(R.id.timeText);
        TextView ampmText = (TextView) findViewById(R.id.ampmText);
        final Button textBtn = (Button) findViewById(R.id.textBtn);
        textBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView text = (TextView) findViewById(R.id.text);
                text.setText(COUNTRIES[(int) (Math.random() * COUNTRIES.length)]);
                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_anim);
                text.startAnimation(anim);
            }
        });


        Date dt = new Date();
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int hours = calendar.get(Calendar.HOUR_OF_DAY) % 12;
        int hours1 = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        int AM_PM = calendar.get(Calendar.AM_PM);

        String hourString = addZero(hours);
        String minuteString = addZero(minutes);

        timeText.setText(hourString + ":" + minuteString);
        timeText.setText((hours1 == 12) ? "12" + ":" + minuteString : hourString + ":" + minuteString);
        ampmText.setText((AM_PM == 1) ? "pm" : "am");
        String curday = year + "/" + month + "/" + day;
        dateText.setText(curday);

        Runnable myRunnableThread = new CountDownRunner();
        myThread = new Thread(myRunnableThread);
        myThread.start();
        callLog();
    }

    class CountDownRunner implements Runnable {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private String addZero(int time) {
        String timeString = (time < 10) ? "0" + time : "" + time;
        return timeString;
    }

    private void callLog() {
        int callcount = 0;
        String callname = "";
        String calltype = "";
        String calllog = "";
        Cursor curCallLog = getCallHistoryCursor(this);
        Log.i(TAG, "processSend() - 1");
        Log.i(TAG, "CallLog: " + curCallLog.getCount());
        Log.i(TAG, "CallLog: " + MESSAGE_TYPE_CONVERSATIONS);

        if (curCallLog.moveToFirst() && curCallLog.getCount() > 0) {
            while (curCallLog.isAfterLast() == false) {
                StringBuffer sb = new StringBuffer();
                sb.append(timeToString(curCallLog.getLong(curCallLog.getColumnIndex(CallLog.Calls.DATE))));
                sb.append("\t").append(calltype);
                sb.append("\t").append(callname);
                sb.append("\t").append(curCallLog.getString(curCallLog.getColumnIndex(CallLog.Calls.NUMBER)));
                curCallLog.moveToNext();

                String backupData = sb.toString();

                callcount++;
                Log.i("call history[", sb.toString());
            }
        }
        TextView callText = (TextView) findViewById(R.id.callText);
        callText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        callText.setText("부재중 : " + MESSAGE_TYPE_CONVERSATIONS);
    }

    private String timeToString(Long time) {
        SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = simpleFormat.format(new Date(time));
        return date;
    }

    private static final String[] COUNTRIES = new String[]{
            "가장 유능한 사람은 가장 배움에 힘쓰는 사람이다. -괴테",
            "기하학에는 왕도가 없다.(학문에는 왕도가 없다.) -유클리드",
            "오늘 할 수 있는 일에만 전력을 쏟으라. -뉴턴",
            "일은 그것이 쓰일 수 있는 시간이 있는 만큼 팽창한다. -파킨스",
            "독수리는 마지막 성공을 거둘 때까지 온 생명을 바쳐 노력한다. -여안교",
            "방황과 변화를 사랑한다는 것은 살아 있다는 증거이다. -바그너",
            "짧은 인생은 시간의 낭비에 의해 더욱 짧아진다. -S. 존슨",
            "성공은 결과이지 목적은 아니다. -G. 플로베르",
            "성공은 그 사람의 성격이나 인격을 높게 한다. -W.S. 모옴",
            "성공은 수만 번의 실패를 감싸준다. -조지 버나드 쇼",
            "성공을 거두기 위하여 필요한 것은 계산된 모험이다. -디오도어 루빈",
            "아무리 위대한 일도 열심히 하지 않고 성공된 예는 없다. -R.W. 에머슨",
            "어려운 일은 시간이 해결해준다. -이솝",
            "가장 귀중한 재산은 사려깊고 헌신적인 친구이다. -다리우스",
            "그 사람을 모르거든 그 벗을 보라. -메난드로스",
            "노력이 적으면 얻는 것도 적다. 인간의 재산은 그의 노고에 달렸다. -헤리크",
            "노력하는 데 있어서 이득을 바라지 마라. -도교",
    };
}