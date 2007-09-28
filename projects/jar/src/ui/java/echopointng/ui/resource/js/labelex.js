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

/* ------------------------------------- */
EPLabel = function(elementId) {
	this.elementId = elementId;
	EP.ObjectMap.put(elementId, this); 
};

EPLabel.ACTIVATE_ON_DBLCLICK = 0;
EPLabel.ACTIVATE_ON_DELAYEDCLICK = 1;

EPLabel.currentLabel = null;

/**
 * EPLabel has a ServerMessage processor.
 */
EPLabel.MessageProcessor = function() { };

EPLabel.MessageProcessor.process = function(messagePartElement) {
    for (var i = 0; i < messagePartElement.childNodes.length; ++i) {
        if (messagePartElement.childNodes[i].nodeType == 1) {
            switch (messagePartElement.childNodes[i].tagName) {
	            case "init":
	                EPLabel.MessageProcessor.processInit(messagePartElement.childNodes[i]);
	                break;
	            case "dispose":
	                EPLabel.MessageProcessor.processDispose(messagePartElement.childNodes[i]);
	                break;
           }
        }
    }
};

EPLabel.MessageProcessor.processDispose = function(disposeMessageElement) {
    for (var itemXML = disposeMessageElement.firstChild; itemXML; itemXML = itemXML.nextSibling) {
        var elementId = itemXML.getAttribute("eid");
		EP.ObjectMap.destroy(elementId);
    }
};

EPLabel.MessageProcessor.processInit = function(initMessageElement) {
    for (var itemXML = initMessageElement.firstChild; itemXML; itemXML = itemXML.nextSibling) {
        var elementId = itemXML.getAttribute("eid");
		EP.ObjectMap.destroy(elementId);
		var obj = new EPLabel(elementId);
		obj.init(itemXML);
    }
};

/* ------------------------------------- */
EPLabel.prototype.init = function(itemXML) {
	this.labelE = document.getElementById(this.elementId);
	this.editable = itemXML.getAttribute('editable') == 'true';
	if (this.editable) {
		this.interpretNewlines = itemXML.getAttribute('interpretNewlines') == 'true';
		this.editableContainerE = document.getElementById(this.elementId + "|EditableContainer");
		this.isTextComponent = itemXML.getAttribute("isTextComponent");
		this.activationMethod = itemXML.getAttribute("activationMethod"); 
		this.text = itemXML.getAttribute('text');
		this.synchronisedWithTarget = itemXML.getAttribute('synchronisedWithTarget') == 'true';
		this.targetId = itemXML.getAttribute('targetId')
	}	
	this.defaultStyle = itemXML.getAttribute('defaultStyle');
	this.defaultIcon = itemXML.getAttribute('defaultIcon');
	
	this.rolloverEnabled = itemXML.getAttribute('rolloverEnabled') == 'true';
	this.rolloverStyle = itemXML.getAttribute('rolloverStyle');
	this.rolloverIcon = itemXML.getAttribute('rolloverIcon');
	
	if (this.rolloverEnabled) {
		EP.Event.addHandler('mouseover',this.labelE,this);
		EP.Event.addHandler('mouseout',this.labelE,this);
	}	
	if (this.targetId) {
		if (this.activationMethod == EPLabel.ACTIVATE_ON_DBLCLICK) {
			EP.Event.addHandler('dblclick',this.labelE,this);
		} else {
			EP.Event.addHandler('mousedown',this.labelE,this);
		}
		if (! this.rolloverEnabled) {
			// we need the mouseout always to allow the user
			// to move/click away from the edit area
			EP.Event.addHandler('mouseout',this.labelE,this);
		}
	}
};



/* ------------------------------------- */
EPLabel.prototype.destroy = function() {
	EP.Event.removeHandler('mouseover',this.labelE);
	EP.Event.removeHandler('mouseout',this.labelE);
	EP.Event.removeHandler('mousedown',this.labelE);
	EP.Event.removeHandler('dblclick',this.labelE);
	
	if (EPLabel.currentLabel == this) {
		EP.DocumentEvent.removeHandler("mousedown","EPLabelDocHandler");
		EP.DocumentEvent.removeHandler("keydown","EPLabelDocHandler");
		EPLabel.currentLabel = null;
	}
};

/* ------------------------------------- */
EPLabel.prototype.getIconElement = function(elementE) {
	if (elementE.tagName && elementE.tagName.toLowerCase() == 'img') {
		return elementE;
	}
	for (elementE  = elementE.firstChild; elementE; elementE = elementE.nextSibling) {
		var childE = this.getIconElement(elementE);
		if (childE) {
			return childE
		}
	}
	return null;
};

/* ------------------------------------- */
EPLabel.prototype.getTextElement = function() {
	return document.getElementById(this.elementId + "|Text");
};

/**
 * This interface method is present on EPNG components
 * that can return a textual representation of their current
 * value.  
 */
EPLabel.prototype.getTextRepresentation = function() {
	return this.text;	
};

/* ------------------------------------- */
EPLabel.prototype.setLabelText = function(text) {
	if (text == this.text || ! this.synchronisedWithTarget) {
		return;
	}
	this.text = text;
	// find the first TextNode of the element and set it to the text
	var textPart = this.getTextElement();
	if (textPart) {
		var newNode;
		if (this.interpretNewlines) {
			// run through the text and create an array of text nodes to be added
			var docFrag  = document.createDocumentFragment();
			var nodes = EP.DOM.encodeNewlines(text,true);
			for(var i=0; i<nodes.length; i++) {
				docFrag.appendChild(nodes[i]);
			}
			newNode = docFrag;
		} else {
			newNode = document.createTextNode(text);
		}
		// remove all nodes of parent that are text nodes and <br/> tags
		EP.DOM.removeChildren(textPart);
		textPart.appendChild(newNode);
	}
};

EPLabel.prototype.isTargetTextArea = function() {
	var targetE = document.getElementById(this.targetId);
	if (targetE && targetE.tagName) {
		if (targetE.tagName.toLowerCase() == 'textarea') {
			return true;
		}
	}
	return false;
};

EPLabel.prototype.getTargetText = function() {
	var text = null;
	if (this.synchronisedWithTarget && this.targetId) {
		// try the EP object map first and see if it has getTextRepresentation() method
		var obj = EP.ObjectMap.get(this.targetId);
		if (obj && obj.getTextRepresentation) {
			text = obj.getTextRepresentation();
		} else {
			var targetE = document.getElementById(this.targetId);
			if (targetE && targetE.tagName) {
				// ok use HTML tags
				if (targetE.tagName.toLowerCase() == 'input') {
					text = targetE.value;
				}
				if (targetE.tagName.toLowerCase() == 'textarea') {
					text = targetE.value;
				}
			}
		}
	}
	return text;	
};

EPLabel.prototype.setTargetText = function(text) {
	if (this.targetId) {
		// try the EP object map first and see if it has getTextRepresentation() method
		var obj = EP.ObjectMap.get(this.targetId);
		if (obj && obj.setTextRepresentation) {
			obj.setTextRepresentation(text);
		} else {
			// ok use HTML tags
			var targetE = document.getElementById(this.targetId);
			if (this.isTextComponent && targetE && targetE.tagName) {
				if (targetE.tagName.toLowerCase() == 'input' || targetE.tagName.toLowerCase() == 'textarea') {
					targetE.value = text;
					// we will take a guess that its a Echo2 TextComponent
					if (EchoTextComponent && EchoTextComponent.updateClientMessage) {
						EchoTextComponent.updateClientMessage(targetE);
					}
			}
			}
		}
	}
};

EPLabel.prototype.onEditStart = function() {
	// hide the label bit 
	EP.setDisplayed(this.labelE,false);
	
	// and show the editable bit
	EP.setDisplayed(this.editableContainerE,true);
	var targetE = document.getElementById(this.targetId);
	if (targetE && targetE.focus) {
		targetE.focus();
	}
	
	// dynamically add an document event handler to listen for Esc or mousedown
	this.saveText = this.getTargetText();
	EPLabel.currentLabel = this;
	EP.DocumentEvent.addHandler("mousedown","EPLabelDocHandler",EPLabel.documentEventHandler);
	EP.DocumentEvent.addHandler("keydown","EPLabelDocHandler",EPLabel.documentEventHandler);
};



EPLabel.prototype.onEditCancelled = function() {
	this.setTargetText(this.saveText);
	this.saveText = null;
	
	EPLabel.curretLabel = null;
	
	// get rid of our document event handlers
	EP.DocumentEvent.removeHandler("mousedown","EPLabelDocHandler");
	EP.DocumentEvent.removeHandler("keydown","EPLabelDocHandler");
	EP.setDisplayed(this.editableContainerE,false);
	EP.setDisplayed(this.labelE,true);
};

EPLabel.prototype.onEditFinished = function() {
	EPLabel.currentLabel = null;
	this.saveText = null;
	
	// get rid of our document event handlers
	EP.DocumentEvent.removeHandler("mousedown","EPLabelDocHandler");
	EP.DocumentEvent.removeHandler("keydown","EPLabelDocHandler");

	EP.setDisplayed(this.editableContainerE,false);
	EP.setDisplayed(this.labelE,true);
	
	// update contents of Label from target
	if (this.synchronisedWithTarget && this.targetId) {
		var text = this.getTargetText();
		if (text != null) {
			this.setLabelText(text);
		}
	}
};

/**
 * Our document level event handler
 */
EPLabel.documentEventHandler = function(echoEvent) {
	if (EPLabel.currentLabel) {
		var labelObj = EPLabel.currentLabel;
		if (echoEvent.type == 'mousedown') {
			if (EP.isAncestorOf(echoEvent.target,labelObj.editableContainerE)) {
				// if we clicked on outselves keep going
				return;
			}
			labelObj.onEditFinished();
		}
		if (echoEvent.type == 'keydown') {
			// tab and enter
			var kc = echoEvent.keyCode;
			// tab ends the editing as does enter but enter only works
			// if is not a multi-line edit field
			if (kc == 9 || (kc == 13 && ! labelObj.isTargetTextArea()) ) {
				
				if (EP.isIE) {
					var doItLater = function() {
						labelObj.onEditFinished();
					}
					// take our time for IE otherwise seems to eat the enter key that 
					// should go to the contained component.
					window.setTimeout(doItLater,100);
				} else {
					labelObj.onEditFinished();
				}
			}
			if (kc == 27) {
				labelObj.onEditCancelled();
			}
		}
	}
};

/* ------------------------------------- */
EPLabel.prototype.eventHandler = function(echoEvent) {
	if (! EchoClientEngine.verifyInput(this.elementId)) {
        return;
    }	
	if (echoEvent.type == 'mousedown') {
		if (this.mousedownTimeOut) {
			window.clearTimeout(this.mousedownTimeOut);
		}

		var that = this;
		var func = function() {
			that.onEditStart();
		}
		this.mousedownTimeOut = window.setTimeout(func,500);
	}
	if (echoEvent.type == 'dblclick') {
		this.onEditStart();
	}
	
	if (echoEvent.type == 'mouseover') {
		if (this.rolloverEnabled) {
			EchoCssUtil.applyStyle(this.labelE,this.rolloverStyle);
			elementE = this.getIconElement(this.labelE);
			if (elementE) {
				elementE.src = this.rolloverIcon;
			}
		}
	}
	if (echoEvent.type == 'mouseout') {
		// if they mouseout before we asynch show the edit area, then too bad!
		if (this.mousedownTimeOut) {
			window.clearTimeout(this.mousedownTimeOut);
		}
		if (this.rolloverEnabled) {
			EchoCssUtil.applyStyle(this.labelE,this.defaultStyle);
			elementE = this.getIconElement(this.labelE);
			if (elementE) {
				elementE.src = this.defaultIcon;
			}
		}
	}
};
