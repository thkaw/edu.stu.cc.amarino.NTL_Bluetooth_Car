/*
  NTL_Bluetooth_Car - Reference MultiColorLamp's Source code to build
  Copyright (c) 2012 Nathaniel_Chen 

  Ver 3.0_beta

  Not allow commercial use.
  E-Mail: s99115247@stu.edu.tw 

  thanks MIT research help me so that I can do this project ;) 


  [Next to do:add level meter control, menu select function.]

 version history
 3.0_beta--New UI Construe, use RelativeLayout. 
  		 --Add English Language support. 
  		 --Add rotation function, but not use right code, will re Activity when every rotation. 
  		 (need to fix...I spend 2 hours to fix but too noob to fail lol!)
  		 --Improve code, button use ClickListener, clear some unnecessary comment but keep some important.
  		 @2012/08/13
  		 
 2.1_beta--Fix Program Crash when execute Amarino.connect(due to ADT update reason). @ 2012/08/09
 1.5_beta--Improve UI Experience.
 1.0_beta--First re-modify control program.
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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.widget.SeekBar.OnSeekBarChangeListener;
import at.abraxas.amarino.Amarino;

public class NTL_Bluetooth_Car extends Activity implements
		OnSeekBarChangeListener {

	private static final String TAG = "NTL_Bluetooth_Car";

	/*
	 * TODO: change the address to the address of your Bluetooth module and
	 * ensure your device is added to Amarino 
	 * 要將這裡改為自己目標藍牙棒的mac位置！
	 */

	private String DEVICE_ADDRESS = "00:11:12:06:00:60"; // 這是310元的

	// private static final String DEVICE_ADDRESS = "00:12:01:30:02:69";
	// //這是xbee BT

	final int DELAY = 150;
	int power_Val;
	long lastChange;
	SeekBar power_SB;
	TextView powerval_Text;
	EditText setmac_ET;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Amarino.connect(this, DEVICE_ADDRESS);

		power_SB = (SeekBar) findViewById(R.id.power_seekBar);
		power_SB.setOnSeekBarChangeListener(this);

		powerval_Text = (TextView) findViewById(R.id.powerval_LargeText);
		setmac_ET = (EditText) findViewById(R.id.setmac_EditText);

		// 以下是button宣告及listener
		Button btnSetmac = (Button) findViewById(R.id.setmac_Button);
		btnSetmac.setTag(0);
		btnSetmac.setOnClickListener(btnOnClick);

		Button btnForward = (Button) findViewById(R.id.forward_Button);
		btnForward.setTag(1);
		btnForward.setOnClickListener(btnOnClick);

		Button btnBack = (Button) findViewById(R.id.back_Button);
		btnBack.setTag(2);
		btnBack.setOnClickListener(btnOnClick);

		Button btnLeft = (Button) findViewById(R.id.left_Button);
		btnLeft.setTag(3);
		btnLeft.setOnClickListener(btnOnClick);

		Button btnRight = (Button) findViewById(R.id.right_Button);
		btnRight.setTag(4);
		btnRight.setOnClickListener(btnOnClick);

		Button btnStop = (Button) findViewById(R.id.stop_Button);
		btnStop.setTag(5);
		btnStop.setOnClickListener(btnOnClick);

	}

	private Button.OnClickListener btnOnClick = new Button.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			switch ((Integer) v.getTag()) {
			case 0:
				SetMacOnClick();
				break;
			case 1:
				update_Forward();
				break;
			case 2:
				update_Back();
				break;
			case 3:
				update_Left();
				break;
			case 4:
				update_Right();
				break;
			case 5:
				update_Stop();
				break;
			}

		}
	};

	public void SetMacOnClick() { // 設定MAC按鈕按下之後的動作
		Amarino.disconnect(this, DEVICE_ADDRESS);
		DEVICE_ADDRESS = setmac_ET.toString();
		setmac_ET.setHint(DEVICE_ADDRESS);
		Amarino.connect(this, DEVICE_ADDRESS);
	}

	// 測試AlertDialog
	/*
	 * private void ShowAlertDialog() { Builder MyAlertDialog = new
	 * AlertDialog.Builder(this); MyAlertDialog.setTitle("標題");
	 * MyAlertDialog.setMessage("我是內容"); MyAlertDialog.show(); }
	 */

	@Override
	protected void onStart() {
		super.onStart();

		// load last state
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		power_Val = prefs.getInt("power_Val", 0);

		// set seekbars and feedback color according to last state
		/*
		 * redSB.setProgress(red); greenSB.setProgress(green);
		 * blueSB.setProgress(blue);
		 */

		power_SB.setProgress(power_Val);

		// colorIndicator.setBackgroundColor(Color.rgb(red, green, blue));

		new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(6000);
				} catch (InterruptedException e) {
				}
				Log.d(TAG, "update data");
				updateAlldata();
			}
		}.start();

	}

	@Override
	protected void onStop() {
		super.onStop();
		// save state
		PreferenceManager.getDefaultSharedPreferences(this).edit()
		/*
		 * .putInt("red", red) .putInt("green", green) .putInt("blue", blue)
		 */
		.putInt("power_Val", power_Val).commit();

		// stop Amarino's background service, we don't need it any more
		Amarino.disconnect(this, DEVICE_ADDRESS);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// do not send to many updates, Arduino can't handle so much
		if (System.currentTimeMillis() - lastChange > DELAY) {
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

		switch (seekBar.getId()) {
		/*
		 * case R.id.SeekBarRed: red = seekBar.getProgress(); updateRed();
		 * break;
		 */
		case R.id.power_seekBar:
			power_Val = seekBar.getProgress();

			powerval_Text.setText(Integer.toString(power_Val)); // 將power_Val轉型塞到textbox裡面

			update_PowerVal();

			break;
		/*
		 * case R.id.SeekBarBlue: blue = seekBar.getProgress(); updateBlue();
		 * break;
		 */
		}

		update_PowerVal();
		// provide user feedback
		// colorIndicator.setBackgroundColor(Color.rgb(red, green, blue));
	}

	private void updateAlldata() { // 這行用來送出指令到arduino
		// send state to Arduino
		// updateRed();
		update_PowerVal();

		// updateBlue();
	}

	private void update_PowerVal() {
		Amarino.sendDataToArduino(this, DEVICE_ADDRESS, 'P', power_Val);
	}

	private void update_Forward() {
		Amarino.sendDataToArduino(this, DEVICE_ADDRESS, 'F', power_Val);
	}

	private void update_Back() {
		Amarino.sendDataToArduino(this, DEVICE_ADDRESS, 'B', power_Val);
	}

	private void update_Left() {
		Amarino.sendDataToArduino(this, DEVICE_ADDRESS, 'L', power_Val);
	}

	private void update_Right() {
		Amarino.sendDataToArduino(this, DEVICE_ADDRESS, 'R', power_Val);
	}

	private void update_Stop() {
		Amarino.sendDataToArduino(this, DEVICE_ADDRESS, 'S', 0);
	}

}