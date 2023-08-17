import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App {

    public static void main(String[] args) {
        Pattern pattern = Pattern.compile("[^ ]{20,}");
        Matcher m = pattern.matcher("1234567891011 123456789 123456789 123456789");
        System.out.println(m.find());
//        Scanner scanner = new Scanner(System.in);
//        System.out.println("Выберите действие:");
//        System.out.println("0 - перевести из заданной системы счисления в десятичную");
//        System.out.println("1 - перевести из десятичной системы счисления в заданную");
//        int choice = scanner.nextInt();
//
//        if (choice == 0) {
//            System.out.print("Введите число в заданной системе счисления: ");
//            String numberT = scanner.next();
//            System.out.print("Введите основание системы счисления (от 2 до 9): ");
//            int radixT = scanner.nextInt();
//
//            if (radixT < 2 || radixT > 9) {
//                System.out.println("Некорректное основание системы счисления");
//                return;
//            }
//            int numberN = 0;
//
//            for (int i = 0; i < numberT.length(); i++) {
//                char back = numberT.charAt(i);
//                int value = Character.digit(back, radixT);
//                numberN = numberN * radixT + value;
//            }
//
//            System.out.println("Число в десятичной системе: " + numberN);
//        } else if (choice == 1) {
//            System.out.print("Введите число: ");
//            int number = scanner.nextInt();
//            System.out.print("Введите основание системы счисления (от 2 до 9): ");
//            int radix = scanner.nextInt();
//
//            if (radix < 2 || radix > 9) {
//                System.out.println("Некорректное основание системы счисления");
//                return;
//            }
//            String numberB = "";
//
//            while (number > 0) {
//                int remainder = number % radix;
//                numberB = remainder + numberB;
//                number /= radix;
//            }
//
//            System.out.println("Число в заданной системе: " + numberB);
//        } else {
//            System.out.println("Некорректный выбор");
//        }
    }
}