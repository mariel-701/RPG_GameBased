package items;

/**
 * Custom exception thrown when attempting to use an item from an empty inventory.
 */
public class EmptyInventoryException extends Exception {

    public EmptyInventoryException() {
        super("Inventory is empty! You have no items to use.");
    }

    public EmptyInventoryException(String message) {
        super(message);
    }
}
