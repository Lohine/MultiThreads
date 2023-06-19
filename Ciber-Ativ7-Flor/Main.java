import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Main {
    public static final String SOURCE_FILE = "C:\\Users\\user\\IdeaProjects\\JAVA\\src\\many-flowers 1137x1517.jpg";
    public static final String DESTINATION_FILE = "./out/many-flowers.jpg";

    public static void main(String[] args) throws IOException, InterruptedException {

        BufferedImage originalImage = ImageIO.read(new File(SOURCE_FILE));
        BufferedImage resultImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        int numThread = 1;
        long startTime = System.currentTimeMillis();
//        recolorImage(originalImage,resultImage,0,0, originalImage.getWidth(), originalImage.getHeight());
//        recolorSingleThreaded(originalImage, resultImage);
        recolorMultiThreaded(originalImage,resultImage,numThread);
        File outputFile = new File(DESTINATION_FILE);
        ImageIO.write(resultImage, "jpg", outputFile);

        long endTime = System.currentTimeMillis();

        long executionTime = endTime - startTime;
        System.out.println("Execution Time: " + executionTime + " milliseconds");

    }

    public static void recolorMultiThreaded(BufferedImage originalImage,BufferedImage resultImage,int numThread) throws InterruptedException {
        Thread[] threads = new Thread[numThread];
        int alturaThread = originalImage.getHeight()/numThread;
        int alturaFalta = originalImage.getHeight()%numThread;
        for (int i=0;i<numThread;i++){
            int topCorner =i*alturaThread;
            int height = alturaThread;
            if(i==numThread-1){
                height+=alturaFalta;
            }
            final int finalTopCorner= topCorner;
            final int finalHeight = height;

            threads[i]=new Thread(() ->{
                recolorImage(originalImage, resultImage, 0, finalTopCorner, originalImage.getWidth(), finalHeight);
            });
            threads[i].start();
        }
        for (Thread thread : threads) {
            thread.join();
        }

    }


    public static void recolorSingleThreaded(BufferedImage originalImage, BufferedImage resultImage) {
        recolorImage(originalImage, resultImage, 0, 0, originalImage.getWidth(), originalImage.getHeight());
    }

    public static void recolorImage(BufferedImage originalImage, BufferedImage resultImage, int leftCorner, int topCorner,
                                    int width, int height) {
        for(int x = leftCorner ; x < leftCorner + width && x < originalImage.getWidth() ; x++) {
            for(int y = topCorner ; y < topCorner + height && y < originalImage.getHeight() ; y++) {
                recolorPixel(originalImage, resultImage, x , y);
            }
        }
    }

    public static void recolorPixel(BufferedImage originalImage, BufferedImage resultImage, int x, int y) {
        int rgb = originalImage.getRGB(x, y);

        int red = getRed(rgb);
        int green = getGreen(rgb);
        int blue = getBlue(rgb);

        int newRed;
        int newGreen;
        int newBlue;
        //aqui vamos popular os novos pixels
        //se o pixel em quest�o for um tom de cinza, vamos aumentar o n�vel de vermelho em 10; o de verde diminuir 80, azul dimiuir 20
        if(isShadeOfGray(red, green, blue)) {
            //para n�o exceder o valor m�ximo (255) pegamos o min
            newRed = Math.min(255, red + 80);
            newGreen = Math.max(0, green - 80);
            //para n�o passar o 0 pegamos o max
            newBlue = Math.max(0, blue - 20);
        } else {
            newRed = red;
            newGreen = green;
            newBlue = blue;
        }
        //M�todo para setar valor rgb na coordenada do pixel da imagem
        int newRGB = createRGBFromColors(newRed, newGreen, newBlue);
        setRGB(resultImage, x, y, newRGB);
    }

    public static void setRGB(BufferedImage image, int x, int y, int rgb) {
        image.getRaster().setDataElements(x, y, image.getColorModel().getDataElements(rgb, null));
    }
    //metodo para verificar se o pixel � tom de cinza (estar� na parte branca da flor)
    //Checa se todos os componentes tem uma intensidade similar (< 30 - determinado empiricamente)
    public static boolean isShadeOfGray(int red, int green, int blue) {
        return Math.abs(red - green) < 30 && Math.abs(red - blue) < 30 && Math.abs( green - blue) < 30;
    }

    public static int createRGBFromColors(int red, int green, int blue) {
        int rgb = 0;
        //opera��o de OR deslocando para esquerda em cada cor
        rgb |= blue;
        rgb |= green << 8;
        rgb |= red << 16;

        rgb |= 0xFF000000;

        return rgb;
    }

    public static int getRed(int rgb) {
        return (rgb & 0x00FF0000) >> 16;
    }

    public static int getGreen(int rgb) {
        return (rgb & 0x0000FF00) >> 8;
    }

    public static int getBlue(int rgb) {
        return rgb & 0x000000FF;
    }
}