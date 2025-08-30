package model;

public class Staff {

    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String mobile;
    private String password;
    private String role;

    // ✅ Private constructor for Builder
    private Staff(Builder builder) {
        this.username = builder.username;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.email = builder.email;
        this.mobile = builder.mobile;
        this.password = builder.password;
        this.role = builder.role;
    }

    // ✅ Builder Class
    public static class Builder {
        private String username;
        private String firstName;
        private String lastName;
        private String email;
        private String mobile;
        private String password;
        private String role;

        public Builder setUsername(String username) {
            this.username = username;
            return this;   // <-- return Builder for chaining
        }

        public Builder setFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder setLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setMobile(String mobile) {
            this.mobile = mobile;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setRole(String role) {
            this.role = role;
            return this;
        }

        public Staff build() {
            return new Staff(this);
        }
    }

    // Getters
    public String getUsername() { return username; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getMobile() { return mobile; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
}
