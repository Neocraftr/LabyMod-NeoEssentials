package me.dominic.neoessentials.reflect;

import java.lang.reflect.Field;

class DefaultFieldAccessor implements FieldAccessor {

	protected Field field;

	DefaultFieldAccessor(Field field) {
		this.field = field;
	}

	@Override
	public <T> T get(Object instance) {
		try {
			return (T) field.get(instance);
		} catch (Exception ex) {
			throw new RuntimeException(String.format("Error while reading field '%s.%s'", instance, field), ex);
		}
	}

	@Override
	public void set(Object instance, Object value) {
		try {
			field.set(instance, value);
		} catch (Exception ex) {
			throw new RuntimeException(String.format("Error while setting field '%s.%s' to '%s'", instance, field, value), ex);
		}
	}

	public Field getField() {
		return field;
	}


}
