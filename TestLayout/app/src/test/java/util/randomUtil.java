package util;

import java.util.Random;

/**
 * Created by R30-A on 2017/12/1.
 */

public class randomUtil {

        private static final Random random = new Random();

        private randomUtil(){}

        //取亂數整數
        public static int nextInt(int start){
            return random.nextInt(start);
        }
        //取亂數小數
        public static float nextFloat(float start){
            return random.nextFloat() * start;
        }

        public static int nextInt(int start, int end){
                    return start == end ?
                            start       :
                            start + random.nextInt(end - start);

        }

        public static float nextFloat(float start, float end){
            return start == end ?
                    start :
                    start + (end - start);
        }


}
