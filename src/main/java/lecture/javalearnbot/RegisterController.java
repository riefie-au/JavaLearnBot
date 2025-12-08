package lecture.javalearnbot;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class RegisterController extends BaseController
{
    @FXML private TextField usernameField;
    @FXML private TextField passwordField;
    @FXML private ComboBox<String> roleComboBox;

    @FXML
    private void onSubmitRegister()
    {
        createAccount(usernameField.getText(), passwordField.getText(), roleComboBox.getValue());
    }

    @FXML
    private void onBackToLogin()
    {
        nav.toLogin();
    }

    private void createAccount(String username, String password, String role)
    {
        try{
            showAlert("Successfully created an account.", "Account has been created!");
            nav.toLogin();
        } catch (Exception ex){
            showError("Error", "Failed to create an account.");
        }
    }
}
