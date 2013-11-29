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
 * Parts of this code where taken from the Yahoo YUI code set and hence
 * the BSD licence is stated below
 */

/*
Software License Agreement (BSD License)

Copyright (c) 2006, Yahoo! Inc.
All rights reserved.

Redistribution and use of this software in source and binary forms, with or without modification, are
permitted provided that the following conditions are met:

* Redistributions of source code must retain the above
  copyright notice, this list of conditions and the
  following disclaimer.

* Redistributions in binary form must reproduce the above
  copyright notice, this list of conditions and the
  following disclaimer in the documentation and/or other
  materials provided with the distribution.

* Neither the name of Yahoo! Inc. nor the names of its
  contributors may be used to endorse or promote products
  derived from this software without specific prior
  written permission of Yahoo! Inc.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

//- - - - - - - - - - - - - - - - - - - -
//EchoPoint DHTML manipulation code
//- - - - - - - - - - - - - - - - - - - -

/* do not instantiate */
EP = function() {};

/** alignment values taken from Alignment constants */

/**
 * Specifies default alignment.
 */
EP.DEFAULT = 0;

/**
 * Specifies leading alignment (left in LTR languages, right in RTL languages).
 */
EP.LEADING = 1;

/**
 * Specifies trailing alignment (right in LTR languages, left in RTL languages).
 */
EP.TRAILING  = 2;
/**
 * Specifies left alignment.
 */
EP.LEFT = 3;
/**
 * Specifies center alignment.
 */
EP.CENTER = 4;
/**
 * Specifies right alignment.
 */
EP.RIGHT = 5;
/**
 * Specifies top alignment.
 */
EP.TOP = 6;
/**
 * Specifies bottom alignment.
 */
EP.BOTTOM = 7;

/**
 * An orientation of horizontal
 */
EP.HORIZONTAL = 0;

/**
 * An orientation of vertical
 */
EP.VERTICAL = 4;
//- - - - - - - - - - - - - - - - - - - -
//EchoPoint Browser code
//- - - - - - - - - - - - - - - - - - - -

EP.userAgent = navigator.userAgent.toLowerCase();
EP.userAgentVersion = parseInt(navigator.appVersion,10);
EP.isDOM = (document.getElementById) ? true : false; // true for ie6, ns6, moz 1
EP.isNS6 = (EP.isDOM && navigator.appName == "Netscape");
EP.isGecko = EP.userAgent.indexOf('gecko') != -1;
EP.isOpera = EP.userAgent.indexOf('opera') != -1;
EP.isIE = (document.all);

/**
 * This is a short named method to write the Echo client side debug log. I
 * created this becase EP.debug() is easy to remeber than
 * EchoDebugManager.consoleWrite().
 */
EP.debug = function(debugStr) {
    EchoDebugManager.consoleWrite(debugStr);
    console.log(debugStr);
};

/*= FireBug Lite Lite ================================= */
if (!("console" in window) || !("firebug" in console))
{
    var names = ["log", "debug", "info", "warn", "error", "assert", "dir", "dirxml",
        "group", "groupEnd", "time", "timeEnd", "count", "trace", "profile", "profileEnd"];

    window.console = {};
    for (var i = 0; i < names.length; ++i)
        window.console[names[i]] = function() {}
}


/**
 * getParent - may return null or the parent Element
 */

EP.getParent = function(htmlE) {
    if (htmlE.parentNode) {
        return htmlE.parentNode;
    }
    if (htmlE.parentElement) {
        return htmlE.parentElement;
    }
    return null;
};

/**
 * Return true of the testAncestorOfE is a ancestor of the provided HTML element.
 */
EP.isAncestorOf = function(htmlE, testAncestorOfE) {
    if (testAncestorOfE && htmlE) {
        if (htmlE == testAncestorOfE) {
            return true;
        }

        var parentE = EP.getParent(htmlE);
        while(parentE) {
            if (parentE == testAncestorOfE) {
                return true;
            }
            parentE = EP.getParent(parentE);
        }
    }
    return false;
};

/**
 * Returns the HMTML tag of the HTML element.
 */
EP.getTag = function(htmlE) {
    if (htmlE.tagName) {
        return htmlE.tagName;
    }
    return "unknown";
};

/**
 * gets the X co-ordinate of the HTML Element
 */
EP.getX = function(htmlE) {
    var x = 0;
    if (EP.isOpera) {
        x = htmlE.style.pixelLeft;
    } else {
        x = htmlE.offsetLeft;
    }
    return x;
};

/**
 * Sets the X co-ordinate of the HTML element
 */
EP.setX = function(htmlE,x) {
    if (EP.isIE || EP.isDOM) {
        htmlE.style.left = ""+x+"px";
    } else {
        htmlE.style.pixelLeft = x;
    }
};

/**
 * Gets the Y co-ordinate of the HTML Element
 */
EP.getY = function(htmlE) {
    var y = 0;
    if (EP.isOpera) {
        y = htmlE.style.pixelTop;
    } else {
        y = htmlE.offsetTop;
    }
    return y;
};

/**
 * Sets the Y co-ordinate of the HTML Element
 */
EP.setY = function(htmlE,y) {
    if (EP.isIE || EP.isDOM) {
        htmlE.style.top = ""+y+"px";
    } else {
        htmlE.style.pixelTop = y;
    }
};

/**
 * Gets the Z co-ordinate of the HTML Element
 */
EP.getZ = function(htmlE) {
    var z = 0;
    if (htmlE.zIndex) {
        z = htmlE.zIndex;
    } else if (htmlE.style) {
        z = parseInt(htmlE.style.zIndex, 10);
    }
    if (isNaN(z)) {
        z = 0;
    }
    return z;
};

/**
 * Determines the Z co-ordinate of the HTML Element. if the z-index is not set
 * them it traverses up the parent tree and finds a component that does have a
 * z-index set
 */
EP.determineZ = function(htmlE) {
    var zIndex = 0;
    while (htmlE !== null) {
        var z = "";
        if (htmlE.zIndex) {
            z = htmlE.zIndex;
        } else if (htmlE.style && htmlE.style.zIndex) {
            z = htmlE.style.zIndex;
        }
        if (! isNaN(parseInt(z,10))) {
            zIndex = parseInt(z,10);
            break;
        }
        htmlE = EP.getParent(htmlE);
    }
    return zIndex;
};
/**
 * Sets the Z co-ordinate of the HTML Element
 */
EP.setZ = function(htmlE,z) {
    if (htmlE.zIndex) {
        htmlE.zIndex = z;
    } else if (htmlE.style) {
        htmlE.style.zIndex = z;
    }
};

/**
 * Gets the height of the HTML Element
 */
EP.getHeight = function(htmlE) {
    if (EP.isOpera) {
        return htmlE.style.pixelHeight;
    } else {
        return htmlE.offsetHeight;
    }
};

/**
 * Sets the height of the HTML Element
 */
EP.setHeight = function(htmlE,h) {
    if (EP.isOpera) {
        htmlE.style.pixelHeight = h;
    } else {
        htmlE.style.height = h;
    }
};

/**
 * Gets the width of the HTML Element
 */
EP.getWidth = function(htmlE) {
    if (EP.isOpera) {
        return htmlE.style.pixelWidth;
    } else {
        return htmlE.offsetWidth;
    }
};

/**
 * Sets the width of the HTML Element
 */
EP.setWidth = function(htmlE,w) {
    if (EP.isOpera) {
        htmlE.style.pixelWidth = w;
    } else {
        htmlE.style.width = w;
    }
};

/**
 * Returns the absolute position in page viewport terms of an element
 * including any scrolling that might be in effect.
 */
EP.getPageXY = function(htmlE) {
    var parent = null;
    var pos = [];
    var box;

    if (htmlE.getBoundingClientRect) { // IE
        box = htmlE.getBoundingClientRect();
        var scrollTop = document.documentElement.scrollTop || document.body.scrollTop;
        var scrollLeft = document.documentElement.scrollLeft || document.body.scrollLeft;

        return [box.left + scrollLeft, box.top + scrollTop];
    }
    else if (document.getBoxObjectFor) { // gecko
        box = document.getBoxObjectFor(htmlE);
        pos = [box.x, box.y];
    }
    else { // safari/opera
        pos = [htmlE.offsetLeft, htmlE.offsetTop];
        parentE = htmlE.offsetParent;
        if (parentE != htmlE) {
            while (parentE) {
                pos[0] += parentE.offsetLeft;
                pos[1] += parentE.offsetTop;
                parentE = parentE.offsetParent;
            }
        }

        // opera & (safari absolute) incorrectly account for body offsetTop
        var ua = navigator.userAgent.toLowerCase();
        var position = 'none';
        if (htmlE.style && htmlE.style.position) {
            position = htmlE.style.position;
        }
        if (ua.indexOf('opera') != -1 || ( ua.indexOf('safari') != -1 && htmlE) && position == 'absolute') {
            pos[1] -= document.body.offsetTop;
        }
    }

    if (htmlE.parentNode) {
        parentE = htmlE.parentNode;
    } else {
        parentE = null;
    }
    while (parentE && parentE.tagName != 'BODY' && parentE.tagName != 'HTML') {
        pos[0] -= parentE.scrollLeft;
        pos[1] -= parentE.scrollTop;
        if (parentE.parentNode) {
            parentE = parentE.parentNode;
        } else {
            parentE = null;
        }
    }
    return pos;
};

/**
 * Returns the X co-ordinate of the HTML Element with respect to the whole
 * browser page.
 */
EP.getPageX = function(htmlE) {
    var x = 0;
    var elem = htmlE;
    if (EP.isOpera) {
        while (elem) {
            x += elem.style.pixelLeft;
            elem = elem.offsetParent;
        }
        return x;
    } else {
        while (elem) {
            if (EP.isIE && (elem.style.position == "relative" || (elem.offsetParent && elem.offsetParent.style.position == "relative"))) {
                x += elem.offsetLeft / 2;
            } else {
                x += elem.offsetLeft;
            }
            elem = elem.offsetParent;
        }
        if (EP.isIE) {
            x += document.body.clientLeft;
        }
        return x;
    }
};
/**
 * Returns the Y co-ordinate of the HTML Element with respect to the whole
 * browser page.
 */
EP.getPageY = function(htmlE) {
    var y = 0;
    var elem = htmlE;
    if (EP.isOpera) {
        while (elem) {
            y += elem.style.pixelTop;
            elem = elem.offsetParent;
        }
        return y;
    } else {
        while (elem) {
            y += elem.offsetTop;
            elem = elem.offsetParent;
        }
        if (EP.isIE) {
            y += document.body.clientTop;
        }
        return y;
    }
};

/**
 * This returns an array of 4 values, the current page dimensions and the current
 * window dimensions.
 */
EP.getPageDimensions = function() {
    /**
     * Code taken from :
     *
     * Lightbox JS: Fullsize Image Overlays
     * by Lokesh Dhakar - http://www.huddletogether.com
     * Licensed under the Creative Commons Attribution 2.5 License - http://creativecommons.org/licenses/by/2.5/
     */
    var xScroll, yScroll, windowWidth, windowHeight, pageWidth, pageHeight;
    if (window.innerHeight && window.scrollMaxY) {
        xScroll = document.body.scrollWidth;
        yScroll = window.innerHeight + window.scrollMaxY;
    } else if (document.body.scrollHeight > document.body.offsetHeight){ // all but Explorer Mac
        xScroll = document.body.scrollWidth;
        yScroll = document.body.scrollHeight;
    } else { // Explorer Mac...would also work in Explorer 6 Strict, Mozilla and Safari
        xScroll = document.body.offsetWidth;
        yScroll = document.body.offsetHeight;
    }

    if (self.innerHeight) {	// all except Explorer
        windowWidth = self.innerWidth;
        windowHeight = self.innerHeight;
    } else if (document.documentElement && document.documentElement.clientHeight) { // Explorer 6 Strict Mode
        windowWidth = document.documentElement.clientWidth;
        windowHeight = document.documentElement.clientHeight;
    } else if (document.body) { // other Explorers
        windowWidth = document.body.clientWidth;
        windowHeight = document.body.clientHeight;
    }

    // for small pages with total height less then height of the viewport
    if(yScroll < windowHeight){
        pageHeight = windowHeight;
    } else {
        pageHeight = yScroll;
    }

    // for small pages with total width less then width of the viewport
    if(xScroll < windowWidth){
        pageWidth = windowWidth;
    } else {
        pageWidth = xScroll;
    }
    return [pageWidth,pageHeight,windowWidth,windowHeight];
};


/**
 * Returns how far left the HTML Element as been scrolled.
 */
EP.getScrollX = function(htmlE) {
    if (htmlE.scrollLeft) {
        return htmlE.scrollLeft;
    }
    return 0;
};

/**
 * Returns how far down the HTML Element as been scrolled.
 */
EP.getScrollY = function(htmlE) {
    if (htmlE.scrollTop) {
        return htmlE.scrollTop;
    }
    return 0;
};

/**
 * Calculates all scrollX values from the HTML element upwards.
 */
EP.calcScrollX = function(htmlE) {
    var scroll = 0;
    var parentE = EP.getParent(htmlE);
    while (parentE) {
        if (EP.getTag(parentE) == "body") {
            break;
        }
        scroll += EP.getScrollX(parentE);
        parentE = EP.getParent(parentE);
    }
    return scroll;
};

/**
 * Calculates all scrollY values from the HTML element upwards
 */
EP.calcScrollY = function(htmlE) {
    var scroll = 0;
    var parentE = EP.getParent(htmlE);
    while (parentE) {
        if (EP.getTag(parentE) == "body") {
            break;
        }
        scroll += EP.getScrollY(parentE);
        parentE = EP.getParent(parentE);
    }
    return scroll;
};

/**
 * Moves the HTML Element to the specified X and Y co-ordinate.
 */
EP.moveTo = function(htmlE,x, y) {
    EP.setX(htmlE,x);
    EP.setY(htmlE,y);
};

/**
 * Moves the HTML Element from its current position by the specified delta
 * values.
 */
EP.moveBy = function(htmlE,deltaX, deltaY) {
    EP.setX(htmlE,EP.getX(htmlE) + deltaX);
    EP.setY(htmlE,EP.getY(htmlE) + deltaY);
};


/**
 * Returns true if the HTML Element is currently visible
 */
EP.isVisible = function(htmlE) {
    if (htmlE.style) {
        if (htmlE.style.visibility == 'hidden') {
            return false;
        }
    }
    return true;
};

/**
 * Returns true if the htmlE and its parent components are all visible
 */
EP.isHierarchyVisible = function(htmlE) {
    if (! EP.isVisible(htmlE)) {
        return false;
    }
    var parentE = EP.getParent(htmlE);
    while(parentE) {
        if (! EP.isVisible(parentE)) {
            return false;
        }
        parentE = EP.getParent(parentE);
    }
    return true;
}

/**
 * Makes the HTML Element visible or not, depending on whether isVisible is set
 * to true or not.
 */
EP.setVisible = function(htmlE,isVisible) {
    if (htmlE.style) {
        if (isVisible) {
            htmlE.style.visibility = 'visible';
        } else {
            htmlE.style.visibility = 'hidden';
        }
    }
};

/**
 * Returns true if the specified HTML Element is currently displayed and hence
 * takes up some space.
 */
EP.isDisplayed = function(htmlE) {
    if (htmlE.style) {
        if (htmlE.style.display == 'none') {
            return false;
        }
    }
    return true;
};

/**
 * Returns true if the specified HTML Element and its parent elements is currently displayed and hence
 * takes up some space.
 */
EP.isHierarchyDisplayed = function(htmlE) {
    if (! EP.isDisplayed(htmlE)) {
        return false;
    }
    var parentE = EP.getParent(htmlE);
    while(parentE) {
        if (! EP.isDisplayed(parentE)) {
            return false;
        }
        parentE = EP.getParent(parentE);
    }
    return true;
}

/**
 * Sets whether the HTML Element is displayed or no. HTML Elements that are not
 * displayed take up no space.
 */
EP.setDisplayed = function(htmlE,isDisplayed) {
    if (htmlE.style) {
        if (isDisplayed){
            htmlE.style.display = '';
        } else {
            htmlE.style.display = 'none';
        }
    }
};

/**
 * Returns true if the HTML Element is positioned absolutely.
 */
EP.isAbsolute = function(htmlE) {
    if (htmlE.style) {
        if (htmlE.style.position == 'absolute') {
            return true;
        }
    }
    return false;
};

/**
 * Returns true if the HTML Element is positioned relatively.
 */
EP.isRelative = function(htmlE) {
    if (htmlE.style) {
        if (htmlE.style.position == 'relative') {
            return true;
        }
    }
    return false;
};

/**
 * Returns the width of the browser page
 */
EP.getPageWidth = function() {
    if (EP.Opera) {
        return frame.innerWidth;
    } else {
        return document.body.clientWidth;
    }
};

/**
 * Returns the height of the browser page
 */
EP.getPageHeight = function() {
    if (EP.Opera) {
        return frame.innerHeight;
    } else {
        return document.body.clientHeight;
    }
};

/**
 * Swaps the source of an IMG tag to the newIconUri
 */
EP.imageSwap = function(imageTagId, newIconUri) {
    var iconElement = document.getElementById(imageTagId);
    if (iconElement) {
        iconElement.src = (newIconUri ? newIconUri : "");
    }
};

/**
 * Forces quirky IE clients to repaint immediately for a sometimes very
 * signficant aesthetic performance improvement.
 *
 * Invoking this method performs no operation/has no effect for other browser
 * clients that do not suffer this quirk.
 *
 * @param the
 *            element to force repaint
 */
EP.forceRepaint = function(htmlE) {
    if (EchoClientProperties.get("quirkIERepaint")) {
        var originalWidth = htmlE.style.width;
        var temporaryWidth = parseInt(htmlE.clientWidth,10) + 1;
        htmlE.style.width = temporaryWidth + "px";
        htmlE.style.width = originalWidth;
    }
};


/**
 * Applies a style to the Element by restoring the original style and then
 * applying the new one if its not null. To go back to the original style,
 * simply provide a null newStyle.
 * <p>
 * This is really designed for rollovers, where there is a base style and then a
 * rollover style and you will want to quickly go back to the base (original)
 * style, say on mouseout.
 */
EP.applyStyle = function(htmlE, newStyle) {
    EchoCssUtil.restoreOriginalStyle(htmlE);
    if (newStyle) {
        EchoCssUtil.applyTemporaryStyle(htmlE, newStyle);
    }
    EP.forceRepaint(htmlE);
};

/**
 * There seems to be a small quirk in FireFox 1.0.7 such that the box is painted
 * at some smaller size even though is reported with the given width.
 *
 * So lets force it and return the width.
 */
EP.relativeBoxQuirk = function(boxE, boxWidth) {
    if (boxE.geckoWidthChangeDone) {
        return boxWidth;
    }
    if (EP.userAgent.indexOf('firefox') != -1 && EP.userAgent.indexOf('1.0') != -1) {
        boxE.geckoWidthChangeDone = true;
        var saveParentE = boxE.parentNode;
        // appending as a child of body and then getting the
        // width does the trick
        document.body.appendChild(boxE);
        var targetBoxWidth = EP.getWidth(boxE);
        EP.setWidth(boxE,targetBoxWidth + "px");
        saveParentE.appendChild(boxE);
        var actualWidth = EP.getWidth(boxE);
        var adjustment = actualWidth - targetBoxWidth;
        boxWidth = targetBoxWidth - adjustment;

        EP.setWidth(boxE,boxWidth + "px");
    }
    return boxWidth;
};


//========================================================================================================
// Object map support. For the storage of JS objects via elementId
//========================================================================================================
EP.ObjectMap = function() {};

EP.ObjectMap.objectMap = [];

/**
 * This will retrieve an object from the global object map with the specified
 * elementId, or null if it cant be found.
 */
EP.ObjectMap.get = function(elementId) {
    var obj = EP.ObjectMap.objectMap[elementId];
    if (obj === null) {
        return null;
    }
    return obj;
};

/**
 * This will place an object into the global map with the specified elementId
 */
EP.ObjectMap.put = function(elementId, object) {
    EP.ObjectMap.objectMap[elementId] = object;
};

/**
 *
 * This will delete an object from the map as well as delete the object in
 * question. If the object has a method called "destroy()", then it will be
 * called as a destructor on the object, allowing it to cleanup resources before
 * the object itself is deleted via the "delete" operator.
 *
 */
EP.ObjectMap.destroy = function(elementId) {
    var obj = EP.ObjectMap.objectMap[elementId];
    if (obj) {
        if (obj.destroy) {
            obj.destroy();
        }
        delete EP.ObjectMap.objectMap[elementId];
        delete obj;
    }
};



//---------------------------------------------------------
// These are the event handlers provided by default with EchoPoint
//---------------------------------------------------------
EP.Event = function() {};

/**
 * This handler allows for server interactions from href="" attributes. There is
 * no need for a dynamic event handler as this is standard javascript call.
 * <p>
 * You would use it something like :
 *
 * <pre>
 * href = &quot;javascript:EP.Event.hrefActionHandler('c_12','click','val1');&quot;
 * </pre>
 */
EP.Event.hrefActionHandler = function(elementId,actionName,actionValue) {
    if (! EchoClientEngine.verifyInput(elementId)) {
        return;
    }
    if (document.selection && document.selection.empty) {
        document.selection.empty();
    }
    actionValue = (actionValue) ? actionValue : "";
    EchoClientMessage.setActionValue(elementId, actionName,actionValue);
    EchoServerTransaction.connect();
};

/**
 * This event handler assumes that the id of the target element is encoded as
 * follows :
 *
 * <pre>
 *
 *
 *   		componentId [ '|' actionName ['|' actionValue ] ]
 *
 *
 * </pre>
 *
 * where the component id is the first part, which is required to find return a
 * value to the component, followed by an optional actionName and actionValue.
 * <p>
 * tihs encoding is required by the Echo handler framework can only call you
 * back with a Event object and it has not extra "context" information
 * available.
 * <p>
 * An example elementId is : 'c47|click|backwards'
 *
 */
EP.Event.doAction = function(echoEvent) {
    EP.Event.cancelEvent(echoEvent);
    if (document.selection && document.selection.empty) {
        document.selection.empty();
    }
    var eventTarget = echoEvent.registeredTarget;
    var idArray   = eventTarget.getAttribute("id").split("|");
    var componentId = idArray[0];
    var actionName = idArray[1] === null ? "" : idArray[1];
    var actionValue = idArray[2] === null ? "" : idArray[2];

    if (! EchoClientEngine.verifyInput(componentId)) {
        return;
    }

    EchoClientMessage.setActionValue(componentId, actionName,actionValue);
    EchoServerTransaction.connect();
};

/**
 * This event handler is for SELECT field. It uses the same id encoding scheme
 * as above. It finds out the selected index of the SELECT field and returns
 * that as actionValue
 */
EP.Event.doSelectIndexAction = function(echoEvent) {
    if (document.selection && document.selection.empty) {
        document.selection.empty();
    }
    var selectTarget = echoEvent.registeredTarget;
    var idArray      = selectTarget.getAttribute("id").split("|");
    var componentId  = idArray[0];
    var actionName   = idArray[1] === null ? "" : idArray[1];
    var actionValue  = "" + selectTarget.selectedIndex;

    if (! EchoClientEngine.verifyInput(componentId)) {
        return;
    }

    EchoClientMessage.setActionValue(componentId, actionName,actionValue);
    EchoServerTransaction.connect();
};

/**
 * This event handler is for SELECT field. It uses the same id encoding scheme
 * as above. It finds out the selected index of the SELECT field and returns
 * value of the selected option as the actionValue.
 */
EP.Event.doSelectValueAction = function(echoEvent) {
    if (document.selection && document.selection.empty) {
        document.selection.empty();
    }
    var selectTarget = echoEvent.registeredTarget;
    var idArray      = selectTarget.getAttribute("id").split("|");
    var componentId  = idArray[0];
    var actionName   = idArray[1] === null ? "" : idArray[1];

    var actionValue  = "" + selectTarget.options[selectTarget.selectedIndex].value;

    if (! EchoClientEngine.verifyInput(componentId)) {
        return;
    }

    EchoClientMessage.setActionValue(componentId, actionName,actionValue);
    EchoServerTransaction.connect();
};

/**
 * This can be called to "set a value" into the client message, (as opposed to
 * taking an action). For example you could store the contents of a text field
 * of select field here. This does not cause an immediate server interaction.
 */
EP.Event.setClientValue = function(elementId, propertyName, propertyValue) {
    var propertyElement = EchoClientMessage.createPropertyElement(elementId, propertyName);
    if (propertyElement.firstChild) {
        propertyElement.firstChild.nodeValue = propertyValue;
    } else {
        propertyElement.appendChild(EchoClientMessage.messageDocument.createTextNode(propertyValue));
    }
    EchoDebugManager.updateClientMessage();
};

/**
 * Cancels the entire event, by stoppping the default and the event propogation
 */
EP.Event.cancelEvent = function(echoEvent) {
    EchoDomUtil.preventEventDefault(echoEvent);
    EchoDomUtil.stopPropagation(echoEvent);
};

/**
 * This event handler will do simply rollovers by looking for styles in the DOM
 * property store called 'rolloverStyle' and 'rolloverIcon' and applying them on
 * mouseover. On mouseout, it looks for 'defaultIcon' to restore any icon back.
 */
EP.Event.doRollOver = function(echoEvent) {
    var htmlE = echoEvent.registeredTarget;
    var elementId = htmlE.getAttribute("id");
    var idArray = elementId.split("|");
    if (idArray.length > 0) {
        if (! EchoClientEngine.verifyInput(idArray[0])) {
            return;
        }
    } else if (! EchoClientEngine.verifyInput(elementId)) {
        return;
    }
    var newStyle, newIcon;
    if (echoEvent.type == 'mouseover') {
        newStyle = EchoDomPropertyStore.getPropertyValue(elementId, "rolloverStyle");
        newIcon = EchoDomPropertyStore.getPropertyValue(elementId, "rolloverIcon");
    } else {
        newIcon = EchoDomPropertyStore.getPropertyValue(elementId, "defaultIcon");
    }
    EP.applyStyle(htmlE);
    if (newStyle) {
        EP.applyStyle(htmlE, newStyle);
    }
    if (newIcon) {
        EP.imageSwap(elementId, newIcon);
    }
};

/**
 * Adds an event handler to the specified HTML element and allows callback to
 * the object's single event handler called 'eventHandler'. This allows call bck
 * into a JS object rather than a static method
 *
 * The object must have an event handler function in the form :
 * <pre>
 * obj.eventHandler(event)
 * </pre>
 *
 * The object must be inside the EP.ObjectMap and able to be found via
 * its element.id or its parent element.id upwards.
 */
EP.Event.addHandler = function(eventType, elem, obj) {
    if (! elem) {
        throw "EP.Event.addHandler : null element provided : " + eventType;
    }
    eventType = EP.Event.transmogifryEventType(eventType);

    var f = EP.Event.dispatchEventHandler;
    if (elem.addEventListener) { // MOZ
        elem.addEventListener(eventType, f, false);
    } else if (elem.attachEvent) { // IE
        elem.attachEvent("on" + eventType, f);
    }
};

/**
 * This removes an event handler that was previously added via
 * EP.Event.addHandler.
 */
EP.Event.removeHandler = function(eventType, elem) {
    if (! elem) {
        return; // nothing to detach
    }
    eventType = EP.Event.transmogifryEventType(eventType);
    var f = EP.Event.dispatchEventHandler;
    if (elem.removeEventListener) { // MOZ
        elem.removeEventListener(eventType,f,false);
    } else if (elem.detachEvent) { // IE
        elem.detachEvent("on" + eventType,f);
    }
};

/**
 * This is not designed to be called but rather process the initial event and
 * then dispatches it to the object that registered the event (it must be on the
 * EP.ObjectMap)
 *
 */
EP.Event.dispatchEventHandler = function(echoEvent) {
    echoEvent = EP.isIE ? window.event : echoEvent;

    echoEvent = EP.Event.transmogifryEvent(echoEvent);
    if (!echoEvent.target && echoEvent.srcElement) {
        echoEvent.target = echoEvent.srcElement;
    }
    var identifiedTarget = echoEvent.target;
    // workaround for FF2 and its broken scroll support
    if (echoEvent.currentTarget && echoEvent.type == 'scroll') {
        identifiedTarget = echoEvent.currentTarget;
    }
    // find the object via the id in the object map
    var obj = null;
    while (identifiedTarget) {
        if (identifiedTarget.id) {
            obj = EP.ObjectMap.get(identifiedTarget.id);
            if (obj) {
                echoEvent.registeredTarget = identifiedTarget;
                break;
            }
        }
        identifiedTarget = EP.getParent(identifiedTarget);
    }
    if (obj && obj.eventHandler) {
        obj.eventHandler(echoEvent);
    } else {
        // fail queitly in this case because the object might be being destroyed.
        // when an event occurred.
        EP.debug('EP.Event.dispatchEvent could not find an object in EP.ObjectMap to invoke!');
    }
};

/**
 * NOT designed to be called directly.  This implementaion function
 * will convert mouseover/mouseout events into mouseenter.mouseleave
 * events on IE
 */
EP.Event.transmogifryEventType = function(eventType) {
    //
    // optimisation for IE where mouseenter/mouseleave is much more efficient
    // then mouseout/mouseover.  But we hide the difference by making it appear
    // as mouseover/mouseout
    //
    // See http://forum.nextapp.com/forum/index.php?showtopic=1584&hl=
    //
    var mouseEnterLeaveSupport = EchoClientProperties.get("proprietaryEventMouseEnterLeaveSupported");
    if (mouseEnterLeaveSupport) {
        if (eventType.toLowerCase() == 'mouseover') {
            eventType = 'mouseenter';
        } else 	if (eventType.toLowerCase() == 'mouseout') {
            eventType = 'mouseleave';
        }
    }
    return eventType;
};

/**
 * See above.  This does the reverse - mouseenter ==> mouseover
 */
EP.Event.transmogifryEvent = function(echoEvent) {
    var mouseEnterLeaveSupport = EchoClientProperties.get("proprietaryEventMouseEnterLeaveSupported");
    if (mouseEnterLeaveSupport) {
        if (echoEvent.type == 'mouseenter') {
            echoEvent = document.createEventObject(echoEvent);
            echoEvent.type = 'mouseover';
        } else 	if (echoEvent.type == 'mouseleave') {
            echoEvent = document.createEventObject(echoEvent);
            echoEvent.type = 'mouseout';
        }
    }
    return echoEvent;
};


/**
 * This targets the echoEvent.target element as a starting point and navigates
 * up the element tree until it finds a element with an identifier.
 * <p>
 * This is useful inside a event handler where a child element is the target but
 * the parent element is the one with the attached event handler.
 */
EP.Event.findIdentifiedElement = function(echoEvent) {
    var identifiedTarget = echoEvent.target || echoEvent.srcElement;
    while (identifiedTarget) {
        if (identifiedTarget.id) {
            return identifiedTarget;
        }
        identifiedTarget = EP.getParent(identifiedTarget);
    }
    return null;
};


/**
 * This is a simple wrapper for the EchoEventProcessor code however
 * it performs the mouseenter/mouseleave transmografication
 * of the event type on the apprpriate platforms.
 *
 * The event handler code still needs to test for both mouseover/mouseenter
 * and mouseout/mouseleave conbinations as in
 *
 * if (echoEvent.type == 'mouseover' || echoEvent.type == 'mouseenter') {
 *    ...
 * }
 */
EP.Event.addStaticHandler = function(elementId, eventType, handler) {
    eventType = EP.Event.transmogifryEventType(eventType);
    EchoEventProcessor.addHandler(elementId, eventType, handler);
}

/**
 * @see EP.Event.addStaticHandler
 */
EP.Event.removeStaticHandler = function(elementId, eventType) {
    eventType = EP.Event.transmogifryEventType(eventType);
    EchoEventProcessor.removeHandler(elementId, eventType);
}


//- - - - - - - - - - - - - - - - - - - -
//EchoPoint Document Event Processor
//- - - - - - - - - - - - - - - - - - - -

/* Do not instantiate */
EP.DocumentEvent = function() {};

EP.DocumentEvent.eventTypeToHandlersMap = [];

/**
 * Adds an event handler for Document level DOM events. All the registered event
 * handlers are called in turn when an event, such as 'mousedown' or 'click',
 * happens on a document.
 * <p>
 * The fact that the event got to the Document object probably means no other
 * HTML elements where interested in the event. You can use Document level
 * events to find out about things like when the user "mouseover"'s away from
 * the current HTML element.
 */
EP.DocumentEvent.addHandler = function(eventType, handlerName, handler) {
    var targetE = document;
    if (EP.isGecko && eventType == 'resize') {
        targetE = window;
    }
    EchoDomUtil.addEventListener(targetE, eventType, EP.DocumentEvent.processEvent, false);

    var handlerMap = EP.DocumentEvent.eventTypeToHandlersMap[eventType];
    if (!handlerMap) {
        handlerMap = [];
        EP.DocumentEvent.eventTypeToHandlersMap[eventType] = handlerMap;
    }
    handlerMap[handlerName] = handler;
};

/**
 * Removes an event handler for Document level DOM events.
 */
EP.DocumentEvent.removeHandler = function(eventType, handlerName) {
    var targetE = document;
    if (EP.isGecko && eventType == 'resize') {
        targetE = window;
    }
    EchoDomUtil.removeEventListener(targetE, eventType, EP.DocumentEvent.processEvent, false);

    var handlerMap = EP.DocumentEvent.eventTypeToHandlersMap[eventType];
    if (!handlerMap) {
        return;
    }
    if (handlerMap[handlerName]) {
        delete handlerMap[handlerName];
    }
};

/**
 * Master event handler for all Document level DOM events. All the handlers that
 * have registered for the event are informed that it has happened.
 */
EP.DocumentEvent.processEvent = function(echoEvent) {
    echoEvent = echoEvent ? echoEvent : window.event;
    //
    // Do we stop propogation because we should be the last on the list?
    //EchoDomUtil.stopPropagation(echoEvent);
    var eventType = echoEvent.type;
    if (!echoEvent.target && echoEvent.srcElement) {
        echoEvent.target = echoEvent.srcElement;
    }

    var handlerMap = EP.DocumentEvent.eventTypeToHandlersMap[eventType];
    if (!handlerMap) {
        return;
    }

    for (handlerName in handlerMap) {
        var handlerFunc = handlerMap[handlerName];
        try {
            handlerFunc(echoEvent);
        } catch (ex) {
            throw "Document level handler: " + handlerName + " (" + ex + ")";
        }
    }
};

//- - - - - - - - - - - - - - - - - - - -
//EchoPoint StyleSheet support
//- - - - - - - - - - - - - - - - - - - -

/* Do not instantiate */
EP.StyleSheet = function() {};

/**
 * Gets all the inner text nodes and appends them together
 */
EP.StyleSheet.getElementText = function(sourceE) {
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

/**
 * Copies the named attribute from one source element to a target element
 */
EP.StyleSheet.copyAttribute = function(sourceE, targetE, attrName) {
    var attrVal = sourceE.getAttribute(attrName);
    if (attrVal) {
        targetE.setAttribute(attrName,attrVal);
    }
};

/**
 * Adds an external stylesheet link to the end of the HEAd section and marks it
 * as belong to the elementId of the component
 */
EP.StyleSheet.addLink = function(elementId, sourceLinkE, index) {
    var styleId = elementId + '|eptemplate|' + index;
    //
    // do we already have this stylesheet
    var headE = document.getElementsByTagName('head')[0];
    var targetLinkE = document.createElement('link');
    targetLinkE.title = styleId;

    EP.StyleSheet.copyAttribute(sourceLinkE,targetLinkE,'media');
    EP.StyleSheet.copyAttribute(sourceLinkE,targetLinkE,'href');
    EP.StyleSheet.copyAttribute(sourceLinkE,targetLinkE,'type');
    EP.StyleSheet.copyAttribute(sourceLinkE,targetLinkE,'rel');
    headE.appendChild(targetLinkE);
    //
    // Some times with Gecko, the 2nd time a link gets added
    // it starts off disabled. So we tweak it to make sure its not
    // disabled and hence in action.
    if (EP.isGecko) {
        targetLinkE.disabled = false;
    }
};

/**
 * Adds an inline style to the end of the HEAD section and marks it as belong to
 * the elementId of the component
 *
 */
EP.StyleSheet.addStyle = function(elementId, sourceStyleE, index) {
    var styleId = elementId + '|epstyle|' + index;
    var headE = document.getElementsByTagName('head')[0];
    var styleText = EP.StyleSheet.getElementText(sourceStyleE);

    var targetStyleE = document.createElement('style');
    EP.StyleSheet.copyAttribute(sourceStyleE,targetStyleE,'media');
    EP.StyleSheet.copyAttribute(sourceStyleE,targetStyleE,'type');

    if (document.createStyleSheet) {
        // IE
        targetStyleE.title = styleId;
        headE.appendChild(targetStyleE);
        //
        // once added into IE, its gets a stylesheet pointer which we must fill
        // with the style text.  style.innerHTML doesnt work in fact it throws an
        // exception.
        targetStyleE.styleSheet.cssText = styleText;
    } else {
        // w3c DOM
        var textNode = document.createTextNode(styleText);
        targetStyleE.appendChild(textNode);
        targetStyleE.title = styleId;
        headE.appendChild(targetStyleE);
        targetStyleE.disabled = false;
    }
};

/**
 * Removes all stylesheets that have been created for a element id.
 */
EP.StyleSheet.removeAllStyles = function(elementId) {
    if (document.styleSheets) {
        var sslen = document.styleSheets.length;
        for ( i = sslen-1; i >= 0; i--) {
            var styleSheet = document.styleSheets[i];
            if (styleSheet.title && styleSheet.title.indexOf(elementId) === 0) {
                if (styleSheet.owningElement) {
                    for (j = 0; j < styleSheet.rules.length; j++) {
                        styleSheet.removeRule(j);
                    }
                    // IE
                    styleSheet.owningElement.removeNode(true);
                } else if (styleSheet.ownerNode) {
                    // w3c
                    styleSheet.ownerNode.parentNode.removeChild(styleSheet.ownerNode);
                }
            }
        }
    }
    var title;
    var headE = document.getElementsByTagName('head')[0];
    // Opera does not have document.styleSheets
    var styles = headE.getElementsByTagName('style');
    for (var i = 0; i < styles.length; i++) {
        var styleE = styles[i];
        title = styleE.title;
        if (title && title.indexOf(elementId) === 0) {
            styleE.disabled = true;
            if (styleE.styleSheet) {
                // IE
                styleE.styleSheet.disabled = true;
                //styleE.styleSheet.cssText = '';
                for (j = 0; j < styleE.styleSheet.rules.length; j++) {
                    styleE.styleSheet.removeRule(j);
                }
                if (styleE.styleSheet.owningElement) {
                    styleE.styleSheet.owningElement.removeNode(true);
                } else {
                    styleE.parentNode.removeChild(styles[i]);
                }
            } else {
                styleE.parentNode.removeChild(styles[i]);
            }
        }
    }

    var links = headE.getElementsByTagName('link');
    for (i = 0; i < links.length; i++) {
        var linkE = links[i];
        title = linkE.title;
        if (title && title.indexOf(elementId) === 0) {
            linkE.disabled = true;
            if (linkE.styleSheet) {
                linkE.styleSheet.disabled = true;
                linkE.styleSheet.cssText = '';
            }
            linkE.parentNode.removeChild(links[i]);
        }
    }
};

/**
 * This can be called to work out the computed style of an element
 * which is the combination of its 'style' attribute and any CSS
 * stlles in place from style sheets and the like.
 */
EP.StyleSheet.getComputedStyle = function(htmlE) {
    if (htmlE.currentStyle) {
        return htmlE.currentStyle;
    }
    if (typeof(document.defaultView) == 'undefined' || document.defaultView === null) {
        return undefined;
    }
    var currentStyle = document.defaultView.getComputedStyle(htmlE,null);
    if (typeof(currentStyle) == 'undefined' || currentStyle === null) {
        return undefined;
    }
    return currentStyle;
};

//- - - - - - - - - - - - - - - - - - - -
//EchoPoint DOM functions
//- - - - - - - - - - - - - - - - - - - -

/* Do not instantiate */
EP.DOM = function() {};

/**
 * Copies all the attributes and child nodes from one node to another
 */
EP.DOM.copyNode = function(destNode, srcNode) {
    EP.DOM.copyNodeAttributes(destNode,srcNode);
    var len = srcNode.childNodes.length;
    for(var i=len-1; i>=0; i--) {
        var srcChildNode = srcNode.childNodes.item(i);
        // create it with the same name
        var destChildNode = null;
        if (srcChildNode.nodeType == 1 ) {
            /*ElementNode*/
            destChildNode = document.createElement(srcChildNode.tageName);

//		} else if (srcChildNode.nodeType == 2 ) {
//			/*AttrNode*/
//			destChildNode = document.createAttribute();
//			destChildNode.name = srcChildNode.name;
//			destChildNode.value = srcChildNode.value;
//
        } else if (srcChildNode.nodeType == 3) {
            /*TextNode*/
            destChildNode = document.createTextNode(srcChildNode.data);

        } else if (srcChildNode.nodeType == 4) {
            /*TextNode*/
            destChildNode = document.createCDATASection(srcChildNode.data);

        }
        if (destChildNode) {
            destNode.appendChild(destChildNode);
        }
    }
};

/**
 * Copies all the attributes from one node to another
 */
EP.DOM.copyNodeAttributes = function(destNode, srcNode) {
    var len = srcNode.attributes.length;
    for(var i=len-1; i>=0; i--) {
        var attr = srcNode.attributes.item(i);
        destNode.setAttribute(attr.name,attr.value);
    }
}
/**
 * Removes all the child nodes of parentNode
 */
EP.DOM.removeChildren = function(parentNode) {
    var len = parentNode.childNodes.length;
    for(var i=len-1; i>=0; i--) {
        var childNode = parentNode.childNodes.item(i);
        parentNode.removeChild(childNode);
    }
};

/**
 * Recursively searches for the first TextNode starting at parentNode including that node
 * itself.
 *
 * @return the first text node found or null if one is not present.
 */
EP.DOM.getFirstTextNode = function(parentNode) {
    if (parentNode.nodeType == 3) {
        return parentNode;
    }
    for(childNode = parentNode.firstChild; childNode; childNode = childNode.nextSibling) {
        if (childNode.nodeType == 3) {
            return childNode;
        }
        var textNode  = this.getFirstTextNode(childNode);
        if (textNode) {
            return textNode;
        }
    }
    return null;
};

/**
 * This is called to create a array Nodes that contains
 * the text with any newlines replaced with <br/> tags
 * if encodeFlag is set to true.
 *
 * @return an array of nodes
 */
EP.DOM.encodeNewlines = function(text, encodeFlag) {
    var nodeList = [];
    var length = text.length;
    var startIndex = 0;
    var index = 0;
    var sb = '';

    while (index < length) {
        switch (text.charAt(index)) {
            case '\r':
            case '\n':
                if (encodeFlag) {
                    if (startIndex < index) {
                        sb += text.substring(startIndex, index);
                    }
                    if (index < length - 1
                        && ((text.charAt(index) == '\r' && text.charAt(index + 1) == '\n') || (text.charCodeAt(index) == '\n' &&
                        text.charAt(index + 1) == '\r'))) {
                        ++index; // skip second character of newline sequence.
                    }
                    var node = document.createTextNode(sb);
                    nodeList[nodeList.length] = node;
                    node = document.createElement("br");
                    nodeList[nodeList.length] = node;
                    sb = ''
                    startIndex = index + 1;
                }
                break;
        }
        ++index;
    }
    if (startIndex < index) {
        sb = sb + text.substr(startIndex);
        var node = document.createTextNode(sb.toString());
        nodeList[nodeList.length] = node;
    }
    return nodeList;
};

/**
 * Returns the string value of an attribute of the DOM element or the default
 * if its got no value
 *
 */
EP.DOM.getAttr = function(elementE, attrName, defaultValue) {
    var value = elementE.getAttribute(attrName);
    return (value ? value : defaultValue);
}

/**
 * Returns the integer value of an attribute of the DOM element or the default
 * if its got no value
 */
EP.DOM.getIntAttr = function(elementE, attrName, defaultIntValue, base) {
    base  = (isNaN(parseInt(base)) ? 10 : parseInt(base));
    var value = parseInt(elementE.getAttribute(attrName),base);
    if (isNaN(parseInt(value))) {
        value = defaultIntValue;
    }
    return parseInt(value);
};

/**
 * Returns the boolean value of an attribute of the DOM element or the default
 * if its got no value
 */
EP.DOM.getBooleanAttr = function(elementE, attrName, defaultValue) {
    var value = elementE.getAttribute(attrName);
    if (value == null) {
        value = defaultValue;
    }
    return (''+value) == 'true';
};
/**
 * This will traverse UP the DOM tree looking for the element
 * with a non null value in the specified attribute
 */
EP.DOM.findElementWithAttr = function(elementE, attrName) {
    while (elementE) {
        var attrValue = elementE.getAttribute(attrName);
        if (attrValue) {
            return elementE;
        }
        elementE = EP.getParent(elementE);
    }
    return null;
};
