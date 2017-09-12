package com.yingtaohuo.wxevent;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.naming.NoNameCoder;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDomDriver;

import java.io.Writer;
import java.lang.reflect.Field;

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/3/2
 * 微信: yin80871901
 * 参见: https://my.oschina.net/jarvan4dev/blog/649555
 */
public class CDATADomDriver extends XppDomDriver {

	private NoNameCoder nameCoder;

	public CDATADomDriver() {
		this.nameCoder = new NoNameCoder();
	}

	@Override
	public HierarchicalStreamWriter createWriter(Writer out) {
		//输出格式化的xml字符串
		return new PrettyPrintWriter(out, nameCoder) {
			//输出压缩的xml字符串，删除多余的空白符
			//return new CompactWriter (out, nameCoder) {
			boolean cdata = false;
			Class<?> targetClass = null;

			@Override
			public void startNode(String name, Class clazz) {
				super.startNode(name, clazz);
				if (targetClass == null) {
					targetClass = clazz;
				}
				cdata = needCDATA(targetClass, name);
			}

			@Override
			protected void writeText(QuickWriter writer, String text) {
				if (cdata) {
					writer.write("<![CDATA[");
					writer.write(text);
					writer.write("]]>");
				} else {
					writer.write(text);
				}
			}
		};
	}

	private static boolean needCDATA(Class<?> targetClass, String fieldAlias){
		boolean cdata = false;
		//first, scan self
		cdata = existsCDATA(targetClass, fieldAlias);
		if(cdata) return cdata;
		//if cdata is false, scan supperClass until java.lang.Object
		Class<?> superClass = targetClass.getSuperclass();
		while(!superClass.equals(Object.class)){
			cdata = existsCDATA(superClass, fieldAlias);
			if(cdata) return cdata;
			superClass = superClass.getSuperclass();
		}
		return false;
	}

	private static boolean existsCDATA(Class<?> clazz, String fieldAlias){
		//scan fields
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			//1. exists XStreamCDATA
			if(field.getAnnotation(XStreamAlias.class) != null ){
				XStreamAlias xStreamAlias = field.getAnnotation(XStreamAlias.class);
				//2. exists XStreamAlias
				if(null != xStreamAlias){
					if(fieldAlias.equals(xStreamAlias.value())) // matched
						return true;
				}else{// not exists XStreamAlias
					if(fieldAlias.equals(field.getName()))
						return true;
				}
			}
		}
		return false;
	}
}
