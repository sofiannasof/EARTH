package earth.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.style.URLSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

public class Credits extends Activity {
 @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.credits);
        
        ViewGroup container = (ViewGroup) findViewById(R.id.credits);        
 		Animation anime = AnimationUtils.loadAnimation(Credits.this, R.anim.credits_start);
 		container.startAnimation(anime);
 		
 		TextView address = (TextView) findViewById(R.id.dept);
 		final URLSpan[] url = address.getUrls();
 		final AlertDialog.Builder ad = new AlertDialog.Builder(this);
        
        address.setOnClickListener(
      	      new ImageButton.OnClickListener() {
      	         public void onClick(View v) {
      	        	ad.setTitle(R.string.attention)
					.setMessage(R.string.url)
					.setIcon(R.drawable.help)
					.setCancelable(true)
					.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							Intent intent = new Intent(Intent.ACTION_VIEW);
							Uri uri = Uri.parse(url[0].getURL());
							intent.setData(uri);
							startActivity(intent);
						}
					})
					.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					})
					.show();
			     }
      	});
 	}
 }