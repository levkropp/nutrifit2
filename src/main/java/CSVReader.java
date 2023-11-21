import java.io.*;
import java.util.*;

public class CSVReader {
    public static List<FoodInfo> readFoodInfo(String fileName) {
        List<FoodInfo> foodList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",", -1); // 使用限制参数来处理空字段
                FoodInfo foodInfo = new FoodInfo(values);
                foodList.add(foodInfo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return foodList;
    }
}
