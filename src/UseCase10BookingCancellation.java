import java.util.*;

// ============================================================
// Custom Exceptions
// ============================================================
class ReservationNotFoundException extends Exception {
    public ReservationNotFoundException(String message) {
        super(message);
    }
}

class AlreadyCancelledException extends Exception {
    public AlreadyCancelledException(String message) {
        super(message);
    }
}

class InvalidRoomTypeException extends Exception {
    public InvalidRoomTypeException(String message) { super(message); }
}

class InvalidGuestNameException extends Exception {
    public InvalidGuestNameException(String message) { super(message); }
}

class InvalidNightsException extends Exception {
    public InvalidNightsException(String message) { super(message); }
}

class RoomNotAvailableException extends Exception {
    public RoomNotAvailableException(String message) { super(message); }
}

class InventoryUnderflowException extends Exception {
    public InventoryUnderflowException(String message) { super(message); }
}

// ============================================================
// Domain Class : Reservation
// ============================================================
class Reservation {
    private static int counter = 1000;
    private String reservationId;
    private String guestName;
    private String roomType;
    private int    roomNumber;
    private int    nights;
    private double pricePerNight;
    private boolean cancelled;

    public Reservation(String guestName, String roomType, int roomNumber, int nights, double pricePerNight) {
        this.reservationId = "RES" + (++counter);
        this.guestName     = guestName;
        this.roomType      = roomType;
        this.roomNumber    = roomNumber;
        this.nights        = nights;
        this.pricePerNight = pricePerNight;
        this.cancelled     = false;
    }

    public String  getReservationId() { return reservationId; }
    public String  getGuestName()     { return guestName; }
    public String  getRoomType()      { return roomType; }
    public int     getRoomNumber()    { return roomNumber; }
    public int     getNights()        { return nights; }
    public double  getPricePerNight() { return pricePerNight; }
    public double  getTotalCost()     { return nights * pricePerNight; }
    public boolean isCancelled()      { return cancelled; }
    public void    setCancelled(boolean cancelled) { this.cancelled = cancelled; }

    public void displayReservation() {
        System.out.println("  Reservation ID : " + reservationId);
        System.out.println("  Guest Name     : " + guestName);
        System.out.println("  Room Type      : " + roomType);
        System.out.println("  Room Number    : " + roomNumber);
        System.out.println("  Nights         : " + nights);
        System.out.println("  Price/Night    : INR " + pricePerNight);
        System.out.println("  Total Cost     : INR " + getTotalCost());
        System.out.println("  Status         : " + (cancelled ? "CANCELLED" : "CONFIRMED"));
    }
}

// ============================================================
// Domain Class : RoomInventory
// ============================================================
class RoomInventory {
    private Map<String, Integer> inventory;

    public RoomInventory(int single, int doubleRoom, int suite) {
        inventory = new LinkedHashMap<>();
        inventory.put("Single", single);
        inventory.put("Double", doubleRoom);
        inventory.put("Suite",  suite);
    }

    public void displayInventory() {
        System.out.print("  Inventory -> ");
        inventory.forEach((type, count) ->
            System.out.print(type + ":" + count + "  "));
        System.out.println();
    }

    public int getCount(String roomType) {
        return inventory.getOrDefault(roomType, -1);
    }

    public boolean isValidType(String roomType) {
        return inventory.containsKey(roomType);
    }

    public void decrement(String roomType) throws InventoryUnderflowException {
        int count = inventory.getOrDefault(roomType, 0);
        if (count <= 0) {
            throw new InventoryUnderflowException(
                "Cannot decrement inventory for " + roomType + ". Count is already " + count + ".");
        }
        inventory.put(roomType, count - 1);
    }

    // Restore inventory on cancellation
    public void increment(String roomType) {
        inventory.put(roomType, inventory.getOrDefault(roomType, 0) + 1);
    }
}

// ============================================================
// Manager Class : CancellationService  NEW IN UC10
// ============================================================
class CancellationService {
    private Map<String, Reservation> bookingRegistry;
    private RoomInventory inventory;
    private Stack<String> rollbackStack;   // Tracks released room IDs (LIFO)

    public CancellationService(Map<String, Reservation> bookingRegistry, RoomInventory inventory) {
        this.bookingRegistry = bookingRegistry;
        this.inventory       = inventory;
        this.rollbackStack   = new Stack<>();
    }

    public void cancelBooking(String reservationId)
            throws ReservationNotFoundException, AlreadyCancelledException {

        // Step 1: Validate reservation exists
        if (!bookingRegistry.containsKey(reservationId)) {
            throw new ReservationNotFoundException(
                "Reservation ID '" + reservationId + "' not found in the system.");
        }

        Reservation reservation = bookingRegistry.get(reservationId);

        // Step 2: Validate not already cancelled
        if (reservation.isCancelled()) {
            throw new AlreadyCancelledException(
                "Reservation ID '" + reservationId + "' has already been cancelled.");
        }

        // Step 3: Record room ID in rollback stack (LIFO)
        String roomKey = reservation.getRoomType() + "-" + reservation.getRoomNumber();
        rollbackStack.push(roomKey);

        // Step 4: Restore inventory count
        inventory.increment(reservation.getRoomType());

        // Step 5: Mark reservation as cancelled
        reservation.setCancelled(true);

        System.out.println("  [CANCELLED] Reservation " + reservationId
                + " for " + reservation.getGuestName()
                + " (" + reservation.getRoomType() + ", Room " + reservation.getRoomNumber() + ")");
        System.out.println("  [ROLLBACK]  Room '" + roomKey + "' pushed to rollback stack.");
    }

    public void displayRollbackStack() {
        System.out.println("  Rollback Stack (top = most recent): " +
                (rollbackStack.isEmpty() ? "[]" : rollbackStack));
    }
}

// ============================================================
// Manager Class : BookingManager
// ============================================================
class BookingManager {
    private RoomInventory inventory;
    private Map<String, Reservation> bookingRegistry;
    private int nextRoomNumber;

    public BookingManager(RoomInventory inventory) {
        this.inventory       = inventory;
        this.bookingRegistry = new LinkedHashMap<>();
        this.nextRoomNumber  = 101;
    }

    public Map<String, Reservation> getBookingRegistry() {
        return bookingRegistry;
    }

    public Reservation bookRoom(String guestName, String roomType, int nights)
            throws InvalidRoomTypeException, RoomNotAvailableException, InventoryUnderflowException {

        if (!inventory.isValidType(roomType)) {
            throw new InvalidRoomTypeException(
                "Invalid room type: '" + roomType + "'. Valid: Single, Double, Suite.");
        }
        if (inventory.getCount(roomType) <= 0) {
            throw new RoomNotAvailableException(
                "No rooms available for type: " + roomType + ".");
        }

        double price = switch (roomType) {
            case "Single" -> 2500.0;
            case "Double" -> 4000.0;
            case "Suite"  -> 8000.0;
            default       -> 0.0;
        };

        inventory.decrement(roomType);
        Reservation reservation = new Reservation(guestName, roomType, nextRoomNumber++, nights, price);
        bookingRegistry.put(reservation.getReservationId(), reservation);
        return reservation;
    }

    public void displayAllBookings() {
        System.out.println("\n  All Reservations (" + bookingRegistry.size() + "):");
        if (bookingRegistry.isEmpty()) {
            System.out.println("  No reservations found.");
            return;
        }
        for (Reservation r : bookingRegistry.values()) {
            System.out.println("  ----------------------------------");
            r.displayReservation();
        }
    }
}

// ============================================================
// Main Class
// ============================================================
public class UseCase10BookingCancellation {

    public static void main(String[] args) {

        System.out.println("========================================");
        System.out.println("  Use Case 10 : Booking Cancellation &  ");
        System.out.println("               Inventory Rollback        ");
        System.out.println("========================================");

        RoomInventory  inventory   = new RoomInventory(3, 2, 1);
        BookingManager manager     = new BookingManager(inventory);
        CancellationService cancelSvc =
                new CancellationService(manager.getBookingRegistry(), inventory);

        System.out.println("\n[System] Initial Inventory:");
        inventory.displayInventory();

        // --- Confirm some bookings ---
        System.out.println("\n--- Confirming Bookings ---\n");
        List<String> reservationIds = new ArrayList<>();

        String[][] bookings = {
            {"Alice Johnson", "Single", "3"},
            {"Bob Smith",     "Double", "2"},
            {"Carol White",   "Suite",  "5"},
            {"David Lee",     "Single", "1"},
            {"Eva Brown",     "Double", "4"},
            {"Frank Green",   "Single", "2"}
        };

        for (String[] b : bookings) {
            try {
                Reservation r = manager.bookRoom(b[0], b[1], Integer.parseInt(b[2]));
                reservationIds.add(r.getReservationId());
                System.out.println("  [CONFIRMED] " + r.getReservationId()
                        + " - " + r.getGuestName() + " (" + r.getRoomType() + ")");
            } catch (Exception e) {
                System.out.println("  [ERROR] " + e.getMessage());
            }
        }

        System.out.println("\n[System] Inventory after bookings:");
        inventory.displayInventory();

        // --- Cancellation Attempts ---
        System.out.println("\n--- Cancellation Attempts ---\n");

        // Cancel first and third reservations
        cancelAttempt(cancelSvc, inventory, reservationIds.get(0), "Valid cancellation - " + reservationIds.get(0));
        cancelAttempt(cancelSvc, inventory, reservationIds.get(2), "Valid cancellation - " + reservationIds.get(2));

        // Attempt to cancel already cancelled reservation
        cancelAttempt(cancelSvc, inventory, reservationIds.get(0), "Already cancelled - " + reservationIds.get(0));

        // Attempt to cancel non-existent reservation
        cancelAttempt(cancelSvc, inventory, "RES9999", "Non-existent reservation - RES9999");

        // Cancel another valid reservation
        cancelAttempt(cancelSvc, inventory, reservationIds.get(3), "Valid cancellation - " + reservationIds.get(3));

        // --- Display rollback stack ---
        System.out.println("\n[System] Rollback Stack State:");
        cancelSvc.displayRollbackStack();

        // --- Display all bookings ---
        manager.displayAllBookings();

        System.out.println("\n[System] Final Inventory:");
        inventory.displayInventory();

        System.out.println("\n[System] Booking cancellation and rollback complete.");
        System.out.println("========================================");
    }

    private static void cancelAttempt(CancellationService svc, RoomInventory inventory,
                                       String reservationId, String scenario) {
        System.out.println("Scenario : " + scenario);
        try {
            svc.cancelBooking(reservationId);
        } catch (ReservationNotFoundException e) {
            System.out.println("  [ERROR - NotFound]        " + e.getMessage());
        } catch (AlreadyCancelledException e) {
            System.out.println("  [ERROR - AlreadyCancelled] " + e.getMessage());
        }
        System.out.print("  ");
        inventory.displayInventory();
        System.out.println();
    }
}
