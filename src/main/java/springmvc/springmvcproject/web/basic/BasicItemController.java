package springmvc.springmvcproject.web.basic;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import springmvc.springmvcproject.domain.item.Item;
import springmvc.springmvcproject.domain.item.ItemRepository;

import java.util.List;

/**
 * Controller class for handling basic item-related HTTP requests
 *
 * @Controller: Indicates that this class serves as a Spring MVC Controller
 *              Controllers are responsible for handling web requests and returning appropriate responses
 *
 * @RequestMapping("/basic/items"): Maps all handler methods in this controller to URLs starting with "/basic/items"
 *                                 This is a base path for all endpoints in this controller
 *
 * @RequiredArgsConstructor: Lombok annotation that generates a constructor with required arguments
 *                          for all final fields, making dependency injection cleaner
 */
@Controller
@RequestMapping("/basic/items")
@RequiredArgsConstructor
public class BasicItemController {

    /**
     * Final field for dependency injection using constructor injection pattern
     * Constructor injection is recommended over field injection (@Autowired on fields)
     * because it enforces mandatory dependencies and improves testability
     */
    private final ItemRepository itemRepository;

    /**
     * Handles GET requests for listing all items.
     * Retrieves all items from the repository and adds them to the model.
     *
     * @GetMapping: Annotation that maps HTTP GET requests to this handler method
     * @param model: Spring's Model interface used to pass data from controller to view
     * @return String: Returns the view name to be rendered (maps to a template file)
     *
     * This method retrieves all items from the repository and adds them to the model
     * so they can be accessed in the view template
     */
    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "basic/items";
    }

    /**
     * Handles GET requests for a specific item by its ID.
     * Retrieves the item from the repository and adds it to the model.
     *
     * @PathVariable: Annotation that indicates the itemId parameter should be bound
     *                to a URI template variable
     * @param itemId: The ID of the item to be retrieved
     * @param model: Spring's Model interface used to pass data from controller to view
     * @return String: Returns the view name to be rendered (maps to a template file)
     *
     * This method retrieves a specific item based on the provided itemId
     * and makes it available to the view template
     */
    @GetMapping("{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "basic/item";
    }

    /**
     * Initialization method that runs after dependency injection is complete
     *
     * @PostConstruct: Java annotation indicating that this method should be executed
     *                after the bean has been constructed and dependencies injected
     *
     * This method creates sample data for testing purposes
     * It's useful during development to have some initial data without setting up a database
     */
    @PostConstruct
    public void init() {
        itemRepository.save(new Item("itemA", 10000, 10));
        itemRepository.save(new Item("itemB", 20000, 20));
    }
}
