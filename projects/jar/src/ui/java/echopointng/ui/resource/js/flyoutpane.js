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


EPFlyoutPane = function(elementId) {
	this.elementId = elementId;
	EP.ObjectMap.put(elementId, this); 
};


EPFlyoutPane.MessageProcessor = function() {};			// receives and processes echo2 server to client messages
EPFlyoutPane.EventProcessor = function() {};			// handles events


/**
 * MessageProcessor process() implementation (invoked by ServerMessage processor).
 *
 * @param messagePartElement the <code>message-part</code> element to process.
 */
EPFlyoutPane.MessageProcessor.process = function(messagePartElement) {

    for (var i = 0; i < messagePartElement.childNodes.length; i++) {
    
        if (messagePartElement.childNodes[i].nodeType == 1) {
        	var tagName = messagePartElement.childNodes[i].tagName.toLowerCase();
            switch (tagName) {
	            case "init":
	                EPFlyoutPane.MessageProcessor.processInit(messagePartElement.childNodes[i]);
	                break;
	            case "expanded":
	                EPFlyoutPane.MessageProcessor.processExpanded(messagePartElement.childNodes[i]);
	                break;
	            case "dispose":
	                EPFlyoutPane.MessageProcessor.processDispose(messagePartElement.childNodes[i]);
	                break;
            }
        }
    }
};


/**
 * Processes an <code>init</code> message to initialise the state of a 
 * Tree component that is being added.
 *
 * @param messageElement the <code>init</code> element to process
 */
EPFlyoutPane.MessageProcessor.processInit = function(messageElement) {

	for (var itemXML = messageElement.firstChild; itemXML; itemXML = itemXML.nextSibling) {
		var elementId = itemXML.getAttribute("eid");
		var flyout = new EPFlyoutPane(elementId);
		flyout.init(itemXML);
	}
};


/**
 * Processes a <code>dispose</code> message to finalize the state of a
 * Tree component that is being removed.
 *
 * @param messageElement the <code>dispose</code> element to process
 */
EPFlyoutPane.MessageProcessor.processDispose = function(messageElement) {
	
    for (var itemXML = messageElement.firstChild; itemXML; itemXML = itemXML.nextSibling) {
		var elementId = itemXML.getAttribute("eid");
		EP.ObjectMap.destroy(elementId);   		
    }
};

EPFlyoutPane.MessageProcessor.processExpanded = function(messageElement) {
	for (var item = messageElement.firstChild; item; item = item.nextSibling) {
		var elementId = item.getAttribute("eid");
	}
};

/*
 * Initilaise the flyout based on the XML message
 */
EPFlyoutPane.prototype.init = function(itemXML) {
	this.containerId = EP.DOM.getAttr(itemXML,'containerId');

	this.horizontalAlignment = EP.DOM.getIntAttr(itemXML,'horizontalAlignment',EP.TOP);
	this.verticalAlignment = EP.DOM.getIntAttr(itemXML,'verticalAlignment',EP.LEFT);
	this.orientation = EP.DOM.getIntAttr(itemXML,'orientation',EP.VERTICAL);
	this.alwaysInView = EP.DOM.getBooleanAttr(itemXML,'alwaysInView',true);
	this.expanded = EP.DOM.getBooleanAttr(itemXML,'expanded',true);

	
	this.width = EP.DOM.getAttr(itemXML,'width','50%');
	this.height = EP.DOM.getAttr(itemXML,'height','50%');
	this.offset = EP.DOM.getAttr(itemXML,'offset','0px');
	this.iconOffset = EP.DOM.getAttr(itemXML,'iconOffset','0px');
	
	this.icon = EP.DOM.getAttr(itemXML,'icon','about:blank');
	this.rolloverIcon = EP.DOM.getAttr(itemXML,'rolloverIcon','blank');
	this.iconWidth = EP.DOM.getAttr(itemXML,'iconWidth','25px');
	this.iconHeight = EP.DOM.getAttr(itemXML,'iconHeight','25px');
	this.iconAlignment = EP.DOM.getIntAttr(itemXML,'iconAlignment',EP.CENTER);
	
	// create the DOM elements
	this.createDOM();
	this.positionFlyoutAndSizeContent();
};

EPFlyoutPane.prototype.destroy = function() {
	
}

EPFlyoutPane.prototype.alignStr = function(alignment) {
	return (alignment == EP.LEFT ? 'left' :
			(alignment == EP.RIGHT ? 'right' :
			 (alignment == EP.TOP ? 'top' :
			  (alignment == EP.BOTTOM ? 'bottom' : 'unknown')
			  )));
}

EPFlyoutPane.prototype.reportOnThyself = function() {
	var ppE = this.findPositionedParent();
	var flyoutE = document.getElementById(this.elementId);
	var flyoutContentE = document.getElementById(this.elementId + 'Content');
	var iconE = document.getElementById(this.elementId + 'Icon');
	var lf = (document.all ? '</br>' : '\n');

	var content = '';
	content += '<pre>';

	content += 'ha='+ this.alignStr(this.horizontalAlignment) + 
				' va=' + this.alignStr(this.verticalAlignment) + lf;
				
	content += 'or='+(this.orientation == EP.HORIZONTAL ? 'horzontal' : 'vertical') + lf;
	//content += 'icon al='+this.alignStr(this.iconAlignment)  + lf;
	
	content += 'offset='+this.offset + lf;
	content += 'icon offset='+this.iconOffset + lf;
	
	content += 'content left='+flyoutContentE.style.left + lf;
	content += 'content top='+flyoutContentE.style.top + lf;
	content += 'content offsetWidth='+flyoutContentE.offsetWidth + lf;
	content += 'content offsetHeight='+flyoutContentE.offsetHeight + lf;

	content += '--------------' + lf
	content += 'flyoutE.left='+flyoutE.style.left + lf;
	content += 'flyoutE.top='+flyoutE.style.top + lf;
	content += 'flyoutE.offsetWidth='+flyoutE.offsetWidth + lf;
	content += 'flyoutE.offsetHeight='+flyoutE.offsetHeight + lf;
	
	content += '--------------' + lf
	content += 'ppE clientWidth='+ppE.clientWidth + lf;
	content += 'ppE clientHeight='+ppE.clientHeight + lf;
	content += 'ppE scrollWidth='+ppE.scrollWidth + lf;
	content += 'ppE scrollHeight='+ppE.scrollHeight + lf;
	content += 'ppE scrollLeft='+ppE.scrollLeft + lf;
	content += 'ppE scrollTop='+ppE.scrollTop + lf;
	
	content += '</pre>';
	
	flyoutContentE.innerHTML = content;		
};

/**
 * Goes up the DOM tree to find the parent element that is positioned
 * eg has po
 */
EPFlyoutPane.prototype.findPositionedParent = function() {
	var parentE = document.getElementById(this.containerId);
	for (; parentE;) {
		parentStyle = parentE.style;
		if (parentStyle.position == "absolute" || parentStyle.position == "relative") {
			break;
		}
		parentE = parentE.parentNode;
	}
	return parentE;
};

/**
 * Converts a value into pixels from percentages etc..
 * 
 * It returns a value with out the 'px' ending
 */
EPFlyoutPane.prototype.toPx = function(value, maxValue) {
	if (value.indexOf('px') >= 0) {
		return parseInt(value,10);
	}
	if (value.indexOf('%') >= 0) {
		// must be %
		var valueInt = parseInt(value,10);
		var pxs = maxValue * (valueInt / 100);
		return Math.ceil(pxs);
	}
	throw 'These values are not yet supported ' + value;
};

/**
 * 
 */
EPFlyoutPane.prototype.createDOM = function() {
	var parentE = document.getElementById(this.containerId);
	if (! parentE) {
		throw 'The parent element : ' + this.containerId + ' could not be found';
	}

	//debugger;
	var flyoutE = document.createElement('div');
	flyoutE.id = this.elementId;
	flyoutE.style.position = 'absolute';
	flyoutE.style.visibility = 'hidden';
	flyoutE.style.overflow = 'hidden';
	flyoutE.style.margin = '0px';
	flyoutE.style.padding = '0px';
	
	flyoutE.style.backgroundColor = 'blue';
	parentE.appendChild(flyoutE);
	
	// create the flyout pane content
	var flyoutContentE = document.createElement('div');
	flyoutContentE.id = this.elementId + 'Content';
	flyoutContentE.style.position = 'absolute';
	flyoutContentE.style.visibility = 'hidden';
	flyoutContentE.style.overflow = 'hidden';
	flyoutContentE.style.margin = '0px';
	flyoutContentE.style.padding = '0px';
	
	flyoutContentE.style.backgroundColor = 'red';
	flyoutE.appendChild(flyoutContentE);

	// create the flyout handle content
	var flyoutHandleE = document.createElement('div');
	flyoutHandleE.id = this.elementId + 'Handle';
	flyoutHandleE.style.position = 'absolute';
	flyoutHandleE.style.visibility = 'hidden';
	flyoutHandleE.style.overflow = 'hidden';
	flyoutHandleE.style.margin = '0px';
	flyoutHandleE.style.padding = '0px';
	
	flyoutHandleE.style.backgroundColor = 'yellow';
	flyoutE.appendChild(flyoutHandleE);
	
	// create icon image tag
	//var iconE = document.createElement('img');
	var iconE = document.createElement('div');
	iconE.id = this.elementId + 'Icon';
	//iconE.src = this.icon;
	iconE.style.position = 'absolute';
	iconE.style.visibility = 'hidden';
	iconE.style.backgroundColor = 'green';

	if (! this.iconWidth) {
		this.iconWidth = '25px';
	}
	if (! this.iconHeight) {
		this.iconHeight = '25px';
	}
	//
	// our handle area is always the same width/height as the icon depending on the orientation
	iconE.style.width = this.iconWidth;
	iconE.style.height = this.iconHeight;
	flyoutHandleE.appendChild(iconE);
	
	//
	// how wide should our flyout be for starters
	this.calculateOuterDimensions();
	if (this.expanded) {
		flyoutE.style.width = this.flyoutMaxWidth+'px';
		flyoutE.style.height = this.flyoutMaxHeight+'px';
	} else {
		flyoutE.style.width = this.flyoutMinWidth+'px';
		flyoutE.style.height = this.flyoutMinHeight+'px';
	}
	//
	// event attachment.  
	var that = this;
	var clickHandler = function() {
		that.renderTargetState(! that.expanded);
		that.expanded = ! that.expanded;
	}
	EchoEventProcessor.addHandler(iconE, 'click', clickHandler, false)
	
	if (this.alwaysInView) {
		that.scrollEventPendingId = null;
		var callbackFunc = function() {
			that.scrollEventPendingId = null;
			that.positionFlyoutAndSizeContent();
		};
		var scrollHandler = function() {
			callbackFunc();
		};
		var ppE = this.findPositionedParent();
		EchoEventProcessor.addHandler(ppE, 'scroll', scrollHandler, false)
	}
};

/**
 * This will calculated dimensions or the flyout outer div and sets them 
 * into the object. This will become the "dimensions" that the flyout will 
 * expand and shrink to.  
 */
EPFlyoutPane.prototype.calculateOuterDimensions = function() {
	//debugger;
	var iconE = document.getElementById(this.elementId + 'Icon');

	var ppE = this.findPositionedParent();
	var ppSW = ppE.scrollWidth;
	var ppSH = ppE.scrollHeight;

	var ppCW = ppE.clientWidth;
	var ppCH = ppE.clientHeight;

	// adjust the total available size by the w/h of the icon handle
	if (this.orientation == EP.VERTICAL) {
		ppCW -= iconE.offsetWidth;
	} else {
		ppCH -= iconE.offsetHeight;
	}
	
	// work out size of the flyout area
	this.flyoutMaxWidth = this.toPx(this.width,ppCW);
	this.flyoutMaxHeight = this.toPx(this.height,ppCH);

	this.flyoutMinWidth = iconE.offsetWidth;
	this.flyoutMinHeight = iconE.offsetHeight;
};

/**
 * This is called to render the flyout to its desired
 * state (expanded or not expanded).  It uses a Fx
 * in order to do this
 */
EPFlyoutPane.prototype.renderTargetState = function(isExpanding) {
	//
	var flyoutE = document.getElementById(this.elementId);
	var fx = new EP.Fx({
		duration : 1000,
		fps : 50
	});

	var that = this;
	var reposTween = {
		onFrame:function() {
			that.positionFlyoutAndSizeContent();
		},
		onStop: function() {
			that.positionFlyoutAndSizeContent();
		}
	};

	var dimensionTween = null;
	var from = 0;
	var to = 0;
	if (this.orientation == EP.VERTICAL) {
		dimensionTween = new EP.Fx.CssTween(flyoutE,'width','px');			
		if (isExpanding) {
			from = this.flyoutMinWidth;
			to = this.flyoutMaxWidth;
		} else {
			from = this.flyoutMaxWidth;
			to = this.flyoutMinWidth;
		}
	} else {
		dimensionTween = new EP.Fx.CssTween(flyoutE,'height','px');			
		if (isExpanding) {
			from = this.flyoutMinHeight;
			to = this.flyoutMaxHeight;
		} else {
			from = this.flyoutMaxHeight;
			to = this.flyoutMinHeight;
		}
	}	
	fx.addTween(dimensionTween,from,to);
	fx.addTween(reposTween,from,to);
	fx.start();
	
};

/**
 * Called to position the flyout based on its current width and height
 * and to take into effect the current scroll parameters of the 
 * positioned parent container.  This is expected to be called
 * inside a EP.Fx to position the areas based on a expanding/shrinking
 * flyout width/height, with the assumption that the width/height has already 
 * been set.
 * 
 */
EPFlyoutPane.prototype.positionFlyoutAndSizeContent = function() {
	//debugger;
	var flyoutE = document.getElementById(this.elementId);
	var flyoutContentE = document.getElementById(this.elementId + 'Content');
	var flyoutHandleE = document.getElementById(this.elementId + 'Handle');
	var iconE = document.getElementById(this.elementId + 'Icon');

	flyoutE.style.visibility = 'hidden';

	var ppE = this.findPositionedParent();
	var ppSW = ppE.scrollWidth;
	var ppSH = ppE.scrollHeight;

	var ppCW = ppE.clientWidth;
	var ppCH = ppE.clientHeight;

	// work out size of the content area and the handle area.  The content area is
	// defined as the flyout area minus the width/height of the handle area.  The handle
	// area is always as wide/high as the handle icon, depending on orientation.
	//
	// We work this out all the time so we can "expand/shrink" in a nice way.  The
	// handle doesnt change size but the content does.
	//
	//debugger;
	var fw = flyoutE.offsetWidth;
	var fh = flyoutE.offsetHeight;
	var iw =  iconE.offsetWidth;
	var ih =  iconE.offsetHeight;
	
	if (this.orientation == EP.VERTICAL) {
		flyoutHandleE.style.width = iw+'px';
		flyoutHandleE.style.height = fh+'px';
		flyoutContentE.style.height = fh+'px';
	} else {
		flyoutHandleE.style.height = ih+'px';
		flyoutHandleE.style.width = fw+'px';
		flyoutContentE.style.width  = fw+'px';
	}
	var cw = (fw - iw);
	var ch = (fh - ih);
	if (this.orientation == EP.VERTICAL) {
		flyoutContentE.style.width = cw +'px';
	} else {		
		flyoutContentE.style.height = ch + 'px';
	}
	//
	// we use top/left and right/bottom positioning of the content and handle areas
	// within the flyout.
	/*
	 * =============================================================
	 * =X----------------|---X               X---|----------------X=
	 * =|                |   |               |   |                |=
	 * =|                |   |               |   |                |=
	 * =|                | H |               | H |                |=
	 * =|                | a |               | a |                |=
	 * =|  Content       | n |               | n |     Content    |=
	 * =|                | d |               | d |                |=
	 * =|                | l |               | l |                |=
	 * =|                | e |               | e |                |=
	 * =|                |   |               |   |                |=
	 * =|----------------|---|               |---|----------------|=
	 * =|    Handle      |   |               |   |  Handle        |=
	 * =X----------------|---|               X---|----------------|=
	 * =                                                           =
	 * =                                                           =
	 * =============================================================
	 */
	var ha = this.horizontalAlignment;
	var va = this.verticalAlignment;
	var or = this.orientation;
	if (ha == EP.TOP && va == EP.LEFT && or == EP.VERTICAL) {
		flyoutContentE.style.top = '0px';
		flyoutContentE.style.left = '0px';
		flyoutHandleE.style.top   = '0px';
		flyoutHandleE.style.right = '0px';
	}
	else if (ha == EP.TOP && va == EP.LEFT && or == EP.HORIZONTAL) {
		flyoutContentE.style.top = '0px';
		flyoutContentE.style.left = '0px';
		flyoutHandleE.style.bottom   = '0px';
		flyoutHandleE.style.left  = '0px';
	}
	else if (ha == EP.TOP && va == EP.RIGHT && or == EP.VERTICAL) {
		flyoutContentE.style.top = '0px';
		flyoutContentE.style.right = '0px';
		flyoutHandleE.style.top   = '0px';
		flyoutHandleE.style.left = '0px';
	}
	else if (ha == EP.TOP && va == EP.RIGHT && or == EP.HORIZONTAL) {
		flyoutContentE.style.top = '0px';
		flyoutContentE.style.left = '0px';
		flyoutHandleE.style.bottom= '0px';
		flyoutHandleE.style.left = '0px';
	}
	/*
	 * =============================================================
 	 * =                                                           =
 	 * =                                                           =
 	 * =                                                           =
	 * =|----------------|---|               |---|----------------|=
	 * =|    Handle      |   |               |   |  Handle        |=
	 * =|----------------|---|               |---|----------------|=
	 * =|                |   |               |   |                |=
	 * =|                | H |               | H |                |=
	 * =|                | a |               | a |                |=
	 * =|                | n |               | n |                |=
	 * =|                | d |               | d |                |=
	 * =|                | l |               | l |                |=
	 * =|                | e |               | e |                |=
	 * =|                |   |               |   |                |=
	 * =|----------------|---|               |---|----------------|=
	 * =============================================================
	 */
	else if (ha == EP.BOTTOM && va == EP.LEFT && or == EP.VERTICAL) {
		flyoutContentE.style.top = '0px';
		flyoutContentE.style.left = '0px';
		flyoutHandleE.style.top= '0px';
		flyoutHandleE.style.right = '0px';
	}
	else if (ha == EP.BOTTOM && va == EP.LEFT && or == EP.HORIZONTAL) {
		flyoutContentE.style.bottom = '0px';
		flyoutContentE.style.left = '0px';
		flyoutHandleE.style.top= '0px';
		flyoutHandleE.style.left = '0px';
	}
	else if (ha == EP.BOTTOM && va == EP.RIGHT && or == EP.VERTICAL) {
		flyoutContentE.style.top = '0px';
		flyoutContentE.style.right = '0px';
		flyoutHandleE.style.top= '0px';
		flyoutHandleE.style.left = '0px';
	}
	else if (ha == EP.BOTTOM && va == EP.RIGHT && or == EP.HORIZONTAL) {
		flyoutContentE.style.bottom = '0px';
		flyoutContentE.style.left = '0px';
		flyoutHandleE.style.top= '0px';
		flyoutHandleE.style.left = '0px';
	}
	//
	// now work out where the outer flyoutE should go with respect to the
	// postioned parent.  We postion outselves with respect to the "viewport"
	// of the pp,  not its "scrollport".  
	var offset = this.toPx(this.offset,this.orientation == EP.VERTICAL ? ppCH : ppCW);
	var iconOffset = this.toPx(this.iconOffset,this.orientation == EP.VERTICAL ? ppCH : ppCW);
	
	//debugger;
	var x = 0;
	var y = 0;
	if (this.horizontalAlignment == EP.TOP) {
		if (this.verticalAlignment == EP.LEFT) {
			x = 0; y = 0;
			if (this.orientation == EP.VERTICAL) {
				// ha = top va=left or=vertical  ---------------------------
				y += offset;
			} else {
				// ha = top va=left or=horizontal  ---------------------------
				x += offset;
			}
		} else {
			x = ppSW - flyoutE.offsetWidth; y = 0;
			if (this.orientation == EP.VERTICAL) {
				//---- ha = top va=right or=vertical ---------------------------
				y += offset;
			} else {
				//---- ha = top va=right or=horzontal ---------------------------
				x -= offset;
			}
		}
	} else {
		if (this.verticalAlignment == EP.LEFT) {
			x = 0; y = ppSH - flyoutE.offsetHeight;
			if (this.orientation == EP.VERTICAL) {
				// ha = bottom va=left or= vertical ---------------------------
				y -= offset;
			} else {
				// ha = bottom va=left or= horizontal ---------------------------
				x += offset;
			}
		} else {
			x = ppSW - flyoutE.offsetWidth; y = ppSH - flyoutE.offsetHeight;
			if (this.orientation == EP.VERTICAL) {
				// ha = bottom va=right or=vertical ---------------------------
				y -= offset;
			} else {
				// ha = bottom va=right or=vertical ---------------------------
				x += offset;
			}
		}
	}
	if (this.alwaysInView) {
		//debugger;
		if (this.verticalAlignment == EP.LEFT) {
			x += ppE.scrollLeft;
		} else {
			x -= (ppE.scrollWidth - ppE.clientWidth);
			x += ppE.scrollLeft;
		}
		if (this.horizontalAlignment == EP.TOP) {
			y += ppE.scrollTop;
		} else {
			y -= (ppE.scrollWidth - ppE.clientHeight);
			y += ppE.scrollTop;
		}
	}
	flyoutE.style.left = x +'px';
	flyoutE.style.top = y +'px';

	// TODO 
	// Position icon inside the handle area based on iconOrientation
	//debugger;
	var style = iconE.style;
	var offset  = this.toPx(this.iconOffset,flyoutHandleE);
	if (this.orientation == EP.VERTICAL) {
		if (this.iconAlignment == EP.BOTTOM) {
			style.left = 0 + 'px';
			style.bottom = (0 + offset)+ 'px';
		} else if (this.iconAlignment == EP.CENTER) {
			style.left = 0 + 'px';
			var val = parseInt((flyoutHandleE.offsetHeight/2) - (iconE.offsetHeight /2));
			style.top = (val + offset)+ 'px';
		} else {
			style.left = 0 + 'px';
			style.top = (0 + offset)+ 'px';
		}
	} else {
		if (this.iconAlignment == EP.RIGHT) {
			style.right = (0 + offset)+ 'px';
			style.top = 0+'px';
		} else if (this.iconAlignment == EP.CENTER) {
			var val = parseInt((flyoutHandleE.offsetWidth/2) - (iconE.offsetWidth /2));
			style.left = (val + offset)+ 'px';
			style.top = 0 + 'px';
		} else {
			style.left = (0 + offset)+ 'px';
			style.top = 0+'px';
		}
	}

	//
	// make things visible	
	flyoutE.style.visibility = 'visible';
	flyoutContentE.style.visibility = 'visible';
	flyoutHandleE.style.visibility = 'visible';
	iconE.style.visibility = 'visible';
	
	//this.reportOnThyself();
};

