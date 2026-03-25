package seedu.address.logic.commands;

// import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.io.File;
import java.io.FileWriter;
// import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.appointment.Appointment;

public class AddApptCommandTest {
    private static final String FILE_PATH = "data/schedule.json";

    @BeforeEach
    public void setup() throws Exception {
        File file = new File(FILE_PATH);
        file.getParentFile().mkdirs();

        FileWriter writer = new FileWriter(file);

        writer.write("{\n"
                + "  \"John Tan\": {\n"
                + "    \"2026-03-20\": {\n"
                + "      \"09:00\": null,\n"
                + "      \"09:30\": null,\n"
                + "      \"10:00\": null,\n"
                + "      \"10:30\": null,\n"
                + "      \"11:00\": null,\n"
                + "      \"11:30\": null\n"
                + "    }\n"
                + "  }\n"
                + "}");

        writer.close();
    }

    // @Test
    // public void execute_validAppointment_success() throws CommandException {
    //     Model model = new ModelManager();
    //     Appointment appt = new Appointment("John Tan", "Jane Doe",
    //                                         "2026-03-20", "10:00");

    //     AddApptCommand command = new AddApptCommand(appt);

    //     CommandResult result = command.execute(model);

    //     ViewSchedCommand comm =
    //             new ViewSchedCommand("john tan", LocalDate.of(2026, 3, 20));

    //     CommandResult res = comm.execute(model);
    //     String expected =
    //             "Schedule for john tan on 2026-03-20\n\n"
    //                     + "09:00 – Available\n"
    //                     + "09:30 – Available\n"
    //                     + "10:00 – Booked\n"
    //                     + "10:30 – Available\n"
    //                     + "11:00 – Available\n"
    //                     + "11:30 – Available\n";
    //     assertEquals(expected, res.getFeedbackToUser());
    // }

    @Test
    public void execute_dateNotFound() throws CommandException {
        Model model = new ModelManager();
        Appointment appt = new Appointment("John Tan", "Jane Doe",
                "2026-03-21", "10:00");
        AddApptCommand command = new AddApptCommand(appt);

        assertThrows(Exception.class, () -> command.execute(model));

    }
}
