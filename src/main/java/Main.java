import java.util.Scanner;
import java.util.Stack;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите строку:");
        String value = scanner.nextLine();
        boolean isCorrect = isStringCorrect(value);
        if (isCorrect) {
            System.out.println("Строка корректна");
        } else {
            System.out.println("Строка некорректна");
        }
    }

    public static boolean isStringCorrect(String value) {
        Stack<Character> stack = new Stack<>();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == '(' || c == '[' || c == '{') {
                stack.push(c);
            } else if (c == ')' || c == ']' || c == '}') {
                if (stack.isEmpty()) {
                    return false;
                }
                char last = stack.pop();
                if ((c == ')' && last != '(') || (c == ']' && last != '[') || (c == '}' && last != '{')) {
                    return false;
                }
            }
        }
        return stack.isEmpty();
    }
}