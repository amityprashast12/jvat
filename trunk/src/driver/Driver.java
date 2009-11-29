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

package driver;


import view.MenuView;
import model.MenuModel;
import controller.MenuController;

public class Driver {
	public static void main(String[] args) {
		MenuModel Model = new MenuModel();
		MenuView View = new MenuView(Model);
		MenuController Control = new MenuController(View, Model);
		View.setVisible(true);
	}
}
