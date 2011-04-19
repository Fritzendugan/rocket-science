package com.quiz;

import java.util.ArrayList;

//import android.content.ContentValues;
import android.content.Context;
//import android.content.ContextWrapper;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
//import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class GameDBManager
{
	Context context;	//Activity that is creating an intance of this class
	private SQLiteDatabase db;
	
	private final String DB_NAME = "questions.db";	//database name
	private final int DB_VERSION = 1;
	
	private String TABLE = "null";
	private final String ROW_ID = "ID";			//Row ID column
	private final String QUESTION = "question";	//Questions column
	private final String T_OR_F = "torf";		//True or False
	private final String OPT_1 = "opt1";		//Option1
	private final String OPT_2 = "opt2";		//Option2
	private final String OPT_3 = "opt3";		//Option3
	private final String ANS = "ans";			//Correct answer
	
	
	public GameDBManager(Context context)
	{
		this.context = context;
		
		GameDBOpenHelper dbHelper = new GameDBOpenHelper(context);
		
		try
		{
			System.out.println("1. Openning database...");
			this.db = dbHelper.getReadableDatabase();
			System.out.println("1. Database Opened: " + db.getPath());
		}
		catch(SQLException sqe)
		{
			System.err.println(sqe.getMessage().toString());
			System.out.println("1 Error!!!");
		}
		System.out.println("1.1 Database Opened!!!");	
	}
	
	public void getTable(String name)
	{
		TABLE = name;
		System.out.println("Table is set to: " + TABLE);
	}
	
	/**
	 * Returns a row that matches a specified ID, as an ArrayList.
	 * @param qID
	 * @return
	 */
	public ArrayList<String> getQuestion(int qID)
	{
		ArrayList<String> question = new ArrayList<String>();
		Cursor cursor;
		
		try
		{
			//cursor = db.query(TABLE, 
			//		new String[] {ROW_ID, QUESTION, T_OR_F, OPT_1, OPT_2, OPT_3, ANS}, 
			//		ROW_ID + "='" + qID + "'", null, null, null, null, null);
			
			cursor = db.query(TABLE, 
					null, ROW_ID + "='" + qID+"'", null, null, null, null, null);
			
			cursor.moveToFirst();
			
			if(!cursor.isAfterLast())
			{
				do
				{
					question.add(cursor.getString(0));
					question.add(cursor.getString(1));
					question.add(cursor.getString(2));
					question.add(cursor.getString(3));
					question.add(cursor.getString(4));
					question.add(cursor.getString(5));
					question.add(cursor.getString(6));
					
				}
				while(cursor.moveToNext());
			}
			cursor.close();
		}
		catch(SQLException e)
		{
			Log.e("DB ERROR", e.getMessage().toString());
			e.printStackTrace();
		}
		
		return question;
	}

//	public ArrayList<String> curserToString()
	{
	//	ArrayList<String> returnMe = new ArrayList<String>();
		
	//	tempObject.
		
	//	return returnMe;
		
	}
	
	/**
	 * returns all questions from the database as an arraylist.
	 * @return
	 */
	public ArrayList<ArrayList<String>> getAllQuestions(ArrayList<Integer> valqID )
	{
		ArrayList<ArrayList<String>> questionArray = new ArrayList<ArrayList<String>>();
		Cursor cursor;
		
		for(int qID : valqID)
		{
			try
			{
				/*cursor = db.query(TABLE, 
						new String[]{ROW_ID, T_OR_F, QUESTION, OPT_1, OPT_2, OPT_3, ANS}, 
						null, null, null, null, null);*/
				
				cursor = db.query(TABLE, 
						null, ROW_ID + "='" + qID+"'", null, null, null, null, null);
				
				
				cursor.moveToFirst();	//move the cursor to position zero;
				
				//if there are data after the current position, add them to arrayList.
				if(!cursor.isAfterLast())
				{
					do
					{
						ArrayList<String> dataList = new ArrayList<String>();
						
						
						/*
						 * Get data in each column at the cursor position.
						 */
						dataList.add(cursor.getString(0));
						dataList.add(cursor.getString(1));
						dataList.add(cursor.getString(2));
						dataList.add(cursor.getString(3));
						dataList.add(cursor.getString(4));
						dataList.add(cursor.getString(5));
						dataList.add(cursor.getString(6));
						
						/*
						 * Add the current row in to the questionArray
						 */
						questionArray.add(dataList);	
					}
					while(cursor.moveToNext());
				}
			}
			catch(SQLException e)
			{
				Log.e("DB ERROR", e.getMessage().toString());
				e.printStackTrace();
			}
		}
		return questionArray;
	}
	
	
	
	
	/**
	 * This is an internal subclass that extends the SQLiteOpenHelper.
	 * This opens the database onCreate();
	 * This will check if the database exists and if not will throw an exception.
	 * @author Nethkelum
	 *
	 */
	private class GameDBOpenHelper extends SQLiteOpenHelper
	{
		public GameDBOpenHelper(Context context)
		{
			super(context, DB_NAME, null, DB_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) 
		{
			/*try
			{
				System.out.println("Openning database...");
				//db = openDatabase(DB_NAME, null, SQLiteDatabase.OPEN_READONLY);
				System.out.println("Database Opened");
			}
			catch(SQLException sqe)
			{
				System.err.println(sqe.getMessage().toString());
			}
			System.out.println("Database Opened!!!");*/
		}

		@Override
		public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) 
		{
			// TODO Auto-generated method stub
			
		}
	}

}

