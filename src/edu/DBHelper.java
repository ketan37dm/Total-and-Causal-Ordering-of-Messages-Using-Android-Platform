package edu.buffalo.cse.cse486586.groupmessenger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
	public String tableName = "messages";
	
	public DBHelper(Context context, String dbName, CursorFactory factory, int version) {
		super(context, dbName, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		String sql_query = "CREATE TABLE IF NOT EXISTS messages(key STRING, value STRING)";
		arg0.execSQL(sql_query);
		Log.v("DBHelper - OnCreate", "Table created");
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
	}
}
