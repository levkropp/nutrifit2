import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;


public class ExercisePanel extends JFrame {

    private TableModel dietModel;
    private UserProfile userProfile;
    private JTable exerciseLogTable;
    Map<String, Map<String, Double>> nutritions;

    Map<String, Double> nutritionalValues;

    private DefaultTableModel exerciseLogTableModel;
    public ExercisePanel(TableModel tableModel, UserProfile userProfile, Map<String, Double> nutritionalValues, Map<String, Map<String, Double>> nutritions) {
        super("Exercise Log");
        this.nutritions = nutritions;
        this.dietModel = tableModel;
        this.userProfile = userProfile;
        this.nutritionalValues = nutritionalValues;
        setSize(1000, 600);
        setLayout(new BorderLayout());

        // Initialize the Exercise Log Table Model
        exerciseLogTableModel = new DefaultTableModel(new String[]{"Date", "Exercise Type", "Intensity", "Duration", "Calories Burned"}, 0);

        // Create the Exercise Log Table
        exerciseLogTable = new JTable(exerciseLogTableModel);

        // Set up the ExerciseLogPanel
        JPanel exerciseLogPanel = createExerciseLogPanel();

        // Add the ExerciseLogPanel to the top of the ExercisePanel
        add(exerciseLogPanel, BorderLayout.NORTH);

        // Add the Exercise Log Table within a JScrollPane to the center of the ExercisePanel
        JScrollPane scrollPane = new JScrollPane(exerciseLogTable);
        add(scrollPane, BorderLayout.CENTER);

        // Initialize the combined panel for date selectors and buttons
        JPanel dateSelectionCombinedPanel = new JPanel();
        dateSelectionCombinedPanel.setLayout(new BoxLayout(dateSelectionCombinedPanel, BoxLayout.Y_AXIS));

        // Calorie chart selection and button
        JPanel calorieDateSelectionPanel = new JPanel(new FlowLayout());
        JDatePickerImpl calorieStartDatePicker = createDatePicker();
        JDatePickerImpl calorieEndDatePicker = createDatePicker();

        calorieDateSelectionPanel.add(new JLabel("Calorie Start Date:"));
        calorieDateSelectionPanel.add(calorieStartDatePicker);
        calorieDateSelectionPanel.add(new JLabel("Calorie End Date:"));
        calorieDateSelectionPanel.add(calorieEndDatePicker);

        JButton showCalorieChartButton = new JButton("Show Calorie Chart");
        showCalorieChartButton.addActionListener(e -> {
            Date startDate = (Date) calorieStartDatePicker.getModel().getValue();
            Date endDate = (Date) calorieEndDatePicker.getModel().getValue();
            showCalorieChart(startDate, endDate);
        });

        calorieDateSelectionPanel.add(showCalorieChartButton);

        // Nutrient chart selection and button
        JPanel nutrientDateSelectionPanel = new JPanel(new FlowLayout());
        JDatePickerImpl nutrientStartDatePicker = createDatePicker();
        JDatePickerImpl nutrientEndDatePicker = createDatePicker();

        nutrientDateSelectionPanel.add(new JLabel("Nutrient Start Date:"));
        nutrientDateSelectionPanel.add(nutrientStartDatePicker);
        nutrientDateSelectionPanel.add(new JLabel("Nutrient End Date:"));
        nutrientDateSelectionPanel.add(nutrientEndDatePicker);

        JButton showNutrientChartButton = new JButton("Show Nutrient Chart");
        showNutrientChartButton.addActionListener(e -> {
            Date startDate = (Date) nutrientStartDatePicker.getModel().getValue();
            Date endDate = (Date) nutrientEndDatePicker.getModel().getValue();
            showNutrientChart(startDate, endDate);
        });

        nutrientDateSelectionPanel.add(showNutrientChartButton);

        dateSelectionCombinedPanel.add(calorieDateSelectionPanel);
        dateSelectionCombinedPanel.add(nutrientDateSelectionPanel);

        // Weight loss calculation panel
        JPanel weightLossPanel = new JPanel(new FlowLayout());
        JDatePickerImpl targetDateSelector = createDatePicker();
        weightLossPanel.add(new JLabel("Select Target Date:"));
        weightLossPanel.add(targetDateSelector);

        JButton calculateWeightLossButton = new JButton("Calculate Weight Loss");
        calculateWeightLossButton.addActionListener(e -> {
            Date targetDate = (Date) targetDateSelector.getModel().getValue();
            calculateAndShowWeightLoss(targetDate);
        });

        weightLossPanel.add(calculateWeightLossButton);
        dateSelectionCombinedPanel.add(weightLossPanel);

        // Add the combined panel to the ExercisePanel
        add(dateSelectionCombinedPanel, BorderLayout.SOUTH);
    }
    private void printTableModelData(TableModel model) {
        for (int row = 0; row < model.getRowCount(); row++) {
            for (int col = 0; col < model.getColumnCount(); col++) {
                // Print each cell value
                System.out.print(model.getValueAt(row, col) + "\t");
            }
            System.out.println(); // New line at the end of each row
        }
    }

    private JDatePickerImpl createDatePicker() {
        UtilDateModel model = new UtilDateModel();
        Properties p = new Properties();
        p.put("text.today", "Today");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        return new JDatePickerImpl(datePanel, new DateLabelFormatter());
    }

    private JPanel createExerciseLogPanel() {
        JPanel exercisePanel = new JPanel();
        exercisePanel.setLayout(new GridLayout(0, 2, 5, 5)); // 使用网格布局

        // 添加日期选择器
        JDatePickerImpl exerciseDatePicker = createDatePicker();
        exercisePanel.add(new JLabel("Exercise Date:"));
        exercisePanel.add(exerciseDatePicker);

        // 运动类型选择
        JComboBox<String> exerciseTypeCombo = new JComboBox<>(new String[]{"Walking", "Running", "Cycling", "Swimming"});
        exercisePanel.add(new JLabel("Exercise Type:"));
        exercisePanel.add(exerciseTypeCombo);

        // 持续时间输入
        JTextField durationField = new JTextField();
        exercisePanel.add(new JLabel("Duration (minutes):"));
        exercisePanel.add(durationField);

        // 强度选择
        JComboBox<String> intensityCombo = new JComboBox<>(new String[]{"Low", "Medium", "High", "Very High"});
        exercisePanel.add(new JLabel("Intensity:"));
        exercisePanel.add(intensityCombo);

        // 计算按钮
        JButton calculateButton = new JButton("Calculate Calories");
        calculateButton.setMaximumSize(new Dimension(200, 30)); // 限制最大大小
        calculateButton.setPreferredSize(new Dimension(200, 30)); // 设定首选大小
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(calculateButton);
        exercisePanel.add(buttonPanel);

        // 添加计算按钮的事件处理
        calculateButton.addActionListener(e -> {
            Date exerciseDate = (Date) exerciseDatePicker.getModel().getValue();
            if (exerciseDate == null) {
                JOptionPane.showMessageDialog(this, "Please select a date for exercise.");
                return;
            }
            String exerciseType = exerciseTypeCombo.getSelectedItem().toString();
            String intensity = intensityCombo.getSelectedItem().toString();
            int duration = 0;
            try {
                duration = Integer.parseInt(durationField.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid duration.");
                return;
            }

            double caloriesBurned = calculateCalories(userProfile, exerciseType, duration, intensity);

            String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(exerciseDate);
            exerciseLogTableModel.addRow(new Object[]{formattedDate, exerciseType, intensity, duration, caloriesBurned});
        });


        return exercisePanel;
    }
    private double calculateCalories(UserProfile user, String exerciseType, int duration, String intensity) {
        double bmr = calculateBMR(user);

        double exerciseRate = getExerciseRate(exerciseType, intensity);

        double bmrPerMinute = bmr / (24 * 60);

        return bmrPerMinute * duration + exerciseRate * duration;
    }

    double calculateBMR(UserProfile user) {
        double weight = user.getWeight();
        double height = user.getHeight();
        int age = user.getAge();
        boolean isMale = user.getGender().equalsIgnoreCase("Male");

        if (isMale) {
            return 10 * weight + 6.25 * height - 5 * age + 5;
        } else {
            return 10 * weight + 6.25 * height - 5 * age - 161;
        }
    }

    private double getExerciseRate(String exerciseType, String intensity) {
        Map<String, Map<String, Double>> caloriesPerMin = new HashMap<>() {{
            put("Walking", new HashMap<>() {{
                put("Low", 3.5);
                put("Medium", 4.0);
                put("High", 4.5);
                put("Very High", 5.0);
            }});
            put("Running", new HashMap<>() {{
                put("Low", 9.0);
                put("Medium", 10.0);
                put("High", 11.0);
                put("Very High", 12.0);
            }});
            put("Cycling", new HashMap<>() {{
                put("Low", 6.0);
                put("Medium", 8.0);
                put("High", 10.0);
                put("Very High", 12.0);
            }});
            put("Swimming", new HashMap<>() {{
                put("Low", 5.0);
                put("Medium", 7.0);
                put("High", 9.0);
                put("Very High", 10.0);
            }});
        }};

        // 获取特定运动类型和强度的卡路里消耗率
        if (caloriesPerMin.containsKey(exerciseType) && caloriesPerMin.get(exerciseType).containsKey(intensity)) {
            return caloriesPerMin.get(exerciseType).get(intensity);
        } else {
            return 0;
        }
    }
    private void showCalorieChart(Date startDate, Date endDate) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        Calendar start = Calendar.getInstance();
        start.setTime(startDate);
        Calendar end = Calendar.getInstance();
        end.setTime(endDate);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // Loop over each day
        for (Date date = start.getTime(); start.before(end); start.add(Calendar.DATE, 1), date = start.getTime()) {
            String formattedDate = sdf.format(date);

            double totalIntake = calculateTotalCaloriesForDate(dietModel, formattedDate);
            double totalExpenditure = calculateTotalCaloriesForDate(exerciseLogTableModel, formattedDate);

            dataset.addValue(totalIntake, "Calorie Intake", formattedDate);
            dataset.addValue(totalExpenditure, "Calorie Expenditure", formattedDate);
        }

        // Creating the chart
        JFreeChart chart = ChartFactory.createBarChart(
                "Calorie Intake and Expenditure",
                "Date",
                "Calories",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        // Creating and displaying the chart in a new window
        ChartPanel chartPanel = new ChartPanel(chart);
        JFrame chartFrame = new JFrame("Calorie Chart");
        chartFrame.setContentPane(chartPanel);
        chartFrame.setSize(800, 600);
        chartFrame.setVisible(true);
    }

    // Method to calculate total calories for a given date from a TableModel
    private double calculateTotalCaloriesForDate(TableModel model, String date) {
        double totalCalories = 0.0;

        for (int i = 0; i < model.getRowCount(); i++) {
            String rowDate = model.getValueAt(i, 0).toString();
            if (rowDate.equals(date)) {
                double calories = Double.parseDouble(model.getValueAt(i, 4).toString());
                totalCalories += calories;
            }
        }

        return totalCalories;
    }

    private void showNutrientChart(Date startDate, Date endDate) {
        Map<String, Double> averageNutrients = calculateAverageDailyNutrients(startDate, endDate);

        DefaultPieDataset pieDataset = new DefaultPieDataset();

        for (Map.Entry<String, Double> entry : averageNutrients.entrySet()) {
            pieDataset.setValue(entry.getKey(), entry.getValue());
        }

        JFreeChart pieChart = ChartFactory.createPieChart(
                "Average Daily Nutrient Intake",
                pieDataset,
                true,
                true,
                false
        );

        ChartPanel chartPanel = new ChartPanel(pieChart);
        JFrame chartFrame = new JFrame("Nutrient Chart");
        chartFrame.setSize(800, 600);

        // Set the layout and add the chart panel
        chartFrame.setLayout(new BorderLayout());
        chartFrame.add(chartPanel, BorderLayout.CENTER);

        // Create notifications panel
        JPanel notificationsPanel = new JPanel();
        notificationsPanel.setLayout(new BoxLayout(notificationsPanel, BoxLayout.Y_AXIS));

        // Recommended intake levels
        Map<String, Double> recommendedIntake = Map.of("VITC", 80.0, "K", 3.5, "NA", 1200.0);
        for (Map.Entry<String, Double> entry : recommendedIntake.entrySet()) {
            String nutrient = entry.getKey();
            double recommendedValue = entry.getValue();
            double averageValue = averageNutrients.getOrDefault(nutrient, 0.0);

            String message = generateNutrientMessage(nutrient, averageValue, recommendedValue);
            notificationsPanel.add(new JLabel(message));
        }
        // Add the notifications panel below the chart
        chartFrame.add(notificationsPanel, BorderLayout.SOUTH);

// Final adjustments and displaying the frame
        chartFrame.pack();  // Adjust the frame size based on its content
        chartFrame.setLocationRelativeTo(null);  // Center the frame on the screen
        chartFrame.setVisible(true);
    }

        private Map<String, Double> calculateAverageDailyNutrients(Date startDate, Date endDate) {
        Map<String, Double> totalNutrients = new HashMap<>();
        Map<String, Double> averageNutrients = new HashMap<>();
        Set<String> specifiedNutrients = new HashSet<>(Arrays.asList("K", "PROT", "CARB", "VITC", "NA"));
        double totalOtherNutrients = 0.0;

        Calendar start = Calendar.getInstance();
        start.setTime(startDate);
        Calendar end = Calendar.getInstance();
        end.setTime(endDate);
        end.add(Calendar.DATE, 1); // Include the end date in the range

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        int daysCount = 0;

        for (Date date = start.getTime(); start.before(end); start.add(Calendar.DATE, 1), date = start.getTime()) {
            String formattedDate = sdf.format(date);

            if (nutritions.containsKey(formattedDate)) {
                Map<String, Double> dailyNutrition = nutritions.get(formattedDate);
                for (Map.Entry<String, Double> entry : dailyNutrition.entrySet()) {
                    String nutrient = entry.getKey();
                    Double value = entry.getValue();

                    if (specifiedNutrients.contains(nutrient)) {
                        totalNutrients.put(nutrient, totalNutrients.getOrDefault(nutrient, 0.0) + value);
                    } else {
                        totalOtherNutrients += value;
                    }
                }
            }

            daysCount++;
        }

        // Calculate the average for each specified nutrient
        for (String nutrient : specifiedNutrients) {
            double total = totalNutrients.getOrDefault(nutrient, 0.0);
            averageNutrients.put(nutrient, total / daysCount);
        }

        // Calculate the average for "Other" nutrients
        averageNutrients.put("Other", totalOtherNutrients / daysCount);

        return averageNutrients;
    }

    private String generateNutrientMessage(String nutrient, double averageValue, double recommendedValue) {
        double percentage = (averageValue / recommendedValue) * 100;
        String status;
        if (percentage < 75) {
            status = "below";
        } else if (percentage <= 125) {
            status = "close to";
        } else {
            status = "above";
        }
        return String.format("Your average intake of %s is %s the recommended level (%.2f%%)", nutrient, status, percentage);
    }

    private void calculateAndShowWeightLoss(Date targetDate) {
        if (targetDate == null) {
            JOptionPane.showMessageDialog(this, "Please select a target date.");
            return;
        }

        // Assuming that calculateTotalCaloriesForDate gives you the calories for a specific date
        // and your UserProfile contains information about the user's basic metabolic rate (BMR)

        Calendar start = Calendar.getInstance();
        start.setTime(new Date()); // start from the current date
        Calendar end = Calendar.getInstance();
        end.setTime(targetDate);

        double totalCaloriesBurned = 0.0;
        double totalCaloriesIntake = 0.0;

        for (Date date = start.getTime(); start.before(end); start.add(Calendar.DATE, 1), date = start.getTime()) {
            String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
            totalCaloriesIntake += calculateTotalCaloriesForDate(dietModel, formattedDate);
            totalCaloriesBurned += calculateBMR(userProfile) + calculateTotalCaloriesForDate(exerciseLogTableModel, formattedDate);
        }

        double netCalories = totalCaloriesBurned - totalCaloriesIntake + 3000;
        double fatLossKg = netCalories / 7700; // 1 kg of fat = 7700 kcal

        JOptionPane.showMessageDialog(this, String.format("Estimated weight loss by %s: %.2f kg",
                new SimpleDateFormat("yyyy-MM-dd").format(targetDate),
                fatLossKg));
    }


}
