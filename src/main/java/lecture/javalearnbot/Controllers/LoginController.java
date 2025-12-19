package lecture.javalearnbot.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class LoginController extends BaseController // LoginController is responsible for handling the login page logic.
{
    @FXML
    private TextField usernameField; // Text field for entering username.
    private TextField passwordField; // Text field for entering password.
    private ComboBox<String> roleComboBox; // Dropdown selection for choosing user role.

    @FXML
    public void initialize () // Adds available roles into the role dropdown lists.
    {
        roleComboBox.getItems().addAll("Admin", "Student");
    }

    @FXML
    public void onLoginClick () // Triggered when login button is clicked; Retrieves values from the fields and performs authentication.
    {
        LoginAuthentication(usernameField.getText(), passwordField.getText(), roleComboBox.getValue());
    }

    @FXML
    public void onRegisterClick ()  // Triggered when register button is clicked; Navigates user to register page.
    {
        nav.toRegister();
    }

    private void LoginAuthentication(String username, String password, String role)
    {
        try {
            if (username.equals("admin") && password.equals("123")) { // Simple authentication check and if login is successful navigates to home page.
                nav.toHome();
            } else {
                showError("Login Failed", "Incorrect username or password."); // Shows error if incorrect password or username.
            }
        } catch (Exception e){ // Handles unexpected system or logic errors and shows the error.
            showError("Error", "Login system error.");
        }
    }

}
