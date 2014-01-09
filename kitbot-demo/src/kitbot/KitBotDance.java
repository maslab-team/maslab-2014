package kitbot;

import jssc.SerialPort;

public class KitBotDance {
    private static SerialPort serialPort;
    private static byte motorA = 0;
    private static byte motorB = 0;
    
    public static void setMotors( double powerA, double powerB ) {
        motorA = (byte)(-powerA*127);
        motorB = (byte)(powerB*127);
        modified();
    }
    
    public static void modified() {
        try {
            byte[] data = new byte[4];
            data[0] = 'S';      // Start signal "S"
            data[1] = motorA;   // Motor A data
            data[2] = motorB;   // Motor B data
            data[3] = 'E';      // End signal "E"
            serialPort.writeBytes(data);
        } catch ( Exception ex ) {
            System.out.println(ex);
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        try {
            serialPort = new SerialPort("/dev/tty.usbmodemfa131");
            serialPort.openPort();
            System.out.println("hello");
            serialPort.setParams(115200, 8, 1, 0);
        }
        catch (Exception ex){
            System.out.println(ex);
        }
        
        for(int i = 0; i < 4; i++) {
            setMotors(0.2, 0.2);
            Thread.sleep(1000);
            setMotors(0.0, 0.0);
            Thread.sleep(100);
            setMotors(0.2, -0.2);
            Thread.sleep(480);
            setMotors(0.0, 0.0);
        }
    }
}
