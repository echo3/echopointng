package echopointng.template;

/* 
 * This file is part of the Echo Point Project.  This project is a collection
 * of Components that have extended the Echo Web Application Framework.
 *
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 */
import java.io.Serializable;

/**
 * <code>SimpleTemplateCachingHints</code> is used to indicate to the
 * templating rendering mechanism whether the compiled template data should be
 * cached and for how long.
 */
public class SimpleTemplateCachingHints implements TemplateCachingHints, Serializable {

	/** the default cache time-to-live is 120 minutes */
	public static final long DEFAULT_TIME_TO_LIVE = 120 * 60 * 1000;

	/** the default cache access time out 5 minutes */
	public static final long DEFAULT_ACCESS_TIMEOUT = 5 * 60 * 1000;

	private long timeToLive;
	private long accessTimeout;
	private long lastModified;

	/**
	 * Constructs a <code>SimpleTemplateCachingHints</code>
	 */
	public SimpleTemplateCachingHints() {
		this.timeToLive = DEFAULT_TIME_TO_LIVE;
		this.accessTimeout = DEFAULT_ACCESS_TIMEOUT;
		this.lastModified = -1;
	}
	/**
	 * @see TemplateCachingHints#getAccessTimeout()
	 */
	public long getAccessTimeout() {
		return accessTimeout;
	}

	/**
	 * Sets the time after which a cached entry will expire if it not accessed,
	 * in milliseconds.
	 * 
	 * @param accessTimeout -
	 *            The newValue to set.
	 */
	public void setAccessTimeout(long accessTimeout) {
		this.accessTimeout = accessTimeout;
	}

	/**
	 * @see TemplateCachingHints#getLastModified()
	 */
	public long getLastModified() {
		return lastModified;
	}

	/**
	 * Sets the last modified time.
	 * 
	 * @param lastModified -
	 *            The newValue to set.
	 */
	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	/**
	 * @see TemplateCachingHints#getTimeToLive()
	 */
	public long getTimeToLive() {
		return timeToLive;
	}

	/**
	 * Sets the time this template data can reside in the cache in milliseconds.
	 * 
	 * @param timeToLive -
	 *            The newValue to set.
	 */
	public void setTimeToLive(long timeToLive) {
		this.timeToLive = timeToLive;
	}
}
