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
// Our EPCssCommand object.
//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
EPCssCommand = function() {};

/**
 * EPCssCommand has a ServerMessage processor.
 */
EPCssCommand.MessageProcessor = function() {};

EPCssCommand.MessageProcessor.process = function(messagePartElement) {
	//debugger;
    for (var i = 0; i < messagePartElement.childNodes.length; ++i) {
        if (messagePartElement.childNodes[i].nodeType == 1) {
            switch (messagePartElement.childNodes[i].tagName) {
	            case "addStyle":
	                EPCssCommand.MessageProcessor.addStyle(messagePartElement.childNodes[i]);
	                break;
	            case "addStyleValue":
	            	EPCssCommand.MessageProcessor.addStyleValue(messagePartElement.childNodes[i]);
	                break;
	            case "addStyleSheet":
	                EPCssCommand.MessageProcessor.addStyleSheet(messagePartElement.childNodes[i]);
	                break;
	            case "applyTo":
	                EPCssCommand.MessageProcessor.applyTo(messagePartElement.childNodes[i]);
	                break;
	            case "removeFrom":
	                EPCssCommand.MessageProcessor.removeFrom(messagePartElement.childNodes[i]);
	                break;
 	            case "removeStyle":
	                EPCssCommand.MessageProcessor.removeStyle(messagePartElement.childNodes[i]);
	                break;
            }
        }
    }
};

EPCssCommand.MessageProcessor.addStyle = function(itemE) {
	var elementId = itemE.getAttribute("eid");
	var styleE = itemE.firstChild;
	
	EP.StyleSheet.addStyle(elementId, styleE, 0);
};

EPCssCommand.MessageProcessor.addStyleValue = function(itemE) {
	var elementId = itemE.getAttribute("eid");
	var styleText = itemE.getAttribute("styleText");
	
	var elementE = document.getElementById(elementId);
	if (elementE) {
		EchoCssUtil.applyStyle(elementE,styleText);
	}
};


EPCssCommand.MessageProcessor.addStyleSheet = function(itemE) {
	var elementId = itemE.getAttribute("eid");
	var linkE = itemE.firstChild;
	
	EP.StyleSheet.addLink(elementId, linkE, 0);
};

EPCssCommand.MessageProcessor.removeStyle = function(itemE) {
	var elementId = itemE.getAttribute("eid");
	EP.StyleSheet.removeAllStyles(elementId);
};

EPCssCommand.MessageProcessor.applyTo = function(itemE) {
	var elementId = itemE.getAttribute("eid");
	var className = itemE.getAttribute("className");
	
	var elementE = document.getElementById(elementId);
	if (elementE) {
		if (elementE.className) {
			className = elementE.className +" "+className;
		}
		elementE.className = className;
	}
};

EPCssCommand.MessageProcessor.removeFrom = function(itemE) {
	var elementId = itemE.getAttribute("eid");
	var className = itemE.getAttribute("className");
	
	var elementE = document.getElementById(elementId);
	if (elementE) {
		if (elementE.className) {
			var newClassName = '';
			var values = elementE.className.split(' ');
			for (var i = 0; i < values.length; i++) {
				if (values[i] != className) {
					newClassName += values[i];
					if (i > 0)
						newClassName += ' ';
				}
			}
			elementE.className = newClassName;
		}
	}
};

