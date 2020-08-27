package me.dominic.neoessentials.reflect;

import com.google.common.base.Joiner;

import java.lang.reflect.*;
import java.util.Arrays;

/**
 * @author Jofkos
 */
public class Reflect {

	private static final FieldAccessor modifiers = getField(Field.class, "modifiers");

	public static FieldAccessor getField(Field field) {
		return new DefaultFieldAccessor(setAccessible(field));
	}
	
	public static FieldAccessor getField(Class<?> clazz, String fieldName) {
		Field field;
		try {
			try {
				field = clazz.getDeclaredField(fieldName);
			} catch (NoSuchFieldException e) {
				field = clazz.getField(fieldName);
			}
		} catch (NoSuchFieldException e) {
			try {
				return getField(clazz.getSuperclass(), fieldName);
			} catch (Exception ignored) {}
			
			throw new RuntimeException("No field '" + fieldName + "' found in '" + clazz.getCanonicalName() + "'", e);
		}
		
		return new DefaultFieldAccessor(setAccessible(field));
	}
	
	public static Field setAccessible(Field field) {
		try {
			field.setAccessible(true);

			if (Modifier.isFinal(field.getModifiers())) {
				modifiers.set(field, field.getModifiers() & ~Modifier.FINAL);
			}

			return field;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static MethodAccessor getMethod(Class<?> clazz, String methodName, Class<?>... args) {
		Method method;
		try {
			try {
				method = clazz.getDeclaredMethod(methodName, args);
			} catch (NoSuchMethodException e) {
				method = clazz.getMethod(methodName, args);
			}
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("No method '" + methodName + "' found in '" + clazz.getCanonicalName() + "'", e);
		}
		method.setAccessible(true);

		return new DefaultMethodAccessor(method);
	}
	
	public static <T> ConstructorAccessor<T> getConstructor(Class<T> clazz, Class<?>... initArgs) {
		Constructor<T> constructor;
		
		try {
			try {
				constructor = clazz.getDeclaredConstructor(initArgs);
			} catch (NoSuchMethodException e) {
				constructor = clazz.getConstructor(initArgs);
			}
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("No constructor with types '" + Joiner.on("', '").join(initArgs) + "' found in '" + clazz.getCanonicalName() + "'", e);
		}
		constructor.setAccessible(true);
		
		return new DefaultConstructorAccessor<>(constructor);
	}
	
	public static Class<?> getGenericTypeArgument(Class<?> clazz, int index) {
		try {
			return (Class<?>) ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[index];
		} catch (Exception e) {
			throw new RuntimeException("Class not a parameterized type?", e);
		}
	}

	public static <S, T> void transferFields(Class<S> source, Class<T> target) {
		Arrays.stream(target.getDeclaredFields())
				.map(Reflect::setAccessible)
				.filter(field -> FieldAccessor.class.isAssignableFrom(field.getType()))
				.map(Reflect::getField)
				.forEach(field -> field.setStatic(Reflect.getField(source, field.getField().getName())));
	}

}
