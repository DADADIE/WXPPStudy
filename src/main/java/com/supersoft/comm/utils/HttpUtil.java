package com.supersoft.comm.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class HttpUtil {
	public static final int METHOD_GET = 1;
	public static final int METHOD_POST = 2;
	public static final int METHOD_PUT = 3;
	public static final int METHOD_DELETE = 4;

	public static String getResult(String url, JSONObject params, boolean post) {
		try {
			StringBuffer sb = null;
			HttpURLConnection conn = null;
			URL u = new URL(url);
			conn = (HttpURLConnection) u.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setInstanceFollowRedirects(true);
			conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			if (post) {
				conn.setRequestMethod("POST");
			} else {
				conn.setRequestMethod("GET");
			}
			BufferedOutputStream hurlBufOus = null;
			hurlBufOus = new BufferedOutputStream(conn.getOutputStream());

			if (params != null && params.size() > 0) {
				StringBuilder content = new StringBuilder();
				for (String key : params.keySet()) {
					String value = params.getString(key);
					if (value != null && value.length() > 0) {
						content.append("&");
						content.append(key);
						content.append("=");
						content.append(URLEncoder.encode(params.getString(key), "utf-8"));
					}
				}
				hurlBufOus.write(content.toString().getBytes());
			}
			hurlBufOus.flush();
			hurlBufOus.close();
			conn.connect();
			BufferedReader br = new BufferedReader(
					new InputStreamReader(conn.getInputStream(),"utf-8"));
			sb = new StringBuffer("");
			String tmp = "";
			while ((tmp = br.readLine()) != null) {
				sb.append(tmp);
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getResult(String url) {
		return getResult(url, null, false);
	}

	public static ResponseValue getResvResult(String url, JSONObject params) {
		return JSONObject.parseObject(getResult(url, params, false),
				ResponseValue.class);
	}

	public static String getResult(String url, JSONObject params) {
		return getResult(url, params, true);
	}

	public static String getResult(String url, boolean post) {
		return getResult(url, true);
	}

	/**
	 * 对Http方法请求做基本的封装
	 *
	 * @param url
	 * @param methodType
	 * @return
	 * @throws Exception
	 */
	public static String request(String url, int methodType) throws Exception {
		HttpRequestBase method = null;
		switch (methodType) {
		case HttpUtil.METHOD_GET:
			method = new HttpGet(url);
			break;
		case HttpUtil.METHOD_POST:
			method = new HttpPost(url);
			break;
		case HttpUtil.METHOD_PUT:
			method = new HttpPut(url);
			break;
		case HttpUtil.METHOD_DELETE:
			method = new HttpDelete(url);
			break;
		default:
			method = new HttpGet(url);
			break;
		}

		boolean isSSL = url.startsWith("https");
		DefaultHttpClient client = getHttpClient(isSSL);
		HttpResponse response = client.execute(method);
		String ret = "";
		int statusCode = response.getStatusLine().getStatusCode();
		HttpEntity entity = response.getEntity();
		ret = EntityUtils.toString(entity, "utf-8");
		if (statusCode != HttpStatus.SC_OK) {
			log.info("httpclient response ret:" + ret);
			throw new Exception(
					"something wrong happened when you request http ,statusCode:"
							+ statusCode);
		}
		client.getConnectionManager().shutdown();
		return ret;
	}

	public static DefaultHttpClient getHttpClient(boolean isSSL) {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		// HttpHost proxy = new HttpHost("192.168.1.147", 8080);
		// httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
		// proxy);

		if (!isSSL) {
			return httpClient;
		}

		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("http", 80, PlainSocketFactory
				.getSocketFactory()));
		try {
			// NOTE: here we trust all ssl certification
			SSLContext sslContext = SSLContext.getInstance("TLS");
			// set up a TrustManager that trusts everything
			sslContext.init(null,
					new TrustManager[] { new TrustAllX509TrustManager() {
					} }, null);
			SSLSocketFactory sf = new TrustAllSSLSocketFactory(sslContext);
			registry.register(new Scheme("https", 443, sf));
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}

		PoolingClientConnectionManager connManager = new PoolingClientConnectionManager(
				registry);
		httpClient = new DefaultHttpClient(connManager);
		return httpClient;
	}

	/**
	 * 通用的Http Post json data
	 *
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static String requestPostJSON(String url, Map<String, Object> param)
			throws Exception {
		HttpClient client = new DefaultHttpClient();
		// 1. 组装URL
		HttpPost post = new HttpPost(url);
		post.setHeader("Content-type", "application/json");

		// 2. 添加参数
		HttpEntity stringEntity = new StringEntity(
				JSONObject.toJSONString(param), "utf-8");

		post.setEntity(stringEntity);
		// 3. 执行Http请求
		HttpResponse response = client.execute(post);
		// 4. 获取Http请求返回的实体
		HttpEntity entity = response.getEntity();
		// 5. 获取实体实际获取的对象：String
		String str = EntityUtils.toString(entity, "utf-8");
		client.getConnectionManager().shutdown();
		return str;
	}
	
	/**
     * 通用的Http Post请求方式
     * @param url
     * @return
     * @throws Exception
     */
    public static String requestPostFile(String url, Map<String, Object> param,String fileName, File file) throws Exception {
        boolean isSSL = url.startsWith("https");
        HttpClient client = getHttpClient(isSSL);
        //1. 组装URL
        HttpPost post = new HttpPost(url);
        MultipartEntity reqEntity = new MultipartEntity();
        //创建待处理的表单域内容文本
        for(String key: param.keySet()){
            reqEntity.addPart(key,new StringBody(param.get(key).toString(), Charset.forName("utf-8")));
        }
        //创建待处理的文件
        FileBody fileBody = new FileBody(file);
        //对请求的表单域进行填充
        reqEntity.addPart(fileName,fileBody);

        //设置请求
        post.setEntity(reqEntity);

        //3. 执行Http请求
        HttpResponse response = client.execute(post);
        //4. 获取Http请求返回的实体
        HttpEntity entity = response.getEntity();
        //5. 获取实体实际获取的对象：String
        String str = EntityUtils.toString(entity, "utf-8");
        client.getConnectionManager().shutdown();
        return str;
    }
    
    /**
     * 通用的Http Post请求方式
     * @param url
     * @return
     * @throws Exception
     */
    public static String requestPostFile(String url, Map<String, Object> param,String fileName, URL u) throws Exception {
        boolean isSSL = url.startsWith("https");
        HttpClient client = getHttpClient(isSSL);
        //1. 组装URL
        HttpPost post = new HttpPost(url);
        MultipartEntity reqEntity = new MultipartEntity();
        //创建待处理的表单域内容文本
        for(String key: param.keySet()){
            reqEntity.addPart(key,new StringBody(param.get(key).toString(), Charset.forName("utf-8")));
        }
        //创建待处理的文件
        URLConnection uc = u.openConnection();
        InputStreamBody inputStreamBody = new InputStreamBody(uc.getInputStream(), fileName);
        //对请求的表单域进行填充
        reqEntity.addPart(fileName,inputStreamBody);

        //设置请求
        post.setEntity(reqEntity);

        //3. 执行Http请求
        HttpResponse response = client.execute(post);
        //4. 获取Http请求返回的实体
        HttpEntity entity = response.getEntity();
        //5. 获取实体实际获取的对象：String
        String str = EntityUtils.toString(entity, "utf-8");
        client.getConnectionManager().shutdown();
        return str;
    }
    
    public static String doHttPostIO(String url, String param) {
		PrintWriter out = null;
	    BufferedReader in = null;
	    String result = "";

		try {
			URL realUrl = new URL(url);
	        // 打开和URL之间的连接
			HttpURLConnection conn = (HttpURLConnection )realUrl.openConnection();
	        // 发送POST请求必须设置如下两行
			conn.setRequestMethod("POST");
	        conn.setDoOutput(true);
	        conn.setDoInput(true);
	        
	        // 获取URLConnection对象对应的输出流
	        out = new PrintWriter(conn.getOutputStream());
	        // 发送请求参数
	        out.print(new String(param.getBytes(), "utf-8"));
	        // flush输出流的缓冲
	        out.flush();
	        // 定义BufferedReader输入流来读取URL的响应
	        in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
	        String line;
	        while ((line = in.readLine()) != null) {
	            result += line;
	        }
	    } catch (Exception e) {
	        System.out.println("发送 POST 请求出现异常！"+e);
	        e.printStackTrace();
	    }
	    //使用finally块来关闭输出流、输入流
	    finally{
	        try{
	            if(out!=null){
	                out.close();
	            }
	            if(in!=null){
	                in.close();
	            }
	        }
	        catch(IOException ex){
	            ex.printStackTrace();
	        }
	    }
		return result;
	}
    
    public static void main(String[] args) throws Exception{
    	String accessToken = "ZLTlsXL5RAiviexn3wbSZcjFzg40HtjC9xVkD3IqKhf_8k3Msf5SFXnIgmz-DLztFZOGlFp303XalimfFJSUcKTuueQp5Ye-NVA3PFqLOoHFpgrChhorhBHL9YPS3l39";
		String url = String.format("https://api.weixin.qq.com/cgi-bin/media/uploadimg?access_token=%s", accessToken);
		Map<String, Object> params = new HashMap<String, Object>();
		//new URI("http://weipos.oss-cn-hangzhou.aliyuncs.com/e69a48c0b58ffc7fb9ecadcf614004ae.jpg")
		URL u = new URL("http://weipos.oss-cn-hangzhou.aliyuncs.com/e69a48c0b58ffc7fb9ecadcf614004ae.jpg");
		String resp = requestPostFile(url, params, "e69a48c0b58ffc7fb9ecadcf614004ae.jpg", u);

		System.out.println(resp);
    }
}
