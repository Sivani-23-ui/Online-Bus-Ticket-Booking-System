package com.busbooking.main;

import com.busbooking.daoimpl.BookingDAOImpl;
import com.busbooking.daoimpl.BusDAOImpl;
import com.busbooking.daoimpl.FeedbackDAOImpl;
import com.busbooking.daoimpl.HistoryDAOImpl;
import com.busbooking.daoimpl.UserDAOImpl;
import com.busbooking.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class MainApp {

    static Scanner sc = new Scanner(System.in);
    static boolean isAdmin = false;
    static String currentUser = "";
    static ArrayList<String> maintainers = new ArrayList<>();

    static {
        maintainers.add(
                "1. Jyoth Patel\n   Role: Project Maintainer / Lead Developer\n   Email: jyoth@example.com\n   Responsibility: Project design, database integration, main application development");
        maintainers.add(
                "2. Rahul Kumar\n   Role: Backend Developer\n   Email: rahul@example.com\n   Responsibility: Bus management module, database queries");
        maintainers.add(
                "3. Priya Sharma\n   Role: Frontend Developer\n   Email: priya@example.com\n   Responsibility: User interface design and menu implementation");
        maintainers.add(
                "4. Ankit Verma\n   Role: Testing & Quality Assurance\n   Email: ankit@example.com\n   Responsibility: Testing booking system, bug fixing, performance checks");
        maintainers.add(
                "5. Sneha Reddy\n   Role: Documentation Maintainer\n   Email: sneha@example.com\n   Responsibility: README documentation, project reports, user guides");
    }

    public static void main(String[] args) {

        BusDAOImpl busDao = new BusDAOImpl();
        BookingDAOImpl booking = new BookingDAOImpl();
        FeedbackDAOImpl feedbackDao = new FeedbackDAOImpl();
        HistoryDAOImpl historyDao = new HistoryDAOImpl();

        login(historyDao);

        while (true) {

            if (isAdmin) {
                System.out.println("====================");
                System.out.println("     ADMIN MENU     ");
                System.out.println("====================");
                System.out.println("1 Available Buses");
                System.out.println("2 Add Bus");
                System.out.println("3 Remove Bus");
                System.out.println("4 Manage Reports");
                System.out.println("5 Show Booked Tickets");
                System.out.println("6 Maintainers Information");
                System.out.println("7 Add Maintainer");
                System.out.println("8 Payment Records");
                System.out.println("9 Seat Configuration");
                System.out.println("10 System History");
                System.out.println("11 Customer Feedback");
                System.out.println("12 Exit");
                System.out.print("pick your Option: ");

                int choice = Integer.parseInt(sc.nextLine());

                switch (choice) {

                    case 1:
                        busDao.viewBuses();
                        break;

                    case 2:
                        System.out.print("Enter bus name: ");
                        String name = sc.nextLine();

                        System.out.print("Enter source: ");
                        String source = sc.nextLine();

                        System.out.print("Enter destination: ");
                        String dest = sc.nextLine();

                        System.out.print("Enter timing (e.g. 09:00): ");
                        String timing = sc.nextLine();

                        System.out.print("Enter reach time (e.g. 13:00): ");
                        String reach = sc.nextLine();

                        System.out.print("Enter total seats: ");
                        int totalSeats = Integer.parseInt(sc.nextLine());

                        System.out.print("Enter cost: ");
                        double cost = Double.parseDouble(sc.nextLine());

                        busDao.addBus(name, source, dest, timing, reach, totalSeats, cost);
                        break;

                    case 3:
                        System.out.print("Enter bus id to remove: ");
                        int removeId = Integer.parseInt(sc.nextLine());

                        busDao.removeBus(removeId);
                        break;

                    case 4:
                        busDao.generateRevenueReport();
                        break;

                    case 5:
                        booking.viewAllBookings();
                        break;

                    case 6:
                        showMaintainers();
                        break;

                    case 7:
                        addMaintainer();
                        break;

                    case 8:
                        booking.viewPaymentRecords();
                        break;

                    case 9:
                        System.out.print("Enter bus id to configure seats: ");
                        int configBusId = Integer.parseInt(sc.nextLine());
                        System.out.print("Enter new total seats: ");
                        int newSeats = Integer.parseInt(sc.nextLine());
                        busDao.configureSeats(configBusId, newSeats);
                        break;

                    case 10:
                        historyDao.viewHistory();
                        break;

                    case 11:
                        feedbackDao.viewAllFeedback();
                        break;

                    case 12:
                        System.exit(0);
                        break;
                }

            } else {
                System.out.println("===================");
                System.out.println("     USER MENU     ");
                System.out.println("===================");
                System.out.println("1 View Buses");
                System.out.println("2 Book Ticket");
                System.out.println("3 Cancel Ticket");
                System.out.println("4 View My Tickets");
                System.out.println("5 Submit Feedback");
                System.out.println("6 Exit");
                System.out.print("Prefer: ");

                int choice = Integer.parseInt(sc.nextLine());

                switch (choice) {

                    case 1:
                        busDao.viewBuses();
                        break;

                    case 2:
                        System.out.print("Enter bus id: ");
                        int busId = Integer.parseInt(sc.nextLine());

                        // Fetch bus details to calculate total cost
                        double busCost = 0.0;
                        int totalSeats = 0;
                        try {
                            Connection con = DBConnection.getConnection();
                            PreparedStatement ps = con.prepareStatement("SELECT cost, total_seats FROM bus WHERE bus_id = ?");
                            ps.setInt(1, busId);
                            ResultSet rs = ps.executeQuery();
                            if (rs.next()) {
                                busCost = rs.getDouble("cost");
                                totalSeats = rs.getInt("total_seats");
                            } else {
                                System.out.println("Invalid Bus ID!");
                                break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            break;
                        }

                        Map<Integer, String> bookedSeatGenderMap = getBookedSeatGenderMap(busId);
                        Set<Integer> bookedSeats = getBookedSeats(bookedSeatGenderMap);
                        Set<Integer> availableSeats = getAvailableSeats(totalSeats, bookedSeats);

                        System.out.println("\nSeat Status:");
                        System.out.println("Total Seats: " + totalSeats);
                        System.out.println("Booked Seats: " + bookedSeats.size());
                        System.out.println("Available Seats: " + availableSeats.size());
                        System.out.println("Booked Female Seats: " + formatBookedSeatsByGender(bookedSeatGenderMap, "female"));
                        System.out.println("Booked Male Seats: " + formatBookedSeatsByGender(bookedSeatGenderMap, "male"));
                        System.out.println("Available Seat Numbers: " + formatSeatList(availableSeats));
                        System.out.println(formatSeatLayout(totalSeats, bookedSeatGenderMap));

                        System.out.print("Enter number of tickets: ");
                        int numTickets = Integer.parseInt(sc.nextLine());
                        if (numTickets > availableSeats.size()) {
                            System.out.println("Only " + availableSeats.size() + " seat(s) available. Booking cancelled.");
                            break;
                        }

                        // Create arrays/lists to hold passenger details before payment
                        String[] names = new String[numTickets];
                        String[] genders = new String[numTickets];
                        String[] seats = new String[numTickets];
                        Set<Integer> selectedSeats = new HashSet<>();
                        Map<Integer, String> selectedSeatGenderMap = new HashMap<>();

                        for (int i = 0; i < numTickets; i++) {
                            System.out.println("\n--- Details for Passenger " + (i + 1) + " ---");
                            System.out.print("Enter Name: ");
                            names[i] = sc.nextLine();

                            System.out.print("Enter Gender (M/F/Other): ");
                            genders[i] = normalizeGender(sc.nextLine());

                            while (true) {
                                System.out.print("Enter Seat Number: ");
                                String seatInput = sc.nextLine().trim();
                                int seatNo;
                                try {
                                    seatNo = Integer.parseInt(seatInput);
                                } catch (NumberFormatException e) {
                                    System.out.println("Seat must be a number. Try again.");
                                    continue;
                                }

                                if (seatNo < 1 || seatNo > totalSeats) {
                                    System.out.println("Seat number should be between 1 and " + totalSeats + ".");
                                    continue;
                                }
                                if (bookedSeats.contains(seatNo)) {
                                    String bookedGender = bookedSeatGenderMap.get(seatNo);
                                    if (bookedGender == null || bookedGender.trim().isEmpty()) {
                                        bookedGender = "Unknown";
                                    }
                                    System.out.println("Seat " + seatNo + " is already booked by " + bookedGender + ". Choose another.");
                                    continue;
                                }
                                if (selectedSeats.contains(seatNo)) {
                                    System.out.println("Seat " + seatNo + " already chosen in this booking. Choose another.");
                                    continue;
                                }
                                if (isMaleBlockedByAdjacentFemale(
                                        seatNo,
                                        totalSeats,
                                        bookedSeatGenderMap,
                                        selectedSeatGenderMap,
                                        genders[i])) {
                                    System.out.println(
                                            "Male passenger cannot book seat " + seatNo + " because adjacent seat has a female.");
                                    continue;
                                }

                                selectedSeats.add(seatNo);
                                selectedSeatGenderMap.put(seatNo, genders[i]);
                                seats[i] = String.valueOf(seatNo);
                                break;
                            }
                        }

                        double totalAmount = busCost * numTickets;
                        System.out.println("\n=================================");
                        System.out.println("       PAYMENT DETAILS           ");
                        System.out.println("=================================");
                        System.out.println("Total Amount Due: " + totalAmount);
                        System.out.println("Select Payment Method:");
                        System.out.println("1. Credit / Debit Card");
                        System.out.println("2. UPI / Wallet");
                        System.out.println("3. Net Banking");
                        System.out.print("Enter choice (1/2/3): ");

                        int pmChoice = Integer.parseInt(sc.nextLine());
                        String paymentMethod = "";
                        if (pmChoice == 1)
                            paymentMethod = "Card";
                        else if (pmChoice == 2)
                            paymentMethod = "UPI";
                        else if (pmChoice == 3)
                            paymentMethod = "Net Banking";
                        else
                            paymentMethod = "Other";

                        System.out.println("Processing Payment... SUCCESS!");

                        // Book tickets in database
                        for (int i = 0; i < numTickets; i++) {
                            booking.bookTicket(names[i], busId, currentUser, genders[i], seats[i], paymentMethod);
                        }
                        break;

                    case 3:

                        System.out.print("Enter booking id: ");
                        int id = Integer.parseInt(sc.nextLine());

                        booking.cancelTicket(id);
                        break;

                    case 4:
                        booking.viewUserTickets(currentUser);
                        break;

                    case 5:
                        System.out.print("Enter bus id for feedback: ");
                        int feedbackBusId = Integer.parseInt(sc.nextLine());

                        System.out.print("Enter rating (1-5): ");
                        int rating = Integer.parseInt(sc.nextLine());
                        if (rating < 1 || rating > 5) {
                            System.out.println("Invalid rating! Please enter between 1 and 5.");
                            break;
                        }

                        System.out.print("Enter your journey feedback: ");
                        String comments = sc.nextLine();
                        feedbackDao.addFeedback(currentUser, feedbackBusId, rating, comments);
                        break;

                    case 6:
                        System.exit(0);
                }
            }
        }
    }

    // MAINTAINERS INFORMATION
    static void showMaintainers() {
        System.out.println("=== MAINTAINERS ===\n");
        System.out.println("Project: BusBookingSystem\n");
        for (String maintainer : maintainers) {
            System.out.println(maintainer + "\n");
        }
    }

    // ADD MAINTAINER
    static void addMaintainer() {
        System.out.println("\n--- ADD MAINTAINER ---");
        System.out.print("Enter name: ");
        String name = sc.nextLine();
        System.out.print("Enter role: ");
        String role = sc.nextLine();
        System.out.print("Enter email: ");
        String email = sc.nextLine();
        System.out.print("Enter responsibility: ");
        String responsibility = sc.nextLine();

        int num = maintainers.size() + 1;
        String maintainer = num + ". " + name + "\n   Role: " + role + "\n   Email: " + email + "\n   Responsibility: "
                + responsibility;
        maintainers.add(maintainer);
        System.out.println("Maintainer added successfully!");
    }

    // LOGIN METHOD
    static void login(HistoryDAOImpl historyDao) {

        System.out.println("============================================");
        System.out.println("      ONLINE BUS TICKET BOOKING SYSTEM      ");
        System.out.println("============================================");
        System.out.println("1 Admin Login");
        System.out.println("2 User Login");
        System.out.print("Select Login Type: ");

        int choice = sc.nextInt();
        sc.nextLine(); // consume newline

        if (choice == 1) {

            System.out.print("Admin Name: ");
            String user = sc.nextLine();

            System.out.print("Password: ");
            String pass = readPassword();

            if (user.equals("admin") && pass.equals("pass")) {
                isAdmin = true;
                System.out.println("Admin login successful!");
            } else {
                System.out.println("Invalid admin credentials!");
                login(historyDao);
            }

        } else {

            UserDAOImpl userDao = new UserDAOImpl();
            boolean loggedIn = false;

            while (!loggedIn) {
                System.out.println("===================");
                System.out.println("    USER ACCESS    ");
                System.out.println("===================");
                System.out.println("1 Log In");
                System.out.println("2 Create Account");
                System.out.println("3 Exit");
                System.out.print("Choose: ");

                int userChoice = sc.nextInt();
                sc.nextLine(); // consume newline

                if (userChoice == 1) {
                    System.out.print("Enter username: ");
                    String uname = sc.nextLine();

                    System.out.print("Enter password: ");
                    String pwd = readPassword();

                    if (userDao.loginUser(uname, pwd)) {
                        currentUser = uname;
                        loggedIn = true;
                        System.out.println("User login successful!");
                    } else {
                        System.out.println("Invalid username or password. Please try again.");
                    }

                } else if (userChoice == 2) {
                    System.out.print("Enter username: ");
                    String uname = sc.nextLine();

                    System.out.print("Enter mobile number: ");
                    String mobile = sc.nextLine();

                    System.out.print("Enter password: ");
                    String pwd = readPassword();

                    if (userDao.createAccount(uname, mobile, pwd)) {
                        System.out.println("Account created successfully! You can now log in.");
                    } else {
                        System.out.println("Account creation failed. Username may already exist.");
                    }
                } else if (userChoice == 3) {
                    System.exit(0);
                } else {
                    System.out.println("Invalid choice.");
                }
            }
        }
    }

    private static Map<Integer, String> getBookedSeatGenderMap(int busId) {
        Map<Integer, String> bookedSeatGenderMap = new HashMap<>();
        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT seat_number, gender FROM booking WHERE bus_id = ?");
            ps.setInt(1, busId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String seat = rs.getString("seat_number");
                String gender = normalizeGender(rs.getString("gender"));
                if (seat == null) {
                    continue;
                }
                try {
                    bookedSeatGenderMap.put(Integer.parseInt(seat.trim()), gender);
                } catch (NumberFormatException ignored) {
                    // Ignore non-numeric legacy seat values.
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bookedSeatGenderMap;
    }

    private static Set<Integer> getBookedSeats(Map<Integer, String> bookedSeatGenderMap) {
        return new HashSet<>(bookedSeatGenderMap.keySet());
    }

    private static Set<Integer> getAvailableSeats(int totalSeats, Set<Integer> bookedSeats) {
        Set<Integer> availableSeats = new HashSet<>();
        for (int seat = 1; seat <= totalSeats; seat++) {
            if (!bookedSeats.contains(seat)) {
                availableSeats.add(seat);
            }
        }
        return availableSeats;
    }

    private static String formatSeatList(Set<Integer> seats) {
        if (seats.isEmpty()) {
            return "None";
        }
        List<Integer> sortedSeats = new ArrayList<>(seats);
        Collections.sort(sortedSeats);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sortedSeats.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(sortedSeats.get(i));
        }
        return sb.toString();
    }

    private static String formatBookedSeatsByGender(Map<Integer, String> bookedSeatGenderMap, String targetGender) {
        List<Integer> seats = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : bookedSeatGenderMap.entrySet()) {
            String gender = entry.getValue();
            if (gender != null && gender.trim().equalsIgnoreCase(targetGender)) {
                seats.add(entry.getKey());
            }
        }

        if (seats.isEmpty()) {
            return "None";
        }

        Collections.sort(seats);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < seats.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(seats.get(i));
        }
        return sb.toString();
    }

    private static String normalizeGender(String raw) {
        if (raw == null) {
            return "Other";
        }
        String value = raw.trim().toLowerCase();
        if (value.equals("m") || value.equals("male")) {
            return "Male";
        }
        if (value.equals("f") || value.equals("female")) {
            return "Female";
        }
        return "Other";
    }

    private static boolean isMaleBlockedByAdjacentFemale(
            int seatNo,
            int totalSeats,
            Map<Integer, String> bookedSeatGenderMap,
            Map<Integer, String> selectedSeatGenderMap,
            String passengerGender) {

        if (!"Male".equalsIgnoreCase(passengerGender)) {
            return false;
        }

        int adjacentSeat = getAdjacentSeatInSamePair(seatNo, totalSeats);
        if (adjacentSeat == -1) {
            return false;
        }

        String bookedAdjacentGender = bookedSeatGenderMap.get(adjacentSeat);
        if ("Female".equalsIgnoreCase(bookedAdjacentGender)) {
            return true;
        }

        String selectedAdjacentGender = selectedSeatGenderMap.get(adjacentSeat);
        return "Female".equalsIgnoreCase(selectedAdjacentGender);
    }

    private static int getAdjacentSeatInSamePair(int seatNo, int totalSeats) {
        int positionInRow = (seatNo - 1) % 4 + 1;
        int adjacentSeat;
        if (positionInRow == 1 || positionInRow == 3) {
            adjacentSeat = seatNo + 1;
        } else {
            adjacentSeat = seatNo - 1;
        }

        if (adjacentSeat < 1 || adjacentSeat > totalSeats) {
            return -1;
        }
        return adjacentSeat;
    }

    private static String formatSeatLayout(int totalSeats, Map<Integer, String> bookedSeatGenderMap) {
        StringBuilder sb = new StringBuilder();
        for (int rowStart = 1; rowStart <= totalSeats; rowStart += 4) {
            int s1 = rowStart;
            int s2 = rowStart + 1;
            int s3 = rowStart + 2;
            int s4 = rowStart + 3;
            sb.append(formatSeatCell(s1, totalSeats, bookedSeatGenderMap)).append(" ");
            sb.append(formatSeatCell(s2, totalSeats, bookedSeatGenderMap)).append("    ");
            sb.append(formatSeatCell(s3, totalSeats, bookedSeatGenderMap)).append(" ");
            sb.append(formatSeatCell(s4, totalSeats, bookedSeatGenderMap)).append("\n");
        }
        sb.append("Legend: A=Available, F=Booked by Female, M=Booked by Male, O=Booked Other");
        return sb.toString();
    }

    private static String formatSeatCell(int seatNo, int totalSeats, Map<Integer, String> bookedSeatGenderMap) {
        if (seatNo > totalSeats) {
            return "      ";
        }

        String gender = bookedSeatGenderMap.get(seatNo);
        String status = "A";
        if (gender != null) {
            if ("Female".equalsIgnoreCase(gender)) {
                status = "F";
            } else if ("Male".equalsIgnoreCase(gender)) {
                status = "M";
            } else {
                status = "O";
            }
        }

        return String.format("%2d[%s]", seatNo, status);
    }

    // UTILITY METHOD TO READ PASSWORD INVISIBLY
    private static String readPassword() {
        try {
            char[] passwordChars = System.console().readPassword();
            if (passwordChars != null) {
                return new String(passwordChars);
            }
        } catch (Exception e) {
            // Fallback to Scanner if console is not available (e.g., in IDEs)
            e.printStackTrace();
        }
        // Fallback to Scanner input if console is not available
        return sc.nextLine();
    }
}
