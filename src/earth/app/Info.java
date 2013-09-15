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

public class Info extends Activity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);

        ViewGroup container = (ViewGroup) findViewById(R.id.infoeco);
 		Animation anime = AnimationUtils.loadAnimation(Info.this, R.anim.info_start);
 		container.startAnimation(anime);
 		
 		TextView address = (TextView) findViewById(R.id.address);
 		final URLSpan[] url = address.getUrls();
 		final AlertDialog.Builder ad = new AlertDialog.Builder(this);
     
        address.setOnClickListener(
      	      new ImageButton.OnClickListener() {
      	         public void onClick(View v) {
      	        	ad.setTitle(R.string.attention)
      	        	.setIcon(R.drawable.help)
					.setMessage(R.string.url)
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
        
        TextView address2 = (TextView) findViewById(R.id.address2); 
        final URLSpan[] url2 = address2.getUrls();
 		final AlertDialog.Builder ad2 = new AlertDialog.Builder(this);
     
        address2.setOnClickListener(
      	      new ImageButton.OnClickListener() {
      	         public void onClick(View v) {
      	        	ad2.setTitle(R.string.attention)
      	        	.setIcon(R.drawable.help)
					.setMessage(R.string.url)
					.setCancelable(true)
					.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							Intent intent = new Intent(Intent.ACTION_VIEW);
							Uri uri = Uri.parse(url2[0].getURL());
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