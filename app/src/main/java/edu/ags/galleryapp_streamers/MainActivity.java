package edu.ags.galleryapp_streamers;

import android.graphics.Typeface;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import edu.ags.galleryapp_streamers.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {

    Streamers[] streamers = {
            new Streamers("TimTheTatman", "descT"),
            new Streamers("Nickmercs", "descN"),
            new Streamers("Dr Disrespect", "descD")
    };

    int[] imgs = {R.drawable.tim, R.drawable.nick, R.drawable.doc};
    int[] textfiles = {R.raw.tim, R.raw.nick, R.raw.doc};
    int[] imgs2 = {R.drawable.timrage, R.drawable.nickrage, R.drawable.docrage};

    public static final String TAG = "myDebug";
    private GestureDetector gestureDetector;
    private int cardNo = 0;
    private boolean isFront = true;

    private TextView tvCard;
    private ImageView imgCard;


    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //Need this or else the app crashes on touch
        //instantiate the gesture listener
        gestureDetector = new GestureDetector(this, this);

        tvCard = findViewById(R.id.tvCard);
        imgCard = findViewById(R.id.imgCard);

        streamers[cardNo].desc = readFile(textfiles[cardNo]);

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Log.d(TAG, "onCreate: ");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {

        Log.d(TAG, "onSingleTapUp: You made it to Single tap");
        String message;

        try {
            if (isFront) {
                //show the back
                message = "Go to Back";
                imgCard.setImageResource(imgs2[cardNo]);
                tvCard.setText(streamers[cardNo].desc);

                Typeface face = Typeface.MONOSPACE;
                tvCard.setTypeface(face);
                tvCard.setTextSize(18);

            } else {
                //show the front
                message = "Go to Front";
                imgCard.setVisibility(View.VISIBLE);
                imgCard.setImageResource(imgs[cardNo]);
                tvCard.setText(streamers[cardNo].name);

                Typeface face = Typeface.SERIF;
                tvCard.setTypeface(face);
                tvCard.setTextSize(32);
            }

            isFront = !isFront;
            Log.d(TAG, "TapTap " + message);
            return true;

        } catch (Exception e) {
            Log.d(TAG, "Error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        Log.d(TAG, "On Fling: ");

        try {
            //decide fling direction
            int x1 = (int) motionEvent.getX();
            int x2 = (int) motionEvent1.getX();

            int numCards = streamers.length;

            if (x1 < x2) {
                Log.d(TAG, "onFling: Right");
                Animation moveRight = AnimationUtils.loadAnimation(this, R.anim.moveright);
                moveRight.setAnimationListener(new AnimationListener());
                imgCard.startAnimation(moveRight);
                tvCard.startAnimation(moveRight);

                cardNo = (cardNo + numCards - 1) % numCards;
            } else {
                Log.d(TAG, "onFling: Left");
                Animation moveLeft = AnimationUtils.loadAnimation(this, R.anim.moveleft);
                moveLeft.setAnimationListener(new AnimationListener());
                imgCard.startAnimation(moveLeft);
                tvCard.startAnimation(moveLeft);
                cardNo = (cardNo + 1) % numCards;
            }

            return true;
        } catch (Exception e) {
            Log.d(TAG, "onFling: Error" + e.getMessage());
        }

        return false;
    }

    //Add for touch events. Need to manually add this
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        return gestureDetector.onTouchEvent(motionEvent);
    }

    private void updateToNextCard()
    {
        streamers[cardNo].desc = readFile(textfiles[cardNo]);
        isFront = true;
        imgCard.setVisibility(View.VISIBLE);
        imgCard.setImageResource(imgs[cardNo]);
        tvCard.setText(streamers[cardNo].name);
    }

    private class AnimationListener implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            Log.d(TAG, "onAnimationEnd: ");
            updateToNextCard();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }


    private String readFile(int fileID)
    {
        InputStream inputStream;
        InputStreamReader inputStreamReader;
        BufferedReader bufferedReader;
        StringBuffer stringBuffer;

        try {
            inputStream = getResources().openRawResource(fileID);
            inputStreamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
            stringBuffer = new StringBuffer();

            String data;

            while((data = bufferedReader.readLine()) !=null)
            {
                stringBuffer.append(data + "\n");
            }

            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();

            return stringBuffer.toString();
        }
        catch (Exception e) {

            e.printStackTrace();
            return e.getMessage();

        }

    }




}