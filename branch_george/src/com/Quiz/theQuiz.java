package com.Quiz;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class theQuiz extends Activity {
    /** Called when the activity is first created. */
	int questionsAnswered = 0;
	int amountCorrect = 0;
	String[][] questions  = {
	{"Question 1", "Answer 1", "Answer 2", "Answer 3", "Answer 4"}
	,{"Question 2", "Answer 1", "Answer 2", "Answer 3", "Answer 4"}
	,{"Question 3", "Answer 1", "Answer 2", "Answer 3", "Answer 4"}
	,{"Question 4", "Answer 1", "Answer 2", "Answer 3", "Answer 4"}
	,{"Question 5", "Answer 1", "Answer 2", "Answer 3", "Answer 4"}
	,{"Question 6 Should Never Get Here", "Answer 1", "Answer 2", "Answer 3", "Answer 4"}};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        
        getQuestions();
        //this does the first question
        getNextQuestion();
    
    }
    
    public void getQuestions()
    {
	
	}
	
    public void getNextQuestion()
    {
        if(questionsAnswered < 5)
        {
    
        createQuestion(questions[questionsAnswered][0],questions[questionsAnswered][1] ,questions[questionsAnswered][2] ,questions[questionsAnswered][3] ,questions[questionsAnswered][4], "Answer 1");
          questionsAnswered++; 
        }
        else
        {
       	 done();
        }
    	
    }
    
    public void createQuestion(String QuestionTxt, final String But1Txt, final String But2Txt, final String But3Txt, final String But4Txt , final String CorrectAnswer)
	{
	
		ScrollView sv = new ScrollView(this);
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.VERTICAL);
		sv.addView(ll);

		
		
		//create the question. 
		TextView tv = new TextView(this);
		tv.setTextSize(12.0f);
		tv.setText(QuestionTxt);
		ll.addView(tv);
		
		Button b1= new Button(this);
		b1.setText(But1Txt);	
		b1.setId(1);
		b1.setTag("b1");
		 b1.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
            	 if(But1Txt.equalsIgnoreCase(CorrectAnswer))
            	 {
            		 Toast.makeText(getApplicationContext(), "Correct", Toast.LENGTH_SHORT).show();
            		 amountCorrect++;
            	 }
            	 else
                     Toast.makeText(getApplicationContext(), "Wrong", Toast.LENGTH_SHORT).show(); 
               
            	 
            	 
            	 getNextQuestion();
             }
         });

		 ll.addView(b1);
		
	
		
		Button b2 = new Button(this);
		b2.setText(But2Txt);
		b2.setId(2);
		b2.setTag("b2");
		 b2.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
            	 if(But2Txt.equalsIgnoreCase(CorrectAnswer))
            	 {
            		 Toast.makeText(getApplicationContext(), "Correct", Toast.LENGTH_SHORT).show();
            		 amountCorrect++;
            	 }
            	 else
                     Toast.makeText(getApplicationContext(), "Wrong", Toast.LENGTH_SHORT).show(); 
               
            	 getNextQuestion();
             }
         });

		 ll.addView(b2);
		
		
		
		
		Button b3 = new Button(this);
		b3.setText(But3Txt);
		b3.setId(3);
		b3.setTag("b3");
		b3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
           	 if(But3Txt.equalsIgnoreCase(CorrectAnswer))
           	 {
           		 Toast.makeText(getApplicationContext(), "Correct", Toast.LENGTH_SHORT).show();
           		amountCorrect++;
           	 }
        	 else
                 Toast.makeText(getApplicationContext(), "Wrong", Toast.LENGTH_SHORT).show(); 
           
           	getNextQuestion();
            }
        });

		ll.addView(b3);
		
		Button b4 = new Button(this);
		b4.setText(But4Txt);
		b4.setId(4);
		b4.setTag("b4");
		b4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
           	 if(But4Txt.equalsIgnoreCase(CorrectAnswer))
        		 {
           		 Toast.makeText(getApplicationContext(), "Correct", Toast.LENGTH_SHORT).show();
           		amountCorrect++;
        		 }
        	 else
                 Toast.makeText(getApplicationContext(), "Wrong", Toast.LENGTH_SHORT).show(); 
           	getNextQuestion();
            }
        });

		ll.addView(b4);
	
		
        this.setContentView(sv);
		//return 0;
	}
    public void done()
    {
    	ScrollView sv = new ScrollView(this);
		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.VERTICAL);
		sv.addView(ll);
		
		TextView tv = new TextView(this);
		tv.setTextSize(12.0f);
		tv.setText("this is the last thing ever \n you got " + amountCorrect + "/5 correct");
		ll.addView(tv);

		Button tempBut = new Button(this);
		tempBut.setText("Quiz Done Return to Game");
		tempBut.setId(4);
		tempBut.setTag("b4");
		tempBut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "This is where the player would be able to return to the game", Toast.LENGTH_LONG).show();
                //finish();
            }
        });
		ll.addView(tempBut);
        this.setContentView(sv);
        
        
    }
    


	
}