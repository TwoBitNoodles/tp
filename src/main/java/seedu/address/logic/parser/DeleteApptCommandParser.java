package seedu.address.logic.parser;

import static seedu.address.logic.commands.DeleteApptCommand.MESSAGE_USAGE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_APPT_ID;

import seedu.address.logic.commands.DeleteApptCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input appointments and creates a new DeleteApptCommand object
 */

public class DeleteApptCommandParser {
    /**
     * Parses the given {@code String} of arguments in the context of the DeleteApptCommand
     * and returns an DeleteApptCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public DeleteApptCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_APPT_ID);

        if (argMultimap.getValue(PREFIX_APPT_ID).isEmpty() || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_USAGE));
        }

        String idValue = argMultimap.getValue(PREFIX_APPT_ID).get().trim();
        int apptId;
        try {
            apptId = Integer.parseInt(idValue);
        } catch (NumberFormatException e) {
            throw new ParseException("Appointment id must be a non-negative integer.");
        }

        if (apptId < 0) {
            throw new ParseException("Appointment id must be a non-negative integer.");
        }

        return new DeleteApptCommand(apptId);
    }

}
