/*
 *   Copyright [2009] [Vassaf Emre Inanli]
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.media.CannotRealizeException;
import javax.media.Duration;
import javax.media.Manager;
import javax.media.NoDataSourceException;
import javax.media.NoPlayerException;
import javax.media.NoProcessorException;
import javax.media.Player;
import javax.media.Processor;
import javax.media.Time;
import javax.media.control.FrameGrabbingControl;
import javax.media.control.FramePositioningControl;
import javax.media.control.TrackControl;
import javax.media.format.VideoFormat;
import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedImageAdapter;
import javax.media.protocol.DataSource;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

import blob.Blob;
import blob.BlobDetection;
import blob.EdgeVertex;

import view.MenuView;
import tool.CannyEdgeDetector;
import model.MenuModel;

public class MenuController implements ActionListener, MouseListener {

	private MenuView menuView;
	private MenuModel menuModel;
	private BufferedImage image;
	private FramePositioningControl fpc;
	private DataSource video_in;
	private Processor P = null;
	private VideoFormat vf;
	private FrameGrabbingControl frameGrabber;
	private Player mediaPlayer;
	private Time duration;
	private Component video;
	private Element svgRoot, path, animate, metadata;
	private JOptionPane dialog;
	private Point2D.Double start, end, middle;
	private File tempfile;
	private LineController handler;
	private StringBuilder animatestr = new StringBuilder();
	private StringBuilder pathstr = new StringBuilder();
	private PrintWriter writer;
	private String svgNS = null;
	private String line = null, meta = null;
	private Graphics g;
	private Document doc;
	private SVGGraphics2D svgGenerator;
	private DOMImplementation impl;
	int totalFrames = FramePositioningControl.FRAME_UNKNOWN;
	QuadCurve2D.Double quad = new QuadCurve2D.Double();
	Object waitSync = new Object();

	public MenuController(MenuView view, MenuModel model) {
		this.menuView = view;
		this.setMenuModel(model);
		setDialog(new JOptionPane());
		MenuModel.setCurrentFrame(0);
		menuView.getfwdButton().addActionListener(this);
		menuView.getquadButton().addActionListener(this);
		menuView.getdeleteButton().addActionListener(this);
		menuView.getlineButton().addActionListener(this);
		menuView.getbwdButton().addActionListener(this);
		menuView.getmoveButton().addActionListener(this);
		menuView.getedgeDetectButton().addActionListener(this);
		menuView.getsaveObjButton().addActionListener(this);
		menuView.getclearButton().addActionListener(this);
		menuView.getfileItem1().addActionListener(this);
		menuView.getfileItem2().addActionListener(this);
		menuView.getfileItem3().addActionListener(this);
		menuView.getvideoitem1().addActionListener(this);
		menuView.getvideoitem2().addActionListener(this);
		menuView.getvideoitem3().addActionListener(this);
		menuView.getvideoitem4().addActionListener(this);
		menuView.getvideoitem5().addActionListener(this);
		menuView.getvideoitem6().addActionListener(this);
		menuView.getvideoitem7().addActionListener(this);
		menuView.gettoolsitem1().addActionListener(this);
		menuView.gettoolsitem2().addActionListener(this);
		menuView.gettoolsitem3().addActionListener(this);
		menuView.gettoolsitem4().addActionListener(this);
		menuView.gettoolsitem5().addActionListener(this);
		menuView.gettoolsitem6().addActionListener(this);
		menuView.gettoolsitem7().addActionListener(this);
		menuView.gettoolsitem8().addActionListener(this);
		handler = new LineController(menuView.getdp());
		menuView.getlp().addMouseListener(this);

		// create svg document
		impl = SVGDOMImplementation.getDOMImplementation();
		svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
		doc = (SVGDocument) impl.createDocument(svgNS, "svg", null);
		svgGenerator = new SVGGraphics2D(doc);
		impl = SVGDOMImplementation.getDOMImplementation();
		svgNS = "http://www.w3.org/2000/svg";
		doc = impl.createDocument(svgNS, "svg", null);
		svgRoot = doc.getDocumentElement();
	}

	public void actionPerformed(ActionEvent ae) {
		String command = ae.getActionCommand();
		if (command.equals("Forward")) {
			int dest = fpc.skip(1);
			model.MenuModel
					.setCurrentFrame(model.MenuModel.getCurrentFrame() + 1);
			menuView.gettextframe().setText(
					String.valueOf(model.MenuModel.getCurrentFrame()));
			System.err.println("Step forward " + dest + " frame.");
			menuView.getlp().repaint();
		} else if (command.equals("Backward")) {
			int dest = fpc.skip(-1);
			model.MenuModel
					.setCurrentFrame(model.MenuModel.getCurrentFrame() - 1);
			menuView.gettextframe().setText(
					String.valueOf(model.MenuModel.getCurrentFrame()));
			menuView.getlp().repaint();
			System.err.println("Step backward " + dest + " frame.");
		} else if (command.equals("Open")) {
			JFileChooser fileChooser = new JFileChooser();
			int result = fileChooser.showOpenDialog(null);
			if (result == JFileChooser.APPROVE_OPTION) {
				if (mediaPlayer != null) {
					mediaPlayer.stop();
					mediaPlayer.close();
					menuView.getlp().removeAll();
				}
				try {
					model.MenuModel.setURL(fileChooser.getSelectedFile()
							.toURI().toURL());
				} catch (MalformedURLException malformedURLException) {
					System.err.println("Could not create URL for the file");
				}
				Manager.setHint(Manager.LIGHTWEIGHT_RENDERER, true);
				try {
					mediaPlayer = Manager.createRealizedPlayer(model.MenuModel
							.getURL());
				} catch (NullPointerException npe) {
					npe.printStackTrace();
				} catch (NoPlayerException e) {
					e.printStackTrace();
				} catch (CannotRealizeException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				try {
					video_in = Manager.createDataSource(model.MenuModel
							.getURL());
					P = Manager.createProcessor(video_in);
					P.configure();
					while (P.getState() != Processor.Configured) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

					P.realize();
					while (P.getState() != Processor.Realized) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

				} catch (NoDataSourceException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (NoProcessorException e) {
					e.printStackTrace();
				}

				TrackControl tc = null;
				TrackControl[] tracks = P.getTrackControls();
				for (int i = 0; i < tracks.length; i++) {
					if (tracks[i].getFormat() instanceof VideoFormat) {
						tc = tracks[i];
						break;
					}
				}

				if (tc == null) {
					System.err.println("Error. Source has no video data!\n");
					System.exit(1);
				}

				vf = (VideoFormat) tc.getFormat();
				setFrameGrabber((FrameGrabbingControl) mediaPlayer
						.getControl("javax.media.control.FrameGrabbingControl"));
				video = mediaPlayer.getVisualComponent();
				video.setBounds(0, 0, (int) vf.getSize().getWidth(), (int) vf
						.getSize().getHeight());
				fpc = (FramePositioningControl) mediaPlayer
						.getControl("javax.media.control.FramePositioningControl");

				if (fpc == null) {
					System.err
							.println("The player does not support FramePositioningControl.");
				}

				duration = mediaPlayer.getDuration();
				if (duration != Duration.DURATION_UNKNOWN) {
					totalFrames = fpc.mapTimeToFrame(duration);
					// if (totalFrames != FramePositioningControl.FRAME_UNKNOWN)
					// System.err.println("Total # of video frames in the movies: "+
					// totalFrames);
					// else
					// System.err
					// .println("The FramePositiongControl does not support mapTimeToFrame.");
				} else {
					duration = new Time(0);
				}
				model.MenuModel.setisMovieLoaded(true);

				menuView.gettoolsitem1().setEnabled(true);
				menuView.gettoolsitem2().setEnabled(true);
				menuView.gettoolsitem3().setEnabled(true);
				menuView.gettoolsitem4().setEnabled(true);
				menuView.gettoolsitem5().setEnabled(true);
				menuView.gettoolsitem6().setEnabled(true);
				menuView.gettoolsitem7().setEnabled(true);
				menuView.gettoolsitem8().setEnabled(true);
				menuView.getvideoitem1().setEnabled(true);
				menuView.getvideoitem2().setEnabled(true);
				menuView.getvideoitem3().setEnabled(true);
				menuView.getvideoitem4().setEnabled(true);
				menuView.getvideoitem5().setEnabled(true);
				menuView.getvideoitem6().setEnabled(true);
				menuView.getvideoitem7().setEnabled(true);

				menuView.getdp().setOpaque(false);
				menuView.getdp().setSize((int) vf.getSize().getWidth(),
						(int) vf.getSize().getHeight());
				menuView.getdp().setBounds(0, 0, (int) vf.getSize().getWidth(),
						(int) vf.getSize().getHeight());

				menuView.getdp().setBackground(video.getForeground());
				menuView.getlp().add(video, JLayeredPane.DEFAULT_LAYER);
				menuView.getlp().add(menuView.getdp(), JLayeredPane.DRAG_LAYER);

				Container jif2cp = menuView.getjif().getContentPane();
				jif2cp.setLayout(new BorderLayout());

				Container jifcp = menuView.getjif().getContentPane();
				jifcp.setLayout(new BorderLayout());
				menuView.getjif2().setVisible(true);
				menuView.getjif2().setBounds(0, 0,
						(int) vf.getSize().getWidth(),
						(int) vf.getSize().getHeight());
				menuView.getjif2().add(menuView.getlp());
				menuView.getjif().setVisible(true);
				menuView.getjif().add(menuView.getcpanel());

				menuView.getContentPane().add("Center", menuView.getjif2());
				menuView.getContentPane().add("South", menuView.getjif());
				// set window size corresponding to video resolution
				menuView.setSize((int) vf.getSize().getWidth() + 8, (int) vf
						.getSize().getHeight() + 163);
				Dimension dimension = Toolkit.getDefaultToolkit()
						.getScreenSize();
				int w = menuView.getSize().width;
				int h = menuView.getSize().height;
				int x = (dimension.width - w) / 2;
				int y = (dimension.height - h) / 2;
				menuView.setLocation(x, y);
				menuView.setVisible(true);
				fpc.skip(1);
				model.MenuModel.setCurrentFrame(1);
				menuView.gettextframe().setText(
						String.valueOf(model.MenuModel.getCurrentFrame()));
				menuView.getjif2()
						.setTitle(model.MenuModel.getURL().toString());
				// set the width and height attribute on the svg root element
				// same as video resolution
				double wdbl = vf.getSize().getWidth();
				String swdbl = Double.toString(wdbl);
				double hdbl = vf.getSize().getHeight();
				String shdbl = Double.toString(hdbl);
				svgRoot.setAttributeNS(null, "viewBox", "0 0 " + swdbl + " "
						+ shdbl);
				svgRoot.setAttributeNS(null, "width", swdbl);
				svgRoot.setAttributeNS(null, "height", shdbl);
				tempfile = new File("$object.dat");
				tempfile.deleteOnExit();
			}

		} else if (command.equals("Move")) {
			for (int ni = 0; ni < model.MenuModel.getcoordinates().size(); ni++) {
				Point2D.Double newp0 = new Point2D.Double(model.MenuModel
						.getcoordinates().get(ni).get(0).getX() + 10.0,
						model.MenuModel.getcoordinates().get(ni).get(0).getY());
				Point2D.Double newp1 = new Point2D.Double(model.MenuModel
						.getcoordinates().get(ni).get(1).getX() + 10.0,
						model.MenuModel.getcoordinates().get(ni).get(1).getY());
				Point2D.Double newp2 = new Point2D.Double(model.MenuModel
						.getcoordinates().get(ni).get(2).getX() + 10.0,
						model.MenuModel.getcoordinates().get(ni).get(2).getY());
				model.MenuModel.getcoordinates().get(ni).add(newp0);
				model.MenuModel.getcoordinates().get(ni).add(newp1);
				model.MenuModel.getcoordinates().get(ni).add(newp2);
				menuView.getdp().repaint();
			}

		} else if (command.equals("Stroke")) {
			setDialog(new JOptionPane());
			String strokeWidth = JOptionPane.showInputDialog(null,
					"Please set the stroke width:");
			int ss = 3;
			if (strokeWidth != null) {
				try {
					ss = Integer.parseInt(strokeWidth);
				} catch (NumberFormatException ex) {
					ss = JOptionPane.ERROR_MESSAGE;
				}
				if ((ss != JOptionPane.ERROR_MESSAGE) && (ss > 0))
					model.MenuModel.setsWidth(ss);
				else
					JOptionPane.showMessageDialog(null,
							"Invalid value entered!", "Error",
							JOptionPane.ERROR_MESSAGE);
			}

		} else if (command.equals("2Frame")) {
			String framenumber = JOptionPane.showInputDialog(null,
					"Please enter the frame number:");
			int fs = Integer.parseInt(framenumber);
			if ((fs - model.MenuModel.getCurrentFrame()) < 0) {
				JOptionPane.showMessageDialog(null, "Invalid value entered!",
						"Error", JOptionPane.ERROR_MESSAGE);
			} else {
				// seek is more suitable but it does not work.
				int dest = fpc.skip(fs);
				model.MenuModel.setCurrentFrame(model.MenuModel
						.getCurrentFrame()
						+ dest);
				menuView.gettextframe().setText(
						String.valueOf(model.MenuModel.getCurrentFrame()));
			}
			menuView.getlp().repaint();
		}

		else if (command.equals("Line")) {
			model.MenuModel.setisLineSelected(true);
			menuView.getsaveObjButton().setEnabled(true);
			if ((model.MenuModel.getlinecount() == 0)
					&& (model.MenuModel.getquadcount() == 0)) {
				start = new Point2D.Double();
				end = new Point2D.Double();
				start.setLocation(50.0, 50.0);
				end.setLocation(100.0, 100.0);
				model.MenuModel.getcoordinates().add(new ArrayList<Point2D>(3));
				model.MenuModel.getcoordinates().get(0).add(start);
				model.MenuModel.getcoordinates().get(0).add(end);
				model.MenuModel.getcoordinates().get(0).add(
						new Point2D.Double(9876.1, 9876.1));
			} else {
				menuView.gettoolsitem2().setEnabled(true);
				menuView.gettoolsitem4().setEnabled(true);
				int sizeofArray = model.MenuModel.getcoordinates().size();
				double lastx = model.MenuModel.getcoordinates().get(
						sizeofArray - 1).get(1).getX();
				double lasty = model.MenuModel.getcoordinates().get(
						sizeofArray - 1).get(1).getY();
				model.MenuModel.getcoordinates().add(new ArrayList<Point2D>(3));
				model.MenuModel.getcoordinates().get(
						model.MenuModel.getlinecount()
								+ model.MenuModel.getquadcount()).add(
						new Point2D.Double(lastx, lasty));
				model.MenuModel.getcoordinates().get(
						model.MenuModel.getlinecount()
								+ model.MenuModel.getquadcount()).add(
						new Point2D.Double(lastx + 20, lasty + 20));
				model.MenuModel.getcoordinates().get(
						model.MenuModel.getlinecount()
								+ model.MenuModel.getquadcount()).add(
						new Point2D.Double(9876.1, 9876.1));
			}

			model.MenuModel.setlinecount(model.MenuModel.getlinecount() + 1);
			menuView.getdp().addMouseListener(handler);
			menuView.getdp().addMouseMotionListener(handler);
			menuView.getdp().repaint();

		} else if (command.equals("Z")) {
			if (!model.MenuModel.getisZselected())
				model.MenuModel.setisZselected(true);
			else
				model.MenuModel.setisZselected(false);
		} else if (command.equals("Quad")) {
			model.MenuModel.setisQuadSelected(true);
			menuView.getsaveObjButton().setEnabled(true);
			if ((model.MenuModel.getquadcount()
					+ model.MenuModel.getlinecount() == 0)) {
				start = new Point2D.Double();
				end = new Point2D.Double();
				middle = new Point2D.Double();
				start.setLocation(50.0, 50.0);
				middle.setLocation(60.0, 60.0);
				end.setLocation(100.0, 100.0);
				model.MenuModel.getcoordinates().add(new ArrayList<Point2D>(3));
				model.MenuModel.getcoordinates().get(0).add(start);
				model.MenuModel.getcoordinates().get(0).add(end);
				model.MenuModel.getcoordinates().get(0).add(middle);
			} else {
				menuView.gettoolsitem2().setEnabled(true);
				menuView.gettoolsitem4().setEnabled(true);
				int sizeofArray = model.MenuModel.getcoordinates().size();
				double lastx = model.MenuModel.getcoordinates().get(
						sizeofArray - 1).get(1).getX();
				double lasty = model.MenuModel.getcoordinates().get(
						sizeofArray - 1).get(1).getY();
				model.MenuModel.getcoordinates().add(new ArrayList<Point2D>(3));
				model.MenuModel.getcoordinates().get(
						model.MenuModel.getlinecount()
								+ model.MenuModel.getquadcount()).add(
						new Point2D.Double(lastx, lasty));
				model.MenuModel.getcoordinates().get(
						model.MenuModel.getlinecount()
								+ model.MenuModel.getquadcount()).add(
						new Point2D.Double(lastx + 20, lasty + 20));
				model.MenuModel.getcoordinates().get(
						model.MenuModel.getlinecount()
								+ model.MenuModel.getquadcount()).add(
						new Point2D.Double((lastx + lastx + 20) / 2, (lasty
								+ lasty + 20) / 2));
			}
			model.MenuModel.setquadcount(model.MenuModel.getquadcount() + 1);
			menuView.getdp().addMouseListener(handler);
			menuView.getdp().addMouseMotionListener(handler);
			menuView.getdp().repaint();

		} else if (command.equals("Close")) {
			if ((model.MenuModel.getlinecount() != 0)
					|| (model.MenuModel.getquadcount() != 0)) {
				int sizeofArray = model.MenuModel.getcoordinates().size();
				double firstx = model.MenuModel.getcoordinates().get(0).get(0)
						.getX();
				double firsty = model.MenuModel.getcoordinates().get(0).get(0)
						.getY();
				double lastx = model.MenuModel.getcoordinates().get(
						sizeofArray - 1).get(1).getX();
				double lasty = model.MenuModel.getcoordinates().get(
						sizeofArray - 1).get(1).getY();
				model.MenuModel.getcoordinates().add(new ArrayList<Point2D>(3));
				model.MenuModel.getcoordinates().get(
						model.MenuModel.getlinecount()
								+ model.MenuModel.getquadcount()).add(
						new Point2D.Double(lastx, lasty));
				model.MenuModel.getcoordinates().get(
						model.MenuModel.getlinecount()
								+ model.MenuModel.getquadcount()).add(
						new Point2D.Double(firstx, firsty));
				model.MenuModel.getcoordinates().get(
						model.MenuModel.getlinecount()
								+ model.MenuModel.getquadcount()).add(
						new Point2D.Double((lastx + firstx) / 2,
								(lasty + firsty) / 2));
				model.MenuModel
						.setquadcount(model.MenuModel.getquadcount() + 1);
				menuView.getdp().repaint();
			}
		} else if (command.equals("Delete")) {
			if (model.MenuModel.getlinecount() != 0) {
				int sizeofArray = model.MenuModel.getcoordinates().size();
				model.MenuModel.getcoordinates().remove(sizeofArray - 1);
				model.MenuModel
						.setlinecount(model.MenuModel.getlinecount() - 1);
				menuView.getdp().repaint();

			} else if (model.MenuModel.getquadcount() != 0) {
				int sizeofArray = model.MenuModel.getcoordinates().size();
				model.MenuModel.getcoordinates().remove(sizeofArray - 1);
				model.MenuModel
						.setlinecount(model.MenuModel.getquadcount() - 1);
				menuView.getdp().repaint();
			}
		} else if (command.equals("Color")) {
			Color newColor = JColorChooser.showDialog(null, "Set Color",
					Color.blue);
			model.MenuModel.setlastc(newColor);
		} else if (command.equals("Edge Detection")) {
			Object response = JOptionPane.showInputDialog(null,
					"Edge Detection Algorithms", "Detect Edges",
					JOptionPane.QUESTION_MESSAGE, null, new Object[] { "Canny",
							"Sobel", "Blob Detection" }, "Canny");
			menuView.getsaveObjButton().setEnabled(true);
			if ((response == "Canny") && (model.MenuModel.getfirstClick()))
				frameToBuffer(2, 0, 0, menuView.getlp().getWidth(), menuView
						.getlp().getHeight());
			else if ((response == "Blob Detection")
					&& (model.MenuModel.getfirstClick()))
				frameToBuffer(1, 0, 0, menuView.getlp().getWidth(), menuView
						.getlp().getHeight());
			else if ((response == "Sobel") && (model.MenuModel.getfirstClick()))
				frameToBuffer(3, 0, 0, menuView.getlp().getWidth(), menuView
						.getlp().getHeight());
			else if ((response == "Canny")
					&& (!model.MenuModel.getfirstClick()))
				frameToBuffer(2, model.MenuModel.getstrx(), model.MenuModel
						.getstry(), model.MenuModel.getstrx2(), model.MenuModel
						.getstry2());
			else if ((response == "Blob Detection")
					&& (!model.MenuModel.getfirstClick()))
				frameToBuffer(1, model.MenuModel.getstrx(), model.MenuModel
						.getstry(), model.MenuModel.getstrx2(), model.MenuModel
						.getstry2());
			else if ((response == "Sobel")
					&& (!model.MenuModel.getfirstClick()))
				frameToBuffer(3, model.MenuModel.getstrx(), model.MenuModel
						.getstry(), model.MenuModel.getstrx2(), model.MenuModel
						.getstry2());
			else
				menuView.getsaveObjButton().setEnabled(false);

		} else if (command.equals("SaveObject")) {
			menuView.getfileItem2().setEnabled(true);
			if (pathstr.length() > 0)
				pathstr.delete(0, pathstr.length());
			if (model.MenuModel.getcoordinates().size() > 0) {
				pathstr.append("M"
						+ model.MenuModel.getcoordinates().get(0).get(0).getX()
						+ ","
						+ model.MenuModel.getcoordinates().get(0).get(0).getY()
						+ " ");
				for (int i = 0; i < model.MenuModel.getcoordinates().size(); i++) {
					if ((model.MenuModel.getcoordinates().get(i).get(2).getX() != 9876.1)
							&& (model.MenuModel.getcoordinates().get(i).get(2)
									.getY() != 9876.1)) {
						pathstr.append("Q"
								+ model.MenuModel.getcoordinates().get(i)
										.get(2).getX()
								+ " "
								+ model.MenuModel.getcoordinates().get(i)
										.get(2).getY()
								+ " "
								+ model.MenuModel.getcoordinates().get(i)
										.get(1).getX()
								+ " "
								+ model.MenuModel.getcoordinates().get(i)
										.get(1).getY() + " ");
					} else {
						pathstr.append("L"
								+ model.MenuModel.getcoordinates().get(i)
										.get(0).getX()
								+ ","
								+ model.MenuModel.getcoordinates().get(i)
										.get(0).getY()
								+ " "
								+ model.MenuModel.getcoordinates().get(i)
										.get(1).getX()
								+ ","
								+ model.MenuModel.getcoordinates().get(i)
										.get(1).getY() + " ");
					}
				}
				if (model.MenuModel.getisZselected())
					pathstr.append("Z");
				if (!model.MenuModel.getisfirstObject())
					pathstr.append(";");
				// keep object coordinates in a temporary file
				try {
					writer = new PrintWriter(new BufferedWriter(new FileWriter(
							tempfile, true)));
				} catch (IOException e) {
					e.printStackTrace();
				}

				if (model.MenuModel.getisfirstObject()) {
					writer.println(model.MenuModel.getCurrentFrame() + ":Path:"
							+ pathstr.toString());
				} else {
					writer.println(model.MenuModel.getCurrentFrame()
							+ ":Animate:" + pathstr.toString());
				}
				if (model.MenuModel.getedCoor().size() > 0) {
					for (int edc = 0; edc < model.MenuModel.getedCoor().size(); edc++) {
						pathstr.append("L"
								+ model.MenuModel.getedCoor().get(edc).getX1()
								+ ","
								+ model.MenuModel.getedCoor().get(edc).getY1()
								+ " "
								+ model.MenuModel.getedCoor().get(edc).getX2()
								+ ","
								+ model.MenuModel.getedCoor().get(edc).getY2()
								+ " ");
					}
				}
				model.MenuModel.setisfirstObject(false);

				writer.flush();
				writer.close();
				model.MenuModel
						.setobjectcount(model.MenuModel.getobjectcount() + 1);
			}

		} else if (command.equals("Info")) {
			String info = "\n File name: "
					+ model.MenuModel.getURL().toString() + "\n Resolution:  "
					+ vf.getSize().getWidth() + "x" + vf.getSize().getHeight()
					+ "\n Movie duration: " + duration.getSeconds()
					+ " seconds";

			JOptionPane.showMessageDialog(null, info, "Info",
					JOptionPane.INFORMATION_MESSAGE);
		} else if (command.equals("Smooth")) {
			int type = BufferedImage.TYPE_INT_RGB;
			int h = video.getHeight();
			int w = video.getWidth();
			image = new BufferedImage(w, h, type);
			Graphics2D g2 = image.createGraphics();
			g2.setPaint(video.getBackground());
			g2.fillRect(0, 0, w, h);
			menuView.getlp().paint(g2);
			g2.dispose();

			int kernelSize = 7;
			float[] kernelMatrix = new float[kernelSize * kernelSize];
			for (int k = 0; k < kernelMatrix.length; k++)
				kernelMatrix[k] = 1.0f / (kernelSize * kernelSize);
			PlanarImage input = (PlanarImage) (new RenderedImageAdapter(image));
			KernelJAI kernel = new KernelJAI(kernelSize, kernelSize,
					kernelMatrix);
			PlanarImage output = JAI.create("convolve", input, kernel);

			try {
				FileOutputStream fos = new FileOutputStream("test.jpg");
				ImageIO.write(output.getAsBufferedImage(), "jpg", fos);
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else if (command.equals("Meta")) {
			meta = JOptionPane.showInputDialog(null, "Please enter meta data:");

		} else if (command.equals("Save")) {
			if (model.MenuModel.getcoordinates().size() > 0) {
				if (pathstr.length() > 0)
					pathstr.delete(0, pathstr.length());
				if (animatestr.length() > 0)
					animatestr.delete(0, pathstr.length());

				BufferedReader reader;
				String dur = null;
				try {
					reader = new BufferedReader(new FileReader("$object.dat"));
					while ((line = reader.readLine()) != null) {
						String[] objdata = line.split(":");
						if (objdata[1].equals("Animate")) {
							animatestr.append(objdata[2]);
							int res = Integer.parseInt(objdata[0]) / 30;
							dur = Integer.toString(res);
						} else {
							pathstr.append(objdata[2]);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				path = doc.createElementNS(svgNS, "path");
				metadata = doc.createElementNS(svgNS, "desc");
				animate = doc.createElementNS(svgNS, "animate");
				path.setAttributeNS(null, "style",
						"fill:#0000ff;fill-opacity:0;stroke:#0000ff");
				path.setAttributeNS(null, "d", pathstr.toString());
				path.setAttributeNS(null, "stroke-width", model.MenuModel
						.getsWidth()
						+ "px");

				svgRoot.appendChild(path);
				// add description element to an object
				if (meta != null) {
					metadata.setTextContent(meta);
					path.appendChild(metadata);
				}
				path.appendChild(animate);
				animate.setAttributeNS(null, "values", animatestr.toString());
				animate.setAttributeNS(null, "attributeName", "d");
				animate.setAttributeNS(null, "dur", dur + "s");
			} else if (model.MenuModel.getedCoor().size() > 0) {
				path = doc.createElementNS(svgNS, "path");
				metadata = doc.createElementNS(svgNS, "desc");
				path.setAttributeNS(null, "style",
						"fill:#0000ff;fill-opacity:0;stroke:#0000ff");
				path.setAttributeNS(null, "d", pathstr.toString());
				path.setAttributeNS(null, "stroke-wideth", model.MenuModel
						.getsWidth()
						+ "px");
			}
			File saveFile = new File("/home/emr/output.svg");
			JFileChooser fileChooser = new JFileChooser();
			while (true) {
				int choice = fileChooser.showSaveDialog(menuView);
				if (choice == JFileChooser.APPROVE_OPTION) {
					File chosen = fileChooser.getSelectedFile();
					if (!chosen.exists()) {
						saveFile = chosen;
						break;
					} else {
						int confirm = JOptionPane.showConfirmDialog(menuView,
								"Overwrite file? " + chosen.getName());
						if (confirm == JOptionPane.OK_OPTION) {
							saveFile = chosen;
							break;
						} else if (confirm == JOptionPane.NO_OPTION) {
							continue;
						}
						break;
					}
				} else {
					break;
				}
			}
			OutputStream os;
			try {
				os = new FileOutputStream(saveFile);
				Writer w = new OutputStreamWriter(os, "iso-8859-1");
				svgGenerator.stream(svgRoot, w);
			} catch (IOException ioe) {
				ioe.getStackTrace();
			}
		} else if (command.equals("Clear")) {
			g = menuView.getdp().getGraphics();
			model.MenuModel.getcoordinates().clear();
			model.MenuModel.getstrCoor().clear();
			model.MenuModel.getedCoor().clear();
			pathstr.delete(0, pathstr.length());
			Graphics2D g2d = (Graphics2D) g;
			g2d.clearRect(0, 0, menuView.getlp().getWidth(), menuView.getlp()
					.getHeight());
			menuView.getlp().repaint();
			menuView.getsaveObjButton().setEnabled(false);
			model.MenuModel.setshowRects(false);
			model.MenuModel.setisLineSelected(false);
			model.MenuModel.setisQuadSelected(false);
			model.MenuModel.setlinecount(0);
			model.MenuModel.setquadcount(0);
		} else if (command.equals("Text")) {
			String text = JOptionPane.showInputDialog(null,
					"Please enter text:");
			if (text != null) {

				model.MenuModel.setstrCount(model.MenuModel.getstrCount() + 1);
				String res = text + "," + model.MenuModel.getstrx() + ","
						+ model.MenuModel.getstry();
				System.out.println(res);
				model.MenuModel.getstrCoor().add(res);
				menuView.getdp().repaint();
			}
		}

		else if (command.equals("Exit")) {
			System.exit(0);
		} else if (command.equals("Play")) {
			// mediaPlayer.start();
			for (int i = 1; i < 500; i++) {
				fpc.skip(1);
				menuView.getsaveObjButton().doClick();
			}

		}
	}

	public void frameToBuffer(int whiched, int x, int y, int wx, int hx) {
		if (x < 0)
			x = 0;
		if (y < 0)
			y = 0;
		if (wx < 0)
			wx = 0;
		if (hx < 0)
			hx = 0;
		if (model.MenuModel.getedCoor().size() > 0) {
			model.MenuModel.getedCoor().clear();
		}
		int type = BufferedImage.TYPE_INT_RGB;
		int h = video.getHeight();
		int w = video.getWidth();
		image = new BufferedImage(w, h, type);
		Graphics2D g2 = image.createGraphics();
		g2.setPaint(video.getBackground());
		g2.fillRect(0, 0, w, h);
		menuView.getlp().paint(g2);
		g2.dispose();
		if ((x != 0) && (y != 0) && (wx != w) && (hx != h)) {
			image = image.getSubimage(x, y, wx, hx);
		}
		g2.dispose();

		int kernelSize = 7;
		float[] kernelMatrix = new float[kernelSize * kernelSize];
		for (int k = 0; k < kernelMatrix.length; k++)
			kernelMatrix[k] = 1.0f / (kernelSize * kernelSize);
		PlanarImage input = (PlanarImage) (new RenderedImageAdapter(image));
		KernelJAI kernel = new KernelJAI(kernelSize, kernelSize, kernelMatrix);
		PlanarImage output = JAI.create("convolve", input, kernel);

		if (whiched == 1) {
			Blob b;
			EdgeVertex eA, eB;
			BlobDetection bd = new BlobDetection(output.getWidth(), output
					.getHeight());
			bd.setPosDiscrimination(true);
			bd.setThreshold(0.2f);
			int w2 = output.getWidth();
			int h2 = output.getHeight();
			int[] rgbs = new int[w2 * h2];
			bd.computeBlobs(output.getAsBufferedImage().getRGB(0, 0, w2, h2,
					rgbs, 0, w2));
			for (int n = 0; n < bd.getBlobNb(); n++) {
				b = bd.getBlob(n);
				if (b != null) {
					for (int m = 0; m < b.getEdgeNb(); m += 1) {
						eA = b.getEdgeVertexA(m);
						eB = b.getEdgeVertexB(m);
						if (eA != null && eB != null) {
							// g = dp.getGraphics();
							// Graphics2D g2d = (Graphics2D) g;
							// g2d.setColor(Color.blue);
							model.MenuModel.getedCoor().add(
									new Line2D.Float(eA.x * w2 + x, eA.y * h2
											+ y, eB.x * w2 + x, eB.y * h2 + y));
							// g2d.draw(new Line2D.Float(eA.x * w2 + x, eA.y *
							// h2 + y,eB.x * w2 + x, eB.y * h2 + y));
						}
					}

				}
			}
			System.out.println(model.MenuModel.getedCoor().size());

		} else if (whiched == 2) {
			CannyEdgeDetector detector = new CannyEdgeDetector();
			detector.setLowThreshold(0.5f);
			detector.setHighThreshold(1f);
			detector.setSourceImage(output.getAsBufferedImage());
			detector.process();
			BufferedImage edges = detector.getEdgesImage();
			for (int xa = 0; xa < edges.getWidth(); xa++) {
				for (int ya = 0; ya < edges.getHeight(); ya++) {
					int rgb = edges.getRGB(xa, ya);
					int red = (rgb & 0x00ff0000) >> 16;
					int green = (rgb & 0x0000ff00) >> 8;
					int blue = rgb & 0x000000ff;
					if ((red > 220) && (green > 220) && (blue > 220)) {
						g = menuView.getdp().getGraphics();
						Graphics2D g2d = (Graphics2D) g;
						g2d.setColor(Color.blue);
						model.MenuModel.getedCoor()
								.add(
										new Line2D.Float(xa + x, ya + y,
												xa + x, ya + y));
					}
				}
			}

		} else if (whiched == 3) {
			ConvolveOp sobelHOp = getSobelHorizOp();
			// ConvolveOp sobelVer = getSobelVertOp();
			BufferedImage destImage = createEdgeImage(output
					.getAsBufferedImage(), sobelHOp);
			// BufferedImage destImage2 = createEdgeImage(image,sobelVer);
			for (int xa = 0; xa < destImage.getWidth(); xa++) {
				for (int ya = 0; ya < destImage.getHeight(); ya++) {
					int rgb1 = destImage.getRGB(xa, ya);
					int red1 = (rgb1 & 0x00ff0000) >> 16;
					int green1 = (rgb1 & 0x0000ff00) >> 8;
					int blue1 = rgb1 & 0x000000ff;

					if ((red1 != 0) && (green1 != 30) && (blue1 != 30)) {
						g = menuView.getdp().getGraphics();
						Graphics2D g2d = (Graphics2D) g;
						g2d.setColor(Color.blue);
						model.MenuModel.getedCoor()
								.add(
										new Line2D.Float(xa + x, ya + y,
												xa + x, ya + y));
						// g2d.draw(new Line2D.Float(xa+x, ya+y, xa+x, ya+y));
					}
				}
			}

		}
		menuView.getdp().repaint();
		File outputfile = new File("saved.png");
		try {
			ImageIO.write(image, "png", outputfile);
			outputfile.deleteOnExit();
		} catch (IOException exc) {
			exc.printStackTrace();
		}

	}

	public static BufferedImage createEdgeImage(BufferedImage srcImage,
			BufferedImageOp op) {
		BufferedImage destImage = op.createCompatibleDestImage(srcImage,
				srcImage.getColorModel());
		destImage = op.filter(srcImage, destImage);
		return destImage;
	}

	public static ConvolveOp getSobelVertOp() {
		float sbvMatrix[] = { -1.0f, -2.0f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
				2.0f, 1.0f };
		Kernel kernel = new Kernel(3, 3, sbvMatrix);
		return getConvolveOp(kernel);
	}

	public static ConvolveOp getSobelHorizOp() {
		float sbhMatrix[] = { 1.0f, -0.0f, -1.0f, 2.0f, 0.0f, -2.0f, 1.0f,
				0.0f, -1.0f };
		Kernel kernel = new Kernel(3, 3, sbhMatrix);
		return getConvolveOp(kernel);
	}

	public static ConvolveOp getConvolveOp(Kernel kernel) {
		RenderingHints hints = new RenderingHints(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, hints);
		return op;
	}

	public void mouseClicked(MouseEvent e) {
		model.MenuModel.setstrx(e.getX());
		model.MenuModel.setstry(e.getY());
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		if (model.MenuModel.getfirstClick()) {
			model.MenuModel.setstrx(e.getX());
			model.MenuModel.setstry(e.getY());
			model.MenuModel.setfirstClick(false);
		} else {
			model.MenuModel.setstrx2(e.getX());
			model.MenuModel.setstry2(e.getY());
		}
	}

	public void mouseReleased(MouseEvent e) {

	}

	protected void processWindowEvent(WindowEvent e) {
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			if (mediaPlayer != null) {
				mediaPlayer.stop();
				mediaPlayer.close();
			}
			System.exit(0);
		}
	}

	public void setFrameGrabber(FrameGrabbingControl frameGrabber) {
		this.frameGrabber = frameGrabber;
	}

	public FrameGrabbingControl getFrameGrabber() {
		return frameGrabber;
	}

	public void setDialog(JOptionPane dialog) {
		this.dialog = dialog;
	}

	public JOptionPane getDialog() {
		return dialog;
	}

	public void setMenuModel(MenuModel menuModel) {
		this.menuModel = menuModel;
	}

	public MenuModel getMenuModel() {
		return menuModel;
	}

}
