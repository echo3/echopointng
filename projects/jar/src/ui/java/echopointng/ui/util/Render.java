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
package echopointng.ui.util;

import org.w3c.dom.Element;

import nextapp.echo2.app.Alignment;
import nextapp.echo2.app.Border;
import nextapp.echo2.app.Color;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.FillImage;
import nextapp.echo2.app.Font;
import nextapp.echo2.app.ImageReference;
import nextapp.echo2.app.Insets;
import nextapp.echo2.app.LayoutData;
import nextapp.echo2.app.Style;
import nextapp.echo2.webcontainer.RenderContext;
import nextapp.echo2.webcontainer.image.ImageRenderSupport;
import nextapp.echo2.webcontainer.propertyrender.AlignmentRender;
import nextapp.echo2.webcontainer.propertyrender.BorderRender;
import nextapp.echo2.webcontainer.propertyrender.ColorRender;
import nextapp.echo2.webcontainer.propertyrender.ExtentRender;
import nextapp.echo2.webcontainer.propertyrender.FillImageRender;
import nextapp.echo2.webcontainer.propertyrender.FontRender;
import nextapp.echo2.webcontainer.propertyrender.InsetsRender;
import nextapp.echo2.webcontainer.propertyrender.LayoutDirectionRender;
import nextapp.echo2.webrender.ClientProperties;
import nextapp.echo2.webrender.output.CssStyle;
import echopointng.BorderEx;
import echopointng.ComponentEx;
import echopointng.able.Alignable;
import echopointng.able.BackgroundImageable;
import echopointng.able.Borderable;
import echopointng.able.Delegateable;
import echopointng.able.Floatable;
import echopointng.able.Heightable;
import echopointng.able.Insetable;
import echopointng.able.MouseCursorable;
import echopointng.able.Positionable;
import echopointng.able.ScrollBarProperties;
import echopointng.able.Scrollable;
import echopointng.able.Sizeable;
import echopointng.able.Widthable;
import echopointng.layout.DisplayLayoutData;
import echopointng.util.ColorKit;
import echopointng.util.HtmlKit;
import echopointng.xhtml.XhtmlFragment;

/**
 * <code>Render</code> is used to render stuff into CssStyle objects. It goals
 * is to reduce the amount of code needed to create valid CssStyle objects.
 */
public class Render {

	/**
	 * This will take an object and render all the interfaces that the Render
	 * code knows about such as Borderable, Insetable or MouseCursorable.
	 * 
	 * @param cssStyle -
	 *            the CssStyle object to render into
	 * @param ableObject -
	 *            the object that may implement some of the known able
	 *            interfaces
	 */
	public static void asAble(CssStyle cssStyle, Object ableObject) {
		asAble(cssStyle, ableObject, null);
	}

	/**
	 * This will take an object and render all the interfaces that the Render
	 * code knows about such as Borderable, Insetable or MouseCursorable.
	 * 
	 * @param cssStyle -
	 *            the CssStyle object to render into
	 * @param ableObject -
	 *            the object that may implement some of the known able
	 *            interfaces
	 * @param fallbackStyle -
	 *            a style to use as a default source of property information if
	 *            it cant be found in the ableObject.
	 */
	public static void asAble(CssStyle cssStyle, Object ableObject, Style fallbackStyle) {
		if (ableObject == null) {
			return;
		}
		
		if (ableObject instanceof Alignable) {
			asAlignable(cssStyle, (Alignable) ableObject, fallbackStyle);
		}
		if (ableObject instanceof Insetable) {
			asInsetable(cssStyle, (Insetable) ableObject, fallbackStyle);
		}
		if (ableObject instanceof MouseCursorable) {
			asMouseCursorable(cssStyle, (MouseCursorable) ableObject, fallbackStyle);
		}
		if (ableObject instanceof Borderable) {
			asBorderable(cssStyle, (Borderable) ableObject, fallbackStyle);
		}
		if (ableObject instanceof Widthable) {
			asWidthable(cssStyle, (Widthable) ableObject, fallbackStyle);
		}
		if (ableObject instanceof Heightable) {
			asHeightable(cssStyle, (Heightable) ableObject, fallbackStyle);
		}
		if (ableObject instanceof Positionable) {
			asPositionable(cssStyle, (Positionable) ableObject, fallbackStyle);
		}
		if (ableObject instanceof Scrollable) {
			asScrollable(cssStyle, (Scrollable) ableObject, fallbackStyle);
		}
		if (ableObject instanceof Floatable) {
			asFloatable(cssStyle, (Floatable) ableObject, fallbackStyle);
		}
	}

	/**
	 * Renders a Border to the CSS border values
	 * 
	 * @param cssStyle -
	 *            the CssStyle to render into
	 * @param border -
	 *            the Border
	 */
	public static void asBorder(CssStyle cssStyle, Border border) {
		borderRenderImpl(cssStyle, border);
	}

	/**
	 * Renders the specified component's property as a Border into the CssStyle
	 * provided.
	 * <p>
	 * This does a lookup of the component's properties via
	 * getRenderProperty(propertyName) and casts the return value appropriately,
	 * so take care with property name.
	 * 
	 * @param cssStyle -
	 *            the CssStyle to render into
	 * @param component -
	 *            the component to inpect via getRenderProperty()
	 * @param propertyName -
	 *            the name of the components's "render" property
	 */
	public static void asBorder(CssStyle cssStyle, Component component, String propertyName) {
		borderRenderImpl(cssStyle, (Border) component.getRenderProperty(propertyName));
	}

	/**
	 * Renders the specified component's property as a Border into the CssStyle
	 * provided.
	 * <p>
	 * This does a lookup of the component's properties via
	 * getRenderProperty(propertyName) and casts the return value appropriately,
	 * so take care with property name.
	 * 
	 * @param cssStyle -
	 *            the CssStyle to render into
	 * @param component -
	 *            the component to inpect via getRenderProperty()
	 * @param propertyName -
	 *            the name of the components's "render" property
	 * @param fallbackStyle -
	 *            a style to use as a default source of property information if
	 *            it cant be found in the component.
	 */
	public static void asBorder(CssStyle cssStyle, Component component, String propertyName, Style fallbackStyle) {
		borderRenderImpl(cssStyle, (Border) getRP(component, propertyName, fallbackStyle));
	}

	/**
	 * Renders a Borderable into the CssStyle.
	 * 
	 * @param cssStyle -
	 *            the style in question
	 * @param able -
	 *            where source of the style information
	 */
	public static void asBorderable(CssStyle cssStyle, Borderable able) {
		asBorderable(cssStyle, able, null);
	}

	/**
	 * Renders a Borderable into the CssStyle.
	 * 
	 * @param cssStyle -
	 *            the style in question
	 * @param able -
	 *            where source of the style information
	 * @param fallbackStyle -
	 *            a style to use as a default source of property information if
	 *            it cant be found in the Borderable.
	 */
	public static void asBorderable(CssStyle cssStyle, Borderable able, Style fallbackStyle) {
		borderRenderImpl(cssStyle, (Border) getRP(able, Borderable.PROPERTY_BORDER, fallbackStyle));
	}

	/**
	 * Renders a color to the specified CSS attribute
	 * 
	 * @param cssStyle -
	 *            the CssStyle to render into
	 * @param color -
	 *            the color to be rendered
	 * @param cssAttributeName -
	 *            the CSS attribute name to place the color value in
	 */
	public static void asColor(CssStyle cssStyle, Color color, String cssAttributeName) {
		if (color != null) {
			cssStyle.setAttribute(cssAttributeName, ColorKit.makeCSSColor(color));
		}
	}

	/**
	 * Renders the specified component's background and foreground properties
	 * into the CssStyle provided.
	 * 
	 * @param cssStyle -
	 *            the CssStyle to render into
	 * @param component -
	 *            the component to inpect via getRenderProperty()
	 */
	public static void asColors(CssStyle cssStyle, Component component) {
		asColors(cssStyle, component, Component.PROPERTY_BACKGROUND, Component.PROPERTY_FOREGROUND);
	}

	/**
	 * Renders the specified component's background property and foreground
	 * property into the CssStyle provided.
	 * <p>
	 * This does a lookup of the component's properties via
	 * getRenderProperty(propertyName) and casts the return value appropriately,
	 * so take care with property name.
	 * 
	 * @param cssStyle -
	 *            the CssStyle to render into
	 * @param component -
	 *            the component to inpect via getRenderProperty()
	 * @param backgroundPropertyName -
	 *            the name of the background "render" property - can be null
	 * @param foregroundPropertyName -
	 *            the name of the foreground "render" property - can be null
	 */
	public static void asColors(CssStyle cssStyle, Component component, String backgroundPropertyName, String foregroundPropertyName) {
		asColors(cssStyle, component, backgroundPropertyName, foregroundPropertyName, null);
	}

	/**
	 * Renders the specified component's background property and foreground
	 * property into the CssStyle provided.
	 * <p>
	 * This does a lookup of the component's properties via
	 * getRenderProperty(propertyName) and casts the return value appropriately,
	 * so take care with property name.
	 * 
	 * @param cssStyle -
	 *            the CssStyle to render into
	 * @param component -
	 *            the component to inpect via getRenderProperty()
	 * @param backgroundPropertyName -
	 *            the name of the background "render" property - can be null
	 * @param foregroundPropertyName -
	 *            the name of the foreground "render" property - can be null
	 * @param fallbackStyle -
	 *            a style to use as a default source of property information if
	 *            it cant be found in the component.
	 */
	public static void asColors(CssStyle cssStyle, Component component, String backgroundPropertyName, String foregroundPropertyName,
			Style fallbackStyle) {
		Color background = (backgroundPropertyName == null) ? null : (Color) getRP(component, backgroundPropertyName, fallbackStyle);
		Color foreground = (foregroundPropertyName == null) ? null : (Color) getRP(component, foregroundPropertyName, fallbackStyle);
		ColorRender.renderToStyle(cssStyle, foreground, background);
	}

	/**
	 * Renders the specified component's background and foreground properties
	 * into the CssStyle provided.
	 * 
	 * @param cssStyle -
	 *            the CssStyle to render into
	 * @param component -
	 *            the component to inpect via getRenderProperty()
	 * @param fallbackStyle -
	 *            a style to use as a default source of property information if
	 *            it cant be found in the component.
	 */
	public static void asColors(CssStyle cssStyle, Component component, Style fallbackStyle) {
		asColors(cssStyle, component, Component.PROPERTY_BACKGROUND, Component.PROPERTY_FOREGROUND, fallbackStyle);
	}

	/**
	 * This will take a component and render all the interfaces that the Render
	 * code knows about such as Borderable, Insetable or MouseCursorable.
	 * <p>
	 * It also renders the standard Component properties into the CssStyle such
	 * as forground color, background color and font.
	 * 
	 * @param cssStyle -
	 *            the CssStyle object to render into
	 * @param component -
	 *            the component that may implement some of the known interfaces
	 */
	public static void asComponent(CssStyle cssStyle, Component component) {
		asComponent(cssStyle, component, null);
	}

	/**
	 * This will take a component and render all the interfaces that the Render
	 * code knows about such as Borderable, Insetable or MouseCursorable.
	 * <p>
	 * It also renders the standard Component properties into the CssStyle such
	 * as forground color, background color and font.
	 * 
	 * @param cssStyle -
	 *            the CssStyle object to render into
	 * @param component -
	 *            the component that may implement some of the known interfaces
	 * @param fallbackStyle -
	 *            a style to use as a default source of property information if
	 *            it cant be found in the component.
	 */
	public static void asComponent(CssStyle cssStyle, Component component, Style fallbackStyle) {
		if (component == null)
			return;

		// able interfaces
		asAble(cssStyle, component, fallbackStyle);

		// now render the standard Component properies as well
		asColors(cssStyle, component, fallbackStyle);
		asFont(cssStyle, component, fallbackStyle);
		LayoutDirectionRender.renderToStyle(cssStyle, component.getLayoutDirection(), component.getLocale());

		// special ComponentEx hidden property
		Object hidden = component.getRenderProperty(ComponentEx.PROPERTY_HIDDEN, Boolean.valueOf(false));
		if (hidden instanceof Boolean) {
			if (((Boolean) hidden).booleanValue() == true) {
				cssStyle.setAttribute("display", "none");
			}
		}
	}

	/**
	 * This will render the FillImage property from the component. It uses
	 * property name to find the FillImage and also assumes the the
	 * ImageRenderSupport uses the same name when providing the Image.
	 * <p>
	 * This does a lookup of the component's properties via
	 * getRenderProperty(propertyName) and casts the return value appropriately,
	 * so take care with property name.
	 * 
	 * 
	 * @param cssStyle -
	 *            the CssStyle object to render into
	 * @param component -
	 *            the component that may implement some of the known interfaces
	 * @param propertyName -
	 *            the name of the components's "render" property
	 * @param rc -
	 *            the RenderContext in place
	 */
	public static void asFillImage(CssStyle cssStyle, Component component, String propertyName, RenderContext rc) {
		asFillImage(cssStyle, component, propertyName, null, rc);
	}

	/**
	 * This will render the FillImage property from the component. It uses
	 * property name to find the FillImage and also assumes the the
	 * ImageRenderSupport uses the same name when providing the Image.
	 * <p>
	 * This does a lookup of the component's properties via
	 * getRenderProperty(propertyName) and casts the return value appropriately,
	 * so take care with property name.
	 * 
	 * 
	 * @param cssStyle -
	 *            the CssStyle object to render into
	 * @param component -
	 *            the component that may implement some of the known interfaces
	 * @param propertyName -
	 *            the name of the components's "render" property
	 * @param fallbackStyle -
	 *            a style to use as a default source of property information if
	 *            it cant be found in the component.
	 * @param rc -
	 *            the RenderContext in place
	 */
	public static void asFillImage(CssStyle cssStyle, Component component, String propertyName, Style fallbackStyle, RenderContext rc) {
		FillImage fillImage = (FillImage) getRP(component, propertyName, fallbackStyle);
		ImageReference imageRef = (fillImage != null ? fillImage.getImage() : null);
		ImageRenderSupport irs = new DirectImageRenderSupport(imageRef);
		FillImageRender.renderToStyle(cssStyle, rc, irs, component, propertyName, fillImage, 0);
	}

	/**
	 * Renders the specified component's "font" property as a Font into the
	 * CssStyle provided.
	 * 
	 * @param cssStyle -
	 *            the CssStyle to render into
	 * @param component -
	 *            the component to inpect via getRenderProperty()
	 */
	public static void asFont(CssStyle cssStyle, Component component) {
		asFont(cssStyle, component, Component.PROPERTY_FONT);
	}

	/**
	 * Renders the specified component's property as a Font into the CssStyle
	 * provided.
	 * <p>
	 * This does a lookup of the component's properties via
	 * getRenderProperty(propertyName) and casts the return value appropriately,
	 * so take care with property name.
	 * 
	 * @param cssStyle -
	 *            the CssStyle to render into
	 * @param component -
	 *            the component to inpect via getRenderProperty()
	 * @param propertyName -
	 *            the name of the components's "render" property
	 */
	public static void asFont(CssStyle cssStyle, Component component, String propertyName) {
		asFont(cssStyle, (Font) component.getRenderProperty(propertyName));
	}

	/**
	 * Renders the specified component's property as a Font into the CssStyle
	 * provided.
	 * <p>
	 * This does a lookup of the component's properties via
	 * getRenderProperty(propertyName) and casts the return value appropriately,
	 * so take care with property name.
	 * 
	 * @param cssStyle -
	 *            the CssStyle to render into
	 * @param component -
	 *            the component to inpect via getRenderProperty()
	 * @param propertyName -
	 *            the name of the components's "render" property
	 * @param fallbackStyle -
	 *            a style to use as a default source of property information if
	 *            it cant be found in the component.
	 */
	public static void asFont(CssStyle cssStyle, Component component, String propertyName, Style fallbackStyle) {
		asFont(cssStyle, (Font) getRP(component, propertyName, fallbackStyle));
	}

	/**
	 * Renders the specified component's "font" property as a Font into the
	 * CssStyle provided.
	 * 
	 * @param cssStyle -
	 *            the CssStyle to render into
	 * @param component -
	 *            the component to inpect via getRenderProperty()
	 * @param fallbackStyle -
	 *            a style to use as a default source of property information if
	 *            it cant be found in the component.
	 */
	public static void asFont(CssStyle cssStyle, Component component, Style fallbackStyle) {
		asFont(cssStyle, component, Component.PROPERTY_FONT, fallbackStyle);
	}

	/**
	 * Renders the specified font into the CssStyle provided.
	 * 
	 * @param cssStyle -
	 *            the CssStyle to render into
	 * @param font -
	 *            the font to render
	 */
	public static void asFont(CssStyle cssStyle, Font font) {
		FontRender.renderToStyle(cssStyle, font);
	}

	/**
	 * Renders a Heightable into the CssStyle.
	 * 
	 * @param cssStyle -
	 *            the style in question
	 * @param able -
	 *            where source of the style information
	 */
	public static void asHeightable(CssStyle cssStyle, Heightable able) {
		asHeightable(cssStyle, able, null);
	}

	/**
	 * Renders a Heightable into the CssStyle.
	 * 
	 * @param cssStyle -
	 *            the style in question
	 * @param able -
	 *            where source of the style information
	 * @param fallbackStyle -
	 *            a style to use as a default source of property information if
	 *            it cant be found in the Heightable.
	 */
	public static void asHeightable(CssStyle cssStyle, Heightable able, Style fallbackStyle) {
		Extent extent = (Extent) getRP(able, Heightable.PROPERTY_HEIGHT, fallbackStyle);
		if (extent != null)
			ExtentRender.renderToStyle(cssStyle, "height", extent);
	}

	/**
	 * Renders an Insetable into the CssStyle.
	 * 
	 * @param cssStyle -
	 *            the style in question
	 * @param able -
	 *            where source of the style information
	 */
	public static void asInsetable(CssStyle cssStyle, Insetable able) {
		asInsetable(cssStyle, able, null);
	}

	/**
	 * Renders an Insetable into the CssStyle.
	 * 
	 * @param cssStyle -
	 *            the style in question
	 * @param able -
	 *            where source of the style information
	 * @param fallbackStyle -
	 *            a style to use as a default source of property information if
	 *            it cant be found in the Insetable.
	 */
	public static void asInsetable(CssStyle cssStyle, Insetable able, Style fallbackStyle) {
		Insets insets;

		insets = (Insets) getRP(able, Insetable.PROPERTY_INSETS, fallbackStyle);
		if (insets != null)
			InsetsRender.renderToStyle(cssStyle, "padding", insets);

		insets = (Insets) getRP(able, Insetable.PROPERTY_OUTSETS, fallbackStyle);
		if (insets != null)
			InsetsRender.renderToStyle(cssStyle, "margin", insets);

	}

	/**
	 * Renders an Alignable into the CssStyle.
	 * 
	 * @param cssStyle -
	 *            the style in question
	 * @param able -
	 *            where source of the style information
	 */
	public static void asAlignable(CssStyle cssStyle, Alignable able) {
		asAlignable(cssStyle, able, null);
	}

	/**
	 * Renders an Alignable into the CssStyle.
	 * 
	 * @param cssStyle -
	 *            the style in question
	 * @param able -
	 *            where source of the style information
	 * @param fallbackStyle -
	 *            a style to use as a default source of property information if
	 *            it cant be found in the Alignable.
	 */
	public static void asAlignable(CssStyle cssStyle, Alignable able, Style fallbackStyle) {
		Alignment value;

		value = (Alignment) getRP(able, Alignable.PROPERTY_ALIGNMENT, fallbackStyle);
		if (value != null)
			AlignmentRender.renderToStyle(cssStyle, value);
	}

	/**
	 * Renders an BackgroundImageable into the CssStyle.
	 * 
	 * @param rc -
	 *            the
	 * @param cssStyle -
	 *            the style in question
	 * @param able -
	 *            where source of the style information
	 */
	public static void asBackgroundImageable(RenderContext rc, CssStyle cssStyle, BackgroundImageable able) {
		asBackgroundImageable(rc, cssStyle, able, null);
	}

	/**
	 * Renders an BackgroundImageable into the CssStyle.
	 * 
	 * @param rc -
	 *            the
	 * @param cssStyle -
	 *            the style in question
	 * @param able -
	 *            where source of the style information
	 * @param fallbackStyle -
	 *            a style to use as a default source of property information if
	 *            it cant be found in the BackgroundImageable.
	 */
	public static void asBackgroundImageable(RenderContext rc, CssStyle cssStyle, BackgroundImageable able, Style fallbackStyle) {
		Component component = (able instanceof Component) ? (Component) able : null;
		FillImage fillImage = (FillImage) getRP(able, BackgroundImageable.PROPERTY_BACKGROUND_IMAGE, fallbackStyle);
		asBackgroundImageImpl(rc, cssStyle, fillImage, component);
	}

	/**
	 * Renders an background <code>FillImage</code> into the CssStyle.
	 * 
	 * @param rc -
	 *            the
	 * @param cssStyle -
	 *            the style in question
	 * @param backgroundImage -
	 *            where source of the background image
	 */
	public static void asBackgroundImage(RenderContext rc, CssStyle cssStyle, FillImage backgroundImage) {
		asBackgroundImageImpl(rc, cssStyle, backgroundImage, null);
	}

	/*
	 * Actual background image implementation
	 */
	private static void asBackgroundImageImpl(RenderContext rc, CssStyle cssStyle, FillImage backgroundImage, Component component) {
		ImageReference imageRef = (backgroundImage != null ? backgroundImage.getImage() : null);
		ImageRenderSupport irs = new DirectImageRenderSupport(imageRef);
		FillImageRender.renderToStyle(cssStyle, rc, irs, component, BackgroundImageable.PROPERTY_BACKGROUND_IMAGE, backgroundImage, 0);
	}
	

	/**
	 * Renders the specified component's property as an Insets into the CssStyle
	 * provided.
	 * <p>
	 * This does a lookup of the component's properties via
	 * getRenderProperty(propertyName) and casts the return value appropriately,
	 * so take care with property name.
	 * 
	 * @param cssStyle -
	 *            the CssStyle to render into
	 * @param component -
	 *            the component to inpect via getRenderProperty()
	 * @param propertyName -
	 *            the name of the components's "render" property
	 * @param cssAttributeName -
	 *            the name of the CSS attribute, such as "margin" or "padding"
	 */
	public static void asInsets(CssStyle cssStyle, Component component, String propertyName, String cssAttributeName) {
		asInsets(cssStyle, component, propertyName, cssAttributeName, null);
	}

	/**
	 * Renders the specified component's property as an Insets into the CssStyle
	 * provided.
	 * <p>
	 * This does a lookup of the component's properties via
	 * getRenderProperty(propertyName) and casts the return value appropriately,
	 * so take care with property name.
	 * 
	 * @param cssStyle -
	 *            the CssStyle to render into
	 * @param component -
	 *            the component to inpect via getRenderProperty()
	 * @param propertyName -
	 *            the name of the components's "render" property
	 * @param cssAttributeName -
	 *            the name of the CSS attribute, such as "margin" or "padding"
	 * @param fallbackStyle -
	 *            a style to use as a default source of property information if
	 *            it cant be found in the component.
	 */
	public static void asInsets(CssStyle cssStyle, Component component, String propertyName, String cssAttributeName, Style fallbackStyle) {
		InsetsRender.renderToStyle(cssStyle, cssAttributeName, (Insets) getRP(component, propertyName, fallbackStyle));
	}

	/**
	 * Renders a MouseCursorable into the CssStyle.
	 * 
	 * @param cssStyle -
	 *            the style in question
	 * @param able -
	 *            where source of the style information
	 */
	public static void asMouseCursorable(CssStyle cssStyle, MouseCursorable able) {
		asMouseCursorable(cssStyle, able, null);
	}

	/**
	 * Renders a MouseCursorable into the CssStyle.
	 * 
	 * @param cssStyle -
	 *            the style in question
	 * @param able -
	 *            where source of the style information
	 * @param fallbackStyle -
	 *            a style to use as a default source of property information if
	 *            it cant be found in the MouseCursorable.
	 */
	public static void asMouseCursorable(CssStyle cssStyle, MouseCursorable able, Style fallbackStyle) {
		StringBuffer sb = new StringBuffer();

		Integer cursorProperty = (Integer) getRP(able, MouseCursorable.PROPERTY_MOUSE_CURSOR, fallbackStyle);
		if (cursorProperty == null) {
			return;
		}
		int mouseCursor = cursorProperty.intValue();
		if (mouseCursor == MouseCursorable.CURSOR_CUSTOM_URI) {
			String mouseCursorURI = (String) getRP(able, MouseCursorable.PROPERTY_MOUSE_CURSOR_URI, fallbackStyle);
			if (mouseCursorURI != null) {
				sb.append("uri('" + HtmlKit.encode(mouseCursorURI) + "');");
			}
		} else {
			switch (mouseCursor) {
			case MouseCursorable.CURSOR_AUTO:
				sb.append("auto");
				break;
			case MouseCursorable.CURSOR_DEFAULT:
				sb.append("default");
				break;
			case MouseCursorable.CURSOR_CROSSHAIR:
				sb.append("crosshair");
				break;
			case MouseCursorable.CURSOR_E_RESIZE:
				sb.append("e-resize");
				break;
			case MouseCursorable.CURSOR_HELP:
				sb.append("help");
				break;
			case MouseCursorable.CURSOR_MOVE:
				sb.append("move");
				break;
			case MouseCursorable.CURSOR_N_RESIZE:
				sb.append("n-resize");
				break;
			case MouseCursorable.CURSOR_NE_RESIZE:
				sb.append("ne-resize");
				break;
			case MouseCursorable.CURSOR_NW_RESIZE:
				sb.append("nw-resize");
				break;
			case MouseCursorable.CURSOR_POINTER:
				sb.append("pointer");
				break;
			case MouseCursorable.CURSOR_S_RESIZE:
				sb.append("s-resize");
				break;
			case MouseCursorable.CURSOR_SE_RESIZE:
				sb.append("se-resize");
				break;
			case MouseCursorable.CURSOR_SW_RESIZE:
				sb.append("sw-resize");
				break;
			case MouseCursorable.CURSOR_TEXT:
				sb.append("text");
				break;
			case MouseCursorable.CURSOR_W_RESIZE:
				sb.append("w-resize");
				break;
			case MouseCursorable.CURSOR_WAIT:
				sb.append("wait");
				break;
			default:
				return;
			}
		}
		cssStyle.setAttribute("cursor", sb.toString());
	}

	/**
	 * Renders a Floatable into the CssStyle.
	 * 
	 * @param cssStyle -
	 *            the style in question
	 * @param able -
	 *            where source of the style information
	 */
	public static void asFloatable(CssStyle cssStyle, Floatable able) {
		asFloatable(cssStyle, able, null);
	}

	/**
	 * Renders a Floatable into the CssStyle.
	 * 
	 * @param cssStyle -
	 *            the style in question
	 * @param able -
	 *            where source of the style information
	 * @param fallbackStyle -
	 *            a style to use as a default source of property information if
	 *            it cant be found in the Floatable.
	 */
	public static void asFloatable(CssStyle cssStyle, Floatable able, Style fallbackStyle) {

		int floatDir = getRP(able, Floatable.PROPERTY_FLOAT, fallbackStyle, Floatable.FLOAT_NONE);
		switch (floatDir) {
			case Floatable.FLOAT_LEFT:
				cssStyle.setAttribute("float", "left");
				break;
			case Floatable.FLOAT_RIGHT:
				cssStyle.setAttribute("float", "right");
				break;
			default:
				break;
		}

		int clear = getRP(able, Floatable.PROPERTY_CLEAR_FLOAT, fallbackStyle, Floatable.FLOAT_CLEAR_NONE);
		switch (clear) {
			case Floatable.FLOAT_CLEAR_LEFT:
				cssStyle.setAttribute("clear", "left");
				break;
			case Floatable.FLOAT_CLEAR_RIGHT:
				cssStyle.setAttribute("clear", "right");
				break;
			case Floatable.FLOAT_CLEAR_BOTH:
				cssStyle.setAttribute("clear", "both");
				break;
			default:
				break;
		}
	}

	
	/**
	 * Renders a Positionable into the CssStyle.
	 * 
	 * @param cssStyle -
	 *            the style in question
	 * @param able -
	 *            where source of the style information
	 */
	public static void asPositionable(CssStyle cssStyle, Positionable able) {
		asPositionable(cssStyle, able, null);
	}

	/**
	 * Renders a Positionable into the CssStyle.
	 * 
	 * @param cssStyle -
	 *            the style in question
	 * @param able -
	 *            where source of the style information
	 * @param fallbackStyle -
	 *            a style to use as a default source of property information if
	 *            it cant be found in the Positionable.
	 */
	public static void asPositionable(CssStyle cssStyle, Positionable able, Style fallbackStyle) {

		int position = getRP(able, Positionable.PROPERTY_POSITION, fallbackStyle, Positionable.STATIC);
		switch (position) {
		case Positionable.ABSOLUTE:
			cssStyle.setAttribute("position", "absolute");
			break;
		case Positionable.FIXED:
			cssStyle.setAttribute("position", "fixed");
			break;
		case Positionable.RELATIVE:
			cssStyle.setAttribute("position", "relative");
			break;
		default:
			break;
		}
		if (position != Positionable.STATIC) {
			Extent extent;
			extent = (Extent) getRP(able, Positionable.PROPERTY_LEFT, fallbackStyle);
			ExtentRender.renderToStyle(cssStyle, "left", extent);
			extent = (Extent) getRP(able, Positionable.PROPERTY_RIGHT, fallbackStyle);
			ExtentRender.renderToStyle(cssStyle, "right", extent);
			extent = (Extent) getRP(able, Positionable.PROPERTY_TOP, fallbackStyle);
			ExtentRender.renderToStyle(cssStyle, "top", extent);
			extent = (Extent) getRP(able, Positionable.PROPERTY_BOTTOM, fallbackStyle);
			ExtentRender.renderToStyle(cssStyle, "bottom", extent);
		}

		int zIndex = getRP(able, Positionable.PROPERTY_Z_INDEX, fallbackStyle, Integer.MIN_VALUE);
		if (zIndex != Integer.MIN_VALUE) {
			cssStyle.setAttribute("z-index", String.valueOf(zIndex));
		}
	}

	/**
	 * Renders a Scrollable into the CssStyle.
	 * 
	 * @param cssStyle -
	 *            the style in question
	 * @param able -
	 *            where source of the style information
	 */
	public static void asScrollable(CssStyle cssStyle, Scrollable able) {
		asScrollable(cssStyle, able, null);
	}

	/**
	 * Renders a Scrollable into the CssStyle.
	 * 
	 * @param cssStyle -
	 *            the style in question
	 * @param able -
	 *            where source of the style information
	 * @param fallbackStyle -
	 *            a style to use as a default source of property information if
	 *            it cant be found in the Scrollable.
	 */
	public static void asScrollable(CssStyle cssStyle, Scrollable able, Style fallbackStyle) {
		int scrollBarPolicy = getRP(able, Scrollable.PROPERTY_SCROLL_BAR_POLICY, fallbackStyle, Scrollable.UNDEFINED);
		switch (scrollBarPolicy) {
		case Scrollable.NEVER:
			cssStyle.setAttribute("overflow", "visible");
			break;
		case Scrollable.ALWAYS:
			cssStyle.setAttribute("overflow", "scroll");
			break;
		case Scrollable.AUTO:
			cssStyle.setAttribute("overflow", "auto");
			break;
		case Scrollable.CLIPHIDE:
			cssStyle.setAttribute("overflow", "hidden");
			break;
		default:
			// dont set anything
			break;
		}
		// scrollable's are sizeables
		asSizeable(cssStyle, able, fallbackStyle);

		// do the scroll bar properties next
		asColor(cssStyle, (Color) getRP(able, Scrollable.PROPERTY_SCROLL_BAR_BASE_COLOR, fallbackStyle), "scrollbar-base-color");

		ScrollBarProperties scrollBarProperties = (ScrollBarProperties) getRP(able, Scrollable.PROPERTY_SCROLL_BAR_PROPERTIES, fallbackStyle);
		if (scrollBarProperties != null) {
			asColor(cssStyle, scrollBarProperties.getThreeDLightColor(), "scrollbar-3dlight-color");
			asColor(cssStyle, scrollBarProperties.getArrowColor(), "scrollbar-arrow-color");
			asColor(cssStyle, scrollBarProperties.getBaseColor(), "scretRollbar-base-color");
			asColor(cssStyle, scrollBarProperties.getDarkShadowColor(), "scrollbar-darkshadow-color");
			asColor(cssStyle, scrollBarProperties.getFaceColor(), "scrollbar-face-color");
			asColor(cssStyle, scrollBarProperties.getHilightColor(), "scrollbar-highlight-color");
			asColor(cssStyle, scrollBarProperties.getShadowColor(), "scrollbar-shadow-color");
		}
	}

	/**
	 * Renders a Sizeable into the CssStyle.
	 * 
	 * @param cssStyle -
	 *            the style in question
	 * @param able -
	 *            where source of the style information
	 */
	public static void asSizeable(CssStyle cssStyle, Sizeable able) {
		asSizeable(cssStyle, able, null);
	}

	/**
	 * Renders a Sizeable into the CssStyle.
	 * 
	 * @param cssStyle -
	 *            the style in question
	 * @param able -
	 *            where source of the style information
	 * @param fallbackStyle -
	 *            a style to use as a default source of property information if
	 *            it cant be found in the Sizeable.
	 */
	public static void asSizeable(CssStyle cssStyle, Sizeable able, Style fallbackStyle) {
		asWidthable(cssStyle, able, fallbackStyle);
		asHeightable(cssStyle, able, fallbackStyle);
	}

	/**
	 * Renders a Widthable into the CssStyle.
	 * 
	 * @param cssStyle -
	 *            the style in question
	 * @param able -
	 *            where source of the style information
	 */
	public static void asWidthable(CssStyle cssStyle, Widthable able) {
		asWidthable(cssStyle, able, null);
	}

	/**
	 * Renders a Widthable into the CssStyle.
	 * 
	 * @param cssStyle -
	 *            the style in question
	 * @param able -
	 *            where source of the style information
	 * @param fallbackStyle -
	 *            a style to use as a default source of property information if
	 *            it cant be found in the Widthable.
	 */
	public static void asWidthable(CssStyle cssStyle, Widthable able, Style fallbackStyle) {
		Extent extent = (Extent) getRP(able, Widthable.PROPERTY_WIDTH, fallbackStyle);
		if (extent != null)
			ExtentRender.renderToStyle(cssStyle, "width", extent);
	}

	/**
	 * Our border rendering implementation
	 */
	private static void borderRenderImpl(CssStyle cssStyle, Border border) {
		if (border instanceof BorderEx) {
			BorderEx borderEx = (BorderEx) border;

			Color leftColor = borderEx.getLeftColor();
			Extent leftSize = borderEx.getLeftSize();
			int leftStyle = borderEx.getLeftStyle();
			Color rightColor = borderEx.getRightColor();
			Extent rightSize = borderEx.getRightSize();
			int rightStyle = borderEx.getRightStyle();
			Color topColor = borderEx.getTopColor();
			Extent topSize = borderEx.getTopSize();
			int topStyle = borderEx.getTopStyle();
			Color bottomColor = borderEx.getBottomColor();
			Extent bottomSize = borderEx.getBottomSize();
			int bottomStyle = borderEx.getBottomStyle();

			if (eqC(leftColor, rightColor) && eqC(leftColor, topColor) && eqC(leftColor, bottomColor) && eqE(leftSize, rightSize)
					&& eqE(leftSize, topSize) && eqE(leftSize, bottomSize) && leftStyle == rightStyle && leftStyle == topStyle
					&& leftStyle == bottomStyle) {
				// borderSetAttr(cssStyle, "", leftSize, leftColor, leftStyle);
				BorderRender.renderToStyle(cssStyle, new Border(leftSize, leftColor, leftStyle));
			} else {
				// now over ride them with the specific ones
				borderSetAttr(cssStyle, "left-", leftSize, leftColor, leftStyle);
				borderSetAttr(cssStyle, "right-", rightSize, rightColor, rightStyle);
				borderSetAttr(cssStyle, "top-", topSize, topColor, topStyle);
				borderSetAttr(cssStyle, "bottom-", bottomSize, bottomColor, bottomStyle);
			}
		} else {
			BorderRender.renderToStyle(cssStyle, border);
		}
		// if (border != null) {
		// cssStyle.setAttribute("border-collapse","collapse");
		// }
	}

	private static void borderSetAttr(CssStyle cssStyle, String attrName, Extent size, Color color, int style) {
		if (size != null && size.getValue() > 0) {
			cssStyle.setAttribute("border-" + attrName + "width", ExtentRender.renderCssAttributeValue(size));
		}
		if (color != null) {
			cssStyle.setAttribute("border-" + attrName + "color", ColorKit.makeCSSColor(color));
		}
		if ((size != null && size.getValue() > 0) || color != null) {
			cssStyle.setAttribute("border-" + attrName + "style", borderStyleValue(style));
		} else {
			if (style == Border.STYLE_NONE) {
				cssStyle.setAttribute("border-" + attrName + "style", borderStyleValue(style));
			}
		}
	}

	/**
	 * Returns the CSS border style value for a given Border.STYLE_XXX constant.
	 */
	private static final String borderStyleValue(int style) {
		switch (style) {
		case Border.STYLE_NONE:
			return "none";
		case Border.STYLE_INSET:
			return "inset";
		case Border.STYLE_OUTSET:
			return "outset";
		case Border.STYLE_SOLID:
			return "solid";
		case Border.STYLE_DOTTED:
			return "dotted";
		case Border.STYLE_DASHED:
			return "dashed";
		case Border.STYLE_GROOVE:
			return "groove";
		case Border.STYLE_RIDGE:
			return "ridge";
		case Border.STYLE_DOUBLE:
			return "double";
		default:
			return "none";
		}
	}

	private static boolean eqC(Color clr1, Color clr2) {
		if (clr1 == clr2)
			return true;
		if (clr1 != null)
			return clr1.equals(clr2);
		return clr2.equals(clr1);
	}

	private static boolean eqE(Extent extent1, Extent extent2) {
		if (extent1 == extent2)
			return true;
		if (extent1 != null)
			return extent1.equals(extent2);
		return extent2.equals(extent1);
	}

	/**
	 * Returns the Rendered property of a component using a Style as a default
	 * source
	 */
	private static Object getRP(Component component, String propertyName, Style fallbackStyle) {
		Object value = component.getRenderProperty(propertyName);
		if (value == null && fallbackStyle != null) {
			value = fallbackStyle.getProperty(propertyName);
		}
		return value;
	}

	/**
	 * Returns the Rendered property of a Delegateable using a Style as a
	 * default source
	 */
	private static Object getRP(Delegateable delegateable, String propertyName, Style fallbackStyle) {
		Object value = delegateable.getRenderProperty(propertyName);
		if (value == null && fallbackStyle != null) {
			value = fallbackStyle.getProperty(propertyName);
		}
		return value;
	}

	/**
	 * Returns the Rendered property of a able with a default int
	 */
	private static int getRP(Delegateable delegateable, String propertyName, Style fallbackStyle, int fallbackValue) {
		Object value = delegateable.getRenderProperty(propertyName);
		if (value == null && fallbackStyle != null) {
			value = fallbackStyle.getProperty(propertyName);
		}
		if (value == null) {
			value = new Integer(fallbackValue);
		}
		return ((Integer) value).intValue();
	}

	/**
	 * <code>DirectImageRenderSupport</code> is used to directly return a
	 * known image.  This remoevs the need for the silly (my opinion) ImageRenderSupport
	 * interface that does nothing but return images from components.
	 */
	private static class DirectImageRenderSupport implements ImageRenderSupport {
		private ImageReference image;

		private DirectImageRenderSupport(ImageReference image) {
			this.image = image;
		}

		public ImageReference getImage(Component component, String imageId) {
			return image;
		}
	};

	/**
	 * Renders a fix for the IE6 "hasLayout" bug to a CssStyle object. Ensures
	 * that the provided style will cause any element on which it is applied to
	 * have "hasLayout = true". This overcomes numerous IE6 rendering bugs
	 * including randomly disappearing elements.
	 * 
	 * It should be used on position:absolute/position:relative elements to
	 * prevent IE6 layout bugs.
	 * 
	 * This code as dontaed by Jem Richardson, who truly is a web developer
	 * "gem".
	 */
	public static void layoutFix(RenderContext rc, CssStyle style) {
		if (isClientIE6(rc)) {
			// do we have position:absolute or position:relative in the style.
			if ("absolute".equalsIgnoreCase(style.getAttribute("position")) || "relative".equalsIgnoreCase(style.getAttribute("position"))) {
				// zoom is proprietary IE CSS crap that forces hasLayout to true
				// in all situations
				//
				// More info here 
				//     http://www.satzansatz.de/cssd/onhavinglayout.html
				style.setAttribute("zoom", "1");
			}
		}
	}

	/**
	 * @returns boolean indicating whether the client is using the IE6 browser.
	 */
	public static boolean isClientIE6(RenderContext rc) {
		ClientProperties props = rc.getContainerInstance().getClientProperties();
		if (props.getBoolean(ClientProperties.BROWSER_INTERNET_EXPLORER)) {
			if ("6".equals(props.getString(ClientProperties.BROWSER_VERSION_MAJOR))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @returns boolean indicating whether the client is using the IE browser.
	 */
	public static boolean isClientIE(RenderContext rc) {
		ClientProperties props = rc.getContainerInstance().getClientProperties();
		if (props.getBoolean(ClientProperties.BROWSER_INTERNET_EXPLORER)) {
			return true;
		}
		return false;
	}
	
	/**
	 * This will create a <code>CssStyle</code> object that can be used on an Element that encloses
	 * a child component that has <code>DislayLayoutData</code> associated with it.
	 * 
	 * @param rc - The RenderingContext in play
	 * @param child - the child component
	 * 
	 * @return A CssStyle object built and ready.  It will have no attributes if the child
	 * does not have any <code>DislayLayoutData</code> associated with it.
	 */
	public static CssStyle itsDisplayLayoutData(RenderContext rc, Component child) {
		CssStyleEx style = new CssStyleEx();
		LayoutData layoutData = (LayoutData) child.getRenderProperty(Component.PROPERTY_LAYOUT_DATA);
		if (layoutData instanceof DisplayLayoutData) {
			renderDisplayLayoutDataToStyle(rc, (DisplayLayoutData) layoutData, style);
		}
		return style;
	}
	
	
	/**
	 * This will create a <code>CssStyle</code> object that can be used on an Element that encloses
	 * a child XhtmlFragment that has <code>DislayLayoutData</code> associated with it.
	 * 
	 * @param rc - The RenderingContext in play
	 * @param child - the child XhtmlFragment
	 * 
	 * @return A CssStyle object built and ready.  It will have no attributes if the child
	 * does not have any <code>DislayLayoutData</code> associated with it.
	 */
	public static CssStyle itsDisplayLayoutData(RenderContext rc, XhtmlFragment child) {
		CssStyleEx style = new CssStyleEx();
		LayoutData layoutData = child.getLayoutData();
		if (layoutData instanceof DisplayLayoutData) {
			renderDisplayLayoutDataToStyle(rc, (DisplayLayoutData) layoutData, style);
		}
		return style;
	}
	
	/**
	 * Renders the attributes of a DisplayLayoutData object to a CssStyle
	 */
	private static void renderDisplayLayoutDataToStyle(RenderContext rc, DisplayLayoutData dld, CssStyle style) {
		
		// ScrollableDisplayLayoutData is handled becaue of the asAble magic function
		// just in case you are wondering!
		Render.asAble(style, dld);
		Render.asColor(style, dld.getForeground(), "color");
		Render.asColor(style, dld.getBackground(), "background-color");
		Render.asFont(style, dld.getFont());
		Render.asBackgroundImageable(rc, style, dld);
		
		if (dld.isInlineLayout()) {
			style.setAttribute("display","inline");
		}
	}
	
	
	/**
	 * This will apply selected attributes on the Element that encloses
	 * a child Component that has <code>DislayLayoutData</code> associated with it.
	 * 
	 * @param rc - The RenderingContext in play
	 * @param child - the child Component
	 * @param element - the Element to render attributes to
	 */
	public static void itsDisplayLayoutData(RenderContext rc, Component child, Element element) {
		LayoutData layoutData = (LayoutData) child.getRenderProperty(Component.PROPERTY_LAYOUT_DATA);
		if (layoutData instanceof DisplayLayoutData) {
			renderDisplayLayoutDataToElement(rc, (DisplayLayoutData) layoutData, element);
		}
	}
	
	
	/**
	 * This will apply selected attributes on the Element that encloses
	 * a child XhtmlFragment that has <code>DislayLayoutData</code> associated with it.
	 * 
	 * @param rc - The RenderingContext in play
	 * @param child - the child XhtmlFragment
	 * @param element - the Element to render attributes to
	 */
	public static void itsDisplayLayoutData(RenderContext rc, XhtmlFragment child, Element element) {
		LayoutData layoutData = child.getLayoutData();
		if (layoutData instanceof DisplayLayoutData) {
			renderDisplayLayoutDataToElement(rc, (DisplayLayoutData) layoutData, element);
		}
	}
	
	
	/**
	 * Renders selected attributes of a DisplayLayoutData to an Element
	 */
	private static void renderDisplayLayoutDataToElement(RenderContext rc, DisplayLayoutData dld, Element element) {
		if (dld.getToolTipText() != null) {
			element.setAttribute("title", dld.getToolTipText());
		}
	}
}