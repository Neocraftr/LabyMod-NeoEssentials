/*
 * Copyright (c) 2017 Jofkos. All rights reserved.
 */

package me.dominic.neoessentials.reflect;

import java.lang.reflect.Constructor;

public class DefaultConstructorAccessor<T> implements ConstructorAccessor<T> {
	
	private Constructor<T> constructor;
	
	DefaultConstructorAccessor(Constructor<T> constructor) {
		this.constructor = constructor;
	}
	
	@Override
	public T newInstance(Object... args) {
		try {
			return constructor.newInstance(args);
		} catch (Exception ex) {
			throw new RuntimeException(String.format("Error while instantiating '%s'", constructor), ex);
		}
	}
	
	@Override
	public Constructor<T> getConstructor() {
		return constructor;
	}
}
