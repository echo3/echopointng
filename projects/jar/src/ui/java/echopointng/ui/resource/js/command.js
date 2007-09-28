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

//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
// Our EPCommand object.
//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
EPCommand = function() {};

/**
 * EPCommand has a ServerMessage processor.
 */
EPCommand.MessageProcessor = function() {};

EPCommand.MessageProcessor.process = function(messagePartElement) {
	//debugger;
    for (var i = 0; i < messagePartElement.childNodes.length; ++i) {
        if (messagePartElement.childNodes[i].nodeType == 1) {
            switch (messagePartElement.childNodes[i].tagName) {
            case "attributesAdd":
                EPCommand.MessageProcessor.attributesAdd(messagePartElement.childNodes[i]);
                break;
           case "print":
                EPCommand.MessageProcessor.print(messagePartElement.childNodes[i]);
                break;
            case "evalJS":
                EPCommand.MessageProcessor.evalJS(messagePartElement.childNodes[i]);
                break;
            case "includeJS":
                EPCommand.MessageProcessor.includeJS(messagePartElement.childNodes[i]);
                break;
           }
        }
    }
};

EPCommand.MessageProcessor.attributesAdd = function(itemE) {
	var elementId = itemE.getAttribute("eid");
	var componentE = document.getElementById(elementId);
	if (componentE) {
		for (attrE = itemE.firstChild; attrE; attrE = attrE.nextSibling) {
			var attrName = attrE.getAttribute("name");
			var attrValue = attrE.getAttribute("value");
			// this works better on most browsers as far as I can tell
			// and it allows people who dont use getAttribute() to use
			// the more direct element.attrName to access the attribute
			//componentE.setAttribute(attrName,attrValue);
			eval('componentE.'+attrName+' = "' + attrValue + '";');
		}
	}
};

EPCommand.MessageProcessor.print = function(itemE) {
	window.print();
};

EPCommand.MessageProcessor.evalJS = function(itemE) {
	var cdata = itemE.firstChild;
	var jsScript = cdata.data;
	var closure = function() {
		eval(jsScript);
	}
	// Done as asynch call to allow Echo2 message processing 
	// to finish first.  No please wait message this way.
	window.setTimeout(closure,100);
};

EPCommand.MessageProcessor.includeJS = function(itemE) {
	var javascriptURI = itemE.getAttribute("javascriptURI");
	var scriptE = document.createElement("script");
	scriptE.setAttribute("src",javascriptURI);
	scriptE.setAttribute("type","text/javascript");
	
	document.getElementsByTagName('head')[0].appendChild(scriptE);
};

