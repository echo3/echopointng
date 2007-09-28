package echopointng;

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

import java.util.HashMap;
import java.util.Map;

import nextapp.echo2.app.Component;
import nextapp.echo2.app.PaneContainer;
import echopointng.template.TemplateDataSource;
import echopointng.template.TemplateTextSubstitution;

/**
 * <code>TemplatePanel</code> is a container that uses a
 * <code>TemplateLayoutData</code> to render a template of content.
 * <p>
 * This layout data can itself contained "named" Components and "named" Text
 * Substitutions.
 * <p>
 * A singleton <code>TemplateDataSource</code> can be used for more than one
 * <code>TemplatePanel</code> and hence the memory footprint is reduced if
 * used in this way.
 * <p>
 * <h3>Supplied Implementation</h3>
 * <p>
 * The supplied template implementation reads XHTML template data from a number
 * of different sources including Files, Strings, Resources and JSP pages.
 * <p>
 * The markup :
 * 
 * <pre><code>
 * 
 *  
 *   &lt;component name=&quot;xxx&quot; style=&quot;xxx&quot; styleName=&quot;xxx&quot;/&gt;
 *   
 *  
 * </code></pre>
 * 
 * is used to indicate where in the template a named component will be placed.
 * <p>
 * The <code>style</code> attribute can contain a CSS statement for setting
 * component properties.
 * <p>
 * For example :
 * 
 * <pre><code>
 * 
 *  
 *   &lt;component name=&quot;xxx&quot; style=&quot;background:red&quot;/&gt;
 *   
 *  
 * </code></pre></code></pre>
 * 
 * will set the background of the inserted component to red. Under the covers a <code>Style</code> object
 * is created with these values and set into the component via setStyle().
 * <p>
 * The <code>styleName</code> attribute can contain a name ready for use with the <code>Component.setStyleName()</code>
 * method.
 * <p>
 * For example :
 * 
 * <pre><code>
 *   &lt;component name=&quot;xxx&quot; styleName=&quot;useThisStyle&quot;/&gt;
 * </code></pre></code></pre>
 * 
 * will end up having <code>component.setStyleName('useThisStyle');</code> invoked.
 * <p>
 * The standard XHTML "controls" markup can also be used also to subsitute
 * components.
 * 
 * <pre><code>&lt;input/&gt;</code></pre><pre><code>&lt;button/&gt;</code></pre>
 * 
 * and
 * 
 * <pre><code>&lt;select/&gt;</code></pre>
 * 
 * can all be used as component subsitution markers.
 * <p>
 * The markup :
 * 
 * <pre><code>&lt;text name="xxx" /&gt;</code></pre>
 * 
 * is used to indicate where text subsitutions will be placed.
 * <p>
 * Text subsitution can occur within components as well.
 * <p>
 * For example imagine the markup of the template looks a bit like this
 * 
 * <pre><code>... preceding markup &lt;component name="button1"&gt;Button 1 Text
 * Here&lt;/component&gt; and so on ...</code></pre>
 * 
 * If the isInvokeSetText() property is true and the component that maps to the
 * name 'button1' has a <code>public void setText(String s)</code> method,
 * then the text inside the component marker, in this case "Button 1 Text Here",
 * will be placed into the component via its setText() method.
 * <p>
 * This is only done once when the template is compiled and isStyleApplied() is
 * false. Of course the component in question must have a setText() method.
 * 
 * <h3>Custom Implementation</h3>
 * <p>
 * The source template data need not be in XHTML format however you must compile
 * it into a XHTML DOM Element. For example you could write your own custom
 * XML/XSLT implementation that produced XHTML.
 * <p>
 * To do this you must provide a <code>TemplateCompiler</code> to the
 * rendering framework that takes the raw template data and converts it into a
 * XHTML DOM element.
 * <p>
 * <code>TemplatePanel</code> is defined as an <code>AbleComponent</code>
 * and as such has border, inset and mouse cursor support. However there is a
 * caveat for to this. These properties only take affect if the top level tag in
 * the the template data source does not provide a style="" attribute. if one is
 * provided then it takes precedence over the AbleComponent properties
 * <p>
 * <code>TemplatePanel</code> also support the use of <code>DisplayLayoutData</code> and
 * <code>ScrollableDisplayLayoutData</code> when used on child components.  This allows
 * the special "style" attributes to be placed within the template around the child component
 * in question, perhaps to make it a certain size or position is  aspecial place.
 * 
 * @see echopointng.template.TemplateDataSource
 * @see echopointng.template.TemplateTextSubstitution
 * @see echopointng.layout.DisplayLayoutData
 * @see echopointng.layout.ScrollableDisplayLayoutData
 */
public class TemplatePanel extends AbleComponent implements PaneContainer {
	public static final String PROPERTY_EXCEPTION_ON_FAILURE = "exceptionOnFailure";

	public static final String PROPERTY_INVOKE_SETTEXT = "invokeSetText";

	public static final String PROPERTY_TEMPLATE_DATA_SOURCE = "templateDataSource";

	public static final String PROPERTY_TEMPLATE_TEXT_SUBSTITUTION = "templateTextSubstition";

	private Map componentNameMap = new HashMap();

	private boolean styleApplied;

	/**
	 * Constructs a <code>TemplatePanel</code> with no template data as yet.
	 */
	public TemplatePanel() {
		super();
	}

	/**
	 * Constructs a <code>TemplatePanel</code> with the specified
	 * TemplateDataSource.
	 * 
	 * @param tds -
	 *            the source for the template data.
	 */
	public TemplatePanel(TemplateDataSource tds) {
		super();
		setTemplateDataSource(tds);
	}

	/**
	 * Adds a component to the <code>TemplatePanel</code> with the associated
	 * name which can the be references from the template data.
	 * 
	 * @param component
	 *            the component to add
	 * @param componentName -
	 *            the name to use when referencing this component in the
	 *            template data.
	 */
	public void addNamedComponent(Component component, String componentName) {
		if (component == null)
			throw new IllegalArgumentException("component must be non null.");
		if (componentName == null)
			throw new IllegalArgumentException("componentName must be non null.");

		componentNameMap.put(componentName, component);
		add(component);
	}

	/**
	 * Returns the name associated with the component or null if it cant be
	 * found.
	 * 
	 * @param component -
	 *            the component associated with the name
	 * @return a name associated with component
	 */
	public String getComponentName(Component component) {
		String componentNames[] = getNamedComponents();
		for (int i = 0; i < componentNames.length; i++) {
			Component c = getNamedComponent(componentNames[i]);
			if (component == c) {
				return componentNames[i];
			}
		}
		return null;
	}

	/**
	 * Returns a component associated with the name or null if it cant be found.
	 * 
	 * @param componentName -
	 *            the name associated with the component
	 * @return a component associated with componentName
	 */
	public Component getNamedComponent(String componentName) {
		return (Component) componentNameMap.get((componentName));
	}

	/**
	 * @return an array of all the component names in the
	 *         <code>TemplatePanel</code>.
	 */
	public String[] getNamedComponents() {
		return (String[]) componentNameMap.keySet().toArray(new String[componentNameMap.keySet().size()]);
	}

	/**
	 * Returns the TemplateDataSource which contains the template data.
	 * 
	 * @return the TemplateDataSource in place
	 */
	public TemplateDataSource getTemplateDataSource() {
		return (TemplateDataSource) getProperty(PROPERTY_TEMPLATE_DATA_SOURCE);
	}

	/**
	 * Returns the TemplateTextSubstitution in place.
	 * 
	 * @return Returns the textSubstitution in place.
	 */
	public TemplateTextSubstitution getTemplateTextSubstitution() {
		return (TemplateTextSubstitution) getProperty(PROPERTY_TEMPLATE_TEXT_SUBSTITUTION);
	}

	/**
	 * The default value is false.
	 * 
	 * @return - the exception failure value
	 */
	public boolean isExceptionOnFailure() {
		return getProperty(PROPERTY_EXCEPTION_ON_FAILURE, false);
	}

	/**
	 * Returns true if the text of a component marker is the template is placed
	 * into the component via its setTextMethod.
	 * 
	 * @return true if the text of a component marker is the template is placed
	 *         into the component via its setTextMethod.
	 */
	public boolean isInvokeSetText() {
		return getProperty(PROPERTY_INVOKE_SETTEXT, false);
	}

	/**
	 * Returns true if the style declarations from the template data have been
	 * applied. Typically not used outside the template rendering framework. The
	 * template rendering framework use this to keep track of when styles have
	 * been applied and to prevent them being unecessarity applied again.
	 * 
	 * @return true if the the style declarations have been applied
	 */
	public boolean isStyleApplied() {
		return styleApplied;
	}

	/**
	 * @see nextapp.echo2.app.Component#remove(nextapp.echo2.app.Component)
	 */
	public void remove(Component c) {
		String componentName = getComponentName(c);
		if (componentName != null) {
			componentNameMap.remove(componentName);
		}
		super.remove(c);
	}

	/**
	 * Removes a named component from the <code>TemplatePanel</code> that was
	 * previously added via the <code>addNamedComponent()</code> method.
	 * 
	 * @param componentName -
	 *            the name of the component
	 */
	public void removeNamedComponent(String componentName) {
		if (componentName == null)
			throw new IllegalArgumentException("componentName must be non null.");

		Component child = getNamedComponent(componentName);
		componentNameMap.remove(componentName);
		if (child != null) {
			remove(child);
		}
	}

	/**
	 * If set to true, then if the template data cannot be compiled, a a runtime
	 * exception will be thrown. If this is false, then a prominent error
	 * message will be returned as the template data but no exception will be
	 * thrown.
	 * <p>
	 * Some people prefer exceptions and "defined" error behaviour. Others
	 * prefer soft failures and the system to keep on running.
	 * 
	 * @param newValue -
	 *            the new failure value
	 */
	public void setExceptionOnFailure(boolean newValue) {
		setProperty(PROPERTY_EXCEPTION_ON_FAILURE, newValue);
	}

	/**
	 * Controls if the text of a component marker is the template is placed into
	 * the component via its setTextMethod.
	 * <p>
	 * The markup of the template might look like this
	 * <p>
	 * <code>
	 * ...
	 * preceding markup &lt;component name="button1"&gt;Button 1 Text Here&lt;/component&gt; and so on 
	 * ...
	 * </code>
	 * <p>
	 * If this property is true and the component that maps to the name button1
	 * has a public void setText(String s) method, then the text inside the
	 * component marker, in this case "Button 1 Text Here", will be placed into
	 * the component via its setText() method.
	 * <p>
	 * This is only done once when the template is compiled and isStyleApplied()
	 * is false. Of course the component in question must have a setText()
	 * method.
	 * 
	 * @param newValue -
	 *            the new boolean value
	 */
	public void setInvokeSetText(boolean newValue) {
		setProperty(PROPERTY_INVOKE_SETTEXT, newValue);
	}

	/**
	 * Controls whether style declarations in the template data have been
	 * applied or not. Typically you would not use this method. It is intended
	 * for use by the rendering framework
	 * 
	 * @param styleApplied -
	 *            The newValue to set.
	 */
	public void setStyleApplied(boolean styleApplied) {
		this.styleApplied = styleApplied;
	}

	/**
	 * Sets the TemplateDataSource to be used
	 * 
	 * @param templateDataSource
	 *            the TemplateDataSource to be used
	 */
	public void setTemplateDataSource(TemplateDataSource templateDataSource) {
		setProperty(PROPERTY_TEMPLATE_DATA_SOURCE, templateDataSource);
	}

	/**
	 * Sets the TemplateTextSubstitution to be used to substitute 'named' text
	 * into the template data
	 * 
	 * @param textSubstitution -
	 *            The newValue to set.
	 */
	public void setTemplateTextSubstitution(TemplateTextSubstitution textSubstitution) {
		setProperty(PROPERTY_TEMPLATE_TEXT_SUBSTITUTION, textSubstitution);
	}

	/**
	 * @see nextapp.echo2.app.Component#validate()
	 */
	public void validate() {
		super.validate();
		String names[] = getNamedComponents();
		for (int i = 0; i < names.length; i++) {
			Component child = getNamedComponent(names[i]);
			if (!this.isAncestorOf(child)) {
				this.add(child);
			}
		}
	}
}
