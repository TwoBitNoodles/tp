package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.logging.Logger;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Patient;
import seedu.address.model.person.Person;


/**
 * Deletes a patient identified using it's displayed index from the address book.
 */
public class DeletePatCommand extends Command {
    public static final String COMMAND_WORD = "delpat";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the patient identified by the index number used in the displayed list.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_DELETE_PATIENT_SUCCESS = "Deleted Patient: %1$s";
    private static final Logger logger = Logger.getLogger(DeletePatCommand.class.getName());

    private final Index targetIndex;

    public DeletePatCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<? extends Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size() || targetIndex.getZeroBased() < 0) {
            logger.info("Invalid index: " + targetIndex.getOneBased());
            throw new CommandException(Messages.MESSAGE_INVALID_PATIENT_DISPLAYED_INDEX);
        }

        Person personToDelete = lastShownList.get(targetIndex.getZeroBased());
        if (!(personToDelete instanceof Patient)) {
            throw new CommandException("The person at the specified index is not a patient.");
        }
        model.deletePatient((Patient) personToDelete);
        logger.info("Deleted patient: " + Messages.format(personToDelete));
        return new CommandResult(String.format(MESSAGE_DELETE_PATIENT_SUCCESS, Messages.format(personToDelete)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeletePatCommand)) {
            return false;
        }

        DeletePatCommand otherDeletePatCommand = (DeletePatCommand) other;
        return targetIndex.equals(otherDeletePatCommand.targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .toString();
    }
}
