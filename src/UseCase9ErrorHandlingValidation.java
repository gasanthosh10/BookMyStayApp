import java.util.*;

// ============================================================
// Custom Exceptions  NEW IN UC9
// ============================================================
class InvalidRoomTypeException extends Exception {
    public InvalidRoomTypeException(String message) {
        super(message);
    }
}

class InvalidGuestNameException extends Exception {
    public InvalidGuestNameException(String message) {
        super(message);
    }
}

class InvalidNightsException extends Exception {
    public InvalidNightsException(String message) {
        super(message);
    }
}

class RoomNotAvailableException extends Exception {
    public RoomNotAvailableException(String message) {
        super(message);
    }
}

class InventoryUnderflowException extends Exception {
    public InventoryUnderflowException(String message) {
        super(message);
    }
}

// ============================================================
// Domain Class : Room
// ============================================================
class Room {
    private int roomNumber;
    private String roomType;
    private double pricePerNight;
    private boolean available;

    public Room(int roomNumber, String roomType, double pricePerNight) {
        this.roomNumber    = roomNumber;
        this.roomType      = roomType;
        this.pricePerNight = pricePerNight;
        this.available     = true;
    }

    public int    getRoomNumber()    { return roomNumber; }
    public String getRoomType()      { return roomType; }
    public double getPricePerNight() { return pricePerNight; }
    public boolean isAvailable()     { return available; }
    public void setAvailable(boolean available) { this.available = available; }
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

    public Reservation(String guestName, String roomType, int roomNumber, int nights, double pricePerNight) {
        this.reservationId  = "RES" + (++counter);
        this.guestName      = guestName;
        this.roomType       = roomType;
        this.roomNumber     = roomNumber;
        this.nights         = nights;
        this.pricePerNight  = pricePerNight;
    }

    public String getReservationId() { return reservationId; }
    public String getGuestName()     { return guestName; }
    public String getRoomType()      { return roomType; }
    public int    getRoomNumber()    { return roomNumber; }
    public int    getNights()        { return nights; }
    public double getTotalCost()     { return nights * pricePerNight; }

    public void displayReservation() {
        System.out.println("  Reservation ID : " + reservationId);
        System.out.println("  Guest Name     : " + guestName);
        System.out.println("  Room Type      : " + roomType);
        System.out.println("  Room Number    : " + roomNumber);
        System.out.println("  Nights         : " + nights);
        System.out.println("  Price/Night    : INR " + pricePerNight);
        System.out.println("  Total Cost     : INR " + getTotalCost());
    }
}

// ============================================================
// Domain Class : RoomInventory
// ============================================================
class RoomInventory {
    private Map<String, Integer> inventory;

    public RoomInventory(int single, int doublRoom, int suite) {
        inventory = new LinkedHashMap<>();
        inventory.put("Single", single);
        inventory.put("Double", doublRoom);
        inventory.put("Suite",  suite);
    }

    public void displayInventory() {
        System.out.print("  Current Inventory -> ");
        inventory.forEach((type, count) ->
            System.out.print(type + ":" + count + "  "));
        System.out.println();
    }

    public int getCount(String roomType) {
        return inventory.getOrDefault(roomType, -1);
    }

    public void decrement(String roomType) throws InventoryUnderflowException {
        int count = inventory.getOrDefault(roomType, 0);
        if (count <= 0) {
            throw new InventoryUnderflowException(
                "Cannot decrement inventory for " + roomType + ". Count is already " + count + ".");
        }
        inventory.put(roomType, count - 1);
    }

    public boolean isValidType(String roomType) {
        return inventory.containsKey(roomType);
    }
}

// ============================================================
// Manager Class : InvalidBookingValidator  NEW IN UC9
// ============================================================
class InvalidBookingValidator {

    private static final Set<String> VALID_ROOM_TYPES =
            new HashSet<>(Arrays.asList("Single", "Double", "Suite"));

    // Validate guest name
    public static void validateGuestName(String name) throws InvalidGuestNameException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidGuestNameException("Guest name cannot be null or empty.");
        }
        if (name.trim().length() < 3) {
            throw new InvalidGuestNameException(
                "Guest name '" + name + "' is too short. Must be at least 3 characters.");
        }
    }

    // Validate room type (case-sensitive as per requirement)
    public static void validateRoomType(String roomType) throws InvalidRoomTypeException {
        if (roomType == null || roomType.trim().isEmpty()) {
            throw new InvalidRoomTypeException("Room type cannot be null or empty.");
        }
        if (!VALID_ROOM_TYPES.contains(roomType)) {
            throw new InvalidRoomTypeException(
                "Invalid room type: '" + roomType + "'. Valid types are: " + VALID_ROOM_TYPES
                + " (Note: case-sensitive).");
        }
    }

    // Validate number of nights
    public static void validateNights(int nights) throws InvalidNightsException {
        if (nights <= 0) {
            throw new InvalidNightsException(
                "Number of nights must be greater than 0. Provided: " + nights);
        }
        if (nights > 30) {
            throw new InvalidNightsException(
                "Number of nights cannot exceed 30. Provided: " + nights);
        }
    }

    // Validate room availability
    public static void validateAvailability(String roomType, RoomInventory inventory)
            throws RoomNotAvailableException {
        if (inventory.getCount(roomType) <= 0) {
            throw new RoomNotAvailableException(
                "No rooms available for type: " + roomType + ". All rooms are fully booked.");
        }
    }
}

// ============================================================
// Manager Class : BookingManager
// ============================================================
class BookingManager {
    private RoomInventory inventory;
    private List<Reservation> confirmedBookings;
    private int nextRoomNumber;

    public BookingManager(RoomInventory inventory) {
        this.inventory        = inventory;
        this.confirmedBookings = new ArrayList<>();
        this.nextRoomNumber   = 101;
    }

    public Reservation bookRoom(String guestName, String roomType, int nights)
            throws InvalidGuestNameException, InvalidRoomTypeException,
                   InvalidNightsException, RoomNotAvailableException,
                   InventoryUnderflowException {

        // Step 1: Validate all inputs (fail-fast)
        InvalidBookingValidator.validateGuestName(guestName);
        InvalidBookingValidator.validateRoomType(roomType);
        InvalidBookingValidator.validateNights(nights);
        InvalidBookingValidator.validateAvailability(roomType, inventory);

        // Step 2: Determine price
        double price = switch (roomType) {
            case "Single" -> 2500.0;
            case "Double" -> 4000.0;
            case "Suite"  -> 8000.0;
            default       -> 0.0;
        };

        // Step 3: Decrement inventory (guarded)
        inventory.decrement(roomType);

        // Step 4: Create and store reservation
        Reservation reservation = new Reservation(guestName, roomType, nextRoomNumber++, nights, price);
        confirmedBookings.add(reservation);
        return reservation;
    }

    public void displayConfirmedBookings() {
        System.out.println("\n  Confirmed Bookings (" + confirmedBookings.size() + "):");
        if (confirmedBookings.isEmpty()) {
            System.out.println("  No bookings confirmed.");
            return;
        }
        for (Reservation r : confirmedBookings) {
            System.out.println("  ----------------------------------");
            r.displayReservation();
        }
    }
}

// ============================================================
// Main Class
// ============================================================
public class UseCase9ErrorHandlingValidation {

    public static void main(String[] args) {

        System.out.println("========================================");
        System.out.println("  Use Case 9 : Error Handling &         ");
        System.out.println("               Validation               ");
        System.out.println("========================================");

        RoomInventory inventory = new RoomInventory(2, 1, 1);
        BookingManager manager  = new BookingManager(inventory);

        System.out.println("\n[System] Initial Inventory:");
        inventory.displayInventory();

        // Helper lambda to attempt a booking and handle all exceptions
        System.out.println("\n--- Booking Attempts ---\n");

        // Attempt 1: Valid booking
        attempt(manager, inventory, "Alice Johnson", "Single", 3,
                "Valid booking - Alice Johnson, Single, 3 nights");

        // Attempt 2: Invalid room type (wrong case)
        attempt(manager, inventory, "Bob Smith", "single", 2,
                "Invalid room type (lowercase 'single')");

        // Attempt 3: Empty guest name
        attempt(manager, inventory, "", "Double", 2,
                "Empty guest name");

        // Attempt 4: Zero nights
        attempt(manager, inventory, "Carol White", "Suite", 0,
                "Zero nights");

        // Attempt 5: Nights exceeding limit
        attempt(manager, inventory, "David Lee", "Double", 35,
                "Nights exceeding maximum (35)");

        // Attempt 6: Valid booking
        attempt(manager, inventory, "Eva Brown", "Double", 2,
                "Valid booking - Eva Brown, Double, 2 nights");

        // Attempt 7: Room not available (only 1 Double was available)
        attempt(manager, inventory, "Frank Green", "Double", 1,
                "Room not available - Double already booked");

        // Attempt 8: Valid Suite booking
        attempt(manager, inventory, "Grace Hall", "Suite", 5,
                "Valid booking - Grace Hall, Suite, 5 nights");

        // Attempt 9: Another Single booking
        attempt(manager, inventory, "Henry Ford", "Single", 2,
                "Valid booking - Henry Ford, Single, 2 nights");

        // Attempt 10: No more Singles available
        attempt(manager, inventory, "Ivy Chen", "Single", 1,
                "Room not available - Single exhausted");

        // Display all confirmed bookings
        System.out.println("\n========================================");
        System.out.println("       CONFIRMED BOOKINGS SUMMARY       ");
        System.out.println("========================================");
        manager.displayConfirmedBookings();

        System.out.println("\n[System] Final Inventory:");
        inventory.displayInventory();

        System.out.println("\n[System] Error handling and validation complete.");
        System.out.println("========================================");
    }

    // Helper method to attempt booking and display result
    private static void attempt(BookingManager manager, RoomInventory inventory,
                                 String name, String type, int nights, String scenario) {
        System.out.println("Scenario : " + scenario);
        try {
            Reservation r = manager.bookRoom(name, type, nights);
            System.out.println("  [SUCCESS] Reservation confirmed: " + r.getReservationId()
                    + " for " + r.getGuestName());
        } catch (InvalidGuestNameException e) {
            System.out.println("  [ERROR - InvalidGuestName]   " + e.getMessage());
        } catch (InvalidRoomTypeException e) {
            System.out.println("  [ERROR - InvalidRoomType]    " + e.getMessage());
        } catch (InvalidNightsException e) {
            System.out.println("  [ERROR - InvalidNights]      " + e.getMessage());
        } catch (RoomNotAvailableException e) {
            System.out.println("  [ERROR - RoomNotAvailable]   " + e.getMessage());
        } catch (InventoryUnderflowException e) {
            System.out.println("  [ERROR - InventoryUnderflow] " + e.getMessage());
        }
        System.out.print("  ");
        inventory.displayInventory();
        System.out.println();
    }
}
