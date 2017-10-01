/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package display.an.image;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

/**
 *
 * @author sanukumar
 */
public class viewImage extends javax.swing.JFrame {

    public viewImage() {
        initComponents();
        startupMyScreen();
        this.setLocation(this.getX()+30, this.getY()+30);
    }
    
    int xMouse;
    int yMouse;
//---------------------------------MyCode!--------------------------------------

    private BufferedImage image = null;
    private String fileName = "";
    private String imagePath = null;
    private int requiredPixels;     //characters are pixel/8
    private String inputString="";
    private String pass="";
    private String signature="sanu";
    
    private void startupMyScreen() {

        this.getContentPane().setBackground(new Color(55,71,79));
        this.setLocationRelativeTo(null);

    }

    private BufferedImage displayImage(String filePath) throws Exception{

        int height = 0;
        int width = 0;
        image = null;
        File f = null;

        //read image
        try {
            f = new File(filePath); //image file path
            height = ImageIO.read(f).getHeight();
            width = ImageIO.read(f).getWidth();
            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            image = ImageIO.read(f);
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        float factor = 0;
        if (height >= width) {
            float h = height;
            factor = 400 / (h);
        } else {
            float w = width;
            factor = 400 / (w);
        }

        height = (int) (height * ((factor * 100) / 100));
        width = (int) (width * ((factor * 100) / 100));

        Image img = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);

        BufferedImage newImage = new BufferedImage(width, height,
        BufferedImage.TYPE_INT_ARGB);
        Graphics g = newImage.getGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();

        return newImage;
    }

    private String getImagePath() throws Exception{

        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setDialogTitle("Select an image to encrypt!");
        jfc.setMultiSelectionEnabled(false);
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);

        jfc.setAcceptAllFileFilterUsed(false);  //Do not let file of any type get selected
        FileNameExtensionFilter filter = new FileNameExtensionFilter("JPEG, PNG", "jpeg", "png");   //Define the desireable file extensions
        jfc.addChoosableFileFilter(filter);

        int returnValue = jfc.showDialog(null, "Select image!");
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            imagePath = jfc.getSelectedFile().getAbsolutePath();
            fileName = jfc.getName(jfc.getSelectedFile());
            jTextField1.setText(imagePath);
            return imagePath;
        } else {
            return "Not found";
        }

    }

    private void encodeImage(String str) {

        encodeDataLength(str);
        encodeData();
        writeEncodedFile();

    }

    private void writeEncodedFile() {
        
        EncodeComplete obj = new EncodeComplete(this, true);
        obj.setLocationRelativeTo(this);
        obj.setVisible(true);
        
        if(obj.filePath!=null) {
            try {
                File outputfile;
                if(fileName.contains("png")){
                     outputfile = new File(obj.filePath + "/" + fileName);
                } else {
                    System.out.println(obj.filePath);
                     outputfile = new File(obj.filePath + "/" + fileName+".png");
                }
                try {
                    ImageIO.write(image, "png", outputfile);
                    jTextField2.setText(obj.filePath);
                } catch (IOException ex) {
                    Logger.getLogger(viewImage.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    private void encodeDataLength(String str) {
        int length = inputString.length();
        requiredPixels = length * 8;
        String requiredPixelsInBinary = Integer.toBinaryString(requiredPixels);

        while (requiredPixelsInBinary.length() < 32) {
            requiredPixelsInBinary = "0" + requiredPixelsInBinary;
        }

        for (int i = 0; i < 32; i++) {

            int r = image.getRGB(i, 0);
            String decodedImagePixel = Integer.toBinaryString(r);

            char[] dip = decodedImagePixel.toCharArray();
            dip[31] = requiredPixelsInBinary.charAt(i);
            String writeablePixesString = String.valueOf(dip);

            int rgbVal = Integer.parseUnsignedInt(writeablePixesString, 2);
            image.setRGB(i, 0, rgbVal);

        }
    }

    private void encodeData() {
        char[] userStringInCharArr = inputString.toCharArray();
        int x = 0;
        int y = 1;
        int k = 0;
        for (int i = 0; i < requiredPixels; i++) {
            String toConvert = Integer.toBinaryString((int)userStringInCharArr[k]);
            while(toConvert.length()<8){
                toConvert = "0"+toConvert;
            }
            
            for(int m=0; m<8; m++) {
                if (x < image.getWidth()) {

                    int r = image.getRGB(x, y);
                    String decodedImagePixel = Integer.toBinaryString(r);
                    char[] dip = decodedImagePixel.toCharArray();

                    dip[31] = toConvert.charAt(m);
                    String writeablePixesString = String.valueOf(dip);

                    int rgbVal = Integer.parseUnsignedInt(writeablePixesString, 2);
                    image.setRGB(x, y, rgbVal);
                    x++;

                } else {
                    x = 0;
                    y++;
                    int r = image.getRGB(x, y);
                    String decodedImagePixel = Integer.toBinaryString(r);
                    char[] dip = decodedImagePixel.toCharArray();

                    dip[31] = toConvert.charAt(m);
                    String writeablePixesString = String.valueOf(dip);

                    int rgbVal = Integer.parseUnsignedInt(writeablePixesString, 2);
                    image.setRGB(x, y, rgbVal);
                }
            }
            
            k++;
            i+=7;
        }
    }
    
//-------------------------------EndOfMyCode!-----------------------------------

@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jTextField3 = new javax.swing.JTextField();
        jTextField1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPasswordField1 = new javax.swing.JPasswordField();
        jLabel4 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Encryptor");
        setBackground(new java.awt.Color(102, 102, 255));
        setBounds(new java.awt.Rectangle(0, 23, 800, 430));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setMinimumSize(new java.awt.Dimension(800, 430));
        setUndecorated(true);
        setResizable(false);
        setSize(new java.awt.Dimension(800, 430));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setBackground(new java.awt.Color(0, 0, 0));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jLabel1.setPreferredSize(new java.awt.Dimension(400, 91));
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 14, -1, 400));

        jScrollPane1.setBackground(new java.awt.Color(128, 203, 196));
        jScrollPane1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 255, 255), 1, true));
        jScrollPane1.setForeground(new java.awt.Color(128, 203, 196));
        jScrollPane1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N

        jTextArea1.setBackground(new java.awt.Color(55, 71, 79));
        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Avenir", 0, 14)); // NOI18N
        jTextArea1.setForeground(new java.awt.Color(255, 255, 255));
        jTextArea1.setRows(5);
        jTextArea1.setText("\n\n\n\tWrite something to encrypt!");
        jTextArea1.setBorder(null);
        jTextArea1.setCaretColor(new java.awt.Color(255, 255, 255));
        jTextArea1.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        jTextArea1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextArea1MouseClicked(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jTextArea1MouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jTextArea1MouseEntered(evt);
            }
        });
        jScrollPane1.setViewportView(jTextArea1);

        getContentPane().add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 150, 361, 142));

        jButton1.setBackground(new java.awt.Color(255, 255, 255));
        jButton1.setFont(new java.awt.Font("Avenir", 0, 16)); // NOI18N
        jButton1.setForeground(new java.awt.Color(55, 71, 79));
        jButton1.setText("Select an Image");
        jButton1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.setOpaque(true);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 40, 165, 38));

        jButton2.setBackground(new java.awt.Color(255, 255, 255));
        jButton2.setFont(new java.awt.Font("Avenir", 0, 16)); // NOI18N
        jButton2.setForeground(new java.awt.Color(55, 71, 79));
        jButton2.setText("Encrypt");
        jButton2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton2.setOpaque(true);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 310, 165, 38));

        jTextField3.setEditable(false);
        jTextField3.setBackground(new java.awt.Color(55, 71, 79));
        jTextField3.setFont(new java.awt.Font("Avenir", 0, 14)); // NOI18N
        jTextField3.setForeground(new java.awt.Color(255, 255, 255));
        jTextField3.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField3.setText("Password(Optional)");
        jTextField3.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jTextField3.setCaretColor(new java.awt.Color(255, 255, 255));
        jTextField3.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        jTextField3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextField3MouseClicked(evt);
            }
        });
        jTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField3ActionPerformed(evt);
            }
        });
        getContentPane().add(jTextField3, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 120, 180, -1));

        jTextField1.setEditable(false);
        jTextField1.setBackground(new java.awt.Color(55, 71, 79));
        jTextField1.setFont(new java.awt.Font("Avenir", 0, 14)); // NOI18N
        jTextField1.setForeground(new java.awt.Color(255, 255, 255));
        jTextField1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField1.setText(".../image path/...");
        jTextField1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jTextField1.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });
        getContentPane().add(jTextField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 90, 367, -1));

        jLabel3.setFont(new java.awt.Font("Avenir", 0, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 40, 170, 20));

        jTextField2.setBackground(new java.awt.Color(55, 71, 79));
        jTextField2.setFont(new java.awt.Font("Avenir", 0, 14)); // NOI18N
        jTextField2.setForeground(new java.awt.Color(255, 255, 255));
        jTextField2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField2.setText(".../press Encrypt! to select a directory/...");
        jTextField2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });
        getContentPane().add(jTextField2, new org.netbeans.lib.awtextra.AbsoluteConstraints(423, 370, 360, -1));

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/display/an/image/ic_close_white_16dp_1x.png"))); // NOI18N
        jLabel7.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel7.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel7MouseClicked(evt);
            }
        });
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(770, 10, 20, 20));

        jLabel6.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("-");
        jLabel6.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel6.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel6MouseClicked(evt);
            }
        });
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 10, 20, 20));

        jPasswordField1.setBackground(new java.awt.Color(55, 71, 79));
        jPasswordField1.setFont(new java.awt.Font("Avenir", 0, 13)); // NOI18N
        jPasswordField1.setForeground(new java.awt.Color(255, 255, 255));
        jPasswordField1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jPasswordField1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255)));
        jPasswordField1.setMinimumSize(new java.awt.Dimension(10, 22));
        jPasswordField1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPasswordField1MouseClicked(evt);
            }
        });
        jPasswordField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPasswordField1ActionPerformed(evt);
            }
        });
        jPasswordField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jPasswordField1KeyPressed(evt);
            }
        });
        getContentPane().add(jPasswordField1, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 120, 180, 22));

        jLabel4.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jLabel4MouseDragged(evt);
            }
        });
        jLabel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jLabel4MousePressed(evt);
            }
        });
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 800, 430));

        jButton3.setText("jButton3");
        getContentPane().add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 400, 0, 0));

        setSize(new java.awt.Dimension(800, 428));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        jTextField2.setText(".../press Encrypt! to select a directory/...");
        try {
            jLabel3.setText("");
            jLabel1.setIcon(new ImageIcon(displayImage(getImagePath())));
        } catch (Exception e) {
            jLabel3.setText("Image Not Selected!");
        }

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        
        try { 
            jLabel3.setText("");
            pass = Arrays.toString(jPasswordField1.getPassword());
            inputString = pass+signature+jTextArea1.getText();
            if(!inputString.contains("Write something to encrypt")) {
                encodeImage(jTextArea1.getText());
            } else {
                jLabel3.setText("Change Text!");
            }
        } catch(Exception e){
            jLabel3.setText("Image Not Selected!");
        }
        jTextArea1.setText("\n" +"\n" +"\n" +"	Write something to encrypt");
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jLabel4MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel4MouseDragged
        int x = evt.getXOnScreen();
        int y = evt.getYOnScreen();

        this.setLocation(x-xMouse, y-yMouse);
    }//GEN-LAST:event_jLabel4MouseDragged

    private void jTextArea1MouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextArea1MouseExited
        if(jTextArea1.getText().length()==0){
            jTextArea1.setText("\n" +"\n" +"\n" +"	Write something to encrypt");
        }
    }//GEN-LAST:event_jTextArea1MouseExited

    private void jTextArea1MouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextArea1MouseEntered
        if(jTextArea1.getText().length()==0||jTextArea1.getText().contains("Write something to encrypt")){
            jTextArea1.setText("");
        }
    }//GEN-LAST:event_jTextArea1MouseEntered

    private void jLabel7MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel7MouseClicked
        this.dispose();
    }//GEN-LAST:event_jLabel7MouseClicked

    private void jLabel6MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel6MouseClicked
        this.setState(this.ICONIFIED);
    }//GEN-LAST:event_jLabel6MouseClicked

    private void jLabel4MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel4MousePressed
        xMouse = evt.getX();
        yMouse = evt.getY();
    }//GEN-LAST:event_jLabel4MousePressed

    private void jTextArea1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextArea1MouseClicked
        jTextArea1.setText("");
        jLabel3.setText("");
    }//GEN-LAST:event_jTextArea1MouseClicked

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2ActionPerformed

    private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField3ActionPerformed

    private void jTextField3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField3MouseClicked

    }//GEN-LAST:event_jTextField3MouseClicked

    private void jPasswordField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPasswordField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jPasswordField1ActionPerformed

    private void jPasswordField1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPasswordField1KeyPressed

    }//GEN-LAST:event_jPasswordField1KeyPressed

    private void jPasswordField1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPasswordField1MouseClicked
        if(jPasswordField1.getPassword()=="@@@@".toCharArray()){
            jPasswordField1.setText("");
        }
    }//GEN-LAST:event_jPasswordField1MouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                

}
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(viewImage.class
.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        

} catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(viewImage.class
.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        

} catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(viewImage.class
.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        

} catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(viewImage.class
.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new viewImage().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    // End of variables declaration//GEN-END:variables
}
