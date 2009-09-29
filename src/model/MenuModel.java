/*
   Copyright [2009] [Vassaf Emre Inanli]

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.net.URL;
import java.util.ArrayList;

public class MenuModel {

	static int currentframe;
	static int linecount = 0;
	static int quadcount = 0;
	private static int sWidth = 3;
	double S = 10.0;
	boolean initialRect = false;
	boolean initialLine = false;
	static boolean isQuadSelected = false;
	static boolean isLineSelected = false;
	static boolean isMovieLoaded = false;
	static boolean isfirstObject = true;
	static boolean isZselected = true;
	static boolean showRects = false;
	boolean stateTransitionOK = true;
	boolean firstTime = true;
	static boolean firstClick = true;
	static int strCount = 0;
	static URL mediaURL = null;
	static boolean isStartSelected = false;
	boolean isEndSelected = false;
	private static ArrayList<ArrayList<Point2D>> coordinates = new ArrayList<ArrayList<Point2D>>();
	private static ArrayList<String> strCoor = new ArrayList<String>();
	private static ArrayList<Line2D> edCoor = new ArrayList<Line2D>();
	private static int totalFrames;
	Graphics2D g2d;
	private static int x, y;
	private static int strx = 100;
	private static int stry = 100;
	private static int strx2 = 0, stry2 = 0;
	private static int objectcount = 0;
	private static Color lastc = Color.blue;

	// Model Accessors
	public static boolean getshowRects() {
		return showRects;
	}

	public static int getx() {
		return x;
	}

	public static int gety() {
		return y;
	}

	public static int gettotalFrames() {
		return totalFrames;
	}

	public static int getsWidth() {
		return sWidth;
	}

	public static int getstrx() {
		return strx;
	}

	public static int getstry() {
		return stry;
	}

	public static int getstrx2() {
		return strx2;
	}

	public static int getstry2() {
		return stry2;
	}

	public static int getCurrentFrame() {
		return currentframe;
	}

	public static URL getURL() {
		return mediaURL;
	}

	public static int getlinecount() {
		return linecount;
	}

	public static ArrayList<ArrayList<Point2D>> getcoordinates() {
		return coordinates;
	}

	public static ArrayList<String> getstrCoor() {
		return strCoor;
	}

	public static ArrayList<Line2D> getedCoor() {
		return edCoor;
	}

	public boolean getisStartSelected() {
		return isStartSelected;
	}

	public static boolean getfirstClick() {
		return firstClick;
	}

	public static int getobjectcount() {
		return objectcount;
	}

	public static int getquadcount() {
		return quadcount;
	}

	public static boolean getisZselected() {
		return isZselected;
	}

	public static Color getlastc() {
		return lastc;
	}

	public static boolean getisfirstObject() {
		return isfirstObject;
	}

	public static int getstrCount() {
		return strCount;
	}

	// Model Mutators
	public static void setx(int val) {
		x = val;
	}

	public static void sety(int val) {
		y = val;
	}

	public static void settotalFrames(int val) {
		totalFrames = val;
	}

	public static void setstrx(int val) {
		strx = val;
	}

	public static void setstrx2(int val) {
		strx2 = val;
	}

	public static void setstry(int val) {
		stry = val;
	}

	public static void setstry2(int val) {
		stry2 = val;
	}

	public static void setlastc(Color nc) {
		lastc = nc;
	}

	public static void setsWidth(int val) {
		sWidth = val;
	}

	public static void setobjectcount(int val) {
		objectcount = val;
	}

	public void setcoordinates(ArrayList<ArrayList<Point2D>> val) {
		coordinates = val;
	}

	public void setstrCoor(ArrayList<String> val) {
		strCoor = val;
	}

	public void setedCoor(ArrayList<Line2D> val) {
		edCoor = val;
	}

	public static void setCurrentFrame(int val) {
		currentframe = val;
	}

	public static void setlinecount(int number) {
		linecount = number;
	}

	public static void setquadcount(int number) {
		quadcount = number;
	}

	public void setinitialRect(boolean tf) {
		initialRect = tf;
	}

	public void setinitialLine(boolean tf) {
		initialLine = tf;
	}

	public static void setshowRects(boolean tf) {
		showRects = tf;
	}

	public static void setisQuadSelected(boolean tf) {
		isQuadSelected = tf;
	}

	public static void setisLineSelected(boolean tf) {
		isLineSelected = tf;
	}

	public static void setisMovieLoaded(boolean tf) {
		isMovieLoaded = tf;
	}

	public static void setisfirstObject(boolean tf) {
		isfirstObject = tf;
	}

	public static void setisZselected(boolean tf) {
		isZselected = tf;
	}

	public void setShowRects(boolean tf) {
		showRects = tf;
	}

	public void setfirstTime(boolean tf) {
		firstTime = tf;
	}

	public static void setfirstClick(boolean tf) {
		firstClick = tf;
	}

	public static void setstrCount(int val) {
		strCount = val;
	}

	public static void setURL(URL nurl) {
		mediaURL = nurl;
	}

	public static void setisStartSelected(boolean tf) {
		isStartSelected = tf;
	}

}
