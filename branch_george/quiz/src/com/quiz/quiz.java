package com.quiz;

import java.util.ArrayList;
import java.util.Collections;

import com.quiz.R;
import com.quiz.R.layout;

import android.app.Activity;
//import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
//import android.util.Log;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class quiz extends Activity {
	
	SQLiteDatabase dataBase;
	GameDBManager dbManager;
	ArrayList<String> questionArray;
	ArrayList<Integer> qID = new ArrayList<Integer>();
	int questionsAnswered;
	int correctAnswer;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        System.out.println("Started the App");
        
        makeqIDs();
       String subject = "technology";

        questionArray = new ArrayList<String>();
        dbManager = new GameDBManager(this);
        dbManager.getTable(subject); // get the subject of the table
        questionsAnswered = 0; // the start with zero questions answered
        correctAnswer = 0; 
        getNextQuestion();
    }
    private void getNextQuestion()
    {
    	
    	ArrayList<String> testQuestion = dbManager.getQuestion(qID.get(questionsAnswered));
    	  if((questionsAnswered <= 5) && (!testQuestion.isEmpty()) )
          {
    		  layoutQuestion(testQuestion);
    		  
    		  questionsAnswered ++;
    	}
    	  else
    	  {
    		  done();
    	  }
    }
    public void layoutQuestion(ArrayList<String> testQuestion)
    {
		ScrollView sv = new ScrollView(this);
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.VERTICAL);
		sv.addView(ll); 
		
	   	//make Question
    	TextView tv = addTextView(testQuestion.get(1));
		ll.addView(tv);
		
		
		
		if (testQuestion.get(2).equalsIgnoreCase("No"))
		{
		ArrayList<String> tempRandom = new ArrayList<String>();	
		tempRandom.add(testQuestion.get(3));//option one
		tempRandom.add(testQuestion.get(4));//option two
		tempRandom.add(testQuestion.get(5));//option three
		tempRandom.add(testQuestion.get(6));//Correct Answer
		Collections.shuffle(tempRandom);//randomize the order
		
		//button 1
		Button b1 = createButton(tempRandom.get(0),testQuestion.get(6));
		//button 2
		Button b2 = createButton(tempRandom.get(1),testQuestion.get(6));
		//button 3
		Button b3 = createButton(tempRandom.get(2),testQuestion.get(6));
		//button 4
		Button b4 = createButton(tempRandom.get(3),testQuestion.get(6));
		ll.addView(b1);
		ll.addView(b2);
		ll.addView(b3);
		ll.addView(b4);
		}
		else// is a true false question
		{
			//button 1
			Button b1 = createButton("true",testQuestion.get(6));
			//button 2
			Button b2 = createButton("false",testQuestion.get(6));
			ll.addView(b1);
			ll.addView(b2);
		}

    	this.setContentView(sv); 
    }

    
    public Button createButton(final String butText, final String Correct)
    {
    	Button button = new Button(this);
    	
    	button.setText(butText);	

    	button.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
            	 if(butText.equalsIgnoreCase(Correct))
            	 {
            		 Toast.makeText(getApplicationContext(), "Correct", Toast.LENGTH_SHORT).show();
            		 getNextQuestion();
            		 correctAnswer++;
            	 }
            	 else
                     Toast.makeText(getApplicationContext(), "Wrong", Toast.LENGTH_SHORT).show(); 
            	 	getNextQuestion();
             }
         });
    	
    	return button;
    }
    
    public void makeqIDs()
    {
    	for(int i = 1; i<5; i++)
    	{	
    		qID.add(i);
    	}
    }
    public TextView addTextView(String textToAdd)
    {
    	TextView tv = new TextView(this);
		tv.setTextSize(12.0f);
		tv.setText(textToAdd);
    	return tv;
    }
    
    public void done()
    {
    	ScrollView sv = new ScrollView(this);
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.VERTICAL);
		sv.addView(ll);
		
		TextView tv = new TextView(this);
		tv.setTextSize(12.0f);
		tv.setText("this is the last thing ever \n you got " + correctAnswer  + "/5 correct");
		ll.addView(tv);

		Button tempBut = new Button(this);
		tempBut.setText("Quiz Done Return to Game");
		tempBut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "This is where the player would be able to return to the game", Toast.LENGTH_LONG).show();
                //finish();     
                questionsAnswered = 0;
                correctAnswer = 0; 
                getNextQuestion();
            }
        });
		ll.addView(tempBut);
        this.setContentView(sv);
    }
    
}