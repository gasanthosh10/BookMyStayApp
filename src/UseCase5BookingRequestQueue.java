// ============================================================
//  File        : UseCase5BookingRequestQueue.java
//  Description : Use Case 5 - Booking Request (First-Come-First-Served)
//  Author      : Santhosh
//  Version     : 5.0
// ============================================================

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

// -------------------------------------------------------
// Abstract Base Class : Room (carried forward from UC4)
// -------------------------------------------------------

/**
 * Room - Abstract base class representing a hotel room.
 *
 * <p>Defines common attributes and behavior shared by all room types.
 * Enforces a consistent structure through abstraction and encapsulation.</p>
 *
 * @author Santhosh
 * @version 5.0
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
 * @version 5.0
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
 * @version 5.0
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
 * @version 5.0
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
// Inventory Class : RoomInventory (carried forward from UC4)
// -------------------------------------------------------

/**
 * RoomInventory - Centralized inventory manager for hotel room availability.
 *
 * <p>HashMap acts as the single source of truth for all room availability.
 * No inventory mutation occurs during booking request intake (UC5).</p>
 *
 * @author Santhosh
 * @version 5.0
 */
class RoomInventory {

    private HashMap<String, Integer> inventory;

    /**
     * Constructs a RoomInventory and initializes default availability.
     */
    public RoomInventory() {
        inventory = new HashMap<>();
        inventory.put("Single Room", 5);
        inventory.put("Double Room", 3);
        inventory.put("Suite Room",  2);
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
     * Returns all inventory entries for iteration.
     *
     * @return entrySet of the inventory map
     */
    public Map<String, Integer> getAllInventory() {
        return inventory;
    }
}


// -------------------------------------------------------
// Domain Class : Reservation
// -------------------------------------------------------

/**
 * Reservation - Represents a guest's intent to book a specific room type.
 *
 * <p>Encapsulates all information related to a single booking request.
 * A Reservation is a value object — it captures guest intent without
 * triggering any inventory mutation or allocation.</p>
 *
 * <p>Acts as the element type stored inside the BookingRequestQueue,
 * preserving all guest-supplied information in an ordered structure.</p>
 *
 * @author Santhosh
 * @version 5.0
 */
class Reservation {

    /** Unique identifier for this reservation request */
    private String reservationId;

    /** Name of the guest making the request */
    private String guestName;

    /** Room type the guest intends to book */
    private String requestedRoomType;

    /** Number of nights for the stay */
    private int numberOfNights;

    /**
     * Constructs a Reservation with the specified guest details.
     *
     * @param reservationId     unique ID for this request
     * @param guestName         name of the requesting guest
     * @param requestedRoomType room type the guest wants to book
     * @param numberOfNights    duration of the stay in nights
     */
    public Reservation(String reservationId, String guestName,
                       String requestedRoomType, int numberOfNights) {
        this.reservationId      = reservationId;
        this.guestName          = guestName;
        this.requestedRoomType  = requestedRoomType;
        this.numberOfNights     = numberOfNights;
    }

    public String getReservationId()      { return reservationId;      }
    public String getGuestName()          { return guestName;          }
    public String getRequestedRoomType()  { return requestedRoomType;  }
    public int    getNumberOfNights()     { return numberOfNights;     }

    /**
     * Displays the reservation details to the console.
     */
    public void displayReservationDetails() {
        System.out.println("  Reservation ID : " + reservationId);
        System.out.println("  Guest Name     : " + guestName);
        System.out.println("  Room Type      : " + requestedRoomType);
        System.out.println("  Nights         : " + numberOfNights);
    }
}


// -------------------------------------------------------
// Queue Class : BookingRequestQueue
// -------------------------------------------------------

/**
 * BookingRequestQueue - Manages incoming booking requests using FIFO ordering.
 *
 * <p>Uses Java's {@code Queue<Reservation>} (backed by {@code LinkedList})
 * to preserve arrival order of booking requests. Ensures fairness by
 * guaranteeing that the earliest request is always processed first.</p>
 *
 * <p>No inventory mutation occurs at this stage. This class purely handles
 * request intake and ordering, decoupled from allocation logic.</p>
 *
 * <p>Key properties:</p>
 * <ul>
 *   <li>FIFO — First-Come-First-Served ordering</li>
 *   <li>O(1) enqueue and dequeue operations</li>
 *   <li>No sorting or timestamp comparison required</li>
 * </ul>
 *
 * @author Santhosh
 * @version 5.0
 */
class BookingRequestQueue {

    /**
     * Queue storing booking requests in FIFO order.
     * LinkedList is used as the underlying implementation.
     */
    private Queue<Reservation> requestQueue;

    /**
     * Constructs an empty BookingRequestQueue.
     */
    public BookingRequestQueue() {
        requestQueue = new LinkedList<>();
    }

    /**
     * Adds a booking request to the end of the queue.
     * Preserves arrival order — no inventory update is triggered.
     *
     * @param reservation the guest's booking request to enqueue
     */
    public void addRequest(Reservation reservation) {
        requestQueue.offer(reservation);
        System.out.println("  [Queued] Request " + reservation.getReservationId()
                + " from " + reservation.getGuestName()
                + " for " + reservation.getRequestedRoomType());
    }

    /**
     * Retrieves and removes the next request from the front of the queue.
     * Follows FIFO — earliest request is returned first.
     *
     * @return the next Reservation, or null if the queue is empty
     */
    public Reservation getNextRequest() {
        return requestQueue.poll();
    }

    /**
     * Peeks at the next request without removing it from the queue.
     *
     * @return the front Reservation without dequeuing, or null if empty
     */
    public Reservation peekNextRequest() {
        return requestQueue.peek();
    }

    /**
     * Returns the current number of pending requests in the queue.
     *
     * @return size of the request queue
     */
    public int getPendingCount() {
        return requestQueue.size();
    }

    /**
     * Checks whether the request queue is empty.
     *
     * @return true if no pending requests, false otherwise
     */
    public boolean isEmpty() {
        return requestQueue.isEmpty();
    }

    /**
     * Displays all pending requests in queue order (FIFO).
     * Read-only — does not remove any entries.
     */
    public void displayQueue() {
        System.out.println("\n--------------------------------------------");
        System.out.println("  PENDING BOOKING REQUESTS (FIFO ORDER)");
        System.out.println("--------------------------------------------");

        if (requestQueue.isEmpty()) {
            System.out.println("  No pending requests in queue.");
        } else {
            int position = 1;
            for (Reservation r : requestQueue) {
                System.out.println("  Position " + position + " :");
                r.displayReservationDetails();
                System.out.println("  ........................................");
                position++;
            }
        }
        System.out.println("  Total Pending  : " + requestQueue.size() + " request(s)");
        System.out.println("--------------------------------------------");
    }
}


// -------------------------------------------------------
// Main Entry Point : UseCase5BookingRequestQueue
// -------------------------------------------------------

/**
 * UseCase5BookingRequestQueue - Application entry point for Use Case 5.
 *
 * <p>Demonstrates FIFO booking request intake using a Queue.
 * Multiple guest requests are submitted simultaneously and stored
 * in arrival order. No inventory mutation occurs at this stage.</p>
 *
 * @author Santhosh
 * @version 5.0
 */
public class UseCase5BookingRequestQueue {

    /**
     * Main method - simulates multiple guests submitting booking requests
     * and demonstrates FIFO ordering in the queue.
     *
     * @param args command-line arguments (not used in this use case)
     */
    public static void main(String[] args) {

        System.out.println("============================================");
        System.out.println("      Hotel Booking System  v5.0           ");
        System.out.println("  Use Case 5 : Booking Request Queue       ");
        System.out.println("============================================");

        // Initialize the booking request queue
        BookingRequestQueue bookingQueue = new BookingRequestQueue();

        // Initialize centralized inventory (no mutation during intake)
        RoomInventory inventory = new RoomInventory();

        // Simulate multiple guests submitting booking requests
        System.out.println("\n  Accepting incoming booking requests...");
        System.out.println("--------------------------------------------");

        bookingQueue.addRequest(new Reservation("RES-001", "Arun Kumar",    "Single Room", 2));
        bookingQueue.addRequest(new Reservation("RES-002", "Priya Sharma",  "Double Room", 3));
        bookingQueue.addRequest(new Reservation("RES-003", "Vikram Nair",   "Suite Room",  1));
        bookingQueue.addRequest(new Reservation("RES-004", "Divya Menon",   "Single Room", 4));
        bookingQueue.addRequest(new Reservation("RES-005", "Rahul Verma",   "Double Room", 2));

        // Display current queue state in FIFO order
        bookingQueue.displayQueue();

        // Peek at the next request to be processed (non-destructive)
        System.out.println("\n--------------------------------------------");
        System.out.println("  NEXT REQUEST TO BE PROCESSED (PEEK)");
        System.out.println("--------------------------------------------");
        Reservation next = bookingQueue.peekNextRequest();
        if (next != null) {
            next.displayReservationDetails();
        }
        System.out.println("--------------------------------------------");

        // Confirm inventory has NOT been modified
        System.out.println("\n--------------------------------------------");
        System.out.println("  INVENTORY STATE (Must Remain Unchanged)");
        System.out.println("--------------------------------------------");
        System.out.println("  Single Room : " + inventory.getAvailability("Single Room") + " available");
        System.out.println("  Double Room : " + inventory.getAvailability("Double Room") + " available");
        System.out.println("  Suite Room  : " + inventory.getAvailability("Suite Room")  + " available");
        System.out.println("  [Confirmed] No inventory changes during request intake.");
        System.out.println("--------------------------------------------");

        System.out.println("\n============================================");
        System.out.println("  Booking Request Queue Setup Complete.");
        System.out.println("============================================");
    }
}
