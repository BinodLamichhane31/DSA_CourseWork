package Question3;

import java.util.Arrays;

public class BusService {

    public static int[] rearrangePassengers(int[] passengers, int k) {
        int n = passengers.length;
        int[] result = new int[n];
        int index = 0;
        for(int i=0;i<n;i+=k){
            int e = Math.min(i+k,n);
            if(e-i==k){
                for(int j=e-1;j>=i;j--){
                    result[index++] = passengers[j];
                }
            }
            else{
                for(int j=i;j<e;j++){
                    result[index++] = passengers[j];
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        int[] passengers = rearrangePassengers(new int[]{1,2,3,4,5}, 2);
        System.out.println(Arrays.toString(passengers));
    }
}
