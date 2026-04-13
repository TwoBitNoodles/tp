package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.DeleteDocCommand;

/**
 * As we are only doing white-box testing, our test cases do not cover path variations
 * outside of the DeleteDocCommand code. For example, inputs "1" and "1 abc" take the
 * same path through the DeleteDocCommand, and therefore we test only one of them.
 * The path variation for those two cases occur inside the ParserUtil, and
 * therefore should be covered by the ParserUtilTest.
 */
public class DeleteDocCommandParserTest {

    private DeleteDocCommandParser parser = new DeleteDocCommandParser();

    @Test
    public void parse_validArgs_returnsDeleteDocCommand() {
        assertParseSuccess(parser, "1", new DeleteDocCommand(INDEX_FIRST_PERSON));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, "a", String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteDocCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_numericInvalidIndex_throwsParseException() {
        assertParseFailure(parser, "0", Messages.MESSAGE_INVALID_DOCTOR_DISPLAYED_INDEX);
    }
}
