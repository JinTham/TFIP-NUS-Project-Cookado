package tfip.project.Services;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.amazonaws.services.s3.model.AmazonS3Exception;

import tfip.project.Models.Recipe;
import tfip.project.Repositories.ImageRepository;
import tfip.project.Repositories.RecipeRepository;

@Service
public class RecipeService {

    @Autowired
    private RecipeRepository recipeRepo;

    @Autowired
    private ImageRepository imageRepo;

    @Autowired
    private UserService userSvc;

    @Value("${SPOONACULAR_APIKEY}")
    private String spoonacularApiKey;

    private static final String URL_RECIPES = "https://api.spoonacular.com/recipes/complexSearch";
    private static final String URL_RECIPE_INFO = "https://api.spoonacular.com/recipes/";
    
    public Optional<List<Recipe>> getRecipesFromSpoonacular(String searchText, String cuisine, Integer maxReadyTime) throws IOException {
        UriComponentsBuilder API_URL_ComponentsBuilder = UriComponentsBuilder.fromUriString(URL_RECIPES)
                                            .queryParam("query", searchText)
                                            .queryParam("apiKey", spoonacularApiKey);
        if (cuisine!=null) {
            API_URL_ComponentsBuilder.queryParam("cuisine",cuisine);
        }
        if (maxReadyTime!=null) {
            API_URL_ComponentsBuilder.queryParam("maxReadyTime",maxReadyTime);
        }
        String API_URL = API_URL_ComponentsBuilder.toUriString();
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> resp = restTemplate.getForEntity(API_URL,String.class);
        List<Recipe> recipes = Recipe.create(resp.getBody());
        if (recipes != null) {
            return Optional.of(recipes);
        }
        return Optional.empty();
    }

    public Optional<Recipe> getRecipeInfoFromSpoonacular(String recipeId) {
        String API_URL = UriComponentsBuilder.fromUriString(URL_RECIPE_INFO+recipeId+"/information")
                            .queryParam("apiKey", spoonacularApiKey)
                            .toUriString();
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> resp = restTemplate.getForEntity(API_URL,String.class);
        Recipe recipe = Recipe.createFull(resp.getBody());
        if (recipe != null) {
            return Optional.of(recipe);
        }
        return Optional.empty();
    }

    public void createRecipe(String userId, Recipe recipe) {
        String username = this.userSvc.getUsernameById(userId).get();
        recipe.setAuthor(username);
        this.recipeRepo.createRecipe(userId, recipe);
    }

    public String postImage(String userId, MultipartFile image) throws AmazonS3Exception, IOException {
        String recipeId = UUID.randomUUID().toString().substring(0, 8);
        String imageUrl = this.imageRepo.uploadImage(image, recipeId).get();
        return recipeId+imageUrl;
    }

    public Optional<List<Document>> getRecipesByUserId(String userId) {
        return recipeRepo.getRecipesByUserId(userId);
    }

    public boolean deleteRecipe(String userId, String recipeId) throws Exception {
        return recipeRepo.deleteRecipe(userId, recipeId);
    }

    public Optional<List<Document>> getRecipesFromMongoDB(String searchText, String cuisine, Integer maxReadyTime) {
        return recipeRepo.getRecipesFromMongoDB(searchText, cuisine, maxReadyTime);
    }

    public String recordCook(String userId, String recipeId, String recipeTitle) {
        return this.recipeRepo.recordCook(userId, recipeId, recipeTitle);
    }

    public Optional<List<Document>> getCookRecords(String userId) {
        return recipeRepo.getCookRecords(userId);
    }

}
