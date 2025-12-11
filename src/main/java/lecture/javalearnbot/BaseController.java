package lecture.javalearnbot;

import javafx.scene.control.Alert;

public abstract class BaseController //BaseController is a parent class for all controllers; It provides shared utilities like NavigationController reference and common alert and error popup methods.
{
        protected NavigationController nav; //This is a reference to NavigationController so every page can navigate between screens.
        public void setNavigationController(NavigationController nav) //Setter: to allow JavaFX loader to inject shared NavigationController.
        {
            this.nav = nav;
        }

        protected void showAlert(String title, String msg) //To display standard information popup dialogs.
        {
            Alert a = new Alert(Alert.AlertType.INFORMATION); //popup type = INFO
            a.setTitle(title); // title bar text
            a.setHeaderText(null); //no headers
            a.setContentText(msg); // message body
            a.show(); // to display popup
        }

        protected void showError(String title, String msg) // To display error dialog popup used for exception handling.
        {
            Alert a = new Alert(Alert.AlertType.ERROR); // popup type = ERROR
            a.setTitle(title); // title bar text
            a.setHeaderText(null); // no extra headers
            a.setContentText(msg); // error message
            a.show(); // to display popup
        }
}
