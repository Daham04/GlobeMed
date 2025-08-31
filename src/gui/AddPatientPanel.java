package gui;

import java.util.List;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import model.MySQL;
import model.Patient;

// Implementor
interface RecordStorage {

    boolean save(Patient patient, JFrame parent, HashMap<String, String> genderMap);
}

// Concrete Implementor 1 - MySQL
class MySQLPatientStorage implements RecordStorage {

    @Override
    public boolean save(Patient patient, JFrame parent, HashMap<String, String> genderMap) {
        try {
            MySQL.execute("INSERT INTO `patient` "
                    + "(`patient_nic`, `first_name`, `last_name`, `mobile`, `age`, "
                    + "`addres_line1`, `address_line2`,`gender_id`) "
                    + "VALUES ('" + patient.getNic() + "', '" + patient.getFirstName() + "', '"
                    + patient.getLastName() + "', '" + patient.getMobile() + "', " + patient.getAge()
                    + ", '" + patient.getAddressLine1() + "', '" + patient.getAddressLine2()
                    + "', '" + genderMap.get(patient.getGender()) + "')");

            JOptionPane.showMessageDialog(parent,
                    "Patient added successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            return true;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent,
                    "Error inserting patient: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}

// Abstraction
abstract class PatientRecordAccess {

    protected RecordStorage storage;

    public PatientRecordAccess(RecordStorage storage) {
        this.storage = storage;
    }

    public abstract boolean addPatient(Patient patient, JFrame parent, HashMap<String, String> genderMap);
}

// Refined Abstraction
class SecurePatientRecordAccess extends PatientRecordAccess {

    public SecurePatientRecordAccess(RecordStorage storage) {
        super(storage);
    }

    @Override
    public boolean addPatient(Patient patient, JFrame parent, HashMap<String, String> genderMap) {
        // Validation logic here
        if (patient.getNic() == null || patient.getNic().trim().isEmpty()) {
            JOptionPane.showMessageDialog(parent, "NIC is required!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (patient.getFirstName() == null || patient.getFirstName().trim().isEmpty()) {
            JOptionPane.showMessageDialog(parent, "First Name is required!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (patient.getLastName() == null || patient.getLastName().trim().isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Last Name is required!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (patient.getMobile() == null || !patient.getMobile().matches("\\d{10}")) {
            JOptionPane.showMessageDialog(parent, "Invalid Mobile Number!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (patient.getGender() == null || patient.getGender().equals("Select")) {
            JOptionPane.showMessageDialog(parent, "Please select a valid Gender.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Delegate to Implementor
        return storage.save(patient, parent, genderMap);
    }
}

public class AddPatientPanel extends javax.swing.JFrame {

    private String patientNIC;

    public HashMap<String, String> genderMap = new HashMap<>();

    public AddPatientPanel(String patientNIC) {
        this.patientNIC = patientNIC;
        initComponents();
        loadSize();
    }

    private void loadSize() {
        try {
            ResultSet resultSet = MySQL.execute("SELECT * FROM `gender`");
            Vector v = new Vector();
            v.add("Select");
            while (resultSet.next()) {
                genderMap.put(resultSet.getString("gender"), resultSet.getString("id"));
                v.add(resultSet.getString("gender"));

            }
            DefaultComboBoxModel model = new DefaultComboBoxModel(v);

            jComboBox1.setModel(model);
            jTextField2.setText(patientNIC);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        kGradientPanel2 = new com.k33ptoo.components.KGradientPanel();
        jButton2 = new javax.swing.JButton();
        jTextField2 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jTextField9 = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setAlwaysOnTop(true);
        setUndecorated(true);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(239, 239, 236));
        jPanel1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(188, 188, 188), 2, true));

        kGradientPanel2.setToolTipText("Product Management");
        kGradientPanel2.setkBorderRadius(0);
        kGradientPanel2.setkEndColor(new java.awt.Color(33, 52, 72));
        kGradientPanel2.setkStartColor(new java.awt.Color(138, 189, 192));
        kGradientPanel2.setName("[138,189,192] "); // NOI18N

        jButton2.setBackground(new java.awt.Color(33, 52, 72));
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/close.png"))); // NOI18N
        jButton2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout kGradientPanel2Layout = new javax.swing.GroupLayout(kGradientPanel2);
        kGradientPanel2.setLayout(kGradientPanel2Layout);
        kGradientPanel2Layout.setHorizontalGroup(
            kGradientPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kGradientPanel2Layout.createSequentialGroup()
                .addContainerGap(722, Short.MAX_VALUE)
                .addComponent(jButton2)
                .addContainerGap())
        );
        kGradientPanel2Layout.setVerticalGroup(
            kGradientPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(kGradientPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton2)
                .addGap(8, 8, 8))
        );

        jTextField2.setBackground(new java.awt.Color(239, 239, 236));
        jTextField2.setFont(new java.awt.Font("Bahnschrift", 0, 12)); // NOI18N
        jTextField2.setForeground(new java.awt.Color(33, 52, 72));
        jTextField2.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(138, 189, 192), 2, true), javax.swing.BorderFactory.createEmptyBorder(5, 8, 5, 8)));

        jLabel8.setBackground(new java.awt.Color(33, 52, 72));
        jLabel8.setFont(new java.awt.Font("Bahnschrift", 0, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(33, 52, 72));
        jLabel8.setText("NIC :");

        jLabel9.setBackground(new java.awt.Color(33, 52, 72));
        jLabel9.setFont(new java.awt.Font("Bahnschrift", 0, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(33, 52, 72));
        jLabel9.setText("First Name :");

        jTextField3.setBackground(new java.awt.Color(239, 239, 236));
        jTextField3.setFont(new java.awt.Font("Bahnschrift", 0, 12)); // NOI18N
        jTextField3.setForeground(new java.awt.Color(33, 52, 72));
        jTextField3.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(138, 189, 192), 2, true), javax.swing.BorderFactory.createEmptyBorder(5, 8, 5, 8)));

        jLabel10.setBackground(new java.awt.Color(33, 52, 72));
        jLabel10.setFont(new java.awt.Font("Bahnschrift", 0, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(33, 52, 72));
        jLabel10.setText("Last Name :");

        jTextField4.setBackground(new java.awt.Color(239, 239, 236));
        jTextField4.setFont(new java.awt.Font("Bahnschrift", 0, 12)); // NOI18N
        jTextField4.setForeground(new java.awt.Color(33, 52, 72));
        jTextField4.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(138, 189, 192), 2, true), javax.swing.BorderFactory.createEmptyBorder(5, 8, 5, 8)));

        jLabel11.setBackground(new java.awt.Color(33, 52, 72));
        jLabel11.setFont(new java.awt.Font("Bahnschrift", 0, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(33, 52, 72));
        jLabel11.setText("Mobile :");

        jTextField5.setBackground(new java.awt.Color(239, 239, 236));
        jTextField5.setFont(new java.awt.Font("Bahnschrift", 0, 12)); // NOI18N
        jTextField5.setForeground(new java.awt.Color(33, 52, 72));
        jTextField5.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(138, 189, 192), 2, true), javax.swing.BorderFactory.createEmptyBorder(5, 8, 5, 8)));

        jLabel12.setBackground(new java.awt.Color(33, 52, 72));
        jLabel12.setFont(new java.awt.Font("Bahnschrift", 0, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(33, 52, 72));
        jLabel12.setText("Gender :");

        jComboBox1.setBackground(new java.awt.Color(239, 239, 236));
        jComboBox1.setFont(new java.awt.Font("Bahnschrift", 0, 14)); // NOI18N
        jComboBox1.setForeground(new java.awt.Color(33, 52, 72));
        jComboBox1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(138, 189, 192), 2, true));
        jComboBox1.setMinimumSize(new java.awt.Dimension(64, 29));
        jComboBox1.setPreferredSize(new java.awt.Dimension(64, 29));

        jLabel13.setBackground(new java.awt.Color(33, 52, 72));
        jLabel13.setFont(new java.awt.Font("Bahnschrift", 0, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(33, 52, 72));
        jLabel13.setText("Age :");

        jTextField6.setBackground(new java.awt.Color(239, 239, 236));
        jTextField6.setFont(new java.awt.Font("Bahnschrift", 0, 12)); // NOI18N
        jTextField6.setForeground(new java.awt.Color(33, 52, 72));
        jTextField6.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(138, 189, 192), 2, true), javax.swing.BorderFactory.createEmptyBorder(5, 8, 5, 8)));

        jLabel14.setBackground(new java.awt.Color(33, 52, 72));
        jLabel14.setFont(new java.awt.Font("Bahnschrift", 0, 12)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(33, 52, 72));
        jLabel14.setText("Address Line1:");

        jTextField7.setBackground(new java.awt.Color(239, 239, 236));
        jTextField7.setFont(new java.awt.Font("Bahnschrift", 0, 12)); // NOI18N
        jTextField7.setForeground(new java.awt.Color(33, 52, 72));
        jTextField7.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(138, 189, 192), 2, true), javax.swing.BorderFactory.createEmptyBorder(5, 8, 5, 8)));

        jTextField9.setBackground(new java.awt.Color(239, 239, 236));
        jTextField9.setFont(new java.awt.Font("Bahnschrift", 0, 12)); // NOI18N
        jTextField9.setForeground(new java.awt.Color(33, 52, 72));
        jTextField9.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.LineBorder(new java.awt.Color(138, 189, 192), 2, true), javax.swing.BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        jTextField9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField9ActionPerformed(evt);
            }
        });

        jLabel16.setBackground(new java.awt.Color(33, 52, 72));
        jLabel16.setFont(new java.awt.Font("Bahnschrift", 0, 12)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(33, 52, 72));
        jLabel16.setText("Address Line2:");

        jButton1.setBackground(new java.awt.Color(33, 52, 72));
        jButton1.setFont(new java.awt.Font("Bahnschrift", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(239, 239, 236));
        jButton1.setText("Add Patient");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(kGradientPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addComponent(jLabel12)
                            .addComponent(jLabel8)
                            .addComponent(jLabel14)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel16)))
                .addGap(28, 28, 28)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField9)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jTextField7, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(233, 233, 233))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(kGradientPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 758, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 363, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    public void resetForm() {
        jTextField2.setText(""); // NIC
        jTextField3.setText(""); // First Name
        jTextField4.setText(""); // Last Name
        jTextField5.setText(""); // Mobile
        jTextField6.setText(""); // Age
        jTextField7.setText(""); // Address Line 1
        jTextField9.setText(""); // Address Line 2

        jComboBox1.setSelectedIndex(0); // reset gender dropdown
    }
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String nic = jTextField2.getText().trim();
        String firstName = jTextField3.getText().trim();
        String lastName = jTextField4.getText().trim();
        String gender = (jComboBox1.getSelectedItem() != null) ? jComboBox1.getSelectedItem().toString() : "";
        String mobile = jTextField5.getText().trim();
        int age = 0;
        try {
            age = Integer.parseInt(jTextField6.getText().trim());
        } catch (Exception ignored) {
        }
        String address1 = jTextField7.getText().trim();
        String address2 = jTextField9.getText().trim();

        Patient patient = new Patient(nic, firstName, lastName, gender, mobile, age, address1, address2);

        // Use Bridge Pattern
        RecordStorage storage = new MySQLPatientStorage();
        PatientRecordAccess recordAccess = new SecurePatientRecordAccess(storage);

        if (recordAccess.addPatient(patient, this, genderMap)) {
            this.dispose();
            JFrame frame = new AddNewAppointment(nic);
            frame.setVisible(true);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jTextField9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField9ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField9ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField9;
    private com.k33ptoo.components.KGradientPanel kGradientPanel2;
    // End of variables declaration//GEN-END:variables
}
