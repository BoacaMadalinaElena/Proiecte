package exemple;
public class TestWriteWithExternFile {
    public void run() {
        System.out.println("Incepeti testul...");

        int num1 = 5;
        int num2 = 7;
        int sum = num1 + num2;
        System.out.println("Suma dintre " + num1 + " si " + num2 + " este: " + sum);

        String text = "Java este minunat!";
        System.out.println("Textul initial: " + text);
        System.out.println("Lungimea textului: " + text.length());
        double pi = 3.14159;
        System.out.println("Valoarea lui PI: " + pi);

        System.out.println("Testul s-a încheiat.");

        TestWriteWithExternFile_f01 ex2_f01Object = new TestWriteWithExternFile_f01();
        ex2_f01Object.printDinAltaParte();
    }
}
