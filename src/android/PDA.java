package com.zlst.pda;
 
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.symbol.scanning.BarcodeManager;
import com.symbol.scanning.ScanDataCollection;
import com.symbol.scanning.Scanner;
import com.symbol.scanning.ScannerException;
import com.symbol.scanning.ScannerInfo;
import com.symbol.scanning.StatusData;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PDA extends CordovaPlugin implements Scanner.StatusListener, Scanner.DataListener  {
    private Scanner scanner=null;

    private BarcodeManager mBarcodeManager = new BarcodeManager();
    private ScannerInfo mInfo = new ScannerInfo("se4710_cam_builtin", "DECODER_2D");


    public static CallbackContext cb = null;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if(scanner==null){
            initPDA();
        }
        cb = callbackContext;
        if (action.equals("read")) {
            this.read();
            return true;
        } else if (action.equals("cancelRead")) {
            cb = callbackContext;
            this.cancelRead();
            return true;
        }
        return false;
    }

    public void initPDA() {
        try {
            scanner = mBarcodeManager.getDevice(mInfo);
            scanner.addStatusListener(this);
            scanner.addDataListener(this);
            scanner.enable();
        } catch (ScannerException e) {
            e.printStackTrace();
        }
    }


    public void read() {
        try {
            scanner.read();
        } catch (ScannerException e) {
            e.printStackTrace();
        }
    }

    public String cancelRead() {
        if ((scanner != null) && (scanner.isEnable())) {
            try {
                scanner.cancelRead();
            } catch (ScannerException e) {
                e.printStackTrace();
                return e.toString();
            }
        }

        return StatusData.ScannerStates.IDLE.name();
    }

    public void onStop() {
        try {
            scanner.cancelRead();
            // scanner.disable();
        } catch (ScannerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatus(StatusData statusData) {
        Log.d("ScannerGunPlugin", "onStatus: " + statusData.toString());
        if (statusData.getState() == StatusData.ScannerStates.IDLE) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        JSONArray newArray = new JSONArray();
                        newArray.put("timeout");

                        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK,newArray);
                        pluginResult.setKeepCallback(true);
                        cb.sendPluginResult(pluginResult);

                    }

                }, 500);
        }
    }

    @Override
    public void onData(ScanDataCollection scanDataCollection) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("friendly_name", scanDataCollection.getFriendlyName());
            jsonObject.put("result", scanDataCollection.getResult().name());
            JSONArray scandatas = new JSONArray();
            for (ScanDataCollection.ScanData scanData : scanDataCollection.getScanData()) {
                JSONObject data = new JSONObject();
                data.put("label_type", scanData.getLabelType().name());
                data.put("data", scanData.getData());
                data.put("timestamp", scanData.getTimeStamp());
                scandatas.put(data);
            }
            jsonObject.put("scandatas", scandatas);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray newArray = new JSONArray();
        newArray.put(jsonObject.toString());

        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK,newArray);
        pluginResult.setKeepCallback(true);
        cb.sendPluginResult(pluginResult);

    }
    
}