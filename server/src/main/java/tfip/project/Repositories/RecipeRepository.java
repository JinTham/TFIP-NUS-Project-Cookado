package tfip.project.Repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.mongodb.client.result.DeleteResult;

import tfip.project.Models.Recipe;

@Repository
public class RecipeRepository {

    private static final String MONGODB_RECIPE_COLLECTION = "recipe";
    private static final String MONGODB_COOK_COLLECTION = "cook";

    @Autowired
    private MongoTemplate mongoTemplate;

    public void createRecipe(String userId, Recipe recipe) {
        Document toSave = new Document();
        toSave.put("userId", userId);
        toSave.put("create_datetime", LocalDateTime.now());
        toSave.put("recipe", recipe);
        this.mongoTemplate.insert(toSave, MONGODB_RECIPE_COLLECTION);
    }

    public Optional<List<Document>> getRecipesByUserId(String userId) {
        List<Document> result = mongoTemplate.find(Query.query(Criteria.where("userId").is(userId))
                                                    .with(Sort.by(Sort.Direction.DESC,"create_datetime"))
                                                    ,Document.class, MONGODB_RECIPE_COLLECTION);
        if (result == null || result.size()<1) {
            return Optional.empty();
        }
        return Optional.of(result);
    }

    public boolean deleteRecipe(String userId, String recipeId) throws Exception {
        Criteria criteria = new Criteria();
        criteria.andOperator(
            Criteria.where("userId").is(userId),
            Criteria.where("recipe.recipeId").is(recipeId)
        );
        Query query = new Query(criteria);
        DeleteResult delete = mongoTemplate.remove(query, MONGODB_RECIPE_COLLECTION);
        if (delete.getDeletedCount()<=0){
            throw new Exception("Failed to delete recipe %s".formatted(recipeId));
        }
        return delete.getDeletedCount()>0;
    }

    public Optional<List<Document>> getRecipesFromMongoDB(String searchText, String cuisine, Integer maxReadyTime) {
        Criteria criteria = new Criteria();
        if (cuisine==null && maxReadyTime==null) {
            criteria = Criteria.where("recipe.recipeTitle").regex(searchText,"i");
        } else if (cuisine==null) {
            criteria.andOperator(
                Criteria.where("recipe.recipeTitle").regex(searchText,"i"),
                Criteria.where("recipe.readyInMinutes").lte(maxReadyTime));
        } else if (maxReadyTime==null) {
            criteria.andOperator(
                Criteria.where("recipe.recipeTitle").regex(searchText,"i"),
                Criteria.where("recipe.cuisine").is(cuisine));
        } else {
            criteria.andOperator(
                Criteria.where("recipe.recipeTitle").regex(searchText,"i"),
                Criteria.where("recipe.cuisine").is(cuisine),
                Criteria.where("recipe.readyInMinutes").lte(maxReadyTime));
        }
        Query query = new Query(criteria).limit(10);
        List<Document> result = mongoTemplate.find(query,Document.class,MONGODB_RECIPE_COLLECTION);
        if (result == null || result.size()<1) {
            return Optional.empty();
        }
        return Optional.of(result);
    }

    public String recordCook(String userId, String recipeId, String recipeTitle) {
        Document toSave = new Document();
        toSave.put("cook_datetime", LocalDateTime.now());
        toSave.put("user_id", userId);
        toSave.put("recipe_id", recipeId);
        toSave.put("recipe_Title", recipeTitle);
        this.mongoTemplate.insert(toSave, MONGODB_COOK_COLLECTION);
        return "Record successful";
    }

    public Optional<List<Document>> getCookRecords(String userId) {
        List<Document> result = mongoTemplate.find(Query.query(Criteria.where("user_id").is(userId))
                                                    .with(Sort.by(Sort.Direction.DESC,"cook_datetime"))
                                                    ,Document.class,MONGODB_COOK_COLLECTION);
        if (result == null || result.size()<1) {
            return Optional.empty();
        }
        return Optional.of(result);
    }
    
}
