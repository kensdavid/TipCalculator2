package com.kensdavid.randomquotes;


import java.util.Random;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {
	int id = 0;
	public static final String KEY_ROWID="_id";
	public static final String KEY_TIP = "defaultTip";
	public static final String KEY_TAX = "defaultTax";
	private static final String TAG = "DBAdapter";
	
	private static final String DATABASE_NAME = "Random";
	private static final String DATABASE_TABLE = "tblTipCalc";
	private static final int DATABASE_VERSION = 1;
	
	//private static final String DATABASE_CREATE = "create table if not exists tblTipCalc (_id integer primary key autoincrement, "
	//		+ "Quote text not null );";
	
	//By default, set default tip = 18.0 and default tax = 8.875
	private static final String DATABASE_CREATE = "create table if not exists tblTipCalc (_id integer primary key autoincrement, "
			+ "defaultTip REAL, defaultTax REAL );";
	
	private final Context context;
	
	private DatabaseHelper DBHelper;
	private SQLiteDatabase db;
	
	public DBAdapter(Context ctx)
	{
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}
	
	private static class DatabaseHelper extends SQLiteOpenHelper
	{
		DatabaseHelper(Context context)
		{
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db)
		{
			ContentValues values = new ContentValues();
			values.put(KEY_TAX, 8.875);
			values.put(KEY_TIP, 18.0);
			db.execSQL(DATABASE_CREATE);
			db.insert(DATABASE_TABLE, null, values);
		}
		
		@Override
		public void onOpen(SQLiteDatabase db)
		{
			db.execSQL(DATABASE_CREATE);
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			Log.w(TAG, "Upgrading database from version " + oldVersion
					+ " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS tblTipCalc");
			onCreate(db);
		}
	}
	
	//Opens the database
	public DBAdapter open() throws SQLException
	{
		db = DBHelper.getWritableDatabase();
		return this;
	}
	
	//Closes the database
	public void close()
	{
		DBHelper.close();
	}
	
	/*//insert a quote into the database
	public long insertQuote(String Quote)
	{
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_QUOTE, Quote);
		return db.insert(DATABASE_TABLE, null, initialValues);
	}*/
	
	//Update the defaultTip Percentage
	public long updateTip(Double tipPct)
	{
		ContentValues values = new ContentValues();
		values.put(KEY_TIP, tipPct);
		return db.update(DATABASE_TABLE, values, "_id=1", null);
	}
	
	//Update the defaultTip Percentage
	public long updateTax(Double taxPct)
	{
		ContentValues values = new ContentValues();
		values.put(KEY_TAX, taxPct);		
		return db.update(DATABASE_TABLE, values, "_id=1", null);
	}
	
	public double getDefaultTip()
	{
		Cursor cursor = db.rawQuery("SELECT defaultTip FROM tblTipCalc where _id = 1", null);
		
		if(cursor.moveToFirst())
		{
			return cursor.getInt(0);
		}
		return cursor.getInt(0);
	}
	
	public double getDefaultTax()
	{
		Cursor cursor = db.rawQuery("SELECT defaultTax FROM tblTipCalc where _id = 1", null);
		
		if(cursor.moveToFirst())
		{
			return cursor.getInt(0);
		}
		return cursor.getInt(0);
	}
	
	/*
	public String showAllEntries()
	{
		Cursor cursor;
		cursor = db.rawQuery("SELECT Quote FROM tblTipCalc", null);
		
		cursor.moveToFirst();
		int Counter = 1;
		String returnString = Counter + " - " + cursor.getString(0) + "\n";
		Counter++;
		
		while(cursor.moveToNext())
		{			
			returnString = returnString + Counter + " - " + cursor.getString(0) + "\n";
			Counter++;
		}
		
		return returnString.substring(0, returnString.length()-1);
	}
	*/
	
	/*
	public String getRandomEntry()
	{
		//id = getAllEntries();
		Random random = new Random();
		int rand = random.nextInt(getAllEntries()) + 1;
		Cursor cursor = db.rawQuery("SELECT Quote FROM tblTipCalc WHERE _id = " + rand, null);
		if(cursor.moveToFirst())
		{
			return cursor.getString(0);
		}
		return cursor.getString(0);
	}
	*/
	
	public void clearData()
	{
		db.execSQL("DROP TABLE IF EXISTS tblTipCalc");		
	}
}
