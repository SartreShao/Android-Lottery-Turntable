package shaolizhi.mymusiclife;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SurfaceViewActivity extends AppCompatActivity {
    WheelOfFortune wheelOfFortune;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surface_view);
        wheelOfFortune = (WheelOfFortune)findViewById(R.id.wheelOfFortune);
        wheelOfFortune.getActivity(this);
    }
}
