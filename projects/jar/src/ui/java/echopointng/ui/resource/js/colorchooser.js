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

EPColorChooser = function(elementId) {
	this.elementId = elementId;
	EP.ObjectMap.put(elementId, this); 
};

/**
 * EPColorChooser has a ServerMessage processor.
 */
EPColorChooser.MessageProcessor = function() { };

EPColorChooser.MessageProcessor.process = function(messagePartElement) {
	//debugger;
    for (var i = 0; i < messagePartElement.childNodes.length; ++i) {
        if (messagePartElement.childNodes[i].nodeType == 1) {
            switch (messagePartElement.childNodes[i].tagName) {
	            case "init":
	                EPColorChooser.MessageProcessor.processInit(messagePartElement.childNodes[i]);
	                break;
	            case "dispose":
	                EPColorChooser.MessageProcessor.processDispose(messagePartElement.childNodes[i]);
	                break;
	            case "value":
	                EPColorChooser.MessageProcessor.processValue(messagePartElement.childNodes[i]);
	                break;
            }
        }
    }
};

EPColorChooser.MessageProcessor.processDispose = function(disposeMessageElement) {
    for (var item = disposeMessageElement.firstChild; item; item = item.nextSibling) {
        var elementId = item.getAttribute("eid");
		EP.ObjectMap.destroy(elementId);
    }
};

EPColorChooser.MessageProcessor.processValue = function(disposeMessageElement) {
	//debugger;
    for (var item = disposeMessageElement.firstChild; item; item = item.nextSibling) {
        var elementId = item.getAttribute("eid");
        var color = parseInt(item.getAttribute("color"),10);
		var chooser = EP.ObjectMap.get(elementId);
		if (chooser) {
			dateChooser.setValue(color);
		}
    }
};

EPColorChooser.MessageProcessor.processInit = function(initMessageElement) {
    for (var item = initMessageElement.firstChild; item; item = item.nextSibling) {
        var elementId = item.getAttribute("eid");
		EP.ObjectMap.destroy(elementId);
		var chooser = new EPColorChooser(elementId);
		chooser.init(item);
    }
};

EPColorChooser.prototype.destroy = function() {
	var cellE = document.getElementById(this.elementId + '|InfoSwatch');
	if (cellE) {
		EP.Event.removeHandler('click',cellE);
		EP.Event.removeHandler('mouseover',cellE);
		EP.Event.removeHandler('mouseout',cellE);
	}
	for (var i = 0; i < this.colors.length; i++) {
		var color = this.colors[i];
		cellE = document.getElementById(this.elementId + '|' + color);
		if (cellE) {
			EP.Event.removeHandler('click',cellE);
			EP.Event.removeHandler('mouseover',cellE);
			EP.Event.removeHandler('mouseout',cellE);
		}
	}
};

/*
 * ---------------------------
 */
EPColorChooser.prototype.init = function(itemXML) {
	//debugger;
	
	this.serverNotify = itemXML.getAttribute('serverNotify') == 'true';
	this.enabled = itemXML.getAttribute("enabled") == "true"; 
    if (! this.enabled) {
        EchoDomPropertyStore.setPropertyValue(this.elementId, "EchoClientEngine.inputDisabled", true);
    }
	this.styleDefault = itemXML.getAttribute('styleDefault');
	this.styleSwatch = itemXML.getAttribute('styleSwatch');
	
	this.swatchesPerRow = itemXML.getAttribute('swatchesPerRow');
	
	this.interestedPartyId = itemXML.getAttribute('interestedPartyId');
	this.currentColorSelection = itemXML.getAttribute('currentColorSelection');
	this.currentColorSelectionText = itemXML.getAttribute('currentColorSelectionText');
	this.showCurrentColorSelectionSwatch = itemXML.getAttribute('showCurrentColorSelectionSwatch') == 'true';
	
	var colorStr = itemXML.getAttribute('colors');
	if (! colorStr) {
		colorStr = this.createWebColorStr();
	}
	this.colors = colorStr.split('|');

	var colorTitlesStr = itemXML.getAttribute('colorTitles');
	var colorTitleArray = colorTitlesStr.split('|');
	this.colorTitles = [];
	for(var i=0; i<this.colors.length; i++) {
		var color = this.colors[i];
		this.colorTitles[color] = colorTitleArray[i];
	}

	var containereid = itemXML.getAttribute('container-eid');
	var parentE = document.getElementById(containereid);
	this.createColorChooser(parentE);
	
	this.updateDisplay(null);
};


/*
 * ---------------------------
 */
EPColorChooser.prototype.createColorChooser = function(parentE) {
	var tableE = document.createElement('table'); parentE.appendChild(tableE);
	tableE.id = this.elementId;
	tableE.cellPadding = 0;
	tableE.cellSpacing = 0;
	EchoCssUtil.applyStyle(tableE,this.styleDefault);
	
	var saveBorderStyle = tableE.style.border;

	var tbodyE = document.createElement('tbody'); tableE.appendChild(tbodyE);
	var trE = document.createElement('tr'); tbodyE.appendChild(trE);
	var tdE = document.createElement('td'); trE.appendChild(tdE);

	// inner table
	tableE = document.createElement('table'); tdE.appendChild(tableE);
	tableE.cellPadding = 0; tableE.cellSpacing = 0;
	
	tbodyE = document.createElement('tbody'); tableE.appendChild(tbodyE);
	trE = document.createElement('tr'); tbodyE.appendChild(trE);
	
	for (var i = 0; i < this.colors.length; i++) {
		if (i > 0 && i % this.swatchesPerRow == 0) {
			trE = document.createElement('tr'); tbodyE.appendChild(trE);
		}
		tdE = document.createElement('td'); trE.appendChild(tdE);
		var color = this.colors[i];
		this.createSwatch(tdE,color);
	}
	//
	// Info row
	var maxRows = 2;
	if (! this.showCurrentColorSelectionSwatch) {
		maxRows = 1;
	}
	var parentE = tbodyE;
	for (i = 0; i < maxRows; i++) {
		trE = document.createElement('tr'); 	parentE.appendChild(trE);
		tdE = document.createElement('td'); trE.appendChild(tdE);
		tdE.colSpan = this.swatchesPerRow;

		divE = document.createElement('div'); tdE.appendChild(divE);
		divE.style.margin = '1px;'
		divE.style.border = saveBorderStyle;

		
		tableE = document.createElement('table'); divE.appendChild(tableE);
		tableE.cellPadding = 0; tableE.cellSpacing = 0; 
		tableE.style.width = '100%';
		tbodyE = document.createElement('tbody'); tableE.appendChild(tbodyE);
		trE = document.createElement('tr'); tbodyE.appendChild(trE);

		// a bigger swatch area for the color
		tdE = document.createElement('td'); trE.appendChild(tdE);
		var infoSwatchE = document.createElement('div'); tdE.appendChild(infoSwatchE); 
		if (i == 0) {
			infoSwatchE.id = this.elementId + '|InfoSwatch';
		} else {
			infoSwatchE.id = this.elementId + '|InfoSwatchCurrentColorSelection';
		}
		infoSwatchE.innerHTML = '<span>&nbsp;</span>';
		infoSwatchE.style.border = '1px #000000 solid';
		infoSwatchE.style.margin = '1px';
		infoSwatchE.style.width = '50px';
		var colorToUse = '#FFFFFF';
		if (i == 1) {
			colorToUse = this.currentColorSelection;
		}
		infoSwatchE.backgroundColor = colorToUse;
		infoSwatchE.title = this.getColorTitle(colorToUse);
		infoSwatchE.setAttribute('color',colorToUse);
		//
		// we can click on this to select a color
		if (this.enabled) {
			infoSwatchE.style.cursor = 'pointer';
			
			EP.Event.addHandler('mouseover',infoSwatchE,this);
			EP.Event.addHandler('mouseout',infoSwatchE,this);
			EP.Event.addHandler('click',infoSwatchE,this);
		}

		// a text area for the displayed color
		tdE = document.createElement('td'); trE.appendChild(tdE);
		tdE.style.width = '100%';
		var infoE = document.createElement('div'); tdE.appendChild(infoE);
		if (i == 0) {
			infoE.id = this.elementId + '|Info';
		} else {
			infoE.id = this.elementId + '|InfoCurrentColorSelection';
		}
		infoE.innerHTML = '&nbsp;';
		infoE.style.paddingLeft = '5px';
		infoE.style.fontSize = '8pt';
	}
};

/*
 * ---------------------------
 */
EPColorChooser.prototype.createSwatch = function(parentE, color) {
		var divE = document.createElement('DIV');
		parentE.appendChild(divE);
		divE.style.backgroundColor=color;
		divE.innerHTML = '<span></span>';
		divE.title = this.getColorTitle(color);	
		divE.id = this.elementId + '|' + color;
		divE.setAttribute('color',color);
		EchoCssUtil.applyStyle(divE,this.styleSwatch);
		divE.style.margin = '1px';
		//
		// add event handlers
		if (this.enabled) {
			divE.style.cursor = 'pointer';
			EP.Event.addHandler('mouseover',divE,this);
			EP.Event.addHandler('mouseout',divE,this);
			EP.Event.addHandler('click',divE,this);
		}
};

/*
 * ---------------------------
 */
EPColorChooser.prototype.createWebColorStr = function() {
	var webColorStr = ''
	for(var r=15;r>=0;r-=3){
		for(var g=0;g<=15;g+=3){
			for(var b=0;b<=15;b+=3){
				var red = r.toString(16);
				var green = g.toString(16);
				var blue = b.toString(16)
				var color = '#' + red + red + green + green + blue + blue;
				color = (''+color).toUpperCase();
				
				webColorStr += color + '|';
			}
		}
	}
	// there are 5 spots left in a 17x12 grid so add white/grey/black etc here
	webColorStr	+= '#FFFFFF' + '|';
	webColorStr	+= '#D3D3D3' + '|';
	webColorStr	+= '#A9A9A9' + '|';
	webColorStr	+= '#778899' + '|';
	webColorStr	+= '#000000';
	
	return webColorStr;
};

/*
 * ---------------------------
 */
EPColorChooser.prototype.getColorTitle = function(color) { 
	var title = this.colorTitles[color];
	return (title ? title : color);
}

EPColorChooser.prototype.onColorClick = function(color) {
	alert(this.elementId + " :  BIG DEAL - onColorClick called - " + color);
}


EPColorChooser.prototype.updateDisplay = function(color) {
	var infoE = document.getElementById(this.elementId + '|Info');
	var title = this.getColorTitle(color);
	if (infoE && color) {
		var infoStr = (''+color).toUpperCase();
		if (title.toUpperCase() != infoStr) {
			infoStr += ' : ' + title;
		}
		infoE.innerHTML = ' ' + infoStr;
		
		var infoSwatchE = document.getElementById(this.elementId + '|InfoSwatch');
		if (infoSwatchE) {
			infoSwatchE.style.backgroundColor = color;
			infoSwatchE.title = color;
			infoSwatchE.setAttribute('color',color);
		}
	}
	// we will find this if its created in the first place
	infoE = document.getElementById(this.elementId + '|InfoCurrentColorSelection');
	if (infoE) {
		infoStr = (''+this.currentColorSelection).toUpperCase();
		if (this.currentColorSelectionText) {
			infoStr = this.currentColorSelectionText + infoStr;
		}
		infoE.innerHTML = ' ' + infoStr;
		var infoSwatchE = document.getElementById(this.elementId + '|InfoSwatchCurrentColorSelection');
		if (infoSwatchE) {
			infoSwatchE.style.backgroundColor = this.currentColorSelection;
			infoSwatchE.title = this.getColorTitle(this.currentColorSelection);
			infoSwatchE.setAttribute('color',this.currentColorSelection);
		}			
	}
}

/*
 * -----------------------------------  
 */
EPColorChooser.prototype.onCellClick = function(color) {
	this.currentColorSelection = color;
	this.updateDisplay(color);
	if (this.serverNotify) {
	    EchoClientMessage.setActionValue(this.elementId, 'click',color);
    	EchoServerTransaction.connect();
	}
	var interestedParty = EP.ObjectMap.get(this.interestedPartyId);
	if (interestedParty && interestedParty.onColorClick) {
		interestedParty.onColorClick(color);
	}
}

/*
 * -----------------------------------  
 */
EPColorChooser.prototype.eventHandler = function(echoEvent) {
	if (! EchoClientEngine.verifyInput(this.elementId)) {
        return;
    }	
	//debugger;
	var elementE = EP.Event.findIdentifiedElement(echoEvent);
	if (! elementE) {
		return;
	}
	var id = elementE.id.split('|');
	//----------------------------------
	// Cell Related
	//----------------------------------
	if (echoEvent.type == 'click') {
		color = elementE.getAttribute('color');
		this.onCellClick(color);
	}
	if (echoEvent.type == 'mouseover') {
		color = elementE.getAttribute('color');
		if (id[1].indexOf('Info') == -1) {
			elementE.style.border = '1px black dashed';
		}
		this.updateDisplay(color);
	}
	if (echoEvent.type == 'mouseout') {
		if (id[1].indexOf('Info') == -1) {
			EchoCssUtil.applyStyle(elementE,this.styleSwatch);
		}
	}
};
