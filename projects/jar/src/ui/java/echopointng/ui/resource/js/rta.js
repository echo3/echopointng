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



//========================================================================================================
// class : EPSP
//
// EPSP provides client side code for RTA spelling corrections.
//========================================================================================================
EPSP = function(rta,elementId, spellings) {
   this.elementId  = elementId;
   this.spellings = spellings ? spellings : "";;
   this.rta = rta;
   EP.ObjectMap.put(elementId,this);
};

EPSP.prototype.destroy = function() {};

// the current spelling select box
EPSP.currentBoxId = null;
EPSP.currentOptionId = null;


EPSP.prototype.addEventHandler = function(eventType, htmlE) {
	EPRTA.addEventHandler(eventType, htmlE, this, this.rta.rtaWin);
};

EPSP.prototype.removeEventHandler = function(eventType, htmlE) {
	EPRTA.removeEventHandler(eventType, htmlE);
};

EPSP.prototype.moveNextTo = function(htmlE, nextToE) {
	var x = nextToE.offsetLeft;
	var y = nextToE.offsetTop;
	var h = nextToE.offsetHeight;
	y = y + h;
	
	htmlE.style.left = ""+x+"px"; 
	htmlE.style.top = ""+y+"px"; 
};

/**
 * Our general callback event handler
 */
EPSP.prototype.eventHandler = function(echoEvent) {
	if (! EchoClientEngine.verifyInput(this.rta.elementId)) {
        return;
    }	
	var intended = echoEvent.target.tagName;
	if (intended == 'DIV') {
		this.optionSelect(echoEvent);
	} else {
		this.spellSpanClick(echoEvent);
	}
};

EPSP.prototype.spellSpanClick = function(echoEvent) {
	//debugger;
	EP.Event.cancelEvent(echoEvent);
	var doc = this.rta.rtaDoc;
	var htmlE = echoEvent.target;
	var alts = this.spellings.split('##');
	if (alts.length == 0) {
		return;
	}
	EPSP.hideSpellingBox(this.rta);

	var spellingBoxE = doc.getElementById(this.elementId + '|Box');
	if (spellingBoxE == null) {
		spellingBoxE = doc.createElement('div');
		spellingBoxE.id = this.elementId + '|Box';
		spellingBoxE.className = 'epspellbox';
		spellingBoxE.style.position = 'absolute';
		spellingBoxE.style.visibility = 'hidden';
		doc.body.appendChild(spellingBoxE);
		
		if (spellingBoxE.contentEditable) {
			spellingBoxE.contentEditable = false;
		}		
		
		for (x in alts) {
			var altText = alts[x];
			var optionE = doc.createElement('div');
			optionE.id = this.elementId + '[' + x;
			optionE.className = 'epspelloption';

			optionE.appendChild(doc.createTextNode(altText));
			spellingBoxE.appendChild(optionE);
			this.addEventHandler('mousedown',optionE);
			this.addEventHandler('mouseover',optionE);
			this.addEventHandler('mouseout',optionE);
		}
		this.moveNextTo(spellingBoxE,htmlE);
		// size the box
		var newWidth = Math.max(htmlE.offsetWidth,spellingBoxE.offsetWidth);
		spellingBoxE.style.width = ""+newWidth+"px"; 
	
	}
	spellingBoxE.style.visibility = '';
	spellingBoxE.style.display = '';
	EPSP.currentBoxId = spellingBoxE.id;
};

EPSP.prototype.optionSelect = function(echoEvent) {
	if (echoEvent.type.indexOf('mouseover') != -1 || echoEvent.type.indexOf('mouseout') != -1) {
		this.optionMouseHandler(echoEvent);
		return;
	}
	EP.Event.cancelEvent(echoEvent);
	
	var doc = this.rta.rtaDoc;
	var optionE =  echoEvent.target;
	var alts 	= this.spellings.split('##');
	var idArray = optionE.id.split('[');
	var alternativeText = alts[idArray[1]];
	var spanE = doc.getElementById(this.elementId);
	var spellingBoxE = doc.getElementById(this.elementId + '|Box')
	if (spanE) {
		try {
			var newtextE = doc.createTextNode(alternativeText);
			var spansParent = spanE.parentNode;
			spansParent.replaceChild(newtextE,spanE);
			if (EP.isIE) {
				//spansParent.focus();
			}
			this.rta.saveState();
			this.rta.updateState();
		} catch (e) {
			//debugger;
		}
	}
	EPSP.hideSpellingBox(this.rta);
	EPSP.currentBoxId = null;
	EPSP.currentOptionId = null;
	//
	// remove our spelling support since the mistake is now corrected.
	EP.ObjectMap.destroy(this.elementId);
};

EPSP.prototype.optionMouseHandler = function(echoEvent) {
	var optionE = echoEvent.target;
	var doc = this.rta.rtaDoc;
	var currentOptionE = (EPSP.currentOptionId ? doc.getElementById(EPSP.currentOptionId) : null);
	if (echoEvent.type == 'mouseover') {
		if (currentOptionE) {
			currentOptionE.className = 'epspelloption';
		}
		optionE.className = 'epspelloptionhilight';
		EPSP.currentOptionId = optionE.id;
	}
	if (echoEvent.type == 'mouseout') {
		optionE.className = 'epspelloption';
		EPSP.currentOptionId = null;
	}
};

EPSP.hideSpellingBox = function(rta) {
	if (EPSP.currentBoxId) {
		var doc = rta.rtaDoc;
		var spellingBoxE = doc.getElementById(EPSP.currentBoxId);
		EPSP.currentBoxId = null;
		if (spellingBoxE) {
			//debugger;
			spellingBoxE.style.visibility = 'hidden';
			spellingBoxE.style.display = 'none'; 
			try {
				if (EP.isIE) {
					doc.appendChild(spellingBoxE);
					doc.removeChild(spellingBoxE);
					window.setTimeout("EPSP.hideSpellingBoxAsync('" + rta.elementId + "');", 100);
				} else {
					doc.removeChild(spellingBoxE);
				}
			} catch (e) {
				//debugger;
			}
		}
	}
};

EPSP.hideSpellingBoxAsync = function(elementId) {
    var rta = EP.ObjectMap.get(elementId);
	rta.rtaWin.focus();
	rta.rtaDoc.body.focus();
};

/**
 * This creates a EPSP object for a spelling span and also attaches an event
 * listener to listen for clicks on the mispelled word.
 */ 
EPSP.initEPSP = function(rta,elementId,spellings) { 
	var epsp = new EPSP(rta,elementId,spellings);
	var elem = rta.rtaDoc.getElementById(elementId);
	epsp.addEventHandler('click',elem);	
};


//========================================================================================================
// class : EPRTAColorChooser
//
// Encapsulates the RTA global color chooser
//========================================================================================================
EPRTAColorChooser = function(elementId) {
	this.callbackF = null;
	this.elementId = elementId+"cc";
	
	this.ccDivE = document.createElement("div");
	this.ccDivE.id = this.elementId;
	this.ccDivE.style.position = 'absolute';
	this.ccDivE.style.background = '#FFFFFF';
	EP.setDisplayed(this.ccDivE,false);
	
	var newParent = document.getElementsByTagName("body")[0];
	newParent.appendChild(this.ccDivE);
	
	var itemXML = document.createElement('item');
    itemXML.setAttribute('eid',this.elementId+"Impl");
    itemXML.setAttribute('container-eid',this.elementId);
    itemXML.setAttribute('swatchesPerRow','17');

    itemXML.setAttribute('styleDefault','padding:1px;border:1px solid #317082;');
    itemXML.setAttribute('styleSwatch','width:10px;height:10px;border:1px solid #000000;');
    itemXML.setAttribute('colorTitles','');

    itemXML.setAttribute('interestedPartyId',this.elementId);
    itemXML.setAttribute('showCurrentColorSelectionSwatch','false');
	itemXML.setAttribute('enabled','true');
	itemXML.setAttribute('serverNotify','false');
        

	this.colorChooser = new EPColorChooser(this.elementId+"Impl");
    this.colorChooser.init(itemXML);
  	
	EP.ObjectMap.put(this.elementId,this);
};

EPRTAColorChooser.currentColorChooser = null;

//------------------------------------------------------------
//------------------------------------------------------------
EPRTAColorChooser.prototype.destroy = function() {
	if (this.colorChooser) {
		this.colorChooser.destroy();
	}
	// remove it
	this.ccDivE.parentNode.removeChild(this.ccDivE);
};

//------------------------------------------------------------
// This method sets the specified 'f' function as the target for color
// notifications. The function must be in the form
//
//			function(newClr)
//
//------------------------------------------------------------
EPRTAColorChooser.prototype.setCallbackFunction = function(f) {
	this.callbackF = f;
};

//------------------------------------------------------------
//
// Called by the RTA ColorChooser table as the user moves over
// the TDs within it or click on it.
//
//------------------------------------------------------------
EPRTAColorChooser.prototype.onColorClick  =function(color) {
	if (this.callbackF != null) {
		this.callbackF(color);
	}
	this.hide();
};

//------------------------------------------------------------
// Our ColorChooser document level event handler
//------------------------------------------------------------
EPRTAColorChooser.documentEventHandler = function(echoEvent) {
	if (EPRTAColorChooser.currentColorChooser) {
		if (echoEvent.type == 'mousedown') {
			if (EP.isAncestorOf(echoEvent.target,EPRTAColorChooser.currentColorChooser.ccDivE)) {
				// if we clicked on outselves keep going
				return;
			}
			EPRTAColorChooser.currentColorChooser.hide();
			EPRTAColorChooser.currentColorChooser = null;
		}
	}
};

//------------------------------------------------------------
// Hides the RTA Color Chooser
//------------------------------------------------------------
EPRTAColorChooser.prototype.hide = function() {
	var ccE = document.getElementById(this.elementId);
	if (ccE == null) {
		alert("ASSERT : color chooser is not present");
		return;
	}
	EP.setDisplayed(ccE,false);
	EPRTAColorChooser.currentColorChooser = null;
	// no need for the document level handler
	EP.DocumentEvent.removeHandler('mousedown',	"EPMI");
};

EPRTAColorChooser.prototype.toCSSColorFromRGB = function(r,g,b) {
	var red = r.toString(16);
	red = red.length == 1 ? '0'+red : red;
	
	var green = g.toString(16);
	green = green.length == 1 ? '0'+green : green;
	
	var blue = b.toString(16)
	blue = blue.length == 1 ? '0'+blue : blue;
	
	var color = '#' + red + green + blue;
	return color.toUpperCase();
};

EPRTAColorChooser.prototype.toCSSColor = function(color) {
	if (typeof(color) == 'number') {
		// on IE is in Long format but its bgr not rgb
		var b = (color >> 16);
		var g = (color >> 8) & 0xff;
		var r = color & 0xff;
		return this.toCSSColorFromRGB(r,g,b);
		
	} else if (typeof(color) == 'string') {
		if (color.indexOf('rgb(') != -1) {
			var bits = color.split(',')
			var r = parseInt(bits[0].substr(4),10);
			var g = parseInt(bits[1]);
			var b = parseInt(bits[2].substr(0,bits[2].length-1));
			return this.toCSSColorFromRGB(r,g,b);
		} else if (color.indexOf('#') == 0) {
			return color;
		}
	}
	return '';
};


//------------------------------------------------------------
// Shows the RTA Color Chooser next to the specified element
//------------------------------------------------------------
EPRTAColorChooser.prototype.showNextTo = function(nextToHtmlId, colorToUse) {
	if (EPRTAColorChooser.currentColorChooser) {
		EPRTAColorChooser.currentColorChooser.hide();
	}
	EPRTAColorChooser.currentColorChooser = this;

	colorToUse = this.toCSSColor(colorToUse);
	this.colorChooser.currentColorSelection = colorToUse;
	this.colorChooser.updateDisplay(colorToUse);
	
	// dynamically add a document level handler
	EP.DocumentEvent.addHandler('mousedown', "EPMI", EPRTAColorChooser.documentEventHandler);
	
	var ccE = document.getElementById(this.elementId);
	if (ccE == null) {
		alert("ASSERT : color chooser is not present");
		return;
	}
	// reparent if we are inside a modal dialog
	var rtaId = this.rta.elementId;
	var newParent = document.getElementsByTagName("body")[0];
	if (EchoModalManager.modalElementId && EchoModalManager.isElementInModalContext(rtaId)) {
		newParent = document.getElementById(EchoModalManager.modalElementId);
	}
	if (ccE.parentNode != newParent) {
		newParent.appendChild(ccE);
	}
	
	var x = 100;
	var y = 100;
	if (nextToHtmlId != null) {
		var nextToE = document.getElementById(nextToHtmlId);
		//x = EP.getPageX(nextToE);
		//y = EP.getPageY(nextToE) + EP.getHeight(nextToE);
		var pos = EP.getPageXY(nextToE);
		var posParent = EP.getPageXY(ccE.parentNode);
		x = pos[0] - posParent[0];
		y = pos[1] - posParent[1];		
		y += + EP.getHeight(nextToE);
	}
	EP.setX(ccE,x);
	EP.setY(ccE,y);
	EP.setZ(ccE,EP.determineZ(ccE.parentNode)+1);
	EP.setDisplayed(ccE,true);
};


//========================================================================================================
// class : EPTBLandF
//
// The encapsulates the styles that must be applied to a ToolBar object
//========================================================================================================
EPTBLandF = function(upItemStyle, downItemStyle, upMouseOverStyle, downMouseOverStyle) {
	this.upItemStyle = upItemStyle;
	this.downItemStyle = downItemStyle;
	this.upMouseOverStyle = upMouseOverStyle;
	this.downMouseOverStyle = downMouseOverStyle;
};


//========================================================================================================
// class : EPToolBarItem
//
// A class that encapsulates a tool bar button or select field. An 'isStateful'
// item may have 2
// states, "up" or "down", in which case the 'pressedState' property will be
// either false or true. If its
// not isStateful, then it is always "up"
//
//========================================================================================================
EPToolBarItem = function(rta, elementId, cmd, isStateful, landf) {
	this.rta = rta;
	this.elementId = elementId;
	this.cmd	= cmd;
	this.isStateful = isStateful;
	this.landf = landf;
	this.pressedState = false;
	this.elem = null;
	EP.ObjectMap.put(elementId,this);
};

//------------------------------------------------------------
// Delayed construction of toolbar item
//------------------------------------------------------------
EPToolBarItem.prototype.create = function() {
	this.elem = document.getElementById(this.elementId);
	if (this.elem == null)
		alert('ASSERT : EPToolBarItem elementId is invalid!' + this.elementId);

	EP.applyStyle(this.elem, this.landf.upItemStyle);
	var tag = this.elem.tagName.toLowerCase();
	if (tag == "img") {
		EPRTA.addEventHandler("mouseover", this.elem, this);
		EPRTA.addEventHandler("mouseout", this.elem, this);
		EPRTA.addEventHandler("mousedown", this.elem, this);
		EPRTA.addEventHandler("mouseup", this.elem, this);
		EPRTA.addEventHandler("click", this.elem, this);
	}
	if (tag == "select") {
		EPRTA.addEventHandler("change", this.elem, this);
	}
};

//------------------------------------------------------------
//Deconstructor of EPToolBarItem
//------------------------------------------------------------
EPToolBarItem.prototype.destroy = function() {
	var tag = this.elem.tagName.toLowerCase();
	if (tag == "img") {
		EP.Event.removeHandler("mouseover",this.elem);
		EP.Event.removeHandler("mouseout",this.elem);
		EP.Event.removeHandler("mousedown",this.elem);
		EP.Event.removeHandler("mouseup",this.elem);
		EP.Event.removeHandler("click",this.elem);
	} else if (tag == "select") {
		EP.Event.removeHandler("change",this.elem);
	}	
};


//------------------------------------------------------------
// Called to execute the command associatde with the given
// TB item.
//------------------------------------------------------------
EPToolBarItem.prototype.execCommand = function(value,showUI) {
	this.rta.execCommand(this.cmd,value,showUI);
};

//------------------------------------------------------------
// This is the generic event handler for the TBItem. All events
// are channelled to this function.
//------------------------------------------------------------
EPToolBarItem.prototype.eventHandler = function(echoEvent) {
	if (! EchoClientEngine.verifyInput(this.rta.elementId)) {
        return;
    }	
	EP.Event.cancelEvent(echoEvent);
	var tag = this.elem.tagName.toLowerCase();
	//
	// button control
	if (echoEvent.type == "click" && tag == "img") {
		//debugger;
		//
		// handle special cases here
		if (this.cmd == "hilitecolor" || this.cmd == "forecolor") {
			if (EP.isIE) {
				// IE lose the selection range because of clicking away
				saveRange = this.rta.rtaDoc.selection.createRange();
			}
			var tbi = this;
			var f = function(clr) {
				if (EP.isIE) {
					// restore selection range on IE
					saveRange.select();
				}
				tbi.execCommand(clr);
			}
			this.rta.getColorChooser().setCallbackFunction(f);
			var currentColor = this.rta.queryCommandValue(this.cmd);
			this.rta.getColorChooser().showNextTo(this.elementId,currentColor);
		} 
		else if (this.cmd == "createlink" || this.cmd == "insertimage") {
			if (EP.isIE) {
				this.execCommand(null,true);
			} else {
				if (url) {
					this.execCommand(url);
				}
			}	
		} else if (this.cmd == "spellcheck") {
			this.rta.spellCheck();
		} else {
			this.execCommand(null);
		}
		return;
	}
	//
	// select control
	if (echoEvent.type == "change" && tag == "select") {
		//debugger;
		var value = this.elem.options[this.elem.selectedIndex].value;
		//
		// we only add the <> brackets when needed!
		if (this.cmd == 'formatblock')
			value = '<' + value + '>';

		this.execCommand(value);
		return;
	}
	//
	// handle mouse over/out/down echoEventents for button appearance reasons.
	// We use the various style classes
	// to control how it looks at mouse over times
	//
	if (echoEvent.type.indexOf("mouse") == 0 && tag == "img") {
		this.redrawButtonAppearance(echoEvent.type);
		return;
	}
};

//------------------------------------------------------------
// Called to update the state of the toolbar item, ie make it
// reflect the current value in either button or a select
// field.
//------------------------------------------------------------
EPToolBarItem.prototype.updateState = function() {
	var i = 0;
	var tag = this.elem.tagName.toLowerCase();
	var cmd = this.cmd;

	// 
	// only buttons that are stateful get updated
	if (tag == "img" && this.isStateful) {
		this.pressedState = false;
		if (this.rta.queryCommandEnabled(cmd)) {
			this.pressedState = this.rta.queryCommandState(cmd);	
		}
		this.redrawButtonAppearance("mouseout");
	}
	//
	// special cases. The state of these are toggled by us not the the
	// underlying RTA editor
	if (cmd == 'spellcheck') {
		this.pressedState = this.rta.spellCheckInProgress; 
		this.redrawButtonAppearance("mouseout");
	}
	if (tag == "select") {
		var select = this.elem;
		var foundVal = false;
		var value = null;
		if (this.rta.queryCommandEnabled(cmd)) {
			value = this.rta.queryCommandValue(cmd);
		}
		if (value && select.options) {
			curVal = (""+value).toLowerCase();
			// try and find a select option that matches that value
			for (i = 0; i < select.options.length; i++) {
				var opval = (""+ select.options[i].value).toLowerCase();
				var opSubVal = ( curVal.length <= opval.length ? opval : opval.substr(0, curVal.length));
				if (opval == curVal || opSubVal == curVal) {
					select.selectedIndex = i;
					foundVal = true;
					break;
				}
			}
			if (! foundVal) {
				//
				// no good! try and find a select option that matches option
				// text
				for (i = 0; i < select.options.length; i++) {
					var opval = (""+ select.options[i].text).toLowerCase();
					var opSubVal = ( curVal.length <= opval.length ? opval : opval.substr(0, curVal.length));
					if (opval == curVal || opSubVal == curVal) {
						select.selectedIndex = i;
						foundVal = true;
						break;
					}
				}
			}
		}
		if (! foundVal) {
			//
			// we didnt find one so set the selected index to the last one
			// in the list. This is defined to be an "indeterminate" value
			select.selectedIndex = select.options.length-1;
		}
	}
};


//------------------------------------------------------------
// Called to force a tbItem to redraw its appearance based on
// an specific event happening such as mouseout
//------------------------------------------------------------
EPToolBarItem.prototype.redrawButtonAppearance = function(eventType) {
	var elem = this.elem;

	if (eventType == "mouseover") {
		EP.applyStyle(elem, (this.pressedState) ? this.landf.downMouseOverStyle : this.landf.upMouseOverStyle);
	}
	if (eventType == "mouseout") {
		EP.applyStyle(elem, (this.pressedState) ? this.landf.downItemStyle : this.landf.upItemStyle);
	}
	//
	// cause the images to "jump" down a little bit on click
	if (eventType == "mousedown") {
		elem.style.left = 1;
		elem.style.top  = 1;
	}
	else {
		elem.style.left = 0;
		elem.style.top  = 0;
	}
};


//========================================================================================================
// class : EPRTA
//
// Our top level RichText class
//========================================================================================================
EPRTA = function(elementId) {
	this.elementId = elementId;
	this.rtaDoc = null;
	this.rtaWin = null;

	this.currentHTML = null;
	this.toolbarItems = [];
	EP.ObjectMap.put(elementId,this);
};

//------------------------------------------------------------
// Delayed load of RTA features.
//------------------------------------------------------------
EPRTA.prototype.create = function(rtaDoc,rtaWin,rtaIFrame,initialHTML,spellCheckInProgress) {
	this.rtaDoc = rtaDoc;
	this.rtaWin = rtaWin;
	this.rtaIFrame = rtaIFrame;
	this.currentHTML = initialHTML;
	this.spellCheckInProgress = spellCheckInProgress;

	//	
	// single event handler handles multiple events
	EPRTA.addEventHandler("keydown", rtaDoc, this, rtaWin);
	EPRTA.addEventHandler("keypress", rtaDoc, this, rtaWin);
	EPRTA.addEventHandler("click", rtaDoc, this, rtaWin);
	
	//
	// now create all our previously registered toolbar items
	for (tbItem in 	this.toolbarItems) {
		this.toolbarItems[tbItem].create();
	}
};

//------------------------------------------------------------
// Called to allow the RTA to save its own state to EP
// Does it smartly so that it doesnt raise events
// unecessarily
//------------------------------------------------------------
EPRTA.prototype.saveState = function() {
	EP.debug('saveState called');
	var state = this.rtaDoc.body.innerHTML;
	if (state != this.currentHTML) {
		this.currentHTML = state;
		EP.Event.setClientValue(this.elementId,"text",state);
	}
};

//------------------------------------------------------------
// Runs the MISAS/MSHTML queryCommandState
//------------------------------------------------------------
EPRTA.prototype.queryCommandState = function(cmd) {
	var state = false;
	try {
		state = this.rtaDoc.queryCommandState(cmd); 
	} catch (ex) {
		EP.debug("queryCommandState Exception [" + cmd + "] : " + ex);	
	}
	return state;
};

//------------------------------------------------------------
// Runs the MISAS/MSHTML queryCommandValue
//------------------------------------------------------------
EPRTA.prototype.queryCommandValue = function(cmd) {
	cmd = EPRTA.remapCommand(cmd);
	var value = null;
	try {
		value = this.rtaDoc.queryCommandValue(cmd);
	} catch (ex) {
		EP.debug("queryCommandValue Exception [" + cmd + "] : " + ex);	
	}
	return value;
};

//------------------------------------------------------------
// Runs the MISAS/MSHTML queryCommandEnabled
//------------------------------------------------------------
EPRTA.prototype.queryCommandEnabled = function(cmd) {
	cmd = EPRTA.remapCommand(cmd);
	return this.rtaDoc.queryCommandEnabled(cmd);
};

//------------------------------------------------------------
// Executes a MIDAS/MSHTML command against the RTA
//------------------------------------------------------------
EPRTA.prototype.execCommand = function(cmd,value,showUI) {
	this.focus();
	cmd = EPRTA.remapCommand(cmd);
	// use <b> tags instead of <span style=""> as per
	// http://forum.nextapp.com/forum/index.php?showtopic=3304	
	if (EP.isGecko) {    
		if(cmd == "forecolor" || cmd == "hilitecolor") {  
			this.rtaDoc.execCommand('useCSS', false, false);  
		} else {  
			this.rtaDoc.execCommand('useCSS', false, true);  
	    }  
	}  	
	if (showUI)
		 this.rtaDoc.execCommand(cmd,showUI,value);
	else
		 this.rtaDoc.execCommand(cmd,false,value);

	this.updateState();
	this.focus();
};

//------------------------------------------------------------
// Remaps the MIDAS/MSHTML command to take into account browser differences
//------------------------------------------------------------
EPRTA.remapCommand = function(cmd) {
	if (cmd == "hilitecolor" && EP.isIE)
		return "backcolor";
	return cmd;
};

//------------------------------------------------------------
//Raise an action to the server to perform the spell check
//------------------------------------------------------------
EPRTA.prototype.spellCheck = function() {
	if (! EchoClientEngine.verifyInput(this.elementId)) {
        return;
    }	
    EchoClientMessage.setActionValue(this.elementId, "spellcheck");
    EchoServerTransaction.connect();
}

//------------------------------------------------------------
// sets focus to the RTA
//------------------------------------------------------------
EPRTA.prototype.focus = function() {
	this.rtaWin.focus();
};

//------------------------------------------------------------
// updates the RTA state and its toolbars etc. Called
// on a "delay" so it happens after and event happens
//------------------------------------------------------------
EPRTA.prototype.updateState = function() {
	//
	// the update is done on a small time delay to allow keys to get through
	// and be processed and hence have a more accurate state of play
	//
	window.setTimeout("EPRTA.asynchUpdateToolBar('" + this.elementId + "');", 10);
};

//------------------------------------------------------------
// updates the tool bar asynchronously
//------------------------------------------------------------
EPRTA.asynchUpdateToolBar = function(elementId) {
	var rta = EP.ObjectMap.get(elementId);
	rta.updateToolBar();
};


//------------------------------------------------------------
// Finds the toolbar item with the givcen command or null if it
// cannot be found.
//------------------------------------------------------------
EPRTA.prototype.findToolBarItem = function(cmd) {
	for (tbItem in 	this.toolbarItems) {
		var tbi = this.toolbarItems[tbItem];
		if (tbi.cmd == cmd)
			return tbi;
	}
	return null;
};

//------------------------------------------------------------
// Called to update the state of each tool bar item that is
// registered. It asks the rtaDoc whether the specific command
// is current given the current position.
//------------------------------------------------------------
EPRTA.prototype.updateToolBar = function() {
	for (tbItem in 	this.toolbarItems) {
		this.toolbarItems[tbItem].updateState();
	}
	// and save the state of control
	this.saveState();
};

//------------------------------------------------------------
// called when key enters the RTA
//------------------------------------------------------------
EPRTA.prototype.onkey = function(echoEvent) {
	EP.debug('onkey called');
	if (echoEvent.ctrlKey) {
		var cmd = null;
		var key = String.fromCharCode(EP.isIE ? echoEvent.keyCode : echoEvent.charCode).toLowerCase();	
		if (key == 'b')
			cmd = "bold";
		else if (key == 'i')
			cmd = "italic";
		else if (key == 'u')
			cmd = "underline";
		else if (key == 'l')
			cmd = "justifyleft";
		else if (key == 'r')
			cmd = "justifyright";
		else if (key == 'e')
			cmd = "justifycenter";
		else if (key == 'j')
			cmd = "justifyfull";
		else if (key == '=')
			cmd = "subscript";
		else if (key == '-')
			cmd = "superscript";
			
		if (cmd) {
			var tbi = this.findToolBarItem(cmd);
			if (tbi) {
				tbi.execCommand(null);
				EP.Event.cancelEvent(echoEvent);
			}
		}
	}
};


//------------------------------------------------------------
// Registers a tool bar button item with the RTA and sets its command
// that it will reported back when pressed
//------------------------------------------------------------
EPRTA.prototype.registerButton = function(cmd, isStateful, landf) {
	var itemHtmlId = this.elementId+'_'+cmd;
	var tbi = new EPToolBarItem(this,itemHtmlId,cmd,isStateful,landf);
	this.toolbarItems[this.toolbarItems.length] = tbi;
	return tbi;
};

//------------------------------------------------------------
// Registers a tool bar button item with the RTA and sets its command
// that it will reported back when pressed
//------------------------------------------------------------
EPRTA.prototype.registerSelect = function(cmd,landf) {
	var itemHtmlId = this.elementId+'_'+cmd;
	var tbi = new EPToolBarItem(this,itemHtmlId,cmd,false,landf);
	this.toolbarItems[this.toolbarItems.length] = tbi;
	return tbi;
};

//------------------------------------------------------------
// Returns the current selection
//------------------------------------------------------------
EPRTA.prototype.getSelection = function() {
	if (EP.isIE) {
		return this.rtaDoc.selection;
	} else {
		return this.rtaWindow.getSelection();
	}
};

//------------------------------------------------------------
//Returns the color chooser for this RTA
//------------------------------------------------------------
EPRTA.prototype.getColorChooser = function() {
	return this.colorChooser;
};

//------------------------------------------------------------
//Sets the color chooser for this RTA
//------------------------------------------------------------
EPRTA.prototype.setColorChooser = function(colorChooser) {
	this.colorChooser = colorChooser;
	this.colorChooser.rta = this;
};

//------------------------------------------------------------
// returns a range for the provided selection
//------------------------------------------------------------
EPRTA.prototype.createRange = function(selection) {
	if (EP.isIE) {
		return selection.createRange();
	} else {
		this.focus();
		if (typeof selection != "undefined") {
			return selection.getRangeAt(0);
		} else {
			return rtaDoc.createRange();
		}
	}
};

//------------------------------------------------------------
// Sets the html text of the RTA
//------------------------------------------------------------
EPRTA.prototype.setHtmlText = function(htmlText) {
	this.rtaDoc.body.innerHTML = htmlText;
	this.updateState();
};


//------------------------------------------------------------
//This is the generic event handler for the RTA. All events
//are channelled to this function. Therefore it needs to
//distinguish between them.
//------------------------------------------------------------
EPRTA.prototype.eventHandler = function(echoEvent) {
	if (! EchoClientEngine.verifyInput(this.elementId)) {
        return;
    }	
	if (echoEvent.type == "keypress" || echoEvent.type == "keydown") {
		this.onkey(echoEvent);
	}
	EPSP.hideSpellingBox(this);
	this.getColorChooser().hide();
	this.updateState();
};

//------------------------------------------------------------
// Creates a rich text object for a given elementId by
// turning the IFRAME into design mode
//------------------------------------------------------------
EPRTA.initEditing = function(elementId, htmlDocument, initialText,spellCheckInProgress) {
	var rtaIFrame = document.getElementById(elementId+"IFrame");
	var rtaWin = rtaIFrame.contentWindow;
	var rtaDoc = rtaIFrame.contentWindow.document;

	rtaDoc.designMode = "On";


	try {
		rtaDoc.open();
		rtaDoc.write(htmlDocument);
		rtaDoc.close();
	}  catch (e) {
		alert("ASSERT : Rich Text Editing is not supported on this browser version." + e);
	}	
	if (EP.isIE) {
		rtaDoc.body.contentEditable = true;
	}

	//
	// get a previously created EPRTA object for load it up with
	// the new values. This implies that it was
	var rta = EP.ObjectMap.get(elementId);
	rta.create(rtaDoc,rtaWin,rtaIFrame,initialText,spellCheckInProgress);

	try {
		rta.execCommand("bold", true);
		rta.execCommand("bold", false);
	}  catch (e) {
		alert("ASSERT : Rich Text Editing is not supported on this browser version.");
	}	
	rta.updateState();
	return rta;
};


/**
 * EPRTA has a ServerMessage processor.
 */
EPRTA.MessageProcessor = function() { };

EPRTA.MessageProcessor.process = function(messagePartElement) {
    for (var i = 0; i < messagePartElement.childNodes.length; ++i) {
        if (messagePartElement.childNodes[i].nodeType == 1) {
            switch (messagePartElement.childNodes[i].tagName) {
            case "init":
                EPRTA.MessageProcessor.processInit(messagePartElement.childNodes[i]);
                break;
            case "dispose":
                EPRTA.MessageProcessor.processDispose(messagePartElement.childNodes[i]);
                break;
            case "htmlChanged":
                EPRTA.MessageProcessor.processHtmlChanged(messagePartElement.childNodes[i]);
                break;
            }
        }
    }
};

EPRTA.MessageProcessor.processDispose = function(messageElement) {
	for (var item = messageElement.firstChild; item; item = item.nextSibling) {
        var elementId = item.getAttribute("eid");
   		EP.ObjectMap.destroy(elementId);
    }
};

//------------------------------------------------------------
//Deconstructor of RTA
//------------------------------------------------------------
EPRTA.prototype.destroy = function() {
	for (tbItem in 	this.toolbarItems) {
		this.toolbarItems[tbItem].destroy();
	}
	this.colorChooser.destroy();
	EP.Event.removeHandler("keydown",this.rtaDoc);
	EP.Event.removeHandler("keypress",this.rtaDoc);
	EP.Event.removeHandler("click",this.rtaDoc);
};


EPRTA.MessageProcessor.processHtmlChanged = function(messageElement) {
	//debugger;
	for (var item = messageElement.firstChild; item; item = item.nextSibling) {
        var elementId = item.getAttribute("eid");
        var htmlText = item.getAttribute("html");
        
        var rta = EP.ObjectMap.get(elementId);
        rta.setHtmlText(htmlText);
        
        rta.spellCheckInProgress = (item.getAttribute("spellCheckInProgress") == "true");
        // initialise spelling support
        var spelling = item.getElementsByTagName("spelling");
        var spellingItem = null;
        for (var i = 0; i < spelling.length; i++) {
        	spellingItem = spelling[i];
        	var spellId = spellingItem.getAttribute("spellId");
        	var spellings = spellingItem.getAttribute("spellings");
        	
			EPSP.initEPSP(rta,spellId,spellings);
        }
        rta.updateState();
    }
};

EPRTA.MessageProcessor.processInit = function(messageElement) {
	//debugger;
    for (var item = messageElement.firstChild; item; item = item.nextSibling) {
        var elementId = item.getAttribute("eid");
		EP.ObjectMap.destroy(elementId);
		//
        // create a EPRTA object
		var rta = new EPRTA(elementId);
        if (item.getAttribute("enabled") == "false") {
            EchoDomPropertyStore.setPropertyValue(elementId, "EchoClientEngine.inputDisabled", true);
        }
		
        //
        // process look and feel elements
        var buttonLandF = null;
        var selectLandF = null;
        
        var landfs = item.getElementsByTagName("landf");
        var landfItem = null;
        for (var i = 0; i < landfs.length; i++) {
        	landfItem = landfs[i];
            //
            // init our look and feel objects
        	tag = landfItem.getAttribute("tag");
    		if (tag == "button") {
    	        	var upItemStyle = landfItem.getAttribute("upItemStyle");
    	            var downItemStyle = landfItem.getAttribute("downItemStyle");
    	    		var upMouseOverStyle = landfItem.getAttribute("upMouseOverStyle");
    	    		var downMouseOverStyle = landfItem.getAttribute("downMouseOverStyle");
    				buttonLandF = new EPTBLandF(upItemStyle,downItemStyle,upMouseOverStyle, downMouseOverStyle); 
    		}
    		if (tag == "select") {
    	        	var selectStyle = landfItem.getAttribute("selectStyle");
    				selectLandF = new EPTBLandF(selectStyle); 
    		}
        }
        //
        // process command elements
        var commands = item.getElementsByTagName("command");
        var cmdItem = null;
        for (var i = 0; i < commands.length; i++) {
        	cmdItem = commands[i];
        	cmd = cmdItem.getAttribute("cmd");
        	isStateful = cmdItem.getAttribute("isStateful") == "true";
        	tag = cmdItem.getAttribute("tag");
    		if (tag == "button") {
    				rta.registerButton(cmd,isStateful,buttonLandF);
    		}
    		if (tag == "select") {
    				rta.registerSelect(cmd,selectLandF);
    		}
        }
        
        //
        // RTA creation
        var htmlDocument = item.getAttribute("htmlDocument");
        var initialText = item.getAttribute("initialText");
        var spellCheckInProgress = (item.getAttribute("spellCheckInProgress") == "true");
        
        EPRTA.initEditing(elementId,htmlDocument,initialText,spellCheckInProgress);

        // initialise spelling support
        var spelling = item.getElementsByTagName("spelling");
        var spellingItem = null;
        for (var i = 0; i < spelling.length; i++) {
        	spellingItem = spelling[i];
        	var spellId = spellingItem.getAttribute("spellId");
        	var spellings = spellingItem.getAttribute("spellings");
        	
			EPSP.initEPSP(rta,spellId,spellings);
        }
		var cc = new EPRTAColorChooser(elementId);
		rta.setColorChooser(cc);
    }
};

/**
 * We have to have our own event list handlers here instead of the standard Ep
 * ones because we need to provide a window object to find events other than the
 * standard "window" on IE. This means we have to use closures for the event
 * function.
 */
EPRTA.objectHandlerList = [];

EPRTA.addEventHandler = function(eventType, elem, obj, win) {
	eventType = EP.Event.transmogifryEventType(eventType);
	
	win = win ? win : window;
	var f = function(echoEvent) {
		echoEvent = EP.isIE ? win.event : echoEvent;
		echoEvent = EP.Event.transmogifryEvent(echoEvent);
		if (!echoEvent.target && echoEvent.srcElement) {
			echoEvent.target = echoEvent.srcElement;
		}
		obj.eventHandler(echoEvent);
	};
	var eventKey = elem.id + eventType;
	EPRTA.objectHandlerList[eventKey] = f;
	if (elem.addEventListener) { // MOZ
		elem.addEventListener(eventType, f, true);
	} else if (elem.attachEvent) { // IE
		elem.attachEvent("on" + eventType, f);
	}
};

EPRTA.removeEventHandler = function(eventType, elem) {
	eventType = EP.Event.transmogifryEventType(eventType);

	var eventKey = elem.id + eventType;
	var f = EPRTA.objectHandlerList[eventKey];
	if (elem.removeEventListener) { // MOZ
		elem.removeEventListener(eventType,f,true);
	} else if (elem.detachEvent) { // IE
		elem.detachEvent("on" + eventType,f);
	}
	delete EPRTA.objectHandlerList[eventKey];
	delete f;
};
