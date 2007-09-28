package echopointng.util.collections;

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

import java.lang.ref.SoftReference;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * <code>ExpiryCache</code> implements a <code>Map</code> cache contains
 * objects that "expire".
 * <p>
 * By default, soft references are used to the cached data so that they can be
 * reclaimed in low memory conditions regardless of whether they have expired or
 * not.
 * <p>
 * The time-to-live and access-timeout is used to decide when an object has
 * expired and needs to be removed from the cache.
 * <p>
 * Time-to-live is simple. Once the specified period elapses, the object is
 * removed from the cache, regardless of how many times its been accessed.
 * <p>
 * Access-timeout is a little more complicated. Each time the object is taken
 * from the cache, its lastAccessTime is tracked. If the access-timeout has
 * expired (since its last access) then the object is taken from the cache.
 * <p>
 * If both the time-to-live and access-timeout is -1, then the object will never
 * expire from the cache.
 * <p>
 * The objVersion value, which can be specified at put time, can be later
 * retrieved along with the object. This allows for application level versioning
 * of objects against when they put in the cache. For example if you cached the
 * contents of a File read, you might want to store the File.lastModified() in
 * the objVersion number. Later when you want the cached contents, you could
 * check the put time objVersion number against the current File.lastModified().
 * So even though the cached content has not expired, the underlying content has
 * and hence should be re-read.
 * <p>
 * Under the cover this class uses a ConcurrentReaderHashMap and hence is thread
 * safe for writes to the cache.
 */
public class ExpiryCache implements Map {

	/** the default cache time-to-live is 60 minutes */
	public static final long DEFAULT_TIME_TO_LIVE = 60 * 60 * 1000;

	/** the default cache access time out 5 minutes */
	public static final long DEFAULT_ACCESS_TIMEOUT = 5 * 60 * 1000;

	private Map cacheMap = new ConcurrentReaderHashMap();

	private long ttl = DEFAULT_TIME_TO_LIVE;

	private long ato = DEFAULT_ACCESS_TIMEOUT;

	private boolean softReferences;

	/**
	 * <code>CacheEntry</code> is used to wrap cached objects and can track
	 * their time-to-live, last access time and access count.
	 */
	private class CacheEntry {

		private Object cachedData;

		private long timeCached;

		private long timeAccessedLast;

		private int numberOfAccesses;

		private long objTTL;

		private long objATO;

		private long objVersion;

		private boolean customTimes;

		private CacheEntry(Object cachedData, long ttl, long ato, long objVersion) {
			long now = System.currentTimeMillis();
			this.cachedData = cachedData;
			this.objVersion = objVersion;
			objTTL = ttl;
			objATO = ato;
			customTimes = true;
			timeCached = now;
			timeAccessedLast = now;
			++numberOfAccesses;
		}

		private Object getCachedData() {
			timeAccessedLast = System.currentTimeMillis();
			++numberOfAccesses;
			return cachedData;
		}

		private boolean hasExpired(long now) {
			long usedTTL = customTimes ? objTTL : ExpiryCache.this.ttl;
			long usedATO = customTimes ? objATO : ExpiryCache.this.ato;
			if (usedTTL != -1) {
				usedTTL = timeCached + usedTTL;
				if (now > usedTTL)
					return true;
			}
			if (usedATO != -1) {
				usedATO = timeAccessedLast + usedATO;
				if (now > usedATO)
					return true;
			}
			return false;
		}

		public String toString() {
			long now = System.currentTimeMillis();
			long usedTTL = customTimes ? objTTL : ExpiryCache.this.ttl;
			long usedATO = customTimes ? objATO : ExpiryCache.this.ato;

			StringBuffer buf = new StringBuffer();
			buf.append(String.valueOf(this.cachedData));
			buf.append(" ");
			buf.append("[put version ");
			buf.append(objVersion);
			buf.append("] ");

			buf.append("[time to live ");
			buf.append((timeCached + usedTTL) - now);
			buf.append("ms] ");

			buf.append("[access timeout in ");
			buf.append((timeAccessedLast + usedATO) - now);
			buf.append("ms]");

			return buf.toString();
		}
	}

	/**
	 * Constructs a default <code>ExpiryCache</code> that uses
	 * <code>SoftReference</code>s
	 */
	public ExpiryCache() {
		this(DEFAULT_TIME_TO_LIVE, DEFAULT_ACCESS_TIMEOUT, true);
	}

	/**
	 * Constructs a <code>ExpiryCache</code> that uses
	 * <code>SoftReference</code>s
	 * 
	 * @param timeToLive -
	 *            the default time-to-live for a cache entry
	 * @param accessTimeout -
	 *            the default access timeout for a cache entry
	 */
	public ExpiryCache(long timeToLive, long accessTimeout) {
		this(timeToLive, accessTimeout, true);
	}

	/**
	 * Constructs a <code>ExpiryCache</code> with all the parameters
	 * 
	 * @param timeToLive -
	 *            the default time-to-live for a cache entry
	 * @param accessTimeout -
	 *            the default access timeout for a cache entry
	 * @param softReferences -
	 *            whether <code>SoftReference</code>s are used to cached data
	 */
	public ExpiryCache(long timeToLive, long accessTimeout, boolean softReferences) {
		ttl = timeToLive;
		ato = accessTimeout;
		this.softReferences = softReferences;
	}

	/**
	 * Sets the default 'time-to-live' for a cache entry
	 * 
	 * @param milliSecs -
	 *            'time-to-live' for a cache entry
	 */
	public void setTimeToLive(long milliSecs) {
		ttl = milliSecs;
	}

	/**
	 * Sets the default access timeout for a cache entry
	 * 
	 * @param milliSecs -
	 *            access timeout for a cache entry
	 */
	public void setAccessTimeout(long milliSecs) {
		ato = milliSecs;
	}

	/**
	 * Returns the time when the object was cached under a given key
	 * 
	 * @param key -
	 *            the key to the cached object
	 * @return the time when the object was cached under a given key
	 */
	public long whenCached(Object key) {
		CacheEntry ce = (CacheEntry) cacheMap.get(key);
		if (ce == null)
			return 0;
		return ce.timeCached;
	}

	/**
	 * Returns the time when the object was last accessed under a given key
	 * 
	 * @param key -
	 *            the key to the cached object
	 * @return the time when the object was last accessed under a given key
	 */
	public long whenLastAccessed(Object key) {
		CacheEntry ce = (CacheEntry) cacheMap.get(key);
		if (ce == null)
			return 0;
		return ce.timeAccessedLast;
	}

	/**
	 * Returns the version number that was provided when the object was placed
	 * in the cache.
	 * 
	 * @param key -
	 *            the key to the cached object
	 * @return the version number that was provided when the object was placed
	 *         in the cache or -1 if it was not provided at cache put.
	 */
	public long whenVersion(Object key) {
		CacheEntry ce = (CacheEntry) cacheMap.get(key);
		if (ce == null)
			return -1;
		return ce.objVersion;
	}

	/**
	 * Returns the number of times the object was accessed under a given key
	 * 
	 * @param key -
	 *            the key to the cached object
	 * @return the number of times the object was accessed under a given key
	 */
	public int howManyTimesAccessed(Object key) {
		CacheEntry ce = (CacheEntry) cacheMap.get(key);
		if (ce == null)
			return 0;
		return ce.numberOfAccesses;
	}

	/**
	 * Returns true if <code>SoftReference</code>s are used to cached data
	 * 
	 * @return true if <code>SoftReference</code>s are used to cached data
	 */
	public boolean isSoftReferences() {
		return softReferences;
	}

	/**
	 * Sets whether <code>SoftReference</code>s are used to hold cache
	 * entries.
	 * 
	 * @param newValue -
	 *            the new value of the flag
	 */
	public void setSoftReferences(boolean newValue) {
		this.softReferences = newValue;
	}

	/**
	 * @see java.util.Map#clear()
	 */
	public void clear() {
		cacheMap.clear();
	}

	/**
	 * @see java.util.Map#containsKey(java.lang.Object)
	 */
	public boolean containsKey(Object key) {
		return cacheMap.containsKey(key);
	}

	/**
	 * @see java.util.Map#remove(java.lang.Object)
	 */
	public Object remove(Object key) {
		CacheEntry ce = (CacheEntry) cacheMap.remove(key);
		if (ce != null)
			return dereferenceCacheEntry(ce);
		return null;
	}

	/**
	 * Note this may return a size larger than the number of non expired
	 * objects. A traversal of cached objects is NOT done here to work out a
	 * correct size value.
	 * 
	 * @see java.util.Map#size()
	 */
	public int size() {
		return cacheMap.size();
	}

	/**
	 * Note this may return false when in fact all objects in the cache have
	 * expired. A traversal of cached objects is NOT done here to work out if it
	 * is empty.
	 * 
	 * @see java.util.Map#isEmpty()
	 */
	public boolean isEmpty() {
		return cacheMap.isEmpty();
	}

	/**
	 * @see java.util.Map#putAll(java.util.Map)
	 */
	public void putAll(Map t) {
		for (Iterator iter = t.keySet().iterator(); iter.hasNext();) {
			Object key = iter.next();
			Object objToCache = t.get(key);
			this.put(key, objToCache);
		}
	}

	/**
	 * Returns a Set of all the keys in the cache. It may contain keys to
	 * objects that have expired so be careful when using this.
	 * 
	 * @return a Set of all the keys in the cache
	 */
	public Set keySet() {
		return cacheMap.keySet();
	}

	/**
	 * This operation is not supported on ExpiryCache.
	 * 
	 * @throws UnsupportedOperationException
	 */
	public Set entrySet() {
		throw new UnsupportedOperationException("public Set entrySet() is an unsupported operation.");
	}

	/**
	 * This operation is not supported on ExpiryCache.
	 * 
	 * @throws UnsupportedOperationException
	 */
	public Collection values() {
		throw new UnsupportedOperationException("public Collection values() is an unsupported operation.");
	}

	/**
	 * This operation is not supported on ExpiryCache.
	 * 
	 * @throws UnsupportedOperationException
	 */
	public boolean containsValue(Object value) {
		throw new UnsupportedOperationException("public boolean containsValue(Object value) is an unsupported operation.");
	}

	/**
	 * Places an object into the cache. The object will have a caches default
	 * 'time-to-live' and a caches default 'access time out' value.
	 * 
	 * @param key -
	 *            the key of the cached object
	 * @param objToCache -
	 *            the object to cache
	 * @return - the old object at this cache key
	 */
	public Object put(Object key, Object objToCache) {
		return put(key, objToCache, ttl, ato, -1);
	}

	/**
	 * Places an object into the cache. The object will have a caches default
	 * 'time-to-live' and a caches default 'access time out' value.
	 * 
	 * @param key -
	 *            the key of the cached object
	 * @param objToCache -
	 *            the object to cache
	 * @param objVersion -
	 *            the version of the object at the time it is put
	 * @return - the old object at this cache key
	 */
	public Object put(Object key, Object objToCache, long objVersion) {
		return put(key, objToCache, ttl, ato, objVersion);
	}

	/**
	 * Places an object into the cache with the specified 'time-to-live' and a
	 * 'access time out' value.
	 * 
	 * @param key -
	 *            the key of the cached object
	 * @param objToCache -
	 *            the object to cache
	 * @param timeToLive -
	 *            the time-to-live on the object or -1 to live for ever
	 * @param accessTimeout -
	 *            the accessTimeout on the object or -1 to never time out
	 * 
	 * @return - the old object at this cache key
	 */
	public Object put(Object key, Object objToCache, long timeToLive, long accessTimeout) {
		return put(key, objToCache, timeToLive, accessTimeout, -1);
	}

	/**
	 * Places an object into the cache with the specified 'time-to-live' and a
	 * 'access time out' value as well as a version number.
	 * 
	 * @param key -
	 *            the key of the cached object
	 * @param objToCache -
	 *            the object to cache
	 * @param timeToLive -
	 *            the time-to-live on the object or -1 to live for ever
	 * @param accessTimeout -
	 *            the accessTimeout on the object or -1 to never time out
	 * @param objVersion -
	 *            a version number that can be used later
	 * 
	 * 
	 * @return - the old object at this cache key
	 */
	public Object put(Object key, Object objToCache, long timeToLive, long accessTimeout, long objVersion) {

		// System.err.println("Putting :" + key);
		//
		// Since we are now using ConcurrentReaderHashMap we can remove
		// the global put synchronisation
		//
		CacheEntry ce = (CacheEntry) cacheMap.get(key);
		if (ce == null) {
			putCacheEntry(key, objToCache, timeToLive, accessTimeout, objVersion);
			return null;
		} else {
			Object obj = dereferenceCacheEntry(ce);
			if (obj == null) {
				if (objToCache == null) {
					// Avoids creating unnecessary new CacheEntry
					// Number of accesses is not reset because object is the
					// same
					ce.timeCached = ce.timeAccessedLast = System.currentTimeMillis();
					return null;
				} else {
					putCacheEntry(key, objToCache, timeToLive, accessTimeout, objVersion);
					return null;
				}
			} else if (obj.equals(objToCache)) {
				// Avoids creating unnecessary new CacheEntry
				// Number of accesses is not reset because object is the same
				ce.timeCached = ce.timeAccessedLast = System.currentTimeMillis();
				return null;
			} else {
				putCacheEntry(key, objToCache, timeToLive, accessTimeout, objVersion);
				return obj;
			}
		}
	}

	/**
	 * Retrieves an object from the cache. If the object has expired, then it
	 * will return null
	 * 
	 * @param key -
	 *            the key to the cached object
	 * @return the object for the key or null
	 */
	public Object get(Object key) {
		return _getObject(key, -1, false);
	}

	/**
	 * Retrieves an object from the cache. If the object has expired or its
	 * recorded put version is less then the objVersion, then it will return
	 * null.
	 * 
	 * @param key -
	 *            the key to the cached object
	 * @param objVersion -
	 *            the object version number to compare against
	 * @return the object for the key or null
	 */
	public Object get(Object key, long objVersion) {
		return _getObject(key, objVersion, true);
	}

	/**
	 * Do the actual cache get and lokk for expired'ness
	 */
	Object _getObject(Object key, long objVersion, boolean useVersioning) {
		logMessage("get",key);
		CacheEntry ce = (CacheEntry) cacheMap.get(key);
		if (ce == null) {
			// TODO - remove this
			logMessage("miss",key);
			return null;
		} else {
			//
			// has it expired. Stale objects are no good
			// and in this version of the class we should
			// reap it now. This slows us down a bit but
			// there is no need for background cleanups
			//
			long now = System.currentTimeMillis();
			if (ce.hasExpired(now)) {
				onExpiredObject(key);
				return null;
			}
			//
			// has the put version we have less than the current version
			// we have
			if (useVersioning && ce.objVersion < objVersion) {
				onExpiredObject(key);
				return null;
			}
			Object value = dereferenceCacheEntry(ce);
			if (value == null && isSoftReferences()) {
				logMessage("null value possible memory reclaim",key);
			}
			return value;
		}
	}

	/**
	 * Called when an object has been detected as expired or versioned out of
	 * existence.
	 * 
	 * @param key -
	 *            the key to the object
	 */
	protected void onExpiredObject(Object key) {
		cacheMap.remove(key);
		// TODO - remove this
		logMessage("expired",key);
	}
	
	private void logMessage(String message, Object key) {
		if (true) return;
		StringBuffer sb = new StringBuffer();
		sb.append("Expiry Cache - ");
		sb.append(new SimpleDateFormat("hh:mm:ss").format(new Date()));
		sb.append(" - ");
		sb.append(message);
		sb.append(" - ");
		sb.append(String.valueOf(key));
		System.out.println(sb);
	}

	/**
	 * Called to see if a cache entry has expired or not at a given point in
	 * time.
	 * 
	 * @param key -
	 *            the key to the cached object
	 * @param when -
	 *            the time to do the comparision against
	 * @return true if the object has expired in the cache or cant be found in
	 *         the cache.
	 */
	public boolean hasExpired(Object key, long when) {
		CacheEntry ce = (CacheEntry) cacheMap.get(key);
		if (ce != null)
			return ce.hasExpired(when);
		return true;
	}

	/**
	 * Called to dereference a CacheEntry's reference to an object, depending on
	 * whether soft references have been used.
	 */
	private Object dereferenceCacheEntry(CacheEntry ce) {
		Object cachedData = ce.getCachedData();
		if (cachedData instanceof SoftReference) {
			return ((SoftReference) cachedData).get();
		}
		return cachedData;
	}

	/**
	 * Called to encode a CacheEntry's reference to an object into the cache,
	 * depending on whether soft references are used
	 */
	private void putCacheEntry(Object key, Object objToCache, long timeToLive, long accessTimeout, long objVersion) {
		logMessage("put",key);
		if (isSoftReferences())
			cacheMap.put(key, new CacheEntry(new SoftReference(objToCache), timeToLive, accessTimeout, objVersion));
		else
			cacheMap.put(key, new CacheEntry(objToCache, timeToLive, accessTimeout, objVersion));
	}

}
