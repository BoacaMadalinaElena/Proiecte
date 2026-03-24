package exemple;
import java.util.Scanner;

public class ReadTerminal {
    public void run() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Introduceti un numar intreg:");
        int intInput = scanner.nextInt();

        scanner.nextLine();

        System.out.println("Introduceti un sir de caractere:");
        String stringInput = scanner.nextLine();

        System.out.println("Introduceti un numar in virgula mobila(separator ,):");
        double doubleInput = scanner.nextDouble();

        System.out.println("Introduceti un caracter:");
        char charInput = scanner.next().charAt(0);

        System.out.println("Numarul intreg introdus: " + intInput);
        System.out.println("Sirul de caractere introdus: " + stringInput);
        System.out.println("Numarul in virgula mobila introdus: " + doubleInput);
        System.out.println("Caracterul introdus: " + charInput);

        scanner.close();
    }
}
