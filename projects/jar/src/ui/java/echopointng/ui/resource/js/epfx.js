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

/**
 * Use an EP.Fx to run a`visual effect that the runs for a given duration, with a specified
 * number of frames per second.  
 * 
 * You need to give the EP.FX 1 or more Tween objects, that is any JS object that implements the following interface
 * 
 * tween = {
 * 
 * 		onFrame : function(value, tween, fx) {
 * 			// do something for each frame and value
 * 		},
 * 
 * 		// optional properties
 * 
 * 		transition : any one of the Ep.Fx.Transitions functions,
 * 
 * 
 * 		// optional methods
 * 		onStart : function(value, tween, fx) {
 * 
 * 		},
 * 
 * 		onEnd : function(value, tween, fx) {
 * 
 * 		}
 * };
 * 
 * The EP.Fx using Tweening functions to determine value over time.  The tween object should
 * perform the actual visual effect inside the onFrame() method.  For example it may set
 * the x position of a div or change opacity of an element....
 * 
 * You start the Ep.Fx via the start() method
 * 
 * NOTE : the value calculated is a floating point number and
 * hence may need to have Math.round() applied to make it an integer.
 * 
 * The options can be 
 * options = {
 * 		duration:		500,							// duration in ms
 * 		fps:			50,								// more fps means a smoother look
 * 		transition:		EP.Tween.Transitions.sineOut,	// which default easing transition to use
 * }
 *
 * The following properties are made available to you in the Tween object automatically
 * 
 * 	tween.fromValue		- where we started from
 * 	tween.toValue 		- where we are going to
 * 	tween.currentValue	- the current value computed based on how much time has passed.
 * 
 */ 
EP.Fx = function(options) {
	this.initialise(options);
};

EP.Fx.prototype = {
	initialise: function(options) {
		if (! options) 
			options = {};
		if (! options.transition)
			options.transition = EP.Fx.Transitions.sineOut;
		if (! options.duration)
			options.duration = 500; // ms
		if (! options.fps)
			options.fps = 50;
			
		this.options = options;
		
		this.tweenArr = [];
	},
	
	/**
	 * Called to add a Tween implementing object to the Fx.
	 */
	addTween: function(tween, fromValue, toValue) {
		if (tween == null || typeof(tween.onFrame) != 'function') {
			throw 'You must provide a Tween object that at least implements onFrame()';
		}
		if (isNaN(parseFloat(fromValue))) {
			throw new 'You must provide a numerical value for fromValue';
		}
		if (isNaN(parseFloat(toValue))) {
			throw new 'You must provide a numerical value for toValue';
		}
		tween.fromValue = fromValue;
		tween.toValue = toValue;
		tween.currentValue = fromValue;
		
		this.tweenArr.push(tween);
	},

	/**
	 * Called to start the EP.Fx to compute the movements for
	 * one or more tweens that have previously been added via addTween()
	 */
	start: function() {
		// in case we are already running
		if (this.timerId) {
			window.clearInterval(this.timerId);
			this.timerId = null;
		}
		for(var index=0; index<this.tweenArr.length; index++) {
			var tween = this.tweenArr[index];
			if (typeof(tween.onStart) == 'function') {
				tween.onStart(tween.currentValue,tween,this);
			}		
		}
		// start a timer
		this.startTime  = new Date().getTime();
		var that = this;
		var onframeclosure = function() {
			that._onFrameRender();
		}
		var ms = Math.round(1000 / this.options.fps);
		this.timerId = window.setInterval(onframeclosure,ms);
	},	
	
	/**
	 * This will stop the Ep.Fx from running.
	 */
	stop: function() {
		if (this.timerId) {
			window.clearInterval(this.timerId);
			this.timerId = null;
		}
		for(var index=0; index<this.tweenArr.length; index++) {
			var tween = this.tweenArr[index];
			if (typeof(tween.onStop) == 'function') {
				tween.onStop(tween.currentValue,tween,this);
			}		
		}
	},
	
	/*
	 * This is where are interval calls back to.  It must compute a new
	 * tween value based on the transition equation in play.  It must
	 * also detect when we have been running longer than the duration.
	 */
	_onFrameRender : function() {
		var stopEffect = true;
		var now = new Date().getTime();
		if (now < this.startTime + this.options.duration) {
			stopEffect = false;
		}
		var ctime = now - this.startTime;
		//
		// for every tween
		for(var index=0; index<this.tweenArr.length; index++) {
			var tween = this.tweenArr[index];
			if (stopEffect) {
				tween.currentValue = tween.toValue;
			} else {
				//
				// compute a new value via its transition function
				tween.currentValue = this._runEquation(ctime,tween);
			}
			// do the callback 
			tween.onFrame(tween.currentValue, tween, this);
		}
		if (stopEffect) {
			this.stop();
		}
	},

	_runEquation : function(ctime, tween) {
		var transition = tween.transition;
		if (! transition) {
			transition = this.options.transition;
		}
		var t = ctime;
		var b = tween.fromValue;
		var c = tween.toValue - tween.fromValue;
		var d = this.options.duration;
		//
		// do the actual easing equation
		return transition(t,b,c,d);
	}

};

/**
 * @param htmlE - the html element in question
 * @param cssPropertyName - a css property style name like 'left' or 'opacity'
 * @param units - optional - something like 'px' or 'em'
 * @transition - an optional Ep.Fx.Transitions easing function
 */
EP.Fx.CssTween = function(htmlE, cssPropertyName, units, transition) {
	this.initialise(htmlE, cssPropertyName, units, transition);
}
EP.Fx.CssTween.prototype  = {
	
	initialise: function(htmlE, cssPropertyName, units, transition) {
		this.htmlE = htmlE;
		this.cssPropertyName = cssPropertyName;
		this.units = units;
		// optional
		this.transition = transition;
	},
	
	onFrame : function(value, tween, fx) {
		var styleObj = this.htmlE.style;
		var propertyValue = '' + value;
		if (this.units) {
			propertyValue += this.units;
		}
		styleObj[this.cssPropertyName] = propertyValue;
	},
	
	onStart : function(value, tween, fx) {
		this.onFrame(value, tween, fx);
	},
	
	onStop : function(value, tween, fx) {
		this.onFrame(value, tween, fx);
	}
};

/*
 * This code was taken from the MooTools code set, whih in turn was based
 * on work done by Robert Pennning.
 * 
 * Author:
 * 	Robert Penner, <http://www.robertpenner.com/easing/>, modified to be used with mootools.
 * 
 * License:
 * 	Easing Equations v1.5, (c) 2003 Robert Penner, all rights reserved. Open Source BSD License.
*/
EP.Fx.Transitions = {

	/* Property: linear */
	linear: function(t, b, c, d){
		return c*t/d + b;
	},

	/* Property: quadIn */
	quadIn: function(t, b, c, d){
		return c*(t/=d)*t + b;
	},

	/* Property: quadOut */
	quadOut: function(t, b, c, d){
		return -c *(t/=d)*(t-2) + b;
	},

	/* Property: quadInOut */
	quadInOut: function(t, b, c, d){
		if ((t/=d/2) < 1) return c/2*t*t + b;
		return -c/2 * ((--t)*(t-2) - 1) + b;
	},

	/* Property: cubicIn */
	cubicIn: function(t, b, c, d){
		return c*(t/=d)*t*t + b;
	},

	/* Property: cubicOut */
	cubicOut: function(t, b, c, d){
		return c*((t=t/d-1)*t*t + 1) + b;
	},

	/* Property: cubicInOut */
	cubicInOut: function(t, b, c, d){
		if ((t/=d/2) < 1) return c/2*t*t*t + b;
		return c/2*((t-=2)*t*t + 2) + b;
	},

	/* Property: quartIn */
	quartIn: function(t, b, c, d){
		return c*(t/=d)*t*t*t + b;
	},

	/* Property: quartOut */
	quartOut: function(t, b, c, d){
		return -c * ((t=t/d-1)*t*t*t - 1) + b;
	},

	/* Property: quartInOut */
	quartInOut: function(t, b, c, d){
		if ((t/=d/2) < 1) return c/2*t*t*t*t + b;
		return -c/2 * ((t-=2)*t*t*t - 2) + b;
	},

	/* Property: quintIn */
	quintIn: function(t, b, c, d){
		return c*(t/=d)*t*t*t*t + b;
	},

	/* Property: quintOut */
	quintOut: function(t, b, c, d){
		return c*((t=t/d-1)*t*t*t*t + 1) + b;
	},

	/* Property: quintInOut */
	quintInOut: function(t, b, c, d){
		if ((t/=d/2) < 1) return c/2*t*t*t*t*t + b;
		return c/2*((t-=2)*t*t*t*t + 2) + b;
	},

	/* Property: sineIn */
	sineIn: function(t, b, c, d){
		return -c * Math.cos(t/d * (Math.PI/2)) + c + b;
	},

	/* Property: sineOut */
	sineOut: function(t, b, c, d){
		return c * Math.sin(t/d * (Math.PI/2)) + b;
	},

	/* Property: sineInOut */
	sineInOut: function(t, b, c, d){
		return -c/2 * (Math.cos(Math.PI*t/d) - 1) + b;
	},

	/* Property: expoIn */
	expoIn: function(t, b, c, d){
		return (t==0) ? b : c * Math.pow(2, 10 * (t/d - 1)) + b;
	},

	/* Property: expoOut */
	expoOut: function(t, b, c, d){
		return (t==d) ? b+c : c * (-Math.pow(2, -10 * t/d) + 1) + b;
	},

	/* Property: expoInOut */
	expoInOut: function(t, b, c, d){
		if (t==0) return b;
		if (t==d) return b+c;
		if ((t/=d/2) < 1) return c/2 * Math.pow(2, 10 * (t - 1)) + b;
		return c/2 * (-Math.pow(2, -10 * --t) + 2) + b;
	},

	/* Property: circIn */
	circIn: function(t, b, c, d){
		return -c * (Math.sqrt(1 - (t/=d)*t) - 1) + b;
	},

	/* Property: circOut */
	circOut: function(t, b, c, d){
		return c * Math.sqrt(1 - (t=t/d-1)*t) + b;
	},

	/* Property: circInOut */
	circInOut: function(t, b, c, d){
		if ((t/=d/2) < 1) return -c/2 * (Math.sqrt(1 - t*t) - 1) + b;
		return c/2 * (Math.sqrt(1 - (t-=2)*t) + 1) + b;
	},

	/* Property: elasticIn */
	elasticIn: function(t, b, c, d, a, p){
		if (t==0) return b; if ((t/=d)==1) return b+c; if (!p) p=d*.3; if (!a) a = 1;
		if (a < Math.abs(c)){ a=c; var s=p/4; }
		else var s = p/(2*Math.PI) * Math.asin(c/a);
		return -(a*Math.pow(2,10*(t-=1)) * Math.sin( (t*d-s)*(2*Math.PI)/p )) + b;
	},

	/* Property: elasticOut */
	elasticOut: function(t, b, c, d, a, p){
		if (t==0) return b; if ((t/=d)==1) return b+c; if (!p) p=d*.3; if (!a) a = 1;
		if (a < Math.abs(c)){ a=c; var s=p/4; }
		else var s = p/(2*Math.PI) * Math.asin(c/a);
		return a*Math.pow(2,-10*t) * Math.sin( (t*d-s)*(2*Math.PI)/p ) + c + b;
	},

	/* Property: elasticInOut */
	elasticInOut: function(t, b, c, d, a, p){
		if (t==0) return b; if ((t/=d/2)==2) return b+c; if (!p) p=d*(.3*1.5); if (!a) a = 1;
		if (a < Math.abs(c)){ a=c; var s=p/4; }
		else var s = p/(2*Math.PI) * Math.asin(c/a);
		if (t < 1) return -.5*(a*Math.pow(2,10*(t-=1)) * Math.sin( (t*d-s)*(2*Math.PI)/p )) + b;
		return a*Math.pow(2,-10*(t-=1)) * Math.sin( (t*d-s)*(2*Math.PI)/p )*.5 + c + b;
	},

	/* Property: backIn */
	backIn: function(t, b, c, d, s){
		if (!s) s = 1.70158;
		return c*(t/=d)*t*((s+1)*t - s) + b;
	},

	/* Property: backOut */
	backOut: function(t, b, c, d, s){
		if (!s) s = 1.70158;
		return c*((t=t/d-1)*t*((s+1)*t + s) + 1) + b;
	},

	/* Property: backInOut */
	backInOut: function(t, b, c, d, s){
		if (!s) s = 1.70158;
		if ((t/=d/2) < 1) return c/2*(t*t*(((s*=(1.525))+1)*t - s)) + b;
		return c/2*((t-=2)*t*(((s*=(1.525))+1)*t + s) + 2) + b;
	},

	/* Property: bounceIn */
	bounceIn: function(t, b, c, d){
		return c - EP.Fx.Transitions.bounceOut (d-t, 0, c, d) + b;
	},

	/* Property: bounceOut */
	bounceOut: function(t, b, c, d){
		if ((t/=d) < (1/2.75)){
			return c*(7.5625*t*t) + b;
		} else if (t < (2/2.75)){
			return c*(7.5625*(t-=(1.5/2.75))*t + .75) + b;
		} else if (t < (2.5/2.75)){
			return c*(7.5625*(t-=(2.25/2.75))*t + .9375) + b;
		} else {
			return c*(7.5625*(t-=(2.625/2.75))*t + .984375) + b;
		}
	},

	/* Property: bounceInOut */
	bounceInOut: function(t, b, c, d){
		if (t < d/2) return EP.Fx.Transitions.bounceIn(t*2, 0, c, d) * .5 + b;
		return EP.Fx.Transitions.bounceOut(t*2-d, 0, c, d) * .5 + c*.5 + b;
	}

};
 