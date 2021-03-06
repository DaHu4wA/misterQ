package misterq.serial;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import misterq.logic.IncomingValueHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;


public class QCommunicator implements SerialPortEventListener {
    SerialPort serialPort;

    private static final String PORT_NAMES[] = {
            "/dev/tty.usbserial-A9007UX1", "/dev/ttyACM0", "/dev/ttyUSB0", "COM5"
    };

    private BufferedReader input;
    private OutputStream output;

    private static final int TIME_OUT = 2000;
    private static final int DATA_RATE = 9600;

    private IncomingValueHandler incomingValueHandler;

    public QCommunicator(IncomingValueHandler incomingValueHandler) {
        this.incomingValueHandler = incomingValueHandler;
    }

    public void initialize() {
        System.setProperty("gnu.io.rxtx.SerialPorts", "COM5");

        CommPortIdentifier portId = null;
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

        while (portEnum.hasMoreElements()) {
            CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
            for (String portName : PORT_NAMES) {
                if (currPortId.getName().equals(portName)) {
                    portId = currPortId;
                    break;
                }
            }
        }
        if (portId == null) {
            System.out.println("Could not find COM port.");
            return;
        }

        try {
            serialPort = (SerialPort) portId.open(this.getClass().getName(),
                    TIME_OUT);

            serialPort.setSerialPortParams(DATA_RATE,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

            input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
            output = serialPort.getOutputStream();

            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    public synchronized void close() {
        if (serialPort != null) {
            serialPort.removeEventListener();
            serialPort.close();
        }
    }

    public synchronized void serialEvent(SerialPortEvent oEvent) {
        if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                String inputLine = input.readLine();
                incomingValueHandler.messageIncoming(inputLine);

            } catch (Exception e) {
                System.err.println(e.toString());
            }
        }
    }

    public synchronized void sendData(String data) throws IOException {
        output.write(data.getBytes());
        output.flush();
    }

    public void moveUp(boolean aLot) {
        try {
            sendData(aLot ? "um" : "u");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void moveHalfUp() {
        try {
            sendData("uh");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void moveDoubleUp() {
        try {
            sendData("ux");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void moveDown(boolean aLot) {
        try {
            sendData(aLot ? "dm" : "d");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void moveHalfDown() {
        try {
            sendData("dh");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void moveDoubleDown() {
        try {
            sendData("dx");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void servoZero(boolean noDown) {
        moveDoubleUp();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onlyZero();

        if (!noDown) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            moveDoubleDown();
        }

    }

    public void onlyZero() {
        try {
            sendData("s0");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void buzzer(boolean longBeep) {

        try {
            // todo
            sendData(longBeep ? "b" : "q");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void servo180() {
        moveDoubleUp();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        only180();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        moveDoubleDown();
    }

    public void only180() {
        try {
            sendData("s1");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void open() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            sendData("O");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    public void closeIt() {
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            sendData("C");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}