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
// Our EPDirectHtml object.
//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
EPDirectHtml = function() {};

/**
 * EPDirectHtml has a ServerMessage processor.
 */
EPDirectHtml.MessageProcessor = function() {};

EPDirectHtml.MessageProcessor.process = function(messagePartElement) {
	//debugger;
    for (var i = 0; i < messagePartElement.childNodes.length; ++i) {
        if (messagePartElement.childNodes[i].nodeType == 1) {
            switch (messagePartElement.childNodes[i].tagName) {
            case "init":
                EPDirectHtml.MessageProcessor.init(messagePartElement.childNodes[i]);
                break;
            case "dispose":
                EPDirectHtml.MessageProcessor.dispose(messagePartElement.childNodes[i]);
                break;
           }
        }
    }
};

EPDirectHtml.MessageProcessor.init = function(item) {
    var elementId = item.getAttribute("eid");
    var containerId = item.getAttribute("container-eid");
   	var containerE = document.getElementById(containerId);
    if (containerE) {
    	if (item.firstChild) {
		    // it has XHTML text so use it
	    	var htmlText = item.firstChild.data;
    		containerE.innerHTML = htmlText; 
	    } else {
	    	// set it to empty if it has no text
    		containerE.innerHTML = ''; 
	    }
    }
};

EPDirectHtml.MessageProcessor.dispose = function(item) {
};


