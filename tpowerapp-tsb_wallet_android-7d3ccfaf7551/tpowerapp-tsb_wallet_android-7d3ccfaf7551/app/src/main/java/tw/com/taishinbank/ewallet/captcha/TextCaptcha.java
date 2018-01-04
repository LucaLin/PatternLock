package tw.com.taishinbank.ewallet.captcha;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.io.CharArrayWriter;
import java.util.ArrayList;
import java.util.Random;

public class TextCaptcha extends Captcha {

    protected TextOptions options;
    private int wordLength;
    private int textSizePixel;

    public enum TextOptions{
        UPPERCASE_ONLY,
        LOWERCASE_ONLY,
        NUMBERS_ONLY,
        LETTERS_ONLY,
        NUMBERS_AND_LETTERS
    }

    /**
     * @param width 整張圖寬度
     * @param height 整張圖高度
     * @param wordLength 多少個字
     * @param opt 隨機驗證碼類型
     * @param textSizePixel 字體大小
     */
    public TextCaptcha(int width, int height, int wordLength, TextOptions opt, int textSizePixel){
        setHeight(height);
        setWidth(width);
        this.options = opt;
        usedColors = new ArrayList<>();
        this.wordLength = wordLength;
        this.textSizePixel = textSizePixel;
        this.image = image();
    }

    @Override
    protected Bitmap image() {
        // 畫透明背景
        Paint paintBackground = new Paint();
        paintBackground.setColor(Color.TRANSPARENT);
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);
        c.drawRect(0, 0, getWidth(), getHeight(), paintBackground);

        // 畫字
        Paint paintText = new Paint();
        paintText.setDither(true);
        paintText.setTextSize(textSizePixel);

        Random r = new Random(System.currentTimeMillis());
        CharArrayWriter cab = new CharArrayWriter();

        // 先寫死為數字
        int u_l_n = 2;
        this.answer = "";
        for (int i = 0; i < this.wordLength; i++) {
            char ch = ' ';
            switch (u_l_n) {
                //UpperCase
                case 0:
                    ch = (char) (r.nextInt(91 - 65) + (65));
                    break;
                //LowerCase
                case 1:
                    ch = (char) (r.nextInt(123 - 97) + (97));
                    break;
                //Numbers
                case 2:
                    ch = (char) (r.nextInt(58 - 49) + (49));
                    break;
            }
            cab.append(ch);
            this.answer += ch;
        }

        // 每個字可以佔的寬度大小，算法是總寬度 -  1.5個字型大小（因為想讓左右各留字型大小的一半多可以傾斜），再除以總字數
        int letterWidthUsage = (getWidth() - (int) (textSizePixel * 1.5)) / wordLength;

        // 每個字可以用的高度，算法是總高度 -  1.5個字型用的空間（想留上下各一點空白）
        int letterHeightUsege = getHeight() - (int) (paintText.getFontSpacing() * 1.5);

        char[] data = cab.toCharArray();
        for (int i = 0; i < data.length; i++) {

            // 最左邊有半個字型大小的間距 + 每個字最多佔的空間
            this.x = textSizePixel / 2 + i * letterWidthUsage;
            // 如果空間夠大再 ＋ 隨機的位置
            if(letterWidthUsage > 0){
                this.x += Math.abs(r.nextInt()) % letterWidthUsage;
            }

            // Y是從中間算，所以要先加字體高度（上面才會多半個高度的空間）
            this.y = ((int) paintText.getFontSpacing());
            // 如果空間夠大再 ＋ 隨機的位置
            if(letterHeightUsege > 0){
                this.y += Math.abs(r.nextInt()) % letterHeightUsege;
            }
            Canvas cc = new Canvas(bitmap);

            // 設定上方歪斜度，希望能讓他跟中心保持0.0~0.5以內的差距
            paintText.setTextSkewX((r.nextFloat() - r.nextFloat()) / 2);
            // 設定字體顏色
            paintText.setColor(Color.BLACK);
            // 把字畫上去
            cc.drawText(data, i, 1, this.x, this.y, paintText);
            // 設定下方歪斜度，希望能讓他跟中心保持0.0~0.5以內的差距
            paintText.setTextSkewX((r.nextFloat() - r.nextFloat()) / 2);
        }
        return bitmap;
    }
}
