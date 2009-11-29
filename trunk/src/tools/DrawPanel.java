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

package tools;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import model.MenuModel;

public class DrawPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	Line2D.Double line = new Line2D.Double();
	QuadCurve2D.Double quad = new QuadCurve2D.Double();
	double S = 10.0;
	private static Rectangle2D.Double[] rects;
	double lcx1 = 0;
	double lcy1 = 0;
	double lcx2 = 0;
	double lcy2 = 0;
	double lcx3 = 0;
	double lcy3 = 0;
	Graphics2D g2 = null;
	String str2g = null;
	int strx, stry;

	public DrawPanel() {
		// constructor
	}

	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g2 = (Graphics2D) g;
		// 3 squares per line or quad
		rects = new Rectangle2D.Double[3 * (MenuModel.getcoordinates().size())];
		for (int j = 0; j < rects.length; j++) {
			rects[j] = new Rectangle2D.Double();
		}

		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		for (int i = 0; i < MenuModel.getcoordinates().size(); i++) {
			g2.setPaint(MenuModel.getlastc());
			lcx1 = MenuModel.getcoordinates().get(i).get(0).getX();
			lcy1 = MenuModel.getcoordinates().get(i).get(0).getY();
			lcx2 = MenuModel.getcoordinates().get(i).get(1).getX();
			lcy2 = MenuModel.getcoordinates().get(i).get(1).getY();
			lcx3 = MenuModel.getcoordinates().get(i).get(2).getX();
			lcy3 = MenuModel.getcoordinates().get(i).get(2).getY();

			if ((lcx3 == 9876.1) && (lcy3 == 9876.1)) {
				line = new Line2D.Double(lcx1, lcy1, lcx2, lcy2);
				g2.draw(line);
			} else {
				quad = new QuadCurve2D.Double(lcx1, lcy1, lcx3, lcy3, lcx2,
						lcy2);
				g2.draw(quad);
			}
			rects[i * 3] = new Rectangle2D.Double(lcx1 - S / 2, lcy1 - S / 2,
					S, S);
			rects[(i * 3) + 1] = new Rectangle2D.Double(lcx2 - S / 2, lcy2 - S
					/ 2, S, S);
			if ((lcx3 == 9876.1) && (lcy3 == 9876.1)) {
				rects[(i * 3) + 2] = new Rectangle2D.Double();
				setCenter();
			} else {
				rects[(i * 3) + 2] = new Rectangle2D.Double(lcx3 - S / 2, lcy3
						- S / 2, S, S);
			}

			if (MenuModel.getshowRects()) {
				g2.setPaint(Color.red);
				for (int j = 0; j < rects.length; j++)
					g2.draw(rects[j]);
			}
		}
		for (int edc = 0; edc < MenuModel.getedCoor().size(); edc++) {
			g2.setPaint(MenuModel.getlastc());
			g2.draw(MenuModel.getedCoor().get(edc));
		}
		for (int strc = 0; strc < MenuModel.getstrCoor().size(); strc++) {
			g2.setPaint(MenuModel.getlastc());
			Font f = new Font("Monospaced", Font.PLAIN, 16);
			g2.setFont(f);
			String strcor = MenuModel.getstrCoor().get(strc).toString();
			String[] xandy = strcor.split(",");
			g2.drawString(xandy[0], Integer.parseInt(xandy[1]), Integer
					.parseInt(xandy[2]));
		}

	}

	public Rectangle2D.Double[] getRects() {
		return rects;
	}

	public void setShowRects(boolean show) {
		MenuModel.setshowRects(show);
		repaint();
	}

	public Point2D getLCobject(int lcxx, int lcxxx) {
		return MenuModel.getcoordinates().get(lcxx).get(lcxxx);
	}

	public int getLCSize() {
		return MenuModel.getcoordinates().size();
	}

	// Set rectangles on a line or quadcurve
	public void setRect(int ind, double x, double y) {
		int lc = ind / 3;
		double dy = y - rects[ind].y;
		double dx = x - rects[ind].x;
		if ((MenuModel.getcoordinates().get(lc).get(2).getX() == 9876.1)
				&& (MenuModel.getcoordinates().get(lc).get(2).getY() == 9876.1)) {
			if ((ind == 2) || (ind % 3 == 2)) {
				rects[ind - 2].setFrame(rects[ind - 2].x + dx, rects[ind - 2].y
						+ dy, S, S);
				rects[ind - 1].setFrame(rects[ind - 1].x + dx, rects[ind - 1].y
						+ dy, S, S);
			} else {
				rects[ind].setFrame(x, y, S, S);
			}
		} else {
			if ((ind == 2) || (ind % 3 == 2)) {
				rects[ind].setFrame(rects[ind].x + dx, rects[ind].y + dy, S, S);
			} else {
				rects[ind].setFrame(rects[ind].x + dx, rects[ind].y + dy, S, S);
				if ((ind + 1) % 3 == 2)
					rects[ind + 1].setFrame(rects[ind + 1].x + dx,
							rects[ind + 1].y + dy, S, S);
				else
					rects[ind + 2].setFrame(rects[ind + 2].x + dx,
							rects[ind + 2].y + dy, S, S);
			}

		}
		setLine(ind);
	}

	private void setLine(int RecSel) {
		// determine line number, starts from 0
		int whichline = RecSel / 3;
		if ((MenuModel.getcoordinates().get(whichline).get(2).getX() == 9876.1)
				&& (MenuModel.getcoordinates().get(whichline).get(2).getY() == 9876.1)) {
			if (whichline == 0) {
				MenuModel.getcoordinates().get(whichline).set(0,
						getCenter(rects[whichline]));
				MenuModel.getcoordinates().get(whichline).set(1,
						getCenter(rects[whichline + 1]));

			} else if (whichline > 0) {
				MenuModel.getcoordinates().get(whichline).set(0,
						getCenter(rects[whichline * 3]));
				MenuModel.getcoordinates().get(whichline).set(1,
						getCenter(rects[(whichline * 3) + 1]));
			}
			setCenter();
		} else {
			if ((RecSel != 2) || (RecSel % 3 != 2)) {
				if (whichline == 0) {
					MenuModel.getcoordinates().get(whichline).set(0,
							getCenter(rects[whichline]));
					MenuModel.getcoordinates().get(whichline).set(1,
							getCenter(rects[whichline + 1]));
					MenuModel.getcoordinates().get(whichline).set(2,
							getCenter(rects[whichline + 2]));
				} else {
					MenuModel.getcoordinates().get(whichline).set(0,
							getCenter(rects[whichline * 3]));
					MenuModel.getcoordinates().get(whichline).set(1,
							getCenter(rects[(whichline * 3) + 1]));
					MenuModel.getcoordinates().get(whichline).set(2,
							getCenter(rects[(whichline * 3) + 2]));
				}
			} else {
				if (whichline == 0) {
					MenuModel.getcoordinates().get(whichline).set(2,
							getCenter(rects[whichline + 2]));
				} else {
					MenuModel.getcoordinates().get(whichline).set(2,
							getCenter(rects[(whichline * 3) + 2]));
				}
			}
		}
		repaint();
	}

	private Point2D.Double getCenter(Rectangle2D.Double r) {
		return new Point2D.Double(r.getCenterX(), r.getCenterY());
	}

	private void setCenter() {
		double cx = 0;
		double cy = 0;
		for (int kx = 0; kx < MenuModel.getcoordinates().size(); kx++) {
			if ((MenuModel.getcoordinates().get(kx).get(2).getX() == 9876.1)
					&& (MenuModel.getcoordinates().get(kx).get(2).getY() == 9876.1)) {
				if (kx == 0) {
					cx = MenuModel.getcoordinates().get(kx).get(0).getX()
							+ (MenuModel.getcoordinates().get(kx).get(1).getX() - MenuModel
									.getcoordinates().get(kx).get(0).getX())
							/ 2;
					cy = MenuModel.getcoordinates().get(kx).get(0).getY()
							+ (MenuModel.getcoordinates().get(kx).get(1).getY() - MenuModel
									.getcoordinates().get(kx).get(0).getY())
							/ 2;
					rects[(kx * 2) + 2].setFrameFromCenter(cx, cy, cx + S / 2,
							cy + S / 2);
				} else if (kx > 0) {
					cx = MenuModel.getcoordinates().get(kx).get(0).getX()
							+ (MenuModel.getcoordinates().get(kx).get(1).getX() - MenuModel
									.getcoordinates().get(kx).get(0).getX())
							/ 2;
					cy = MenuModel.getcoordinates().get(kx).get(0).getY()
							+ (MenuModel.getcoordinates().get(kx).get(1).getY() - MenuModel
									.getcoordinates().get(kx).get(0).getY())
							/ 2;
					rects[(kx * 3) + 2].setFrameFromCenter(cx, cy, cx + S / 2,
							cy + S / 2);
				}
			} else {
				cx = MenuModel.getcoordinates().get(kx).get(2).getX();
				cy = MenuModel.getcoordinates().get(kx).get(2).getY();
				if (kx == 0) {
					rects[(kx * 2) + 2].setFrameFromCenter(cx, cy, cx + S / 2,
							cy + S / 2);
				} else {
					rects[(kx * 3) + 2].setFrameFromCenter(cx, cy, cx + S / 2,
							cy + S / 2);
				}
			}
		}
	}
}