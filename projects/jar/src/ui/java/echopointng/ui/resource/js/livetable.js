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

//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
// Our EPLiveTable object.
//- - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
EPLiveTable = function(elementId) { 
	this.elementId = elementId;
	EP.ObjectMap.put(elementId, this); 
};



/**
 * This function allows for server interactions from href="" attributes. Use
 * this in you generated row data to allow clicks to generate events back on the
 * server.
 * <p>
 * You would use it something like :
 * 
 * <pre>
 * href = &quot;javascript:EPLiveTable.href('c_1234','valueXXX');&quot;
 * </pre>
 */
EPLiveTable.href  = function(elementId, actionValue) {
	EP.Event.hrefActionHandler(elementId,'click',actionValue);
};

/**
 * EPLiveTable has a ServerMessage processor.
 */
EPLiveTable.MessageProcessor = function() { };

EPLiveTable.MessageProcessor.process = function(messagePartElement) {
	//debugger;
    for (var i = 0; i < messagePartElement.childNodes.length; ++i) {
        if (messagePartElement.childNodes[i].nodeType == 1) {
            switch (messagePartElement.childNodes[i].tagName) {
	            case "init":
	                EPLiveTable.MessageProcessor.processInit(messagePartElement.childNodes[i]);
	                break;
	            case "dispose":
	                EPLiveTable.MessageProcessor.processDispose(messagePartElement.childNodes[i]);
	                break;
            }
        }
    }
};


EPLiveTable.MessageProcessor.processInit = function(initMessageElement) {
    //EchoServerDelayMessage.activate();
    for (var item = initMessageElement.firstChild; item; item = item.nextSibling) {
        var elementId = item.getAttribute("eid");
        EP.ObjectMap.destroy(elementId);
		// just in case they exist
		EP.StyleSheet.removeAllStyles(elementId);
        
		var liveTable  = new EPLiveTable(elementId);
        if (item.getAttribute("enabled") == "false") {
            EchoDomPropertyStore.setPropertyValue(elementId, "EchoClientEngine.inputDisabled", true);
        }
		liveTable.totalRows = parseInt(item.getAttribute("totalRows"));
		liveTable.rowsPerPage = parseInt(item.getAttribute("rowsPerPage"));
		liveTable.pageFetchSize = parseInt(item.getAttribute("pageFetchSize"));
		liveTable.rolloverEnabled = item.getAttribute("rolloverEnabled") == 'true';
		liveTable.rolloverStyleName = item.getAttribute("rolloverStyleName");
		liveTable.selectionEnabled = item.getAttribute("selectionEnabled") == 'true';
		liveTable.selectionStyleName = item.getAttribute("selectionStyleName");
		
		liveTable.originalStyleName = [];
		
		liveTable.pageCount = liveTable.totalRows / liveTable.rowsPerPage;
		
		liveTable.pageIsLoaded = [];
		for (var i = 0; i < liveTable.pageCount; i++) {
			liveTable.pageIsLoaded[i] = false;
		}
		// 
		// we come down with the pageFetchSize pages pre-loaded
		for (var i = 0; i < liveTable.pageFetchSize; i++) {
			liveTable.pageIsLoaded[i] = true;
		}
		
		var scrollerDivE = document.getElementById(elementId);

		liveTable.scrollerDivE = scrollerDivE; 
		var tableE = scrollerDivE.firstChild;

		// events
    	EP.Event.addHandler("scroll", scrollerDivE, liveTable);
    	if (liveTable.rolloverEnabled) {
	    	EP.Event.addHandler("mouseover", scrollerDivE, liveTable);
	    	EP.Event.addHandler("mouseout", scrollerDivE, liveTable);
    	}

    	// style support
    	var styleIndex = 0;
		var styles = item.getElementsByTagName("styles")[0];
	    for (var sourceStyleE = styles.firstChild; sourceStyleE; sourceStyleE = sourceStyleE.nextSibling) {
			EP.StyleSheet.addStyle(elementId, sourceStyleE, styleIndex);
			styleIndex++;
	    }
		
		//
		// we make multiple copies of the empty row on the client to
		// save bandwidth.
		//
		var emptyRowTR = item.getElementsByTagName("emptyRow")[0].firstChild;
        var newTR = EchoDomUtil.importNode(document, emptyRowTR, true);
		var tbody = document.createElement("tbody");
        tbody.appendChild(newTR);
		var singleEmptyRowHtml = tbody.innerHTML;
		delete tbody;
		
		var emptyTbodyHtml = [];
		emptyTbodyHtml.push('<tbody>');
		for (var j = 0; j < liveTable.rowsPerPage; j++) {
			emptyTbodyHtml.push(singleEmptyRowHtml);
		}
		emptyTbodyHtml.push('</tbody>');
		emptyTbodyHtml = emptyTbodyHtml.join('');
		
		// build up an array of tbody pages
		var emptyPagesHtml = [];
		for (var i = 0; i < liveTable.pageCount; i++) {
			if (liveTable.pageIsLoaded[i]) {
				continue;
			}
			emptyPagesHtml.push(emptyTbodyHtml);
		}
		try {
			//
			// append the set of empty pages to the exisiting html. Its seems
			// much faster this way
			var attrs = tableE.attributes;
			var newTableHtml = '<table ';
			for (i = 0; i < attrs.length; i++) {
				newTableHtml += ' ' + attrs[i].name + '="' + attrs[i].value + '"' 
			}
			newTableHtml += '>';
			newTableHtml += tableE.innerHTML;
			newTableHtml += emptyPagesHtml.join('');
			newTableHtml += '</table>';
			
			scrollerDivE.innerHTML = newTableHtml; 
		 } catch (ex) {
		    EchoServerDelayMessage.deactivate();
		 	throw ex;
		 }
    }
    //EchoServerDelayMessage.deactivate();
};

EPLiveTable.MessageProcessor.processDispose = function(disposeMessageElement) {
    for (var item = disposeMessageElement.firstChild; item; item = item.nextSibling) {
        var elementId = item.getAttribute("eid");
		EP.StyleSheet.removeAllStyles(elementId);
		EP.ObjectMap.destroy(elementId);
    }
};

EPLiveTable.prototype.destroy = function() {
	if (this.scrollerDivE) {
    	EP.Event.removeHandler("scroll", this.scrollerDivE);
    	if (this.rolloverEnabled) {
    		EP.Event.removeHandler("mouseover", this.scrollerDivE);
    		EP.Event.removeHandler("mouseout", this.scrollerDivE);
    	}
	}
}

/**
 * Our top level event handler function
 */
EPLiveTable.prototype.eventHandler = function(echoEvent) {
	if (! EchoClientEngine.verifyInput(this.elementId)) {
        return;
    }	
	if (echoEvent.type == "scroll") {
		this.onscroll(echoEvent);
	}
	if (echoEvent.type.indexOf("mouse") == 0) {
		this.onmouse(echoEvent);
	}
};

/**
 * Called when mouse events happen
 */
EPLiveTable.prototype.onmouse = function(echoEvent) {
    var target = echoEvent.target;
	EchoDebugManager.consoleWrite(echoEvent.type + " - target : " + target.tagName + " - " + target.className);	
	// navigate up to the to its top level TR
	while (true) {
		// oops we hit the top level
		if (target.id == this.elementId) {
			EchoDebugManager.consoleWrite(echoEvent.type + " no TR found");	
			return;
		}
		if (target.tagName.toLowerCase() == 'tr') {
			// is it a top level TR
			if (target.parentNode.parentNode.parentNode.id == this.elementId) {
				break;
			}
		}
		target = target.parentNode;
	}
    EchoDomUtil.preventEventDefault(echoEvent);
	
	var trElement = target;
    for (var i = 0; i < trElement.cells.length; ++i) {
    	var td = trElement.cells[i];
    	EchoDebugManager.consoleWrite(echoEvent.type + " - td : " + td.className);	
    	if (echoEvent.type == "mouseover") {
	    	var origClassName = td.className;
	    	if (origClassName) {
	    		this.originalStyleName[td] = origClassName;
				td.className = this.rolloverStyleName;
	    	}
	    }
    	if (echoEvent.type == "mouseout") {
    		for (var i = 0; i < trElement.cells.length; ++i) {
    			var td = trElement.cells[i];
    			var origClassName = this.originalStyleName[td];
    			if (origClassName) {
    				td.className = origClassName;
    			}
    		}
    	}
	}
}


/**
 * Called when the scroller div is scrolled and hence new data may need to be
 * loaded
 */
EPLiveTable.prototype.onscroll = function(echoEvent) {
	if (this.visibleTimerId) {
		clearTimeout(this.visibleTimerId);
		this.visibleTimerId = null;
	}
	if (this.postVisibleTimerId) {
		clearTimeout(this.postVisibleTimerId);
		this.postVisibleTimerId = null;
	}
	if (this.preVisibleTimerId) {
		clearTimeout(this.preVisibleTimerId);
		this.preVisibleTimerId = null;
	}
	var div = this.scrollerDivE;
	var pixelsPerRow = div.scrollHeight / this.totalRows;
	var rowsPerPage = this.rowsPerPage;
	
	var topVisibleRow = Math.round(1 + ( div.scrollTop / pixelsPerRow));
	var rowsInViewPort = Math.floor((div.clientHeight / pixelsPerRow));

	this.topVisibleRow  = topVisibleRow;
	this.rowsInViewPort = rowsInViewPort;
	
	var prevPage = 0;;
	var currentPage = Math.floor(topVisibleRow / rowsPerPage);
	if(this.doesPageNeedLoading(currentPage)){
		this.visibleTimerId = this.loadPage(currentPage);
	}

	prevPage = currentPage;
	currentPage = Math.floor( (topVisibleRow+rowsInViewPort) / rowsPerPage);
	if(currentPage != prevPage && this.doesPageNeedLoading(currentPage)){
		this.postVisibleTimerId = this.loadPage(currentPage);
	}

	prevPage = currentPage;
	currentPage = Math.floor((topVisibleRow-(rowsInViewPort/2)) / rowsPerPage);
	if(currentPage != prevPage && this.doesPageNeedLoading(currentPage)){
		this.preVisibleTimerId = this.loadPage(currentPage);
	}
};

/**
 * Called to determine if a given page needs loading
 */
EPLiveTable.prototype.doesPageNeedLoading = function(currentPage) {
	if (currentPage < 0) {
		return false;
	}
	var isPresent = this.scrollerDivE.firstChild.childNodes[currentPage]; 
	var isAlreadyLoaded = this.pageIsLoaded[currentPage]; 
	if (isPresent && isAlreadyLoaded == false){
		return true;
	}
	return false;
}

/**
 * Loads a given page of data using aysnch methods
 */
EPLiveTable.prototype.loadPage = function(currentPage) {
	var funcionCall = 'EPLiveTable.asynchLoad(\'' + this.elementId + '\',' + currentPage + ')'; 
	return setTimeout(funcionCall,200);
}


/**
 * Called in an asynch manner (setTimeout) to load data into the LiveTable.
 * 
 * The asynch call is made to allow new scroll events to happen before this
 * function completes and hence prevent data being loaded for pages that might
 * not be visible any more. This smooths out the actual scrolling code.
 */
EPLiveTable.asynchLoad = function(elementId, currentPage) {
	var liveTable = EP.ObjectMap.get(elementId);
	if (liveTable == null) {
		return;
	}
	
	var uri = EchoClientEngine.baseServerUri + "?serviceId=EPNG.LiveTable&elementId=" + elementId + "&currentPage=" + currentPage;
	
    var conn = new EchoHttpConnection(uri, "GET");
    conn.elementId = elementId;
    conn.currentPage = currentPage;
    
    conn.responseHandler = EPLiveTable.responseHandler;
    conn.invalidResponseHandler = EPLiveTable.invalidResponseHandler;
    
    
    //EchoServerDelayMessage.activate();
    conn.connect();
};

/**
 * Processes a response from the HTTP request made to the LiveTable service and
 * loads the appropriate pages.
 * 
 * The XML structure is something like :
 * 
 * |data 
 * 	|----tbody <-- represents a page of row data 
 * 		|----tr, td .... 
 * 		|----tr, td .... 
 * 		|----tr, td .... 
 * 	|----tbody 
 * 		|----tr, td .... 
 * 		|----tr, td ....
 * 		|----tr, td .... 
 * 	|----tbody 
 * 		|----tr, td .... 
 * 		|----tr, td .... 
 * 		|----tr, td
 * ....
 * 
 * @param conn
 *            the EchoHttpConnection containing the response information.
 */
EPLiveTable.responseHandler = function(conn) {
	//EchoServerDelayMessage.deactivate();
	var elementId = conn.elementId;
	var liveTable = EP.ObjectMap.get(elementId);
	if (liveTable == null) {
		return;
	}

	// we assume there is only one data section, which contains one or more
	// tbody (page level) elements
	var dataElement = conn.getResponseXml().documentElement;
    var tbodyElements = dataElement.getElementsByTagName("tbody");
    for (var i = 0; i < tbodyElements.length; ++i) {
		var currentPage = conn.currentPage + i;

		//
		// replace the TBODY page in the inner TABLE with the new data
		var tableE = liveTable.scrollerDivE.firstChild;
		var oldChild = tableE.childNodes[currentPage];
		if (oldChild) {
			var newChild = tbodyElements[i];
	
			// FireFox requires the XML to be in the XHTML namespace
			newChild.setAttribute("xmlns", "http://www.w3.org/1999/xhtml");
			// import the XTHML into our document structure
	        newChild = EchoDomUtil.importNode(tableE.ownerDocument, newChild, true);
			
	        // now replace the current page placeholder with the new data
			liveTable.scrollerDivE.firstChild.replaceChild(newChild,oldChild);
			
			liveTable.pageIsLoaded[currentPage] = true;
		}
	}		
};

/**
 * Processes an invalid response to table data request
 */
EPLiveTable.invalidResponseHandler = function() {
    //EchoServerDelayMessage.deactivate();
    alert("Invalid response from server to LiveTable data request.");
};
