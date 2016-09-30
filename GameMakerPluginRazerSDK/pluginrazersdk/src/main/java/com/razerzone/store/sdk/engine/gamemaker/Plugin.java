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
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.razerzone.store.sdk.CancelIgnoringResponseListener;
import com.razerzone.store.sdk.Controller;
import com.razerzone.store.sdk.GamerInfo;
import com.razerzone.store.sdk.PurchaseResult;
import com.razerzone.store.sdk.ResponseListener;
import com.razerzone.store.sdk.StoreFacade;
import com.razerzone.store.sdk.purchases.Product;
import com.razerzone.store.sdk.purchases.Purchasable;
import com.razerzone.store.sdk.purchases.Receipt;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by tim.graupmann on 7/13/2016.
 */
public class Plugin {
    private static final String TAG = Plugin.class.getSimpleName();

    private static final boolean sEnableLogging = false;

    private static final boolean sEnableInputLogging = false;

    private static final String sFalse = "false";
    private static final String sTrue = "true";
    private static final double sNoop = 0;

    private static boolean sInitialized = false;

    private static InputView sInputView = null;

    public static RunnerRelay sRelay = null;

    // Your game talks to the StoreFacade, which hides all the mechanics of doing an in-app purchase.
    private static StoreFacade sStoreFacade = null;

    // listener for init complete
    private static CancelIgnoringResponseListener<Bundle> sInitCompleteListener = null;

    // listener for requesting login
    private static ResponseListener<Void> sRequestLoginListener = null;

    // listener for requesting gamer info
    private static ResponseListener<GamerInfo> sRequestGamerInfoListener = null;

    // listener for getting products
    private static ResponseListener<List<Product>> sRequestProductsListener = null;

    // listener for requesting purchase
    private static ResponseListener<PurchaseResult> sRequestPurchaseListener = null;

    // listener for getting receipts
    private static ResponseListener<Collection<Receipt>> sRequestReceiptsListener = null;

    // listener for shutdown
    private static CancelIgnoringResponseListener<Void> sShutdownListener = null;

    // GameMakers async id
    private static ArrayList<String> sAsyncResults = new ArrayList<String>();

    public static double clearButtonStatesPressedReleased() {
        if (null != sInputView) {
            sInputView.clearButtonStatesPressedReleased();
        }
        return sNoop;
    }

    public static boolean dispatchGenericMotionEvent(MotionEvent motionEvent) {
        if (sEnableInputLogging) {
            DebugInput.debugControllerMotionEvent(motionEvent);
        }
        if (null != sInputView) {
            return sInputView.dispatchGenericMotionEvent(motionEvent);
        }
        return true;
    }

    public static boolean dispatchKeyEvent(KeyEvent keyEvent) {
        if (sEnableInputLogging) {
            Log.d(TAG, "dispatchKeyEvent keyCode=" + DebugInput.debugGetButtonName(keyEvent.getKeyCode()));
        }
        if (null != sInputView) {
            return sInputView.dispatchKeyEvent(keyEvent);
        }
        return true;
    }

    //boolean, string
    public static String getAnyButton(String strButton) {
        if (null != sInputView) {
            int button = Integer.parseInt(strButton);
            for (int playerNum = 0; playerNum < Controller.MAX_CONTROLLERS; ++playerNum) {
                boolean result = sInputView.getButton(playerNum, button);
                if (result) {
				/*
				if (sEnableLogging) {
					Log.i(TAG, "getAnyButton playerNum="+strPlayerNum+" button="+strButton+" result="+result);
				}
				*/
                    return sTrue;
                }
            }
        }
        return sFalse;
    }

    //boolean, string
    public static String getAnyButtonDown(String strButton) {
        if (null != sInputView) {
            int button = Integer.parseInt(strButton);
            for (int playerNum = 0; playerNum < Controller.MAX_CONTROLLERS; ++playerNum) {
                boolean result = sInputView.getButtonDown(playerNum, button);
                if (result) {
				/*
				if (sEnableLogging) {
					Log.i(TAG, "getAnyButtonDown playerNum="+strPlayerNum+" button="+strButton+" result="+result);
				}
				*/
                    return sTrue;
                }
            }
        }
        return sFalse;
    }

    //boolean, string
    public static String getAnyButtonUp(String strButton) {
        if (null != sInputView) {
            int button = Integer.parseInt(strButton);
            for (int playerNum = 0; playerNum < Controller.MAX_CONTROLLERS; ++playerNum) {
                boolean result = sInputView.getButtonUp(playerNum, button);
                if (result) {
				/*
				if (sEnableLogging) {
					Log.i(TAG, "getAnyButtonUp playerNum="+strPlayerNum+" button="+strButton+" result="+result);
				}
				*/
                    return sTrue;
                }
            }
        }
        return sFalse;
    }

    public static double getAsyncDataArrayCount() {
        if (sAsyncResults.size() > 0) {
            String jsonData = sAsyncResults.get(0);
            if (null == jsonData) {
                return 0;
            }
            try {
                JSONObject json = new JSONObject(jsonData);
                if (json.has("data")) {
                    JSONArray data = json.getJSONArray("data");
                    if (null == data) {
                        return 0;
                    } else {
                        return data.length();
                    }
                }
            } catch (JSONException e) {
            }
        }
        return 0;
    }

    public static double getAsyncDataArrayDouble(String strIndex, String field) {
        if (sAsyncResults.size() > 0) {
            String jsonData = sAsyncResults.get(0);
            if (null == jsonData) {
                return 0;
            }
            try {
                JSONObject json = new JSONObject(jsonData);
                if (json.has("data")) {
                    JSONArray data = json.getJSONArray("data");
                    if (null == data) {
                        return 0;
                    }

                    int index = Integer.parseInt(strIndex);
                    if (data.length() <= index) {
                        return 0;
                    }
                    JSONObject item = data.getJSONObject(index);
                    if (null == item) {
                        return 0;
                    }
                    if (item.has(field)) {
                        return item.getDouble(field);
                    }
                }
            } catch (JSONException e) {
            }
        }
        return 0;
    }

    public static String getAsyncDataArrayString(String strIndex, String field) {
        if (sAsyncResults.size() > 0) {
            String jsonData = sAsyncResults.get(0);
            if (null == jsonData) {
                return "";
            }
            try {
                JSONObject json = new JSONObject(jsonData);
                if (json.has("data")) {
                    JSONArray data = json.getJSONArray("data");
                    if (null == data) {
                        return "";
                    }

                    int index = Integer.parseInt(strIndex);
                    if (data.length() <= index) {
                        return "";
                    }
                    JSONObject item = data.getJSONObject(index);
                    if (null == item) {
                        return "";
                    }
                    if (item.has(field)) {
                        String result = item.getString(field);
                        if (null == result) {
                            return "";
                        } else {
                            return result;
                        }
                    }
                }
            } catch (JSONException e) {
            }
        }
        return "";
    }

    public static String getAsyncDataString(String field) {
        if (sAsyncResults.size() > 0) {
            String jsonData = sAsyncResults.get(0);
            if (null == jsonData) {
                return "";
            }
            try {
                JSONObject json = new JSONObject(jsonData);
                if (json.has("data")) {
                    JSONObject data = json.getJSONObject("data");
                    if (null == data) {
                        return "";
                    } else {
                        if (data.has(field)) {
                            String result = data.getString(field);
                            if (null == result) {
                                return "";
                            } else {
                                return result;
                            }
                        }
                    }
                }
            } catch (JSONException e) {
            }
        }
        return "";
    }

    public static String getAsyncMethod() {
        if (sAsyncResults.size() > 0) {
            String jsonData = sAsyncResults.get(0);
            Log.i(TAG, "getAsyncMethod jsonData=" + jsonData);
            if (null == jsonData) {
                return "";
            }
            try {
                JSONObject json = new JSONObject(jsonData);
                if (json.has("method")) {
                    String result = json.getString("method");
                    if (null == result) {
                        return "";
                    } else {
                        return result;
                    }
                }
            } catch (JSONException e) {
            }
        }
        return "";
    }

    public static String getAsyncResult() {
        if (sAsyncResults.size() > 0) {
            return sAsyncResults.get(0);
        } else {
            return "";
        }
    }

    //double, string, string
    public static double getAxis(String strPlayerNum, String strAxis) {
        if (null != sInputView) {
            int playerNum = Integer.parseInt(strPlayerNum);
            int axis = Integer.parseInt(strAxis);
            double result = sInputView.getAxis(playerNum, axis);
            /*
            if (sEnableLogging) {
                Log.i(TAG, "getAxis playerNum="+strPlayerNum+" axis="+strAxis+" result="+result);
            }
            */
            return result;
        } else {
            return 0;
        }
    }

    //boolean, string, string
    public static String getButton(String strPlayerNum, String strButton) {
        if (null != sInputView) {
            int playerNum = Integer.parseInt(strPlayerNum);
            int button = Integer.parseInt(strButton);
            boolean result = sInputView.getButton(playerNum, button);
            /*
            if (sEnableLogging) {
                Log.i(TAG, "getButton playerNum="+strPlayerNum+" button="+strButton+" result="+result);
            }
            */
            return Boolean.toString(result);
        }
        else
        {
            return sFalse;
        }
    }

    //boolean, string, string
    public static String getButtonDown(String strPlayerNum, String strButton) {
        if (null != sInputView) {
            int playerNum = Integer.parseInt(strPlayerNum);
            int button = Integer.parseInt(strButton);
            boolean result = sInputView.getButtonDown(playerNum, button);
		/*
		if (sEnableLogging) {
			Log.i(TAG, "getButtonDown playerNum="+strPlayerNum+" button="+strButton+" result="+result);
		}
		*/
            return Boolean.toString(result);
        }
        else
        {
            return sFalse;
        }
    }

    public static String getButtonName(double button) {
        if (null == sStoreFacade) {
            Log.e(TAG, "StoreFacade is null!");
            return "UNKNOWN";
        }
        Controller.ButtonData buttonData = Controller.getButtonData((int) button);
        if (null != buttonData) {
            String buttonName = buttonData.buttonName;
            if (null == buttonName) {
                return "UNKNOWN";
            }
            Log.d(TAG, "Button:" + (int) button + " ButtonName: " + buttonName);
            return buttonName;
        } else {
            return "UNKNOWN";
        }
    }

    public static String getButtonUp(String strPlayerNum, String strButton) {
        if (null != sInputView) {
            int playerNum = Integer.parseInt(strPlayerNum);
            int button = Integer.parseInt(strButton);
            boolean result = sInputView.getButtonUp(playerNum, button);
		/*
		if (sEnableLogging) {
			Log.i(TAG, "getButtonUp playerNum="+strPlayerNum+" button="+strButton+" result="+result);
		}
		*/
            return Boolean.toString(result);
        }
        else
        {
            return sFalse;
        }
    }

    public static String getDeviceHardwareName() {
        if (null == sStoreFacade) {
            Log.e(TAG, "StoreFacade is null!");
            return "UNKNOWN";
        }
        final StoreFacade.DeviceHardware deviceHardware = sStoreFacade.getDeviceHardware();
        if (null == deviceHardware) {
            return "UNKNOWN";
        }
        String deviceName = deviceHardware.deviceName();
        if (null == deviceName) {
            return "UNKNOWN";
        }
        return deviceName;
    }

    public static RunnerRelay getRelay() { return sRelay; }

    public static String init(final String secretApiKey) {

        if (sEnableLogging) {
            Log.d(TAG, "init: secretApiKey=" + secretApiKey);
        }

        final Activity activity = Plugin.getRelay().getCurrentActivity();
        if (null == activity) {
            Log.d(TAG, "Current activity is null");
            return sFalse;
        } else {
            Log.d(TAG, "Current activity is valid");
        }

        final FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);
        if (null == content) {
            Log.d(TAG, "Content is null");
            return sFalse;
        } else {
            Runnable runnable = new Runnable() {
                public void run() {
                    Log.d(TAG, "Disable screensaver");
                    content.setKeepScreenOn(true);

                    Log.d(TAG, "Add inputView");
                    sInputView = new InputView(activity);

                    Bundle developerInfo = null;
                    try {
                        developerInfo = StoreFacade.createInitBundle(secretApiKey);
                    } catch (InvalidParameterException e) {
                        Log.e(TAG, e.getMessage());
                        activity.finish();
                        return;
                    }

                    if (sEnableLogging) {
                        Log.d(TAG, "developer_id=" + developerInfo.getString(StoreFacade.DEVELOPER_ID));
                    }

                    if (sEnableLogging) {
                        Log.d(TAG, "developer_public_key length=" + developerInfo.getByteArray(StoreFacade.DEVELOPER_PUBLIC_KEY).length);
                    }

                    sInitCompleteListener = new CancelIgnoringResponseListener<Bundle>() {
                        @Override
                        public void onSuccess(Bundle bundle) {
                            if (sEnableLogging) {
                                Log.d(TAG, "InitCompleteListener: onSuccess");
                            }
                            JSONObject json = new JSONObject();
                            try {
                                json.put("method", "onSuccessInit");
                            } catch (JSONException e1) {
                            }
                            String jsonData = json.toString();
                            sAsyncResults.add(jsonData);
                            sInitialized = true;
                        }

                        @Override
                        public void onFailure(int errorCode, String errorMessage, Bundle optionalData) {
                            if (sEnableLogging) {
                                Log.d(TAG, "InitCompleteListener: onFailure errorCode="+errorCode+" errorMessage="+errorMessage);
                            }
                            JSONObject json = new JSONObject();
                            try {
                                json.put("method", "onFailureInit");
                                JSONObject data = new JSONObject();
                                data.put("errorCode", Integer.toString(errorCode));
                                data.put("errorMessage", errorMessage);
                                json.put("data", data);
                            } catch (JSONException e1) {
                            }
                            String jsonData = json.toString();
                            sAsyncResults.add(jsonData);
                        }
                    };

                    sStoreFacade = StoreFacade.getInstance();
                    try {
                        sStoreFacade.init(activity, developerInfo, sInitCompleteListener);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    sRequestLoginListener = new ResponseListener<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            if (sEnableLogging) {
                                Log.d(TAG, "sRequestLoginListener: onSuccess");
                            }
                            JSONObject json = new JSONObject();
                            try {
                                json.put("method", "onSuccessRequestLogin");
                            } catch (JSONException e1) {
                            }
                            String jsonData = json.toString();
                            sAsyncResults.add(jsonData);
                        }

                        @Override
                        public void onFailure(int errorCode, String errorMessage, Bundle optionalData) {
                            if (sEnableLogging) {
                                Log.e(TAG, "sRequestLoginListener: onFailure errorCode=" + errorCode + " errorMessage=" + errorMessage);
                            }
                            JSONObject json = new JSONObject();
                            try {
                                json.put("method", "onFailureRequestLogin");
                                JSONObject data = new JSONObject();
                                data.put("errorCode", Integer.toString(errorCode));
                                data.put("errorMessage", errorMessage);
                                json.put("data", data);
                            } catch (JSONException e1) {
                            }
                            String jsonData = json.toString();
                            sAsyncResults.add(jsonData);
                        }

                        @Override
                        public void onCancel() {
                            if (sEnableLogging) {
                                Log.d(TAG, "sRequestLoginListener: onCancel");
                            }
                            JSONObject json = new JSONObject();
                            try {
                                json.put("method", "onCancelRequestLogin");
                            } catch (JSONException e1) {
                            }
                            String jsonData = json.toString();
                            sAsyncResults.add(jsonData);
                        }
                    };

                    sRequestGamerInfoListener = new ResponseListener<GamerInfo>() {
                        @Override
                        public void onSuccess(GamerInfo info) {
                            if (null == info) {
                                Log.e(TAG, "GamerInfo is null!");
                                return;
                            }
                            if (sEnableLogging) {
                                Log.d(TAG, "sRequestGamerInfoListener: onSuccess uuid=" + info.getUuid() + " username=" + info.getUsername());
                            }
                            JSONObject json = new JSONObject();
                            try {
                                json.put("method", "onSuccessRequestGamerInfo");
                                JSONObject data = new JSONObject();
                                data.put("uuid", info.getUuid());
                                data.put("username", info.getUsername());
                                json.put("data", data);
                            } catch (JSONException e1) {
                            }
                            String jsonData = json.toString();
                            sAsyncResults.add(jsonData);
                        }

                        @Override
                        public void onFailure(int errorCode, String errorMessage, Bundle optionalData) {
                            if (sEnableLogging) {
                                Log.e(TAG, "sRequestGamerInfoListener: onFailure errorCode=" + errorCode + " errorMessage=" + errorMessage);
                            }
                            JSONObject json = new JSONObject();
                            try {
                                json.put("method", "onFailureRequestGamerInfo");
                                JSONObject data = new JSONObject();
                                data.put("errorCode", Integer.toString(errorCode));
                                data.put("errorMessage", errorMessage);
                                json.put("data", data);
                            } catch (JSONException e1) {
                            }
                            String jsonData = json.toString();
                            sAsyncResults.add(jsonData);
                        }

                        @Override
                        public void onCancel() {
                            if (sEnableLogging) {
                                Log.d(TAG, "sRequestGamerInfoListener: onCancel");
                            }
                            JSONObject json = new JSONObject();
                            try {
                                json.put("method", "onCancelRequestGamerInfo");
                            } catch (JSONException e1) {
                            }
                            String jsonData = json.toString();
                            sAsyncResults.add(jsonData);
                        }
                    };

                    sRequestProductsListener = new ResponseListener<List<Product>>() {
                        @Override
                        public void onSuccess(final List<Product> products) {
                            if (null == products) {
                                Log.e(TAG, "Products are null!");
                                return;
                            }
                            if (sEnableLogging) {
                                Log.i(TAG, "sRequestProductsListener: onSuccess");
                            }
                            JSONObject json = new JSONObject();
                            try {
                                json.put("method", "onSuccessRequestProducts");
                                JSONArray data = new JSONArray();
                                int index = 0;
                                for (Product product : products) {
                                    JSONObject item = new JSONObject();
                                    try {
                                        item.put("currencyCode", product.getCurrencyCode());
                                        item.put("description", product.getDescription());
                                        item.put("identifier", product.getIdentifier());
                                        item.put("localPrice", product.getLocalPrice());
                                        item.put("name", product.getName());
                                        item.put("originalPrice", product.getOriginalPrice());
                                        item.put("percentOff", product.getPercentOff());
                                        item.put("developerName", product.getDeveloperName());
                                        data.put(index, item);
                                        ++index;
                                    } catch (JSONException e2) {
                                    }
                                }
                                json.put("data", data);
                            } catch (JSONException e1) {
                            }
                            String jsonData = json.toString();
                            sAsyncResults.add(jsonData);
                        }

                        @Override
                        public void onFailure(int errorCode, String errorMessage, Bundle optionalData) {
                            if (sEnableLogging) {
                                Log.e(TAG, "sRequestProductsListener: onFailure errorCode=" + errorCode + " errorMessage=" + errorMessage);
                            }
                            JSONObject json = new JSONObject();
                            try {
                                json.put("method", "onFailureRequestProducts");
                                JSONObject data = new JSONObject();
                                data.put("errorCode", Integer.toString(errorCode));
                                data.put("errorMessage", errorMessage);
                                json.put("data", data);
                            } catch (JSONException e1) {
                            }
                            String jsonData = json.toString();
                            sAsyncResults.add(jsonData);
                        }

                        @Override
                        public void onCancel() {
                            if (sEnableLogging) {
                                Log.i(TAG, "sRequestProductsListener: onCancel");
                            }
                            JSONObject json = new JSONObject();
                            try {
                                json.put("method", "onCancelRequestProducts");
                            } catch (JSONException e1) {
                            }
                            String jsonData = json.toString();
                            sAsyncResults.add(jsonData);
                        }
                    };

                    sRequestPurchaseListener = new ResponseListener<PurchaseResult>() {

                        @Override
                        public void onSuccess(PurchaseResult result) {
                            if (null == result) {
                                Log.e(TAG, "PurchaseResult is null!");
                                return;
                            }
                            if (sEnableLogging) {
                                Log.i(TAG, "sRequestPurchaseListener: onSuccess");
                            }
                            JSONObject json = new JSONObject();
                            try {
                                json.put("method", "onSuccessRequestPurchase");
                                JSONObject data = new JSONObject();
                                data.put("identifier", result.getProductIdentifier());
                                data.put("ownerId", result.getOrderId());
                                json.put("data", data);
                            } catch (JSONException e1) {
                            }
                            String jsonData = json.toString();
                            sAsyncResults.add(jsonData);
                        }

                        @Override
                        public void onFailure(int errorCode, String errorMessage, Bundle optionalData) {
                            if (sEnableLogging) {
                                Log.e(TAG, "sRequestPurchaseListener: onFailure errorCode=" + errorCode + " errorMessage=" + errorMessage);
                            }
                            JSONObject json = new JSONObject();
                            try {
                                json.put("method", "onFailureRequestPurchase");
                                JSONObject data = new JSONObject();
                                data.put("errorCode", Integer.toString(errorCode));
                                data.put("errorMessage", errorMessage);
                                json.put("data", data);
                            } catch (JSONException e1) {
                            }
                            String jsonData = json.toString();
                            sAsyncResults.add(jsonData);
                        }

                        @Override
                        public void onCancel() {
                            if (sEnableLogging) {
                                Log.i(TAG, "sRequestPurchaseListener: onCancel");
                            }
                            JSONObject json = new JSONObject();
                            try {
                                json.put("method", "onCancelRequestPurchase");
                            } catch (JSONException e1) {
                            }
                            String jsonData = json.toString();
                            sAsyncResults.add(jsonData);
                        }
                    };

                    sRequestReceiptsListener = new ResponseListener<Collection<Receipt>>() {

                        @Override
                        public void onSuccess(Collection<Receipt> receipts) {
                            if (null == receipts) {
                                Log.e(TAG, "Receipts are null!");
                                return;
                            }
                            if (sEnableLogging) {
                                Log.i(TAG, "requestReceipts onSuccess: received " + receipts.size() + " receipts");
                            }
                            JSONObject json = new JSONObject();
                            try {
                                json.put("method", "onSuccessRequestReceipts");
                                JSONArray data = new JSONArray();
                                int index = 0;
                                for (Receipt receipt : receipts) {
                                    JSONObject item = new JSONObject();
                                    try {
                                        item.put("identifier", receipt.getIdentifier());
                                        item.put("purchaseDate", receipt.getPurchaseDate());
                                        item.put("gamer", receipt.getGamer());
                                        item.put("uuid", receipt.getUuid());
                                        item.put("localPrice", receipt.getLocalPrice());
                                        item.put("currency", receipt.getCurrency());
                                        item.put("generatedDate", receipt.getGeneratedDate());
                                        data.put(index, item);
                                        ++index;
                                    } catch (JSONException e2) {
                                    }
                                }
                                json.put("data", data);
                            } catch (JSONException e1) {
                            }
                            String jsonData = json.toString();
                            sAsyncResults.add(jsonData);
                        }

                        @Override
                        public void onFailure(int errorCode, String errorMessage, Bundle optionalData) {
                            if (sEnableLogging) {
                                Log.e(TAG, "requestReceipts onFailure: errorCode=" + errorCode + " errorMessage=" + errorMessage);
                            }
                            JSONObject json = new JSONObject();
                            try {
                                json.put("method", "onFailureRequestReceipts");
                                JSONObject data = new JSONObject();
                                data.put("errorCode", Integer.toString(errorCode));
                                data.put("errorMessage", errorMessage);
                                json.put("data", data);
                            } catch (JSONException e1) {
                            }
                            String jsonData = json.toString();
                            sAsyncResults.add(jsonData);
                        }

                        @Override
                        public void onCancel() {
                            if (sEnableLogging) {
                                Log.i(TAG, "requestReceipts onCancel");
                            }
                            JSONObject json = new JSONObject();
                            try {
                                json.put("method", "onCancelRequestReceipts");
                            } catch (JSONException e1) {
                            }
                            String jsonData = json.toString();
                            sAsyncResults.add(jsonData);
                        }
                    };

                    sShutdownListener = new CancelIgnoringResponseListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            if (sEnableLogging) {
                                Log.i(TAG, "shutdown onSuccess");
                            }
                            JSONObject json = new JSONObject();
                            try {
                                json.put("method", "onSuccessShutdown");
                            } catch (JSONException e1) {
                            }
                            String jsonData = json.toString();
                            sAsyncResults.add(jsonData);
                        }

                        @Override
                        public void onFailure(int i, String s, Bundle bundle) {
                            if (sEnableLogging) {
                                Log.i(TAG, "shutdown onFailure");
                            }
                            JSONObject json = new JSONObject();
                            try {
                                json.put("method", "onFailureShutdown");
                            } catch (JSONException e1) {
                            }
                            String jsonData = json.toString();
                            sAsyncResults.add(jsonData);
                        }
                    };

                    Controller.init(activity);
                }
            };
            activity.runOnUiThread(runnable);
        }

        return sTrue;
    }

    public static String isAnyConnected() {
        for (int playerNum = 0; playerNum < Controller.MAX_CONTROLLERS; ++playerNum) {
            if (Controller.getControllerByPlayer(playerNum) != null) {
				/*
				if (sEnableLogging) {
					Log.i(TAG, "isAnyConnected playerNum="+strPlayerNum);
				}
				*/
                return sTrue;
            }
        }
        return sFalse;
    }

    public static String isConnected(String strPlayerNum) {
        int playerNum = Integer.parseInt(strPlayerNum);
        if (Controller.getControllerByPlayer(playerNum) == null) {
            return sFalse;
        } else {
            return sTrue;
        }
    }

    public static String isInitialized() {
        return Boolean.toString(sInitialized);
    }

    public static boolean onGenericMotionEvent(MotionEvent motionEvent) {
        if (null != sInputView) {
            return sInputView.onGenericMotionEvent(motionEvent);
        }
        return true;
    }

    public static boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
        if (sEnableInputLogging) {
            Log.d(TAG, "onKeyDown keyCode=" + DebugInput.debugGetButtonName(keyEvent.getKeyCode()));
        }
        if (null != sInputView) {
            return sInputView.onKeyDown(keyCode, keyEvent);
        }
        return true;
    }

    public static boolean onKeyUp(int keyCode, KeyEvent keyEvent) {
        if (sEnableInputLogging) {
            Log.d(TAG, "onKeyUp keyCode=" + DebugInput.debugGetButtonName(keyEvent.getKeyCode()));
        }
        if (null != sInputView) {
            return sInputView.onKeyUp(keyCode, keyEvent);
        }
        return true;
    }

    public static String popAsyncResult() {
        if (sAsyncResults.size() > 0) {
            sAsyncResults.remove(0);
        }
        return "";
    }

    public static void processOnActivityResult(int requestCode, int resultCode, Intent data) {

		/*
		if (sEnableLogging) {
			Log.i(TAG, "onActivityResult called in MyExtensionClass extension");
		}
		*/

        if (null == sStoreFacade) {
            return;
        }

        // Forward this result to the facade, in case it is waiting for any activity results
        sStoreFacade.processActivityResult(requestCode, resultCode, data);
    }

    public static double requestLogin() {
        if (null == sStoreFacade) {
            Log.e(TAG, "StoreFacade is null!");
            return sNoop;
        }
        final Activity activity = Plugin.getRelay().getCurrentActivity();
        if (null == activity) {
            Log.d(TAG, "requestLogin: Activity is null");
            return sNoop;
        }
        if (null == sRequestLoginListener) {
            Log.e(TAG, "requestLogin: sRequestLoginListener is null");
            return sNoop;
        }
        Runnable runnable = new Runnable() {
            public void run() {
                sStoreFacade.requestLogin(activity, sRequestLoginListener);
            }
        };
        activity.runOnUiThread(runnable);
        return sNoop;
    }

    public static double requestGamerInfo() {
        if (null == sStoreFacade) {
            Log.e(TAG, "StoreFacade is null!");
            return sNoop;
        }
        final Activity activity = Plugin.getRelay().getCurrentActivity();
        if (null == activity) {
            Log.d(TAG, "requestGamerInfo: Activity is null");
            return sNoop;
        }
        if (null == sRequestGamerInfoListener) {
            Log.e(TAG, "requestGamerInfo: sRequestGamerInfoListener is null");
            return sNoop;
        }
        Runnable runnable = new Runnable() {
            public void run() {
                sStoreFacade.requestGamerInfo(activity, sRequestGamerInfoListener);
            }
        };
        activity.runOnUiThread(runnable);
        return sNoop;
    }

    public static double requestProducts(final String identifiers) {
        if (null == sStoreFacade) {
            Log.e(TAG, "StoreFacade is null!");
            return sNoop;
        }
        final Activity activity = Plugin.getRelay().getCurrentActivity();
        if (null == activity) {
            Log.d(TAG, "Activity is null");
            return sNoop;
        }
        Runnable runnable = new Runnable() {
            public void run() {

                List<String> products = new ArrayList<String>();
                String[] ids = identifiers.split(",");
                for (String identifier : ids) {
                    products.add(identifier);
                }
                String[] purchasables = new String[products.size()];
                purchasables = products.toArray(purchasables);
                sStoreFacade.requestProductList(activity, purchasables, sRequestProductsListener);
            }
        };
        activity.runOnUiThread(runnable);
        return sNoop;
    }

    public static double requestPurchase(final String identifier, final String productType) {
        if (null == sStoreFacade) {
            Log.e(TAG, "StoreFacade is null!");
            return sNoop;
        }
        final Activity activity = Plugin.getRelay().getCurrentActivity();
        if (null == activity) {
            Log.d(TAG, "Activity is null");
            return sNoop;
        }
        if (null == sRequestPurchaseListener) {
            Log.e(TAG, "requestPurchase: sRequestPurchaseListener is null");
            return sNoop;
        }
        Runnable runnable = new Runnable() {
            public void run() {
                Product product = new Product(identifier, "", 0, 0, "", 0, 0, "", "", Product.Type.valueOf(productType));
                Purchasable purchasable = product.createPurchasable();
                sStoreFacade.requestPurchase(activity, purchasable, sRequestPurchaseListener);
            }
        };
        activity.runOnUiThread(runnable);
        return sNoop;
    }

    public static double requestReceipts() {
        if (null == sStoreFacade) {
            Log.e(TAG, "StoreFacade is null!");
            return sNoop;
        }
        final Activity activity = Plugin.getRelay().getCurrentActivity();
        if (null == activity) {
            Log.d(TAG, "Activity is null");
            return sNoop;
        }
        if (null == sRequestReceiptsListener) {
            Log.e(TAG, "requestReceipts: sRequestReceiptsListener is null");
            return sNoop;
        }
        Runnable runnable = new Runnable() {
            public void run() {
                sStoreFacade.requestReceipts(activity, sRequestReceiptsListener);
            }
        };
        activity.runOnUiThread(runnable);
        return sNoop;
    }

    public static void setRelay(RunnerRelay relay) { sRelay = relay; }

    public static double shutdown() {
        if (null == sStoreFacade) {
            Log.e(TAG, "StoreFacade is null!");
            return sNoop;
        }
        sStoreFacade.shutdown(sShutdownListener);
        return 0;
    }
}
