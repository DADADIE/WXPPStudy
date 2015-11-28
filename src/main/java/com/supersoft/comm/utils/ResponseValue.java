package com.supersoft.comm.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import java.util.Map;

public class ResponseValue {
	//成功=0，失败=非0
	private int status;
	//成功的信息
	private String info;
	//数据参数
	private JSONObject dataParams;
	//失败的信息
	private String errMsg = "";
	//返回的数据
	private Object data = null;
	//标识
	private Boolean flag = null;
	
	//SOA重试标识
	private Boolean soaRetry = false;

	public ResponseValue(){
		this.status = ResultStatus.SUCCESS_STATUS;
		this.info = ResultInfo.SUCCESS_INFO;
		this.dataParams = new JSONObject();
		this.errMsg = "";
		this.data = null;
		this.flag = null;
		this.soaRetry = false;
	}
	
	/**
	 * 返回错误状态信息
	 */
	protected final static int RETURN_ERROR_INFO = 1;

	public Boolean getSoaRetry() {
		return soaRetry;
	}

	public void setSoaRetry(Boolean soaRetry) {
		this.soaRetry = soaRetry;
	}
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public void setData(Object data) {
		this.data = data;
	}

	
	public Object getData() {
		return data;
	}

	public Boolean getFlag() {
		return flag;
	}

	public void setFlag(Boolean flag) {
		this.flag = flag;
	}

	public void setDataParams(String key, Object value) {
		dataParams.put(key, value);
	}

	public void setDataParams(Map<? extends String, ? extends Object> map) {
		dataParams.putAll(map);
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		if(errMsg != null && errMsg.length() > 0){
			this.status = ResultStatus.FAILED_STATUS;
		}
		this.errMsg = errMsg;
	}

	public String getDataString() {
		if(data!=null){
			return data.toString();
		}
		return null;
	}

	private Object judgeData() {
		if(data == null) {
			if(dataParams.size() == 0) {
				data = "";
				if (flag != null) {
					if (!flag) {
						this.status = ResultStatus.FAILED_STATUS;
						if (RETURN_ERROR_INFO == ConstantsConfig.getConstants().getReturnErrorInfoStatus()) {
							this.info = (info == null || info.equals(ResultInfo.SUCCESS_INFO)) ? ResultInfo.INSERT_UPDATE_FAILED_INFO : info;
						} 
					}
				} else {
					if (status == ResultStatus.SUCCESS_STATUS) {
						this.status = ResultStatus.NO_VALUE_STATUS;
						this.info = ResultInfo.NO_VALUE_INFO;
					} else {
						if (RETURN_ERROR_INFO == ConstantsConfig.getConstants().getReturnErrorInfoStatus()) {
							this.info = info == null ? ResultInfo.SELECT_FAILED_INFO : info;
						}
					}
				}
			} else if (dataParams.size() > 0) {
				data = dataParams;
			}
		}
		return data;
	}

	@Override
	public String toString() {
		JSONObject resultValue = new JSONObject();
		if (judgeData() != null && !data.equals("")) {
			resultValue.put("data", data);
		}
		resultValue.put("soaRetry", this.soaRetry);
		resultValue.put("status", status);
		resultValue.put("info", info);
		resultValue.put("flag", flag);
		
		if (ResponseValue.RETURN_ERROR_INFO == ConstantsConfig.getConstants().getReturnErrorInfoStatus()) {
			resultValue.put("errMsg", errMsg);
		}
		if (log.isDebugEnabled()) {
			log.debug("返回值：" + resultValue.toString());
		}
		//return resultValue.toString();
		return JSON.toJSONString(resultValue, SerializerFeature.DisableCircularReferenceDetect);
	}
}
