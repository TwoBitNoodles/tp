package seedu.address.logic.parser;

import static seedu.address.logic.parser.CliSyntax.PREFIX_APPT_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NEWDATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NEWDOC;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NEWTIME;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import seedu.address.logic.commands.EditApptCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * parses the input tp creat an EditApptCommand
 */
public class EditApptCommandParser {
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("H:mm");

    /**
     * Parses the given {@code String} of arguments in the context of the EditApptCommand
     * and returns an EditApptCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public EditApptCommand parse(String args) throws ParseException {
        if (args.contains("newn/")) {
            throw new ParseException("Editing patient name is not supported. "
                    + "Delete the appointment and add a new one instead.");
        }

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args,
                PREFIX_APPT_ID,
                PREFIX_NEWDATE, PREFIX_NEWTIME, PREFIX_NEWDOC);

        if (argMultimap.getValue(PREFIX_APPT_ID).isEmpty() || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException("Missing required fields to identify the appointment! "
                    + "Need id/.");
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

        String newDoc = argMultimap.getValue(PREFIX_NEWDOC).map(String::trim).orElse(null);
        String newDate = argMultimap.getValue(PREFIX_NEWDATE).map(String::trim).orElse(null);
        String newTime = argMultimap.getValue(PREFIX_NEWTIME).map(String::trim).orElse(null);

        if ("".equals(newDoc)) {
            throw new ParseException("Doctor id cannot be empty.");
        }
        if (newDoc != null) {
            try {
                int parsed = Integer.parseInt(newDoc);
                if (parsed <= 0) {
                    throw new ParseException("Doctor id must be a positive integer.");
                }
            } catch (NumberFormatException e) {
                throw new ParseException("Doctor id must be a positive integer.");
            }
        }

        if (newDate != null) {
            try {
                LocalDate.parse(newDate);
            } catch (DateTimeParseException e) {
                throw new ParseException("Please input a valid date. The date must be formatted as YYYY-MM-DD");
            }
        }

        if (newTime != null) {
            try {
                LocalTime parsed = LocalTime.parse(newTime, TIME_FORMAT);
                if (parsed.getMinute() % 30 != 0) {
                    throw new ParseException("Please choose a valid timeslot.");
                }
            } catch (DateTimeParseException e) {
                throw new ParseException("Please input a valid time. Time must be formatted as H:MM "
                        + "(e.g. 9:00 or 09:00)");
            }
        }

        return new EditApptCommand(apptId, newDoc, newDate, newTime);
    }
}
