package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import seedu.address.logic.commands.ViewSchedCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ViewSchedCommand object.
 * Date is optional; if omitted, weekly schedule is shown.
 */
public class ViewSchedCommandParser implements Parser<ViewSchedCommand> {

    @Override
    public ViewSchedCommand parse(String args) throws ParseException {
        requireNonNull(args);

        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, CliSyntax.PREFIX_DOCTOR, CliSyntax.PREFIX_DATE);

        // Doctor prefix is required
        Optional<String> doctorOpt = argMultimap.getValue(CliSyntax.PREFIX_DOCTOR);
        if (doctorOpt.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                            ViewSchedCommand.MESSAGE_USAGE));
        }

        String doctorName = doctorOpt.get().trim();

        // Date prefix is optional
        Optional<String> dateOpt = argMultimap.getValue(CliSyntax.PREFIX_DATE);
        LocalDate date = null;
        if (dateOpt.isPresent()) {
            try {
                date = LocalDate.parse(dateOpt.get().trim());
            } catch (DateTimeParseException e) {
                throw new ParseException(
                        String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                                ViewSchedCommand.MESSAGE_USAGE));
            }
        }

        return new ViewSchedCommand(doctorName, date);
    }
}
