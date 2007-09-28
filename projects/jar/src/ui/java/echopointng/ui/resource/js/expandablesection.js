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
// Our EPExpandableSection object.
//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
EPExpandableSection = function(elementId) { 
	this.elementId = elementId;
	EP.ObjectMap.put(elementId, this); 
};


EPExpandableSection.groupIdArrayMap = [];

/**
 * EPExpandableSection has a ServerMessage processor.
 */
EPExpandableSection.MessageProcessor = function() { };

EPExpandableSection.MessageProcessor.process = function(messagePartElement) {
    for (var i = 0; i < messagePartElement.childNodes.length; ++i) {
        if (messagePartElement.childNodes[i].nodeType == 1) {
            switch (messagePartElement.childNodes[i].tagName) {
	            case "init":
	                EPExpandableSection.MessageProcessor.processInit(messagePartElement.childNodes[i]);
	                break;
	            case "dispose":
	                EPExpandableSection.MessageProcessor.processDispose(messagePartElement.childNodes[i]);
	                break;
	            case "expansion":
	                EPExpandableSection.MessageProcessor.processExpansion(messagePartElement.childNodes[i]);
	                break;
            }
        }
    }
};

EPExpandableSection.MessageProcessor.processDispose = function(disposeMessageElement) {
    for (var item = disposeMessageElement.firstChild; item; item = item.nextSibling) {
        var elementId = item.getAttribute("eid");
		EP.ObjectMap.destroy(elementId);
    }
};

EPExpandableSection.MessageProcessor.processExpansion = function(disposeMessageElement) {
    for (var item = disposeMessageElement.firstChild; item; item = item.nextSibling) {
		//debugger;
        var elementId = item.getAttribute("eid");
        var expanded = (item.getAttribute("expanded")=='true');
        EP.debug('ExpandableSection expansion : ' + elementId + ' - ' + expanded);
		var es = EP.ObjectMap.get(elementId);
		if (es != null) {
			// this assumes that we will get a expanded and a contracted message
			// and hence the semantics of accordian mode will be honoured.  We dont
			// close the group in this case.
			if (expanded) {
				EP.setDisplayed(es.contentE,true);
			} else {
				EP.setDisplayed(es.contentE,false);
			}
			es.saveState();
		}
    }
};

EPExpandableSection.MessageProcessor.processInit = function(initMessageElement) {
	//debugger;
    for (var item = initMessageElement.firstChild; item; item = item.nextSibling) {
        var elementId = item.getAttribute("eid");
        EP.debug('ExpandableSection init : ' + elementId);
		EP.ObjectMap.destroy(elementId);
		var es = new EPExpandableSection(elementId);
        if (item.getAttribute("enabled") == "false") {
            EchoDomPropertyStore.setPropertyValue(elementId, "EchoClientEngine.inputDisabled", true);
        }
		
		var groupId = item.getAttribute("groupId");
		if (groupId) {
			es.setButtonGroup(elementId,groupId);
		}
		es.accordionMode = ( item.getAttribute("accordionMode") == 'true');
		es.titleBarId = item.getAttribute("titleBarId")
		es.contentE = document.getElementById(elementId+'|Content');
    }
};

EPExpandableSection.prototype.setButtonGroup = function(elementId, groupId) {
    var esArray = EPExpandableSection.groupIdArrayMap[groupId];
    if (!esArray) {
    	esArray = [];
    	EPExpandableSection.groupIdArrayMap[groupId] = esArray;
    }
    esArray.push(elementId);
    this.groupId = groupId;
};

/**
 * Closes all the others in the group except us
 */
EPExpandableSection.prototype.deselectGroup = function() {
	if (this.groupId) {
	    var esArray = EPExpandableSection.groupIdArrayMap[this.groupId];
	    for (var i = 0; i < esArray.length; ++i) {
	        var elementId = esArray[i];
			var es = EP.ObjectMap.get(elementId);
			if (es != null && es != this) {
				es.hideContent();
			}
	    }
	}
};

/**
 * Is the content expanded (showing) or not
 */
EPExpandableSection.prototype.isExpanded = function() {
	return EP.isDisplayed(this.contentE);
};

EPExpandableSection.prototype.hideContent = function() {
	EP.setDisplayed(this.contentE,false);
	var tb = EP.ObjectMap.get(this.titleBarId);
	if (tb) {
		tb.updateState(false);
	}
}

EPExpandableSection.prototype.saveState = function() {
	EP.Event.setClientValue(this.elementId,"expanded",'' + this.isExpanded());
};

/**
 * This is called by the TitleBar to tell us to change our expansion state
 * We have to tell it what state we went to after this all finished, so
 * the TitleBar can updated its UI.
 */
EPExpandableSection.prototype.onTitleBarExpansion = function(isExpanded) {
	//debugger;
	// close all the others in the group
	this.deselectGroup();
	if (this.accordionMode) {
		// if we are already showing and they want to close us, then too bad
		// cause accordions dont work like that
		if (this.isExpanded()) {
			;
		} else {
			EP.setDisplayed(this.contentE,true);
		}
	} else {
		if (isExpanded) {
			EP.setDisplayed(this.contentE,true);
		} else {
			EP.setDisplayed(this.contentE,false);
		}
	}
	this.saveState();
	return this.isExpanded();
};
