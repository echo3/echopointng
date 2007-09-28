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

EP.Drag = function() {};

/**
 * The startDrag method is called to start the drag operation, typically in
 * response to a mousedown event on a certain HTML element.
 * 
 * Any previous drag operation will be ended and the onDragEnd() callback will
 * be invoked on the previous object.
 * 
 * The mouse will be captured until the mouseup event terminates the drag
 * operation.
 * 
 * An object is called back via a series of well know methods during the drag
 * operation if the callbackObj implements these methods.
 * 
 * The methods are as follows:
 * 
 * 	onDragStart(echoEvent,contextObj); 
 * 	onDragMouseMove(echoEvent,contextObj);
 * 	onDragMouseUp(echoEvent,contextObj); 
 * 	onDragEnd(echoEvent,contextObj);
 * 
 * A contextObject will be filled out with the following properties:
 * 
 * contextObj.htmlE			- the initial HTML Element
 * 
 * contextObj.eventStartX 	- the starting x position of the mouse in viewport terms
 * contextObj.eventStartY 	- the starting y position of the mouse in viewport terms
 *
 * contextObj.eventX 	 	- the current x position of the mouse in viewport terms
 * contextObj.eventY      	- the current y position of the mouse in viewport terms
 *
 * contextObj.eventDeltaX 	- the delta x which is eventX - eventStartX in viewport terms
 * contextObj.eventDeltaY 	- the delta y which is eventY - eventStartY in viewport terms
 *
 * contextObj.elementStartX - the starting x position of the element
 * contextObj.elementStartY - the starting y position of the element
 * 
  * 
 * @param echoEvent -
 *            the event that caused the drag start (typically mousedown)
 * @param htmlE -
 *            the html element from which to take "relative" drag positions from
 * @param obj -
 *            a JS object than will be called back with the various dragMethods
 * @param contextObj -
 *            an optional context object that is presented to the dragMethods.
 */
EP.Drag.startDragOperation = function(echoEvent, htmlE, callbackObj, contextObj) {
	var addHandlers = true;
	var dragInstance = EP.Drag.instance;
	if (dragInstance.callbackObj) {
		if (dragInstance.callbackObj.onDragEnd) {
			// tell the last guy that the drag is over
			dragInstance.callbackObj.onDragEnd(echoEvent,dragInstance.contextObj);
		}
		addHandlers = false;
	}
	if (! contextObj) {
		contextObj = {};
	}
	dragInstance.contextObj = contextObj;
	dragInstance.callbackObj = callbackObj;
	
	contextObj.eventStartX 	= echoEvent.clientX;
	contextObj.eventStartY 	= echoEvent.clientY;
	contextObj.eventX 		= echoEvent.clientX;
	contextObj.eventY 		= echoEvent.clientY;
	contextObj.eventDeltaX 	= 0;
	contextObj.eventDeltaY 	= 0;
	if (htmlE) {
		contextObj.elementStartX = EP.getX(htmlE);
		contextObj.elementStartY = EP.getY(htmlE);
		contextObj.htmlE = htmlE;
		htmlE.onDragStart = EP.Drag.dragStartEatHandler;
	}
	
	if (addHandlers) {
		EP.DocumentEvent.addHandler("mousemove","epdrag_mousemove",EP.Drag.dragOperationHandler);
		EP.DocumentEvent.addHandler("mouseup",  "epdrag_mouseup",  EP.Drag.dragOperationHandler);
	}
	
	if (dragInstance.callbackObj) {
		if (dragInstance.callbackObj.onDragStart) {
			dragInstance.callbackObj.onDragStart(echoEvent,dragInstance.contextObj);
		}
	}
	EP.Event.cancelEvent(echoEvent);
};

/**
 * This method is called stop the current drag operation and "clean up" any document 
 * listeners from the current drag operation.
 */
EP.Drag.endDragOperation = function(echoEvent) {
	var dragInstance = EP.Drag.instance;
	if (dragInstance.callbackObj) {
		if (dragInstance.callbackObj.onDragEnd) {
			// tell the last guy that the drag is over
			dragInstance.callbackObj.onDragEnd(echoEvent,dragInstance.contextObj);
		}
	}
	if (dragInstance.contextObj && dragInstance.contextObj.htmlE) {
		dragInstance.contextObj.htmlE.onDragStart = null;
		dragInstance.contextObj.htmlE = null;
	}
	dragInstance.callbackObj = null;
	dragInstance.contextObj = null;
	// clean up listener resources
	EP.DocumentEvent.removeHandler("mousemove","epdrag_mousemove");
	EP.DocumentEvent.removeHandler("mouseup","epdrag_mouseup");
};


/*
 * This PRIVATE method is the call back handler for the drag events
 */
EP.Drag.dragOperationHandler = function(echoEvent) {
	var dragInstance = EP.Drag.instance;
	if (! dragInstance.callbackObj) {
		EP.Drag.endDragOperation(echoEvent);
		return;
	}
	dragInstance.contextObj.eventX 		= echoEvent.clientX;
	dragInstance.contextObj.eventY 		= echoEvent.clientY;
	
	dragInstance.contextObj.eventDeltaX = echoEvent.clientX - dragInstance.contextObj.eventStartX;
	dragInstance.contextObj.eventDeltaY = echoEvent.clientY - dragInstance.contextObj.eventStartY;
	
	if (echoEvent.type == 'mousemove') {
		if (dragInstance.callbackObj.onDragMouseMove) {
			dragInstance.callbackObj.onDragMouseMove(echoEvent,dragInstance.contextObj);
		}
	}
	if (echoEvent.type == 'mouseup') {
		if (dragInstance.callbackObj.onDragMouseUp) {
			dragInstance.callbackObj.onDragMouseUp(echoEvent,dragInstance.contextObj);
		}
		// mouse up ends the drag operation and mouse capture
		EP.Drag.endDragOperation(echoEvent);
	}
	EP.Event.cancelEvent(echoEvent);
};

/**
 * This NOOP event handler prevents the browser onDrag event handlers
 * from kicking it.
 */
EP.Drag.dragStartEatHandler = function(echoEvent) {
	EP.Event.cancelEvent(echoEvent);
	return false;
};

EP.Drag.instance = new EP.Drag();


