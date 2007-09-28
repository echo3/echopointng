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

/*
 * This file was take from the Echo2 project and hence is
 * Copyright (C) 2002-2005 NextApp, Inc.  It was made part of the
 * EchoPoint on 16/5/05.  We dont use the base button JS because it
 * doesnt quite work for us.
 */

/* 
 * This file is part of the Echo Web Application Framework (hereinafter "Echo").
 * Copyright (C) 2002-2005 NextApp, Inc.
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

//__________________
// Object EPButton

EPButton = function(elementId) { 
	this.elementId = elementId;
	EP.ObjectMap.put(elementId, this); 
};

/**
 * EPButton has a ServerMessage processor.
 */
EPButton.MessageProcessor = function() {};

EPButton.MessageProcessor.process = function(messagePartElement) {
    for (var i = 0; i < messagePartElement.childNodes.length; ++i) {
        if (messagePartElement.childNodes[i].nodeType == 1) {
            switch (messagePartElement.childNodes[i].tagName) {
            case "init":
                EPButton.MessageProcessor.init(messagePartElement.childNodes[i]);
                break;
            case "dispose":
                EPButton.MessageProcessor.dispose(messagePartElement.childNodes[i]);
                break;
           }
        }
    }
};

EPButton.MessageProcessor.init = function(messageElement) {
	for (var itemXML = messageElement.firstChild; itemXML; itemXML = itemXML.nextSibling) {
	    var elementId = itemXML.getAttribute("eid");
    	var buttonObj = new EPButton(elementId);
    	buttonObj.init(itemXML);
	}
};

EPButton.MessageProcessor.dispose = function(messageElement) {
	for (var itemXML = messageElement.firstChild; itemXML; itemXML = itemXML.nextSibling) {
	    var elementId = itemXML.getAttribute("eid");
		EP.ObjectMap.destroy(elementId);
	}
};

EPButton.prototype.init = function(itemXML) {
	var buttonE = document.getElementById(this.elementId);
	
	// a bug on IE prevents the type='' attribute from being set properly
	// so we "re-create" the button but with type
	var typeValue = EP.DOM.getAttr(itemXML,'type','button');
	if (document.all && typeValue != 'button') {
		// we recreate the button using the same HTML but with type='xxx' in it
		// This only works and only needs to be done on IE (of course)
		var innerHTML = buttonE.innerHTML;
		var parts = buttonE.outerHTML.split('>');
		var xhtml = '';
		for (i = 0; i < parts.length; i++) {
			if (parts[i]) {
				xhtml += parts[i];
				if (i == 0) {
					xhtml += ' type="' + typeValue + '"';
				}
				xhtml += '>';
			}
		}
		// new button creation, as a clone
		var newButtonE = document.createElement(xhtml);
		newButtonE.innerHTML = innerHTML;
		
		var parentE = buttonE.parentNode;
		var nextsiblingE = buttonE.nextSibling;
		parentE.removeChild(buttonE);
		parentE.insertBefore(newButtonE,nextsiblingE);
		buttonE = newButtonE;
	}
	
	this.serverNotify = EP.DOM.getBooleanAttr(itemXML,'serverNotify',true);
    if (! this.serverNotify) {
        EchoDomPropertyStore.setPropertyValue(this.elementId, "serverNotify", false);
    }
	this.enabled = EP.DOM.getBooleanAttr(itemXML,'enabled',true);
    if (! this.enabled) {
        EchoDomPropertyStore.setPropertyValue(this.elementId, "EchoClientEngine.inputDisabled", true);
    }

	// add event handlers required
	if (EP.DOM.getBooleanAttr(itemXML,'event-click',false)) {
		this.addHandler('click',buttonE);		
		this.addHandler('keypress',buttonE);		
	}
	if (EP.DOM.getBooleanAttr(itemXML,'event-mousedown',false)) {
		this.addHandler('mousedown',buttonE);		
	}
	if (EP.DOM.getBooleanAttr(itemXML,'event-mouseup',false)) {
		this.addHandler('mouseup',buttonE);		
	}
	if (EP.DOM.getBooleanAttr(itemXML,'event-mouseover',false)) {
		this.addHandler('mouseover',buttonE);		
	}
	if (EP.DOM.getBooleanAttr(itemXML,'event-mouseout',false)) {
		this.addHandler('mouseout',buttonE);		
	}
};

EPButton.prototype.destroy = function(itemXML) {
	var buttonE = document.getElementById(this.elementId);
	for (x in this.eventHandlers) {
		var eventType = this.eventHandlers[x];
		EP.Event.removeHandler(eventType,buttonE);
	}
};

EPButton.prototype.addHandler = function(eventType, buttonE) {
	if (! this.eventHandlers) {
		this.eventHandlers = [];
	}
	this.eventHandlers[this.eventHandlers.length] = eventType;
	EP.Event.addHandler(eventType,buttonE,this);
};

EPButton.prototype.eventHandler = function(echoEvent) {
	try {
		if (echoEvent.type == 'click') {
			EPButton.doAction(echoEvent);
		}
		if (echoEvent.type == 'keypress') {
			// react to Enter and Space
			var kc = echoEvent.keyCode;
			if (kc == 13 || kc == 32 || echoEvent.charCode == 32) {
				EPButton.doAction(echoEvent);
			}
		}
		
		if (echoEvent.type == 'mousedown') {
			EPButton.doPressed(echoEvent);
		}
		if (echoEvent.type == 'mouseup') {
			EPButton.doReleased(echoEvent);
		}
		if (echoEvent.type == 'mouseover') {
			EPButton.doRolloverEnter(echoEvent);
		}
		if (echoEvent.type == 'mouseout') {
			EPButton.doRolloverExit(echoEvent);
		}
	} catch (ex) {
		throw ex;
	}
};

EPButton.STATE_DEFAULT = 0;
EPButton.STATE_ROLLOVER = 1;
EPButton.STATE_PRESSED = 2;

EPButton.setIcon = function(elementId, newIconUri) {
    var iconElement = document.getElementById(elementId + "_icon");
    iconElement.src = newIconUri;
};

EPButton.updateVisualState = function(buttonElement, newState) {
    // Prevent TextNode events.
    if (buttonElement.nodeType != 1) {
        return;
    }
    var newStyle, newIcon;
    switch (newState) {
    case EPButton.STATE_ROLLOVER:
        newStyle = EchoDomPropertyStore.getPropertyValue(buttonElement.id, "rolloverStyle");
        newIcon = EchoDomPropertyStore.getPropertyValue(buttonElement.id, "rolloverIcon");
        break;
    case EPButton.STATE_PRESSED:
        newStyle = EchoDomPropertyStore.getPropertyValue(buttonElement.id, "pressedStyle");
        newIcon = EchoDomPropertyStore.getPropertyValue(buttonElement.id, "pressedIcon");
        break;
    default:
        newIcon = EchoDomPropertyStore.getPropertyValue(buttonElement.id, "defaultIcon");
    }
    EP.applyStyle(buttonElement);
    if (newStyle) {
       	EP.applyStyle(buttonElement, newStyle);
    }
    if (newIcon) {
    	EPButton.setIcon(buttonElement.id, newIcon);
    }
};


EPButton.doAction = function(echoEvent) {
    EchoDomUtil.preventEventDefault(echoEvent);
    var elementId = echoEvent.registeredTarget.getAttribute("id");
	if (! EchoClientEngine.verifyInput(elementId)) {
        return;
    }	
    if ("false" == EchoDomPropertyStore.getPropertyValue(elementId, "serverNotify")) {
        return;
    }
    if (document.selection && document.selection.empty) {
        document.selection.empty();
    }
    // capture the click
    var metaKeys = 0;
    if (echoEvent.altKey) {
    	metaKeys |= 1;
    }
    if (echoEvent.shiftKey) {
    	metaKeys |= 2;
    }
    if (echoEvent.ctrlKey) {
    	metaKeys |= 4;
    }
    if (echoEvent.metaKey) {
    	metaKeys |= 8;
    }
    EchoClientMessage.setActionValue(elementId, "click",metaKeys);
    EchoServerTransaction.connect();
};

EPButton.doPressed = function(echoEvent) {
    EchoDomUtil.preventEventDefault(echoEvent);
    var elementId = echoEvent.registeredTarget.getAttribute("id");
	if (! EchoClientEngine.verifyInput(elementId)) {
        return;
    }	
    if (document.selection && document.selection.empty) {
        document.selection.empty();
    }
    var eventTarget = echoEvent.registeredTarget;
    EPButton.updateVisualState(eventTarget, EPButton.STATE_PRESSED);
};

EPButton.doReleased = function(echoEvent) {
    EchoDomUtil.preventEventDefault(echoEvent);
	EPButton.updateVisualState(echoEvent.registeredTarget, EPButton.STATE_ROLLOVER);
};

EPButton.doRolloverEnter = function(echoEvent) {
    var elementId = echoEvent.registeredTarget.getAttribute("id");
	if (! EchoClientEngine.verifyInput(elementId)) {
        return;
    }	
    EPButton.updateVisualState(echoEvent.registeredTarget, EPButton.STATE_ROLLOVER);
};

EPButton.doRolloverExit = function(echoEvent) {
    EPButton.updateVisualState(echoEvent.registeredTarget, EPButton.STATE_DEFAULT);
};
