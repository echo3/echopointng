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

EPLightBox = function(elementId) {
	this.elementId = elementId;
	EP.ObjectMap.put(elementId, this); 
};

/**
 * EPLightBox has a ServerMessage processor.
 */
EPLightBox.MessageProcessor = function() { };

EPLightBox.MessageProcessor.process = function(messagePartElement) {
    for (var i = 0; i < messagePartElement.childNodes.length; ++i) {
        if (messagePartElement.childNodes[i].nodeType == 1) {
            switch (messagePartElement.childNodes[i].tagName) {
	            case "init":
	                EPLightBox.MessageProcessor.processInit(messagePartElement.childNodes[i]);
	                break;
	            case "dispose":
	                EPLightBox.MessageProcessor.processDispose(messagePartElement.childNodes[i]);
	                break;
	            case "hidden":
	                EPLightBox.MessageProcessor.processHidden(messagePartElement.childNodes[i]);
	                break;
            }
        }
    }
};

EPLightBox.MessageProcessor.processDispose = function(messageElement) {
    for (var itemXML = messageElement.firstChild; itemXML; itemXML = itemXML.nextSibling) {
        var elementId = itemXML.getAttribute("eid");
		EP.ObjectMap.destroy(elementId);
    }
};

EPLightBox.prototype.destroy = function() {
	var divE = document.getElementById(this.elementId);
	if (divE) {
		//EP.Event.removeHandler('mousedown',divE);
		//EP.Event.removeHandler('keydown',divE);
		
		// we need to remove the div because it is not parented like
		// other DHML elements in Echo2
		divE.parentNode.removeChild(divE);
	}
};

/*
 * -----------------------------------  
 */
EPLightBox.MessageProcessor.processHidden = function(messageElement) {
    for (var itemXML = messageElement.firstChild; itemXML; itemXML = itemXML.nextSibling) {
        var elementId = itemXML.getAttribute("eid");
	    var showLightBox  = itemXML.getAttribute('hidden') == 'false';
		var lightBoxObj = EP.ObjectMap.get(elementId);
		this.divE = document.getElementById(elementId);
		if (lightBoxObj && this.divE) {
			lightBoxObj.toggleLightBox(showLightBox);
		}
    }
};


EPLightBox.MessageProcessor.processInit = function(messageElement) {
    for (var itemXML = messageElement.firstChild; itemXML; itemXML = itemXML.nextSibling) {
        var elementId = itemXML.getAttribute("eid");
		EP.ObjectMap.destroy(elementId);
		var clientObj = new EPLightBox(elementId);
		clientObj.init(itemXML);
    }
};


/*
 * ---------------------------
 */
EPLightBox.prototype.init = function(itemXML) {
    var showLightBox  		= itemXML.getAttribute('hidden') == 'false';
   	var translucentImage 	= itemXML.getAttribute('translucentImage');
	this.enabled 			= itemXML.getAttribute('enabled') == 'true';
    var divE 				= document.getElementById(this.elementId);
   	if (! divE) {
   		// then create it if it not there
		divE = document.createElement('div');
		divE.id = this.elementId;
		divE.style.backgroundColor = 'transparent';
		divE.style.display = 'none';
		divE.style.position = 'absolute';
	    divE.style.left = "0px";
	    divE.style.top = "0px";
	    divE.style.margin = "0px";
	    divE.style.padding = "0px";
	    divE.style.cursor = 'wait';
	    
	    //EP.Event.addHandler("mousedown",divE,this);
	    //EP.Event.addHandler("keydown",divE,this);
	    	
    	if (document.all) {
    		var filterStr = 'progid:DXImageTransform.Microsoft.AlphaImageLoader(src="' + translucentImage + '",sizingMethod="scale");';
    		divE.style.filter = filterStr;
    		divE.style.backgroundColor = 'transparent';
    	} else {
	    	var overlayImg = document.createElement('img');
	    	overlayImg.style.width = "100%";
	    	overlayImg.style.height = "100%";
	    	overlayImg.style.left = "0px";
	    	overlayImg.style.top = "0px";
	    	overlayImg.src = translucentImage;
	    	divE.appendChild(overlayImg);
    	}
    	document.body.appendChild(divE);
    	this.divE = divE;
   	}
   	this.toggleLightBox(showLightBox);
};

/*
 * -----------------------------------  
 */
EPLightBox.prototype.showLightBox = function() {
	//
	// where do we insert outselves in the hierarchy
	var parentE = document.body;
	var zIndex = 0;
	if (EchoModalManager.modalElementId) {
		var modalE = document.getElementById(EchoModalManager.modalElementId);
		zIndex = EP.determineZ(modalE)
	}
	if (this.divE != parentE.firstChild) {
		parentE.insertBefore(this.divE,parentE.firstChild);
	}
	
	var screen = this.getScreenDimensions();
	this.divE.style.width = '100%';
	if (document.all) {	
		this.divE.style.width = screen[0] + 'px';
	}
	this.divE.style.height = screen[1] + 'px';
	EP.setDisplayed(this.divE,true);
}

EPLightBox.prototype.hideLightBox = function() {
	EP.setDisplayed(this.divE,false);
}


/*
 * -----------------------------------  
 */
EPLightBox.prototype.toggleLightBox = function(showLightBox) {
	if (showLightBox) {
		this.showLightBox();
	} else {
		this.hideLightBox();
	}
};


/*
 * -----------------------------------  
 */
EPLightBox.prototype.getScreenDimensions = function() {
	return EP.getPageDimensions();
};


/*
 * -----------------------------------  
 */
EPLightBox.prototype.eventHandler = function(echoEvent) {
	// we suck up all input type events!
	EP.Event.cancelEvent(echoEvent);
};
