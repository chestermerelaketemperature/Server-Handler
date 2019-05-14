package com.chestermere.lake.temperature.utils;

public class Utils {

	/**
	 * Tests whether a given class exists in the classpath.
	 * 
	 * @author Skript team.
	 * @param className The {@link Class#getCanonicalName() canonical name} of the class
	 * @return Whether the given class exists.
	 */
	public static boolean classExists(String className) {
		try {
			Class.forName(className);
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	/**
	 * Tests whether a method exists in the given class.
	 * 
	 * @author Skript team.
	 * @param c The class
	 * @param methodName The name of the method
	 * @param parameterTypes The parameter types of the method
	 * @return Whether the given method exists.
	 */
	public static boolean methodExists(Class<?> c, String methodName, Class<?>... parameterTypes) {
		try {
			c.getDeclaredMethod(methodName, parameterTypes);
			return true;
		} catch (NoSuchMethodException | SecurityException e) {
			return false;
		}
	}

	public static boolean methodExists(Class<?> c, String methodName) {
		try {
			c.getDeclaredMethod(methodName);
			return true;
		} catch (NoSuchMethodException | SecurityException e) {
			return false;
		}
	}

}
