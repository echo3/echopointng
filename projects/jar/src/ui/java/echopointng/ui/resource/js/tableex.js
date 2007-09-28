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
 * This file was made part of the EchoPointNG project on the 8th of June 2006
 * It was orginally developed by NextApp and hence..
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
  
//_________________
// Object EchoTable

/**
 * Static object/namespace for TableEx support.
 * This object/namespace should not be used externally.
 * <p>
 * Constructor to create new <code>EPTableEx</code> instance.
 * 
 * @param element the supported <code>TABLE</code> DOM element 
 */
EPTableEx = function(elementId) {
    
    this.multipleSelect = false;
    this.rolloverEnabled = false;
    this.rolloverStyle = null;
    this.selectionEnabled = false;
    this.selectionStyle = null;
    this.rowCount = 0;
    this.selectionState = null;
    this.headerVisible = false;
    this.footerVisible = false;
    this.lastSelectedIndex = -1;
    this.ignoreMetaKeys = false;
    
	this.elementId = elementId;
	EP.ObjectMap.put(elementId, this); 
};

/**
 * Static object/namespace for TableEx MessageProcessor 
 * implementation.
 */
EPTableEx.MessageProcessor = function() { };

/**
 * MessageProcessor process() implementation 
 * (invoked by ServerMessage processor).
 *
 * @param messagePartElement the <code>message-part</code> element to process.
 */
EPTableEx.MessageProcessor.process = function(messagePartElement) {
    for (var i = 0; i < messagePartElement.childNodes.length; ++i) {
        if (messagePartElement.childNodes[i].nodeType == 1) {
            switch (messagePartElement.childNodes[i].tagName) {
            case "init":
                EPTableEx.MessageProcessor.processInit(messagePartElement.childNodes[i]);
                break;
            case "dispose":
                EPTableEx.MessageProcessor.processDispose(messagePartElement.childNodes[i]);
                break;
            }
        }
    }
};

/**
 * Processes a <code>dispose</code> message to finalize the state of a
 * Table component that is being removed.
 *
 * @param disposeMessageElement the <code>dispose</code> element to process
 */
EPTableEx.MessageProcessor.processDispose = function(disposeMessageElement) {
    for (var item = disposeMessageElement.firstChild; item; item = item.nextSibling) {
        var elementId = item.getAttribute("eid");
		EP.ObjectMap.destroy(elementId);
    }
};

/**
 * Processes an <code>init</code> message to initialize the state of a 
 * TableEx component that is being added.
 *
 * @param initMessageElement the <code>init</code> element to process
 */
EPTableEx.MessageProcessor.processInit = function(initMessageElement) {
    var rolloverStyle = initMessageElement.getAttribute("rollover-style");
    var selectionStyle = initMessageElement.getAttribute("selection-style");

    for (var item = initMessageElement.firstChild; item; item = item.nextSibling) {
        var tableElementId = item.getAttribute("eid");
        
        var table = new EPTableEx(tableElementId);
        table.enabled = item.getAttribute("enabled") != "false";
        table.rowCount = parseInt(item.getAttribute("rowCount"),10);
        table.headerVisible = item.getAttribute("header-visible") == "true";
        table.footerVisible = item.getAttribute("footer-visible") == "true";
        table.rolloverEnabled = item.getAttribute("rollover-enabled") == "true";
        if (table.rolloverEnabled) {
            table.rolloverStyle = rolloverStyle;
        }
        
        table.selectionEnabled = item.getAttribute("selection-enabled") == "true";
        if (table.selectionEnabled) {
            table.selectionStyle = selectionStyle;
            table.multipleSelect = item.getAttribute("selection-mode") == "multiple";
            table.serverNotify = item.getAttribute("server-notify") == "true";
        }
        table.ignoreMetaKeys = item.getAttribute("ignoreMetaKeys") == "true";
        
        // perform xhtml fragment fixups
		var xhtmlfragments = initMessageElement.getElementsByTagName('xhtmlfragment');
        for(var index=0; index< xhtmlfragments.length; index++) {
        	var cellE = document.getElementById(xhtmlfragments[index].getAttribute('cellid'));
        	if (cellE) {
	       		var cdata = xhtmlfragments[index].firstChild;
	       		if (cdata && cdata.data) {
		       		cellE.innerHTML = cdata.data;
	       		}
        	}
        }
        table.scrollable = item.getAttribute("scrollable") == "true";
        table.resizeable = item.getAttribute("resizeable") == "true";
        table.resizeDragBarUsed = item.getAttribute("resizeDragBarUsed") == "true";
        table.resizeGrowsTable = item.getAttribute("resizeGrowsTable") == "true";

    	table.heightStretched = EP.DOM.getBooleanAttr(item,"heightStretched",false);
	    if (table.heightStretched) {
			table.minHeight = EP.DOM.getIntAttr(item,"minimumStretchedHeight",null);
	    	table.maxHeight = EP.DOM.getIntAttr(item,"maximumStretchedHeight",null);
	    }
	    
	    table.actionCausingCells = {};
	    var elems = item.getElementsByTagName('actionCausingCell');
	    for(var i = 0; i < elems.length; i++) {
	    	var e = elems[i];
	    	var row = e.getAttribute('row');
	    	var cells = e.getAttribute('cells');
	    	table.actionCausingCells[row] = cells;	
	    }
	    table.selectionCausingCells = {};
	    var elems = item.getElementsByTagName('selectionCausingCell');
	    for(var i = 0; i < elems.length; i++) {
	    	var e = elems[i];
	    	var row = e.getAttribute('row');
	    	var cells = e.getAttribute('cells');
	    	table.selectionCausingCells[row] = cells;	
	    }

        // init the table object
        table.init();
        
        var rowElements = item.getElementsByTagName("row");
        for (var rowIndex = 0; rowIndex < rowElements.length; ++rowIndex) {
            var tableRowIndex = parseInt(rowElements[rowIndex].getAttribute("index"));
            table.setSelected(tableRowIndex, true);
            //
            // should we record this into the last selection index
            if (table.multipleSelect && ! this.ignoreMetaKeys) {
            	table.lastSelectedIndex = tableRowIndex;
            }
        }
    }
};

/**
 * Deselects all selected rows in a Table.
 */
EPTableEx.prototype.clearSelected = function() {
    for (var i = 0; i < this.rowCount; ++i) {
        if (this.isSelected(i)) {
            this.setSelected(i, false);
        }
    }
};

/**
 * Disposes of an <code>EPTableEx</code> instance, de-registering 
 * listeners and cleaning up resources.
 */
EPTableEx.prototype.destroy = function() {
	var offset = 0;
	if (this.scrollable == false && this.headerVisible) {
		offset = 1;
    }
    if (this.rolloverEnabled || this.selectionEnabled) {
         for (var rowIndex = 0; rowIndex < this.rowCount; ++rowIndex) {
            var trElement = this.tableE.rows[rowIndex + offset];
            if (this.rolloverEnabled) {
 				EP.Event.removeHandler('mouseout',trElement);
			 	EP.Event.removeHandler('mouseover',trElement);
            }
            if (this.selectionEnabled) {
			 	EP.Event.removeHandler('click',trElement);
            }
        }
    }
    if (this.scrollable) {
    	if (this.resizeable) {
    		if (this.headerVisible) {
			 	EP.Event.removeHandler('mousedown',this.headerDivE);
			 	EP.Event.removeHandler('mousemove',this.headerDivE);
    		}
    	}
	 	EP.Event.removeHandler('scroll',this.contentDivE);
	 	EP.Event.removeHandler('resize',this.contentDivE);
	 	
	    if (EP.isGecko) {
	  		EP.DocumentEvent.removeHandler('resize', 'EPTableEx');
	    }
	 	
    }
    this.tableE = null;
};

/**
 * Redraws a row in the appropriate style (i.e., selected or deselected).
 *
 * @param rowIndex the index of the row to redraw
 */
EPTableEx.prototype.drawRowStyle = function(rowIndex) {
    var selected = this.isSelected(rowIndex);
    var trElement = this.getRowElement(rowIndex);
    
    for (var i = 0; i < trElement.cells.length; ++i) {
        if (selected) {
            EchoCssUtil.restoreOriginalStyle(trElement.cells[i]);
            EchoCssUtil.applyTemporaryStyle(trElement.cells[i], this.selectionStyle);
        } else {
            EchoCssUtil.restoreOriginalStyle(trElement.cells[i]);
        }
    }
};

/**
 * Returns the <code>TR</code> element associated with a specific
 * row index.
 * 
 * @param rowIndex the row index
 * @return the relevant <code>TR</code> element
 */
EPTableEx.prototype.getRowElement = function(rowIndex) {
    if (this.headerVisible && this.scrollable == false) {
        if (rowIndex == -1) {
            return this.tableE.rows[0];
        } else if (rowIndex >= 0 && rowIndex < this.rowCount) {
            return this.tableE.rows[rowIndex + 1];
        }
    } else {
        if (rowIndex >= 0 && rowIndex < this.rowCount) {
            return this.tableE.rows[rowIndex];
        }
    }
    return null;
};

/**
 * Determines the index of a table row based on a 
 * <code>TR</code> element.  This method is used for
 * processing events.
 * 
 * @param trElement the <code>TR</code> element to evaluate
 * @return the row index
 */
EPTableEx.prototype.getRowIndex = function(trElement) {
    var stringIndex = trElement.id.lastIndexOf("_tr_") + 4;
    var rowIndex = trElement.id.substring(stringIndex);
    if (rowIndex == "header" || rowIndex == "emptyrow") {
        return -1;
    } else if (rowIndex == "footer") {
        return -2;
    } else {
        return parseInt(rowIndex);
    }
};

/**
 * Initializes the state of an <code>EPTableEx</code> instance,
 * registering event handlers and binding it to it target
 * <code>TABLE</code> DOM element.
 */
EPTableEx.prototype.init = function() {
	// who are acting as - a scrollable or non scrollable table
    if (this.scrollable) {
    	this.initScrollable(this.elementId);
    	this.tableE = this.contentTableE;
    } else {
    	this.tableE = document.getElementById(this.elementId);
    }
	var offset = 0;
	if (this.scrollable == false && this.headerVisible) {
		offset = 1;
    }
    this.selectionState = new Array();
    if (this.rolloverEnabled || this.selectionEnabled) {
        var mouseEnterLeaveSupport = EchoClientProperties.get("proprietaryEventMouseEnterLeaveSupported");
        for (var rowIndex = 0; rowIndex < this.rowCount; ++rowIndex) {
            var trElement = this.tableE.rows[rowIndex + offset];
            if (this.rolloverEnabled) {
            	EP.Event.addHandler('mouseout',trElement,this);
            	EP.Event.addHandler('mouseover',trElement,this);
            }
            if (this.selectionEnabled) {
            	EP.Event.addHandler('click',trElement,this);
            }
        }
    }
};

/**
 * Determines if a row is selected.
 * 
 * @param index the index of the row to evaluate
 * @return true if the row is selected
 */
EPTableEx.prototype.isSelected = function(index) {
    if (this.selectionState.length <= index) {
        return false;
    } else {
        return this.selectionState[index];
    }
};

/**
 * Returns the top level TR for that has been the ultimate cause of an Table event.
 * This will be the TR that is in a TBODY that is contained within this TABLE element.
 */
EPTableEx.prototype.findRowForEvent = function(echoEvent) {
	var trElement = EP.Event.findIdentifiedElement(echoEvent);
	while (trElement) {
		if (trElement.tagName.toLowerCase() == 'tr') {
			if (trElement.parentNode.parentNode == this.tableE) {
				return trElement;
			}
		}
		trElement = trElement.parentNode;
	}
	return null;
}

/**
 * Returns the top level cell TD for that has been the ultimate cause of an Table event.
 * This will be the TD that is in a TR/ TBODY that is contained within this TABLE element.
 */
EPTableEx.prototype.findCellForEvent = function(echoEvent) {
	var tdElement = EP.Event.findIdentifiedElement(echoEvent);
	while (tdElement) {
		if (tdElement.tagName.toLowerCase() == 'td') {
			if (tdElement.parentNode.parentNode.parentNode == this.tableE) {
				return tdElement;
			}
		}
		tdElement = tdElement.parentNode;
	}
	return null;
}


/**
 * Processes a row selection (click) event.
 *
 * @param echoEvent the event, preprocessed by the 
 *        <code>EchoEventProcessor</code>
 */
EPTableEx.prototype.processClick = function(echoEvent) {
    if (!this.enabled || !EchoClientEngine.verifyInput(this.tableE)) {
        return;
    }
    if (!this.selectionEnabled) {
        return;
    }
    var trElement = this.findRowForEvent(echoEvent);
    var rowIndex = this.getRowIndex(trElement);
    if (rowIndex == -1) {
        return;
    }
    var tdElement = this.findCellForEvent(echoEvent);
    var colIndex = parseInt(tdElement.getAttribute('c'),10);
    var colKey = 'c:' + colIndex + ';';
    var actionCells = this.actionCausingCells[rowIndex];
    var selectionCells = this.selectionCausingCells[rowIndex];
    //
    // the list of cells per row is the list of cells that DONT cause an action/selection
    // so if we have none, the its OK and if we have some then we need to NOT find it
    // in the list.
    //
    var isActionCausing = (actionCells == null ? true : actionCells.indexOf(colKey) == -1);
    var isSelectionCausing = (selectionCells == null ? true : selectionCells.indexOf(colKey) == -1);
     
    if (isSelectionCausing) {
	    //
	    // Should we do this.  What about a TreeTable that needs the click?
	    EchoDomUtil.preventEventDefault(echoEvent);
	    
		if (document.selection && document.selection.empty) {
	        document.selection.empty();
	    }    
	
	    if (! this.multipleSelect) {
	        this.clearSelected();
	    } else {
	    	//
	    	// IF we are using MetaKeys and they have not pressed a MetaKey then clear the selection
	    	//
	    	if (! this.ignoreMetaKeys) {
	    		if (! (echoEvent.shiftKey || echoEvent.ctrlKey || echoEvent.metaKey || echoEvent.altKey)) {
	  				this.clearSelected(); 
	    		}
	    	}
	    }
	    //
	    // IF we are multi mode and we are using the meta keys and they pressed SHIFT and there was a previous
	    // last selection, THEN we can do a "shift" style selection, OTHERWISE is a simple
	    // "single" row style selection.
	    //
	    if (this.multipleSelect && (! this.ignoreMetaKeys) && echoEvent.shiftKey && this.lastSelectedIndex != -1) {
	        if (this.lastSelectedIndex < rowIndex) {
	            startIndex = this.lastSelectedIndex;
	            endIndex = rowIndex;
	        } else {
	            startIndex = rowIndex;
	            endIndex = this.lastSelectedIndex;
	        }
	        for (var i = startIndex; i <= endIndex; ++i) {
	            this.setSelected(i, true);
	        }
	    } else {
	        var rowIsSelected = this.isSelected(rowIndex);
	        this.setSelected(rowIndex, ! rowIsSelected);
	        this.lastSelectedIndex = rowIndex;
	    }
	    
	    // Update ClientMessage.
	    this.updateSelectionState();
    }
    
    
    // Notify server if required.
    if (this.serverNotify && isActionCausing) {
	    var metaKeys = 0;
	    if (echoEvent.altKey) {
	    	metaKeys |= 1;
	    }
	    if (echoEvent.shiftKey) {
	    	metaKeys |= 2;
	    }
	    if (echoEvent.ctrlKey) {
	    	metaKeys |= 4;
	    }
	    if (echoEvent.metaKey) {
	    	metaKeys |= 8;
	    }
    	var actionInfo = 'r:' + rowIndex + ';c:'+ colIndex +';' + 'mk:' + metaKeys + ';';
        EchoClientMessage.setActionValue(this.elementId, "action", actionInfo);
        EchoServerTransaction.connect();
    }
};


/**
 * Sets the selection state of a table row.
 *
 * @param rowIndex the index of the row
 * @param newValue the new selection state (a boolean value)
 */
EPTableEx.prototype.setSelected = function(rowIndex, newValue) {
    this.selectionState[rowIndex] = newValue;

    // Redraw.
    this.drawRowStyle(rowIndex);
};

/**
 * Updates the selection state in the outgoing <code>ClientMessage</code>.
 * If any server-side <code>ActionListener</code>s are registered, an action
 * will be set in the ClientMessage and a client-server connection initiated.
 */
EPTableEx.prototype.updateSelectionState = function() {
    if (this.selectionEnabled) {
		var propertyElement = EchoClientMessage.createPropertyElement(this.elementId, "selection");
	    // remove previous values
	    while(propertyElement.hasChildNodes()){
	        propertyElement.removeChild(propertyElement.firstChild);
	    }
	    for (var i = 0; i < this.rowCount; ++i) {
	        if (this.isSelected(i)) {
	            var rowElement = EchoClientMessage.messageDocument.createElement("row");
	            rowElement.setAttribute("index", i);
	            propertyElement.appendChild(rowElement);
	        }
	    }
    }
    EchoDebugManager.updateClientMessage();
};
/**
 * Processes a row mouse over event.
 *
 * @param echoEvent the event, preprocessed by the 
 *        <code>EchoEventProcessor</code>
 */
EPTableEx.prototype.processRolloverEnter = function(echoEvent) {
    if (!this.enabled || !EchoClientEngine.verifyInput(this.tableE)) {
        return;
    }

    var trElement = this.findRowForEvent(echoEvent);
    var rowIndex = this.getRowIndex(trElement);
    
    if (rowIndex == -1) {
        return;
    }
    
    if (this.rolloverStyle) {
        for (var i = 0; i < trElement.cells.length; ++i) {
            EchoCssUtil.applyTemporaryStyle(trElement.cells[i], this.rolloverStyle);
        }
    }
};

/**
 * Processes a row mouse out event.
 *
 * @param echoEvent the event, preprocessed by the 
 *        <code>EchoEventProcessor</code>
 */
EPTableEx.prototype.processRolloverExit = function(echoEvent) {
    if (!this.enabled || !EchoClientEngine.verifyInput(this.tableE)) {
        return;
    }

    var trElement = this.findRowForEvent(echoEvent);
    var rowIndex = this.getRowIndex(trElement);

    if (rowIndex == -1) {
        return;
    }
    this.drawRowStyle(rowIndex);
};


/**
 * The common EPNG eventHandler for TableEx.  Relates an event back to its
 * JS object instance.
 *
 * @param echoEvent the event, preprocessed by the EP.js event handling
 */
EPTableEx.prototype.eventHandler = function(echoEvent) {
	if (echoEvent.type == "click") {
		this.processClick(echoEvent);
	}
	if (echoEvent.type == "mouseover") {
		this.processRolloverEnter(echoEvent);
	}
	if (echoEvent.type == "mouseout") {
		this.processRolloverExit(echoEvent);
	}
	
	if (echoEvent.type == 'mousedown') {
		this.onMouseDown(echoEvent);	
	}
	if (echoEvent.type == 'mousemove') {
		this.onMouseMove(echoEvent);	
	}
	if (echoEvent.type == 'scroll') {
		this.onScroll(echoEvent);	
	}
	if (echoEvent.type == 'resize') {
		this.onResize(echoEvent);	
	}
}

/*=====================================================================*/

/**
 * Global document event handler
 */
EPTableEx.documentEventHandler = function(echoEvent) {
	if (echoEvent.type == 'resize') {
		// tell every EpTableEx that the document has re-sized
		for (x in EP.ObjectMap.objectMap) {
			var obj = EP.ObjectMap.objectMap[x];
			if (obj.componentType && obj.componentType == 'EPTableEx') {
				obj.onResize(echoEvent);
			}
		}
		
	}
};
/**
 * Internal use only - used for internal debugging purposes
 */
EPTableEx.getReportE = function() {
	var reportE = EPTableEx.reportE;
  	if (! reportE) {
		reportE = document.createElement('pre');
	 	reportE.style.position = 'absolute';
	 	reportE.style.left = '450px'; reportE.style.top = '70px';
	 	reportE.style.backgroundColor = '#F0E68C'; 
	 	reportE.style.zIndex = '1000';
	 	reportE.style.fontFamily = 'monospace';
	 	reportE.style.fontSize = '8pt';
	 	EPTableEx.reportE = reportE;
	 	document.body.insertBefore(reportE,document.body.firstChild);
  	} 	
 	return reportE;
 };

/**
 * Internal use only - used for internal debugging purposes
 */
EPTableEx.report = function(str) {
  	var reportE = EPTableEx.getReportE();
   	reportE.innerHTML = reportE.innerHTML + '<br/>' + str;
};

/**
 * Internal use only - used for internal debugging purposes
 */
EPTableEx.report_clear = function() {
  	var reportE = EPTableEx.getReportE();
   	reportE.innerHTML = '';
};

/**
 * Internal use only - used for internal debugging purposes
 */
EPTableEx.prototype.reportOnTable = function() {
	return;
	EPTableEx.report_clear();
	var report = EPTableEx.report;
	if (this.contentCells) {
		var totalcolgroupWidth = 0;
		var totalcontentWidth = 0;
		for (var i = 0; i < this.contentCells.length; i++) {
			var cell  = this.contentCells[i];
			EPTableEx.report('content  cell['+ i + ']  ow =' + cell.offsetWidth + ' sw=' + cell.style.width);
			totalcontentWidth += cell.offsetWidth;
			
			if (this.headerCells) {
				cell = this.headerCells[i];
				report('header   cell['+ i + ']  ow =' + cell.offsetWidth + ' sw=' + cell.style.width);
			}		
			cell = this.getColGroupCell(i);
			if (cell) {
				totalcolgroupWidth += this.getWidth(cell);
				report('colgroup cell['+ i + ']  ow =' + cell.offsetWidth + ' sw=' + cell.style.width);
			}
		}
		
		report('totalcontentWidth = ' + totalcontentWidth);
		report('totalcolgroupWidth = ' + totalcolgroupWidth);
		report('---');
	}
	
	if (this.headerVisible) {
		report('headerTableE');
		report('   ow  =' + this.headerTableE.offsetWidth + ' sw = ' + this.headerTableE.style.width);
		report('   sx  =' + this.headerTableE.scrollLeft);
		
		report('headerDivE');
		report('   ow  =' + this.headerDivE.offsetWidth + ' oh=' + this.headerDivE.offsetHeight);
		report('   sw = ' + this.headerDivE.style.width + ' sh=' + this.headerDivE.style.height);
		report('   sx  =' + this.headerDivE.scrollLeft);
	
		report('headerScrollerDivE');
		report('   ow  =' + this.headerScrollerDivE.offsetWidth + ' sw = ' + this.headerScrollerDivE.style.width);
		report('   sx  =' + this.headerScrollerDivE.scrollLeft);
		report('   left=' + this.headerScrollerDivE.style.left);
		report('---');
	}
	
	report('tableE');
	report('   ow  =' + this.contentTableE.offsetWidth + ' sw = ' + this.contentTableE.style.width);
	report('   sx  =' + this.contentTableE.scrollLeft);

	report('contentDivE');
	report('   ow  =' + this.contentDivE.offsetWidth + ' oh=' + this.contentDivE.offsetHeight);
	report('   sw = ' + this.contentDivE.style.width + ' sh=' + this.contentDivE.style.height);
	report('   sx  =' + this.contentDivE.scrollLeft);

	report('outerDivE');
	report('   ow  =' + this.outerDivE.offsetWidth + ' oh=' + this.outerDivE.offsetHeight);
	report('   sw = ' + this.outerDivE.style.width + ' sh=' + this.outerDivE.style.height);

	report('elementId = ' + this.elementId);
};


/**
 * Gets the COLGROUP cell for the given cellIndex.
 */
EPTableEx.prototype.getColGroupCell = function(cellIndex) {
	return this._getCellImpl(this.colgroupCells,cellIndex);
};

/**
 * Returns the content cell for a given cell index
 */
EPTableEx.prototype.getContentCell = function(cellIndex) {
	return this._getCellImpl(this.contentCells,cellIndex);
};

/**
 * Returns the header cell for a given cell index
 */
EPTableEx.prototype.getHeaderCell = function(cellIndex) {
	return this._getCellImpl(this.headerCells,cellIndex);
};

/**
 * Returns the footer cell for a given cell index
 */
EPTableEx.prototype.getFooterCell = function(cellIndex) {
	return this._getCellImpl(this.footerCells,cellIndex);
};

/**
 * Private implementation function
 */
EPTableEx.prototype._getCellImpl = function(cellArray, cellIndex) {
	if (cellArray == null || cellIndex > cellArray.length-1)
		return null;
	return cellArray[cellIndex];
};

/**
 * Returns the Nth child of htmlE that has the specified tag name
 * such as 'DIV'.
 */
EPTableEx.prototype.getNthChild = function(htmlE, index, tagName) {
	var foundCount = 0;
	for(var index=0; index<htmlE.childNodes.length; index++) {
		var childE = htmlE.childNodes[index];
		if (childE.tagName && childE.tagName.toUpperCase() == tagName.toUpperCase()) {
			if (foundCount == index) {
				return childE;
			}
			foundCount++;
		}
	}
	return null;
};

EPTableEx.prototype.getBorderLeftWidth = function(htmlE) {
	var width = 0;
	var style = EP.StyleSheet.getComputedStyle(htmlE);
	if (style.borderLeftWidth.indexOf('px') != -1) {
		width += parseInt(style.borderLeftWidth);
	}
	return width;
};

EPTableEx.prototype.getBorderRightWidth = function(htmlE) {
	var width = 0;
	var style = EP.StyleSheet.getComputedStyle(htmlE);
	if (style.borderRightWidth.indexOf('px') != -1) {
		width += parseInt(style.borderRightWidth);
	}
	return width;
};

/**
 * Returns the combined width of the left and right border of the htmlE
 */
EPTableEx.prototype.getBorderWidths = function(htmlE) {
	var width = 0;
	width += this.getBorderLeftWidth(htmlE);
	width += this.getBorderRightWidth(htmlE);
	return width;
};


/**
 * Returns true if the element has a horizontal scrollbar showing
 */
EPTableEx.prototype.hasHorzScrollBar = function(htmlE) {
	var widthDiff = htmlE.offsetWidth - htmlE.clientWidth;
	var bNoScrollbar = (widthDiff == 0);
	return ! bNoScrollbar;
};

/**
 * Returns true if the element has a horizontal scrollbar showing
 */
EPTableEx.prototype.hasVertScrollBar = function(htmlE) {
	var widthDiff = htmlE.offsetHeight - htmlE.clientHeight;
	var bNoScrollbar = (widthDiff == 0);
	return ! bNoScrollbar;
};

/**
 * Returns the width of a element as an integer value
 */
EPTableEx.prototype.getWidth = function(cellElement) {
	var width = 0;
	if (cellElement) {
		if (cellElement.tagName && cellElement.tagName.toUpperCase() == 'COL') {
			width = cellElement.getAttribute('width');
		} else {
			if (cellElement.style.width) {
				width = cellElement.style.width;
			} else {
				width = EP.getWidth(cellElement);
			}
		}
	}
	return parseInt(width,10);
};

/**
 * Sets the width a element to a pixel value.  Returns its actual width
 * after the set.
 */
EPTableEx.prototype.setWidth = function(cellElement, width) {
	if (cellElement) {
		width = (''+width).indexOf('px') == -1 ? width+'px' : width;
		if (cellElement.tagName.toUpperCase() == 'COL') {
			cellElement.setAttribute('width',width);
		} else {
			cellElement.style.width = width;
		}
		var actualWidth =  this.getWidth(cellElement);
		return actualWidth;
	}
};


/**
 * Returns how far left (in absolute co-ords) the element is
 * 
 */
EPTableEx.prototype.getClientX = function(cellE) {
	var x = 0;
	if (cellE) {
		for (var el = cellE; el; el = el.offsetParent) {
			x += el.offsetLeft;
			x -= el.scrollLeft;
		}
	}
	return x;	
};

/**
 * Returns how far top (in absolute co-ords) the element is
 * 
 */
EPTableEx.prototype.getClientY = function(cellE) {
	var y = 0;
	if (cellE) {
		for (var el = cellE; el; el = el.offsetParent) {
			y += el.offsetTop;
			y -= el.scrollTop;
		}
	}
	return y;	
};


/**
 * This is called to "hit test" the mouse movements over the header
 * div.  If the mouse is over the "resize" area of a cell then the index
 * of that cell is returned, otherwise -1 is returned (no resize).
 * 
 * The resize area of a cell is the x pixels from the right edge of the
 * cell and n pixels of the left the next cell
 * 
 * |---------------^----|----^-------------^----|----^----------
 * |   cell 1      ^    |    ^     cell2   ^    |    ^     cell3...
 * |---------------^----|----^-------------^----|----^----------
 *                 resize area             resize area
 * 				    
 *					
 */
EPTableEx.prototype.hitTestHeaderCell = function(echoEvent) {
	var x = echoEvent.clientX;
	var cellIndex = -1;
	var leftEdgeX = 0;
	var testWidth = 0;
	var rightEdgeX = 0;
	if (this.headerCells) {
		var length = this.headerCells.length;
		for (var i = 0; i < length; i++) {
			var headerCell = this.headerCells[i];
			leftEdgeX = this.getClientX(headerCell);
			testWidth = EP.getWidth(headerCell);
			rightEdgeX = leftEdgeX+testWidth;
			//
			// out allowable region is the cell plus some area to the right
			if (x > leftEdgeX && x < (rightEdgeX+5)) {
				// its inside this cell.  But it must be in the 
				// the last 5 pixels of the cell to be resizing
				if (x >= (rightEdgeX-5)) {
					cellIndex = i;
					break;
				}
				// ok it might be in the first 5 pixels of the cell in which
				// case the "resize operation" is really for the previous cell.  One
				// exception is the first cell which has no previous cell.
				if (x <= leftEdgeX+5 && i > 0) {
					cellIndex = i-1;
					break;
				}
				break;
			}
		}
		// we dont allow a hit on the last cell index. At present we dont allow them to 
		// resize the end of the last cell.  Maybe one day we will
		if (cellIndex == this.headerCells.length) {
			cellIndex = -1;
		}
	}
	return cellIndex;
};


/**
 * Updates the column width state in the outgoing <code>ClientMessage</code>.
 */
EPTableEx.prototype.updateColumnWidthState = function() {
    if (this.resizeable) {
		var propertyElement = EchoClientMessage.createPropertyElement(this.elementId, "columnWidths");
	    // remove previous values
	    while(propertyElement.hasChildNodes()){
	        propertyElement.removeChild(propertyElement.firstChild);
	    }
	    var cellArray = this.contentCells;
	    if (cellArray == null) {
	    	cellArray = this.headerCells;
	    }
	    if (cellArray) {
		    for (var i = 0; i < cellArray.length; ++i) {
		        var width = this.getWidth(cellArray[i]);
		        var widthElement = EchoClientMessage.messageDocument.createElement("columnWidth");
		        widthElement.setAttribute("width", width);
	            propertyElement.appendChild(widthElement);
		    }
	    }
    }
    EchoDebugManager.updateClientMessage();
};

EPTableEx.prototype.synchronizeTableColumnsAsych = function() {
	var that = this;
	window.setTimeout(function() {
		that.synchronizeTableColumns();
	}, 100);
};

/**
 * Called to synchronise the width of the header cells 
 * to the width of the content cells.
 */
EPTableEx.prototype.synchronizeTableColumns = function() {
	if (this.rowCount > 0) {
		if (this.headerVisible) {
			this.headerDivE.style.width = this.contentDivE.style.width;
		}
		if (this.footerVisible) {
			this.footerDivE.style.width = this.contentDivE.style.width;
		}
		//
		// a little border appearance issue.  We get double right borders if the table is 1 border width
		// less than the content area so we turn them off when its butted up agianst the content area
		// and we turned it back on when its not against it
		var contentBorderWidth = this.getBorderWidths(this.contentDivE);
		var rightBorderWidth = this.getBorderRightWidth(this.contentDivE);
		var doBorderAdjusting = true;
		if (this.headerVisible && EP.isGecko && this.contentOriginalWidth.indexOf('%') > -1) {
			this.headerDivE.style.width = (this.contentDivE.offsetWidth-contentBorderWidth) + 'px';
		}
		if (this.footerVisible && EP.isGecko && this.contentOriginalWidth.indexOf('%') > -1) {
			this.footerDivE.style.width = (this.contentDivE.offsetWidth-contentBorderWidth) + 'px';
		}
		if (doBorderAdjusting) {
			if (this.contentTableE.offsetWidth+contentBorderWidth == this.contentDivE.offsetWidth) {
		    	this.contentTableE.style.borderRightWidth = '0px';
		    	if (this.headerVisible) {
			    	this.headerTableE.style.borderRightWidth = '0px';
		    	}
		    	if (this.footerVisible) {
			    	this.footerTableE.style.borderRightWidth = '0px';
		    	}
			} else {
		    	this.contentTableE.style.borderRightWidth = this.contentDivE.style.borderRightWidth;
		    	if (this.headerVisible) {
			    	this.headerTableE.style.borderRightWidth = this.headerDivE.style.borderRightWidth;
		    	}
		    	if (this.footerVisible) {
			    	this.footerTableE.style.borderRightWidth = this.footerDivE.style.borderRightWidth;
		    	}
			}
		}
		//	
	    // make the header table the same size
	    var width = this.contentTableE.offsetWidth;
	  	if (this.headerVisible) {
		    this.setWidth(this.headerTableE,width);
	  	}
	  	if (this.footerVisible) {
		    this.setWidth(this.footerTableE,width);
	  	}
	    
		if (this.contentCells) {
			for (var i = 0; i < this.contentCells.length; ++i) {
				if (this.headerVisible) {
					this.synchroniseHeaderCell(i);
				}
				if (this.footerVisible) {
					this.synchroniseFooterCell(i);
				}
			}
	    }
	}
	// update our client message
	this.updateColumnWidthState();
};


/**
 * This is called to set the header cell to to be the same width
 * as the content cell at the specified cellIndex.  This can take into
 * account browsers differences in borders and the like around cells.
 */
EPTableEx.prototype.synchroniseHeaderCell = function(cellIndex) {
	var contentCell = this.getContentCell(cellIndex);
	var headerCell = this.getHeaderCell(cellIndex);
	var width = parseInt(this.getWidth(contentCell),10);
	//
	// if some browsers differ in cell border's setting etc.. so take this into account here
	this.setWidth(headerCell,width);
};

EPTableEx.prototype.synchroniseFooterCell = function(cellIndex) {
	var contentCell = this.getContentCell(cellIndex);
	var footerCell = this.getFooterCell(cellIndex);
	var width = parseInt(this.getWidth(contentCell),10);
	//
	// if some browsers differ in cell border's setting etc.. so take this into account here
	this.setWidth(footerCell,width);
};

/**
 * Sets both the cell and colgroup cell to a new width and returns
 * the width of the new cell.
 */
EPTableEx.prototype.adjustCellWidths = function(cellIndex, newWidth) {
	var colgroupCell = this.getColGroupCell(cellIndex);
	if (colgroupCell) {
		this.setWidth(colgroupCell,newWidth);
	}
	var cellE;
	if (this.rowCount > 0) {
		cellE = this.getContentCell(cellIndex);
	} else {
		cellE = this.getHeaderCell(cellIndex);
	}
	var actualWidth = this.setWidth(cellE,newWidth);
	return actualWidth;
};

EPTableEx.MIN_CELL_WIDTH = 5;

/**
 * Resizes the column cells for the current drag operation.
 */
EPTableEx.prototype.resizeDraggedColumn = function(echoEvent,dragContext, deltaX, forceMinWidth) {
	this.hasBeenResized = true;

	var startWidth = dragContext.startWidth;
	var startTableWidth = dragContext.startTableWidth;
	var cellIndex = dragContext.cellIndex;
	var newWidth = 0;
	var newTableWidth = 0;
	
	if (deltaX >= 0) {
		// dragging right
		newWidth = startWidth + deltaX;
		newTableWidth = startTableWidth + deltaX;
	} else {
		// dragging left
		deltaX = Math.abs(deltaX);
		newWidth = startWidth - deltaX;
		newTableWidth = startTableWidth - deltaX;
		if (newWidth <= EPTableEx.MIN_CELL_WIDTH) {
			newWidth = -1;
			if (forceMinWidth) {
				newWidth = EPTableEx.MIN_CELL_WIDTH;
			}
		}
	}
	var widthDifference = newWidth - startWidth;
	// set the new widths
	if (newWidth != -1) {
		var actualWidth = newWidth;
		try {
			actualWidth = this.adjustCellWidths(cellIndex,newWidth);
			
			// in the fixed column width scenario, we make one smaller/bigger
			// and make its right hand side neighbour bigger/smaller
			if (this.resizeGrowsTable == false) {
				var neighbourCellE = dragContext.dragNeighbor;
				var neighbourWidth = dragContext.startNeighborWidth;
				if (widthDifference < 0) {
					neighbourWidth += Math.abs(widthDifference);
				} else {
					neighbourWidth -= widthDifference;
				}
				this.adjustCellWidths(cellIndex+1,neighbourWidth);
			}
		} catch(ex) {
			EP.debug('EPTableEx - exception thrown during cell width set' + newWidth);
		}	
		if (this.resizeGrowsTable) {
			// adjust the table with by the new amount if the cell width actually stuck!
			if (actualWidth == newWidth) {
				try {
					// set the table Width	
					var targetTableE = this.contentTableE;
					if (this.rowCount <= 0) {
						targetTableE = this.headerTableE;
					}
					this.setWidth(targetTableE,newTableWidth);
				} catch(ex) {
					EP.debug('EPTableEx - exception thrown during table width set' + newWidth);
				}
			}
		}	
	}
	// adjust the header cells to match
	//this.synchroniseHeaderCell(cellIndex);
	this.synchronizeTableColumns();
	this.synchroniseScrollPositions();
	this.reportOnTable();
}

/**
 * Returns the x co--ord of the mouse drag operation within allowable bounds
 * for the current mode of operation.
 */
EPTableEx.prototype.getValidDragMovement = function(echoEvent,dragContext) {
	var targetTableE = this.contentTableE;
	var cellE = this.getContentCell(dragContext.cellIndex);
	if (this.rowCount <= 0) {
		cellE = this.getHeaderCell(dragContext.cellIndex);
		targetTableE = this.headerTableE;
	}
	var fudge = 0;
	var leftX = this.getClientX(cellE);
	if (echoEvent.clientX < (leftX+fudge+EPTableEx.MIN_CELL_WIDTH)) {
		return leftX+fudge+EPTableEx.MIN_CELL_WIDTH;
	}
	if (this.resizeGrowsTable == false) {
		var maxRightX = 0;
		var neighbourCell = null;
		if (this.rowCount > 0) {
			neighbourCell = this.getContentCell(dragContext.cellIndex+1);
		} else {
			neighbourCell = this.getHeaderCell(dragContext.cellIndex+1);
		}
		if (neighbourCell == null) {
			// must be the last cell and hence it doesnt have a right hand side neighbour cell
			// but the boundary now is the table edge
			maxRightX = this.getClientX(targetTableE) + this.getWidth(targetTableE);
		} else {
			maxRightX = this.getClientX(neighbourCell) + this.getWidth(neighbourCell);
		}
		if (echoEvent.clientX > (maxRightX-fudge-EPTableEx.MIN_CELL_WIDTH)) {
			return maxRightX-fudge-EPTableEx.MIN_CELL_WIDTH;
		}
	}
	return echoEvent.clientX;
};

/**
 * Called to move the drag bar to the specified position
 */
EPTableEx.prototype.moveDragBar = function(echoEvent,dragContext, clientX) {
	dragContext.dragBarE.style.left = (clientX-0)+'px';
	dragContext.dragBarE.style.display = '';
};

/**
 * Creates a drag bar to visually indicate where a drag is occurring.
 */
EPTableEx.prototype.createDragBar = function(echoEvent, cellIndex) {
	var targetTableE = this.contentTableE;
	var cellE = this.getContentCell(cellIndex);
	if (this.rowCount <= 0) {
		cellE = this.getHeaderCell(cellIndex);
		targetTableE = this.headerTableE;
	}
	var cellX = this.getClientX(cellE);
	var leftX = this.getClientX(this.headerDivE);
	var topY = this.getClientY(this.headerDivE);
	
	var borderTopWidth 		= parseInt(this.headerDivE.style.borderTopWidth,10);
	var borderBottomWidth 	= parseInt(this.contentDivE.style.borderBottomWidth,10);
	borderTopWidth 			= (isNaN(borderTopWidth) ? 1 :  borderTopWidth + 1);
	borderBottomWidth 		= (isNaN(borderBottomWidth) ? 0 :  borderBottomWidth);
	
	var dragBarE = document.createElement('div');
	dragBarE.style.position = 'absolute';
	// snap to start of column not the mouse position
	dragBarE.style.left = (cellX-0)+'px';
	//dragBarE.style.top = (topY + this.headerDivE.offsetHeight) + 'px';
	dragBarE.style.top = (topY + borderTopWidth) + 'px';
	dragBarE.style.width = '2px';
	dragBarE.style.height = (this.outerDivE.offsetHeight-borderBottomWidth)+'px';
	dragBarE.style.backgroundColor = '#A19669';
	dragBarE.style.display = 'none';
	
	var bodyE = document.getElementsByTagName('body')[0];
	bodyE.appendChild(dragBarE);
	return dragBarE;
};


/**
 * This is a call back from the EPDRAG functionality and allows the
 * cell widths to be changed as the user drags the captured mouse.
 */
EPTableEx.prototype.onDragMouseMove = function(echoEvent,dragContext) {
	var clientX = this.getValidDragMovement(echoEvent,dragContext);
	var deltaX = clientX - dragContext.eventStartX;
	if (this.resizeDragBarUsed) {
		// move the drag bar only
		this.moveDragBar(echoEvent,dragContext,clientX);
	} else {
		this.resizeDraggedColumn(echoEvent,dragContext, deltaX, false);
	}
};

/**
 * This is called when the the drag operation is ended (mouse up)
 */
EPTableEx.prototype.onDragEnd = function(echoEvent,dragContext) {
	this.dragInOperation = false;
	if (this.resizeDragBarUsed) {
		// use the left position of the drag bar not the mouse proper
		var leftX = parseInt(dragContext.dragBarE.style.left,10);
		// we then need to fudge the deltaX value so that it only goes as far as the 
		// the drag bar not the mouse
		var deltaX = leftX - dragContext.eventStartX;
		// now move the columns
		this.resizeDraggedColumn(echoEvent,dragContext,deltaX,true);
		
		// get rid of the drag bar
		dragContext.dragBarE.parentNode.removeChild(dragContext.dragBarE);
	}
	this.synchronizeTableColumnsAsych();
};

/**
 * Called when the mouse is pressed down in the header cell.  If its in a resize
 * area, it will allow a drag operation to start.
 */
EPTableEx.prototype.onMouseDown = function(echoEvent) {
	var cellIndex = this.hitTestHeaderCell(echoEvent);
	if (cellIndex == -1) {
		// dont allow mouse down if its not in a resize area or its
		// not a valid cell for this mode
		this.dragInOperation = false;
		return;
	}

	var targetTableE;	
	this.dragInOperation = true;
	var dragContext = {};
	dragContext.cellIndex = cellIndex;
	//
	// do we have any content, if not then our target is the header cells
	if (this.rowCount > 0) {
		dragContext.dragCellE = this.getContentCell(cellIndex);
		dragContext.dragNeighbor = this.getContentCell(cellIndex+1);
		targetTableE = this.contentTableE;
	} else {
		dragContext.dragCellE = this.getHeaderCell(cellIndex);
		dragContext.dragNeighbor = this.getHeaderCell(cellIndex+1);
		targetTableE = this.headerTableE;
	}
	dragContext.startWidth = this.getWidth(dragContext.dragCellE);
	dragContext.startTableWidth = this.getWidth(targetTableE);
	if (dragContext.dragNeighbor) {
		dragContext.startNeighborWidth = this.getWidth(dragContext.dragNeighbor);
	}
	if (this.resizeDragBarUsed) {
		// create a drag bar DIV
		dragContext.dragBarE = this.createDragBar(echoEvent);
	}
	
	EP.Drag.startDragOperation(echoEvent,null,this,dragContext);
};    


/**
 * Called when the mouse is moved in the header div area.  It decides whether to
 * change the mouse cursor or not
 */
EPTableEx.prototype.onMouseMove = function(echoEvent) {
	if (this.dragInOperation) {
		return;
	}	
	var cellIndex = this.hitTestHeaderCell(echoEvent);
	if (cellIndex == -1) {
		this.headerDivE.style.cursor = 'auto';
		return;
	}
	// we dont allow the last cell to be resized if its not allowed to grow the
	// table while dragging.
	if (this.headerCells) {
		if (this.resizeGrowsTable == false && cellIndex == this.headerCells.length-1) {
			this.headerDivE.style.cursor = 'auto';
			return;
		}
	}
	this.headerDivE.style.cursor = 'e-resize';
};

/**
 * Called to make the header scroll X the same as the content area
 */
EPTableEx.prototype.synchroniseScrollPositions = function() {
	var scrollX = this.contentDivE.scrollLeft;
	if (this.headerVisible) {
		this.synchroniseScrollPositionsTo(scrollX,this.headerDivE,this.headerScrollerDivE);
	}
	if (this.footerVisible) {
		this.synchroniseScrollPositionsTo(scrollX,this.footerDivE,this.footerScrollerDivE);
	}
};

EPTableEx.prototype.synchroniseScrollPositionsTo = function(scrollX, divE, scrollerDivE) {
	divE.scrollLeft = scrollX;
	var overScrollX = scrollX - divE.scrollLeft;
	if (overScrollX > 0) {
		scrollerDivE.style.left = -(overScrollX) + 'px';
	} else {
		scrollerDivE.style.left = 0 + 'px';
	}
}

/**
 * Called when the content DIV is scrolled.
 */
EPTableEx.prototype.onScroll = function(echoEvent) {
	this.synchroniseScrollPositions();
	//this.reportOnTable();
};

/**
 * Called when the content DIV is resized.
 */
EPTableEx.prototype.onResize = function(echoEvent) {
	// we only reset if its not been adjusted by the user.
	if (! this.hasBeenResized) {
		this.initContainers();
	}
	this.synchroniseScrollPositions();
	this.synchronizeTableColumns();
};

/**
 * Gecko has some issues when the logical size of the table is > than the content div size.  It
 * re-adjusts the table so that it fits into the content div instead of letting it be a "natural size"
 * and have it scroll as appropriate.  So we clone it and gets its "natural width" as it would layout if
 * it has not width specified.
 * 
 */
EPTableEx.prototype.getNaturalTableWidth = function(tableE) {
	var startWidth = tableE.offsetWidth;
	var naturalWidth = startWidth;
	if (true) {
		var cloneTableE = tableE.cloneNode(true);
		var bodyE = document.getElementsByTagName('body')[0];
		cloneTableE.style.width = '';
		cloneTableE.className = '';
		
		var cloneHolderE = document.createElement('div');
		cloneHolderE.style.position = 'absolute';
		cloneHolderE.style.visibility = 'hidden';
		cloneHolderE.appendChild(cloneTableE);
		
		bodyE.insertBefore(cloneHolderE,bodyE.firstChild);
		naturalWidth = cloneTableE.offsetWidth;
		bodyE.removeChild(cloneHolderE);
		
	}
	return naturalWidth;
};

/**
 * Called to build our internal the cells of the table
 */
EPTableEx.prototype.initCells = function() {
	this.headerCells = null;
	if (this.headerVisible && this.headerTableE.tBodies[0].rows.length > 0) {
		this.headerCells  = this.headerTableE.tBodies[0].rows[0].cells;
	}
	this.footerCells = null;
	if (this.footerVisible && this.footerTableE.tBodies[0].rows.length > 0) {
		this.footerCells  = this.footerTableE.tBodies[0].rows[0].cells;
	}
	this.contentCells = null;
	if (this.contentTableE.tBodies[0].rows.length > 0) {
		this.contentCells = this.contentTableE.tBodies[0].rows[0].cells;
	}
	
	this.colgroupCells =  [];
	var colgroup = this.contentTableE.getElementsByTagName('colgroup')[0];
	if (colgroup && colgroup.parentNode == this.contentTableE) {
		this.colgroupCells =  [];
		for (var i = 0; i < colgroup.childNodes.length; i++) {
			var col = colgroup.childNodes.item(i);
			if (col.tagName && col.tagName.toUpperCase() == 'COL') {
				this.colgroupCells[this.colgroupCells.length] = col;
			}
		}
	}
};

/**
 * Allows partial styling of som rows within the content area
 */
EPTableEx.prototype.initContentStyles = function(startRow, endRow) {
	if (this.contentCells) {
		var maxCol = this.contentCells.length-1;
		var style = EP.StyleSheet.getComputedStyle(this.contentDivE);
		var styleHeader = EP.StyleSheet.getComputedStyle(this.headerDivE);
		if (style.borderLeftWidth != '') {
			// if we have a border then we need to apply that to the contents cells
			// in a special way to make sure we have cell border width
			var rows = this.contentTableE.tBodies[0].rows
			for(var row=startRow; row < rows.length && row <= endRow; row++) {
				var cells = this.contentTableE.tBodies[0].rows[row].cells;
				for(var col=0; col < cells.length; col++) {
					var cellE = this.getNthChild(cells[col],1,'div');
					// right hand border of content cells
					if (col > 0) {
						cellE.style.borderLeftWidth = style.borderLeftWidth;
						cellE.style.borderLeftColor = style.borderLeftColor;
						cellE.style.borderLeftStyle = style.borderLeftStyle;
						if (col == maxCol) {
							// dragging over this edge wont make it disappear this way
							this.contentTableE.style.borderRightWidth = style.borderLeftWidth;
							this.contentTableE.style.borderRightColor = style.borderLeftColor;
							this.contentTableE.style.borderRightStyle = style.borderLeftStyle;
						}
					}
					if (row < rows.length-1) {
						cellE.style.borderBottomWidth = style.borderBottomWidth;
						cellE.style.borderBottomColor = style.borderBottomColor;
						cellE.style.borderBottomStyle = style.borderBottomStyle;
					} else {
						// normally we dont put a bottom border on the last row because
						// it will "clash" with the outer div border.  But if the
						// this doesnt fill the div, then we need to put one on there
						var tHeight = this.contentTableE.offsetHeight;
						var dHeight = this.contentDivE.offsetHeight;
						if (dHeight-tHeight > 2) {
							cellE.style.borderBottomWidth = style.borderBottomWidth;
							cellE.style.borderBottomColor = style.borderBottomColor;
							cellE.style.borderBottomStyle = style.borderBottomStyle;
						}
					}
					// IE6 has an issue where is collapse the border together even
					// if we havent asked it to.  So we inject them back again by hand. 
					if (row == 0 && document.all) {
						cellE.style.borderTopWidth = styleHeader.borderTopWidth;
						cellE.style.borderTopColor = styleHeader.borderTopColor;
						cellE.style.borderTopStyle = styleHeader.borderTopStyle;
					}
				}
			}
		}
	}
}

/**
 * Called to tweak some of the style settings to make it look nicer.
 * 
 * When the row count gets great then some value, then this becomes an time consuming operation
 * so we do it via a "delayed" strategy when applicable
 */
 
EPTableEx.MAGIG_STYLE_ROW_COUNT = 50;

EPTableEx.prototype.initStyles = function() {
	var table = this;
	// take this out of the main Echo2 processing loop because it can take so long if the
	// rowCount is large.  Just how large is debatable.
	if (table.rowCount > EPTableEx.MAGIG_STYLE_ROW_COUNT) {
		//
		// do the first n now so it looks nice and then do the rest later
		table.initContentStyles(0,EPTableEx.MAGIG_STYLE_ROW_COUNT);
		var initStylesClosure = function() {
			table.initContentStyles(EPTableEx.MAGIG_STYLE_ROW_COUNT,table.rowCount);		
			table.synchronizeTableColumns();
		};
		window.setTimeout(initStylesClosure,10);
	} else {
		table.initContentStyles(0,table.rowCount);
	}
	if (table.headerCells) {
		var maxCol = table.headerCells.length-1;
		var style = EP.StyleSheet.getComputedStyle(table.headerDivE);
		for(var col=0; col<table.headerCells.length; col++) {
			var cellE = table.getNthChild(table.headerCells[col],1,'div');
			if (col > 0) {
				cellE.style.borderLeftWidth = style.borderLeftWidth;
				cellE.style.borderLeftColor = style.borderLeftColor;
				cellE.style.borderLeftStyle = style.borderLeftStyle;
				if (col == maxCol) {
					table.headerTableE.style.borderRightWidth = style.borderLeftWidth;
					table.headerTableE.style.borderRightColor = style.borderLeftColor;
					table.headerTableE.style.borderRightStyle = style.borderLeftStyle;
				}
			}
		}
	}
	if (table.footerCells) {
		var maxCol = table.footerCells.length-1;
		var style = EP.StyleSheet.getComputedStyle(table.footerDivE);
		for(var col=0; col<table.footerCells.length; col++) {
			var cellE = table.getNthChild(table.footerCells[col],1,'div');
			if (col > 0) {
				cellE.style.borderLeftWidth = style.borderLeftWidth;
				cellE.style.borderLeftColor = style.borderLeftColor;
				cellE.style.borderLeftStyle = style.borderLeftStyle;
				if (col == maxCol) {
					table.footerTableE.style.borderRightWidth = style.borderLeftWidth;
					table.footerTableE.style.borderRightColor = style.borderLeftColor;
					table.footerTableE.style.borderRightStyle = style.borderLeftStyle;
				}
			}
		}
	}
	
};

/**
 * Called to initialise the container elements of the TableEx
 */
EPTableEx.prototype.initContainers = function() {
	var targetDivE = this.contentDivE;
	var targetTableE = this.contentTableE;
	//
	// and exception is where we have a header visible but now rows of table data
	// in this case we only want to resize the header not the content area
	if (this.rowCount == 0 && this.headerVisible) {
		targetDivE = this.headerDivE;
		targetTableE = this.headerTableE;
	}
	
	// For some reason Gecko INSISTS the content table must be 100% in order for the
	// cells to overlap when dragging and from the testing I sense that this MUST be the FIRST
	// ever setting of the style value. If its doesnt start with 100% as its value it refuses
	// to allow the cells to overlap later.  So make sure its set in the style="" in the rendering peer
	if (EP.isGecko) {
		targetTableE.style.width = '100%';
	}
    
    // Initial layout.  We have 2 elements that control the width of the logical TableEx
    // the content Div (which is scrollable) and the content table itself but we only have
    // one "width" specified on the component.  Therefore we make the 1 value apply magically
    // to 2 elements in a special way.
    //
    // Also in order to be "scrollable" the contentDiv must end up a fixed pixel size to ensure
    // that it acts as a viewport for content table part.
    //
    //debugger;
    var naturalWidth = this.getNaturalTableWidth(targetTableE);
    var contentWidth = targetDivE.offsetWidth;
    var tableWidth = targetTableE.offsetWidth;
    var contentBorderWidth = this.getBorderWidths(targetDivE);
    if (this.contentOriginalWidth == '' || this.contentOriginalWidth == 'auto') {
    	// No width has been specified.  We let the table lay itself out
    	// as it sees fit and if the resulting table is smaller then the
    	// content div area, we shrink the content div to match the table.  This is table
    	// like behaviour in that it makes itself as wide as it wants to an no more.
		targetTableE.style.width = naturalWidth + 'px';
		if (targetDivE.offsetWidth > naturalWidth) {
	    	targetDivE.style.width = naturalWidth+'px';
	    	// we dont want a horizontal scroll bar if we dont need it
	    	if (this.hasHorzScrollBar(targetDivE)) {
		    	targetDivE.style.width = naturalWidth+(this.contentDivE.offsetWidth-contentBorderWidth-this.contentDivE.clientWidth)+'px';
	    	}
		}
		
		// size the header div to match
		var newWidth = (targetDivE.offsetWidth-contentBorderWidth) + 'px';
		if (this.headerVisible) {
			this.headerDivE.style.width = newWidth;
		}
		if (this.footerVisible) {
			this.footerDivE.style.width = newWidth;
		}
    } else if (this.contentOriginalWidth.indexOf('%') != -1) {
    	//
    	// the table must be made to fit its content div as the content div is trying to fit
    	// all or part of its of its container.  It may need to be stretched sideways to
    	// fit the container.
    	if (EP.isIE && this.contentOriginalWidth == '100%') {
    		targetDivE.style.width = '99%';
    	}
    	targetTableE.style.width = '100%';
    	tableWidth = targetTableE.offsetWidth;
    	if (EP.isGecko) {
    		// Gecko will cause the table to shrink to fit the container
    		// which is not what we want regardless once it 100%.  
    		// We want at least its natural width
    		if (naturalWidth > targetDivE.offsetWidth) {
    			// force it to go to its natural width
		    	targetTableE.style.width = naturalWidth+'px';
    		} else {
		    	if (this.hasHorzScrollBar(targetDivE)) {
			    	targetTableE.style.width = tableWidth+'px';
		    	} else {
			    	targetTableE.style.width = tableWidth+'px';
		    	}
    		}
    	} else if (EP.isIE) {
	    	// HACK - IE ends up showing a unwanted horizontal scroll bar here if
	    	// a height is specified
	    	if (this.hasHorzScrollBar(targetDivE)) {
		    	if (this.contentOriginalHeight.indexOf('px') != -1) {
		    		var scrollBarWidth = targetDivE.offsetWidth - targetDivE.clientWidth; 
		    		targetTableE.style.width = (targetDivE.offsetWidth-scrollBarWidth)+'px';
		    	} else {
					targetTableE.style.width = tableWidth+'px';	    	
				}
	    	}
    	}
    } else {
    	// it must be in pixels or em or some such fixed value.  The table should layout as it wants
    	// to and the possibly "scroll" within the fixed size content div.
 	    targetTableE.style.width = naturalWidth + 'px';
 	    
		// size the header div to match
		var newWidth = (targetDivE.offsetWidth-contentBorderWidth) + 'px';
		if (this.headerVisible) {
			this.headerDivE.style.width = newWidth;
		}
		if (this.footerVisible) {
			this.footerDivE.style.width = newWidth;
		}
    }
    //
    // now the final step is to "constrict the table down so fits sinde its parent container".  We only
    // do this if the user has not asked for an explictly sized table 
    if (this.contentOriginalHeight.indexOf('px') == -1) {
		// we want to tap into the EP stretch function so that we knwo when containers are resized
		// but we dont want the default implementation.  So we replace it and call our own
		if (this.heightStretched && ! this.verticalStretcher) {
			// save away our natural height so we can grow and shrink as need be later
			var that = this;
			this.verticalStretcher = EP.Stretch.verticalStretcher.getInstance(this.contentDivE.id, this.minHeight, this.maxHeight);
			this.verticalStretcher.stretch = function() {
				that.constrictHeightToContainer();
				that.synchroniseScrollPositions();
				that.synchronizeTableColumns();
			}
		}
    }
};

/**
 * Called to allow the content to shrink and expand into the container
 */
EPTableEx.prototype.constrictHeightToContainer = function() {
	
	// find the first positioned parent with height
	var elementE = this.contentDivE;
	for (var parentE = elementE.parentNode; parentE;) {
		parentStyle = parentE.style;
		if (parentStyle) {
			if (parentStyle.height || parentStyle.position == "absolute" || parentStyle.position == "fixed") {
				break;
			}
		}
		parentE = parentE.parentNode;
	}
	if (parentE == null) {
		return;
	}
	var allContentHeight = 0;
	for (var i = 0; i < parentE.childNodes.length; i++) {
		var node = parentE.childNodes[i];
		// Ignore text, floated, and absolutely positioned nodes
		if (node.nodeType === 1) {
			s = node.style;
			if (!s.styleFloat && !s.cssFloat && s.position != "absolute" && s.position != "fixed") {
				allContentHeight += node.offsetHeight;
			}
		}
	}
	var availableHeight = parentE.clientHeight;
	var contentDivHeight = this.contentDivE.clientHeight;
	var contentTableHeight = this.contentTableE.offsetHeight;
	
	//
	// ok we are naturally bigger then the containers available space, so we should srinky by enough to just fit
	// the container
	//var newHeight = Math.max(0, this.contentNaturalHeight + availableHeight - allContentHeight);
	var newHeight = Math.max(0, contentDivHeight + availableHeight - allContentHeight);
	//
	// if the natural height of the content table is less then the container
	// height then we dont need to stretch it forward
	if (contentTableHeight < newHeight) {
		return;
	}
	
	var respectedLimits = false;
	if (this.minHeight && ! isNaN(this.minHeight) && newHeight < this.minHeight) {
		newHeight = this.minHeight;
		respectedLimits = true;
	}
	if (this.maxHeight && ! isNaN(this.maxHeight) && newHeight > this.maxHeight) {
		newHeight = this.maxHeight;
		respectedLimits = true;
	}
	
	if (newHeight > 0) {
		//
		// prevent "flash" of scroll bars
		var saveOverflow = this.contentDivE.style.overflow;
		var saveParentOverflow = parentE.style.overflow;
		
		this.contentDivE.style.overflow = parentE.style.overflow = "hidden";
		this.contentDivE.style.height = newHeight + 'px';
		if (! respectedLimits) {
			// Firefox sometimes does not include margins in offsetHeight so adjust again if we created some overflow
			this.contentDivE.style.height = Math.max(0, newHeight + parentE.offsetHeight - parentE.scrollHeight) + "px";
		}
		// restore overflow settings
		this.contentDivE.style.overflow = saveOverflow;
		parentE.style.overflow = saveParentOverflow;
	}
};


/**
 * Initialises the TableEx for scrollable support.  Some basic design assumptions have been
 * made here.  First off if the header/content div strategy is being used then the
 * TableEx is being made scrollable, otherwise we would simple have 1 table without all
 * the containing DIV malarchy.
 */
EPTableEx.prototype.initScrollable = function(elementId) {
	//debugger;
	this.outerDivE 			=  document.getElementById(elementId);

	this.headerDivE 		=  document.getElementById(elementId+'_headerDiv');
	this.headerScrollerDivE =  document.getElementById(elementId+'_headerScrollerDiv');
	this.headerTableE 		=  document.getElementById(elementId+'_headerTable');

	this.footerDivE 		=  document.getElementById(elementId+'_footerDiv');
	this.footerScrollerDivE =  document.getElementById(elementId+'_footerScrollerDiv');
	this.footerTableE 		=  document.getElementById(elementId+'_footerTable');
	
	this.contentDivE 		=  document.getElementById(elementId+'_contentDiv');
	this.contentTableE 		=  document.getElementById(elementId+'_contentTable');

	// save our originally specified widths and heights
    this.contentOriginalWidth = this.contentDivE.style.width;
	this.contentOriginalHeight = this.contentDivE.style.height;
	
	this.initCells();
	this.initStyles();
	this.initContainers();
	
    if (this.resizeable) {
    	if (this.headerVisible) {
		    EP.Event.addHandler("mousedown", this.headerDivE, this);
    	    EP.Event.addHandler("mousemove", this.headerDivE, this);
    	}
	    
	    //
	    // If the table columns are resizeable then they must be
	    // expressed in pixels.  So we run through their offsetWidths
	    // and set the cell widths to those pixels sizes.  They will not 
	    // resize consistently if we dont.   
	    //
	    if (this.contentCells) {
	    	for(var index=0; index<this.contentCells.length; index++) {
	    		var cellE = this.contentCells[index];
	    		var width = this.getWidth(cellE)
	    		this.setWidth(cellE,width);
	    		var colGroupE = this.getColGroupCell(index);
	    		if (colGroupE) {
		    		this.setWidth(colGroupE,width);
	    		}
	    	}
	    }
    }
    
	//
	// The user may want a table that is X pixels in height.  So we need to adjust the content div height
	// to be its current pixel height - the current header height.  We only do this if the 
	// height is previously specified in pixels.  And we only do this once
	if (this.rowCount > 0 && this.headerVisible && this.contentOriginalHeight.indexOf('px') != -1) {
		var headerHeight = this.headerDivE.offsetHeight;
		if (this.footerVisible) {
			headerHeight += this.footerDivE.offsetHeight;
		}
		var currentHeight = this.contentDivE.offsetHeight;
		this.contentDivE.style.height = (currentHeight - headerHeight) + 'px';
	}
	// save away our natural height for later use
   	this.contentNaturalHeight = this.contentDivE.clientHeight;
	
	// synch the header cells with the table cells
	this.synchronizeTableColumns();
	this.synchroniseScrollPositions();
	if (this.rowCount > 0) {	
		// it must have a scroll handler if its scrollable
	    EP.Event.addHandler("scroll", this.contentDivE, this);
	    // if should respond to changes in the browser size
	    EP.Event.addHandler("resize", this.contentDivE, this);
	    if (EP.isGecko) {
	    	// resize doesnt seem to work on Gecko at the element level!
	  		EP.DocumentEvent.addHandler('resize', 'EPTableEx', EPTableEx.documentEventHandler);
	    }
	}
	this.reportOnTable();	
};

