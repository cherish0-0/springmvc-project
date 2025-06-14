package springmvc.springmvcproject.web.basic;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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
     * Handles GET requests for displaying the item creation form.
     *
     * @GetMapping("/add"): Maps HTTP GET requests to "/basic/items/add" to this handler method
     *
     * This method does not pass any model attributes as it's just displaying an empty form.
     * The form will POST its data to a separate endpoint that handles the item creation.
     */
    @GetMapping("/add")
    public String addForm() {
        return "basic/addForm";
    }

    /**
     * Version 1: Process item addition using individual request parameters
     *
     * @RequestParam: Annotation to bind HTTP request parameters to method parameters
     *
     * This method manually extracts each parameter from the request,
     * creates a new Item object, sets its properties individually,
     * saves it to the repository, and then adds it to the model.
     * This approach is more verbose(장황한) but gives explicit(명백한) control over parameter handling.
     */
    // @PostMapping("/add")
    public String addItemV1(@RequestParam String itemName,
                       @RequestParam int price,
                       @RequestParam Integer quantity,
                       Model model) {
        Item item = new Item();
        item.setItemName(itemName);
        item.setPrice(price);
        item.setQuantity(quantity);

        itemRepository.save(item);

        model.addAttribute("item", item);

        return "basic/item";
    }

    /**
     * Version 2: Process item addition using @ModelAttribute with explicit name
     *
     * @ModelAttribute("item"): Annotation that binds request parameters to a model object
     *                        and also adds that object to the model with the specified name ("item")
     *
     * This method uses @ModelAttribute with an explicit name ("item") which:
     * 1. Creates an Item object
     * 2. Binds request parameters to the Item object properties
     * 3. Adds the Item to the model with the name "item"
     * This removes the need for manual parameter binding and model addition.
     */
    // @PostMapping("/add")
    public String addItemV2(@ModelAttribute("item") Item item) {

        itemRepository.save(item);

        // model.addAttribute("item", item); // No need to add item to model, @ModelAttribute does it automatically

        return "basic/item";
    }

    /**
     * Version 3: Process item addition using @ModelAttribute without explicit name
     *
     * @ModelAttribute: Annotation without an explicit name
     *
     * When @ModelAttribute is used without specifying a name, Spring automatically
     * adds the object to the model using the class name with the first letter lowercased. (Item -> "item")
     */
    // @PostMapping("/add")
    public String addItemV3(@ModelAttribute Item item) {

        itemRepository.save(item);

        return "basic/item";
    }

    /**
     * Version 4: Process item addition with implicit @ModelAttribute
     *
     * This method doesn't use the @ModelAttribute annotation entirely.
     * Spring MVC automatically applies @ModelAttribute for any parameter
     * that is not a simple type (like String, int, etc.) and is not
     * annotated with other parameter annotations (like @RequestParam).
     *
     * This is the most compact approach but may be less clear to developers
     * unfamiliar with Spring MVC's parameter handling conventions.
     */
    //  @PostMapping("/add")
    public String addItemV4(Item item) {

        itemRepository.save(item);

        return "basic/item";
    }

    /**
     * Version 5: Process item addition with redirect to prevent duplicate form submission
     *
     * This method implements the PRG (Post-Redirect-Get) pattern to solve the form resubmission problem:
     * 1. The browser POSTs form data to create an item
     * 2. The server processes the data and saves the item
     * 3. The server responds with a redirect (302 Found) instead of returning the view directly
     * 4. The browser follows the redirect with a GET request to the item's detail page
     *
     * This pattern prevents duplicate submissions if the user refreshes the page,
     * as they'll refresh the GET result page rather than resubmitting the POST request.
     */
    @PostMapping("/add")
    public String addItemV5(Item item) {

        itemRepository.save(item);

        return "redirect:/basic/items/" + item.getId();
    }

    /**
     * Handles GET requests for displaying the item edit form.
     *
     * @GetMapping("/{itemId}/edit"): Maps HTTP GET requests to "/basic/items/{itemId}/edit" to this handler method
     * @PathVariable Long itemId: Extracts the itemId from the URL path
     *
     * This method retrieves the item with the specified ID from the repository,
     * adds it to the model so it can be fiooed with existing values in the form,
     * and returns the view name for the edit form template.
     */
    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "basic/editForm";
    }

    /**
     * Handles POST requests for processing the item edit form submission.
     *
     * @ModelAttribute Item item: Binds form data to an Item object and adds it to the model
     *
     * This method updates the existing item in the repository with the edited values from the form.
     * After successfully updating the item, it redirects to the item detail page using a PRG
     * (Post/Redirect/Get) pattern to prevent duplicate form submissions.
     *
     * The {itemId} in the redirect URL is automatically replaced with the actual itemId value
     * by Spring MVC, allowing for a clean way to redirect to the specific item's detail page.
     */
    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/basic/items/{itemId}";
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
