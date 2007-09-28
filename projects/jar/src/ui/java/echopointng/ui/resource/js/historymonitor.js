
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

//========================================================================================================
// The following is the code taken from the UnFocus hsitory keeper codeset.  Its LGPL and the comment
// has been reprimted as per the LGPL licence.
//
//
// http://www.unfocus.com/Projects/HistoryKeeper/
//
//========================================================================================================

// fix array methods - I believe I borrowed this from Dean Edwards's IE7 script - same license ;-)
if (![].push) Array.prototype.push = function() {
	for(var i=0; i<arguments.length; i++)
		this[this.length-1] = arguments[i];
	return this.length;
};

//========================================================================================================
/*
	unFocus.Utilities.EventManager, version 1.0b (beta) (2005/12/16)
	Copyright: 2005, Kevin Newman (http://www.unfocus.com/Projects/)
	License: http://creativecommons.org/licenses/LGPL/2.1/
*/
// make sure faux-namespace is available before adding to it
unFocus = {};
unFocus.Utilities = {};

/* Class EventManager
	Provides the interface and functionality to a Subscriber/Subscriber Pattern interface for
	classes to easily inherit or use. Event types are passed in during instantiation. I'm not
	sure that's the best way to do it, but that's how it is for now (comments are welcome). */
unFocus.Utilities.EventManager = function(arg) {
	this._listeners = {};
	for (var i = arguments.length; -1 < --i;) {
		this._listeners[arguments[i]] = [];
	}
};

/* Method: addEventListener
	A public method that adds an event listener to be called when the hash changes. */
unFocus.Utilities.EventManager.prototype.addEventListener = function($type, $listener) {
	// check that listener is not in list
	for (var i = this._listeners[$type].length; -1 < --i;)
		if (this._listeners[$type][i] == $listener) return;
	// add listener to appropriate list
	this._listeners[$type].push($listener);
};

/* Method: removeListener
	A public method that removes an event listener. */
unFocus.Utilities.EventManager.prototype.removeEventListener = function($type, $listener) {
	// search for the listener method
	for (var i = this._listeners[$type].length; -1 < --i;) {
		if (this._listeners[$type][i] == $listener) {
			this._listeners.splice(i,1);
			break;
		}
	}
};

/* Method: notifyListeners
	Notifies listeners when an event accurs. */
unFocus.Utilities.EventManager.prototype.notifyListeners = function($type, $data) {
	for (var i = this._listeners[$type].length; -1 < --i;)
		this._listeners[$type][i]($data);
};

//========================================================================================================
/*
unFocus.History, version 1.9 (alpha) (2006/04/14)
Copyright: 2005-2006, Kevin Newman (http://www.unfocus.com/Projects/HistoryKeeper/)
License: http://creativecommons.org/licenses/LGPL/2.1/
*/
/*
	Class: unFocus.History
		A singleton with subscriber interface (<unFocus.Utilities.EventManager>) 
		that keeps a history and provides deep links for Flash and AJAX apps
*/
unFocus.History = (function() {

// use a closure to avoid poluting the global scope, and to discourage reinstantiation (like a singleton)
function Keeper() {
	// bool: initialize - whether or not the class has been initialized
	var _this = this,
		// set the poll interval here.
		_pollInterval = 200, _intervalID,
		// get the initial Hash state
		_currentHash = _getHash();

	/*
	method: _getHash
		A private method that gets the Hash from the location.hash property.
	 
	returns:
		a string containing the current hash from the url
	*/
	function _getHash() {
		return location.hash.substring(1);
	}
	
	/*
	method: _setHash
		A private method that sets the Hash on the location string (the current url).
	*/
	function _setHash($newHash) {
		window.location.hash = $newHash;
	}
	
	/*
	method: _watchHash
		A private method that is called every n miliseconds (<_pollInterval>) to check if the hash has changed.
		This is the primary Hash change detection method for most browsers. It doesn't work to detect the hash
		change in IE 5.5+ or various other browsers. Workarounds like the iframe method are used for those 
		browsers (IE 5.0 will use an anchor creation hack).
	*/
	function _watchHash() {
		var $newHash = _getHash();
		if (_currentHash != $newHash) {
			_currentHash = $newHash;
			_this.notifyListeners("historyChange", $newHash);
		}
	}
	// set the interval
	if (setInterval) _intervalID = setInterval(_watchHash, _pollInterval);
	
	/*
	method: getCurrentBookmark
		A public method to retrieve the current history string
	
	returns:
		The current History Hash
	*/
	_this.getCurrent = function() {
		return _currentHash;
	};
	
	/* 
	Method: _createAnchor
		Various browsers may need an achor to be present in the dom for the hash to actually be set,
		so we add one every time a history entry is made.
	*/
	function _createAnchor($newHash) {
		if (!_checkAnchorExists($newHash)) {
			var $anchor = document.createElement("a");
			$anchor.setAttribute("name", $newHash);
			if (/MSIE/.test(navigator.userAgent) && !window.opera)
				$anchor = document.createElement('<a name="'+$newHash+'">'+$newHash+"</a>");
			$anchor.style.position = "absolute";
			$anchor.style.top = getScrollY()+"px";
			$anchor.style.left = getScrollX()+"px";
			//$anchor.style.display = 'none';
			//$anchor.innerHTML = $newHash;
			document.body.insertBefore($anchor,document.body.firstChild);
		}
	}
	function _checkAnchorExists($name) {
		var $anchors = document.anchors;
		for (var i = 0; i < $anchors.length; i++)
			if ($anchors[i].name == $name)
				return true;
		return false;
	}
	// Keeps IE 5.0 from scrolling to the top every time a new history is entered.
	// Also retains the scroll position in the history (doesn't seem to work on IE 5.5+).
	if (typeof self.pageYOffset == "number") {
		function getScrollY() {
			return self.pageYOffset;
		}
	} else if (document.documentElement && document.documentElement.scrollTop) {
		function getScrollY() {
			return document.documentElement.scrollTop;
		}
	} else if (document.body) {
		function getScrollY() {
			return document.body.scrollTop;
		}
	}
	// clone getScrollY to getScrollX
	eval(String(getScrollY).toString().replace(/Top/g,"Left").replace(/Y/g,"X"));
	
	// stub methods, to prevent errors on unsupported browsers
	_this.addHistory = function(){};

	/**
	 * These are the platform specific interface methods. Since some platforms (most notably, IE 5.5+)
	 * require almost completely different techniques to create history entries, browser detection is
	 * used and the appropriate method is created. It would be nice to use object or feature detection
	 * here, but these workarounds deal mostly with very specific bugs and other oddities in the 
	 * various implementations. So browser sniffing it is.
	 */
	// Safari
	if (navigator.appVersion.indexOf("Safari") != -1) {
		var _windowHistoryLength = history.length,
			_historyArray = [],
			_recentlyAdded = false;
		// set initial history entry
		_historyArray[_windowHistoryLength] = location.hash;
		
		_this.addHistory = function($newHash) { // adds history and bookmark hash
			if (_currentHash != $newHash) {
				_createAnchor($newHash);
				_currentHash = $newHash;
				_setHash($newHash); // :NOTE: this doesn't update history.length right away
				_windowHistoryLength = history.length+1;
				_historyArray[_windowHistoryLength] = $newHash;
				_recentlyAdded = true;
				_this.notifyListeners("historyChange",$newHash);
			}
		};
		
		var _watchHistoryLength = function() {
			if (!_recentlyAdded) { // :NOTE: for some reason the first time this is called, it can't tell that anything has changed.
				var _historyLength = history.length;
				if (_historyLength != _windowHistoryLength) {// && _historyArray[_historyLength]
					_windowHistoryLength = _historyLength;
					
					var $newHash = _historyArray[_windowHistoryLength];
					if (_currentHash != $newHash) {
						_currentHash = $newHash;
						_this.notifyListeners("historyChange", $newHash);
					}
				}
			} else _recentlyAdded = false;
		};
		
		// since it doesn't work, might as well cancel the location.hash check (checking location.href might work)
		clearInterval(_intervalID);
		// watch the history.length prop for changes
		_intervalID = setInterval(_watchHistoryLength, _pollInterval);
		
	// IE 5.5+ Windows
	} else if (typeof ActiveXObject != "undefined" && window.print && 
			   !window.opera && navigator.userAgent.match(/MSIE (\d\.\d)/)[1] >= 5.5) {
		/* iframe references */
		var _historyFrameObj, _historyFrameRef;
		
		/*
		method: _createHistoryFrame
			
			This is for IE only for now.
		*/
		function _createHistoryFrame() {
			var $historyFrameName = "unFocusHistoryFrame";
			_historyFrameObj = document.createElement("iframe");
			_historyFrameObj.setAttribute("name", $historyFrameName);
			_historyFrameObj.setAttribute("id", $historyFrameName);
			_historyFrameObj.style.position = "absolute";
			_historyFrameObj.style.top = "-900px";
			document.body.insertBefore(_historyFrameObj,document.body.firstChild);
			// get reference to the frame from frames array (needed for document.open)
			// :NOTE: there might be an issue with this according to quirksmode.org
			// http://www.quirksmode.org/js/iframe.html
			_historyFrameRef = frames[$historyFrameName];
			
			// add base history entry
			_createHistoryHTML(_currentHash, true);
		}
		
		/*
		method: _createHistoryHTML
			This is an alternative to <_setHistoryHTML> that is used by IE (and others if I can get it to work).
			This method will create the history page completely in memory, with no need to download a new file
			from the server.
		*/
		function _createHistoryHTML($newHash) {
			_historyFrameRef.document.open("text/html");
			_historyFrameRef.document.write("<html><head></head><body onl",
				'oad="parent.unFocus.History._updateFromHistory(\''+$newHash+'\');">',
				$newHash+"</body></html>");
			_historyFrameRef.document.close();
		}
		
		/*
		method: _updateFromHistory
			A private method that is meant to be called only from HistoryFrame.html.
			It is not meant to be used by an end user even though it is accessable as public.
		*/
		_this._updateFromHistory = function() {
			// hides the first call to the method, and sets up the real method for the rest of the calls
			_this._updateFromHistory = function($hash) {
				_currentHash = $hash;
				_this.notifyListeners("historyChange", $hash);
			};
		};
		//if (navigator.userAgent.match(/MSIE (\d\.\d)/)[1] < 5.5) {
			_this.addHistory = function($newHash) {
				// do initialization stuff on first call
				_createHistoryFrame();
				
				// replace this function with a slimmer one on first call
				_this.addHistory = function($newHash) { // adds history and bookmark hash
					if (_currentHash != $newHash) {
						// IE will create an entry if there is an achor on the page, but it
						// does not allow you to detect the state change, so we skip inserting an Anchor
						_currentHash = $newHash;
						// sets hash and notifies listeners
						_createHistoryHTML($newHash);
					}
				};
				// call the first call
				_this.addHistory($newHash);
			};
			// anonymouse method - subscribe to self to update the hash when the history is updated
			_this.addEventListener("historyChange", function($hash) { _setHash($hash) });
		//} else { /* IE 5.0 */ }
		
	} else /*if (!/Safari/.test(navigator.userAgent))*/ {
		_this.addHistory = function($newHash) { // adds history and bookmark hash
			// on first call, make an anchor for the root history entry
			_createAnchor(_currentHash);
			// replace with slimmer versions...
			_this.addHistory = function($newHash) {
				if (_currentHash != $newHash) {
					_createAnchor($newHash);
					_currentHash = $newHash;
					_setHash($newHash);
					_this.notifyListeners("historyChange",$newHash);
				}
			};
			// ...do first call
			_this.addHistory($newHash);
		};
	}
}
Keeper.prototype = new unFocus.Utilities.EventManager("historyChange");

return new Keeper();

})();


//========================================================================================================
// class : EPHistoryMonitor
//
// Our top level TemplatePanel class does nothing yet!
//========================================================================================================
EPHistoryMonitor = function(elementId) {
	this.stateCounter = 0;
	this.maxState = 0;
	this.addingHistory = false;
	this.elementId = elementId;
	EP.ObjectMap.put(elementId, this); 
}

/**
 * Adds some history to the underlying history manager code
 */
EPHistoryMonitor.prototype.addHistory = function(historyHash)  {
	
	// it ends up making a call to historyListener when we add history
	// which is annoying or interesting, depending on your viewpoint.
	this.addingHistory = true;
	unFocus.History.addHistory(historyHash);
	this.addingHistory = false;
}

/**
 * Out listener to the underlying 
 */
EPHistoryMonitor.prototype.historyListener = function(historyHash) {
	if (this.addingHistory) {
		return;
	}
   	this.raiseAction(historyHash,true);
};

/**
 * Called to raise an action back to the server
 */
EPHistoryMonitor.prototype.raiseAction = function(historyHash) {
	var actionName = "history";
	var actionValue = ""+historyHash;
    EchoClientMessage.setActionValue(this.elementId, actionName, actionValue);
    EchoServerTransaction.connect();
}

/**
 * EPHistoryMonitor has a ServerMessage processor.
 */
EPHistoryMonitor.MessageProcessor = function() { };

EPHistoryMonitor.MessageProcessor.process = function(messagePartElement) {
	//debugger;
    for (var i = 0; i < messagePartElement.childNodes.length; ++i) {
        if (messagePartElement.childNodes[i].nodeType == 1) {
            switch (messagePartElement.childNodes[i].tagName) {
            case "init":
            	EPHistoryMonitor.MessageProcessor.processInit(messagePartElement.childNodes[i]);
                break;
            case "addHistory":
            	EPHistoryMonitor.MessageProcessor.processAddHistory(messagePartElement.childNodes[i]);
                break;
            case "dispose":
            	EPHistoryMonitor.MessageProcessor.processDispose(messagePartElement.childNodes[i]);
                break;
            }
        }
    }
};

EPHistoryMonitor.MessageProcessor.processDispose = function(disposeMessageElement) {
	//debugger;
	for (var item = disposeMessageElement.firstChild; item; item = item.nextSibling) {
        var elementId = item.getAttribute("eid");
		EP.ObjectMap.destroy(elementId);
    }
};

EPHistoryMonitor.MessageProcessor.processInit = function(initMessageElement) {
	//debugger;
    for (var item = initMessageElement.firstChild; item; item = item.nextSibling) {
        var elementId = item.getAttribute("eid");
		EP.ObjectMap.destroy(elementId);
		
		var historyMonitor = new EPHistoryMonitor(elementId);
		var eventCallback = function(historyHash) {
			historyMonitor.historyListener(historyHash);
		}
		unFocus.History.addEventListener('historyChange', eventCallback);
		var currentState = unFocus.History.getCurrent();
    }
};

/**
 * Called to add history state to the underlying history manager code.
 */
EPHistoryMonitor.MessageProcessor.processAddHistory = function(messageElement) {
	//debugger;
    for (var item = messageElement.firstChild; item; item = item.nextSibling) {
        var elementId = item.getAttribute("eid");
		var historyMonitor = EP.ObjectMap.get(elementId);
		
	    for (var historyElement = item.firstChild; historyElement; historyElement = historyElement.nextSibling) {
	    	var historyHash = historyElement.getAttribute('historyHash');
	    	if (historyHash) {
		    	historyMonitor.addHistory(historyHash);
	    	}
	    }
    }
};

