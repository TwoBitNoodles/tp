package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddApptCommand;
import seedu.address.logic.commands.AddDocCommand;
import seedu.address.logic.commands.AddPatCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.DeleteApptCommand;
import seedu.address.logic.commands.DeleteDocCommand;
import seedu.address.logic.commands.DeletePatCommand;
import seedu.address.logic.commands.EditApptCommand;
import seedu.address.logic.commands.EditDocCommand;
import seedu.address.logic.commands.EditDocCommand.EditDoctorDescriptor;
import seedu.address.logic.commands.EditPatCommand;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.ViewSchedCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Doctor;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.person.Patient;
import seedu.address.testutil.DoctorBuilder;
import seedu.address.testutil.EditDoctorDescriptorBuilder;
import seedu.address.testutil.PatientBuilder;
import seedu.address.testutil.PersonUtil;

public class AddressBookParserTest {

    private final AddressBookParser parser = new AddressBookParser();

    @Test
    public void parseCommand_addDoc() throws Exception {
        Doctor doctor = new DoctorBuilder().build();
        AddDocCommand command = (AddDocCommand) parser.parseCommand(PersonUtil.getDocAddCommand(doctor));
        assertEquals(new AddDocCommand(doctor), command);
    }

    @Test
    public void parseCommand_addPat() throws Exception {
        Patient patient = new PatientBuilder().build();
        AddPatCommand command = (AddPatCommand) parser.parseCommand(PersonUtil.getPatAddCommand(patient));
        assertEquals(new AddPatCommand(patient), command);
    }

    @Test
    public void parseCommand_addAppt() throws Exception {
        assertTrue(parser.parseCommand(AddApptCommand.COMMAND_WORD
                + " d/Mavis Goh n/Papa Drac date/2026-04-30 time/09:00") instanceof AddApptCommand);
    }

    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD) instanceof ClearCommand);
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD + " 3") instanceof ClearCommand);
    }

    @Test
    public void parseCommand_editDoctor() throws Exception {
        Doctor doctor = new DoctorBuilder().build();
        EditDoctorDescriptor descriptor = new EditDoctorDescriptorBuilder(doctor).build();
        EditDocCommand command = (EditDocCommand) parser.parseCommand(EditDocCommand.COMMAND_WORD + " "
                + INDEX_FIRST_PERSON.getOneBased() + " " + PersonUtil.getEditDoctorDescriptorDetails(descriptor));
        assertEquals(new EditDocCommand(INDEX_FIRST_PERSON, descriptor), command);
    }

    @Test
    public void parseCommand_editPatient() throws Exception {
        assertTrue(parser.parseCommand(EditPatCommand.COMMAND_WORD + " "
                + INDEX_FIRST_PERSON.getOneBased() + " n/Kitty") instanceof EditPatCommand);
    }

    @Test
    public void parseCommand_editAppt() throws Exception {
        assertTrue(parser.parseCommand(EditApptCommand.COMMAND_WORD
                + " d/Mavis Goh date/2026-04-30 time/09:00 d/Johnny date/2026-05-01 time/10:00")
                instanceof EditApptCommand);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_find() throws Exception {
        List<String> keywords = Arrays.asList("foo", "bar", "baz");
        FindCommand command = (FindCommand) parser.parseCommand(
                FindCommand.COMMAND_WORD + " " + keywords.stream().collect(Collectors.joining(" ")));
        assertEquals(new FindCommand(new NameContainsKeywordsPredicate(keywords)), command);
    }

    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_list() throws Exception {
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD) instanceof ListCommand);
        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD + " 3") instanceof ListCommand);
    }

    @Test
    public void parseCommand_deleteDoc() throws Exception {
        assertTrue(parser.parseCommand(DeleteDocCommand.COMMAND_WORD + " 1") instanceof DeleteDocCommand);
    }

    @Test
    public void parseCommand_deletePat() throws Exception {
        assertTrue(parser.parseCommand(DeletePatCommand.COMMAND_WORD + " 1") instanceof DeletePatCommand);
    }

    @Test
    public void parseCommand_deleteAppt() throws Exception {
        assertTrue(parser.parseCommand(DeleteApptCommand.COMMAND_WORD
                + " d/Mavis Goh n/Papa Drac date/2026-04-30 time/09:00") instanceof DeleteApptCommand);
    }

    @Test
    public void parseCommand_viewSched() throws Exception {
        assertTrue(parser.parseCommand(ViewSchedCommand.COMMAND_WORD
                + " d/Mavis Goh date/2026-04-30") instanceof ViewSchedCommand);
    }


    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE), ()
            -> parser.parseCommand(""));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_UNKNOWN_COMMAND, () -> parser.parseCommand("unknownCommand"));
    }
}
