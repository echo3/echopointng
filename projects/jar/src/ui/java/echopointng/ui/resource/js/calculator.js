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
var epcalc_calculatorKeyMap;

//-------------------------------
// A list of the key codes we can handle
//-------------------------------
epc_setupKeys = function() {
	if (epcalc_calculatorKeyMap) {
		return;
	}
	epcalc_calculatorKeyMap = [];

	var keyCode  = 48; // VK_0 -> VK_9
	for (i = keyCode; i <= 57; i++) {
		epcalc_calculatorKeyMap[i] = String.fromCharCode(i);
	}
	keyCode  = 96; // VK_NUMPAD0 -> VK_NUMPAD9
	for (i = keyCode; i <= 105; i++) {
		var cmdCode = 48 + (i - 96);
		epcalc_calculatorKeyMap[i] = String.fromCharCode(cmdCode);
	}
	epcalc_calculatorKeyMap[8] = 'bksp';	// VK_BACKSPACE
	epcalc_calculatorKeyMap[3] = 'c';		// VK_CANCEL
	epcalc_calculatorKeyMap[12] = 'ce';	// VK_CLEAR
	epcalc_calculatorKeyMap[13] = '=';	// VK_ENTER
	epcalc_calculatorKeyMap[14] = '=';	// VK_RETURN
	epcalc_calculatorKeyMap[27] = 'c';	// VK_ESC
	epcalc_calculatorKeyMap[46] = 'ce';	// VK_DELETE
	epcalc_calculatorKeyMap[61] = '=';	// VK_EQUALS
	epcalc_calculatorKeyMap[106] = '*';	// VK_MULTIPLY
	epcalc_calculatorKeyMap[107] = '+';	// VK_ADD
	epcalc_calculatorKeyMap[109] = '-';	// VK_SUBTRACT
	epcalc_calculatorKeyMap[110] = '.';	// VK_DECIMAL
	epcalc_calculatorKeyMap[111] = '/';	// VK_DIVIDE
	epcalc_calculatorKeyMap[120] = '+/-';	// VK_F9
	epcalc_calculatorKeyMap[188] = '.';	// VK_COMMA
	epcalc_calculatorKeyMap[190] = '.';	// VK_PERIOD
	epcalc_calculatorKeyMap[191] = '/';	// VK_SLASH
	
	epcalc_calculatorKeyMap[82] = '1/x';	// VK_R
};

//-------------------------------
// Our Fraction Object hold integer
// numerators and denomintators to try and stop
// loss of precision.
//-------------------------------
EPFraction = function(numberNorS) {
	var numberS = '' + numberNorS;
	var decimalPoint = numberS.indexOf('.'); 
	var expPoint = numberS.indexOf('e'); 
	if (decimalPoint >= 0 && expPoint == -1) {
		var decimalPlaces = numberS.length - decimalPoint - 1;
		// remove the decimal place. same as multiplying it out
		numberS = numberS.substr(0,decimalPoint) + numberS.substr(decimalPoint+1);
		this.numerator = parseFloat(numberS);
		this.denominator = Math.abs(Math.pow(10,decimalPlaces));
	} else {
		this.numerator = parseFloat(numberS);
		this.denominator = 1;
	}
};

//-------------------------------
// Operates on this fraction against
// another fraction. The operators are
// '+' '-' '*' and '/'
//-------------------------------
EPFraction.prototype.operate = function(operator, rhsFrac) {
	if (operator == '/') {
		// 		  (iNumerator1 * iDenominator2)
		// ---------------------------------------------------------
		// 		(iDenominator1 * iNumerator2)
		this.numerator	 = this.numerator   * rhsFrac.denominator; 
		this.denominator = this.denominator * rhsFrac.numerator;
		
	} else if (operator == '*') {
		// 		  (iNumerator1 * iNumerator2)
		// ---------------------------------------------------------
		// 		(iDenominator1 * iDenominator2)
		this.numerator   = this.numerator   * rhsFrac.numerator; 
		this.denominator = this.denominator * rhsFrac.denominator;
		
	} else if (operator == '+') {
		// (iNumerator1 * iDenominator2) + (iNumerator2 * iDenominator1)
		// ---------------------------------------------------------
        // 				iDenominator1 * iDenominator2
		this.numerator   = (this.numerator   * rhsFrac.denominator) + (rhsFrac.numerator * this.denominator);
		this.denominator = (this.denominator * rhsFrac.denominator);
		
	} else if (operator == '-') {
		// (iNumerator1 * iDenominator2) - (iNumerator2 * iDenominator1)
		// ---------------------------------------------------------
        // 				iDenominator1 * iDenominator2
		this.numerator   = (this.numerator   * rhsFrac.denominator) - (rhsFrac.numerator * this.denominator);
		this.denominator = (this.denominator * rhsFrac.denominator); 
	}
};

//-------------------------------
EPFraction.prototype.reverseSign = function() {
	this.numerator *= -1; 
};

EPFraction.prototype.inverse = function() {
	var tmp = this.numerator;
	this.numerator = this.denominator;
	this.denominator =tmp;
};

EPFraction.prototype.sqrt = function() {
	var number = this.toNumber();
	this.numerator = Math.sqrt(number);
	this.denominator = 1;
};

//-------------------------------
EPFraction.prototype.toNumber = function() {
	return this.numerator / this.denominator;
};

//-------------------------------
EPFraction.prototype.toString = function() {
	return '' + this.toNumber();
};

//-------------------------------
EPFraction.prototype.toFraction = function() {
	return '' + this.numerator + '/' + this.denominator;
};

//-------------------------------
// Our Calculator Object
//-------------------------------
EPCalculator = function(elementId, value, memory) {
	this.elementId = elementId;
	this.state = 'start';
	this.lhsFrac = null;
	this.rhsFrac = null;
	this.numberS = '';
	this.memoryF = null;
	this.operator = null;
	this.decimalPlaces = 25;
	this.eDisplay = null;
	this.eOperation = null;
	this.eMemory = null;
	if (value && ! isNaN(parseFloat(value))) {
		this.state = 'lhs';
		this.numberS = value;
	}
	if (memory && ! isNaN(parseFloat(memory))) {
		this.memoryF = parseFloat(memory);
	}
	EP.ObjectMap.put(elementId, this); 
};



//-------------------------------
EPCalculator.prototype.debug = function() {
	var s = ''; 
	s += ' state:<b>' + this.state + '</b>';
	s += ' ns:' + this.numberS + '';
	s += ' op:' + this.operator + '';
	s += ' m:' + this.memoryF + '';
	s += ' lhs:' + (this.lhsFrac == null ? null : this.lhsFrac.toFraction()) + '';
	s += ' rhs:' + (this.rhsFrac == null ? null : this.rhsFrac.toFraction()) + '';
	
	EP.debug(s);
};

//-------------------------------
EPCalculator.prototype.isNumber = function(cmd) {
	switch (cmd) {
	   case '0' :
	   case '1' :
	   case '1' :
	   case '2' :
	   case '3' :
	   case '4' :
	   case '5' :
	   case '6' :
	   case '7' :
	   case '8' :
	   case '9' :
	   case '.' :
	   	return true;
	   default :
	   	return false;
	}
};

//-------------------------------
EPCalculator.prototype.isOperator = function(cmd) {
	switch (cmd) {
	   case '/' :
	   case '*' :
	   case '-' :
	   case '+' :
	   		return true;
	   default :
	   		return false;
	}
};

//-------------------------------
EPCalculator.prototype.isMemory = function(cmd) {
	switch (cmd) {
	   case 'mc' :
	   case 'm+' :
	   case 'mr' :
	   case 'ms' :
	   		return true;
	   default :
	   		return false;
	}
};

//-------------------------------
EPCalculator.prototype.isModifier = function(cmd) {
	switch (cmd) {
	   case 'sqrt' :
	   case '+/-' :
	   case '1/x' :
	   		return true;
	   default :
	   		return false;
	}
};

//-------------------------------
EPCalculator.prototype.reset = function() {
	this.lhsFrac = null;
	this.rhsFrac = null;
	this.numberS = '';
	this.operator = null;
	this.state = 'start';
	this.updateDisplay(0);
};

//-------------------------------
EPCalculator.prototype.transferNumberS = function() {
	if (this.state == 'lhs' || this.state == 'postlhs') {
		this.lhsFrac = new EPFraction(parseFloat(this.numberS));
	} else {
		this.rhsFrac = new EPFraction(parseFloat(this.numberS));
	}
};

//-------------------------------
EPCalculator.prototype.pressedModifier = function(cmd) {
	var numberN = parseFloat(this.numberS);
	
	if (cmd == '+/-') {
		numberN = (numberN > 0 ? - numberN : Math.abs(numberN));
		if (this.lhsFrac != null && this.state == 'equals') {
			this.lhsFrac.reverseSign();
		}
	} else if (cmd == 'sqrt') {
		numberN = (numberN > 0 ? Math.sqrt(numberN) : numberN);
		if (this.lhsFrac != null && this.state == 'equals') {
			this.lhsFrac.sqrt();
		}
	} else if (cmd == '1/x') {
		numberN = (1 /numberN);
		if (this.lhsFrac != null && this.state == 'equals') {
			this.lhsFrac.inverse();
		}
	}
	this.numberS  = '' + numberN;
	this.updateDisplay(this.numberS);

	if (this.state == 'lhs') {
		return 'postlhs';
	}
	if (this.state == 'rhs') {
		return 'postrhs';
	}
	return this.state;
};

//-------------------------------
EPCalculator.prototype.pressedMemory = function(cmd) {
	if (cmd == 'mc') {
		this.memoryF = null;
		return this.state;
	} else if (cmd == 'mr' && this.memoryF != null) {
		this.numberS = '' + this.memoryF; 
		this.updateDisplay(this.numberS);
		if (this.state == 'start' || this.state == 'lhs' || this.state == 'equals' || this.state == 'postlhs') {
			return 'postlhs';
		} else {
			return 'postrhs';
		}
	}
	var numberN = parseFloat(this.numberS);
	if (cmd == 'ms'  && this.numberS.length > 0) {
		this.memoryF = numberN;
	} else if (cmd == 'm+') {
		if (isNaN(numberN)) {
			return this.state;
		}
		
		if (this.memoryF == null) {
			this.memoryF = numberN;
		} else {
			var memoryF = this.memoryF;
			memoryF += numberN;
			this.memoryF = memoryF;
		}
		if (this.state == 'lhs') {
			return 'postlhs';
		}
		if (this.state == 'rhs') {
			return 'postrhs';
		}
	}
	return this.state;	
};

//-------------------------------
EPCalculator.prototype.pressedPercent = function() {
	if (this.lhsFrac == null) {
		return this.state;
	}
	
	var numberFrac;
	var lhsFrac = this.lhsFrac;
	var numberN = parseFloat(this.numberS);
	if (isNaN(numberN)) {
		numberFrac = lhsFrac;
	} else {
		numberFrac = new EPFraction(numberN);
	}
	if (isNaN(numberN) == false) {
		numberFrac.operate('/',new EPFraction(100));
		
		lhsFrac.operate('*',numberFrac);
		if (this.state == 'lhs' || this.state == 'postlhs') {
			this.lhsFrac = lhsFrac;
		} else {
			this.rhsFrac = lhsFrac;
		}

		this.numberS = lhsFrac.toString();
		this.updateDisplay(this.numberS);
		return 'operator';
	}
	return this.state;
};


//-------------------------------
EPCalculator.prototype.pressedBackSpace = function() {
	var numberS = this.numberS;
	if (numberS.length > 0) {
		numberS = numberS.substr(0,numberS.length-1);
		if (numberS.length == 0) {
			numberS = '0';
		}
		this.numberS = numberS;
		this.updateDisplay(this.numberS);
	}
};

//-------------------------------
//Performs the actual calculation
//and returns the state to place
//the machine in.
//-------------------------------
EPCalculator.prototype.pressedOperation = function(operator) {
	if (this.lhsFrac == null || this.rhsFrac == null) {
		return this.state;
	}
		
	if (isNaN(this.lhsFrac.toNumber())) {
		alert('ASSERT : LHS value is not a number : ' + this.lhsFrac);
	}
	if (isNaN(this.rhsFrac.toNumber())) {
		alert('ASSERT : RHS value is not a number : ' + this.rhsFrac);
	}
	
	if (operator == '/' && this.rhsFrac.toNumber() == 0) {
		this.updateDisplay('Cannot divide by zero');
		return 'dividebyzero';
	}
	this.lhsFrac.operate(operator,this.rhsFrac);
	this.updateDisplay(this.lhsFrac);
	return 'operator';
};

//-------------------------------
EPCalculator.prototype.pressedNumber = function(cmd) {
	var decimalPlace = this.numberS.indexOf('.'); 
	if (cmd == '.') {
		// do we have a period in the number already
		if (decimalPlace != -1) {
			return;
		}
		if (this.numberS.length == 0) {
			this.numberS = '0';
		}
	}
	this.numberS = this.numberS + cmd;
	
	// remove preceding 0's
	if (this.numberS.length > 1 && this.numberS.charCodeAt(0) == 48 && this.numberS.charCodeAt(1) != 46) {
		this.numberS = this.numberS.substr(1);
	}
	//
	// remove preceding 0 zeroes
	this.updateDisplay(this.numberS);
};


//-------------------------------
//Maps keys to calculator commands
//-------------------------------
EPCalculator.prototype.doKeyCommand = function(element, echoEvent) {
	var keyCode = echoEvent.keyCode;
	var cmd = epcalc_calculatorKeyMap[keyCode];
	// some fix ups because of shift keys etc..
	if (keyCode == 9) {
		if ( ! EP.isIE) {
			this.eDisplay.blur();
		}
		return;
	}
	if (keyCode == 32) {	// VK_SPACE
		// if its not the diplay let it go through to the keeper
		if (element != this.eDisplay) {
			return;
		}
	}
	if (echoEvent.shiftKey) {
		if (keyCode == 50) {	// Shift + VK_2
			cmd = 'sqrt';
		}
		if (keyCode == 53) {	// Shift + VK_5
			cmd = '%';
		}
		if (keyCode == 56) {	// Shift + VK_8
			cmd = '*';
		}
		if (keyCode == 61) {	// Shift + VK_EQUALS
			cmd = '+';
		}
	}
	if (echoEvent.ctrlKey) {
		if (keyCode == 80) {	// Ctrl + VK_P
			cmd = 'm+';
		}
		if (keyCode == 76) {	// Ctrl + VK_L
			cmd = 'mc';
		}
		if (keyCode == 82) {	// Ctrl + VK_R
			cmd = 'mr';
		}
		if (keyCode == 77) {	// Ctrl + VK_M
			cmd = 'ms';
		}
	}
	EP.Event.cancelEvent(echoEvent);
	return cmd;
};

//-------------------------------
EPCalculator.prototype.updateOperation = function() {
	if (this.eOperation) {
		var opText = (this.operator == null ? '&nbsp;' : this.operator); 
		this.eOperation.innerHTML = '<i>' + opText + '</i>';
	}
};

//-------------------------------
EPCalculator.prototype.updateMemory = function() {
	if (this.eMemory) {
		var opText = (this.memoryF == null ? '&nbsp;' : 'M'); 
		this.eMemory.innerHTML = '<b>' + opText + '</b>';
	}
};

//-------------------------------
EPCalculator.prototype.updateDisplay = function(numberNorS) {
	var numberS = '' + numberNorS;
	var decimalPlace = numberS.indexOf('.'); 
	if (decimalPlace == -1) {
		numberS += '.';
	} else if (decimalPlace < numberS.length-1) {
		var expPlace = numberS.indexOf('e');
		if (expPlace == -1) {
			var intBit = numberS.substr(0,decimalPlace);
			var fracBit = numberS.substr(decimalPlace+1,decimalPlace+1+this.decimalPlaces);
			// its fractional number, so make sure its not too long
			if (fracBit.length > this.decimalPlaces) {
				fracBit = fracBit.substr(0,this.decimalPlaces);
			}
			numberS = intBit + '.' + fracBit;
		}
	}
	this.eDisplay.value = numberS;
};

//-------------------------------
EPCalculator.prototype.saveState = function(cmd) {
	var value = this.eDisplay.value;
    EchoClientMessage.setPropertyValue(this.elementId, "text", ''+value);
	if (this.memoryF) {
	    EchoClientMessage.setPropertyValue(this.elementId, "memory", ''+this.memoryF);
	}

	if (cmd == "xfer" && this.serverNotify) {
		if (! EchoClientEngine.verifyInput(this.elementId)) {
	        return;
	    }	
		EchoClientMessage.setActionValue(this.elementId, "transfer");
		EchoServerTransaction.connect();
	}
};


//-------------------------------
//-------------------------------
EPCalculator.prototype.doCommand = function(cmd) {
	// special command modifiers
	if (cmd == 'c') {
		this.reset();
		return;
	}
	if (cmd == 'ce') {
		if (this.state == 'rhs' || this.state == 'postrhs') {
			this.rhsFrac = null;
			this.numberS = '';
		} else if (this.state == 'lhs' || this.state == 'postlhs') {
			this.lhsFrac = null;
			this.numberS = '';
		} else {
			this.reset();
		}
		this.updateDisplay(0);
		return;
	}
	if (cmd == 'bksp') {
		if (this.state == 'rhs' || this.state=='lhs' || this.state == 'postrhs' || this.state=='postlhs') {
			this.pressedBackSpace();
			return;
		}
	}
	
	// START
	if (this.state == 'start') {
		if (this.isNumber(cmd)) {
			this.state = 'lhs';
			this.pressedNumber(cmd);
		} else if (this.isMemory(cmd)) {
			this.state = this.pressedMemory(cmd);
		}
	// LHS
	} else if (this.state == 'lhs') {
		if (this.isNumber(cmd)) {
			this.pressedNumber(cmd);
		} else if (this.isModifier(cmd)) {
			this.state = this.pressedModifier(cmd);
		} else if (this.isMemory(cmd)) {
			this.state = this.pressedMemory(cmd);
		} else if (this.isOperator(cmd)) {
			this.transferNumberS();
			this.operator = cmd;
			this.state = 'operator';
		}

	// POST LHS
	} else if (this.state == 'postlhs') {
		this.state = 'lhs';
		if (this.isNumber(cmd)) {
			this.numberS = '';
			this.pressedNumber(cmd);
		} else if (this.isModifier(cmd)) {
			this.state = this.pressedModifier(cmd);
		} else if (this.isMemory(cmd)) {
			this.state = this.pressedMemory(cmd);
		} else if (this.isOperator(cmd)) {
			this.transferNumberS();
			this.operator = cmd;
			this.state = 'operator';
		}
	// RHS
	} else if (this.state == 'rhs') {
		if (this.isNumber(cmd)) {
			this.pressedNumber(cmd);
		} else if (this.isModifier(cmd)) {
			this.state = this.pressedModifier(cmd);
		} else if (this.isMemory(cmd)) {
			this.state = this.pressedMemory(cmd);
		} else if (cmd == '%') {
			this.state = this.pressedPercent(cmd);
		} else if (this.isOperator(cmd)) {
			// perform the last operation
			this.transferNumberS();
			this.state = this.pressedOperation(this.operator);
			this.operator = cmd;
			this.rhsFrac = null;
			this.numberS = this.lhsFrac.toString();
		} else if (cmd == '=') {
			this.transferNumberS();
			this.state = this.pressedOperation(this.operator);
			this.numberS = this.lhsFrac.toString();
			if (this.state != 'dividebyzero') {
				this.state = 'equals';
			}
		}

	// POST RHS
	} else if (this.state == 'postrhs') {
		this.state = 'rhs';
		if (this.isNumber(cmd)) {
			this.numberS = '';
			this.pressedNumber(cmd);
		} else if (this.isModifier(cmd)) {
			this.state = this.pressedModifier(cmd);
		} else if (this.isMemory(cmd)) {
			this.state = this.pressedMemory(cmd);
		} else if (cmd == '%') {
			this.state = this.pressedPercent(cmd);
		} else if (this.isOperator(cmd)) {
			// perform the last operation
			this.transferNumberS();
			this.state = this.pressedOperation(this.operator);
			this.operator = cmd;
			this.rhsFrac = null;
			this.numberS = this.lhsFrac.toString();
		} else if (cmd == '=') {
			this.transferNumberS();
			this.state = this.pressedOperation(this.operator);
			this.numberS = this.lhsFrac.toString();
			if (this.state != 'dividebyzero') {
				this.state = 'equals';
			}
		}
		
	// OPERATOR
	} else if (this.state == 'operator') {
		if (cmd == '=') {
			this.pressedOperation(this.operator);
			if (this.state != 'dividebyzero') {
				this.state = 'equals';
			}
		} else if (this.isModifier(cmd)) {
			this.state = this.pressedModifier(cmd);
		} else if (this.isMemory(cmd)) {
			this.state = this.pressedMemory(cmd);
		} else if (cmd == '%') {
			this.state = this.pressedPercent(cmd);
		} else if (this.isNumber(cmd)) {
			this.state = 'rhs';
			this.rhsFrac = null;
			this.numberS = '';
			this.pressedNumber(cmd);
		} else if (this.isOperator(cmd)) {
			this.operator = cmd;
			this.state = 'operator';
			this.rhsFrac = null;
		}

	// EQUALS
	} else if (this.state == 'equals') {
		if (cmd == '=') {
			this.pressedOperation(this.operator);
			this.numberS = this.lhsFrac.toString();
			if (this.state != 'dividebyzero') {
				this.state = 'equals';
			}
		} else if (this.isModifier(cmd)) {
			this.state = this.pressedModifier(cmd);
		} else if (this.isMemory(cmd)) {
			this.state = this.pressedMemory(cmd);
		} else if (cmd == '%') {
			this.state = this.pressedPercent(cmd);
		} else if (this.isOperator(cmd)) {
			this.operator = cmd;
			this.state = 'operator';
		} else if (this.isNumber(cmd)) {
			this.reset();
			this.state = 'lhs';
			this.pressedNumber(cmd);
		}
		
	// DIVIDE BY ZERO
	} else if (this.state == 'dividebyzero') {
		// we never move out of this state until they press CLEAR
	}
	
	this.updateOperation();
	this.updateMemory();
	this.debug();
};

//-------------------------------
//General calculator event handler
//-------------------------------
EPCalculator.prototype.eventHandler = function(echoEvent) {
	//debugger;
	if (! EchoClientEngine.verifyInput(this.elementId)) {
        return;
    }	
	// do we have a command associated with what was pressed
	var element = echoEvent.target;
	var cmd = element.id.split('|');
	if (echoEvent.type == 'click') {
		// put the focus in the display input area
		this.eDisplay.focus();
		if (cmd.length != 2) {
			return;
		}
	}
	if (cmd.length > 1) {
		cmd = cmd[1];
	}
	
	// handle key strokes by converting them into commands
	if (echoEvent) {
		if (echoEvent.type == 'keydown') {
			cmd = this.doKeyCommand(element,echoEvent);
			if (! cmd) {
				EP.debug(echoEvent.type + ' kc:' + echoEvent.keyCode + ' cmd:' + cmd);
				return;
			}
		}
		if (echoEvent.type == 'keypress') {
			//
			// we need to eat Mozilla keypress events
			if (! EP.isIE) {
				EP.Event.cancelEvent(echoEvent);
			}
			return;
		}
	}
	// okay what did they want to do
	this.doCommand(cmd);
	this.saveState(cmd);
};


/**
 * EPCalculator has a ServerMessage processor.
 */
EPCalculator.MessageProcessor = function() { };

EPCalculator.MessageProcessor.process = function(messagePartElement) {
	for (var i = 0; i < messagePartElement.childNodes.length; ++i) {
        if (messagePartElement.childNodes[i].nodeType == 1) {
            switch (messagePartElement.childNodes[i].tagName) {
	            case "init":
	                EPCalculator.MessageProcessor.processInit(messagePartElement.childNodes[i]);
	                break;
	            case "text":
	                EPCalculator.MessageProcessor.processTextChanged(messagePartElement.childNodes[i]);
	                break;
	            case "dispose":
	                EPCalculator.MessageProcessor.processDispose(messagePartElement.childNodes[i]);
	                break;
            }
        }
    }
};

EPCalculator.MessageProcessor.processDispose = function(disposeMessageElement) {
	//debugger;
	for (var item = disposeMessageElement.firstChild; item; item = item.nextSibling) {
		var elementId = item.getAttribute("eid");
		EP.ObjectMap.destroy(elementId);
    }
};

EPCalculator.MessageProcessor.textChanged = function(disposeMessageElement) {
	//debugger;
	for (var item = disposeMessageElement.firstChild; item; item = item.nextSibling) {
		var elementId = item.getAttribute("eid");
		var text = item.getAttribute("text");
		var calc = EP.ObjectMap.get(elementId);
		if (calc) {
			calc.reset(text);
		}
    }
};


//-------------------------------
EPCalculator.prototype.destroy = function() {
   	var calcE = document.getElementById(this.elementId);
    if (calcE) {
    	EP.Event.removeHandler("keydown", calcE);
    	EP.Event.removeHandler("keypress", calcE);
    	EP.Event.removeHandler("click", calcE);
	}
}


EPCalculator.MessageProcessor.processInit = function(initMessageElement) {
	//debugger;
	
	// key mapping setup is is a one time business
	epc_setupKeys();
	for (var item = initMessageElement.firstChild; item; item = item.nextSibling) {
        var elementId = item.getAttribute("eid");
		var text = item.getAttribute("text");
		var memory = item.getAttribute("memory");

		EP.ObjectMap.destroy(elementId);

		var calc = new EPCalculator(elementId,text,memory);
        if (item.getAttribute("enabled") == "false") {
            EchoDomPropertyStore.setPropertyValue(elementId, "EchoClientEngine.inputDisabled", true);
        }
		calc.serverNotify = item.getAttribute("serverNotify") == "true";
 
        var calcE = document.getElementById(elementId);
        EP.Event.addHandler("keydown", calcE, calc);
    	EP.Event.addHandler("keypress", calcE, calc);    	
    	EP.Event.addHandler("click", calcE, calc);

		calc.eDisplay = document.getElementById(elementId + "Display");
		calc.eOperation = document.getElementById(elementId + "Operation");
		calc.eMemory = document.getElementById(elementId + "Memory");

		if (text) {
			calc.updateDisplay(text);
		} else {
			calc.updateDisplay(0);
		}
		calc.updateOperation();
		calc.updateMemory();
    }
};
