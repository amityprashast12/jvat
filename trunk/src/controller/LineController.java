/*
 *   Copyright 2009 Vassaf Emre Inanli
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package controller;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import javax.swing.event.MouseInputAdapter;
import tools.DrawPanel;

public class LineController extends MouseInputAdapter {
	DrawPanel view;
	int S = 6;
	Rectangle net = new Rectangle(S, S);
	int selectedIndex = -1;
	Point2D.Double offset = new Point2D.Double();
	boolean dragging = false;

	public LineController(DrawPanel dal) {
		view = dal;
	}

	public void mousePressed(MouseEvent e) {
		Point p = e.getPoint();
		System.out.println("showRects " + model.MenuModel.getshowRects());
		if (!model.MenuModel.getshowRects()) {
			net.setFrameFromCenter(p.x, p.y, p.x + S / 2, p.y + S / 2);
			for (int alc = 0; alc < view.getLCSize(); alc++) {
				if ((view.getLCobject(alc, 2).getX() == 9876.1)
						&& (view.getLCobject(alc, 2).getY() == 9876.1)) {

					Line2D.Double line = new Line2D.Double(view.getLCobject(
							alc, 0).getX(), view.getLCobject(alc, 0).getY(),
							view.getLCobject(alc, 1).getX(), view.getLCobject(
									alc, 1).getY());

					if (line.intersects(net)) {
						view.setShowRects(true);
					}
				} else {
					QuadCurve2D.Double quad = new QuadCurve2D.Double(view
							.getLCobject(alc, 0).getX(), view.getLCobject(alc,
							0).getY(), view.getLCobject(alc, 2).getX(), view
							.getLCobject(alc, 2).getY(), view.getLCobject(alc,
							1).getX(), view.getLCobject(alc, 1).getY());
					if (quad.intersects(net)) {
						view.setShowRects(true);
					}
				}

			}

		} else {
			// Rectangle2D.Double[] rects = view.getRects();

			for (int j = 0; j < view.getRects().length; j++) {
				if (view.getRects()[j].contains(p)) {
					System.out.println("Rectangle " + view.getRects()[j]
							+ " selected.");
					selectedIndex = j;
					// System.out.println(j);
					offset.x = p.x - view.getRects()[j].x;
					offset.y = p.y - view.getRects()[j].y;
					dragging = true;
				}
			}
			if (selectedIndex == -1) {
				view.setShowRects(false);
			}
		}
	}

	public void mouseReleased(MouseEvent e) {
		selectedIndex = -1;
		dragging = false;
	}

	public void mouseDragged(MouseEvent e) {
		if (dragging) {
			double x = e.getX() - offset.x;
			double y = e.getY() - offset.y;
			view.setRect(selectedIndex, x, y);
		}
	}
}
