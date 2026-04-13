package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.DeleteApptCommand;
import seedu.address.logic.parser.exceptions.ParseException;

public class DeleteApptCommandParserTest {
    private final DeleteApptCommandParser parser = new DeleteApptCommandParser();

    @Test
    public void parse_validArgs_success() throws Exception {
        assertTrue(parser.parse(" apptid/0") instanceof DeleteApptCommand);
    }

    @Test
    public void parse_invalidId_throws() {
        assertThrows(ParseException.class, () -> parser.parse(" apptid/-1"));
        assertThrows(ParseException.class, () -> parser.parse(" apptid/abc"));
    }

    @Test
    public void parse_missingApptId_throws() {
        assertThrows(ParseException.class, () -> parser.parse(" "));
    }

    @Test
    public void parse_preamblePresent_throws() {
        assertThrows(ParseException.class, () -> parser.parse("garbage apptid/0"));
    }
}

