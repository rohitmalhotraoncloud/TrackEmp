package com.os.app.utils;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;

public class NullAwareBeanUtil extends BeanUtilsBean {

	static NullAwareBeanUtil instance = new NullAwareBeanUtil();

	public static NullAwareBeanUtil getInstance() {
		return instance;
	}

	private UpdateablePropertyUtilsBean propertyUtilsBean = new UpdateablePropertyUtilsBean();

	@Override
	public PropertyUtilsBean getPropertyUtils() {
		return propertyUtilsBean;
	}

	@Override
	public void copyProperty(Object dest, String name, Object value)
			throws IllegalAccessException, InvocationTargetException {
		if (value == null) {
			return;
		}
		super.copyProperty(dest, name, value);
	}
}
