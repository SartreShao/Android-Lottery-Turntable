package shaolizhi.mymusiclife;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class LotteryTurntableActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottery_turntable);
        LotteryTurntable lotteryTurntable = (LotteryTurntable) findViewById(R.id.lottery_turntable);
        ImageView startButton = (ImageView)findViewById(R.id.imageButton);
        lotteryTurntable.controller(startButton);
    }
}
