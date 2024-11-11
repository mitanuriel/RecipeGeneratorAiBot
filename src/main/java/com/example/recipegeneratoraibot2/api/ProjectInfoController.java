package com.example.recipegeneratoraibot2.api;



import com.example.recipegeneratoraibot2.dtos.MyResponse;
import com.example.recipegeneratoraibot2.service.OpenAiService;
import org.springframework.web.bind.annotation.*;

/**
 * This class handles selfmade replies that the ChatGPT AI has not been trained with.
 */
@RestController
@RequestMapping("/api/v1/projectinfo")
@CrossOrigin(origins = "*")
public class ProjectInfoController {

  private final String SYSTEM_MESSAGE="You are a helpful assistant. When using any of the following links make your response as short as possible."
          +"If asked about who created this recipe generator, respond with: 'Created by https://www.linkedin.com/in/elina-kamby-800167257/";


  OpenAiService openAiService;

  /**
   * The controller called from the frontend client.
   * @param openAiService
   */
  public ProjectInfoController(OpenAiService openAiService) {
    this.openAiService = openAiService;
  }

  /**
   * Handles the request from the browser client.
   * @param question to handle
   * @return the response from ChatGPT.
   */
  @GetMapping
  public MyResponse getProjectInfo(@RequestParam String question){
    return openAiService.generateRecipe(question,SYSTEM_MESSAGE);
  }
}
