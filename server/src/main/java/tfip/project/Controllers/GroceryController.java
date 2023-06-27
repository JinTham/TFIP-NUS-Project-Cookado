package tfip.project.Controllers;

import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import tfip.project.Models.GroceryList;
import tfip.project.Models.Item;
import tfip.project.Services.GroceryService;

@Controller
@RequestMapping(path="/api/grocery")
@CrossOrigin(origins="*")
public class GroceryController {
    
    @Autowired
    private GroceryService grocerySvc;

    @GetMapping(path="/{userId}")
    public ResponseEntity<String> getGrocery(@PathVariable String userId) {
        Optional<GroceryList> opt = grocerySvc.getGrocery(userId);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body("User's grocery is empty!");
        }
        GroceryList groceryList = opt.get();
        return ResponseEntity.status(HttpStatus.OK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(groceryList.toJSON().toString());
    }
    
    @PostMapping(path="/{userId}")
    public ResponseEntity<JsonObject> updateGrocery(@PathVariable String userId, @RequestBody GroceryList groceryList) throws Exception {
        boolean update = grocerySvc.updateGrocery(groceryList);
        if (update){
            JsonObject jo = Json.createObjectBuilder().add("msg","Update successful").build();
            return ResponseEntity.status(HttpStatus.CREATED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(jo);
        }
        JsonObject jo = Json.createObjectBuilder().add("msg","Update failed").build();
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(jo);
    }

    @GetMapping(path="/item/{userId}")
    public ResponseEntity<String> getAllItems(@PathVariable String userId) {
        Optional<List<Item>> opt = grocerySvc.getAllItems(userId);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body("User's item list is empty!");
        }
        List<Item> items = opt.get();
        JsonArrayBuilder jarrBuilder = Json.createArrayBuilder();
        for (Item item : items) {
            jarrBuilder.add(item.toJSON());
        }
        JsonArray results = jarrBuilder.build();
        return ResponseEntity.status(HttpStatus.OK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(results.toString());
    }

    @PostMapping(path="/item/{userId}")
    public ResponseEntity<JsonObject> postItem(@RequestBody Item item, @PathVariable String userId) {
        Optional<String> itemId = grocerySvc.updateItem(userId, item);
        if (itemId.isEmpty()){
            JsonObject jo = Json.createObjectBuilder().add("msg","Item update failed").build();
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(jo);
        }
        JsonObject jo = Json.createObjectBuilder().add("msg",itemId.get()).build();
        return ResponseEntity.status(HttpStatus.CREATED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(jo);
    }

    @DeleteMapping(path="/item/{userId}")
    public ResponseEntity<JsonObject> deleteItem(@RequestParam String itemId, @PathVariable String userId) throws Exception {
        boolean delete = grocerySvc.deleteItem(userId, itemId);
        if (delete){
            JsonObject jo = Json.createObjectBuilder().add("msg",itemId+" delete successful").build();
            return ResponseEntity.status(HttpStatus.OK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(jo);
        }
        JsonObject jo = Json.createObjectBuilder().add("msg",itemId+" delete failed").build();
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(jo);
    }

    @GetMapping(path="/records/{userId}", produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getGroceryUpdateRecords(@PathVariable String userId) {
        Optional<List<Document>> opt = grocerySvc.getGroceryUpdateRecords(userId);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body("No records");
        }
        List<Document> docs = opt.get();
        JsonArrayBuilder jarrBuilder = Json.createArrayBuilder();
        for (Document doc : docs) {
            jarrBuilder.add(GroceryList.docToJSON(doc));
        }
        JsonArray results = jarrBuilder.build();
        return ResponseEntity.status(HttpStatus.OK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(results.toString());
    }

    @GetMapping(path="/item/records/{userId}", produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getItemsUpdateRecords(@PathVariable String userId) {
        Optional<List<Document>> opt = grocerySvc.getItemsUpdateRecords(userId);
        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body("No records");
        }
        List<Document> docs = opt.get();
        JsonArrayBuilder jarrBuilder = Json.createArrayBuilder();
        for (Document doc : docs) {
            jarrBuilder.add(Item.docToJSON(doc));
        }
        JsonArray results = jarrBuilder.build();
        return ResponseEntity.status(HttpStatus.OK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(results.toString());
    }
    
}
