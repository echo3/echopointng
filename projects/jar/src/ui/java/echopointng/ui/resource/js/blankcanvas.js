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

EPBlankTemplate = function(elementId) {
	this.elementId = elementId;
	EP.ObjectMap.put(elementId, this); 
};

/**
 * EPBlankTemplate has a ServerMessage processor.
 */
EPBlankTemplate.MessageProcessor = function() { };

EPBlankTemplate.MessageProcessor.process = function(messagePartElement) {
    for (var i = 0; i < messagePartElement.childNodes.length; ++i) {
        if (messagePartElement.childNodes[i].nodeType == 1) {
            switch (messagePartElement.childNodes[i].tagName) {
	            case "init":
	                EPBlankTemplate.MessageProcessor.processInit(messagePartElement.childNodes[i]);
	                break;
	            case "dispose":
	                EPBlankTemplate.MessageProcessor.processDispose(messagePartElement.childNodes[i]);
	                break;
	            case "otherProperty":
	                EPBlankTemplate.MessageProcessor.processOtherProperty(messagePartElement.childNodes[i]);
	                break;
            }
        }
    }
};

EPBlankTemplate.MessageProcessor.processDispose = function(messageElement) {
    for (var itemXML = messageElement.firstChild; itemXML; itemXML = itemXML.nextSibling) {
        var elementId = itemXML.getAttribute("eid");
		EP.ObjectMap.destroy(elementId);
    }
};

/*
 * -----------------------------------  
 */
EPBlankTemplate.prototype.destroy = function() {
	//
	// delete any event handlers you may have
	var divE = document.getElementById(this.elementId);
	if (divE) {
		EP.Event.removeHandler('click',divE);
	}
};

/*
 * -----------------------------------  
 */
EPBlankTemplate.MessageProcessor.processOtherProperty = function(messageElement) {
    for (var itemXML = messageElement.firstChild; itemXML; itemXML = itemXML.nextSibling) {
        var elementId = itemXML.getAttribute("eid");
    }
};

EPBlankTemplate.MessageProcessor.processInit = function(messageElement) {
    for (var itemXML = messageElement.firstChild; itemXML; itemXML = itemXML.nextSibling) {
        var elementId = itemXML.getAttribute("eid");
		EP.ObjectMap.destroy(elementId);
		var clientObj = new EPBlankTemplate(elementId);
		clientObj.init(itemXML);
    }
};

/*
 * ---------------------------
 */
EPBlankTemplate.prototype.init = function(itemXML) {
    this.divE = document.getElementById(this.elementId);
    this.property1 = itemXML.getAttribute('property1');
    this.property2 = itemXML.getAttribute('property2');
    this.intProperty3 = parseInt(itemXML.getAttribute('intProperty3',10));
    this.booleanProperty4 = itemXML.getAttribute('booleanProperty4') == 'true';
    
	EP.Event.addHandler('click',this.divE,this);
};


/*
 * -----------------------------------  
 */
EPBlankTemplate.prototype.eventHandler = function(echoEvent) {
	if (! EchoClientEngine.verifyInput(this.elementId)) {
        return;
    }	
	//debugger;
};
