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
EPSlider = function(elementId) {
	this.elementId = elementId;
	EP.ObjectMap.put(elementId, this); 
};

/**
 * EPSlider has a ServerMessage processor.
 */
EPSlider.MessageProcessor = function() { };

EPSlider.MessageProcessor.process = function(messagePartElement) {
    for (var i = 0; i < messagePartElement.childNodes.length; ++i) {
        if (messagePartElement.childNodes[i].nodeType == 1) {
            switch (messagePartElement.childNodes[i].tagName) {
	            case "init":
	                EPSlider.MessageProcessor.processInit(messagePartElement.childNodes[i]);
	                break;
	            case "dispose":
	                EPSlider.MessageProcessor.processDispose(messagePartElement.childNodes[i]);
	                break;
	            case "value":
	                EPSlider.MessageProcessor.processValue(messagePartElement.childNodes[i]);
	                break;
            }
        }
    }
};

EPSlider.MessageProcessor.processDispose = function(disposeMessageElement) {
    for (var item = disposeMessageElement.firstChild; item; item = item.nextSibling) {
        var elementId = item.getAttribute("eid");
		EP.ObjectMap.destroy(elementId);
    }
};

EPSlider.MessageProcessor.processValue = function(disposeMessageElement) {
	//debugger;
    for (var item = disposeMessageElement.firstChild; item; item = item.nextSibling) {
        var elementId = item.getAttribute("eid");
        var value = parseInt(item.getAttribute("value"),10);
		var slider = EP.ObjectMap.get(elementId);
		if (slider) {
			slider.setValue(value);
		}
    }
};

EPSlider.MessageProcessor.processInit = function(initMessageElement) {
	//debugger;
    for (var item = initMessageElement.firstChild; item; item = item.nextSibling) {
        var elementId = item.getAttribute("eid");
		EP.ObjectMap.destroy(elementId);
		var slider = new EPSlider(elementId);
		slider.init(item);
    }
};

/* ------------------------------------- */
EPSlider.prototype.init = function(itemXML) {
	this.containerE = document.getElementById(this.elementId);
	this.handleE = document.getElementById(this.elementId + '|Handle');
	this.railE = document.getElementById(this.elementId + '|Rail');
	this.infoE = document.getElementById(this.elementId + '|Info');
	
	this.horizontal = itemXML.getAttribute('horizontal') == 'true';
	this.immediateNotification = itemXML.getAttribute('immediateNotification') == 'true';
	
	this.handleURI = itemXML.getAttribute('handleURI');
	this.handleRolloverURI = itemXML.getAttribute('handleRolloverURI');
	
	this.valueRatio = parseFloat(itemXML.getAttribute('valueRatio'),10);
	this.valueDecimalPlaces = parseInt(itemXML.getAttribute('valueDecimalPlaces'),10);
	
	this.min = parseInt(itemXML.getAttribute('min'),10);
	this.max = parseInt(itemXML.getAttribute('max'),10);
	
	this.minPixels = 0;
	if (this.horizontal) {
		this.maxPixels = EP.getWidth(this.railE);
	} else {
		this.maxPixels = EP.getHeight(this.railE);
	}

	var value = parseInt(itemXML.getAttribute('value'),10);
	this.setValue(value);
	
	EP.Event.addHandler('mousedown',this.handleE,this);
	EP.Event.addHandler('mouseover',this.handleE,this);
	EP.Event.addHandler('mouseout',this.handleE,this);
	
	EP.Event.addHandler('mousedown',this.railE,this);
};



/* ------------------------------------- */
EPSlider.prototype.destroy = function() {
	EP.Event.removeHandler('mousedown',this.handleE);
	EP.Event.removeHandler('mouseover',this.handleE);
	EP.Event.removeHandler('mouseout',this.handleE);
	
	EP.Event.removeHandler('mousedown',this.railE);
};

/* ------------------------------------- */
EPSlider.prototype.debug = function(s) {
	EP.debug(s);
	
	var debugE = document.getElementById('debug');
	if (debugE) {
		var htmlText = debugE.innerHTML;
		htmlText = htmlText.replace('<b>','');
		htmlText = htmlText.replace('</b>','');
		debugE.innerHTML = '<b>' + s + '<b>' + htmlText;
	}
};


/* ------------------------------------- */
EPSlider.prototype.onDragStart = function(echoEvent,contextObj) {
	var xy = contextObj.elementStartX + contextObj.eventDeltaX;
	if (! this.horizontal) {
		xy = contextObj.elementStartY + contextObj.eventDeltaY;
	}
	this.dragOperationInProgress = true;
	this.handleE.src = this.handleRolloverURI;
	this.cancelTimer();
	this.moveInfo(xy);
};

/* ------------------------------------- */
EPSlider.prototype.onDragEnd = function(echoEvent,contextObj) {
	this.dragOperationInProgress = false;
	this.handleE.src = this.handleURI;
	this.hideInfoSlowly();
	if (this.immediateNotification) {
		this.notifyServer();
    }	
};

/* ------------------------------------- */
EPSlider.prototype.onDragMouseMove = function(echoEvent,contextObj) {
	var xy = contextObj.elementStartX + contextObj.eventDeltaX;
	if (! this.horizontal) {
		xy = contextObj.elementStartY + contextObj.eventDeltaY;
	}
	xy = this.normaliseXY(xy);
	this.saveState(xy);
	this.moveTo(xy);
	this.moveInfo(xy);
};

/* ------------------------------------- */
EPSlider.prototype.moveTo = function(xy) {
	if (this.horizontal) {
		EP.setX(this.handleE,xy);
	} else {
		EP.setY(this.handleE,xy);
	}
};

/* ------------------------------------- */
EPSlider.prototype.normaliseXY = function(xy) {
	//
	// the -5 is half the width/height of the slider handle image
	xy = (xy < this.minPixels-5) ? this.minPixels-5 : xy;
	xy = (xy > this.maxPixels-5) ? this.maxPixels-5 : xy;
	return xy;
};

/* ------------------------------------- */
EPSlider.prototype.saveState = function(xy) {
	this.xy = xy;
	var value = this.getValue(xy);
	EP.Event.setClientValue(this.elementId,"value",''+value);
};

/* ------------------------------------- */
EPSlider.prototype.notifyServer = function() {
	var value = this.getValue(this.xy);
	if (EchoClientEngine.verifyInput(this.elementId)) {
	    EchoClientMessage.setActionValue(this.elementId, "value",''+value);
	    EchoServerTransaction.connect();
	}
}


/* ------------------------------------- */
EPSlider.prototype.getValue = function(xy) {
	xy = xy + 5; // take into action our handle width
	var range = this.max - this.min;
	var rangePixels = this.maxPixels - this.minPixels;
	var rangeRatio = range / rangePixels;
	var value = xy * rangeRatio;
	value = value + this.min;
	if (this.valueRatio) {
		value = value * this.valueRatio;
		
	}
	if (this.valueDecimalPlaces > 0) {
		var powersOf = 10 * this.valueDecimalPlaces;
		value = parseInt(value * powersOf,10);
		value = value / powersOf;
		var s = '' + value;
		
		var zeroes = 0;
		var decimalPoint = s.indexOf('.');
		if (decimalPoint == -1) {
			zeroes = this.valueDecimalPlaces;
			s += '.';
			for (i = 0; i < zeroes; i++) {
				s += '0';
			}
		} else if (decimalPoint < s.length-1 - this.valueDecimalPlaces) {
			s = s.substring(0,decimalPoint+this.valueDecimalPlaces+1);
		} else {
			zeroes = s.length - decimalPoint - 1;
			for (i = zeroes; i < this.valueDecimalPlaces; i++) {
				s += '0';
			}
		}
		return s;
	} else {
		return parseInt(value,10);	
	}
};

/* ------------------------------------- */
EPSlider.prototype.setValue = function(value) {
	//debugger;
	var range = this.max - this.min;
	var rangePixels = this.maxPixels - this.minPixels;
	var rangeRatio = range / rangePixels;
	var xy = (value - this.min) / rangeRatio;
	if (this.min < 0) {
		xy = (value + Math.abs(this.min)) / rangeRatio;
	}
	xy = parseInt(xy,10);
	xy = xy - 5; // take into action our handle width
	this.moveTo(xy);
};

/* ------------------------------------- */
EPSlider.prototype.hideInfo = function() {
	if (this.infoE) {
		EP.setVisible(this.infoE,false);
		EP.setDisplayed(this.infoE,false);
	}
};

/* ------------------------------------- */
EPSlider.prototype.hideInfoSlowly = function() {
	if (! this.infoE) {
		return;
	}
	this.cancelTimer();
	var slider = this;
	// hide the info after a few seconds
	var func = function() {
		slider.hideInfo();
		slider.timerId = null;
	};
	this.timerId = window.setTimeout(func,950);
};

/* ------------------------------------- */
EPSlider.prototype.cancelTimer = function(xy) {
	if (this.timerId) {
		window.clearTimeout(this.timerId);
		this.timerId = null;
	}
};

/* ------------------------------------- */
EPSlider.prototype.moveInfo = function(xy) {
	if (this.infoE) {
		this.infoE.innerHTML = this.getValue(xy);
		
		// just in case to stop flicker
		EP.setDisplayed(this.infoE,false);
		EP.setVisible(this.infoE,false);
		
		EP.setDisplayed(this.infoE,true);
			
		var infoHeight 	= EP.getHeight(this.infoE);
		var infoWidth 	= EP.getWidth(this.infoE);
		var handleX 	= EP.getX(this.handleE);
		var handleY 	= EP.getY(this.handleE);
		var handleHeight = EP.getHeight(this.handleE);
		var handleWidth  = EP.getWidth(this.handleE);
		if (this.horizontal) {
			x = handleX;
			y = handleY + handleHeight + 10;
			EP.setX(this.infoE,x);
			EP.setY(this.infoE,y);
		} else {
			x = handleX + handleWidth;
			y = handleY;
			EP.setX(this.infoE,x);
			EP.setY(this.infoE,y);
		}
		EP.setVisible(this.infoE,true);
	}
};



/* ------------------------------------- */
EPSlider.animateMoveToImpl = function(elementId, targetXY, count) {
	var slider = EP.ObjectMap.get(elementId);
	if (! slider) {
		return;
	}
	var done = false;
	var xy = 0;
	if (slider.horizontal) {
		xy = EP.getX(slider.handleE);
	} else {
		xy = EP.getY(slider.handleE);
	}
	var stepAmount = 2 + count;
	count++;
	if (targetXY < xy) {
		stepAmount = -stepAmount;
		xy = xy + stepAmount;
		if (xy < targetXY) {
			xy = targetXY;
			done = true;
		}
	} else {
		xy = xy + stepAmount;
		if (xy > targetXY) {
			xy = targetXY;
			done = true;
		}
	}
	xy = slider.normaliseXY(xy);
	if (slider.horizontal) {
		EP.setX(slider.handleE,xy);
	} else {
		EP.setY(slider.handleE,xy);
	}
	if (! done) {
		slider.timerId = window.setTimeout('EPSlider.animateMoveToImpl(\'' + elementId +'\',' + targetXY +',' + count + ')',25);
	} else {
		slider.moveInfo(xy);
		slider.hideInfoSlowly();
		// do we raise an action event
		if (slider.immediateNotification) {
			slider.notifyServer();
	    }	
	}
};

/* ------------------------------------- */
EPSlider.prototype.animateMoveTo = function(targetXY) {
	this.cancelTimer();
	this.hideInfo();
	this.saveState(targetXY);
	EPSlider.animateMoveToImpl(this.elementId,targetXY,0);
};


/* ------------------------------------- */
EPSlider.prototype.eventHandler = function(echoEvent) {
	if (echoEvent.type == 'mousedown') {
		if (echoEvent.target == this.handleE) {
			EP.Drag.startDragOperation(echoEvent,this.handleE,this);
		}
		if (echoEvent.target == this.railE) {
			var xy = echoEvent.layerX ? echoEvent.layerX : echoEvent.offsetX;
			if (! this.horizontal) {
				xy = echoEvent.layerY ? echoEvent.layerY : echoEvent.offsetY;
			}
			// move there using animation
			xy = this.normaliseXY(xy);
			this.animateMoveTo(xy);
		}
	}
	if (! this.dragOperationInProgress) {
		if (echoEvent.type == 'mouseover') {
			this.handleE.src = this.handleRolloverURI;
		}
		if (echoEvent.type == 'mouseout') {
			this.handleE.src = this.handleURI;
		}
	}
};
