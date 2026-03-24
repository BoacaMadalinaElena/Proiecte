package exemple;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;

public class ReadFileExample {
    public void run() {
    String fileName = "exempluTest.txt"; 

    try {
        File file = new File(fileName);

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line); 
            }
        } catch (IOException e) {
            System.err.println("A aparut o eroare la citirea fisierului: " + e.getMessage());
            e.printStackTrace();
        }
    } catch (Exception ex) {
        System.err.println("Nu s-a putut initializa fisierul: " + ex.getMessage());
        ex.printStackTrace();
    }
}

}

