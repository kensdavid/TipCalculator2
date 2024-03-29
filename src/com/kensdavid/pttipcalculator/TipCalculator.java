package com.kensdavid.pttipcalculator;

 

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.kensdavid.randomquotes.DBAdapter;

public class TipCalculator extends Activity {
	private static final String BILL_TOTAL = "BILL_TOTAL";
	private static final String TAX_PCT = "TAX_PCT";
	private static final String SPLIT_BY = "SPLIT_BY";
    private static final String TIP_PERCENT = "TIP_PERCENT";
    
    private double currentPreTaxBill;
    private double currentTaxPct;
    private int currentSplitBy;
    
    private double currentTipPct;
    private String tipPctString;
    
    private double indPreTaxAmt; 
    private double tipAmt;
    private double indTipAmt;
    private double taxAmt;
    private double indTaxAmt;
    private double totalAmt;
    private double indTotalAmt;
    
    //Pre-tax, Tip %, Tip $, Tax %, Tax $, Total $
    private EditText billEditText;
    private EditText tipPctEditText;
    private EditText tipAmtEditText;
    private EditText taxPctEditText;
    private EditText taxAmtEditText;
    private EditText totalAmtEditText;
    //Pre-Tax, Tip $, Tax $, Total $
    private EditText indBillEditText;
    private EditText indTipEditText;
    private EditText indTaxEditText;
    private EditText indTotalEditText;
    //Capture "Split-By" functionality
    private TextView splitByNumTextView;
    private TextView yourTotalTextView;
    private TextView perPersonTextView;
    
    private SeekBar splitBySeekBar;
    
    private Button hideKeyboardButton;
    private Button setDefaultsButton;
    private Button getDefaultTipButton;
    private Button minusTipButton;
    private Button plusTipButton;
    private Button selectValuesButton;
    
    private Intent setValues;
    private boolean created = false;
    
    DBAdapter db = new DBAdapter(this);
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); //call the superclass' version
        setContentView(R.layout.main); //inflate the GUI
        
        currentSplitBy = 1; //initialize split by qty to 1
        
        if(getIntent().getExtras() != null)
		{
			Bundle extras = getIntent().getExtras();
			currentPreTaxBill = extras.getDouble("PRE_TAX_AMT");
			currentTaxPct = extras.getDouble("TAX_PCT");
			currentTipPct = extras.getDouble("TIP_PCT");
		}
        else
        {
        	if(savedInstanceState == null)
        	{
            
            	currentPreTaxBill = 50.00; //initialize pre tax bill to 50.00           	
            	try{	
            		db.open();
            		currentTaxPct = db.getDefaultTax();
                	currentTipPct = db.getDefaultTip();
                	db.close();
            	}
            	catch(Exception e)
            	{
            		currentTaxPct = 8.875;
            		currentTipPct = 18.00;
            	}
            }
        
	        else
	        {
	        	currentPreTaxBill = savedInstanceState.getDouble(BILL_TOTAL);
	        	currentSplitBy = savedInstanceState.getInt(SPLIT_BY);
	        	currentTipPct = savedInstanceState.getInt(TIP_PERCENT);
	        	currentTaxPct = savedInstanceState.getDouble(TAX_PCT);
	        }
        }
        
        yourTotalTextView = (TextView)findViewById(R.id.yourTotalTextView);
        perPersonTextView = (TextView)findViewById(R.id.perPersonTextView);
        
         billEditText = (EditText)findViewById(R.id.billEditText);
         tipPctEditText = (EditText)findViewById(R.id.tipPctEditText);
         tipAmtEditText = (EditText)findViewById(R.id.tipAmtEditText);
         taxPctEditText = (EditText)findViewById(R.id.taxPctEditText);
         taxAmtEditText = (EditText)findViewById(R.id.taxAmtEditText);
         totalAmtEditText = (EditText)findViewById(R.id.totalAmtEditText);
         totalAmtEditText.setOnClickListener(selectValuesListener);
         indBillEditText = (EditText)findViewById(R.id.indBillEditText);
         indTipEditText = (EditText)findViewById(R.id.indTipEditText);
         indTaxEditText = (EditText)findViewById(R.id.indTaxEditText);
         indTotalEditText = (EditText)findViewById(R.id.indTotalEditText);
         
         hideKeyboardButton = (Button)findViewById(R.id.hideKeyboardButton);
         hideKeyboardButton.setOnClickListener(hideKeyboardListener);
         
         setDefaultsButton = (Button)findViewById(R.id.saveDefaultsButton);
         setDefaultsButton.setOnClickListener(setDefaultsListener);
         
         minusTipButton = (Button)findViewById(R.id.minusTipButton);
         minusTipButton.setOnClickListener(changeTipListener);
         
         plusTipButton = (Button)findViewById(R.id.plusTipButton);
         plusTipButton.setOnClickListener(changeTipListener);
         
         //getDefaultTipButton = (Button)findViewById(R.id.defaultTipButton);
         //getDefaultTipButton.setOnClickListener(loadTipDefaultListener);
         
         selectValuesButton = (Button)findViewById(R.id.selectValuesButton);
         selectValuesButton.setOnClickListener(selectValuesListener);
         
         splitByNumTextView = (TextView)findViewById(R.id.splitByNumTextView);
         
         billEditText.addTextChangedListener(billEditTextWatcher);
         tipPctEditText.addTextChangedListener(tipEditTextWatcher);
         taxPctEditText.addTextChangedListener(taxEditTextWatcher);         
         
         splitBySeekBar = (SeekBar)findViewById(R.id.splitBySeekBar);
         splitBySeekBar.setOnSeekBarChangeListener(splitBySeekBarListener);
         
         splitBySeekBar.setProgress(currentSplitBy);
         billEditText.setText(String.format("%.02f", currentPreTaxBill));
         tipPctEditText.setText(Double.toString(currentTipPct));
         taxPctEditText.setText(Double.toString(currentTaxPct));
         showPerPerson(false);
         setTextBoxes();
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	setTextBoxes();
    }
    
    private void setTextBoxes() {
    	tipAmtEditText.setText(String.format("%.2f", tipAmt));
    	taxAmtEditText.setText(String.format("%.2f", taxAmt));
    	totalAmtEditText.setText(String.format("%.2f", totalAmt));
    	indBillEditText.setText(String.format("%.2f", indPreTaxAmt));
    	indTipEditText.setText(String.format("%.2f", indTipAmt));
    	indTaxEditText.setText(String.format("%.2f", indTaxAmt));
    	indTotalEditText.setText(String.format("%.2f", indTotalAmt));
	}

    private void setButtonText() {
    	selectValuesButton.setText("<- Back");
    	selectValuesButton.setGravity(Gravity.CENTER);
    	hideKeyboardButton.setText("Hide Keyboard");
    	hideKeyboardButton.setGravity(Gravity.CENTER);
    	minusTipButton.setText("-1");
    	minusTipButton.setGravity(Gravity.CENTER);
    	plusTipButton.setText("+1");
    	plusTipButton.setGravity(Gravity.CENTER);
    	splitByNumTextView.setGravity(Gravity.CENTER);
    }

	//Save all values
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
    	super.onSaveInstanceState(outState);
    	
    	outState.putDouble(BILL_TOTAL, currentPreTaxBill);
    	outState.putDouble(TAX_PCT, currentTaxPct);
    	outState.putInt(SPLIT_BY, currentSplitBy);
    	outState.putDouble(TIP_PERCENT, currentTipPct);
    }
    
    private OnClickListener hideKeyboardListener = new android.view.View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			hideSoftKeyboard();
			//Reset the button text to account for the Gravity bug
			setButtonText();
		}
	};
	
	private OnClickListener setDefaultsListener = new android.view.View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Double tipPct = Double.valueOf(tipPctEditText.getText().toString());
			Double taxPct = Double.valueOf(taxPctEditText.getText().toString());
			db.open();
			db.updateTip(tipPct);
			db.updateTax(taxPct);
			db.close();
			//Reset the button text to account for the Gravity bug
			setButtonText();
		}
	};
	
	private OnClickListener selectValuesListener = new android.view.View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			 setValues = new Intent(TipCalculator.this, SelectValues.class);
			 setValues.putExtra("PRE_TAX_AMT", currentPreTaxBill);
			 setValues.putExtra("TAX_PCT", currentTaxPct);
			 setValues.putExtra("TIP_PCT", currentTipPct);
			 startActivity(setValues);
			 finish();
		}
	};
	private OnClickListener loadTipDefaultListener = new android.view.View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			db.open();
			//tipPctEditText.setText(Double.toString(db.getDefaultTip()), null);
			currentTipPct = db.getDefaultTip();
			tipPctEditText.setText(String.format("%.2f", currentTipPct));
			db.close();
			//Reset the button text to account for the Gravity bug
			setButtonText();
		}
	};
	
	/*private OnClickListener loadTaxDefaultListener = new android.view.View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			db.open();
			currentTaxPct = db.getDefaultTax();
			taxPctEditText.setText(String.format("%.3f", currentTaxPct));
			db.close();
		}
	};*/
	
	private OnClickListener changeTipListener = new android.view.View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(v == plusTipButton)
				currentTipPct++;
			if(v == minusTipButton && currentTipPct >= 1.0)
				currentTipPct--;
			tipPctString = Double.toString(currentTipPct);
			tipPctEditText.setText(tipPctString);
			updateValues(tipPctString, "tip");
			//Reset the button text to account for the Gravity bug
			setButtonText();
		}
	};
	
	private void hideSoftKeyboard() {
		// hide the soft keyboard
        ((InputMethodManager) getSystemService(
           Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
           billEditText.getWindowToken(), 0);
	}
    
    private OnSeekBarChangeListener splitBySeekBarListener = new OnSeekBarChangeListener() {
		
    	@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			String splitByText;
			Resources res = getResources();
			
    		if(seekBar.getProgress() > 1)
			{
				currentSplitBy = seekBar.getProgress();
				splitByText = Integer.toString(currentSplitBy);
				splitByNumTextView.setTextColor(Color.RED);				
				showPerPerson(true);
			}
			else
			{
				currentSplitBy = 1;
				splitByText = res.getString(R.string.defaultSplitBy); 
				splitByNumTextView.setTextColor(Color.BLACK);
				showPerPerson(false);
			}
			splitByNumTextView.setText(splitByText);
			hideSoftKeyboard();
			updateValues(null, null);		
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			
		}
		
		

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			
		}
	};
    
	private void showPerPerson(boolean showPerPerson) {
	    int visibility;
	    if(showPerPerson)
	    	visibility = EditText.VISIBLE;
	    else
	    	visibility = EditText.INVISIBLE;
	    
		indBillEditText.setVisibility(visibility);
	    indTipEditText.setVisibility(visibility);
	    indTaxEditText.setVisibility(visibility);
	    indTotalEditText.setVisibility(visibility);
	    yourTotalTextView.setVisibility(visibility);
	    perPersonTextView.setVisibility(visibility);
	}
	
	private TextWatcher billEditTextWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			updateValues(s.toString(), "bill");
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
		}
	};
	
	private TextWatcher taxEditTextWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
					
			updateValues(s.toString(), "tax");
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
		}
	};
	
	private TextWatcher tipEditTextWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {						
			updateValues(s.toString(), "tip");
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
		}
	};	
	
	private void recognizeValues(String taxString, String billString, String tipString) {
		if(taxString != null) {
			try
			{
				currentTaxPct = Double.parseDouble(taxString);
			}
			catch (Exception e) {
				currentTaxPct = 0;
				// taxPctEditText.setText(Double.toString(currentTaxPct));
			}	
		}
		if(billString != null){
			try
			{
				currentPreTaxBill = Double.parseDouble(billString);
			}
			catch (Exception e) {
				currentPreTaxBill = 0.0;
			}
		}
		if(tipString != null){
			try
			{
				currentTipPct = Double.parseDouble(tipString);
			}
			catch (Exception e) {
				currentTipPct = 0.00;
			}
		}
		
		
	}

    private void updateValues(String s, String typeString)
    {
    	if(typeString != null){
    		if(typeString.equals("tip"))
        		recognizeValues(null, null, s);
        	if(typeString.equals("tax"))
        		recognizeValues(s, null, null);
        	if(typeString.equals("bill"))
        		recognizeValues(null, s, null);	
    	}
    	
    	
    	if(currentSplitBy == 0)
    		currentSplitBy = 1;
    	
    	indPreTaxAmt = currentPreTaxBill / currentSplitBy; 
    	tipAmt = currentPreTaxBill * currentTipPct / 100;
    	indTipAmt = tipAmt / currentSplitBy;
    	taxAmt = (currentPreTaxBill * currentTaxPct) / 100;
    	indTaxAmt = taxAmt / currentSplitBy;
    	totalAmt = currentPreTaxBill + tipAmt + taxAmt;
    	indTotalAmt = totalAmt / currentSplitBy;
    	
    	setTextBoxes();
    }
}