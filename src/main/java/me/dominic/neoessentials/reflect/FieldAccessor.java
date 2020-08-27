package me.dominic.neoessentials.reflect;

import java.lang.reflect.Field;

public interface FieldAccessor {

	<T> T get(Object instance);
	default <T> T getStatic() {
		return get(null);
	}

	void set(Object instance, Object value);
	default void setStatic(Object value) {
		set(null, value);
	}

	Field getField();

}
