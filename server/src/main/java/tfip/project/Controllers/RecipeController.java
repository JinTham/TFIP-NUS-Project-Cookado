package tfip.project.Controllers;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.AmazonS3Exception;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import tfip.project.Models.Recipe;
import tfip.project.Services.RecipeService;

@Controller
@RequestMapping(path="/api/recipe")
public class RecipeController {
    
    @Autowired
    private RecipeService recipeSvc;

    @GetMapping(path="/api")
    public ResponseEntity<String> getRecipesFromSpoonacular(@RequestParam String searchText, @RequestParam(required=false) String cuisine, @RequestParam(required=false) Integer maxReadyTime) throws IOException {
        Optional<List<Recipe>> opt = this.recipeSvc.getRecipesFromSpoonacular(searchText,cuisine,maxReadyTime);
        JsonArrayBuilder jarrBuilder = Json.createArrayBuilder();
        if (opt.isPresent()) {
            List<Recipe> recipes = opt.get();
            for (Recipe recipe : recipes) {
                jarrBuilder.add(Recipe.toJSON(recipe));
            }
        }
        JsonArray results = jarrBuilder.build();
        if (results.size()<=0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body("No recipe found!");
        }
        return ResponseEntity.status(HttpStatus.OK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(results.toString());
    }

    @GetMapping(path="/mongo")
    public ResponseEntity<String> getRecipesFromMongo(@RequestParam String searchText, @RequestParam(required=false) String cuisine, @RequestParam(required=false) Integer maxReadyTime) throws IOException {
        Optional<List<Document>> optMongo = this.recipeSvc.getRecipesFromMongoDB(searchText,cuisine,maxReadyTime);
        JsonArrayBuilder jarrBuilder = Json.createArrayBuilder();
        if (optMongo.isPresent()) {
            List<Document> recipesMongo = optMongo.get();
            for (Document recipeMongo : recipesMongo) {
                jarrBuilder.add(Recipe.docToJSON(recipeMongo).get("recipe"));
            }
        }
        JsonArray results = jarrBuilder.build();
        if (results.size()<=0) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body("No recipe found!");
        }
        return ResponseEntity.status(HttpStatus.OK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(results.toString());
    }


    @GetMapping(path="/{recipeId}")
    public ResponseEntity<String> getRecipeInfo(@PathVariable String recipeId) {
        Optional<Recipe> opt = this.recipeSvc.getRecipeInfoFromSpoonacular(recipeId);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body("Recipe Id not found");
        }
        Recipe recipe = opt.get();
        return ResponseEntity.status(HttpStatus.OK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(Recipe.toJSON(recipe).toString());
    }

    @PostMapping(path="/image/{userId}", consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<JsonObject> postImage(@PathVariable String userId, @RequestPart MultipartFile image) throws AmazonS3Exception, IOException {
        String msg = "";
        if (!image.isEmpty()) {
            msg = this.recipeSvc.postImage(userId, image);
        }
        JsonObject jo = Json.createObjectBuilder().add("msg",msg).build();
        return ResponseEntity.status(HttpStatus.CREATED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(jo);
    }

    @PostMapping(path="/{userId}")
    public ResponseEntity<JsonObject> postRecipe(@PathVariable String userId, @RequestBody Recipe recipe) {
        this.recipeSvc.createRecipe(userId, recipe);
        JsonObject jo = Json.createObjectBuilder().add("msg","Recipe created successfully").build();
        return ResponseEntity.status(HttpStatus.CREATED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(jo);
    }

    @GetMapping(path="/own/{userId}", produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getRecipesByUserId(@PathVariable String userId) {
        Optional<List<Document>> opt = recipeSvc.getRecipesByUserId(userId);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body("No recipes created by this user");
        }
        List<Document> docs = opt.get();
        JsonArrayBuilder jarrBuilder = Json.createArrayBuilder();
        for (Document doc : docs) {
            jarrBuilder.add(Recipe.docToJSON(doc));
        }
        JsonArray results = jarrBuilder.build();
        return ResponseEntity.status(HttpStatus.OK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(results.toString());
    }

    @DeleteMapping(path="/{userId}")
    public ResponseEntity<JsonObject> deleteRecipe(@RequestParam String recipeId, @PathVariable String userId) throws Exception {
        boolean delete = recipeSvc.deleteRecipe(userId, recipeId);
        if (delete){
            JsonObject jo = Json.createObjectBuilder().add("msg",recipeId+" delete successful").build();
            return ResponseEntity.status(HttpStatus.OK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(jo);
        }
        JsonObject jo = Json.createObjectBuilder().add("msg",recipeId+" delete failed").build();
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(jo);
    }

    @GetMapping(path="/cook/{userId}")
    public ResponseEntity<JsonObject> recordCook(@RequestParam String recipeId, @RequestParam String recipeTitle, @PathVariable String userId) {
        String msg = recipeSvc.recordCook(userId, recipeId, recipeTitle);
        JsonObject jo = Json.createObjectBuilder().add("msg",msg).build();
        return ResponseEntity.status(HttpStatus.OK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(jo);
    }

    @GetMapping(path="/cookrecord/{userId}")
    public ResponseEntity<String> getCookRecord(@PathVariable String userId) {
        Optional<List<Document>> opt = recipeSvc.getCookRecords(userId);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body("No records");
        }
        List<Document> docs = opt.get();
        JsonArrayBuilder jarrBuilder = Json.createArrayBuilder();
        for (Document doc : docs) {
            String datetime = doc.get("cook_datetime").toString();
            String recipeTitle = doc.get("recipe_Title").toString();
            JsonObject jo = Json.createObjectBuilder()
                        .add("cookDatetime",datetime)
                        .add("recipeTitle",recipeTitle)
                        .build();
            jarrBuilder.add(jo);
        }
        JsonArray results = jarrBuilder.build();
        return ResponseEntity.status(HttpStatus.OK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(results.toString());
    }

}
