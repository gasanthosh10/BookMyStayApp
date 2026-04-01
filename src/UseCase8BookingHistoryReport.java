import java.util.*;

// ============================================================
// Domain Class : Reservation
// ============================================================
class Reservation {
    private static int counter = 1000;
    private String reservationId;
    private String guestName;
    private String roomType;
    private int roomNumber;
    private int nights;
    private double pricePerNight;
    private List<String> addOns;

    public Reservation(String guestName, String roomType, int roomNumber, int nights, double pricePerNight) {
        this.reservationId = "RES" + (++counter);
        this.guestName = guestName;
        this.roomType = roomType;
        this.roomNumber = roomNumber;
        this.nights = nights;
        this.pricePerNight = pricePerNight;
        this.addOns = new ArrayList<>();
    }

    public void addAddOn(String service) {
        addOns.add(service);
    }

    public double getTotalCost() {
        return nights * pricePerNight;
    }

    public String getReservationId() { return reservationId; }
    public String getGuestName()     { return guestName; }
    public String getRoomType()      { return roomType; }
    public int getRoomNumber()       { return roomNumber; }
    public int getNights()           { return nights; }
    public double getPricePerNight() { return pricePerNight; }
    public List<String> getAddOns()  { return addOns; }

    public void displayReservation() {
        System.out.println("  Reservation ID : " + reservationId);
        System.out.println("  Guest Name     : " + guestName);
        System.out.println("  Room Type      : " + roomType);
        System.out.println("  Room Number    : " + roomNumber);
        System.out.println("  Nights         : " + nights);
        System.out.println("  Price/Night    : INR " + pricePerNight);
        System.out.println("  Total Cost     : INR " + getTotalCost());
        if (!addOns.isEmpty()) {
            System.out.println("  Add-On Services: " + addOns);
        }
    }
}

// ============================================================
// Domain Class : BookingHistory  NEW IN UC8
// ============================================================
class BookingHistory {
    private List<Reservation> history;

    public BookingHistory() {
        this.history = new ArrayList<>();
    }

    // Add a confirmed reservation to history
    public void addBooking(Reservation reservation) {
        history.add(reservation);
        System.out.println("[BookingHistory] Reservation " + reservation.getReservationId()
                + " for " + reservation.getGuestName() + " added to history.");
    }

    // Retrieve all bookings (read-only view)
    public List<Reservation> getAllBookings() {
        return Collections.unmodifiableList(history);
    }

    // Retrieve bookings by room type
    public List<Reservation> getBookingsByRoomType(String roomType) {
        List<Reservation> result = new ArrayList<>();
        for (Reservation r : history) {
            if (r.getRoomType().equalsIgnoreCase(roomType)) {
                result.add(r);
            }
        }
        return result;
    }

    public int getTotalBookings() {
        return history.size();
    }
}

// ============================================================
// Manager Class : BookingReportService  NEW IN UC8
// ============================================================
class BookingReportService {
    private BookingHistory bookingHistory;

    public BookingReportService(BookingHistory bookingHistory) {
        this.bookingHistory = bookingHistory;
    }

    // Full booking history report
    public void generateFullReport() {
        List<Reservation> all = bookingHistory.getAllBookings();
        System.out.println("\n========================================");
        System.out.println("       FULL BOOKING HISTORY REPORT      ");
        System.out.println("========================================");
        if (all.isEmpty()) {
            System.out.println("  No bookings recorded yet.");
            return;
        }
        for (int i = 0; i < all.size(); i++) {
            System.out.println("\n  -- Booking #" + (i + 1) + " --");
            all.get(i).displayReservation();
        }
        System.out.println("\n  Total Bookings : " + all.size());
        System.out.println("========================================");
    }

    // Summary report
    public void generateSummaryReport() {
        List<Reservation> all = bookingHistory.getAllBookings();
        System.out.println("\n========================================");
        System.out.println("         BOOKING SUMMARY REPORT         ");
        System.out.println("========================================");
        if (all.isEmpty()) {
            System.out.println("  No bookings to summarise.");
            return;
        }

        double totalRevenue = 0;
        Map<String, Integer> roomTypeCount = new LinkedHashMap<>();
        roomTypeCount.put("Single", 0);
        roomTypeCount.put("Double", 0);
        roomTypeCount.put("Suite",  0);

        for (Reservation r : all) {
            totalRevenue += r.getTotalCost();
            roomTypeCount.merge(r.getRoomType(), 1, Integer::sum);
        }

        System.out.printf("  Total Bookings   : %d%n", all.size());
        System.out.printf("  Total Revenue    : INR %.2f%n", totalRevenue);
        System.out.println("\n  Bookings by Room Type:");
        for (Map.Entry<String, Integer> entry : roomTypeCount.entrySet()) {
            System.out.printf("    %-8s : %d%n", entry.getKey(), entry.getValue());
        }
        System.out.println("========================================");
    }

    // Report filtered by room type
    public void generateRoomTypeReport(String roomType) {
        List<Reservation> filtered = bookingHistory.getBookingsByRoomType(roomType);
        System.out.println("\n========================================");
        System.out.println("   ROOM TYPE REPORT : " + roomType.toUpperCase());
        System.out.println("========================================");
        if (filtered.isEmpty()) {
            System.out.println("  No bookings found for room type: " + roomType);
            return;
        }
        for (int i = 0; i < filtered.size(); i++) {
            System.out.println("\n  -- Booking #" + (i + 1) + " --");
            filtered.get(i).displayReservation();
        }
        System.out.println("\n  Total " + roomType + " Bookings : " + filtered.size());
        System.out.println("========================================");
    }
}

// ============================================================
// Main Class
// ============================================================
public class UseCase8BookingHistoryReport {

    public static void main(String[] args) {

        System.out.println("========================================");
        System.out.println("   Use Case 8 : Booking History &       ");
        System.out.println("                Reporting                ");
        System.out.println("========================================");

        // --- Setup booking history and report service ---
        BookingHistory bookingHistory     = new BookingHistory();
        BookingReportService reportService = new BookingReportService(bookingHistory);

        // --- Simulate confirmed reservations being added ---
        System.out.println("\n[System] Confirming and recording bookings...\n");

        Reservation r1 = new Reservation("Alice Johnson", "Single", 101, 3, 2500.0);
        r1.addAddOn("Breakfast");
        bookingHistory.addBooking(r1);

        Reservation r2 = new Reservation("Bob Smith", "Double", 201, 2, 4000.0);
        r2.addAddOn("Airport Transfer");
        r2.addAddOn("Spa");
        bookingHistory.addBooking(r2);

        Reservation r3 = new Reservation("Carol White", "Suite", 301, 5, 8000.0);
        r3.addAddOn("Breakfast");
        r3.addAddOn("Laundry");
        bookingHistory.addBooking(r3);

        Reservation r4 = new Reservation("David Lee", "Single", 102, 1, 2500.0);
        bookingHistory.addBooking(r4);

        Reservation r5 = new Reservation("Eva Brown", "Double", 202, 4, 4000.0);
        r5.addAddOn("Spa");
        bookingHistory.addBooking(r5);

        // --- Admin requests reports ---
        System.out.println("\n[Admin] Requesting full booking history report...");
        reportService.generateFullReport();

        System.out.println("\n[Admin] Requesting summary report...");
        reportService.generateSummaryReport();

        System.out.println("\n[Admin] Requesting room-type report for 'Double'...");
        reportService.generateRoomTypeReport("Double");

        System.out.println("\n[Admin] Requesting room-type report for 'Suite'...");
        reportService.generateRoomTypeReport("Suite");

        System.out.println("\n[System] Booking history and reporting complete.");
    }
}
