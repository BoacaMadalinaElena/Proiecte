package exemple;
import java.io.FileWriter;
import java.io.IOException;

public class JavaWriteFile {
    public  void run() {
        String sirDeScris = "Acesta este un sir de caractere pe care dorim sa-l scriem in fisier.";

        try {
            FileWriter writer = new FileWriter("fisier.txt");
            writer.write(sirDeScris);
            writer.close();
            System.out.println("S-a scris sirul in fisier cu succes.");
        } catch (IOException e) {
            System.out.println("A aparut o exceptie: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
