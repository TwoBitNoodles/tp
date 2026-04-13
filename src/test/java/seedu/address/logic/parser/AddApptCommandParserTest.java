package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddApptCommand;
import seedu.address.logic.parser.exceptions.ParseException;

public class AddApptCommandParserTest {
    private final AddApptCommandParser parser = new AddApptCommandParser();

    @Test
    public void parse_validArgs_success() throws Exception {
        assertTrue(parser.parse(" id/1 pid/2 date/2026-04-09 time/9:00") instanceof AddApptCommand);
    }

    @Test
    public void parse_invalidDoctorIdNonInteger_throws() {
        assertThrows(ParseException.class, () -> parser.parse(" id/abc pid/2 date/2026-04-09 time/9:00"));
    }

    @Test
    public void parse_invalidDoctorIdNonPositive_throws() {
        assertThrows(ParseException.class, () -> parser.parse(" id/0 pid/2 date/2026-04-09 time/9:00"));
        assertThrows(ParseException.class, () -> parser.parse(" id/-1 pid/2 date/2026-04-09 time/9:00"));
    }

    @Test
    public void parse_invalidPatientId_throws() {
        assertThrows(ParseException.class, () -> parser.parse(" id/1 pid/abc date/2026-04-09 time/9:00"));
        assertThrows(ParseException.class, () -> parser.parse(" id/1 pid/0 date/2026-04-09 time/9:00"));
    }

    @Test
    public void parse_missingPrefix_throws() {
        assertThrows(ParseException.class, () -> parser.parse(" id/1 date/2026-04-09 time/9:00"));
    }

    @Test
    public void parse_preamblePresent_throws() {
        assertThrows(ParseException.class, () -> parser.parse("garbage id/1 pid/2 date/2026-04-09 time/9:00"));
    }
}
