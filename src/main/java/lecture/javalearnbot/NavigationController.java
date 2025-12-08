package lecture.javalearnbot;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class NavigationController
{
    private Stage stage;
    public NavigationController(Stage stage)
    {
        this.stage = stage;
    }
    public void goTo(String fxmlName, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlName));
            Scene scene = new Scene(loader.load());
            stage.setTitle(title);
            stage.setScene(scene);

            Object controller = loader.getController();
            if (controller instanceof BaseController) {
                ((BaseController) controller).setNavigationController(this);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void toLogin() { goTo("loginPage.fxml", "Login"); }
    public void toRegister() { goTo("registerPage.fxml", "Register"); }
    public void toHome() { goTo("homePage.fxml", "Home"); }
    public void toChatBot() { goTo("chatPage.fxml", "Chat Bot"); }
    public void toDocuments() { goTo("documentPage.fxml", "Documents"); }
    public void toEvaluationDashboard() { goTo("evaluationDashboardPage.fxml", "Evaluation dashboard"); }
    public void toAdmin () { goTo("adminPage.fxml", "Admin"); }

}


