package seedu.address.logic.parser;

import static seedu.address.logic.parser.CliSyntax.*;

import seedu.address.logic.commands.EditApptCommand;
import seedu.address.logic.parser.exceptions.ParseException;

public class EditApptCommandParser {

    public EditApptCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args,
                PREFIX_DATE, PREFIX_TIME, PREFIX_DOCTOR, PREFIX_NAME);

        if (argMultimap.getAllValues(PREFIX_DOCTOR).isEmpty()
                || argMultimap.getAllValues(PREFIX_DATE).isEmpty()
                || argMultimap.getAllValues(PREFIX_TIME).isEmpty()) {
            throw new ParseException("Missing required fields to identify the appointment! "
                    + "Need d/, date/, and time/.");
        }

        String oldDoc = argMultimap.getAllValues(PREFIX_DOCTOR).get(0);
        String oldDate = argMultimap.getAllValues(PREFIX_DATE).get(0);
        String oldTime = argMultimap.getAllValues(PREFIX_TIME).get(0);

        String newDoc = argMultimap.getAllValues(PREFIX_DOCTOR).size() > 1
                ? argMultimap.getAllValues(PREFIX_DOCTOR).get(1) : null;

        String newDate = argMultimap.getAllValues(PREFIX_DATE).size() > 1
                ? argMultimap.getAllValues(PREFIX_DATE).get(1) : null;

        String newTime = argMultimap.getAllValues(PREFIX_TIME).size() > 1
                ? argMultimap.getAllValues(PREFIX_TIME).get(1) : null;

        String newPat = argMultimap.getValue(PREFIX_NAME).orElse(null);

        return new EditApptCommand(oldDoc, oldDate, oldTime, newPat, newDoc, newDate, newTime);
    }
}
