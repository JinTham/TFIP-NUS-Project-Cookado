package tfip.project.Repositories;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import tfip.project.Models.Grocery;
import tfip.project.Models.GroceryList;
import tfip.project.Models.Item;

@Repository
public class GroceryRepository {
    
    // -----------------------------------------------MYSQL-------------------------------------------------------------
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserRepository userRepo;

    // INVENTORY table
    private static final String SQL_INSERT_INVENTORY = "insert into inventory (user_id, item_id, quantity) values (?,?,?)";
    private static final String SQL_UPDATE_INVENTORY = "update inventory set quantity=? where user_id=? and item_id=?";
    private static final String SQL_GET_QUANTITY = "select quantity from inventory where user_id=? and item_id=?";
    private static final String SQL_DELETE_INVENTORY = "delete from inventory where user_id=? and item_id=?";
    private static final String SQL_SELECT_INVENTORY = """
        select inv.user_id, username, inv.item_id, item_name, quantity
        from inventory as inv
        inner join user_info as u on inv.user_id = u.user_id
        inner join item_info as i on inv.item_id = i.item_id
        where inv.user_id = ? and quantity > 0
        order by item_name
        """;
    private static final String SQL_CHECK_INVENTORY = """
        select inv.user_id, username, inv.item_id, item_name, quantity
        from inventory as inv
        inner join user_info as u on inv.user_id = u.user_id
        inner join item_info as i on inv.item_id = i.item_id
        where inv.user_id = ?
        and inv.item_id = ?
        """;

    public Optional<GroceryList> getGrocery (String userId) {
        SqlRowSet rs = jdbcTemplate.queryForRowSet(SQL_SELECT_INVENTORY, userId);
        GroceryList groceryList = new GroceryList();
        List<Grocery> groceries = new LinkedList<>();
        groceryList.setUserId(userId);
        groceryList.setUsername(userRepo.getUsernameById(userId).get());
        while (rs.next()) {
            Grocery grocery = new Grocery();
            grocery.setItemId(rs.getString("item_id"));
            grocery.setItemName(rs.getString("item_name"));
            grocery.setQuantity(rs.getInt("quantity"));
            groceries.add(grocery);
        }
        groceryList.setGroceries(groceries);
        if (groceryList.getGroceries()==null || groceryList.getGroceries().size()<=0){
            return Optional.empty();
        }
        return Optional.of(groceryList);
    }

    public boolean checkItemInInventory(String userId, String itemId) {
        SqlRowSet rs = jdbcTemplate.queryForRowSet(SQL_CHECK_INVENTORY, userId, itemId);
        if (!rs.next()) {
            return false;
        }
        return true;
    }

    public boolean insertInventory(String userId, String itemId, Integer quantity) throws Exception{
        int insert = jdbcTemplate.update(SQL_INSERT_INVENTORY, userId, itemId, quantity);
        if (insert<=0){
            throw new Exception("Failed to insert item %s".formatted(itemId));
        }
        return insert>0;
    }

    public boolean updateQuantity(String userId, String itemId, Integer quantity) throws Exception {
        int update = jdbcTemplate.update(SQL_UPDATE_INVENTORY, quantity, userId, itemId);
        if (update<=0){
            throw new Exception("Failed to update item %s".formatted(itemId));
        }
        return update>0;
    }

    public Integer getQuantity(String userId, String itemId) {
        return (Integer) jdbcTemplate.queryForObject(SQL_GET_QUANTITY,  Integer.class, userId, itemId); 
    }

    public boolean deleteGrocery(String userId, String itemId) {
        int delete = jdbcTemplate.update(SQL_DELETE_INVENTORY, userId, itemId);
        return delete>0;
    }

    // ITEM_INFO table
    private static final String SQL_SELECT_ALL_ITEMS = "select * from item_info where user_id=?";
    private static final String SQL_SELECT_ITEM_BYID = "select * from item_info where user_id=? and item_id=?";
    private static final String SQL_SELECT_ITEM_BYNAME = "select * from item_info where user_id=? and item_name=?";
    private static final String SQL_INSERT_ITEM = "insert into item_info (user_id, item_id, item_name, topup_amount, safety_stock, unit) values (?,?,?,?,?,?)";
    private static final String SQL_UPDATE_ITEM = "update item_info set topup_amount=?, safety_stock=?, unit=? where user_id=? and item_id=?";
    private static final String SQL_DELETE_ITEM = "delete from item_info where user_id=? and item_id=?";

    public Optional<List<Item>> getAllItems (String userId) {
        SqlRowSet rs = jdbcTemplate.queryForRowSet(SQL_SELECT_ALL_ITEMS, userId);
        List<Item> items = new LinkedList<>();
        while (rs.next()) {
            Item item = new Item();
            item.setItemId(rs.getString("item_id"));
            item.setItemName(rs.getString("item_name"));
            item.setTopupAmount(rs.getInt("topup_amount"));
            item.setSafetyStock(rs.getInt("safety_stock"));
            item.setUnit(rs.getString("unit"));
            items.add(item);
        }
        if (items==null || items.size()<=0){
            return Optional.empty();
        }
        return Optional.of(items);
    }

    public Optional<Item> getItemById (String userId, String itemId) {
        SqlRowSet rs = jdbcTemplate.queryForRowSet(SQL_SELECT_ITEM_BYID, userId, itemId);
        if (!rs.next()) {
            return Optional.empty();
        }
        Item item = new Item();
        item.setItemId(itemId);
        item.setItemName(rs.getString("item_name"));
        item.setTopupAmount(rs.getInt("topup_amount"));
        item.setSafetyStock(rs.getInt("safety_stock"));
        item.setUnit(rs.getString("unit"));
        return Optional.of(item);
    }

    public Optional<Item> getItemByName (String userId, String itemName) {
        SqlRowSet rs = jdbcTemplate.queryForRowSet(SQL_SELECT_ITEM_BYNAME, userId, itemName);
        if (!rs.next()) {
            return Optional.empty();
        }
        Item item = new Item();
        item.setItemId(rs.getString("item_id"));
        item.setItemName(itemName);
        item.setTopupAmount(rs.getInt("topup_amount"));
        item.setSafetyStock(rs.getInt("safety_stock"));
        item.setUnit(rs.getString("unit"));
        return Optional.of(item);
    }

    public boolean insertItem(String userId, String itemId, String itemName, Integer topupAmount, Integer safetyStock, String unit) {
        int insert = jdbcTemplate.update(SQL_INSERT_ITEM, userId, itemId, itemName, topupAmount, safetyStock, unit);
        return insert>0;
    }

    public boolean updateItem(String userId, String itemId, Integer topupAmount, Integer safetyStock, String unit) {
        int update = jdbcTemplate.update(SQL_UPDATE_ITEM, topupAmount, safetyStock, unit, userId, itemId);
        return update>0;
    }

    public boolean deleteItem(String userId, String itemId) throws Exception {
        int delete = jdbcTemplate.update(SQL_DELETE_ITEM, userId, itemId);
        if (delete<=0){
            throw new Exception("Failed to delete item %s".formatted(itemId));
        }
        return delete>0;
    }


    // -----------------------------------------------MONGODB-------------------------------------------------------------
    @Autowired
    private MongoTemplate mongoTemplate;

    private static final String MONGODB_GROCERY_COLLECTION = "grocery";
    private static final String MONGODB_ITEM_COLLECTION = "item";

    // Grocery update table
    public String recordGroceryUpdate(GroceryList groceryList) {
        Document toSave = new Document();
        toSave.put("update_datetime", LocalDateTime.now());
        toSave.put("grocery_list", groceryList);
        this.mongoTemplate.insert(toSave, MONGODB_GROCERY_COLLECTION);
        return "Grocery update saved";
    }

    public Optional<List<Document>> getGroceryUpdateRecords(String userId) {
        List<Document> result = mongoTemplate.find(Query.query(Criteria.where("grocery_list.userId").is(userId))
                                                                .with(Sort.by(Sort.Direction.DESC,"update_datetime"))
                                                                ,Document.class,MONGODB_GROCERY_COLLECTION);
        if (result == null || result.size()<1) {
            return Optional.empty();
        }
        return Optional.of(result);
    }

    // Item update table
    public String recordItemUpdate(String userId, Item item, String action) {
        Document toSave = new Document();
        toSave.put("user_id", userId);
        toSave.put("update_datetime", LocalDateTime.now());
        toSave.put("action",action);
        toSave.put("item", item);
        this.mongoTemplate.insert(toSave, MONGODB_ITEM_COLLECTION);
        return "Item update saved";
    }

    public Optional<List<Document>> getItemsUpdateRecords(String userId) {
        List<Document> result = mongoTemplate.find(Query.query(Criteria.where("user_id").is(userId))
                                                            .with(Sort.by(Sort.Direction.DESC,"update_datetime"))
                                                            ,Document.class,MONGODB_ITEM_COLLECTION);
        if (result == null || result.size()<1) {
            return Optional.empty();
        }
        return Optional.of(result);
    }

}
