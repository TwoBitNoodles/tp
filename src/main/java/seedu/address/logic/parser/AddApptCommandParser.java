package seedu.address.logic.parser;

import static seedu.address.logic.commands.AddApptCommand.MESSAGE_USAGE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DOCTOR_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PATIENT_ID;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TIME;

import java.util.stream.Stream;

import seedu.address.logic.commands.AddApptCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.appointment.Appointment;

/**
 * Parses input appointments and creates a new AddApptCommand object
 */
public class AddApptCommandParser {
    /**
     * Parses the given {@code String} of arguments in the context of the AddApptCommand
     * and returns an AddApptCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public AddApptCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_DATE, PREFIX_TIME,
                                                                    PREFIX_DOCTOR_ID, PREFIX_PATIENT_ID);


        if (!arePrefixesPresent(argMultimap, PREFIX_DOCTOR_ID, PREFIX_PATIENT_ID, PREFIX_DATE, PREFIX_TIME)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_USAGE));
        }


        String date = argMultimap.getValue(PREFIX_DATE).get();
        String time = argMultimap.getValue(PREFIX_TIME).get();

        String doctorIdValue = argMultimap.getValue(PREFIX_DOCTOR_ID).get().trim();
        int doctorId;
        try {
            doctorId = Integer.parseInt(doctorIdValue);
        } catch (NumberFormatException e) {
            throw new ParseException("Doctor id must be a positive integer.");
        }
        if (doctorId <= 0) {
            throw new ParseException("Doctor id must be a positive integer.");
        }

        String patientIdValue = argMultimap.getValue(PREFIX_PATIENT_ID).get().trim();
        int patientId;
        try {
            patientId = Integer.parseInt(patientIdValue);
        } catch (NumberFormatException e) {
            throw new ParseException("Patient id must be a positive integer.");
        }
        if (patientId <= 0) {
            throw new ParseException("Patient id must be a positive integer.");
        }

        Appointment appt = new Appointment(doctorId, patientId, date, time);

        return new AddApptCommand(appt);
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }
}
