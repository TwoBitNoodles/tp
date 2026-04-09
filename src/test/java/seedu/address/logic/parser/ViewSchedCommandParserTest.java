package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ViewSchedCommand;

/**
 * Tests for ViewSchedCommandParser.
 */
public class ViewSchedCommandParserTest {

    private ViewSchedCommandParser parser = new ViewSchedCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        String userInput = " d/John Tan id/1 date/2026-03-20";

        ViewSchedCommand expectedCommand =
                new ViewSchedCommand("John Tan", 1, LocalDate.of(2026, 3, 20));

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    // test written by codex
    @Test
    public void parse_optionalDateOmitted_success() {
        String userInput = " d/John Tan id/1";

        ViewSchedCommand expectedCommand = new ViewSchedCommand("John Tan", 1, null);

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_extraWhitespace_success() {
        String userInput = "   d/   John   Tan   id/  1   date/2026-03-20   ";

        ViewSchedCommand expectedCommand =
                new ViewSchedCommand("John Tan", 1, LocalDate.of(2026, 3, 20));

        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_missingDoctor_failure() {
        String userInput = " date/2026-03-20";

        assertParseFailure(parser, userInput,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        ViewSchedCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_missingDoctorId_failure() {
        String userInput = " d/John Tan date/2026-03-20";

        assertParseFailure(parser, userInput,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        ViewSchedCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidDateFormat_failure() {
        String userInput = " d/John Tan id/1 date/20-03-2026";

        assertParseFailure(parser, userInput,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        ViewSchedCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidDoctorId_failure() {
        String userInput = " d/John Tan id/abc date/2026-03-20";

        assertParseFailure(parser, userInput,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        ViewSchedCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_emptyInput_failure() {
        String userInput = "";

        assertParseFailure(parser, userInput,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        ViewSchedCommand.MESSAGE_USAGE));
    }
}
