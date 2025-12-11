package lecture.javalearnbot;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class RegisterController extends BaseController //UI component bindings from registerPage.fxml
{
    @FXML private TextField usernameField; // Text field where user types in username.
    @FXML private TextField passwordField; // Text field where user types in password.
    @FXML private ComboBox<String> roleComboBox; // Dropdown for selecting admin or student role.

    @FXML
    private void onSubmitRegister() // triggered when user clicks register button
    {
        createAccount(usernameField.getText(), passwordField.getText(), roleComboBox.getValue());
    }

    @FXML
    private void onBackToLogin()
    {
        nav.toLogin();
    } // triggered when user clicks back button; uses navigation controller to return to login page.

    private void createAccount(String username, String password, String role) // Handles account creation, would be saved to DB.
    {
        try{
            showAlert("Successfully created an account.", "Account has been created!"); // Shows popup confirming success.
            nav.toLogin(); // Navigates back to login page.
        } catch (Exception ex){ // Shows error message if account creation failed.
            showError("Error", "Failed to create an account.");
        }
    }
}
