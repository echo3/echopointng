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
// Our EPTitleBar object.
//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
EPTitleBar = function(elementId) { 
	this.elementId = elementId;
	EP.ObjectMap.put(elementId, this); 
};

/**
 * EPTitleBar has a ServerMessage processor.
 */
EPTitleBar.MessageProcessor = function() { };

EPTitleBar.MessageProcessor.process = function(messagePartElement) {
    for (var i = 0; i < messagePartElement.childNodes.length; ++i) {
        if (messagePartElement.childNodes[i].nodeType == 1) {
            switch (messagePartElement.childNodes[i].tagName) {
	            case "init":
	                EPTitleBar.MessageProcessor.processInit(messagePartElement.childNodes[i]);
	                break;
	            case "dispose":
	                EPTitleBar.MessageProcessor.processDispose(messagePartElement.childNodes[i]);
	                break;
	            case "expansion":
	                EPTitleBar.MessageProcessor.processExpansion(messagePartElement.childNodes[i]);
	                break;
            }
        }
    }
};

EPTitleBar.MessageProcessor.processDispose = function(disposeMessageElement) {
	//debugger;
    for (var item = disposeMessageElement.firstChild; item; item = item.nextSibling) {
        var elementId = item.getAttribute("eid");
		EP.ObjectMap.destroy(elementId);
    }
};

EPTitleBar.prototype.destroy = function() {
	var element = document.getElementById(this.elementId);
	if (element) {
    	EP.Event.removeHandler("mouseover", element);
    	EP.Event.removeHandler("mouseout", element);
	}
	element = document.getElementById(this.elementId + '|LeftIcon');
	if (element) {
    	EP.Event.removeHandler("click", element);
	}	
	element = document.getElementById(this.elementId + '|RightIcon');
	if (element) {
    	EP.Event.removeHandler("click", element);
	}	
	element = document.getElementById(this.elementId + '|Title');
	if (element) {
    	EP.Event.removeHandler("click", element);
	}	
}

EPTitleBar.MessageProcessor.processExpansion = function(disposeMessageElement) {
    for (var item = disposeMessageElement.firstChild; item; item = item.nextSibling) {
        var elementId = item.getAttribute("eid");
		var tb = EP.ObjectMap.get(elementId);
		if (tb != null  && this.partnerComponentId == null) {
			tb.expanded = (item.getAttribute("expanded") == 'true');
			tb.updateIcons();
		}
    }
};

EPTitleBar.MessageProcessor.processInit = function(initMessageElement) {
	//debugger;
    for (var item = initMessageElement.firstChild; item; item = item.nextSibling) {
        var elementId = item.getAttribute("eid");
		EP.ObjectMap.destroy(elementId);
		var tb = new EPTitleBar(elementId);
        if (item.getAttribute("enabled") == "false") {
            EchoDomPropertyStore.setPropertyValue(elementId, "EchoClientEngine.inputDisabled", true);
        }
		tb.expanded = (item.getAttribute("expanded") == 'true');
		
		tb.partnerComponentId = item.getAttribute("partnerComponentId");
		
		tb.styleDefault = item.getAttribute("styleDefault");
		tb.styleRollover = item.getAttribute("styleRollover");
		tb.rolloverEnabled = item.getAttribute("rolloverEnabled") == 'true';
		
		tb.leftIcon = item.getAttribute("leftIcon");
		tb.leftExpandedIcon = item.getAttribute("leftExpandedIcon");

		tb.rightIcon = item.getAttribute("rightIcon");
		tb.rightExpandedIcon = item.getAttribute("rightExpandedIcon");

		tb.title = item.getAttribute("title");
		tb.expandedTitle = item.getAttribute("expandedTitle");
		
		tb.leftIconE = document.getElementById(elementId + '|LeftIcon');
		tb.rightIconE = document.getElementById(elementId + '|RightIcon');
		tb.titleE = document.getElementById(elementId + '|Title');
		
		if (tb.leftIconE) {
	    	EP.Event.addHandler("click", tb.leftIconE, tb);
		}
		if (tb.rightIconE) {
	    	EP.Event.addHandler("click", tb.rightIconE, tb);
		}
		if (tb.titleE) {
	    	EP.Event.addHandler("click", tb.titleE, tb);
		}
		
		if (tb.rolloverEnabled) {
			element = document.getElementById(elementId);
	    	EP.Event.addHandler("mouseover", element, tb);
	    	EP.Event.addHandler("mouseout", element, tb);
		}
    }
};

/**
 * Updates the icons to reflect the current expansion state
 */
EPTitleBar.prototype.updateIcons = function() {
	if (this.leftIconE) {
		this.leftIconE.src = (this.expanded ? this.leftExpandedIcon : this.leftIcon);
	}
	if (this.rightIconE) {
		this.rightIconE.src = (this.expanded ? this.rightExpandedIcon : this.rightIcon);
	}
	if(this.titleE) {
		//debugger;
		var titleText = (this.expanded ? this.expandedTitle : this.title)
		var currentTextNode = this.titleE.firstChild;
		if (currentTextNode == null) {
			var newTextNode = document.createTextNode(titleText);
			this.titleE.appendChild(newTextNode);
		} else {
			if (currentTextNode.nodeValue != titleText) {			
				var newTextNode = document.createTextNode(titleText);
				this.titleE.replaceChild(newTextNode,currentTextNode);
			}
		}
	}
};

EPTitleBar.prototype.saveState = function(isExpanded) {
	EP.Event.setClientValue(this.elementId,"expanded",'' + isExpanded);
};

/**
 * This can be called to force the TitleBar to update
 * its state representation in UI terms
 */
EPTitleBar.prototype.updateState = function(isExpanded) {
	//debugger;
	this.expanded = isExpanded;
	this.updateIcons();
	this.saveState(this.expanded);
};

/**
 * Toggles our expansion state, just for the model back home in Java land
 */
EPTitleBar.prototype.toggle = function() {
	this.expanded = ! this.expanded;
	this.updateIcons();
	this.saveState(this.expanded);
};



/**
 * A direct event handler on the titlbar object itself.  If it has a partner
 * it will call the partner via 'onTitleBarExpansion'.  This is expected
 * to return the new "expansion" state.
 */
EPTitleBar.prototype.eventHandler = function(echoEvent) {
	if (! EchoClientEngine.verifyInput(this.elementId)) {
        return;
    }	
	if (echoEvent.type == 'click') {
		//
		// if we can find a partner object with an onTitleBarExpansion function
		// then we will call it
		//
		//debugger;
		var parenterObj = EP.ObjectMap.get(this.partnerComponentId);
		if (parenterObj && parenterObj.onTitleBarExpansion) {
			var newExpansionState = (! this.expanded);
			this.expanded = parenterObj.onTitleBarExpansion(newExpansionState);
			this.updateIcons();
			this.saveState(this.expanded);
		} else {
			this.toggle();
		}
	}
	if (echoEvent.type.indexOf('mouse') != -1) {
		var element = document.getElementById(this.elementId);
		if (echoEvent.type == 'mouseover') {
			EchoCssUtil.applyStyle(element,this.styleRollover);
		}
		if (echoEvent.type == 'mouseout') {
			EchoCssUtil.applyStyle(element,this.styleDefault);
		}
	}
};
