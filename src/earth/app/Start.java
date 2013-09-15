package earth.app;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

public class Start extends Activity{
	public static Database db;
	AlertDialog.Builder ad;
	private ViewGroup container;
	AnimationDrawable animation;
	ImageButton globe_button,info_button,credits_button;
	private int intent=0;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start);
		
		container = (ViewGroup) findViewById(R.id.start);
		ad = new AlertDialog.Builder(this);
		//////////////
		globe_button = (ImageButton) findViewById(R.id.globe);
		Animation anime = AnimationUtils.loadAnimation(this, R.anim.start);
		globe_button.setAdjustViewBounds(true);
		
		animation = new AnimationDrawable();
		animation.addFrame(getResources().getDrawable(R.drawable.g3), 500);
		animation.addFrame(getResources().getDrawable(R.drawable.g1), 500);
		animation.addFrame(getResources().getDrawable(R.drawable.g2), 500);
		animation.addFrame(getResources().getDrawable(R.drawable.g0), 500);
		animation.addFrame(getResources().getDrawable(R.drawable.g2), 500);
		animation.addFrame(getResources().getDrawable(R.drawable.g1), 500);
		animation.setOneShot(false);
		
		globe_button.setBackgroundDrawable(animation);
		globe_button.startAnimation(anime);
		globe_button.post(new Starter());
		globe_button.setOnClickListener(
				new ImageButton.OnClickListener() {
					public void onClick(View v) {
						intent=1;
						Intent intent = new Intent(getApplicationContext(), Map.class);
						startActivity(intent);
					}
				});
	   ///////////////
	   info_button = (ImageButton)this.findViewById(R.id.info);
	   info_button.setAdjustViewBounds(true);
	   info_button.setOnClickListener(
			   new ImageButton.OnClickListener() {
				   public void onClick(View v) {
					   intent=2;
					   Intent intent = new Intent(getApplicationContext(), Info.class);
					   startActivity(intent);
				   }
			   });
	   ////////////////
	   credits_button = (ImageButton)this.findViewById(R.id.mpes);
	   credits_button.setAdjustViewBounds(true);
	   credits_button.setOnClickListener(
			   new ImageButton.OnClickListener() {
				   public void onClick(View v) {
					   intent=3;
					   Intent intent = new Intent(getApplicationContext(), Credits.class);
					   startActivity(intent);
					   
				   }
			   });
	   ///////////////
	   db = new Database(this);
	   try {
			db.createDataBase();
		} catch (IOException e) {
			e.printStackTrace();
		}
		db.close();
	}
	public void onStart(){
		super.onStart();
		if(intent==1){
			intent=0;
			Animation anime = AnimationUtils.loadAnimation(this, R.anim.map_exit);
			container.startAnimation(anime);
		}
		if(intent==2){
			intent=0;
			Animation anime = AnimationUtils.loadAnimation(this, R.anim.info_exit);
			container.startAnimation(anime);
		}
		else if(intent==3){
			intent=0;
			Animation anime = AnimationUtils.loadAnimation(this, R.anim.credits_exit);
			container.startAnimation(anime);
		}
	}
   @Override
   public boolean onCreateOptionsMenu(Menu menu) {     
		super.onCreateOptionsMenu(menu);
		menu.add(0, 0, 1, getString(R.string.help)).setIcon(R.drawable.help).setOnMenuItemClickListener(
				new OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {
						final AlertDialog alertDialog = new AlertDialog.Builder(Start.this).create();
				    	alertDialog.setTitle(getString(R.string.name));
				    	alertDialog.setButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
				    	      public void onClick(DialogInterface dialog, int which) {
				    	    	  alertDialog.dismiss();
				  		    } });
				    	alertDialog.setIcon(R.drawable.info);
				    	alertDialog.setMessage(getString(R.string.help_text));
				    	alertDialog.show();
				    	return true;
				    }
				});
		menu.add(0, 0, 2, getString(R.string.exit)).setIcon(R.drawable.exit).setOnMenuItemClickListener(
				new OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {
						ad.setTitle(getString(R.string.attention));
						ad.setMessage(getString(R.string.close));
						ad.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								finish();
								System.exit(0);
							}
						});
						ad.setCancelable(true);
						ad.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
						ad.show();
						return true;
					}
				});
		return true;
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	    	ad.setTitle(getString(R.string.attention));
			ad.setMessage(getString(R.string.close));
			ad.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					finish();
					System.exit(0);
				}
			});
			ad.setCancelable(true);
			ad.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			ad.show();
			return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	//animation
	private class Starter implements Runnable {
		public void run() {
			animation.start();
		}
	}
}

