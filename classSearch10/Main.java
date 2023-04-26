//
// Gluck10universal
//
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

// Lab Class
class Lab {
    // Fields
    private int crn;
    private String roomNumber;

    // Constructor
    public Lab(int crn, String roomNumber) {
        this.crn = crn;
        this.roomNumber = roomNumber;
    }

    // Getters
    public int getCRN() {
        return crn;
    }

    public String getRoomNumber() {
        return roomNumber;
    }
}

// Lecture Class
class Lecture {
    // Fields
    private int crn;
    private String prefix;
    private String title;
    private String type;
    private String buildingCode;
    private String roomNumber;
    private boolean isOnline;
    private boolean hasLabs;
    private ArrayList<Lab> labs;

    // Constructor
    // Lecture Is Online Online
    public Lecture(int crn, String prefix, String title, String type) {
        this(crn, prefix, title, type, null, null, false);
        this.isOnline = true;
    }

    // Master Constructor
    public Lecture(int crn, String prefix, String title, String type, String buildingCode, String roomNumber,
            boolean hasLabs) {
        this.crn = crn;
        this.prefix = prefix;
        this.title = title;
        this.type = type;
        this.buildingCode = buildingCode;
        this.roomNumber = roomNumber;
        this.hasLabs = hasLabs;
        // If Lecture Has Labs
        if (this.hasLabs) {
            labs = new ArrayList<Lab>();
        }
    }

    // Add A Lab
    public void addLab(int crn, String roomNumber) {
        labs.add(new Lab(crn, roomNumber));
    }

    // Returns If The Lecture Is Online
    public boolean isOnline() {
        return isOnline;
    }

    // Returns If Lecture Has Labs
    public boolean hasLabs() {
        return hasLabs;
    }

    // Returns Room Number
    public String getRoomNumber() {
        return roomNumber;
    }

    // Returns All Labs
    public ArrayList<Lab> getLabs() {
        return labs;
    }

    // Returns CRN
    public int getCRN() {
        return crn;
    }

    // To String
    @Override
    public String toString() {
        if (isOnline()) {
            return String.format("%d, %s, %s, %s, Online",
                    crn, prefix, title, type);
        } else {
            return String.format("%d, %s, %s, %s, %s, %s, %s",
                    crn, prefix, title, type, buildingCode, roomNumber, hasLabs() ? "Yes" : "No");
        }
    }
}

public class Main {

    public static ArrayList<Lecture> readLectures(String fileName) {
        // ArrayList To Hold Lecture
        ArrayList<Lecture> lectures = new ArrayList<Lecture>();

        File inputFile = new File(fileName);
        boolean readLabs = false;
        // Open The File For Reading
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String line;
            // Read File Line By Line
            while ((line = br.readLine()) != null) {
                // Split The Line
                String[] fields = line.trim().split(",");

                if (readLabs) {
                    if (fields.length != 2) {
                        readLabs = false;
                    } else {
                        // Get Lab CRN and Room Number
                        int crn = Integer.parseInt(fields[0].trim());
                        String roomNumber = fields[1].trim();
                        // Add Lab To Last Lecture
                        lectures.get(lectures.size() - 1).addLab(crn, roomNumber);
                        continue;
                    }
                }
                // Get CRN, Prefix, Title, Type
                int crn = Integer.parseInt(fields[0].trim());
                String prefix = fields[1].trim();
                String title = fields[2].trim();
                String type = fields[3].trim();
                // Check If Lecture is Online
                if (fields[4].trim().equalsIgnoreCase("ONLINE")) {
                    // Create A New Online Lecture
                    lectures.add(new Lecture(crn, prefix, title, type));
                } else {
                    // Lecture is Not Online
                    // Get Building Number

                    String buildingCode = fields[4].trim();
                    String roomNumber = fields[5].trim();
                    boolean hasLabs = false;

                    // Check If Lecture Has Labs
                    if (fields[6].trim().equalsIgnoreCase("YES")) {
                        // Has Labs
                        hasLabs = true;
                        readLabs = true;
                    }
                    // Add Lectrue
                    lectures.add(new Lecture(crn, prefix, title, type, buildingCode, roomNumber, hasLabs));
                }
            }
        } catch (IOException | IndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }

        return lectures;
    }

    // Main
    public static void main(String[] args) {
        // Scanner For User Input
        Scanner input = new Scanner(System.in);

        ArrayList<Lecture> lectures = readLectures("lec.txt");
        // Find And Print Online Courses
        int numOnline = 0;
        for (Lecture lecture : lectures) {
            if (lecture.isOnline()) {
                numOnline++;
            }
        }
        System.out.printf("\n  -  There are %d online lectures offered.\n", numOnline);

        // Input Room Number
        System.out.print("  -  Enter the classroom: ");
        String roomNumber = input.nextLine();

        for (Lecture lecture : lectures) {
            if (!lecture.isOnline()) {
                // Check Lecture
                if (lecture.getRoomNumber().equalsIgnoreCase(roomNumber)) {
                    System.out.println("\tThe crns held in MSB-123 are: "+lecture.getCRN()+"\n");
                }

                if (lecture.hasLabs()) {
                    // Check Labs
                    ArrayList<Lab> labs = lecture.getLabs();
                    for (Lab lab : labs) {
                        if (lab.getRoomNumber().equalsIgnoreCase(roomNumber)) {
                            System.out.println("\n\tThe crns held in MSB-123 are: "+lab.getCRN()+"\n");
                        }
                    }
                }
            }
        }
        // Clean Up
        input.close();

        // Create LecOnly.txt
        String outputFileName = "lecturesOnly.txt";
        try {
            PrintWriter writer = new PrintWriter(outputFileName);

            // Write Online Lectures
            writer.write("Online Lectures\n");
            for (Lecture lecture : lectures) {
                if (lecture.isOnline()) {
                    writer.write(lecture.toString() + "\n");
                }
            }

            // Write Lectures With No Labs
            writer.write("\n\nLectures With No Labs\n");
            for (Lecture lecture : lectures) {
                if (!lecture.isOnline() && !lecture.hasLabs()) {
                    writer.write(lecture.toString() + "\n");
                }
            }
            writer.close();
            System.out.printf("  -  %s is created.\n\n", outputFileName);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}