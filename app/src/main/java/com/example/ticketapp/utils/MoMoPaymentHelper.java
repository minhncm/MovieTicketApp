package com.example.ticketapp.utils;

import android.util.Log;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MoMoPaymentHelper {
    
    private static final String TAG = "MoMoPaymentHelper";
    
    // MoMo Sandbox Test Credentials
    private static final String PARTNER_CODE = "MOMOBKUN20180529";
    private static final String ACCESS_KEY = "klm05TvNBzhg7h7j";
    private static final String SECRET_KEY = "at67qH6mk8w5Y1nAyMoYKMWACiEi2bsa";
    private static final String ENDPOINT = "https://test-payment.momo.vn/v2/gateway/api/create";
    
    // Redirect URLs
    private static final String REDIRECT_URL = "https://webhook.site/b3088a6a-2d17-4f8d-a383-71389a6c600b";
    private static final String IPN_URL = "https://webhook.site/b3088a6a-2d17-4f8d-a383-71389a6c600b";
    
    public interface MoMoPaymentCallback {
        void onSuccess(String payUrl, String orderId);
        void onError(String errorMessage);
    }
    
    /**
     * Tạo request thanh toán MoMo
     * @param amount Số tiền thanh toán (VND)
     * @param orderInfo Thông tin đơn hàng
     * @param callback Callback xử lý kết quả
     */
    public static void createPayment(long amount, String orderInfo, MoMoPaymentCallback callback) {
        new Thread(() -> {
            try {
                // 1. Tạo các tham số
                String orderId = "TICKET_" + System.currentTimeMillis();
                String requestId = UUID.randomUUID().toString();
                String extraData = "";
                String requestType = "payWithATM"; // hoặc "captureWallet"
                
                // 2. Tạo raw signature
                String rawSignature = "accessKey=" + ACCESS_KEY +
                        "&amount=" + amount +
                        "&extraData=" + extraData +
                        "&ipnUrl=" + IPN_URL +
                        "&orderId=" + orderId +
                        "&orderInfo=" + orderInfo +
                        "&partnerCode=" + PARTNER_CODE +
                        "&redirectUrl=" + REDIRECT_URL +
                        "&requestId=" + requestId +
                        "&requestType=" + requestType;
                
                Log.d(TAG, "Raw signature: " + rawSignature);
                
                // 3. Tạo signature bằng HMAC SHA256
                String signature = hmacSHA256(rawSignature, SECRET_KEY);
                Log.d(TAG, "Signature: " + signature);
                
                // 4. Tạo JSON request body
                JSONObject jsonBody = new JSONObject();
                jsonBody.put("partnerCode", PARTNER_CODE);
                jsonBody.put("partnerName", "Test");
                jsonBody.put("storeId", "MomoTestStore");
                jsonBody.put("requestId", requestId);
                jsonBody.put("amount", amount);
                jsonBody.put("orderId", orderId);
                jsonBody.put("orderInfo", orderInfo);
                jsonBody.put("redirectUrl", REDIRECT_URL);
                jsonBody.put("ipnUrl", IPN_URL);
                jsonBody.put("lang", "vi");
                jsonBody.put("extraData", extraData);
                jsonBody.put("requestType", requestType);
                jsonBody.put("signature", signature);
                
                Log.d(TAG, "Request body: " + jsonBody.toString());
                
                // 5. Gửi request đến MoMo
                OkHttpClient client = new OkHttpClient();
                MediaType JSON = MediaType.get("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
                
                Request request = new Request.Builder()
                        .url(ENDPOINT)
                        .post(body)
                        .build();
                
                Response response = client.newCall(request).execute();
                String responseBody = response.body().string();
                
                Log.d(TAG, "Response: " + responseBody);
                
                // 6. Parse response
                JSONObject jsonResponse = new JSONObject(responseBody);
                int resultCode = jsonResponse.getInt("resultCode");
                
                if (resultCode == 0) {
                    // Thành công
                    String payUrl = jsonResponse.getString("payUrl");
                    callback.onSuccess(payUrl, orderId);
                } else {
                    // Lỗi
                    String message = jsonResponse.optString("message", "Unknown error");
                    callback.onError("MoMo Error: " + message);
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Error creating MoMo payment", e);
                callback.onError("Error: " + e.getMessage());
            }
        }).start();
    }
    
    /**
     * Tạo HMAC SHA256 signature
     */
    private static String hmacSHA256(String data, String key) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        
        byte[] hash = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
        
        // Convert byte array to hex string
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        
        return hexString.toString();
    }
}
