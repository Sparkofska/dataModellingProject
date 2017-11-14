package md;

public class Logger {
	private static Logger instance = null;
	private static final int LOG_LEVEL_DEBUG = 1;
	private static final int LOG_LEVEL_INFO = 2;

	public static Logger getInstance() {
		if (instance == null) {
			instance = new Logger(LOG_LEVEL_DEBUG);
		}
		return instance;
	}

	private int logLevel = 0;

	private Logger(int logLevel) {
		this.logLevel = logLevel;
	}

	public void info(String msg) {
		if (this.logLevel <= LOG_LEVEL_INFO)
			System.out.println(msg);
	}

	public void debug(String msg) {
		if (this.logLevel <= LOG_LEVEL_DEBUG)
			System.out.println(msg);
	}
}
