package seedu.address.logic.parser;

import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TIME;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.logic.commands.AddApptCommand;

public class AddApptCommandParser {
    public AddApptCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_DATE, PREFIX_TIME);

        // Basic check: did they provide d/ and t/?
        if (argMultimap.getValue(PREFIX_DATE).isEmpty() || argMultimap.getValue(PREFIX_TIME).isEmpty()) {
            throw new ParseException("Missing date (d/) or time (t/)!");
        }

        String person = argMultimap.getPreamble(); // This gets whatever is before the first prefix
        String date = argMultimap.getValue(PREFIX_DATE).get();
        String time = argMultimap.getValue(PREFIX_TIME).get();

        return new AddApptCommand(person, date, time);
    }
}
