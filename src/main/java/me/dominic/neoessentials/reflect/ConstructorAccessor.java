/*
 * Copyright (c) 2017 Jofkos. All rights reserved.
 */

package me.dominic.neoessentials.reflect;

import java.lang.reflect.Constructor;

public interface ConstructorAccessor<T> {
	
	T newInstance(Object... args);
	Constructor<T> getConstructor();

}
