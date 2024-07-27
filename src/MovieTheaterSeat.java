public class MovieTheaterSeat {
    public static boolean canFriendsSeatTogether(int[] nums, int indexDiff, int valueDiff){
        for(int i=0;i<nums.length;i++){
            for(int j=i+1;j<nums.length && j-i<=valueDiff;j++){
                if(Math.abs(nums[i] - nums[j])<=valueDiff){
                    return true;
                }
            }
        }
        return false;
    }
    public static void main(String[] args) {
        int[] nums = {2,3,5,4,9};
        System.out.println(canFriendsSeatTogether(nums,2,1));
    }
}
