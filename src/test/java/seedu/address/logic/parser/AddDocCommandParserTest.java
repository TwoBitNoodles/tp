package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.ADDRESS_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.EMAIL_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.NAME_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.PHONE_DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddDocCommand;
import seedu.address.model.person.Address;
import seedu.address.model.person.Doctor;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;

public class AddDocCommandParserTest {
    private AddDocCommandParser parser = new AddDocCommandParser();

    @Test
    public void parse_noFieldsMissing_success() {
        Doctor bob = new Doctor(new Name(VALID_NAME_BOB),
                new Phone(VALID_PHONE_BOB), new Email(VALID_EMAIL_BOB),
                new Address(VALID_ADDRESS_BOB));
        AddDocCommand adddocCommand = new AddDocCommand(bob);
        assertParseSuccess(parser, NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB
                + ADDRESS_DESC_BOB, adddocCommand);
    }

    @Test
    public void parse_missingNameField_failure() {
        assertParseFailure(parser, PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddDocCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_missingPhoneField_failure() {
        assertParseFailure(parser, NAME_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddDocCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_missingEmailField_failure() {
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + ADDRESS_DESC_BOB,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddDocCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_missingAddressField_failure() {
        assertParseFailure(parser, NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddDocCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidPhoneFormat_failure() {
        assertParseFailure(parser, NAME_DESC_BOB + " p/invalid" + EMAIL_DESC_BOB + ADDRESS_DESC_BOB,
                Phone.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_duplicateNamePrefix_failure() {
        assertParseFailure(parser,
                NAME_DESC_BOB + NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));
    }

    @Test
    public void parse_duplicatePhonePrefix_failure() {
        assertParseFailure(parser,
                NAME_DESC_BOB + PHONE_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));
    }

    @Test
    public void parse_duplicateEmailPrefix_failure() {
        assertParseFailure(parser,
                NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_EMAIL));
    }

    @Test
    public void parse_duplicateAddressPrefix_failure() {
        assertParseFailure(parser,
                NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB + ADDRESS_DESC_BOB,
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ADDRESS));
    }

    @Test
    public void parse_extraTextBeforeFirstPrefix_failure() {
        assertParseFailure(parser,
                "bleh9/" + NAME_DESC_BOB + PHONE_DESC_BOB + EMAIL_DESC_BOB + ADDRESS_DESC_BOB,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddDocCommand.MESSAGE_USAGE));
    }
}
