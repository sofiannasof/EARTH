package earth.app;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper{
	 
	private static String DB_PATH = "/data/data/earth.app/databases/ECO";
	private static String DB_NAME = "ECO";
	private SQLiteDatabase myDB; 
	private final Context myContext;
	public static String table="eco_en";

	public Database(Context context) {
		super(context, DB_NAME, null, 1);
		this.myContext = context;
	}	
	public void createDataBase() throws IOException{
		boolean dbExist = checkDataBase();
		if(!dbExist){
			this.getReadableDatabase();			
			try {
				copyDataBase();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}		
	}
	private boolean checkDataBase(){
		SQLiteDatabase checkDB = null;
		int count = 0;
		try{
			checkDB = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READONLY);
		}catch(SQLiteException e){
			e.printStackTrace();
		}
		if(checkDB != null)
			checkDB.close();
		return checkDB != null && count!=0 ? true : false;
	}

	private void copyDataBase() throws IOException{
		InputStream myInput = myContext.getAssets().open(DB_NAME);
		OutputStream myOutput = new FileOutputStream(DB_PATH);
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer))>0){
			myOutput.write(buffer, 0, length);
		}
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}
	public void queryPref(CharSequence query) throws SQLException{
		myDB = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READONLY);
		Map.c1 = myDB.rawQuery("select lat,lng,addr,info from "+table+" where pref=='"+query.toString()+"'", null);
	}
	public void queryPrefMun(CharSequence query) throws SQLException{
		myDB = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READONLY);
		Map.c1 = myDB.rawQuery("select lat,lng,mun,addr from "+table+" where pref=='"+query.toString()+"' group by mun", null);
	}
	public void queryMun(CharSequence query) throws SQLException{
		myDB = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READONLY);
		Map.c1 = myDB.rawQuery("select lat,lng,addr,info from "+table+" where mun=='"+query.toString()+"'", null);
	}
	public void queryDist(CharSequence query, int lat, int lng) throws SQLException{
		myDB = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READONLY);
		int dist = 12000 * Integer.parseInt(query.subSequence(0, query.length()-2).toString());
		Map.c1 = myDB.rawQuery("select lat,lng,addr,info " +
								"from(select lat,lng,addr,info, (((lat-("+lat+"))*(lat-("+lat+")))+((lng-("+lng+"))*(lng-("+lng+")))) as dist " +
									"from "+table+")temp " +
								"where dist<("+dist+"*"+dist+")", null);
	}
	public void queryNear(int lat, int lng) throws SQLException{
		myDB = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READONLY);
		Map.c1 = myDB.rawQuery("select lat,lng,addr,info,dist " +
								"from(select lat,lng,addr,info,(((lat-("+lat+"))*(lat-("+lat+")))+((lng-("+lng+"))*(lng-("+lng+")))) as dist " +
										"from "+table+")temp " +
								"where dist=(select min(dist) " +
											"from( select lat,lng,addr,info, (((lat-("+lat+"))*(lat-("+lat+")))+((lng-("+lng+"))*(lng-("+lng+")))) as dist " +
											"from "+table+")temp)", null);
	}
	@Override
	public synchronized void close(){
		if(myDB != null)
			myDB.close();
		super.close();
	}
	@Override
	public void onCreate(SQLiteDatabase db) {}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
		
}