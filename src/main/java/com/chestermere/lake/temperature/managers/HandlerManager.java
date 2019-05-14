package com.chestermere.lake.temperature.managers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.reflections.Reflections;

import com.chestermere.lake.temperature.Server;
import com.chestermere.lake.temperature.sockets.Handler;

public class HandlerManager {

	private final Set<Handler> registered = new HashSet<>();

	public HandlerManager(Server instance) {
		Reflections reflections = new Reflections("com.chestermere.lake.temperature.handlers");
		for (Class<? extends Handler> clazz : reflections.getSubTypesOf(Handler.class)) {
			try {
				Constructor<? extends Handler> constructor = clazz.getConstructor(Server.class);
				if (constructor == null)
					continue;
				Handler handler = constructor.newInstance(instance);
				registered.add(handler);
			} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				instance.getLogger().atWarning()
						.withCause(e)
						.log("Handler %s did not have a Server.class constructor.", clazz.getName());
			}
		}
	}

	protected void registerHandler(Handler handler) {
		for (String name : handler.getNames()) {
			if (getHandler(name).isPresent())
				return;
		}
		registered.add(handler);
	}

	@SuppressWarnings("unchecked")
	public <T> Optional<T> getHandler(Class<? extends Handler> clazz) {
		return (Optional<T>) registered.parallelStream()
				.filter(handler -> clazz.isAssignableFrom(handler.getClass()))
				.findFirst();
	}

	public Optional<Handler> getHandler(String input) {
		for (Handler handler : registered) {
			for (String name : handler.getNames()) {
				if (name.equalsIgnoreCase(input))
					return Optional.of(handler);
			}
		}
		return Optional.empty();
	}

}
