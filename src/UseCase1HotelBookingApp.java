/**
 * UseCase1HotelBookingApp - Application Entry & Welcome Message
 *
 * <p>This class serves as the entry point for the Hotel Booking Management System.
 * It demonstrates the fundamental structure of a Java application by establishing
 * a clear and predictable starting point that prints a welcome message to the console.</p>
 *
 * @author Santhosh
 * @version 1.0
 */
public class UseCase1HotelBookingApp {

    /**
     * The main method is the entry point of the Hotel Booking application.
     * The JVM invokes this method to begin program execution.
     *
     * <p>Execution Flow:</p>
     * <ol>
     *   <li>User runs the application from the command line or IDE.</li>
     *   <li>JVM invokes the main() method.</li>
     *   <li>Application prints a welcome message with name and version.</li>
     *   <li>Application terminates.</li>
     * </ol>
     *
     * @param args command-line arguments (not used in this use case)
     */
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   Welcome to Hotel Booking System      ");
        System.out.println("   Application Name : BookMyStayApp     ");
        System.out.println("   Version          : 1.0               ");
        System.out.println("========================================");
    }
}
