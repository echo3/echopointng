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
// Object to hold a value and rendering style class
//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
EPComboBoxItem = function(itemValue, itemStyle) {
	this.itemValue = itemValue;
	this.itemStyle = itemStyle;
};

//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
// Our EPComboBox object.
//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
EPComboBox = function(elementId) { 
	this.elementId = elementId;
	this.selectedIndex = -1;
	this.lastKeyPressed = null;
	this.items = [];
	this.options = null;
	EP.ObjectMap.put(elementId, this); 
};

/**
 * EPComboBox has a ServerMessage processor.
 */
EPComboBox.MessageProcessor = function() { };

EPComboBox.MessageProcessor.process = function(messagePartElement) {
	// Popup script handles documentEventHandler
    for (var i = 0; i < messagePartElement.childNodes.length; ++i) {
        if (messagePartElement.childNodes[i].nodeType == 1) {
            switch (messagePartElement.childNodes[i].tagName) {
	            case "init":
	                EPComboBox.MessageProcessor.processInit(messagePartElement.childNodes[i]);
	                break;
	            case "dispose":
	                EPComboBox.MessageProcessor.processDispose(messagePartElement.childNodes[i]);
	                break;
            }
        }
    }
};

EPComboBox.MessageProcessor.processDispose = function(disposeMessageElement) {
    for (var item = disposeMessageElement.firstChild; item; item = item.nextSibling) {
        EP.ObjectMap.destroy(elementId) + "|Combo";
    }
};

EPComboBox.MessageProcessor.processInit = function(initMessageElement) {
	//debugger;
    for (var item = initMessageElement.firstChild; item; item = item.nextSibling) {
        
		// get popup control from which this combo is derived
        var elementId = item.getAttribute("eid");

		var popup = EP.ObjectMap.get(elementId);
		if (popup == null) 	{
			alert('ComboBox init error: Base popup JS object not found');
		}
		//
		// the combo box wants to have its own event handlers
		// on the edit field, so please remove them popup base code
		popup.removeKeyTargetEvents();
		//
		// dont mess with the combo box width
		popup.eBox.geckoWidthChangeDone = true;
		
        // init a new EPComboBox object
        var comboId = elementId + "|Combo";
		EP.ObjectMap.destroy(comboId);
		
        var combo = new EPComboBox(comboId);
        combo.popupId = elementId;
		combo.actionOnSelection = item.getAttribute("actionOnSelection") == "true";
		combo.caseSensitive = item.getAttribute("caseSensitive") == "true";
		combo.defaultHoverStyle = item.getAttribute("defaultHoverStyle");
		combo.defaultItemStyle = item.getAttribute("defaultItemStyle");
		combo.textMatching = item.getAttribute("textMatching") == "true";
		combo.visibleRows = parseInt(item.getAttribute("visibleRows"));
        
        // Add event handlers to various elements of the ComboBox
		combo.eOuter = popup.eOuter;
		if (combo.eOuter == null) {
			alert('ComboBox init error: outer box element not found');
		}
		// disallow tabbing to the meaningless outer div
		combo.eOuter.removeAttribute("tabindex");
		combo.eOuter.removeAttribute("tabIndex");
		
		var inputE = popup.eTarget.getElementsByTagName('input')[0];
        if (inputE == null) alert('ComboBox init error: textfield not found');
        combo.eText = inputE;
		
		// Make sure browser's auto-complete box is turned off
		inputE.setAttribute("autocomplete", "off");
		
		// Allow combo's background colour to fill the input field
		if (!inputE.style.backgroundColor) {
			inputE.style.backgroundColor = "transparent";
		}
		
		// Fix text field so that it fills the parent table cell, if necessary
		var eTD = inputE.parentNode.parentNode;
		if (eTD.nodeName.toLowerCase() === "td") {
			var tdWidth = eTD.clientWidth;
			var inputWidth = inputE.offsetWidth;
			if (inputWidth < tdWidth) {
				inputE.style.width = tdWidth + "px";
				// Account for padding / margin etc
				if (inputE.offsetWidth > tdWidth) {
					inputE.style.width = (tdWidth + tdWidth - inputE.offsetWidth) + "px";
				}
			}
		}
				
		// Remove any handlers created by the TextComponent.js. We will delegate to them manually
		if (EchoTextComponent.getComponent(inputE)) {
			EchoEventProcessor.removeHandler(inputE, "blur");
	    	EchoEventProcessor.removeHandler(inputE, "focus");
		    EchoEventProcessor.removeHandler(inputE, "keyup");
	    	EchoDomUtil.removeEventListener(inputE, "keypress", EchoTextComponent.processKeyPress, false);    
		} else {
			alert('ComboBox init error: TextComponent not found');
		}
		
        EP.Event.addStaticHandler(inputE.id, "blur", "EPComboBox.processTextFieldEvent");
        EP.Event.addStaticHandler(inputE.id, "keydown", "EPComboBox.processTextFieldEvent");
        EP.Event.addStaticHandler(inputE.id, "keyup", "EPComboBox.processTextFieldEvent");

		combo.eToggle = popup.eToggle;
		if (combo.eToggle == null) {
			alert('ComboBox init error: toggle button not found');
		}
		EP.Event.addStaticHandler(combo.eToggle.id, "click", "EPComboBox.processToggleEvent");
	
		combo.eBox = popup.eBox;
		if (combo.eBox == null) {
			alert('ComboBox init error: list box not found');
		}
		EP.Event.addStaticHandler(combo.eBox.id, "click", "EPComboBox.processListBoxEvent");
        
        // Add list box options
        var options = item.getElementsByTagName("option");
        for (i = 0; i < options.length; i++) {
        	combo.addItem(options[i].getAttribute("value"), options[i].getAttribute("style"));
        }

/*
		EchoDebugManager.consoleWrite("comboId:" + combo.elementId
									+ "; popupId:" + combo.popupId
									+ "; actionOnSelection:" + combo.actionOnSelection
									+ "; caseSensitive:" + combo.caseSensitive
									+ "; defaultItemStyle:" + combo.defaultItemStyle
									+ "; defaultHoverStyle:" + combo.defaultHoverStyle
									+ "; textMatching:" + combo.textMatching
									+ "; visibleRows:" + combo.visibleRows
									+ "; itemCount:" + combo.items.length
									);
*/
    }
};

//-------------------------------------------
// Handles events on the comboBox's toggle element
//-------------------------------------------
EPComboBox.processToggleEvent = function(echoEvent) {
    var eventTarget = echoEvent.registeredTarget;
    var combo = EPComboBox.getComboBoxFromElementId(eventTarget.id);

	if (eventTarget == combo.eToggle) {
		if (echoEvent.type == "click") {
			combo.toggle(echoEvent);
		}
	}
	
};

//-------------------------------------------
// Handles events on the comboBox's text field
//-------------------------------------------
EPComboBox.processTextFieldEvent = function(echoEvent) {

    var eventTarget = echoEvent.registeredTarget;
    var id = eventTarget.id;
    var foundTarget = false;
    
    // the text field doesn't have an id like the other children of the popup.
    // need to find the nearest parent that does (should be combo.eTarget).
    var parent = EP.getParent(eventTarget);
    while (parent != null && !foundTarget) {
    	if (parent.id.indexOf('|') != -1) {
    		id = parent.id;
    		foundTarget = true;
    	} else {
    		parent = EP.getParent(parent);
    	}
    }
    if (!foundTarget) {
    	alert('ASSERT : processTextFieldEvent could not find text field target container');
    }
    
    var combo = EPComboBox.getComboBoxFromElementId(id);

	if (eventTarget == combo.eText) {
		if (echoEvent.type == "blur") {
			combo.onBlur(echoEvent);
		} else if (echoEvent.type == "keydown") {
			combo.onTextKeyDown(echoEvent);
		} else if (echoEvent.type == "keyup") {
			combo.onTextKeyUp(echoEvent);
		}
	}
};

//-------------------------------------------
// Handles events on the comboBox's list
//-------------------------------------------
EPComboBox.processListBoxEvent = function(echoEvent) {
    var eventTarget = echoEvent.registeredTarget;
    var combo = EPComboBox.getComboBoxFromElementId(eventTarget.id);
    combo.hideListBox(echoEvent);
};

//-------------------------------------------
// Gets the combo object based on one of its child item ids
//-------------------------------------------
EPComboBox.getComboBoxFromElementId = function(elementId) {
	if (elementId.indexOf('|') != -1) {
    	elementId = elementId.split('|')[0]; // get the id at the front of the |
    }
    elementId += "|Combo";
    
	var combo = EPComboBox.getObj(elementId);
	if (combo == null) {
		alert('ASSERT : combobox |' + elementId + '| not found as expected in Object Map');
	}
	return combo;
};

//-------------------------------------------
// returns a EPComboBox either directly or by elementId lookup
//-------------------------------------------
EPComboBox.getObj = function (elementIdorObj) {
	var combo = elementIdorObj;
	if (typeof(elementIdorObj) == "string") combo = EP.ObjectMap.get(elementIdorObj);
	return combo;
};

//-------------------------------------------
// Triggers a server interaction
//-------------------------------------------
EPComboBox.doAction = function(combo) {
	// Forward action handling to embedded text field
	EchoTextComponent.getComponent(combo.eText).doAction();
};


//===========================================================
// EPComboBox object
//===========================================================

//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
// Add an item to the list of possible options
//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
EPComboBox.prototype.addItem = function(itemValue, itemStyle) {
	var newItemStyle = itemStyle;
	if (itemStyle == undefined || itemStyle == null || itemStyle.length == 0) newItemStyle = this.defaultItemStyle;
	var item = new EPComboBoxItem(itemValue, newItemStyle);
	this.items[this.items.length] = item;
};

//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
// Hides the popup list
//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
EPComboBox.prototype.hideListBox = function(echoEvent) {
	// delegate visibility handling to popup click handler
	var popup = EP.ObjectMap.get(this.popupId);
	if (popup.isExpanded()) {
		popup.togglePopup(echoEvent, true)
		//popup.onclick(echoEvent);
	}
};

//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
// Shows the popup list
//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
EPComboBox.prototype.showListBox = function(echoEvent) {
	this.adjustSize();
	this.scrollToSelection();

	// delegate visibility handling to popup click handler
	var popup = EP.ObjectMap.get(this.popupId);
	if (!popup.isExpanded()) {
		popup.togglePopup(echoEvent)
		//popup.onclick(echoEvent);
	}
};

//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
// Resizes the popup list and positions it next to the text field
// Note that we're using w3c content box model, so width/height 
// dont include padding or borders.
//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
EPComboBox.prototype.adjustSize = function() {

	var rows = this.getListBoxRows();
	
	// Get width of parent control, subtracting the listBox's padding & borders
	// This will make the list line up with the parent unless it needs to be wider
	var wParent = EP.getWidth(this.eOuter);
	var s = this.eBox.style;
	wParent -= parseInt(s.borderLeftWidth);
	wParent -= parseInt(s.borderRightWidth);
	wParent -= parseInt(s.paddingLeft);
	wParent -= parseInt(s.paddingRight);
	
	// fudge factor to allow room for vertical scrollbar on browsers that insert it over content
	var wScroll = (this.options.length > rows && !EP.isIE) ? 20 : 0;
	var wBox = EP.getWidth(this.eBox) + wScroll;

	EP.setWidth(this.eBox, Math.max(wBox, wParent) + "px");

	// Set height to be the combined height of the number of visible options
	// Get the difference between the y-coords of the first and last+1 items
	var height = 0;
	var rows = this.getListBoxRows();

	if (rows < this.options.length) {
		var start = (this.selectedIndex > 0 ? this.selectedIndex : 0);
		var end = Math.min(start + rows, this.options.length) - 1;
		start += (end - start + 1 - rows);
		
		var top = EP.getY(this.options[start]);
		var option = this.options[end];
		var bot = EP.getY(option) + EP.getHeight(option);

		height = bot - top;
	}
	if (height > 0) EP.setHeight(this.eBox, height + "px");

};

//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
// Returns the number of rows shown in the List Box
//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
EPComboBox.prototype.getListBoxRows = function() {
	if (this.visibleRows > this.options.length || this.visibleRows < 1) {
		return this.options.length;
	} else {
		return this.visibleRows;
	}
};

//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
// Called to set the focus to the textfield
//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
EPComboBox.prototype.setTextFieldFocus = function() {
	this.eText.focus();
};

//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
// sets Listbox selection
//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
EPComboBox.prototype.setSelection = function(index) {
	this.selectedIndex = index;
	this.options[index].style.cssText = this.defaultHoverStyle;
};

//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
// scrolls ListBox to the current selection
//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
EPComboBox.prototype.scrollToSelection = function() {

	if (this.selectedIndex > -1) {
		var scrollTop = EP.getScrollY(this.eBox);
		
		// we only want content height
		var listHeight = EP.getHeight(this.eBox);
		var s = this.eBox.style;
		listHeight -= parseInt(s.borderTopWidth);
		listHeight -= parseInt(s.borderBottomWidth);
		listHeight -= parseInt(s.paddingTop);
		listHeight -= parseInt(s.paddingBottom);
	
		var option = this.options[this.selectedIndex];
		var optHeight = EP.getHeight(option);
		
		// bottom of lowest visible option = scrollTop + listHeight
		var listBtm = scrollTop + listHeight;
		var selTop = EP.getY(option);
		var selBtm = selTop + optHeight;
		
		// adjust scrollTop if selection is partially/fully outside the visible area
		if (selTop < scrollTop) {
			this.eBox.scrollTop = selTop;
		} else if (selBtm > listBtm) {
			this.eBox.scrollTop = selBtm - listHeight;
		}
	} else {
		this.eBox.scrollTop = 0;
	}
};

//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
// Called to update the textfield from selection
//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
EPComboBox.prototype.updateTextField = function() {
	if (this.selectedIndex > -1) {
		this.eText.value = this.options[this.selectedIndex].childNodes[0].nodeValue;
		EchoTextComponent.getComponent(this.eText).updateClientMessage();
	}
};

//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
// Counts the number of possible items that begin with the 
// current text value.
//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
EPComboBox.prototype.countMatchingItems = function() {
	var count = 0;
	var val = this.caseSensitive ? this.eText.value : this.eText.value.toLowerCase();
	if (val.length > 0) {
		for (var i = 0; i < this.items.length; i++) {
			var item = this.caseSensitive ? 
							this.items[i].itemValue : 
							this.items[i].itemValue.toLowerCase();
			if (item.indexOf(val) == 0) count++;		
		}
	}
	return count;
};

//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
// Searchs for the index of the first filtered option that 
// matches the current text value. Returns -1 if no match
//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
EPComboBox.prototype.searchFullMatch = function() {
	var val = this.caseSensitive ? this.eText.value : this.eText.value.toLowerCase();
	if (val.length > 0) {
		for (var i = 0; i < this.options.length; i++) {
			var option = this.caseSensitive ? 
							this.options[i].childNodes[0].nodeValue : 
							this.options[i].childNodes[0].nodeValue.toLowerCase();
			if (option == val) return i;
		}
	}
	return -1;
};

//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
// Searchs for the index of the first filtered option that
// begins with the current text value. Returns -1 if no match
//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
EPComboBox.prototype.searchPartialMatch = function() {
	var val = this.caseSensitive ? this.eText.value : this.eText.value.toLowerCase();
	if (val.length > 0) {
		for (var i = 0; i < this.options.length; i++) {
			var option = this.caseSensitive ? 
							this.options[i].childNodes[0].nodeValue : 
							this.options[i].childNodes[0].nodeValue.toLowerCase();
			if (option.indexOf(val) == 0) return i;
		}
	}
	return -1;
};

//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
// Gets the index number of a given list option
//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
EPComboBox.prototype.getOptionIndex = function(option) {
	for (var i = 0; i < this.options.length; i++) {
		if (this.options[i] == option) break;
	}
	return i;
};

//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
// Handles text field onBlur event
//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
EPComboBox.prototype.onBlur = function(echoEvent) {
	// hide listbox if tab pressed
	if (this.lastKeyPressed == 9) {
		this.hideListBox(echoEvent);
		this.lastKeyPressed = null;
	}
	// pass on event to echo text component onblur handler
	EchoTextComponent.getComponent(this.eText).processBlur(echoEvent);
};

//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
// Function called when a list item is clicked
//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
EPComboBox.prototype.onListClick = function(option) {
	this.setSelection(this.getOptionIndex(option));
	this.updateTextField();
	this.setTextFieldFocus();
	this.selectText();
	if (this.actionOnSelection) EPComboBox.doAction(this);
};


/**
 * @return true if a keyCode can be ignored by the SuggestField's key event handler
 * @param {int} keyCode
 */
EPComboBox.prototype.ignoreKey = function(keyCode) {
		
	if ((keyCode >= 16 && keyCode <= 20)		// shift, ctrl, alt, pause, caps 
	|| (keyCode >= 112 && keyCode <= 123)) 		// function keys
	{
		return true;
	}
	
	switch (keyCode) {
		case 35:	// end
		case 36:	// home
		case 37:	// left
		case 39:	// right
		case 44:	// print screen
		case 45:	// insert
		case 144:	// num lock
		case 145:	// scroll lock
			return true;
	}
	
	return false;
};


//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
// Called at key down time on the text field
// Stores key pressed so onBlur will know to hide the listbox
// if tab was pressed (onblur is also triggered by scrolling
// the list box, in which case we don't want to hide it).
// nb: Moz only catches tab onkeydown, not up/press.
//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
EPComboBox.prototype.onTextKeyDown = function(echoEvent) {

	var key = echoEvent.keyCode;
	if (this.ignoreKey(key)) {
		return;
	}
		
	this.lastKeyPressed = key;

	switch (key) {
		case 33: //page up
		case 34: //page down
		case 38: //up arrow
		case 40: //down arrow
			if (EP.isVisible(this.eBox)) {
				this.handleNavKeys(echoEvent);
			}
			break;
			
		default:
			// Everything else is caught on the onKeyUp handler to save on unneccessary processing
			if (!EchoClientEngine.verifyInput(this.elementId, true)) {
				EchoDomUtil.preventEventDefault(echoEvent);
	    		return;
			}
	}
};

//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
// Called at key press time in the text field
//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
EPComboBox.prototype.onTextKeyUp = function(echoEvent) {

	var key = echoEvent.keyCode;
	if (this.ignoreKey(key)) {
		return;
	}

	switch (key) {
		case 33: //page up
		case 34: //page down
		case 38: //up arrow
		case 40: //down arrow
			if (EP.isVisible(this.eBox)) {
				break;  // handled by keyDown
			}
			// fall through...
			
		// list box navigation keys
		case 9:  //tab
		case 13: //enter
		case 27: //esc
			this.handleNavKeys(echoEvent);
			break;

		// all other keys
		default:
			// first call regular text component handler
			EchoTextComponent.getComponent(this.eText).processKeyUp(echoEvent);
	
			if (this.textMatching) {
				var matches = this.rebuildOptions(false);
				if (matches > 0) {
					if (matches == 1) {
						// select option if we have a full match
						var index = this.searchFullMatch();
						if (index >= 0) this.setSelection(index);
					}
					this.showListBox(echoEvent);
					break;
				}
			}
			
			this.hideListBox(echoEvent);
	}
};

//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
// Handles keyboard navigation of the list items
//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
EPComboBox.prototype.handleNavKeys = function(echoEvent) {

	switch (echoEvent.keyCode) {

		case 9:  //tab
			this.hideListBox(echoEvent);
			return;
			
		case 13: //enter
			if (EP.isVisible(this.eBox)) {
				this.hideListBox(echoEvent);
				this.updateTextField();
				this.setTextFieldFocus();
				this.selectText();
				if (this.actionOnSelection) {
					EPComboBox.doAction(this);	
				}
			} else {
				EPComboBox.doAction(this);
			}
			break;

		case 27: //esc
			if (EP.isVisible(this.eBox)) {
				this.hideListBox(echoEvent);
				this.setTextFieldFocus();
			}
			break;
			
		case 33: //page up
			this.incrementSelection(echoEvent, this.visibleRows > 1 ? -this.visibleRows : -1);
			break;

		case 34: //page down
			this.incrementSelection(echoEvent, this.visibleRows > 1 ? +this.visibleRows : +1);
			break;
			
		case 38: //up arrow
			this.incrementSelection(echoEvent, -1);
			break;

		case 40: //down arrow
			this.incrementSelection(echoEvent, +1);
			break;
		
		default:
			// nothing
	}
	
	// All of these are handled - prevent browser default actions from happening
	// eg - Down/Up can cause page to scroll in IE. Down/Up can cause cursor to move left/right in text field in Moz
	EchoDomUtil.preventEventDefault(echoEvent);
};

//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
// called when the key is pressed up or down
// increments the list selection and shows the result in
// the text field
//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
EPComboBox.prototype.incrementSelection = function(echoEvent, direction) {

	if (!EP.isVisible(this.eBox)) {
		this.toggle(echoEvent);
		return;
	}
	
	var index = -1;
	var maxIndex = this.options.length - 1;
			
	if (this.selectedIndex == -1) {
		if (this.textMatching) {
			// do a search for the best matching index
			index = this.searchFullMatch();
			if (index == -1) index = this.searchPartialMatch();
		}
		
	} else {
		// deselect currently selected item first
		this.options[this.selectedIndex].onmouseout();
		
		if (direction > 0) {
			if (this.selectedIndex == maxIndex) {
				index = 0; // wrap selection to start
			} else {
				index = Math.min(maxIndex, this.selectedIndex + direction);
			}
		} else {
			if (this.selectedIndex == 0) {
				index = maxIndex; // wrap selection to end
			} else {
				index = Math.max(0, this.selectedIndex + direction);
			}
		}
	}
	
	if (index == -1) {
		if (EP.isVisible(this.eBox)) {
			index = direction > 0 ? 0 : maxIndex;
		} else {
			this.toggle(echoEvent);	// shows all options
			return;
		}
	}
	
	this.setSelection(index);
	this.scrollToSelection();
};

//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
// Rebuilds the array of "options" in the listbox,  returns True
// if matches occur
//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
EPComboBox.prototype.rebuildOptions = function(showAll) {

	var combo = this;
	var matches = 0;
	var textVal = "";
	if (this.textMatching && !showAll) {
		textVal = this.caseSensitive ? this.eText.value : this.eText.value.toLowerCase();
	}
	
	// remove all list items
	while (this.eBox.childNodes.length > 0) {
		var child = this.eBox.childNodes[0];
		this.eBox.removeChild(child);
	}

	// reset listbox so it will resize itself when options are added
	EP.setWidth(this.eBox, "auto");
	EP.setHeight(this.eBox, "auto");

	// copy matching internal options to listbox
	this.options = [];
	this.selectedIndex = -1;

   	for (var i = 0; i < this.items.length; i++) {
   		var val = this.caseSensitive ? this.items[i].itemValue : this.items[i].itemValue.toLowerCase();

	   	if (showAll || (textVal.length > 0 && val.indexOf(textVal) == 0)) {
	   	
	   		var option = document.createElement("div");
	   		var optionStyle = this.items[i].itemStyle;
			option.style.cssText = optionStyle;
	   		option.appendChild(document.createTextNode(this.items[i].itemValue));
			
			option.onmouseover = function(e) {
				if (combo.selectedIndex > -1) {
					combo.options[combo.selectedIndex].onmouseout(); // deselect current
				}
				combo.setSelection(combo.getOptionIndex(this));
			};
			option.onmouseout = function(e) {
				this.style.cssText = optionStyle;
			};
			option.onclick = function(e) {
				combo.onListClick(this);
			};
			this.options[this.options.length] = option;
			this.eBox.appendChild(option);
			matches++;
		}
   	}

   	return matches;
};


//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
// Toggles the list box visibility
//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
EPComboBox.prototype.toggle = function(echoEvent) {

	if (EP.isVisible(this.eBox)) {
		this.hideListBox(echoEvent);
	} else {
		var matches = (this.textMatching ? this.countMatchingItems() : 0);
		if (matches <= 0) {
			// no matches - show all options
			this.rebuildOptions(true);
			
		} else if (matches == 1) {
			// a single match
			this.rebuildOptions(true);
			var index = this.searchFullMatch();
			if (index >= 0) {
				// Single full match - show all options, highlight the match
				this.setSelection(index);
			} else {
				// Single partial match, show only the matched option
				this.rebuildOptions(false);
			}
			
		} else {
			// multiple matches - show only matching options, select complete match
			this.rebuildOptions(false);
			var index = this.searchFullMatch();
			if (index >= 0) this.setSelection(index);
		}
		if (this.options.length > 0) {
			this.showListBox(echoEvent);
		}
	}
	
	this.setTextFieldFocus();
};

//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
// Called to set the text selection of the textfield
//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
EPComboBox.prototype.selectText = function() {
	this.eText.select();
};
