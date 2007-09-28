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

/**
 * extern statement used by www.jslint.com javascript validation tool.
 */
/*extern EchoClientEngine, EchoClientMessage, EchoClientProperties, EchoCssUtil, EchoDebugManager, EchoDomPropertyStore, EchoDomUtil, EchoEventProcessor, EchoServerTransaction, EchoFocusManager, EchoStringUtil */


EchoPointTree = function() {};
EchoPointTree.MessageProcessor = function() {};			// receives and processes echo2 server to client messages
EchoPointTree.EventProcessor = function() {};			// handles events


/**
 * MessageProcessor process() implementation (invoked by ServerMessage processor).
 *
 * @param messagePartElement the <code>message-part</code> element to process.
 */
EchoPointTree.MessageProcessor.process = function(messagePartElement) {

    for (var i = 0; i < messagePartElement.childNodes.length; i++) {
    
        if (messagePartElement.childNodes[i].nodeType == 1) {
        
            switch (messagePartElement.childNodes[i].tagName) {
	            case "init":
	                EchoPointTree.MessageProcessor.processInit(messagePartElement.childNodes[i]);
	                break;
	            case "scrollIntoView":
	                EchoPointTree.MessageProcessor.processScrollIntoView(messagePartElement.childNodes[i]);
	                break;
	            case "dispose":
	                EchoPointTree.MessageProcessor.processDispose(messagePartElement.childNodes[i]);
	                break;
            }
        }
    }
};


/**
 * Processes an <code>init</code> message to initialise the state of a 
 * Tree component that is being added.
 *
 * @param messageElement the <code>init</code> element to process
 */
EchoPointTree.MessageProcessor.processInit = function(messageElement) {

	for (var item = messageElement.firstChild; item; item = item.nextSibling) {
		var element = document.getElementById(item.getAttribute("eid"));
		EchoEventProcessor.addHandler(element, "click", "EchoPointTree.EventProcessor.onClick");
		EchoDomPropertyStore.setPropertyValue(element, "cacheNodes", item.getAttribute("cacheNodes") == "true");
	}
};


/**
 * Processes a <code>dispose</code> message to finalize the state of a
 * Tree component that is being removed.
 *
 * @param messageElement the <code>dispose</code> element to process
 */
EchoPointTree.MessageProcessor.processDispose = function(messageElement) {
	
    for (var item = messageElement.firstChild; item; item = item.nextSibling) {
   		EchoEventProcessor.removeHandler(item.getAttribute("eid"), "click");
    }
};

EchoPointTree.MessageProcessor.processScrollIntoView = function(messageElement) {
	for (var item = messageElement.firstChild; item; item = item.nextSibling) {
		var elementId = item.getAttribute("eid");
		var scrollIntoViewPath = item.getAttribute("scrollIntoViewPath");
		var expandedTargetPath = item.getAttribute('expandedTargetPath');
		
		//
		// scroll the peer or last child into view
		EchoPointTree.scrollNodeIntoView(elementId,scrollIntoViewPath, false);
		//
		// It is important that the user sees their original expand node.  If the scrolling 
		// causes the original expand target to be out of view, then we scroll back to 
		// it.  If it didnt scoll out of view then no harm
		EchoPointTree.scrollNodeIntoView(elementId,expandedTargetPath, EP.isIE ? true : false);
	}
};

EchoPointTree.scrollNodeIntoView = function(elementId, treePath, towardsTop) {
	var targetNode = document.getElementById(EchoPointTree.makeNodeId(elementId,'text',treePath));
	if (! targetNode) {
		targetNode = document.getElementById(EchoPointTree.makeNodeId(elementId,'icon',treePath));
	}
	if (targetNode && targetNode.scrollIntoView) {
		targetNode.scrollIntoView(towardsTop);
	}
}

EchoPointTree.makeNodeId = function(elementId, cellType, treePath) {
	var id = 'tree|' + elementId + '|' + cellType + '|select|' + treePath;
	return id;
}

/**
 * Handles a click anywhere on the Tree as a whole then attempts to find
 * the specific node that was clicked and perform its appropriate action.
 * 
 * @param {Object} e - preprocessed echoEvent
 */
EchoPointTree.EventProcessor.onClick = function(e) {

	// The event is on the tree as a whole.
    var elementId = EchoDomUtil.getComponentId(e.registeredTarget.id);
    
    if (!EchoClientEngine.verifyInput(elementId)) { return; }

	var treeEl = document.getElementById(elementId);
	
	// Find the tree cell containing whatever was clicked (it might have been an image or piece of text)
	var el = e.target;
	if (el === treeEl) {
		return; // Must have clicked empty space outside or between nodes.
	}
	while (true) {	
		if (el == treeEl) {
			break; // gone up to far
		}
		if (el.nodeName.toLowerCase() == "td") {
			// Ignore "uninteresting" cells - only monitor ones like this: "tree|c_99|icon|select|[root][3]"
		    if (el.id && (el.id.indexOf("|select|") > 0 || el.id.indexOf("|expando|") > 0)) {
				
				// Stop click event from propagating to document and/or other components
				EchoDomUtil.stopPropagation(e);
				
				// Obtain action info from node id
				var idArray = el.id.split("|");
				EchoClientMessage.setActionValue(idArray[1], idArray[3], idArray[4]);
				EchoServerTransaction.connect();
			}
		}
		el = el.parentNode;
	}
};

/**
 * Handles a click on a specific node. Used by the TreeTable, which has 
 * tree nodes split amongst separate table rows.
 * 
 * @param {Object} e - preprocessed echoEvent
 */
EchoPointTree.EventProcessor.onNodeClick = function(e) {
	
    var el = e.registeredTarget;
	
	// Node id should be something like this: "tree|c_99|icon|select|[root][3]"
    var idArray   = el.getAttribute("id").split("|"); 
    var treeId = idArray[1];
	
	if (!EchoClientEngine.verifyInput(treeId)) { 
		return; 
	}
	
	// Stop click event from propagating to document and/or other components
	EchoDomUtil.stopPropagation(e);
	
    EchoClientMessage.setActionValue(treeId, idArray[3], idArray[4]);
    EchoServerTransaction.connect();
};

