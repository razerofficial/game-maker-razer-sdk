package ${YYAndroidPackageName};

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import com.razerzone.store.sdk.engine.gamemaker.Plugin;
import com.razerzone.store.sdk.engine.gamemaker.RunnerRelay;
import com.yoyogames.runner.RunnerJNILib;
import java.io.IOException;
import java.io.InputStream;
import java.lang.String;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ${YYAndroidPackageName}.R;
import ${YYAndroidPackageName}.RunnerActivity;

public class RazerSDK extends RunnerSocial implements RunnerRelay {
	
	private static final String TAG = RazerSDK.class.getSimpleName();
	
	private static final boolean sEnableLogging = false;

    public double clearButtonStatesPressedReleased() {
        return Plugin.clearButtonStatesPressedReleased();
    }

    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent motionEvent) {
        return Plugin.dispatchGenericMotionEvent(motionEvent);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        return Plugin.dispatchKeyEvent(keyEvent);
    }

    public String getAnyButton(String strButton) {
        return Plugin.getAnyButton(strButton);
    }

    public String getAnyButtonDown(String strButton) {
        return Plugin.getAnyButtonDown(strButton);
    }

    public String getAnyButtonUp(String strButton) {
        return Plugin.getAnyButtonUp(strButton);
    }

    public double getAsyncDataArrayCount() {
        if (sEnableLogging) {
            Log.d(TAG, "getAsyncDataArrayCount:");
        }
        return Plugin.getAsyncDataArrayCount();
    }

    public double getAsyncDataArrayDouble(String strIndex, String field) {
        if (sEnableLogging) {
            Log.d(TAG, "getAsyncDataArrayDouble:");
        }
        return Plugin.getAsyncDataArrayDouble(strIndex, field);
    }

    public String getAsyncDataArrayString(String strIndex, String field) {
        if (sEnableLogging) {
            Log.d(TAG, "getAsyncDataArrayString:");
        }
        return Plugin.getAsyncDataArrayString(strIndex, field);
    }

    public String getAsyncDataString(String field) {
        if (sEnableLogging) {
            Log.d(TAG, "getAsyncDataString:");
        }
        return Plugin.getAsyncDataString(field);
    }

    public String getAsyncMethod() {
        if (sEnableLogging) {
            Log.d(TAG, "getAsyncMethod:");
        }
        return Plugin.getAsyncMethod();
    }

    public String getAsyncResult() {
        if (sEnableLogging) {
            Log.d(TAG, "getAsyncResult:");
        }
        return Plugin.getAsyncResult();
    }

    public double getAxis(String strPlayerNum, String strAxis) {
        return Plugin.getAxis(strPlayerNum, strAxis);
    }

    public String getButton(String strPlayerNum, String strButton) {
        return Plugin.getButton(strPlayerNum, strButton);
    }

    public String getButtonDown(String strPlayerNum, String strButton) {
        return Plugin.getButtonDown(strPlayerNum, strButton);
    }

    public String getButtonName(double button) {
        return Plugin.getButtonName(button);
    }

    public String getButtonUp(String strPlayerNum, String strButton) {
        return Plugin.getButtonUp(strPlayerNum, strButton);
    }

	@Override
	public Activity getCurrentActivity() {
		return RunnerActivity.CurrentActivity;
	}

    public String getDeviceHardwareName() {
        return Plugin.getDeviceHardwareName();
    }
	
	@Override
	public void Init() {
		if (sEnableLogging) {
			Log.d(TAG, "Init:");
		}
		Plugin.setRelay(this);
	}

	public String init(final String secretApiKey) {
        if (sEnableLogging) {
            Log.d(TAG, "init:");
        }
		return Plugin.init(secretApiKey);
	}

    public String isAnyConnected() {
        return Plugin.isAnyConnected();
    }

    public String isConnected(String strPlayerNum) {
        return Plugin.isConnected(strPlayerNum);
    }

    public static String isInitialized() {
        if (sEnableLogging) {
            Log.d(TAG, "isInitialized:");
        }
        return Plugin.isInitialized();
    }

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (sEnableLogging) {
			Log.i(TAG, "onActivityResult: requestCode="+requestCode+" resultCode="+resultCode);
		}
		Plugin.processOnActivityResult(requestCode, resultCode, data);
	}

    @Override
    public boolean onGenericMotionEvent(MotionEvent motionEvent) {
        return Plugin.onGenericMotionEvent(motionEvent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        return Plugin.onKeyDown(keyCode, keyEvent);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent keyEvent) {
        return Plugin.onKeyUp(keyCode, keyEvent);
    }

    public String popAsyncResult() {
        if (sEnableLogging) {
            Log.d(TAG, "popAsyncResult:");
        }
        return Plugin.popAsyncResult();
    }

    public double requestGamerInfo() {
        if (sEnableLogging) {
            Log.d(TAG, "requestGamerInfo:");
        }
        return Plugin.requestGamerInfo();
    }

    public double requestProducts(final String identifiers) {
        if (sEnableLogging) {
            Log.d(TAG, "requestProducts:");
        }
        return Plugin.requestProducts(identifiers);
    }

    public double requestPurchase(final String identifier) {
        if (sEnableLogging) {
            Log.d(TAG, "requestPurchase:");
        }
        return Plugin.requestPurchase(identifier);
    }

    public double requestReceipts() {
        if (sEnableLogging) {
            Log.d(TAG, "requestReceipts:");
        }
        return Plugin.requestReceipts();
    }

    public double shutdown() {
        if (sEnableLogging) {
            Log.d(TAG, "shutdown:");
        }
        return Plugin.shutdown();
    }
}
