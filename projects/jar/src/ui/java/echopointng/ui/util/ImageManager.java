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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import nextapp.echo2.app.AwtImageReference;
import nextapp.echo2.app.Component;
import nextapp.echo2.app.FillImage;
import nextapp.echo2.app.HttpImageReference;
import nextapp.echo2.app.ImageReference;
import nextapp.echo2.app.StreamImageReference;
import nextapp.echo2.webcontainer.ContainerInstance;
import nextapp.echo2.webcontainer.RenderContext;
import nextapp.echo2.webcontainer.RenderState;
import nextapp.echo2.webcontainer.image.AwtImageService;
import nextapp.echo2.webcontainer.image.StreamImageService;
import nextapp.echo2.webcontainer.propertyrender.ExtentRender;
import nextapp.echo2.webrender.WebRenderServlet;
import nextapp.echo2.webrender.output.CssStyle;

import org.w3c.dom.Element;

/**
 * <code>ImageManager</code> is used to manage <code>ImageReference</code>
 * 's within the EPNG code.
 * <p>
 * The base nextapp.echo2.webcontainer.image.ImageRenderSupport does not do a
 * <i>simple </i> enough job of managing images as it uses a component based
 * <i>lookup in a callback </i> to implement image serving.
 * <p>
 * This <code>ImageManager</code> class will allow URI's for images to be
 * generated as well as IMG HTML tag elements to be created without needing the
 * component as the lookup context.
 * <p>
 * All that is needed is the <code>ImageReference</code> itself.
 * <p>
 * And instance of this class can be used to store images against a logical name
 * via the setImage() and getImage() methods.
 * 
 * <code>ImageManager</code> implements RenderState and hence can be stored
 * against a given renderer per component.
 */

public class ImageManager implements RenderState {

	public static interface ImageReferenceURIHandler {
		/**
		 * This method is called to return an URI for the given image. if the
		 * handler can handle this type of <code>ImageReference</code> then it
		 * should return null.
		 * 
		 * @param containerInstance -
		 *            the ContainerInstance that is current in play
		 * @param imageReference -
		 *            the ImageReference to return a URI for
		 * 
		 * @return the URI that can be used to serve up the
		 *         <code>ImageReference</code> or null if the handler can
		 *         handle this type of <code>ImageReference</code>
		 */
		public String getImageURI(ContainerInstance containerInstance, ImageReference imageReference);
	}

	private static Set handlerSet = new HashSet();

	/**
	 * Registers a <code>ImageReferenceURIHandler</code> to the static set of
	 * handlers
	 * 
	 * @param handler -
	 *            the handler to add
	 */
	public static void registerURIHandler(ImageReferenceURIHandler handler) {
		handlerSet.add(handler);
	}

	/**
	 * Deregisters a <code>ImageReferenceURIHandler</code> from the static set
	 * of handlers
	 * 
	 * @param handler -
	 *            the handler to deregister
	 */
	public static void deregisterURIHandler(ImageReferenceURIHandler handler) {
		handlerSet.remove(handler);
	}

	private static ImageReferenceURIHandler DEFAULT_HANDLER;
	static {
		// create a default ImageHandler
		DEFAULT_HANDLER = new ImageReferenceURIHandler() {
			/**
			 * @see echopointng.ui.util.ImageManager.ImageReferenceURIHandler#getImageURI(ContainerInstance,
			 *      ImageReference)
			 */
			public String getImageURI(ContainerInstance containerInstance, ImageReference imageReference) {
				if (imageReference instanceof StreamImageReference) {
					containerInstance.getIdTable().register(imageReference);
					return StreamImageService.INSTANCE.createUri(containerInstance, imageReference.getRenderId());

				} else if (imageReference instanceof HttpImageReference) {
					return ((HttpImageReference) imageReference).getUri();

				} else if (imageReference instanceof AwtImageReference) {
					containerInstance.getIdTable().register(imageReference);
					return AwtImageService.INSTANCE.createUri(containerInstance, imageReference.getRenderId());
				} else {
					return null;
				}
			}
		};
	}

	/**
	 * This returns the URI that can be used to serve up the specified
	 * ImageReference.
	 * <p>
	 * 
	 * @param rc -
	 *            the RenderContext to use
	 * @param imageReference -
	 *            the ImageReference in question
	 * @return an URI for the ImageReference
	 */
	public static String getURI(RenderContext rc, ImageReference imageReference) {
		return getURIImpl(rc.getContainerInstance(), imageReference);
	}

	/**
	 * This returns the URI that can be used to serve up the specified
	 * ImageReference.
	 * <p>
	 * 
	 * @param imageReference -
	 *            the ImageReference in question
	 * @return an URI for the ImageReference
	 * 
	 * @throws <code>NullPointerException</code> if there is no action connection
	 *             in place when this call is made.
	 */
	public static String getURI(ImageReference imageReference) {
		ContainerInstance containerInstance = (ContainerInstance) WebRenderServlet.getActiveConnection().getUserInstance();
		return getURIImpl(containerInstance, imageReference);
	}

	/*
	 * The actual implementation that returns a URI for a ImageReference
	 */
	private static String getURIImpl(ContainerInstance containerInstance, ImageReference imageReference) {
		String imageURI = null;
		for (Iterator iter = handlerSet.iterator(); iter.hasNext();) {
			ImageReferenceURIHandler handler = (ImageReferenceURIHandler) iter.next();
			imageURI = handler.getImageURI(containerInstance, imageReference);
			if (imageURI != null) {
				break;
			}
		}
		if (imageURI == null) {
			imageURI = DEFAULT_HANDLER.getImageURI(containerInstance, imageReference);
		}
		return imageURI;
	}

	/**
	 * This returns the URI that can be used to serve up the specified property
	 * if its an ImageReference or FillImage value.
	 * 
	 * @param rc
	 *            the RenderContext to use
	 * @param component -
	 *            the component to look up render properties in
	 * @param propertyName -
	 *            the name of the property
	 * @return an URI or null
	 */
	public static String getURI(RenderContext rc, Component component, String propertyName) {
		ImageReference imageRef = getImageRefFromProperty(component, propertyName);
		return getURI(rc, imageRef);
	}

	/**
	 * This returns the URI that can be used to serve up the specified property
	 * if its an ImageReference or FillImage value.
	 * 
	 * @param component -
	 *            the component to look up render properties in
	 * @param propertyName -
	 *            the name of the property
	 * @return an URI or null
	 * 
	 * @throws <code>NullPointerException</code> if there is no action connection
	 *             in place when this call is made.
	 */
	public static String getURI(Component component, String propertyName) {
		ImageReference imageRef = getImageRefFromProperty(component, propertyName);
		ContainerInstance containerInstance = (ContainerInstance) WebRenderServlet.getActiveConnection().getUserInstance();
		return getURIImpl(containerInstance, imageRef);
	}

	/**
	 * This will create an 'img' element for the specified ImageReference.
	 * <p>
	 * This <strong>assumes </strong> that the image URI is based on the
	 * ImageReference.getRenderId().
	 * 
	 * @param rc -
	 *            the RenderContext to use
	 * @param imageRef -
	 *            the ImageReference in question. This must not be null.
	 * 
	 * @return an 'img' element
	 */
	public static Element createImgE(RenderContext rc, ImageReference imageRef) {
		return createImgE(rc, null, imageRef);
	}

	/**
	 * This will create an 'img' element for the specified ImageReference.
	 * <p>
	 * This <strong>assumes </strong> that the image URI is based on the
	 * ImageReference.getRenderId().
	 * <p>
	 * If the CssStyle passsed in is non null, then it will be used to set extra
	 * 'img' tag styles such a "border:none;width:x;height:y". It will only set
	 * these properties if they are not already set in the style.
	 * 
	 * @param rc -
	 *            the RenderContext to use
	 * @param cssStyle -
	 *            a CssStyle to use on the 'img' tag. Can be null
	 * @param imageRef -
	 *            the ImageReference in question. This must not be null.
	 * 
	 * @return an 'img' element
	 */
	public static Element createImgE(RenderContext rc, CssStyle cssStyle, ImageReference imageRef) {
		String imageURI = getURI(rc, imageRef);
		return createImgImpl(rc, cssStyle, imageURI, imageRef);
	}

	/**
	 * Creates an IMG tag by using extracting the value of the named property
	 * from the component. It can handle ImageReference properties as well as
	 * FillImage properties.
	 * 
	 * @param rc -
	 *            the RenderingContext to use
	 * @param cssStyle -
	 *            the CssStyle to use if applicable.
	 * @param component -
	 *            the component whose property will be looked up
	 * @param propertyName -
	 *            the name of the property
	 * @return
	 */
	public static Element createImgEFromProperty(RenderContext rc, CssStyle cssStyle, Component component, String propertyName) {
		ImageReference imageRef = getImageRefFromProperty(component, propertyName);
		String imageURI = getURI(rc, imageRef);
		return createImgImpl(rc, cssStyle, imageURI, imageRef);
	}

	/**
	 * @see ImageManager#createImgEFromProperty(RenderContext, CssStyle,
	 *      Component, String)
	 */
	public static Element createImgEFromProperty(RenderContext rc, Component component, String propertyName) {
		return createImgEFromProperty(rc, null, component, propertyName);
	}

	/*
	 * Implementation that can handle ImageReference and FillImages
	 */
	private static ImageReference getImageRefFromProperty(Component component, String propertyName) {
		ImageReference imageRef = null;
		Object propertyValue = component.getRenderProperty(propertyName);
		if (propertyValue instanceof ImageReference) {
			imageRef = (ImageReference) propertyValue;
		} else if (propertyValue instanceof FillImage) {
			imageRef = ((FillImage) propertyValue).getImage();
		}
		return imageRef;
	}

	/*
	 * The implementation of creating an img element.
	 */
	private static Element createImgImpl(RenderContext rc, CssStyle cssStyle, String imageURI, ImageReference imageRef) {
		Element imgElement = rc.getServerMessage().getDocument().createElement("img");
		imgElement.setAttribute("src", imageURI);
		if (cssStyle == null) {
			cssStyle = new CssStyle();
		}
		if (cssStyle.getAttribute("border") == null) {
			cssStyle.setAttribute("border", "none");
		}
		if (imageRef != null) {
			if (imageRef.getHeight() != null && cssStyle.getAttribute("height") == null) {
				ExtentRender.renderToStyle(cssStyle, "height", imageRef.getHeight());
			}
			if (imageRef.getWidth() != null && cssStyle.getAttribute("width") == null) {
				ExtentRender.renderToStyle(cssStyle, "width", imageRef.getWidth());
			}
		}
		imgElement.setAttribute("style", cssStyle.renderInline());
		return imgElement;
	}

	private Map imageMap;

	/**
	 * Constructs a <code>ImageManager</code>
	 */
	public ImageManager() {
		imageMap = new HashMap();
	}

	/**
	 * Stores an <code>ImageReference</code> into the
	 * <code>ImageManager</code> using a given imageId.
	 * 
	 * @param imageId -
	 *            the id to use to store the <code>ImageReference</code>
	 * @param imageReference -
	 *            the <code>ImageReference</code> to store
	 */
	public void setImage(String imageId, ImageReference imageReference) {
		imageMap.put(imageId, imageReference);
	}

	/**
	 * Returns an <code>ImageReference</code> from the
	 * <code>ImageManager</code> by imageId
	 * 
	 * @param imageId -
	 *            the id used to store the <code>ImageReference</code> in the
	 *            ImageManager.
	 * @return - the <code>ImageReference</code> or null
	 * 
	 */
	public ImageReference getImage(String imageId) {
		return (ImageReference) imageMap.get(imageId);
	}

	/**
	 * Adds the <code>ImageReference</code> to the <code>ImageManager</code>
	 * via its renderId.
	 * 
	 * @param imageReference -
	 *            the <code>ImageReference</code> to add. If this is null then
	 *            nothing happens.
	 * 
	 */
	public void addImage(ImageReference imageReference) {
		if (imageReference != null) {
			imageMap.put(imageReference.getRenderId(), imageReference);
		}
	}

	/**
	 * Removes the <code>ImageReference</code> from the
	 * <code>ImageManager</code> via its renderId.
	 * 
	 * @param imageReference -
	 *            the <code>ImageReference</code> to remove
	 * 
	 */
	public void removeImage(ImageReference imageReference) {
		if (imageReference != null) {
			imageMap.remove(imageReference.getRenderId());
		}
	}

	/**
	 * Removes all <code>ImageReferences</code> from the
	 * <code>ImageManager</code>
	 * 
	 */
	public void removeAllImages() {
		imageMap.clear();
	}
}
