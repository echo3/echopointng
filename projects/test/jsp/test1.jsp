<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<xxxlink rel="stylesheet" type="text/css" href="file:///c:/temp/template/test1.css" />
<xxxlink rel="stylesheet" type="text/css" href="file:///c:/temp/template/test2.css" />
<link rel="stylesheet" type="text/css" href="css/test1.css" />
<link rel="stylesheet" type="text/css" href="css/test2.css" />

<style  type="text/css">
.redH1 { color : red }
</style>


<style  type="text/css">
.example { background-color : #FDFDEF; color : black; }
.blueTag { color : blue }
.darkgrayTag { color : darkgray }
.blackTag { color : black  }
.borderedTag { border : thin black solid; padding : 2px; margin 2px; background-color : #F0F8FF;}
</style>

</head>

<body>

<p class="borderedTag">This P tag contains markup to include
    JSP attributes. Inside your Echo2 application you can add named
    attributes that will be inserted into the JSP Request object.
	<br/><br/>
    The JSP markup is first compiled by the JSP engine of your
    application server and the XHTML result of that is then fed into
    the TemplatePanel XHTML compiler mechanism.
	<br/><br/>
    Remember that the result of the JSP page execution must evaluate 
	to a valid XHTML document.
	<br/><br/>

    <code class="example">&lt;%=request.getAttribute("attr1")
    %&gt;</code><br/>
    <br/>
    Here is the JSP attribute :
    <%=request.getAttribute("attr1") %>
</p>
<h1 class="redH1"> XHTML Template</h1>

<p class="blueTag">
This P tag has a class of "blueTag" and hence the style rules <code class="example">.blueTag { color : blue }</code>
will be applied to it.  The H1 tag above has class="redH1" and hence will have the style rules
<code>.redH1 { color : red }</code> applied to it.
</p>


<p class="darkgrayTag">
This P tag has a class of "darkgrayTag" and hence the style rules <code class="example">.darkgrayTag { color : darkgray }</code>
will be applied to it.  You must be careful when creating styles.  If you use unspecific ones
like <code class="example">div {color : red }</code> then you will turn all DIV tags red not just ones inside this template.
</p>

<p class="borderedTag">
This P tag contains markup for a named component called <b>"comp1"</b>.  The markup looks like this
<br/><br/>
<code class="example">
&lt;component name="comp1"/&gt;.  
</code>
<br/><br/>
This causes the component named "comp1" to be replaced with the associated
Echo2 component which was placed in the TemplatePanel via the <code>setNamedComponent()</code> method..  
<br/><br/>
Here is the component : <component name="comp1"/>
</p>

<p class="borderedTag blueTag">
This P tag contains markup for a named component called <b>"comp2"</b>.  The markup looks like this
<br/><br/>
<code class="example">
&lt;component name="comp2" style="foreground:red;background:white"/&gt;.  
</code>
<br/><br/>
This will cause the "foreground" and background" properties of the component to be set to red and white respectively.  Notice that the CSS syntax is Echo2 property names
not W3C CSS names such as "color" or "background-color".  The EchoPointNG CSS mechanism is used to set the
component properties once on template compilation.
<br/><br/>
The P tag also has a compound style applied to it of class="borderedTag blueTag".  
<br/><br/>
Here is the component : <component name="comp2" style="foreground:red;background:white"/>
</p>

<div class="borderedTag">
This DIV tag contains markup for a named component called <b>"comp3"</b>.  The markup looks like this
<br/><br/>
<code class="example">
&lt;component name="comp3"&gt;Some Text To Be Subsituted into comp3&lt;/component&gt;.  
</code>
<br/><br/>
Notice it has
text inside the &lt;component&gt; markup.  This can be replaced into the associated component if the following is 
true 
<ul>
<li>The TemplatePanel has isInvokeSetText() set to true</li>
<li>The substituted component has a <code>public void setText(String s)</code> method</li>
<li>The text between the  &lt;component&gt; markup has a length greater than 0</li>
</ul>
And here is the component : <component name="comp3">Text To Be Subsituted Into comp3</component>
</div>

<p class="borderedTag">
This P tag contains markup for a named component called <b>"comp4"</b>.  You can also use standard XHTML control tags such
as &lt;input&gt;, &lt;button&gt; and &lt;select&gt;.  This allows you to draw your templates as this might appear, for example <input type="text"/> and then have a component substituted into it (in this case a TextField component).
<br/><br/>
This is the markup <code class="example">&lt;input type="text" name="comp4" value="comp4 Text"/&gt;</code>
<br/><br/>
Notice also that the value="" attribute can be used to obtain the text to be set into the component, assuming it has a setText() method.

<br/><br/>
And here is the component : <input type="text" name="comp4" value="comp4 Text"/>
</p>


<p class="borderedTag">
This P tag contains markup for a named component called <b>"comp5"</b>.  It showns the &lt;button&gt; HTML tag in action.
<br/><br/>
This is the markup <code class="example">&lt;button name="comp5"&gt;comp5 Text&lt;/button&gt;</code>
<br/><br/>
And here is the component : <button name="comp5">comp5 Text</button>
</p>

<p class="borderedTag">
This P tag contains markup for a named component called <b>"comp6"</b>.  It showns the &lt;select&gt; HTML tag in action.
<br/><br/>
This is the markup <code class="example">&lt;select name="comp6"&gt;...&lt;/select&gt;</code> with the options ommitted. Note that the options
are not set into the component.
<br/><br/>
And here is the component : <select name="comp6"><option>Option 1</option><option>Option 2</option></select>
</p>


<p class="borderedTag">
This P tag contains markup for a named component called "xxxx".  However no component named "xxxx" has been
added to the template.  Therefore an error segment wiull be shown here.
<br/><br/>
This is controlled by the isExceptionOnFailure() property of TemplatePanel.  The default is for an error
segment to be shown.  If isExceptionOnFailure() is true, then a RuntimeException is thrown.
<br/><br/>
And here is the component : <component name="xxxx"/>
</p>


<p class="borderedTag">
This P tag contains markup for a  component that is not named.  Since it is not named then nothing
is substituted and hence nothing changes from a template point of view
<br/><br/>
And here is the component : <component name=""/>
</p>

<p>
The following P tag has class="externalClass" which is contained in the external CSS file called test1.css.  Its contents look like this :
<pre><code class="example">
.externalClass {
	color : orange;
	border : thin black solid;
}
</code></pre>
and hence it has orange text with a black border around it.  As you can see extenal stylesheets can also be handled.  

Be careful because a stylesheet loaded for a template actually affects all the components on the client browser.  You must make you rules very specific.

</p>

<p class="externalClass">
Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Etiam in orci vel quam pulvinar pellentesque. Morbi ullamcorper nisl ornare erat. Etiam laoreet, pede et vehicula sodales, nulla nulla commodo erat, eu gravida massa lectus in metus. Praesent lacinia massa luctus pede. Maecenas imperdiet. Aliquam erat volutpat. Suspendisse potenti. Sed mauris metus, ultrices vitae, tristique vitae, interdum in, enim. Sed non diam id purus ultrices placerat. Donec a augue sed metus volutpat aliquam. Curabitur varius nibh sit amet orci. Nam placerat dapibus augue.
</p>

<p>
The following P tag has class="externalClassDotted" which is contained in the external CSS file called test2.css.  Its contents look like this :
<pre><code class="example">
.externalClassDotted {
	color : pink;
	border : thin black dotted;
	font-weight : bold;
}
</code></pre>
and hence it has orange text with a black dotted around it and bolded text.  
</p>

<p class="externalClassDotted">
Sed a erat. Nullam arcu sem, pulvinar et, posuere ut, eleifend ut, lacus. Mauris a arcu. Praesent vitae massa. Pellentesque ut nulla eu ipsum bibendum imperdiet. Maecenas enim. Donec pulvinar. Etiam a sapien id odio auctor feugiat. Suspendisse urna enim, ullamcorper sit amet, convallis non, elementum nec, justo. Donec quis lacus. Quisque neque diam, sollicitudin ut, laoreet ut, varius et, dolor. Pellentesque nec lorem. Duis eget neque.
</p>

<p>
Pellentesque et orci. Vivamus in dui. Vestibulum luctus rutrum neque. In hac habitasse platea dictumst. Sed sagittis feugiat tortor. Etiam massa sapien, venenatis at, pretium nec, tempus vitae, libero. Aenean et turpis. Cras tincidunt est vel pede egestas gravida. Cras adipiscing lobortis pede. Morbi sed orci. Nulla dictum, quam in eleifend pulvinar, velit tortor viverra turpis, eu fringilla lacus leo non dolor. Nullam volutpat wisi ac neque eleifend consequat.
</p>

<p class="blueTag">
Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Integer vulputate magna lobortis libero. Mauris venenatis pretium turpis. Quisque sapien nisl, volutpat non, egestas eget, ultrices nec, nulla. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Quisque dignissim. Curabitur rhoncus, diam ut consectetuer egestas, elit tortor posuere odio, eget feugiat risus odio ac elit. Aliquam odio arcu, vulputate vel, sollicitudin eget, convallis et, orci. Suspendisse sollicitudin erat vel lacus. Aenean nisl. Etiam neque pede, commodo nec, facilisis ac, cursus non, odio. Ut condimentum semper purus. Sed vehicula ligula eu nibh. Nam vel sapien. Nulla venenatis risus sed lacus. Sed a nisl ut libero pharetra aliquet. Vestibulum lectus arcu, scelerisque a, tincidunt nec, molestie in, urna. Donec mollis porttitor tortor. Donec rutrum porta eros. Nulla suscipit mi sit amet enim.
</p>

<p class="darkgrayTag">
Sed egestas. Phasellus fringilla, wisi ut eleifend facilisis, massa lectus tincidunt quam, sit amet interdum augue ante nec pede. Vivamus vel sapien. Pellentesque iaculis commodo leo. Fusce laoreet ornare enim. Maecenas molestie sem sit amet mi. Praesent est. Cras vel erat. Phasellus pellentesque semper dui. Praesent a arcu. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos hymenaeos.
</p>


<p class="blueTag">
Integer pede. Fusce lacus dui, dapibus vitae, placerat non, tempus ac, mi. Duis turpis leo, adipiscing vitae, dapibus nec, dictum a, risus. In tempus wisi eu felis. Vivamus vehicula interdum ligula. Quisque ut tellus a mauris fringilla commodo. Maecenas congue dui gravida turpis. Aliquam dictum nunc sit amet nunc. Ut eu nunc non ante tincidunt sodales. Sed malesuada tellus et eros. Morbi aliquet enim a pede. Morbi sed nulla. Suspendisse felis sem, viverra fringilla, blandit vitae, scelerisque at, ante. Donec semper sagittis urna. Ut laoreet mi et ante. Etiam nunc. Praesent faucibus elementum nulla.
</p>

<p class="borderedTag">
Nulla tincidunt mauris quis tortor. Vivamus imperdiet eros eu est. Fusce sollicitudin, est vel aliquet pharetra, mauris erat pellentesque tortor, id elementum eros mauris ut massa. Fusce egestas. Cras nonummy arcu. Phasellus eu mi. Vivamus nec dolor. Duis lacinia. Suspendisse semper consectetuer sem. Suspendisse mollis velit vel nibh. Nam a leo. Duis vitae enim id purus tincidunt porta. Nunc eget elit ut ante ornare eleifend. Donec eget quam sit amet erat feugiat tincidunt. Mauris luctus eleifend lacus.

</p>

<p class="borderedTag">
Aliquam sed pede id justo egestas tempor. Sed sagittis. Sed mollis. Pellentesque vel wisi. Curabitur turpis urna, cursus et, blandit nec, fringilla vitae, nibh. Quisque ornare porttitor diam. Vivamus imperdiet sollicitudin lorem. Nunc semper sapien condimentum urna. Maecenas vulputate dui. Suspendisse in justo. Phasellus a eros. Aliquam lectus. Vivamus viverra arcu ac turpis. Quisque commodo est id quam. Donec vestibulum. Integer euismod lorem ac lectus.
</p>

<p class="borderedTag">
Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos hymenaeos. Nam convallis. Donec aliquam blandit purus. Maecenas aliquam, nulla volutpat dictum viverra, elit lacus consectetuer ipsum, in luctus sem dui at lacus. Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Fusce ligula. Mauris et orci. Aenean tempor tellus id tellus. Cras in sapien vel nibh blandit malesuada. In sagittis. Duis turpis tortor, auctor et, sodales nec, dapibus in, erat.
</p>

<p class="borderedTag">
Nunc eget ligula quis lectus convallis vulputate. Aenean lobortis justo eu diam. Nam congue iaculis dolor. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Vestibulum venenatis felis vitae risus. Praesent eu sapien eget ipsum vestibulum lacinia. Etiam dolor ligula, pharetra et, accumsan at, pellentesque a, erat. Aliquam erat volutpat. Vestibulum vulputate, lorem sed euismod pulvinar, pede pede rhoncus turpis, sit amet aliquam velit leo non metus. Praesent sed pede. Aenean sed sapien in mauris blandit pellentesque. Vivamus metus nulla, interdum vitae, faucibus id, laoreet nec, orci.
</p>
</body>
</html>