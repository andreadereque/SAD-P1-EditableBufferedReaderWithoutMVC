/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package editablebufferedreader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 *
 * @author entel
 */
public class EditableBufferedReader extends BufferedReader{
    Line line;
    
    public static final int RIGHT = 0;
    public static final int LEFT = 1;
    public static final int HOME = 2;
    public static final int END = 3;
    public static final int INS = 4;
    public static final int SUPR = 5;
    public static final int ENTER = 13;
    public static final int BKSP = 127;
    
    
    public EditableBufferedReader(Reader in) {
        super(in);
        line = new Line();
    }

    public void setRaw() throws IOException {
        String[] command = {"/bin/sh", "-c", "stty raw -echo </dev/tty"};
        Runtime.getRuntime().exec(command);
    }

    public void unsetRaw() throws IOException {
        String[] command = {"/bin/sh", "-c", "stty -raw echo </dev/tty"};
        Runtime.getRuntime().exec(command);

    }
     @Override
    public int read() throws IOException {
        int read;

        switch (read = super.read()) { 
            case '\033': 
                super.read(); 

                switch (super.read()) {
                    case 'C':
                        return RIGHT;
                    case 'D':
                        return LEFT;
                    case 'H':
                        return HOME;
                    case 'F':
                        return END;
                    case '2':
                        super.read(); 
                        return INS;
                    case '3':
                        super.read();
                        return SUPR;
                }

            default: 
                return read;

        }
    }
    @Override

    public String readLine() throws IOException {
        int readline;
        this.setRaw();
        
        while ((readline = this.read()) != ENTER) {  
            switch (readline) {
                case RIGHT:
                    if (line.right()) {
                        System.out.print("\033[C");
                    }
                    break;
                case LEFT:
                    if (line.left()) {
                        System.out.print("\033[D");
                    }                                               
                    break;
                case HOME:
                    System.out.print("\033[" + line.home() + "D"); 
                    break;
                case END:
                    if(line.end() > 0){
                    System.out.print("\033[" + line.end() + "C");
                    }
                    break;
                case INS:
                    line.insert();
                    break;
                case SUPR:
                    if (line.supr()) {
                        System.out.print("\033[P");
                    }
                    break;
                case BKSP:
                    if (line.bksp()) {
                        System.out.print("\033[D"); 
                        System.out.print("\033[P"); 
                    }
                    break;
                default:                
                    System.out.print((char) readline);
                    
            }
        }

        this.unsetRaw();
        return line.getLine();

    }

}
