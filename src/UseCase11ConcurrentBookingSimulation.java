import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

// ============================================================
// Domain Class : BookingRequest
// ============================================================
class BookingRequest {
    private String guestName;
    private String roomType;
    private int    nights;

    public BookingRequest(String guestName, String roomType, int nights) {
        this.guestName = guestName;
        this.roomType  = roomType;
        this.nights    = nights;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType()  { return roomType; }
    public int    getNights()    { return nights; }

    @Override
    public String toString() {
        return "[" + guestName + " | " + roomType + " | " + nights + " night(s)]";
    }
}

// ============================================================
// Domain Class : Reservation
// ============================================================
class Reservation {
    private static AtomicInteger counter = new AtomicInteger(1000);
    private String reservationId;
    private String guestName;
    private String roomType;
    private int    roomNumber;
    private int    nights;
    private double pricePerNight;

    public Reservation(String guestName, String roomType, int roomNumber, int nights, double pricePerNight) {
        this.reservationId = "RES" + counter.incrementAndGet();
        this.guestName     = guestName;
        this.roomType      = roomType;
        this.roomNumber    = roomNumber;
        this.nights        = nights;
        this.pricePerNight = pricePerNight;
    }

    public String getReservationId() { return reservationId; }
    public String getGuestName()     { return guestName; }
    public String getRoomType()      { return roomType; }
    public int    getRoomNumber()    { return roomNumber; }
    public int    getNights()        { return nights; }
    public double getTotalCost()     { return nights * pricePerNight; }

    public void displayReservation() {
        System.out.println("    Reservation ID : " + reservationId);
        System.out.println("    Guest Name     : " + guestName);
        System.out.println("    Room Type      : " + roomType);
        System.out.println("    Room Number    : " + roomNumber);
        System.out.println("    Nights         : " + nights);
        System.out.println("    Price/Night    : INR " + pricePerNight);
        System.out.println("    Total Cost     : INR " + getTotalCost());
    }
}

// ============================================================
// Domain Class : RoomInventory (Thread-Safe)  NEW IN UC11
// ============================================================
class RoomInventory {
    private Map<String, Integer> inventory;
    private AtomicInteger        nextRoomNumber = new AtomicInteger(101);

    public RoomInventory(int single, int doubleRoom, int suite) {
        // LinkedHashMap wrapped — access guarded by synchronized methods
        inventory = new LinkedHashMap<>();
        inventory.put("Single", single);
        inventory.put("Double", doubleRoom);
        inventory.put("Suite",  suite);
    }

    public synchronized void displayInventory() {
        System.out.print("  Inventory -> ");
        inventory.forEach((type, count) ->
            System.out.print(type + ":" + count + "  "));
        System.out.println();
    }

    public synchronized boolean isValidType(String roomType) {
        return inventory.containsKey(roomType);
    }

    public synchronized int getCount(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    // Thread-safe allocation — returns room number or -1 if unavailable
    public synchronized int allocateRoom(String roomType) {
        int count = inventory.getOrDefault(roomType, 0);
        if (count <= 0) return -1;
        inventory.put(roomType, count - 1);
        return nextRoomNumber.getAndIncrement();
    }

    public synchronized void releaseRoom(String roomType) {
        inventory.put(roomType, inventory.getOrDefault(roomType, 0) + 1);
    }
}

// ============================================================
// Manager Class : ConcurrentBookingProcessor  NEW IN UC11
// ============================================================
class ConcurrentBookingProcessor {
    private RoomInventory                inventory;
    private Queue<BookingRequest>        requestQueue;   // shared booking queue
    private List<Reservation>            confirmedList;  // shared confirmed list
    private List<String>                 rejectedList;   // shared rejected list

    public ConcurrentBookingProcessor(RoomInventory inventory) {
        this.inventory     = inventory;
        this.requestQueue  = new LinkedList<>();
        this.confirmedList = Collections.synchronizedList(new ArrayList<>());
        this.rejectedList  = Collections.synchronizedList(new ArrayList<>());
    }

    // Add booking requests to shared queue
    public synchronized void addRequest(BookingRequest request) {
        requestQueue.offer(request);
    }

    // Thread-safe request processor — critical section
    public synchronized BookingRequest nextRequest() {
        return requestQueue.poll();
    }

    public void processRequest(BookingRequest request, String threadName) {
        String roomType = request.getRoomType();

        if (!inventory.isValidType(roomType)) {
            synchronized (rejectedList) {
                rejectedList.add(threadName + " -> " + request.getGuestName()
                        + " : Invalid room type '" + roomType + "'");
            }
            System.out.println("  [" + threadName + "] REJECTED (invalid type): " + request);
            return;
        }

        // Critical section: allocate room atomically
        int roomNumber = inventory.allocateRoom(roomType);

        if (roomNumber == -1) {
            synchronized (rejectedList) {
                rejectedList.add(threadName + " -> " + request.getGuestName()
                        + " : No " + roomType + " rooms available");
            }
            System.out.println("  [" + threadName + "] REJECTED (unavailable): " + request);
        } else {
            double price = switch (roomType) {
                case "Single" -> 2500.0;
                case "Double" -> 4000.0;
                case "Suite"  -> 8000.0;
                default       -> 0.0;
            };
            Reservation r = new Reservation(
                    request.getGuestName(), roomType, roomNumber, request.getNights(), price);
            confirmedList.add(r);
            System.out.println("  [" + threadName + "] CONFIRMED " + r.getReservationId()
                    + " -> " + request.getGuestName()
                    + " (" + roomType + ", Room " + roomNumber + ")");
        }
    }

    public List<Reservation> getConfirmedList() { return confirmedList; }
    public List<String>      getRejectedList()  { return rejectedList; }
}

// ============================================================
// Runnable : BookingWorker  NEW IN UC11
// ============================================================
class BookingWorker implements Runnable {
    private ConcurrentBookingProcessor processor;
    private String                     threadName;

    public BookingWorker(ConcurrentBookingProcessor processor, String threadName) {
        this.processor  = processor;
        this.threadName = threadName;
    }

    @Override
    public void run() {
        while (true) {
            BookingRequest request = processor.nextRequest();
            if (request == null) break;   // No more requests

            System.out.println("  [" + threadName + "] picked up request: " + request);
            try {
                // Simulate slight processing delay
                Thread.sleep((long) (Math.random() * 50));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            processor.processRequest(request, threadName);
        }
        System.out.println("  [" + threadName + "] finished — no more requests.");
    }
}

// ============================================================
// Main Class
// ============================================================
public class UseCase11ConcurrentBookingSimulation {

    public static void main(String[] args) throws InterruptedException {

        System.out.println("========================================");
        System.out.println("  Use Case 11 : Concurrent Booking      ");
        System.out.println("               Simulation (Thread Safety)");
        System.out.println("========================================");

        // Inventory: 3 Single, 2 Double, 1 Suite
        RoomInventory inventory = new RoomInventory(3, 2, 1);
        ConcurrentBookingProcessor processor = new ConcurrentBookingProcessor(inventory);

        System.out.println("\n[System] Initial Inventory:");
        inventory.displayInventory();

        // --- Add booking requests to shared queue ---
        System.out.println("\n[System] Loading booking requests into shared queue...\n");

        String[][] requests = {
            {"Alice Johnson",  "Single", "3"},
            {"Bob Smith",      "Double", "2"},
            {"Carol White",    "Suite",  "5"},
            {"David Lee",      "Single", "1"},
            {"Eva Brown",      "Double", "4"},
            {"Frank Green",    "Single", "2"},
            {"Grace Hall",     "Suite",  "3"},   // Suite already taken — should be rejected
            {"Henry Ford",     "Single", "1"},   // Single exhausted — should be rejected
            {"Ivy Chen",       "Double", "2"},   // Double exhausted — should be rejected
            {"Jack Wilson",    "Single", "4"},
        };

        for (String[] r : requests) {
            BookingRequest req = new BookingRequest(r[0], r[1], Integer.parseInt(r[2]));
            processor.addRequest(req);
            System.out.println("  [Queue] Added: " + req);
        }

        // --- Launch concurrent worker threads ---
        System.out.println("\n[System] Launching 3 concurrent booking threads...\n");

        Thread t1 = new Thread(new BookingWorker(processor, "Thread-1"));
        Thread t2 = new Thread(new BookingWorker(processor, "Thread-2"));
        Thread t3 = new Thread(new BookingWorker(processor, "Thread-3"));

        t1.start();
        t2.start();
        t3.start();

        // Wait for all threads to complete
        t1.join();
        t2.join();
        t3.join();

        // --- Summary ---
        System.out.println("\n========================================");
        System.out.println("           CONFIRMED BOOKINGS           ");
        System.out.println("========================================");
        List<Reservation> confirmed = processor.getConfirmedList();
        if (confirmed.isEmpty()) {
            System.out.println("  No bookings confirmed.");
        } else {
            for (Reservation r : confirmed) {
                System.out.println("  ----------------------------------");
                r.displayReservation();
            }
        }
        System.out.println("  Total Confirmed : " + confirmed.size());

        System.out.println("\n========================================");
        System.out.println("           REJECTED BOOKINGS            ");
        System.out.println("========================================");
        List<String> rejected = processor.getRejectedList();
        if (rejected.isEmpty()) {
            System.out.println("  No bookings rejected.");
        } else {
            rejected.forEach(msg -> System.out.println("  [REJECTED] " + msg));
        }
        System.out.println("  Total Rejected  : " + rejected.size());

        System.out.println("\n[System] Final Inventory:");
        inventory.displayInventory();

        System.out.println("\n[System] Concurrent booking simulation complete.");
        System.out.println("========================================");
    }
}
