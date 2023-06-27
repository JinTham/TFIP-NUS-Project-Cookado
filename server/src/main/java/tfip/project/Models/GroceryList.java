package tfip.project.Models;

import java.util.LinkedList;
import java.util.List;

import org.bson.Document;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;

public class GroceryList {
    
    private String userId;
    private String username;
    private List<Grocery> groceries;
    private List<Item> items;
    
    public GroceryList() {
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public List<Grocery> getGroceries() {
        return groceries;
    }
    public void setGroceries(List<Grocery> groceries) {
        this.groceries = groceries;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public JsonObject toJSON() {
        JsonArrayBuilder jarrBuilder = Json.createArrayBuilder();
        for (Grocery grocery : this.getGroceries()) {
            jarrBuilder.add(grocery.toJSON());
        }
        return Json.createObjectBuilder()
                .add("userId",this.getUserId())
                .add("username",this.getUsername())
                .add("groceries",jarrBuilder.build())
                .build();
    }

    public static JsonObject docToJSON(Document doc) {
        JsonArrayBuilder jarrBuilder = Json.createArrayBuilder();
        String datetime = doc.get("update_datetime").toString();
        Document groceryListDoc = (Document) doc.get("grocery_list");
        List<Document> groceries = groceryListDoc.get("groceries",new LinkedList<Document>());
        for (Document grocery : groceries) {
            String itemName = (String) grocery.get("itemName");
            Integer quantityChg = (Integer) grocery.get("quantity");
            JsonObject jo = Json.createObjectBuilder()
                                .add("itemName",itemName)
                                .add("quantity",quantityChg)
                                .build();
            jarrBuilder.add(jo);
        }
        JsonArray groceriesJArr = jarrBuilder.build();
        return Json.createObjectBuilder()
                    .add("updateDatetime",datetime)
                    .add("groceries",groceriesJArr)
                    .build();
    }

}
