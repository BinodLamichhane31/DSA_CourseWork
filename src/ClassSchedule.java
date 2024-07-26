import java.util.Arrays;
import java.util.Comparator;

public class ClassSchedule {
    public static int mostUsedClassroom(int n, int[][] classes){
        Arrays.sort(classes, (a,b)-> {
            return a[0] == b[0] ? (b[1] - b[0]) - (a[1] - a[0]) : a[0] - b[0];
        });

        int[] endTimes = new int[n];
        int[] count = new int[n];

        for(int[] c:classes){
            int start = c[0];
            int end = c[1];

            int earliestAvailableClassroom = 0;
            for(int i=1; i<n; i++){
                if(endTimes[i] < endTimes[earliestAvailableClassroom]){
                    earliestAvailableClassroom = i;
                }
            }

            if(endTimes[earliestAvailableClassroom] <= start){
                endTimes[earliestAvailableClassroom] = end;
            }
            else{
                endTimes[earliestAvailableClassroom] += (end-start);
            }
            count[earliestAvailableClassroom]++;

        }
        int max = 0;
        int result = -1;
        for(int i=0; i<n; i++){
            if(count[i] > max || (count[i] == max && i<result)){
                max = count[i];
                result = i;
            }
        }
        return result;


    }

    public static void main(String[] args) {
        int n = 3;
        int[][] classes = {{1, 20}, {2, 10}, {3, 5}, {4, 9},{6,8}};
        System.out.println(mostUsedClassroom(n, classes));
    }
}
