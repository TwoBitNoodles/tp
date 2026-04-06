package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import java.io.File;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Doctor;
import seedu.address.testutil.DoctorBuilder;

/**
 * Contains unit tests for {@code AddDocCommand}.
 */
public class AddDocCommandTest {

    @Test
    public void constructor_nullDoctor_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddDocCommand(null));
    }

    @Test
    public void execute_validDoctor_success() throws Exception {
        Model model = new ModelManager();
        Doctor validDoctor = new DoctorBuilder().withName("Alice")
                .withPhone("12121212")
                .withEmail("alice@tan.com")
                .withAddress("66 Conlins Street").build();

        AddDocCommand addDocCommand = new AddDocCommand(validDoctor);
        Model expectedModel = new ModelManager();
        expectedModel.addDoctor(validDoctor);
        assertCommandSuccess(addDocCommand, model,
                String.format(AddDocCommand.MESSAGE_SUCCESS, Messages.format(validDoctor)),
                expectedModel);
    }

    @Test
    public void execute_validDoctor_scheduleMade() throws Exception {
        Model model = new ModelManager();
        Doctor validDoctor = new DoctorBuilder().withName("Grace Wong")
                .withPhone("11111111").withEmail("gwong@doctor.com")
                .withAddress("11 Schedule Road").build();

        AddDocCommand addDocCommand = new AddDocCommand(validDoctor);
        addDocCommand.execute(model);
        File schedule = new File("data/schedule.json");
        assertTrue(schedule.exists());
        ObjectMapper mapper = new ObjectMapper();
        // Copilot assisted with coding the following lines
        @SuppressWarnings("unchecked")
        Map<String, Object> scheduleData = mapper.readValue(schedule, Map.class);
        assertTrue(scheduleData.containsKey("Grace Wong"));
    }

    @Test
    public void execute_duplicateDoctor_throwsCommandException() {
        Model model = new ModelManager();
        Doctor doctor = new DoctorBuilder().withName("Alice")
                .withPhone("12121212").withEmail("alice@tan.com")
                .withAddress("66 Conlins Street").build();
        model.addDoctor(doctor);
        AddDocCommand addDocCommand = new AddDocCommand(doctor);
        assertCommandFailure(addDocCommand, model, AddDocCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_sameNameDifferentPhoneEmail_success() throws Exception {
        Model model = new ModelManager();

        Doctor doctor = new DoctorBuilder().withName("Mary Tan")
                .withPhone("88888888")
                .withEmail("mary@tan.com")
                .withAddress("Blk 610").build();
        Doctor differentContactsDoctor = new DoctorBuilder().withName("Mary Tan")
                .withPhone("99999999")
                .withEmail("marytan@doc.com")
                .withAddress("Blk 34").build();

        model.addDoctor(doctor);
        AddDocCommand addDocCommand = new AddDocCommand(differentContactsDoctor);
        Model expectedModel = new ModelManager();
        expectedModel.addDoctor(doctor);
        expectedModel.addDoctor(differentContactsDoctor);
        assertCommandSuccess(addDocCommand, model,
                String.format(AddDocCommand.MESSAGE_SUCCESS, Messages.format(differentContactsDoctor)),
                expectedModel);
    }

    @Test
    public void execute_sameNameAndEmail_throwsCommandException() {
        Model model = new ModelManager();

        Doctor doctor = new DoctorBuilder().withName("Mary Tan")
                .withPhone("88888888")
                .withEmail("mary@tan.com")
                .withAddress("Blk 610").build();
        Doctor duplicateDoctor = new DoctorBuilder().withName("mary tan")
                .withPhone("99999999")
                .withEmail("mary@tan.com")
                .withAddress("Blk 34").build();

        model.addDoctor(doctor);
        AddDocCommand addDocCommand = new AddDocCommand(duplicateDoctor);
        assertCommandFailure(addDocCommand, model, AddDocCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_sameNameAndPhone_throwsCommandException() {
        Model model = new ModelManager();

        Doctor doctor = new DoctorBuilder().withName("Shrek")
                .withPhone("33333333")
                .withEmail("shrek@shrek.com")
                .withAddress("100 Castle").build();
        Doctor duplicateDoctor = new DoctorBuilder().withName("shrek")
                .withPhone("33333333")
                .withEmail("fiona@castle.com")
                .withAddress("110 Castle").build();

        model.addDoctor(doctor);
        AddDocCommand addDocCommand = new AddDocCommand(duplicateDoctor);
        assertCommandFailure(addDocCommand, model, AddDocCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_differentNameSameEmail_success() throws Exception {
        Model model = new ModelManager();

        Doctor doctor = new DoctorBuilder().withName("Shrek")
                .withPhone("33333333")
                .withEmail("shrek@shrek.com")
                .withAddress("100 Castle").build();
        Doctor differentDoctor = new DoctorBuilder().withName("Fiona")
                .withPhone("44444444")
                .withEmail("shrek@shrek.com")
                .withAddress("110 Castle").build();

        model.addDoctor(doctor);
        AddDocCommand addDocCommand = new AddDocCommand(differentDoctor);
        Model expectedModel = new ModelManager();
        expectedModel.addDoctor(doctor);
        expectedModel.addDoctor(differentDoctor);
        assertCommandSuccess(addDocCommand, model,
                String.format(AddDocCommand.MESSAGE_SUCCESS, Messages.format(differentDoctor)),
                expectedModel);
    }

    @Test
    public void execute_differentNameSamePhone_success() throws Exception {
        Model model = new ModelManager();

        Doctor doctor = new DoctorBuilder().withName("Jerry")
                .withPhone("11112222")
                .withEmail("jerry@tom.com")
                .withAddress("2 Holland Avenue").build();
        Doctor differentDoctor = new DoctorBuilder().withName("Odie")
                .withPhone("11112222")
                .withEmail("odie@garfield.com")
                .withAddress("3 Holland Avenue").build();

        model.addDoctor(doctor);
        AddDocCommand addDocCommand = new AddDocCommand(differentDoctor);
        Model expectedModel = new ModelManager();
        expectedModel.addDoctor(doctor);
        expectedModel.addDoctor(differentDoctor);
        assertCommandSuccess(addDocCommand, model,
                String.format(AddDocCommand.MESSAGE_SUCCESS, Messages.format(differentDoctor)),
                expectedModel);
    }

    @Test
    public void equals() {
        Doctor alice = new DoctorBuilder().withName("Alice").build();
        Doctor bob = new DoctorBuilder().withName("Bob").withPhone("00001111")
                .withEmail("bob@doctor.com").build();
        AddDocCommand addAliceCommand = new AddDocCommand(alice);
        AddDocCommand addBobCommand = new AddDocCommand(bob);
        // same object then returns true
        assert addAliceCommand.equals(addAliceCommand);

        // same values then returns true
        AddDocCommand addAliceCommandCopy = new AddDocCommand(alice);
        assert addAliceCommand.equals(addAliceCommandCopy);

        // different types then returns false
        assert !addAliceCommand.equals(1);

        // null then returns false
        assert !addAliceCommand.equals(null);

        // different doctor then returns false
        assert !addAliceCommand.equals(addBobCommand);
    }

    @Test
    public void toStringMethod() {
        Doctor alice = new DoctorBuilder().withName("Alice").build();
        AddDocCommand addDocCommand = new AddDocCommand(alice);
        String result = addDocCommand.toString();
        assertTrue(result.contains("toAdd="));
        assertTrue(result.contains(alice.toString()));
    }
}
