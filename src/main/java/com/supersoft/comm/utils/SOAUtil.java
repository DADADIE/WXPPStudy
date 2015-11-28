package com.supersoft.comm.utils;

import com.alibaba.fastjson.JSONObject;
import com.supersoft.comm.config.ConfigHelper;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

public class SOAUtil {

	private static final String WX_CENTER = ConfigHelper.getProperty("WX_CENTER");
	private static final String WX_APPID = ConfigHelper.getProperty("WX_APPID");
	private static final String WX_SECRET = ConfigHelper.getProperty("WX_SECRET");
	private static final String WX_CENTER_GET_TOKEN_URL = String.format(
			WX_CENTER + "token?grant_type=client_credential&appid=%s&secret=%s",
			WX_APPID, WX_SECRET);
	private static ConcurrentHashMap<String, AccessToken> accessTokenMap = new ConcurrentHashMap<String, AccessToken>();
	
	private static AccessToken getSOAAccessToken()
			throws Exception {
		AccessToken accessToken = accessTokenMap.get("AccessToken");
		if (accessToken == null || accessToken.isExpired()) {
			String reuslt = HttpUtil.getResult(WX_CENTER_GET_TOKEN_URL);
			//endTime_Token
			Date endTime_Token=new Date();
			SimpleDateFormat timeFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS"); 
			logStr.append(timeFormat.format(endTime_Token));
			
			ResponseValue rv = JSONObject.parseObject(reuslt,
					ResponseValue.class);
			if (rv.getStatus() == REQUEST_OK) {
				accessToken = JSONObject.parseObject(rv.getDataString(),
						AccessToken.class);
				accessTokenMap.put(appCode, accessToken);
			} else {
				throw new RuntimeException(rv.getInfo());
			}
		}
		return accessToken;
	}
	/**
	 * 根据appCode获取url
	 * @param appCode
	 * @return
	 */
	public static AccessToken getAccessTokenByAppCode(String appCode){
		StringBuffer logStr = new StringBuffer();
		SimpleDateFormat timeFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS"); 
		//startTime_SOA
		Date startTime_SOA=new Date();
		logStr.append(timeFormat.format(startTime_SOA) + ",");
		String url = "";
		AccessToken accessToken = new AccessToken();
		try {
			accessToken =  getSOAAccessToken(appCode, startTime_SOA,logStr);
			url=accessToken.getProviderUrl();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return accessToken;
	}
	

    private static AccessToken getSOAAccessToken(String appCode,
                                                 JSONObject soaParams) throws Exception {
        AccessToken accessToken = accessTokenMap.get(appCode);
        if (accessToken == null || accessToken.isExpired()) {
            String url = soaParams.getString("SOA_CENTER")
                    + "accessToken/get?consumerAppCode="
                    + soaParams.getString("SOA_APP_CODE") + "&secret="
                    + soaParams.getString("SOA_SECRET") + "&providerAppCode="
                    + appCode;
            String reuslt = HttpUtil.getResult(url);
            ResponseValue rv = JSONObject.parseObject(reuslt,
                    ResponseValue.class);
            if (rv.getStatus() == REQUEST_OK) {
                accessToken = JSONObject.parseObject(rv.getDataString(),
                        AccessToken.class);
                accessTokenMap.put(appCode, accessToken);
            } else {
                throw new RuntimeException(rv.getInfo());
            }
        }
        return accessToken;
    }

	@Deprecated
	public static JSONObject getFromSOA(String appCode, String method,
			JSONObject params) {
		StringBuffer logStr = new StringBuffer();
		SimpleDateFormat timeFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS"); 
		//startTime_SOA
		Date startTime_SOA=new Date();
		logStr.append(timeFormat.format(startTime_SOA) + ",");
		
		JSONObject ret;
		try {
			int status = REQUEST_OK;
			int callCount = 3;
			do {
				AccessToken accessToken = getSOAAccessToken(appCode, startTime_SOA,logStr);
				params.put("accessToken", accessToken.getAccessToken());
				String url = accessToken.getProviderUrl() + method;
				String str = HttpUtil.getResult(url, params);
				//endTime_SOA
				Date endTime_SOA=new Date();
				
				ret = JSONObject.parseObject(str);
				status = (Integer) ret.get("status");
				long cost = endTime_SOA.getTime() - startTime_SOA.getTime();
				
				logStr.append("," + timeFormat.format(endTime_SOA) + "," + cost + "," + appCode + "," + method + "," + status + "\n");
				log.info(logStr);
				
				log.info("soa result : "+str);
				
				callCount--;
				if (status == -1) {
					accessTokenMap.remove(appCode);
				}
			} while (status == -1 && callCount > 0);
		} catch (Exception e) {
			e.printStackTrace();
			ret = new JSONObject();
			ret.put("status", REQUEST_FAILED);
			ret.put("info", e.getMessage());
		}
		return ret;
	}

	@Deprecated
	public static JSONObject getFromSOA(String appCode, String method) {
		return getFromSOA(appCode, method, new JSONObject());
	}

	public static ResponseValue checkAccessToken(String accessToken) {
		ResponseValue rv = new ResponseValue();
		String url = String.format(SOA_CENTER_TOKEN_CHECK_URL, accessToken);
		try {
			
			String ret = HttpUtil.getResult(url);
			JSONObject data = JSONObject.parseObject(ret);
			Integer status = getValue(data, "status", 0);
			String info = getValue(data, "info", "");
			if (status <= 0) {
				rv.setInfo(info);
				rv.setStatus(status);
				return rv;
			}
		} catch (Exception e) {
			e.printStackTrace();
			rv.setInfo(e.getMessage()); 
			return rv;
		}
		rv.setDataParams("status", REQUEST_OK);
		rv.setDataParams("info", "ok");
		return rv;
	}

	public static ResponseValue getResValFromSOA(String appCode, String method,
			JSONObject params) {
		
		StringBuffer logStr = new StringBuffer();
		SimpleDateFormat timeFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS"); 
		//startTime_SOA
		Date startTime_SOA=new Date();
		logStr.append(timeFormat.format(startTime_SOA) + ",");
		
		ResponseValue rv = null;
		try {
			int status = REQUEST_OK;
			boolean retry = true;
			int callCount = 3;
			do {
				AccessToken accessToken = getSOAAccessToken(appCode, startTime_SOA,logStr);
				params.put(Const.ACCESS_TOKEN, accessToken.getAccessToken());
				String url = accessToken.getProviderUrl() + method;
				String reuslt = HttpUtil.getResult(url, params);
				//endTime_SOA
				Date endTime_SOA=new Date();
				
				JSONObject ret;
				ret = JSONObject.parseObject(reuslt);
				status = (Integer) ret.get("status");
				long cost = endTime_SOA.getTime() - startTime_SOA.getTime();
				
				logStr.append("," + timeFormat.format(endTime_SOA) + "," + cost + "," + appCode + "," + method + "," + status + "\n");
				log.info(logStr);
				
				rv = JSONObject.parseObject(reuslt, ResponseValue.class);
				retry = rv.getSoaRetry();
				callCount--;
				if (retry) {
					accessTokenMap.remove(appCode);
				}
			} while (retry && callCount > 0);
		} catch (Exception e) {
			e.printStackTrace();
			rv = new ResponseValue();
			rv.setStatus(REQUEST_FAILED);
			rv.setInfo(e.getMessage());
		}
		return rv;
	}

    public static ResponseValue getResValFromSOA(String appCode, String method,
                                                 JSONObject params, JSONObject soaParams) {
        if (soaParams == null) {
            return getResValFromSOA(appCode, method, params);
        } else {
            ResponseValue rv = null;
            try {
                boolean retry = true;
                int callCount = 3;
                do {
                    AccessToken accessToken = getSOAAccessToken(appCode,soaParams);
                    params.put(Const.ACCESS_TOKEN, accessToken.getAccessToken());
                    String url = accessToken.getProviderUrl() + method;
					long startTime = System.currentTimeMillis();
                    String reuslt = HttpUtil.getResult(url, params);
					long endTime = System.currentTimeMillis();
					log.debug("------SOA请求到返回耗时:--------" + (endTime - startTime) + "--------------------");
                    log.info("");
                    log.info("url = "+url+", params = "+params.toJSONString());
                    log.info("reuslt = "+reuslt);
                    rv = JSONObject.parseObject(reuslt, ResponseValue.class);
                    retry = rv.getSoaRetry();
                    callCount--;
                    if (retry) {
                        accessTokenMap.remove(appCode);
                    }
                } while (retry && callCount > 0);
            } catch (Exception e) {
                e.printStackTrace();
                rv = new ResponseValue();
                rv.setStatus(REQUEST_FAILED);
                rv.setInfo(e.getMessage());
            }
            return rv;
        }

    }

	public static ResponseValue getResValFromSOA(String appCode, String method) {
		return getResValFromSOA(appCode, method, new JSONObject());
	}

	@SuppressWarnings("unchecked")
	public static <T extends Object> T getValue(JSONObject map, String name,
			T defaultValue) {
		if (map == null) {
			return defaultValue;
		}
		Object obj = map.get(name);
		if (obj == null) {
			if (defaultValue == null) {
				return null;
			} else {
				obj = defaultValue;
			}
		}
		return (T) obj;
	}

}
