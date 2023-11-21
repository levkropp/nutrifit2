import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MainUI extends JFrame {

    private UserProfile userProfile = new UserProfile();

    private JComboBox<String> genderCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
    private JFormattedTextField birthDateField = new JFormattedTextField(new SimpleDateFormat("yyyy-MM-dd"));
    private JTextField heightField = new JTextField(10);
    private JTextField weightField = new JTextField(10);
    private JComboBox<String> unitCombo = new JComboBox<>(new String[]{"Metric", "Imperial"});
    private JTextField nameField = new JTextField(10);


    public MainUI() {
        setTitle("Main UI");
        setSize(800, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel userProfilePanel = createUserProfilePanel();
        loadUserProfile();
        displayUserProfile();
        add(userProfilePanel, BorderLayout.NORTH);

        JButton dietLogButton = new JButton("Diet Log");
        dietLogButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setSize(800, 600);
                enterDietLogPanel();
            }
        });

        // 将按钮添加到用户资料面板中
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(dietLogButton);
        add(buttonPanel, BorderLayout.SOUTH);

    }
    private void enterDietLogPanel() {
        // 移除当前的组件
        getContentPane().removeAll();
        getContentPane().revalidate();
        getContentPane().repaint();

        // 添加 DietLogPanel
        DietLogPanel dietLogPanel = new DietLogPanel(userProfile);
        add(dietLogPanel);
        revalidate();
        repaint();
    }


    private JPanel createUserProfilePanel() {
        JPanel panel = new JPanel(new GridLayout(8, 4));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Gender:"));
        panel.add(genderCombo);
        panel.add(new JLabel("Birth Date (YYYY-MM-DD):"));
        birthDateField.setFocusLostBehavior(JFormattedTextField.PERSIST);
        panel.add(birthDateField);
        panel.add(new JLabel("Height:"));
        panel.add(heightField);
        panel.add(new JLabel("Weight:"));
        panel.add(weightField);
        panel.add(new JLabel("Unit:"));
        panel.add(unitCombo);



        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveUserProfile();
            }
        });
        panel.add(saveButton);

        JButton loadButton = new JButton("Load");
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadUserProfile();
                displayUserProfile();
            }
        });
        panel.add(loadButton);
        return panel;
    }

    private void saveUserProfile() {
        if (!validateData()) {
            return;
        }

        userProfile.setGender((String) genderCombo.getSelectedItem());
        userProfile.setBirthDate(birthDateField.getText());
        userProfile.setHeight(Double.parseDouble(heightField.getText()));
        userProfile.setWeight(Double.parseDouble(weightField.getText()));
        userProfile.setUnit((String) unitCombo.getSelectedItem());
        userProfile.setName(nameField.getText());

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("userProfile.dat"))) {
            oos.writeObject(userProfile);
            JOptionPane.showMessageDialog(this, "Profile Saved!");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private boolean validateData() {
        try {
            double height = Double.parseDouble(heightField.getText());
            double weight = Double.parseDouble(weightField.getText());
            if (height <= 0 || weight <= 0) {
                JOptionPane.showMessageDialog(this, "Height and weight must be positive numbers.");
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid height or weight.");
            return false;
        }

        // 验证日期格式
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            format.setLenient(false);
            Date birthDate = format.parse(birthDateField.getText());
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid birth date. Use format YYYY-MM-DD.");
            return false;
        }

        return true;
    }

    private void loadUserProfile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("userProfile.dat"))) {
            userProfile = (UserProfile) ois.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    private void displayUserProfile() {
        genderCombo.setSelectedItem(userProfile.getGender());
        birthDateField.setText(userProfile.getBirthDate());
        heightField.setText(String.valueOf(userProfile.getHeight()));
        weightField.setText(String.valueOf(userProfile.getWeight()));
        unitCombo.setSelectedItem(userProfile.getUnit());
        nameField.setText(userProfile.getName());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MainUI().setVisible(true);
            }
        });
    }

}

