---
  layout: default.md
  title: "User Guide"
  pageNav: 4
---

# CLInicDesk User Guide

CLInicDesk is a desktop application designed for **receptionists at small-scale medical clinics to manage patients, doctors, and appointments efficiently**.

CLInicDesk is optimized for use through a Command Line Interface (CLI) while still providing the convenience of a Graphical User Interface (GUI). CLInicDesk enables receptionists who can type quickly to perform clinic management tasks such as adding patients, viewing doctor availabilities, and booking appointments faster than traditional systems.

**Note:** This app is meant to be used with commands in English only.
<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## Setup Guidelines

<div class="quick-start-steps">

1. Ensure you have Java `17` or above installed in your Computer.<br>
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

1. Download the latest `.jar` file from [here](https://github.com/AY2526S2-CS2103T-W12-1/tp/releases).

1. Copy the file to the folder you want to use as the _home folder_ for your CLInicDesk.

1. Open a command terminal, `cd` into the folder you put the jar file in, and use the `java -jar clinicdesk.jar` command to run the application.<br>
   A GUI similar to the below should appear in a few seconds. Note how the app contains some sample data.<br>
   <div class="image-container">

   ![Ui](images/Ui.png)

   </div>

1. Type the command in the command box and press Enter to execute it. e.g. typing **`help`** and pressing Enter will open the help window.<br>
   Some example commands you can try:

   * `list` : Lists all patients and doctors.
   * `adddoc n/John Doe p/98765432 e/johnd@doctor.com a/John street, block 123, #01-01` : Adds a doctor named `John Doe`.
   * `deldoc 3` : Deletes the 3rd doctor shown in the current list.
   * `clear` : Clears all entries from the display.
   * `exit` : Exits the app.

1. Refer to the [Features](#features) below for details of each command.

</div>

--------------------------------------------------------------------------------------------------------------------

## Features

### Command format and input constraints

<table class="convention-table">
<tr><th>Convention</th><th>Meaning</th><th>Example</th></tr>
<tr><td><code>UPPER_CASE</code></td><td>A parameter you supply</td><td><code>adddoc n/NAME</code> → <code>adddoc n/John Doe</code></td></tr>
<tr><td><code>[square brackets]</code></td><td>Optional field</td><td><code>viewsched d/DOCTOR_NAME [date/YYYY-MM-DD]</code></td></tr>
<tr><td>Any parameter order</td><td>Parameters can appear in any order</td><td><code>n/NAME p/PHONE</code> or <code>p/PHONE n/NAME</code></td></tr>
</table>

<box type="info" seamless>

**Good to know:**
* Commands that take no parameters (`help`, `list`, `exit`, `clear`) will ignore any extra text. e.g. `help 123` is treated as `help`.
* If copying commands from a PDF, watch out for missing spaces around line-breaks.

</box>

The table below summarises the rules and constraints for all input fields used across commands.

<table class="constraints-table">
<tr><th>Field</th><th>Constraints</th></tr>
<tr><td><strong>NAME</strong></td><td>Alphanumeric characters and spaces only. Case-insensitive for matching (e.g. <code>john doe</code> matches <code>John Doe</code>). Must not be blank.</td></tr>
<tr><td><strong>PHONE_NUMBER</strong></td><td>Numeric digits only. Must be at least 3 digits long.</td></tr>
<tr><td><strong>EMAIL</strong></td><td>Must follow the standard <code>local-part@domain</code> format (e.g. <code>name@example.com</code>).</td></tr>
<tr><td><strong>ADDRESS</strong></td><td>Any non-blank string.</td></tr>
<tr><td><strong>INDEX</strong></td><td>A positive integer (1, 2, 3, …) referring to the position in the currently displayed list.</td></tr>
<tr><td><strong>DATE</strong> (<code>YYYY-MM-DD</code>)</td><td>Must be in strict ISO 8601 format (e.g. <code>2026-04-10</code>). Must be today or within the next 7 days.</td></tr>
<tr><td><strong>TIME</strong> (<code>HH:MM</code>)</td><td>Must be one of the half-hourly slots from <code>09:00</code> to <code>16:30</code> (i.e. <code>09:00</code>, <code>09:30</code>, <code>10:00</code>, … <code>16:30</code>).</td></tr>
<tr><td><strong>DOCTOR_NAME</strong></td><td>Must exactly match an existing doctor's name (case-insensitive).</td></tr>
<tr><td><strong>PATIENT_NAME</strong></td><td>Must exactly match an existing patient's name (case-insensitive).</td></tr>
</table>

<box type="info" seamless>

**Additional assumptions:**
* **Doctor duplicate detection:** Two doctors are considered duplicates if they share the same name (case-insensitive) **and** either the same phone number or the same email.
* **Schedule window:** Doctor schedules are displayed and bookable for a rolling 7-day window from today.
* **Doctor IDs:** Each doctor is automatically assigned a unique, persistent ID that is preserved across edits. IDs are not user-editable.

</box>

--------------------------------------------------------------------------------------------------------------------

### Managing doctors

Commands for adding, editing, and removing doctors from the system.

#### Adding a doctor : `adddoc`

Adds a doctor to the app.

Format: `adddoc n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS`

**Notes:**
* `NAME` is the name of the doctor. It should not be blank. Only alphabets and spaces are allowed.
* `PHONE_NUMBER` should only contain numbers and be at least 3 digits.
* `EMAIL` must match the standard email format (e.g. `name@example.com`).

Examples:
* `adddoc n/John Doe p/98765432 e/johnd@doctor.com a/John street, block 123, #01-01`
* `adddoc n/Betsy Crowe e/betsycrowe@doctor.com a/Newgate Hospital p/1234567`

Expected output:
```
New doctor added: John Doe; Phone: 98765432; Email: johnd@doctor.com; Address: John street, block 123, #01-01
```

#### Editing a doctor : `editdoc`

Edits an existing doctor in the app.

Format: `editdoc INDEX [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS]`

**Notes:**
* Edits the doctor at the specified `INDEX`.
* The index refers to the index number shown in the displayed list. The index must be a positive integer 1, 2, 3, …​
* At least one of the optional fields must be provided.
* Existing values will be updated to the input values.

Examples:
* `editdoc 1 p/91234567 e/johnd@doctor.com` updates the phone and email of the doctor at index 1.
* `editdoc 2 n/Betsy Crower` updates the name of the doctor at index 2.

Expected output:
```
Edited Doctor: John Doe; Phone: 91234567; Email: johnd@doctor.com; Address: 21 Bencoolen
```

#### Deleting a doctor : `deldoc`

Deletes the specified doctor from the app.

Format: `deldoc INDEX`

**Notes:**
* Deletes the doctor at the specified `INDEX`.
* The index refers to the index number shown in the displayed list.
* The index **must be a positive integer** 1, 2, 3, …​

Examples:
* If the list shows (1) Patient, (2) Doctor, (3) Patient — type `deldoc 2` to delete the doctor.

Expected output:
```
Deleted Doctor: John Doe; Phone: 98765432; Email: johnd@doctor.com; Address: John street, block 123, #01-01
```

--------------------------------------------------------------------------------------------------------------------

### Managing patients

Commands for adding, editing, and removing patients from the system.

#### Adding a patient : `addpat`

Adds a patient to the app.

Format: `addpat n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS`

**Notes:**
* `NAME` is the name of the patient. It should not be blank. Only alphabets and spaces are allowed.
* `PHONE_NUMBER` should only contain numbers and be at least 3 digits.
* `EMAIL` must match the standard email format (e.g. `name@example.com`).

Examples:
* `addpat n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01`
* `addpat n/Betsy Crowe e/betsycrowe@example.com a/Newgate Hospital p/1234567`

Expected output:
```
New patient added: John Doe; Phone: 98765432; Email: johnd@example.com; Address: John street, block 123, #01-01
```

#### Editing a patient : `editpat`

Edits the details of an existing patient in the app.

Format: `editpat INDEX [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS]`

**Notes:**
* Edits the patient at the specified `INDEX`.
* The index refers to the index number shown in the displayed list.
* The index **must be a positive integer** 1, 2, 3, …​
* At least one of the optional fields must be provided. e.g. `editpat 2 n/John Doe` is acceptable, but `editpat 2` is not.

Examples:
* `editpat 2 n/John Doe` changes the name of the patient at index 2 to `John Doe`.

Expected output:
```
Edited Patient: John Doe; Phone: 91234567; Email: johndoe@example.com; Address: 123456
```

#### Deleting a patient : `delpat`

Deletes the specified patient from the app.

Format: `delpat INDEX`

**Notes:**
* Deletes the patient at the specified `INDEX`.
* The index refers to the index number shown in the displayed list.
* The index **must be a positive integer** 1, 2, 3, …​

Examples:
* `delpat 2` deletes the 2nd entry in the displayed list, provided it is a patient.

Expected output:
```
Deleted Patient: John Doe; Phone: 98765432; Email: johnd@example.com; Address: John street, block 123, #01-01
```

--------------------------------------------------------------------------------------------------------------------

### Managing appointments

Commands for scheduling, modifying, and cancelling appointments, and viewing doctor schedules.

<box type="tip" seamless>

**Tip:** Use `viewsched` before booking an appointment with `addappt` to confirm which slots are free, so you can advise the patient on available timings.

</box>

#### Viewing a doctor's schedule : `viewsched`

Displays all appointment slots for a specific doctor for a week or on a given date, showing whether each slot is available or booked.

Format: `viewsched d/DOCTOR_NAME [date/YYYY-MM-DD]`

**Notes:**
* `DOCTOR_NAME` must match an existing doctor's name. The match is case-insensitive. e.g. `john tan` will match `John Tan`.
* `DATE` must be in the strict `YYYY-MM-DD` format. Other formats such as `22-02-2026` or `Feb 22 2026` are not accepted.
* The date cannot be in the past and must be within 7 days of today's date.
* Appointment slots are displayed in half-hourly intervals from 09:00 to 17:00.

Examples:
* `viewsched d/John Tan date/2026-04-10` displays John Tan's schedule on 10 Apr 2026.
* `viewsched d/Alice Lim` displays Alice Lim's schedule for the next 7 days.

Expected output:
```
Schedule for John Tan on 2026-04-10
```

#### Adding an appointment : `addappt`

Adds an appointment on a specific date, at a specific time in a doctor's schedule.

Format: `addappt d/DOCTOR_NAME n/PATIENT_NAME date/YYYY-MM-DD time/HH:MM`

**Notes:**
* Books an appointment in the relevant doctor's schedule at the specified date and time.
* Date must be within the next 7 days (counted from today's date).
* Time must fall within operating hours (09:00 to 16:30), in 30-minute intervals.

Examples:
* `addappt d/John Tan n/Jane date/2026-04-10 time/09:00` books an appointment for Jane in Dr John Tan's schedule on 2026-04-10 at 9am. A subsequent `viewsched d/John Tan date/2026-04-10` command will show the 9am slot as `Booked`.

Expected output:
```
New appointment added!
```

#### Editing an appointment : `editappt`

Edits the details of an existing appointment.

Format: `editappt d/OLD_DOCTOR date/OLD_DATE time/OLD_TIME [n/NEW_NAME] [d/NEW_DOC] [date/NEW_DATE] [time/NEW_TIME]`

**Notes:**
* Edits the appointment at the old date and time for the old doctor.
* The new fields in square brackets are optional, but at least one new field must be provided.
  e.g. `editappt d/Louis date/2026-04-10 time/09:00 time/10:00` is acceptable and will rebook the slot to 10:00 for the same patient, but `editappt d/Louis date/2026-04-10 time/09:00` is invalid on its own.

Examples:
* `editappt d/Louis date/2026-04-10 time/09:00 d/Harvey time/10:00` edits the appointment to be with Dr Harvey instead of Dr Louis at 10am on the same date.

Expected output:
```
Appointment edited!
```

#### Deleting an appointment : `delappt`

Deletes an appointment on a specific date, at a specific time from a doctor's schedule.

Format: `delappt d/DOCTOR_NAME n/PATIENT_NAME date/YYYY-MM-DD time/HH:MM`

**Notes:**
* Deletes the appointment in the relevant doctor's schedule at the specified date and time.
* Date must be within the next 7 days (counted from today's date).
* Time must fall within operating hours (09:00 to 16:30), in 30-minute intervals.

Examples:
* If the 9am slot for Dr John Tan on 2026-04-10 was booked, then `delappt d/John Tan n/Jane date/2026-04-10 time/09:00` followed by `viewsched d/John Tan date/2026-04-10` will show the 9am slot as `Available`.

Expected output:
```
Edited Patient: John Doe; Phone: 91234567; Email: johndoe@example.com; Address: 123456; Tags:
```
## Editing an appointment : `editappt`
Edits the details of an existing appointment
Format : `editappt d/OLD_DOCTOR date/OLD_DATE time/OLD_TIME (n/NEW_NAME) (d/NEW_DOC) (date/NEW_DATE) (time/NEW_TIME)`

**Notes**
* Edits the appointment at the old date and time for the old doctor
* The new fields in brackets are optional, but there must be at least one new field to edit.
e.g. `editappt d/Louis date/2026-03-28 time/09:00 time/10:00` is acceptable and will rebook the slot to 10am
for the same patient,but `editappt d/Louis date/2026-03-28 time/09:00` is invalid on its own.

Examples:
* `find John` returns `john` and `John Doe`.
* `find alex david` returns `Alex Yeoh`, `David Li`.

<div class="image-container">

![result for 'find alex david'](images/findAlexDavidResult.png)

</div>

#### Clearing all entries : `clear`

Clears all entries from the app UI temporarily. This does not delete data.

Format: `clear`

#### Exiting the program : `exit`

Exits the program.

Format: `exit`

--------------------------------------------------------------------------------------------------------------------

## Data management

### Saving the data

CLInicDesk data is saved to the hard disk automatically after any command that changes the data. There is no need to save manually.

### Editing the data files

* Doctor data is saved automatically to `[JAR file location]/data/doctors.json`.
* Patient data is saved automatically to `[JAR file location]/data/patients.json`.
* Appointment data is saved automatically to `[JAR file location]/data/schedule.json`.

Advanced users are welcome to update data directly by editing these files.

<box type="warning" seamless>

**Caution:**
If your changes to a data file make its format invalid, CLInicDesk will discard all data and start with an empty data file at the next run. It is recommended to take a backup of the file before editing it.<br>
Furthermore, certain edits can cause CLInicDesk to behave in unexpected ways (e.g. if a value entered is outside the acceptable range). Therefore, edit the data files only if you are confident that you can update them correctly.

</box>
--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another computer?<br>
**A**: Install the app on the other computer and overwrite the empty data files it creates with the files that contain the data from your previous CLInicDesk home folder.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.
2. **If you minimize the Help Window** and then run the `help` command (or use the `Help` menu, or the keyboard shortcut `F1`) again, the original Help Window will remain minimized, and no new Help Window will appear. The remedy is to manually restore the minimized Help Window.

--------------------------------------------------------------------------------------------------------------------

## Command summary

<table class="command-summary-table">
<tr>
  <th>Category</th>
  <th>Action</th>
  <th>Format &amp; Example</th>
</tr>
<tr>
  <td class="cat-doctor" rowspan="3">Doctor<br>Management</td>
  <td><strong>Add Doctor</strong></td>
  <td><code>adddoc n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS</code><br>e.g., <code>adddoc n/James Ho p/22224444 e/jamesho@example.com a/123, Clementi Rd, 1234665</code></td>
</tr>
<tr>
  <td><strong>Edit Doctor</strong></td>
  <td><code>editdoc INDEX [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS]</code><br>e.g., <code>editdoc 1 p/91234567 e/johnd@doctor.com</code></td>
</tr>
<tr>
  <td><strong>Delete Doctor</strong></td>
  <td><code>deldoc INDEX</code><br>e.g., <code>deldoc 3</code></td>
</tr>
<tr class="cat-divider">
  <td class="cat-patient" rowspan="3">Patient<br>Management</td>
  <td><strong>Add Patient</strong></td>
  <td><code>addpat n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS</code><br>e.g., <code>addpat n/James Ho p/22224444 e/jamesho@example.com a/123, Clementi Rd, 1234665</code></td>
</tr>
<tr>
  <td><strong>Edit Patient</strong></td>
  <td><code>editpat INDEX [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS]</code><br>e.g., <code>editpat 2 n/James Ho p/22224444</code></td>
</tr>
<tr>
  <td><strong>Delete Patient</strong></td>
  <td><code>delpat INDEX</code><br>e.g., <code>delpat 2</code></td>
</tr>
<tr class="cat-divider">
  <td class="cat-appt" rowspan="4">Appointment<br>Management</td>
  <td><strong>View Schedule</strong></td>
  <td><code>viewsched d/DOCTOR_NAME [date/YYYY-MM-DD]</code><br>e.g., <code>viewsched d/John Tan date/2026-04-10</code></td>
</tr>
<tr>
  <td><strong>Add Appointment</strong></td>
  <td><code>addappt d/DOCTOR_NAME n/PATIENT_NAME date/YYYY-MM-DD time/HH:MM</code><br>e.g., <code>addappt d/James Ho n/Jane Tan date/2026-04-10 time/09:00</code></td>
</tr>
<tr>
  <td><strong>Edit Appointment</strong></td>
  <td><code>editappt d/OLD_DOCTOR date/OLD_DATE time/OLD_TIME [n/NEW_NAME] [d/NEW_DOC] [date/NEW_DATE] [time/NEW_TIME]</code><br>e.g., <code>editappt d/Louis date/2026-04-10 time/09:00 d/Harvey time/10:00</code></td>
</tr>
<tr>
  <td><strong>Delete Appointment</strong></td>
  <td><code>delappt d/DOCTOR_NAME n/PATIENT_NAME date/YYYY-MM-DD time/HH:MM</code><br>e.g., <code>delappt d/James Ho n/Jane Tan date/2026-04-10 time/09:00</code></td>
</tr>
<tr class="cat-divider">
  <td class="cat-general" rowspan="5">General</td>
  <td><strong>Help</strong></td>
  <td><code>help</code></td>
</tr>
<tr>
  <td><strong>List</strong></td>
  <td><code>list</code></td>
</tr>
<tr>
  <td><strong>Find</strong></td>
  <td><code>find KEYWORD [MORE_KEYWORDS]</code><br>e.g., <code>find James Jake</code></td>
</tr>
<tr>
  <td><strong>Clear</strong></td>
  <td><code>clear</code></td>
</tr>
<tr>
  <td><strong>Exit</strong></td>
  <td><code>exit</code></td>
</tr>
</table>
