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

//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
// Our EPKeyStroke object.
//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
EPKeyStroke = function(elementId) { 
	this.elementId = elementId;
	this.lastKeyCode = 0;
	EP.ObjectMap.put(elementId, this); 
};

EPKeyStroke.ALT_MASK = 0x1000;
EPKeyStroke.CONTROL_MASK = 0x2000;
EPKeyStroke.SHIFT_MASK = 0x4000;
EPKeyStroke.META_MASK = 0x8000;

/**
 * EPKeyStroke has a ServerMessage processor.
 */
EPKeyStroke.MessageProcessor = function() { };

EPKeyStroke.MessageProcessor.process = function(messagePartElement) {
    for (var i = 0; i < messagePartElement.childNodes.length; ++i) {
        if (messagePartElement.childNodes[i].nodeType == 1) {
            switch (messagePartElement.childNodes[i].tagName) {
	            case "init":
	                EPKeyStroke.MessageProcessor.processInit(messagePartElement.childNodes[i]);
	                break;
	            case "dispose":
	                EPKeyStroke.MessageProcessor.processDispose(messagePartElement.childNodes[i]);
	                break;
            }
        }
    }
};

EPKeyStroke.MessageProcessor.processDispose = function(disposeMessageElement) {
	//debugger;
    for (var item = disposeMessageElement.firstChild; item; item = item.nextSibling) {
        var elementId = item.getAttribute("eid");
		EP.ObjectMap.destroy(elementId);
    }
};

EPKeyStroke.prototype.destroy = function() {
	var targetE = document.getElementById(this.targetid)
	if (this.serverNotify) {
		if (targetE) {
			EchoDomUtil.removeEventListener(targetE, "keydown", this.eventHandlerWrapper, false);
			EchoDomUtil.removeEventListener(targetE, "keyup",   this.eventHandlerWrapper, false);
		} else {
			var handleName = "epks"+this.elementId;
			EP.DocumentEvent.removeHandler("keydown",handleName);
			EP.DocumentEvent.removeHandler("keyup",handleName);
		}
	}
}


EPKeyStroke.MessageProcessor.processInit = function(initMessageElement) {
	//debugger;
    for (var itemXML = initMessageElement.firstChild; itemXML; itemXML = itemXML.nextSibling) {
        var elementId = itemXML.getAttribute("eid");
		EP.ObjectMap.destroy(elementId);
		
		var ks = new EPKeyStroke(elementId);
		ks.init(itemXML);
     }
};

/**
 * Intialises the KeyStrokeListener from the message XML
 */
EPKeyStroke.prototype.init = function(itemXML) {
	//debugger;
	if (itemXML.getAttribute("enabled") == "false") {
        EchoDomPropertyStore.setPropertyValue(this.elementId, "EchoClientEngine.inputDisabled", true);
    }
	this.keyCombinations = itemXML.getAttribute("keyCombinations").split("|");
	this.serverNotify = itemXML.getAttribute("serverNotify") == "true";
	this.cancelMode = itemXML.getAttribute("cancelMode") == "true";
	this.targetid = itemXML.getAttribute("targetid");
	if (this.serverNotify) {
		var ks = this;
    	this.eventHandlerWrapper = function(echoEvent) {
    		ks.eventHandler(echoEvent);
    	};
		var targetE = document.getElementById(this.targetid)
		if (targetE) {
			EchoDomUtil.addEventListener(targetE, "keydown", this.eventHandlerWrapper, false);
			EchoDomUtil.addEventListener(targetE, "keyup",   this.eventHandlerWrapper, false);
		} else {
		    var handleName = "epks"+this.elementId;
		    EP.DocumentEvent.addHandler("keydown", handleName,this.eventHandlerWrapper);
		    EP.DocumentEvent.addHandler("keyup",   handleName,this.eventHandlerWrapper);
		}
	}	
};

/**
 * Applies the given set of masks to the keyCode if the echoevent
 * actually has the given shit/control/alt combination in effect
 * at the time of the keystroke
 */
EPKeyStroke.prototype.applyMask = function(echoEvent, keyCode, keyMask) {
	if (((keyMask & EPKeyStroke.SHIFT_MASK)  == EPKeyStroke.SHIFT_MASK) && echoEvent.shiftKey) {
		keyCode = EPKeyStroke.SHIFT_MASK | keyCode; 
	}
	if (((keyMask & EPKeyStroke.CONTROL_MASK)  == EPKeyStroke.CONTROL_MASK) && echoEvent.ctrlKey) {
		keyCode = EPKeyStroke.CONTROL_MASK | keyCode; 
	}
	if (((keyMask & EPKeyStroke.ALT_MASK)  == EPKeyStroke.ALT_MASK) && echoEvent.altKey) {
		keyCode = EPKeyStroke.ALT_MASK | keyCode; 
	}
	return keyCode;
};

/**
 * Returns true if the given key combo is handled
 */
EPKeyStroke.prototype.hasKeyCombo = function(keyCode) {
    for (x in this.keyCombinations) {
    	var testKey = this.keyCombinations[x];
    	if (testKey == keyCode) {
    		return true;
    	}
    }
    return false;
};

/**
 * An event handler for key strokes
 */
EPKeyStroke.prototype.eventHandler = function(echoEvent) {
	//debugger;
	if (! EchoClientEngine.verifyInput(this.elementId)) {
        return;
    }	
    if (! this.serverNotify) {
        return;
    }
    var doAction = false;
    var keyCode = echoEvent.keyCode;
    if (echoEvent.type == 'keydown') {
    	keyCode = this.applyMask(echoEvent,keyCode,EPKeyStroke.SHIFT_MASK | EPKeyStroke.CONTROL_MASK | EPKeyStroke.ALT_MASK);
    	doAction = this.hasKeyCombo(keyCode);
    	if (doAction) {
    		this.lastKeyCode = keyCode;
    	}
    }
    if (echoEvent.type == 'keyup') {
    	// we only handle shift / control / alt on key up and then only if the last keyCode handled
    	// did not involve some shift / control / alt
    	if (keyCode == 16 || keyCode == 17 || keyCode == 18) {
        	var doKeyTest = false;
    		if (keyCode == 16) {
    	    	if ((this.lastKeyCode & EPKeyStroke.SHIFT_MASK) != EPKeyStroke.SHIFT_MASK) {
        	    	keyCode = this.applyMask(echoEvent,keyCode,EPKeyStroke.CONTROL_MASK | EPKeyStroke.ALT_MASK);
    	    		doKeyTest = true;
    	    	}
    	    	this.lastKeyCode = this.lastKeyCode && (~ EPKeyStroke.SHIFT_MASK);
    		}
    		if (keyCode == 17) {
    	    	if ((this.lastKeyCode & EPKeyStroke.CONTROL_MASK) != EPKeyStroke.CONTROL_MASK) {
    	    		keyCode = this.applyMask(echoEvent,keyCode,EPKeyStroke.SHIFT_MASK | EPKeyStroke.ALT_MASK);
    	    		doKeyTest = true;
    	    	}
    	    	this.lastKeyCode = this.lastKeyCode && (~ EPKeyStroke.CONTROL_MASK);
    		}
    		if (keyCode == 18) {
    	    	if ((this.lastKeyCode & EPKeyStroke.ALT_MASK) != EPKeyStroke.ALT_MASK) {
    	    		keyCode = this.applyMask(echoEvent,keyCode,EPKeyStroke.SHIFT_MASK | EPKeyStroke.CONTROL_MASK);
    	    		doKeyTest = true;
    	    	}
    	    	this.lastKeyCode = this.lastKeyCode && (~ EPKeyStroke.ALT_MASK);
    		}
    		if (doKeyTest) {
        		this.lastKeyCode = 0;
            	doAction = this.hasKeyCombo(keyCode);
    		}
    	}
    }
    
    if (doAction) {
    	//debugger;
    	if (this.cancelMode) {
    		EP.Event.cancelEvent(echoEvent);
    	}
    	EchoClientMessage.setActionValue(this.elementId, "keystroke",''+keyCode);
    	//
    	// we make the AJAX request asynch so that the keystroke can travel
    	// through the keyboard system and go do its rightful target
    	window.setTimeout("EchoServerTransaction.connect()",150);
    }
};

