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

package tool;

import java.awt.Dimension;
import java.awt.Toolkit;
import view.MenuView;
import model.MenuModel;
import controller.MenuController;

public class Driver {
	public static void main(String[] args) {
		MenuModel Model = new MenuModel();
		MenuView View = new MenuView(Model);
		MenuController Control = new MenuController(View, Model);
		// According to the screen size, place the window in the middle
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int w = View.getSize().width;
		int h = View.getSize().height;
		int x = (dimension.width - w) / 2;
		int y = (dimension.height - h) / 2;
		View.setTitle("JVAT");
		View.setLocation(x, y);
		View.setVisible(true);
	}
}
