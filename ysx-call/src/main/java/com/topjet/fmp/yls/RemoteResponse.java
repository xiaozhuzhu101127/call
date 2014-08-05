package com.topjet.fmp.yls;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JsonConfig;

import org.apache.commons.lang.ArrayUtils;

import com.topjet.fmp.util.JSONUtil;

public class RemoteResponse implements Serializable {

	private static final long serialVersionUID = -2090469464672663492L;

	/**
	 * 请求处理是否成功。1为成功，0为失败。默认为1。
	 */
	private int succ = 1;

	/**
	 * 返回的消息。
	 */
	private String msg;
	
	private String id;

	/**
	 * 请求返回的结果
	 */
	private Object result;

	public int getSucc() {
		return succ;
	}

	public void setSucc(int succ) {
		this.succ = succ;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	/**
	 * 将此对象转换成json格式的字符串
	 * 
	 * @see #toJSON(JsonConfig)
	 * @see #toJSON(String[])
	 * 
	 * @return
	 */
	public String toJSON() {
		return JSONUtil.toJSON(this);
	}

	/**
	 * 将此对象转换成json格式的字符串
	 * 
	 * @see #toJSON()
	 * @see #toJSON(JsonConfig)
	 * 
	 * @param excludes
	 *            排除掉的字段
	 * @return
	 */
	public String toJSON(String[] excludes) {
		return JSONUtil.toJSON(excludes);
	}

	public String toJSON(final String[] excludes, final String datePattern) {
		return JSONUtil.toJSON(this, excludes, datePattern);
	}

	public String toJSON(final String datePattern) {
		return JSONUtil.toJSON(this, datePattern);
	}

	/**
	 * 通过指定某个class的字段返回所有被排除的字段，用于转换JSON对象时排除某些不需要的字段。
	 * 
	 * @param clazz
	 * @param includeFields
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static List<String> convertToExcludeFields(Class clazz, String[] includeFields) {

		List<String> exclude = new ArrayList<String>();

		Field[] fields = clazz.getDeclaredFields();

		for (Field field : fields) {

			if (!ArrayUtils.contains(includeFields, field.getName())) {
				exclude.add(field.getName());
			}
		}

		return exclude;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	
}
