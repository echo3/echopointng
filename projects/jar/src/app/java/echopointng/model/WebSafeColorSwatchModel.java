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
package echopointng.model;

import java.util.ArrayList;
import java.util.List;

import echopointng.util.ColorKit;
import nextapp.echo2.app.Color;

/**
 * <code>WebSafeColorSwatchModel</code> generates the Netscpae defined safe
 * web colors.  This fits neatly into a 17x12 row of colors.
 * 
 */
public class WebSafeColorSwatchModel implements ColorSwatchModel {

	private static Color[] WEB_SAFE_COLORS;
	static {
		List colorList = new ArrayList();
		for (int r = 15; r >= 0; r -= 3) {
			for (int g = 0; g <= 15; g += 3) {
				for (int b = 0; b <= 15; b += 3) {
					int red = (r * 16) + r;
					int green = (g * 16) + g;
					int blue = (b * 16) + b;
					Color color = ColorKit.clr(red, green, blue);
					colorList.add(color);
				}
			}
		}
		// there are 5 spots left in a 17x12 grid so add white/grey/black etc
		// here
		colorList.add(ColorKit.clr("#FFFFFF"));
		colorList.add(ColorKit.clr("#D3D3D3"));
		colorList.add(ColorKit.clr("#A9A9A9"));
		colorList.add(ColorKit.clr("#778899"));
		colorList.add(ColorKit.clr("#000000"));

		WEB_SAFE_COLORS = (Color[]) colorList.toArray(new Color[colorList
				.size()]);

	}

	/**
	 * @see echopointng.model.ColorSwatchModel#getColorSwatches()
	 */
	public Color[] getColorSwatches() {
		return WEB_SAFE_COLORS;
	}

	/**
	 * @see echopointng.model.ColorSwatchModel#getColorDescription(nextapp.echo2.app.Color)
	 */
	public String getColorDescription(Color color) {
		return null;
	}

}
