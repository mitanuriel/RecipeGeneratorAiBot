package com.example.recipegeneratoraibot2.api;

import com.example.recipegeneratoraibot2.dtos.MyResponse;
import com.example.recipegeneratoraibot2.service.OpenAiService;
import org.springframework.web.bind.annotation.*;

/**
 * This class handles fetching a recipe via the ChatGPT API
 */
@RestController
@RequestMapping("/api/v1/recipe")
@CrossOrigin(origins = "*")
public class RecipeController {

  private final OpenAiService service;

  /**
   * This contains the message to the ChatGPT API, directing it to generate recipes based on ingredients provided by the user.
   */
  final static String SYSTEM_MESSAGE = "You are a helpful assistant that simple recipes based on the ingredients provided."+
          "If the user provides a list of ingredients, create a recipe that uses those ingredients.";

  /**
   *  Constructor for the RecipeController.
   * @param service the OpenAiService to interact with OpenAI API.
   */
  public RecipeController(OpenAiService service) {
    this.service = service;
  }

  /**
   * Handles the request from the browser client to generate a recipe.
   * @param ingredients contains the ingredients that ChatGPT uses to create a recipe
   * @return the response from ChatGPT.
   */
  @GetMapping("/generate")
  public MyResponse getRecipe(@RequestParam String ingredients) {

    return service.generateRecipe(ingredients,SYSTEM_MESSAGE);
  }
}
