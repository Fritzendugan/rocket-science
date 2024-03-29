package org.anddev.andengine.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.MatchResult;

import android.os.Build;

/**
 * @author Nicolas Gramlich
 * @since 15:50:31 - 14.07.2010
 */
public class SystemUtils {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final String BOGOMIPS_PATTERN = "BogoMIPS[\\s]*:[\\s]*(\\d+\\.\\d+)[\\s]*\n";
	private static final String MEMTOTAL_PATTERN = "MemTotal[\\s]*:[\\s]*(\\d+)[\\s]*kB\n";
	private static final String MEMFREE_PATTERN = "MemFree[\\s]*:[\\s]*(\\d+)[\\s]*kB\n";

	// ===========================================================
	// Fields
	// ===========================================================

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	/**
	 * @param pBuildVersionCode
	 *            taken from {@link Build.VERSION_CODES}.
	 */
	public static boolean isAndroidVersionOrHigher(final int pBuildVersionCode) {
		return Integer.parseInt(Build.VERSION.SDK) >= pBuildVersionCode;
	}

	public static float getCPUBogoMips() throws SystemUtilsException {
		final MatchResult matchResult = matchSystemFile("/proc/cpuinfo", BOGOMIPS_PATTERN, 1000);

		try {
			if(matchResult.groupCount() > 0) {
				return Float.parseFloat(matchResult.group(1));
			} else {
				throw new SystemUtilsException();
			}
		} catch (NumberFormatException e) {
			throw new SystemUtilsException(e);
		}
	}

	/**
	 * @return in kiloBytes.
	 * @throws SystemUtilsException
	 */
	public static int getMemoryTotal() throws SystemUtilsException {
		final MatchResult matchResult = matchSystemFile("/proc/meminfo", MEMTOTAL_PATTERN, 1000);

		try {
			if(matchResult.groupCount() > 0) {
				return Integer.parseInt(matchResult.group(1));
			} else {
				throw new SystemUtilsException();
			}
		} catch (NumberFormatException e) {
			throw new SystemUtilsException(e);
		}
	}

	/**
	 * @return in kiloBytes.
	 * @throws SystemUtilsException
	 */
	public static int getMemoryFree() throws SystemUtilsException {
		final MatchResult matchResult = matchSystemFile("/proc/meminfo", MEMFREE_PATTERN, 1000);

		try {
			if(matchResult.groupCount() > 0) {
				return Integer.parseInt(matchResult.group(1));
			} else {
				throw new SystemUtilsException();
			}
		} catch (NumberFormatException e) {
			throw new SystemUtilsException(e);
		}
	}

	/**
	 * @return in kiloHertz.
	 * @throws SystemUtilsException
	 */
	public static int getCPUFrequencyCurrent() throws SystemUtilsException {
		return SystemUtils.readSystemFileAsInt("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq");
	}

	/**
	 * @return in kiloHertz.
	 * @throws SystemUtilsException
	 */
	public static int getCPUFrequencyMin() throws SystemUtilsException {
		return SystemUtils.readSystemFileAsInt("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq");
	}

	/**
	 * @return in kiloHertz.
	 * @throws SystemUtilsException
	 */
	public static int getCPUFrequencyMax() throws SystemUtilsException {
		return SystemUtils.readSystemFileAsInt("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq");
	}

	/**
	 * @return in kiloHertz.
	 * @throws SystemUtilsException
	 */
	public static int getCPUFrequencyMinScaling() throws SystemUtilsException {
		return SystemUtils.readSystemFileAsInt("/sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq");
	}

	/**
	 * @return in kiloHertz.
	 * @throws SystemUtilsException
	 */
	public static int getCPUFrequencyMaxScaling() throws SystemUtilsException {
		return SystemUtils.readSystemFileAsInt("/sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq");
	}

	private static MatchResult matchSystemFile(final String pSystemFile, final String pPattern, final int pHorizon) throws SystemUtilsException {
		InputStream in = null;
		try {
			final Process process = new ProcessBuilder(new String[] { "/system/bin/cat", pSystemFile }).start();

			in = process.getInputStream();
			final Scanner scanner = new Scanner(in);

			final boolean matchFound = scanner.findWithinHorizon(pPattern, pHorizon) != null;
			if(matchFound) {
				return scanner.match();
			} else {
				throw new SystemUtilsException();
			}
		} catch (final IOException e) {
			throw new SystemUtilsException(e);
		} finally {
			StreamUtils.closeStream(in);
		}
	}

	private static int readSystemFileAsInt(final String pSystemFile) throws SystemUtilsException {
		InputStream in = null;
		try {
			final Process process = new ProcessBuilder(new String[] { "/system/bin/cat", pSystemFile }).start();

			in = process.getInputStream();
			final String content = StreamUtils.readFully(in);
			return Integer.parseInt(content);
		} catch (final IOException e) {
			throw new SystemUtilsException(e);
		} catch (final NumberFormatException e) {
			throw new SystemUtilsException(e);
		} finally {
			StreamUtils.closeStream(in);
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	public static class SystemUtilsException extends Exception {
		// ===========================================================
		// Constants
		// ===========================================================

		private static final long serialVersionUID = -7256483361095147596L;

		// ===========================================================
		// Methods
		// ===========================================================

		public SystemUtilsException() {

		}

		public SystemUtilsException(final Throwable pThrowable) {
			super(pThrowable);
		}
	}
}
