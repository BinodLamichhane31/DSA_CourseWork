import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class SecretDecoderRing {

    static class Instruction{
        int start_disc;
        int end_disc;
        int direction;

        Instruction(int start_disc, int end_disc, int direction) {
            this.start_disc = start_disc;
            this.end_disc = end_disc;
            this.direction = direction;
        }
    }

    public static String decipherMessage(String s, List<Instruction> instructions) {
        char[] messageArray = s.toCharArray();
        for (Instruction instruction : instructions) {
            int start_disc = instruction.start_disc;
            int end_disc = instruction.end_disc;
            int direction = instruction.direction;

            for (int i = start_disc; i <= end_disc; i++) {
                char currentChar = messageArray[i];
                if(direction==1){
                    messageArray[i] = currentChar == 'z' ? 'a' : (char) (currentChar+1);
                }
                else{
                    messageArray[i] = currentChar == 'a' ? 'z' : (char) (currentChar-1);
                }
            }
        }
        return new String(messageArray);

    }

    public static void main(String[] args) {
        String message = "hello";
        List<Instruction> shifts = Arrays.asList(
                new Instruction(0, 1, 1),
                new Instruction(2, 3, 0),
                new Instruction(0, 2, 1)
        );
        String decipheredMessage = decipherMessage(message, shifts);
        System.out.println("Deciphered Message: " + decipheredMessage);
    }
}
