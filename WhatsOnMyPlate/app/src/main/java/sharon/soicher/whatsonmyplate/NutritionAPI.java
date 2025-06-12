package sharon.soicher.whatsonmyplate;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NutritionAPI {
    private static final String TAG = "NutritionAPI";

    // USDA FoodData Central API - free to use
    private static final String BASE_URL = "https://api.nal.usda.gov/fdc/v1/foods/search";
    private static final String API_KEY = "DEMO_KEY"; // You can use DEMO_KEY for testing or get your own free API key

    private final OkHttpClient client;

    public interface NutritionCallback {
        void onSuccess(FoodNutritionData foodData);
        void onError(String errorMessage);
    }



    public NutritionAPI() {
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    public void getNutritionData(String ingredient, double weightInGrams, NutritionCallback callback) {
        HttpUrl url = HttpUrl.parse(BASE_URL).newBuilder()
                .addQueryParameter("api_key", API_KEY)
                .addQueryParameter("query", ingredient)
                .addQueryParameter("pageSize", "1") // Get only the best match
                .addQueryParameter("dataType", "SR Legacy") // Standard Reference data
                .build();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("Network error: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onError("API error: " + response.code() + " " + response.message());
                    return;
                }

                try {
                    String responseBody = response.body().string();
                    JSONObject jsonResponse = new JSONObject(responseBody);

                    if (!jsonResponse.has("foods") || jsonResponse.getJSONArray("foods").length() == 0) {
                        callback.onError("No nutrition data found for " + ingredient);
                        return;
                    }

                    // Get the first result
                    JSONObject food = jsonResponse.getJSONArray("foods").getJSONObject(0);
                    String foodName = food.getString("description");
                    JSONArray nutrients = food.getJSONArray("foodNutrients");

                    // Default values
                    double caloriesPer100g = 0;
                    double proteinPer100g = 0;
                    double fatPer100g = 0;
                    double carbsPer100g = 0;

                    // Extract nutrient values
                    for (int i = 0; i < nutrients.length(); i++) {
                        JSONObject nutrient = nutrients.getJSONObject(i);

                        // The structure can vary between API versions
                        String nutrientName = "";
                        double value = 0;

                        if (nutrient.has("nutrientName") && nutrient.has("value")) {
                            // Direct format
                            nutrientName = nutrient.getString("nutrientName");
                            value = nutrient.getDouble("value");
                        } else if (nutrient.has("nutrient")) {
                            // Nested format
                            JSONObject nutrientObj = nutrient.getJSONObject("nutrient");
                            if (nutrientObj.has("name")) {
                                nutrientName = nutrientObj.getString("name");
                            }
                            if (nutrient.has("amount")) {
                                value = nutrient.getDouble("amount");
                            }
                        }

                        // Match nutrients by name
                        if (nutrientName.contains("Energy") && (nutrientName.contains("kcal") || nutrientName.contains("KCAL"))) {
                            caloriesPer100g = value;
                        } else if (nutrientName.contains("Protein")) {
                            proteinPer100g = value;
                        } else if (nutrientName.contains("Total lipid") || nutrientName.equals("Fat")) {
                            fatPer100g = value;
                        } else if (nutrientName.contains("Carbohydrate")) {
                            carbsPer100g = value;
                        }
                    }

                    // Calculate nutrition based on requested weight
                    double calories = (caloriesPer100g * weightInGrams) / 100;
                    double protein = (proteinPer100g * weightInGrams) / 100;
                    double fat = (fatPer100g * weightInGrams) / 100;
                    double carbs = (carbsPer100g * weightInGrams) / 100;

                    // Create nutrition data object
                    FoodNutritionData foodData = new FoodNutritionData(
                            foodName,
                            weightInGrams,
                            calories,
                            protein,
                            fat,
                            carbs
                    );

                    callback.onSuccess(foodData);

                } catch (JSONException e) {
                    Log.e(TAG, "JSON parsing error", e);
                    callback.onError("Error parsing nutrition data: " + e.getMessage());
                }
            }
        });
    }

    // Data class to hold nutrition information
    public static class FoodNutritionData {
        private final String name;
        private final double weightInGrams;
        private final double calories;
        private final double protein;
        private final double fat;
        private final double carbs;

        public FoodNutritionData(String name, double weightInGrams, double calories,
                                 double protein, double fat, double carbs) {
            this.name = name;
            this.weightInGrams = weightInGrams;
            this.calories = calories;
            this.protein = protein;
            this.fat = fat;
            this.carbs = carbs;
        }

        public String getName() {
            return name;
        }

        public double getWeightInGrams() {
            return weightInGrams;
        }

        public double getCalories() {
            return calories;
        }

        public double getProtein() {
            return protein;
        }

        public double getFat() {
            return fat;
        }

        public double getCarbs() {
            return carbs;
        }

        @Override
        public String toString() {
            return String.format("%s (%.1fg):\nCalories: %.1f kcal\nProtein: %.1fg\nFat: %.1fg\nCarbs: %.1fg",
                    name, weightInGrams, calories, protein, fat, carbs);
        }
    }
}