package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditPatCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new EditPatCommand object
 */
public class EditPatCommandParser implements Parser<EditPatCommand> {
    /**
     * Parses the given {@code String} of arguments in the context of the EditDocCommand
     * and returns an EditDocCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public EditPatCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS, PREFIX_TAG);

        Index index;

        try {
            index = ParserUtil.parseEditIndex(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw pe;
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS);

        EditPatCommand.EditPatDescriptor editPatDescriptor = new EditPatCommand.EditPatDescriptor();

        if (argMultimap.getValue(PREFIX_NAME).isPresent()) {
            editPatDescriptor.setName(ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get()));
        }
        if (argMultimap.getValue(PREFIX_PHONE).isPresent()) {
            editPatDescriptor.setPhone(ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE).get()));
        }
        if (argMultimap.getValue(PREFIX_EMAIL).isPresent()) {
            editPatDescriptor.setEmail(ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL).get()));
        }
        if (argMultimap.getValue(PREFIX_ADDRESS).isPresent()) {
            editPatDescriptor.setAddress(ParserUtil.parseAddress(argMultimap.getValue(PREFIX_ADDRESS).get()));
        }

        if (!editPatDescriptor.isAnyFieldEdited()) {
            throw new ParseException(EditPatCommand.MESSAGE_NOT_EDITED);
        }

        return new EditPatCommand(index, editPatDescriptor);
    }
}
