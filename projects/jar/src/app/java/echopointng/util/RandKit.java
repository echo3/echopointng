package echopointng.util;
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

/**
 * A utility class to help with Random number generation.
 *
 * @author Brad Baker 
 */

import java.util.Calendar;
import java.util.Random;

public class RandKit {

	/** not instantiable */
	private RandKit() {
	}

	private static Random rn = new Random();

	/**
	 * get random numbers in a range, lo <= number <= hi
	 */
	public static double rand(double lo, double hi) {

		if (lo > hi) {
			double save = lo;
			lo = hi;
			hi = save;
		}
		double range = hi - lo + 1;

		// compute a fraction of the range, 0 <= frac < range
		double frac = (range * rn.nextDouble());

		// add the fraction to the lo value and return the sum
		return (frac + lo);
	}
	/**
	 * get random numbers in a range, lo <= number <= hi
	 */
	public static int rand(int lo, int hi) {
		return (int) rand((long) lo, (long) hi);
	}
	/**
	 * get random numbers in a range, lo <= number <= hi
	 */
	public static long rand(long lo, long hi) {

		if (lo > hi) {
			long save = lo;
			lo = hi;
			hi = save;
		}
		long range = hi - lo + 1;

		// compute a fraction of the range, 0 <= frac < range
		long frac = (long) (range * rn.nextDouble());

		// add the fraction to the lo value and return the sum
		return (frac + lo);
	}
	/**
	 * Returns a new Calendar object which is between start and end
	 */
	public static Calendar rand(Calendar start, Calendar end) {
		if (start.after(end)) {
			Calendar temp = start;
			start = end;
			end = temp;
		}
		long diff = end.getTime().getTime() - start.getTime().getTime();
		long daysDiff = diff / (1000 * 60 * 60 * 24);

		int delta = rand(0, (int) daysDiff);

		Calendar newCal = Calendar.getInstance();
		newCal.setTime(start.getTime());
		newCal.setTimeZone(start.getTimeZone());

		newCal.add(Calendar.DAY_OF_MONTH, delta);
		newCal.add(Calendar.HOUR, rand(0, 23));
		newCal.add(Calendar.MINUTE, rand(0, 59));
		newCal.add(Calendar.SECOND, rand(0, 59));

		// check range cause we might random picked value
		// greater than the range.
		if (newCal.after(end)) {
			newCal.setTime(end.getTime());
			newCal.setTimeZone(end.getTimeZone());
		}
		if (newCal.before(start)) {
			newCal.setTime(start.getTime());
			newCal.setTimeZone(start.getTimeZone());
		}

		return newCal;
	}
	/**
	 * Returns a random char from the array of chars
	 */
	public static char roll(char[] chars) {
		int index = rand(0, chars.length - 1);
		return chars[index];
	}
	/**
	 * Returns a random double from the array of doubles
	 */
	public static double roll(double[] doubles) {
		int index = rand(0, doubles.length - 1);
		return doubles[index];
	}
	/**
	 * Returns a random float from the array of floats
	 */
	public static float roll(float[] floats) {
		int index = rand(0, floats.length - 1);
		return floats[index];
	}
	/**
	 * Returns a random int from the array of ints
	 */
	public static int roll(int[] ints) {
		int index = rand(0, ints.length - 1);
		return ints[index];
	}
	/**
	 * Returns a random long from the array of longs
	 */
	public static long roll(long[] longs) {
		int index = rand(0, longs.length - 1);
		return longs[index];
	}
	/**
	 * Returns a random object from the array of objects
	 */
	public static Object roll(Object[] arr) {
		int index = rand(0, arr.length - 1);
		return arr[index];
	}
	/**
	 * Returns a random String from the array of Strings
	 */
	public static String roll(String[] arrStrings) {
		int index = rand(0, arrStrings.length - 1);
		return arrStrings[index];
	}
	/**
	 * Returns a random boolean from the array of booleans
	 */
	public static boolean roll(boolean[] booleans) {
		int index = rand(0, booleans.length - 1);
		return booleans[index];
	}
	/**
	 * Returns true "chance" percent of the time.  Of course chance should
	 * be an integer between 0 and 100.
	 */
	public static boolean roll(int percentageChance) {
		int val = rand(0, 100);
		if (percentageChance > 100)
			percentageChance = 100;
		if (percentageChance < 0)
			percentageChance = 0;

		return val >= percentageChance;
	}
	/**
	 * Returns a random Object from the List of Objects
	 */
	public static Object roll(java.util.List list) {
		int index = rand(0, list.size()-1);
		return list.get(index);
	}
	/**
	 * Returns true of false 50 percent of the time
	 */
	public static boolean roll5050() {
		return (rand(0, 100001) > (100001 / 2));
	}

	/**
	 * This method will return a random sample of the
	 * possibles array into the destination array, without
	 * repeating any options.
	 * 
	 * By providing a destination array, then the eact type of the
	 * returned array can be controlled.
	 * 
	 * @param possibles - an array of possible choices
	 * @param destination - the destination array for random choices
	 * @return - the destination array itself
	 */
	public static Object[] rand(Object[] possibles, Object[] destination) {
		if (possibles == null || destination == null) {
			return destination;
		}
		int maxTimes = Math.min(possibles.length,destination.length);
		int index = 0;
		int count = 0;
		while (true) {
			count++;
			if (index >= maxTimes) {
				break;
			}
			Object choice = roll(possibles);
			boolean found = false;
			for (int j = 0; j < destination.length; j++) {
				if (choice == destination[j]) {
					found = true;
					break;
				}
			}
			if (! found) {
				destination[index] = choice;
				index++;
			}
			if (count > maxTimes * 100) {
				break;
			}
		}
		return destination;
	}
}
