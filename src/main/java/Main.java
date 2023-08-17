//import java.util.Scanner;
//import java.util.Stack;
//
//public class Main {
//    public static void main(String[] args) {
//        Scanner scanner = new Scanner(System.in);
//        System.out.println("Введите строку:");
//        String value = scanner.nextLine();
//        boolean isCorrect = isStringCorrect(value);
//        if (isCorrect) {
//            System.out.println("Строка корректна");
//        } else {
//            System.out.println("Строка некорректна");
//        }
//    }
//
//    public static boolean isStringCorrect(String value) {
//        Stack<Character> stack = new Stack<>();
//        for (int i = 0; i < value.length(); i++) {
//            char c = value.charAt(i);
//            if (c == '(' || c == '[' || c == '{') {
//                stack.push(c);
//            } else if (c == ')' || c == ']' || c == '}') {
//                if (stack.isEmpty()) {
//                    return false;
//                }
//                char last = stack.pop();
//                if ((c == ')' && last != '(') || (c == ']' && last != '[') || (c == '}' && last != '{')) {
//                    return false;
//                }
//            }
//        }
//        return stack.isEmpty();
//    }
//}
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the amount: ");
        long amount = scanner.nextLong();
        long amountCorrected = findAmountCorrected(amount);
        long comis = commission(amountCorrected);
        System.out.println("commission: " + comis);
        System.out.println("Corrected amount: " + amountCorrected);
        long sum = comis + amountCorrected;
        System.out.println("sum: " + sum);
    }

    public static long commission(long amount) {
        long commission;
        if (amount <= 3000) {
            commission = Math.round(amount * 0.05);
        } else {
            commission = Math.round(3000 * 0.05);
            commission += Math.round((amount - 3000) * 0.01);
        }
        return commission;
    }

    public static long findAmountCorrected(long amount) {
        long left = 0;
        long right = amount;
        while (left < right) {
            long mid = (left + right) / 2;
            long f = commission(mid) + mid;
            System.out.println("mid: " + mid +" left: " + left + " right: " + right);
            if (f == amount) {
                return mid;
            }
            if (f > amount) {
                right = mid;
            } else {
                left = mid + 1;
                f = commission(left) + left;
                if (f > amount){
                    return  mid;
                }
            }
        }
        return left;
    }
}