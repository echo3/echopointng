/*
 * of Components that have extended the Echo Web Application Framework.
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

//-------------------------------------------
// The EPPU object is used to hold properties
// for a pop up control. A pop up consists
// target area, a toggle image and a hidden pop up box
// that is show/hidden when the toggle image is
// clicked.
//
//-------------------------------------------
EPPU = function(elementId) {
	this.elementId = elementId;
	this.parent = null;
	this.children = [];
	EP.ObjectMap.put(elementId,this);
};

// we can have at most 1 pop up box item current at a time
EPPU.currentObj = null;
EPPU.asynchMouseOutId = null;
EPPU.asynchMouseOverId = null;
EPPU.asynchShowBoxId = null;


/**
 * EPPU has a ServerMessage processor.
 */
EPPU.MessageProcessor = function() { };

EPPU.MessageProcessor.process = function(messagePartElement) {

    for (var i = 0; i < messagePartElement.childNodes.length; ++i) {
        if (messagePartElement.childNodes[i].nodeType == 1) {
            switch (messagePartElement.childNodes[i].tagName) {
            case "init":
                EPPU.MessageProcessor.processInit(messagePartElement.childNodes[i]);
                break;
            case "expansion":
                EPPU.MessageProcessor.processExpansion(messagePartElement.childNodes[i]);
                break;
            case "dispose":
                EPPU.MessageProcessor.processDispose(messagePartElement.childNodes[i]);
                break;
            }
        }
    }
};

EPPU.MessageProcessor.processExpansion = function(expansionMessageElement) {
    for (var item = expansionMessageElement.firstChild; item; item = item.nextSibling) {
        var elementId = item.getAttribute("eid");
        var expanded  = item.getAttribute("expanded") == "true";

		var pu = EP.ObjectMap.get(elementId);
		if (expanded) {
			pu.showBox(false); // no property change
		} else {
			pu.hideBoxesDown(false); // no property change
		}
    }
};

EPPU.MessageProcessor.processDispose = function(disposeMessageElement) {
    for (var item = disposeMessageElement.firstChild; item; item = item.nextSibling) {
    	var targetElem = null;
        var elementId = item.getAttribute("eid");
        EP.ObjectMap.destroy(elementId);
    	targetElem = document.getElementById(elementId + '|IframeQuirk');
    	if (targetElem) {
    		targetElem.parentNode.removeChild(targetElem);
    	}
    }
};

//-------------------------------------------
//Our deconstructor
//-------------------------------------------
EPPU.prototype.destroy = function() {
	if (this.parent) {
		this.parent.removeChild(this);
	}
	var targetElem;
	var keyTargetId = this.keyTargetId;
    if (keyTargetId) {
    	targetElem = document.getElementById(keyTargetId);
    	if (targetElem) {
    		EP.Event.removeHandler("keydown", targetElem);
    		EP.Event.removeHandler("click", targetElem);
    	}
    }
    
	var popupBoxId = this.elementId + '|Box';
	targetElem = document.getElementById(popupBoxId);
	if (targetElem) {
        EP.Event.removeStaticHandler(popupBoxId, "click");
	}
	 
    var toggleId = this.elementId + '|Toggle';
	targetElem = document.getElementById(toggleId);
	if (targetElem) {
		EP.Event.removeStaticHandler(toggleId, "click");
		EP.Event.removeStaticHandler(toggleId, "mouseout");
		EP.Event.removeStaticHandler(toggleId, "mouseover");
		EP.Event.removeStaticHandler(toggleId, "mousedown");
		EP.Event.removeStaticHandler(toggleId, "mouseup");
	}

    var targetId = this.elementId + '|Target';
	targetElem = document.getElementById(targetId);
	if (targetElem) {
		EP.Event.removeStaticHandler(targetId, "mouseover");
		EP.Event.removeStaticHandler(targetId, "mouseout");
	}
	
    var outerId = this.elementId + '|Outer';
	targetElem = document.getElementById(outerId);
	if (targetElem) {
		EP.Event.removeStaticHandler(outerId, "mouseout");
		EP.Event.removeStaticHandler(outerId, "mouseover");
	}
};


EPPU.MessageProcessor.processInit = function(initMessageElement) {
	//debugger;
    for (var item = initMessageElement.firstChild; item; item = item.nextSibling) {
        var elementId = item.getAttribute("eid");
		EP.ObjectMap.destroy(elementId);
		//
        // init an EPPU object
        var pu = new EPPU(elementId);
    	pu.level = null;
        pu.elementId = elementId;
        
        if (item.getAttribute("enabled") == "false") {
            EchoDomPropertyStore.setPropertyValue(elementId, "EchoClientEngine.inputDisabled", true);
        }
    	pu.defaultStyle = item.getAttribute("defaultStyle");
    	pu.rolloverStyle = item.getAttribute("rolloverStyle");

    	pu.toggleDefaultStyle = item.getAttribute("toggleDefaultStyle");
    	pu.toggleDefaultIcon = item.getAttribute("toggleDefaultIcon");

    	pu.toggleRolloverStyle = item.getAttribute("toggleRolloverStyle");
    	pu.toggleRolloverIcon = item.getAttribute("toggleRolloverIcon");

    	pu.togglePressedStyle = item.getAttribute("togglePressedStyle");
    	pu.togglePressedIcon = item.getAttribute("togglePressedIcon");

    	pu.targetDefaultStyle = item.getAttribute("targetDefaultStyle");
    	pu.targetRolloverStyle = item.getAttribute("targetRolloverStyle");

    	var num = parseInt(item.getAttribute("leftOffset"),10);
   		pu.leftOffset = isNaN(num) ? 0 : num;
    	num = parseInt(item.getAttribute("topOffset"),10);
   		pu.topOffset = isNaN(num) ? 0 : num;

   		num = parseInt(item.getAttribute("hAlign"),10);
   		pu.hAlign = isNaN(num) ? EP.RIGHT : num;
    	num = parseInt(item.getAttribute("vAlign"),10);
   		pu.vAlign = isNaN(num) ? EP.TOP : num;
   		
   		pu.nextToToggle = item.getAttribute("nextToToggle") == "true";
   	 	pu.onRolloverShowBox = item.getAttribute("onRolloverShowBox") == "true";
  		pu.popUpAlwaysOnTop = item.getAttribute("popUpAlwaysOnTop") == "true";
  		 
    	pu.showBoxTimeOut = 100;

        var parentId = item.getAttribute("parentId");
        var parentObj = EP.ObjectMap.get(parentId);
        if (parentObj) {
        	parentObj.addChild(pu);
        }
        
        //
        // we want to know about keys and clicks on the target component
        pu.keyTargetId = item.getAttribute("keyTargetId");
        pu.addKeyTargetEvents();
        
        //
        // we want to know about clicks and mouse events on the toggle
        var toggleId = elementId + '|Toggle';
    	pu.eToggle = document.getElementById(toggleId);
        EP.Event.addStaticHandler(toggleId, "click", "EPPU.generalEventHandler");
        EP.Event.addStaticHandler(toggleId, "mouseout", "EPPU.generalEventHandler");
        EP.Event.addStaticHandler(toggleId, "mouseover", "EPPU.generalEventHandler");
        EP.Event.addStaticHandler(toggleId, "mousedown", "EPPU.generalEventHandler");
        EP.Event.addStaticHandler(toggleId, "mouseup", "EPPU.generalEventHandler");

        //
        // we want to know about mouse events on the target
        var targetId = elementId + '|Target';
    	pu.eTarget = document.getElementById(targetId);
        EP.Event.addStaticHandler(targetId, "mouseover", "EPPU.generalEventHandler");
        EP.Event.addStaticHandler(targetId, "mouseout", "EPPU.generalEventHandler");

        //
        // events on the main object
        var outerId = elementId + '|Outer';
    	pu.eOuter = document.getElementById(outerId);
        EP.Event.addStaticHandler(outerId, "mouseover", "EPPU.generalEventHandler");
        EP.Event.addStaticHandler(outerId, "mouseout", "EPPU.generalEventHandler");
        
        //
        // pop up box events are direct at it, cause it eats them anyway
        var popupBoxId = elementId + '|Box';
        pu.eBox = document.getElementById(popupBoxId);
        EP.Event.addStaticHandler(popupBoxId, "click", 	  "EPPU.eatEventHandler");

        var boxContainerId = elementId + '|BoxContainer';
        pu.eBoxContainer = document.getElementById(boxContainerId);

        pu.eIframeQuirk = document.getElementById(elementId + '|IframeQuirk');
        
    	//
    	// bump the zindex of the box so it sits just above
    	// the current z-index.
 		var zIndex= EP.determineZ(document.getElementById(elementId));
 		zIndex = zIndex+1;
		EP.setZ(pu.eBox,zIndex);
		if (pu.eIframeQuirk) {
			EP.setZ(pu.eBox,zIndex+1);
			EP.setZ(pu.eIframeQuirk,zIndex);
			if (pu.eBoxContainer) {
				EP.setZ(pu.eBoxContainer,zIndex);
			}
		}
		pu.initialZIndex = zIndex;

		var expanded = item.getAttribute("expanded") == "true";
		if (expanded) {
			pu.showBox(false); // does not cause property update
		}
    }
};
//-------------------------------------------
// returns a EPPU either directly or by elementId lookup
//-------------------------------------------
EPPU.getObj = function (elementIdorObj) {
	var pu = elementIdorObj;
	if (typeof(elementIdorObj) == 'string') {
		pu = EP.ObjectMap.get(elementIdorObj);
	}
	return pu;
};


//-------------------------------------------
//-------------------------------------------
EPPU.prototype.addChild = function(child) {
	this.children[this.children.length] = child;
	child.parent = this;
	child.level = null;
};

//-------------------------------------------
//-------------------------------------------
EPPU.prototype.removeChild = function(child) {
	for (i in this.children) {
		var testItem = this.children[i];
		if (testItem == child) {
			child.parent = null;
		}
	}
};

//-------------------------------------------
//-------------------------------------------
EPPU.prototype.isParent = function(dropDown) {
	return dropDown.parent == this;
};

//-------------------------------------------
//-------------------------------------------
EPPU.prototype.isAncestor = function(dropDown) {
	if (this == dropDown) {
		return true;
	}
	while (dropDown) {
		if (dropDown == this) {
			return true;
		}
		dropDown = dropDown.parent;
	} 
	return false;
};

//-------------------------------------------
//-------------------------------------------
EPPU.prototype.getLevel = function() {
	if (this.level == null) {
		var level = 0;
		var pu = this;
		while (pu.parent) {
			pu = pu.parent;
			level++;
		}
		this.level = level;
	}
	return this.level;
};

//-------------------------------------------
//-------------------------------------------
EPPU.prototype.isParent = function(pu) {
	return pu.parent == this;
};

//-------------------------------------------
// is the passed in item an ancestor of this object
//-------------------------------------------
EPPU.prototype.isAncestor = function(pu) {
	if (this == pu) {
		return true;
	}
	while (pu) {
		if (pu == this) {
			return true;
		}
		pu = pu.parent;
	} 
	return false;
};

//-------------------------------------------
//is the passed in item an descendant of this object
//-------------------------------------------
EPPU.prototype.isDescendant= function(pu) {
	return pu.isAncestor(this);
};

//-------------------------------------------
//-------------------------------------------
EPPU.prototype.isSibling = function(pu) {
	if (this.parent) {
		for (i in this.parent.children) {
			var testItem = this.parent.children[i];
			if (testItem == pu) {
				return true;
			}
		}
	}
	return false;
};

//-------------------------------------------
//hides all the pop up boxes leading down from
//this object to the botom of the hierarchy
//-------------------------------------------
EPPU.prototype.hideBoxesDown = function(saveState) {
	if (this.eBox) {
		EP.setVisible(this.eBox,false);
		EP.setDisplayed(this.eBox,false);
		if (this.eIframeQuirk) {
			EP.setVisible(this.eIframeQuirk,false);
			EP.setDisplayed(this.eIframeQuirk,false);
		}
		// reset zindex to initial level
		var zIndex = this.initialZIndex;
		EP.setZ(this.eBox,zIndex);
		if (this.eIframeQuirk) {
			EP.setZ(this.eBox,zIndex+1);
			EP.setZ(this.eIframeQuirk,zIndex);
			if (this.eBoxContainer) {
				EP.setZ(this.eBoxContainer,zIndex);
			}
		}
	}
	for (i in this.children) {
		var pu = this.children[i];
		if (pu.isExpanded()) {
			pu.hideBoxesDown(saveState);
		}
	}
	if (saveState) {
		this.saveState();
	}
};

//-------------------------------------------
//hides all the pop up boxes leading up from
//this pop up item to the top of the hierarchy
//-------------------------------------------
EPPU.prototype.hideBoxesUp = function(saveState) {
	var pu = this;
	while (pu) {
		pu.hideBoxesDown(saveState);
		pu = pu.parent;
	}
};

//-------------------------------------------
// a pop up is expanded if its box is visible
//-------------------------------------------
EPPU.prototype.isExpanded = function() {
	return EP.isVisible(this.eBox);
};


//-------------------------------------------
// swaps the state of the pop up and saves
// in into a property that will flow back to the server
//-------------------------------------------
EPPU.prototype.saveState = function() {
	var state = ""+this.isExpanded();
	EP.Event.setClientValue(this.elementId,"expanded",state);
};


//-------------------------------------------
//called when moving from prevObj to 'this'
//-------------------------------------------
EPPU.prototype.hidePrevious = function(prevObj) {
	if (this == prevObj) {
		// how can we deselect outserlves when moving to our ourselves
		return;
	}
	// if the previous item was our ancestor then
	var prevIsAncestor  = prevObj.isAncestor(this);
	if (prevIsAncestor) {
		return;
	}
	if (prevObj.isDescendant(this)) {
		prevObj.hideBoxesDown();
		prevObj.saveState();
	} else {
		prevObj.hideBoxesUp();
		prevObj.saveState();
	}
};

//-------------------------------------------
// called to reparent the popup box if its
// always on top.  Respects the Echo2 model
// element.
//-------------------------------------------
EPPU.prototype.reparentBox = function() {
	//debugger;
	if (this.popUpAlwaysOnTop) {
		var newParent = document.getElementsByTagName("body")[0];
		if (EchoModalManager.modalElementId && EchoModalManager.isElementInModalContext(this.elementId)) {
			newParent = document.getElementById(EchoModalManager.modalElementId);
		}
		if (this.eBox.parentNode != newParent) {
			newParent.appendChild(this.eBox);
			if (this.eIframeQuirk) {
				newParent.appendChild(this.eIframeQuirk);
			}
		}
	}
}


//-------------------------------------------
//called to position the popup box where it should be shown
//-------------------------------------------
EPPU.prototype.positionBox = function() {
	this.reparentBox();
	
	EP.setDisplayed(this.eBox,true);
	if (this.eIframeQuirk) {
		EP.setDisplayed(this.eIframeQuirk,true);
	}
	
	var eShowNextTo = this.eTarget;
	var targetX = 0;
	var targetY = 0;
	if (this.popUpAlwaysOnTop) {
		eShowNextTo = document.getElementById(this.elementId);
		
		var pos = EP.getPageXY(eShowNextTo);
		var posParent = EP.getPageXY(this.eBox.parentNode);
		targetX = pos[0] - posParent[0];
		targetY = pos[1] - posParent[1];		
	}
	
	if (this.nextToToggle) {
		eShowNextTo = this.eToggle;
		if (this.popUpAlwaysOnTop) {
			targetX += EP.getWidth(this.eTarget);
		} else {
			targetX = EP.getWidth(this.eTarget);
		}
	}
	var boxWidth = EP.getWidth(this.eBox);
	var boxHeight = EP.getHeight(this.eBox);
	
	if (this.eIframeQuirk) {
		EP.setWidth(this.eIframeQuirk,boxWidth);
		EP.setHeight(this.eIframeQuirk,boxHeight);
	}
	// this qurik only happens for contained pos:realtive elements
	if (! this.popUpAlwaysOnTop) {
		boxWidth = EP.relativeBoxQuirk(this.eBox,boxWidth);
	}
	
	var targetWidth = eShowNextTo.offsetWidth;
	var targetHeight = eShowNextTo.offsetHeight;

	var x = targetX + this.leftOffset;
	if (this.hAlign == EP.LEFT) {
		x = x;
	} else if (this.hAlign == EP.RIGHT) {
		x = x + targetWidth;
	} else if (this.hAlign == EP.CENTER) {
		x = x + (targetWidth /2) - (boxWidth / 2);
	}
	var y = targetY + this.topOffset;
	if (this.vAlign == EP.TOP) {
		if (this.popUpAlwaysOnTop) {
			y = y - boxHeight;
		} else {
			y = y - boxHeight - targetHeight;
		}
	} else if (this.vAlign == EP.BOTTOM) {
		if (this.popUpAlwaysOnTop) {
			y = y + targetHeight;
		} else {
			y = y;
		}
	} else if (this.vAlign == EP.CENTER) {
		y = y + (targetHeight /2) - (boxHeight / 2);
	}
	
	EP.setX(this.eBox,x);
	EP.setY(this.eBox,y);
	if (this.eIframeQuirk) {
		EP.setX(this.eIframeQuirk,x);
		EP.setY(this.eIframeQuirk,y);
	}
};

//-------------------------------------------
// Show the popup Box
//-------------------------------------------
EPPU.prototype.showBoxImpl = function(saveState) {
	//
	// temp adjust its z-index to high values
	//
	var zIndex = 32000;
	EP.setZ(this.eBox,zIndex);
	if (this.eIframeQuirk) {
		EP.setZ(this.eBox,zIndex+1);
		EP.setZ(this.eIframeQuirk,zIndex);
		if (this.eBoxContainer) {
			EP.setZ(this.eBoxContainer,zIndex);
		}
	}
	
	this.positionBox();
	
	EP.setVisible(this.eBox,true);
	if (this.eIframeQuirk) {
		EP.setVisible(this.eIframeQuirk,true);
	}
	if (saveState) {
		this.saveState();
	}
};

//-------------------------------------------
// Asynch showing of a pop up Box or it can be synch
// if a EPPU object is passed in directly
//-------------------------------------------
EPPU.asynchShowBox = function(elementIdorObj,saveState) {
	EPPU.asynchShowBoxId = null;
	var pu = EPPU.getObj(elementIdorObj);
	if (pu == null || pu.eBox == null) {
		return;
	}
	pu.showBoxImpl(saveState);
};

//-------------------------------------------
// shows a pop up box
//-------------------------------------------
EPPU.prototype.showBox = function(saveState) {
	if (EPPU.asynchShowBoxId) {
		window.clearTimeout(EPPU.asynchShowBoxId);
		EPPU.asynchShowBoxId = null;
	}
	if (this.eBox == null) {
		return;
	}

	// dynamically add a document level handler
	EP.DocumentEvent.addHandler('click', "EPPU",	EPPU.documentEventHandler);
	
	var timeout = this.showBoxTimeOut;
	if (timeout <= 0) {
		EPPU.asynchShowBox(this,saveState);
	} else {
		EPPU.asynchShowBoxId = window.setTimeout('EPPU.asynchShowBox("' + this.elementId + '",' + saveState + ')',timeout);
	}
};

//-------------------------------------------
// Called when a pop up box toggle image is clicked on
//-------------------------------------------
EPPU.prototype.togglePopup = function(echoEvent, forceHide) {
	EP.Event.cancelEvent(echoEvent);
	if (! EchoClientEngine.verifyInput(this.elementId)) {
        return;
    }	
    
	var pu = this;
	var prevObj = EPPU.currentObj;
	EPPU.currentObj = pu;

	//
	// if they have to click to make the popup go up then we want to go back to
	// non presssed style
	if (! pu.onRolloverShowBox) {
		//
		// restore to non pressed style
	    if (pu.toggleDefaultIcon) {
	        EP.imageSwap(pu.elementId + "|Icon", this.toggleDefaultIcon);
	    }
	    EP.applyStyle(pu.eToggle, this.toggleDefaultStyle);
	    EP.applyStyle(pu.eTarget, this.targetDefaultStyle);
	    EP.applyStyle(pu.eOuter, this.defaultStyle);
	}
	
	// remove document level handler. we may need it later but then agin
	// we might not. Browser performance is better if we add and remove document
	// level handlers.
	EP.DocumentEvent.removeHandler('click','EPPU');
	
	//
	// we have clicked on ourselves after where became the current pop up
	// so what state are we on
	if (prevObj == pu) {
		if (pu.isExpanded() || forceHide) {
			pu.hideBoxesDown(true);
		} else {
			pu.showBox(true);
			//EPPU.asynchShowBox(this);
		}
	} else {
		// was there a previous current pop up, then hide it
		if (prevObj) {
			pu.hidePrevious(prevObj);
		}
		// now our pop up box must be show
		if (pu.isExpanded() || forceHide) {
			pu.hideBoxesDown(true);
		} else {
			pu.showBox(true);
			//EPPU.asynchShowBox(this);
		}
	}
};

/**
 * Adds the event handlers to the keytarget component
 */
EPPU.prototype.addKeyTargetEvents = function() {
    if (this.keyTargetId) {
    	var targetElem = document.getElementById(this.keyTargetId);
    	EP.Event.addHandler("keydown", targetElem, this);
    	EP.Event.addHandler("click", targetElem, this);
    }
};

/**
 * Removes the event handlers to the keytarget component
 */
EPPU.prototype.removeKeyTargetEvents = function() {
    if (this.keyTargetId) {
    	var targetElem = document.getElementById(this.keyTargetId);
    	if (targetElem) {
        	EP.Event.removeHandler("keydown", targetElem);
        	EP.Event.removeHandler("click", targetElem);
    	}
    }
};

//-------------------------------------------
// Called when the mouse moves over and out of a
// toggle image
//-------------------------------------------
EPPU.prototype.onmouse = function(echoEvent) {
    var actualTarget = echoEvent.target;
    var eventTarget = echoEvent.registeredTarget;
	EchoDebugManager.consoleWrite(echoEvent.type + " - " + eventTarget.id + " - target : " + actualTarget.id);	
	if (echoEvent.type == "mouseout" || echoEvent.type == "mouseleave") {
		if (eventTarget == this.eToggle) {
		    if (this.toggleDefaultIcon) {
		        EP.imageSwap(this.elementId + "|Icon", this.toggleDefaultIcon);
		    }
		    EP.applyStyle(this.eToggle, this.toggleDefaultStyle);
			EP.applyStyle(this.eOuter, this.defaultStyle);
		}
		if (eventTarget == this.eTarget) {
			EP.applyStyle(this.eTarget, this.targetDefaultStyle);
			EP.applyStyle(this.eOuter, this.defaultStyle);
		}
		if (eventTarget == this.eOuter) {
			if (actualTarget == this.eOuter) {
				EP.applyStyle(this.eOuter, this.defaultStyle);
			}
		}
	}
	if (echoEvent.type == "mouseover" || echoEvent.type == "mouseenter") {
		if (eventTarget == this.eToggle) {
			if (this.toggleRolloverIcon) {
				EP.imageSwap(this.elementId + "|Icon", this.toggleRolloverIcon);
			}
			EP.applyStyle(this.eToggle,this.toggleRolloverStyle);
			// over the toggle is over the outer
			EP.applyStyle(this.eOuter,this.rolloverStyle);
		}
		if (eventTarget == this.eTarget) {
			EP.applyStyle(this.eTarget, this.targetRolloverStyle);
			// over the target is over the outer
			EP.applyStyle(this.eOuter,this.rolloverStyle);
		}
				
		if (eventTarget == this.eOuter) {
			// if they mouse over the box bit it still in the outer
			// but its not what we want.
			if (actualTarget == this.eOuter) {
				EP.applyStyle(this.eOuter,this.rolloverStyle);
			}
		}
	}
	if (echoEvent.type == "mousedown") {
		if (eventTarget == this.eToggle) {
			if (this.togglePressedIcon) {
				EP.imageSwap(this.elementId + "|Icon", this.togglePressedIcon);
			}
		    EP.applyStyle(this.eToggle,this.togglePressedStyle);
		}
	}
	if (echoEvent.type == "mouseup") {
		if (eventTarget == this.eToggle) {
			if (this.togglePressedIcon) {
				EP.imageSwap(this.elementId + "|Icon", this.toggleRolloverIcon ? this.toggleRolloverIcon : this.toggleDefaultIcon);
			}
		    EP.applyStyle(this.eToggle,this.toggleRolloverStyle);
		}
	}
	
};

//-------------------------------------------
// A direct to object instance event handler
//-------------------------------------------
EPPU.prototype.eventHandler = function(echoEvent) {
	if (! EchoClientEngine.verifyInput(this.elementId)) {
        return;
    }	
	if (echoEvent.type == 'keydown') {
		EchoDebugManager.consoleWrite("EPPU.prototype.eventHandler " + echoEvent.type);	
		var key = echoEvent.keyCode;
		var alt = echoEvent.altKey;

		// DOWN ARROW - UP ARROW - F4
		if (key == 40 || key == 38 || key == 115) { 		
			this.togglePopup(echoEvent);
		}
	}
	if (echoEvent.type == 'click') {
		this.togglePopup(echoEvent, true); // force a hide always
	}
};
//-------------------------------------------
// A general event handler for pop up box events
//-------------------------------------------
EPPU.generalEventHandler = function(echoEvent) {
    var eventTarget = echoEvent.registeredTarget;
    var elementId = eventTarget.getAttribute("id");
    if (elementId.indexOf('|') != -1) {
    	elementId = elementId.split('|')[0]; // get the id at the front of the |
    }
	if (! EchoClientEngine.verifyInput(elementId)) {
        return;
    }	
    
	var pu = EPPU.getObj(elementId);
	if (pu == null) {
		alert('ASSERT : pop up box |' + elementId + '| not found as expected in Object Map');
		return;
	}
	
	if (echoEvent.type.indexOf("mouse") >= 0) {
		pu.onmouse(echoEvent);
	}
	if (echoEvent.type == 'click') {
		pu.togglePopup(echoEvent);
	}
	// if we mouse over the onRolloverShowBox flag is true then we show the
	// popup as
	// if a click happened.
	if (echoEvent.type == 'mouseover' || echoEvent.type == 'mouseenter') {
		if (pu.eToggle == eventTarget) {
			if (pu.onRolloverShowBox && pu.isExpanded() == false) {
				pu.togglePopup(echoEvent);
			}
		}
	}
};

//-------------------------------------------
//A event handler that eats all events
//-------------------------------------------
EPPU.eatEventHandler = function(echoEvent) {
	EchoDebugManager.consoleWrite("EPPU.eatEventHandler " + echoEvent.type);	
	EP.Event.cancelEvent(echoEvent);
};


//-------------------------------------------
//a global document event handler that is registered
//for 'click' and 'mouseout' events. Allows us to
//close any open pop up box if the user clicks away from a pop up box
//-------------------------------------------
EPPU.documentEventHandler = function(echoEvent) {
	EP.debug("popup document level " + echoEvent.type);
	if (echoEvent.type == "click") {
        EchoDomUtil.preventEventDefault(echoEvent);
		if (EPPU.currentObj) {
			//debugger;
			EPPU.currentObj.hideBoxesUp(true);
			EPPU.currentObj = null;
		}
		EP.DocumentEvent.removeHandler('click','EPPU');
	}
};

