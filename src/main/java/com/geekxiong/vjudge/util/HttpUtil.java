package com.geekxiong.vjudge.util;

import com.alibaba.fastjson.JSONObject;
import okhttp3.*;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @Description
 * @Author Geekxiong
 * @Date 2020-03-31 16:49
 */

public class HttpUtil {

    private static final byte[] LOCKER = new byte[0];
    private static HttpUtil httpUtil;
    private OkHttpClient mOkHttpClient;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static String[] USER_AGENTS = {
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36 OPR/26.0.1656.60",
            "Opera/8.0 (Windows NT 5.1; U; en)",
            "Mozilla/5.0 (Windows NT 5.1; U; en; rv:1.8.1) Gecko/20061208 Firefox/2.0.0 Opera 9.50",
            "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; en) Opera 9.50",
            "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:34.0) Gecko/20100101 Firefox/34.0",
            "Mozilla/5.0 (X11; U; Linux x86_64; zh-CN; rv:1.9.2.10) Gecko/20100922 Ubuntu/10.10 (maverick) Firefox/3.6.10",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.57.2 (KHTML, like Gecko) Version/5.1.7 Safari/534.57.2",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.71 Safari/537.36",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.64 Safari/537.11",
            "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US) AppleWebKit/534.16 (KHTML, like Gecko) Chrome/10.0.648.133 Safari/534.16",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.101 Safari/537.36",
            "Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/536.11 (KHTML, like Gecko) Chrome/20.0.1132.11 TaoBrowser/2.0 Safari/536.11",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.71 Safari/537.1 LBBROWSER",
            "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E; LBBROWSER)",
            "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; QQDownload 732; .NET4.0C; .NET4.0E; LBBROWSER)",
            "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; .NET4.0E; QQBrowser/7.0.3698.400)",
            "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; QQDownload 732; .NET4.0C; .NET4.0E)",
            "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.84 Safari/535.11 SE 2.X MetaSr 1.0",
            "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0; SV1; QQDownload 732; .NET4.0C; .NET4.0E; SE 2.X MetaSr 1.0)",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Maxthon/4.4.3.4000 Chrome/30.0.1599.101 Safari/537.36",
            "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.122 UBrowser/4.0.3214.0 Safari/537.36",
    };

    /**
     * 私有构造方法，单例模式
     */
    private HttpUtil() {
        OkHttpClient.Builder ClientBuilder = new OkHttpClient.Builder();
        ClientBuilder.readTimeout(20, TimeUnit.SECONDS);//读取超时
        ClientBuilder.connectTimeout(6, TimeUnit.SECONDS);//连接超时
        ClientBuilder.writeTimeout(60, TimeUnit.SECONDS);//写入超时
        //支持HTTPS请求，跳过证书验证
        ClientBuilder.sslSocketFactory(createSSLSocketFactory(), new TrustAllCerts());
        ClientBuilder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        mOkHttpClient = ClientBuilder.build();
    }

    /**
     * 返回单例HttpUtil
     * @return
     */
    public static HttpUtil getInstance() {
        if (httpUtil == null) {
            synchronized (LOCKER) {
                if (httpUtil == null) {
                    httpUtil = new HttpUtil();
                }
            }
        }
        return httpUtil;
    }

    public static String getRandomUA(){
        Random random = new Random();
        int i = random.nextInt(USER_AGENTS.length);
        return USER_AGENTS[i];
    }

    /**
     * 同步get请求
     * @param url
     * @param urlParams
     * @return
     */
    public Response doGet(String url, Map<String, String> headers, Map<String, String> urlParams) {
        //0 处理url参数
        String params = CommonUtil.getUrlParams(urlParams);
        //1 构造Request
        Request.Builder builder = new Request.Builder();
        Request request = builder
                .get()
                .url(url + params)
                .headers(Headers.of(headers))
                .build();

        //2 将Request封装为Call
        Call call = mOkHttpClient.newCall(request);
        //3 执行Call，得到response
        Response response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * 同步post请求
     * @param url
     * @param bodyParams
     * @return
     */
    public Response doPost(String url, Map<String, String> headers, Map<String, String> bodyParams) {
        //1构造RequestBody
        RequestBody body = setRequestBody(bodyParams);
        //2 构造Request
        Request.Builder requestBuilder = new Request.Builder();
        Request request = requestBuilder
                .post(body)
                .url(url)
                .headers(Headers.of(headers))
                .build();
        //3 将Request封装为Call
        Call call = mOkHttpClient.newCall(request);
        //4 执行Call，得到response
        Response response = null;
        try {
            response = call.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }


//    /**
//     * 同步post请求
//     * @param url
//     * @param jsonBody
//     * @return
//     */
//    public Response doPostJsonBody(String url, JSONObject jsonBody) {
//        //1构造RequestBody
//        RequestBody body = RequestBody.create(jsonBody.toJSONString(), JSON);
//        //2 构造Request
//        Request.Builder requestBuilder = new Request.Builder();
//        Request request = requestBuilder.post(body).url(url).build();
//        //3 将Request封装为Call
//        Call call = mOkHttpClient.newCall(request);
//        //4 执行Call，得到response
//        Response response = null;
//        try {
//            response = call.execute();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return response;
//    }
//
//    /**
//     * 自定义网络回调接口
//     */
//    public interface ICallback {
//        void success(Call call, Response response) throws IOException;
//        void failed(Call call, IOException e);
//    }
//
//    /**
//     * 异步get请求
//     * @param url
//     * @param urlParams
//     * @param iCallback
//     * @return
//     */
//    public void doGetAsyn(String url, Map<String, String> urlParams, final ICallback iCallback) {
//        //0 处理url参数
//        String params = CommonUtil.getUrlParams(urlParams);
//
//        //1 构造Request
//        Request.Builder builder = new Request.Builder();
//        Request request = builder.get().url(url+params).build();
//        //2 将Request封装为Call
//        Call call = mOkHttpClient.newCall(request);
//        //3 执行Call
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                iCallback.failed(call, e);
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                iCallback.success(call, response);
//
//            }
//        });
//    }
//
//    /**
//     * 异步post请求
//     * @param url
//     * @param bodyParams
//     * @param iCallback
//     */
//    public void doPostAsyn(String url, Map<String, String> bodyParams, final ICallback iCallback) {
//        //1构造RequestBody
//        RequestBody body = setRequestBody(bodyParams);
//        //2 构造Request
//        Request.Builder requestBuilder = new Request.Builder();
//        Request request = requestBuilder.post(body).url(url).build();
//        //3 将Request封装为Call
//        Call call = mOkHttpClient.newCall(request);
//        //4 执行Call
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                iCallback.failed(call, e);
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                iCallback.success(call, response);
//
//            }
//        });
//    }
//
//    /**
//     * 异步post请求
//     * @param url
//     * @param jsonBody
//     * @param iCallback
//     */
//    public void doPostJsonBodyAsyn(String url, JSONObject jsonBody, final ICallback iCallback) {
//        //1构造RequestBody
//        RequestBody body = RequestBody.create(jsonBody.toJSONString(), JSON);
//        //2 构造Request
//        Request.Builder requestBuilder = new Request.Builder();
//        Request request = requestBuilder.post(body).url(url).build();
//        //3 将Request封装为Call
//        Call call = mOkHttpClient.newCall(request);
//        //4 执行Call
//        call.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                iCallback.failed(call, e);
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                iCallback.success(call, response);
//            }
//        });
//    }




    /**
     * post的请求参数，构造RequestBody
     *
     * @param BodyParams
     * @return
     */
    private RequestBody setRequestBody(Map<String, String> BodyParams) {
        RequestBody body;
        FormBody.Builder formEncodingBuilder = new FormBody.Builder();
        if (BodyParams != null) {
            Iterator<String> iterator = BodyParams.keySet().iterator();
            String key = "";
            while (iterator.hasNext()) {
                key = iterator.next().toString();
                formEncodingBuilder.add(key, BodyParams.get(key));
                System.out.println("post http: post_Params===" + key + "====" + BodyParams.get(key));
            }
        }
        body = formEncodingBuilder.build();
        return body;
    }




    /**
     * 生成安全套接字工厂，用于https请求的证书跳过
     * @return
     */
    private SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }
        return ssfFactory;
    }


    /**
     * 用于信任所有证书
     */
    private class TrustAllCerts implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }
}
