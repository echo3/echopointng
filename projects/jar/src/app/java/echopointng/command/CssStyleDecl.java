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
package echopointng.command;

import nextapp.echo2.app.ApplicationInstance;
import nextapp.echo2.app.Command;
import nextapp.echo2.app.RenderIdSupport;

/**
 * <code>CssStyleDecl</code> is a abstract base class for Commands that
 * implement CSS style and style sheet capabilities.
 */
public abstract class CssStyleDecl implements Command, RenderIdSupport {

	private String id;

	private String media;

	/**
	 * Constructs a <code>CssStyleDecl</code> object
	 */
	protected CssStyleDecl() {
		this.id = ApplicationInstance.generateSystemId();
		media = "all";
	}

	/**
	 * Generates a unique id for the Command so it can be addressed
	 * later.
	 * 
	 * @see nextapp.echo2.app.RenderIdSupport#getRenderId()
	 */
	public String getRenderId() {
		return id;
	}

	/**
	 * Sets the media for the CSS Style information The following types are
	 * currently know to most CSS implementations
	 * 
	 * <ul>
	 * <li><code>screen</code> - Intended for non-paged computer screens.
	 * <li><code>tty</code> - Intended for media using a fixed-pitch character grid, such as
	 * teletypes, terminals, or portable devices with limited display
	 * capabilities.</li>
	 * <li><code>tv</code> - Intended for television-type devices (low resolution, color,
	 * limited scrollability).</li>
	 * <li><code>projection</code> - Intended for projectors.</li>
	 * <li><code>handheld</code> - Intended for handheld devices (small screen, monochrome,
	 * bitmapped graphics, limited bandwidth).</li>
	 * <li><code>print</code> - Intended for paged, opaque material and for documents viewed
	 * on screen in print preview mode.</li>
	 * <li><code>braille</code> - Intended for braille tactile feedback devices.</li>
	 * <li><code>aural</code> - Intended for speech synthesizers.</li>
	 * <li><code>all</code> Suitable for all devices.</li>
	 * </ul>
	 * 
	 * @return the media
	 */
	public String getMedia() {
		return media;
	}

	/**
	 * Sets the media that the CSS style information applies to. The following
	 * types are currently know to most CSS implementations
	 * 
	 * <ul>
	 * <li><code>screen</code> - Intended for non-paged computer screens.
	 * <li><code>tty</code> - Intended for media using a fixed-pitch character grid, such as
	 * teletypes, terminals, or portable devices with limited display
	 * capabilities.</li>
	 * <li><code>tv</code> - Intended for television-type devices (low resolution, color,
	 * limited scrollability).</li>
	 * <li><code>projection</code> - Intended for projectors.</li>
	 * <li><code>handheld</code> - Intended for handheld devices (small screen, monochrome,
	 * bitmapped graphics, limited bandwidth).</li>
	 * <li><code>print</code> - Intended for paged, opaque material and for documents viewed
	 * on screen in print preview mode.</li>
	 * <li><code>braille</code> - Intended for braille tactile feedback devices.</li>
	 * <li><code>aural</code> - Intended for speech synthesizers.</li>
	 * <li><code>all</code> Suitable for all devices.</li>
	 * </ul>
	 * 
	 * @param media -
	 *            the media to use
	 */
	public void setMedia(String media) {
		this.media = media;
	}
}
