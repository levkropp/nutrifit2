import org.junit.jupiter.api.Test;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ExercisePanelTest {

    @Test
    void exercisePanelInitializationTest() {
        UserProfile testProfile = new UserProfile();
        TableModel testTableModel = new DefaultTableModel();
        Map<String, Double> testNutritionalValues = new HashMap<>();
        Map<String, Map<String, Double>> testNutritions = new HashMap<>();

        ExercisePanel panel = new ExercisePanel(testTableModel, testProfile, testNutritionalValues, testNutritions);

        try {
            Field exerciseLogTableModelField = ExercisePanel.class.getDeclaredField("exerciseLogTableModel");
            exerciseLogTableModelField.setAccessible(true);
            DefaultTableModel exerciseLogTableModel = (DefaultTableModel) exerciseLogTableModelField.get(panel);

            assertNotNull(exerciseLogTableModel, "exerciseLogTableModel should not be null");

        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Failed to access exerciseLogTableModel");
        }
    }

    @Test
    void exercisePanelBMRTest() {
        UserProfile testProfile = new UserProfile();
        TableModel testTableModel = new DefaultTableModel();
        Map<String, Double> testNutritionalValues = new HashMap<>();
        Map<String, Map<String, Double>> testNutritions = new HashMap<>();

        ExercisePanel panel = new ExercisePanel(testTableModel, testProfile, testNutritionalValues, testNutritions);

        try {
            Field exerciseLogTableModelField = ExercisePanel.class.getDeclaredField("exerciseLogTableModel");
            exerciseLogTableModelField.setAccessible(true);
            DefaultTableModel exerciseLogTableModel = (DefaultTableModel) exerciseLogTableModelField.get(panel);

            assertNotNull(exerciseLogTableModel, "exerciseLogTableModel should not be null");

        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Failed to access exerciseLogTableModel");
        }
    }

    @Test
    void calculateBMRMaleTest() {
        UserProfile maleUser = new UserProfile();
        maleUser.setGender("Male");
        maleUser.setBirthDate("2000-11-05");
        maleUser.setHeight(175); // 以厘米为单位
        maleUser.setWeight(70); // 以千克为单位

        ExercisePanel panel = new ExercisePanel(null, maleUser, null, null);
        double expectedBMR = 10 * 70 + 6.25 * 175 - 5 * 23 + 5;
        assertEquals(expectedBMR, panel.calculateBMR(maleUser), "BMR calculation for male is incorrect");
    }

    @Test
    void calculateBMRFemaleTest() {
        UserProfile femaleUser = new UserProfile();
        femaleUser.setGender("Female");
        femaleUser.setBirthDate("2000-11-05");
        femaleUser.setHeight(160);
        femaleUser.setWeight(55);

        ExercisePanel panel = new ExercisePanel(null, femaleUser, null, null);
        double expectedBMR = 10 * 55 + 6.25 * 160 - 5 * 23 - 161;
        assertEquals(expectedBMR, panel.calculateBMR(femaleUser), "BMR calculation for female is incorrect");
    }
}