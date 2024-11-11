package com.example.recipegeneratoraibot2.api;

import com.example.recipegeneratoraibot2.dtos.MyResponse;
import com.example.recipegeneratoraibot2.service.OpenAiService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class handles generating a recipe via the ChatGPT API, but is IP-rate limited.
 */
@RestController
@RequestMapping("/api/v1/recipelimited")
@CrossOrigin(origins = "*")
public class RecipeRateLimitedController {

  @Value("${app.bucket_capacity}")
  private int BUCKET_CAPACITY;

  @Value("${app.refill_amount}")
  private int REFILL_AMOUNT;

  @Value("${app.refill_time}")
  private int REFILL_TIME;

  private final OpenAiService service;
  private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

  /**
   * Constructor for the RecipeRateLimitedController.
   * @param service The OpenAiService used to generate recipes.
   */
  public RecipeRateLimitedController(OpenAiService service) {
    this.service = service;
  }

  /**
   * Creates a new bucket for handling IP-rate limitations.
   * @return A new bucket instance for rate limiting
   */
  private Bucket createNewBucket() {
    Bandwidth limit = Bandwidth.classic(BUCKET_CAPACITY, Refill.greedy(REFILL_AMOUNT, Duration.ofMinutes(REFILL_TIME)));
    return Bucket.builder().addLimit(limit).build();
  }

  /**
   * Returns an existing bucket by IP key or creates a new one.
   * @param key The IP address of the client
   * @return A bucket instance for rate limiting
   */
  private Bucket getBucket(String key) {
    return buckets.computeIfAbsent(key, k -> createNewBucket());
  }

  /**
   * Handles the request for generating a recipe, with rate limiting applied.
   * @param ingredients The ingredients input that ChatGPT uses to generate a recipe.
   * @param request The current HTTP request used to get the client IP
   * @return The generated recipe from ChatGPT
   */
  @GetMapping("/generate")
  public MyResponse getRecipeWithRateLimited(@RequestParam String ingredients, HttpServletRequest request) {

    // Get the IP of the client.
    String ip = request.getRemoteAddr();
    // Get or create the bucket for the given IP/key.
    Bucket bucket = getBucket(ip);

    // Check if the request adheres to IP-rate limitations.
    if (!bucket.tryConsume(1)) {
      // If not, return "Too many requests" error.
      throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many requests, try again later");
    }

    // Otherwise, request a recipe and return the response.
    return service.generateRecipe(ingredients, RecipeController.SYSTEM_MESSAGE);
  }
}
