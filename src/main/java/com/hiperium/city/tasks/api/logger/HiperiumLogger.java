package com.hiperium.city.tasks.api.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public final class HiperiumLogger {

	private Logger logger;

	private HiperiumLogger() {
		// Nothing to do
	}

	private HiperiumLogger(Class<?> clazz) {
		this();
		this.logger = LoggerFactory.getLogger(clazz);
	}

	public static HiperiumLogger getLogger(Class<?> clazz) {
		return new HiperiumLogger(clazz);
	}

	public void debug(String message) {
		if (Objects.nonNull(this.logger) && this.logger.isDebugEnabled()) {
			this.logger.debug(message);
		}
	}

	public void debug(String message, Object... args) {
		if (Objects.nonNull(this.logger) && this.logger.isDebugEnabled()) {
			this.logger.debug(message, args);
		}
	}

	public void warn(String message) {
		if (Objects.nonNull(this.logger)) {
			this.logger.warn(message);
		}
	}

	public void warn(String message, Object... args) {
		if (Objects.nonNull(this.logger)) {
			this.logger.warn(message, args);
		}
	}

	public void info(String message) {
		if (Objects.nonNull(this.logger)) {
			this.logger.info(message);
		}
	}

	public void info(String message, Object... args) {
		if (Objects.nonNull(this.logger)) {
			this.logger.info(message, args);
		}
	}

	public void error(String message) {
		if (Objects.nonNull(this.logger)) {
			this.logger.error(message);
		}
	}

	public void error(String message, Object... args) {
		if (Objects.nonNull(this.logger)) {
			this.logger.error(message, args);
		}
	}

	public void error(String message, Throwable throwable) {
		if (Objects.nonNull(this.logger)) {
			this.logger.error(message, throwable);
		}
	}
}
