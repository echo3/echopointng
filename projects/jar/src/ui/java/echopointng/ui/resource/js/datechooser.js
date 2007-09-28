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

/* */
EPDateChooser = function(elementId) {
	this.elementId = elementId;
	EP.ObjectMap.put(elementId, this); 
};

EPDateChooser.MS_IN_A_DAY = 1000*60*60*24;

/**
 * EPDateChooser has a ServerMessage processor.
 */
EPDateChooser.MessageProcessor = function() { };

EPDateChooser.MessageProcessor.process = function(messagePartElement) {
	//debugger;
    for (var i = 0; i < messagePartElement.childNodes.length; ++i) {
        if (messagePartElement.childNodes[i].nodeType == 1) {
            switch (messagePartElement.childNodes[i].tagName) {
	            case "init":
	                EPDateChooser.MessageProcessor.processInit(messagePartElement.childNodes[i]);
	                break;
	            case "dispose":
	                EPDateChooser.MessageProcessor.processDispose(messagePartElement.childNodes[i]);
	                break;
	            case "datesChanged":
	                EPDateChooser.MessageProcessor.processDatesChanged(messagePartElement.childNodes[i]);
	                break;
            }
        }
    }
};

EPDateChooser.MessageProcessor.processDispose = function(disposeMessageElement) {
    for (var itemXML = disposeMessageElement.firstChild; itemXML; itemXML = itemXML.nextSibling) {
        var elementId = itemXML.getAttribute("eid");
		EP.ObjectMap.destroy(elementId);
    }
};

EPDateChooser.MessageProcessor.processDatesChanged = function(directiveXML) {
	//debugger;
    for (var itemXML = directiveXML.firstChild; itemXML; itemXML = itemXML.nextSibling) {
        var elementId = itemXML.getAttribute("eid");
		var dateChooser = EP.ObjectMap.get(elementId);
		if (dateChooser) {
			var ymdStr = itemXML.getAttribute('displayedDate');
		    if (ymdStr) {
    			dateChooser.displayedDate = dateChooser.fromymd(ymdStr);
				// normalise to the first day of the month
				dateChooser.displayedDate.setDate(1);
			}
			ymdStr = itemXML.getAttribute('selectedDate');
		    if (ymdStr) {
    			dateChooser.selectedDate = dateChooser.fromymd(ymdStr);
			}
			dateChooser.updateDisplay();
		}
    }
};

EPDateChooser.MessageProcessor.processInit = function(initMessageElement) {
    for (var itemXML = initMessageElement.firstChild; itemXML; itemXML = itemXML.nextSibling) {
        var elementId = itemXML.getAttribute("eid");
		EP.ObjectMap.destroy(elementId);
		var dateChooser = new EPDateChooser(elementId);
		dateChooser.init(itemXML);
    }
};

/* */
EPDateChooser.prototype.init = function(itemXML) {
	// debugger;

    if (itemXML.getAttribute("enabled") == "false") {
        EchoDomPropertyStore.setPropertyValue(this.elementId, "EchoClientEngine.inputDisabled", true);
    }
    var ymdStr = itemXML.getAttribute('displayedDate');
    if (ymdStr) {
    	this.displayedDate = this.fromymd(ymdStr);
    } else {
    	this.displayedDate = this.clearTime(new Date());
    }
	// normalise to the first day of the month
	this.displayedDate.setDate(1);
	
    ymdStr = itemXML.getAttribute('selectedDate');
    if (ymdStr) {
    	this.selectedDate = this.fromymd(ymdStr);
    } else {
    	this.selectedDate = this.clearTime(new Date());
    }

    ymdStr = itemXML.getAttribute('maximumDate');
    if (ymdStr) {
    	this.maximumDate = this.fromymd(ymdStr)
    } else {
    	this.maximumDate = null;
    }
    ymdStr = itemXML.getAttribute('minimumDate');
    if (ymdStr) {
    	this.minimumDate = this.fromymd(ymdStr);
    } else {
    	this.minimumDate = null;
    }
	
    this.fastMode = itemXML.getAttribute('fastMode') == 'true';
	this.monthNames = itemXML.getAttribute('monthNames').split('|');
    this.navigationInhibited = itemXML.getAttribute('navigationInhibited') == 'true';
	this.yearRange = parseInt(itemXML.getAttribute('yearRange'),10);

	this.styleOutOfMonth = itemXML.getAttribute('styleOutOfMonth');
	this.styleOutOfMonthRollover = itemXML.getAttribute('styleOutOfMonthRollover');
	
	this.styleInMonth = itemXML.getAttribute('styleInMonth');
	this.styleInMonthRollover = itemXML.getAttribute('styleInMonthRollover');
	
	this.styleSelectedDate = itemXML.getAttribute('styleSelectedDate');
	this.styleSelectedDateRollover = itemXML.getAttribute('styleSelectedDateRollover');

	this.styleNotSelectable = itemXML.getAttribute('styleNotSelectable');
	this.styleNotSelectableRollover = itemXML.getAttribute('styleNotSelectableRollover');
	
	this.styleSelection = itemXML.getAttribute('styleSelection');
	this.styleSelectionRollover = itemXML.getAttribute('styleSelectionRollover');

	this.firstDayOfWeek	= EP.DOM.getIntAttr(itemXML,'firstDayOfWeek',2 /* Mon in java */);
	/*
	 * Now to complicate things, java.util.Calendar useS Sun = 1...Sat = 7, and the 
	 * developer can choose what day starts the week on the server, so we need to
	 * translate between the server Java dow numbers and the client side JS versions.
	 *
	 *			Java		JavaScript
	 * Sun		1				0
	 * Mon		2				1
	 * Tue		3				2
	 * Wed		4				3
	 * ...		...				...
	 * Sat		7				6
	 */
	this.firstDayOfWeek = (this.firstDayOfWeek - 1);
	 
	// event handlers	
	var id = this.elementId + '|bwd';
	var cellE = document.getElementById(id);
	if (cellE) {
		EP.Event.addHandler('click',cellE,this);
	}
	
	id = this.elementId + '|fwd';
	cellE = document.getElementById(id);
	if (cellE) {
		EP.Event.addHandler('click',cellE,this);	
	}

	id = this.elementId + '|sel';
	cellE = document.getElementById(id);
	EP.Event.addHandler('click',cellE,this);	
	EP.Event.addHandler('mouseover',cellE,this);	
	EP.Event.addHandler('mouseout',cellE,this);	

	id = this.elementId + '|month';
	cellE = document.getElementById(id);
	EP.Event.addHandler('change',cellE,this);	

	id = this.elementId + '|year';
	cellE = document.getElementById(id);
	EP.Event.addHandler('change',cellE,this);	
	
	for (var row = 0; row < 6; row++) {
		for (var col = 0; col < 7; col++) {
			id = this.elementId + '|' + row + '|' + col;
			cellE = document.getElementById(id);
			EP.Event.addHandler('click',cellE,this);
			EP.Event.addHandler('mouseover',cellE,this);
			EP.Event.addHandler('mouseout',cellE,this);
		}
	}
	this.updateDisplay();
};

/* */
EPDateChooser.prototype.destroy = function() {
	var id = this.elementId + '|bwd';
	var cellE = document.getElementById(id);
	if (cellE) {
		EP.Event.removeHandler('click',cellE);
	}
	
	id = this.elementId + '|fwd';
	cellE = document.getElementById(id);
	if (cellE) {
		EP.Event.removeHandler('click',cellE);
	}

	id = this.elementId + '|sel';
	cellE = document.getElementById(id);
	if (cellE) {
		EP.Event.removeHandler('click',cellE);
		EP.Event.removeHandler('mouseover',cellE);
		EP.Event.removeHandler('mouseout',cellE);
	}
	
	id = this.elementId + '|month';
	cellE = document.getElementById(id);
	if (cellE) {
		EP.Event.removeHandler('change',cellE);
	}
	
	id = this.elementId + '|year';
	cellE = document.getElementById(id);
	if (cellE) {
		EP.Event.removeHandler('change',cellE);
	}
	
	for (var row = 0; row < 6; row++) {
		for (var col = 0; col < 7; col++) {
			id = this.elementId + '|' + row + '|' + col;
			cellE = document.getElementById(id);
			if (cellE) {
				EP.Event.removeHandler('click',cellE);
				EP.Event.removeHandler('mouseover',cellE);
				EP.Event.removeHandler('mouseout',cellE);
			}
		}
	}
};

/* */
EPDateChooser.prototype.debug = function(s) {
	EP.debug(s);
	
	var debugE = document.getElementById('debug');
	if (debugE) {
		var htmlText = debugE.innerHTML;
		htmlText = htmlText.replace('<b>','');
		htmlText = htmlText.replace('</b>','');
		debugE.innerHTML = '<b>' + s + '<b>' + htmlText;
	}
};

/* */
EPDateChooser.prototype.copyDate = function(date) {
	var d = new Date(date.getTime());
	d = this.clearTime(d);
	return d;
};

/* */
EPDateChooser.prototype.clearTime = function(date) {
	date.setHours(0,0,0,0);
	return date;
};

/* */
EPDateChooser.prototype.addDays = function(date,amount) {
	var d = this.copyDate(date);
	d.setDate(date.getDate() + amount);
	return d;
};

/* */
EPDateChooser.prototype.subtractDays = function(date,amount) {
	 var d = this.addDays(date,(amount*-1));
	 return d;
};

/* */
EPDateChooser.prototype.addMonths = function(date,amount) {
	var d = this.copyDate(date);
	var years = 0;
	var newMonth = date.getMonth() + amount;
	if (newMonth < 0) {
		while (newMonth < 0)
		{
			newMonth += 12;
			years -= 1;
		}
	} else if (newMonth > 11) {
		while (newMonth > 11)
		{
			newMonth -= 12;
			years += 1;
		}
	}
	d.setMonth(newMonth);
	d.setFullYear(date.getFullYear() + years);
	return d;
};

/* */
EPDateChooser.prototype.subtractMonths = function(date,amount) {
	var d = this.addMonths(date,(amount*-1));
	return d;
};

/* */
EPDateChooser.prototype.getWeekOfYear = function(date, calendarYear, weekStartsOn) {
	if (! weekStartsOn) {
		weekStartsOn = 1; // Mondays
	}
	if (! calendarYear) {
		calendarYear = date.getFullYear();
	}
	var weekNum = -1;
	
	var jan1 = new Date(calendarYear,0,1);
	var jan1DayOfWeek = jan1.getDay();
	
	var month = date.getMonth();
	var day = date.getDate();
	var year = date.getFullYear();
	
	// Find the number of days the passed in date is away from the calendar year
	// start
	var dayOffset = Math.ceil((date.getTime()-jan1.getTime()) / EPDateChooser.MS_IN_A_DAY);
	if (dayOffset < 0 && dayOffset >= (-1 * jan1DayOfWeek)) {
		weekNum = 1;
	} else {
		weekNum = 1;
		var testDate = this.copyDate(jan1);
		while (testDate.getTime() < date.getTime() && testDate.getFullYear() == calendarYear) {
			weekNum += 1;
			// add a week
			testDate.setDate(testDate.getDate() + 7);
		}
	}
	return weekNum;
};


/*
 * Works out the offset from the starting
 * day of the month back to the closeSt precending "start of Week" day typically Monday. 
 * eg if the 1 Jan is a Thursday then the offset will be 3. 
 * If its a Monday then it will be 0 and if its a Sunday it will be 6.
 *
 *
 */
EPDateChooser.prototype.getMonthStartOffset = function(date) {
	// go the first day of the month for the given date
	var d = this.copyDate(date);
	d.setDate(1);	
	var offset = 0;
	var dow = d.getDay();
	while(dow != this.firstDayOfWeek) {
		offset += 1;
		d = this.subtractDays(d,1);
		dow = d.getDay();
	}
	return offset;
};

/*
 * Returns the Date closest to FirstDay Of the Week (eg Monday)
 * starting from the currently displayed date.
 */
EPDateChooser.prototype.getMonthStartDate = function(date) {
	// go to start of the month
	var date = this.copyDate(date);
	date.setDate(1);	
	
	var offset 	= this.getMonthStartOffset(date);
	var zerozeroDate = this.subtractDays(date, offset); 
	// this is first date shown
	return zerozeroDate;
};

/*
 *Test whether a date is within the range
 * of minumum and maximum date. If the withinMonth flag is set to true then
 * allows this test to fall to the start of the month
 *  
 */ 
EPDateChooser.prototype.isInRange = function(date, withinMonth) {
	if (! this.navigationInhibited) {
		return true;
	}
	withinMonth = (withinMonth ? withinMonth : false);
	if (this.minimumDate) {
		testDate = this.copyDate(this.minimumDate);
		if (withinMonth) {
			testDate.setDate(1);
		}
		if (date.getTime() < testDate.getTime()) {
			// not allowed to go there
			return false;
		}
	}
	if (this.maximumDate) {
		testDate = this.copyDate(this.maximumDate);
		if (withinMonth) {
			date = this.copyDate(date);
			date.setDate(1);
		}
		if (date.getTime() > testDate.getTime()) {
			// not allowed to go there
			return false;
		}
	}
	return true;
};


/* */ 
EPDateChooser.prototype.getElementText = function(sourceE) {
	if (sourceE.innerHTML) {
		return sourceE.innerHTML;
	} else {
		var s = '';
		var nodeList = sourceE.childNodes;
		for (i = 0; i < nodeList.length; i++) {
			var childNode = nodeList.item(i);
			if (childNode.nodeType == 3) { //Node.TEXT_NODE
				s = s + childNode.data;
			}
		}
		return s;
	}
};

/* */ 
EPDateChooser.prototype.setElementText = function(sourceE,text) {
	if (sourceE.innerHTML) {
		sourceE.innerHTML = ''+text;
	} else {
		var nodeList = sourceE.childNodes;
		for (i = 0; i < nodeList.length; i++) {
			var childNode = nodeList.item(i);
			if (childNode.nodeType == 3) { //Node.TEXT_NODE
				var newTextNode = document.createTextNode(''+text);
				childNode.parent.replaceChild(newTextNode,childNode);
			}
		}
	}
};

/* */ 
EPDateChooser.prototype.removeChildNodes = function(sourceE) {
	var nodeList = sourceE.childNodes;
	for (var i = nodeList.length-1; i >=0; i--) {
		var childNode = nodeList.item(i);
		sourceE.removeChild(childNode);
	}
};

EPDateChooser.prototype.toymd = function(date) {
	var year = String(date.getFullYear());
	var month = String(date.getMonth()+1);
	if (month.length < 2) {
		month = '0' + month;
	}
	var day = String(date.getDate());
	if (day.length < 2) {
		day = '0' + day;
	}
	return year+month+day;
}

EPDateChooser.prototype.fromymd = function(ymdStr) {
	var year = parseInt(ymdStr.substr(0,4),10);
	var month = parseInt(ymdStr.substr(4,2),10)-1;
	var day = parseInt(ymdStr.substr(6,2),10);
	var dateObj = new Date(year,month,day);
	this.clearTime(dateObj);
	return dateObj;
}


EPDateChooser.prototype.saveState = function() {
	// we are only interested in the yyyymmdd not the time.  	
	this.displayedDate.setDate(1);
	EP.Event.setClientValue(this.elementId,"displayedDate",this.toymd(this.displayedDate));
};

/* */
EPDateChooser.prototype.raiseAction = function(actionName,actionValue) {
	//debugger;
	if (! EchoClientEngine.verifyInput(this.elementId)) {
        return;
    }	
    if (document.selection && document.selection.clear) {
        document.selection.clear();
    }
	this.saveState();
    EchoClientMessage.setActionValue(this.elementId, actionName,actionValue);
    EchoServerTransaction.connect();
};

/*
 *Returns the Date of a given cell within
 * the datechooser based on the current selected date and the row and column
 * given.
 */
EPDateChooser.prototype.getCellDate = function(row,col) {
	var zerozeroDate = this.getMonthStartDate(this.displayedDate);
	var date = zerozeroDate;
	for (var rowX = 0; rowX < 6; rowX++) {
		for (var colX = 0; colX < 7; colX++) {
			if (rowX == row && colX == col) {
				return date;
			}
			date = this.addDays(date,1);
		}
	}
	return d;
};

/*
 *Sets the text of the given cell to be
 * the specified day from the Date
 */
EPDateChooser.prototype.setCellDate = function(date,row,col) {
	this.setCellDateImpl(date,row,col,this.styleOutOfMonth,this.styleInMonth, this.styleSelectedDate,this.styleNotSelectable);
};


/*
 *Sets the text of the given cell to be
 * the specified day from the Date
 */
EPDateChooser.prototype.setCellDateRollover = function(date,row,col) {
	if (! EchoClientEngine.verifyInput(this.elementId)) {
        return;
    }	
	this.setCellDateImpl(date,row,col,this.styleOutOfMonthRollover,this.styleInMonthRollover, this.styleSelectedDateRollover,this.styleNotSelectableRollover);
};

/* */
EPDateChooser.prototype.setCellDateImpl = function(date,row,col, styleOutOfMonth, styleInMonth, styleSelectedDate, styleNotSelectable) {
	var id = this.elementId + '|' + row + '|' + col;
	var displayedMonth  = this.displayedDate.getMonth();
	var selectedTime = this.selectedDate.getTime();
	var dateTime = date.getTime();
	var dateMonth = date.getMonth();
	var dateStr = ''+date.getDate();
	
	var cellE = document.getElementById(id);
	this.setElementText(cellE,dateStr);
	
	var styleStr = styleOutOfMonth;
	if (! this.isInRange(date)) {
		styleStr = styleNotSelectable;
	} else if (dateTime == selectedTime) {
		styleStr = styleSelectedDate;
	} else if (dateMonth == displayedMonth) {
		styleStr = styleInMonth;
	} else {
		styleStr = styleOutOfMonth;
	}
	EchoCssUtil.applyStyle(cellE,styleStr);
};


/* */
EPDateChooser.prototype.onSelectionClick = function() {
	this.displayedDate = this.copyDate(this.selectedDate);
	this.displayedDate.setDate(1);
	if (this.fastMode) {
		this.updateDisplay();
	} else {
		this.raiseAction('sel');
	}
};

/* */
EPDateChooser.prototype.onMonthChange = function(element) {
	//debugger;
	if (! EchoClientEngine.verifyInput(this.elementId)) {
        return;
    }	
	var month = element.options[element.selectedIndex].value;
	this.displayedDate.setMonth(month);
	this.displayedDate.setDate(1);
	
	if (this.fastMode) {
		this.updateDisplay();
	} else {
		this.raiseAction('month',month);
	}
};

/* */
EPDateChooser.prototype.onYearChange = function(element) {
	//debugger;
	if (! EchoClientEngine.verifyInput(this.elementId)) {
        return;
    }	
	var year = element.options[element.selectedIndex].value;
	this.displayedDate.setFullYear(year);
	this.displayedDate.setDate(1);
	if (this.fastMode) {
		this.updateDisplay();
	} else {
		this.raiseAction('year',year);
	}
};

/* */
EPDateChooser.prototype.allowedBackward = function() {
	var date = this.subtractMonths(this.displayedDate,1);
	// move to first day of month
	date.setDate(1);
	if (this.navigationInhibited && this.minimumDate) {
		var testDate = this.copyDate(this.minimumDate);
		// adjust to start of month to allow us to at least get on this page
		testDate.setDate(1);
		if (date.getTime() < testDate.getTime()) {
			// not allowed to go there
			return false;
		}
	}
	return true;
};

/* */
EPDateChooser.prototype.onMoveBackward = function() {
	if (! EchoClientEngine.verifyInput(this.elementId)) {
        return;
    }	
	if (this.allowedBackward()) {
		this.displayedDate = this.subtractMonths(this.displayedDate,1);
	}
	if (this.fastMode) {
		this.updateDisplay();
	} else {
		this.raiseAction('bwd');
	}
};

/* */
EPDateChooser.prototype.allowedForward = function() {
	var date = this.addMonths(this.displayedDate,1);
	// move to first day of month
	date.setDate(1);
	if (this.navigationInhibited && this.minimumDate) {
		var testDate = this.copyDate(this.maximumDate);
		// adjust to start of month to allow us to at least get on this page
		testDate.setDate(1);
		if (date.getTime() > testDate.getTime()) {
			// not allowed to go there
			return false;
		}
	}
	return true;
};

/* */
EPDateChooser.prototype.onMoveForward = function() {
	if (! EchoClientEngine.verifyInput(this.elementId)) {
        return;
    }	
	if (this.allowedForward()) {
		this.displayedDate = this.addMonths(this.displayedDate,1);
	}
	if (this.fastMode) {
		this.updateDisplay();
	} else {
		this.raiseAction('fwd');
	}
};

/*
 */
EPDateChooser.prototype.onCellClick = function(date) {
	if (! EchoClientEngine.verifyInput(this.elementId)) {
        return;
    }	
	if (this.isInRange(date)) {
		this.selectedDate = this.copyDate(date);
		this.displayedDate = this.copyDate(date);
		this.displayedDate.setDate(1);
		
		if (this.fastMode) {
			this.updateDisplay();
		}
		// a click on a date always results in an action
		this.raiseAction('click',this.toymd(date));
	}
};

/* */
EPDateChooser.prototype.updateMonth = function() {
	//debugger;
	var displayedMonth  = this.displayedDate.getMonth();
	var monthE = document.getElementById(this.elementId + '|month');
	if (monthE.tagName.toLowerCase() != 'select') {
		this.setElementText(monthE,this.monthNames[displayedMonth]);
	} else {
		this.removeChildNodes(monthE);
		var date = this.copyDate(this.displayedDate);
		for (var i = 0; i < this.monthNames.length; i++) {
			var month = i;
			date.setDate(1);
			date.setMonth(month);
			if (this.isInRange(date,true)) {
				var optionE = document.createElement('option');
				optionE.value = month;
				optionE.text = this.monthNames[month];
				try {
					monthE.add(optionE, null); // standards compliant; doesn't
											   // work in IE
				} catch(ex) {
					monthE.add(optionE); // IE only
				}
				if (month == displayedMonth) {
					optionE.selected = true;
				}
			}
		}
		//
		// There is a bug in IE (and when isnt there) that means that the
		// removal and addition of child options causes the select field to
		// be made visible even when it should not be (it its parent is not visible)
		// so if we force it invisible and then back to visible (ie no actual state change)
		// IE re-paints it correctly as visible but inside a non visible parent.
		//
		// This really affected DateField not DateChooser per se.
		//
		if (EP.isIE) {
			if (! EP.isHierarchyDisplayed(monthE) || ! EP.isHierarchyVisible(monthE)) {
				EP.setVisible(monthE,false);
				EP.setVisible(monthE,true);
			}
		}
	}
};

/* */
EPDateChooser.prototype.updateYear = function() {
	var displayedYear  = this.displayedDate.getFullYear();
	var yearE = document.getElementById(this.elementId + '|year');
	if (yearE.tagName.toLowerCase() != 'select') {
		this.setElementText(yearE,displayedYear);
	} else {
		this.removeChildNodes(yearE);
		var date = this.copyDate(this.displayedDate);
		for (var i = 0; i < this.yearRange; i++) {
			var year = Math.round(displayedYear - (this.yearRange/2)) + i;
			date.setDate(1);
			date.setFullYear(year);
			if (this.isInRange(date,true)) {
				var optionE = document.createElement('option');
				optionE.value = year;
				optionE.text = year;
				try {
					yearE.add(optionE, null); // standards compliant; doesn't
											  // work in IE
				} catch(ex) {
					yearE.add(optionE); // IE only
				}
				if (year == displayedYear) {
					optionE.selected = true;
				}
			}
		}
		// see above for an explanation
		if (EP.isIE) {
			if (! EP.isHierarchyDisplayed(yearE) || ! EP.isHierarchyVisible(yearE)) {
				EP.setVisible(yearE,false);
				EP.setVisible(yearE,true);
			}
		}
	}
};

/*
 *Called to update the Calendar DOM with
 * the current state of the displayed and selected date.
 *  
 */
EPDateChooser.prototype.updateDisplay = function() {
	//
	// update cells
	var zerozeroDate = this.getMonthStartDate(this.displayedDate);
	var date = zerozeroDate;
	for (var row = 0; row < 6; row++) {
		var woyE = document.getElementById(this.elementId + '|' + row + '|woy');
		if (woyE) {
			var woyDate = date;
			if (row == 0 && this.displayedDate.getMonth() == 0) {
				woyDate = this.displayedDate;
			}
			this.setElementText(woyE,this.getWeekOfYear(woyDate));
		}
		for (var col = 0; col < 7; col++) {
			// which style do we use
			// set the text of the cell
			this.setCellDate(date,row,col);
			
			// next date please
			date = this.addDays(date,1);
		}
	}
	var displayedMonth  = this.displayedDate.getMonth();
	var displayedYear  = this.displayedDate.getFullYear();
	//
	// update navigation buttons
	var fwdE = document.getElementById(this.elementId + '|fwd');
	if (fwdE) {
		EP.setVisible(fwdE,this.allowedForward());
	}
	
	var bwdE = document.getElementById(this.elementId + '|bwd');
	if (bwdE) {
		EP.setVisible(bwdE,this.allowedBackward());
	}
	//
	// update month bit
	this.updateMonth();
	//
	// update year bit
	this.updateYear();
	//
	// update selection
	if (this.fastMode) {
		var selE = document.getElementById(this.elementId + '|sel');
		this.setElementText(selE,this.selectedDate.toLocaleDateString());
	}

	// 
	// save our state
	this.saveState();
};

/**
 * */
EPDateChooser.prototype.eventHandler = function(echoEvent) {
	//debugger;
	var element = EP.Event.findIdentifiedElement(echoEvent);
	if (! element) {
		return;
	}
	var id = element.id.split('|');
	if (id[1] == 'bwd') {
		//----------------------------------
		// Navigate Backward
		//----------------------------------
		if (echoEvent.type == 'click') {
			this.onMoveBackward();
		}
	} else if (id[1] == 'fwd') {
		//----------------------------------
		// Navigate Forward
		//----------------------------------
		if (echoEvent.type == 'click') {
			this.onMoveForward();
		}
	} else if (id[1] == 'sel') {
		//----------------------------------
		// Selection
		//----------------------------------
		if (echoEvent.type == 'click') {
			this.onSelectionClick();
		}
		if (echoEvent.type == 'mouseover') {
			EchoCssUtil.applyStyle(element,this.styleSelectionRollover);
		}
		if (echoEvent.type == 'mouseout') {
			EchoCssUtil.applyStyle(element,this.styleSelection);
		}
	} else if (id[1] == 'month') {
		//----------------------------------
		// Month
		//----------------------------------
		if (echoEvent.type == 'change') {
			this.onMonthChange(element);
		}
	} else if (id[1] == 'year') {
		//----------------------------------
		// Year
		//----------------------------------
		if (echoEvent.type == 'change') {
			this.onYearChange(element);
		}
	} else {
		//----------------------------------
		// Cell Related
		//----------------------------------
		var row = parseInt(id[1],10);
		var col = parseInt(id[2],10);
		var date = this.getCellDate(row,col);
		if (echoEvent.type == 'click') {
			this.onCellClick(date);
		}
		if (echoEvent.type == 'mouseover') {
			this.setCellDateRollover(date,row,col);
		}
		if (echoEvent.type == 'mouseout') {
			this.setCellDate(date,row,col);
		}
	}
};
