package org.apache.cordova.facerecognition;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import com.webank.facelight.contants.WbCloudFaceContant;
import com.webank.facelight.contants.WbFaceError;
import com.webank.facelight.contants.WbFaceVerifyResult;
import com.webank.facelight.listerners.WbCloudFaceVeirfyLoginListner;
import com.webank.facelight.listerners.WbCloudFaceVeirfyResultListener;
import com.webank.facelight.tools.IdentifyCardValidate;
import com.webank.facelight.tools.WbCloudFaceVerifySdk;
import com.webank.facelight.ui.FaceVerifyStatus;
import com.webank.mbank.wehttp.WeLog;
import com.webank.mbank.wehttp.WeOkHttp;
import com.webank.mbank.wehttp.WeReq;

/**
 * This class echoes a string called from JavaScript.
 */
public class FaceRecognition extends CordovaPlugin {
    private static final String TAG = "FaceVerifyDemoActivity";

    private SignUseCase signUseCase = new SignUseCase(this);

    private String name = "";
    private String id = "";

    private String userId = "WbFaceVerifyAll" + System.currentTimeMillis();
    private String nonce = "52014832029547845621032584562012";
    private String compareType = WbCloudFaceContant.NONE;
    private String keyLicence = "NwKivlx4CuaA0r1Ri/x7VGugcN5bfIUm9Q0ZfUHmr2R6mjwuZUGRUNL+ydQhfRjaCl4s+YdUnVPxGGBfxCeSYpHk0AZIRUHUy5TETKUlSKrolSR+svPde8ZImxQhXIK5Tyr+zweHGZpPzOsuYglLuPeECYNGtDfw+4pTEIXFkwBbUMuoAt/RcLBxGpjB8Ol5meMP/8A10YfWJwPvuhVttMxXX7fIqPVxrC7bMRG8Y0AXUJQtJmFR8u35BvCY1YZYrQ3puOHTVvAdOJH53+w+kKVt1sMzVaa/1qnjgNHtC8DkHJ6+FJx5nOn2Etah7oWKE4dQrd+HOjXQeWFRdb9/ww==";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("coolMethod")) {
            String message = args.getString(0);
            this.coolMethod(message, callbackContext);
            return true;
        }
        return false;
    }

    private void coolMethod(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            Toast.makeText(cordova.getContext(), "11111111111", Toast.LENGTH_SHORT).show();
            checkOnId("data_mode_act");
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    private void checkOnId(String mode) {
        String appId = "TIDA0001";

        //自带源和活体对比不需要姓名 身份证
        Log.i(TAG, "No need check Param!");
        name = "";
        id = "";
        Log.i(TAG, "Called Face Verify Sdk!" + mode);
        signUseCase.execute(mode, appId, userId, nonce);
    }

    //仅活体检测
    public void openCloudFaceService(String sign) {
        final FaceVerifyStatus.Mode mode = FaceVerifyStatus.Mode.ACT;

        String order = "testReflect" + System.currentTimeMillis();
        String appId = "TIDA0001";

        Log.e("name----", name);
        Log.e("id----", id);
        Log.e("order----", order);
        Log.e("appId----", appId);
        Log.e("nonce----", nonce);
        Log.e("userId----", userId);
        Log.e("sign----", sign);
        Log.e("mode----", mode.toString());
        Log.e("keyLicence----", keyLicence);

        Bundle data = new Bundle();
        WbCloudFaceVerifySdk.InputData inputData = new WbCloudFaceVerifySdk.InputData(
                name,
                "01",
                id,
                order,
                appId,
                "1.0.0",
                nonce,
                userId,
                sign,
                mode,
                keyLicence);

        data.putSerializable(WbCloudFaceContant.INPUT_DATA, inputData);
        //是否展示刷脸成功页面，默认展示
        data.putBoolean(WbCloudFaceContant.SHOW_SUCCESS_PAGE, true);
        //是否展示刷脸失败页面，默认展示
        data.putBoolean(WbCloudFaceContant.SHOW_FAIL_PAGE, true);
        //颜色设置
        data.putString(WbCloudFaceContant.COLOR_MODE, WbCloudFaceContant.BLACK);
        //是否需要录制上传视频 默认需要
        data.putBoolean(WbCloudFaceContant.VIDEO_UPLOAD, false);
        //是否开启闭眼检测，默认不开启
        data.putBoolean(WbCloudFaceContant.ENABLE_CLOSE_EYES, false);
        //是否播放提示音，默认播放
        data.putBoolean(WbCloudFaceContant.PLAY_VOICE, true);

        //设置选择的比对类型  默认为公安网纹图片对比
        //公安网纹图片比对 WbCloudFaceVerifySdk.ID_CRAD
        //自带比对源比对  WbCloudFaceVerifySdk.SRC_IMG
        //仅活体检测  WbCloudFaceVerifySdk.NONE
        //默认公安网纹图片比对
        data.putString(WbCloudFaceContant.COMPARE_TYPE, compareType);

        Log.i(TAG, "init");
        WbCloudFaceVerifySdk.getInstance().init(cordova.getActivity(), data, new WbCloudFaceVeirfyLoginListner() {
            @Override
            public void onLoginSuccess() {
                Log.i(TAG, "onLoginSuccess");

                WbCloudFaceVerifySdk.getInstance().startWbFaceVeirifySdk(cordova.getActivity(), new WbCloudFaceVeirfyResultListener() {
                    @Override
                    public void onFinish(WbFaceVerifyResult result) {
                        if (result != null) {
                            if (result.isSuccess()) {
                                Log.d(TAG, "刷脸成功! Sign=" + result.getSign() + "; liveRate=" + result.getLiveRate() +
                                        "; similarity=" + result.getSimilarity() + "userImageString=" + result.getUserImageString());
                                Toast.makeText(cordova.getActivity(), "刷脸成功", Toast.LENGTH_SHORT).show();
                            } else {
                                WbFaceError error = result.getError();
                                if (error != null) {
                                    Log.d(TAG, "刷脸失败！domain=" + error.getDomain() + " ;code= " + error.getCode()
                                            + " ;desc=" + error.getDesc() + ";reason=" + error.getReason() + ";sign=" + result.getSign());
                                    if (error.getDomain().equals(WbFaceError.WBFaceErrorDomainCompareServer)) {
                                        Log.d(TAG, "对比失败，liveRate=" + result.getLiveRate() +
                                                "; similarity=" + result.getSimilarity() + ";sign=" + result.getSign());
                                    }
                                    Toast.makeText(cordova.getActivity(), "刷脸失败!" + error.getDesc(),
                                            Toast.LENGTH_LONG).show();
                                } else {
                                    Log.e(TAG, "sdk返回error为空！");
                                }
                            }
                        } else {
                            Log.e(TAG, "sdk返回结果为空！");
                        }
                        //测试用代码
                        //不管刷脸成功失败，只要结束了，自带对比和活体检测都更新userId
                        if (!compareType.equals(WbCloudFaceContant.ID_CARD)) {
                            Log.d(TAG, "更新userId");
                            userId = "WbFaceVerifyREF" + System.currentTimeMillis();
                        }
                    }
                });
            }

            @Override
            public void onLoginFailed(WbFaceError error) {
                Log.i(TAG, "onLoginFailed!");
                if (error != null) {
                    Log.d(TAG, "登录失败！domain=" + error.getDomain() + " ;code= " + error.getCode()
                            + " ;desc=" + error.getDesc() + ";reason=" + error.getReason());
                    if (error.getDomain().equals(WbFaceError.WBFaceErrorDomainParams)) {
                        Toast.makeText(cordova.getActivity(), "传入参数有误！" + error.getReason(), Toast.LENGTH_SHORT).show();
                    } else if (error.getDomain().equals(WbFaceError.WBFaceErrorDomainDevices)) {
                        Toast.makeText(cordova.getActivity(), error.getDesc(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(cordova.getActivity(), "登录刷脸sdk失败！" + error.getReason(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "sdk返回error为空！");
                }
            }
        });
    }

}
