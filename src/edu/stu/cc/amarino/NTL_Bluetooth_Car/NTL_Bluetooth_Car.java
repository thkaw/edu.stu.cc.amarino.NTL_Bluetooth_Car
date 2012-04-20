/*
  NTL_Bluetooth_Car - Reference MultiColorLamp's Source code to build
  Copyright (c) 2012 Nathaniel_Chen 
  
  Ver 1.0_Beta
   
  Not allow commercial use.
  E-Mail: s99115247@stu.edu.tw 
  
  thanks MIT research help me so that I can do this project ;) 
 
 =========================================================================
 
  MultiColorLamp - Example to use with Amarino
  Copyright (c) 2009 Bonifaz Kaufmann. 
  
  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public
  License along with this library; if not, write to the Free Software
  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
    
*/
package edu.stu.cc.amarino.NTL_Bluetooth_Car;

import edu.stu.cc.amarino.NTL_Bluetooth_Car.R;
import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.AlertDialog.Builder;

import android.content.SharedPreferences;
//import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
//import android.view.View.OnTouchListener;

import android.widget.SeekBar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.widget.SeekBar.OnSeekBarChangeListener;
import at.abraxas.amarino.Amarino;

public class NTL_Bluetooth_Car extends Activity implements OnSeekBarChangeListener{
	
	private static final String TAG = "MultiColorLamp";
	
	/* TODO: change the address to the address of your Bluetooth module
	 * and ensure your device is added to Amarino
	 */
	
	private  String DEVICE_ADDRESS = "00:11:12:06:00:60"; //這是310元的
	
	//private static final String DEVICE_ADDRESS = "00:12:01:30:02:69"; //這是xbee BT
	
	final int DELAY = 150;
	/*SeekBar redSB;
	SeekBar greenSB;
	SeekBar blueSB;*/
	
	SeekBar power_SB;
	Button  setmac_BTN;
	
	EditText setmac_ET;
	
	
	Button  forward_BTN;
	Button  back_BTN;
	Button  left_BTN;
	Button  right_BTN;
	
	TextView powerval_Text;
	
	//View colorIndicator;
	
	//int red, green, blue;
	
	int power_Val;
	
	long lastChange;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		Amarino.connect(this, DEVICE_ADDRESS);
        
        // get references to views defined in our main.xml layout file
		power_SB = (SeekBar) findViewById(R.id.power_seekBar);
     
       // colorIndicator = findViewById(R.id.ColorIndicator);

        // register listeners
        power_SB.setOnSeekBarChangeListener(this);
        
        powerval_Text = (TextView) findViewById(R.id.powerval_LargeText);
        setmac_ET = (EditText) findViewById(R.id.setmac_EditText);
        
        
    }
    public void SetMacOnClick(View SetMacView){ //設定MAC按鈕按下之後的動作
    	Amarino.disconnect(this, DEVICE_ADDRESS);
    	DEVICE_ADDRESS = setmac_ET.toString();
    	setmac_ET.setHint(DEVICE_ADDRESS);
    	Amarino.connect(this, DEVICE_ADDRESS);
    }
    
    
    public void ForwardOnClick(View ForwardView){
    	update_Forward();
    }
    
    public void BackOnClick(View BackView){
    	update_Back();
    }
    
    public void LeftOnClick(View LeftView){
    	update_Left();
    }
    
    //右鍵
    public void RightOnClick(View RightView){
    	update_Right();
    }
    
    public void StopOnClick(View StopView){
    	update_Stop();
    }
    
    
    
    //測試AlertDialog
   /* private void ShowAlertDialog()
    {
     Builder MyAlertDialog = new AlertDialog.Builder(this);
     MyAlertDialog.setTitle("標題");
     MyAlertDialog.setMessage("我是內容");
     MyAlertDialog.show();
    }*/
    
    
	@Override
	protected void onStart() {
		super.onStart();

		// load last state
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
       // red = prefs.getInt("red", 0);
       // green = prefs.getInt("green", 0);
       // blue = prefs.getInt("blue", 0);
        power_Val = prefs.getInt("power_Val", 0);
        
        
        // set seekbars and feedback color according to last state
       /* redSB.setProgress(red);
        greenSB.setProgress(green);
        blueSB.setProgress(blue);*/
        
        power_SB.setProgress(power_Val);
        
        
//       colorIndicator.setBackgroundColor(Color.rgb(red, green, blue));
        
        new Thread(){
        	public void run(){
        		try {
					Thread.sleep(6000);
				} catch (InterruptedException e) {}
				Log.d(TAG, "update colors");
        		updateAllColors();
        	}
        }.start();
        
	}
	

	@Override
	protected void onStop() {
		super.onStop();
		// save state
		PreferenceManager.getDefaultSharedPreferences(this)
			.edit()
				/*.putInt("red", red)
				.putInt("green", green)
				.putInt("blue", blue)*/
			.putInt("power_Val", power_Val)
			.commit();
		
		// stop Amarino's background service, we don't need it any more 
		Amarino.disconnect(this, DEVICE_ADDRESS);
	}



	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		// do not send to many updates, Arduino can't handle so much
		if (System.currentTimeMillis() - lastChange > DELAY ){
			updateState(seekBar);
			lastChange = System.currentTimeMillis();
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		lastChange = System.currentTimeMillis();
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		updateState(seekBar);
	}

	private void updateState(final SeekBar seekBar) {
		
		switch (seekBar.getId()){
			/*case R.id.SeekBarRed:
				red = seekBar.getProgress();
				updateRed();
				break;*/
			case R.id.power_seekBar:
				power_Val = seekBar.getProgress();
				
				powerval_Text.setText(Integer.toString(power_Val)); //將power_Val轉型塞到textbox裡面
				
				update_PowerVal();
	
				break;
			/*case R.id.SeekBarBlue:
				blue = seekBar.getProgress();
				updateBlue();
				break;*/
		}
		
		
		update_PowerVal();
		// provide user feedback
		//colorIndicator.setBackgroundColor(Color.rgb(red, green, blue));
	}
	
	private void updateAllColors() { //這行用來送出指令到arduino
		// send state to Arduino
       // updateRed();
        update_PowerVal();
        
       // updateBlue();
	}
	
	
	
	
	private void update_PowerVal(){
		Amarino.sendDataToArduino(this, DEVICE_ADDRESS, 'P', power_Val);
	}
	private void update_Forward(){
		Amarino.sendDataToArduino(this, DEVICE_ADDRESS, 'F', power_Val);
	}
	
	private void update_Back(){
		Amarino.sendDataToArduino(this, DEVICE_ADDRESS, 'B', power_Val);
	}
	
	private void update_Left(){
		Amarino.sendDataToArduino(this, DEVICE_ADDRESS, 'L', power_Val);
	}
	
	private void update_Right(){
		Amarino.sendDataToArduino(this, DEVICE_ADDRESS, 'R', power_Val);
	}
	
	private void update_Stop(){
		Amarino.sendDataToArduino(this, DEVICE_ADDRESS, 'S', 0);
	}
	
	
 /*	private void updateAllColors() {
		// send state to Arduino
        updateRed();
        updateGreen();
        updateBlue();
	}
	private void updateRed(){
		// I have chosen random small letters for the flag 'o' for red, 'p' for green and 'q' for blue
		// you could select any small letter you want
		// however be sure to match the character you register a function for your in Arduino sketch
		Amarino.sendDataToArduino(this, DEVICE_ADDRESS, 'o', red);
	}
	
	private void updateGreen(){
		Amarino.sendDataToArduino(this, DEVICE_ADDRESS, 'p', green);
	}
	
	private void updateBlue(){
		Amarino.sendDataToArduino(this, DEVICE_ADDRESS, 'q', blue);
	}*/
	
}