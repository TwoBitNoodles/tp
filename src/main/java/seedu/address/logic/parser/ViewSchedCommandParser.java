package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import seedu.address.logic.commands.ViewSchedCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ViewSchedCommand object.
 */
public class ViewSchedCommandParser implements Parser<ViewSchedCommand> {

    @Override
    public ViewSchedCommand parse(String args) throws ParseException {
        requireNonNull(args);

        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, CliSyntax.PREFIX_DOCTOR, CliSyntax.PREFIX_DATE);

        if (!arePrefixesPresent(argMultimap, CliSyntax.PREFIX_DOCTOR, CliSyntax.PREFIX_DATE)) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                            ViewSchedCommand.MESSAGE_USAGE));
        }

        String doctorName = argMultimap.getValue(CliSyntax.PREFIX_DOCTOR).get().trim();
        String dateStr = argMultimap.getValue(CliSyntax.PREFIX_DATE).get().trim();

        try {
            LocalDate date = LocalDate.parse(dateStr);
            return new ViewSchedCommand(doctorName, date);
        } catch (DateTimeParseException e) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                            ViewSchedCommand.MESSAGE_USAGE));
        }
    }

    /**
     * Returns true if all prefixes are present.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap,
                                              Prefix... prefixes) {
        for (Prefix prefix : prefixes) {
            if (argumentMultimap.getValue(prefix).isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
