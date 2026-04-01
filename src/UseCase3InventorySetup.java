// ============================================================
//  File        : UseCase3InventorySetup.java
//  Description : Use Case 3 - Centralized Room Inventory Management
//  Author      : Santhosh
//  Version     : 3.0
// ============================================================

import java.util.HashMap;
import java.util.Map;

// -------------------------------------------------------
// Abstract Base Class : Room (carried forward from UC2)
// -------------------------------------------------------

/**
 * Room - Abstract base class representing a hotel room.
 *
 * <p>Defines common attributes and behavior shared by all room types.
 * Enforces a consistent structure through abstraction and encapsulation.</p>
 *
 * @author Santhosh
 * @version 3.0
 */
abstract class Room {

    private int    roomNumber;
    private String roomType;
    private int    numberOfBeds;
    private double roomSize;
    private double pricePerNight;

    /**
     * Constructs a Room with the specified attributes.
     *
     * @param roomNumber    unique room identifier
     * @param roomType      type/category of the room
     * @param numberOfBeds  number of beds available
     * @param roomSize      size of the room in sq ft
     * @param pricePerNight cost per night in INR
     */
    public Room(int roomNumber, String roomType, int numberOfBeds,
                double roomSize, double pricePerNight) {
        this.roomNumber    = roomNumber;
        this.roomType      = roomType;
        this.numberOfBeds  = numberOfBeds;
        this.roomSize      = roomSize;
        this.pricePerNight = pricePerNight;
    }

    public int    getRoomNumber()    { return roomNumber;    }
    public String getRoomType()      { return roomType;      }
    public int    getNumberOfBeds()  { return numberOfBeds;  }
    public double getRoomSize()      { return roomSize;      }
    public double getPricePerNight() { return pricePerNight; }

    /** Abstract method - each subclass must implement its own display. */
    public abstract void displayRoomDetails();
}


// -------------------------------------------------------
// Concrete Class : SingleRoom
// -------------------------------------------------------

/**
 * SingleRoom - Represents a single occupancy hotel room.
 *
 * @author Santhosh
 * @version 3.0
 */
class SingleRoom extends Room {

    /**
     * Constructs a SingleRoom: 1 bed, 180 sq ft, INR 1500/night.
     *
     * @param roomNumber unique room identifier
     */
    public SingleRoom(int roomNumber) {
        super(roomNumber, "Single Room", 1, 180.0, 1500.0);
    }

    @Override
    public void displayRoomDetails() {
        System.out.println("  Room Number    : " + getRoomNumber());
        System.out.println("  Room Type      : " + getRoomType());
        System.out.println("  Number of Beds : " + getNumberOfBeds());
        System.out.println("  Room Size      : " + getRoomSize() + " sq ft");
        System.out.println("  Price/Night    : INR " + getPricePerNight());
    }
}


// -------------------------------------------------------
// Concrete Class : DoubleRoom
// -------------------------------------------------------

/**
 * DoubleRoom - Represents a double occupancy hotel room.
 *
 * @author Santhosh
 * @version 3.0
 */
class DoubleRoom extends Room {

    /**
     * Constructs a DoubleRoom: 2 beds, 280 sq ft, INR 2500/night.
     *
     * @param roomNumber unique room identifier
     */
    public DoubleRoom(int roomNumber) {
        super(roomNumber, "Double Room", 2, 280.0, 2500.0);
    }

    @Override
    public void displayRoomDetails() {
        System.out.println("  Room Number    : " + getRoomNumber());
        System.out.println("  Room Type      : " + getRoomType());
        System.out.println("  Number of Beds : " + getNumberOfBeds());
        System.out.println("  Room Size      : " + getRoomSize() + " sq ft");
        System.out.println("  Price/Night    : INR " + getPricePerNight());
    }
}


// -------------------------------------------------------
// Concrete Class : SuiteRoom
// -------------------------------------------------------

/**
 * SuiteRoom - Represents a luxury suite hotel room.
 *
 * @author Santhosh
 * @version 3.0
 */
class SuiteRoom extends Room {

    /**
     * Constructs a SuiteRoom: 3 beds, 500 sq ft, INR 5000/night.
     *
     * @param roomNumber unique room identifier
     */
    public SuiteRoom(int roomNumber) {
        super(roomNumber, "Suite Room", 3, 500.0, 5000.0);
    }

    @Override
    public void displayRoomDetails() {
        System.out.println("  Room Number    : " + getRoomNumber());
        System.out.println("  Room Type      : " + getRoomType());
        System.out.println("  Number of Beds : " + getNumberOfBeds());
        System.out.println("  Room Size      : " + getRoomSize() + " sq ft");
        System.out.println("  Price/Night    : INR " + getPricePerNight());
    }
}


// -------------------------------------------------------
// Inventory Class : RoomInventory
// -------------------------------------------------------

/**
 * RoomInventory - Centralized inventory manager for hotel room availability.
 *
 * <p>Replaces scattered boolean variables from Use Case 2 with a single
 * HashMap that acts as the single source of truth for all room availability.
 * Provides O(1) lookup, update, and retrieval operations.</p>
 *
 * <p>Separation of concerns is maintained: RoomInventory manages how many
 * rooms are available, while Room objects manage what a room is.</p>
 *
 * @author Santhosh
 * @version 3.0
 */
class RoomInventory {

    /**
     * Centralized HashMap storing room type as key and available count as value.
     * Provides constant-time access and scalable state management.
     */
    private HashMap<String, Integer> inventory;

    /**
     * Constructs a RoomInventory and initializes availability
     * for all predefined room types.
     */
    public RoomInventory() {
        inventory = new HashMap<>();
        inventory.put("Single Room", 5);
        inventory.put("Double Room", 3);
        inventory.put("Suite Room",  2);
    }

    /**
     * Returns the available count for a given room type.
     * Provides O(1) lookup from the centralized HashMap.
     *
     * @param roomType the type of room to query
     * @return number of available rooms, or 0 if type not found
     */
    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    /**
     * Updates the availability count for a given room type.
     * Supports both increment (check-out) and decrement (check-in).
     *
     * @param roomType the type of room to update
     * @param count    new availability count to set
     */
    public void updateAvailability(String roomType, int count) {
        if (inventory.containsKey(roomType)) {
            inventory.put(roomType, count);
            System.out.println("  [Updated] " + roomType + " availability set to " + count);
        } else {
            System.out.println("  [Error] Room type '" + roomType + "' not found in inventory.");
        }
    }

    /**
     * Displays the complete current inventory state to the console.
     * Iterates over all entries in the HashMap.
     */
    public void displayInventory() {
        System.out.println("\n--------------------------------------------");
        System.out.println("  CURRENT ROOM INVENTORY");
        System.out.println("--------------------------------------------");
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.printf("  %-15s : %d room(s) available%n",
                    entry.getKey(), entry.getValue());
        }
        System.out.println("--------------------------------------------");
    }
}


// -------------------------------------------------------
// Main Entry Point : UseCase3InventorySetup
// -------------------------------------------------------

/**
 * UseCase3InventorySetup - Application entry point for Use Case 3.
 *
 * <p>Demonstrates centralized inventory management using HashMap.
 * Replaces the scattered boolean variables from Use Case 2 with a
 * single source of truth managed by the RoomInventory class.</p>
 *
 * @author Santhosh
 * @version 3.0
 */
public class UseCase3InventorySetup {

    /**
     * Main method - initializes inventory, demonstrates availability
     * retrieval, performs controlled updates, and displays inventory state.
     *
     * @param args command-line arguments (not used in this use case)
     */
    public static void main(String[] args) {

        System.out.println("============================================");
        System.out.println("      Hotel Booking System  v3.0           ");
        System.out.println("  Use Case 3 : Inventory Setup             ");
        System.out.println("============================================");

        // Initialize the centralized inventory
        RoomInventory roomInventory = new RoomInventory();

        // Display initial inventory state
        System.out.println("\n  Inventory initialized successfully.");
        roomInventory.displayInventory();

        // Demonstrate O(1) availability lookup
        System.out.println("\n--------------------------------------------");
        System.out.println("  AVAILABILITY LOOKUP");
        System.out.println("--------------------------------------------");
        System.out.println("  Single Room : " + roomInventory.getAvailability("Single Room") + " available");
        System.out.println("  Double Room : " + roomInventory.getAvailability("Double Room") + " available");
        System.out.println("  Suite Room  : " + roomInventory.getAvailability("Suite Room")  + " available");

        // Demonstrate controlled availability update
        System.out.println("\n--------------------------------------------");
        System.out.println("  AVAILABILITY UPDATE");
        System.out.println("--------------------------------------------");
        roomInventory.updateAvailability("Single Room", 4);
        roomInventory.updateAvailability("Suite Room",  1);

        // Display updated inventory state
        roomInventory.displayInventory();

        System.out.println("\n============================================");
        System.out.println("  Inventory Setup Complete.");
        System.out.println("============================================");
    }
}
