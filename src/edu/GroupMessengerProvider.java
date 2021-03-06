package edu.buffalo.cse.cse486586.groupmessenger;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

/**
 * GroupMessengerProvider is a key-value table. Once again, please note that we do not implement
 * full support for SQL as a usual ContentProvider does. We re-purpose ContentProvider's interface
 * to use it as a key-value table.
 * 
 * Please read:
 * 
 * http://developer.android.com/guide/topics/providers/content-providers.html
 * http://developer.android.com/reference/android/content/ContentProvider.html
 * 
 * before you start to get yourself familiarized with ContentProvider.
 * 
 * There are two methods you need to implement---insert() and query(). Others are optional and
 * will not be tested.
 * 
 * @author stevko
 *
 */

public class GroupMessengerProvider extends ContentProvider {
	private static DBHelper dbHelper;
	private static String uriString = "content://edu.buffalo.cse.cse486586.groupmessenger.provider/" + "ds_development";
	private static Uri contentUri = Uri.parse(uriString);
	
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // You do not need to implement this.
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        // You do not need to implement this.
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        /*
         * TODO: You need to implement this method. Note that values will have two columns (a key
         * column and a value column) and one row that contains the actual (key, value) pair to be
         * inserted.
         * 
         * For actual storage, you can use any option. If you know how to use SQL, then you can use
         * SQLite. But this is not a requirement. You can use other storage options, such as the
         * internal storage option that I used in PA1. If you want to use that option, please
         * take a look at the code for PA1.
         */
    	SQLiteDatabase db = dbHelper.getWritableDatabase();
    	long result = db.insert(dbHelper.tableName, null, values);
    	if(result > 0){
    		Uri new_uri = ContentUris.withAppendedId(contentUri, result);
			getContext().getContentResolver().notifyChange(new_uri, null);
			Log.v("provider-insert", "insert successful : " + values.toString());
			return new_uri;
    	}else{
    		Log.v("provider-insert", "insertion failed");
    	}
        return uri;
    }

    @Override
    public boolean onCreate() {
        // If you need to perform any one-time initialization task, please do it here.
    	dbHelper = new DBHelper(this.getContext(), "ds_development", null, 1);
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        /*
         * TODO: You need to implement this method. Note that you need to return a Cursor object
         * with the right format. If the formatting is not correct, then it is not going to work.
         * 
         * If you use SQLite, whatever is returned from SQLite is a Cursor object. However, you
         * still need to be careful because the formatting might still be incorrect.
         * 
         * If you use a file storage option, then it is your job to build a Cursor * object. I
         * recommend building a MatrixCursor described at:
         * http://developer.android.com/reference/android/database/MatrixCursor.html
         */
    	SQLiteDatabase db = dbHelper.getReadableDatabase();
    	String query = "SELECT * FROM " + dbHelper.tableName + " WHERE key LIKE '"+ selection + "'";
    	Cursor cursor = db.rawQuery(query, null);
        Log.v("provider-query", selection);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // You do not need to implement this.
        return 0;
    }
}
