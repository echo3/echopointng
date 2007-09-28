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
package echopointng.text;

import echopointng.xhtml.XhtmlFragment;

/**
 * The AutoLookupModel provides for support for looking up
 * <code>AutoLookupModel.Entry</code>'s based on partial-string matching.
 * 
 * <h3>Client Side Caching</h3>
 * A cache of values will be kept on the client, depending on the values of
 * maximumCacheAge and maximumCacheSize.
 * <p>
 * The client cache uses a combination of Entry value, sortValue and
 * XhtmlFragement to produce a unique key for the
 * <code>AutoLookupModel.Entry</code>. This is then used to keep unique
 * copies of the entries in the client side cache and duplicates will not be
 * shown.
 * <p>
 * If you have values that map to one of more display values (such as postcode)
 * then you should ensure that the value/sortValue/xhtml combination is indeed
 * unique.
 * 
 */
public interface AutoLookupModel {

	/**
	 * When this match option is in used, the lookup matching is expected to
	 * only match from the start of the value string. By default matching is
	 * anywhere in the value string.
	 */
	public static final int MATCH_ONLY_FROM_START = 1;

	/**
	 * When this match option is in used, the lookup matching is case sensitive.
	 * By default the matching is case insensitive.
	 */
	public static final int MATCH_IS_CASE_SENSITIVE = 2;

	/**
	 * <code>AutoLookupModel.Entry</code> represents the entries that can be
	 * returned by the AutoLookupModel.
	 */
	public interface Entry {
		/**
		 * @return the value that is used
		 */
		public String getValue();

		/**
		 * @return an optional sort value that is used to sort arrays
		 *         <code>AutoLookupModel.Entry</code>'s If this is not
		 *         present, then the value is used.
		 */
		public String getSortValue();

		/**
		 * @return an optional XhtmlFragment that is used to display
		 *         <code>AutoLookupModel.Entry</code>'s on the client. If
		 *         this is not present, then the value is used.
		 */
		public XhtmlFragment getXhtmlFragment();

	}

	/**
	 * A simple implementation of <code>AutoLookupModel.Entry</code> is
	 * provided.
	 * 
	 */
	public static class DefaultEntry implements Entry {
		private String value;

		private String sortValue;

		private XhtmlFragment xhtml;

		/**
		 * Constructs a <code>AutoLookupModel.DefaultEntry</code> where all
		 * three values (value, sortValue, xhtml) are the same
		 */
		public DefaultEntry(String value) {
			this(value, value, new XhtmlFragment(value));
		}

		/**
		 * Constructs a <code>AutoLookupModel.DefaultEntry</code>
		 */
		public DefaultEntry(String value, String sortValue, String xhtml) {
			this(value, sortValue, new XhtmlFragment(xhtml));
		}

		/**
		 * Constructs a <code>AutoLookupModel.DefaultEntry</code>
		 */
		public DefaultEntry(String value, String sortValue, XhtmlFragment xhtml) {
			setValue(value);
			setSortValue(sortValue);
			setXhtmlFragment(xhtml);
		}

		/**
		 * @see echopointng.text.AutoLookupModel.Entry#getSortValue()
		 */
		public String getSortValue() {
			return sortValue;
		}

		/**
		 * Sets the sort value
		 * 
		 * @param sortValue -
		 *            the new sort value
		 */
		public void setSortValue(String sortValue) {
			this.sortValue = sortValue;
		}

		/**
		 * @see echopointng.text.AutoLookupModel.Entry#getValue()
		 */
		public String getValue() {
			return value;
		}

		/**
		 * Sets the value
		 * 
		 * @param value -
		 *            the new value The value to set.
		 */
		public void setValue(String value) {
			this.value = value;
		}

		/**
		 * @see echopointng.text.AutoLookupModel.Entry#getXhtmlFragment()
		 */
		public XhtmlFragment getXhtmlFragment() {
			return xhtml;
		}

		/**
		 * Sets the <code>XhtmlFragment</code>
		 * 
		 * @param xhtml
		 *            The xhtml to set.
		 */
		public void setXhtmlFragment(XhtmlFragment xhtml) {
			this.xhtml = xhtml;
		}

	}

	/**
	 * @return the maximum age in milliseconds of entries that are allowed to be
	 *         cached on the client from this model. A value of -1 means that
	 *         the entries never expire.
	 */
	public int getMaximumCacheAge();

	/**
	 * @return the maximum number of entries that are allowed to be cached on
	 *         the client from this model. A value of -1 means that the number
	 *         of entries is unlimited.
	 */
	public int getMaximumCacheSize();

	/**
	 * @return the current match options which can be one of the following
	 *         values OR'ed together:
	 *         <ul>
	 *         <li>MATCH_ONLY_FROM_START - match from the start of the search
	 *         value</li>
	 *         <li>MATCH_IS_CASE_SENSITIVE - matching is case senstive</li>
	 *         </ul>
	 */
	public int getMatchOptions();

	/**
	 * This is called to prepopulate a cache of
	 * <code>AutoLookupModel.Entry</code>'s on the client.
	 * 
	 * @return a NON-NULL array of <code>AutoLookupModel.Entry</code>'s. This
	 *         may be zero length but may not be null.
	 */
	public Entry[] prePopulate();

	/**
	 * This is called to populate a cache of <code>AutoLookupModel.Entry</code>'s
	 * on the client based on the partial search value
	 * 
	 * @param partialSearchValue -
	 *            the partial search value to use
	 * @param matchOptions -
	 *            the matching options in play which can be on of the following
	 *            values:
	 *            <ul>
	 *            <li>MATCH_ONLY_FROM_START - match from the start of the
	 *            search value</li>
	 *            <li>MATCH_IS_CASE_SENSITIVE - matching is case senstive</li>
	 *            </ul>
	 * 
	 * @return a NON-NULL array of <code>AutoLookupModel.Entry</code>'s. This
	 *         may be zeor length but may not be null.
	 */
	public Entry[] searchEntries(String partialSearchValue, int matchOptions);
}
