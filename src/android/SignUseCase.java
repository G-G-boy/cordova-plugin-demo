package org.apache.cordova.facerecognition;

import android.text.TextUtils;
import android.util.Log;

import com.webank.mbank.wehttp.WeLog;
import com.webank.mbank.wehttp.WeOkHttp;
import com.webank.mbank.wehttp.WeReq;

import java.io.IOException;
import java.io.Serializable;


/**
 * Created by leoraylei on 16/9/19.
 */

public class SignUseCase {
    private static final String TAG = "SignUseCase";

    public static final int ERROR_DATA = -100;

    public String SIGN_DATA = "";

    private FaceRecognition app;

    public SignUseCase(FaceRecognition app) {
        this.app = app;
        initHttp();
    }

    private WeOkHttp myOkHttp = new WeOkHttp();

    private void initHttp() {
        //拿到OkHttp的配置对象进行配置
        //WeHttp封装的配置
        myOkHttp.config()
                //配置超时,单位:s
                .timeout(20, 20, 20)
                        //添加PIN
                .log(WeLog.Level.BODY);
    }

    public void execute(final String mode, String appId, String userId, String nonce) {
        final String url = getUrl(appId, userId, nonce);

        requestExec(url, new WeReq.WeCallback<SignResponse>() {
            @Override
            public void onStart(WeReq weReq) {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onFailed(WeReq weReq, int i, int i1, String s, IOException e) {
                requestError(i1, s);
            }

            @Override
            public void onSuccess(WeReq weReq, SignResponse signResponse) {
                if (signResponse != null) {
                    String sign = signResponse.sign;
                    processBody(mode, sign);
                    SIGN_DATA = sign;
                    Log.e("---+++++++++-",SIGN_DATA);
                    app.openCloudFaceService(sign);
                } else {
                    requestError(ERROR_DATA, "get response is null");
                }
            }
        });
    }

    private void requestError(int code, String message) {
        Log.d(TAG, "签名请求失败:code=" + code + ",message=" + message);
    }

    private void processBody(String mode, String sign) {
        if (TextUtils.isEmpty(sign) || "null".equals(sign)) {

        } else {
            Log.d(TAG, "签名请求成功:" + sign);
        }
    }

    public static class SignResponse implements Serializable {
        public String sign;     //签名
    }

    public void requestExec(String url, WeReq.WeCallback<SignResponse> callback) {
        myOkHttp.<SignResponse>get(url).execute(SignResponse.class, callback);
    }

    private String getUrl(String appId, String userId, String nonce) {
        //特别注意：此方法仅供demo使用，合作方开发时需要自己的后台提供接口生成签名
        final String s = "https://ida.webank.com/" + "/ems-partner/cert/signature?appid=" + appId + "&nonce=" + nonce + "&userid=" + userId;
        Log.d(TAG, "get sign url=" + s);
        return s;
    }


}
