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

EPContainerEx = function(elementId) {
	this.elementId = elementId;
	EP.ObjectMap.put(elementId, this); 
};

/**
 * EPContainerEx has a ServerMessage processor.
 */
EPContainerEx.MessageProcessor = function() { };

EPContainerEx.MessageProcessor.process = function(messagePartElement) {
    for (var i = 0; i < messagePartElement.childNodes.length; ++i) {
        if (messagePartElement.childNodes[i].nodeType == 1) {
            switch (messagePartElement.childNodes[i].tagName) {
	            case "init":
	                EPContainerEx.MessageProcessor.processInit(messagePartElement.childNodes[i]);
	                break;
	            case "dispose":
	                EPContainerEx.MessageProcessor.processDispose(messagePartElement.childNodes[i]);
	                break;
	            case "scroll-horizontal":
	                EPContainerEx.MessageProcessor.processScroll(messagePartElement.childNodes[i]);
	                break;
	            case "scroll-vertical":
	                EPContainerEx.MessageProcessor.processScroll(messagePartElement.childNodes[i]);
	                break;
            }
        }
    }
};

EPContainerEx.MessageProcessor.processDispose = function(disposeMessageElement) {
    for (var item = disposeMessageElement.firstChild; item; item = item.nextSibling) {
        var elementId = item.getAttribute("eid");
		EP.ObjectMap.destroy(elementId);
    }
};


EPContainerEx.MessageProcessor.processInit = function(initMessageElement) {
    for (var item = initMessageElement.firstChild; item; item = item.nextSibling) {
        var elementId = item.getAttribute("eid");
		EP.ObjectMap.destroy(elementId);
		var chooser = new EPContainerEx(elementId);
		chooser.init(item);
    }
};

EPContainerEx.prototype.destroy = function() {
	var divE = document.getElementById(this.elementId);
	if (divE) {
		EP.Event.removeHandler('scroll',divE);
	}
	if (this.stretcher) {
		this.stretcher.destroy();
	}
};

/*
 * ---------------------------
 */
EPContainerEx.prototype.init = function(itemXML) {
    this.divE = document.getElementById(this.elementId);
    var horizontalScroll = itemXML.getAttribute("horizontal-scroll");
    var verticalScroll = itemXML.getAttribute("vertical-scroll");
    if (horizontalScroll) {
        this.divE.scrollLeft = parseInt(horizontalScroll,10);
    }
    if (verticalScroll) {
        this.divE.scrollTop = parseInt(verticalScroll,10);
    }
    EP.Event.addHandler('scroll',this.divE,this);

    // Only run the virtual position checking code
    // if virtual positioning is enabled.
    if (EchoVirtualPosition.enabled) {

        // See if our own element needs VP support
        if (EPContainerEx.isVPRequired(this.divE)) {
            EchoVirtualPosition.register(this.elementId);
        }
                
        // Since all children of our div are purely our own
        // wrapper divs, see if the styles defined in their
        // LayoutData require VP support as well
        for (var count = 0; count < this.divE.childNodes.length; count++) {

            var node = this.divE.childNodes[count];
            
            if (EPContainerEx.isVPRequired(node)) {
                EchoVirtualPosition.register(node.id);
            }
        }
    }
	
    // do they want us to stretch outselves
    var heightStretched = EP.DOM.getBooleanAttr(itemXML,"heightStretched",false);
    if (heightStretched) {
        var minHeight = EP.DOM.getIntAttr(itemXML,"minimumStretchedHeight",null);
        var maxHeight = EP.DOM.getIntAttr(itemXML,"maximumStretchedHeight",null);
        this.stretcher = EP.Stretch.verticalStretcher.getInstance(this.elementId, minHeight, maxHeight);
    }
};

/**
 * Checks if virtual positioning is required on a given element.
 *
 * @param The DOM element to check.
 * @return True if virtual positioning is need on the element. false otherwise.
 */
EPContainerEx.isVPRequired = function(element) {

    var isPx = EchoVirtualPosition.verifyPixelValue;
    var isPxUndef = EchoVirtualPosition.verifyPixelOrUndefinedValue;
    var s = element.style;
    
	// Check vertical positioning properties
    var verticalPos = (isPx(s.top) && isPx(s.bottom)
        && isPxUndef(s.paddingTop) && isPxUndef(s.paddingBottom)
        && isPxUndef(s.marginTop) && isPxUndef(s.marginBottom)
        && isPxUndef(s.borderTopWidth) && isPxUndef(s.borderBottomWidth));
    
	if (verticalPos) {
		return true;
    }
	
	// Check horizontal positioning properties
	return (isPx(s.left) && isPx(s.right)
	        && isPxUndef(s.paddingLeft) && isPxUndef(s.paddingRight)
	        && isPxUndef(s.marginLeft) && isPxUndef(s.marginRight)
	        && isPxUndef(s.borderLeftWidth) && isPxUndef(s.borderRightWidth));
};

/*
 * -----------------------------------  
 */
EPContainerEx.MessageProcessor.processScroll = function(scrollMessageElement) {
    var elementId = scrollMessageElement.getAttribute("eid");
    var position = parseInt(scrollMessageElement.getAttribute("position"),10);
    var divElement = document.getElementById(elementId);
    //
    // handle -1 as down to the bottom/right
    if (scrollMessageElement.nodeName == "scroll-horizontal") {
	    if (position < 0) {
	        position = 1000000;
	    }
        divElement.scrollLeft = position;
    } else if (scrollMessageElement.nodeName == "scroll-vertical") {
	    if (position < 0) {
	        position = 1000000;
	    }
        divElement.scrollTop = position;
    }
};

/*
 * -----------------------------------  
 */
EPContainerEx.prototype.eventHandler = function(echoEvent) {
	if (! EchoClientEngine.verifyInput(this.elementId)) {
        return;
    }	
	//debugger;
    EchoClientMessage.setPropertyValue(this.elementId, "horizontalScroll", this.divE.scrollLeft + "px");
    EchoClientMessage.setPropertyValue(this.elementId, "verticalScroll",  this.divE.scrollTop + "px");
};
