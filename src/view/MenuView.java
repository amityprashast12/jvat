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

package view;

import java.awt.FlowLayout;
import java.awt.LayoutManager;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.awt.Dimension;
import java.awt.Toolkit;

import tools.DrawPanel;

import controller.MenuController;
import model.MenuModel;

public class MenuView extends JFrame {
	private static final long serialVersionUID = 1L;

	MenuController MenuListener;
	MenuModel model;
	protected JButton fwdButton;
	protected JButton bwdButton;
	protected JButton move;
	protected JButton edgeDetect;
	protected JButton deleteButton;
	protected JButton saveObj;
	protected JButton lineButton;
	protected JButton clear;
	protected JButton quadButton;
	protected JMenu filemenu;
	protected static JMenuItem fileItem1;
	protected static JMenuItem fileItem2;
	protected static JMenuItem fileItem3;
	protected static JMenuBar menubar;
	private JMenu toolsmenu;
	private JMenu videomenu;
	private JMenuItem videoitem1;
	private JMenuItem videoitem2;
	private JMenuItem videoitem3;
	private JMenuItem videoitem4;
	private JMenuItem videoitem5;
	private JMenuItem videoitem6;
	private JMenuItem videoitem7;
	private JMenuItem toolsitem1;
	private JMenuItem toolsitem2;
	private JMenuItem toolsitem3;
	private JMenuItem toolsitem4;
	private JMenuItem toolsitem5;
	private JMenuItem toolsitem6;
	private JMenuItem toolsitem7;
	private JMenuItem toolsitem8;
	private JTextArea textframe;
	private JLayeredPane lp;
	private JInternalFrame jif;
	private JInternalFrame jif2;
	private JPanel cpanel;
	private DrawPanel dp;

	public MenuView(MenuModel model) {
		this.model = model;
		ImageIcon drawLine = new ImageIcon("images/line.png");
		lineButton = new JButton(drawLine);
		lineButton.setToolTipText("Draw Line");
		lineButton.setActionCommand("Line");
		lineButton.setBorderPainted(false);
		ImageIcon Backward = new ImageIcon("images/backward.png");
		bwdButton = new JButton(Backward);
		bwdButton.setToolTipText("Backward");
		bwdButton.setActionCommand("Backward");
		bwdButton.setBorderPainted(false);
		ImageIcon Forward = new ImageIcon("images/forward.png");
		fwdButton = new JButton(Forward);
		fwdButton.setToolTipText("Forward");
		fwdButton.setActionCommand("Forward");
		fwdButton.setBorderPainted(false);
		textframe = new JTextArea();
		textframe.setColumns(1);
		textframe.setRows(1);
		textframe.setEditable(false);

		ImageIcon Selection = new ImageIcon("images/selection.png");
		move = new JButton(Selection);
		move.setToolTipText("Move Object");
		move.setActionCommand("Move");
		move.setBorderPainted(false);
		ImageIcon ED = new ImageIcon("images/edge.png");
		edgeDetect = new JButton(ED);
		edgeDetect.setToolTipText("Detect Edges");
		edgeDetect.setActionCommand("Edge Detection");
		edgeDetect.setBorderPainted(false);
		ImageIcon Quad = new ImageIcon("images/quad.png");
		quadButton = new JButton(Quad);
		quadButton.setActionCommand("Quad");
		quadButton.setToolTipText("Draw QuadCurve");
		quadButton.setBorderPainted(false);
		ImageIcon delete = new ImageIcon("images/delete.png");
		deleteButton = new JButton(delete);
		deleteButton.setToolTipText("Delete the last object");
		deleteButton.setActionCommand("Delete");
		deleteButton.setBorderPainted(false);

		ImageIcon save = new ImageIcon("images/save.png");
		saveObj = new JButton(save);
		saveObj.setToolTipText("Save this object");
		saveObj.setActionCommand("SaveObject");
		saveObj.setBorderPainted(false);
		clear = new JButton("CLS");
		clear.setActionCommand("Clear");
		menubar = new JMenuBar();
		filemenu = new JMenu("File");
		fileItem1 = new JMenuItem("Open");
		fileItem2 = new JMenuItem("Save SVG");
		fileItem2.setActionCommand("Save");
		fileItem2.setEnabled(false);
		fileItem3 = new JMenuItem("Exit");

		filemenu.add(fileItem1);
		filemenu.add(fileItem2);
		filemenu.add(fileItem3);

		videomenu = new JMenu("Video");
		videoitem1 = new JMenuItem("Info...");
		videoitem1.setActionCommand("Info");
		videoitem1.setEnabled(false);
		videoitem2 = new JMenuItem("Detect Edges");
		videoitem2.setActionCommand("Edge Detection");
		videoitem2.setEnabled(false);
		videoitem3 = new JMenuItem("Move Object");
		videoitem3.setActionCommand("Move");
		videoitem3.setEnabled(false);
		videoitem4 = new JMenuItem("Enter Text");
		videoitem4.setActionCommand("Text");
		videoitem4.setEnabled(false);
		videoitem5 = new JMenuItem("Add meta data");
		videoitem5.setActionCommand("Meta");
		videoitem5.setEnabled(false);
		videoitem6 = new JMenuItem("Smooth Image");
		videoitem6.setActionCommand("Smooth");
		videoitem6.setEnabled(false);
		videoitem7 = new JMenuItem("Play...");
		videoitem7.setActionCommand("Play");
		videoitem7.setEnabled(false);

		toolsmenu = new JMenu("Tools");
		toolsitem1 = new JMenuItem("Draw Line");
		toolsitem1.setActionCommand("Line");
		toolsitem1.setEnabled(false);
		toolsitem2 = new JMenuItem("Draw Quad Curve");
		toolsitem2.setActionCommand("Quad");
		toolsitem2.setEnabled(false);
		toolsitem3 = new JMenuItem("Delete last object");
		toolsitem3.setEnabled(false);
		toolsitem3.setActionCommand("Delete");
		toolsitem3.setEnabled(false);
		toolsitem4 = new JMenuItem("Close the object");
		toolsitem4.setEnabled(false);
		toolsitem4.setActionCommand("Close");
		toolsitem4.setEnabled(false);
		toolsitem5 = new JMenuItem("Stroke size");
		toolsitem5.setEnabled(true);
		toolsitem5.setActionCommand("Stroke");
		toolsitem5.setEnabled(false);
		toolsitem6 = new JMenuItem("Skip to Frame");
		toolsitem6.setActionCommand("2Frame");
		toolsitem6.setEnabled(false);
		toolsitem7 = new JMenuItem("Add Z attribute");
		toolsitem7.setEnabled(true);
		toolsitem7.setActionCommand("Z");
		toolsitem7.setEnabled(false);
		toolsitem8 = new JMenuItem("Set Color");
		toolsitem8.setEnabled(true);
		toolsitem8.setActionCommand("Color");
		toolsitem8.setEnabled(false);
		// tool menu items
		toolsmenu.add(toolsitem1);
		toolsmenu.add(toolsitem2);
		toolsmenu.add(toolsitem3);
		toolsmenu.add(toolsitem4);
		toolsmenu.add(toolsitem5);
		toolsmenu.add(toolsitem6);
		toolsmenu.add(toolsitem7);
		toolsmenu.add(toolsitem8);
		// video items
		videomenu.add(videoitem1);
		videomenu.add(videoitem2);
		videomenu.add(videoitem3);
		videomenu.add(videoitem4);
		videomenu.add(videoitem5);
		videomenu.add(videoitem6);
		videomenu.add(videoitem7);
		menubar.add(filemenu);
		menubar.add(toolsmenu);
		menubar.add(videomenu);
		setJMenuBar(menubar);
		lp = new JLayeredPane();
		jif = new JInternalFrame("Tools", false, false, true, true);
		jif2 = new JInternalFrame("Video", false, false, true, true);
		dp = new DrawPanel();
		cpanel = new JPanel();
		cpanel.add(bwdButton);
		cpanel.add(fwdButton);
		cpanel.add(textframe);
		cpanel.add(move);
		cpanel.add(edgeDetect);
		cpanel.add(lineButton);
		cpanel.add(quadButton);
		cpanel.add(deleteButton);
		cpanel.add(saveObj);
		cpanel.add(clear);
		cpanel.setLayout((LayoutManager) new FlowLayout(FlowLayout.LEFT));
		setSize(300, 100);
                Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int w = this.getSize().width;
		int h = this.getSize().height;
		int x = (dimension.width - w) / 2;
		int y = (dimension.height - h) / 2;
		this.setTitle("JVAT");
		this.setLocation(x, y);
		saveObj.setEnabled(false);
	}

	public JButton getfwdButton() {
		return fwdButton;
	}

	public JButton getbwdButton() {
		return bwdButton;
	}

	public JButton getmoveButton() {
		return move;
	}

	public JButton getedgeDetectButton() {
		return edgeDetect;
	}

	public JButton getdeleteButton() {
		return deleteButton;
	}

	public JButton getsaveObjButton() {
		return saveObj;
	}

	public JButton getlineButton() {
		return lineButton;
	}

	public JButton getclearButton() {
		return clear;
	}

	public JButton getquadButton() {
		return quadButton;
	}

	public JMenu getfilemenu() {
		return filemenu;
	}

	public JMenuItem getfileItem1() {
		return fileItem1;
	}

	public JMenuItem getfileItem2() {
		return fileItem2;
	}

	public JMenuItem getfileItem3() {
		return fileItem3;
	}

	public JMenuBar getmenubar() {
		return menubar;
	}

	public JMenu gettoolsmenu() {
		return toolsmenu;
	}

	public JMenu getvideosmenu() {
		return videomenu;
	}

	public JMenuItem getvideoitem1() {
		return videoitem1;
	}

	public JMenuItem getvideoitem2() {
		return videoitem2;
	}

	public JMenuItem getvideoitem3() {
		return videoitem3;
	}

	public JMenuItem getvideoitem4() {
		return videoitem4;
	}

	public JMenuItem getvideoitem5() {
		return videoitem5;
	}

	public JMenuItem getvideoitem6() {
		return videoitem6;
	}

	public JMenuItem getvideoitem7() {
		return videoitem7;
	}

	public JMenuItem gettoolsitem1() {
		return toolsitem1;
	}

	public JMenuItem gettoolsitem2() {
		return toolsitem2;
	}

	public JMenuItem gettoolsitem3() {
		return toolsitem3;
	}

	public JMenuItem gettoolsitem4() {
		return toolsitem4;
	}

	public JMenuItem gettoolsitem5() {
		return toolsitem5;
	}

	public JMenuItem gettoolsitem6() {
		return toolsitem6;
	}

	public JMenuItem gettoolsitem7() {
		return toolsitem7;
	}

	public JMenuItem gettoolsitem8() {
		return toolsitem8;
	}

	public JTextArea gettextframe() {
		return textframe;
	}

	public JLayeredPane getlp() {
		return lp;
	}

	public JInternalFrame getjif() {
		return jif;
	}

	public JInternalFrame getjif2() {
		return jif2;
	}

	public JPanel getcpanel() {
		return cpanel;
	}

	public DrawPanel getdp() {
		return dp;
	}
}
