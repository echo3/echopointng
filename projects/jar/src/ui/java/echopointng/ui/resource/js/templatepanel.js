
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



//========================================================================================================
// class : EPTemplatePanel
//
// Our top level TemplatePanel class does nothing yet!
//========================================================================================================
EPTemplatePanel = function(elementId) {
};


/**
 * Called to debug the stylesheets on offer.  Not needed for production code
 */
EPTemplatePanel.debugStyleSheets = function() {
	var sslen = document.styleSheets.length;
	alert('document.styleSheets.length : ' + sslen);
	for ( i = 0; i < sslen; i++ ) {
		var styleSheet = document.styleSheets[i];
		var s = '';
		s += 'document.styleSheets[' + i + '].title = ' + styleSheet.title;
		s += '\n';
		s += 'document.styleSheets[' + i + '].href = ' + styleSheet.href;
		if (styleSheet.cssText) {
			s += '\n';
			s += 'document.styleSheets[' + i + '].cssText = ' + styleSheet.cssText;
		}
		s += '\n';
		s += 'document.styleSheets[' + i + '].disabled = ' + styleSheet.disabled;
		EP.debug(s);
		alert(s);
	}
};

/**
 * Removes any scripts that the document that have our element id
 * @param elementId - the TemplatePanel element id
 */
EPTemplatePanel.removeScripts = function(elementId) {
	var headE = document.getElementsByTagName("head")[0];
	var scripts = headE.getElementsByTagName("script");
	for(index = scripts.length-1; index >= 0; index--) {
		var id = scripts[index].getAttribute('epngtemplateid');
		if (id && id.indexOf(elementId) == 0) {
			headE.removeChild(scripts[index]);
			id = null;
		}
	}
};



/**
 * EPTemplatePanel has a ServerMessage processor.
 */
EPTemplatePanel.MessageProcessor = function() { };

EPTemplatePanel.MessageProcessor.process = function(messagePartElement) {
	//debugger;
    for (var i = 0; i < messagePartElement.childNodes.length; ++i) {
        if (messagePartElement.childNodes[i].nodeType == 1) {
            switch (messagePartElement.childNodes[i].tagName) {
            case "init":
            	EPTemplatePanel.MessageProcessor.processInit(messagePartElement.childNodes[i]);
                break;
            case "dispose":
            	EPTemplatePanel.MessageProcessor.processDispose(messagePartElement.childNodes[i]);
                break;
            }
        }
    }
};

EPTemplatePanel.MessageProcessor.processDispose = function(disposeMessageElement) {
	//debugger;
	for (var item = disposeMessageElement.firstChild; item; item = item.nextSibling) {
        var elementId = item.getAttribute("eid");
		EP.ObjectMap.destroy(elementId);
        EP.StyleSheet.removeAllStyles(elementId);
        EPTemplatePanel.removeScripts(elementId);
        
    }
};

EPTemplatePanel.MessageProcessor.processInit = function(initMessageElement) {
	//debugger;
    for (var item = initMessageElement.firstChild; item; item = item.nextSibling) {
        var elementId = item.getAttribute("eid");
		EP.ObjectMap.destroy(elementId);
        //
        // remove any previous stylesheets that might exist with this elementId
        // just in case, we have changed template data without removing the template component
        //
        //EP.StyleSheet.removeAllStyles(elementId);
        //EPTemplatePanel.removeScripts(elementId);
        
        var index = 0;
        //
        // <link> fixup
        var externalStyleContainer = item.getElementsByTagName("externalStyleContainer");
        for (var i = 0; i < externalStyleContainer.length; i++) {
        	for (var linkE = externalStyleContainer[i].firstChild; linkE; linkE = linkE.nextSibling) {
        		EP.StyleSheet.addLink(elementId, linkE, index);
        		index++;
        	}
        	
        }
         
        //
        // <style> fixup
        var inlineStyleContainer = item.getElementsByTagName("inlineStyleContainer");
        for (i = 0; i < inlineStyleContainer.length; i++) {
        	for (var styleE = inlineStyleContainer[i].firstChild; styleE; styleE = styleE.nextSibling) {
        		EP.StyleSheet.addStyle(elementId, styleE,index);
        		index++;
        	}
        }
        
        
        //
        // script fixups
        var headE = document.getElementsByTagName("head")[0];
        var externalScriptContainer = item.getElementsByTagName("externalScriptContainer");
        for (i = 0; i < externalScriptContainer.length; i++) {
        	for (var scriptE = externalScriptContainer[i].firstChild; scriptE; scriptE = scriptE.nextSibling) {
        		// add to the head
		        var newScriptE = null;
		        try {
	        		newScriptE = document.createElement("script");
	    			EP.DOM.copyNode(newScriptE,scriptE);
	        		newScriptE.setAttribute('epngtemplateid',elementId+"_"+index);
		        } catch (e) {
		        	//
		        	// IE has a bug where the innerHTML or document.createText() cannot
		        	// be used to dynamically insert script code into the browser.
		        	// It throws an unknown runtime exception.  So we catch it and ignore
		        	// it since there is nothing we can do until they fix IE
		        	//
		        	EP.debug("TemplatePanel JS import exception : " + e);
		        }
        	 	if (newScriptE) {
        	 		headE.appendChild(newScriptE);
        	 	}
 	      		index++;
        	}
        }
        // uncomment this out to see what is contained within the head as far as style go
        //EPTemplatePanel.debugStyleSheets();
    }
};

