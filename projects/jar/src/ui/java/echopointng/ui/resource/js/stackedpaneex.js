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
 * This design for this file was taken from the Echo2
 * TabPane which is Copyright (C) 2005-2006 NextApp, Inc.
 *  
 */
 
/* 
 * This file is part of the Echo2 Extras Project.
 * Copyright (C) 2005-2006 NextApp, Inc.
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
 * EPStackedPaneEx Object/Namespace/Constructor.
 *
 * @param elementId the root id of the tab pane.
 * @param containerElementId the id of the DOM element which will contain the 
 *        tab pane.
 * @param activePaneId the id of the active tab (if applicable)
 */
EPStackedPaneEx = function(elementId, containerElementId, activePaneId) {
    this.elementId = elementId;
    this.containerElementId = containerElementId;
    this.activePaneId = activePaneId;

    this.panes = new Array();
};

EPStackedPaneEx.prototype.create = function() {
    var containerElement = document.getElementById(this.containerElementId);
    if (!containerElement) {
        throw "Container element of StackedPaneEx not found: " + this.containerElementId;
    }

    this.stackedPaneDivElement = document.createElement("div");
    this.stackedPaneDivElement.id = this.elementId;
    
	this.stackedPaneDivElement.style.position = "absolute";
	this.stackedPaneDivElement.style.width = "100%";
	this.stackedPaneDivElement.style.height = "100%";
	this.stackedPaneDivElement.style.overflow = "auto";
    
    //
    // rendered on the server and sent down to us
    EchoCssUtil.applyStyle(this.stackedPaneDivElement, this.styleText);
	// 
	// 
	var attrs = this.standardWebSupportE.attributes;
	for(var index=0; index<attrs.length; index++) {
		 var attr = attrs[index];
		 this.stackedPaneDivElement.setAttribute(attr.name,attr.value);
	}
    EchoVirtualPosition.register(this.stackedPaneDivElement.id);
    containerElement.appendChild(this.stackedPaneDivElement);
    
    EchoDomPropertyStore.setPropertyValue(this.elementId, "stackedPane", this);
};

EPStackedPaneEx.prototype.addPane = function(pane, paneIndex) {
    EPStackedPaneEx.Arrays.insertElement(this.panes, pane, paneIndex);
    pane.contentDivElement = document.createElement("div");
    pane.contentDivElement.id = this.elementId + "_content_" + pane.paneId;
    pane.contentDivElement.style.display = "none";
    pane.contentDivElement.style.position = "absolute";
    pane.contentDivElement.style.overflow = "auto";
    pane.contentDivElement.style.left = "0px";
    pane.contentDivElement.style.right = "0px";
    pane.contentDivElement.style.bottom = "0px";
    pane.contentDivElement.style.top = "0px";
    this.stackedPaneDivElement.appendChild(pane.contentDivElement);
    
    EchoVirtualPosition.register(pane.contentDivElement.id);
    
    if (this.activePaneId == pane.paneId) {
        this.selectPane(pane.paneId);
    }
};


EPStackedPaneEx.prototype.dispose = function() {
    for (var i = 0; i < this.panes.length; ++i) {
        this.panes[i].dispose();
    }
    this.panes = null;
    EchoDomPropertyStore.dispose(this.stackedPaneDivElement);
    this.stackedPaneDivElement = null;
};

/**
 * Retrieves the EPStackedPaneEx.Pane instance with the specified pane id.
 * 
 * @param paneId the pane id
 * @return the Pane, or null if no pane is present with the specified id
 */
EPStackedPaneEx.prototype.getPaneById = function(paneId) {
    for (var i = 0; i < this.panes.length; ++i) {
        if (this.panes[i].paneId == paneId) {
            return this.panes[i];
        }
    }
    return null;
};

EPStackedPaneEx.prototype.removePane = function(pane) {
    EPStackedPaneEx.Arrays.removeElement(this.panes, pane);
    pane.contentDivElement.parentNode.removeChild(pane.contentDivElement);
    if (this.activePaneId == pane.paneId) {
        this.selectPane(null);
    }
};

EPStackedPaneEx.prototype.selectPane = function(paneId) {
    if (this.activePaneId) {
        this.updatePaneState(this.activePaneId, false);
    }
    if (paneId != null) {
        this.updatePaneState(paneId, true);
    }
    // Update state information.
    this.activePaneId = paneId;
    var activePane = this.getPaneById(paneId);
    if (activePane) {
        EchoVirtualPosition.redraw();
    }
};

EPStackedPaneEx.prototype.updatePaneState = function(paneId, selected) {
    var newContentDivElement = document.getElementById(this.elementId + "_content_" + paneId);
    if (newContentDivElement) {
	    newContentDivElement.style.display = selected ? "block" : "none";
    }
};

EPStackedPaneEx.getComponent = function(stackedPaneId) {
    return EchoDomPropertyStore.getPropertyValue(stackedPaneId, "stackedPane");
};


/**
 * A data object which represents a single pane within an StackedPaneEx.
 * Creates a new Pane.
 *
 * @param paneId the id of the pane
 * @param rendered a boolean flag indicating whether the pane's content has
 *        been rendered to the client (if it has not it must be fetched when
 *        the pane is selected)
 */
EPStackedPaneEx.Pane = function(paneId, rendered) { 
    this.paneId = paneId;
    this.rendered = rendered;
};

EPStackedPaneEx.Pane.prototype.dispose = function() {
    this.contentDivElement = null;
};

EPStackedPaneEx.Arrays = function() {};

EPStackedPaneEx.Arrays.insertElement = function(array, element, index) {
    if (index == 0) {
        array.unshift(element);
    } else if (index == -1 || index == array.length) {
        array.push(element);
    } else if (index > array.length) {
        throw "Array index of bounds: " + index + " (size=" + array.length + ")";
    } else {
        for (var i = array.length - 1; i >= index; --i) {
            array[i + 1] = array[i];
        }
        array[index] = element;
    }
};

EPStackedPaneEx.Arrays.removeElement = function(array, element) {
    var index = EPStackedPaneEx.Arrays.indexOf(array, element);
    if (index == -1) {
        return;
    }
    EPStackedPaneEx.Arrays.removeIndex(array, index);
};

EPStackedPaneEx.Arrays.indexOf = function(array, element) {
    for (var i = 0; i < array.length; ++i) {
        if (array[i] == element) {
            return i;
        }
    }
    return -1;
}

EPStackedPaneEx.Arrays.removeIndex = function(array, index) {
    for (i = index; i < array.length - 1; ++i) {
        array[i] = array[i + 1];
    }
    array.length = array.length - 1;
};


/**
 * Static object/namespace for StackedPaneEx MessageProcessor 
 * implementation.
 */
EPStackedPaneEx.MessageProcessor = function() { };

/**
 * MessageProcessor process() implementation 
 * (invoked by ServerMessage processor).
 *
 * @param messagePartElement the <code>message-part</code> element to process.
 */
EPStackedPaneEx.MessageProcessor.process = function(messagePartElement) {
    for (var i = 0; i < messagePartElement.childNodes.length; ++i) {
        if (messagePartElement.childNodes[i].nodeType === 1) {
            switch (messagePartElement.childNodes[i].tagName) {
            case "add-pane":
                EPStackedPaneEx.MessageProcessor.processAddPane(messagePartElement.childNodes[i]);
                break;
            case "dispose":
                EPStackedPaneEx.MessageProcessor.processDispose(messagePartElement.childNodes[i]);
                break;
            case "init":
                EPStackedPaneEx.MessageProcessor.processInit(messagePartElement.childNodes[i]);
                break;
            case "remove-pane":
                EPStackedPaneEx.MessageProcessor.processRemovePane(messagePartElement.childNodes[i]);
                break;
            case "set-active-pane":
                EPStackedPaneEx.MessageProcessor.processSetActivePane(messagePartElement.childNodes[i]);
                break;
            }
        }
    }
};

/**
 * Processes an <code>add-pane</code> message to add a new pane to the StackedPaneEx.
 *
 * @param addPaneMessageElement the <code>add-pane</code> element to process
 */
EPStackedPaneEx.MessageProcessor.processAddPane = function(addPaneMessageElement) {
    var elementId = addPaneMessageElement.getAttribute("eid");
    var stackedPane = EPStackedPaneEx.getComponent(elementId);
    if (!stackedPane) {
        throw "StackedPaneEx not found with id: " + elementId;
    }
    
    var paneId = addPaneMessageElement.getAttribute("pane-id");
    var paneIndex = addPaneMessageElement.getAttribute("pane-index");
    var rendered = addPaneMessageElement.getAttribute("rendered") == "true";

    var pane = new EPStackedPaneEx.Pane(paneId, rendered);
    stackedPane.addPane(pane, paneIndex);
};

/**
 * Processes an <code>dispose</code> message to dispose the state of a 
 * StackedPaneEx component that is being removed.
 *
 * @param disposeMessageElement the <code>dispose</code> element to process
 */
EPStackedPaneEx.MessageProcessor.processDispose = function(disposeMessageElement) {
    var elementId = disposeMessageElement.getAttribute("eid");
    var stackedPane = EPStackedPaneEx.getComponent(elementId);
    if (stackedPane) {
        stackedPane.dispose();
    }
};

/**
 * Processes an <code>init</code> message to initialize the state of a 
 * StackedPaneEx component that is being added.
 *
 * @param initMessageElement the <code>init</code> element to process
 */
EPStackedPaneEx.MessageProcessor.processInit = function(initMessageElement) {
    var elementId = initMessageElement.getAttribute("eid");
    var containerElementId = initMessageElement.getAttribute("container-eid");
    var activePaneId = initMessageElement.getAttribute("active-pane");
    var stackedPane = new EPStackedPaneEx(elementId, containerElementId, activePaneId);

    stackedPane.enabled = initMessageElement.getAttribute("enabled") != "false";
    stackedPane.styleText = initMessageElement.getAttribute("style");
    stackedPane.standardWebSupportE = initMessageElement.getElementsByTagName('standardWebSupport')[0];
    
    stackedPane.create();
};

/**
 * Processes a <code>remove-pane</code> message to remove a pane from the StackedPaneEx.
 * 
 * @param removePaneMessageElement the <code>remove-pane</code> element to process
 */
EPStackedPaneEx.MessageProcessor.processRemovePane = function(removePaneMessageElement) {
    var elementId = removePaneMessageElement.getAttribute("eid");
    var paneId = removePaneMessageElement.getAttribute("pane-id");
    var stackedPane = EPStackedPaneEx.getComponent(elementId);
    var pane = stackedPane.getPaneById(paneId);
    if (pane) {
        stackedPane.removePane(pane);
    }
};

/**
 * Processes a <code>set-active-pane</code> message to set the active pane.
 * 
 * @param setActivePaneMessageElement the <code>set-active-pane</code> element to process
 */
EPStackedPaneEx.MessageProcessor.processSetActivePane = function(setActivePaneMessageElement) {
    var stackedPaneId = setActivePaneMessageElement.getAttribute("eid");
    var paneId = setActivePaneMessageElement.getAttribute("active-pane");
    var stackedPane = EPStackedPaneEx.getComponent(stackedPaneId);
    if (paneId != null) {
	    stackedPane.getPaneById(paneId).rendered = true;
    }
    stackedPane.selectPane(paneId);
};
