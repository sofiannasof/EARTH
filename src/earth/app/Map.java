package earth.app;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.ViewGroup;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class Map extends MapActivity implements LocationListener{
	static LocationManager locationManager;
	public static MapController mc;
	private static MyLocationOverlay overlayMe;
	MapView mapView;
	static int lat=0, lng=0;
	static Resources res;
	CharSequence[] dist;
	CharSequence[] pref;
	private static ProgressDialog dialog;
	public static Cursor c1;
	private boolean locLis = false;
	private boolean locAnim = true;
	private int count=0;
	static String eLat="0",eLng="0",radius="0";
	static String description="0";
	Road mRoad;
	public static String loc;
	//////////
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what==5){
				if(description.equals("0")){
					locAnim = false;
					dialog.dismiss();
					Toast.makeText(Map.this, getString(R.string.toast5), Toast.LENGTH_SHORT).show();
				}
				else{
					locAnim = true;
					mapView.getOverlays().add(0,new MapOverlay(mRoad));
					mapView.invalidate();
					dialog.dismiss();
					eLat = eLng = "0";
					Toast.makeText(Map.this, getString(R.string.toast4)+description+getString(R.string.toast41), Toast.LENGTH_SHORT).show();
				}
			}
			else if(msg.what!=0){
				locAnim = false;
				if(mapView.getOverlays().size()>0)
					mapView.getOverlays().clear();
				if (locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ))
					mapView.getOverlays().add(overlayMe);
				if(c1!=null){
					Drawable marker = res.getDrawable(R.drawable.i);
					marker.setBounds(0, 0, marker.getIntrinsicWidth(),marker.getIntrinsicHeight());
					mapView.getOverlays().add(new SitesOverlay(marker,c1));
					mapView.invalidate();
					dialog.dismiss();
					if(msg.what==1)
						Toast.makeText(Map.this, getString(R.string.toast1)+" "+count+" "+getString(R.string.toast11), Toast.LENGTH_SHORT).show();
					else if(msg.what==2)
						Toast.makeText(Map.this, getString(R.string.toast1)+" "+count+" "+getString(R.string.toast12), Toast.LENGTH_SHORT).show();
					else if(msg.what==3)
						if(count==0)
							Toast.makeText(Map.this, getString(R.string.toast2)+" "+radius+"...", Toast.LENGTH_SHORT).show();
						else
							Toast.makeText(Map.this, getString(R.string.toast1)+" "+count+" "+getString(R.string.toast12), Toast.LENGTH_SHORT).show();
					else if(msg.what==4)
			    		Toast.makeText(Map.this, getString(R.string.toast3), Toast.LENGTH_SHORT).show();	    								
				}			
				c1.close();
				Start.db.close();				
			}
		}	
	};
	private InputStream getConnection(String url) {
		InputStream is = null;
		try {
			URLConnection conn = new URL(url).openConnection();
			is = conn.getInputStream();
		} catch (MalformedURLException e) {
			System.out.println("Exception1:"+e);
		} catch (IOException e) {
			System.out.println("Exception1:"+e);
		}
		return is;
	}
	@Override
    public void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
			try{    
	        setContentView(R.layout.main_map);
	        
	        ViewGroup container = (ViewGroup) findViewById(R.id.map);
	        Animation anime = AnimationUtils.loadAnimation(Map.this, R.anim.map_start);
	 		container.startAnimation(anime);
	        mapView = (MapView) findViewById(R.id.mapview);
	        mapView.setBuiltInZoomControls(true);
	        mapView.setSatellite(true);
   	        mc = mapView.getController();
	        locLis = false;
	        locAnim = true;
	        res = getResources();
	        pref = res.getStringArray(R.array.pref_array);
	        dist = res.getStringArray(R.array.dist_array);
			locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		}
		catch(Exception e){
			System.out.println("WhereAmI (onCreate): "+e);
		}
	}
	public void onStart(){
		super.onStart();
		loc = getResources().getConfiguration().locale.getDisplayName();
		if(loc.startsWith("Ελληνικά"))
			Database.table = "eco_el";
		else
			Database.table = "eco_en";
		startLocationManager();
	}
	@Override
	protected boolean isRouteDisplayed() {
        return false;
    }
	private void startLocationManager(){
		try{
			if (!locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ))
				buildAlertMessageNoGps();
			//else{
			else if(!locLis){
				dialog = ProgressDialog.show(this,"",getString(R.string.dialog1),true,true);
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, Map.this);
				overlayMe  = new MyLocationOverlay(Map.this ,mapView);
				overlayMe.enableMyLocation();
				overlayMe.enableCompass();
				overlayMe.runOnFirstFix(new Runnable(){
					public void run(){
						mc.setZoom(15);
						mc.animateTo(overlayMe.getMyLocation());
					}
				});
				locLis=true;
				mapView.getOverlays().add(overlayMe);
			}
			if(lat!=0 && lng!=0)
				mc.animateTo(new GeoPoint(lat,lng));
		}
		catch(Exception e){
			System.out.println("startLocationManager: "+e);
		}
	}
	@Override
	public void onLocationChanged(Location location){
		try{
			lat = (int)(location.getLatitude()* 1000000);
			lng = (int)(location.getLongitude()* 1000000);
			if(locAnim)
				mc.animateTo(new GeoPoint(lat,lng));
			if(dialog.isShowing())
				dialog.dismiss();
		}
		catch(Exception e){
			System.out.println("onLocationChenged: "+e);
		}
	}
	@Override
	public void onProviderDisabled(String arg0) {}
	@Override
	public void onProviderEnabled(String provider) {}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {}

	private void buildAlertMessageNoGps() {
		try{
			final AlertDialog.Builder builder = new AlertDialog.Builder(Map.this);
			builder.setMessage(getString(R.string.gps))
			.setCancelable(false)
			.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
				public void onClick( DialogInterface dialog,  final int id) {  
					Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS); 
					startActivityForResult(intent, 0); 
				}
			})
			.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
				public void onClick(final DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			final AlertDialog alert = builder.create();
			alert.show();
		}
		catch(Exception e){
			System.out.println("WhereAmI (alert): "+e);
		}					
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {     
		super.onCreateOptionsMenu(menu);
		menu.add(0, 0, 0, getString(R.string.settings)).setIcon(R.drawable.settings).setIntent(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
		menu.add(0, 0, 1, getString(R.string.loc)).setIcon(R.drawable.home).setOnMenuItemClickListener(
				new OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {
						locAnim = true;
						if (!locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ))
							buildAlertMessageNoGps();
						else if(lat==0 && lng==0)
							dialog = ProgressDialog.show(Map.this,"",getString(R.string.dialog1),true,true);
						else
							mc.animateTo(new GeoPoint(lat,lng));
						return true;
					}
				});
		SubMenu moreMenu = menu.addSubMenu(0,0,2,R.string.search).setIcon(R.drawable.search);
		moreMenu.add(R.string.pref).setOnMenuItemClickListener(
				new OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {
						final AlertDialog.Builder builder = new AlertDialog.Builder(Map.this);
						builder.setTitle(R.string.pref)
						.setIcon(R.drawable.search)
						.setItems(pref, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialogInt, final int item) {
								dialog = ProgressDialog.show(Map.this,"",getString(R.string.dialog2),true,true);
								final String query = pref[item].toString();
								if(query.equals(getString(R.string.pref1)) || query.equals(getString(R.string.pref2)) ){
									new Thread() {
							            public void run() {
							            	Start.db.queryPrefMun(query);
											handler.sendEmptyMessage(1);
							            }
							        }.start();
								}
								else{
									new Thread() {
							            public void run() {
							            	Start.db.queryPref(query);
											handler.sendEmptyMessage(2);
							            }
							        }.start();
								}
							}
						});
						final AlertDialog alert = builder.create();
						alert.show();
						return true;
					}
				});
		moreMenu.add(R.string.dist).setOnMenuItemClickListener(
				new OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {
						if (!locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ))
							buildAlertMessageNoGps();
						else if(lat==0 || lng==0)
							dialog = ProgressDialog.show(Map.this,"",getString(R.string.dialog1),true,true);
						else{
							final AlertDialog.Builder builder = new AlertDialog.Builder(Map.this);
							builder.setTitle(R.string.dist)
							.setIcon(R.drawable.search)
							.setItems(dist, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialogInt, final int item) {
									dialog = ProgressDialog.show(Map.this,"",getString(R.string.dialog2),true,true);
									radius = dist[item].toString();
									new Thread() {
							            public void run() {
							            	Start.db.queryDist(radius,lat,lng);
											handler.sendEmptyMessage(3);
							            }
							        }.start();
								}
							});
							final AlertDialog alert = builder.create();
							alert.show();
						}
						return true;
					}
				});
		moreMenu.add(getString(R.string.near)).setOnMenuItemClickListener(
				new OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {
						if (!locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ))
							buildAlertMessageNoGps();
						else if(lat==0 || lng==0)
							dialog = ProgressDialog.show(Map.this,"",getString(R.string.dialog1),true,true);
						else{
							dialog = ProgressDialog.show(Map.this,"",getString(R.string.dialog2),true,true);
							new Thread() {
					            public void run() {
					            	Start.db.queryNear(lat,lng);
									handler.sendEmptyMessage(4);
					            }
					        }.start();
						}
						return true;
						
					}
				});;
		return true;
	}
	////////
	private class SitesOverlay extends ItemizedOverlay<OverlayItem>{
		public final List<OverlayItem> items=new ArrayList<OverlayItem>();
		private Drawable marker=null;
		public SitesOverlay(Drawable marker, Cursor c1) {
			super(marker);
			this.marker = marker;
			GeoPoint geoPoint = new GeoPoint(Map.lat,Map.lng);
			count = c1.getCount();
			eLat = eLng = "0";
			Map.mc.setZoom(12);
			while (c1.moveToNext()){
				geoPoint = new GeoPoint(c1.getInt(0),c1.getInt(1));
				items.add(new OverlayItem(geoPoint,c1.getColumnName(2), c1.getString(2)+"\n\n*"+c1.getString(3)+"*"));
				if(c1.getColumnCount()==5){
					eLat = String.valueOf(c1.getInt(0));
					eLng = String.valueOf(c1.getInt(1));
					Map.mc.setZoom(15);
				}
			}
			Map.mc.animateTo(geoPoint);
			populate();
		}
		@Override
		protected OverlayItem createItem(int i) {
			return(items.get(i));
		}
		@Override
		public void draw(Canvas canvas, MapView mapView,boolean shadow) {
			super.draw(canvas, mapView, shadow);
			boundCenterBottom(marker);
		}
		@Override
		protected boolean onTap(int i) {
			String title = items.get(i).getTitle();
			final String snippet = items.get(i).getSnippet();
			if(title.equals("mun")){
				final AlertDialog.Builder builder = new AlertDialog.Builder(Map.this);
				builder.setTitle(getString(R.string.mun1)+" "+snippet)
				.setMessage(getString(R.string.mun2))
				.setIcon(R.drawable.icon)
				.setCancelable(false)
				.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
					public void onClick( DialogInterface dialog,  final int id) {
						new Thread() {
				            public void run() {
								int i = snippet.indexOf("\n");
				            	Start.db.queryMun(snippet.substring(0, i));
								handler.sendEmptyMessage(2);
				            }
				        }.start();
					}
				})
				.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
				final AlertDialog alert = builder.create();
				alert.show();
			}
			else if(!eLat.equals("0") && !eLng.equals("0")){
				final AlertDialog.Builder builder = new AlertDialog.Builder(Map.this);
				builder.setTitle(getString(R.string.addr))
				.setMessage(snippet+"\n\n"+getString(R.string.path))
				.setIcon(R.drawable.info)
				.setCancelable(false)
				.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
					public void onClick( DialogInterface dialog,  final int id) {
						Map.dialog = ProgressDialog.show(Map.this,"",getString(R.string.dialog3),true,true);
						new Thread() {
				            public void run() {
				            	String sLat = String.valueOf(lat);
								if(lat>0)
									sLat = sLat.substring(0, 2)+"."+sLat.substring(2);
								else
									sLat = sLat.substring(0, 3)+"."+sLat.substring(3);
								String sLng = String.valueOf(lng);
								if(lng>0)
									sLng = sLng.substring(0, 2)+"."+sLng.substring(2);
								else
									sLng = sLng.substring(0, 3)+"."+sLng.substring(3);
								eLat = eLat.substring(0, 2)+"."+eLat.substring(2);
								eLng = eLng.substring(0, 2)+"."+eLng.substring(2);
								/////
								String url = RoadProvider.getUrl(sLat, sLng, eLat,eLng);
								InputStream is = getConnection(url);
								mRoad = RoadProvider.getRoute(is);
								
								int index1 = description.indexOf(":");
								int index2 = description.indexOf("(");
								if (index1 != -1 && index2!=-1)
									description = description.substring(index1+1, index2);
								else
									description = "0";
								handler.sendEmptyMessage(5);
				            }
				        }.start();
					}
				})
				.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
				final AlertDialog alert = builder.create();
				alert.show();
			}
			else{
				final AlertDialog.Builder builder = new AlertDialog.Builder(Map.this);
				builder.setTitle(getString(R.string.addr))
				.setMessage(snippet)
				.setIcon(R.drawable.info)
				.setCancelable(false)
				.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
					public void onClick( DialogInterface dialog,  final int id) { 
						dialog.cancel();
					}
				});
				final AlertDialog alert = builder.create();
				alert.show();
			}
			return(true);
		}
		@Override
		public int size() {
			return(items.size());
		}
	}
	class MapOverlay extends Overlay {
		ArrayList<GeoPoint> mPoints = new ArrayList<GeoPoint>();
		public MapOverlay(Road road) {
			if (road.mRoute.length > 0) 
				for (int i = 0; i < road.mRoute.length; i++)
					mPoints.add(new GeoPoint((int) (road.mRoute[i][1] * 1000000),	(int) (road.mRoute[i][0] * 1000000)));
		}
		@Override
		public boolean draw(Canvas canvas, MapView mv, boolean shadow, long when) {
			super.draw(canvas, mv, shadow);
			drawPath(mv, canvas);
			return true;
		}

		public void drawPath(MapView mv, Canvas canvas) {
			int x1 = -1, y1 = -1, x2 = -1, y2 = -1;
			Paint paint = new Paint();
			paint.setColor(Color.GREEN);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(3);
			Point point = new Point();
			for (int i = 0; i < mPoints.size(); i++) {
				mv.getProjection().toPixels(mPoints.get(i), point);
				x2 = point.x;
				y2 = point.y;
				if (i > 0)
					canvas.drawLine(x1, y1, x2, y2, paint);
				x1 = x2;
				y1 = y2;
			}
		}
	}
	
//DD
}