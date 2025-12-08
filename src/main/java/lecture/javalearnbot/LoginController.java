package lecture.javalearnbot;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class LoginController extends BaseController
{
    @FXML
    private TextField usernameField;
    private TextField passwordField;
    private ComboBox<String> roleComboBox;

    @FXML
    public void initialize ()
    {
        roleComboBox.getItems().addAll("Admin", "Student");
    }

    @FXML
    public void onLoginClick ()
    {
        LoginAuthentication(usernameField.getText(), passwordField.getText(), roleComboBox.getValue());
    }

    @FXML
    public void onRegisterClick ()
    {
        nav.toRegister();
    }

    private void LoginAuthentication(String username, String password, String role)
    {
        try {
            if (username.equals("admin") && password.equals(123)) {
                nav.toHome();
            } else {
                showError("Login Failed", "Incorrect username or password.");
            }
        } catch (Exception e){
            showError("Error", "Login system error.");
        }
    }

}
