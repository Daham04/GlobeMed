package gui;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.Component;
import java.sql.ResultSet;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import model.MySQL;

abstract class AuthenticationHandler {

    protected AuthenticationHandler next;
  
    public void setNext(AuthenticationHandler next) {
        this.next = next;
    }

    public abstract boolean handle(String username, String password);
}

class FieldsHandler extends AuthenticationHandler {

    @Override
    public boolean handle(String username, String password) {
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please Enter Your Username!", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        } else if (password.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please Enter Your Password!", "Warning", JOptionPane.WARNING_MESSAGE);
            return false;
        } else {
            if (next != null) {
                next.handle(username, password);
                return true;
            }
            return false;
        }
    }

}

class UserExistsHandler extends AuthenticationHandler {
    
    protected String role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public boolean handle(String username, String password) {

        try {
            ResultSet resultSet = MySQL.execute("SELECT * FROM `staff` "
                    + "INNER JOIN `staff_role` ON `staff_role`.`id` = `staff`.`staff_role_id` "
                    + "WHERE `username` = '" + username + "' AND `password` = '" + password + "'");

            if (resultSet.next()) {
                setRole(resultSet.getString("staff_role.role"));
                return (next != null) ? next.handle(username, password) : true;
            } else {
                JOptionPane.showMessageDialog(
                        null,
                        "Invalid username or password!",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE
                );
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    null,
                    "Error while checking credentials!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return false;
        }
    }
}

class RoleHandler extends AuthenticationHandler {
    
    private UserExistsHandler userHandler;
    
    public RoleHandler(UserExistsHandler userHandler) {
        this.userHandler = userHandler;
    }

    @Override
    public boolean handle(String username, String password) {
        
        String userRoll = userHandler.getRole();
        System.out.println(userRoll);
        if (!userRoll.isEmpty()) {
            switch (userRoll) {
                case "Doctor":
                    System.out.println("Loading Doctor Dashboard...");
                    break;
                case "Nurse":
                    System.out.println("Loading Nurse Dashboard...");
                    break;
                case "Admin":
                    System.out.println("Loading Admin Dashboard...");
                    break;
                default:
                    System.out.println("❌ Unknown Role!");
                    return false;
            }
            return true;

        } else {
            return false;
        }
    }

}

public class SignIn extends javax.swing.JFrame {

    public SignIn() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jPasswordField1 = new javax.swing.JPasswordField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/GLobeMed_Logo.png"))); // NOI18N

        jPanel1.setBackground(new java.awt.Color(239, 239, 236));

        jPanel2.setBackground(new java.awt.Color(239, 239, 236));

        jLabel2.setBackground(new java.awt.Color(33, 52, 72));
        jLabel2.setFont(new java.awt.Font("Bahnschrift", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(33, 52, 72));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("SIGN IN");

        jLabel3.setBackground(new java.awt.Color(33, 52, 72));
        jLabel3.setFont(new java.awt.Font("Bahnschrift", 0, 16)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(33, 52, 72));
        jLabel3.setText("Password");

        jTextField1.setBackground(new java.awt.Color(239, 239, 236));
        jTextField1.setFont(new java.awt.Font("Bahnschrift", 0, 16)); // NOI18N
        jTextField1.setForeground(new java.awt.Color(33, 52, 72));
        jTextField1.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(148, 180, 193), 2, true), javax.swing.BorderFactory.createEmptyBorder(5, 8, 5, 8)));

        jLabel4.setBackground(new java.awt.Color(33, 52, 72));
        jLabel4.setFont(new java.awt.Font("Bahnschrift", 0, 16)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(33, 52, 72));
        jLabel4.setText("Username");

        jPasswordField1.setBackground(new java.awt.Color(239, 239, 236));
        jPasswordField1.setFont(new java.awt.Font("Bahnschrift", 0, 16)); // NOI18N
        jPasswordField1.setForeground(new java.awt.Color(33, 52, 72));
        jPasswordField1.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(138, 180, 193), 2, true), javax.swing.BorderFactory.createEmptyBorder(5, 8, 5, 8)));

        jButton1.setBackground(new java.awt.Color(33, 52, 72));
        jButton1.setFont(new java.awt.Font("Bahnschrift", 1, 18)); // NOI18N
        jButton1.setForeground(new java.awt.Color(239, 239, 236));
        jButton1.setText("Sign In");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTextField1)
                    .addComponent(jPasswordField1)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addGap(46, 46, 46)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPasswordField1, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(57, 57, 57)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(68, Short.MAX_VALUE))
        );

        jButton2.setBackground(new java.awt.Color(239, 239, 236));
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/close.png"))); // NOI18N
        jButton2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(67, 67, 67)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(68, 68, 68))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton2)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton2)
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(0, 0, 0)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(0, 0, Short.MAX_VALUE))
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String username = jTextField1.getText();
        String password = String.valueOf(jPasswordField1.getPassword());

        AuthenticationHandler fieldsChecker = new FieldsHandler();
        AuthenticationHandler userCheker = new UserExistsHandler();
        AuthenticationHandler roleCheker = new RoleHandler((UserExistsHandler) userCheker);

        fieldsChecker.setNext(userCheker);
        userCheker.setNext(roleCheker);

        fieldsChecker.handle(username, password);
        jTextField1.setText("");
        jPasswordField1.setText("");
        
        jTextField1.grabFocus();
    }//GEN-LAST:event_jButton1ActionPerformed

    public static void main(String args[]) {
        FlatLightLaf.setup();
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SignIn().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
