package tfip.project.Models;

import jakarta.json.Json;
import jakarta.json.JsonObject;

public class Ingredient {

    private String ingredientId;
    private String ingredientName;
    private Integer amount;
    private String unit;

    public Ingredient() {
    }

    public String getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(String ingredientId) {
        this.ingredientId = ingredientId;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public static JsonObject toJSON(Ingredient ingredient) {
        return Json.createObjectBuilder()
                .add("ingredientId",ingredient.getIngredientId())
                .add("ingredientName",ingredient.getIngredientName())
                .add("amount",ingredient.getAmount())
                .add("unit",ingredient.getUnit())
                .build();
    }

    public static Ingredient fromJSON(JsonObject jo) {
        Ingredient ingredient = new Ingredient();
        ingredient.setIngredientId(String.valueOf(jo.getInt("id")));
        ingredient.setIngredientName(jo.getString("name"));
        ingredient.setAmount(jo.getInt("amount"));
        ingredient.setUnit(jo.getString("unit"));
        return ingredient;
    }

}
