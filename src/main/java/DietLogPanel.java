
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class DietLogPanel extends JPanel implements interfaceContact {

    private UserProfile userProfile;

    Map<String, Double> nutritionalValues;
    Map<String, Map<String, Double>> dateNutritionalValues;
    private JTextField foodSearchField;
    private JComboBox<String> mealTypeCombo;
    private JTextField foodQuantityField;
    private DefaultComboBoxModel<String> foodModel;
    private JDatePickerImpl datePicker;
    private DefaultTableModel mealLogTableModel;
    private JTable mealLogTable;
    private JButton logMealButton;
    private JComboBox<String> foodCombo;

    private JLabel nutritionInfoLabel;

    private DefaultTableModel exerciseLogTableModel;


    private boolean isUserInput = false; // 新增标志变量

    public DietLogPanel(UserProfile userProfile) {

        this.userProfile = userProfile;
        setLayout(new BorderLayout());
        nutritionalValues = new HashMap<>();
        dateNutritionalValues = new HashMap<>();

        // Top Panel for search field and date picker
        JPanel topPanel = new JPanel(new FlowLayout());
        foodSearchField = new JTextField(20);
        topPanel.add(foodSearchField);

        datePicker = createDatePicker();
        topPanel.add(datePicker);
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new FlowLayout());
        foodModel = new DefaultComboBoxModel<>();
        foodCombo = new JComboBox<>(foodModel);
        foodCombo.setEditable(true);
        foodCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals("comboBoxEdited") || e.getActionCommand().equals("comboBoxChanged")) {
                    Object selectedItem = foodCombo.getSelectedItem();
                    if (selectedItem != null) {
                        String selectedFood = selectedItem.toString();
                        if (!selectedFood.equals(foodSearchField.getText())) {
                            foodSearchField.setText(selectedFood);
                        }
                    }
                }
            }
        });
        centerPanel.add(foodCombo);

        mealTypeCombo = new JComboBox<>(new String[]{"Breakfast", "Lunch", "Dinner", "Snack"});
        centerPanel.add(mealTypeCombo);

        foodQuantityField = new JTextField(5);
        centerPanel.add(foodQuantityField);
        add(centerPanel, BorderLayout.CENTER);

        // Bottom Panel for log meal button and meal log table
        JPanel bottomPanel = new JPanel(new BorderLayout());
        logMealButton = new JButton("Log Meal");
        logMealButton.addActionListener(e -> logMeal());
        bottomPanel.add(logMealButton, BorderLayout.NORTH);

        nutritionInfoLabel = new JLabel("Nutrition Information");
        JPanel infoPanel = new JPanel();
        infoPanel.add(nutritionInfoLabel);
        bottomPanel.add(infoPanel, BorderLayout.SOUTH); // 将信息面板添加到底部面板

        mealLogTableModel = new DefaultTableModel(new String[]{"Date", "Meal Type", "Food", "Quantity", "Calories"}, 0);
        mealLogTable = new JTable(mealLogTableModel);
        bottomPanel.add(new JScrollPane(mealLogTable), BorderLayout.CENTER);

        foodSearchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                isUserInput = true;
            }
        });

        foodCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals("comboBoxEdited") || e.getActionCommand().equals("comboBoxChanged")) {
                    isUserInput = false;
                    Object selectedItem = foodCombo.getSelectedItem();
                    if (selectedItem != null) {
                        String selectedFood = selectedItem.toString();
                        foodSearchField.setText(selectedFood);
                    }
                }
            }
        });
        foodSearchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                updateFoodSuggestions();
            }

            public void removeUpdate(DocumentEvent e) {
                updateFoodSuggestions();
            }

            public void changedUpdate(DocumentEvent e) {
                updateFoodSuggestions();
            }

            private void updateFoodSuggestions() {
                String searchText = foodSearchField.getText();
                if (!searchText.isEmpty() && !searchText.equals(foodCombo.getSelectedItem())) {
                    SwingUtilities.invokeLater(() -> updateFoodModel(searchText));
                }
            }
        });

        JButton nextWindowButton = new JButton("Go to Next Window");
        bottomPanel.add(nextWindowButton, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);

        nextWindowButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openNextWindow();
            }
        });

    }
    // Method to handle the opening of the next window and passing data
    private void openNextWindow() {
        ExercisePanel nextWindow = new ExercisePanel(mealLogTableModel, userProfile, nutritionalValues, dateNutritionalValues);

        // Display the new window
        nextWindow.setVisible(true);
    }


    private JDatePickerImpl createDatePicker() {
        UtilDateModel model = new UtilDateModel();
        Properties p = new Properties();
        p.put("text.today", "Today");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        return new JDatePickerImpl(datePanel, new DateLabelFormatter());
    }

    private void logMeal() {
        if (!parseAndValidateInput()) {
            return;
        }
        calculateAndDisplayNutrition();
        recordMeal();
    }

    private boolean parseAndValidateInput() {
        String selectedFood = (String) foodCombo.getSelectedItem();
        String mealType = (String) mealTypeCombo.getSelectedItem();
        String quantityStr = foodQuantityField.getText();
        double quantity;
        try {
            quantity = Double.parseDouble(quantityStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid quantity format");
            return false;
        }
        nutritionalValues = getNutritionalValue(selectedFood);

        if (nutritionalValues.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No nutritional data found for the selected food.");
            return false;
        }
        return true;
    }

    private void calculateAndDisplayNutrition() {
        String selectedFood = (String) foodCombo.getSelectedItem();
        double calories = nutritionalValues.getOrDefault("KCAL", 0.0);
        double protein = nutritionalValues.getOrDefault("PROT", 0.0);
        double carbs = nutritionalValues.getOrDefault("CARB", 0.0);
        double vitaminC = nutritionalValues.getOrDefault("VITC", 0.0);

        nutritionInfoLabel.setText(buildNutritionInfoHtml(selectedFood, calories, protein, carbs, vitaminC));
    }

    private String buildNutritionInfoHtml(String food, double calories, double protein, double carbs, double vitaminC) {
        return "<html>Nutrition for " + food + ":<br>" +
                "Calories: " + calories + " kcal<br>" +
                "Protein: " + protein + " g<br>" +
                "Carbs: " + carbs + " g<br>" +
                "Vitamin C: " + vitaminC + " mg</html>";
    }

    private void recordMeal() {
        String selectedFood = (String) foodCombo.getSelectedItem();
        String mealType = (String) mealTypeCombo.getSelectedItem();
        double quantity = Double.parseDouble(foodQuantityField.getText());
        double calories = nutritionalValues.getOrDefault("KCAL", 0.0) * quantity / 100;

        Date selectedDate = (Date) datePicker.getModel().getValue();
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(selectedDate);

        updateDateNutritionalValues(formattedDate);
        mealLogTableModel.addRow(new Object[]{formattedDate, mealType, selectedFood, quantity, calories});
    }

    private void updateDateNutritionalValues(String formattedDate) {
        if (dateNutritionalValues.containsKey(formattedDate)) {
            Map<String, Double> existingValues = dateNutritionalValues.get(formattedDate);
            updateExistingNutritionalValues(existingValues);
            dateNutritionalValues.put(formattedDate, existingValues);
        } else {
            dateNutritionalValues.put(formattedDate, new HashMap<>(nutritionalValues));
        }
    }

    private void updateExistingNutritionalValues(Map<String, Double> existingValues) {
        for (String key : nutritionalValues.keySet()) {
            double existingValue = existingValues.getOrDefault(key, 0.0);
            double newValue = nutritionalValues.getOrDefault(key, 0.0);
            existingValues.put(key, existingValue + newValue);
        }
    }
    public Map<String, Double> getNutritionalValue(String food) {
        Map<String, Double> nutritionalValues = new HashMap<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DatabaseConnection.getConnection();
            String query = "SELECT N.NutrientSymbol, NA.NutrientValue FROM NUTRIENT_AMOUNT NA "
                    + "JOIN NUTRIENT_NAME N ON NA.NutrientID = N.NutrientID "
                    + "WHERE NA.FoodID = (SELECT FoodID FROM FOOD_NAME WHERE FoodDescription = ?)";

            stmt = conn.prepareStatement(query);
            stmt.setString(1, food);
            rs = stmt.executeQuery();

            while (rs.next()) {
                String nutrientSymbol = rs.getString("NutrientSymbol");
                double nutrientValue = rs.getDouble("NutrientValue");
                nutritionalValues.put(nutrientSymbol, nutrientValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return nutritionalValues;
    }


    public void updateFoodModel(String searchText) {
        new Thread(() -> {
            List<String> foodDescriptions = new ArrayList<>();
            try (Connection conn = DatabaseConnection.getConnection()) {
                String query = "SELECT FoodDescription FROM FOOD_NAME WHERE FoodDescription LIKE ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, "%" + searchText + "%");
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            foodDescriptions.add(rs.getString("FoodDescription"));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            SwingUtilities.invokeLater(() -> {
                foodModel.removeAllElements();
                for (String desc : foodDescriptions) {
                    foodModel.addElement(desc);
                }

                if (!foodDescriptions.isEmpty()) {
                    foodCombo.showPopup();
                } else {
                    foodCombo.hidePopup();
                }

                if (!foodDescriptions.contains(foodSearchField.getText())) {
                    foodCombo.setSelectedItem(null);
                }
            });
        }).start();
    }



}