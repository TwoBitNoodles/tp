package seedu.address.logic.parser;

import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DOCTOR;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NEWDATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NEWDOC;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NEWNAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NEWTIME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TIME;

import seedu.address.logic.commands.EditApptCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * parses the input tp creat an EditApptCommand
 */
public class EditApptCommandParser {
    /**
     * Parses the given {@code String} of arguments in the context of the EditApptCommand
     * and returns an EditApptCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public EditApptCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args,
                PREFIX_DATE, PREFIX_TIME, PREFIX_DOCTOR,
                PREFIX_NEWDATE, PREFIX_NEWTIME, PREFIX_NEWDOC, PREFIX_NEWNAME);

        if (argMultimap.getAllValues(PREFIX_DOCTOR).isEmpty()
                || argMultimap.getAllValues(PREFIX_DATE).isEmpty()
                || argMultimap.getAllValues(PREFIX_TIME).isEmpty()) {
            throw new ParseException("Missing required fields to identify the appointment! "
                    + "Need d/, date/, and time/.");
        }

        String oldDoc = argMultimap.getAllValues(PREFIX_DOCTOR).get(0);
        String oldDate = argMultimap.getAllValues(PREFIX_DATE).get(0);
        String oldTime = argMultimap.getAllValues(PREFIX_TIME).get(0);

        String newDoc = argMultimap.getAllValues(PREFIX_NEWDOC).size() > 1
                ? argMultimap.getAllValues(PREFIX_NEWDOC).get(1) : null;

        String newDate = argMultimap.getAllValues(PREFIX_NEWDATE).size() > 1
                ? argMultimap.getAllValues(PREFIX_NEWDATE).get(1) : null;

        String newTime = argMultimap.getAllValues(PREFIX_NEWTIME).size() > 1
                ? argMultimap.getAllValues(PREFIX_NEWTIME).get(1) : null;

        String newPat = argMultimap.getValue(PREFIX_NEWNAME).orElse(null);

        return new EditApptCommand(oldDoc, oldDate, oldTime, newPat, newDoc, newDate, newTime);
    }
}
