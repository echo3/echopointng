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

/**
 * <code>TemplateCachingHints</code> is used to indicate to the templating
 * rendering mechanism whether the compiled template data should be cached and
 * for how long.
 */
public interface TemplateCachingHints {

	/**
	 * If the cached template data is not accessed in this time (in
	 * milliseconds) then it can be expired from the cache.
	 * <p>
	 * If this is -1, then the cached template data will not expire
	 * based on acess time.
	 * 
	 * @return the time after which any cached entry will expire if it is not
	 *         accessed, in milliseconds.
	 */
	public long getAccessTimeout();

	/**
	 * Returns the time the content of this <code>TemplateDataSource</code>
	 * was last modified.
	 * <p>
	 * The return value is used to decide whether to reparse a Source or not.
	 * Reparsing is done if the value returned here <em>differs</em> from the
	 * value returned at the last processing time. This may not return a 'real'
	 * time, it needs just to be comparable to itself; so some sort of version
	 * counter would be perfect as well.
	 * 
	 * @return long a modification time or counter
	 */
	public long getLastModified();

	/**
	 * The cached template data can reside in the cache for this many
	 * milliseconds, after which it can expire.
	 * <p>
	 * If this is -1, then the cached template data will live forever.
	 * 
	 * @return the time this template data can reside in the cache in
	 *         milliseconds.
	 */
	public long getTimeToLive();
}