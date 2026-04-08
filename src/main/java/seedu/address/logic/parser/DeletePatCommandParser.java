package seedu.address.logic.parser;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeletePatCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new DeletePatCommand object
 */
public class DeletePatCommandParser implements Parser<DeletePatCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the DeletePatCommand
     * and returns a DeletePatCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public DeletePatCommand parse(String args) throws ParseException {
        try {
            Index index = ParserUtil.parseDelIndex(args);
            return new DeletePatCommand(index);
        } catch (ParseException pe) {
            throw pe;

        }
    }
}
