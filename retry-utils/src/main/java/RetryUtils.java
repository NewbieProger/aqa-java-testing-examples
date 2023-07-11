import exceptions.SomeTestException;
import lombok.experimental.UtilityClass;

import java.util.function.Predicate;
import java.util.function.Supplier;

@UtilityClass
public class RetryUtils {

	public static final RetrySpec DEFAULT_RETRY_SPEC = new RetrySpec(0, 40, 500);
	public static final RetrySpec RETRY_SPEC_WITH_INITIAL_DELAY = new RetrySpec(2000, 40, 500);

	private static final RetryCallback NOOP_CALLBACK = new RetryCallback() {
	};

	public static void runWithRetry(Runnable function) {
		runWithRetry(DEFAULT_RETRY_SPEC, function);
	}

	public static void runWithRetry(RetrySpec spec, Runnable function) {
		sleep(spec.initialDelay);

		for (int tryCount = 0; tryCount < spec.retryCount; tryCount++) {
			try {
				function.run();
				return;
			} catch (NonRetryableException e) {
				throw e;
			} catch (Throwable e) {
				sleep(spec.retryTimeout);
			}
		}

		function.run();
	}

	public static <T> T getUntil(RetrySpec spec, Supplier<T> function, Predicate<T> condition) {
		return getUntil(spec, function, condition, NOOP_CALLBACK);
	}

	public static <T> T getUntil(Supplier<T> function, Predicate<T> condition, Runnable onConditionPassed) {
		return getUntil(DEFAULT_RETRY_SPEC, function, condition, onConditionPassed);
	}

	public static <T> T getUntil(RetrySpec spec, Supplier<T> function, Predicate<T> condition, Runnable onConditionPassed) {
		return getUntil(spec, function, condition, new RetryCallback() {
			@Override
			public void onConditionPassed() {
				onConditionPassed.run();
			}
		});
	}

	public static <T> T getUntil(Supplier<T> function, Predicate<T> condition, RetryCallback retryCallback) {
		return getUntil(DEFAULT_RETRY_SPEC, function, condition, retryCallback);
	}

	public static <T> T getUntil(RetrySpec spec, Supplier<T> function, Predicate<T> condition, RetryCallback retryCallback) {

		T result = null;

		for (int tryCount = 0; tryCount < spec.retryCount; tryCount++) {

			if (tryCount == 0) {
				sleep(spec.initialDelay);
			} else {
				sleep(spec.retryTimeout);
			}
			result = function.get();

			if (condition.test(result)) {
				retryCallback.onConditionPassed();
				return result;
			}

		}

		retryCallback.onRetriesCountExhausted();

		return result;
	}

	private void sleep(int timeMs) {
		if (timeMs <= 0) {
			return;
		}

		try {
			Thread.sleep(timeMs);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new SomeTestException("Something went wrong during sleeping in thread", e);
		}
	}

	public interface RetryCallback {
		default void onConditionPassed() {

		}

		default void onRetriesCountExhausted() {

		}

	}

	public static class RetrySpec {

		private final int initialDelay;
		private final int retryCount;
		private final int retryTimeout;

		public RetrySpec(int initialDelay, int retryCount, int retryTimeout) {
			this.initialDelay = initialDelay;
			this.retryCount = retryCount;
			this.retryTimeout = retryTimeout;
		}
	}

	public static class NonRetryableException extends RuntimeException {

		public NonRetryableException(String message) {
			super(message);
		}
	}
}
