import java.util.*;
import java.io.*;

// ============================================================
// Domain Class : Reservation (Serializable)  NEW IN UC12
// ============================================================
class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;
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
    public void    setCancelled(boolean c) { this.cancelled = c; }

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
// Domain Class : SystemSnapshot (Serializable)  NEW IN UC12
// ============================================================
class SystemSnapshot implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, Integer> inventorySnapshot;
    private List<Reservation>    bookingSnapshot;
    private String               savedAt;

    public SystemSnapshot(Map<String, Integer> inventory, List<Reservation> bookings, String savedAt) {
        this.inventorySnapshot = new LinkedHashMap<>(inventory);
        this.bookingSnapshot   = new ArrayList<>(bookings);
        this.savedAt           = savedAt;
    }

    public Map<String, Integer> getInventorySnapshot() { return inventorySnapshot; }
    public List<Reservation>    getBookingSnapshot()   { return bookingSnapshot; }
    public String               getSavedAt()           { return savedAt; }
}

// ============================================================
// Domain Class : RoomInventory
// ============================================================
class RoomInventory {
    private Map<String, Integer> inventory;
    private int nextRoomNumber;

    public RoomInventory(int single, int doubleRoom, int suite) {
        inventory = new LinkedHashMap<>();
        inventory.put("Single", single);
        inventory.put("Double", doubleRoom);
        inventory.put("Suite",  suite);
        nextRoomNumber = 101;
    }

    // Restore constructor from snapshot
    public RoomInventory(Map<String, Integer> snapshot) {
        this.inventory     = new LinkedHashMap<>(snapshot);
        this.nextRoomNumber = 201; // offset to avoid room number conflicts after recovery
    }

    public void displayInventory() {
        System.out.print("  Inventory -> ");
        inventory.forEach((type, count) ->
            System.out.print(type + ":" + count + "  "));
        System.out.println();
    }

    public Map<String, Integer> getInventoryMap() { return inventory; }

    public boolean isValidType(String roomType) { return inventory.containsKey(roomType); }

    public int getCount(String roomType) { return inventory.getOrDefault(roomType, 0); }

    public boolean allocate(String roomType) {
        int count = inventory.getOrDefault(roomType, 0);
        if (count <= 0) return false;
        inventory.put(roomType, count - 1);
        return true;
    }

    public int getNextRoomNumber() { return nextRoomNumber++; }
}

// ============================================================
// Manager Class : PersistenceService  NEW IN UC12
// ============================================================
class PersistenceService {
    private static final String SAVE_FILE = "system_state.dat";

    // Serialize and save system state to file
    public static void saveState(RoomInventory inventory, List<Reservation> bookings) {
        String timestamp = new java.util.Date().toString();
        SystemSnapshot snapshot = new SystemSnapshot(inventory.getInventoryMap(), bookings, timestamp);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            oos.writeObject(snapshot);
            System.out.println("  [PersistenceService] State saved successfully at: " + timestamp);
            System.out.println("  [PersistenceService] File: " + SAVE_FILE);
        } catch (IOException e) {
            System.out.println("  [PersistenceService] ERROR saving state: " + e.getMessage());
        }
    }

    // Deserialize and restore system state from file
    public static SystemSnapshot loadState() {
        File file = new File(SAVE_FILE);

        if (!file.exists()) {
            System.out.println("  [PersistenceService] No save file found. Starting fresh.");
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(SAVE_FILE))) {
            SystemSnapshot snapshot = (SystemSnapshot) ois.readObject();
            System.out.println("  [PersistenceService] State loaded successfully.");
            System.out.println("  [PersistenceService] Snapshot saved at: " + snapshot.getSavedAt());
            return snapshot;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("  [PersistenceService] ERROR loading state (corrupted/missing): " + e.getMessage());
            System.out.println("  [PersistenceService] Starting with default state.");
            return null;
        }
    }

    public static boolean saveFileExists() {
        return new File(SAVE_FILE).exists();
    }

    public static void deleteSaveFile() {
        File file = new File(SAVE_FILE);
        if (file.exists()) {
            file.delete();
            System.out.println("  [PersistenceService] Save file deleted (simulating fresh start).");
        }
    }
}

// ============================================================
// Manager Class : BookingManager
// ============================================================
class BookingManager {
    private RoomInventory     inventory;
    private List<Reservation> bookings;

    public BookingManager(RoomInventory inventory) {
        this.inventory = inventory;
        this.bookings  = new ArrayList<>();
    }

    // Restore from snapshot
    public BookingManager(RoomInventory inventory, List<Reservation> restoredBookings) {
        this.inventory = inventory;
        this.bookings  = new ArrayList<>(restoredBookings);
    }

    public Reservation bookRoom(String guestName, String roomType, int nights) {
        if (!inventory.isValidType(roomType)) {
            System.out.println("  [ERROR] Invalid room type: " + roomType);
            return null;
        }
        if (!inventory.allocate(roomType)) {
            System.out.println("  [ERROR] No " + roomType + " rooms available.");
            return null;
        }
        double price = switch (roomType) {
            case "Single" -> 2500.0;
            case "Double" -> 4000.0;
            case "Suite"  -> 8000.0;
            default       -> 0.0;
        };
        Reservation r = new Reservation(guestName, roomType, inventory.getNextRoomNumber(), nights, price);
        bookings.add(r);
        return r;
    }

    public List<Reservation> getBookings()    { return bookings; }
    public RoomInventory     getInventory()   { return inventory; }

    public void displayAllBookings() {
        System.out.println("  Total Bookings : " + bookings.size());
        for (Reservation r : bookings) {
            System.out.println("  ----------------------------------");
            r.displayReservation();
        }
    }
}

// ============================================================
// Main Class
// ============================================================
public class UseCase12DataPersistenceRecovery {

    public static void main(String[] args) {

        System.out.println("========================================");
        System.out.println("  Use Case 12 : Data Persistence &      ");
        System.out.println("               System Recovery           ");
        System.out.println("========================================");

        // --------------------------------------------------------
        // PHASE 1: Initial Run — Book rooms and save state
        // --------------------------------------------------------
        System.out.println("\n========================================");
        System.out.println("  PHASE 1 : Initial System Run           ");
        System.out.println("========================================");

        RoomInventory  inventory1 = new RoomInventory(3, 2, 1);
        BookingManager manager1   = new BookingManager(inventory1);

        System.out.println("\n[Phase 1] Initial Inventory:");
        inventory1.displayInventory();

        System.out.println("\n[Phase 1] Processing bookings...");
        String[][] bookings = {
            {"Alice Johnson", "Single", "3"},
            {"Bob Smith",     "Double", "2"},
            {"Carol White",   "Suite",  "5"},
            {"David Lee",     "Single", "1"},
            {"Eva Brown",     "Double", "4"},
        };

        for (String[] b : bookings) {
            Reservation r = manager1.bookRoom(b[0], b[1], Integer.parseInt(b[2]));
            if (r != null) {
                System.out.println("  [CONFIRMED] " + r.getReservationId()
                        + " - " + r.getGuestName() + " (" + r.getRoomType() + ")");
            }
        }

        System.out.println("\n[Phase 1] Inventory after bookings:");
        inventory1.displayInventory();

        System.out.println("\n[Phase 1] Saving system state before shutdown...");
        PersistenceService.saveState(inventory1, manager1.getBookings());

        System.out.println("\n[Phase 1] System shutting down...");
        System.out.println("  (All in-memory data cleared)\n");

        // --------------------------------------------------------
        // PHASE 2: System Restart — Restore state from file
        // --------------------------------------------------------
        System.out.println("========================================");
        System.out.println("  PHASE 2 : System Restart & Recovery    ");
        System.out.println("========================================");

        System.out.println("\n[Phase 2] Checking for saved state...");
        SystemSnapshot snapshot = PersistenceService.loadState();

        RoomInventory  inventory2;
        BookingManager manager2;

        if (snapshot != null) {
            inventory2 = new RoomInventory(snapshot.getInventorySnapshot());
            manager2   = new BookingManager(inventory2, snapshot.getBookingSnapshot());
            System.out.println("\n[Phase 2] State successfully recovered!");
        } else {
            System.out.println("\n[Phase 2] No state found. Starting with defaults.");
            inventory2 = new RoomInventory(3, 2, 1);
            manager2   = new BookingManager(inventory2);
        }

        System.out.println("\n[Phase 2] Recovered Inventory:");
        inventory2.displayInventory();

        System.out.println("\n[Phase 2] Recovered Bookings:");
        manager2.displayAllBookings();

        // --------------------------------------------------------
        // PHASE 3: Continue operations after recovery
        // --------------------------------------------------------
        System.out.println("\n========================================");
        System.out.println("  PHASE 3 : Post-Recovery Operations     ");
        System.out.println("========================================");

        System.out.println("\n[Phase 3] Adding new bookings after recovery...");
        Reservation r1 = manager2.bookRoom("Frank Green", "Single", 2);
        if (r1 != null) {
            System.out.println("  [CONFIRMED] " + r1.getReservationId()
                    + " - " + r1.getGuestName() + " (" + r1.getRoomType() + ")");
        }

        Reservation r2 = manager2.bookRoom("Grace Hall", "Double", 3);
        if (r2 != null) {
            System.out.println("  [CONFIRMED] " + r2.getReservationId()
                    + " - " + r2.getGuestName() + " (" + r2.getRoomType() + ")");
        }

        // Attempt booking on exhausted room type
        Reservation r3 = manager2.bookRoom("Henry Ford", "Suite", 1);
        if (r3 != null) {
            System.out.println("  [CONFIRMED] " + r3.getReservationId()
                    + " - " + r3.getGuestName() + " (" + r3.getRoomType() + ")");
        }

        System.out.println("\n[Phase 3] Inventory after post-recovery bookings:");
        inventory2.displayInventory();

        System.out.println("\n[Phase 3] Saving updated state...");
        PersistenceService.saveState(inventory2, manager2.getBookings());

        // --------------------------------------------------------
        // PHASE 4: Graceful handling of missing/corrupted file
        // --------------------------------------------------------
        System.out.println("\n========================================");
        System.out.println("  PHASE 4 : Missing File Recovery Test   ");
        System.out.println("========================================");

        PersistenceService.deleteSaveFile();
        System.out.println("\n[Phase 4] Attempting to load deleted save file...");
        SystemSnapshot missing = PersistenceService.loadState();
        if (missing == null) {
            System.out.println("  [Phase 4] System handled missing file gracefully.");
            System.out.println("  [Phase 4] Starting with fresh default state.");
        }

        System.out.println("\n[System] Data persistence and recovery complete.");
        System.out.println("========================================");
    }
}
