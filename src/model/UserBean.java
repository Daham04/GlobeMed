package model;

public class UserBean {

    private int id;
    private String username;
    private String fname;
    private String lname;
    private String mobile;
    private String userRole;
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getFname() {
        return fname;
    }
   
    public void setFname(String fname) {
        this.fname = fname;
    }
    
    public String getLname() {
        return lname;
    }
 
    public void setLname(String lname) {
        this.lname = lname;
    }


    public String getMobile() {
        return mobile;
    }
   
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    
}
