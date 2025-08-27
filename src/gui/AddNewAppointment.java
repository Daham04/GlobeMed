package gui;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import model.MySQL;

public class AddNewAppointment extends javax.swing.JFrame {

    private String patientNIC;

    public HashMap<String, String> hospitalMap = new HashMap<>();
    public HashMap<String, String> DoctorTypeMap = new HashMap<>();
    public HashMap<String, String> DoctorMap = new HashMap<>();
    public HashMap<String, String> TimeSlotMap = new HashMap<>();

    public AddNewAppointment(String patientNIC) {
        this.patientNIC = patientNIC;
        initComponents();
        loadHospital();
        loadDoctorTypes();
    }

    private void loadHospital() {
        try {
            ResultSet resultSet = MySQL.execute("SELECT * FROM `hospital`");
            Vector v = new Vector();
            v.add("Select");
            while (resultSet.next()) {
                hospitalMap.put(resultSet.getString("hospital"), resultSet.getString("id"));
                v.add(resultSet.getString("hospital"));

            }
            DefaultComboBoxModel model = new DefaultComboBoxModel(v);

            jComboBox1.setModel(model);
            jTextField2.setText(patientNIC);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void loadDoctorTypes() {
        try {
            ResultSet resultSet = MySQL.execute("SELECT * FROM `type`");
            Vector v = new Vector();
            v.add("Select");
            while (resultSet.next()) {
                DoctorTypeMap.put(resultSet.getString("type"), resultSet.getString("id"));
                v.add(resultSet.getString("type"));

            }
            DefaultComboBoxModel model = new DefaultComboBoxModel(v);

            jComboBox4.setModel(model);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void loadDoctors() {
        String hospital = jComboBox1.getSelectedItem().toString();
        String type = jComboBox4.getSelectedItem().toString();
        Date date = jDateChooser1.getDate();

        System.out.println(hospital);
        System.out.println(type);
        System.out.println(date);

        if (hospital.equals("Select") || type.equals("Select") || date == null) {
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        String dayOfWeek = sdf.format(date);

        try {
            String query = "SELECT s.id, s.first_name, s.last_name "
                    + "FROM staff s "
                    + "INNER JOIN staff_has_hospital shh ON s.id = shh.staff_id "
                    + "INNER JOIN hospital h ON h.id = shh.hospital_id "
                    + "INNER JOIN type t ON t.id = shh.type_id "
                    + "INNER JOIN days d ON d.id = shh.days_id "
                    + "WHERE h.hospital = '" + hospital + "' "
                    + "AND t.type = '" + type + "' "
                    + "AND d.day = '" + dayOfWeek + "'";

            ResultSet rs = MySQL.execute(query);
            //System.out.println(rs.getString("s.first_name") + " " + rs.getString("s.last_name"));

            Vector v = new Vector();
            v.add("Select");

            while (rs.next()) {

                String doctorName = rs.getString("s.first_name") + " " + rs.getString("s.last_name");
                DoctorMap.put(doctorName, rs.getString("s.id"));
                v.add(doctorName);
            }

            DefaultComboBoxModel model = new DefaultComboBoxModel(v);
            jComboBox2.setModel(model);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadTimeSlot() {
        String hospital = jComboBox1.getSelectedItem().toString();
        String type = jComboBox4.getSelectedItem().toString();
        String doctor = jComboBox2.getSelectedItem().toString();
        Date date = jDateChooser1.getDate();

        if (hospital.equals("Select") || type.equals("Select") || date == null || doctor.equals("Select")) {
            return;
        }

        // Convert date → DayOfWeek (e.g., Monday, Tuesday)
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        String dayOfWeek = sdf.format(date);

        try {
            // get doctorId from map
            String doctorId = DoctorMap.get(doctor);
            if (doctorId == null) {
                System.out.println("Doctor not found in map!");
                return;
            }

            int availability = 1; // or dynamic if needed

            String query = "SELECT ts.id, ts.time "
                    + "FROM staff s "
                    + "INNER JOIN staff_has_hospital shh ON s.id = shh.staff_id "
                    + "INNER JOIN hospital h ON h.id = shh.hospital_id "
                    + "INNER JOIN type t ON t.id = shh.type_id "
                    + "INNER JOIN days d ON d.id = shh.days_id "
                    + "INNER JOIN time_slot ts ON ts.id = shh.time_slot_id "
                    + "WHERE h.hospital = '" + hospital + "' "
                    + "AND t.type = '" + type + "' "
                    + "AND d.day = '" + dayOfWeek + "' "
                    + "AND shh.staff_id = '" + doctorId + "' "
                    + "AND shh.availability_id = '" + availability + "'";

            ResultSet rs = MySQL.execute(query);

            Vector<String> v = new Vector<>();
            v.add("Select");

            while (rs.next()) {
                String timeSlots = rs.getString("ts.time");
                TimeSlotMap.put(timeSlots, rs.getString("ts.id"));
                v.add(timeSlots);
            }

            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(v);
            jComboBox3.setModel(model);

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
        jLabel12 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel10 = new javax.swing.JLabel();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jLabel13 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jLabel15 = new javax.swing.JLabel();
        jComboBox3 = new javax.swing.JComboBox<>();
        jLabel16 = new javax.swing.JLabel();
        jComboBox4 = new javax.swing.JComboBox<>();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setAlwaysOnTop(true);
        setUndecorated(true);

        jPanel1.setBackground(new java.awt.Color(239, 239, 236));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(188, 188, 188)));

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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });

        jLabel8.setBackground(new java.awt.Color(33, 52, 72));
        jLabel8.setFont(new java.awt.Font("Bahnschrift", 0, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(33, 52, 72));
        jLabel8.setText("NIC :");

        jLabel12.setBackground(new java.awt.Color(33, 52, 72));
        jLabel12.setFont(new java.awt.Font("Bahnschrift", 0, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(33, 52, 72));
        jLabel12.setText("Hospital :");

        jComboBox1.setBackground(new java.awt.Color(239, 239, 236));
        jComboBox1.setFont(new java.awt.Font("Bahnschrift", 0, 12)); // NOI18N
        jComboBox1.setForeground(new java.awt.Color(33, 52, 72));
        jComboBox1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(138, 189, 192), 2, true));
        jComboBox1.setMinimumSize(new java.awt.Dimension(64, 29));
        jComboBox1.setPreferredSize(new java.awt.Dimension(64, 29));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jLabel10.setBackground(new java.awt.Color(33, 52, 72));
        jLabel10.setFont(new java.awt.Font("Bahnschrift", 0, 16)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(33, 52, 72));
        jLabel10.setText("Appoinment Date :");

        jDateChooser1.setBackground(new java.awt.Color(239, 239, 236));
        jDateChooser1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(138, 189, 192)));
        jDateChooser1.setForeground(new java.awt.Color(239, 239, 236));

        jLabel13.setBackground(new java.awt.Color(33, 52, 72));
        jLabel13.setFont(new java.awt.Font("Bahnschrift", 0, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(33, 52, 72));
        jLabel13.setText("Notes :");

        jScrollPane1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(138, 189, 192), 2, true));

        jTextArea1.setBackground(new java.awt.Color(239, 239, 236));
        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jButton1.setBackground(new java.awt.Color(33, 52, 72));
        jButton1.setFont(new java.awt.Font("Bahnschrift", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(239, 239, 236));
        jButton1.setText("Add Apointment");

        jLabel14.setBackground(new java.awt.Color(33, 52, 72));
        jLabel14.setFont(new java.awt.Font("Bahnschrift", 0, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(33, 52, 72));
        jLabel14.setText("Doctor :");

        jComboBox2.setBackground(new java.awt.Color(239, 239, 236));
        jComboBox2.setFont(new java.awt.Font("Bahnschrift", 0, 12)); // NOI18N
        jComboBox2.setForeground(new java.awt.Color(33, 52, 72));
        jComboBox2.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(138, 189, 192), 2, true));
        jComboBox2.setMinimumSize(new java.awt.Dimension(64, 29));
        jComboBox2.setPreferredSize(new java.awt.Dimension(64, 29));
        jComboBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox2ActionPerformed(evt);
            }
        });

        jLabel15.setBackground(new java.awt.Color(33, 52, 72));
        jLabel15.setFont(new java.awt.Font("Bahnschrift", 0, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(33, 52, 72));
        jLabel15.setText("Time Slot :");

        jComboBox3.setBackground(new java.awt.Color(239, 239, 236));
        jComboBox3.setFont(new java.awt.Font("Bahnschrift", 0, 12)); // NOI18N
        jComboBox3.setForeground(new java.awt.Color(33, 52, 72));
        jComboBox3.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(138, 189, 192), 2, true));
        jComboBox3.setMinimumSize(new java.awt.Dimension(64, 29));
        jComboBox3.setPreferredSize(new java.awt.Dimension(64, 29));
        jComboBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox3ActionPerformed(evt);
            }
        });

        jLabel16.setBackground(new java.awt.Color(33, 52, 72));
        jLabel16.setFont(new java.awt.Font("Bahnschrift", 0, 14)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(33, 52, 72));
        jLabel16.setText("Type :");

        jComboBox4.setBackground(new java.awt.Color(239, 239, 236));
        jComboBox4.setFont(new java.awt.Font("Bahnschrift", 0, 12)); // NOI18N
        jComboBox4.setForeground(new java.awt.Color(33, 52, 72));
        jComboBox4.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(138, 189, 192), 2, true));
        jComboBox4.setMinimumSize(new java.awt.Dimension(64, 29));
        jComboBox4.setPreferredSize(new java.awt.Dimension(64, 29));
        jComboBox4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox4ActionPerformed(evt);
            }
        });

        jButton4.setBackground(new java.awt.Color(239, 239, 236));
        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/search.png"))); // NOI18N
        jButton4.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 2, true));
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setBackground(new java.awt.Color(239, 239, 236));
        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/search.png"))); // NOI18N
        jButton5.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 2, true));
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(kGradientPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(jLabel12)
                            .addComponent(jLabel13))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(224, 224, 224)
                                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(31, 31, 31)
                                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(28, 28, 28)
                                        .addComponent(jLabel10)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(31, 31, 31)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jComboBox1, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14)
                            .addComponent(jLabel15)
                            .addComponent(jLabel16))
                        .addGap(23, 23, 23)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboBox3, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jComboBox4, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jComboBox2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jButton5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(kGradientPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel8)
                        .addComponent(jLabel10))
                    .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel16)
                        .addComponent(jComboBox4, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel14)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(13, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox2ActionPerformed

    private void jComboBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox3ActionPerformed

    private void jComboBox4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox4ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        loadDoctors();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        loadTimeSlot();
    }//GEN-LAST:event_jButton5ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JComboBox<String> jComboBox4;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField2;
    private com.k33ptoo.components.KGradientPanel kGradientPanel2;
    // End of variables declaration//GEN-END:variables
}
