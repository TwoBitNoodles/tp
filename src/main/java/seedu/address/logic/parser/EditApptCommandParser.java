package seedu.address.logic.parser;

import static seedu.address.logic.parser.CliSyntax.PREFIX_APPT_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NEWDATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NEWDOC;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NEWTIME;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.logic.commands.EditApptCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input and creates an EditApptCommand object.
 */
public class EditApptCommandParser {
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("H:mm");
    private static final Pattern PREFIX_PATTERN = Pattern.compile("(?<!\\S)([A-Za-z]+/)");
    private static final Set<String> VALID_PREFIXES = Set.of(
            PREFIX_APPT_ID.getPrefix(),
            PREFIX_NEWDOC.getPrefix(),
            PREFIX_NEWDATE.getPrefix(),
            PREFIX_NEWTIME.getPrefix());

    private static final String MESSAGE_UNSUPPORTED_PATIENT_EDIT =
            "Editing patient name is not supported. Delete the appointment and add a new one instead.";
    private static final String MESSAGE_INVALID_EDIT_FIELD =
            "Unrecognized field for editappt. Use apptid/, nid/, ndate/, or ntime/.";

    /**
     * Parses the given string of arguments and returns an EditApptCommand object.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public EditApptCommand parse(String args) throws ParseException {
        if (args.contains("newn/")) {
            throw new ParseException(MESSAGE_UNSUPPORTED_PATIENT_EDIT);
        }

        String unknownPrefix = findUnknownPrefix(args);
        if (unknownPrefix != null) {
            throw new ParseException(MESSAGE_INVALID_EDIT_FIELD);
        }

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args,
                PREFIX_APPT_ID,
                PREFIX_NEWDATE, PREFIX_NEWTIME, PREFIX_NEWDOC);

        if (argMultimap.getValue(PREFIX_APPT_ID).isEmpty() || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException("Missing required fields to identify the appointment! "
                    + "Need apptid/.");
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

        if (newDoc == null && newDate == null && newTime == null) {
            throw new ParseException("At least one new field is required");
        }

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

    private String findUnknownPrefix(String args) {
        Matcher matcher = PREFIX_PATTERN.matcher(args);
        while (matcher.find()) {
            String prefix = matcher.group(1);
            if (!VALID_PREFIXES.contains(prefix)) {
                return prefix;
            }
        }
        return null;
    }
}
