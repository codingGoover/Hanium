package com.example.hanium;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class CarRegisterRequest extends StringRequest {
    // 서버 URL설정 (PHP 파일 연동)
    final static private  String URL = "http://auddms.ivyro.net/CarRegister.php";
    private Map<String,String> map;

    public CarRegisterRequest(String userID, String carNum, int parkingState,Response.Listener<String> listener){
        super(Method.POST,URL,listener, null);

        map = new HashMap<>();
        map.put("userID",userID);
        map.put("carNum",carNum);
        map.put("parkingState",parkingState+"");

    }
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}
