package tfip.project.Models;

import org.bson.Document;

import jakarta.json.Json;
import jakarta.json.JsonObject;

public class Item {
    
    private String userId;
    private String itemId;
    private String itemName;
    private Integer topupAmount;
    private Integer safetyStock;
    private String unit;
    
    public Item() {
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getTopupAmount() {
        return topupAmount;
    }

    public void setTopupAmount(Integer topupAmount) {
        this.topupAmount = topupAmount;
    }

    public Integer getSafetyStock() {
        return safetyStock;
    }

    public void setSafetyStock(Integer safetyStock) {
        this.safetyStock = safetyStock;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
   

    public JsonObject toJSON() {
        return Json.createObjectBuilder()
                .add("itemId",this.getItemId())
                .add("itemName",this.getItemName())
                .add("topupAmount",this.getTopupAmount())
                .add("safetyStock",this.getSafetyStock())
                .add("unit",this.getUnit())
                .build();
    }

    public static JsonObject docToJSON(Document doc) {
        String datetime = doc.get("update_datetime").toString();
        String action = doc.get("action").toString();
        Document itemDoc = (Document) doc.get("item");
        JsonObject item = Json.createObjectBuilder()
                                .add("itemId",itemDoc.get("itemId").toString())
                                .add("itemName",itemDoc.get("itemName").toString())
                                .add("topupAmount",(Integer) itemDoc.get("topupAmount"))
                                .add("safetyStock",(Integer) itemDoc.get("safetyStock"))
                                .add("unit",itemDoc.get("unit").toString())
                                .build();
        return Json.createObjectBuilder()
                    .add("updateDatetime",datetime)
                    .add("action",action)
                    .add("item",item)
                    .build();
    }
   
}
