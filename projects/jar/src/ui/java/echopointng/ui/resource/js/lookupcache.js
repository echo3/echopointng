/*
 * This file is part of the Echo Point Project. This project is a collection of
 * Components that have extended the Echo Web Application Framework.
 * 
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 * 
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 * 
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or the
 * GNU Lesser General Public License Version 2.1 or later (the "LGPL"), in which
 * case the provisions of the GPL or the LGPL are applicable instead of those
 * above. If you wish to allow use of your version of this file only under the
 * terms of either the GPL or the LGPL, and not to allow others to use your
 * version of this file under the terms of the MPL, indicate your decision by
 * deleting the provisions above and replace them with the notice and other
 * provisions required by the GPL or the LGPL. If you do not delete the
 * provisions above, a recipient may use your version of this file under the
 * terms of any one of the MPL, the GPL or the LGPL.
 */


/**
 * LookupCacheEntry is the object that is stored inside a LookupCache.
 * It basically has a value, an optional sortValue and an optional XHTML repsentation of that
 * value.  It also tracks a createdDateTime which is when the object was 
 * created.
 * 
 * @param value - the value used when looking up this entry
 * 
 * @param sortValue - an optional value used to sort the entry.  This allows
 * 						for sort orders other than by lookup value.
 * 						If it is null, then value will be used
 * 
 * @param xhtml - an optional xhtml fragment that is used to display the 
 * 					matching entries.  If it is null, then value will be used
 */    
EP.LookupCacheEntry = function(value, sortValue, xhtml) {
	this.value = value;
	this.sortValue = sortValue;
	this.xhtml = xhtml;
	this.createdDateTime  = new Date();
};

/**
 * @return how many milliseconds old this LCE is at the time the function
 * was calle
 */
EP.LookupCacheEntry.prototype.age = function() {
	var now = new Date();
	return now.getTime() - this.createdDateTime.getTime(); 
};

/**
 * @return true if the LCE is older than the max age in ms
 */
EP.LookupCacheEntry.prototype.hasExpired = function(maxage) {
	if (maxage == -1) {
		return false;
	}
	if (this.age() <= maxage) {
		return false;
	}
	return true;
};

/**
 * Clones the current object into another one
 */
EP.LookupCacheEntry.prototype.clone = function() {
	var cloneLCE = new EP.LookupCacheEntry(this.value,this.sortValue,this.xhtml);
	return cloneLCE;
};

EP.LookupCacheEntry.prototype.toUniqueKey = function() {
	var s = '';
	s += 'LCE : ';
	s += this.value;
	s += ' : ';
	s += this.sortValue;
	s += ' : ';
	s += this.xhtml;
	return s;
};

EP.LookupCacheEntry.prototype.toString = function() {
	var s = '';
	s += this.toUniqueKey();
	s += ' : ';
	s += this.createdDateTime;
	return s;
};

/*-------------------------------------------------------*/

/**
 * Contructs a EP.LookupCache
 * 
 * @param maxage - the maximum age in ms for cache entries. -1 means unlimited
 * 					entries
 * 
 * @param maxsize - the maximum size of the cache. -1 means unlimited 
 * 					entries
 */
EP.LookupCache = function(maxage, maxsize, matchOptions) {
	this.maxage = maxage;
	this.maxsize = maxsize;
	this.matchOptions = matchOptions;
	
	this.lceEntries  = [];
	this.lceUniqueEntries  = [];
};

EP.LookupCache.MATCH_ONLY_FROM_START = 1;
EP.LookupCache.MATCH_IS_CASE_SENSITIVE = 2;


EP.LookupCache.strCompare = function(strA, strB) {
	var a = (strA ? strA : '');
	var b = (strB ? strB : '');
	if (a == b) {
		return 0;
	}
	if (a > b) {
		return 1;
	} else {
		return -1;
	}
};

/**
 * A static sort function that sorts by sort valuem then value and then by xhtml
 */
EP.LookupCache.sortByDefault = function(entryA,entryB) {
	if (entryA && entryB) {
		// compare based on sort value first and then on value and then on xhtml
		var rc = EP.LookupCache.strCompare(entryA.sortValue, entryB.sortValue);
		if (rc == 0) {
			rc = EP.LookupCache.strCompare(entryA.value, entryB.value);
			if (rc == 0) {
				rc = EP.LookupCache.strCompare(entryA.xhtml, entryB.xhtml);
			}
		}	
		return rc;	
	} else if (entryA) {
		return 1;
	} else {
		return -1;
	}
};

/**
 * A static sort function that sorts by lce age
 */
EP.LookupCache.sortByAge = function(entryA,entryB) {
	if (entryA && entryB) {
		// compare based on age
		var ageA = entryA.createdDateTime.getTime();
		var ageB = entryB.createdDateTime.getTime();
		
		if (ageA == ageB) {
			return 0;
		} else if (ageA < ageB) {
			return -1;
		} else {
			return 1;
		}
	} else if (entryA) {
		return 1;
	} else {
		return -1;
	}
};

/**
 * This is called when the lce has been found to exist in the cache
 * and the new lce object (with new createdatetime) is to be replace
 * the current one.  All indexes should be updated.
 */
EP.LookupCache.prototype.replaceEntry = function(newLCE) {
	var uniqueKey = newLCE.toUniqueKey();
	var oldLCE;
	for (x in this.lceEntries) {
		oldLCE = this.lceEntries[x];
		if (oldLCE.toUniqueKey() == uniqueKey) {
			this.lceEntries[x] = newLCE;
			break;
		}
	}
	this.lceUniqueEntries[uniqueKey] = newLCE;
};

/**
 * Deletes the expired entries in the cache
 * @return - the number of expired entries deleted
 */
EP.LookupCache.prototype.deleteExpiredEntries = function() {
	var count = 0;
	// sort by age first
	this.lceEntries.sort(EP.LookupCache.sortByAge);
	// first run through and expire any objects
	for(var index=this.lceEntries.length-1; index>= 0; index--) {
		var lce = this.lceEntries[index];
		if (lce.hasExpired(this.maxage)) {

			// update unique index as well		
			var uniqueKey = lce.toUniqueKey();
			this.lceUniqueEntries[uniqueKey] = null;
			delete this.lceUniqueEntries[uniqueKey];
			
			delete lce;
			this.lceEntries.length = this.lceEntries.length - 1;
			count++;
		}
	}
	// back to value order
	this.lceEntries.sort(EP.LookupCache.sortByDefault);
	return count;
};

/**
 * Called to delete the oldest entries in the cache
 * 
 * @param howMany - how many old entries to delete
 */
EP.LookupCache.prototype.deleteOldestEntries = function(howMany) {
	var count = 0;
	
	count = this.deleteExpiredEntries();
	if (count >= howMany) {
		return;
	}

	// sort by age first
	this.lceEntries.sort(EP.LookupCache.sortByAge);
	// now delete as many as we require after that
	for(var index=this.lceEntries.length-1; index>= 0; index--) {
		if (count >= howMany || this.lceEntries.length == 0) {
			break;
		}
		var lce = this.lceEntries[index];

		// update unique index as well		
		var uniqueKey = lce.toUniqueKey();
		this.lceUniqueEntries[uniqueKey] = null;
		delete this.lceUniqueEntries[uniqueKey];
		
		delete lce;
		this.lceEntries.length = this.lceEntries.length - 1;
		count++;
	}
	// now sort it to keep it in value order
	this.lceEntries.sort(EP.LookupCache.sortByDefault);
};


/**
 * Called to add an array of entries into the LC
 */
EP.LookupCache.prototype.insertEntries = function(lceArr) {
	// do we need to make room for them
	if (this.maxsize > 0) {
		var newlen = this.lceEntries.length + lceArr.length
		if (newlen > this.maxsize) {
			var howMany = newlen - this.maxsize;
			this.deleteOldestEntries(howMany);
		}
	}
	// insert/replace the  entries
	var lce;
	for(x in lceArr) {
		if (this.maxsize > -1 && this.lceEntries.length >= this.maxsize) {
			break; // cant fit anymore
		}
		lce = lceArr[x];
		// do we already have this entry
		var uniqueKey = lce.toUniqueKey();
		var lceLookup = this.lceUniqueEntries[uniqueKey];
		if (lceLookup) {
			this.replaceEntry(lce);			
		} else {
			this.lceEntries[this.lceEntries.length] = lce;
			this.lceUniqueEntries[uniqueKey] = lce;
		}
	}
	// now sort it to keep it in value order
	this.lceEntries.sort(EP.LookupCache.sortByDefault);
};

/**
 * Called to look into the catch for matches in the cache for a specific
 * value.
 * 
 * @param lookupValue - the lookup value to use
 * @param matchFromStart - boolean true if the matching should be from the start of the string
 * 					and false if it can occur anywhere in the string
 * 
 * @return an array of LCE entries.  If my be zero length.
 */
EP.LookupCache.prototype.findMatches = function(lookupValue) {
	var returnArr = [];
	var lce;
	var caseSensitive = false;
	if (this.matchOptions & EP.LookupCache.MATCH_IS_CASE_SENSITIVE == EP.LookupCache.MATCH_IS_CASE_SENSITIVE) {
		caseSensitive = true;
	}
	var matchAnywhere = true;
	if (this.matchOptions & EP.LookupCache.MATCH_ONLY_FROM_START == EP.LookupCache.MATCH_ONLY_FROM_START) {
		matchAnywhere = false;
	}
	
	lookupValue = '' + lookupValue;
	if (! caseSensitive) {
		lookupValue = lookupValue.toUpperCase();
	}
	// Can we optimise the lookup algorithmn to something better
	// than brute force lookup.  Is it really going to make a 
	// difference?
	for(var index=0; index<this.lceEntries.length; index++) {
		lce = this.lceEntries[index];
		var matches = false;
		var searchValue = lce.value;
		if (! caseSensitive) {
			searchValue = searchValue.toUpperCase();
		}
		if (matchAnywhere) {
			matches = searchValue.indexOf(lookupValue) != -1;
		} else {
			matches = searchValue.indexOf(lookupValue) == 0;
		}
		if (matches) {
			if (! lce.hasExpired(this.maxage)) {
				var newLCE = lce.clone();
				returnArr[returnArr.length] = newLCE; 
			}
		}
	}
	return returnArr;
};

EP.LookupCache.prototype.dumpCache = function() {
	var str = '';
	str += 'cache len=' + this.lceEntries.length;
	str += '\n';
	str += 'unique cache len=' + this.lceUniqueEntries.length;
	str += '\n';
	for (x in this.lceEntries) {
		var entry = this.lceEntries[x];
		
		str += entry.toString();
		str += '\n';
	}	
	return str;
};

EP.LookupCache.prototype.dumpToElement = function(htmlE) {
	var str = '';
	if (this.lceEntries.length == 0) {
		str = 'No entries in cache!';
	}
	var count = 0;
	for (x in this.lceEntries) {
		var entry = this.lceEntries[x];
		str += count + ':'
		str += entry.toString();
		str += '<br/>\n';
		
		count++;
	}	
	htmlE.innerHTML   = str;
};