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

/***
 * USAGE:
 * 
 * on component init:
 *  stretcher = EP.Stretch.verticalStretcher.getInstance(elementId);
 * 
 * on component disposal:
 * 	stretcher.destroy();
 */
 
EP.Stretch = function() {};

EP.Stretch.MessageProcessor = function() {};

EP.Stretch.MessageProcessor.process = function(messagePartElement) {
    for (var i = 0; i < messagePartElement.childNodes.length; ++i) {
        if (messagePartElement.childNodes[i].nodeType == 1) {
            switch (messagePartElement.childNodes[i].tagName) {
            case "init":
                EP.Stretch.MessageProcessor.processInit(messagePartElement.childNodes[i]);
                break;
            case "dispose":
                EP.Stretch.MessageProcessor.processDispose(messagePartElement.childNodes[i]);
                break;
            }
        }
    }
};

EP.Stretch.MessageProcessor.processInit = function(messageElement) {
    for (var itemXML = messageElement.firstChild; itemXML; itemXML = itemXML.nextSibling) {
        var elementId = itemXML.getAttribute("eid");
	    var heightStretched = EP.DOM.getBooleanAttr(itemXML,"heightStretched",false);
	    if (heightStretched) {
		    var minHeight = EP.DOM.getIntAttr(itemXML,"minimumStretchedHeight",null);
		    var maxHeight = EP.DOM.getIntAttr(itemXML,"maximumStretchedHeight",null);
	    	var stretcher = EP.Stretch.verticalStretcher.getInstance(elementId, minHeight, maxHeight);
	    }
    }
};

EP.Stretch.MessageProcessor.processDispose = function(messageElement) {
    for (var itemXML = messageElement.firstChild; itemXML; itemXML = itemXML.nextSibling) {
        var elementId = itemXML.getAttribute("eid");
        var stretcher = EP.Stretch.verticalStretcher._getByElementId(elementId);
        if (stretcher) {
        	stretcher.destroy();
        }
    }
};


/**
 * Replacement function for EchoVirtualPosition.redraw that mixes our vertical stretchers with 
 * virtual-positioned elements. Allows you to place a virtual-positioned control (eg SplitPane)
 * inside a vertically stretchable EPNG control (eg ContainerEx) and have it all redraw correctly
 * on a browser window resize.
 */	
EchoVirtualPosition.redraw = function(element) {

    if (element != undefined) {
        EchoVirtualPosition.adjust(element);
		
    } else {
	    var removedIds = false;
    	var updatedIdList = [];
		
        if (!EchoVirtualPosition.elementIdListSorted) {
            EP.Stretch.sort();
        }

        for (var i = 0; i < EchoVirtualPosition.elementIdList.length; i++) {
			
			var elementId = EchoVirtualPosition.elementIdList[i];
			if (elementId === null) {
				removedIds = true;
				continue;
			}
			
			var vStretcher = EP.Stretch.verticalStretcher._getByElementId(elementId);
			if (vStretcher) {
				if (EP.isIE) {
					// IE6 behaves a lot better if we resize elements on a timeout, even if it is tiny.
					// It sometimes does not resize synchronously because a parent element height has not been calculated yet.
					window.setTimeout(EP.Stretch.stretchProxy(elementId), 0);
				} else {
					vStretcher.stretch();
				}
				updatedIdList.push(elementId);
				
			} else {
				// Not a vertical stretcher - so it must be a regular EchoVirtualPosition element
	            element = document.getElementById(elementId);
				
	            if (element) {
					if (EP.isIE) {
						// IE6 behaves a lot better if we resize elements on a timeout, even if it is tiny.
						// It sometimes does not resize synchronously because a parent element height has not been calculated yet.
						window.setTimeout(EP.Stretch.EVPAdjustProxy(element), 0);
					} else {
	                	EchoVirtualPosition.adjust(element);
					}
					updatedIdList.push(elementId);
					
	            } else {
	                // Element no longer exists. Set 'removedIds' flag to true such that elementIdList will
	                // be pruned for nulls once redrawing has been completed.
					EchoVirtualPosition.elementIdSet.remove(elementId);
	                removedIds = true;
	            }
			}
        }
        
        // Update id list if necessary.
        if (removedIds) {
            EchoVirtualPosition.elementIdList = updatedIdList;
        }
    }

};


/**
 * Sorts virtual positioned and stretched elementIds into the correct dom order prior to repositioning.
 */
EP.Stretch.sort = function() {

	// First "mark" elements that are virtual positioned so we don't have to scan a set repeatedly.
	// This makes the search time linearly related to dom nodes size rather than a cartesian product.
	for (var elementId in EchoVirtualPosition.elementIdSet.associations) {
		var el = document.getElementById(elementId);
		if (el) {
			el.e2VP = true;
		}
	}
	
	// Call the orginal EchoVirtualPosition.sort
    EchoVirtualPosition.sort();
};


/**
 * Overrides EchoVirtualPosition.sortImpl making it far more efficient for large dom trees.
 * Doesn't use EchoVirtualPosition.elementIdSet because it causes a cartesian product between 
 * dom nodes and set size when checking to see if an element is virtual positioned. Instead it
 * checks for elements with a custom element attribute "e2VP" which is set before the sort begins.
 */
EchoVirtualPosition.sortImpl = function(element, sortedList) {
    // If element has id and element is marked with our custom attribute (much faster than using set)
	if (element.id && element.e2VP) {
        sortedList.push(element.id);
    }
    
    for (var child = element.firstChild; child; child = child.nextSibling) {
        if (child.nodeType == 1) {
            EchoVirtualPosition.sortImpl(child, sortedList);
        }
    }
};


/**
 * @return a function that can be used to stretch an element at a later time (eg using setTimeout)
 */
EP.Stretch.stretchProxy = function(elementId) {
    return function() {
		var vStretcher = EP.Stretch.verticalStretcher._getByElementId(elementId);
		if (vStretcher) {
			vStretcher.stretch();
		}
	};
};


/**
 * @return a function that can be used to call EchoVirtualPosition.adjust(element) at a later time (eg using setTimeout)
 */
EP.Stretch.EVPAdjustProxy = function(element) {
    return function() {
		if (EP.isIE) {
			// Fix IE bug which sometimes leaves SplitPane invisible on first redraw because
			// the parentNode's offsetHeight is 0, even when it has a valid height set.
			element.parentNode.style.zoom = 0;
			element.parentNode.style.zoom = 1;
		}
		EchoVirtualPosition.adjust(element);
	};
};


/**
 * Global list of elements which must be vertically stretched on browser resize
 */
EP.Stretch.verticalStretchersList = [];


/**
 * Stretches a block element so that it vertically fills its nearest 
 * "positioned" parent with defined height.
 */
EP.Stretch.verticalStretcher = function(elementId, minHeight, maxHeight) {
	this.initialise(elementId, minHeight, maxHeight);	
};


/**
 * @return a vertical stretcher for the given element. Creates a new one if necessary.
 */
EP.Stretch.verticalStretcher.getInstance = function(elementId, minHeight, maxHeight) {

	var stretcher = EP.Stretch.verticalStretcher._getByElementId(elementId);
	if (!stretcher) {
		stretcher = new EP.Stretch.verticalStretcher(elementId, minHeight, maxHeight);
	}
	return stretcher;
};


/**
 * @return a previously created EP.Stretch.verticalStretcher for the specified DOM element ID
 */
EP.Stretch.verticalStretcher._getByElementId = function(elementId) {

	var arr = EP.Stretch.verticalStretchersList;
	for (var i = 0; i < arr.length; i++) {
		if (arr[i].elementId === elementId) {
			return arr[i];
		}
	}
	return null;
};


/**
 * A prototypical vertical Stretcher
 */
EP.Stretch.verticalStretcher.prototype = {
	
	initialise: function(elementId, minHeight, maxHeight) {
		this.elementId = elementId;
		this.minHeight = parseInt(minHeight, 10);
		this.maxHeight = parseInt(maxHeight, 10);
		if (! isNaN(this.minHeight) && ! isNaN(this.maxHeight)) {
			this.minHeight = Math.min(this.minHeight,this.maxHeight);
			this.maxHeight = Math.max(this.minHeight,this.maxHeight);
		}
		// store a global reference to this stretcher so it can be accessed on resize
		EP.Stretch.verticalStretchersList.push(this);
		
		// We're "piggybacking" EchoVirtualPosition so enable it even if we're using a browser that doesn't need it.
		// We do this so we can put an EchoVirtualPosition'd component inside a v.stretched one (eg SplitPane inside ContainerEx)
		if (!EchoVirtualPosition.enabled) {
			EchoVirtualPosition.init();
		}
		EchoVirtualPosition.register(elementId);
	},
		
	destroy: function() {
		var arr = EP.Stretch.verticalStretchersList;
		for (var i = 0; i < arr.length; i++) {
			if (arr[i] === this) {
				arr.splice(i, 1);
			}
		}
		
		// "unregister" from EchoVirtualPosition
		EchoVirtualPosition.elementIdSet.remove(this.elementId);
		
		for (var i = 0; i < EchoVirtualPosition.elementIdList.length; i++) {
			if (this.elementId == EchoVirtualPosition.elementIdList[i]) {
				EchoVirtualPosition.elementIdList[i] = null;
				break;
			}
		}
		
		this.elementId = null;
	},
	
	/**
	 * This can be called to add a callback function that is called "before" the 
	 * height is stretched.  This allows a user of this code to "tweak" the height.
	 * 
	 * The callback function sytax is
	 * 
	 *  var tweakedHeight = stretchCallback(newHeight, context, verticalStretcher);
	 * 
	 * 	where 
	 * 		newHeight is the calculated new height
	 * 		context is an arbitary context object
	 * 		verticalStretcher is the stretcher object making the callback
	 *
	 * If this callback returns -1, then it is assumed it either doesnt want the
	 * resize to occur or it has done the resize itself according to its own rules.
	 */
	addBeforeStretchCallback : function(stretchCallbackFunction, stretchCallbackContext) {
		this.stretchBeforeCallbackFunction = stretchCallbackFunction;
		this.stretchBeforeCallbackContext = stretchCallbackContext;
	},

	/**
	 * Same as addBeforeStretchCallback except is its called after the stretch
	 * has taken place.
	 */
	addAfterStretchCallback : function(stretchCallbackFunction, stretchCallbackContext) {
		this.stretchAfterCallbackFunction = stretchCallbackFunction;
		this.stretchAfterCallbackContext = stretchCallbackContext;
	},
	
	/**
	 * Can be called to return the positioned parent that will be "stretched" to fill.
	 */
	getPositionedParent : function() {
		var element = document.getElementById(this.elementId);
		if (! element) {
			return null;
		}
		for (var parentE = element.parentNode; parentE; parentE = parentE.parentNode) {
			var s = parentE.style;
			if (s.height || ((s.position == "absolute" || s.position == "fixed") && s.top && s.bottom)) {
				break;
			}
		}
		return parentE;
	},

	/**
	 * Returns the height of the content of parentE.
	 */
	getContentHeight : function(parentE) {
		var contentHeight = 0;
		for (var i = 0; i < parentE.childNodes.length; i++) {
			var node = parentE.childNodes[i];
			// Ignore text, floated, and absolutely positioned nodes
			if (node.nodeType === 1) {
				var s = node.style;
				if (!s.styleFloat && !s.cssFloat && s.position != "absolute" && s.position != "fixed") {
					contentHeight += node.offsetHeight;
				}
			}
		}
		return contentHeight;
	},
	
	stretch: function() {
		var element = document.getElementById(this.elementId);
		if (! element) {
			return;
		}
		// Find nearest parent with a specified height, 
		// or absolute positioning (could have top & bottom specified)
		var parentE = this.getPositionedParent();
		if (! parentE) {
			return;
		}
		var parentStyle = parentE.style;
				
		// Temporarily override overflow to stop scrollbar flash
		var eStyle = element.style;
		var saveOverflow = eStyle.overflow;
		var saveParentOverflow = parentStyle.overflow;
		eStyle.overflow = parentStyle.overflow = "hidden";

		// Ensure we are using pixels
		if (!EchoVirtualPosition.verifyPixelValue(eStyle.height)) {
			eStyle.height = element.clientHeight + "px";
		}
		
		// Determine parentE's available height and current height of all child content
		var availableHeight = parentE.clientHeight;
		var contentHeight = this.getContentHeight(parentE);
		
		// Adjust the child element's height to the "gap" between available and content height
		var newHeight = Math.max(0, parseInt(eStyle.height, 10) + availableHeight - contentHeight);
		
		// respect max and min height if they are present
		var respectedLimits = false;
		if (this.minHeight && ! isNaN(this.minHeight) && newHeight < this.minHeight) {
			newHeight = this.minHeight;
			respectedLimits = true;
		}
		if (this.maxHeight && ! isNaN(this.maxHeight) && newHeight > this.maxHeight) {
			newHeight = this.maxHeight;
			respectedLimits = true;
		}
		//
		// we allow users to "customise" the desired height "before" we set it.  This do this
		// by providing a optional "stretchCallback" function
		if (this.stretchBeforeCallbackFunction) {
			newHeight = this.stretchBeforeCallbackFunction(newHeight, this.stretchBeforeCallbackContext, this);
		}
		if (newHeight > 0) {
			//
			// now set the height into the element
			eStyle.height = newHeight + "px";
	
			if (! respectedLimits) {
				// Firefox sometimes does not include margins in offsetHeight so adjust again if we created some overflow
				eStyle.height = Math.max(0, newHeight + parentE.offsetHeight - parentE.scrollHeight) + "px";
			}
		}
		//
		// restore the over flow back to the previous values
		eStyle.overflow = saveOverflow;
		parentStyle.overflow = saveParentOverflow;
		
		if (this.stretchAfterCallbackFunction) {
			newHeight = parseInt(eStyle.height, 10);
			this.stretchAfterCallbackFunction(newHeight, this.stretchAfterCallbackContext, this);
		}
		
	}
};



