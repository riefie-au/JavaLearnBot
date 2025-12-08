package lecture.javalearnbot;

import javafx.scene.control.Alert;

public abstract class BaseController
{
        protected NavigationController nav;
        public void setNavigationController(NavigationController nav)
        {
            this.nav = nav;
        }

        protected void showAlert(String title, String msg)
        {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle(title);
            a.setHeaderText(null);
            a.setContentText(msg);
            a.show();
        }

        protected void showError(String title, String msg)
        {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle(title);
            a.setHeaderText(null);
            a.setContentText(msg);
            a.show();
        }
}
