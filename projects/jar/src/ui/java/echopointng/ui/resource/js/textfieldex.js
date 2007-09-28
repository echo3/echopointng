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

/*
 * This file was based on the original Echo2 TExtComponent.js and hence is copyright
 * NextApp Inc.  It was included in the EPNG on the 2/11/2006
 */

/* 
 * This file is part of the Echo Web Application Framework (hereinafter "Echo").
 * Copyright (C) 2002-2005 NextApp, Inc.
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

//_________________________
// Object EPTextFieldEx

/**
 * Creates a new text component data object.
 * 
 * @param elementId the id of the supported text component element
 */
EPTextFieldEx = function(elementId) {
    this.elementId = elementId;
	EP.ObjectMap.put(elementId, this); 
};

EPTextFieldEx.prototype.destroy = function() {
    var element = this.getElement();
    EchoEventProcessor.removeHandler(element, "blur");
    EchoEventProcessor.removeHandler(element, "focus");
    EchoEventProcessor.removeHandler(element, "keyup");
    EchoDomUtil.removeEventListener(element, "keypress", EPTextFieldEx.processKeyPress, false);    
    if (this.actionCausedOnChange) {
	    EchoEventProcessor.removeHandler(element, "change");
    }

    // Remove any updates to text component that occurred during client/server transaction.
    EchoClientMessage.removePropertyElement(element.id, "text");
    
    EchoDomPropertyStore.dispose(element);
    
    if (this.lookupSupport) {
    	EP.ObjectMap.destroy(this.lookupSupport.elementId);
    }
};

/**
 * Processes a user "action request" on the text component i.e., the pressing
 * of the ENTER key when the the component is focused.
 * If any server-side <code>ActionListener</code>s are registered, an action
 * will be set in the ClientMessage and a client-server connection initiated.
 */
EPTextFieldEx.prototype.doAction = function() {
    if (!this.serverNotify) {
        return false;
    }
    
    if (!this.enabled || !EchoClientEngine.verifyInput(this.getElement(), false)) {
        return false;
    }
    this.updateClientMessage();
    EchoClientMessage.setActionValue(this.elementId, "action");
    EchoServerTransaction.connect();
    return true;
};

EPTextFieldEx.prototype.getElement = function() {
    return document.getElementById(this.elementId);
};

EPTextFieldEx.prototype.init = function(itemXML) {
	this.text 					= EP.DOM.getAttr(itemXML,"text","");
	this.maximumLength 			= EP.DOM.getIntAttr(itemXML,"maximum-length",-1);
	this.horizontalScroll 		= EP.DOM.getIntAttr(itemXML,"horizontal-scroll",0); 
	this.verticalScroll 		= EP.DOM.getIntAttr(itemXML,"vertical-scroll",0); 
	this.enabled 				= EP.DOM.getBooleanAttr(itemXML,"enabled",true);
	this.serverNotify 			= EP.DOM.getBooleanAttr(itemXML,"server-notify",false);
	this.actionCausedOnChange 	= EP.DOM.getBooleanAttr(itemXML,'actionCausedOnChange',false);

	// do we have a lookup model
	var autoLookUpModelE = itemXML.getElementsByTagName('autoLookupModel');
	if (autoLookUpModelE.length > 0) {
		this.lookupSupport = new EPTextFieldEx.LookupSupport(this,autoLookUpModelE);
	}
	
	// - ok init the DOM elements
    var element = this.getElement();
    
    if (!this.enabled) {
        element.readOnly = true;
    }
    if (this.text) {
        element.value = this.text;
    }
    

    if (this.horizontalScroll != 0) {
        element.scrollLeft = this.horizontalScroll;
    }
    
    if (this.verticalScroll != 0) {
        if (EchoClientProperties.get("quirkIERepaint")) {
            // Avoid IE quirk where browser will fail to set scroll bar position.
            var originalWidth = element.style.width;
            var temporaryWidth = parseInt(element.clientWidth) - 1;
            element.style.width = temporaryWidth + "px";
            element.style.width = originalWidth;
        }
        element.scrollTop = this.verticalScroll;
    }
    
    if (EchoClientProperties.get("quirkMozillaTextInputRepaint")) {
        // Avoid Mozilla quirk where text will be rendered outside of text field
        // (this appears to be a Mozilla bug).
        var noValue = !element.value;
        if (noValue) {
            element.value = "-";
        }
        var currentWidth = element.style.width;
        element.style.width = "20px";
        element.style.width = currentWidth;
        if (noValue) {
            element.value = "";
        }
    }
    EchoDomPropertyStore.setPropertyValue(element, "component", this);
    
    EchoEventProcessor.addHandler(element, "blur", "EPTextFieldEx.processBlur");
    EchoEventProcessor.addHandler(element, "focus", "EPTextFieldEx.processFocus");
    EchoEventProcessor.addHandler(element, "keyup", "EPTextFieldEx.processKeyUp");
    EchoDomUtil.addEventListener(element, "keypress", EPTextFieldEx.processKeyPress, false);
    if (this.actionCausedOnChange) {
	    EchoEventProcessor.addHandler(element,  "change", "EPTextFieldEx.processChange");
    }
    
    // regex handling
    this.normalProperties 		= EchoDomUtil.getCssText(element);
	var regexStr 				= EP.DOM.getAttr(itemXML,"regex",null);
	this.regex					= regexStr ? new RegExp(regexStr) : null;
	this.regexFailureProperties	= EP.DOM.getAttr(itemXML,"regexFailureProperties",null);
	this.executeRegex();    
};


/**
 * Processes a focus blur event:
 * Records the current state of the text field to the ClientMessage.
 *
 * @param echoEvent the event, preprocessed by the 
 *        <code>EchoEventProcessor</code>
 */
EPTextFieldEx.prototype.processBlur = function(echoEvent) {
    if (!this.enabled || !EchoClientEngine.verifyInput(this.getElement())) {
        return;
    }
    this.updateClientMessage();
    EchoFocusManager.setFocusedState(this.elementId, false);
};

/**
 * Processes a focus event:
 * Notes focus state in ClientMessage.
 *
 * @param echoEvent the event, preprocessed by the 
 *        <code>EchoEventProcessor</code>
 */
EPTextFieldEx.prototype.processFocus = function(echoEvent) {
    if (!this.enabled || !EchoClientEngine.verifyInput(this.getElement())) {
        return;
    }
    EchoFocusManager.setFocusedState(this.elementId, true);
};

/**
 * Processes a key press event:
 * Initiates an action in the event that the key pressed was the
 * ENTER key.
 *
 * @param e the DOM Level 2 event
 */
EPTextFieldEx.prototype.processKeyPress = function(e) {
    if (!this.enabled || !EchoClientEngine.verifyInput(this.getElement(), true)) {
        EchoDomUtil.preventEventDefault(e);
        return;
    }
    var didAction = false;
    if (e.keyCode == 13) {
        didAction = this.doAction();
    }
    if (this.lookupSupport) {
    	this.lookupSupport.onkeypress(e,didAction);
    }
};

/**
 * Processes a key up event:
 * Records the current state of the text field to the ClientMessage.
 *
 * @param echoEvent the event, preprocessed by the 
 *        <code>EchoEventProcessor</code>
 */
EPTextFieldEx.prototype.processKeyUp = function(echoEvent) {
    var element = this.getElement();
    if (!this.enabled || !EchoClientEngine.verifyInput(element, true)) {
        EchoDomUtil.preventEventDefault(echoEvent);
        return;
    }
    
    if (this.maximumLength >= 0) {
        if (element.value && element.value.length > this.maximumLength) {
            element.value = element.value.substring(0, this.maximumLength);
        }
    }
    this.updateClientMessage();
    if (this.lookupSupport) {
    	this.lookupSupport.onkeyup(echoEvent);
    }
};

EPTextFieldEx.prototype.processChange = function(e) {
    if (!this.enabled || !EchoClientEngine.verifyInput(this.getElement(), true)) {
        return;
    }
    if (this.actionCausedOnChange) {
        this.doAction();
    }
};

/**
 * Executes the regex based on the current value of the
 * the TextFieldEx and updates the look and feel as 
 * appropriate
 */
EPTextFieldEx.prototype.executeRegex = function() {
	if (this.regex != null) {
		var element = this.getElement();
		var value = element.value;
		if (! this.regex.test(value)) {
			if (this.regexFailureProperties) {
				// apply the failure l&f
				EP.applyStyle(element,this.regexFailureProperties);
			}
		} else {
			// apply the ok l&f
			EP.applyStyle(element,this.normalProperties);
		}
	}
};

/**
 * Updates the component state in the outgoing <code>ClientMessage</code>.
 */
EPTextFieldEx.prototype.updateClientMessage = function() {
	this.executeRegex();
	
    var element = this.getElement();
    var newText = element.value;
    var textPropertyElement = EchoClientMessage.createPropertyElement(this.elementId, "text");
    if (textPropertyElement.firstChild) {
        textPropertyElement.firstChild.nodeValue = newText;
    } else {
        textPropertyElement.appendChild(EchoClientMessage.messageDocument.createTextNode(newText));
    }
    EchoClientMessage.setPropertyValue(this.elementId, "horizontalScroll", element.scrollLeft);
    EchoClientMessage.setPropertyValue(this.elementId, "verticalScroll", element.scrollTop);
};

/**
 * Returns the TextComponent data object instance based on the root element id
 * of the TextComponent.
 *
 * @param element the root element or element id of the TextComponent
 * @return the relevant TextComponent instance
 */
EPTextFieldEx.getComponent = function(element) {
    return EchoDomPropertyStore.getPropertyValue(element, "component");
};

/**
 * Static object/namespace for Text Component MessageProcessor 
 * implementation.
 */
EPTextFieldEx.MessageProcessor = function() { };

/**
 * MessageProcessor process() implementation 
 * (invoked by ServerMessage processor).
 *
 * @param messagePartElement the <code>message-part</code> element to process.
 */
EPTextFieldEx.MessageProcessor.process = function(messagePartElement) {
    for (var i = 0; i < messagePartElement.childNodes.length; ++i) {
        if (messagePartElement.childNodes[i].nodeType == 1) {
            switch (messagePartElement.childNodes[i].tagName) {
            case "init":
                EPTextFieldEx.MessageProcessor.processInit(messagePartElement.childNodes[i]);
                break;
            case "dispose":
                EPTextFieldEx.MessageProcessor.processDispose(messagePartElement.childNodes[i]);
                break;
            case "set-text":
                EPTextFieldEx.MessageProcessor.processSetText(messagePartElement.childNodes[i]);
                break;
            }
        }
    }
};

/**
 * Processes a <code>dispose</code> message to finalize the state of a
 * Text Component that is being removed.
 *
 * @param disposeMessageElement the <code>dispose</code> element to process
 */
EPTextFieldEx.MessageProcessor.processDispose = function(disposeMessageElement) {
    for (var item = disposeMessageElement.firstChild; item; item = item.nextSibling) {
        var elementId = item.getAttribute("eid");
		EP.ObjectMap.destroy(elementId);
    }
};

/**
 * Processes a <code>set-text</code> message to update the text displayed in a
 * Text Component.
 *
 * @param setTextMessageElement the <code>set-text</code> element to process
 */
EPTextFieldEx.MessageProcessor.processSetText = function(setTextMessageElement) {

    for (var item = setTextMessageElement.firstChild; item; item = item.nextSibling) {
        var elementId = item.getAttribute("eid");
        var text = item.getAttribute("text");
        var textComponent = document.getElementById(elementId);
        textComponent.value = text;
        
        // Remove any updates to text component that occurred during client/server transaction.
        EchoClientMessage.removePropertyElement(textComponent.id, "text");
    }
};

/**
 * Processes an <code>init</code> message to initialize the state of a 
 * Text Component that is being added.
 *
 * @param initMessageElement the <code>init</code> element to process
 */
EPTextFieldEx.MessageProcessor.processInit = function(initMessageElement) {
    for (var item = initMessageElement.firstChild; item; item = item.nextSibling) {
        var elementId = item.getAttribute("eid");
        var textComponent = new EPTextFieldEx(elementId);
        textComponent.init(item);
    }
};

/**
 * Processes a focus blur event:
 * Records the current state of the text field to the ClientMessage.
 * Delegates to data object method.
 *
 * @param echoEvent the event, preprocessed by the 
 *        <code>EchoEventProcessor</code>
 */
EPTextFieldEx.processBlur = function(echoEvent) {
    var textComponent = EPTextFieldEx.getComponent(echoEvent.registeredTarget);
    textComponent.processBlur(echoEvent);
};

/**
 * Processes a focus event:
 * Notes focus state in ClientMessage.
 * Delegates to data object method.
 *
 * @param echoEvent the event, preprocessed by the 
 *        <code>EchoEventProcessor</code>
 */
EPTextFieldEx.processFocus = function(echoEvent) {
    var textComponent = EPTextFieldEx.getComponent(echoEvent.registeredTarget);
    textComponent.processFocus(echoEvent);
};

/**
 * Processes a key press event:
 * Initiates an action in the event that the key pressed was the
 * ENTER key.
 * Delegates to data object method.
 *
 * @param e the DOM Level 2 event, if avaialable
 */
EPTextFieldEx.processKeyPress = function(e) {
    e = e ? e : window.event;
    var target = EchoDomUtil.getEventTarget(e);
    var textComponent = EPTextFieldEx.getComponent(target);
    textComponent.processKeyPress(e);
};

/**
 * Processes a key up event:
 * Records the current state of the text field to the ClientMessage.
 * Delegates to data object method.
 *
 * @param echoEvent the event, preprocessed by the 
 *        <code>EchoEventProcessor</code>
 */
EPTextFieldEx.processKeyUp = function(echoEvent) {
    var textComponent = EPTextFieldEx.getComponent(echoEvent.registeredTarget);
    textComponent.processKeyUp(echoEvent);
};

EPTextFieldEx.processChange = function(echoEvent) {
    var textComponent = EPTextFieldEx.getComponent(echoEvent.registeredTarget);
    textComponent.processChange(echoEvent);
};

/* ----------------------------------------------------------------------- */

/**
 * This object is used to provide auto lookup support to a text field
 */
EPTextFieldEx.LookupSupport = function(textFieldEx,autoLookUpModelE) {
	this.textFieldEx = textFieldEx;
	this.elementId = textFieldEx.elementId + '_DropDown';
	EP.ObjectMap.put(this.elementId, this); 
	
	//debugger;
	if (autoLookUpModelE.length) {
		autoLookUpModelE = autoLookUpModelE[0];
	}
	var maxAge 			= EP.DOM.getIntAttr(autoLookUpModelE,'maximumCacheAge',-1);
	var maxSize 		= EP.DOM.getIntAttr(autoLookUpModelE,'maximumCacheSize',-1);
	var matchOptions 	= EP.DOM.getIntAttr(autoLookUpModelE,'matchOptions',0);
	
	this.lookupCache = new EP.LookupCache(maxAge,maxSize,matchOptions);
	
	this.addLookupEntriesFromModel(autoLookUpModelE);
	
	this.popupProperties 			= EP.DOM.getAttr(autoLookUpModelE,'popupProperties','');
	this.entryProperties 			= EP.DOM.getAttr(autoLookUpModelE,'entryProperties','');
	this.entryRolloverProperties 	= EP.DOM.getAttr(autoLookUpModelE,'entryRolloverProperties','');
	this.searchBarProperties 		= EP.DOM.getAttr(autoLookUpModelE,'searchBarProperties','');
	this.searchBarRolloverProperties = EP.DOM.getAttr(autoLookUpModelE,'searchBarRolloverProperties','');
	
	this.searchBarShown				= EP.DOM.getBooleanAttr(autoLookUpModelE,'searchBarShown',true);
	this.searchBarIcon 				= EP.DOM.getAttr(autoLookUpModelE,'searchBarIcon',null);
	this.searchBarSearchingIcon 	= EP.DOM.getAttr(autoLookUpModelE,'searchBarSearchingIcon',null);
	this.searchBarText 				= EP.DOM.getAttr(autoLookUpModelE,'searchBarText',null);
	this.searchBarSearchingText 	= EP.DOM.getAttr(autoLookUpModelE,'searchBarSearchingText',null);
	this.noMatchingOptionText	 	= EP.DOM.getAttr(autoLookUpModelE,'noMatchingOptionText',null);
	
	
	
	// create drop down div
	this.popupDivE = document.createElement('div');
	this.popupDivE.id = this.elementId;
	this.popupDivE.style.position = 'absolute';
	EchoCssUtil.applyStyle(this.popupDivE,this.popupProperties);
	EP.setVisible(this.popupDivE,false);

	this.contentParentE = this.popupDivE;
	
	// we need the iframe trick if its IE
	if (document.all) {
		this.iframeE = document.createElement('iframe');
		this.iframeE.setAttribute("src", "javascript:false;");
		this.iframeE.setAttribute("frameborder", "0");
		this.iframeE.setAttribute("scrolling", "no");
		this.iframeE.style.position = 'absolute';
		this.iframeE.style.margin = this.popupDivE.style.margin;
		EP.setVisible(this.iframeE,false);
	}
	
	// search bit
	this.searchBarDivE = null;
	if (this.searchBarShown) {
		this.searchBarDivE = document.createElement('div');
		this.searchBarDivE.id = textFieldEx.elementId + '_DropDownSearch';
		EchoCssUtil.applyStyle(this.searchBarDivE,this.searchBarProperties);
		EP.ObjectMap.put(this.searchBarDivE.id, this); 
		this.contentParentE.appendChild(this.searchBarDivE);
	}

	// create DOM 
	var bodyE = document.getElementsByTagName('body')[0];
	bodyE.appendChild(this.popupDivE);
	if (this.iframeE) {
		bodyE.appendChild(this.iframeE);
	}

	this.updateSearchUI(false);
	
	if (this.searchBarDivE) {
	   	EP.Event.addHandler('click',this.searchBarDivE,this);
	   	EP.Event.addHandler('mouseover',this.searchBarDivE,this);
	}
 };

EPTextFieldEx.LookupSupport.prototype.destroy = function() {
	this.removeOptions();
   	if (this.searchBarDivE) {
   		EP.Event.removeHandler('click',this.searchBarDivE);
   		EP.Event.removeHandler('mouseover',this.searchBarDivE);
   	}
   	
   	this.popupDivE.parentNode.removeChild(this.popupDivE);
   	if (this.iframeE) {
   		this.iframeE.parentNode.removeChild(this.iframeE);
   	}
};

EPTextFieldEx.LookupSupport.prototype.getCurrentTextValue = function() {
	return this.textFieldEx.getElement().value;
};

EPTextFieldEx.LookupSupport.prototype.setCurrentTextValue = function(newValue) {
	this.textFieldEx.getElement().value = newValue;
	this.textFieldEx.updateClientMessage();
};

EPTextFieldEx.LookupSupport.prototype.cancelAnyAjaxCalls = function() {
	//
	// if we have an outstanding AJAX call in progress then we should cancell it.  It may
	// complete but we dont care for its results any more.
	if (this.outstandingAjaxCall) {
		this.outstandingAjaxCall.cancelled = true;
		this.outstandingAjaxCall = null;
		EP.debug('Previous AJAX Search is to be cancelled');
	}
	// update the search UI so that it not longer has "Searching...." as its text
	this.updateSearchUI(false);
};

EPTextFieldEx.LookupSupport.prototype.updateSearchUI = function(isSearching) {
	if (this.searchBarDivE) {
		var icon = this.searchBarIcon;
		var text = this.searchBarText;
		if (isSearching) {
			icon = this.searchBarSearchingIcon;
			text = this.searchBarSearchingText;
		}
		var xhtml = '<table cellpadding=0 cellspacing=0 border=0><tbody><tr>';
		if (icon) {
			xhtml += '<td><img src="' + icon + '"/></td>';
		}
		xhtml += '<td>' + text + '</td></tr></tbody></table>';
		this.searchBarDivE.innerHTML = xhtml;
	}
};



EPTextFieldEx.LookupSupport.prototype.performLookup = function() {
	EP.debug('EPTextFieldEx.LookupSupport.prototype.performLookup called');
	this.cancelAnyAjaxCalls();
	var partialSearchValue = this.getCurrentTextValue();
	if (partialSearchValue) {
		//debugger;
		// do a lookup
		var lceArr = this.lookupCache.findMatches(partialSearchValue);

		// remove any previous entries in the list
		this.removeOptions();
		this.addOptions(lceArr);
		this.showDropDown();
	} else {
		// no value so hide the drop down
		this.closeDropDown();
	}
};

/**
 * Called to select a specific option as the current one
 */
EPTextFieldEx.LookupSupport.prototype.selectOption = function(newSelectedOptionE) {
	// de hilight to previous selected item
	if (this.selectedOptionE) {
		EchoCssUtil.restoreOriginalStyle(this.selectedOptionE);
	}
	this.selectedOptionE = newSelectedOptionE;
	if (this.selectedOptionE) {
		var uiProps = this.entryRolloverProperties;
		if (this.selectedOptionE.getAttribute('noMatchingOption')) {
			this.selectedOptionE = this.searchBarDivE;
		}
		if (this.selectedOptionE) {
			if (this.selectedOptionE.id.indexOf('Search') != -1) {
				uiProps = this.searchBarRolloverProperties;
			}
			EchoCssUtil.applyTemporaryStyle(this.selectedOptionE,uiProps);
		}
	}
};

EPTextFieldEx.LookupSupport.prototype.incrementOption = function(forward) {
	if (this.optionCount > 0) {
		if (this.selectedOptionE) {
			var newSelectionE = (forward ? this.selectedOptionE.nextSibling : this.selectedOptionE.previousSibling);
			if (newSelectionE) {
				this.selectOption(newSelectionE);
			}
		}
	} else {
		this.performLookup();
	}
}

EPTextFieldEx.LookupSupport.prototype.actionOption = function() {
	//debugger;
	if (this.selectedOptionE) {
		if (this.selectedOptionE == this.searchBarDivE) {
			this.searchClicked();
		} else {
			var optionValue = this.selectedOptionE.getAttribute('optionValue');
			this.setCurrentTextValue(optionValue);
			this.closeDropDown();
		}
	}
};


EPTextFieldEx.LookupSupport.prototype.addOptions = function(lceArr) {
	// now add the new ones as mouseoverable divs with xhtml
   	this.selectOption(null);
	var parentE = this.contentParentE;
	for (var i = 0; i < lceArr.length; i++) {
		var lce = lceArr[i];
		var lceDiv = document.createElement('div');
		parentE.insertBefore(lceDiv, this.searchBarDivE); // we always have a search div as a reference point
		
		lceDiv.innerHTML  = lce.xhtml ? lce.xhtml : lce.value;
		lceDiv.id = parentE.id + '_' + i;
		// so we later can know what value this represents		
		lceDiv.setAttribute('optionIndex',i);
		lceDiv.setAttribute('optionValue',lce.value);

		EchoCssUtil.applyStyle(lceDiv,this.entryProperties);
	   	EP.Event.addHandler('mouseover',lceDiv);
	   	EP.Event.addHandler('click',lceDiv);
	   	
	   	if (i == 0) { // first is auto selected when adding
		   	this.selectOption(lceDiv);
	   	}
	}
	// if we have no matching options then show some text indicating this
	if (lceArr.length == 0 && this.noMatchingOptionText) {
		var noMatchingOptionsE = document.createElement('div');
		EchoCssUtil.applyStyle(noMatchingOptionsE,this.entryProperties);
		noMatchingOptionsE.innerHTML = this.noMatchingOptionText;
		noMatchingOptionsE.setAttribute('noMatchingOption',true);
		parentE.insertBefore(noMatchingOptionsE,this.searchBarDivE);
	}
	if (this.selectedOptionE == null) {
		this.selectOption(this.searchBarDivE);
	}
	this.optionCount = lceArr.length;
	this.resizeThings();
	
};

EPTextFieldEx.LookupSupport.prototype.removeOptions = function() {
	var childListArr = this.contentParentE.getElementsByTagName('div');
	for(var index=childListArr.length-1; index >= 0; index--) {
		var lceDiv = childListArr[index];
		if (lceDiv.getAttribute('optionValue')) {
		   	EP.Event.removeHandler('mouseover',lceDiv);
		   	EP.Event.removeHandler('click',lceDiv);
		   	this.contentParentE.removeChild(lceDiv);
		}
		if (lceDiv.getAttribute('noMatchingOption')) {
		   	this.contentParentE.removeChild(lceDiv);
		}
	}
	this.optionCount = 0;
	this.resizeThings();
};

EPTextFieldEx.LookupSupport.prototype.positionDropDown = function() {
	var textBoxE = this.textFieldEx.getElement();
	
	var pos = EP.getPageXY(textBoxE);
	var posParent = EP.getPageXY(this.popupDivE.parentNode);
	var targetX = pos[0] - posParent[0];
	var targetY = pos[1] - posParent[1];		
	targetY += EP.getHeight(textBoxE);
	
	this.popupDivE.style.left = targetX + 'px';
	this.popupDivE.style.top = targetY + 'px';
	
	var zIndex = EP.determineZ(this.popupDivE);
	this.popupDivE.style.zIndex = zIndex + 2;
	if (this.iframeE) {
		this.iframeE.style.zIndex = zIndex + 1;
		this.iframeE.style.left = targetX + 'px';
		this.iframeE.style.top = targetY + 'px';
	}
};

EPTextFieldEx.LookupSupport.prototype.resizeThings = function() {
	if (this.iframeE) {
		this.iframeE.style.width = EP.getWidth(this.popupDivE) + 'px';
		this.iframeE.style.height = EP.getHeight(this.popupDivE) + 'px';
	}
}

EPTextFieldEx.LookupSupport.prototype.showDropDown = function() {
	//EP.debug('EPTextFieldEx.LookupSupport.prototype.showDropDown called');
	if (this.popupDivE.style.visibility != 'hidden') {
		return;
	}
	this.positionDropDown();
	if (this.iframeE) {
		EP.setVisible(this.iframeE,true);
	}
	EP.setVisible(this.popupDivE,true);
	//
	// attach document listener so we can know about outside clicks
	var that = this;
	var docClickHandler = function(echoEvent) {
		var targetE = echoEvent.target;
		if (EP.isAncestorOf(targetE,that.popupDivE) || EP.isAncestorOf(targetE,that.textFieldEx.getElement())) {
			// its on us so safe to ignore
			return;
		} 
		// they have clicked outside the popup or text field so hide it
		that.closeDropDown();
	};	
	EP.DocumentEvent.addHandler('mousedown','TextFieldExClickHandler',docClickHandler);
};

EPTextFieldEx.LookupSupport.prototype.hideDropDown = function() {
	EP.debug('EPTextFieldEx.LookupSupport.prototype.hideDropDown called');
	EP.setVisible(this.popupDivE,false);
	if (this.iframeE) {
		EP.setVisible(this.iframeE,false);
	}
	
	EP.DocumentEvent.removeHandler('mousedown','TextFieldExClickHandler');
	EP.debug('EPTextFieldEx.LookupSupport.prototype.hideDropDown completed');
};

/**
 * This not only hides the drop down but it cancels any AJAX calls and
 * removes options as well.
 */
EPTextFieldEx.LookupSupport.prototype.closeDropDown = function() {
	this.cancelAnyAjaxCalls();
	this.hideDropDown();
	this.removeOptions();
};

EPTextFieldEx.LookupSupport.prototype.closeDropDownAsynch = function() {
	EP.debug('EPTextFieldEx.LookupSupport.prototype.closeDropDownAsynch called');
	var that = this;
	var callback = function() {
		that.closeDropDown();
	};
	window.setTimeout(callback,200);
}

/**
 * Called to add the entries into the lookup cache 
 */
EPTextFieldEx.LookupSupport.prototype.addLookupEntriesFromModel = function(autoLookUpModelE) {
	if (autoLookUpModelE.length) {
		autoLookUpModelE = autoLookUpModelE[0]
	}
	// get all the entries
	var entriesNL = autoLookUpModelE.getElementsByTagName('entry');
	var lceArr = [];
	for(var index=0, len = entriesNL.length; index<len; index++) {
		var entryE = entriesNL[index];
		var value = entryE.getElementsByTagName('value')[0].firstChild.data;
		var sortValue = entryE.getElementsByTagName('sortValue')[0].firstChild.data;
		var xhtml = entryE.getElementsByTagName('xhtml')[0].firstChild.data;
		
		var lce = new EP.LookupCacheEntry(value,sortValue,xhtml);
		lceArr[lceArr.length] = lce;
	}
	if (lceArr.length > 0) {
		this.lookupCache.insertEntries(lceArr);
	}
};


/**
 * Called when the user presses the search button.  We AJAX back to the server to get some
 * new entries based on the current value
 */
EPTextFieldEx.LookupSupport.prototype.searchClicked = function() {
	//debugger;
	this.cancelAnyAjaxCalls();

	var partialSearchValue = this.getCurrentTextValue();
	if (! partialSearchValue) {
		return; // nothing to search for - TODO - is this really the case
	}
	// update
	this.updateSearchUI(true);
	
	
	// Make an AJAX call to search for new values
	var uri = EchoClientEngine.baseServerUri + "?serviceId=EPNG.AutoLookup&elementId=" + this.textFieldEx.elementId + "&searchValue=" + partialSearchValue;
	
    var ajaxCall = new EchoHttpConnection(uri, "GET");
    ajaxCall.lookupSupport = this;
	ajaxCall.cancelled = false;
    this.outstandingAjaxCall = ajaxCall;
    
    // if failed
    ajaxCall.invalidResponseHandler = function(ajaxCall) {
   		ajaxCall.lookupSupport.outstandingAjaxCall = null;
   		ajaxCall.cancelled = true;
    	ajaxCall.dispose();
    }
    //
    // it worked!
    ajaxCall.responseHandler = function(ajaxCall) {
    	try {
    		//debugger;
    		if (ajaxCall.cancelled) {
    			// dont do anything but dispose of the call
    			ajaxCall.dispose();
    			return;
    		}
    		
    		// update ourselves to say we dont have any outstanding ajax lookups
    		ajaxCall.lookupSupport.outstandingAjaxCall = null;
    		
	    	// OK we get a series of XML messages back here just like the pre-populate message
	    	// so add those entries
			var dataElement = ajaxCall.getResponseXml().documentElement;
			var autoLookUpModelE = dataElement.getElementsByTagName('autoLookupModel');
			if (autoLookUpModelE.length > 0) {
				ajaxCall.lookupSupport.addLookupEntriesFromModel(autoLookUpModelE);
			}
			ajaxCall.lookupSupport.performLookup();
    	} finally {
    		ajaxCall.dispose();
    	}
    }
    ajaxCall.connect();
};

EPTextFieldEx.LookupSupport.prototype.onmouseover = function(echoEvent) {
	if (this.searchBarDivE && EP.isAncestorOf(echoEvent.target, this.searchBarDivE)) {
		this.selectOption(this.searchBarDivE);
	} else {
		var optionE = EP.DOM.findElementWithAttr(echoEvent.target,'optionValue');
		if (optionE) {
			this.selectOption(optionE);
		}
	}
};

EPTextFieldEx.LookupSupport.prototype.onclick = function(echoEvent) {
	this.actionOption();
	// we never want focus on the popup
	this.textFieldEx.getElement().focus();
};

EPTextFieldEx.LookupSupport.prototype.handleNavKeys = function(echoEvent) {
	switch (echoEvent.keyCode) {
		case 27: // ESC
		case 9 : // TAB
			this.closeDropDown();
			break;
   		case 38:  // ÙP ARROW
	   		this.incrementOption(forward=false);
	   		break;
	   	case 40: // DOWN ARROW
	   		this.incrementOption(forward=true);
	   		break;
	}			
};

/**
 * Key event has NOT completed on keypress.  So special keys only here
 */
EPTextFieldEx.LookupSupport.prototype.onkeypress = function(echoEvent,didAction) {
	switch (echoEvent.keyCode) {
		case 13:  // ENTER
			if (! didAction) {
				// action the current option
				this.actionOption();
   			}
   			break;
   	}
};

/**
 * Key event has completed on keyup
 */
EPTextFieldEx.LookupSupport.prototype.onkeyup = function(echoEvent) {
	//EP.debug('EPTextFieldEx.LookupSupport.prototype.onkeyup called - ' + echoEvent.keyCode)
	var key = echoEvent.keyCode;

	// ignore function keys
	if (key >= 112 && key <= 123) return;

	switch (key) {
		// none of these affect the text field content so ignore
		case 16: //shift
		case 17: //ctrl
		case 18: //alt
		case 19: //pause
		case 20: //caps lock
		case 35: //end
		case 36: //home
		case 37: //left arrow
		case 39: //right arrow
		case 44: //print screen
		case 45: //insert
		case 144: //num lock
		case 145: //scroll lock
		case 33: //page up
		case 34: //page down
			break;
			
		// list box navigation keys
		case 9:  //tab
		case 13: //enter
		case 27: //esc
		case 38: //up arrow
		case 40: //down arrow
			this.handleNavKeys(echoEvent);
			break;

		// all other keys
		default:
			this.performLookup();
			break
	}
};

/**
 * Our general purpose EPNG event handler
 */
EPTextFieldEx.LookupSupport.prototype.eventHandler = function(echoEvent) {
	if (echoEvent.type == "click") {
		this.onclick(echoEvent);
	}
	if (echoEvent.type == "mouseover") {
		this.onmouseover(echoEvent);
	}
};
