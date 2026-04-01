// ============================================================
//  File        : UseCase7AddOnServiceSelection.java
//  Description : Use Case 7 - Add-On Service Selection
//  Author      : Santhosh
//  Version     : 7.0
// ============================================================

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

// -------------------------------------------------------
// Abstract Base Class : Room (carried forward)
// -------------------------------------------------------

/**
 * Room - Abstract base class representing a hotel room.
 *
 * <p>Defines common attributes and behavior shared by all room types.
 * Enforces a consistent structure through abstraction and encapsulation.</p>
 *
 * @author Santhosh
 * @version 7.0
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
 * @version 7.0
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
 * @version 7.0
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
 * @version 7.0
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
// Inventory Class : RoomInventory (carried forward)
// -------------------------------------------------------

/**
 * RoomInventory - Centralized inventory manager for hotel room availability.
 *
 * <p>HashMap acts as the single source of truth for room availability.
 * Add-on service selection does NOT modify inventory state.</p>
 *
 * @author Santhosh
 * @version 7.0
 */
class RoomInventory {

    private HashMap<String, Integer> inventory;

    /**
     * Constructs a RoomInventory and initializes default availability.
     */
    public RoomInventory() {
        inventory = new HashMap<>();
        inventory.put("Single Room", 3);
        inventory.put("Double Room", 2);
        inventory.put("Suite Room",  1);
    }

    /**
     * Read-only retrieval of availability for a given room type.
     *
     * @param roomType the type of room to query
     * @return number of available rooms, or 0 if not found
     */
    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    /**
     * Decrements availability by 1 after a successful allocation.
     *
     * @param roomType the room type to decrement
     */
    public void decrementAvailability(String roomType) {
        int current = inventory.getOrDefault(roomType, 0);
        if (current > 0) {
            inventory.put(roomType, current - 1);
        }
    }
}


// -------------------------------------------------------
// Domain Class : Reservation (carried forward)
// -------------------------------------------------------

/**
 * Reservation - Represents a guest's confirmed booking.
 *
 * <p>Stores guest intent and booking details. Used as the key
 * entity to which add-on services are associated.</p>
 *
 * @author Santhosh
 * @version 7.0
 */
class Reservation {

    private String reservationId;
    private String guestName;
    private String requestedRoomType;
    private int    numberOfNights;
    private String assignedRoomId;

    /**
     * Constructs a Reservation with specified guest and booking details.
     *
     * @param reservationId     unique request ID
     * @param guestName         name of the guest
     * @param requestedRoomType room type the guest wants
     * @param numberOfNights    intended duration of stay
     */
    public Reservation(String reservationId, String guestName,
                       String requestedRoomType, int numberOfNights) {
        this.reservationId      = reservationId;
        this.guestName          = guestName;
        this.requestedRoomType  = requestedRoomType;
        this.numberOfNights     = numberOfNights;
        this.assignedRoomId     = null;
    }

    public String getReservationId()     { return reservationId;     }
    public String getGuestName()         { return guestName;         }
    public String getRequestedRoomType() { return requestedRoomType; }
    public int    getNumberOfNights()    { return numberOfNights;    }
    public String getAssignedRoomId()    { return assignedRoomId;    }

    /**
     * Sets the assigned room ID after successful allocation.
     *
     * @param roomId the unique room ID assigned to this reservation
     */
    public void setAssignedRoomId(String roomId) {
        this.assignedRoomId = roomId;
    }

    /**
     * Displays reservation details to the console.
     */
    public void displayReservationDetails() {
        System.out.println("  Reservation ID : " + reservationId);
        System.out.println("  Guest Name     : " + guestName);
        System.out.println("  Room Type      : " + requestedRoomType);
        System.out.println("  Assigned Room  : " + (assignedRoomId != null ? assignedRoomId : "Pending"));
        System.out.println("  Nights         : " + numberOfNights);
    }
}


// -------------------------------------------------------
// Domain Class : AddOnService  ← NEW IN UC7
// -------------------------------------------------------

/**
 * AddOnService - Represents a single optional service available to guests.
 *
 * <p>Encapsulates service name and cost. Services are composed with
 * reservations rather than inherited, following composition over inheritance.
 * This enables flexible, modular addition of new service types without
 * changing any existing booking or allocation logic.</p>
 *
 * <p>Available service types include:</p>
 * <ul>
 *   <li>Breakfast    - INR  500/day</li>
 *   <li>Airport Pick - INR  800/trip</li>
 *   <li>Spa Access   - INR 1200/session</li>
 *   <li>Laundry      - INR  300/service</li>
 *   <li>Room Service - INR  600/service</li>
 * </ul>
 *
 * @author Santhosh
 * @version 7.0
 */
class AddOnService {

    /** Name of the optional service */
    private String serviceName;

    /** Cost of the optional service in INR */
    private double serviceCost;

    /**
     * Constructs an AddOnService with a name and cost.
     *
     * @param serviceName name of the service
     * @param serviceCost cost of the service in INR
     */
    public AddOnService(String serviceName, double serviceCost) {
        this.serviceName = serviceName;
        this.serviceCost = serviceCost;
    }

    /**
     * Returns the service name.
     *
     * @return serviceName
     */
    public String getServiceName() { return serviceName; }

    /**
     * Returns the service cost.
     *
     * @return serviceCost
     */
    public double getServiceCost() { return serviceCost; }

    /**
     * Displays the service details to the console.
     */
    public void displayServiceDetails() {
        System.out.printf("    %-20s : INR %.2f%n", serviceName, serviceCost);
    }
}


// -------------------------------------------------------
// Manager Class : AddOnServiceManager  ← NEW IN UC7
// -------------------------------------------------------

/**
 * AddOnServiceManager - Manages association of add-on services with reservations.
 *
 * <p>Uses {@code Map<String, List<AddOnService>>} to model a one-to-many
 * relationship between a reservation ID and its selected services.</p>
 *
 * <p>Key design decisions:</p>
 * <ul>
 *   <li>Map key  = reservation ID (String) for O(1) lookup</li>
 *   <li>Map value = List of services (preserves insertion order)</li>
 *   <li>Core booking and inventory state are never modified here</li>
 *   <li>Cost aggregation is handled separately from room pricing</li>
 * </ul>
 *
 * @author Santhosh
 * @version 7.0
 */
class AddOnServiceManager {

    /**
     * One-to-many mapping: reservationId → list of selected services.
     * List preserves insertion order and allows multiple services per booking.
     */
    private Map<String, List<AddOnService>> serviceMap;

    /**
     * Constructs an empty AddOnServiceManager.
     */
    public AddOnServiceManager() {
        serviceMap = new HashMap<>();
    }

    /**
     * Attaches an add-on service to a reservation.
     * Creates a new service list if the reservation has no services yet.
     * Core booking and inventory state remain unchanged.
     *
     * @param reservationId the ID of the target reservation
     * @param service       the add-on service to attach
     */
    public void addService(String reservationId, AddOnService service) {
        // Initialize list for new reservation if not present
        serviceMap.putIfAbsent(reservationId, new ArrayList<>());
        serviceMap.get(reservationId).add(service);
        System.out.println("  [Added] " + service.getServiceName()
                + " → Reservation " + reservationId);
    }

    /**
     * Returns the list of services selected for a given reservation.
     * Returns an empty list if no services have been added.
     *
     * @param reservationId the reservation to query
     * @return list of AddOnService objects, or empty list
     */
    public List<AddOnService> getServicesForReservation(String reservationId) {
        return serviceMap.getOrDefault(reservationId, new ArrayList<>());
    }

    /**
     * Calculates the total additional cost of all services for a reservation.
     * Cost aggregation is separate from room pricing logic.
     *
     * @param reservationId the reservation to calculate for
     * @return total add-on cost in INR
     */
    public double calculateTotalAddOnCost(String reservationId) {
        double total = 0.0;
        for (AddOnService service : getServicesForReservation(reservationId)) {
            total += service.getServiceCost();
        }
        return total;
    }

    /**
     * Displays all selected services and cost breakdown for a reservation.
     *
     * @param reservationId the reservation to display services for
     * @param guestName     the guest's name for display context
     * @param nightCount    number of nights for total cost calculation
     * @param roomPrice     price per night for the reserved room type
     */
    public void displayServiceSummary(String reservationId, String guestName,
                                      int nightCount, double roomPrice) {

        List<AddOnService> services = getServicesForReservation(reservationId);

        System.out.println("\n--------------------------------------------");
        System.out.println("  RESERVATION : " + reservationId);
        System.out.println("  GUEST       : " + guestName);
        System.out.println("--------------------------------------------");

        if (services.isEmpty()) {
            System.out.println("  No add-on services selected.");
        } else {
            System.out.println("  Selected Add-On Services :");
            for (AddOnService s : services) {
                s.displayServiceDetails();
            }
        }

        double roomTotal   = roomPrice * nightCount;
        double addOnTotal  = calculateTotalAddOnCost(reservationId);
        double grandTotal  = roomTotal + addOnTotal;

        System.out.println("  ........................................");
        System.out.printf("  Room Cost   (%d nights x INR %.0f) : INR %.2f%n",
                nightCount, roomPrice, roomTotal);
        System.out.printf("  Add-On Cost                        : INR %.2f%n", addOnTotal);
        System.out.printf("  Grand Total                        : INR %.2f%n", grandTotal);
        System.out.println("--------------------------------------------");
    }

    /**
     * Displays a full summary of all reservations and their selected services.
     */
    public void displayAllServiceMappings() {
        System.out.println("\n============================================");
        System.out.println("  ALL RESERVATION ADD-ON MAPPINGS");
        System.out.println("============================================");

        if (serviceMap.isEmpty()) {
            System.out.println("  No add-on services have been selected.");
            return;
        }

        for (Map.Entry<String, List<AddOnService>> entry : serviceMap.entrySet()) {
            System.out.println("\n  Reservation : " + entry.getKey());
            System.out.println("  Services    :");
            for (AddOnService s : entry.getValue()) {
                s.displayServiceDetails();
            }
            System.out.printf("  Add-On Total: INR %.2f%n",
                    calculateTotalAddOnCost(entry.getKey()));
        }
        System.out.println("============================================");
    }
}


// -------------------------------------------------------
// Main Entry Point : UseCase7AddOnServiceSelection
// -------------------------------------------------------

/**
 * UseCase7AddOnServiceSelection - Application entry point for Use Case 7.
 *
 * <p>Demonstrates optional service selection using Map and List.
 * Add-on services are composed with reservations without modifying
 * core booking or inventory logic from previous use cases.</p>
 *
 * @author Santhosh
 * @version 7.0
 */
public class UseCase7AddOnServiceSelection {

    /**
     * Main method - creates confirmed reservations, attaches add-on services,
     * calculates costs, and displays a full service summary.
     *
     * @param args command-line arguments (not used in this use case)
     */
    public static void main(String[] args) {

        System.out.println("============================================");
        System.out.println("      Hotel Booking System  v7.0           ");
        System.out.println("  Use Case 7 : Add-On Service Selection    ");
        System.out.println("============================================");

        // Simulate confirmed reservations from Use Case 6
        Reservation res1 = new Reservation("RES-001", "Arun Kumar",   "Single Room", 2);
        Reservation res2 = new Reservation("RES-002", "Priya Sharma", "Double Room", 3);
        Reservation res3 = new Reservation("RES-003", "Vikram Nair",  "Suite Room",  1);

        res1.setAssignedRoomId("SINGLE-ROOM-100");
        res2.setAssignedRoomId("DOUBLE-ROOM-200");
        res3.setAssignedRoomId("SUITE-ROOM-300");

        // Display confirmed reservations
        System.out.println("\n  Confirmed Reservations :");
        System.out.println("--------------------------------------------");
        res1.displayReservationDetails();
        System.out.println("  ........................................");
        res2.displayReservationDetails();
        System.out.println("  ........................................");
        res3.displayReservationDetails();
        System.out.println("--------------------------------------------");

        // Define available add-on services
        AddOnService breakfast   = new AddOnService("Breakfast",     500.0);
        AddOnService airportPick = new AddOnService("Airport Pickup", 800.0);
        AddOnService spaAccess   = new AddOnService("Spa Access",    1200.0);
        AddOnService laundry     = new AddOnService("Laundry",        300.0);
        AddOnService roomService = new AddOnService("Room Service",   600.0);

        // Initialize the add-on service manager
        AddOnServiceManager serviceManager = new AddOnServiceManager();

        // Guest 1 selects services
        System.out.println("\n  Arun Kumar selecting services...");
        System.out.println("--------------------------------------------");
        serviceManager.addService("RES-001", breakfast);
        serviceManager.addService("RES-001", airportPick);

        // Guest 2 selects services
        System.out.println("\n  Priya Sharma selecting services...");
        System.out.println("--------------------------------------------");
        serviceManager.addService("RES-002", breakfast);
        serviceManager.addService("RES-002", spaAccess);
        serviceManager.addService("RES-002", laundry);

        // Guest 3 selects services
        System.out.println("\n  Vikram Nair selecting services...");
        System.out.println("--------------------------------------------");
        serviceManager.addService("RES-003", breakfast);
        serviceManager.addService("RES-003", airportPick);
        serviceManager.addService("RES-003", spaAccess);
        serviceManager.addService("RES-003", roomService);

        // Display individual service summaries with cost breakdown
        System.out.println("\n  Generating Cost Summaries...");
        serviceManager.displayServiceSummary("RES-001", res1.getGuestName(),
                res1.getNumberOfNights(), 1500.0);
        serviceManager.displayServiceSummary("RES-002", res2.getGuestName(),
                res2.getNumberOfNights(), 2500.0);
        serviceManager.displayServiceSummary("RES-003", res3.getGuestName(),
                res3.getNumberOfNights(), 5000.0);

        // Display all mappings in one view
        serviceManager.displayAllServiceMappings();

        System.out.println("\n============================================");
        System.out.println("  Add-On Service Selection Complete.");
        System.out.println("============================================");
    }
}
