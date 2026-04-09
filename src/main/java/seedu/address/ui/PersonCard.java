package seedu.address.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.address.model.person.Doctor;
import seedu.address.model.person.Patient;
import seedu.address.model.person.Person;

/**
 * An UI component that displays information of a {@code Person}.
 */
public class PersonCard extends UiPart<Region> {

    private static final String FXML = "PersonListCard.fxml";

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on AddressBook level 4</a>
     */

    public final Person person;

    @FXML
    private HBox cardPane;
    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private FlowPane tags;
    @FXML
    private Label phone;
    @FXML
    private Label address;
    @FXML
    private Label email;
    @FXML
    private Label doctorId;
    @FXML
    private Label patientId;

    /**
     * Creates a {@code PersonCode} with the given {@code Person} and index to display.
     */
    public PersonCard(Person person, int displayedIndex) {
        super(FXML);
        this.person = person;
        id.setText(displayedIndex + ". ");
        name.setText(person.getName().fullName);
        phone.setText(person.getPhone().value);
        address.setText(person.getAddress().value);
        email.setText(person.getEmail().value);
        Label roleTag = new Label(person.getRoleTag());
        if (person instanceof Doctor) {
            roleTag.getStyleClass().add("doctor-tag");
        } else if (person instanceof seedu.address.model.person.Patient) {
            roleTag.getStyleClass().add("patient-tag");
        }
        tags.getChildren().add(roleTag);
        // doctor id information added by copilot
        if (person instanceof Doctor) {
            doctorId.setText("Doctor ID: " + ((Doctor) person).getDocId());
            patientId.setVisible(false);
            patientId.setManaged(false);
        } else if (person instanceof Patient) {
            patientId.setText("Patient ID: " + ((Patient) person).getPatientId());
            doctorId.setVisible(false);
            doctorId.setManaged(false);
        } else {
            doctorId.setVisible(false);
            doctorId.setManaged(false);
            patientId.setVisible(false);
            patientId.setManaged(false);
        }
    }
}
