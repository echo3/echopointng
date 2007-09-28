
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
// class : EPNativeWindow
//
//========================================================================================================
EPNativeWindow = function(elementId) {
	this.url = "";
	this.dependent = false;
	this.modal = false;
	this.windowRef = null;
	this.timerId = null;
	this.features = 0;
	this.top = 0;
	this.left = 0;
	this.centred = true;
	this.elementId = elementId;
	EP.ObjectMap.put(elementId, this); 
};

EPNativeWindow.prototype.destroy = function() {
	if (this.dependent) {
		this.closeWindow();
	}
};


/**
 * Called to raise an action back to the server
 */
EPNativeWindow.prototype.raiseAction = function(historyHash) {
	var actionName = "closed";
	var actionValue = "";
    EchoClientMessage.setActionValue(this.elementId, actionName, actionValue);
    EchoServerTransaction.connect();
};

EPNativeWindow.prototype.isWindowClosed = function() {
	var isClosed = true;
	if (this.windowRef) {
		try {
			isClosed = this.windowRef.closed;
		} catch (e) {
		}
	}
	return isClosed;
};

EPNativeWindow.prototype.centerWindow = function() {
	// TODO - implement NativeWindow centering support!
	alert('TODO - add centering support')
};

EPNativeWindow.prototype.closeWindow = function() {
	this.open = false;
	if (this.timerId) {
		window.clearInterval(this.timerId);
		this.timerId = null;
	}
	
	// close the actual window
	if (! this.isWindowClosed()) {
		var openerRef = this.windowRef.opener;
		this.windowRef.close();
		try {
			openerRef.focus();
		} catch (e) {
		}
	}
	
	// clear the modality
	if (this.modal) {
		this.clearModality();
	}
};

EPNativeWindow.FEATURE_LOCATION = 1;
EPNativeWindow.FEATURE_SCROLLBARS = 2;
EPNativeWindow.FEATURE_STATUSBAR = 4;
EPNativeWindow.FEATURE_TOOLBAR = 8;
EPNativeWindow.FEATURE_RESIZEABLE = 16;
EPNativeWindow.FEATURE_MENUBAR = 32;

EPNativeWindow.prototype.openWindow = function() {
	//debugger;
	if (! this.isWindowClosed()) {
		return;
	}
	// close an previous window just in case
	this.closeWindow();
	//
	// build features - TODO - build this for real!
	var features = "";
	if ((this.features & EPNativeWindow.FEATURE_LOCATION) == EPNativeWindow.FEATURE_LOCATION) {
		features += ",location=yes"
	} else {
		features += ",location=no"
	}
	if ((this.features & EPNativeWindow.FEATURE_SCROLLBARS) == EPNativeWindow.FEATURE_SCROLLBARS) {
		features += ",scrollbars=yes"
	} else {
		features += ",scrollbars=no"
	}
	if ((this.features & EPNativeWindow.FEATURE_TOOLBAR) == EPNativeWindow.FEATURE_TOOLBAR) {
		features += ",toolbar=yes"
	} else {
		features += ",toolbar=no"
	}
	if ((this.features & EPNativeWindow.FEATURE_STATUSBAR) == EPNativeWindow.FEATURE_STATUSBAR) {
		features += ",status=yes"
	} else {
		features += ",status=no"
	}
	if ((this.features & EPNativeWindow.FEATURE_MENUBAR) == EPNativeWindow.FEATURE_MENUBAR) {
		features += ",menubar=yes"
	} else {
		features += ",menubar=no"
	}
	if ((this.features & EPNativeWindow.FEATURE_RESIZEABLE) == EPNativeWindow.FEATURE_RESIZEABLE) {
		features += ",resizeable=yes"
	} else {
		features += ",resizeable=yes"
	}
	if (this.dependent) {
		features += ",dependent=yes"
	}
	if (features.indexOf(",") == 0) {
		features = features.substring(1);
	}
	// reset all for case where they dont specifiy anything!
	if (this.features == 0 && ! this.dependent) {
		features = null;
	}
	//
	// open a new window
	try {
		this.windowRef = window.open(this.url,this.elementId, features);
	} catch (e) {
	}
	
	// did it work
	//debugger;
	if (this.isWindowClosed()) {
		alert('A NativeWindow could not be opened.  Perhaps a popup blocker is active');
	    EchoClientMessage.setActionValue(this.elementId, 'open', 'false');
    	EchoServerTransaction.connect();
		return;
	} 
	// start a timer to check if the window is closed
	var that = this;
	var timerClosure = function() {
		that.onTimer();
	}
	this.timerId = window.setInterval(timerClosure,300);
	this.open = true;
	
	// position the window now
	this.moveTo();
	if (this.height && this.width) {
		this.resizeTo();
	}
	
	// add modal support to NativeWindow
	if (this.modal) {
		this.showModality();
	}
};

EPNativeWindow.prototype.moveTo = function() {
	try {
		if (this.left && this.top) {
			this.windowRef.moveTo(this.left,this.top);
		}
	} catch (e) {
	}
};

EPNativeWindow.prototype.resizeTo = function() {
	try {
		if (this.height && this.width) {
			this.windowRef.resizeTo(this.left,this.top);
		}
	} catch (e) {
	}
};

EPNativeWindow.prototype.onTimer = function() {
	var isClosed = this.isWindowClosed();
	if (isClosed) {
		// clean up for good
		this.closeWindow();
	    EchoClientMessage.setActionValue(this.elementId, 'open', 'false');
    	EchoServerTransaction.connect();
	}
};

EPNativeWindow.prototype.showModality = function() {
	//debugger;
	// make the current content protected by an invisible
	var modalDivE = document.createElement('div');
	modalDivE.id = this.elementId + 'Modal';
	modalDivE.style.position = 'absolute';
	modalDivE.style.left = '0px';  
	modalDivE.style.top = '0px';
	modalDivE.style.backgroundColor = 'transparent';
	modalDivE.style.zindex = 20000;
	
	modalDivE.innerHTML = '<table border="0" style="padding : 0xp; margin : 0xp; height : 100%; width : 100%;"><tbody><tr><td></td></tr></tbody></table>';
	
	var screen = EP.getPageDimensions();
	modalDivE.style.width = '100%';
	if (document.all) {	
		modalDivE.style.width = screen[0] + 'px';
	}
	modalDivE.style.height = screen[1] + 'px';
	document.body.appendChild(modalDivE);
	this.modalDivE = modalDivE;
	
	var that = this;
	var docHandler = function(echoEvent) {
		if (! that.isWindowClosed()) {
			that.windowRef.focus();
		}
	};	
	EP.DocumentEvent.addHandler('mousedown',this.elementId,docHandler);
	EP.DocumentEvent.addHandler('focus',this.elementId,docHandler);
	EP.DocumentEvent.addHandler('keydown',this.elementId,docHandler);
};


EPNativeWindow.prototype.clearModality = function() {
	var modalDivE = document.getElementById(this.elementId + 'Modal');
	if (modalDivE) {
		modalDivE.parentNode.removeChild(modalDivE);
		EP.DocumentEvent.removeHandler('mousedown',this.elementId);
		EP.DocumentEvent.removeHandler('focus',this.elementId);
		EP.DocumentEvent.removeHandler('keydown',this.elementId);
	}
};


/**
 * EPNativeWindow has a ServerMessage processor.
 */
EPNativeWindow.MessageProcessor = function() { };

EPNativeWindow.MessageProcessor.process = function(messagePartElement) {
	//debugger;
    for (var i = 0; i < messagePartElement.childNodes.length; ++i) {
        if (messagePartElement.childNodes[i].nodeType == 1) {
            switch (messagePartElement.childNodes[i].tagName) {
            case "init":
            	EPNativeWindow.MessageProcessor.processInit(messagePartElement.childNodes[i]);
                break;
            case "update":
            	EPNativeWindow.MessageProcessor.processUpdate(messagePartElement.childNodes[i]);
                break;
            case "dispose":
            	EPNativeWindow.MessageProcessor.processDispose(messagePartElement.childNodes[i]);
                break;
            }
        }
    }
};

EPNativeWindow.MessageProcessor.processDispose = function(disposeMessageElement) {
	//debugger;
	for (var item = disposeMessageElement.firstChild; item; item = item.nextSibling) {
        var elementId = item.getAttribute("eid");
		var nativeWindow = EP.ObjectMap.get(elementId);
		if (nativeWindow.dependent) {
			nativeWindow.closeWindow();
		}
		if (nativeWindow.timerId) {
			window.clearTimeout(nativeWindow.timerId);
			nativeWindow.timerId = null;
		}
		nativeWindow.windowRef = null;
		EP.ObjectMap.destroy(elementId);
    }
};

EPNativeWindow.MessageProcessor.processInit = function(initMessageElement) {
	//debugger;
    for (var item = initMessageElement.firstChild; item; item = item.nextSibling) {
        var elementId = item.getAttribute("eid");
		EP.ObjectMap.destroy(elementId);
		
		var nativeWindow       = new EPNativeWindow(elementId);
		nativeWindow.dependent = item.getAttribute("dependent") == "true";
		nativeWindow.modal     = item.getAttribute("modal") == "true";
		nativeWindow.open      = item.getAttribute("open") == "true";
		nativeWindow.url       = item.getAttribute("url");
		nativeWindow.features  = parseInt(item.getAttribute("features"),10);

		if (item.getAttribute("left")) {
			nativeWindow.left  = parseInt(item.getAttribute("left"),10);
		}		
		if (item.getAttribute("top")) {
			nativeWindow.top = parseInt(item.getAttribute("top"),10);
		}		
		if (item.getAttribute("height")) {
			nativeWindow.height  = parseInt(item.getAttribute("height"),10);
		}		
		if (item.getAttribute("width")) {
			nativeWindow.width  = parseInt(item.getAttribute("width"),10);
		}		
		
		if (nativeWindow.open) {
			nativeWindow.openWindow();
		}
    }
};

/**
 * We can get updates to most of our properties at any time
 */
EPNativeWindow.MessageProcessor.processUpdate = function(messageElement) {
	//debugger;
    for (var item = messageElement.firstChild; item; item = item.nextSibling) {
        var elementId = item.getAttribute("eid");
		var nativeWindow = EP.ObjectMap.get(elementId);
		// what property is changing
		if (item.getAttribute("url")) {
			nativeWindow.url = item.getAttribute("url");
		}
		if (item.getAttribute("modal")) {
			nativeWindow.modal = item.getAttribute("modal") == "true";
		}
		if (item.getAttribute("dependent")) {
			nativeWindow.dependent = item.getAttribute("dependent") == "true";
		}
		if (item.getAttribute("centered")) {
			nativeWindow.centered = item.getAttribute("centered") == "true";
		}
		if (item.getAttribute("features")) {
			nativeWindow.features  = parseInt(item.getAttribute("features"),10);
		}
		// dimension related
		if (item.getAttribute("left")) {
			nativeWindow.left  = parseInt(item.getAttribute("left"),10);
		}		
		if (item.getAttribute("top")) {
			nativeWindow.top = parseInt(item.getAttribute("top"),10);
		}		
		if (item.getAttribute("height")) {
			nativeWindow.height  = parseInt(item.getAttribute("height"),10);
		}		
		if (item.getAttribute("width")) {
			nativeWindow.width  = parseInt(item.getAttribute("width"),10);
		}		
		
		// now are we changing open state?
		var openPending = false;
		if (item.getAttribute("open")) {
			var newOpen      = item.getAttribute("open") == "true";
			if (! newOpen) {
				nativeWindow.closeWindow();
			}
			if (newOpen && nativeWindow.isWindowClosed()) {
				openPending = true;
			}
		}
		//
		// If we have a pending open operation then we can combine
		// all the changes into one open call.  Other wise we can only actually
		// change some of them dynamically like URL and position based ones
		// others will take effect at a latter time.
		var isCurrentlyClosed = nativeWindow.isWindowClosed();
		if (! openPending) {
			if (item.getAttribute("url")) {
				if (! isCurrentlyClosed) {
					nativeWindow.windowRef.location.replace(this.url);
				}
			}
			if (! isCurrentlyClosed) {
				//
				// centering overrrides all positionable parameters
				if (! nativeWindow.centered) {
					if (item.getAttribute('left') || item.getAttribute("top")) {
						nativeWindow.moveTo();					
					}
					if (item.getAttribute('height') || item.getAttribute("width")) {
						nativeWindow.resizeTo();					
					}
				} else {
					nativeWindow.centerWindow();
				}
			}
		}
		if (openPending) {
			nativeWindow.openWindow();
		}
	}
};

