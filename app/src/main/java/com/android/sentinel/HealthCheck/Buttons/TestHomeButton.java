package com.android.sentinel.HealthCheck.Buttons;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.sentinel.HealthCheck.HealthCheck;
import com.android.sentinel.R;

import static com.android.sentinel.HealthCheck.TestFragment.FAILED;
import static com.android.sentinel.HealthCheck.TestFragment.FROM;
import static com.android.sentinel.HealthCheck.TestFragment.HOME;
import static com.android.sentinel.HealthCheck.TestFragment.POWER;
import static com.android.sentinel.HealthCheck.TestFragment.SUCCESS;
import static com.android.sentinel.HealthCheck.TestFragment.UNCHECKED;
import static com.android.sentinel.HealthCheck.TestFragment.setDefaults;

public class TestHomeButton extends AppCompatActivity {
    HomeWatcher mHomeWatcher;
    Runnable timerTask;
    Handler handler;
    TextView result, skip, explanation;
    Button btn;
    ProgressBar progressBar;
    Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_test_home_button);
        result = findViewById(R.id.result);
        skip = findViewById(R.id.skip);
        explanation = findViewById(R.id.explain);
        btn = findViewById(R.id.button);
        progressBar = findViewById(R.id.progress);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDefaults(HOME, UNCHECKED, TestHomeButton.this);
                if (getIntent().getExtras() != null) {
                    String val = getIntent().getStringExtra(FROM);
                    if (val.equals(POWER)) {
                        Intent intent = new Intent(TestHomeButton.this,
                                TestVolume.class);
                        intent.putExtra(FROM, HOME);
                        startActivity(intent);
                    }
                } else {
                    finish();
                }
            }
        });

        handler = new Handler();
        runProgress();
        timer(10000);
        mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(new OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                setPass();
            }

            @Override
            public void onHomeLongPressed() {
                setPass();
            }
        });

        mHomeWatcher.startWatch();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setDefaults(HOME, UNCHECKED, this);
                Intent intent = new Intent(this, HealthCheck.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setDefaults(HOME, UNCHECKED, this);
    }

    private void timer(long delayMillis) {
       timerTask = new Runnable() {
            @Override
            public void run() {
                setFail();
            }
        };

        handler.postDelayed(timerTask, delayMillis);
    }


    private void runProgress() {
        thread = new Thread(){
            @Override
            public void run(){
                super.run();
                for (int i = 0; i <= 10; i++ ){
                    if (i > 0) {
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    progressBar.setMax(10);
                    progressBar.setProgress(i);
                }
            }
        };
        thread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHomeWatcher.stopWatch();
        handler.removeCallbacks(timerTask);
    }

    private void setPass() {
        setDefaults(HOME, SUCCESS, TestHomeButton.this);
        result.setText("PASS");
        result.setTextColor(getResources().getColor(R.color.green));
        btn.setText("Continue");
        btn.setVisibility(View.VISIBLE);
        skip.setVisibility(View.GONE);
        explanation.setVisibility(View.GONE);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getIntent().getExtras() != null) {
                    String val = getIntent().getStringExtra(FROM);
                    if (val.equals(POWER)) {
                        Intent intent = new Intent(TestHomeButton.this,
                                TestVolume.class);
                        intent.putExtra(FROM, HOME);
                        startActivity(intent);
                    }
                } else {
                    finish();
                }
            }
        });
        progressBar.setVisibility(View.GONE);
        handler.removeCallbacks(timerTask);
    }

    private void setFail() {
        setDefaults(HOME, FAILED, TestHomeButton.this);
        result.setText("FAIL");
        result.setTextColor(getResources().getColor(R.color.colorPrimary));
        btn.setText("Continue");
        progressBar.setVisibility(View.GONE);
        btn.setVisibility(View.VISIBLE);
        skip.setVisibility(View.GONE);
        explanation.setVisibility(View.GONE);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getIntent().getExtras() != null) {
                    String val = getIntent().getStringExtra(FROM);
                    if (val.equals(POWER)) {
                        Intent intent = new Intent(TestHomeButton.this,
                                TestVolume.class);
                        intent.putExtra(FROM, HOME);
                        startActivity(intent);
                    }
                } else {
                    finish();
                }
            }
        });

    }

    public class HomeWatcher {
        private Context mContext;
        private IntentFilter mFilter;
        private OnHomePressedListener mListener;
        private InnerReceiver mReceiver;

        public HomeWatcher(Context context) {
            mContext = context;
            mFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        }

        public void setOnHomePressedListener(OnHomePressedListener listener) {
            mListener = listener;
            mReceiver = new InnerReceiver();
        }

        public void startWatch() {
            if (mReceiver != null) {
                mContext.registerReceiver(mReceiver, mFilter);
            }
        }

        public void stopWatch() {
            if (mReceiver != null) {
                mContext.unregisterReceiver(mReceiver);
            }
        }

        class InnerReceiver extends BroadcastReceiver {
            final String SYSTEM_DIALOG_REASON_KEY = "reason";
            final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
            final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                    String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                    if (reason != null) {
                        if (mListener != null) {
                            if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                                mListener.onHomePressed();
                            } else if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                                mListener.onHomeLongPressed();
                            }
                        }
                    }
                }
            }
        }
    }

    public interface OnHomePressedListener {
        void onHomePressed();
        void onHomeLongPressed();
    }
}

