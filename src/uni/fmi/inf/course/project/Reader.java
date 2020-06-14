package uni.fmi.inf.course.project;

/**
 *
 * @author Nedko Gospodinov
 *
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Reader {
    public Scanner fileScanner;
    public File fileSettings;
    public FileInputStream fis;
    public Reader(String fileName) {
        try {
            fileSettings = new File(fileName);
            fis = new FileInputStream(fileSettings);
            fileScanner = new Scanner(fis);
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

}