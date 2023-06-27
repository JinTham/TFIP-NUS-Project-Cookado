package tfip.project.Models;

import java.io.Serializable;

import jakarta.json.Json;
import jakarta.json.JsonObject;

public class Grocery implements Serializable {

    private String itemId;
    private String itemName;
    private Integer quantity;

    public Grocery() {
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public JsonObject toJSON() {
        return Json.createObjectBuilder()
                .add("itemId",this.getItemId())
                .add("itemName",this.getItemName())
                .add("quantity",this.getQuantity())
                .build();
    }

}
