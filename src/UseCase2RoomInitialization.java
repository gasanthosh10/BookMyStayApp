// ============================================================
//  File        : UseCase2RoomInitialization.java
//  Description : Use Case 2 - Basic Room Types & Static Availability
//  Author      : Santhosh
//  Version     : 2.0
// ============================================================

// -------------------------------------------------------
// Abstract Base Class : Room
// -------------------------------------------------------

/**
 * Room - Abstract base class representing a hotel room.
 *
 * <p>Defines common attributes and behavior shared by all room types.
 * Enforces a consistent structure through abstraction and encapsulation.</p>
 *
 * @author Santhosh
 * @version 2.0
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
 * @version 2.0
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
 * @version 2.0
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
 * @version 2.0
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
// Main Entry Point : UseCase2RoomInitialization
// -------------------------------------------------------

/**
 * UseCase2RoomInitialization - Application entry point for Use Case 2.
 *
 * <p>Demonstrates object modeling through inheritance and abstraction.
 * Room availability is stored using simple boolean variables to highlight
 * limitations of static state before data structures are introduced.</p>
 *
 * @author Santhosh
 * @version 2.0
 */
public class UseCase2RoomInitialization {

    /**
     * Main method - initializes rooms, stores static availability,
     * and displays room details to the console.
     *
     * @param args command-line arguments (not used in this use case)
     */
    public static void main(String[] args) {

        System.out.println("============================================");
        System.out.println("      Hotel Booking System  v2.0           ");
        System.out.println("   Use Case 2 : Room Initialization        ");
        System.out.println("============================================");

        // Polymorphism: Room reference holding concrete objects
        Room singleRoom = new SingleRoom(101);
        Room doubleRoom = new DoubleRoom(201);
        Room suiteRoom  = new SuiteRoom(301);

        // Static availability using simple boolean variables
        boolean isSingleRoomAvailable = true;
        boolean isDoubleRoomAvailable = true;
        boolean isSuiteRoomAvailable  = false;

        // ---- Single Room ----
        System.out.println("\n--------------------------------------------");
        System.out.println("  SINGLE ROOM DETAILS");
        System.out.println("--------------------------------------------");
        singleRoom.displayRoomDetails();
        System.out.println("  Availability   : " +
                (isSingleRoomAvailable ? "Available" : "Not Available"));

        // ---- Double Room ----
        System.out.println("\n--------------------------------------------");
        System.out.println("  DOUBLE ROOM DETAILS");
        System.out.println("--------------------------------------------");
        doubleRoom.displayRoomDetails();
        System.out.println("  Availability   : " +
                (isDoubleRoomAvailable ? "Available" : "Not Available"));

        // ---- Suite Room ----
        System.out.println("\n--------------------------------------------");
        System.out.println("  SUITE ROOM DETAILS");
        System.out.println("--------------------------------------------");
        suiteRoom.displayRoomDetails();
        System.out.println("  Availability   : " +
                (isSuiteRoomAvailable ? "Available" : "Not Available"));

        System.out.println("\n============================================");
        System.out.println("  Room Initialization Complete.");
        System.out.println("============================================");
    }
}
