package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.EditApptCommand;
import seedu.address.logic.parser.exceptions.ParseException;

public class EditApptCommandParserTest {
    private final EditApptCommandParser parser = new EditApptCommandParser();

    @Test
    public void parse_validArgs_success() throws Exception {
        assertTrue(parser.parse(" apptid/3 ntime/10:00 nid/2 ndate/2026-04-10") instanceof EditApptCommand);
    }

    @Test
    public void parse_invalidDoctorId_throws() {
        assertThrows(ParseException.class, () -> parser.parse(" apptid/3 nid/abc"));
        assertThrows(ParseException.class, () -> parser.parse(" apptid/3 nid/0"));
    }

    @Test
    public void parse_invalidTime_throws() {
        assertThrows(ParseException.class, () -> parser.parse(" apptid/3 ntime/110:00"));
        assertThrows(ParseException.class, () -> parser.parse(" apptid/3 ntime/09:15"));
    }

    @Test
    public void parse_unknownField_throws() {
        assertThrows(ParseException.class, () -> parser.parse(" apptid/3 ndoc/2"));
    }

    @Test
    public void parse_editNameBlocked_throws() {
        assertThrows(ParseException.class, () -> parser.parse(" apptid/3 newn/Alice"));
    }
}
