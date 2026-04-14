---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# CLInicDesk Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**
* GitHub Copilot has been used by the team to assist code writing, particularly in helping get unstuck, improving the User Guide's UI, and updating some sections of the Developer Guide.

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/AY2526S2-CS2103T-W12-1/tp/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `deldoc 1`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/AY2526S2-CS2103T-W12-1/tp/tree/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` that is made up of parts e.g. `CommandBox`, `ResultDisplay`, `PersonListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.


The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/AY2526S2-CS2103T-W12-1/tp/tree/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Person` object residing in the `Model`.

### Logic component

**API** : [`Logic.java`](https://github.com/AY2526S2-CS2103T-W12-1/tp/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delpat 1")` API call as an example.

<puml src="diagrams/DeletePatientSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delpat 1` Command" />

<box type="info" seamless>

**Note:** The lifeline for `DeletePatCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `AddressBookParser` object which in turn creates a parser that matches the command (e.g., `DeletePatCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeletePatCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddCommand`) which the `AddressBookParser` returns back as a `Command` object.
* All `XYZCommandParser` classes (e.g., `AddCommandParser`, `DeleteCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

### Model component
**API** : [`Model.java`](https://github.com/AY2526S2-CS2103T-W12-1/tp/tree/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the currently 'selected' `Person` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Person>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)


### Storage component

**API** : [`Storage.java`](https://github.com/AY2526S2-CS2103T-W12-1/tp/tree/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from both `AddressBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.address.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### \[Planned\] Command History Retention

#### Planned Enhancement

The proposed command history retention feature allows users to navigate through previously executed commands using arrow keys, similar to how a standard CLI (Command Line Interface) works. This feature is facilitated by a `CommandHistory` component that maintains a chronological record of all executed commands.

The `CommandHistory` component will implement the following operations:

* `CommandHistory#addCommand(String command)` — Records a new command in the history.
* `CommandHistory#getPreviousCommand()` — Retrieves the previous command in the history.
* `CommandHistory#getNextCommand()` — Retrieves the next command in the history.
* `CommandHistory#getCurrentIndex()` — Returns the current position in the history.

These operations will be exposed in the `UI` component's `CommandBox` class, which handles user input and key event processing. When a user presses the **Up Arrow** key, the system will retrieve the previous command from history and populate it in the command input field. Pressing the **Down Arrow** key will retrieve the next command.

#### Usage Scenario

Step 1. The user launches the application. The `CommandHistory` is initialized as empty.

Step 2. The user executes the command `addpat n/John Doe p/81234567 e/john@example.com a/123 Main St`. This command is recorded in the command history.

Step 3. The user executes the command `viewsched d/Dr. Smith id/1`. This command is also recorded in the history.

Step 4. The user presses the **Up Arrow** key while the command box is focused. The previous command `viewsched d/Dr. Smith id/1` is retrieved from history and displayed in the command box.

Step 5. The user presses the **Up Arrow** key again. The command `addpat n/John Doe p/81234567 e/john@example.com a/123 Main St` is now displayed, moving further back in history.

Step 6. The user presses the **Down Arrow** key. The command `viewsched d/Dr. Smith id/1` is displayed again, moving forward in the command history.

Step 7. When the user presses the **Down Arrow** key and reaches the most recent command, subsequent presses will clear the command box, allowing the user to type a new command.

#### Design Considerations

**Aspect: Storage of command history:**

* **Alternative 1 (current choice):** Store command history in-memory during the application session.
  * Pros: Simple to implement, fast access, no disk I/O overhead.
  * Cons: Command history is lost when the application is closed.

* **Alternative 2:** Persist command history to disk (e.g., in a history file).
  * Pros: Command history is retained across sessions, allowing users to access commands from previous runs.
  * Cons: Requires file I/O, adds complexity, potential privacy concerns if sensitive commands are stored.

**Aspect: Handling of failed commands:**

* **Alternative 1:** Record all commands in history, regardless of success or failure.
  * Pros: Users can recall and re-execute failed commands with modifications.
  * Cons: History may become cluttered with erroneous commands.

* **Alternative 2 (current choice):** Record only successfully executed commands.
  * Pros: Keeps history clean and focused on valid operations.
  * Cons: Users cannot quickly recall the exact syntax of a failed command.

**Aspect: History size limit:**

* **Alternative 1:** No limit on command history size.
  * Pros: Users can access all historical commands from the session.
  * Cons: May consume significant memory for long sessions.

* **Alternative 2 (current choice):** Implement a configurable limit (e.g., last 100 commands).
  * Pros: Bounded memory usage, maintains reasonable history depth.
  * Cons: Older commands will be discarded once the limit is reached.

_{more aspects and alternatives to be added}_

--------------------------------------------------------------------------------------------------------------------

#### \[Planned\] Patient Appointment Viewing

#### Planned Enhancement

The proposed patient appointment viewing feature allows receptionists to view a patient's upcoming appointments directly, complementing the existing doctor schedule viewing functionality. This addresses the need for quick access to patient-specific appointment information during clinic operations, such as when patients call to confirm or reschedule their appointments.

This feature will introduce a new command, such as `viewpatappt`, that takes a patient identifier (name or ID) and optionally a date range, displaying all future appointments for that patient, including doctor details, appointment times, and appointment IDs.

#### Usage Scenario

Step 1. A patient calls the clinic and cannot remember their next appointment details.

Step 2. The receptionist enters the command `viewpatappt n/John Doe` to view John's upcoming appointments.

Step 3. The system validates the patient name and retrieves their appointments.

Step 4. The system displays a list of John's future appointments, showing dates, times, assigned doctors, and appointment IDs.

Step 5. The receptionist informs the patient of their next appointment and can proceed to book, edit, or cancel appointments as needed.

   Use case ends.

**Extensions**

* 3a. The patient name does not match any existing patient.
  * 3a1. System shows: `Patient not found.`

    Use case resumes at step 2.

* 3b. The patient has no upcoming appointments.
  * 3b1. System shows: `No upcoming appointments found for this patient.`

    Use case ends.

#### Design Considerations

**Aspect: Patient identification method:**

* **Alternative 1:** Use patient name (e.g., `viewpatappt n/John Doe`).
  * Pros: Intuitive and easy to remember, aligns with existing command patterns.
  * Cons: Potential ambiguity if multiple patients share similar names.

* **Alternative 2 (current choice):** Use patient ID (e.g., `viewpatappt id/1`).
  * Pros: Unique identification, eliminates name conflicts, consistent with appointment management commands.
  * Cons: Requires receptionist to know or quickly look up patient ID.

**Aspect: Date range and filtering:**

* **Alternative 1:** Show all future appointments without limit.
  * Pros: Provides complete appointment history and future schedule.
  * Cons: May overwhelm users with extensive appointment lists, especially for long-term patients.

* **Alternative 2 (current choice):** Limit to next 7-14 days by default, with optional date range parameters.
  * Pros: Focuses on immediate and relevant appointments, reduces information overload, matches the 7-day limit in doctor schedule viewing.
  * Cons: May miss important longer-term appointments.

**Aspect: Display format and information:**

* **Alternative 1:** Simple list format showing appointment details in chronological order.
  * Pros: Consistent with existing list-based displays, easy to implement.
  * Cons: May not provide clear overview for patients with multiple appointments.

* **Alternative 2:** Grouped by date with doctor information highlighted.
  * Pros: Better organization for multiple appointments, easier to scan for specific dates.
  * Cons: More complex implementation, potential inconsistency with other views.

_{more aspects and alternatives to be added}_

--------------------------------------------------------------------------------------------------------------------

### \[Planned\] App Reset Feature

#### Planned Enhancement

The proposed app reset feature allows users to clear all stored data in the application, providing a clean slate for testing purposes or starting fresh after a period of use. This feature introduces a new command, such as `reset`, that removes all patients, doctors, appointments, and any other stored information, resetting the app to its initial state with no data loaded.

#### Usage Scenario

Step 1. The user decides to reset the app, perhaps for testing new features or clearing accumulated data.

Step 2. The user enters the `reset` command.

Step 3. The system prompts for confirmation to ensure the user intends to clear all data.

Step 4. The user confirms the reset by typing 'yes' or a similar affirmative response.

Step 5. The system clears all data from memory and storage files, then confirms the reset with a message indicating the app has been reset.

   Use case ends.

**Extensions**

* 3a. The user cancels the confirmation.
  * 3a1. System shows: `Reset cancelled. No data was cleared.`

    Use case ends.

* 4a. The user provides an invalid confirmation response.
  * 4a1. System shows: `Invalid response. Reset cancelled.`

    Use case resumes at step 3.

#### Design Considerations

**Aspect: Confirmation mechanism:**

* **Alternative 1 (current choice):** Require explicit user confirmation before proceeding with the reset.
  * Pros: Prevents accidental data loss, gives users a chance to reconsider.
  * Cons: Adds an extra step to the process, may be inconvenient for automated testing.

* **Alternative 2:** No confirmation required.
  * Pros: Faster execution, suitable for scripts or automated processes.
  * Cons: High risk of accidental data loss, especially in production environments.

**Aspect: Scope of data reset:**

* **Alternative 1 (current choice):** Clear all data including patients, doctors, appointments, and user preferences.
  * Pros: Provides a complete clean slate, ensures no residual data remains.
  * Cons: May be too aggressive for users who want to keep some data.

* **Alternative 2:** Allow selective reset (e.g., reset only patients or only appointments).
  * Pros: Gives users more control over what data to clear.
  * Cons: Increases command complexity, requires additional parameters and validation.

**Aspect: Data persistence after reset:**

* **Alternative 1:** Immediately overwrite storage files with empty data.
  * Pros: Ensures data is permanently cleared, no recovery possible.
  * Cons: Irreversible action, potential for regret.

* **Alternative 2:** Clear in-memory data but keep backup of storage files.
  * Pros: Allows recovery if reset was accidental.
  * Cons: Doesn't provide a true "clean slate", may confuse users expecting complete reset.

_{more aspects and alternatives to be added}_

--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* receptionist at a small-scale medical centre with 3-5 practitioners
* needs to manage a significant number of contacts for patients, doctors, and scheduling appointments
* frequently multitasks while on calls with doctors and patients booking appointments and needs to be able to switch attention quickly between patients, practitioners and schedules
* works alone most of the time, occasionally overlapping with another receptionist during peak hours
* works under time pressure, especially during peak hours
* uses a shared desktop computer at the front desk
* prefer desktop apps over other types
* can type fast
* prefers typing to mouse interactions
* is reasonably comfortable using CLI apps

**Value proposition**: quickly access patient contact details, practitioner schedules and scheduled appointments
for patients faster than a typical mouse/GUI driven app via high-speed, keyboard-driven workflows, reducing time spent
searching, scrolling, or clicking during live interactions.


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …​ | I want to …​                                               | So that I can…​                                             |
|----------|----------|------------------------------------------------------------|-------------------------------------------------------------|
| `* * *`  | new receptionist | see sample patient and practitioner data                   | understand what kind of information the system holds        |
| `* * *`  | first-time user | understand what each field represents                      | avoid misusing or misunderstanding stored information       |
| `* * *`  | receptionist | add a new patient's contact details                        | quickly reference them in future interactions               |
| `* * *`  | receptionist | add a new doctor's contact details                         | track which doctors are available for appointments          |
| `* * *`  | receptionist | delete a patient who no longer visits the clinic           | keep search results uncluttered                             |
| `* * *`  | receptionist | delete a doctor from the system                            | remove practitioners who are no longer with the clinic      |
| `* * *`  | receptionist | view a practitioner's schedule for a specific day          | answer availability questions quickly                       |
| `* * *`  | receptionist | book an appointment slot for a patient with a doctor       | confirm appointments during calls                           |
| `* * *`  | receptionist | cancel an existing appointment                             | free up slots when patients reschedule or cancel            |
| `* *`    | receptionist | search for a patient by name                               | look up their contact details                               |
| `* *`    | receptionist | view a patient's contact details in one place              | avoid asking the patient for information repeatedly         |
| `* *`    | receptionist | update a patient's contact information                     | keep records accurate                                       |
| `* *`    | receptionist | see which practitioners are currently on duty              | avoid giving incorrect availability information to patients |
| `* *`    | receptionist | quickly switch between different practitioners' schedules  | compare availability during a call                          |
| `* *`    | receptionist who types fast | perform common actions using the keyboard                  | avoid slowing down to use the mouse                         |
| `* *`    | receptionist | correct mistakes quickly                                   | ensure small typing errors don't disrupt my workflow        |
| `*`      | receptionist | check upcoming availability without leaving my current task | stay focused during calls                                   |
| `*`      | receptionist | return to my previous view quickly                         | avoid losing context during busy periods                    |
| `*`      | receptionist returning after a break | quickly regain an overview of practitioners and patients   | resume work smoothly                                        |
| `*`      | receptionist | rely on consistent data organisation                       | avoid relearning the system after time away                 |

---

### Use cases

(For all use cases below, the **System** is `CLInicDesk` and the **Actor** is the `receptionist`, unless specified otherwise)

---

**Use case: Add a doctor**

**MSS**

1. Receptionist enters the add doctor command with name, phone, email, and address.
2. System validates all fields.
3. System adds the doctor and confirms with doctor details.

   Use case ends.

**Extensions**

* 2a. The name contains invalid characters.
  * 2a1. System shows: `Names should only contain letters, and may include single spaces, apostrophes, or hyphens between words. Name should not be blank`

    Use case resumes at step 1.

* 2b. The phone number is not exactly 8 digits.
  * 2b1. System shows: `Phone numbers should only contain numbers, and it should be 8 digits long`

    Use case resumes at step 1.

* 2c. The email is not in a valid format.
  * 2c1. System shows:
  ```
    Emails should be of the format local-part@domain and adhere to the following constraints:
    1. The local-part should only contain alphanumeric characters and these special characters, excluding the parentheses, (+_.-). The local-part may not start or end with any special characters.
    2. This is followed by a '@' and then a domain name. The domain name is made up of domain labels separated by periods.
       The domain name must:
        - end with a domain label at least 2 characters long
        - have each domain label start and end with alphanumeric characters
        - have each domain label consist of alphanumeric characters, separated only by hyphens, if any.
   ```

    Use case resumes at step 1.

* 2d. A doctor with the same name (case-insensitive) and email/phone already exists.
  * 2d1. System shows: `This doctor already exists in the app`

    Use case ends.

---

**Use case: Edit a doctor**

**MSS**

1. Receptionist views the list.
2. System displays the list with indices.
3. Receptionist enters the `editdoc` command with the target index and the fields to update (name, phone, email, and/or address).
4. System validates all provided fields.
5. System updates the doctor entry and confirms with the updated record's details.

   Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. A name is entered instead of an index.
    * 3a1. System shows:
  ```
  Invalid command format!
  editdoc: Edits the details of the doctor identified by the index number used in the displayed list. Existing values will be overwritten by the input values.
  Parameters: INDEX (must be a positive integer) [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS]
  Example: editdoc 1 p/91234567 e/johndoe@example.com
  ```

  Use case resumes at step 2.

* 3b. The index does not refer to any entry in the currently displayed list.
    * 3b1. System shows: `The doctor index provided is invalid`

      Use case resumes at step 2.

* 3c. The edited entry is not a doctor.
    * 3c1. System shows `The person at the specified index is not a doctor.`

      Use case resumes at step 2.

* 4a. The name contains invalid characters.
    * 4a1. System shows: `Names should only contain letters, and may include single spaces, apostrophes, or hyphens between words. Name should not be blank`

      Use case resumes at step 3.

* 4b. The phone number is not exactly 8 digits.
    * 4b1. System shows: `Phone numbers should only contain numbers, and it should be 8 digits long`

      Use case resumes at step 3.

* 4c. The email is not in a valid format.
    * 4c1. System shows the same email validation error message as "Add a doctor" use case.

      Use case resumes at step 3.

* 4d. The updated name and email or phone conflicts with an existing doctor's details.
    * 4d1. System shows: `This doctor already exists in the app`

      Use case resumes at step 3.

---

**Use case: Delete a doctor**

**MSS**

1. Receptionist views the list.
2. System displays the list with indices.
3. Receptionist enters the `deldoc` command with the target index.
4. System deletes the doctor entry and confirms with the deleted record's details.

   Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. A name is entered instead of an index.
    * 3a1. System shows:
  ```
  Invalid command format!
  deldoc: Deletes the doctor identified by the index number used in the displayed doctor list.
  Parameters: INDEX (must be a positive integer)
  Example: deldoc 1
  ```

  Use case resumes at step 2.

* 3b. The index does not refer to any entry in the currently displayed list.
    * 3b1. System shows: `The doctor index provided is invalid`

      Use case resumes at step 2.

* 3c. The deleted entry is not a doctor.
    * 3c1. System shows `The person at the specified index is not a doctor.`

      Use case resumes at step 2.

---

**Use case: Add a patient**

**MSS**

1. Receptionist enters the add patient command with name, phone, email, and address.
2. System validates all fields.
3. System adds the patient and confirms with patient details.

   Use case ends.

**Extensions**

* 2a. The name contains invalid characters.
  * 2a1. System shows: `Names should only contain letters, and may include single spaces, apostrophes, or hyphens between words. Name should not be blank`

    Use case resumes at step 1.

* 2b. The phone number is not exactly 8 digits.
  * 2b1. System shows: `Phone numbers should only contain numbers, and it should be 8 digits long`

    Use case resumes at step 1.

* 2c. The email is not in a valid format.
  * 2c1. System shows:
  ```
      Emails should be of the format local-part@domain and adhere to the following constraints:
    1. The local-part should only contain alphanumeric characters and these special characters, excluding the parentheses, (+_.-). The local-part may not start or end with any special characters.
    2. This is followed by a '@' and then a domain name. The domain name is made up of domain labels separated by periods.
       The domain name must:
        - end with a domain label at least 2 characters long
        - have each domain label start and end with alphanumeric characters
        - have each domain label consist of alphanumeric characters, separated only by hyphens, if any.
  ```

    Use case resumes at step 1.

* 2d. A patient with the same name (case-insensitive) and same email already exists.
  * 2d1. System shows: `This patient already exists in the app`

    Use case ends.

---

**Use case: Edit a patient**

**MSS**

1. Receptionist views the list.
2. System displays the list with indices.
3. Receptionist enters the `editpat` command with the target index and the fields to update (name, phone, email, and/or address).
4. System validates all provided fields.
5. System updates the patient entry and confirms with the updated record's details.

   Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. A name is entered instead of an index.
    * 3a1. System shows:
  ```
  Invalid command format!
  editpat: Edits the details of the patient identified by the index number used in the displayed list. Existing values will be overwritten by the input values.
  Parameters: INDEX (must be a positive integer) [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS]
  Example: editpat 1 p/91234567 e/johndoe@example.com
  ```

  Use case resumes at step 2.

* 3b. The index does not refer to any entry in the currently displayed list.
    * 3b1. System shows: `The patient index provided is invalid`

      Use case resumes at step 2.

* 3c. The edited entry is not a patient.
    * 3c1. System shows `The person at the specified index is not a patient.`

      Use case resumes at step 2.

* 4a. The name contains invalid characters.
    * 4a1. System shows: `Names should only contain letters, and may include single spaces, apostrophes, or hyphens between words. Name should not be blank`

      Use case resumes at step 3.

* 4b. The phone number is not exactly 8 digits.
    * 4b1. System shows: `Phone numbers should only contain numbers, and it should be 8 digits long`

      Use case resumes at step 3.

* 4c. The email is not in a valid format.
    * 4c1. System shows the same email validation error message as "Add a patient" use case.

      Use case resumes at step 3.

* 4d. The updated name and email conflict with an existing patient's details.
    * 4d1. System shows: `This patient already exists in the app`

      Use case resumes at step 3.

---

**Use case: Delete a patient**

**MSS**

1. Receptionist views the list.
2. System displays the list with indices.
3. Receptionist enters the `delpat` command with the target index.
4. System deletes the patient entry and confirms with the deleted record's details.

   Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. A name is entered instead of an index.
    * 3a1. System shows:
  ```
  Invalid command format!
  delpat: Deletes the patient identified by the index number used in the displayed patient list.
  Parameters: INDEX (must be a positive integer)
  Example: delpat 1
  ```

  Use case resumes at step 2.

* 3b. The index does not refer to any entry in the currently displayed list.
    * 3b1. System shows: `The patient index provided is invalid`

      Use case resumes at step 2.

* 3c. The deleted entry is not a patient.
    * 3c1. System shows `The person at the specified index is not a patient.`

      Use case resumes at step 2.

---

**Use case: View a doctor's schedule**

**MSS**

1. Receptionist enters the view schedule command with a doctor name, doctor id and optionally a date.
2. System validates the doctor name and id (and, if exists, the date).
3. System displays all half-hourly slots for that doctor for next 7 days or on a specific date, each marked as Available or Booked (with patient name, patient id and appointment id).

   Use case ends.

**Extensions**

* 2a. The doctor name does not match any existing doctor.
  * 2a1. System shows: `Doctor not found.`

    Use case ends.

* 2b. The date is in an invalid format.
  * 2b1. System shows:
  ```
  Invalid command format!
  viewsched: Views the schedule of a doctor (optionally for a specific date).
  Parameters: d/DOCTOR_NAME id/DOCTOR_ID [date/YYYY-MM-DD]
  Example: viewsched d/John Tan id/1 date/2026-03-20
  ```

    Use case resumes at step 1.

* 2c. The date is in the past.
  * 2c1. System shows: `No schedule available for this date.`

    Use case resumes at step 1.

---

**Use case: Add an appointment**

**MSS**

1. Receptionist views the doctor's schedule (see Use case: View a doctor's schedule).
2. Receptionist enters the add appointment command with patient ID, doctor ID, date, and time.
3. System validates all fields and checks slot availability.
4. System books the appointment and confirms with appointment details.

   Use case ends.

**Extensions**

* 3a. The patient ID does not match any existing patient.
  * 3a1. System shows: `Patient not found: <ID>`

    Use case ends.

* 3b. The doctor ID does not match any existing doctor.
  * 3b1. System shows: `Doctor not found: <ID>`

    Use case ends.

* 3c. The date is invalid or in the past.
  * 3c1. Date in the past, system shows: `Appointment date must be within 7 days from today!`
  * 3c2. Invalid date, system shows: `Please input a valid date. The date must be formatted as YYYY-MM-DD`

    Use case resumes at step 2.

* 3d. The time is not one of the valid half-hourly slots (09:00–16:30).
  * 3d1. System shows: `The time <HH:MM> is not a valid 30-minute slot for this doctor.`

    Use case resumes at step 2.

* 3e. The selected slot is already booked with that doctor.
  * 3e1. System shows: `This slot is already booked. Please edit the appointment if you wish to change it`

    Use case resumes at step 1.

* 3f. The patient already has an appointment at the same date and time.
  * 3f1. System shows that the appointment slot is taken.

    Use case resumes at step 2.

---

**Use case: Edit an appointment**

**MSS**

1. Receptionist views a doctor's schedule (see Use case: View a doctor's schedule).
2. Receptionist identifies an appointment to edit.
3. Receptionist enters the `editappt` command with the appointment ID and the fields to update (patient ID, doctor ID, date, and/or time).
4. System validates all provided fields and checks slot availability.
5. System updates the appointment entry and confirms with the updated record's details.

   Use case ends.

**Extensions**

* 3a. The appointment ID does not exist.
  * 3a1. System shows: `Appointment not found: <ID>`

    Use case ends.

* 4a. The patient ID does not match any existing patient.
  * 4a1. System shows: `Patient not found: <ID>`

    Use case resumes at step 3.

* 4b. The doctor ID does not match any existing doctor.
  * 4b1. System shows: `Doctor not found: <ID>`

    Use case resumes at step 3.

* 4c. The date is invalid or in the past.
  * 4c1. System shows the appropriate date validation error message.

    Use case resumes at step 3.

* 4d. The time is not one of the valid half-hourly slots (09:00–16:30).
  * 4d1. System shows: `The time <HH:MM> is not a valid 30-minute slot for this doctor.`

    Use case resumes at step 3.

* 4e. The selected slot is already booked with the doctor.
  * 4e1. System shows: `Could not edit appointment: This slot is already booked. Please edit the appointment if you wish to change it`

    Use case resumes at step 3.

* 4f. The patient already has an appointment at the new date and time.
  * 4f1. System shows: `Patient already has an appointment at this time.`

    Use case resumes at step 3.

---

**Use case: Delete an appointment**

**MSS**

1. Receptionist views a doctor's schedule (see Use case: View a doctor's schedule).
2. Receptionist identifies an appointment to delete.
3. Receptionist enters the `delappt` command with the appointment ID.
4. System deletes the appointment entry and confirms with the deleted record's details.

   Use case ends.

**Extensions**

* 3a. The appointment ID does not exist.
  * 3a1. System shows: `Appointment not found: ID`

    Use case ends.

* 3b. The appointment ID format is invalid.
  * 3b1. System shows: 
  ```
  Invalid command format!
  delappt: Deletes the appointment identified by the appointment ID.
  Parameters: ID (must be a valid appointment ID)
  Example: delappt 1
  ```

    Use case ends.

---

**Use case: Find persons**

**MSS**

1. Receptionist enters the `find` command with one or more search keywords.
2. System searches for persons (patients or doctors) matching the keywords.
3. System displays a filtered list of all matching persons with their indices.

   Use case ends.

**Extensions**

* 1a. The search keywords are empty.
  * 1a1. System shows: 
  ```
  Invalid command format!
  find: Finds all persons whose names contain any of the specified keywords (case-insensitive) and displays them as a list with index numbers.
  Parameters: KEYWORD [MORE_KEYWORDS]...
  Example: find alice bob charlie
  ```

    Use case ends.

* 2a. No persons match the search criteria.
  * 2a1. System displays an empty list and shows: `0 persons listed!`

    Use case ends.

* 2b. Multiple persons match the search criteria.
  * 2b1. System displays all matching persons in a filtered list with their indices.

    Use case ends.

* 2c. The search matches both patients and doctors.
  * 2c1. System displays all matching persons from both categories.

    Use case ends.

---

### Non-Functional Requirements

1. Should work on any _mainstream OS_ as long as it has Java `17` or above installed.
2. Should be able to hold up to 1000 patients, doctors and appointments without a noticeable sluggishness in performance for typical usage.
3. The response to any command should be visible within 5 seconds of entering the command.
4. A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
5. A new user should be able to learn how to use the commands easily.
6. The user interface and user guide should be intuitive enough for a new user.

### Glossary

* **Mainstream OS**: Windows, Linux, Unix, MacOS
* **Private contact detail**: A contact detail that is not meant to be shared with others
* **Slot**: A time slot (30-minute intervals from 09:00 to 16:30) in a doctor's schedule
* **Schedule**: A calendar of available and booked time slots for a doctor
* **Patient ID**: A unique auto-generated identifier for each patient (e.g., 1, 2, 3, ...)
* **Doctor ID**: A unique auto-generated identifier for each doctor (e.g., 1, 2, 3, ...)
* **Index**: A one-based number used to identify a person's position in the displayed list (e.g., 1st person, 2nd person)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</box>

### Launch and shutdown

1. Initial launch

   1. Download the jar file and copy into an empty folder

   2. Open a terminal and run `cd PATH_TO_FOLDER` to change directory to the location of the jar file.

   3. Run the command `java -jar clinicdesk.jar` to launch the app.<br>
       Expected: App launches successfully without any error.

1. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   2. Re-launch the app by double-clicking the jar file.<br>
       Expected: The most recent window size and location is retained.


### Deleting a doctor

1. Deleting a person while all persons are being shown

   1. Prerequisites: List all persons using the `list` command. Multiple persons in the list. Doctor is first person in the list.

   2. Test case: `deldoc 1`<br>
      Expected: Doctor at index 1 is deleted from the list. Details of the deleted contact shown in the status message.

   3. Test case: `deldoc 0`<br>
      Expected: No person is deleted. Error details shown in the status message.

   4. Other incorrect delete commands to try: `deldoc`, `deldoc x` (where x is larger than the list size)<br>
      Expected: Similar to previous.


### Saving data

1. Dealing with missing data files
   1. When a data file (patients.json, doctors.json, schedule.json) is missing, the app starts with an empty data container for that file.

   2. The app will continue to function normally, but there is no data loaded for that file.

   3. Example: If patients.json is missing, the app starts with no patients but continues to function normally.

2. Dealing with corrupted data files
   1. If a data file exists but contains invalid JSON format or illegal values, no data will be loaded.

   2. To solve this, the user can either fix the corrupted file (e.g., by correcting the JSON format or values) or delete the corrupted file to start with an empty data container for that file before relaunching the app.

   3. Example: If patients.json contains malformed JSON, the app will discard it and start with no data entry.

**Data loss prevention:**
- Make regular backups of the `data/` folder
- Only edit JSON files if you understand the structure
- Corrupted files are not repaired automatically; deleted data cannot be recovered

