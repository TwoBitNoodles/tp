package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Doctor;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Patient;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;

/**
 * Jackson-friendly version of {@link Person}.
 */
class JsonAdaptedPerson {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Person's %s field is missing!";

    private static final String TYPE_DOCTOR = "doctor";
    private static final String TYPE_PATIENT = "patient";
    private static final String TYPE_PERSON = "person";
    // We understood that we need to add a type to the json to differentiate b/w
    // doctors and patients, and asked Copilot to help identify what to change in order
    // to make data correctly persist using type
    private final String type;
    private final String name;
    private final String phone;
    private final String email;
    private final String address;
    private final Integer docId;
    private final Integer patId;

    /**
     * Constructs a {@code JsonAdaptedPerson} with the given person details.
     */
    @JsonCreator
    public JsonAdaptedPerson(@JsonProperty("type") String type,
            @JsonProperty("name") String name, @JsonProperty("phone") String phone,
            @JsonProperty("email") String email, @JsonProperty("address") String address,
            @JsonProperty("docId") Integer docId,
            @JsonProperty("patId") Integer patId) {
        this.type = type;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.docId = docId;
        this.patId = patId;
    }

    /**
     * Converts a given {@code Person} into this class for Jackson use.
     */
    public JsonAdaptedPerson(Person source) {
        if (source instanceof Doctor) {
            type = TYPE_DOCTOR;
            docId = ((Doctor) source).getDocId();
            patId = null;
        } else if (source instanceof Patient) {
            type = TYPE_PATIENT;
            docId = null;
            patId = ((Patient) source).getPatientId();
        } else {
            type = TYPE_PERSON;
            docId = null;
            patId = null;
        }
        name = source.getName().fullName;
        phone = source.getPhone().value;
        email = source.getEmail().value;
        address = source.getAddress().value;
    }

    /**
     * Converts this Jackson-friendly adapted person object into the model's {@code Person} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person.
     */
    public Person toModelType() throws IllegalValueException {
        final Name modelName = validateAndGetName();
        final Phone modelPhone = validateAndGetPhone();
        final Email modelEmail = validateAndGetEmail();
        final Address modelAddress = validateAndGetAddress();

        if (TYPE_DOCTOR.equals(type)) {
            if (docId != null) {
                return new Doctor(modelName, modelPhone, modelEmail, modelAddress, docId);
            }
            return new Doctor(modelName, modelPhone, modelEmail, modelAddress);
        } else if (TYPE_PATIENT.equals(type)) {
            if (patId != null) {
                return new Patient(modelName, modelPhone, modelEmail, modelAddress, patId);
            }
            return new Patient(modelName, modelPhone, modelEmail, modelAddress);
        }
        return new Person(modelName, modelPhone, modelEmail, modelAddress);
    }

    private Name validateAndGetName() throws IllegalValueException {
        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        if (!Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        return new Name(name);
    }

    private Phone validateAndGetPhone() throws IllegalValueException {
        if (phone == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName()));
        }
        if (!Phone.isValidPhone(phone)) {
            throw new IllegalValueException(Phone.MESSAGE_CONSTRAINTS);
        }
        return new Phone(phone);
    }

    private Email validateAndGetEmail() throws IllegalValueException {
        if (email == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName()));
        }
        if (!Email.isValidEmail(email)) {
            throw new IllegalValueException(Email.MESSAGE_CONSTRAINTS);
        }
        return new Email(email);
    }

    private Address validateAndGetAddress() throws IllegalValueException {
        if (address == null) {
            throw new IllegalValueException(
                    String.format(MISSING_FIELD_MESSAGE_FORMAT, Address.class.getSimpleName()));
        }
        if (!Address.isValidAddress(address)) {
            throw new IllegalValueException(Address.MESSAGE_CONSTRAINTS);
        }
        return new Address(address);
    }

}
