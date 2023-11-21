public class FoodInfo {
    String foodID;
    String foodCode;
    String foodGroupID;
    String foodSourceID;
    String foodDescription;
    String foodDescriptionF;
    String foodDateOfEntry;
    String foodDateOfPublication;
    String countryCode;
    String scientificName;

    public FoodInfo(String[] data) {
        this.foodID = data.length > 0 ? data[0] : "";
        this.foodCode = data.length > 1 ? data[1] : "";
        this.foodGroupID = data.length > 2 ? data[2] : "";
        this.foodSourceID = data.length > 3 ? data[3] : "";
        this.foodDescription = data.length > 4 ? data[4] : "";
        this.foodDescriptionF = data.length > 5 ? data[5] : "";
        this.foodDateOfEntry = data.length > 6 ? data[6] : "";
        this.foodDateOfPublication = data.length > 7 ? data[7] : "";
        this.countryCode = data.length > 8 ? data[8] : "";
        this.scientificName = data.length > 9 ? data[9] : "";
    }

    public String getFoodID() {
        return foodID;
    }

    public void setFoodID(String foodID) {
        this.foodID = foodID;
    }

    public String getFoodCode() {
        return foodCode;
    }

    public void setFoodCode(String foodCode) {
        this.foodCode = foodCode;
    }

    public String getFoodGroupID() {
        return foodGroupID;
    }

    public void setFoodGroupID(String foodGroupID) {
        this.foodGroupID = foodGroupID;
    }

    public String getFoodSourceID() {
        return foodSourceID;
    }

    public void setFoodSourceID(String foodSourceID) {
        this.foodSourceID = foodSourceID;
    }

    public String getFoodDescription() {
        return foodDescription;
    }

    public void setFoodDescription(String foodDescription) {
        this.foodDescription = foodDescription;
    }

    public String getFoodDescriptionF() {
        return foodDescriptionF;
    }

    public void setFoodDescriptionF(String foodDescriptionF) {
        this.foodDescriptionF = foodDescriptionF;
    }

    public String getFoodDateOfEntry() {
        return foodDateOfEntry;
    }

    public void setFoodDateOfEntry(String foodDateOfEntry) {
        this.foodDateOfEntry = foodDateOfEntry;
    }

    public String getFoodDateOfPublication() {
        return foodDateOfPublication;
    }

    public void setFoodDateOfPublication(String foodDateOfPublication) {
        this.foodDateOfPublication = foodDateOfPublication;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }
}
