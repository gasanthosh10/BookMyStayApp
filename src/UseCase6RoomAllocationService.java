// ============================================================
//  File        : UseCase6RoomAllocationService.java
//  Description : Use Case 6 - Reservation Confirmation & Room Allocation
//  Author      : Santhosh
//  Version     : 6.0
// ============================================================

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
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
 * @version 6.0
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
 * @version 6.0
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
 * @version 6.0
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
 * @version 6.0
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
 * <p>HashMap acts as the single source of truth for all room availability.
 * Updated immediately after every successful room allocation.</p>
 *
 * @author Santhosh
 * @version 6.0
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
     * @return number of available rooms, or 0 if type not found
     */
    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    /**
     * Decrements availability by 1 after a successful allocation.
     * Called as part of the atomic allocation operation.
     *
     * @param roomType the room type whose count should be decremented
     */
    public void decrementAvailability(String roomType) {
        int current = inventory.getOrDefault(roomType, 0);
        if (current > 0) {
            inventory.put(roomType, current - 1);
        }
    }

    /**
     * Displays the complete current inventory state.
     */
    public void displayInventory() {
        System.out.println("\n--------------------------------------------");
        System.out.println("  CURRENT INVENTORY STATE");
        System.out.println("--------------------------------------------");
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.printf("  %-15s : %d room(s) available%n",
                    entry.getKey(), entry.getValue());
        }
        System.out.println("--------------------------------------------");
    }
}


// -------------------------------------------------------
// Domain Class : Reservation (carried forward)
// -------------------------------------------------------

/**
 * Reservation - Represents a guest's booking request.
 *
 * <p>Captures guest intent without triggering any allocation.
 * Used as the element type inside BookingRequestQueue.</p>
 *
 * @author Santhosh
 * @version 6.0
 */
class Reservation {

    private String reservationId;
    private String guestName;
    private String requestedRoomType;
    private int    numberOfNights;

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
    }

    public String getReservationId()     { return reservationId;     }
    public String getGuestName()         { return guestName;         }
    public String getRequestedRoomType() { return requestedRoomType; }
    public int    getNumberOfNights()    { return numberOfNights;    }

    /**
     * Displays reservation details to the console.
     */
    public void displayReservationDetails() {
        System.out.println("  Reservation ID : " + reservationId);
        System.out.println("  Guest Name     : " + guestName);
        System.out.println("  Room Type      : " + requestedRoomType);
        System.out.println("  Nights         : " + numberOfNights);
    }
}


// -------------------------------------------------------
// Queue Class : BookingRequestQueue (carried forward)
// -------------------------------------------------------

/**
 * BookingRequestQueue - Manages incoming booking requests using FIFO ordering.
 *
 * <p>Uses {@code Queue<Reservation>} backed by {@code LinkedList}.
 * Ensures fairness — earliest request is always processed first.</p>
 *
 * @author Santhosh
 * @version 6.0
 */
class BookingRequestQueue {

    private Queue<Reservation> requestQueue;

    /**
     * Constructs an empty BookingRequestQueue.
     */
    public BookingRequestQueue() {
        requestQueue = new LinkedList<>();
    }

    /**
     * Adds a booking request to the end of the queue.
     *
     * @param reservation the guest's booking request to enqueue
     */
    public void addRequest(Reservation reservation) {
        requestQueue.offer(reservation);
        System.out.println("  [Queued] " + reservation.getReservationId()
                + " | " + reservation.getGuestName()
                + " | " + reservation.getRequestedRoomType());
    }

    /**
     * Retrieves and removes the next request from the front of the queue (FIFO).
     *
     * @return the next Reservation, or null if the queue is empty
     */
    public Reservation getNextRequest() {
        return requestQueue.poll();
    }

    /**
     * Checks whether the request queue is empty.
     *
     * @return true if no pending requests exist
     */
    public boolean isEmpty() {
        return requestQueue.isEmpty();
    }

    /**
     * Returns the number of pending requests.
     *
     * @return current queue size
     */
    public int getPendingCount() {
        return requestQueue.size();
    }
}


// -------------------------------------------------------
// Service Class : RoomAllocationService
// -------------------------------------------------------

/**
 * RoomAllocationService - Processes queued booking requests and allocates rooms.
 *
 * <p>Implements safe allocation by combining three key mechanisms:</p>
 * <ul>
 *   <li>FIFO dequeue from {@code BookingRequestQueue}</li>
 *   <li>{@code Set<String>} to enforce globally unique room ID assignment</li>
 *   <li>{@code HashMap<String, Set<String>>} to group allocated IDs by room type</li>
 * </ul>
 *
 * <p>Allocation is treated as an atomic logical unit — room ID generation,
 * uniqueness check, Set insertion, and inventory decrement all happen
 * together to prevent partial or inconsistent state.</p>
 *
 * @author Santhosh
 * @version 6.0
 */
class RoomAllocationService {

    /** Reference to the centralized inventory — updated on each allocation. */
    private RoomInventory roomInventory;

    /**
     * Tracks all allocated room IDs globally.
     * Set enforces uniqueness — no duplicate assignments possible.
     */
    private Set<String> allocatedRoomIds;

    /**
     * Maps each room type to the set of room IDs assigned for that type.
     * Enables grouped reporting and per-type duplicate prevention.
     */
    private HashMap<String, Set<String>> allocationMap;

    /** Counter used to generate sequential room IDs per type. */
    private HashMap<String, Integer> roomCounter;

    /**
     * Constructs the allocation service with a reference to the inventory.
     *
     * @param roomInventory the centralized inventory to update on allocation
     */
    public RoomAllocationService(RoomInventory roomInventory) {
        this.roomInventory   = roomInventory;
        this.allocatedRoomIds = new HashSet<>();
        this.allocationMap   = new HashMap<>();
        this.roomCounter     = new HashMap<>();

        // Initialize allocation map and counters for each room type
        allocationMap.put("Single Room", new HashSet<>());
        allocationMap.put("Double Room", new HashSet<>());
        allocationMap.put("Suite Room",  new HashSet<>());

        roomCounter.put("Single Room", 100);
        roomCounter.put("Double Room", 200);
        roomCounter.put("Suite Room",  300);
    }

    /**
     * Processes all pending requests from the queue in FIFO order.
     * Each request is either confirmed with a unique room ID or rejected
     * if inventory is insufficient.
     *
     * @param bookingQueue the queue of pending booking requests
     */
    public void processAllRequests(BookingRequestQueue bookingQueue) {

        System.out.println("\n============================================");
        System.out.println("  PROCESSING BOOKING REQUESTS (FIFO)       ");
        System.out.println("============================================");

        while (!bookingQueue.isEmpty()) {
            Reservation request = bookingQueue.getNextRequest();
            allocateRoom(request);
        }
    }

    /**
     * Attempts to allocate a room for a single reservation.
     *
     * <p>Atomic logical operation:</p>
     * <ol>
     *   <li>Check availability in inventory</li>
     *   <li>Generate a unique room ID</li>
     *   <li>Verify ID is not already in the global Set (double-booking guard)</li>
     *   <li>Add ID to global Set and type-specific Set</li>
     *   <li>Decrement inventory immediately</li>
     *   <li>Confirm reservation</li>
     * </ol>
     *
     * @param reservation the booking request to process
     */
    private void allocateRoom(Reservation reservation) {

        String roomType   = reservation.getRequestedRoomType();
        int    available  = roomInventory.getAvailability(roomType);

        System.out.println("\n--------------------------------------------");
        System.out.println("  Processing : " + reservation.getReservationId()
                + " | " + reservation.getGuestName());
        System.out.println("  Requested  : " + roomType);

        // Step 1: Check inventory availability
        if (available <= 0) {
            System.out.println("  Status     : REJECTED - No rooms available for " + roomType);
            System.out.println("--------------------------------------------");
            return;
        }

        // Step 2: Generate unique room ID
        int    counter = roomCounter.getOrDefault(roomType, 100);
        String roomId  = roomType.replace(" ", "-").toUpperCase() + "-" + counter;
        roomCounter.put(roomType, counter + 1);

        // Step 3: Double-booking guard — check Set for ID uniqueness
        if (allocatedRoomIds.contains(roomId)) {
            System.out.println("  Status     : REJECTED - Room ID " + roomId + " already allocated.");
            System.out.println("--------------------------------------------");
            return;
        }

        // Step 4: Record room ID in global Set and type-specific Set (atomic)
        allocatedRoomIds.add(roomId);
        allocationMap.get(roomType).add(roomId);

        // Step 5: Decrement inventory immediately after allocation
        roomInventory.decrementAvailability(roomType);

        // Step 6: Confirm reservation
        System.out.println("  Assigned   : " + roomId);
        System.out.println("  Nights     : " + reservation.getNumberOfNights());
        System.out.println("  Status     : CONFIRMED");
        System.out.println("--------------------------------------------");
    }

    /**
     * Displays a summary of all confirmed room allocations grouped by type.
     * Shows which room IDs have been assigned and remaining inventory.
     */
    public void displayAllocationSummary() {

        System.out.println("\n============================================");
        System.out.println("  ALLOCATION SUMMARY");
        System.out.println("============================================");

        for (Map.Entry<String, Set<String>> entry : allocationMap.entrySet()) {
            String      type        = entry.getKey();
            Set<String> assignedIds = entry.getValue();

            System.out.println("\n  Room Type    : " + type);
            System.out.println("  Allocated    : " + assignedIds.size() + " room(s)");
            System.out.println("  Room IDs     : " +
                    (assignedIds.isEmpty() ? "None" : assignedIds));
            System.out.println("  Remaining    : " +
                    roomInventory.getAvailability(type) + " room(s)");
        }

        System.out.println("\n  Total Unique Allocations : " + allocatedRoomIds.size());
        System.out.println("============================================");
    }
}


// -------------------------------------------------------
// Main Entry Point : UseCase6RoomAllocationService
// -------------------------------------------------------

/**
 * UseCase6RoomAllocationService - Application entry point for Use Case 6.
 *
 * <p>Demonstrates safe room allocation with double-booking prevention.
 * Requests are dequeued in FIFO order, unique room IDs are generated,
 * and inventory is decremented atomically on each confirmed booking.</p>
 *
 * @author Santhosh
 * @version 6.0
 */
public class UseCase6RoomAllocationService {

    /**
     * Main method - sets up inventory, loads the booking queue,
     * processes all requests, and displays the allocation summary.
     *
     * @param args command-line arguments (not used in this use case)
     */
    public static void main(String[] args) {

        System.out.println("============================================");
        System.out.println("      Hotel Booking System  v6.0           ");
        System.out.println("  Use Case 6 : Room Allocation Service     ");
        System.out.println("============================================");

        // Initialize centralized inventory
        RoomInventory inventory = new RoomInventory();

        // Display initial inventory
        System.out.println("\n  Initial Inventory State :");
        inventory.displayInventory();

        // Initialize the booking request queue
        BookingRequestQueue bookingQueue = new BookingRequestQueue();

        // Simulate multiple guests submitting requests
        System.out.println("\n  Accepting Booking Requests...");
        System.out.println("--------------------------------------------");
        bookingQueue.addRequest(new Reservation("RES-001", "Arun Kumar",   "Single Room", 2));
        bookingQueue.addRequest(new Reservation("RES-002", "Priya Sharma", "Double Room", 3));
        bookingQueue.addRequest(new Reservation("RES-003", "Vikram Nair",  "Suite Room",  1));
        bookingQueue.addRequest(new Reservation("RES-004", "Divya Menon",  "Single Room", 4));
        bookingQueue.addRequest(new Reservation("RES-005", "Rahul Verma",  "Double Room", 2));
        bookingQueue.addRequest(new Reservation("RES-006", "Sneha Iyer",   "Single Room", 1));
        bookingQueue.addRequest(new Reservation("RES-007", "Karthik Raja", "Suite Room",  3));

        System.out.println("--------------------------------------------");
        System.out.println("  Total Requests Queued : " + bookingQueue.getPendingCount());

        // Initialize allocation service and process all requests
        RoomAllocationService allocationService = new RoomAllocationService(inventory);
        allocationService.processAllRequests(bookingQueue);

        // Display final allocation summary
        allocationService.displayAllocationSummary();

        // Display final inventory state after all allocations
        System.out.println("\n  Final Inventory State :");
        inventory.displayInventory();

        System.out.println("\n============================================");
        System.out.println("  Room Allocation Complete.");
        System.out.println("============================================");
    }
}
