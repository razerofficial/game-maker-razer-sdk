/*
 * Copyright (C) 2012-2016 Razer, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.razerzone.store.sdk.engine.gamemaker;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.razerzone.store.sdk.Controller;
import com.razerzone.store.sdk.InputMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class InputView extends View {

	private static final String TAG = InputView.class.getSimpleName();
	
	private static final boolean sEnableLogging = false;
	
	private static InputView mInstance = null;
	
	// Interim axis and button values before applied to states
	private static SparseArray<HashMap<Integer, Float>> sAxisValues = new SparseArray<HashMap<Integer, Float>>();
	private static SparseArray<HashMap<Integer, Boolean>> sButtonValues = new SparseArray<HashMap<Integer, Boolean>>();
	
	private static final float DEAD_ZONE = 0.25f;
	
	// State of axis and buttons to be used by engine	
	private static List<SparseArray<Float>> sStateAxis = new ArrayList<SparseArray<Float>>();
	private static List<SparseBooleanArray> sStateButton = new ArrayList<SparseBooleanArray>();
	private static List<SparseBooleanArray> sStateButtonDown = new ArrayList<SparseBooleanArray>();
	private static List<SparseBooleanArray> sStateButtonUp = new ArrayList<SparseBooleanArray>();
	private static List<SparseBooleanArray> sLastStateButtonDown = new ArrayList<SparseBooleanArray>();
	private static List<SparseBooleanArray> sLastStateButtonUp = new ArrayList<SparseBooleanArray>();
	
	private static class ViewRemappedEventDispatcher implements InputMapper.RemappedEventDispatcher {
		public static InputView mView;
		public ViewRemappedEventDispatcher(InputView view) {
			mView = view;
		}
		@Override
		public boolean dispatchGenericMotionEvent(MotionEvent motionEvent) {
			return mView.onGenericMotionEvent(motionEvent);
		}
		@Override
		public boolean dispatchKeyEvent(KeyEvent keyEvent) {
			if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
				return mView.onKeyDown(keyEvent.getKeyCode(), keyEvent);
			} else if (keyEvent.getAction() == KeyEvent.ACTION_UP) {
				return mView.onKeyUp(keyEvent.getKeyCode(), keyEvent);
			} else {
				return false;
			}
		}
	}
	
	private static ViewRemappedEventDispatcher sViewRemappedEventDispatcher = null;
	
	static {
		for (int index = 0; index < Controller.MAX_CONTROLLERS; ++index) {
			HashMap<Integer, Float> axisMap = new HashMap<Integer, Float>();
			axisMap.put(MotionEvent.AXIS_HAT_X, 0f);
			axisMap.put(MotionEvent.AXIS_HAT_Y, 0f);
			sAxisValues.put(index, axisMap);
			HashMap<Integer, Boolean> buttonMap = new HashMap<Integer, Boolean>();
			sButtonValues.put(index, buttonMap);
		}
    }
	
    public InputView(Context context, AttributeSet attrs) {
    	super(context, attrs);
        init();
    }

    public InputView(Context context, AttributeSet attrs, int defStyle) {
    	super(context, attrs, defStyle);
        init();
    }

    public InputView(Context context) {
        super(context);
        init();
    }

    private void init() {
    	if (sEnableLogging) {
    		Log.i(TAG, "Construct InputView");
    	}
    	mInstance = this;
		sViewRemappedEventDispatcher = new ViewRemappedEventDispatcher(this);
		Activity activity = ((Activity)getContext());		
		if (null != activity) {			
		
			FrameLayout content = (FrameLayout)activity.findViewById(android.R.id.content);
			if (null != content) {
				content.addView(this);
			} else {
				Log.e(TAG, "Content view is missing");
			}
			
			InputMapper.init(activity);
			
			// prepare OUYA-Everywhere Input states
			for (int playerNum = 0; playerNum < Controller.MAX_CONTROLLERS; ++playerNum) {
				sStateAxis.add(new SparseArray<Float>());
				sStateButton.add(new SparseBooleanArray());
				sStateButtonDown.add(new SparseBooleanArray());
				sStateButtonUp.add(new SparseBooleanArray());
				sLastStateButtonDown.add(new SparseBooleanArray());
				sLastStateButtonUp.add(new SparseBooleanArray());
			}
			
			// give our custom view focus
			View oldFocus = activity.getCurrentFocus();
			if (null != oldFocus) {
				if (sEnableLogging) {
					Log.i(TAG, "Disable the current focus");
				}
				oldFocus.setFocusable(false);
			}			
			activity.takeKeyEvents(true);			
			setFocusable(true);
			requestFocus();
		} else {
			Log.e(TAG, "Activity is null");
		}
	}
	
	public void shutdown() {
		Activity activity = ((Activity)getContext());		
		if (null != activity) {
    		InputMapper.shutdown(activity);
    	} else {
    		Log.e(TAG, "Activity was not found.");
    	}
    }
	
	public static InputView getInstance() {
		return mInstance;
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent keyEvent) {
		if (sEnableLogging) {
			Controller controller = Controller.getControllerByDeviceId(keyEvent.getDeviceId());
			if (null == controller) {
				Log.i(TAG, "dispatchKeyEvent keyCode=" + keyEvent.getKeyCode()+" name="+DebugInput.debugGetKeyEvent(keyEvent));
			} else {
				Log.i(TAG, "dispatchKeyEvent name=\""+ controller.getDeviceName()+"\" keyCode=" + keyEvent.getKeyCode()+" name="+DebugInput.debugGetKeyEvent(keyEvent));
			}
		}
		Activity activity = ((Activity)getContext());		
		if (null != activity) {
	    	if (InputMapper.shouldHandleInputEvent(keyEvent)) {
	    		return InputMapper.dispatchKeyEvent(keyEvent, this, sViewRemappedEventDispatcher);
	    	}
	    } else {
	    	Log.e(TAG, "Activity was not found.");
	    }
		return false;
	}
		
	@Override
	public boolean dispatchGenericMotionEvent(MotionEvent motionEvent) {
		/*
    	if (sEnableLogging) {
			DebugInput.debugMotionEvent(motionEvent);
		}
		*/
    	
    	Activity activity = ((Activity)getContext());		
		if (null != activity) {
		    if (InputMapper.shouldHandleInputEvent(motionEvent)) {
		    	return InputMapper.dispatchGenericMotionEvent(motionEvent, this, sViewRemappedEventDispatcher);
		    }
	    } else {
	    	Log.e(TAG, "Activity was not found.");
	    }
		return false;
    }
	
	@Override
	public boolean onGenericMotionEvent(MotionEvent motionEvent) {
		/*
    	if (sEnableLogging) {
    		Log.i(TAG, "remappedDispatchGenericMotionEvent");
    		DebugInput.debugControllerMotionEvent(motionEvent);
    	}
		*/
    	
    	int playerNum = Controller.getPlayerNumByDeviceId(motionEvent.getDeviceId());
    	if (playerNum < 0 || playerNum >= Controller.MAX_CONTROLLERS) {
    		playerNum = 0;
    	}
		
		SparseArray<Float> stateAxises = sStateAxis.get(playerNum);
		if (null == stateAxises) {
			Log.e(TAG, "onGenericMotionEvent stateAxises is null");
			return false;
		}
		
		float dpadX = motionEvent.getAxisValue(MotionEvent.AXIS_HAT_X);
		float dpadY = motionEvent.getAxisValue(MotionEvent.AXIS_HAT_Y);
		sAxisValues.get(playerNum).put(MotionEvent.AXIS_HAT_X, dpadX);
		sAxisValues.get(playerNum).put(MotionEvent.AXIS_HAT_Y, dpadY);
		
		if (null == sButtonValues.get(playerNum).get(Controller.BUTTON_DPAD_LEFT) &&
			null == sButtonValues.get(playerNum).get(Controller.BUTTON_DPAD_RIGHT)) {
			if (dpadX < -DEAD_ZONE) {
				processKeyDown(playerNum, Controller.BUTTON_DPAD_LEFT);
			} else {
				processKeyUp(playerNum, Controller.BUTTON_DPAD_LEFT);
			}
			if (dpadX > DEAD_ZONE) {
				processKeyDown(playerNum, Controller.BUTTON_DPAD_RIGHT);
			} else {
				processKeyUp(playerNum, Controller.BUTTON_DPAD_RIGHT);
			}
		}
		
		if (null == sButtonValues.get(playerNum).get(Controller.BUTTON_DPAD_DOWN) &&
			null == sButtonValues.get(playerNum).get(Controller.BUTTON_DPAD_UP)) {
			if (dpadY > DEAD_ZONE) {
				processKeyDown(playerNum, Controller.BUTTON_DPAD_DOWN);
			} else {
				processKeyUp(playerNum, Controller.BUTTON_DPAD_DOWN);
			}
			if (dpadY < -DEAD_ZONE) {
				processKeyDown(playerNum, Controller.BUTTON_DPAD_UP);
			} else {
				processKeyUp(playerNum, Controller.BUTTON_DPAD_UP);
			}
		}
		
		stateAxises.put(MotionEvent.AXIS_HAT_X, dpadX);
    	stateAxises.put(MotionEvent.AXIS_HAT_Y, dpadY);
    	stateAxises.put(Controller.AXIS_LS_X, motionEvent.getAxisValue(Controller.AXIS_LS_X));
    	stateAxises.put(Controller.AXIS_LS_Y, motionEvent.getAxisValue(Controller.AXIS_LS_Y));
    	stateAxises.put(Controller.AXIS_RS_X, motionEvent.getAxisValue(Controller.AXIS_RS_X));
    	stateAxises.put(Controller.AXIS_RS_Y, motionEvent.getAxisValue(Controller.AXIS_RS_Y));
    	stateAxises.put(Controller.AXIS_L2, motionEvent.getAxisValue(Controller.AXIS_L2));
    	stateAxises.put(Controller.AXIS_R2, motionEvent.getAxisValue(Controller.AXIS_R2));
		
    	return false;
    }
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent keyEvent) {
		
		if (keyEvent.getSource() == InputDevice.SOURCE_JOYSTICK) {
			return false;
		}
		
		/*
		if (sEnableLogging) {
			Log.i(TAG, "onKeyUp keyCode=" + keyCode + " ("+DebugInput.debugGetButtonName(keyCode)+") source="+keyEvent.getSource());
		}
		*/
		
    	int playerNum = Controller.getPlayerNumByDeviceId(keyEvent.getDeviceId());
    	if (playerNum < 0 || playerNum >= Controller.MAX_CONTROLLERS) {
    		playerNum = 0;
    	}
		
		switch (keyCode) {
			case Controller.BUTTON_DPAD_DOWN:
				if (keyEvent.getSource() == InputDevice.SOURCE_JOYSTICK ) {
					if (sAxisValues.get(playerNum).get(MotionEvent.AXIS_HAT_Y) > DEAD_ZONE) {
						processKeyUp(playerNum, keyCode);
					}
					return true;
				} else {
					sButtonValues.get(playerNum).put(keyCode, false);
				}
				break;
			case Controller.BUTTON_DPAD_LEFT:
				if (keyEvent.getSource() == InputDevice.SOURCE_JOYSTICK ) {
					if (sAxisValues.get(playerNum).get(MotionEvent.AXIS_HAT_X) < -DEAD_ZONE) {
						processKeyUp(playerNum, keyCode);
					}
					return true;
				} else {
					sButtonValues.get(playerNum).put(keyCode, false);
				}
				break;
			case Controller.BUTTON_DPAD_RIGHT:
				if (keyEvent.getSource() == InputDevice.SOURCE_JOYSTICK ) {
					if (sAxisValues.get(playerNum).get(MotionEvent.AXIS_HAT_X) > DEAD_ZONE) {
						processKeyUp(playerNum, keyCode);
					}
					return true;
				} else {
					sButtonValues.get(playerNum).put(keyCode, false);
				}
				break;
			case Controller.BUTTON_DPAD_UP:
				if (keyEvent.getSource() == InputDevice.SOURCE_JOYSTICK ) {
					if (sAxisValues.get(playerNum).get(MotionEvent.AXIS_HAT_Y) < -DEAD_ZONE) {
						processKeyUp(playerNum, keyCode);
					}
					return true;
				} else {
					sButtonValues.get(playerNum).put(keyCode, false);
				}
				break;
			case KeyEvent.KEYCODE_BUTTON_L2:
				sAxisValues.get(playerNum).put(Controller.AXIS_L2, 0f);
				break;
			case KeyEvent.KEYCODE_BUTTON_R2:
				sAxisValues.get(playerNum).put(Controller.AXIS_R2, 0f);
				break;
		}
		
		return processKeyUp(playerNum, keyCode);
	}
	
	private boolean processKeyUp(int playerNum, int keyCode) {
		
		SparseBooleanArray stateButton = sStateButton.get(playerNum);
		if (null == stateButton) {
			Log.e(TAG, "processKeyUp stateButton playerNum="+playerNum+" keyCode="+keyCode+" is null");
			return false;
		}
		if (stateButton.get(keyCode) != false) {
			stateButton.put(keyCode, false);
			/*
			if (sEnableLogging) {
				Log.i(TAG, "processKeyUp stateButton playerNum="+playerNum+" keyCode="+keyCode+" ("+DebugInput.debugGetButtonName(keyCode)+") is="+stateButton.get(keyCode));
			}
			*/
			
			SparseBooleanArray stateButtonUp = sStateButtonUp.get(playerNum);
			if (null == stateButtonUp) {
				Log.e(TAG, "processKeyUp stateButtonUp playerNum="+playerNum+" keyCode="+keyCode+" is null");
			} else {
				stateButtonUp.put(keyCode, true);
				/*
				if (sEnableLogging) {
					Log.i(TAG, "processKeyUp stateButtonUp playerNum="+playerNum+" keyCode="+keyCode+" ("+DebugInput.debugGetButtonName(keyCode)+") is="+stateButtonUp.get(keyCode));
				}
				*/
			}
		}
		
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
	
		if (keyEvent.getSource() == InputDevice.SOURCE_JOYSTICK) {
			return false;
		}
		
		/*
		if (sEnableLogging) {
			Log.i(TAG, "onKeyDown keyCode=" + keyCode + " ("+DebugInput.debugGetButtonName(keyCode)+") source="+keyEvent.getSource());
		}
		*/
		
    	int playerNum = Controller.getPlayerNumByDeviceId(keyEvent.getDeviceId());
    	if (playerNum < 0 || playerNum >= Controller.MAX_CONTROLLERS) {
    		playerNum = 0;
    	}
		
		switch (keyCode) {
			case Controller.BUTTON_DPAD_DOWN:
				if (keyEvent.getSource() == InputDevice.SOURCE_JOYSTICK ) {
					if (sAxisValues.get(playerNum).get(MotionEvent.AXIS_HAT_Y) > DEAD_ZONE) {
						processKeyDown(playerNum, keyCode);
					}
					return true;
				} else {
					sButtonValues.get(playerNum).put(keyCode, true);
				}
				break;
			case Controller.BUTTON_DPAD_LEFT:
				if (keyEvent.getSource() == InputDevice.SOURCE_JOYSTICK ) {
					if (sAxisValues.get(playerNum).get(MotionEvent.AXIS_HAT_X) < -DEAD_ZONE) {
						processKeyDown(playerNum, keyCode);
					}
					return true;
				} else {
					sButtonValues.get(playerNum).put(keyCode, true);
				}
				break;
			case Controller.BUTTON_DPAD_RIGHT:
				if (keyEvent.getSource() == InputDevice.SOURCE_JOYSTICK ) {
					if (sAxisValues.get(playerNum).get(MotionEvent.AXIS_HAT_X) > DEAD_ZONE) {
						processKeyDown(playerNum, keyCode);
					}
					return true;
				} else {
					sButtonValues.get(playerNum).put(keyCode, true);
				}
				break;
			case Controller.BUTTON_DPAD_UP:
				if (keyEvent.getSource() == InputDevice.SOURCE_JOYSTICK ) {
					if (sAxisValues.get(playerNum).get(MotionEvent.AXIS_HAT_Y) < -DEAD_ZONE) {
						processKeyDown(playerNum, keyCode);
					}
					return true;
				} else {
					sButtonValues.get(playerNum).put(keyCode, true);
				}
				break;
			case KeyEvent.KEYCODE_BUTTON_L2:
				sAxisValues.get(playerNum).put(Controller.AXIS_L2, 1f);
				break;
			case KeyEvent.KEYCODE_BUTTON_R2:
				sAxisValues.get(playerNum).put(Controller.AXIS_R2, 1f);
				break;
		}
		
		return processKeyDown(playerNum, keyCode);
	}
	
	private boolean processKeyDown(int playerNum, int keyCode) {

		SparseBooleanArray stateButton = sStateButton.get(playerNum);
		if (null == stateButton) {
			Log.e(TAG, "processKeyDown stateButton playerNum="+playerNum+" keyCode="+keyCode+" is null");
			return false;
		}
		if (stateButton.get(keyCode) != true) {
			stateButton.put(keyCode, true);
			/*
			if (sEnableLogging) {
				Log.i(TAG, "processKeyDown stateButton playerNum="+playerNum+" keyCode="+keyCode+" ("+DebugInput.debugGetButtonName(keyCode)+") is="+stateButton.get(keyCode));
			}
			*/
			
			SparseBooleanArray stateButtonDown = sStateButtonDown.get(playerNum);
			if (null == stateButtonDown) {
				Log.e(TAG, "processKeyDown stateButtonDown playerNum="+playerNum+" keyCode="+keyCode+" is null");
			} else {
				stateButtonDown.put(keyCode, true);
				/*
				if (sEnableLogging) {
					Log.i(TAG, "processKeyDown stateButtonDown playerNum="+playerNum+" keyCode="+keyCode+" ("+DebugInput.debugGetButtonName(keyCode)+") is="+stateButtonDown.get(keyCode));
				}
				*/
			}
		}

		return true;
	}
	
	public float getAxis(int playerNum, int axis) {
		
		SparseArray<Float> stateAxises = sStateAxis.get(playerNum);
		if (null == stateAxises) {
			Log.e(TAG, "getAxis stateAxises is null");
			return 0;
		}
		
		Float result = stateAxises.get(axis);
		if (null == result) {
			return 0;
		}
		
		return result.floatValue();
	}
	
	public boolean getButton(int playerNum, int button) {
		/*
		if (sEnableLogging) {
			Log.i(TAG, "getButton playerNum="+playerNum+" button=" + button);
		}
		*/
		if (sStateButton.size() <= playerNum) {
			return false;
		}
		SparseBooleanArray stateButton = sStateButton.get(playerNum);
		if (null == stateButton) {
			Log.e(TAG, "getButton stateButton is null");
			return false;
		}		
		boolean result = stateButton.get(button);
		/*
		if (sEnableLogging) {
			Log.i(TAG, "getButton result=" + result);
		}
		*/
		return result;
	}
	
	public boolean getButtonDown(int playerNum, int button) {
		if (sLastStateButtonDown.size() <= playerNum) {
			return false;
		}
		SparseBooleanArray stateButtonDown = sLastStateButtonDown.get(playerNum);
		if (null == stateButtonDown) {
			Log.e(TAG, "getButtonDown stateButtonDown is null");
			return false;
		}
		boolean result = stateButtonDown.get(button);
		if (sEnableLogging &&
			result) {
			Log.i(TAG, "getButtonDown playerNum="+playerNum+" keyCode="+button+" ("+DebugInput.debugGetButtonName(button)+")");
		}
		return result;
	}
	
	public boolean getButtonUp(int playerNum, int button) {
		if (sLastStateButtonUp.size() <= playerNum) {
			return false;
		}
		SparseBooleanArray stateButtonUp = sLastStateButtonUp.get(playerNum);
		if (null == stateButtonUp) {
			Log.e(TAG, "getButtonUp stateButtonUp is null");
			return false;
		}		
		boolean result = stateButtonUp.get(button);
		if (sEnableLogging &&
			result) {
			Log.i(TAG, "getButtonUp playerNum="+playerNum+" keyCode="+button+" ("+DebugInput.debugGetButtonName(button)+")");
		}
		return result;
	}
	
	public void clearButtonStatesPressedReleased() {
		for (int playerNum = 0; playerNum < Controller.MAX_CONTROLLERS; ++playerNum) {
			SparseBooleanArray stateButtonDown = sStateButtonDown.get(playerNum);
			if (null == stateButtonDown) {
				Log.e(TAG, "clearButtonStatesPressedReleased stateButtonDown is null");
			} else {
				sLastStateButtonDown.get(playerNum).clear();
				for (int index = 0; index < stateButtonDown.size(); ++index) {
					int keyCode = sStateButtonDown.get(playerNum).keyAt(index);
					sLastStateButtonDown.get(playerNum).put(keyCode, true);
				}
				stateButtonDown.clear();
			}
			
			SparseBooleanArray stateButtonUp = sStateButtonUp.get(playerNum);
			if (null == stateButtonUp) {
				Log.e(TAG, "clearButtonStatesPressedReleased stateButtonUp is null");
			} else {
				sLastStateButtonUp.get(playerNum).clear();
				for (int index = 0; index < stateButtonUp.size(); ++index) {
					int keyCode = sStateButtonUp.get(playerNum).keyAt(index);
					sLastStateButtonUp.get(playerNum).put(keyCode, true);
				}
				stateButtonUp.clear();
			}
		}
	}
		
}
