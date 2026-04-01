// ============================================================
//  File        : UseCase4RoomSearch.java
//  Description : Use Case 4 - Room Search & Availability Check
//  Author      : Santhosh
//  Version     : 4.0
// ============================================================

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// -------------------------------------------------------
// Abstract Base Class : Room (carried forward from UC3)
// -------------------------------------------------------

/**
 * Room - Abstract base class representing a hotel room.
 *
 * <p>Defines common attributes and behavior shared by all room types.
 * Enforces a consistent structure through abstraction and encapsulation.</p>
 *
 * @author Santhosh
 * @version 4.0
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
 * @version 4.0
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
 * @version 4.0
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
 * @version 4.0
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
// Inventory Class : RoomInventory (carried forward from UC3)
// -------------------------------------------------------

/**
 * RoomInventory - Centralized inventory manager for hotel room availability.
 *
 * <p>HashMap acts as the single source of truth for all room availability.
 * Provides O(1) lookup, update, and retrieval operations.</p>
 *
 * @author Santhosh
 * @version 4.0
 */
class RoomInventory {

    /**
     * Centralized HashMap: room type → available count.
     */
    private HashMap<String, Integer> inventory;

    /**
     * Constructs a RoomInventory and initializes default availability.
     */
    public RoomInventory() {
        inventory = new HashMap<>();
        inventory.put("Single Room", 5);
        inventory.put("Double Room", 3);
        inventory.put("Suite Room",  0);  // intentionally 0 to demo filtering
    }

    /**
     * Read-only retrieval of availability for a given room type.
     * Does NOT modify inventory state.
     *
     * @param roomType the type of room to query
     * @return number of available rooms, or 0 if type not found
     */
    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    /**
     * Updates the availability count for a given room type.
     *
     * @param roomType the type of room to update
     * @param count    new availability count to set
     */
    public void updateAvailability(String roomType, int count) {
        if (inventory.containsKey(roomType)) {
            inventory.put(roomType, count);
        } else {
            System.out.println("  [Error] Room type '" + roomType + "' not found.");
        }
    }

    /**
     * Returns a read-only view of all inventory entries.
     * Used by SearchService to iterate without mutating state.
     *
     * @return entrySet of the inventory map
     */
    public Map<String, Integer> getAllInventory() {
        return inventory;
    }
}


// -------------------------------------------------------
// Service Class : RoomSearchService
// -------------------------------------------------------

/**
 * RoomSearchService - Handles read-only room search and availability check.
 *
 * <p>Isolates search functionality from inventory mutation and booking logic.
 * Applies validation to filter out unavailable room types, ensuring guests
 * only see actionable options. System state is never modified here.</p>
 *
 * @author Santhosh
 * @version 4.0
 */
class RoomSearchService {

    /** Reference to the centralized inventory — accessed read-only. */
    private RoomInventory roomInventory;

    /** Domain model objects for room details and pricing. */
    private List<Room> roomCatalog;

    /**
     * Constructs the search service with inventory and room catalog.
     *
     * @param roomInventory the centralized inventory to read from
     * @param roomCatalog   list of room domain objects for detail lookup
     */
    public RoomSearchService(RoomInventory roomInventory, List<Room> roomCatalog) {
        this.roomInventory = roomInventory;
        this.roomCatalog   = roomCatalog;
    }

    /**
     * Searches and displays all available rooms.
     *
     * <p>Performs read-only access to inventory. Filters out room types
     * with zero availability using defensive validation logic.
     * Domain model objects supply pricing and descriptive information.</p>
     */
    public void searchAvailableRooms() {

        System.out.println("\n============================================");
        System.out.println("      AVAILABLE ROOMS FOR BOOKING          ");
        System.out.println("============================================");

        boolean anyAvailable = false;

        for (Room room : roomCatalog) {

            String roomType    = room.getRoomType();
            int    availability = roomInventory.getAvailability(roomType);

            // Validation: skip rooms with zero or negative availability
            if (availability <= 0) {
                continue;
            }

            anyAvailable = true;
            System.out.println("\n--------------------------------------------");
            room.displayRoomDetails();
            System.out.println("  Rooms Available: " + availability);
            System.out.println("--------------------------------------------");
        }

        // Defensive: inform guest if no rooms are available at all
        if (!anyAvailable) {
            System.out.println("\n  No rooms are currently available.");
            System.out.println("  Please check back later.");
        }

        System.out.println("\n  [Note] System state has NOT been modified.");
        System.out.println("============================================");
    }

    /**
     * Checks and displays availability for a specific room type.
     * Read-only operation — does not modify inventory.
     *
     * @param roomType the room type to check
     */
    public void checkAvailability(String roomType) {
        int availability = roomInventory.getAvailability(roomType);
        System.out.println("\n--------------------------------------------");
        System.out.println("  Availability Check : " + roomType);
        System.out.println("--------------------------------------------");
        if (availability > 0) {
            System.out.println("  Status    : Available");
            System.out.println("  Count     : " + availability + " room(s)");
        } else {
            System.out.println("  Status    : Not Available");
            System.out.println("  Count     : 0 room(s)");
        }
        System.out.println("--------------------------------------------");
    }
}


// -------------------------------------------------------
// Main Entry Point : UseCase4RoomSearch
// -------------------------------------------------------

/**
 * UseCase4RoomSearch - Application entry point for Use Case 4.
 *
 * <p>Demonstrates read-only room search with defensive validation.
 * Separates search concerns from inventory mutation introduced in UC3.
 * Only available room types are displayed to the guest.</p>
 *
 * @author Santhosh
 * @version 4.0
 */
public class UseCase4RoomSearch {

    /**
     * Main method - sets up inventory, initializes search service,
     * and demonstrates both full search and specific availability check.
     *
     * @param args command-line arguments (not used in this use case)
     */
    public static void main(String[] args) {

        System.out.println("============================================");
        System.out.println("      Hotel Booking System  v4.0           ");
        System.out.println("   Use Case 4 : Room Search                ");
        System.out.println("============================================");

        // Initialize centralized inventory (Suite Room = 0 to demo filtering)
        RoomInventory inventory = new RoomInventory();

        // Build room catalog using domain model objects (polymorphism)
        List<Room> roomCatalog = new ArrayList<>();
        roomCatalog.add(new SingleRoom(101));
        roomCatalog.add(new DoubleRoom(201));
        roomCatalog.add(new SuiteRoom(301));

        // Initialize search service — read-only access to inventory
        RoomSearchService searchService = new RoomSearchService(inventory, roomCatalog);

        // Guest initiates a full room search
        System.out.println("\n  Guest initiating room search...");
        searchService.searchAvailableRooms();

        // Guest checks availability for a specific room type
        System.out.println("\n  Guest checking specific room type...");
        searchService.checkAvailability("Double Room");
        searchService.checkAvailability("Suite Room");

        System.out.println("\n============================================");
        System.out.println("  Room Search Complete.");
        System.out.println("============================================");
    }
}
