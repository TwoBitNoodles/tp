package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_DOCTOR;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NEWDATE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NEWDOC;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NEWNAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NEWTIME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TIME;

import java.io.IOException;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
/**
 * Edits an existing appointment
 */

public class EditApptCommand extends Command {
    public static final String COMMAND_WORD = "editappt";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": changes the doctor/patient/date/time of an appointment"
            + " Identifies the old appointment from the doctor, date and time"
            + "Paramters: "
            + PREFIX_DOCTOR + "OLD_DOCTORNAME"
            + PREFIX_DATE + " DATE (yyyy-mm-dd)"
            + PREFIX_TIME + " TIME (H:MM)\n"
            + "[" + PREFIX_NEWNAME + "NEW_PATIENT_NAME] "
            + "[" + PREFIX_NEWDOC + "NEW_DOCTOR_NAME] "
            + "[" + PREFIX_NEWDATE + "NEW_DATE] "
            + "[" + PREFIX_NEWTIME + "NEW_TIME]\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_DOCTOR + "Dr. Han " + PREFIX_DATE + "2023-10-10 " + PREFIX_TIME + "10:00 "
            + PREFIX_NEWTIME + "11:30";

    public static final String MESSAGE_SUCCESS = "Edited appointment!";

    private final String oldDoc;
    private final String oldDate;
    private final String oldTime;
    private final String newDoc;
    private final String newPat;
    private final String newDate;
    private final String newTime;

    /**
     * creates an EditApptCommand to edit an existing appointment
     * @param oldDoc
     * @param oldDate
     * @param oldTime
     * @param newPat
     * @param newDoc
     * @param newDate
     * @param newTime
     */
    public EditApptCommand(String oldDoc, String oldDate,
                           String oldTime, String newPat, String newDoc, String newDate, String newTime) {
        this.oldDoc = oldDoc;
        this.oldDate = oldDate;
        this.oldTime = oldTime;
        this.newDoc = newDoc;
        this.newPat = newPat;
        this.newDate = newDate;
        this.newTime = newTime;


    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);


        try {
            model.editAppt(oldDoc, oldDate, oldTime, newPat, newDoc, newDate, newTime);

            return new CommandResult(MESSAGE_SUCCESS);
        } catch (IOException e) {
            throw new CommandException("Could not edit appointment: " + e.getMessage());
        }
    }

}
