package com.kensdavid.pttipcalculator;

import com.kensdavid.randomquotes.DBAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class SelectValues extends Activity {

	private static final String BILL_TOTAL = "BILL_TOTAL";
	private static final String TAX_PCT = "TAX_PCT";
    private static final String TIP_PERCENT = "TIP_PERCENT";
	
	private EditText preTaxEditText;
	private EditText taxPctEditText;
	private EditText tipPctEditText;
	
	private Button continueButton;
	private Button setDefaultsButton;
	private Button useDefaultsButton;
	
	private CheckBox billDefaultCheckBox;
	private CheckBox tipDefaultCheckBox;
	private CheckBox taxDefaultCheckBox;
	
	private double preTaxAmt = 0.0;
	private double taxPct = 0.0;
	private double tipPct = 0.0;
	
	private Intent detailView;
	
	DBAdapter db;

    /** Called when the activity is first created.*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); //call the superclass' version
        setContentView(R.layout.select_values); //inflate the GUI
        
        preTaxEditText = (EditText)findViewById(R.id.billEditTextSV);
    	taxPctEditText = (EditText)findViewById(R.id.taxPctEditTextSV);
    	tipPctEditText = (EditText)findViewById(R.id.tipPctEditTextSV);
        
        continueButton = (Button)findViewById(R.id.continueButton);
        continueButton.setOnClickListener(continueButtonListener);
        
        setDefaultsButton = (Button)findViewById(R.id.setDefaultButtonSV);
        setDefaultsButton.setOnClickListener(setDefaultsListener);
        
        useDefaultsButton = (Button)findViewById(R.id.useDefaultsButtonSV);
        useDefaultsButton.setOnClickListener(useDefaultsListener);
        
        billDefaultCheckBox = (CheckBox)findViewById(R.id.billDefaultCheckBox);
    	tipDefaultCheckBox = (CheckBox)findViewById(R.id.tipDefaultCheckBox);
    	taxDefaultCheckBox = (CheckBox)findViewById(R.id.taxDefaultCheckBox);
        
        db = new DBAdapter(this);
        
 
        
        if(getIntent().getExtras() != null)
		{
			Bundle extras = getIntent().getExtras();
			preTaxAmt = extras.getDouble("PRE_TAX_AMT");
			taxPct = extras.getDouble("TAX_PCT");
			tipPct = extras.getDouble("TIP_PCT");
		}
        else
        {
	        if(savedInstanceState == null)
	    	{               	            	
	        	try
	        	{
	        		db.open();
	        		preTaxAmt = db.getDefaultBill();
	        		taxPct = db.getDefaultTax();
	            	tipPct = db.getDefaultTip();
	            	db.close();
	        	}
	        	catch(Exception e)
	        	{
	        		preTaxAmt = 99.99;
	        		taxPct = 8.875;
	        		tipPct = 18.00;
	        	}
	        }
	    
	        else
	        {
	        	preTaxAmt = savedInstanceState.getDouble(BILL_TOTAL);
	        	tipPct = savedInstanceState.getInt(TIP_PERCENT);
	        	taxPct = savedInstanceState.getDouble(TAX_PCT);
	        }
        }
        setTextBoxes();
    }
    
    private void setButtonText() {
    	continueButton.setText("Continue ->");
    	setDefaultsButton.setText("Set As Defaults");
    	useDefaultsButton.setText("Use Default Values");
    }
    
    private void setTextBoxes() {
    	tipPctEditText.setText(String.format("%.2f", tipPct));
    	taxPctEditText.setText(String.format("%.3f", taxPct));
    	preTaxEditText.setText(String.format("%.2f", preTaxAmt));
	}

	private OnClickListener continueButtonListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			preTaxAmt = Double.valueOf(preTaxEditText.getText().toString());
			taxPct = Double.valueOf(taxPctEditText.getText().toString());
			tipPct = Double.valueOf(tipPctEditText.getText().toString());
			
			detailView = new Intent(SelectValues.this, TipCalculator.class);
			detailView.putExtra("PRE_TAX_AMT", preTaxAmt);
			detailView.putExtra("TAX_PCT", taxPct);
			detailView.putExtra("TIP_PCT", tipPct);
			startActivity(detailView);
			finish();
		}
	};
	
	private OnClickListener setDefaultsListener = new android.view.View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			preTaxAmt = Double.valueOf(preTaxEditText.getText().toString());
			tipPct = Double.valueOf(tipPctEditText.getText().toString());
			taxPct = Double.valueOf(taxPctEditText.getText().toString());
			
			db.open();
			if(billDefaultCheckBox.isChecked())
				db.updateBill(preTaxAmt);
			if(tipDefaultCheckBox.isChecked())
				db.updateTip(tipPct);
			if(taxDefaultCheckBox.isChecked())
				db.updateTax(taxPct);
			
			db.close();
			setButtonText();
		}
	};
	
	private OnClickListener useDefaultsListener = new android.view.View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			db.open();
			
			if(billDefaultCheckBox.isChecked()){
				preTaxAmt = db.getDefaultBill();
				preTaxEditText.setText(String.format("%.2f", preTaxAmt));
			}
			
			
			if(tipDefaultCheckBox.isChecked()){
				tipPct = db.getDefaultTip();
				tipPctEditText.setText(String.format("%.2f", tipPct));
			}
			
			
			if(taxDefaultCheckBox.isChecked()){
				taxPct = db.getDefaultTax();
				taxPctEditText.setText(String.format("%.3f", taxPct));
			}
			
			
			db.close();
			
			//Reset the button text to account for the Gravity bug
			setButtonText();
		}
	};
}
