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

//-------------------------------------------
//The EPMI object is used to hold properties
//for the menubar, menus and menu items.
//
//Each menuItem must have a corresponding HTML element
//with the same elementId. Also if the MenuItem contains
//children, the children must be contained
//in a HTML element named elementId + 'Box'
//
//
// NOTE : we tried the trick of mapping mouseover/mouseenter
// on IE but it doesnt work with the "timed" nature of the 
// Menu.
//-------------------------------------------

EPMI = function(elementId) {
	this.elementId = elementId;
	this.children = [];
	EP.ObjectMap.put(elementId,this);
};

// we can have at most 1 menu item curren at a time, across all menus
EPMI.currentMenu = null;
EPMI.asynchMouseOutId = null;
EPMI.asynchMouseOverId = null;
EPMI.asynchShowBoxId = null;

/**
 * EPMI has a ServerMessage processor.
 */
EPMI.MessageProcessor = function() { };

EPMI.MessageProcessor.process = function(messagePartElement) {
    for (var i = 0; i < messagePartElement.childNodes.length; ++i) {
        if (messagePartElement.childNodes[i].nodeType === 1) {
            switch (messagePartElement.childNodes[i].tagName) {
            case "init":
                EPMI.MessageProcessor.processInit(messagePartElement.childNodes[i]);
                break;
            case "dispose":
                EPMI.MessageProcessor.processDispose(messagePartElement.childNodes[i]);
                break;
            }
        }
    }
};

EPMI.MessageProcessor.processDispose = function(disposeMessageElement) {
    for (var item = disposeMessageElement.firstChild; item; item = item.nextSibling) {
        var elementId = item.getAttribute("eid");
		EP.ObjectMap.destroy(elementId);
    	var targetElem = document.getElementById(elementId + '|Box');
    	if (targetElem) {
            targetElem.parentNode.removeChild(targetElem);
    	}
    	targetElem = document.getElementById(elementId + '|IframeQuirk');
    	if (targetElem) {
    		targetElem.parentNode.removeChild(targetElem);
    	}
    	
    }
};

//-------------------------------------------
EPMI.prototype.destroy = function() {
	if (this.parent) {
		this.parent.removeChild(this);
	} 
	while (this.children.length > 0) {
		EP.ObjectMap.destroy(this.children[0].elementId);
	}
	var menuBoxId = this.elementId + 'Box';
	var menuBoxE = document.getElementById(menuBoxId);
	if (menuBoxE) {
		EP.Event.removeStaticHandler(menuBoxId, "mouseover");
		EP.Event.removeStaticHandler(menuBoxId, "mouseout");
	}
    EP.Event.removeStaticHandler(this.elementId, "click");
    EP.Event.removeStaticHandler(this.elementId, "mouseover");
    EP.Event.removeStaticHandler(this.elementId, "mouseout");
};


EPMI.MessageProcessor.processInit = function(initMessageElement) {
    for (var item = initMessageElement.firstChild; item; item = item.nextSibling) {
        var elementId = item.getAttribute("eid");
        var elementType = item.getAttribute("etype");
    	if (elementType === "componentBox") {
    		//
    		// non MenuItems have a box around them that
    		// dehilights the last menu
    		EP.Event.addStaticHandler(elementId, "mouseover", "EPMI.componentBoxEventHandler");
    	    EP.Event.addStaticHandler(elementId, "click",  "EPMI.eatEventHandler");
    	    
    	    var componentBoxE = document.getElementById(elementId);
    	    componentBoxE.className = 'EPMI.componentBox';
    	} else {
   			EP.ObjectMap.destroy(elementId);
    		//
            // init an EPMI object
            var mi = new EPMI(elementId);
        	mi.level = null;
            mi.elementId = elementId;
            mi.enabled = true;
            if (item.getAttribute("enabled") === "false") {
                mi.enabled = false;
                EchoDomPropertyStore.setPropertyValue(elementId, "EchoClientEngine.inputDisabled", true);
            }
            mi.visibleComponentCount = parseInt(item.getAttribute("visibleComponentCount"),10);
        	
        	mi.defaultStyle = item.getAttribute("defaultStyle");
        	mi.defaultIcon = item.getAttribute("defaultIcon");
        	mi.defaultSubmenuImage = item.getAttribute("defaultSubmenuImage");

        	mi.rolloverStyle = item.getAttribute("rolloverStyle");
        	mi.rolloverIcon = item.getAttribute("rolloverIcon");
        	mi.rolloverSubmenuImage = item.getAttribute("rolloverSubmenuImage");
        	
        	mi.serverNotify = item.getAttribute("serverNotify") === "true";
        	
        	mi.isRootMenu = item.getAttribute("isRootMenu") === "true";
        	mi.horizontal = item.getAttribute("horizontal")==="true";
        	mi.keepAlive = item.getAttribute("keepAlive")==="true";
        	mi.clickToOpen = item.getAttribute("clickToOpen")==="true";
        	mi.submenuClickToOpen = item.getAttribute("submenuClickToOpen")==="true";
        	mi.submenuImageBordered = item.getAttribute("submenuImageBordered")==="true";
        	mi.menuAlwaysOnTop = item.getAttribute("menuAlwaysOnTop")==="true";
        	
        	var num = parseInt(item.getAttribute("leftOffset"),10);
       		mi.leftOffset = isNaN(num) ? 0 : num;
        	num = parseInt(item.getAttribute("topOffset"),10);
       		mi.topOffset = isNaN(num) ? 0 : num;
        	
        	mi.showBoxTimeOut = 100;
        	mi.onmouseOutTimeOut = 300;
        	mi.onmouseOverTimeOut = 0;

        	// IE fix (it often ignores the cellspacing=0 attribute)
        	if (EchoClientProperties.get("quirkIERepaint")) {
        		if (document.getElementById(elementId)) {
            		var tables = document.getElementById(elementId).getElementsByTagName("table");
            		if (tables.length > 0) {
            			tables[0].cellSpacing = "0";
            		}
        		}
        	}   
        	
            //
            // seach for parent menu item
    		if (! mi.isRootMenu) {
	            var parentMenuId = item.getAttribute("parentMenuId");
	            var parentMI = EP.ObjectMap.get(parentMenuId);
	            if (parentMI) {
	            	parentMI.addChild(mi);
	            }
    		}
    		if (! mi.isRootMenu) {
            	// add standard MI handlers
        	    EP.Event.addStaticHandler(elementId, "mouseout",  "EPMI.eatEventHandler");
        	    EP.Event.addStaticHandler(elementId, "mouseover", "EPMI.generalEventHandler");
           	    EP.Event.addStaticHandler(elementId, "click",  "EPMI.generalEventHandler");
    		} else {
    			// the root "fixed" menu area has no events
        	    EP.Event.addStaticHandler(elementId, "mouseout",  "EPMI.eatEventHandler");
        	    EP.Event.addStaticHandler(elementId, "mouseover", "EPMI.eatEventHandler");
    		}
    		var menuBoxId = mi.elementId + 'Box';
    		var menuBox = document.getElementById(menuBoxId);
    		if (menuBox) {
        	    EP.Event.addStaticHandler(menuBoxId, "mouseout",  "EPMI.eatEventHandler");
        	    EP.Event.addStaticHandler(menuBoxId, "mouseover", "EPMI.eatEventHandler");
        	    
         		mi.eBox 		= document.getElementById(menuBoxId);
         		mi.eBoxContainer = document.getElementById(elementId);
                mi.eIframeQuirk 	= document.getElementById(elementId + '|IframeQuirk');
         	    
            	//
            	// bump the zindex of the box so it sits just above
            	// the current z-index.
         		var zIndex= EP.determineZ(document.getElementById(elementId));
         		zIndex = zIndex+1;
        		EP.setZ(mi.eBox,zIndex);
        		if (mi.eIframeQuirk) {
        			EP.setZ(mi.eBox,zIndex+1);
        			EP.setZ(mi.eBoxContainer,zIndex);
        			EP.setZ(mi.eIframeQuirk,zIndex);
        		}
        		mi.initialZIndex = zIndex;        	    
    		}
        }
    }
};

//-------------------------------------------
//-------------------------------------------
EPMI.prototype.addChild = function(childMenu) {
	this.children[this.children.length] = childMenu;
	childMenu.parent = this;
	childMenu.level = null;
};

//-------------------------------------------
//-------------------------------------------
EPMI.prototype.removeChild = function(childMenu) {
	////debugger;
	for (i in this.children) {
		var testItem = this.children[i];
		if (testItem === childMenu) {
			childMenu.parent = null;
			delete this.children[i];
			this.children.splice(i, 1);
			break;
		}
	}
};

//-------------------------------------------
//returns a EPMI either directly or by elementId lookup
//-------------------------------------------
EPMI.getMI = function (elementIdorMI) {
	var targetMI = elementIdorMI;
	if (typeof(elementIdorMI) === 'string') {
		targetMI = EP.ObjectMap.get(elementIdorMI);
	}
	return targetMI;
};


//-------------------------------------------
//-------------------------------------------
EPMI.prototype.getLevel = function() {
	if (this.level === null) {
		var level = 0;
		var targetMI = this;
		while (targetMI.parent) {
			targetMI = targetMI.parent;
			level++;
		}
		this.level = level;
	}
	return this.level;
};

//-------------------------------------------
//-------------------------------------------
EPMI.prototype.isParent = function(menuItem) {
	return menuItem.parent === this;
};

//-------------------------------------------
//-------------------------------------------
EPMI.prototype.isAncestor = function(menuItem) {
	if (this === menuItem) {
		return true;
	}
	while (menuItem) {
		if (menuItem === this) {
			return true;
		}
		menuItem = menuItem.parent;
	} 
	return false;
};

//-------------------------------------------
//-------------------------------------------
EPMI.prototype.isSibling = function(menuItem) {
	if (this.parent) {
		for (i in this.parent.children) {
			var testItem = this.parent.children[i];
			if (testItem === menuItem) {
				return true;
			}
		}
	}
	return false;
};

//-------------------------------------------
// Returns the highest common ancestor between
// this object and menuItem. if menu item is
// above this in the hierarchy, then the search is
// reverse.
// This includes
// grand parents and grand siblings of the menuItem but does
// not include the case where this === menuItem.parent
// or this is a direct sibling of menuItem
//-------------------------------------------
EPMI.prototype.getCommonAncestor = function(menuItem) {
	var gp = this;
	var gc = menuItem;
	if (gp.getLevel() > gc.getLevel()) {
		menuItem = gp; gp = gc; gc = menuItem;
	}
	if (gc.parent === null) {
		return null;
	}
	if (gp === gc.parent) {
		return null;
	}
	if (gp.isSibling(gc)) {
		return null;
	}
	gc = gc.parent;
	while (gc && gc.parent) {
		if (gp === gc.parent) {
			return gc;
		}
		if (gp.isSibling(gc)) {
			return gc; 
		}
		gc = gc.parent;
	} 
	return null;
};

//-------------------------------------------
// The keepAlive property is in fact a function
// of the Menu not a MenuItem. Therefore
// we look for children and if dont find some,
// we consult the parent
//-------------------------------------------
EPMI.prototype.isKeptAlive = function() {
	var ka = this.keepAlive;
	if (this.children.length === 0) {
		if (this.parent) {
			ka = this.parent.keepAlive;
		}
	}
	return ka;
};
//----------------------------------------------------
// De hilights the menu item class and any icons
//----------------------------------------------------
EPMI.prototype.dehilight = function() {
    //debugger;
	if (this.eMI) {
	    if (this.defaultIcon) {
	        EP.imageSwap(this.elementId + "Icon", this.defaultIcon);
	    }
	    if (this.defaultSubmenuImage) {
	        EP.imageSwap(this.elementId + "SubMenuIMG", this.defaultSubmenuImage);
	    }
	    if (this.eMI.style.cssText) {
	    	this.eMI.style.cssText = this.defaultStyle;
	    } else {
			EP.applyStyle(this.eMI,this.defaultStyle);
	    }
	}
};

//----------------------------------------------------
//Hilights the menu item class and any rollover icons
//----------------------------------------------------
EPMI.prototype.hilight = function() {
    //debugger;
	if (this.eMI) {
	    if (this.rolloverIcon) {
	        EP.imageSwap(this.elementId + "Icon", this.rolloverIcon);
	    }
	    if (this.rolloverSubmenuImage) {
	        EP.imageSwap(this.elementId + "SubMenuIMG", this.rolloverSubmenuImage);
	    }
	    // Theres seems to be a bug in IE 6 where of I set position:relative and then
	    // call applyStye, it loses the text/icons somehow.  However if I set the style.cssText
	    // directly then it works.  Dont know why.
	    if (this.eMI.style.cssText) {
	    	this.eMI.style.cssText = this.rolloverStyle;
	    } else {
			EP.applyStyle(this.eMI,this.rolloverStyle);
	    }
	}
};


//-------------------------------------------
//hides all the menu boxes leading down from
//menuItem to the botom of the hierarchy
//-------------------------------------------
EPMI.prototype.hideBoxesDown = function() {
	if (this.eBox) {
		this.eBoxShowing = false;
		EP.setVisible(this.eBox,false);
		EP.setDisplayed(this.eBox,false);
		if (this.eIframeQuirk) {
			EP.setVisible(this.eIframeQuirk,false);
			EP.setDisplayed(this.eIframeQuirk,false);
		}
		this.dehilight();
		// reset zindex to initial level
		var zIndex = this.initialZIndex;
		EP.setZ(this.eBox,zIndex);
		if (this.eIframeQuirk) {
			EP.setZ(this.eBox,zIndex+1);
			EP.setZ(this.eIframeQuirk,zIndex);
			if (this.menuAlwaysOnTop) {
				EP.setZ(this.eBoxContainer,zIndex);
			}
		}
		
	}
	for (i in this.children) {
		var targetMI = this.children[i];
		if (targetMI && targetMI.eBox && EP.isVisible(targetMI.eBox)) {
			targetMI.hideBoxesDown();
		}
	}
};

//-------------------------------------------
// hides all the menu boxes leading up from
// this menu item to the top of the hierarchy
//-------------------------------------------
EPMI.prototype.hideBoxesUp = function() {
	var menuItem = this;
	while (menuItem) {
		menuItem.hideBoxesDown();
		menuItem.dehilight();
		menuItem = menuItem.parent;
	}
};

//-------------------------------------------
// called when moving from prevMI to 'this'
//-------------------------------------------
EPMI.prototype.deselectItem = function(prevMI) {
	if (this === prevMI) {
		// how can we deselect outserlves when moving to our ourselves
		return;
	}
	// if the previous item was our ancestor then
	// we dont change the hilighting class
	var prevIsAncestor  = prevMI.isAncestor(this);
	if (! prevIsAncestor) {
		prevMI.dehilight();
	}

	// if its a sibling then just hide any its menu boxes
	if (prevMI.isSibling(this)) {
		prevMI.hideBoxesDown();

	// if its a direct child then just hide any if its menu boxes
	} else 	if (this.isParent(prevMI)) {
		prevMI.hideBoxesDown();

	// if our parent then dont do anything since its already showing
	} else if (prevMI.isParent(this)) {
		prevMI = prevMI; // do nothing

	// if its a sibling of our parent then hide the parents menu box only
	} else if (prevMI.parent && prevMI.parent.isSibling(this)) {
		prevMI.parent.hideBoxesDown();
		prevMI.parent.dehilight();
		prevMI.dehilight();

	// must be some other menu, perhaps in the same tree line
	} else {
		var commonMI = prevMI.getCommonAncestor(this);
		if (commonMI === null) {
			prevMI.hideBoxesUp();
		} else {
			commonMI.hideBoxesDown();
		}
	}
};


//-------------------------------------------
// Selects a MenuItem by highlighting it and
// automaticaly shows a drop down box
// (assuming the menu is not clickToOpen).
//-------------------------------------------
EPMI.prototype.selectItem = function() {
	this.hilight();
	if (! this.clickToOpen) {
		this.showBox();
	}
};

//-------------------------------------------
//called to reparent the box if its
//always on top.  Respects the Echo2 model
//element.
//-------------------------------------------
EPMI.prototype.reparentBox = function() {
	//debugger;
	if (this.menuAlwaysOnTop) {
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
// Asynch showing of a Box or it can be direct
// if a EPMI is passed in directly
//-------------------------------------------
EPMI.prototype.showBoxImpl = function() {
	EP.setDisplayed(this.eBox,true);
	this.reparentBox();

	var boxWidth = EP.getWidth(this.eBox);
	var boxHeight = EP.getHeight(this.eBox);
	if (this.eIframeQuirk) {
		EP.setDisplayed(this.eIframeQuirk,true);
		EP.setWidth(this.eIframeQuirk,boxWidth);
		EP.setHeight(this.eIframeQuirk,boxHeight);
	}
	// this quirk only happens on Moz when contained with a relative:TD not when the box is
	// parented to the body or modal window.
	if (! this.menuAlwaysOnTop) {
		boxWidth = EP.relativeBoxQuirk(this.eBox,boxWidth);
	}
	var pageWidth = EP.getPageWidth();
	var pageHeight = EP.getPageHeight();
	
	var targetX = 0;
	var targetY = 0;
	if (this.menuAlwaysOnTop) {
		var pos = EP.getPageXY(this.eBoxContainer);
		var posParent = EP.getPageXY(this.eBox.parentNode);
		targetX = pos[0] - posParent[0];
		targetY = pos[1] - posParent[1];		
	}
	//
	// the menu items sit inside a parent box (potentially) and hence
	// the width of the item is its bounding box not the item
	// text area, but only if its a vertical parent box, otherwise
	// its the x position of the parent menu text
	//
	var horizontal = (this.parent ? this.parent.horizontal : this.horizontal);
	var itemWidth = EP.getWidth(this.eMI);
	var itemHeight = EP.getHeight(this.eMI);
	if (this.parent && this.parent.eBox && ! horizontal) { 
		itemWidth = EP.getWidth(this.parent.eBox);
	}
	
	var x = targetX + this.leftOffset;
	// if the Menu is vertical and not the root Menu, then adjust by the width
	if (! horizontal && this.getLevel() > 0) {
		x += itemWidth;
	}
	// if too wide then try to align to left of item
	if (x + boxWidth > pageWidth) {
		x = targetX - boxWidth;
		if (x < 0) {
			// if to narrow then align screen width - box width
			x = pageWidth - boxWidth;
		}
	}
	
	var y = targetY + this.topOffset;
	// if the menu is horizontal or a top level one then
	// adjust by the height
	if (horizontal || this.getLevel() === 0) {
		y += itemHeight;
	}
	
	EP.debug("x = " + x + " y=" + y + " targetX=" + targetX + " targetY="+targetY);
	EP.setX(this.eBox,x);
	EP.setY(this.eBox,y);
	if (this.eIframeQuirk) {
		EP.setX(this.eIframeQuirk,x);
		EP.setY(this.eIframeQuirk,y);
	}
	
	//
	// temp boost its z-index to high values
	//
	var zIndex = 32000;
	EP.setZ(this.eBox,zIndex);
	if (this.eIframeQuirk) {
		EP.setZ(this.eBox,zIndex+1);
		EP.setZ(this.eIframeQuirk,zIndex);
		if (this.menuAlwaysOnTop) {
			EP.setZ(this.eBoxContainer,zIndex);
		}
	}	
	
	EP.setVisible(this.eBox,true);
	if (this.eIframeQuirk) {
		EP.setVisible(this.eIframeQuirk,true);
	}
	this.eBoxShowing = true;
};

//-------------------------------------------
// Asynch call back function
//-------------------------------------------
EPMI.asynchShowBox = function(elementIdorMI) {
	EPMI.asynchShowBoxId = null;
	var targetMI = EPMI.getMI(elementIdorMI);
	if (targetMI === null || targetMI.eBox === null) {
		return;
	}
	targetMI.showBoxImpl();
};

//-------------------------------------------
// shows a menu box beside the parent menu item
// It needs to respect all the edges of the screen
// so that it can be seen. It will only show
// something if there are children to be shown
//-------------------------------------------
EPMI.prototype.showBox = function() {
	if (EPMI.asynchShowBoxId) {
		window.clearTimeout(EPMI.asynchShowBoxId);
		EPMI.asynchShowBoxId = null;
	}
	if (this.eBox === null) {
		return;
	}
	// only show the box if we have children
	if (this.visibleComponentCount <= 0) {
		return;
	}
	var timeout = this.showBoxTimeOut;
	if (timeout <= 0) {
		EPMI.asynchShowBox(this);
	} else {
		EPMI.asynchShowBoxId = window.setTimeout('EPMI.asynchShowBox("' + this.elementId + '")',timeout);
	}
};

//-------------------------------------------
//called a little time after the mouse has moved
//over a menu item. This produces a more pleasing
//affect when selecting menu and reduces flicker
//-------------------------------------------
EPMI.asynchMouseOver = function(elementIdorMI) {
	EPMI.asynchMouseOutId = null;
	
	var prevMI = EPMI.currentMenu;
	var targetMI = EPMI.getMI(elementIdorMI);
	EPMI.currentMenu = targetMI;
	// was there a previous current menu, then hide it
	if (prevMI && prevMI !== targetMI) {
		targetMI.deselectItem(prevMI);
	}
	targetMI.selectItem();
	EPMI.asynchMouseOverId = null;
};

//-------------------------------------------
// Calls the asynchMouseOver() method above
//-------------------------------------------
EPMI.prototype.onmouseover = function(echoEvent) {
	EP.debug("Menu - onmouseover");
	EP.Event.cancelEvent(echoEvent);
	if (! EchoClientEngine.verifyInput(this.elementId)) {
        return;
    }	
	// cancel any outstanding asynch events. We are only
	// interested in the current MenuItem now!
	if (EPMI.asynchMouseOutId) { 	
		window.clearTimeout(EPMI.asynchMouseOutId);
		EPMI.asynchMouseOutId = null;
	}
	if (EPMI.asynchMouseOverId) { 	
		window.clearTimeout(EPMI.asynchMouseOverId);
		EPMI.asynchMouseOverId = null;
	}
	if (EPMI.asynchShowBoxId) {
		window.clearTimeout(EPMI.asynchShowBoxId);
		EPMI.asynchShowBoxId = null;
	}
	// dynamically add the document level handlers
	EP.DocumentEvent.addHandler("click", "EPMI", EPMI.documentEventHandler);
	EP.DocumentEvent.addHandler("mouseover", "EPMI", EPMI.documentEventHandler);	
	
	var timeout = this.onmouseOverTimeOut;
	if (timeout <= 0) {
		EPMI.asynchMouseOver(this);
	} else {
		EPMI.asynchMouseOverId = window.setTimeout("EPMI.asynchMouseOver('" + this.elementId + "')",timeout);
	}
};

//-------------------------------------------
//called a little time after the mouse has moved
//out of a menu item
//-------------------------------------------
EPMI.asynchMouseOut = function() {
	//EP.debug("EPMI.asynchMouseOut");
	//debugger;
	if (EPMI.asynchMouseOutId === null) {
		return;
	}
	
	var targetMI = EPMI.currentMenu;
	if (targetMI) {
		if (! targetMI.isKeptAlive()) {
			// we must have moused out but not moused into another menu
			// so clean up everything from the current menu
			targetMI.dehilight();
			targetMI.hideBoxesUp();
			EPMI.currentMenu=null;
		}
	}
	EPMI.asynchMouseOutId=null;
};

//-------------------------------------------
// Calls the asynchMouseOut() method above
//-------------------------------------------
EPMI.prototype.onmouseout = function(echoEvent) {
	EP.debug("Menu - onmouseout");
	EP.Event.cancelEvent(echoEvent);
	if (EPMI.asynchMouseOutId) { 	
		window.clearTimeout(EPMI.asynchMouseOutId);
		EPMI.asynchMouseOutId = null;
	}
	var timeout = this.onmouseOutTimeOut;
	EPMI.asynchMouseOutId = window.setTimeout('EPMI.asynchMouseOut()',timeout);
};

//-------------------------------------------
// Called to make the action back to the server
//-------------------------------------------
EPMI.prototype.doAction = function(echoEvent) {
	if (! EchoClientEngine.verifyInput(this.elementId)) {
        return;
    }	
    // we hide the menu item box on action
	this.dehilight();
	this.hideBoxesUp();
	EPMI.currentMenu=null;
    
    if (this.serverNotify) {
	    //if (document.selection && document.selection.empty) {
	    //    document.selection.empty();
	    //}
	    EchoClientMessage.setActionValue(this.elementId, "click");
	    EchoServerTransaction.connect();
    }
};

//-------------------------------------------
// Called when a MenuItem is clicked on
//-------------------------------------------
EPMI.prototype.onclick = function(echoEvent) {
	EP.Event.cancelEvent(echoEvent);
	if (! EchoClientEngine.verifyInput(this.elementId)) {
        return;
    }	
    var doBoxOpen = false;
    var doAction = false;
	if (this.clickToOpen) {
		if (this.submenuClickToOpen) {
			if (echoEvent.target === this.eSubMenuIMG) {
				// they click on our submenu img only
				doBoxOpen = true;
			} else {
				// they click NOT on the submenu img so just action it!
				doAction = true;
			}
		} else {
			doBoxOpen = true;
		}
	} else {
		doAction = true;
	}
	
	if (doAction) {
		this.doAction(echoEvent);
	}
	if (doBoxOpen) {
		var prevMI = EPMI.currentMenu;
		var targetMI = this;
		EPMI.currentMenu = targetMI;
		//
		// we have clicked on ourselves after where became the current menu
		// so what state are we on
		if (prevMI === targetMI) {
			if (targetMI.eBoxShowing) {
				targetMI.hideBoxesDown();
				targetMI.hilight();
			} else {
				targetMI.hilight();
				EPMI.asynchShowBox(this);
			}
		} else {
			// was there a previous current menu, then hide it
			if (prevMI) {
				targetMI.deselectItem(prevMI);
			}
			// hilight and show the menu box
			targetMI.hilight();
			EPMI.asynchShowBox(this);
		}
	}
	
};

//-------------------------------------------
// A general event handler for menu events
//-------------------------------------------
EPMI.generalEventHandler = function(echoEvent) {
	EP.debug("EPMI.generalEventHandler " + echoEvent.type);
    var eventTarget = echoEvent.registeredTarget;
    var elementId = eventTarget.getAttribute("id");
	var targetMI = EPMI.getMI(elementId);
	if (targetMI === null) {
		alert('ASSERT : menu |' + elementId + '| not found as expected in Menu Map');
		return;
	}
	
	targetMI.eMI = document.getElementById(targetMI.elementId + 'MenuItem');
	if (targetMI.eMI === null) {
		targetMI.eMI = eventTarget;
	}
	targetMI.eBoxContainer 	= document.getElementById(targetMI.elementId);
	targetMI.eBox 			= document.getElementById(targetMI.elementId + 'Box');
	targetMI.eText 			= document.getElementById(targetMI.elementId + 'Text');
	targetMI.eSubMenuIMG 	= document.getElementById(targetMI.elementId + 'SubMenuIMG');
	targetMI.eIframeQuirk 	= document.getElementById(targetMI.elementId + '|IframeQuirk');
	
	if (! targetMI.isRoot) {
		if (targetMI.children.length > 0 && targetMI.eBox === null) {
			alert('ASSERT : menu box |' + targetMI.elementId + 'Box | not found as expected');
		}
	}
	// our event handlers (per menu item) can now be invoked
	if (echoEvent.type === 'mouseover' || echoEvent.type === 'mouseenter') {
		targetMI.onmouseover(echoEvent);
	} else if (echoEvent.type === 'click') {
		targetMI.onclick(echoEvent);
	}
};

//-------------------------------------------
//Called for events on a Box around the
//the custom components
//-------------------------------------------
EPMI.componentBoxEventHandler = function(echoEvent) {
	EP.debug("EPMI.componentBoxEventHandler " + echoEvent.type);
	if (EPMI.currentMenu) {
		if (echoEvent.type === 'mouseover' || echoEvent.type === 'mouseenter') {
			EP.Event.cancelEvent(echoEvent);
			EPMI.currentMenu.dehilight();
			if (EPMI.currentMenu.getLevel() > 0) {
				EPMI.currentMenu.hideBoxesDown();
			}
		}
	}
};

//-------------------------------------------
//A event handler that eats all events
//-------------------------------------------
EPMI.eatEventHandler = function(echoEvent) {
	//EP.debug("EPMI.eatEventHandler " + echoEvent.type);
	EP.Event.cancelEvent(echoEvent);
};


//-------------------------------------------
//a global document event handler that is registered
//for 'click' and 'mouseout' events. Allows us to
//close any open menu if the user clicks away from a menu
//-------------------------------------------
EPMI.documentEventHandler = function(echoEvent) {
	//EP.debug("documentEventHandler " + echoEvent.type);
	if (echoEvent.type === "click") {
		if (EPMI.currentMenu) {
			EPMI.currentMenu.dehilight();
			EPMI.currentMenu.hideBoxesUp();
			EPMI.currentMenu = null;
	 	 }
		EP.DocumentEvent.removeHandler("click","EPMI");
	} else if (echoEvent.type === "mouseover" || echoEvent.type === "mouseenter") {
		if (EPMI.currentMenu) {
			if (! EPMI.documentShouldWeCloseTheMenu(echoEvent,EPMI.currentMenu)) {
		       // prevent mouseout action, we are moving the mouse within the menubox
				return;
			}
		    EPMI.currentMenu.onmouseout(echoEvent);			
		}
		EP.DocumentEvent.removeHandler("mouseover","EPMI");
	}
};

//-------------------------------------------
// Determines if we should close the menu during a 
// document level event.  Needs to respect menu hierarchies as well
// as DOM hirearchies.
//
//-------------------------------------------
EPMI.documentShouldWeCloseTheMenu = function(echoEvent, currentMenu) {
		var currentMenuE = document.getElementById(EPMI.currentMenu.elementId);
		var targetE = echoEvent.target;
		
		var isMenuParentOfTarget = EP.isAncestorOf(targetE, currentMenuE);
		var isMenuBoxParentOfTarget =  EP.isAncestorOf(targetE, EPMI.currentMenu.eBox);
		if (isMenuParentOfTarget || isMenuBoxParentOfTarget) {
   			return false;
		}
		// ok its not in the DOM hirearchy but have we moused over someting inside the logical
		// menu hirearchy like a parent menus MenuBox.  Ie differs from FireFox in how it fires
		// document events and hence we could get here without meaning to.
		targetE = EP.Event.findIdentifiedElement(echoEvent);
		var parentMenu = currentMenu.parent;
		if (parentMenu) {
			if (parentMenu.eBox == targetE) {
				return false;
			}
		}
		// have we hovered over a componentBox, if so then leave the thing around
		var parentE = targetE;
		while (parentE) {
			if (parentE.className == 'EPMI.componentBox') {
				return false;
			}
			parentE = parentE.parentNode;
		}
		return true;
	
};