package tfip.project.Services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import tfip.project.Models.Grocery;
import tfip.project.Models.GroceryList;
import tfip.project.Models.Item;
import tfip.project.Repositories.GroceryRepository;

@Service
public class GroceryService {
    
    @Autowired
    private GroceryRepository groceryRepo;

    public Optional<GroceryList> getGrocery (String userId) {
        return groceryRepo.getGrocery(userId);
    }

    @Transactional
    public boolean updateGrocery(GroceryList groceryList) throws Exception {
        boolean update = true;
        String userId = groceryList.getUserId();
        List<Grocery> groceries = groceryList.getGroceries();
        for (Grocery grocery : groceries) {
            Integer quantity = grocery.getQuantity();
            if (groceryRepo.checkItemInInventory(userId, grocery.getItemId())) {
                System.out.println("In inventory");
                Integer oriQuantity = groceryRepo.getQuantity(userId, grocery.getItemId());
                Integer toBeQuantity = oriQuantity+quantity;
                if (toBeQuantity < 0) {
                    System.out.println("Minus too much");
                    toBeQuantity = 0;
                    grocery.setQuantity(-oriQuantity);
                }
                if (!groceryRepo.updateQuantity(userId, grocery.getItemId(), toBeQuantity)) {
                    System.out.println("Update failed");
                    update = false;
                }
            } else {
                System.out.println("Not in inventory");
                if (quantity > 0) {
                    System.out.println("Positive quantity");
                    if (!groceryRepo.insertInventory(userId, grocery.getItemId(), quantity)) {
                        System.out.println("Insert failed");
                        update = false;
                    }
                } else {
                    System.out.println("Negative quantity");
                    throw new Exception("Cannot insert grocery with negative quantity!"); 
                }
            }
        }
        groceryRepo.recordGroceryUpdate(groceryList);
        return update;
    }

    public Optional<List<Item>> getAllItems(String userId) {
        return groceryRepo.getAllItems(userId);
    }

    public Optional<String> updateItem(String userId, Item item) {
        String itemId = item.getItemId();
        if (itemId==null || itemId.equalsIgnoreCase("")) {
            Optional<Item> opt = groceryRepo.getItemByName(userId, item.getItemName());
            if (opt.isEmpty()) {
                // Item not in item list yet
                itemId = UUID.randomUUID().toString().substring(0, 8);
                if (groceryRepo.insertItem(userId, itemId, item.getItemName(), item.getTopupAmount(), item.getSafetyStock(), item.getUnit())) {
                    item.setItemId(itemId);
                    groceryRepo.recordItemUpdate(userId, item, "insert");
                    return Optional.of(itemId);
                } else {
                    return Optional.empty();
                }
            }
            itemId = opt.get().getItemId();
        }
        // Item already in item list
        if (groceryRepo.updateItem(userId, itemId, item.getTopupAmount(), item.getSafetyStock(), item.getUnit())) {
            groceryRepo.recordItemUpdate(userId, item, "update");
            return Optional.of(item.getItemId());
        } else {
            return Optional.empty();
        }
    }

    @Transactional
    public boolean deleteItem(String userId, String itemId) throws Exception {
        Optional<Item> opt = groceryRepo.getItemById(userId, itemId);
        if (opt.isPresent()) {
            if (groceryRepo.checkItemInInventory(userId, itemId)) { //item exists in inventory
                if (groceryRepo.deleteGrocery(userId, itemId) && groceryRepo.deleteItem(userId, itemId)){
                    groceryRepo.recordItemUpdate(userId, opt.get(), "delete");
                    return true;
                }
            } else { //item not in inventory
                groceryRepo.recordItemUpdate(userId, opt.get(), "delete");
                return groceryRepo.deleteItem(userId, itemId);
            }
        }
        return false;
    }

    public Optional<List<Document>> getGroceryUpdateRecords(String userId) {
        return groceryRepo.getGroceryUpdateRecords(userId);
    }

    public Optional<List<Document>> getItemsUpdateRecords(String userId) {
        return groceryRepo.getItemsUpdateRecords(userId);
    }

}
