package bank.model;

import bank.exception.InvalidInputException;
import bank.util.Validator;
import java.io.Serializable;

/** ENCAPSULATION: private fields, validated construction, read-only access. */
public class Customer implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String phone;
    private String email;

    public Customer(String name, String phone, String email) throws InvalidInputException {
        this.name = Validator.requireName(name);
        this.phone = Validator.requirePhone(phone);
        this.email = Validator.requireEmail(email);
    }

    public String getName()  { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }

    public void setPhone(String phone) throws InvalidInputException {
        this.phone = Validator.requirePhone(phone);
    }

    public void setEmail(String email) throws InvalidInputException {
        this.email = Validator.requireEmail(email);
    }
}
