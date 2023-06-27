package tfip.project.Models;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.bson.Document;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;

public class Recipe {
    
    private String recipeId;
    private String recipeTitle;
    private String author;
    private Integer readyInMinutes;
    private String image;
    private String summary;
    private String cuisine;
    private String instructions;
    private List<Ingredient> extendedIngredients;

    public Recipe() {
    }

    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public String getRecipeTitle() {
        return recipeTitle;
    }

    public void setRecipeTitle(String recipeTitle) {
        this.recipeTitle = recipeTitle;
    }

    public Integer getReadyInMinutes() {
        return readyInMinutes;
    }

    public void setReadyInMinutes(Integer readyInMinutes) {
        this.readyInMinutes = readyInMinutes;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public List<Ingredient> getExtendedIngredients() {
        return extendedIngredients;
    }

    public void setExtendedIngredients(List<Ingredient> extendedIngredients) {
        this.extendedIngredients = extendedIngredients;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }


    public static JsonObject toJSON(Recipe recipe) {
        String author;
        if (recipe.getAuthor() == null){
            author = "";
        } else {
            author = recipe.getAuthor();
        }
        String cuisine;
        if (recipe.getCuisine() == null){
            cuisine = "";
        } else {
            cuisine = recipe.getCuisine();
        }
        Integer readyInMinutes;
        if (recipe.getReadyInMinutes()==null){
            readyInMinutes = 0;
        } else {
            readyInMinutes = recipe.getReadyInMinutes();
        }
        String summary;
        if (recipe.getSummary()==null){
            summary = "";
        } else {
            summary = recipe.getSummary();
        }
        String instructions;
        if (recipe.getInstructions()==null){
            instructions = "";
        } else {
            instructions = recipe.getInstructions();
        }
        JsonArrayBuilder jarrBuilder = Json.createArrayBuilder();
        if (recipe.getExtendedIngredients() != null && recipe.getExtendedIngredients().size()>0){
            for (Ingredient ingredient : recipe.getExtendedIngredients()){
                jarrBuilder.add(Ingredient.toJSON(ingredient));
            }
        }
        JsonArray ingredients = jarrBuilder.build();
        return Json.createObjectBuilder()
                    .add("recipeId",recipe.getRecipeId())
                    .add("recipeTitle",recipe.getRecipeTitle())
                    .add("author",author)
                    .add("readyInMinutes",readyInMinutes)
                    .add("image",recipe.getImage())
                    .add("summary",summary)
                    .add("cuisines",cuisine)
                    .add("instructions",instructions)
                    .add("extendedIngredients",ingredients)
                    .build();
    }

    public static Recipe fromJSONShort(JsonObject jo) {
        Recipe recipe = new Recipe();
        recipe.setRecipeId(String.valueOf(jo.getInt("id")));
        recipe.setRecipeTitle(jo.getString("title"));
        if (!jo.isNull("image")){
            recipe.setImage(jo.getString("image"));
        } else {
            recipe.setImage("");
        }
        return recipe;
    }

    public static Recipe fromJSONFull(JsonObject jo) {
        Recipe recipe = new Recipe();
        // RecipeId
        recipe.setRecipeId(String.valueOf(jo.getInt("id")));
        // RecipeTitle
        recipe.setRecipeTitle(jo.getString("title"));
        // Author
        recipe.setAuthor("Spoonacular");
        // ReadyInMinutes
        if (!jo.isNull("readyInMinutes")){
            recipe.setReadyInMinutes(jo.getInt("readyInMinutes"));
        } else {
            recipe.setReadyInMinutes(0);
        }
        // Image
        if (!jo.isNull("image")){
            recipe.setImage(jo.getString("image"));
        } else {
            recipe.setImage("");
        }
        // Summary
        if (!jo.isNull("summary")){
            recipe.setSummary(jo.getString("summary"));
        } else {
            recipe.setSummary("");
        }
        // Cuisines
        recipe.setCuisine("");
        if (!jo.isNull("cuisines")){
            JsonArray cuisinesJo = (JsonArray) jo.get("cuisines");
            if (cuisinesJo.size()>0){
                recipe.setCuisine(cuisinesJo.getString(0));
            }
        }
        // Instructions
        if (!jo.isNull("instructions")){
            recipe.setInstructions(jo.getString("instructions"));
        } else {
            recipe.setInstructions("");
        }
        // ExtendedIngredients
        List<Ingredient> ingredients = new LinkedList<>();
        if (!jo.isNull("extendedIngredients")){
            JsonArray ingredientsJo = (JsonArray) jo.get("extendedIngredients");
            for (JsonValue ingredient : ingredientsJo){
                ingredients.add(Ingredient.fromJSON((JsonObject) ingredient));
            }
        }
        recipe.setExtendedIngredients(ingredients);
        return recipe;
    }

    public static List<Recipe> create(String body) throws IOException {
        List<Recipe> recipes = new LinkedList<>();
        try (InputStream is = new ByteArrayInputStream(body.getBytes())) {
            JsonReader jrd = Json.createReader(is);
            JsonObject jo = jrd.readObject();
            if (!jo.isNull("results")){
                recipes = jo.getJsonArray("results").stream()
                            .map(recipe -> (JsonObject) recipe)
                            .map(recipe -> Recipe.fromJSONShort(recipe))
                            .toList();
            }
        }
        return recipes;
    }

    public static Recipe createFull(String body) {
        InputStream is = new ByteArrayInputStream(body.getBytes());
        JsonReader jrd = Json.createReader(is);
        JsonObject jo = jrd.readObject();
        Recipe recipe = Recipe.fromJSONFull(jo);
        return recipe;
    }

    public static JsonObject docToJSON(Document doc) {
        String datetime = doc.get("create_datetime").toString();
        Document recipeDoc = (Document) doc.get("recipe");
        List<Document> extendedIngredients = recipeDoc.get("extendedIngredients",new LinkedList<Document>());
        JsonArrayBuilder jarrBuilder = Json.createArrayBuilder();
        for (Document ingredient : extendedIngredients) {
            String ingredientName = (String) ingredient.get("ingredientName");
            Integer amount = (Integer) ingredient.get("amount");
            String unit = (String) ingredient.get("unit");
            JsonObject jo = Json.createObjectBuilder()
                                .add("ingredientName",ingredientName)
                                .add("amount",amount)
                                .add("unit",unit)
                                .build();
            jarrBuilder.add(jo);
        }
        JsonArray ingredientsJArr = jarrBuilder.build();
        JsonObject recipe = Json.createObjectBuilder()
                                .add("recipeId",recipeDoc.get("recipeId").toString())
                                .add("recipeTitle",recipeDoc.get("recipeTitle").toString())
                                .add("author",recipeDoc.get("author").toString())
                                .add("readyInMinutes",(Integer) recipeDoc.get("readyInMinutes"))
                                .add("image",recipeDoc.get("image").toString())
                                .add("summary",recipeDoc.get("summary").toString())
                                .add("cuisine",recipeDoc.get("cuisine").toString())
                                .add("instructions",recipeDoc.get("instructions").toString())
                                .add("extendedIngredients",ingredientsJArr)
                                .build();
        return Json.createObjectBuilder()
                    .add("createDatetime",datetime)
                    .add("recipe",recipe)
                    .build();
    }

}
