package model;

public class Patient {
    private String nic;
    private String firstName;
    private String lastName;
    private String gender;
    private String mobile;
    private int age;
    private String addressLine1;
    private String addressLine2;

    // constructor
    public Patient(String nic, String firstName, String lastName, String gender, 
                   String mobile, int age, String addressLine1, String addressLine2) {
        this.nic = nic;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.mobile = mobile;
        this.age = age;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
    }

    // getters
    public String getNic() { return nic; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getGender() { return gender; }
    public String getMobile() { return mobile; }
    public int getAge() { return age; }
    public String getAddressLine1() { return addressLine1; }
    public String getAddressLine2() { return addressLine2; }
}
