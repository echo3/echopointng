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
 
/*
 * This file was made part of the EchoPoint library on the
 * 12/03/2004.  It is based largely on the Echo 
 * ResourceImageReference however it has been retrofitted
 * to use java.net.URLs.
 */
 
 

package echopointng.image;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import nextapp.echo2.app.ApplicationInstance;
import nextapp.echo2.app.Extent;
import nextapp.echo2.app.StreamImageReference;

/**
 * <code>URLImageReference</code> provides an <code>ImageReference</code>
 * that gets its image data from an <code>java.net.URL</code>.
 */
public class URLImageReference extends StreamImageReference { 

    private static final Map extContentType = new HashMap();
    static {
        extContentType.put("gif",  "image/gif");
        extContentType.put("png",  "image/png");
        extContentType.put("jpeg", "image/jpeg");
        extContentType.put("jpg",  "image/jpg");
        extContentType.put("bmp",  "image/bmp");
        
		extContentType.put("cod",	"image/cis-cod");
		extContentType.put("cpi",	"image/cpi");
		extContentType.put("fif",	"image/fif");
		extContentType.put("ief",	"image/ief");
		extContentType.put("rip",	"image/rip");
		extContentType.put("tif",	"image/tiff");
		extContentType.put("tiff",	"image/tiff");
		extContentType.put("svh",	"image/svh");
		extContentType.put("mcf",	"image/vasa");
		extContentType.put("svf",	"image/vnd");
		extContentType.put("dwg",	"image/vnd");
		extContentType.put("dxf",	"image/vnd");
		extContentType.put("wbmp",	"image/vnd.wap.wbmp");
		extContentType.put("xif",	"image/vnd.xiff");
		extContentType.put("wi",	"image/wavelet");
		extContentType.put("clp",	"image/x-clp");
		extContentType.put("ras",	"image/x-cmu-raster");
		extContentType.put("cmx",	"image/x-cmx");
		extContentType.put("emf",	"image/x-emf");
		extContentType.put("etf",	"image/x-etf");
		extContentType.put("fpx",	"image/x-fpx");
		extContentType.put("fh5",	"image/x-freehand");
		extContentType.put("fh4",	"image/x-freehand");        
		extContentType.put("fhc",	"image/x-freehand");
		extContentType.put("cut",	"image/x-halo-cut");
		extContentType.put("jps",	"image/x-jps");
		extContentType.put("dsf",	"image/x-mgx-dsf");                  
		extContentType.put("pnm",	"image/x-portable-anymap");
		extContentType.put("pbm",	"image/x-portable-bitmap");
		extContentType.put("pgm",	"image/x-portable-graymap");
		extContentType.put("ppm",	"image/x-portable-pixmap");
		extContentType.put("qti",	"image/x-quicktime");
		extContentType.put("rgb",	"image/x-rgb");
		extContentType.put("wmf",	"image/x-wmf");
		extContentType.put("xbm",	"image/x-xbitmap");
		extContentType.put("xpm",	"image/x-xpixmap");              
		extContentType.put("xwd",	"image/x-xwindowdump");              
    }
    
	private URL resourceURL;
	private String contentType;
	private Extent width;
	private Extent height;
	private String id = ApplicationInstance.generateSystemId();
    
    /**
     * Automatically determines the content type based on the name of a 
     * resource.
     */
    private static String determineContentType(URL imageURL) {
        String contentType;
    
        // Determine content type.
        String resourceName = imageURL.toExternalForm();
        int extensionDelimiterPosition = resourceName.lastIndexOf(".");
        if (extensionDelimiterPosition == -1) {
            throw new IllegalArgumentException("Invalid content type (URL resource has no extension: " + resourceName + ")");
        } else {
            String extension = resourceName.substring(extensionDelimiterPosition + 1).toLowerCase(Locale.ENGLISH);
            contentType = (String) extContentType.get(extension);
            if (contentType == null) {
                throw new IllegalArgumentException("Invalid content type (no matching content type: " + resourceName + ")");
            }
        }
        
        return contentType;
    }

    /**
     * Creates an <code>URLImageReference</code> retrieved from the provided URL.
     * <p>
     * The content type of the image will be determined based on the 
     * extension of the URLs external form.
     */
    public URLImageReference(URL imageURL) {
        this(imageURL, determineContentType(imageURL), null, null);
    }

    /**
     * Creates an <code>URLImageReference</code> retrieved from the provided URL
     * and the specified content type.
     */
    public URLImageReference(URL imageURL, String contentType) {
        this(imageURL, contentType, null, null);
    }

    /**
     * Creates an <code>URLImageReference</code> retrieved from the provided URL
     * with the specified width and height.  
     * <p>
     * The content type of the image will be determined based on the 
     * extension of the URLs external form.
     */
    public URLImageReference(URL imageURL, Extent width, Extent height) {
        this(imageURL, determineContentType(imageURL), width, height);
    }

	/**
	 * Creates an <code>URLImageReference</code> retrieved from the provided URL
	 * with the specified content type, width and height.  
	 */
    public URLImageReference(URL imageURL, String contentType, Extent width, Extent height) {
        super();
        
        this.resourceURL = imageURL;
        this.contentType = contentType;
        this.width = width;
        this.height = height;
    }
    
	/**
	 * @see nextapp.echo2.app.RenderIdSupport#getRenderId()
	 */
	public String getRenderId() {
		return id;
	}

    /**
     * @see nextapp.echo2.app.StreamImageReference#getContentType()
     */
    public String getContentType() {
        return contentType;
    }
    
    /**
     * @see nextapp.echo2.app.ImageReference#getHeight()
     */
    public Extent getHeight() {
        return height;
    }
    
    /**
     * @see nextapp.echo2.app.ImageReference#getWidth()
     */
    public Extent getWidth() {
        return width;
    }
    
	/**
	 * Not supported.
	 */
	public void update() { }

	private static final int BUFFER_SIZE = 4096;
    
    /**
     * @see nextapp.echo2.app.StreamImageReference#render(java.io.OutputStream)
     */
    public void render(OutputStream out) throws IOException {

        InputStream in = null;
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = 0;
        
		in = resourceURL.openStream();
        do {
            bytesRead = in.read(buffer);
            if (bytesRead > 0) {
                out.write(buffer, 0, bytesRead);
            }
        } while (bytesRead > 0);
        try {
			in.close();
		} catch (IOException ioe) {
			// do we care if we cant close it!
		}
    }

}
