package flyingantsstudios.com.mypal;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.TextView;

/**
 * Created by shadabbaig on 02/04/19.
 */

public class PopUpService extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_window);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((int) (width * 0.7), (int) (height * 0.6));

        // Get the transferred data from source activity.
        Intent intent = getIntent();
        String message = intent.getStringExtra("popupMessage");
        TextView textView = (TextView)findViewById(R.id.popup_text);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setText(message);

    }
}
